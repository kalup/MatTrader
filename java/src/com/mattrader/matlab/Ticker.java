package com.mattrader.matlab;

import java.util.ArrayList;

import com.mattrader.common.EventCom;
import com.mattrader.common.EventManagerCom;
import com.mattrader.common.HistoricalTableCom;
import com.mattrader.common.OrderCom;
import com.mattrader.common.TickerCom;

public class Ticker {

	final TickerCom ticker; // not private, see DarwinClientBase.resizeBuffer()
	
	private boolean isSessionSet = false;
	private SessionDB session = null;

	Ticker(TickerCom ticker) throws Exception {
		this.ticker = ticker;
		if(ticker == null)
			throw new Exception();
	}
	
	public void close() {
		ticker.close();
	}
	
	public void openService(String service) {
		ticker.openService(service);
	}
	
	public void closeService(String service) {
		ticker.closeService(service);
	}

	public Order[] getOrderList() {
		ArrayList<OrderCom> orderList = ticker.getOrderList();
		Order[] returnOrder = new Order[orderList.size()];
		int i = 0;
		for(OrderCom orderCom : orderList)
			try {
				returnOrder[i] = new Order(orderCom);
				i++;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return returnOrder;
	}
	
	public SessionDB getSession() {
		if(session != null && isSessionSet)
			return session;
		try {
			session = new SessionDB(ticker.getSession());
			if(session != null)
				isSessionSet = true;
			return session;
		} catch (Exception e) {
			isSessionSet = false;
			return null;
		}
	}
	
	public void flush() {
		ticker.flush();
	}
	
	public void resizeSessionBuffer(int bufferMaxSize) {
		ticker.resizeSessionBuffer(bufferMaxSize);
	}
	
	public char[][] tickerCode() {
		return Utils.toMatlabChar(ticker.tickerCode());
	}

	/**
	 * @return the ISIN
	 */
	public char[][] ISIN() {
		return Utils.toMatlabChar(ticker.ISIN());
	}

	/**
	 * @return the description of the ticker
	 */
	public char[][] description() {
		return Utils.toMatlabChar(ticker.description());
	}

	/**
	 * @return the reference price
	 */
	public double referencePrice() {
		return ticker.referencePrice();
	}

	/**
	 * @return the opening price
	 */
	public double openPrice() {
		return ticker.openPrice();
	}

	/**
	 * @return the free float
	 */
	public double freeFloat() {
		return (double)ticker.freeFloat();
	}

	/**
	 * @return the price
	 */
	public double priceAuct() {
		return ticker.priceAuct();
	}

	/**
	 * @return a snapshot of the actual situation
	 */
	public Snapshot now() {
		try {
			Snapshot s = new Snapshot(ticker.now());
			return s;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * @return a snapshot of the actual situation in matrix form
	 */
	public Book book() {
		try {
			return new Book(ticker.book());
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @return the price
	 */
	public double price() {
		return ticker.price();
	}

	/**
	 * @return stocks volume
	 */
	public double volume() {
		return (double)ticker.volume();
	}

	/**
	 * @return the progressive amount of stocks
	 */
	public double progStocks() {
		return (double)ticker.progStocks();
	}

	/**
	 * @return the progressive amount of exchanges
	 */
	public double progExchanges() {
		return (double)ticker.progExchanges();
	}

	/**
	 * @return the minimum of the day
	 */
	public double dailyMin() {
		return ticker.dailyMin();
	}

	/**
	 * @return the maximum of the day
	 */
	public double dailyMax() {
		return ticker.dailyMax();
	}

	/**
	 * @return bid/ask
	 */
	public BidAsk bidAsk() {
		try {
			return new BidAsk(ticker.bidAsk());
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @return 5 level book
	 */
	public Book5 book5() {
		try {
			return new Book5(ticker.book5());
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @return the spread, bid/ask difference
	 */
	public double spread() {
		return ticker.spread();
	}
	
	public double stockPrice() {
		return ticker.stockPrice();
	}
	
	public TBTSeries getTBT(int days) {
		try {
			return new TBTSeries(ticker.getTBT(days));
		} catch (Exception e) {
			return null;
		}
	}

	public TBTSeries getTBT(String dayTimeB, String dayTimeE) {
		try {
			return new TBTSeries(ticker.getTBT(dayTimeB, dayTimeE));
		} catch (Exception e) {
			return null;
		}
	}

	public CandleSeries getCANDLE(int days, int period) {
		try {
			return new CandleSeries(ticker.getCANDLE(days, period));
		} catch (Exception e) {
			return null;
		}
	}
	
	public CandleSeries getCANDLE(String dayTimeB, String dayTimeE, int period) {
		try {
			return new CandleSeries(ticker.getCANDLE(dayTimeB, dayTimeE, period));
		} catch (Exception e) {
			return null;
		}
	}
	
	public Stock getStock() {
		try {
			return new Stock(ticker.getStock());
		} catch (Exception e) {
			return null;
		}
	}
	
	public HistoricalTable hist(int days, String period) {
		HistoricalTableCom hist = ticker.hist(days, period);
		if(hist == null)
			return null;
		try {
			return new HistoricalTable(hist);
		} catch (Exception e) {
			return null;
		}
	}

	public HistoricalTable hist(String date, String period, int number) {
		HistoricalTableCom hist = ticker.hist(date, period, number);
		if(hist == null)
			return null;
		try {
			return new HistoricalTable(hist);
		} catch (Exception e) {
			return null;
		}
		
	}

	public HistoricalTable hist(String dayTimeB, String dayTimeE, String period) {
		HistoricalTableCom hist = ticker.hist(dayTimeB, dayTimeE, period);
		if(hist == null)
			return null;
		try {
			return new HistoricalTable(hist);
		} catch (Exception e) {
			return null;
		}
	}

	public Order buy(double price, int qty) {
		try {
			return new Order(ticker.buy(price, qty));
		} catch (Exception e) {
			return null;
		}
	}

	public Order sell(double price, int qty) {
		try {
			return new Order(ticker.sell(price, qty));
		} catch (Exception e) {
			return null;
		}
	}

	public Order buy(int qty) {
		try {
			return new Order(ticker.buy(qty));
		} catch (Exception e) {
			return null;
		}
	}

	public Order sell(int qty) {
		try {
			return new Order(ticker.sell(qty));
		} catch (Exception e) {
			return null;
		}
	}

	public Order buyToCover(double price, int qty, double signal) {
		try {
			return new Order(ticker.buyToCover(price, qty, signal));
		} catch (Exception e) {
			return null;
		}
	}

	public Order stopLoss(double price, int qty, double signal) {
		try {
			return new Order(ticker.stopLoss(price, qty, signal));
		} catch (Exception e) {
			return null;
		}
	}

	public Order buyToCover(int qty, double signal) {
		try {
			return new Order(ticker.buyToCover(qty, signal));
		} catch (Exception e) {
			return null;
		}
	}

	public Order stopLoss(int qty, double signal) {
		try {
			return new Order(ticker.stopLoss(qty, signal));
		} catch (Exception e) {
			return null;
		}
	}
	
	public void revoke(Order order) {
		ticker.revoke(order.getOrderCom());
	}
	
	public void revokeAll() {
		ticker.revokeAll();
	}
	
	public void confirm(Order order) {
		ticker.confirm(order.getOrderCom());
	}
	
	public void editPrice(Order order, double price) {
		ticker.editPrice(order.getOrderCom(), price);
	}
	
	public void editPriceAll(double price) {
		ticker.editPriceAll(price);
	}
	
	// Event related methods
	
	public EventManagerCom onAnagEventManager() {
		return ticker.onAnagEventManager();
	}
	
	public EventManagerCom onPriceEventManager() {
		return ticker.onPriceEventManager();
	}
	
	public EventManagerCom onPriceAuctEventManager() {
		return ticker.onPriceAuctEventManager();
	}
	
	public EventManagerCom onBook5EventManager() {
		return ticker.onBook5EventManager();
	}
	
	public EventManagerCom onBidAskEventManager() {
		return ticker.onBidAskEventManager();
	}
	
	public EventManagerCom onTradeEventManager() {
		return ticker.onTradeEventManager();
	}

	/**
	 * This method returns an {@link EventManagerCom} that will collect
	 * {@link EventCom.StockEvent} and trigger callbacks
	 * 
	 * @return the {@link EventManagerCom} specific for {@link EventCom.StockEvent}
	 * @see EventManagerCom
	 */
	public EventManagerCom onStockUpdateEventManager() {
		return ticker.onStockUpdateEventManager();
	}

}
