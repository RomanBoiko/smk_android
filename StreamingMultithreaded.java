package com.smarkets.android.services;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Stack;

import smarkets.seto.SmarketsSetoPiqi;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

public class StreamingApiClient implements Runnable{
	private final Socket smkSocket;
	private final StreamingApiRequestsFactory factory;
	private Thread serverResponseReadingThread;
	private final CodedOutputStream outStream;
	private final Stack<SmarketsSetoPiqi.Payload> responsesStack = new Stack<SmarketsSetoPiqi.Payload>();

	public StreamingApiClient(Socket smkSocket, StreamingApiRequestsFactory factory) throws IOException {
		this.smkSocket = smkSocket;
		this.factory = factory;
		this.outStream = CodedOutputStream.newInstance(smkSocket.getOutputStream());
	}

	@Override
	public void run() {

		try {
			CodedInputStream inStream = CodedInputStream.newInstance(smkSocket.getInputStream());
			while(true) {
				SmarketsSetoPiqi.Payload response = SmarketsSetoPiqi.Payload.parseFrom(inStream.readBytes());
				System.out.println("StreamingApi message: " + response.toString());
				if (!response.toString().contains("type: PAYLOAD_HEARTBEAT")) {
					this.responsesStack.add(response);
					if (response.toString().contains("type: PAYLOAD_LOGOUT")) {
						break;
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Streaming server communication failed", e);
		}
	}

	private void startAsynchronousStreamingServerCommunication() {
		Thread serverCommunicationThread = new Thread(this);
		serverCommunicationThread.setDaemon(true);
		serverCommunicationThread.start();
	}

	public SmarketsSetoPiqi.Payload getSmkResponse(SmarketsSetoPiqi.Payload requestPayload) throws UnknownHostException, IOException, InterruptedException {
		if (null == serverResponseReadingThread) {
			startAsynchronousStreamingServerCommunication();
		}
		responsesStack.clear();

		writeServerRequest(requestPayload);

		while(responsesStack.isEmpty()) {
			Thread.sleep(200);
		}

		return responsesStack.pop();
	}

	private void writeServerRequest(SmarketsSetoPiqi.Payload requestPayload)
			throws IOException {
		int byteCount = requestPayload.getSerializedSize();
		outStream.writeRawBytes(encodeVarint(byteCount));
		requestPayload.writeTo(outStream);
		outStream.writeRawBytes(padding(byteCount));
		outStream.flush();
		smkSocket.getOutputStream().flush();
	}

	private byte[] padding(int byteCount) throws IOException {
		// padding = '\x00' * max(0, 3 - byte_count);
		ByteString.Output out = ByteString.newOutput();
		for (int i = 0; i < Math.max(0, 3 - byteCount); i++) {
			out.write(0x00);
		}
		return out.toByteString().toByteArray();
	}

	private byte[] encodeVarint(int value) throws IOException {
		ByteString.Output out = ByteString.newOutput();
		int bits = value & 0x7f;
		value = value >> 7;
		while (value > 0) {
			out.write(0x80 | bits);
			bits = value & 0x7f;
			value = value >> 7;
		}
		out.write(bits);
		return out.toByteString().toByteArray();
	}
	

}
