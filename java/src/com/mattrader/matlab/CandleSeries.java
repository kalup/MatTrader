package com.mattrader.matlab;

import com.mattrader.common.CandleSeriesCom;
import com.mattrader.common.HistoricalDataSeriesCom;

public class CandleSeries extends HistoricalDataSeries {

	// Performance vs space
	protected double[] offPrices = null;
	protected boolean isSetOffPrices = false;
	protected double[] minPrices = null;
	protected boolean isSetMinPrices = false;
	protected double[] maxPrices = null;
	protected boolean isSetMaxPrices = false;
	protected double[] openPrices = null;
	protected boolean isSetOpenPrices = false;

	protected CandleSeries(HistoricalDataSeriesCom series) throws Exception {
		super(series);
	}

	public double[] getOffPrices() {
		if(series.isReady() && !isSetOffPrices) {
			offPrices = Utils.arrayListToArrayDouble(((CandleSeriesCom) series).getOffPrices());
			isSetOffPrices = true;
		}
		return offPrices;
	}

	public double[] getMinPrices() {
		if(series.isReady() && !isSetMinPrices) {
			minPrices = Utils.arrayListToArrayDouble(((CandleSeriesCom) series).getMinPrices());
			isSetMinPrices = true;
		}
		return minPrices;
	}

	public double[] getMaxPrices() {
		if(series.isReady() && !isSetMaxPrices) {
			maxPrices = Utils.arrayListToArrayDouble(((CandleSeriesCom) series).getMaxPrices());
			isSetMaxPrices = true;
		}
		return maxPrices;
	}

	public double[] getOpenPrices() {
		if(series.isReady() && !isSetOpenPrices) {
			openPrices = Utils.arrayListToArrayDouble(((CandleSeriesCom) series).getOpenPrices());
			isSetOpenPrices = true;
		}
		return openPrices;
	}

}
