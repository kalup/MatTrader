package com.mattrader.matlab;

import com.mattrader.common.BidAskCom;

public class BidAsk {

	private final BidAskCom ba;

	private BidAskData bid = null;
	private BidAskData ask = null;
	private boolean isBidSet = false;
	private boolean isAskSet = false;
	
	BidAsk(BidAskCom ba) throws Exception {
		this.ba = ba;
		if(ba == null)
			throw new Exception();
	}
	
	/**
	 * Returns a reference to the bid
	 * 
	 * @return a BidAskDataCom object that represent bid data
	 */
	public BidAskData bid() {
		if(isBidSet && bid != null)
			return bid;
		try {
			isBidSet = true;
			return new BidAskData(ba.bid());
		} catch (Exception e) {
			isBidSet = false;
			return null;
		}
	}

	
	/**
	 * Returns a reference to the ask
	 * 
	 * @return a BidAskDataCom object that represent ask data
	 */
	public BidAskData ask() {
		if(isAskSet && ask != null)
			return ask;
		try {
			isAskSet = true;
			return new BidAskData(ba.ask());
		} catch (Exception e) {
			isAskSet = false;
			return null;
		}
	}

	/**
	 * Returns the timestamp of when the transaction occurred
	 * 
	 * @return the timestamp
	 */
	public char[][] timestamp() {
		return Utils.toMatlabChar(ba.timestamp());
	}

}
