package com.smarkets.android;

public class SmkConfig {

	//==========SANDBOX=============
	public final String smkRestApiRoot() { return "https://api.sandbox.smarkets.com"; }
	public final String smkStreamingApiHost() { return "api.sandbox.smarkets.com"; }
//	public final String smkRestApiRoot() { return "https://api.smarkets.com"; }
//	public final String smkStreamingApiHost() { return "api.smarkets.com"; }
	public final Integer smkStreamingApiPort() { return 3701; }
	public final Boolean smkStreamingApiSslEnabled() { return true; }

	//==========LOCAL VARGANT=======
//	public final String smkRestApiRoot = "http://vagrant-dev.corp.smarkets.com:8007";
//	public final String smkStreamingApiHost = "vagrant-dev.corp.smarkets.com";
//	public final Integer smkStreamingApiPort = 3700;
//	public final Boolean smkStreamingApiSslEnabled = false;

}
