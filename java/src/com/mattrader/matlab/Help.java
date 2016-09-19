package com.mattrader.matlab;

public abstract class Help {

	private static final String pkg = "com.mattrader.matlab.";

	public static char[][] doc(String name) {
		return Utils.toMatlabChar(docString(name));
	}
	
	public static char[][] doc(Object obj) {
		return doc(obj.getClass().getName());
	}

	private static String docString(String name) {
		name = name.replace(pkg, "");
		name = name.replaceAll("()", "");
		if(is(name,"timestamp"))
			return "Un timestamp puà avere due formati"; //TODO uniformare i timestamp e documentarli
		// BidAsk
		else if(is(name,"BidAsk.bid"))
			return "Restituisce la componente bid di un BidAsk.\n" +
					"Vedasi " + link("BidAskData");
		else if(is(name,"BidAsk.ask"))
			return "Restituisce la componente ask di un BidAsk.\n" +
			"Vedasi " + link("BidAskData");
		else if(is(name,"BidAsk.timestamp"))
			return "Restituisce il timestamp.\n" +
			"Vedasi " + link("timestamp");
		else if(is(name,"BidAsk"))
			return "Questa classe modella un Bid/Ask; metodi a disposizione\n" +
					link(name+".bid")+"\n" +
					link(name+".ask");
		// BidAskData
		else if(is(name,"BidAskData.offers"))
			return "Restituisce il numero di offerte presenti in questo livello.";
		else if(is(name,"BidAskData.price"))
			return "Restituisce il prezzo del livello.";
		else if(is(name,"BidAskData.volume"))
			return "Restituisce il volume delle offerte presenti in questo livello.";
		else if(is(name,"BidAskData.timestamp"))
			return "Restituisce il timestamp.\n" +
			"Vedasi " + link("timestamp");
		else if(is(name,"BidAskData"))
			return "Questa classe modella un livello di bid o di ask; metodi a disposizione\n" +
					link(name+".offers")+"\n" +
					link(name+".price")+"\n" +
					link(name+".volume")+"\n" +
					link(name+".timestamp");
		// Book
		else if(is(name,"Book.bid"))
			return "Restituisce una matrice 5x2, avente in prima colonna i prezzi del bid e sulla seconda " +
					"i volumi.\n NB: Tutti i valori sono double.";
		else if(is(name,"Book.ask"))
			return "Restituisce una matrice 5x2, avente in prima colonna i prezzi del ask e sulla seconda " +
			"i volumi.\n NB: Tutti i valori sono double.";
		else if(is(name,"Book.price"))
			return "Restituisce un vettore contenente il prezzo e il volume dell'ultimo scambiato.";
		else if(is(name,"Book"))
			return "Questa classe modella un Book, mettendo direttamente a disposizione i dati di bid, ask " +
					"e prezzo ultimo scambiato e relativi volumi; metodi a disposizione\n" +
					link(name+".bid")+"\n" +
					link(name+".ask")+"\n" +
					link(name+".price");
		// Book5
		else if(is(name,"Book5.bid"))
			return "Restituisce la componente bid di un Book a 5 livelli.\n" +
					"Vedasi " + link("Book5Data");
		else if(is(name,"Book5.ask"))
			return "Restituisce la componente ask di un Book a 5 livelli.\n" +
					"Vedasi " + link("Book5Data");
		else if(is(name,"Book5.level"))
			return "Restituisce un livello del book a 5 livelli, in forma Bid/Ask. Si aspetta come parametro " +
					"un intero indicante il livello desiderato [0-4].\n" +
					"Vedasi " + link("BidAsk");
		else if(is(name,"Book5.timestamp"))
			return "Restituisce il timestamp.\n" +
					"Vedasi " + link("timestamp");
		else if(is(name,"Book5"))
			return "Questa classe modella un book a 5 livelli; metodi a disposizione\n" +
					link(name+".bid")+"\n" +
					link(name+".ask")+"\n" +
					link(name+".level");
		// Book5Data
		else if(is(name,"Book5Data.offers"))
			return "Restituisce un vettore contenente il numero di offerte per questo " +
					"ramo.\n NB: Tutti i valori sono double.";
		else if(is(name,"Book5Data.price"))
			return "Restituisce un vettore contenente i prezzi per questo ramo.\n NB: Tutti i valori sono double.";
		else if(is(name,"Book5Data.volume"))
			return "Restituisce un vettore contenente i volumi per questo ramo.\n NB: Tutti i valori sono double.";
		else if(is(name,"Book5Data.timestamp"))
			return "Restituisce il timestamp.\n" +
			"Vedasi " + link("timestamp");
		else if(is(name,"Book5Data"))
			return "Questa classe modella un ramo bid o ask di un book a 5 livelli; metodi a disposizione\n" +
					link(name+".offers")+"\n" +
					link(name+".price")+"\n" +
					link(name+".volume")+"\n" +
					link(name+".timestamp");
		// CandleSeries
		else if(is(name,"CandleSeries.getMaxPrices"))
			return "Restituisce un vettore contenente i prezzi massimi di ciascuna candela.";
		else if(is(name,"CandleSeries.getMinPrices"))
			return "Restituisce un vettore contenente i prezzi minimi di ciascuna candela.";
		else if(is(name,"CandleSeries.getOffPrices"))
			return "Restituisce un vettore contenente i prezzi di uscita di ciascuna candela.";
		else if(is(name,"CandleSeries.getOpenPrices"))
			return "Restituisce un vettore contenente i prezzi di entrata di ciascuna candela.";
		else if(is(name,"CandleSeries"))
			return "Questa classe modella una serie di candele; metodi a disposizione\n" +
					link(name+".getMaxPrices")+"\n" +
					link(name+".getMinPrices")+"\n" +
					link(name+".getOffPrices")+"\n" +
					link(name+".getOpenPrices")+"\n\n" +
					"Vedasi: " + link("HistoricalDataSeries") + ", " + link("TBTSeries");
		// DarwinClientBase
		else if(is(name,"DarwinClientBase.getTicker"))
			return ""; //TODO
		else if(is(name,"DarwinClientBase.openServices"))
			return ""; //TODO
		else if(is(name,"DarwinClientBase.close"))
			return ""; //TODO
		else if(is(name,"DarwinClientBase.defaultSessionBuffer"))
			return ""; //TODO
		else if(is(name,"DarwinClientBase.getOpenServices"))
			return ""; //TODO
		else if(is(name,"DarwinClientBase.isReady"))
			return ""; //TODO
		else if(is(name,"DarwinClientBase.onErrorEvent"))
			return ""; //TODO
		else if(is(name,"DarwinClientBase.ignoreHeartbeat"))
			return ""; //TODO
		else if(is(name,"DarwinClientBase.printOutput"))
			return ""; //TODO
		else if(is(name,"DarwinClientBase"))
			return "Client che permette di interfacciarsi con la Darwin offerta da Directa. Vi è la possibilità " +
					"di avere contemporaneamente più client attivi contemporaneamente, purché ciascuno abbia " +
					"nomi differenti. Tramite questa classe è possibile richiedere degli oggetti che permettano " +
					"le operazioni finanziarie e di analisi sui vari ticker. Inoltre si può gestire l'output " +
					"dei messaggi a schermo e chiudere tutte gli oggetti connessi a questo client; metodi a " +
					"disposizione: \n" +
					link(name+".getTicker")+"\n" +
					link(name+".openServices")+"\n" +
					link(name+".close")+"\n" +
					link(name+".defaultSessionBuffer")+"\n"+
					link(name+".getOpenServices")+"\n"+
					link(name+".isReady")+"\n"+
					link(name+".onErrorEvent")+"\n"+
					link(name+".ignoreHeartbeat")+"\n"+
					link(name+".printOutput")+"\n\n" +
					"Vedasi: " + link("DarwinClientManager") + ", " + link("Ticker");
		// DarwinClientManager
		else if(is(name,"DarwinClientManager.getClient"))
			return ""; //TODO
		else if(is(name,"DarwinClientManager.getClients"))
			return ""; //TODO
		else if(is(name,"DarwinClientManager.stopAll"))
			return ""; //TODO
		else if(is(name,"DarwinClientManager"))
			return "Questa classe permette di gestire i client attivi, in particolare permette di ottenere " +
					"un client dato un nome o un vettore contennte tutti i client attivi e permette di " +
					"stoppare tutti i client. All'atto pratico si tratta di un singleton, di solito non viene " +
					"usata dall'utente Matlab finale e viene gestita direttamente tramite la classe matlab " +
					"DarwinManager; metodi a disposizione: \n" +
					link(name+".getClient")+"\n" +
					link(name+".getClients")+"\n" +
					link(name+".stopAll")+"\n\n" +
					"Vedasi: " + link("DarwinClientBase");
		// HistoricalDataSeries
		else if(is(name,"HistoricalDataSeries.getDateTimeSeries"))
			return ""; //TODO
		else if(is(name,"HistoricalDataSeries.getTickerCode"))
			return ""; //TODO
		else if(is(name,"HistoricalDataSeries.getVolumes"))
			return ""; //TODO
		else if(is(name,"HistoricalDataSeries.size"))
			return ""; //TODO
		else if(is(name,"HistoricalDataSeries.isReady"))
			return ""; //TODO
		else if(is(name,"HistoricalDataSeries.onReadyEventManager"))
			return ""; //TODO
		else if(is(name,"HistoricalDataSeries"))
			return "Questa classe modella una serie storica di dati, è solo una implementazione astratta che " +
					"viene opportunamente estesa da classi più specifiche, per gestire per esempio le candele " +
					"o le serie Tick-by-tick; metodi a disposizione, ereditati dalle classi figlie\n" +
					link(name+".getDateTimeSeries")+"\n" +
					link(name+".getTickerCode")+"\n" +
					link(name+".getVolumes")+"\n" +
					link(name+".size")+"\n" +
					link(name+".isReady")+"\n\n" +
					link(name+".onReadyEventManager")+"\n" +
					"Vedasi: " + link("CandleSeries") + ", " + link("TBTSeries");



		return "Elemento non riconosciuto (o attualmente non documentato), per favore controllare la sintassi.";
	
	}

	private static boolean is(String name, String what) {
		return name.compareTo(what) == 0;
	}
	
	private static String link(String what) {
		return link(what, pkg+what);
	}
	
	private static String link(String what, String where) {
		return "<a href=\"matlab: DarwinManager.help('"+where+"')\">"+what+"</a>";
	}
}
