package com.smarkets.android.services;

import smarkets.seto.SmarketsSetoPiqi;

public interface SmkCallback {
	void process(SmarketsSetoPiqi.Payload response);
}
