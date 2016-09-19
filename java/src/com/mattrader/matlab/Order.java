package com.mattrader.matlab;

import com.mattrader.common.EventManagerCom;
import com.mattrader.common.OrderCom;

public class Order {
	
	public static final char[][] ACQAZ = {"ACQAZ".toCharArray()};
	public static final char[][] VENAZ = {"VENAZ".toCharArray()};
	public static final char[][] ACQMARK = {"ACQMARK".toCharArray()};
	public static final char[][] VENMARK = {"VENMARK".toCharArray()};
	public static final char[][] ACQSTOP = {"ACQSTOP".toCharArray()};
	public static final char[][] VENSTOP = {"VENSTOP".toCharArray()};
	public static final char[][] ACQSTOPLIM = {"ACQSTOPLIM".toCharArray()};
	public static final char[][] VENSTOPLIM = {"VENSTOPLIM".toCharArray()};

	public static final int ERROR = -1;
	public static final int PENDING = 0;
	public static final int RECEIVED = 3000;
	public static final int EXECUTED = 3001;
	public static final int CANCELLED = 3002;
	public static final int CONFIRMATION_NEEDED = 3003;

	private final OrderCom order;

	Order(OrderCom order) throws Exception {
		this.order = order;
		if(order == null)
			throw new Exception();
	}

	OrderCom getOrderCom() {
		return order;
	}

	/**
	 * @return the time
	 */
	public char[][] getTime() {
		return Utils.toMatlabChar(order.getTime());
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(String time) {
		order.setTime(time);
	}

	/**
	 * @return the operationType
	 */
	public char[][] getOperationType() {
		return Utils.toMatlabChar(order.getOperationType());
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return order.getStatus();
	}

	/**
	 * @return the price
	 */
	public double getPrice() {
		return order.getPrice();
	}

	/**
	 * @return the priceSignal
	 */
	public double getPriceSignal() {
		return order.getPriceSignal();
	}

	/**
	 * @return the quantity
	 */
	public double getQuantity() {
		return (double)order.getQuantity();
	}

	/**
	 * @return a String that describe the situation of the order, if available
	 */
	public char[][] getDetails()  {
		return Utils.toMatlabChar(order.getDetails());
	}

	/**
	 * @return the orderId
	 */
	public char[][] getOrderId() {
		return Utils.toMatlabChar(order.getOrderId());
	}

	/**
	 * @return the ticker
	 */
	public Ticker getTicker() {
		try {
			return new Ticker(order.getTicker());
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Revoke this order
	 * 
	 * @see Ticker#revoke(Order)
	 */
	public void revoke() {
		order.revoke();
	}
	
	public EventManagerCom onTradeEventManager() {
		return order.onTradeEventManager();
	}
	
	public static int convertStausCode(int code) {
		int result = Order.ERROR;
		if(code == 0)
			result = Order.PENDING;
		else if(code == 3000 || code == 2000 || code == 2002)
			result = Order.RECEIVED;
		else if(code == 3001 || code == 2003)
			result = Order.EXECUTED;
		else if(code == 3002 || code == 2004)
			result = Order.CANCELLED;
		else if(code == 3003 || code == 2005)
			result = Order.CONFIRMATION_NEEDED;
		return result;
	}

}
