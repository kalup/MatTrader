package com.mattrader.common;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class will internally manage the order of historical requests (CANDLE/TBT)
 * 
 * @author Luca Poletti
 *
 */
class HistoricalCallsManagerCom {
	
	private LogCom log;

	private LinkedBlockingQueue<HistoricalDataSeriesCom> tickerFIFO;
	
	private final DarwinClientBaseCom dcb;
	
	/**
	 * Constructor
	 * 
	 * @param dcb - {@link DarwinClientBaseCom} served by this class
	 */
	HistoricalCallsManagerCom(DarwinClientBaseCom dcb) {

		log = dcb.log();
		log.c(this, "constructor");
		tickerFIFO = new LinkedBlockingQueue<HistoricalDataSeriesCom>();
		this.dcb = dcb;
	}

	/**
	 * Add a TBT request for the ticker specified by the tickerCode to the list
	 * 
	 * @param tickerCode - the code that specify the {@link TickerCom} ticker
	 * @return an empty {@link TBTSeriesCom} that will be populated when data
	 * become available
	 */
	TBTSeriesCom queueTBT(String tickerCode) {
		log.f(this, "historical data TBT added to queue: " + tickerCode);
		TBTSeriesCom tbtSeries = null;
		TickerCom ticker = null;
		if((ticker = dcb.dispatcher.getSubscription(tickerCode)) != null) {
			try {
				tbtSeries = new TBTSeriesCom(ticker, dcb);
				tickerFIFO.put(tbtSeries);
			} catch (InterruptedException e) {
				log.e(this, e);
			}
		}
		return tbtSeries;
	}

	/**
	 * Add a Candle request for the ticker specified by the tickerCode to the list
	 * 
	 * @param tickerCode - the code that specify the {@link TickerCom} ticker
	 * @return an empty {@link CandleSeriesCom} that will be populated when data
	 * become available
	 */
	CandleSeriesCom queueCandle(String tickerCode) {
		log.f(this, "historical data Candle added to queue: " + tickerCode);
		CandleSeriesCom candleSeries = null;
		TickerCom ticker = null;
		if((ticker = dcb.dispatcher.getSubscription(tickerCode)) != null) {
			try {
				candleSeries = new CandleSeriesCom(ticker, dcb);
				tickerFIFO.put(candleSeries);
			} catch (InterruptedException e) {
				log.e(this, e);
			}
		}
		return candleSeries;
	}
	
	/**
	 * This method get called by a dispatcher whenever it receive data related to
	 * historical calls
	 * 
	 * @param message - {@link MessageCom} message containing informations received
	 * from Darwin
	 */
	void receiveMessage(MessageCom message) {
		String messType = message.getType();

		if(messType.equals(MessageCom.BEGIN))
			begin(message.getData());
		else if(messType.equals(MessageCom.END))
			end(message.getData());
		else if(messType.equals(MessageCom.CANDLE))
			candle(message.getData());
		else if(messType.equals(MessageCom.TBT))
			tbt(message.getData());

	}
	
	/**
	 * This method get called whenever is received a BEGIN message. Do nothing
	 * 
	 * @param data - a key-value collection representing received data
	 */
	private void begin(HashMap<String,String> data) {
		return;
	}

	/**
	 * This method get called whenever is received an END message. It fires
	 * the event that the {@link HistoricalDataSeriesCom} is ready
	 * 
	 * @param data - a key-value collection representing received data
	 */
	private void end(HashMap<String,String> data) {
		HistoricalDataSeriesCom hds = tickerFIFO.poll();
		
		log.f(this,"historical data resolved: " + hds.getTickerCode());

//		if(data.containsKey("ticker")) {
//			String ticker = data.get("ticker");
//			if(!ticker.equals(hds.getTickerCode())) {
//				System.err.println("Error: ticker queue malformed; GOT "+
//						ticker + " EXPECTED " + hds.getTickerCode());
//				//TODO: add an excpetion throw
//			} else {
//				hds.isReady = true;
//				System.out.println("Ready");
//			}
//		}
		hds.isReady = true;
		hds.notifyReady();
	}

	/**
	 * This method get called whenever is received a CANDLE message. It adds
	 * information to the current {@link CandleSeriesCom}
	 * 
	 * @param data - a key-value collection representing received data
	 */
	private void candle(HashMap<String,String> data) {

		((CandleSeriesCom) tickerFIFO.peek()).addCandle(data.get("date") + data.get("time"),
				Double.parseDouble(data.get("prOff")), Double.parseDouble(data.get("prMin")),
				Double.parseDouble(data.get("prMax")), Double.parseDouble(data.get("prOpen")),
				Long.parseLong(data.get("qty")));

	}

	/**
	 * This method get called whenever is received a TBT message. It adds
	 * information to the current {@link TBTSeriesCom}
	 * 
	 * @param data - a key-value collection representing received data
	 */
	private void tbt(HashMap<String,String> data) {

		((TBTSeriesCom) tickerFIFO.peek()).addTick(data.get("date") + data.get("time"),
				Double.parseDouble(data.get("prOff")), Long.parseLong(data.get("qty")));

	}

}
