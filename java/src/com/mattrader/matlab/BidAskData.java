package com.mattrader.matlab;

import com.mattrader.common.BidAskDataCom;

public class BidAskData {

	private final BidAskDataCom bad;

	BidAskData(BidAskDataCom bad) throws Exception {
		this.bad = bad;
		if(bad == null)
			throw new Exception();
	}

	/**
	 * returns the volume
	 * 
	 * @return the volume
	 */
	public double volume() {
		return (double)bad.volume();
	}

	/**
	 * Returns the number of offers
	 * 
	 * @return the number of offers
	 */
	public double offers() {
		return (double)bad.offers();
	}

	/**
	 * Returns the price
	 * 
	 * @return the price
	 */
	public double price() {
		return bad.price();
	}

	/**
	 * Returns the timestamp of when the transaction occurred
	 * 
	 * @return the timestamp
	 */
	public char[][] timestamp() {
		return Utils.toMatlabChar(bad.timestamp());
	}

}
