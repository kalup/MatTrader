package com.mattrader.common;

/**
 * This is an interface that must be implemented to be able to connect to the provider.
 * A provider is expected to push information via socket from a specific address and a specific port
 * ie. www.example.com:12345
 *
 */
public interface IProvider {
	
	/**
	 * @return the hostname of the provider usually an URL or an IP
	 */
	public String getHostname();
	
	/**
	 * @return the port to connect to get DataFeed streams
	 */
	public int getDataFeedServicePort();

	/**
	 * @return the port to connect to get Trading streams
	 */
	public int getTradingServicePort();

	/**
	 * @return the port to connect to get History streams
	 */
	public int getHistoryServicePort();

}
