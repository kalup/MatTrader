package com.mattrader.matlab;

import com.mattrader.common.Book5DataCom;

public class Book5Data {
	
	private final Book5DataCom bData;
	
	Book5Data(Book5DataCom bData) throws Exception {
		this.bData = bData;
		if(bData == null)
			throw new Exception();
	}

	/**
	 * Returns an array containing volumes
	 * 
	 * @return an array containing volumes
	 */
	public double[] volume() {
		long[] lVolume = bData.volume();
		if(lVolume == null)
			return null;
		double[] dVolume = new double[lVolume.length];
		for(int i = 0; i < lVolume.length; ++i)
			dVolume[i] = lVolume[i];
		return dVolume;
	}

	/**
	 * Returns an array containing the number of offers
	 * 
	 * @return an array containing the number of offers
	 */
	public double[] offers() {
		long[] lOffers = bData.offers();
		if(lOffers == null)
			return null;
		double[] dOffers = new double[lOffers.length];
		for(int i = 0; i < lOffers.length; ++i)
			dOffers[i] = lOffers[i];
		return dOffers;
	}

	/**
	 * Returns an array containing prices
	 * 
	 * @return an array containing prices
	 */
	public double[] price() {
		return bData.price();
	}

	/**
	 * Returns the timestamp of when the transaction occurred
	 * 
	 * @return the timestamp
	 */
	public char[][] timestamp() {
		return Utils.toMatlabChar(bData.timestamp());
	}

}
