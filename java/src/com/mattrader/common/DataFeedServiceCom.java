package com.mattrader.common;

/**
 * A class extending {@link ServiceCom} specialized in the connection to the DataFeed service
 * 
 * @author Luca Poletti
 *
 */
public class DataFeedServiceCom extends ServiceCom {

	/**
	 * Constructor, it prepare the structure, but it neither creates any connection
	 * nor spawns any thread
	 * 
	 * @param callerDcb - the client that creating this service
	 */
	DataFeedServiceCom(MTClientBaseCom callerDcb) {
		super(callerDcb);
	}

	/* (non-Javadoc)
	 * @see com.mattrader.common.ServiceCom#getPort()
	 */
	@Override
	public int getPort() {
		return dcb.DATAFEED_PORT;
	}
	
	// DataFeed specific functions
	
	/**
	 * Low level call. Subscribe a ticker
	 * @param tickers
	 */
	void d_SUB(String tickers) {
		d__println("SUB " + tickers);
	}

	/**
	 * Low level call. Subscribe a ticker
	 * @param tickers
	 */
	void d_SUBALL(String tickers) {
		d__println("SUBALL " + tickers);
	}

	/**
	 * Low level call. Subscribe a ticker
	 * @param tickers
	 */
	void d_SUBPRZ(String tickers) {
		d__println("SUBPRZ " + tickers);
	}

	/**
	 * Low level call. Subscribe a ticker
	 * @param tickers
	 */
	void d_SUBPRZALL(String tickers) {
		d__println("SUBPRZALL " + tickers);
	}

	/**
	 * Low level call. Unsubscribe a ticker
	 * @param tickers
	 */
	void d_UNS(String tickers) {
		d__println("UNS " + tickers);
	}
	
	// TODO: add LOG HANDLER
	
	//////// Open and close events ////////

	/* (non-Javadoc)
	 * @see com.mattrader.common.ServiceCom#onOpen()
	 */
	@Override
	public void onOpen() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.mattrader.common.ServiceCom#onClose()
	 */
	@Override
	public void onClose() {
		// TODO Auto-generated method stub
		
	}

}
