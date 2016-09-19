package com.mattrader.common;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.Hashtable;

/** Wrap a candle, display data in a useful way */

/**
 * This class is an higher level class, it is a wrapper for a candle, it provide
 * methods for easier query of data and a different way of representing them. It
 * is built having in mind the way Yahoo! Finance shows historical data
 * (<a href="https://it.finance.yahoo.com/q/hp?s=FCA.MI">example</a>).
 * <p>
 * Notes:
 * <p>
 *  - Data are sorted in <b>ascending</b> temporal order
 * <br>
 *  - If data are requested while the market, in which the ticker lives, is open,
 *  then those data will contains in the last raw information relative the current
 *  date; so the last raw may be <b>inconsistent</b> because contains live
 *  informations.
 * <br>
 * @author Luca Poletti
 *
 */
public class HistoricalTableCom implements EventManagerCom.DirectaConnectorListener {

	private LogCom log;
	
	/**
	 * 
	 */
	final static Hashtable<String, Integer> period = new Hashtable<String, Integer>() {
		private static final long serialVersionUID = -1476495028769360878L;

		{ // TODO implement W, M, Y; directly Directa doesn't give those data; aggregate by hand
			put("s",1);
			put("m",60);
			put("h",3600);
			put("D",86400);
//			put("W",604800);
//			put("M",2592000);
//			put("Y",31536000);
		}
	};

	private CandleSeriesCom candle;
	
	private ArrayList<Double> openingPrice;
	private ArrayList<Double> closePrice;
	private ArrayList<Double> maxPrice;
	private ArrayList<Double> minPrice;
	private ArrayList<Long> volume;
	private ArrayList<String> date;
	
	private int maxRows = 0;
	
	private Hashtable<String,ArrayList<?>> table;
	
	private boolean initialized;
	
	private EventManagerCom readyEventManager;
	
	/**
	 * Constructor
	 * 
	 * @param candle - the {@link CandleSeriesCom} containing information
	 * @param dcb - the {@link DarwinClientBaseCom} client whose table refers to
	 */
	HistoricalTableCom(CandleSeriesCom candle, DarwinClientBaseCom dcb) {

		log = dcb.log();
		log.ff(this,"constructor");

		this.candle = candle;
		initialized = false;

		table = new Hashtable<String,ArrayList<?>>();
		openingPrice = new ArrayList<Double>();
		closePrice = new ArrayList<Double>();
		maxPrice = new ArrayList<Double>();
		minPrice = new ArrayList<Double>();
		volume = new ArrayList<Long>();
		date = new ArrayList<String>();
		
		candle.onReadyEventManager().addDirectaConnectorListener(this);
		
		readyEventManager = new EventManagerCom(dcb);

		init();
	}
	
	/**
	 * Constructor
	 * 
	 * @param candle the {@link CandleSeriesCom} containing information
	 * @param maxRow the maximum number of entries this table must keep
	 * @param dcb the {@link DarwinClientBaseCom} client whose table refers to
	 */
	HistoricalTableCom(CandleSeriesCom candle, int maxRows, DarwinClientBaseCom dcb) {

		log = dcb.log();
		log.ff(this,"constructor");

		this.candle = candle;
		initialized = false;
		this.maxRows = maxRows;

		table = new Hashtable<String,ArrayList<?>>();
		openingPrice = new ArrayList<Double>();
		closePrice = new ArrayList<Double>();
		maxPrice = new ArrayList<Double>();
		minPrice = new ArrayList<Double>();
		volume = new ArrayList<Long>();
		date = new ArrayList<String>();
		
		candle.onReadyEventManager().addDirectaConnectorListener(this);
		
		readyEventManager = new EventManagerCom(dcb);

		init();
	}
	
	/**
	 * This method load data from the candle into this table structure
	 */
	private void init() {
		if(!initialized && candle.isReady()) {
			int size = candle.getVolumes().size();
			log.w(this, "init "+size+maxRows);
			int begin = size - maxRows;
			if(maxRows != 0 && maxRows < size) {
				openingPrice = new ArrayList<Double>(candle.getOpenPrices().subList(begin, size));
				closePrice = new ArrayList<Double>(candle.getOffPrices().subList(begin, size));
				maxPrice = new ArrayList<Double>(candle.getMaxPrices().subList(begin, size));
				minPrice = new ArrayList<Double>(candle.getMinPrices().subList(begin, size));
				volume = new ArrayList<Long>(candle.getVolumes().subList(begin, size));
				date = new ArrayList<String>(candle.getDateTimeSeries().subList(begin, size));
			} else {
				openingPrice = candle.getOpenPrices();
				closePrice = candle.getOffPrices();
				maxPrice = candle.getMaxPrices();
				minPrice = candle.getMinPrices();
				volume = candle.getVolumes();
				date = candle.getDateTimeSeries();
			}
			
			table = new Hashtable<String,ArrayList<?>>();
			table.put("Date", date);
			table.put("Open", openingPrice);
			table.put("High", maxPrice);
			table.put("Low", minPrice);
			table.put("Close", closePrice);
			table.put("Volume", volume);
			
			initialized = true;
		}
	}
	
	/**
	 * Check whether this table is ready or no
	 * @return true if the table is ready
	 */
	public boolean isReady() {
		init();
		return initialized;
	}

	/**
	 * Returns the open prices
	 * 
	 * @return a list of prices or null if the table is not ready
	 */
	public ArrayList<Double> openingPrice() {
		/*if(initialized)
			return this.openingPrice;
		if(this.isReady()) {
			init();
			return this.openingPrice;
		}
		return candle.getOpenPrices();	// perché mai facevo così? Non ha senso, crea pure dei
											// casini con potenziali race conditions
		 */
		init();
		return this.openingPrice;
	}

	/**
	 * Returns the close prices
	 * 
	 * @return a list of prices or null if the table is not ready
	 */
	public ArrayList<Double> closePrice() {
		init();
		return this.closePrice;
	}

	/**
	 * Returns the high prices
	 * 
	 * @return a list of prices or null if the table is not ready
	 */
	public ArrayList<Double> maxPrice() {
		init();
		return this.openingPrice;
	}

	/**
	 * Returns the low prices
	 * 
	 * @return a list of prices or null if the table is not ready
	 */
	public ArrayList<Double> minPrice() {
		init();
		return this.minPrice;
	}

	/**
	 * Returns volumes
	 * 
	 * @return a list of volumes or null if the table is not ready
	 */
	public ArrayList<Long> volume() {
		init();
		return this.volume;
	}

	/**
	 * Returns date
	 * 
	 * @return a list of String representing dates or null if the table is
	 * not ready
	 */
	public ArrayList<String> date() {
		init();
		return this.date;
	}

	/**
	 * Returns the number of rows this table contains
	 * 
	 * @return the length of the table
	 */
	public int size() {
		init();
		return candle.size();
	}

	/**
	 * Returns the code of the ticker this table refers to
	 * 
	 * @return the code of the ticker this table refers to
	 */
	public String tickerCode() {
		init();
		return candle.getTickerCode();
	}
	
	/**
	 * @return the table containing all the data or null if the table is not
	 * ready. Data are sorted in temporal ascending order. Columns are:
	 * <p>
	 *  - Date;
	 * <br>
	 *  - Open;
	 * <br>
	 *  - High;
	 * <br>
	 *  - Low;
	 * <br>
	 *  - Close;
	 * <br>
	 *  - Volume;
	 * <br><p>
	 * The last row contains data of the current date. If it is open, data
	 * represented may be <b>inconsistent</b>
	 */
	public Hashtable<String, ArrayList<?>> table() {
		init();
		return this.table;
	}

	/**
	 * This method returns an {@link EventManagerCom} that will collect
	 * {@link EventCom.TableReadyEvent} and trigger callback
	 * @return the {@link EventManagerCom} specific for {@link EventCom.TableReadyEvent}
	 * @see EventManagerCom
	 */
	public EventManagerCom onReadyEventManager() {
		return candle.onReadyEventManager();
	}
	
	/**
	 * This method collect the notification that specify the table is ready.
	 * This will fire a {@link EventCom.TableReadyEvent}
	 */
	void notifyReady() {
		readyEventManager.receivedEvent(new EventCom.TableReadyEvent(this, candle.getTicker()));
	}

	@Override
	public void onEvent(EventObject event) {
		if(!initialized)
			notifyReady();
	}


}
