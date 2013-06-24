package com.smarkets.android.services;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import smarkets.seto.SmarketsSetoPiqi;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

public class StreamingApiClient {
	private Socket smkSocket;
	private final StreamingApiRequestsFactory factory;

	public StreamingApiClient(String streamingApiHost, Integer streamingApiPort,
			Boolean smkStreamingSslEnabled, StreamingApiRequestsFactory factory) throws UnknownHostException, IOException {

		this.smkSocket = smkStreamingSslEnabled ?
				new SslConnectionFactory().factoryWhichTrustsEveryone().createSocket(streamingApiHost, streamingApiPort)
				: new Socket(streamingApiHost, streamingApiPort);
		this.factory = factory;
	}

	public SmarketsSetoPiqi.Payload getSmkResponse(SmarketsSetoPiqi.Payload requestPayload) throws UnknownHostException, IOException {
		CodedOutputStream co = CodedOutputStream.newInstance(smkSocket.getOutputStream());

		int byteCount = requestPayload.getSerializedSize();
		co.writeRawBytes(encodeVarint(byteCount));
		requestPayload.writeTo(co);
		co.writeRawBytes(padding(byteCount));
		co.flush();
		smkSocket.getOutputStream().flush();

		CodedInputStream ci = CodedInputStream.newInstance(smkSocket.getInputStream());
		while(true) {
			SmarketsSetoPiqi.Payload response = SmarketsSetoPiqi.Payload.parseFrom(ci.readBytes());
			if (!response.toString().contains("type: PAYLOAD_HEARTBEAT")) {
				return response;
			}
		}
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

	public String toString() {
		return smkSocket.toString() + ", " + factory.toString();
	}

}
