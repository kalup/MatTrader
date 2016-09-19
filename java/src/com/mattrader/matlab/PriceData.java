package com.mattrader.matlab;

import com.mattrader.common.PriceDataCom;

public class PriceData {
	
	private final PriceDataCom pd;
	
	PriceData(PriceDataCom pd) throws Exception {
		this.pd = pd;
		if(pd == null)
			throw new Exception();
	}

	/**
	 * @return the price
	 */
	public double price() {
		return pd.price();
	}

	/**
	 * @return the prMin
	 */
	public double priceMin() {
		return pd.priceMin();
	}

	/**
	 * @return the prMax
	 */
	public double priceMax() {
		return pd.priceMax();
	}

	/**
	 * @return the qty
	 */
	public double volume() {
		return (double)pd.volume();
	}

	/**
	 * @return the progStocks
	 */
	public double progStocks() {
		return (double)pd.progStocks();
	}

	/**
	 * @return the progExchanges
	 */
	public double progExchanges() {
		return (double)pd.progExchanges();
	}

	/**
	 * @return the timestamp
	 */
	public char[][] timestamp() {
		return Utils.toMatlabChar(pd.timestamp());
	}

}
