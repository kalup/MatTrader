package com.mattrader.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Set;

/**
 * A manager class for orders. It keep tracks of every order of the client and
 * it provides a method that will propose new unique orderIds on request.
 * 
 * @author Luca Poletti
 *
 */
public final class OrderManagerCom {

	private LogCom log;

	public/*DEBUG private*/ Hashtable<String, OrderCom> orders; //XXX
	private DarwinClientBaseCom dcb;
	private int lastOrdID;

	/**
	 * Constructor
	 * 
	 * @param dcb - the client this order manager belongs to
	 */
	OrderManagerCom(DarwinClientBaseCom dcb) {

		log = dcb.log();
		log.c(this, "constructor");

		orders = new Hashtable<String, OrderCom>();
		this.dcb = dcb;
		lastOrdID = 0;
	}

	/**
	 * Used to insert a new order in the list of those who are handled by this manager
	 * 
	 * @param ticker - the {@link TickerCom} ticker those order refers to
	 * @param operationType - the type of operation performed
	 * @param qty - the amount of stocks required by this order
	 * 
	 * @return an {@link OrderCom} order that will be in a not ready state
	 */
	synchronized OrderCom insertNewOrder(TickerCom ticker, String operationType, int qty) {
		log.f(this, "new order insertion: " + operationType + "; " + ticker.tickerCode);
		String orderId = getNewId();
		if(orderId == null)
			return null;
		OrderCom newOrder = new OrderCom(orderId, ticker, dcb);
		newOrder.setOperationType(operationType);
		newOrder.setQuantity(qty);
		orders.put(orderId, newOrder);
		ticker.registerOrder(newOrder);
		return newOrder;
	}

	/**
	 * Used to insert a new order in the list of those who are handled by this manager
	 * 
	 * @param ticker - the {@link TickerCom} ticker those order refers to
	 * @param operationType - the type of operation performed
	 * @param price - the price this operation is trying to operate
	 * @param qty - the amount of stocks required by this order
	 * 
	 * @return an {@link OrderCom} order that will be in a not ready state
	 */
	synchronized OrderCom insertNewOrder(TickerCom ticker, String operationType, double price, int qty) {
		OrderCom newOrder = insertNewOrder(ticker, operationType, qty);
		newOrder.setPrice(price);
		return newOrder;
	}

	/**
	 * Used to insert a new order in the list of those who are handled by this manager
	 * 
	 * @param ticker - the {@link TickerCom} ticker those order refers to
	 * @param operationType - the type of operation performed
	 * @param qty - the amount of stocks required by this order
	 * @param signal - the price at which the order must be inserted in the market
	 * 
	 * @return an {@link OrderCom} order that will be in a not ready state
	 */
	synchronized OrderCom insertNewOrder(TickerCom ticker, String operationType, int qty, double signal) {
		OrderCom newOrder = insertNewOrder(ticker, operationType, qty);
		newOrder.setPriceSignal(signal);
		return newOrder;
	}

	/**
	 * Used to insert a new order in the list of those who are handled by this manager
	 * 
	 * @param ticker - the {@link TickerCom} ticker those order refers to
	 * @param operationType - the type of operation performed
	 * @param price - the price this operation is trying to operate
	 * @param qty - the amount of stocks required by this order
	 * @param signal - the price at which the order must be inserted in the market
	 * 
	 * @return an {@link OrderCom} order that will be in a not ready state
	 */
	synchronized OrderCom insertNewOrder(TickerCom ticker, String operationType, double price, int qty,
			double signal) {
		OrderCom newOrder = insertNewOrder(ticker, operationType, qty, signal);
		newOrder.setPrice(price);
		return newOrder;
	}

	/**
	 * @return a String representing a new and unique ID for an order
	 */
	synchronized private String getNewId() {
		return "ORD_DM_"+dcb.clientName.toUpperCase(Locale.ENGLISH)+"_"+lastOrdID++;
	}

	/**
	 * @param orderId - ID of the order we are seeking
	 * @return the {@link OrderCom} order associated with that orderId
	 */
	OrderCom getOrder(String orderId) {
		return orders.get(orderId);
	}

	/**
	 * @return a set of orderIds
	 */
	Set<String> getOrderIds() {
		return orders.keySet();
	}
	
	/**
	 * @return a collection of orders
	 */
	Collection<OrderCom> getOrders() {
		return orders.values();
	}

	/**
	 * Use this method to notify the manager that a message relative to an order has been
	 * received
	 * 
	 * @param message - the {@link MessageCom} received
	 */
	void receiveMessage(MessageCom message) {
		String messType = message.getType();
		if(messType.equals(MessageCom.ORDER))
			orderListEntry(message.getData());
	}

	/**
	 * Register or update a new order
	 * 
	 * @param data - data of the {@link MessageCom} received by the manager
	 */
	private void orderListEntry(HashMap<String, String> data) {
		int id;
		String ordID = data.get("ordId");
		OrderCom order;
		// TODO check getTicker Empty
		TickerCom ticker = dcb.getTicker(data.get("ticker"),"none");
		
		if(lastOrdID < (id = Integer.parseInt("0" + ordID.replaceAll("\\D", ""))))
			lastOrdID = id;
		if(!orders.containsKey(ordID)) {
			order = new OrderCom(ordID, ticker, dcb);
			orders.put(ordID, order);
		} else
			order = orders.get(ordID);
		order.setOperationType(data.get("command"));
		order.setPrice(Double.parseDouble("0" + data.get("prLim")));
		order.setPriceSignal(Double.parseDouble("0" + data.get("prSig")));
		order.setQuantity(Integer.parseInt("0" + data.get("qty")));
		
		int status = Integer.parseInt("0" + data.get("orderStatus"));
//		if(status == 2000 || status == 2002)
//			status = 3000;
//		else if(status == 2001)
//			status = 0;
//		else if(status == 2003)
//			status = 3001;
//		else if(status == 2004)
//			status = 3002;
//		else if(status == 2005)
//			status = 3003;
		status = OrderCom.convertStausCode(status);
		order.setStatus(status);

		ticker.registerOrder(order);
	}
	

}
