package com.mattrader.common;

/**
 * This class provide a wrapper for the data contained in a branch of a bid/ask
 * 
 * @author Luca Poletti
 *
 */
public class BidAskDataCom {

	private final long qty;
	private final long nProp;
	private final double price;
	private final String timestamp;

	/**
	 * BidAskDataCom constructor. It should be package visible
	 * 
	 * @param qty		Volume
	 * @param nProp		Number of proposal
	 * @param price		Price
	 * @param timestamp	Timestamp of the transaction
	 */
	BidAskDataCom(long qty, long nProp, double price, String timestamp) {
		this.qty = qty;
		this.nProp = nProp;
		this.price = price;
		this.timestamp = timestamp;
	}

	/**
	 * returns the volume
	 * 
	 * @return the volume
	 */
	public long volume() {
		return qty;
	}

	/**
	 * Returns the number of offers
	 * 
	 * @return the number of offers
	 */
	public long offers() {
		return nProp;
	}

	/**
	 * Returns the price
	 * 
	 * @return the price
	 */
	public double price() {
		return price;
	}
	
	/**
	 * Returns the timestamp of when the transaction occurred
	 * 
	 * @return the timestamp
	 */
	public String timestamp() {
		return timestamp;
	}
}
