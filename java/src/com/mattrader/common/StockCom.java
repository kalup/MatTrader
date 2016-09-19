package com.mattrader.common;

/**
 * This class collect data about the stock of a ticker.
 * <p>
 * An instance of this class is not immutable.
 * 
 * @author Luca Poletti
 *
 */
public class StockCom {

	private LogCom log;
	
	private TickerCom tickerCaller;

	private String ticker;
	private double avgPrice;
	private long qtyPort;
	private long qtyDir;
	private long qtyNeg;
	private double gain;
	private String timestamp;

	/**
	 * Constructor
	 * 
	 * @param caller - the ticker this stock refers to
	 * @param dcb - the client this stock belong to
	 */
	StockCom(TickerCom caller, DarwinClientBaseCom dcb) {

		log = dcb.log();
		log.c(this, "constructor: " + caller.tickerCode);
		this.ticker = "";
		this.avgPrice = 0;
		this.qtyPort = 0;
		this.qtyDir = 0;
		this.qtyNeg = 0;
		this.gain = 0;
		this.timestamp = "";
		tickerCaller = caller;
	}
	
	// perché aggiornare lo stock e non istanziarne ogni volta uno nuovo? Non rischio per di più thread
	// issues?
	/**
	 * Update data within the stock.
	 * 
	 * @param ticker - name of the ticker
	 * @param avgPrice - average price at which the transactions took place
	 * @param qtyPort - quantity of stocks in portfolio
	 * @param qtyDir - quantity of stocks owned by Directa
	 * @param qtyNeg - quantity of stocks in negotiation
	 * @param gain - gain/loss
	 * @param timestamp - timestamp at which data are sampled
	 */
	void update(String ticker, double avgPrice, long qtyPort, long qtyDir, long qtyNeg, double gain,
			String timestamp) {
		log.i(this, "update: " + timestamp);
		this.ticker = ticker;
		this.avgPrice = avgPrice;
		this.qtyPort = qtyPort;
		this.qtyDir = qtyDir;
		this.qtyNeg = qtyNeg;
		this.gain = gain;
		this.timestamp = timestamp;
	}

	/**
	 * @return the ticker
	 */
	public String ticker() {
		return ticker;
	}

	/**
	 * @return the avgPrice
	 */
	public double price() {
		return avgPrice;
	}

	/**
	 * @return the qtyPort
	 */
	public long qtyPortfolio() {
		return qtyPort;
	}

	/**
	 * @return the qtyDir
	 */
	public long qtyDirecta() {
		return qtyDir;
	}

	/**
	 * @return the qtyNeg
	 */
	public long qtyNegotiation() {
		return qtyNeg;
	}

	/**
	 * @return the gain in €
	 */
	public double gain() {
		return gain;
	}

	/**
	 * @return the timestamp
	 */
	public String timestamp() {
		return timestamp;
	}
	
	/**
	 * Request an update of the stock to Darwin
	 */
	public void update() {
		tickerCaller.dcb.trading.d_GETPOSITION(ticker);
	}

}
