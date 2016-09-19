package com.mattrader.common;

/**
 * Introduca un comando del tipo b = tit.book che ritorni una struttura fatta come segue:
 *  b.ask : matrice di 5 righe e 2 colonne; prima colonna ask prices, seconda colonna relativi volumi
 *  b.bid : matrice di 5 righe e 2 colonne; prima colonna bid prices, seconda colonna relativi volumi
 *  b.price : vettore riga di due colonne; prima colonna ultimo prezzo battuto, seconda colonna relativo volume.
 * 
 * @author Luca Poletti
 *
 */
public class BookCom {

	private LogCom log;

	private double[][] bid = new double[5][2];
	private double[][] ask = new double[5][2];
	private double[][] price = new double[1][2];
	
	/**
	 * Constructor
	 * 
	 * @param priceData
	 * @param book
	 * @param bidAsk
	 * @param dcb
	 */
	BookCom(PriceDataCom priceData, Book5Com book5, BidAskCom bidAsk, DarwinClientBaseCom dcb) {

		log = dcb.log();
		log.fff(this, "constructor");

		// Se ho a disposizione il book popolo i livelli
		if(book5 != null) {
			Book5DataCom bid = book5.bid();
			Book5DataCom ask = book5.ask();
			int i = 0;
			for(; i < 5; ++i) {
				this.bid[i][0] = bid.price()[i];
				this.bid[i][1] = bid.volume()[i];
				this.ask[i][0] = ask.price()[i];
				this.ask[i][1] = ask.volume()[i];
			}
		}
		// Se ho a disposizione il bidask, e il timestamp è successivo, allora valuto l'update
		if(bidAsk != null &&
				(bid[1][0] == 0 || bid[1][1] == 0 || ask[1][0] == 0 || ask[1][1] == 0) &&
				(book5 == null || book5 != null && bidAsk.timestamp().compareTo(book5.timestamp()) > 0)) {
			BidAskDataCom bid = bidAsk.bid();
			BidAskDataCom ask = bidAsk.ask();
			this.bid[0][0] = bid.price();
			this.bid[0][1] = bid.volume();
			this.ask[0][0] = ask.price();
			this.ask[0][1] = ask.volume();
		}
		price[0][0] = priceData.price();
		price[0][1] = priceData.volume();
	}

	/**
	 * @return a matrix 5x2 containing in the first column bid prices and in the second column volumes
	 */
	public double[][] bid() {
		return bid;
	}

	/**
	 * @return a matrix 5x2 containing in the first column ask prices and in the second column volumes
	 */
	public double[][] ask() {
		return ask;
	}

	/**
	 * @return price and volume of last exchange
	 */
	public double[][] price() {
		return price;
	}
}
