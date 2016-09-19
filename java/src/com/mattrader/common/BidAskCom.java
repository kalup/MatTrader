package com.mattrader.common;

/**
 *	This class models a bid/ask structure
 *
 * @author Luca Poletti
 *
 */
public class BidAskCom {

	private LogCom log;
	
	private BidAskDataCom bid;
	private BidAskDataCom ask;
	
	private final String timestamp;
	
	/**
	 * BidAskCom constructor. It should be package visible
	 * 
	 * @param qtyBid	Volume of the bid
	 * @param nPropBid	Number of proposal of the bid
	 * @param prBid		Price of the bid
	 * @param qtyAsk	Volume of the ask
	 * @param nPropAsk	Number of proposal of the ask
	 * @param prAsk		Price of the ask
	 * @param timestamp	Timestamp of the transaction
	 */
	BidAskCom(long qtyBid, long nPropBid, double prBid, long qtyAsk, long nPropAsk, double prAsk,
			String timestamp, DarwinClientBaseCom dcb) {

		log = dcb.log();
		log.fff(this, "constructor; timestamp: " + timestamp);

		this.bid = new BidAskDataCom(qtyBid, nPropBid, prBid, timestamp);
		this.ask = new BidAskDataCom(qtyAsk, nPropAsk, prAsk, timestamp);
		this.timestamp = timestamp;
	}
	
	/**
	 * Returns a reference to the bid
	 * 
	 * @return a BidAskDataCom object that represent bid data
	 */
	public BidAskDataCom bid() {
		return bid;
	}

	
	/**
	 * Returns a reference to the ask
	 * 
	 * @return a BidAskDataCom object that represent ask data
	 */
	public BidAskDataCom ask() {
		return ask;
	}

	/**
	 * Returns the timestamp of when the transaction occurred
	 * 
	 * @return the timestamp
	 */
	public String timestamp() {
		return timestamp;
	}

}
