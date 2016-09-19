package com.mattrader.matlab;

import com.mattrader.common.HistoricalDataSeriesCom;
import com.mattrader.common.TBTSeriesCom;

public class TBTSeries extends HistoricalDataSeries {

	// Performance vs space
	protected double[] prices = null;
	protected boolean isSetPrices = false;

	protected TBTSeries(HistoricalDataSeriesCom series) throws Exception {
		super(series);
	}
	
	public double[] getPrices() {
		if(series.isReady() && !isSetPrices) {
			prices = Utils.arrayListToArrayDouble(((TBTSeriesCom) series).getPrices());
			isSetPrices = true;
		}
		return prices;
	}

}
