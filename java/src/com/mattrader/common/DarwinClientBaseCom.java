package com.mattrader.common;

import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

/**
 * Main class that will be used as an interface for Darwin. It provides methods to get a
 * {@link TickerCom} and to start/stop the service.
 * <p>
 * This class connects to Darwin and it's used to retrieve every {@link TickerCom} needed:
 * <pre>
 * {@code
 * DarwinClientBaseCom dcb = new DarwinClientBaseCom();
 * TickerCom fca = dcb.getTicker("FCA");
 * }
 * </pre>
 * 
 * @author Luca Poletti
 *
 */
public class DarwinClientBaseCom {

	private final LogCom log;

	final String clientName;

	static final String hostname = "localhost";

	static final int DATAFEED_PORT = 10001;
	static final int TRADING_PORT = 10002;
	static final int HISTORY_PORT = 10003;
	
	boolean ignoreHeartbeat;
	boolean printOutput;

	final HistoricalCallsManagerCom histCallsMan;
	final DispatcherCom dispatcher;
	final OrderManagerCom orderMan; // maybe not final;
	
	final DataFeedServiceCom dataFeed;
	final HistoryServiceCom history;
	final TradingServiceCom trading;
	
	private Timer heartbeatTimer;
	private static final int heartbeaInterval = 20000;
	
	private Timer stockTimer;
	private int stockInterval = 1000;

	final HashMap<String,TickerCom> tickerList;
	
	// default dimension of the buffer of the session
	private int sessionBufferSize = 10000;
	
	private EventManagerCom errorEventManager;
	
	/**
	 * Construct a DarwinClientBaseCom and initialize connections to Darwin
	 */
	DarwinClientBaseCom(String name) {
		
		clientName = name;
		ignoreHeartbeat = true;
		printOutput = false;
		
		log = new LogCom(this);
		
		errorEventManager = new EventManagerCom(this);
		log.setErrorEventManager(errorEventManager);
		
		log.c(this, "constructor; name: " + name);

		tickerList = new HashMap<String,TickerCom>();

		histCallsMan = new HistoricalCallsManagerCom(this);
		dispatcher = new DispatcherCom(this);
		orderMan = new OrderManagerCom(this);
		
		dataFeed = new DataFeedServiceCom(this);
		history = new HistoryServiceCom(this);
		trading = new TradingServiceCom(this);
		
		openServices();
		
		// TODO add a function to handle hafterhours
		if(history != null)
			history.d_VOLUMEAFTERHOURS("CNT");
	}
	
	/**
	 * Force start services if they are not running
	 */
	public void openServices() {
		log.c(this, "opening services");

		dataFeed.open();
		history.open();
		trading.open();

		if(!(dataFeed.isOpen() && history.isOpen() && trading.isOpen()))
			log.e(this, "error opening some services. Opened services: " + getOpenServices());

		startHeartbeatTimer();
		startStockTimer();
	}
	
	/**
	 * Force stop all services and every TickerCom; it doesn't revoke any order.
	 */
	public void close() { //XXX: implement closeable, autocloseable
		// XXX Is clearing tickerList correct? Or we should keep them alive and implements 'reconnect()'?
		// TODO: use enhanced for [for(TickerCom ticker: tickerList) { do }
//		Iterator<TickerCom> tickerIter = tickerList.values().iterator();
//		TickerCom tTicker;
//		while(tickerIter.hasNext()) {
//			tTicker = tickerIter.next();
//			tTicker.close();
//		}
		
		log.c(this, "close begin");
		
		for(TickerCom ticker : tickerList.values().toArray(new TickerCom[0])) {
			ticker.close();
		}
		
		heartbeatTimer.cancel();
		heartbeatTimer = null;
		
		stockTimer.cancel();
		stockTimer = null;

		// Finally close streams
		dataFeed.close();
		history.close();
		trading.close();
		
		log.c(this, "close end");
	}
	
	/**
	 * Check whether the client is ready to be used
	 * @return true if the client is ready
	 */
	public boolean isReady() {
		return dataFeed.isOpen() && trading.isOpen() && history.isOpen();
	}
	
	/**
	 * Give a list of all available services. 
	 * @return a String containing the list of all connected services
	 */
	public String getOpenServices() {
		String openedServices = "";
		int count = 0;
		if(dataFeed.isOpen()) {
			openedServices += "DataFeed";
			count++;
		}
		if(trading.isOpen()) {
			if(count > 0)
				openedServices += ", ";
			openedServices += "Trading";
			count++;
		}
		if(history.isOpen()) {
			if(count > 0)
				openedServices += ", ";
			openedServices += "History";
			count++;
		}
		if(count == 0)
			openedServices = "-";
		return openedServices;
	}

	/**
	 * Start the timer that will notify {@link ServiceStreamReaderCom}
	 * threads that an heartbeat should have occurred
	 */
	private void startHeartbeatTimer() {
		heartbeatTimer = new Timer();
		heartbeatTimer.scheduleAtFixedRate(
				new TimerTask() {
					@Override
					public void run() {
						//XXX debug for lifecycles
						//System.out.println("[ " + System.currentTimeMillis() + " ] ");
						if(dataFeed.isOpen())
							dataFeed.checkHeartbeat();
						if(history.isOpen())
							history.checkHeartbeat();
						if(trading.isOpen())
							trading.checkHeartbeat();
					}
				},
				heartbeaInterval,
				heartbeaInterval);
	}

	/**
	 * Start the timer that will make requests for fresh stock info
	 */
	private void startStockTimer() {
		stockTimer = new Timer();
		stockTimer.scheduleAtFixedRate(
				new TimerTask() {
					@Override
					public void run() {
						if(trading.isOpen());
							//XXX
							//trading.d_INFOSTOCKS();
					}
				},
				stockInterval,
				stockInterval);
	}
	
	/**
	 * Creates a {@link TickerCom} object with no service activated
	 * @param ticker - the code used by Directa for the stock
	 * @param sessionBufferSize - the dimension of the buffer of the {@link SessionDBCom}
	 * @return the requested {@link TickerCom} object
	 */
	private TickerCom getEmptyTicker(String ticker, int sessionBufferSize) {
		// XXX Check whether the name of tickers may be in english letters only or not;
		log.i(this, "requested new ticker: " + ticker + "; bufferSize: " + sessionBufferSize);
		
		ticker = ticker.toUpperCase(Locale.ENGLISH);
		if(!tickerList.containsKey(ticker)) {
			TickerCom nTicker = new TickerCom(ticker, this, sessionBufferSize);
			tickerList.put(ticker, nTicker);
			return nTicker;
		} else {
			return tickerList.get(ticker);
		}
	}
	
	/**
	 * Creates a {@link TickerCom} object with every service activated and the default
	 * size for the buffer.
	 * @param ticker - the code used by Directa for the stock
	 * @return the requested {@link TickerCom} object
	 * @see DarwinClientBaseCom#defaultSessionBuffer(int)
	 */
	public TickerCom getTicker(String ticker) {
		TickerCom nTicker = getEmptyTicker(ticker, sessionBufferSize);
		nTicker.openService("all");
		return nTicker;
	}
	
	/**
	 * Creates a {@link TickerCom} object with the specified service activated and the
	 * default size for the buffer.
	 * @param ticker - the code used by Directa for the stock
	 * @param service - a String representing the service required ("all", "dataFeed",
	 *  "trading", "history")
	 * @return the requested {@link TickerCom} object
	 * @see DarwinClientBaseCom#defaultSessionBuffer(int)
	 * @see TickerCom#openService(String)
	 */
	public TickerCom getTicker(String ticker, String service) {
		TickerCom nTicker = getEmptyTicker(ticker, sessionBufferSize);
		nTicker.openService(service);
		return nTicker;
	}
	
	/**
	 * Creates a {@link TickerCom} object with the specified services activated and the
	 * default size for the buffer.
	 * @param ticker - the code used by Directa for the stock
	 * @param service1 - a String representing the first service required ("all", "dataFeed",
	 *  "trading", "history")
	 * @param service2 - a String representing the second service required ("all", "dataFeed",
	 *  "trading", "history")
	 * @return the requested {@link TickerCom} object
	 * @see DarwinClientBaseCom#defaultSessionBuffer(int)
	 * @see TickerCom#openService(String)
	 */
	public TickerCom getTicker(String ticker, String service1, String service2) {
		TickerCom nTicker = getEmptyTicker(ticker, sessionBufferSize);
		nTicker.openService(service1);
		nTicker.openService(service2);
		return nTicker;
	}

	/**
	 * Creates a {@link TickerCom} object with every service activated and the specified
	 * size for the buffer.
	 * @param ticker - the code used by Directa for the stock
	 * @param sessionBufferSize - the dimension of the buffer of the {@link SessionDBCom}
	 * @return the requested {@link TickerCom} object
	 */
	public TickerCom getTicker(String ticker, int sessionBufferSize) {
		TickerCom nTicker = getEmptyTicker(ticker, sessionBufferSize);
		nTicker.openService("all");
		return nTicker;
	}

	/**
	 * Creates a {@link TickerCom} object with every service activated and the specified
	 * size for the buffer.
	 * @param ticker - the code used by Directa for the stock
	 * @param service - a String representing the service required ("all", "dataFeed",
	 *  "trading", "history")
	 * @param sessionBufferSize - the dimension of the buffer of the {@link SessionDBCom}
	 * @return the requested {@link TickerCom} object
	 * @see TickerCom#openService(String)
	 */
	public TickerCom getTicker(String ticker, String service, int sessionBufferSize) {
		TickerCom nTicker = getEmptyTicker(ticker, sessionBufferSize);
		nTicker.openService(service);
		return nTicker;
	}

	/**
	 * Creates a {@link TickerCom} object with every service activated and the specified
	 * size for the buffer.
	 * @param ticker - the code used by Directa for the stock
	 * @param service1 - a String representing the first service required ("all", "dataFeed",
	 *  "trading", "history")
	 * @param service2 - a String representing the second service required ("all", "dataFeed",
	 *  "trading", "history")
	 * @param sessionBufferSize - the dimension of the buffer of the {@link SessionDBCom}
	 * @return the requested {@link TickerCom} object
	 * @see TickerCom#openService(String)
	 */
	public TickerCom getTicker(String ticker, String service1, String service2, int sessionBufferSize) {
		TickerCom nTicker = getEmptyTicker(ticker, sessionBufferSize);
		nTicker.openService(service1);
		nTicker.openService(service2);
		return nTicker;
	}

	/**
	 * Close the ticker and remove it from the pool of available tickers
	 * @param ticker - the {@link TickerCom} object to remove
	 */
	public void removeTicker(TickerCom ticker) {
		log.i(this, "remove ticker: " + ticker.tickerCode);
		
		String tickerCode = ticker.tickerCode.toUpperCase(Locale.ENGLISH);
		tickerList.remove(tickerCode);
		// TODO ticker.close
	}
	
	/**
	 * Control if the heartbeat signal should be printed or not
	 * @param ignore - true to avoid printing heartbeat signal
	 */
	public void ignoreHeartbeat(boolean ignore) {
		log.c(this, "ignore heartbeat: " + ignore);
		ignoreHeartbeat = ignore;
	}
	
	/**
	 * Suppress heartbeat signal in the output
	 */
	public void ignoreHeartbeat() {
		log.c(this, "ignore heartbeat: true");
		ignoreHeartbeat = true;
	}
	
	/**
	 * Control if received messages must be shown in console
	 * @param printBool - true to print data in console
	 */
	public void printOutput(boolean printBool) {
		log.c(this, "print output: " + printBool);
		printOutput = printBool;
	}
	
	/**
	 * Show all messages received in console
	 */
	public void printOutput() {
		log.c(this, "print output: true");
		printOutput = true;
	}
	
	/**
	 * This method change the default size of the buffer of the {@link SessionDBCom}
	 * for new tickers. Tickers already opened are not affected. Default size is 10000.
	 * 
	 * @param sessionBufferSize
	 */
	public void defaultSessionBuffer(int sessionBufferSize) {
		log.c(this, "change default session buffer size; previous: "
				+ this.sessionBufferSize + "; new :" + sessionBufferSize); 
		this.sessionBufferSize = sessionBufferSize;
	}
	
	/**
	 * Set the log level specifying which message levels will be logged by the console.
	 * Message levels lower than this value will be discarded.
	 * <p>
	 * The level values are:
	 * <p>
	 *  - ALL - any level is logged
	 * <br>
	 *  - OFF - no level is logged
	 * <br>
	 *  - SEVERE - only SEVERE error are logged
	 * <br>
	 *  - WARNING - only WARNING or higher levels are logged
	 * <br>
	 *  - INFO - only info or INFO levels are logged
	 * <br>
	 *  - CONFIG - only CONFIG or higher levels are logged
	 * <br>
	 *  - FINE - only FINE or higher levels are logged
	 * <br>
	 *  - FINER - only FINER or higher levels are logged
	 * <br>
	 *  - FINEST - only FINEST or higher levels are logged
	 * <br>
	 *  - null - this node inherit its level from its nearest
	 *  ancestor with a specific (non-null) level value
	 * @param newLevel - a {@link String} representing the new value for the log level (may be null)
	 */
	synchronized void changeConsoleLogLevel(String newLevel) {
		log.c(this, "change default log level in console:" + newLevel);

		Level level = null;
		if(newLevel.toUpperCase() == "ALL")
			level = Level.ALL;
		else if(newLevel.toUpperCase() == "CONFIG")
			level = Level.CONFIG;
		else if(newLevel.toUpperCase() == "FINE")
			level = Level.FINE;
		else if(newLevel.toUpperCase() == "FINER")
			level = Level.FINER;
		else if(newLevel.toUpperCase() == "FINEST")
			level = Level.FINEST;
		else if(newLevel.toUpperCase() == "INFO")
			level = Level.INFO;
		else if(newLevel.toUpperCase() == "OFF")
			level = Level.OFF;
		else if(newLevel.toUpperCase() == "SEVERE")
			level = Level.SEVERE;
		else if(newLevel.toUpperCase() == "WARNING")
			level = Level.WARNING;

		log.changeConsoleLogLevel(level);
	}
	
	/**
	 * Set the log level specifying which message levels will be logged by the console.
	 * Message levels lower than this value will be discarded.
	 * <p>
	 * The level values are:
	 * <p>
	 *  - ALL - any level is logged
	 * <br>
	 *  - OFF - no level is logged
	 * <br>
	 *  - SEVERE - only SEVERE error are logged
	 * <br>
	 *  - WARNING - only WARNING or higher levels are logged
	 * <br>
	 *  - INFO - only info or INFO levels are logged
	 * <br>
	 *  - CONFIG - only CONFIG or higher levels are logged
	 * <br>
	 *  - FINE - only FINE or higher levels are logged
	 * <br>
	 *  - FINER - only FINER or higher levels are logged
	 * <br>
	 *  - FINEST - only FINEST or higher levels are logged
	 * <br>
	 *  - null - this node inherit its level from its nearest ancestor with a specific (non-null) level value
	 * @param newLevel - a {@link String} representing the new value for the log level (may be null)
	 */
	synchronized void changeFileLogLevel(String newLevel) {
		log.c(this, "change default log level of log file:" + newLevel);
		
		Level level = null;
		if(newLevel.toUpperCase() == "ALL")
			level = Level.ALL;
		else if(newLevel.toUpperCase() == "CONFIG")
			level = Level.CONFIG;
		else if(newLevel.toUpperCase() == "FINE")
			level = Level.FINE;
		else if(newLevel.toUpperCase() == "FINER")
			level = Level.FINER;
		else if(newLevel.toUpperCase() == "FINEST")
			level = Level.FINEST;
		else if(newLevel.toUpperCase() == "INFO")
			level = Level.INFO;
		else if(newLevel.toUpperCase() == "OFF")
			level = Level.OFF;
		else if(newLevel.toUpperCase() == "SEVERE")
			level = Level.SEVERE;
		else if(newLevel.toUpperCase() == "WARNING")
			level = Level.WARNING;

		log.changeFileLogLevel(level);
	}

	/**
	 * Package visible, used to retrieve the {@link LogCom} object associated
	 * to this client
	 * @return the log object
	 */
	LogCom log() {
		return log;
	}

	/**
	 * This method returns an {@link EventManagerCom} that will collect
	 * {@link EventCom.PriceAuctEvent} and trigger callbacks
	 * 
	 * @return the {@link EventManagerCom} specific for {@link EventCom.PriceAuctEvent}
	 * @see EventManagerCom
	 */
	public EventManagerCom onErrorEventManager() {
		return errorEventManager;
	}
	
}
