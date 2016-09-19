package com.mattrader.common;

import java.util.ArrayList;

/**
 *	This class models the structure of a financial Tick-by-tick (TBT).
 * 
 * @author Luca Poletti
 *
 */
public class TBTSeriesCom extends HistoricalDataSeriesCom {

	private final ArrayList<Double> price;

	/**
	 * TBTSeriesCom constructor. It should be package visible.
	 * 
	 * @param ticker - the ticker this TBT refers to
	 * @param dcb - the client that will use this TBT
	 */
	TBTSeriesCom(TickerCom ticker, DarwinClientBaseCom dcb) {
		super(ticker, dcb);

		price = new ArrayList<Double>();
	}

	/**
	 * Method to add a tick-by-tick to the list of tick-by-tick within
	 * this series. Internal use only. It is synchronized to avoid
	 * concurrent modification.
	 * 
	 * @param dateTime
	 * @param price
	 * @param qty
	 */
	synchronized void addTick(String dateTime, double price, long qty) {
		this.dateTime.add(dateTime);
		this.price.add(price);
		this.qty.add(qty);
	}

	/**
	 * @return list of prices
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Double> getPrices() {
		return (ArrayList<Double>) price.clone();
	}

}
