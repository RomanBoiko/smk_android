package com.smarkets.android.services.seto;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import android.os.AsyncTask;

import smarkets.seto.SmarketsSetoPiqi;
import smarkets.eto.SmarketsEtoPiqi;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.smarkets.android.services.SslConnectionFactory;

public class StreamingApiClient2 {
	// Breif justification:
	// We can't write to the sockets in the UI thread because it might block
	// We can't read from the sockets in a non-dedicated thread because we don't know when data will be made available
	// Solution is to run two threads
	//	A request thread which feeds requests out of a queue and into the socket
	//	A response theread which recieves responses and evaluates (hopefully short) callbacks

	private final StreamingApiRequestsFactory factory;
	private final ArrayBlockingQueue<SmarketsSetoPiqi.Payload> requestQueue = new ArrayBlockingQueue<SmarketsSetoPiqi.Payload>(20);
	private final ConcurrentHashMap<Long, StreamingCallback> callbacks = new ConcurrentHashMap<Long, StreamingCallback>();
	private RequestDaemon requestDaemon;
	private ResponseDaemon responseDaemon;
	private Thread requestThread;
	private Thread responseThread;

	private class OpenConnectionTask extends AsyncTask<Void, Void, Socket> {
		private final String host;
		private final Integer port;
		private final boolean sslEnabled;

		public OpenConnectionTask(String host, Integer port, boolean sslEnabled) {
			this.host = host;
			this.port = port;
			this.sslEnabled = sslEnabled;
		}

		@Override
		protected Socket doInBackground(Void... wut) {
			try {
				Socket apiSocket;
				if(sslEnabled) {
					apiSocket = new SslConnectionFactory().factoryWhichTrustsEveryone().createSocket(host, port);
				} else {
					apiSocket = new Socket(host, port);
				}
				apiSocket.setSoTimeout(0);
				return apiSocket;
			} catch (Exception e) {
				return null;
			}
		}

		protected void onPostExecute(Socket socket) {
			try {
				CodedOutputStream outStream = CodedOutputStream.newInstance(socket.getOutputStream());
				CodedInputStream inStream = CodedInputStream.newInstance(socket.getInputStream());
				StreamingApiClient2.this.requestDaemon = new RequestDaemon(outStream);
				StreamingApiClient2.this.requestThread = new Thread(requestDaemon);
				StreamingApiClient2.this.requestThread.setDaemon(true);
				StreamingApiClient2.this.responseDaemon = new ResponseDaemon(inStream);
				StreamingApiClient2.this.responseThread = new Thread(responseDaemon);
				StreamingApiClient2.this.responseThread.setDaemon(true);
				StreamingApiClient2.this.requestThread.start();
				StreamingApiClient2.this.responseThread.start();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private class RequestDaemon implements Runnable {
		private final CodedOutputStream stream; 

		public RequestDaemon(CodedOutputStream stream) {
			this.stream = stream;
		}

		@Override
		public void run() {
			try {
				while (true) {
					sendRequest(requestQueue.take());
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		private void sendRequest(SmarketsSetoPiqi.Payload request)
			throws IOException {
			int byteCount = request.getSerializedSize();
			stream.writeRawBytes(SetoByteUtils.encodeRequestHeaderBytes(byteCount));
			request.writeTo(stream);
			stream.writeRawBytes(SetoByteUtils.padding(byteCount));
			stream.flush();
			System.out.println("Message2server: " + request);
		}
	}

	private class ResponseDaemon implements Runnable {
		private boolean running = true;
		private final CodedInputStream stream; 

		public ResponseDaemon(CodedInputStream stream) {
			this.stream = stream;
		}

		@Override
		public void run() {
			try {
				while (running) {
					SmarketsSetoPiqi.Payload response = SmarketsSetoPiqi.Payload.parseFrom(stream.readBytes());
					handleResponse(response);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		private void handleResponse(SmarketsSetoPiqi.Payload response)
			throws InterruptedException {
			System.out.println("Message2client: " + response);
			// Heartbeat messages won't have callbacks but we will need to send another hearbeat after we recieve the first
			if (response.toString().contains("type: PAYLOAD_HEARTBEAT")) {
				requestQueue.put(factory.heartbeatResponse());
				return;
			}
			// Must handle the callback before trying to logout
			long seq = getSequence(response);
			StreamingCallback callback = callbacks.remove(seq);
			if (callback != null) {
				callback.process(response);
			}
			// Kill the loop guard in Thread.run
			if (response.toString().contains("type: PAYLOAD_LOGOUT")) {
				running = false;
			}
		}
	}

	public StreamingApiClient2(String streamingApiHost,
			Integer streamingApiPort, Boolean smkStreamingSslEnabled,
			StreamingApiRequestsFactory factory) throws UnknownHostException,
			IOException {
		this.factory = factory;
		new OpenConnectionTask(streamingApiHost, streamingApiPort, smkStreamingSslEnabled).execute();
	}

	protected static long getSequence(SmarketsSetoPiqi.Payload payload) {
		SmarketsEtoPiqi.Payload etoPayload = payload.getEtoPayload();
		if (!etoPayload.hasSeq()) {
			return 0;
		}

		return etoPayload.getSeq();
	}

	public void request(SmarketsSetoPiqi.Payload requestPayload, StreamingCallback callback) throws IOException {
		long seq = getSequence(requestPayload);
		this.callbacks.put(seq, callback);
		boolean accepted = requestQueue.offer(requestPayload);
		if (!accepted) {
			throw new IOException("Seto request queue is full");
		}
	}
}
