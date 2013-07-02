package com.smarkets.android.services.seto;

import java.io.IOException;

import smarkets.seto.SmarketsSetoPiqi;

public interface StreamingApiClient {

	public void request(SmarketsSetoPiqi.Payload requestPayload, StreamingCallback callback) throws IOException;
}
