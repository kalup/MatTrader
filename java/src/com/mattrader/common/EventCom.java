package com.mattrader.common;

import java.util.EventObject;


public abstract class EventCom {

	/**
	 * This class is a specific implementation of an {@link EventObject}
	 * which get fired when the ticker receive a registry update
	 * 
	 * @see EventManagerCom
	 * 
	 * @author Luca Poletti
	 *
	 */
	public static class AnagEvent extends EventObject {
		private static final long serialVersionUID = -6469418519553929407L;

		private final TickerCom ticker;

		private final String ISIN;
		private final String desc;
		private final double prClose;
		private final double prOpen;
		private final long freeFloat;

		private final String timestamp;

		AnagEvent(TickerCom caller, String ISIN, String desc, double prClose, double prOpen, long freeFloat,
				String timestamp) {
			super(caller);
			this.ISIN = ISIN;
			this.desc = desc;
			this.prClose = prClose;
			this.prOpen = prOpen;
			this.freeFloat = freeFloat;
			this.timestamp = timestamp;
			this.ticker = caller;
		}

		/**
		 * @return the ticker that dispatched the event
		 */
		public TickerCom ticker() {
			return ticker;
		}

		/**
		 * @return the ISIN
		 */
		public String ISIN() {
			return ISIN;
		}

		/**
		 * @return the description of the ticker
		 */
		public String description() {
			return desc;
		}

		/**
		 * @return the reference price
		 */
		public double referencePrice() {
			return prClose;
		}

		/**
		 * @return the opening price
		 */
		public double openPrice() {
			return prOpen;
		}

		/**
		 * @return the free float
		 */
		public long freeFloat() {
			return freeFloat;
		}

		public String timestamp() {
			return timestamp;
		}
	}

	/**
	 * This class is a specific implementation of an {@link EventObject}
	 * which get fired when the ticker receive a price update
	 * 
	 * @see EventManagerCom
	 * 
	 * @author Luca Poletti
	 *
	 */
	public static class PriceEvent extends EventObject {
		private static final long serialVersionUID = -769423774933384212L;

		private final TickerCom ticker;

		private final double price;
		private final double priceMin;
		private final double priceMax;
		private final long volume;
		private final long progStocks;
		private final long progExchanges;

		private final String timestamp;

		PriceEvent(TickerCom caller, PriceDataCom priceData) {
			super(caller);
			this.price = priceData.price();
			this.priceMin = priceData.priceMin();
			this.priceMax = priceData.priceMax();
			this.volume = priceData.volume();
			this.progStocks = priceData.progStocks();
			this.progExchanges = priceData.progExchanges();
			this.timestamp = priceData.timestamp();
			this.ticker = caller;
		}

		/**
		 * @return the ticker that dispatched the event
		 */
		public TickerCom ticker() {
			return ticker;
		}

		/**
		 * @return the price
		 */
		public double price() {
			return price;
		}

		/**
		 * @return the priceMin
		 */
		public double priceMin() {
			return priceMin;
		}

		/**
		 * @return the priceMax
		 */
		public double priceMax() {
			return priceMax;
		}

		/**
		 * @return the qty
		 */
		public long volume() {
			return volume;
		}

		/**
		 * @return the progStocks
		 */
		public long progStocks() {
			return progStocks;
		}

		/**
		 * @return the progExchanges
		 */
		public long progExchanges() {
			return progExchanges;
		}

		/**
		 * @return the timestamp
		 */
		public String timestamp() {
			return timestamp;
		}
	}

	/**
	 * This class is a specific implementation of an {@link EventObject}
	 * which get fired when the ticker receive a price auction update
	 * 
	 * @see EventManagerCom
	 * 
	 * @author Luca Poletti
	 *
	 */
	public static class PriceAuctEvent extends EventObject {
		private static final long serialVersionUID = 7248217967816303704L;

		private final TickerCom ticker;

		private final double price;
		private final String timestamp;

		PriceAuctEvent(TickerCom caller, double price, String timestamp) {
			super(caller);
			this.price = price;
			this.timestamp = timestamp;
			this.ticker = caller;
		}

		/**
		 * @return the ticker that dispatched the event
		 */
		public TickerCom ticker() {
			return ticker;
		}

		public double price() {
			return price;
		}

		public String timestamp() {
			return timestamp;
		}
	}

	/**
	 * This class is a specific implementation of an {@link EventObject}
	 * which get fired when the ticker receive a book5 update
	 * 
	 * @see EventManagerCom
	 * 
	 * @author Luca Poletti
	 *
	 */
	public static class Book5Event extends EventObject {
		private static final long serialVersionUID = -6661322575335679750L;

		private final TickerCom ticker;

		private final Book5Com book5;
		private String timestamp;

		Book5Event(TickerCom caller, Book5Com book5) {
			super(caller);
			this.book5 = book5;
			this.timestamp = book5.timestamp();
			this.ticker = caller;
		}

		/**
		 * @return the ticker that dispatched the event
		 */
		public TickerCom ticker() {
			return ticker;
		}

		public Book5Com book5() {
			return book5;
		}

		public String timestamp() {
			return timestamp;
		}
	}

	/**
	 * This class is a specific implementation of an {@link EventObject}
	 * which get fired when the ticker receive a bid/ask update
	 * 
	 * @see EventManagerCom
	 * 
	 * @author Luca Poletti
	 *
	 */
	public static class BidAskEvent extends EventObject {
		private static final long serialVersionUID = -5604899237679624286L;

		private final TickerCom ticker;

		private final BidAskCom bidask;
		private final String timestamp;

		BidAskEvent(TickerCom caller, BidAskCom bidask) {
			super(caller);
			this.bidask = bidask;
			this.timestamp = bidask.timestamp();
			this.ticker = caller;
		}

		/**
		 * @return the ticker that dispatched the event
		 */
		public TickerCom ticker() {
			return ticker;
		}

		public BidAskCom bidask() {
			return bidask;
		}

		public String timestamp() {
			return timestamp;
		}
	}

	/**
	 * This class is a specific implementation of an {@link EventObject}
	 * which get fired when an OrderCom receive an update
	 * 
	 * @see EventManagerCom
	 * 
	 * @author Luca Poletti
	 *
	 */
	public static class TradeEvent extends EventObject {
		private static final long serialVersionUID = -1938404150025827531L;

		private final TickerCom ticker;

		private final OrderCom order;

		TradeEvent(TickerCom caller, OrderCom order) {
			super(caller);
			this.order = order;
			this.ticker = caller;
		}

		TradeEvent(OrderCom caller, TickerCom ticker) {
			super(caller);
			this.order = caller;
			this.ticker = ticker;
		}

		/**
		 * @return the ticker that dispatched the event
		 */
		public TickerCom ticker() {
			return ticker;
		}

		/**
		 * @return the order
		 */
		public OrderCom order() {
			return order;
		}
	}

	/**
	 * This class is a specific implementation of an {@link EventObject}
	 * which get fired when there is an update to the {@link StockCom}
	 * 
	 * @see EventManagerCom
	 * 
	 * @author Luca Poletti
	 *
	 */
	public static class StockEvent extends EventObject {
		private static final long serialVersionUID = -4601709345375922626L;

		private final TickerCom ticker;

		private final StockCom stock;
		private final String timestamp;

		StockEvent(TickerCom caller, StockCom stock) {
			super(caller);
			this.stock = stock;
			this.timestamp = stock.timestamp();
			this.ticker = caller;
		}

		/**
		 * @return the ticker that dispatched the event
		 */
		public TickerCom ticker() {
			return ticker;
		}

		/**
		 * @return the stock
		 */
		public StockCom stock() {
			return stock;
		}

		/**
		 * @return the timestamp
		 */
		public String timestamp() {
			return timestamp;
		}
	}

	/**
	 * This class is a specific implementation of an {@link EventObject}
	 * which get fired when an HistoricalDataSeriesCom is ready and populated
	 * 
	 * @see EventManagerCom
	 * 
	 * @author Luca Poletti
	 *
	 */
	public static class SeriesReadyEvent extends EventObject {
		private static final long serialVersionUID = -1938404150025827531L;

		private final TickerCom ticker;

		private final HistoricalDataSeriesCom series;

		/**
		 * Constructor
		 * 
		 * @param caller - the {@link HistoricalDataSeriesCom} object who fired the event
		 * @param ticker - the {@link TickerCom} ticker whose this series refer to
		 */
		SeriesReadyEvent(HistoricalDataSeriesCom caller, TickerCom ticker) {
			super(caller);
			this.series = caller;
			this.ticker = ticker;
		}

		/**
		 * @return the ticker that dispatched the event
		 */
		public TickerCom ticker() {
			return ticker;
		}

		/**
		 * @return the series
		 */
		public HistoricalDataSeriesCom series() {
			return series;
		}
	}

	/**
	 * This class is a specific implementation of an {@link EventObject}
	 * which get fired when an HistoricalDataSeriesCom is ready and populated
	 * 
	 * @see EventManagerCom
	 * 
	 * @author Luca Poletti
	 *
	 */
	public static class TableReadyEvent extends EventObject {
		private static final long serialVersionUID = -1938404150025827531L;

		private final TickerCom ticker;

		private final HistoricalTableCom table;

		/**
		 * Constructor
		 * 
		 * @param caller - the {@link HistoricalTableCom} object who fired the event
		 * @param ticker - the {@link TickerCom} ticker whose this series refer to
		 */
		TableReadyEvent(HistoricalTableCom caller, TickerCom ticker) {
			super(caller);
			this.table = caller;
			this.ticker = ticker;
		}

		/**
		 * @return the ticker
		 */
		public TickerCom ticker() {
			return ticker;
		}

		/**
		 * @return the table that dispatched the event
		 */
		public HistoricalTableCom table() {
			return table;
		}
	}

	/**
	 * This class is a specific implementation of an {@link EventObject}
	 * which get fired when an error occurs
	 * 
	 * @see EventManagerCom
	 * 
	 * @author Luca Poletti
	 *
	 */
	public static class ErrorEvent extends EventObject {

		private static final long serialVersionUID = -6871674801858100669L;

		private Object caller;
		private String errorDesc;
		private int errorCode;
		
		/**
		 * Constructor
		 * 
		 * @param caller the object who fired the event
		 * @param errorDesc a string containing the description of the error
		 * @param errorCode an int representing the code of the error. See Darwin
		 * wiki for error codes. If it is an Exception, the errorCode will be -1.
		 */
		ErrorEvent(Object caller, String errorDesc, int errorCode) {
			super(caller);
			this.caller = caller;
			this.errorDesc = errorDesc;
			this.errorCode = errorCode;
		}

		/**
		 * @return the name of the class who rose the error
		 */
		public String caller() {
			return caller.getClass().getName();
		}

		/**
		 * @return a string containing a description of the error
		 */
		public String description() {
			return errorDesc;
		}

		/**
		 * @return the code of the error if raised by Darwin or -1 if raised internally
		 */
		public int errorCode() {
			return errorCode;
		}
	}

}
