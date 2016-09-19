package com.mattrader.common;

import java.util.ArrayList;

/**
 *	This class models the structure of a financial candle.
 * 
 * @author Luca Poletti
 *
 */
public class CandleSeriesCom extends HistoricalDataSeriesCom {
	
	private final ArrayList<Double> prOff;
	private final ArrayList<Double> prMin;
	private final ArrayList<Double> prMax;
	private final ArrayList<Double> prOpen;

	/**
	 * CandleSeriesCom constructor. It should be package visible.
	 * 
	 * @param ticker - the ticker this candle refers to
	 * @param dcb - the client that will use this Candle
	 */
	CandleSeriesCom(TickerCom ticker, DarwinClientBaseCom dcb) {
		super(ticker, dcb);

		prOff = new ArrayList<Double>();
		prMin = new ArrayList<Double>();
		prMax = new ArrayList<Double>();
		prOpen = new ArrayList<Double>();
	}
	
	/**
	 * Method to add a candle to the list of candles within this series.
	 * Internal use only. It is synchronized to avoid concurrent modification.
	 * 
	 * @param dateTime
	 * @param prOff
	 * @param prMin
	 * @param prMax
	 * @param prOpen
	 * @param qty
	 */
	synchronized void addCandle(String dateTime, double prOff, double prMin,double prMax, double prOpen,
			long qty) {
		this.dateTime.add(dateTime);
		this.prOff.add(prOff);
		this.prMin.add(prMin);
		this.prMax.add(prMax);
		this.prOpen.add(prOpen);
		this.qty.add(qty);
	}

	/**
	 * Returns prices at the end of each candle
	 * 
	 * @return a list of prices
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Double> getOffPrices() {
		return (ArrayList<Double>) prOff.clone();
	}

	/**
	 * Returns minimum prices of each candle
	 * 
	 * @return a list of prices
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Double> getMinPrices() {
		return (ArrayList<Double>) prMin.clone();
	}

	/**
	 * Returns maximal prices of each candle
	 * 
	 * @return a list of prices
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Double> getMaxPrices() {
		return (ArrayList<Double>) prMax.clone();
	}

	/**
	 * Returns prices at the end of each candle
	 * 
	 * @return a list of prices
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Double> getOpenPrices() {
		return (ArrayList<Double>) prOpen.clone();
	}

}
