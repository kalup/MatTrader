package com.mattrader.common;

/**
 * An immutable snapshot of data. It is used to have an immutable reference to prices
 * and book levels data that doesn't vary between different queries.
 * <p>
 * It keep track of bidAsk, book5 and priceData.
 * 
 * @author Luca Poletti
 *
 */
public class SnapshotCom {

	private LogCom log;

	private BidAskCom bidAsk;
	private Book5Com book;
	private PriceDataCom priceData;
	
	/**
	 * Constructor
	 * 
	 * @param priceData
	 * @param book
	 * @param bidAsk
	 * @param dcb
	 */
	SnapshotCom(PriceDataCom priceData, Book5Com book, BidAskCom bidAsk, DarwinClientBaseCom dcb) {

		log = dcb.log();
		log.fff(this, "constructor");

		this.bidAsk = bidAsk;
		this.book = book;
		this.priceData = priceData;
	}

	/**
	 * @return the bidAsk
	 */
	public BidAskCom getBidAsk() {
		return bidAsk;
	}

	/**
	 * @return book at five levels
	 */
	public Book5Com getBook() {
		return book;
	}

	/**
	 * @return the priceData
	 */
	public PriceDataCom getPriceData() {
		return priceData;
	}
}
