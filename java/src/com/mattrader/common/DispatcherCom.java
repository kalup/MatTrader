package com.mattrader.common;

import java.util.HashMap;

/**
 * An instance of this class collects data from some {@link ServiceCom} instances and dispatch
 * messages to the correct receivers
 * @author Luca Poletti
 *
 */
class DispatcherCom {
	
	private LogCom log;

	private HashMap<String,TickerCom> subTicker;
	private DarwinClientBaseCom dcb;
	
	/**
	 * Constructor
	 * @param dcb - the {@link DarwinClientBaseCom} client which will be served by the instance
	 * of this class
	 */
	DispatcherCom(DarwinClientBaseCom dcb) {

		log = dcb.log();
		log.c(this, "constructor");
		
		this.subTicker = new HashMap<String,TickerCom>();
		this.dcb = dcb;
	}
	
	/**
	 * Add the {@link TickerCom} ticker to the list of those who will receive messages
	 * from this dispatcher
	 * 
	 * @param ticker - The ticker requesting subscription.
	 * @return true if the ticker was already registered and it has been updated
	 */
	synchronized boolean subscribe(TickerCom ticker) {
		log.i(this, "ticker subscribe: " + ticker.tickerCode);

		String tickerCode = ticker.tickerCode;
		boolean contained = subTicker.containsKey(tickerCode);
		subTicker.put(tickerCode, ticker);
		return contained;
	}

	/**
	 * Remove the {@link TickerCom} ticker from the list of those who will receive 
	 * messages from this dispatcher
	 * 
	 * @param ticker - The ticker requesting unsubscription.
	 */
	synchronized void unsubscribe(TickerCom ticker) {
		this.unsubscribe(ticker.tickerCode);
	}

	/**
	 * Remove the {@link TickerCom} ticker from the list of those who will receive 
	 * messages from this dispatcher
	 * 
	 * @param tickerCode - A string representing the ticker requesting unsubscription.
	 */
	synchronized void unsubscribe(String tickerCode) {
		log.i(this, "ticker unsubscribe: " + tickerCode);

		subTicker.remove(tickerCode);
	}
	
//	public void dispatch(String tickerCode) {
//		if(subTicker.containsKey(tickerCode))
//			subTicker.get(tickerCode).receiveNotification();
//	}
	
	/**
	 * Dispatch a message received by Darwin to recipients
	 * @param input - a String received by Darwin
	 */
	void dispatch(String input) {
		if(input.compareTo("H") != 0)
			log.ff(this, input);
		else
			log.fff(this, input);

		MessageCom message = new MessageCom(input, dcb);
		dispatch(message);
	}

	/**
	 * Dispatch a {@link MessageCom} message received by 
	 * {@link DispatcherCom#dispatch(String)} to recipients
	 * @param message - a {@link MessageCom} to send to recipients
	 */
	void dispatch(MessageCom message) {
		String messDest = message.getDest();
		HashMap<String, String> messData = message.getData();
		if(messDest.equals(MessageCom.TICKER)) {
			// dispatch message to the ticker;
			TickerCom subTarget = subTicker.get(messData.get("ticker"));
			if(subTarget != null)
				subTarget.receiveMessage(message);
		}
		if(messDest.equals(MessageCom.HIST_MANAGER)) {
			dcb.histCallsMan.receiveMessage(message);
		}
		if(messDest.equals(MessageCom.ORDER_MANAGER)) {
			dcb.orderMan.receiveMessage(message);
		}
	}
	
	/**
	 * Dispatch a {@link MessageCom} message received by 
	 * {@link DispatcherCom#dispatch(String)} to a new recipient
	 * defined by newDest
	 * @param message - a {@link MessageCom} to send to recipients
	 * @param newDest - a String describing the new recipient of the message
	 */
	void redispatch(MessageCom message, String newDest) {
		message.messDest = newDest;
		dispatch(message);
	}
	
	/**
	 * Retrieve the {@link TickerCom} linked to the ticker code passed as 
	 * a parameter
	 * @param tickerCode - code of the ticker we are interested in
	 * @return a {@link TickerCom} whose code is equal to the one passed 
	 * as a parameter
	 */
	TickerCom getSubscription(String tickerCode) {
		return subTicker.get(tickerCode);
	}

}
