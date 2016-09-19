package com.mattrader.common;

import java.util.HashMap;
import java.util.Locale;

/**
 * This class help transforming String messages received by Directa
 * Darwin into a data structure easier to parse. A {@link MessageCom}
 * essentially is a HashMap in which information are stored in
 * key-value pairs
 * 
 * @author Luca Poletti
 *
 */
class MessageCom {
	// TODO we have new messages, update the code (ie. TABLE)

	private LogCom log;
	private EventManagerCom errorEventManager;

	// ################################### //
	// ### Begin of messType const def ### //
	
	final static String STOCK = "STOCK";
	final static String AVAILABILITY = "AVAILABILITY";
	final static String DARWIN_STATUS = "DARWIN_STATUS";
	final static String INFOACCOUNT = "INFOACCOUNT";
	final static String ORDER = "ORDER";
	final static String ERR = "ERR";
	final static String ANAG = "ANAG";
	final static String PRICE = "PRICE";
	final static String PRICE_AUCT = "PRICE_AUCT";
	final static String BOOK_5 = "BOOK_5";
	final static String BIDASK = "BIDASK";
	final static String LOG_ENABLED = "LOG_ENABLED";
	final static String LOG_DISABLED = "LOG_DISABLED";
	final static String TRADOK = "TRADOK";
	final static String TRADERR = "TRADERR";
	final static String TRADCONFIRM = "TRADCONFIRM";
	final static String VOLUME_AFTERHOURS = "VOLUME_AFTERHOURS";
	final static String BEGIN = "BEGIN";
	final static String END = "END";
	final static String CANDLE = "CANDLE";
	final static String TBT = "TBT";

	// #### End of messType const def #### //
	// ################################### //



	// ################################### //
	// ### Begin of messDest const def ### //
	/**
	 * Recipient is the TickerCom class
	 */
	final static String TICKER = "ticker";
	/**
	 * Recipient is the OrderManagerCom class
	 */
	final static String ORDER_MANAGER = "orderManager";
	/**
	 * Recipient is the HistoryService class
	 */
	final static String HIST_MANAGER = "historicalCallsManager";
	
	// #### End of messDest  const def #### //
	// ################################### //
	
	private String message;
	private final String messType;
	private HashMap<String,String> messData;
	/*final*/ String messDest; // no more final since we have DispatcherCom#redispatch(MessageCom,String)
	
	private String[] messParts;

	/**
	 * Constructor
	 * @param message - String containing the message to be transformed
	 * @param dcb - {@link MTClientBaseCom} client that captured this
	 * message
	 */
	MessageCom(String message, MTClientBaseCom dcb) {

		log = dcb.log();
		errorEventManager = dcb.onErrorEventManager();
		
		this.message = message;
		
		messData = new HashMap<String,String>();

		messDest = "";
		
		messParts = message.split(";");
		if(message.indexOf("VOLUME_AFTERHOURS ") == 0)
			messParts = message.split(" ");
		if(message.indexOf("BEGIN ") == 0)
			messParts = message.split(" ");
		if(message.indexOf("END ") == 0)
			messParts = message.split(" ");
		messType = messParts[0];

		if(messType.equals(MessageCom.STOCK))
			stock();
		else if(messType.equals(MessageCom.AVAILABILITY))
			availability();
		else if(messType.equals(MessageCom.DARWIN_STATUS))
			darwinStatus();
		else if(messType.equals(MessageCom.INFOACCOUNT))
			infoAccount();
		else if(messType.equals(MessageCom.ORDER))
			order();
		else if(messType.equals(MessageCom.ERR))
			err();
		else if(messType.equals(MessageCom.ANAG))
			anag();
		else if(messType.equals(MessageCom.PRICE))
			price();
		else if(messType.equals(MessageCom.PRICE_AUCT))
			priceAuct();
		else if(messType.equals(MessageCom.BOOK_5))
			book5();
		else if(messType.equals(MessageCom.BIDASK))
			bidAsk();
		else if(messType.equals(MessageCom.LOG_ENABLED))
			logEnabled();
		else if(messType.equals(MessageCom.LOG_DISABLED))
			logDisabled();
		else if(messType.equals(MessageCom.TRADOK))
			tradOk();
		else if(messType.equals(MessageCom.TRADERR))
			tradErr();
		else if(messType.equals(MessageCom.TRADCONFIRM))
			tradConfirm();
		else if(messType.equals(MessageCom.VOLUME_AFTERHOURS))
			volumeAfterhours();
		else if(messType.equals(MessageCom.BEGIN))
			begin();
		else if(messType.equals(MessageCom.END))
			end();
		else if(messType.equals(MessageCom.CANDLE))
			candle();
		else if(messType.equals(MessageCom.TBT))
			tbt();

	}
	
	/* TODO Create static final arrays for every type of message and put key strings there:
	 * 
	 * ie.
	 * public static final String[] TBT = {"ticker", "date", "time", "prOff", "qty"};
	 * ...
	 * 
	 * or an abstract class containing the final String[] and a function and implement it for
	 * every possible message
	 * 
	 * ie.
	 * public instance MessageType {
	 * 	public static final String[] keys = {"ticker", "date", "time", "prOff", "qty"};
	 * 	public abstract void dispatcher();
	 * }
	 * 	
	 * 
	 * Then create an association HashMap<String,String[]> or HashMap<String,MessageType>
	 * 
	 * ie.
	 * [public/private] static final HashMap<String,String[]> assocMap;
	 * 
	 * static {
	 * 	assocMap = HashMap<String,String[]>();
	 * 	assocMap.put("TBT",TBT);
	 *  ...
	 * }
	 * 
	 * instead of the list of IF ELSE IF it can be used a simple FOR
	 */

	/**
	 * Transform a TBT message
	 */
	private void tbt() {
		messData.put("ticker", messParts[1].toUpperCase(Locale.ENGLISH));
		messData.put("date", messParts[2]);
		messData.put("time", messParts[3]);
		messData.put("prOff", messParts[4]);
		if(messParts.length == 5)
			messData.put("qty", "0");
		else
			messData.put("qty", messParts[5]);
		messDest = MessageCom.HIST_MANAGER;
	}

	/**
	 * Transform a CANDLE message
	 */
	private void candle() {
		messData.put("ticker", messParts[1].toUpperCase(Locale.ENGLISH));
		messData.put("date", messParts[2]);
		messData.put("time", messParts[3]);
		messData.put("prOff", messParts[4]);
		messData.put("prMin", messParts[5]);
		messData.put("prMax", messParts[6]);
		messData.put("prOpen", messParts[7]);
		if(messParts.length == 8)
			messData.put("qty", "0");
		else
			messData.put("qty", messParts[8]);
		messDest = MessageCom.HIST_MANAGER;
	}

	/**
	 * Transform a END message
	 */
	private void end() {
		messData.put("histData", messParts[1]);
		messDest = MessageCom.HIST_MANAGER;
	}

	/**
	 * Transform a BEGIN message
	 */
	private void begin() {
		messData.put("histData", messParts[1]);
		messDest = MessageCom.HIST_MANAGER;
	}

	/**
	 * Transform a VOLUME_AFTERHOURS message
	 */
	private void volumeAfterhours() {
		messData.put("detail", messParts[1]);
		//XXX messDest
	}

	/**
	 * Transform a TRADECONF message
	 */
	private void tradConfirm() {
		messData.put("ticker", messParts[1].toUpperCase(Locale.ENGLISH));
		messData.put("ordId", messParts[2]);
		messData.put("tradeCode", messParts[3]);
		messData.put("command", messParts[4]);
		messData.put("qty", messParts[5]);
		messData.put("price", messParts[6]);
		if(messParts.length == 7)
			messData.put("messDesc", "0");
		else
			messData.put("messDesc", messParts[7]);
		messDest = MessageCom.TICKER; //TODO to handle in TickerCom
		log.f(this, "trade confirm received "+messParts[2]);
	}

	/**
	 * Transform a TRADEERR message
	 */
	private void tradErr() {
		messData.put("ticker", messParts[1].toUpperCase(Locale.ENGLISH));
		messData.put("ordId", messParts[2]);
		messData.put("errCode", messParts[3]);
		messData.put("command", messParts[4]);
		messData.put("qty", messParts[5]);
		messData.put("price", messParts[6]);
		if(messParts.length == 7)
			messData.put("messDesc", "0");
		else
			messData.put("messDesc", messParts[7]);
		messDest = MessageCom.TICKER; //TODO to handle in TickerCom
		log.e(this,"trade error!\n"+message);
	}

	/**
	 * Transform a TRADEOK message
	 */
	private void tradOk() {
		messData.put("ticker", messParts[1].toUpperCase(Locale.ENGLISH));
		messData.put("ordId", messParts[2]);
		messData.put("tradeCode", messParts[3]);
		messData.put("command", messParts[4]);
		messData.put("qty", messParts[5]);
		if(messParts.length == 6)
			messData.put("price", "0");
		else
			messData.put("price", messParts[6]);
		messDest = MessageCom.TICKER; //TODO to handle in TickerCom
		log.f(this,"trade OK received "+messParts[2]);
	}

	/**
	 * Transform a LOG_DISABLED message
	 */
	private void logDisabled() {
	}

	/**
	 * Transform a LOG_ENABLED message
	 */
	private void logEnabled() {
	}

	/**
	 * Transform a BIDASK message
	 */
	private void bidAsk() {
		messData.put("ticker", messParts[1].toUpperCase(Locale.ENGLISH));
		messData.put("time", messParts[2]);
		messData.put("qtyBid_1", messParts[3]);
		messData.put("nPropBid_1", messParts[4]);
		messData.put("prBid_1", messParts[5]);
		messData.put("qtyAsk_1", messParts[6]);
		messData.put("nPropAsk_1", messParts[7]);
		if(messParts.length == 8)
			messData.put("prAsk_1", "0");
		else
			messData.put("prAsk_1", messParts[8]);
		messDest = MessageCom.TICKER;
	}

	/**
	 * Transform a BOOK5 message
	 */
	private void book5() {
		messData.put("ticker",		messParts[ 1]);
		messData.put("time",		messParts[ 2]);
		messData.put("qtyBid_1",	messParts[ 3]);
		messData.put("nPropBid_1",	messParts[ 4]);
		messData.put("prBid_1",		messParts[ 5]);
		messData.put("qtyBid_2",	messParts[ 6]);
		messData.put("nPropBid_2",	messParts[ 7]);
		messData.put("prBid_2",		messParts[ 8]);
		messData.put("qtyBid_3",	messParts[ 9]);
		messData.put("nPropBid_3",	messParts[10]);
		messData.put("prBid_3",		messParts[11]);
		messData.put("qtyBid_4",	messParts[12]);
		messData.put("nPropBid_4",	messParts[13]);
		messData.put("prBid_4",		messParts[14]);
		messData.put("qtyBid_5",	messParts[15]);
		messData.put("nPropBid_5",	messParts[16]);
		messData.put("prBid_5",		messParts[17]);
		messData.put("qtyAsk_1",	messParts[18]);
		messData.put("nPropAsk_1",	messParts[19]);
		messData.put("prAsk_1",		messParts[20]);
		messData.put("qtyAsk_2",	messParts[21]);
		messData.put("nPropAsk_2",	messParts[22]);
		messData.put("prAsk_2",		messParts[23]);
		messData.put("qtyAsk_3",	messParts[24]);
		messData.put("nPropAsk_3",	messParts[25]);
		messData.put("prAsk_3",		messParts[26]);
		messData.put("qtyAsk_4",	messParts[27]);
		messData.put("nPropAsk_4",	messParts[28]);
		messData.put("prAsk_4",		messParts[29]);
		messData.put("qtyAsk_5",	messParts[30]);
		messData.put("nPropAsk_5",	messParts[31]);
		if(messParts.length == 32)
			messData.put("prAsk_5", "0");
		else
			messData.put("prAsk_5",		messParts[32]);
		messDest = MessageCom.TICKER;
	}

	/**
	 * Transform a PRICEAUCT message
	 */
	private void priceAuct() {
		messData.put("ticker", messParts[1].toUpperCase(Locale.ENGLISH));
		messData.put("time", messParts[2]);
		if(messParts.length == 3)
			messData.put("price", "0");
		else
			messData.put("price", messParts[3]);
		messDest = MessageCom.TICKER;
	}

	/**
	 * Transform a PRICE message
	 */
	private void price() {
		messData.put("ticker", messParts[1].toUpperCase(Locale.ENGLISH));
		messData.put("time", messParts[2]);
		messData.put("price", messParts[3]);
		messData.put("qty", messParts[4]);
		messData.put("progStocks", messParts[5]);
		messData.put("progExchanges", messParts[6]);
		messData.put("prMin", messParts[7]);
		if(messParts.length == 8)
			messData.put("prMax", "0");
		else
			messData.put("prMax", messParts[8]);
		messDest = MessageCom.TICKER;
	}

	/**
	 * Transform a ANAG message
	 */
	private void anag() {
		messData.put("ticker", messParts[1].toUpperCase(Locale.ENGLISH));
		messData.put("time", messParts[2]);
		messData.put("ISIN", messParts[3]);
		messData.put("desc", messParts[4]);
		messData.put("prClose", messParts[5]);
		messData.put("prOpen", messParts[6]);
		if(messParts.length == 7)
			messData.put("freeFloat", "0");
		else
			messData.put("freeFloat", messParts[7]);
		messDest = MessageCom.TICKER;
	}

	/**
	 * Transform a ERR message
	 */
	private void err() {
		messData.put("errMess", messParts[1].toUpperCase(Locale.ENGLISH));
		if(messParts.length == 2)
			messData.put("errCode", "0");
		else
			messData.put("errCode", messParts[2]);
		log.w(this, "Error received: " + messData.get("errMess") + "; " + messData.get("errCode"));
		// Generate an error event that will be catch by listeners
		if(errorEventManager != null)
			errorEventManager.receivedEvent(new EventCom.ErrorEvent(this, messData.get("errMess"),
					Integer.parseInt(messData.get("errCode"))));
	}

	/**
	 * Transform a ORDER message
	 */
	private void order() {
		messData.put("ticker", messParts[1].toUpperCase(Locale.ENGLISH));
		messData.put("time", messParts[2]);
		messData.put("ordId", messParts[3]);
		messData.put("command", messParts[4]);
		messData.put("prLim", messParts[5]);
		messData.put("prSig", messParts[6]);
		messData.put("qty", messParts[7]);
		if(messParts.length == 8)
			messData.put("orderStatus", "0");
		else
			messData.put("orderStatus", messParts[8]);
		messDest = MessageCom.ORDER_MANAGER;
	}

	/**
	 * Transform a INFOACCOUNT message
	 */
	private void infoAccount() {
		messData.put("time", messParts[1]);
		messData.put("codConto", messParts[2]);
		messData.put("liq", messParts[3]);
		messData.put("gain", messParts[4]);
		if(messParts.length == 5)
			messData.put("openProfLoss", "0");
		else
			messData.put("openProfLoss", messParts[5]);
		// XXX messDest = MessageCom.???;
	}

	/**
	 * Transform a DARWIN_STATUS message
	 */
	private void darwinStatus() {
		messData.put("connStat", messParts[1]);
		if(messParts.length == 2)
			messData.put("rel", "0");
		else
			messData.put("rel", messParts[2]);
		// XXX messDest = MessageCom.???;
	}

	/**
	 * Transform a AVAILABILITY message
	 */
	private void availability() {
		messData.put("time", messParts[1]);
		messData.put("dispAz", messParts[2]);
		messData.put("dispAzMargin", messParts[3]);
		messData.put("dispDeriv", messParts[4]);
		messData.put("dispDerivMargin", messParts[5]);
		if(messParts.length == 6)
			messData.put("totLiq", "0");
		else
			messData.put("totLiq", messParts[6]);
		// XXX messDest = MessageCom.???;
	}

	/**
	 * Transform a STOCK message
	 */
	private void stock() {
		messData.put("ticker", messParts[1].toUpperCase(Locale.ENGLISH));
		messData.put("time", messParts[2]);
		messData.put("qtyPortf", messParts[3]);
		messData.put("qtyDirecta", messParts[4]);
		messData.put("qtyNeg", messParts[5]); // TODO it may contains special chars like (>, ]) see DOC
		messData.put("prAvg", messParts[6]);
		if(messParts.length == 7) // gain is not defined (titolo scaduto e ancora in portafoglio)
			messData.put("gain", "0");
		else
			messData.put("gain", messParts[7]);
		messDest = MessageCom.TICKER; //XXX who is the dest? We need a Stock obj?
	}

	/**
	 * @return a the message type
	 */
	String getType() {
		return messType;
	}
	
	/**
	 * @return data contained in that message. Data obrained by the mean of this
	 * methods are a swallow copy of those contained in this message. Any change
	 * in the structure of the returned HashMap is not reflected in the message
	 */
	@SuppressWarnings("unchecked")
	HashMap<String,String> getData() {
		return (HashMap<String, String>) messData.clone();
	}
	
	/**
	 * @return the recipient of this message
	 */
	String getDest() {
		return messDest;
	}

}
