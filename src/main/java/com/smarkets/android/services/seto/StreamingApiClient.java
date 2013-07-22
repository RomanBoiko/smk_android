package com.smarkets.android.services.seto;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import smarkets.seto.SmarketsSetoPiqi;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.smarkets.android.services.SslConnectionFactory;

public class StreamingApiClient {
	private final StreamingApiDaemon apiDaemon;

	public StreamingApiClient(String streamingApiHost,
			Integer streamingApiPort, Boolean smkStreamingSslEnabled,
			StreamingApiRequestsFactory factory) throws UnknownHostException,
			IOException {

		this.apiDaemon = new StreamingApiDaemon(streamingApiHost,
				streamingApiPort, smkStreamingSslEnabled, factory);
	}
	
	public void request(SmarketsSetoPiqi.Payload requestPayload, StreamingCallback callback) throws IOException {
		apiDaemon.request(requestPayload, callback);
	}

	private static class StreamingApiDaemon implements Runnable {
		private Socket apiSocket;
		private CodedOutputStream outStream;
		private CodedInputStream inStream;
		private final Thread serverCommunicationThread = new Thread(this);
		private final StreamingApiRequestsFactory factory;
		private Queue<StreamingCallback> callbacksQueue = new ArrayBlockingQueue<StreamingCallback>(2);
		private final String streamingApiHost;
		private final  Integer streamingApiPort;
		private final Boolean smkStreamingSslEnabled;

		public StreamingApiDaemon(String streamingApiHost, Integer streamingApiPort,
				Boolean smkStreamingSslEnabled, StreamingApiRequestsFactory factory) throws IOException {
			this.factory = factory;
			this.streamingApiHost = streamingApiHost;
			this.streamingApiPort = streamingApiPort;
			this.smkStreamingSslEnabled = smkStreamingSslEnabled;
		}


		public void startDaemon() {
			serverCommunicationThread.setDaemon(true);
			serverCommunicationThread.start();
		}

		public void request(SmarketsSetoPiqi.Payload requestPayload, StreamingCallback callback) throws IOException {
			callbacksQueue.add(callback);
			pushPayload(requestPayload);
		}

		private void initIfFirstTime() throws IOException {
			if(null == apiSocket) {
				if(smkStreamingSslEnabled) {
					this.apiSocket = new SslConnectionFactory().factoryWhichTrustsEveryone().createSocket(streamingApiHost,streamingApiPort);
				} else {
					this.apiSocket = new Socket(streamingApiHost, streamingApiPort);
				}
				apiSocket.setSoTimeout(0);
				this.outStream = CodedOutputStream.newInstance(this.apiSocket.getOutputStream());
				this.inStream = CodedInputStream.newInstance(this.apiSocket.getInputStream());
				startDaemon();
			}
		}

		private synchronized void pushPayload(SmarketsSetoPiqi.Payload requestPayload)
				throws IOException {
			initIfFirstTime();
			int byteCount = requestPayload.getSerializedSize();
			outStream.writeRawBytes(SetoByteUtils.encodeRequestHeaderBytes(byteCount));
			requestPayload.writeTo(outStream);
			outStream.writeRawBytes(SetoByteUtils.padding(byteCount));
			outStream.flush();
			System.out.println("Message2server: " + requestPayload);
		}

		@Override
		public void run() {
			try {
				while (true) {
					SmarketsSetoPiqi.Payload response = SmarketsSetoPiqi.Payload
							.parseFrom(inStream.readBytes());
					System.out.println("Message2client: " + response);
					if (response.toString().contains("type: PAYLOAD_HEARTBEAT")) {
						pushPayload(factory.heartbeatResponse());
					} else {
						callbacksQueue.remove().process(response);
						if (response.toString().contains("type: PAYLOAD_LOGOUT")) {
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
