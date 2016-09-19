package com.mattrader.matlab;

import com.mattrader.common.StockCom;

public class Stock {
	
	private final StockCom s;
	
	Stock(StockCom s) throws Exception {
		this.s = s;
		if(s == null)
			throw new Exception();
	}

	/**
	 * @return the ticker
	 */
	public char[][] ticker() {
		return Utils.toMatlabChar(s.ticker());
	}

	/**
	 * @return the avgPrice
	 */
	public double price() {
		return s.price();
	}

	/**
	 * @return the qtyPort
	 */
	public double qtyPortfolio() {
		return (double)s.qtyPortfolio();
	}

	/**
	 * @return the qtyDir
	 */
	public double qtyDirecta() {
		return (double)s.qtyDirecta();
	}

	/**
	 * @return the qtyNeg
	 */
	public double qtyNegotiation() {
		return (double)s.qtyNegotiation();
	}

	/**
	 * @return the gain in €
	 */
	public double gain() {
		return s.gain();
	}

	/**
	 * @return the timestamp
	 */
	public char[][] timestamp() {
		return Utils.toMatlabChar(s.timestamp());
	}
	
	/**
	 * Request an update of the stock to Darwin
	 */
	public void update() {
		s.update();
	}

}
