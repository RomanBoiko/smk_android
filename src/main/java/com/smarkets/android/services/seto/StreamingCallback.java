package com.smarkets.android.services.seto;

import smarkets.seto.SmarketsSetoPiqi;

public interface StreamingCallback {
	void process(SmarketsSetoPiqi.Payload response);
}
