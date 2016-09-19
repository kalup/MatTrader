package com.mattrader.matlab;

import com.mattrader.common.SessionDBCom;

public class SessionDB {

	private final SessionDBCom session;

	SessionDB(SessionDBCom session) throws Exception {
		this.session = session;
		if(session == null)
			throw new Exception();
	}
	
	public void flush() {
		session.flush();
	}
	
	public void resizeBuffer(int bufferMaxSize) {
		session.resizeBuffer(bufferMaxSize);
	}

	/**
	 * @param time
	 * @return
	 */
	public double selectAvgWeightedPrice(String time) {
		return session.selectAvgWeightedPrice(time);
	}

	public double[] selectAvgWeightedPrice(String timeB, String timeE) {
		return Utils.arrayListToArrayDouble(session.selectAvgWeightedPrice(timeB, timeE));
	}
	
	public double selectPrice(String time) {
		return session.selectPrice(time);
	}
	
	public double[] selectPrice(String timeB, String timeE) {
		return Utils.arrayListToArrayDouble(session.selectPrice(timeB, timeE));
	}
	
	public double[] selectPriceDetails(String time) {
		return selectPriceDetails(time, time);
	}
	
	public double[] selectPriceDetails(String timeB, String timeE) {
		return Utils.arrayListToArrayDouble(session.selectPriceDetails(timeB, timeE));
	}
	
	public double selectDailyMinPrice(String time) {
		return session.selectDailyMinPrice(time);
	}
	
	public double[] selectDailyMinPrice(String timeB, String timeE) {
		return Utils.arrayListToArrayDouble(session.selectDailyMinPrice(timeB, timeE));
	}
	
	public double[] selectDailyMinPriceDetails(String time) {
		return selectDailyMinPriceDetails(time, time);
	}
	
	public double[] selectDailyMinPriceDetails(String timeB, String timeE) {
		return Utils.arrayListToArrayDouble(session.selectDailyMinPriceDetails(timeB, timeE));
	}
	
	public double selectDailyMaxPrice(String time) {
		return session.selectDailyMaxPrice(time);
	}
	
	public double[] selectDailyMaxPrice(String timeB, String timeE) {
		return Utils.arrayListToArrayDouble(session.selectDailyMaxPrice(timeB, timeE));
	}
	
	public double[] selectDailyMaxPriceDetails(String time) {
		return selectDailyMaxPriceDetails(time, time);
	}
	
	public double[] selectDailyMaxPriceDetails(String timeB, String timeE) {
		return Utils.arrayListToArrayDouble(session.selectDailyMaxPriceDetails(timeB, timeE));
	}

	/**Select the total volume at the specified time
	 * @param time
	 * @return total volume at the specified time
	 */
	public double selectVolume(String time) {
		return session.selectVolume(time);
	}
	
	public double[] selectVolumeDetails(String time) {
		return selectVolumeDetails(time, time);
	}
	
	public double[] selectVolume(String timeB, String timeE) {
		return Utils.arrayListToArrayDouble(session.selectVolume(timeB, timeE));
	}
	
	public double[] selectVolumeDetails(String timeB, String timeE) {
		return Utils.arrayListToArrayDouble(session.selectVolumeDetails(timeB, timeE));
	}
	
	public double selectProgStocks(String time) {
		return session.selectProgStocks(time);
	}
	
	public double[] selectProgStocks(String timeB, String timeE) {
		return Utils.arrayListToArrayDouble(session.selectProgStocks(timeB, timeE));
	}
	
	public double[] selectProgStocksDetails(String time) {
		return selectProgStocksDetails(time, time);
	}
	
	public double[] selectProgStocksDetails(String timeB, String timeE) {
		return Utils.arrayListToArrayDouble(session.selectProgStocksDetails(timeB, timeE));
	}
	
	public double selectProgExchanges(String time) {
		return session.selectProgExchanges(time);
	}
	
	public double[] selectProgExchanges(String timeB, String timeE) {
		return Utils.arrayListToArrayDouble(session.selectProgExchanges(timeB, timeE));
	}
	
	public double[] selectProgExchangesDetails(String time) {
		return selectProgExchangesDetails(time, time);
	}
	
	public double[] selectProgExchangesDetails(String timeB, String timeE) {
		return Utils.arrayListToArrayDouble(session.selectProgExchanges(timeB, timeE));
	}
	
	public PriceData[] selectPriceData(String time) {
		return selectPriceData(time, time);
			
	}
	
	public PriceData[] selectPriceData(String timeB, String timeE) {
		return Utils.arrayListToArrayPriceData(session.selectPriceData(timeB, timeE));
	}
	
	public Book5[] selectBook5(String time) {
		return selectBook5(time, time);
	}
	
	public Book5[] selectBook5(String timeB, String timeE) {
		return Utils.arrayListToArrayBook(session.selectBook5(timeB, timeE));
	}
	
	public BidAsk[] selectBidAsk(String time) {
		return selectBidAsk(time, time);
	}
	
	public BidAsk[] selectBidAsk(String timeB, String timeE) {
		return Utils.arrayListToArrayBidAsk(session.selectBidAsk(timeB, timeE));
	}
	
	public char[][] selectTimePriceDetails(String time) {
		return selectTimePriceDetails(time, time);
	}
	
	public char[][] selectTimePriceDetails(String timeB, String timeE) {
		return Utils.arrayListToArrayMatlabChar(session.selectTimePriceDetails(timeB, timeE));
	}
	
	public char[][] selectTimeBook5Details(String time) {
		return selectTimePriceDetails(time, time);
	}
	
	public char[][] selectTimeBook5Details(String timeB, String timeE) {
		return Utils.arrayListToArrayMatlabChar(session.selectTimeBook5Details(timeB, timeE));
	}
	
	public char[][] selectTimeBidAskDetails(String time) {
		return selectTimeBidAskDetails(time, time);
	}
	
	public char[][] selectTimeBidAskDetails(String timeB, String timeE) {
		return Utils.arrayListToArrayMatlabChar(session.selectTimeBidAskDetails(timeB, timeE));
	}

	public char[][] getTimePrice() {
		return Utils.arrayListToArrayMatlabChar(session.getTimePrice());
	}

	public char[][] getTimeBook5() {
		return Utils.arrayListToArrayMatlabChar(session.getTimeBook5());
	}

	public char[][] getTimeBidAsk() {
		return Utils.arrayListToArrayMatlabChar(session.getTimeBidAsk());
	}

}
