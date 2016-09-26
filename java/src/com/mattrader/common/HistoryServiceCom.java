package com.mattrader.common;

/**
 * A class extending {@link ServiceCom} specialized in the connection to the History service
 * 
 * @author Luca Poletti
 *
 */
public class HistoryServiceCom extends ServiceCom {

	/**
	 * Constructor, it prepare the structure, but it neither creates any connection
	 * nor spawns any thread
	 * 
	 * @param callerDcb - the client that creating this service
	 */
	HistoryServiceCom(MTClientBaseCom callerDcb) {	// We ensure this class can't be instantiated by external app
		super(callerDcb);
	}

	/* (non-Javadoc)
	 * @see com.mattrader.common.ServiceCom#getPort()
	 */
	@Override
	public int getPort() {
		return dcb.HISTORY_PORT;
	}
	
	// History specific functions

	/**
	 * Low level call. Request a tick-by-tick view of the past days
	 * 
	 * @param ticker - ticker code we are interested in
	 * @param days - number of days (day)
	 */
	void d_TBT(String ticker, int days) {
		d__println("TBT " + ticker + " " + days);
	}

	/**
	 * Low level call. Request a tick-by-tick view of the past days
	 * 
	 * @param ticker - ticker code we are interested in
	 * @param dayTimeB - starting dayTime (String DATETIME)
	 * @param dayTimeE - ending dayTime (String DATETIME)
	 */
	void d_TBTRANGE(String ticker, String dayTimeB, String dayTimeE) {
		d__println("TBTRANGE " + ticker + " " + dayTimeB + " " + dayTimeE);
	}

	/**
	 * Low level call. Request a candle view of the past days
	 * 
	 * @param ticker - ticker code we are interested in
	 * @param days - number of days (day)
	 * @param period - the granularity (second)
	 */
	void d_CANDLE(String ticker, int days, int period) {
		d__println("CANDLE " + ticker + " " + days + " " + period);
	}

	/**
	 * Low level call. Request a candle view of the past days
	 * 
	 * @param ticker - ticker code we are interested in
	 * @param dayTimeB - starting dayTime (String DATETIME)
	 * @param dayTimeE - ending dayTime (String DATETIME)
	 * @param period - the granularity (second)
	 */
	void d_CANDLERANGE(String ticker, String dayTimeB, String dayTimeE, int period) {
		d__println("CANDLERANGE " + ticker + " " + dayTimeB + " " + dayTimeE + " " + period);
	}
	
	/**
	 * Low level call. Request the current after hour settings
	 */
	void d_VOLUMEAFTERHOURS () {
		d__println("VOLUMEAFTERHOURS");
	}
	
	/**
	 * Low level call. Set the current after hour to the specified value
	 * 
	 * @param volumes - detail requested; possible value: ("CNT", "AH", "CNT+AH")}
	 */
	void d_VOLUMEAFTERHOURS (String volumes) {
		d__println("VOLUMEAFTERHOURS " + volumes);
	}
	
	// Functions working with the FIFO queue
	
	// TODO synchronized over historical calls

	/**
	 * Request a tick-by-tick view of the past days
	 * 
	 * @param ticker - ticker code we are interested in
	 * @param days - number of days (day)
	 * @return an empty {@link TBTSeriesCom} that will be populated when data
	 * become available
	 */
	TBTSeriesCom TBT(String ticker, int days) {
		//synchronized (TBTLock) {
		d_TBT(ticker, days);
		return dcb.histCallsMan.queueTBT(ticker);
	}

	/**
	 * Request a tick-by-tick view of the past days
	 * 
	 * @param ticker - ticker code we are interested in
	 * @param dayTimeB - starting dayTime (String DATETIME)
	 * @param dayTimeE - ending dayTime (String DATETIME)
	 * @return an empty {@link TBTSeriesCom} that will be populated when data
	 * become available
	 */
	TBTSeriesCom TBTRANGE(String ticker, String dayTimeB, String dayTimeE) {
		//synchronized (TBTLock) {
		d_TBTRANGE(ticker, dayTimeB, dayTimeE);
		return dcb.histCallsMan.queueTBT(ticker);
	}

	/**
	 * Request a candle view of the past days
	 * 
	 * @param ticker - ticker code we are interested in
	 * @param days - number of days (day)
	 * @param period - the granularity (second)
	 * @return an empty {@link CandleSeriesCom} that will be populated when data
	 * become available
	 */
	CandleSeriesCom CANDLE(String ticker, int days, int period) {
		//synchronized (CANDLELock) {
		d_CANDLE(ticker, days, period);
		return dcb.histCallsMan.queueCandle(ticker);
	}

	/**
	 * Low level call. Request a candle view of the past days
	 * 
	 * @param ticker - ticker code we are interested in
	 * @param dayTimeB - starting dayTime (String DATETIME)
	 * @param dayTimeE - ending dayTime (String DATETIME)
	 * @param period - the granularity (second)
	 * @return an empty {@link CandleSeriesCom} that will be populated when data
	 * become available
	 */
	CandleSeriesCom CANDLERANGE(String ticker, String dayTimeB, String dayTimeE, int period) {
		//synchronized (CANDLELock) {
		d_CANDLERANGE(ticker, dayTimeB, dayTimeE, period);
		return dcb.histCallsMan.queueCandle(ticker);
	}
	
	//////// Open and close events ////////

	/* (non-Javadoc)
	 * @see com.mattrader.common.ServiceCom#onOpen()
	 */
	@Override
	public void onOpen() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.mattrader.common.ServiceCom#onClose()
	 */
	@Override
	public void onClose() {
		// TODO Auto-generated method stub
		
	}

}
