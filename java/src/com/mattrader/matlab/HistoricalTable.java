package com.mattrader.matlab;

import com.mattrader.common.EventManagerCom;
import com.mattrader.common.HistoricalTableCom;

public class HistoricalTable {

	private final HistoricalTableCom hist;
	
	private double[] openingPrice;
	private double[] closePrice;
	private double[] maxPrice;
	private double[] minPrice;
	private double[] volume;
	private char[][] date;
	
	//TODO char[][]
	private String[][] table;
	
	private boolean initialized;

	HistoricalTable(HistoricalTableCom hist) throws Exception {
		this.hist = hist;
		if(hist == null)
			throw new Exception();
		initialized = false;
		if(hist.isReady())
			init();
	}
	
	private void init() {
		openingPrice = Utils.arrayListToArrayDouble(hist.openingPrice());
		closePrice = Utils.arrayListToArrayDouble(hist.closePrice());
		maxPrice = Utils.arrayListToArrayDouble(hist.maxPrice());
		minPrice = Utils.arrayListToArrayDouble(hist.minPrice());
		volume = Utils.arrayListToArrayDouble(hist.volume());
		date = Utils.arrayListToArrayMatlabChar(hist.date());
		
		table = Utils.hashtableToMatrixString(hist.table());
		
		initialized = true;
	}
	
	public boolean isReady() {
		if(hist.isReady())
			init();
		return hist.isReady();
	}

	public double[] openingPrice() {
		if(initialized)
			return this.openingPrice;
		if(this.isReady()) {
			init();
			return this.openingPrice;
		}
		return Utils.arrayListToArrayDouble(hist.openingPrice());
	}

	public double[] closePrice() {
		if(initialized)
			return this.closePrice;
		if(this.isReady()) {
			init();
			return this.closePrice;
		}
		return Utils.arrayListToArrayDouble(hist.closePrice());
	}

	public double[] maxPrice() {
		if(initialized)
			return this.maxPrice;
		if(this.isReady()) {
			init();
			return this.maxPrice;
		}
		return Utils.arrayListToArrayDouble(hist.maxPrice());
	}

	public double[] minPrice() {
		if(initialized)
			return this.minPrice;
		if(this.isReady()) {
			init();
			return this.minPrice;
		}
		return Utils.arrayListToArrayDouble(hist.minPrice());
	}
	
	public double[] volume() {
		if(initialized)
			return this.volume;
		if(this.isReady()) {
			init();
			return this.volume;
		}
		return Utils.arrayListToArrayDouble(hist.volume());
	}
	
	public char[][] date() {
		if(initialized)
			return this.date;
		if(this.isReady()) {
			init();
			return this.date;
		}
		return Utils.arrayListToArrayMatlabChar(hist.date());
	}

	public double size() {
		if(this.isReady())
			init();
		return hist.size();
	}
	
	public char[][] tickerCode() {
		if(this.isReady())
			init();
		return Utils.toMatlabChar(hist.tickerCode());
	}
	
	//TODO char[][][]
	public String[][] table() {
		if(initialized)
			return this.table;
		if(this.isReady()) {
			init();
			return this.table;
		}
		return Utils.hashtableToMatrixString(hist.table());
	}
	
	public EventManagerCom onReadyEventManager() {
		return hist.onReadyEventManager();
	}

}
