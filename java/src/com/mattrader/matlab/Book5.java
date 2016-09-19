package com.mattrader.matlab;

import com.mattrader.common.Book5Com;

public class Book5 {

	private final Book5Com b;

	private Book5Data bid = null;
	private Book5Data ask = null;
	private boolean isBidSet = false;
	private boolean isAskSet = false;
	
	Book5(Book5Com b) throws Exception {
		this.b = b;
		if(b == null)
			throw new Exception();
	}
	
	/**
	 * Returns a reference to the bid
	 * 
	 * @return a Book5DataCom object that represent bid data
	 */
	public Book5Data bid() {
		if(isBidSet && bid != null)
			return bid;
		try {
			isBidSet = true;
			return new Book5Data(b.bid());
		} catch (Exception e) {
			isBidSet = false;
			return null;
		}
	}
	
	/**
	 * Returns a reference to the ask
	 * 
	 * @return a Book5DataCom object that represent ask data
	 */
	public Book5Data ask() {
		if(isAskSet && ask != null)
			return ask;
		try {
			isAskSet = true;
			return new Book5Data(b.ask());
		} catch (Exception e) {
			isAskSet = false;
			return null;
		}
	}
	
	/**
	 * Returns a level of the book specified by the input parameter
	 * 
	 * @param 	level	the level of the book
	 * @return	a BidAskCom object representing the level required
	 */
	public BidAsk level(int level) {
		if(level < 0 && level >= 5)
			return null;
		try {
			return new BidAsk(b.level(level));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Returns the timestamp of when the transaction occurred
	 * 
	 * @return the timestamp
	 */
	public char[][] timestamp() {
		return Utils.toMatlabChar(b.timestamp());
	}

}
