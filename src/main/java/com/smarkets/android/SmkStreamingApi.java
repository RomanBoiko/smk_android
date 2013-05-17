package com.smarkets.android;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import smarkets.eto.SmarketsEtoPiqi;
import smarkets.seto.SmarketsSetoPiqi;
import smarkets.seto.SmarketsSetoPiqi.Payload;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

public class SmkStreamingApi {
	private class EtoSequence {
		private long currentValue = 0;
		public long nextValue() {
			return ++currentValue;
		}
	}

	private final SmkConfig smkConfig;
	private Socket smkSocket;
	private EtoSequence conversationSequence;

	public SmkStreamingApi() throws UnknownHostException, IOException {
		smkConfig = new SmkConfig();
		smkSocket = new Socket(smkConfig.smkStreamingApiHost, smkConfig.smkStreamingApiPort);
		conversationSequence = new EtoSequence();
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
	
	public SmarketsSetoPiqi.Payload loginRequest(String username, String password) {
		return SmarketsSetoPiqi.Payload
				.newBuilder()
				.setType(SmarketsSetoPiqi.PayloadType.PAYLOAD_LOGIN)
				.setEtoPayload(etoPayload(SmarketsEtoPiqi.PayloadType.PAYLOAD_LOGIN))
				.setLogin(SmarketsSetoPiqi.Login.newBuilder().setUsername(username).setPassword(password).build())
				.build();
	}

	public Payload logoutRequest() {
		return SmarketsSetoPiqi.Payload
				.newBuilder()
				.setType(SmarketsSetoPiqi.PayloadType.PAYLOAD_ETO)
				.setEtoPayload(etoPayload(SmarketsEtoPiqi.PayloadType.PAYLOAD_LOGOUT))
				.build();
	}

	public Payload accountStateRequest() {
		return SmarketsSetoPiqi.Payload
				.newBuilder()
				.setType(SmarketsSetoPiqi.PayloadType.PAYLOAD_ACCOUNT_STATE_REQUEST)
				.setEtoPayload(etoPayload(SmarketsEtoPiqi.PayloadType.PAYLOAD_NONE))
				.build();
	}

	private SmarketsEtoPiqi.Payload etoPayload(SmarketsEtoPiqi.PayloadType etoPayloadType) {
		return SmarketsEtoPiqi.Payload.newBuilder()
				.setType(etoPayloadType)
				.setSeq(conversationSequence.nextValue())
				.build();
	}

}
