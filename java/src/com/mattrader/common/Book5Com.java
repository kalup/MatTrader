package com.mattrader.common;

/**
 *	This class models a book structure
 *
 * @author Luca Poletti
 *
 */
public class Book5Com {
	
	private LogCom log;
	
	private BidAskCom[] levels;

	private Book5DataCom bid;
	private Book5DataCom ask;
	
	private final String timestamp;
	
	/**
	 * Book5Com constructor. It should be package visible
	 * 
	 * @param l0		level-0 bid/ask
	 * @param l1		level-1 bid/ask
	 * @param l2		level-2 bid/ask
	 * @param l3		level-3 bid/ask
	 * @param l4		level-4 bid/ask
	 * @param timestamp	Timestamp of the transaction
	 * @param dcb		Client
	 */
	Book5Com(BidAskCom l0, BidAskCom l1, BidAskCom l2, BidAskCom l3, BidAskCom l4,
			String timestamp, MTClientBaseCom dcb) {

		log = dcb.log();
		log.fff(this, "constructor; timestamp: " + timestamp);
		
		levels = new BidAskCom[5];

		levels[0] = l0;
		levels[1] = l1;
		levels[2] = l2;
		levels[3] = l3;
		levels[4] = l4;

		bid = new Book5DataCom(
				new long[] {l0.bid().volume(), l1.bid().volume(), l2.bid().volume(),
						l3.bid().volume(), l4.bid().volume()},
				new long[] {l0.bid().offers(), l1.bid().offers(), l2.bid().offers(),
						l3.bid().offers(), l4.bid().offers()},
				new double[] {l0.bid().price(), l1.bid().price(), l2.bid().price(),
						l3.bid().price(), l4.bid().price()},
				timestamp);
		ask = new Book5DataCom(
				new long[] {l0.ask().volume(), l1.ask().volume(), l2.ask().volume(),
						l3.ask().volume(), l4.ask().volume()},
				new long[] {l0.ask().offers(), l1.ask().offers(), l2.ask().offers(),
						l3.ask().offers(), l4.ask().offers()},
				new double[] {l0.ask().price(), l1.ask().price(), l2.ask().price(),
						l3.ask().price(), l4.ask().price()},
				timestamp);

		this.timestamp = timestamp;
	}
	
	/**
	 * Returns a reference to the bid
	 * 
	 * @return a Book5DataCom object that represent bid data
	 */
	public Book5DataCom bid() {
		return bid;
	}
	
	/**
	 * Returns a reference to the ask
	 * 
	 * @return a Book5DataCom object that represent ask data
	 */
	public Book5DataCom ask() {
		return ask;
	}
	
	/**
	 * Returns a level of the book specified by the input parameter
	 * 
	 * @param 	level	the level of the book
	 * @return	a BidAskCom object representing the level required
	 */
	public BidAskCom level(int level) {
		if(level < 0 && level >= 5)
			return null;
		return levels[level];
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
