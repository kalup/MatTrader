package com.mattrader.common;

/**
 * This class provide a view over data related to the price
 * 
 * @author Luca Poletti
 *
 */
public class PriceDataCom {

	private double price;
	private double prMin;
	private double prMax;
	private long qty;
	private long progStocks;
	private long progExchanges;
	private final String timestamp;
	
	/**
	 * Constructor
	 * 
	 * @param price - the price
	 * @param prMin - the minimum price registered in the day so far
	 * @param prMax - the maximum price registered in the day so far
	 * @param qty - the volume exchanged at that price
	 * @param progStocks - the progressive number of stocks traded so far
	 * @param progExchanges - the progressive number of exchanges
	 * happened so far
	 * @param timestamp - timestamp of the transaction
	 */
	PriceDataCom (double price, double prMin, double prMax, long qty, long progStocks, long progExchanges,
			String timestamp) {

		this.price = price;
		this.prMin = prMin;
		this.prMax = prMax;
		this.qty = qty;
		this.progStocks = progStocks;
		this.progExchanges = progExchanges;
		this.timestamp = timestamp;
	}

	/**
	 * @return the price
	 */
	public double price() {
		return price;
	}

	/**
	 * @return the prMin
	 */
	public double priceMin() {
		return prMin;
	}

	/**
	 * @return the prMax
	 */
	public double priceMax() {
		return prMax;
	}

	/**
	 * @return the qty
	 */
	public long volume() {
		return qty;
	}

	/**
	 * @return the progStocks
	 */
	public long progStocks() {
		return progStocks;
	}

	/**
	 * @return the progExchanges
	 */
	public long progExchanges() {
		return progExchanges;
	}

	/**
	 * @return the timestamp
	 */
	public String timestamp() {
		return timestamp;
	}
}
