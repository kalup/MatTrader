package com.mattrader.common;

/**
 * A class extending {@link ServiceCom} specialized in the connection to the History service
 * 
 * @author Luca Poletti
 *
 */
public class TradingServiceCom extends ServiceCom {

	/**
	 * Constructor, it prepare the structure, but it neither creates any connection
	 * nor spawns any thread
	 * 
	 * @param callerDcb - the client that creating this service
	 */
	TradingServiceCom(MTClientBaseCom callerDcb) {	// We ensure this class can't be instantiated
													// by external apps
		super(callerDcb);
	}

	/* (non-Javadoc)
	 * @see com.mattrader.common.ServiceCom#getPort()
	 */
	@Override
	public int getPort() {
		return dcb.TRADING_PORT;
	}
	
	// Trading specific functions

	/**
	 * Low level call. Request List of stocks in the portfolio
	 * and in negotiation
	 */
	void d_INFOSTOCKS() {
		d__println("INFOSTOCKS");
	}
	
	/**
	 * Low level call. Request info about the situation of the
	 * availability in the portfolio
	 */
	void d_INFOAVAILABILITY() {
		d__println("INFOAVAILABILITY");
	}
	
	/**
	 * Low level call. Returns information about the position
	 * of a single ticker in the same format as the INFOSTOCKS
	 * command
	 * 
	 * @param ticker - the ticker we are interested in
	 */
	void d_GETPOSITION(String ticker) {
		d__println("GETPOSITION " + ticker);
	}
	
	/**
	 * Low level call. Request information about the asset
	 * balance of the account
	 */
	void d_INFOACCOUNT() {
		d__println("INFOACCOUNT");
	}
	
	/**
	 * Low level call. Returns the orders list of any ticker
	 */
	void d_ORDERLIST() {
		d__println("ORDERLIST");
	}
	
	// TODO Handle with an OrderManagerCom

	/**
	 * Low level call. Send an order for buying a defined amount
	 * of stocks at a specified price for the specified ticker
	 * given an order ID
	 * 
	 * @param idOrder - order unique identifier
	 * @param ticker - the ticker
	 * @param qty - the amount
	 * @param price - the price
	 * 
	 * @see TickerCom#buy(double, int)
	 */
	void d_ACQAZ(String idOrder, String ticker, int qty, double price) {
		d__println("ACQAZ " + idOrder + "," + ticker + "," + qty + "," + price);
	}
	
//	void d_ACQAZ_OrderManager(String ticker, int qty, double price) {
//		d__println("ORDERLIST");
//	}
	
	/**
	 * Low level call. Send an order for selling a defined amount
	 * of stocks at a specified price for the specified ticker
	 * given an order ID
	 * 
	 * @param idOrder - order unique identifier
	 * @param ticker - the ticker
	 * @param qty - the amount
	 * @param price - the price
	 * 
	 * @see TickerCom#sell(double, int)
	 */
	void d_VENAZ(String idOrder, String ticker, int qty, double price) {
		d__println("VENAZ " + idOrder + "," + ticker + "," + qty + "," + price);
	}
	
//	void d_VENAZ_OrderManager(String ticker, int qty, double price) {
//		d__println("ORDERLIST");
//	}

	/**
	 * Low level call. Send an order for buying a defined amount
	 * of stocks at market price for the specified ticker
	 * given an order ID
	 * 
	 * @param idOrder - order unique identifier
	 * @param ticker - the ticker
	 * @param qty - the amount
	 * 
	 * @see TickerCom#buy(int)
	 */
	void d_ACQMARK(String idOrder, String ticker, int qty) {
		d__println("ACQMARK " + idOrder + "," + ticker + "," + qty);
	}
	
//	void d_ACQMARK_OrderManager(String ticker, int qty) {
//		d__println("ORDERLIST");
//	}
	
	/**
	 * Low level call. Send an order for selling a defined amount
	 * of stocks at market price for the specified ticker
	 * given an order ID
	 * 
	 * @param idOrder - order unique identifier
	 * @param ticker - the ticker
	 * @param qty - the amount
	 * 
	 * @see TickerCom#sell(int)
	 */
	void d_VENMARK(String idOrder, String ticker, int qty) {
		d__println("VENMARK " + idOrder + "," + ticker + "," + qty);
	}
	
//	void d_VENMARK_OrderManager(String ticker, int qty) {
//		d__println("ORDERLIST");
//	}

	/**
	 * Low level call. Send an order for buying a defined amount
	 * of stocks at market price for the specified ticker
	 * given an order ID, when the price reach a defined signal
	 * 
	 * @param idOrder - order unique identifier
	 * @param ticker - the ticker
	 * @param qty - the amount
	 * @param signal - the signal
	 * 
	 * @see TickerCom#buyToCover(int, double)
	 */
	void d_ACQSTOP(String idOrder, String ticker, int qty, double signal) {
		d__println("ACQSTOP " + idOrder + "," + ticker + "," + qty + "," + signal);
	}
	
//	void d_ACQSTOP_OrderManager(String ticker, int qty, double signal) {
//		d__println("ORDERLIST");
//	}
	
	/**
	 * Low level call. Send an order for selling a defined amount
	 * of stocks at market price for the specified ticker
	 * given an order ID, when the price reach a defined signal
	 * 
	 * @param idOrder - order unique identifier
	 * @param ticker - the ticker
	 * @param qty - the amount
	 * @param signal - the signal
	 * 
	 * @see TickerCom#stopLoss(int, double)
	 */
	void d_VENSTOP(String idOrder, String ticker, int qty, double signal) {
		d__println("VENSTOP " + idOrder + "," + ticker + "," + qty + "," + signal);
	}
	
//	void d_VENSTOP_OrderManager(String ticker, int qty, double signal) {
//		d__println("ORDERLIST");
//	}

	/**
	 * Low level call. Send an order for buying a defined amount
	 * of stocks at a specified price for the specified ticker
	 * given an order ID, when the price reach a defined signal
	 * 
	 * @param idOrder - order unique identifier
	 * @param ticker - the ticker
	 * @param qty - the amount
	 * @param price - the price
	 * @param signal - the signal
	 * 
	 * @see TickerCom#buyToCover(int, double)
	 */
	void d_ACQSTOPLIMIT(String idOrder, String ticker, int qty, double price, double signal) {
		d__println("ACQSTOPLIMIT " + idOrder + "," + ticker + "," + qty + "," + price + "," + signal);
	}
	
//	void d_ACQSTOPLIMIT_OrderManager(String ticker, int qty, double price, double signal) {
//		d__println("ORDERLIST");
//	}
	
	/**
	 * Low level call. Send an order for selling a defined amount
	 * of stocks at a specified price for the specified ticker
	 * given an order ID, when the price reach a defined signal
	 * 
	 * @param idOrder - order unique identifier
	 * @param ticker - the ticker
	 * @param qty - the amount
	 * @param price - the price
	 * @param signal - the signal
	 * 
	 * @see TickerCom#stopLoss(int, double)
	 */
	void d_VENSTOPLIMIT(String idOrder, String ticker, int qty, double price, double signal) {
		d__println("VENSTOPLIMIT " + idOrder + "," + ticker + "," + qty + "," + price + "," + signal);
	}
	
//	void d_VENSTOPLIMIT_OrderManager(String ticker, int qty, double price, double signal) {
//		d__println("ORDERLIST");
//	}
	
	/**
	 * Low level call. Revoke an order
	 * 
	 * @param idOrder - the unique identifier of the order to
	 * revoke
	 * 
	 * @see TickerCom#revoke(OrderCom)
	 */
	void d_REVORD(String idOrder) {
		d__println("REVORD " + idOrder);
	}
	
	/**
	 * Low level call. Revoke any order of the specified ticker
	 * 
	 * @param ticker - ticker code
	 * 
	 * @see TickerCom#revokeAll()
	 */
	void d_REVALL(String ticker) {
		d__println("REVALL " + ticker);
	}
	
	/**
	 * Low level call. Confirm given order after that a
	 * confirmation has been required
	 * 
	 * @param idOrder - the unique identifier of the order that
	 * need confirmation
	 * 
	 * @see TickerCom#confirm(OrderCom)
	 */
	void d_CONFORD(String idOrder) {
		d__println("CONFORD " + idOrder);
	}
	
	/**
	 * Low level call. Change price for the specified order
	 * 
	 * @param idOrder - the unique identifier of the order
	 * @param price - the new price
	 * 
	 * @see TickerCom#editPrice(OrderCom, double)
	 * @see TickerCom#editPriceAll(double)
	 */
	void d_MODORD(String idOrder, double price) {
		d__println("MODORD " + idOrder + "," + price);
	}
	
	//////// Open and close events ////////

	/* (non-Javadoc)
	 * @see com.mattrader.common.ServiceCom#onOpen()
	 */
	@Override
	public void onOpen() {
		// TODO: Call OrderManagerCom and initialize it (INFOSTOCK)
	}

	/* (non-Javadoc)
	 * @see com.mattrader.common.ServiceCom#onClose()
	 */
	@Override
	public void onClose() {
		// TODO Auto-generated method stub
		
	}

}
