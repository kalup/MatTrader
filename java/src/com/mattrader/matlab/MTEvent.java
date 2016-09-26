package com.mattrader.matlab;

import java.util.EventObject;

import com.mattrader.common.BidAskCom;
import com.mattrader.common.Book5Com;
import com.mattrader.common.CandleSeriesCom;
import com.mattrader.common.EventCom;
import com.mattrader.common.EventManagerCom;
import com.mattrader.common.HistoricalDataSeriesCom;
import com.mattrader.common.HistoricalTableCom;
import com.mattrader.common.StockCom;
import com.mattrader.common.TBTSeriesCom;
import com.mattrader.common.TickerCom;

public class MTEvent {
	
	public static interface BaseEvent {
	}

	/**
	 * This class is a specific implementation of an {@link EventObject}
	 * which get fired when the ticker receive a registry update
	 * 
	 * @see EventManagerCom
	 * 
	 * @author Luca Poletti
	 *
	 */
	public static class AnagEvent implements BaseEvent {
		
		private final EventCom.AnagEvent e;

		AnagEvent(EventCom.AnagEvent e) throws Exception {
			this.e = e;
			if(e == null)
				throw new Exception();
		}

		/**
		 * @return the ticker that dispatched the event
		 */
		public Ticker ticker() {
			try {
				return new Ticker(e.ticker());
			} catch (Exception e) {
				return null;
			}
		}

		/**
		 * @return the ISIN
		 */
		public char[][] ISIN() {
			return Utils.toMatlabChar(e.ISIN());
		}

		/**
		 * @return the description of the ticker
		 */
		public char[][] description() {
			return Utils.toMatlabChar(e.description());
		}

		/**
		 * @return the reference price
		 */
		public double referencePrice() {
			return e.referencePrice();
		}

		/**
		 * @return the opening price
		 */
		public double openPrice() {
			return e.openPrice();
		}

		/**
		 * @return the free float
		 */
		public double freeFloat() {
			return (double)e.freeFloat();
		}

		public char[][] timestamp() {
			return Utils.toMatlabChar(e.timestamp());
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
	public static class PriceEvent implements BaseEvent {
		
		private final EventCom.PriceEvent e;

		PriceEvent(EventCom.PriceEvent e) throws Exception {
			this.e = e;
			if(e == null)
				throw new Exception();
		}

		/**
		 * @return the ticker that dispatched the event
		 */
		public Ticker ticker() {
			try {
				return new Ticker(e.ticker());
			} catch (Exception e) {
				return null;
			}
		}

		/**
		 * @return the price
		 */
		public double price() {
			return e.price();
		}

		/**
		 * @return the priceMin
		 */
		public double priceMin() {
			return e.priceMin();
		}

		/**
		 * @return the priceMax
		 */
		public double priceMax() {
			return e.priceMax();
		}

		/**
		 * @return the qty
		 */
		public double volume() {
			return (double)e.volume();
		}

		/**
		 * @return the progStocks
		 */
		public double progStocks() {
			return (double)e.progStocks();
		}

		/**
		 * @return the progExchanges
		 */
		public double progExchanges() {
			return e.progExchanges();
		}

		/**
		 * @return the timestamp
		 */
		public char[][] timestamp() {
			return Utils.toMatlabChar(e.timestamp());
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
	public static class PriceAuctEvent implements BaseEvent {

		private final EventCom.PriceAuctEvent e;

		PriceAuctEvent(EventCom.PriceAuctEvent e) throws Exception {
			this.e = e;
			if(e == null)
				throw new Exception();
		}

		/**
		 * @return the ticker that dispatched the event
		 */
		public Ticker ticker() {
			try {
				return new Ticker(e.ticker());
			} catch (Exception e) {
				return null;
			}
		}

		public double price() {
			return e.price();
		}

		public char[][] timestamp() {
			return Utils.toMatlabChar(e.timestamp());
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
	public static class Book5Event implements BaseEvent {

		private final EventCom.Book5Event e;

		Book5Event(EventCom.Book5Event e) throws Exception {
			this.e = e;
			if(e == null)
				throw new Exception();
		}

		/**
		 * @return the ticker that dispatched the event
		 */
		public Ticker ticker() {
			try {
				return new Ticker(e.ticker());
			} catch (Exception e) {
				return null;
			}
		}

		public Book5Com book5() {
			return e.book5();
		}

		public char[][] timestamp() {
			return Utils.toMatlabChar(e.timestamp());
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
	public static class BidAskEvent implements BaseEvent {

		private final EventCom.BidAskEvent e;

		BidAskEvent(EventCom.BidAskEvent e) throws Exception {
			this.e = e;
			if(e == null)
				throw new Exception();
		}

		/**
		 * @return the ticker that dispatched the event
		 */
		public Ticker ticker() {
			try {
				return new Ticker(e.ticker());
			} catch (Exception e) {
				return null;
			}
		}

		public BidAskCom bidask() {
			return e.bidask();
		}

		public char[][] timestamp() {
			return Utils.toMatlabChar(e.timestamp());
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
	public static class TradeEvent implements BaseEvent {

		private final EventCom.TradeEvent e;

		TradeEvent(EventCom.TradeEvent e) throws Exception {
			this.e = e;
			if(e == null)
				throw new Exception();
		}

		/**
		 * @return the ticker that dispatched the event
		 */
		public Ticker ticker() {
			try {
				return new Ticker(e.ticker());
			} catch (Exception e) {
				return null;
			}
		}

		/**
		 * @return the order
		 */
		public Order order() {
			try {
				return new Order(e.order());
			} catch (Exception e) {
				return null;
			}
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
	public static class StockEvent implements BaseEvent {

		private final EventCom.StockEvent e;

		StockEvent(EventCom.StockEvent e) throws Exception {
			this.e = e;
			if(e == null)
				throw new Exception();
		}

		/**
		 * @return the ticker that dispatched the event
		 */
		public Ticker ticker() {
			try {
				return new Ticker(e.ticker());
			} catch (Exception e) {
				return null;
			}
		}

		/**
		 * @return the stock
		 */
		public StockCom stock() {
			return e.stock();
		}

		/**
		 * @return the timestamp
		 */
		public char[][] timestamp() {
			return Utils.toMatlabChar(e.timestamp());
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
	public static class SeriesReadyEvent implements BaseEvent {

		private final EventCom.SeriesReadyEvent e;
		private HistoricalDataSeries h = null;
		private boolean isSeriesSet = false;

		/**
		 * Constructor
		 * 
		 * @param e - the event that will be fired
		 */
		SeriesReadyEvent(EventCom.SeriesReadyEvent e) throws Exception {
			this.e = e;
			if(e == null)
				throw new Exception();
		}

		/**
		 * @return the ticker that dispatched the event
		 */
		public Ticker ticker() {
			try {
				return new Ticker(e.ticker());
			} catch (Exception e) {
				return null;
			}
		}

		/**
		 * @return the series
		 */
		public HistoricalDataSeries series() {
			if(isSeriesSet && h != null)
				return h;
			try {
				HistoricalDataSeriesCom s = e.series();
				if(s instanceof CandleSeriesCom) {
					h = new CandleSeries(s);
					if(h != null)
						isSeriesSet = true;
					return h;
				} else if(s instanceof TBTSeriesCom) {
					h = new TBTSeries(s);
					if(h != null)
						isSeriesSet = true;
					return h;
				} else
					return null;
			} catch (Exception e) {
				return null;
			}
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
	public static class TableReadyEvent implements BaseEvent {

		private final EventCom.TableReadyEvent e;

		private HistoricalTable table = null;
		private boolean isTableSet = false;

		/**
		 * Constructor
		 * 
		 * @param e - the event that will be fired
		 */
		TableReadyEvent(EventCom.TableReadyEvent e) throws Exception {
			this.e = e;
			if(e == null)
				throw new Exception();
		}

		/**
		 * @return the ticker
		 */
		public Ticker ticker() {
			try {
				return new Ticker(e.ticker());
			} catch (Exception e) {
				return null;
			}
		}

		/**
		 * @return the table that dispatched the event
		 */
		public HistoricalTable table() {
			if(isTableSet && table != null)
				return table;
			try {
				table = new HistoricalTable(e.table());
				if(table != null)
					isTableSet = true;
				return table;
			} catch (Exception e) {
				return null;
			}
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
	public static class ErrorEvent implements BaseEvent {

		private final EventCom.ErrorEvent e;
		
		/**
		 * Constructor
		 * 
		 * @param e - the event that will be fired
		 */
		ErrorEvent(EventCom.ErrorEvent e) throws Exception {
			this.e = e;
			if(e == null)
				throw new Exception();
		}

		/**
		 * @return the name of the class who rose the error
		 */
		public char[][] caller() {
			return Utils.toMatlabChar(e.caller());
		}

		/**
		 * @return a string containing a description of the error
		 */
		public char[][] description() {
			return Utils.toMatlabChar(e.description());
		}

		/**
		 * @return the code of the error if raised by Darwin or -1 if raised internally
		 */
		public int errorCode() {
			return e.errorCode();
		}
	}
	
	public static BaseEvent toMatlabEvent(EventObject e) {
		try {
			if(e instanceof EventCom.AnagEvent)
				return new AnagEvent((com.mattrader.common.EventCom.AnagEvent) e);
			else if(e instanceof EventCom.PriceEvent)
				return new PriceEvent((com.mattrader.common.EventCom.PriceEvent) e);
			else if(e instanceof EventCom.PriceAuctEvent)
				return new PriceAuctEvent((com.mattrader.common.EventCom.PriceAuctEvent) e);
			else if(e instanceof EventCom.Book5Event)
				return new Book5Event((com.mattrader.common.EventCom.Book5Event) e);
			else if(e instanceof EventCom.BidAskEvent)
				return new BidAskEvent((com.mattrader.common.EventCom.BidAskEvent) e);
			else if(e instanceof EventCom.TradeEvent)
				return new TradeEvent((com.mattrader.common.EventCom.TradeEvent) e);
			else if(e instanceof EventCom.StockEvent)
				return new StockEvent((com.mattrader.common.EventCom.StockEvent) e);
			else if(e instanceof EventCom.SeriesReadyEvent)
				return new SeriesReadyEvent((com.mattrader.common.EventCom.SeriesReadyEvent) e);
			else if(e instanceof EventCom.TableReadyEvent)
				return new TableReadyEvent((com.mattrader.common.EventCom.TableReadyEvent) e);
			else if(e instanceof EventCom.ErrorEvent)
				return new ErrorEvent((com.mattrader.common.EventCom.ErrorEvent) e);
			else
				return null;
		} catch(Exception ex) {
			return null;
		}
	}

}
