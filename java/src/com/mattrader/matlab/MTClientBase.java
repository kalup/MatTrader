package com.mattrader.matlab;

import com.mattrader.common.MTClientBaseCom;
import com.mattrader.common.EventCom;
import com.mattrader.common.EventManagerCom;

public class MTClientBase {

	private final MTClientBaseCom dcb;

	MTClientBase(MTClientBaseCom dcb) throws Exception {
		if(dcb == null)
			throw new Exception();
		this.dcb = dcb;
	}

	/**
	 * Force start of services if they aren't running
	 */
	public void openServices() {
		dcb.openServices();
	}
	
	/**
	 * Force stop all services and every TickerCom; it doesn't revoke any order.
	 */
	public void close() {
		dcb.close();
	}
	
	public boolean isReady() {
		return dcb.isReady();
	}
	
	/**
	 * Return a String containing the list of all connected services
	 * @return
	 */
	public char[][] getOpenServices() {
		return Utils.toMatlabChar(dcb.getOpenServices());
	}
	
	public Ticker getTicker(String ticker) {
		try {
			return new Ticker(dcb.getTicker(ticker));
		} catch (Exception e) {
			return null;
		}
	}
	
	public Ticker getTicker(String ticker, String service) {
		try {
			return new Ticker(dcb.getTicker(ticker, service));
		} catch (Exception e) {
			return null;
		}
	}
	
	public Ticker getTicker(String ticker, String service1, String service2) {
		try {
			return new Ticker(dcb.getTicker(ticker, service1, service2));
		} catch (Exception e) {
			return null;
		}
	}
	
	public Ticker getTicker(String ticker, int sessionBufferSize) {
		try {
			return new Ticker(dcb.getTicker(ticker, sessionBufferSize));
		} catch (Exception e) {
			return null;
		}
	}
	
	public Ticker getTicker(String ticker, String service, int sessionBufferSize) {
		try {
			return new Ticker(dcb.getTicker(ticker, service, sessionBufferSize));
		} catch (Exception e) {
			return null;
		}
	}
	
	public Ticker getTicker(String ticker, String service1, String service2, int sessionBufferSize) {
		try {
			return new Ticker(dcb.getTicker(ticker, service1, service2, sessionBufferSize));
		} catch (Exception e) {
			return null;
		}
	}
	
	void removeTicker(Ticker ticker) { // why not public?
		dcb.removeTicker(ticker.ticker);
	}
	
	public void ignoreHeartbeat(boolean ignore) {
		dcb.ignoreHeartbeat(ignore);
	}
	
	public void ignoreHeartbeat() {
		dcb.ignoreHeartbeat();
	}
	
	public void printOutput(boolean printBool) {
		dcb.printOutput(printBool);
	}
	
	public void printOutput() {
		dcb.printOutput();
	}
	
	/**
	 * This method change the default size of the buffer for new Tickers. Tickers already opened are not affected. Default size is 10000.
	 * 
	 * @param sessionBufferSize
	 */
	public void defaultSessionBuffer(int sessionBufferSize) {
		dcb.defaultSessionBuffer(sessionBufferSize);
	}

	/**
	 * This method returns an {@link EventManagerCom} that will collect
	 * {@link EventCom.PriceAuctEvent} and trigger callbacks
	 * 
	 * @return the {@link EventManagerCom} specific for {@link EventCom.PriceAuctEvent}
	 * @see EventManagerCom
	 */
	public EventManagerCom onErrorEventManager() {
		return dcb.onErrorEventManager();
	}

}
