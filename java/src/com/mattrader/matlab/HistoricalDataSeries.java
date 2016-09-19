package com.mattrader.matlab;

import com.mattrader.common.EventManagerCom;
import com.mattrader.common.HistoricalDataSeriesCom;

public abstract class HistoricalDataSeries {
	
	final HistoricalDataSeriesCom series;
	
	// Performance vs space
	protected char[][] timeSeries = null;
	protected boolean isSetTimeSeries = false;
	protected double[] volumes = null;
	protected boolean isSetVolumes = false;

	protected HistoricalDataSeries(HistoricalDataSeriesCom series) throws Exception {
		this.series = series;
		if(series == null)
			throw new Exception();
	}

	public char[][] getDateTimeSeries() {
		if(series.isReady() && !isSetTimeSeries) {
			timeSeries = Utils.arrayListToArrayMatlabChar(series.getDateTimeSeries());
			isSetTimeSeries = true;
		}
		return timeSeries;
	}

	public double[] getVolumes() {
		if(series.isReady() && !isSetVolumes) {
			volumes = Utils.arrayListToArrayDouble(series.getVolumes());
			isSetVolumes = true;
		}
		return volumes;
	}
	
	public boolean isReady() {
		return series.isReady();
	}

	public double size() {
		return (double)series.size();
	}
	
	public char[][] getTickerCode() {
		return Utils.toMatlabChar(series.getTickerCode());
	}
	
	public EventManagerCom onReadyEventManager() {
		return series.onReadyEventManager();
	}

}
