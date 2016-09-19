package com.mattrader.matlab;

import com.mattrader.common.SnapshotCom;

public class Snapshot {

	private final BidAsk bidAsk;
	private final Book5 book;
	private final PriceData priceData;
	
	Snapshot(SnapshotCom s) throws Exception {
		if(s == null)
			throw new Exception();
		bidAsk = new BidAsk(s.getBidAsk());
		book = new Book5(s.getBook());
		priceData = new PriceData(s.getPriceData());
	}

	/**
	 * @return the bidAsk
	 */
	public BidAsk getBidAsk() {
		return bidAsk;
	}

	/**
	 * @return book at five levels
	 */
	public Book5 getBook() {
		return book;
	}

	/**
	 * @return the priceData
	 */
	public PriceData getPriceData() {
		return priceData;
	}
}
