package com.mattrader.common;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;

/**
 * TL;DR This is the main class, has ton of features!
 * <p>
 * [ draft ]
 * <p>
 * It provides methods for opening and closing services.<br>
 * It give you methods to handle {@link OrderCom} orders, {@link SessionDBCom}, 
 * 
 * @author Luca Poletti
 *
 */
public class TickerCom {

	private LogCom log;

	final String tickerCode;
	final DarwinClientBaseCom dcb;
	
	private Hashtable<String, OrderCom> orders; // XXX final?
	
	private final SessionDBCom session;
	
	private String ISIN;
	private String desc;
	private long freeFloat;
	private double prClose;
	private double prOpen;

	private BidAskCom bidAsk;
	private Book5Com book5;
	private PriceDataCom priceData;
	private StockCom stock;

	private double priceAuct;
	
	private boolean historyReady;
	private boolean tradingReady;
	private boolean datafeedReady;
	
	// Event related variables
	private EventManagerCom anagEventManager;
	private EventManagerCom priceEventManager;
	private EventManagerCom priceAuctEventManager;
	private EventManagerCom book5EventManager;
	private EventManagerCom bidAskEventManager;
	private EventManagerCom tradeEventManager;
	private EventManagerCom stockEventManager;

	/**
	 * Constructor
	 * 
	 * @param ticker - a String with the ticker code
	 * @param client - the client that spawn this {@link TickerCom}
	 * @param sessionBufferSize - the maximum size of the buffer for the session
	 * of this Ticker
	 */
	TickerCom(String ticker, DarwinClientBaseCom client, int sessionBufferSize) {
		log = client.log();
		log.c(this, "constructor: " + this.tickerCode);
		
		tickerCode = ticker;
		
		dcb = client;

		anagEventManager = new EventManagerCom(dcb);
		priceEventManager = new EventManagerCom(dcb);
		priceAuctEventManager = new EventManagerCom(dcb);
		book5EventManager = new EventManagerCom(dcb);
		bidAskEventManager = new EventManagerCom(dcb);
		tradeEventManager = new EventManagerCom(dcb);
		stockEventManager = new EventManagerCom(dcb);

		session = new SessionDBCom(sessionBufferSize, dcb);
		
		orders = new Hashtable<String, OrderCom>();

		historyReady = false;
		tradingReady = false;
		datafeedReady = false;

		ISIN = "";
		desc = "";
		freeFloat = 0;
		
		prClose = 0;
		prOpen = 0;

		bidAsk = null;
		book5 = null;
		priceData = null;
		stock = new StockCom(this, dcb);

		priceAuct = 0;

		if(dcb.dataFeed.isOpen()) {
			dcb.dispatcher.subscribe(this);
			dcb.dataFeed.d_SUBALL(ticker);
			// TODO ERROR Handling if datafeed is not open
		}

	}
	
	/**
	 * Open a specific service on this ticker. Possible values are:
	 * ("history", "trading", "datafeed", "all")
	 * 
	 * @param service - a String describing what service 
	 */
	public void openService(String service) {
		log.c(this, "open service: " + service);
		service = service.toLowerCase(Locale.ENGLISH);
		if(service.compareTo("history") != 0 &&
				service.compareTo("trading") != 0 &&
				service.compareTo("datafeed") != 0 &&
				service.compareTo("all") != 0) {
			if(service.compareTo("none") != 0)
				log.w(this, "expected \"history\", \"trading\", \"datafeed\", \"all\" or \"none\"");
			return;
		}
		// XXX we can use an hashmap, but it doesn't worth the effort?
		if(service.compareTo("history") == 0) {

			// while(dcb.history.isOpen() == false)
			if(dcb.history.isOpen() == false)
				dcb.history.open();
			if(dcb.history.isOpen() == true)
				this.historyReady = true;
		} else if(service.compareTo("trading") == 0) {

			// while(dcb.history.isOpen() == false)
			if(dcb.history.isOpen() == false)
				dcb.history.open();
			if(dcb.history.isOpen() == true)
				this.tradingReady = true;
		} else if(service.compareTo("datafeed") == 0) {

			// while(dcb.history.isOpen() == false)
			if(dcb.history.isOpen() == false)
				dcb.history.open();
			if(dcb.history.isOpen() == true)
				this.datafeedReady = true;
		} else if(service.compareTo("all") == 0) {

			// while(dcb.isReady() == false)
			if(dcb.isReady() == false)
				dcb.openServices();
			if(dcb.isReady() == true) {
				this.historyReady = true;
				this.tradingReady = true;
				this.datafeedReady = true;
			}
		}
	}
	
	/**
	 * Close a specific service on this ticker. Possible values are:
	 * ("history", "trading", "datafeed", "all")
	 * 
	 * @param service - a String describing what service 
	 */
	public void closeService(String service) {
		log.c(this, "close service: " + service);
		if(service.toLowerCase(Locale.ENGLISH).compareTo("history") != 0 &&
				service.toLowerCase(Locale.ENGLISH).compareTo("trading") != 0 &&
				service.toLowerCase(Locale.ENGLISH).compareTo("datafeed") != 0 &&
				service.toLowerCase(Locale.ENGLISH).compareTo("all") != 0)
			return;
		if(service.toLowerCase(Locale.ENGLISH).compareTo("history") == 0)
			this.historyReady = false;
		else if(service.toLowerCase(Locale.ENGLISH).compareTo("trading") == 0) {
			this.tradingReady = false;
		}
		else if(service.toLowerCase(Locale.ENGLISH).compareTo("datafeed") == 0) {
			this.datafeedReady = false;
			dcb.dataFeed.d_UNS(this.tickerCode);
		}
		else if(service.toLowerCase(Locale.ENGLISH).compareTo("all") == 0) {
			this.close();
		}
	}

	/**
	 * Close every service
	 */
	public void close() {
		log.c(this, "close: " + this.tickerCode);
		dcb.dispatcher.unsubscribe(this);
		dcb.dataFeed.d_UNS(this.tickerCode);
		dcb.removeTicker(this);
		this.historyReady = false;
		this.tradingReady = false;
		this.datafeedReady = false;
	}

	/**
	 * Not working already!!!
	 * 
	 * @return a view of the internal list of orders connected to this ticker.
	 * The returned list is baked to the internal list of orders, so any change
	 * is reflected to both lists.
	 */
	public ArrayList<OrderCom> getOrderList() {
		return new ArrayList<OrderCom>(orders.values());
	}
	
	/**
	 * @return the session database for this ticker. This is a reference, so
	 * any update to prices or book levels of this ticker are reflected into
	 * returned session.
	 */
	public SessionDBCom getSession() {
		// XXX can we obtain session if datafeed is not active? Direi di NO
		return this.session;
	}

	/**
	 * Empty the database deleting all data recorded up to this point
	 */
	public void flush() {
		// XXX se il datafeed è disattivato questo metodo non dovrebbe agire
		this.session.flush();
	}

	/**
	 * Change the size of this database
	 * 
	 * @param bufferMaxSize -the new maximal size
	 * 
	 * @see SessionDBCom#resizeBuffer(int)
	 */
	public void resizeSessionBuffer(int bufferMaxSize) {
		this.session.resizeBuffer(bufferMaxSize);
	}
	
	/**
	 * @return this ticker code
	 */
	public String tickerCode() {
		return this.tickerCode;
	}

	/**
	 * Use this method to notify the ticker that a message has been
	 * received
	 * 
	 * @param message - the {@link MessageCom} received
	 */
	void receiveMessage(MessageCom message) {
		String messType = message.getType();
		if(messType.equals(MessageCom.ANAG))
			anag(message.getData());
		else if(messType.equals(MessageCom.PRICE))
			price(message.getData());
		else if(messType.equals(MessageCom.PRICE_AUCT))
			priceAuct(message.getData());
		else if(messType.equals(MessageCom.BOOK_5))
			book5(message.getData());
		else if(messType.equals(MessageCom.BIDASK))
			bidAsk(message.getData());
		else if(messType.equals(MessageCom.TRADOK))
			tradeOk(message.getData());
		else if(messType.equals(MessageCom.TRADCONFIRM))
			tradeConfirm(message.getData());
		else if(messType.equals(MessageCom.TRADERR))
			tradeErr(message.getData());
		else if(messType.equals(MessageCom.STOCK))
			stock(message.getData());
		//XXX onReceivedMessage();
	}

	/**
	 * Register into this structure registry received. This method
	 * fires an {@link EventCom.AnagEvent}
	 * 
	 * @param data - data received
	 */
	private void anag(HashMap<String, String> data) {
		log.fff(this, "anag message received");
		ISIN = data.get("ISIN");
		desc = data.get("desc");
		prClose = Double.parseDouble(data.get("prClose"));
		prOpen = Double.parseDouble(data.get("prOpen"));
		freeFloat = Long.parseLong(data.get("freeFloat"));
		anagEventManager.receivedEvent(new EventCom.AnagEvent(
				this,ISIN, desc, prClose, prOpen, freeFloat, data.get("time")));
	}

	/**
	 * Register into this structure price data received. This method
	 * fires a {@link EventCom.PriceEvent}
	 * 
	 * @param data - data received
	 */
	private void price(HashMap<String, String> data) {
		log.fff(this, "price message received");
		// XXX bug not understood, why should data be null?
		if(data == null)
			log.e(this, "data is null. Function price");
		PriceDataCom priceData = new PriceDataCom(Double.parseDouble(data.get("price")),
				Double.parseDouble(data.get("prMin")),
				Double.parseDouble(data.get("prMax")),
				Long.parseLong(data.get("qty")),
				Long.parseLong(data.get("progStocks")),
				Long.parseLong(data.get("progExchanges")),
				data.get("time"));
		this.priceData = priceData;
		
		if(datafeedReady)
			session.insert(data.get("time"), priceData);
		
		priceEventManager.receivedEvent(new EventCom.PriceEvent(this, priceData));
	}

	/**
	 * Register into this structure auction price received. This method
	 * fires a {@link EventCom.PriceAuctEvent}
	 * 
	 * @param data - data received
	 */
	private void priceAuct(HashMap<String, String> data) {
		log.fff(this, "priceAuct message received");
		priceAuct = Double.parseDouble(data.get("price"));

		// alla funzione passo il dato estratto dal data, in quanto no è detto che la variabile
		// 'priceAuct' nel mentre non sia variata.
		priceAuctEventManager.receivedEvent(new EventCom.PriceAuctEvent(
				this,Double.parseDouble(data.get("price")),data.get("time")));
	}

	/**
	 * Register into this structure five levels of book received.
	 * This method fires a {@link EventCom.Book5Event}
	 * 
	 * @param data - data received
	 */
	private void book5(HashMap<String, String> data) {
		log.fff(this, "book5 message received");
		String timestamp = data.get("time");
		BidAskCom tempBidAsk[] = new BidAskCom[5];
		for(int i = 0; i < 5; ++i) {
			tempBidAsk[i] = new BidAskCom(
					Long.parseLong(data.get("qtyBid_"+(i+1))), 
					Long.parseLong(data.get("nPropBid_"+(i+1))),
					Double.parseDouble(data.get("prBid_"+(i+1))),
					Long.parseLong(data.get("qtyAsk_"+(i+1))),
					Long.parseLong(data.get("nPropAsk_"+(i+1))),
					Double.parseDouble(data.get("prAsk_"+(i+1))),
					timestamp,
					dcb);
		}
		// bidAsk = tempBidAsk[0];
		Book5Com book5 = new Book5Com(
				tempBidAsk[0],
				tempBidAsk[1],
				tempBidAsk[2],
				tempBidAsk[3],
				tempBidAsk[4],
				timestamp,
				dcb);
		this.book5 = book5;

		if(datafeedReady)
			session.insert(data.get("time"), book5);
		// session.insert(data.get("time"), bidAsk);

		book5EventManager.receivedEvent(new EventCom.Book5Event(this, book5));
	}

	/**
	 * Register into this structure bid/ask received. This method
	 * fires a {@link EventCom.BidAskEvent}
	 * 
	 * @param data - data received
	 */
	private void bidAsk(HashMap<String, String> data) {
		log.fff(this, "bidAsk message received");
		BidAskCom bidAsk = new BidAskCom(
				Long.parseLong(data.get("qtyBid_1")), 
				Long.parseLong(data.get("nPropBid_1")),
				Double.parseDouble(data.get("prBid_1")),
				Long.parseLong(data.get("qtyAsk_1")),
				Long.parseLong(data.get("nPropAsk_1")),
				Double.parseDouble(data.get("prAsk_1")),
				data.get("time"),
				dcb);
		this.bidAsk = bidAsk;
		
		if(datafeedReady)
			session.insert(data.get("time"), bidAsk);
		
		// Also because of bug in bid/ask stream <== WHAT BUG??? WHY I DON?T WRITE THINGS!!!!

		bidAskEventManager.receivedEvent(new EventCom.BidAskEvent(this,bidAsk));
	}

	/**
	 * Notify corresponding order that a tradeOK message has been received.
	 * This method fires a {@link EventCom.TradeEvent}
	 * 
	 * @param data - data received
	 */
	private void tradeOk(HashMap<String, String> data) {
		dcb.trading.d_GETPOSITION(tickerCode); // XXX perché non fare stock.update?
		OrderCom order;
		String orderId;
		if((orderId = data.get("ordId")) == null || !orders.containsKey(orderId) ||
				data.get("tradeCode") == null) {
			if(orderId == null) {
				log.w(this, "error occurred in resolving TRADEOK; orderId is null");
				return;
			}
			if(!orders.containsKey(orderId))
				log.w(this, "error occurred in resolving TRADEOK; orderId is not in the list: "
						+ orderId);
			if(data.get("tradeCode") == null)
				log.w(this, "error occurred in resolving TRADEOK; missing Trade Code" + orderId);
			return;
		}
		log.fff(this, "tradeOk message received: " + orderId);
		order = orders.get(orderId);
		order.setStatus(OrderCom.convertStausCode(Integer.parseInt("0" + data.get("tradeCode"))));
		order.setPrice(Double.parseDouble("0" + data.get("price")));

		tradeEventManager.receivedEvent(new EventCom.TradeEvent(this, order));
		order.notifyEvent();
	}

	/**
	 * Notify corresponding order that a tradeConfirm message has been received.
	 * This method fires a {@link EventCom.TradeEvent}
	 * 
	 * @param data - data received
	 */
	private void tradeConfirm(HashMap<String, String> data) {
		dcb.trading.d_GETPOSITION(tickerCode); // XXX perché non fare stock.update?
		OrderCom order;
		String orderId;
		if((orderId = data.get("ordId")) == null || !orders.containsKey(orderId)) {
			if(orderId == null)
				log.w(this, "error occurred in resolving TRADECONF; orderId is null");
			else if(!orders.containsKey(orderId))
				log.w(this, "error occurred in resolving TRADECONF; orderId is not in the list: "
						+ orderId);
			return;
		}
		log.fff(this, "tradeConfirm message received: " + orderId);
		order = orders.get(orderId);
		order.setStatus(OrderCom.CONFIRMATION_NEEDED);
		order.setPrice(Double.parseDouble("0" + data.get("price")));
		// TODO: use Rules? They said they are not appropriate for legal matters

		tradeEventManager.receivedEvent(new EventCom.TradeEvent(this, order));
		order.notifyEvent();
	}

	/**
	 * Notify corresponding order that a tradeError message has been received.
	 * This method fires a {@link EventCom.TradeEvent}
	 * 
	 * @param data - data received
	 */
	private void tradeErr(HashMap<String, String> data) {
		dcb.trading.d_GETPOSITION(tickerCode); // XXX perché non fare stock.update?
		OrderCom order;
		String orderId;
		if((orderId = data.get("ordId")) == null || !orders.containsKey(orderId)) {
			if(orderId == null)
				log.w(this, "error occurred in resolving TRADEERR; orderId is null");
			else if(!orders.containsKey(orderId))
				log.w(this, "error occurred in resolving TRADEERR; orderId is not in the list: "
						+ orderId);
			return;
		}
		log.fff(this, "tradeErr message received: " + orderId);
		order = orders.get(orderId);
		order.setStatus(OrderCom.ERROR); // XXX ?? Calcolare bene quale status inserire
			// perché dipende da quando l'errore scaturisce; per esempio se appare dopo un MODORD?
		order.setDetails(data.get("messDesc"));
		order.setPrice(Double.parseDouble("0" + data.get("price")));
		// TODO: use Rules? They said they are not appropriate for legal matters

		tradeEventManager.receivedEvent(new EventCom.TradeEvent(this, order));
		order.notifyEvent();
	}

	/**
	 * Register into this structure changes to the stock.
	 * This method fire a {@link EventCom.StockEvent}
	 * 
	 * @param data - data received
	 * @see TickerCom#getStock()
	 */
	private void stock(HashMap<String, String> data) {
		log.fff(this, "stock message received");
		String prAvg = data.get("prAvg");
		String qtyPortf = data.get("qtyPortf");
		String qtyDirecta = data.get("qtyDirecta");
		String[] qtyNegList = data.get("qtyNeg").split("[^-\\d]");
		int qtyNegInt = 0;
		for(String qty : qtyNegList) {
			try {
				qtyNegInt += Integer.parseInt(qty);
			} catch (NumberFormatException e) {
			}
		}
		String qtyNeg = "" + qtyNegInt;
		String gain = data.get("gain").replaceAll("[^-\\d]","")
				.replaceAll("\\D$", "").replaceAll("\\D$", ""); // perché lo ho messo doppio?

		if(prAvg.length() == 0)
			prAvg = "0";
		if(qtyPortf.length() == 0)
			qtyPortf = "0";
		if(qtyDirecta.length() == 0)
			qtyDirecta = "0";
		if(qtyNeg.length() == 0)
			qtyNeg = "0";
		if(gain.length() == 0)
			gain = "0";

		stock.update(
				data.get("ticker"),
				Double.parseDouble(prAvg),
				Long.parseLong(qtyPortf),
				Long.parseLong(qtyDirecta),
				Long.parseLong(qtyNeg),
				Double.parseDouble(gain),
				data.get("time"));

		// Lo stock non viene aggiornato automaticamente dalla darwin, bensì su nostra richiesta
		// pertanto ha poco senso notificare gli aggiornamenti dello stock dal momento in cui
		// sono chiamate interne. Lo si ritorna lo stesso, nel dubbio.
		stockEventManager.receivedEvent(new EventCom.StockEvent(this, stock));
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

	/**
	 * @return the price during auction
	 */
	public double priceAuct() {
		return priceAuct;
	}

	/**
	 * @return a snapshot of the actual situation
	 */
	public SnapshotCom now() {
		return new SnapshotCom(priceData, book5, bidAsk, dcb);
	}
	
	/**
	 * @return a snapshot of the actual situation in matrix form
	 */
	public BookCom book() {
		return new BookCom(priceData, book5, bidAsk, dcb);
	}

	/**
	 * @return the price
	 */
	public double price() {
		// XXX handle internally priceAuct?
		if(priceData == null)
			return 0;
		return priceData.price();
	}

	/**
	 * @return stocks volume
	 */
	public long volume() {
		if(priceData == null)
			return 0;
		return priceData.volume();
	}

	/**
	 * @return the progressive amount of stocks
	 */
	public long progStocks() {
		if(priceData == null)
			return 0;
		return priceData.progStocks();
	}

	/**
	 * @return the progressive amount of exchanges
	 */
	public long progExchanges() {
		if(priceData == null)
			return 0;
		return priceData.progExchanges();
	}

	/**
	 * @return the minimum of the day
	 */
	public double dailyMin() {
		if(priceData == null)
			return 0;
		return priceData.priceMin();
	}

	/**
	 * @return the maximum of the day
	 */
	public double dailyMax() {
		if(priceData == null)
			return 0;
		return priceData.priceMax();
	}

	/**
	 * @return bid/ask
	 */
	public BidAskCom bidAsk() {
		return bidAsk;
	}

	/**
	 * @return 5 level book5
	 */
	public Book5Com book5() {
		return book5;
	}

	/**
	 * @return the spread, bid/ask difference
	 */
	public double spread() {
		BidAskCom temp = bidAsk;
		return temp.ask().price() - temp.bid().price();
	}

	/**
	 * @return the stock price, the price at which stocks have
	 * been bought
	 */
	public double stockPrice() {
		dcb.trading.d_GETPOSITION(tickerCode); // XXX perché non fare stock.update?
		if(stock != null)
			return stock.price();
		return 0;
	}

	/**
	 * Request a tick-by-tick view of the past days
	 * 
	 * @param days - number of days (day)
	 * @return an empty {@link TBTSeriesCom} that will be populated when data
	 * become available
	 */
	public TBTSeriesCom getTBT(int days) {
		if(this.historyReady)
			return dcb.history.TBT(tickerCode, days);
		log.w(this,"History not activated");
		return null;
	}

	/**
	 * Request a tick-by-tick view of the past days
	 * 
	 * @param dayTimeB - starting dayTime (String DATETIME)
	 * @param dayTimeE - ending dayTime (String DATETIME)
	 * @return an empty {@link TBTSeriesCom} that will be populated when data
	 * become available
	 */
	public TBTSeriesCom getTBT(String dayTimeB, String dayTimeE) {
		DateTimeCom dtB = null;
		DateTimeCom dtE = null;
		try {
			dtB = new DateTimeCom(dayTimeB);
			dtE = new DateTimeCom(dayTimeE);
		} catch(ParseException e) {
			log.e(this, e);
			return null;
		}
		if(dtB.isFuture())
			dtB.setNow();
		if(this.historyReady)
			return dcb.history.TBTRANGE(tickerCode, dtB.toString(), dtE.toString());
		log.w(this,"History not activated");
		return null;
	}

	/**
	 * Request a candle view of the past days
	 * 
	 * @param days - number of days (day)
	 * @param period - the granularity (second)
	 * @return an empty {@link CandleSeriesCom} that will be populated when data
	 * become available
	 */
	public CandleSeriesCom getCANDLE(int days, int period) {
		if(this.historyReady)
			return dcb.history.CANDLE(tickerCode, days, period);
		log.w(this,"History not activated");
		return null;
	}

	/**
	 * Request a candle view of the past days
	 * 
	 * @param dayTimeB - starting dayTime (String DATETIME)
	 * @param dayTimeE - ending dayTime (String DATETIME)
	 * @param period - the granularity (second)
	 * @return an empty {@link CandleSeriesCom} that will be populated when data
	 * become available
	 */
	public CandleSeriesCom getCANDLE(String dayTimeB, String dayTimeE, int period) {
		DateTimeCom dtB = null;
		DateTimeCom dtE = null;
		try {
			dtB = new DateTimeCom(dayTimeB);
			dtE = new DateTimeCom(dayTimeE);
		} catch(ParseException e) {
			log.e(this, e);
			return null;
		}
		if(dtB.isFuture())
			dtB.setNow();
		if(this.historyReady)
			return dcb.history.CANDLERANGE(tickerCode, dtB.toString(), dtE.toString(), period);
		log.w(this,"History not activated");
		return null;
	}
	
	/**
	 * @return a reference to the stock. Any update that will happen to
	 * the stock will be reflected in the reference obtained by a call to this
	 * method. Data contained in the stock are not real time data but may suffer
	 * some delays.
	 * <p>
	 * To catch updates to stock use {@link TickerCom#onStockUpdateEventManager()}
	 * <p>
	 * A call to this method force update informations stored into the returned
	 * {@link StockCom} object
	 */
	public StockCom getStock() {
		dcb.trading.d_GETPOSITION(tickerCode); // XXX perché non fare stock.update?
		return stock;
	}

	/**
	 * Request a table view of the past days
	 * 
	 * @param days - number of days (day)
	 * @param period - the granularity ("s", "m", "h", "D")
	 * @return an empty {@link HistoricalTableCom} that will be populated when data
	 * become available
	 */
	public HistoricalTableCom hist(int days, String period) {
		// TODO Use a calendar
		if(HistoricalTableCom.period.containsKey(period) == false) {
			// return some error
			log.e(this, " -#- period not valid, use 's','m','h','D','W','M','Y'");
			return null;
		}
		return new HistoricalTableCom(this.getCANDLE(days, HistoricalTableCom.period.get(period)),dcb);
	}

	/**
	 * Request a table view of financial values with a specified granularity, with a defined
	 * maximum number of rows up to a specified date.
	 * The number of rows is less or equal the number of rows requested
	 * 
	 *  ie. ticker.hist(datestr(now,'yyyymmddHHMMSS'),'m',40)
	 *   give information on the last 40 minutes in which some transactions occurred.
	 * 
	 * @param date a String containing the reference time, matlab format: yyyymmddHHMMSS
	 * @param period the granularity ("s", "m", "h", "D")
	 * @param number the number of raw desired
	 * @return an empty {@link HistoricalTableCom} that will be populated when data
	 * become available or null if an error occurred
	 */
	public HistoricalTableCom hist(String date, String period, int number) {
		if(HistoricalTableCom.period.containsKey(period) == false) {
			// return some error
			log.e(this, " -#- period not valid, use 's','m','h','D','W','M','Y'");
			return null;
		}
		DateTimeCom dtB = null;
		DateTimeCom dtE = null;
		try {
			dtB = new DateTimeCom(date);
			dtE = new DateTimeCom(date);
		} catch(ParseException e) {
			log.e(this, e);
			return null;
		}
		if(period.equals("s"))
			dtB.calendar().add(Calendar.SECOND, -number*45);
		else if(period.equals("m"))
			dtB.calendar().add(Calendar.SECOND, -number*2*60);
		else if(period.equals("h"))
			dtB.calendar().add(Calendar.HOUR, -number);
		else if(period.equals("D"))
			dtB.calendar().add(Calendar.DAY_OF_YEAR, -number);
		else if(period.equals("W"))
			dtB.calendar().add(Calendar.WEEK_OF_YEAR, -number);
		else if(period.equals("M"))
			dtB.calendar().add(Calendar.MONTH, -number);
		else if(period.equals("Y"))
			dtB.calendar().add(Calendar.YEAR, -number);
		if(dtB.isFuture())
			dtB.setNow();
		return new HistoricalTableCom(
				this.getCANDLE(
						dtB.toString(),
						dtE.toString(),
						HistoricalTableCom.period.get(period)),
				number,
				dcb);
	}

	/**
	 * Request a table view of the past days
	 * 
	 * @param dayTimeB - starting dayTime (String DATETIME)
	 * @param dayTimeE - ending dayTime (String DATETIME)
	 * @param period - the granularity ("s", "m", "h", "D")
	 * @return an empty {@link HistoricalTableCom} that will be populated when data
	 * become available
	 */
	public HistoricalTableCom hist(String dayTimeB, String dayTimeE, String period) {
		DateTimeCom dtB = null;
		DateTimeCom dtE = null;
		try {
			dtB = new DateTimeCom(dayTimeB);
			dtE = new DateTimeCom(dayTimeE);
		} catch(ParseException e) {
			log.e(this, e);
			return null;
		}
		if(dtB.isFuture())
			dtB.setNow();
		if(HistoricalTableCom.period.containsKey(period) == false) {
			// return some error
			log.e(this, " -#- period not valid, use 's','m','h','D','W','M','Y'");
			return null;
		}
		return new HistoricalTableCom(
				this.getCANDLE(
						dtB.toString(),
						dtE.toString(),
						HistoricalTableCom.period.get(period)),
				dcb);
	}

	// Beginning of Trading part
	
	/**
	 * Register a new order into this ticker order list
	 * 
	 * @param order - the order to register
	 */
	void registerOrder(OrderCom order) {
		String orderId = order.orderId;
		// TODO è possibile che l'order esista già in lista? Gestire la situazione
		if(!orders.containsKey(orderId))
			orders.put(orderId, order);
	}

	/**
	 * Send an order for buying a defined amount of stocks at a specified
	 * price
	 * 
	 * @param price - the price
	 * @param qty - the amount
	 * @return an {@link OrderCom} object that keep track of the status of
	 * the order
	 */
	public OrderCom buy(double price, int qty) {
		log.i(this, "buy; ticker: " + tickerCode + "; price: " + price + "; quantity: " + qty); 
		if(this.tradingReady) {
			OrderCom order = dcb.orderMan.insertNewOrder(this, OrderCom.ACQAZ, price, qty);
			dcb.trading.d_ACQAZ(order.orderId, this.tickerCode, qty, price);
			return order;
		}
		log.w(this, "Trading not activated");
		return null;
	}

	/**
	 * Send an order for selling a defined amount of stocks at a specified
	 * price
	 * 
	 * @param price - the price
	 * @param qty - the amount
	 * @return an {@link OrderCom} object that keep track of the status of
	 * the order
	 */
	public OrderCom sell(double price, int qty) {
		log.i(this, "sell; ticker: " + tickerCode + "; price: " + price + "; quantity: " + qty);
		if(this.tradingReady) {
			OrderCom order = dcb.orderMan.insertNewOrder(this, OrderCom.VENAZ, price, qty);
			dcb.trading.d_VENAZ(order.orderId, this.tickerCode, qty, price);
			return order;
		}
		log.w(this, "Trading not activated");
		return null;
	}

	/**
	 * Send an order for buying a defined amount of stocks at market price
	 * 
	 * @param qty - the amount
	 * @return an {@link OrderCom} object that keep track of the status of
	 * the order
	 */
	public OrderCom buy(int qty) {
		log.i(this, "buy; ticker: " + tickerCode + "; quantity: " + qty);
		// XXX debug
		System.out.println(this.tradingReady);
		if(this.tradingReady) {
			OrderCom order = dcb.orderMan.insertNewOrder(this, OrderCom.ACQMARK, qty);
			dcb.trading.d_ACQMARK(order.orderId, this.tickerCode, qty);
			return order;
		}
		log.w(this, "Trading not activated");
		return null;
	}

	/**
	 * Send an order for selling a defined amount of stocks at market price
	 * 
	 * @param qty - the amount
	 * @return an {@link OrderCom} object that keep track of the status of
	 * the order
	 */
	public OrderCom sell(int qty) {
		log.i(this, "sell; ticker: " + tickerCode + "; quantity: " + qty); 
		if(this.tradingReady) {
			OrderCom order = dcb.orderMan.insertNewOrder(this, OrderCom.VENMARK, qty);
			dcb.trading.d_VENMARK(order.orderId, this.tickerCode, qty);
			return order;
		}
		log.w(this, "Trading not activated");
		return null;
	}

	/**
	 * Send an order for buying a defined amount of stocks at specified price
	 * when the price reach a defined signal. price > signal
	 * 
	 * @param price - the price
	 * @param qty - the amount
	 * @param signal - the signal
	 * @return an {@link OrderCom} object that keep track of the status of
	 * the order
	 */
	public OrderCom buyToCover(double price, int qty, double signal) {
		log.i(this, "buy to cover; ticker: " + tickerCode + "; price: " + price
				+ "; signal price: " + signal + "; quantity: " + qty);
		if(this.tradingReady) {
			OrderCom order = dcb.orderMan.insertNewOrder(this, OrderCom.ACQSTOPLIM, price, qty, signal);
			dcb.trading.d_ACQSTOPLIMIT(order.orderId, this.tickerCode, qty, price, signal);
			return order;
		}
		log.w(this, "Trading not activated");
		return null;
	}

	/**
	 * Send an order for selling a defined amount of stocks at specified price
	 * when the price reach a defined signal. price < signal
	 * 
	 * @param price - the price
	 * @param qty - the amount
	 * @param signal - the signal
	 * @return an {@link OrderCom} object that keep track of the status of
	 * the order
	 */
	public OrderCom stopLoss(double price, int qty, double signal) {
		log.i(this, "stop loss; ticker: " + tickerCode + "; price: " + price
				+ "; signal price: " + signal + "; quantity: " + qty);
		if(this.tradingReady) {
			OrderCom order = dcb.orderMan.insertNewOrder(this, OrderCom.VENSTOPLIM, price, qty, signal);
			dcb.trading.d_VENSTOPLIMIT(order.orderId, this.tickerCode, qty, price, signal);
			return order;
		}
		log.w(this, "Trading not activated");
		return null;
	}

	/**
	 * Send an order for buying a defined amount of stocks at market price
	 * when the price reach a defined signal.
	 * 
	 * @param qty - the amount
	 * @param signal - the signal
	 * @return an {@link OrderCom} object that keep track of the status of
	 * the order
	 */
	public OrderCom buyToCover(int qty, double signal) {
		log.i(this, "buy to cover; ticker: " + tickerCode
				+ "; signal price: " + signal + "; quantity: " + qty);
		if(this.tradingReady) {
			OrderCom order = dcb.orderMan.insertNewOrder(this, OrderCom.ACQSTOP, qty, signal);
			dcb.trading.d_ACQSTOP(order.orderId, this.tickerCode, qty, signal);
			return order;
		}
		log.w(this, "Trading not activated");
		return null;
	}

	/**
	 * Send an order for selling a defined amount of stocks at market price
	 * when the price reach a defined signal.
	 * 
	 * @param qty - the amount
	 * @param signal - the signal
	 * @return an {@link OrderCom} object that keep track of the status of
	 * the order
	 */
	public OrderCom stopLoss(int qty, double signal) {
		log.i(this, "stop loss; ticker: " + tickerCode
				+ "; signal price: " + signal + "; quantity: " + qty);
		if(this.tradingReady) {
			OrderCom order = dcb.orderMan.insertNewOrder(this, OrderCom.VENSTOP, qty, signal);
			dcb.trading.d_VENSTOP(order.orderId, this.tickerCode, qty, signal);
			return order;
		}
		log.w(this, "Trading not activated");
		return null;
	}

	/**
	 * Revoke an order
	 * 
	 * @param order - the order being revoked
	 */
	public void revoke(OrderCom order) {
		log.i(this, "revoke order: " + order.orderId);
		if(!this.tradingReady) {
			log.w(this, "Trading not activated");
			return;
		}
		dcb.trading.d_REVORD(order.orderId);
	}

	/**
	 * Revoke any order of this ticker
	 */
	public void revokeAll() {
		log.w(this, "revoke all orders");
		if(!this.tradingReady) {
			log.w(this, "Trading not activated");
			return;
		}
		dcb.trading.d_REVALL(tickerCode);
	}
	
	/**
	 * Confirm given order after that a confirmation has been required
	 * 
	 * @param order - {@link OrderCom} that need to be confirmed
	 */
	public void confirm(OrderCom order) {
		log.i(this, "confirm order: " + order.orderId);
		if(this.tradingReady) {
			dcb.trading.d_CONFORD(order.orderId);
		}
		log.w(this, "Trading not activated");
	}

	/**
	 * <b>WARNING</b> - under test, because we may loose reference to the original order
	 * Change price for the specified order
	 * 
	 * @param order - the {@link OrderCom} to be modified
	 * @param price - new price for order
	 */
	public void editPrice(OrderCom order, double price) {
		log.i(this, "edit price of order: " + order.orderId + "; price: " + price);
		if(this.tradingReady) {
			dcb.trading.d_MODORD(order.orderId, price);
		}
		log.w(this, "Trading not activated");
	}

	/**
	 * <b>WARNING</b> - under test, because we may loose reference to the original
	 * orders of this ticker
	 * Edit the price of every order
	 * 
	 * @param price - new price for orders
	 */
	public void editPriceAll(double price) {
		log.i(this, "edit price of all orders: price: " + price);
		if(this.tradingReady) {
			for(OrderCom order : orders.values()) {
				int status = order.getStatus();
				if(status == OrderCom.PENDING || status == OrderCom.RECEIVED || status == OrderCom.CONFIRMATION_NEEDED)
					editPrice(order, price);
			}
		}
		log.w(this, "Trading not activated");
	}
	
	// Event related methods

	/**
	 * This method returns an {@link EventManagerCom} that will collect
	 * {@link EventCom.AnagEvent} and trigger callbacks
	 * 
	 * @return the {@link EventManagerCom} specific for {@link EventCom.AnagEvent}
	 * @see EventManagerCom
	 */
	public EventManagerCom onAnagEventManager() {
		return anagEventManager;
	}

	/**
	 * This method returns an {@link EventManagerCom} that will collect
	 * {@link EventCom.PriceEvent} and trigger callbacks
	 * 
	 * @return the {@link EventManagerCom} specific for {@link EventCom.PriceEvent}
	 * @see EventManagerCom
	 */
	public EventManagerCom onPriceEventManager() {
		return priceEventManager;
	}

	/**
	 * This method returns an {@link EventManagerCom} that will collect
	 * {@link EventCom.PriceAuctEvent} and trigger callbacks
	 * 
	 * @return the {@link EventManagerCom} specific for {@link EventCom.PriceAuctEvent}
	 * @see EventManagerCom
	 */
	public EventManagerCom onPriceAuctEventManager() {
		return priceAuctEventManager;
	}

	/**
	 * This method returns an {@link EventManagerCom} that will collect
	 * {@link EventCom.Book5Event} and trigger callbacks
	 * 
	 * @return the {@link EventManagerCom} specific for {@link EventCom.Book5Event}
	 * @see EventManagerCom
	 */
	public EventManagerCom onBook5EventManager() {
		return book5EventManager;
	}

	/**
	 * This method returns an {@link EventManagerCom} that will collect
	 * {@link EventCom.BidAskEvent} and trigger callbacks
	 * 
	 * @return the {@link EventManagerCom} specific for {@link EventCom.BidAskEvent}
	 * @see EventManagerCom
	 */
	public EventManagerCom onBidAskEventManager() {
		return bidAskEventManager;
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

	/**
	 * This method returns an {@link EventManagerCom} that will collect
	 * {@link EventCom.StockEvent} and trigger callbacks
	 * 
	 * @return the {@link EventManagerCom} specific for {@link EventCom.StockEvent}
	 * @see EventManagerCom
	 */
	public EventManagerCom onStockUpdateEventManager() {
		return stockEventManager;
	}
	
}
