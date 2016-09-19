package com.mattrader.common;

/**
 * This class provide a wrapper for the data contained in a bid or in an ask branch of a book
 * 
 * @author Luca Poletti
 *
 */
public class Book5DataCom {

	private final long qty[];
	private final long nProp[];
	private final double price[];
	private final String timestamp;

	/**
	 * Book5DataCom constructor. It should be package visible
	 * 
	 * @param qty		Array of volumes
	 * @param nProp		Array of number of proposals
	 * @param price		Array of prices
	 * @param timestamp	Timestamp of the transaction
	 */
	Book5DataCom(long qty[], long nProp[], double price[],
			String timestamp) {
		this.qty = qty;
		this.nProp = nProp;
		this.price = price;
		this.timestamp = timestamp;
	}

	/**
	 * Returns an array containing volumes
	 * 
	 * @return an array containing volumes
	 */
	public long[] volume() {
		return qty;
	}

	/**
	 * Returns an array containing the number of offers
	 * 
	 * @return an array containing the number of offers
	 */
	public long[] offers() {
		return nProp;
	}

	/**
	 * Returns an array containing prices
	 * 
	 * @return an array containing prices
	 */
	public double[] price() {
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
