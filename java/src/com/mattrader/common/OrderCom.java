package com.mattrader.common;


/**
 * This class will model an order, giving information on the status (error,
 * pending, received, executed, cancelled, confirmation_needed) and handling
 * data specific of the order.
 * 
 * @author Luca Poletti
 *
 */
public final class OrderCom {

	private LogCom log;
	
	public static final String ACQAZ = "ACQAZ";
	public static final String VENAZ = "VENAZ";
	public static final String ACQMARK = "ACQMARK";
	public static final String VENMARK = "VENMARK";
	public static final String ACQSTOP = "ACQSTOP";
	public static final String VENSTOP = "VENSTOP";
	public static final String ACQSTOPLIM = "ACQSTOPLIM";
	public static final String VENSTOPLIM = "VENSTOPLIM";

	public static final int ERROR = -1;
	public static final int PENDING = 0;
	public static final int RECEIVED = 3000;
	public static final int EXECUTED = 3001;
	public static final int CANCELLED = 3002;
	public static final int CONFIRMATION_NEEDED = 3003;

	final String orderId;
	final TickerCom ticker;
	private String time; // TODO: use Calendar maybe?
	private /*final*/ String operationType;
	private int code;
	private double price;
	private double priceSignal;
	private int quantity;
	private String orderDetails;
	
	private EventManagerCom tradeEventManager;
	
	/**
	 * Constructor, it will prepare an empty structure that will be filled when data
	 * will be available
	 * 
	 * @param orderId - an unique ID that identify the order
	 * @param ticker - the ticker the order refers to
	 * @param dcb - the client which requested the transaction
	 */
	OrderCom(String orderId, TickerCom ticker, DarwinClientBaseCom dcb) {

		log = dcb.log();
		log.ff(this, "constructor; orderID: " + orderId + "; ticker: " + ticker.tickerCode);

		this.orderId = orderId;
		this.ticker = ticker;
		code = OrderCom.PENDING;
		operationType = null;
		code = 0; // FIXME duplicate
		price = 0;
		priceSignal = 0;
		quantity = 0;
		orderDetails = "";

		tradeEventManager = new EventManagerCom(dcb);
	}

	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(String time) {
		// FIXME dove lo uso? A che mi serve? Perché è pubblico?
		this.time = time;
	}

	/**
	 * @return the operationType
	 */
	public String getOperationType() {
		return operationType;
	}

	/**
	 * @param operationType the operationType to set
	 */
	void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return code;
	}

	/**
	 * @param status the status to set
	 */
	void setStatus(int status) {
		log.ff(this, "orderID: " + this.orderId + "status set: " + this.getStatus());
		this.code = status;
	}

	/**
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	void setPrice(double price) {
		this.price = price;
	}

	/**
	 * @return the priceSignal
	 */
	public double getPriceSignal() {
		return priceSignal;
	}

	/**
	 * @param priceSignal the priceSignal to set
	 */
	void setPriceSignal(double priceSignal) {
		this.priceSignal = priceSignal;
	}

	/**
	 * @return the quantity
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the quantity
	 */
	public String getDetails() {
		return orderDetails;
	}

	/**
	 * @param details - additional details
	 */
	void setDetails(String details) {
		this.orderDetails = details;
	}

	/**
	 * @return the orderId
	 */
	public String getOrderId() {
		return orderId;
	}

	/**
	 * @return the ticker
	 */
	public TickerCom getTicker() {
		return ticker;
	}
	
	/**
	 * Revoke this order
	 * 
	 * @see TickerCom#revoke(OrderCom)
	 */
	public void revoke() {
		ticker.revoke(this);
	}
	
	/**
	 * Convert the numerical status received from Darwin into a code
	 * defined in that class
	 * @param code - the numerical status
	 * @return a class specific code
	 */
	public static int convertStausCode(int code) {
		int result = OrderCom.ERROR;
		if(code == 0)
			result = OrderCom.PENDING;
		else if(code == 3000 || code == 2000 || code == 2002)
			result = OrderCom.RECEIVED;
		else if(code == 3001 || code == 2003)
			result = OrderCom.EXECUTED;
		else if(code == 3002 || code == 2004)
			result = OrderCom.CANCELLED;
		else if(code == 3003 || code == 2005)
			result = OrderCom.CONFIRMATION_NEEDED;
		return result;
	}
	
	/**
	 * This method collect the notification that specify the order has been
	 * updated.
	 * This will fire a {@link EventCom.TradeEvent}
	 */
	void notifyEvent() {
		tradeEventManager.receivedEvent(new EventCom.TradeEvent(this, ticker));
	}

	/**
	 * This method returns an {@link EventManagerCom} that will collect
	 * {@link EventCom.TradeEvent} and trigger callbacks
	 * 
	 * @return the {@link EventManagerCom} specific for {@link EventCom.TradeEvent}
	 * @see EventManagerCom
	 */
	public EventManagerCom onTradeEventManager() {
		return tradeEventManager;
	}
}
