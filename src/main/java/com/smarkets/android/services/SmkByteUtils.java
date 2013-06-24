package com.smarkets.android.services;

import java.io.IOException;

import com.google.protobuf.ByteString;

public class SmkByteUtils {
	public static byte[] padding(int byteCount) throws IOException {
		// padding = '\x00' * max(0, 3 - byte_count);
		ByteString.Output out = ByteString.newOutput();
		for (int i = 0; i < Math.max(0, 3 - byteCount); i++) {
			out.write(0x00);
		}
		return out.toByteString().toByteArray();
	}

	public static byte[] encodeVarint(int value) throws IOException {
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
