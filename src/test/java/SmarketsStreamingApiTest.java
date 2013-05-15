import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Test;

import smarkets.eto.SmarketsEtoPiqi;
import smarkets.seto.SmarketsSetoPiqi;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

public class SmarketsStreamingApiTest {

	@Test
	public void shouldLoginToSmkWithoutExceptions() throws UnknownHostException, IOException {
		SmarketsSetoPiqi.Payload loginRequest = loginRequest("user@smarkets.com", "password");
		System.out.println("===>request:" + loginRequest.toString());
		ByteString responseBytes = getSmkResponse(loginRequest);
		SmarketsSetoPiqi.Payload loginResponse = SmarketsSetoPiqi.Payload.parseFrom(responseBytes);
		System.out.println("===>response:" + loginResponse.toString());
	}

	private ByteString getSmkResponse(SmarketsSetoPiqi.Payload loginRequest) throws UnknownHostException, IOException {
		Socket ssl = new Socket("api-sandbox.smarkets.com", 3701);
		CodedOutputStream co = CodedOutputStream.newInstance(ssl.getOutputStream());

		int byteCount = loginRequest.getSerializedSize();
		co.writeRawBytes(encodeVarint(byteCount));
		loginRequest.writeTo(co);
		co.writeRawBytes(padding(byteCount));
		co.flush();
		ssl.getOutputStream().flush();

		CodedInputStream ci = CodedInputStream.newInstance(ssl.getInputStream());
		ByteString responseBytes = ci.readBytes();
		return responseBytes;
	}

	private SmarketsSetoPiqi.Payload loginRequest(String username, String password) {
		return SmarketsSetoPiqi.Payload
				.newBuilder()
				.setType(SmarketsSetoPiqi.PayloadType.PAYLOAD_LOGIN)
				.setEtoPayload(
						SmarketsEtoPiqi.Payload.newBuilder().setType(SmarketsEtoPiqi.PayloadType.PAYLOAD_LOGIN)
								.setSeq(1).build())
				.setLogin(SmarketsSetoPiqi.Login.newBuilder().setUsername(username).setPassword(password).build())
				.build();
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
