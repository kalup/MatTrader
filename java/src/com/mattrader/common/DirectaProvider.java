package com.mattrader.common;

public class DirectaProvider implements IProvider {

	@Override
	public String getHostname() {
		return "localhost";
	}

	@Override
	public int getDataFeedServicePort() {
		return 10001;
	}

	@Override
	public int getTradingServicePort() {
		return 10002;
	}

	@Override
	public int getHistoryServicePort() {
		return 10003;
	}

}
