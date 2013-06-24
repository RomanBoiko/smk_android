package com.smarkets.android.services;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import smarkets.seto.SmarketsSetoPiqi;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

public class StreamingApiClient {
	private final StreamingApiDaemon apiDaemon;
	private final StreamingApiRequestsFactory factory;

	public StreamingApiClient(String streamingApiHost,
			Integer streamingApiPort, Boolean smkStreamingSslEnabled,
			StreamingApiRequestsFactory factory) throws UnknownHostException,
			IOException {

		Socket smkSocket = smkStreamingSslEnabled ?
				new SslConnectionFactory().factoryWhichTrustsEveryone().createSocket(streamingApiHost,streamingApiPort)
				: new Socket(streamingApiHost, streamingApiPort);
		this.apiDaemon = new StreamingApiDaemon(smkSocket);
		this.factory = factory;
		this.apiDaemon.startDaemon();
	}
	
	public void request(SmarketsSetoPiqi.Payload requestPayload, SmkCallback callback) throws IOException {
		apiDaemon.request(requestPayload, callback);
	}

	private static class StreamingApiDaemon implements Runnable {
		private final Socket apiSocket;
		private CodedOutputStream outStream;
		private CodedInputStream inStream;
		private final Thread serverCommunicationThread = new Thread(this);
		private Queue<SmkCallback> callbacksQueue = new ArrayBlockingQueue<SmkCallback>(2);

		public StreamingApiDaemon(Socket apiSocket) throws IOException {
			this.apiSocket = apiSocket;
			this.outStream = CodedOutputStream.newInstance(this.apiSocket.getOutputStream());
			this.inStream = CodedInputStream.newInstance(this.apiSocket.getInputStream());
		}


		public void startDaemon() {
			serverCommunicationThread.setDaemon(true);
			serverCommunicationThread.start();
		}

		public void request(SmarketsSetoPiqi.Payload requestPayload, SmkCallback callback) throws IOException {
			callbacksQueue.add(callback);
			pushPayload(requestPayload);
		}


		private synchronized void pushPayload(SmarketsSetoPiqi.Payload requestPayload)
				throws IOException {
			int byteCount = requestPayload.getSerializedSize();
			outStream.writeRawBytes(SmkByteUtils.encodeVarint(byteCount));
			requestPayload.writeTo(outStream);
			outStream.writeRawBytes(SmkByteUtils.padding(byteCount));
			outStream.flush();
		}

		@Override
		public void run() {
			try {
				while (true) {
					SmarketsSetoPiqi.Payload response = SmarketsSetoPiqi.Payload
							.parseFrom(inStream.readBytes());
					if (response.toString().contains("type: PAYLOAD_HEARTBEAT")) {
						System.out.println("HEARTBEAT: " + response);
					} else {
						callbacksQueue.remove().process(response);
						if (response.toString()
								.contains("type: PAYLOAD_LOGOUT")) {
							break;
						}
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
