package com.mattrader.common;

import java.util.ArrayList;

/**
 *	This class models the structure of a financial historical data series.
 *	It is extended by {@link CandleSeriesCom} and {@link TBTSeriesCom},
 *	modeling respectively a Candle and a Tick-by-tick series.
 * 
 * @author Luca Poletti
 *
 */
public abstract class HistoricalDataSeriesCom {

	private LogCom log;
	
	boolean isReady;
	
	final TickerCom ticker;

	protected final ArrayList<Long> qty;
	protected final ArrayList<String> dateTime;
	
	private EventManagerCom readyEventManager;

	/**
	 * HistoricalDataSeriesCom constructor
	 * 
	 * @param ticker - the ticker this series refers to
	 * @param dcb - the client that will use this series
	 */
	HistoricalDataSeriesCom(TickerCom ticker, MTClientBaseCom dcb) {

		log = dcb.log();
		log.ff(this,"constructor: " + ticker.tickerCode);
		this.ticker = ticker;

		qty = new ArrayList<Long>();
		dateTime = new ArrayList<String>();

		isReady = false;
		
		readyEventManager = new EventManagerCom(dcb);
	}

	/**
	 * Returns date/times at which data are sampled within this series
	 * 
	 * @return a list of date/times
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getDateTimeSeries() {
		return (ArrayList<String>) dateTime.clone();
	}

	/**
	 * Returns volumes of each candle
	 * 
	 * @return a list of vulmes
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Long> getVolumes() {
		return (ArrayList<Long>) qty.clone();
	}

	/**
	 * Check whether the series is ready to be used and has been populated
	 * 
	 * @return true if the series is ready
	 */
	public boolean isReady() {
		return isReady;
	}

	/**
	 * Returns the number of elements this series contains
	 * 
	 * @return the size of the series
	 */
	public int size() {
		return dateTime.size();
	}
	
	/**
	 * Returns the code of the ticker this series refers to
	 * 
	 * @return the code of the ticker this series refers to
	 */
	public String getTickerCode() {
		return ticker.tickerCode;
	}
	
	/**
	 * Returns the ticker this series refers to
	 * 
	 * @return the ticker this series refers to
	 */
	TickerCom getTicker() {
		return ticker;
	}
	
	/**
	 * This method collect the notification that specify the series is ready.
	 * This will fire a {@link EventCom.SeriesReadyEvent}
	 */
	void notifyReady() {
		readyEventManager.receivedEvent(new EventCom.SeriesReadyEvent(this, ticker));
	}

	/**
	 * This method returns an {@link EventManagerCom} that will collect
	 * {@link EventCom.SeriesReadyEvent} and trigger callbacks
	 * @return the {@link EventManagerCom} specific for {@link EventCom.SeriesReadyEvent}
	 * @see EventManagerCom
	 */
	public EventManagerCom onReadyEventManager() {
		return readyEventManager;
	}
}
