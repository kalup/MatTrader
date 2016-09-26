package com.mattrader.common;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A little in memory database, it will keep track of prices and book levels that
 * happens while the ticker has been subscribed to Darwin. Practically this database
 * store informations for a {@link TickerCom} that has open a dataFeed service.
 * Methods for querying data are provided. Every operation should be thread safe and
 * a couple of locks are involved.
 * <p>
 * The session has a maximum size, see
 * {@link MTClientBaseCom#defaultSessionBuffer(int)}, and data will be truncated
 * if this limit is overflowed by more than 10%. Data may also be discarded by the
 * mean of a call to {@link SessionDBCom#flush()}
 * 
 * @author Luca Poletti
 *
 */
public class SessionDBCom {

	private LogCom log;

	private TreeMap<String, Integer> indexPriceMap;
	private TreeMap<String, Integer> indexBook5Map;
	private TreeMap<String, Integer> indexBidAskMap;

	private ArrayList<String> timePrice;
	private ArrayList<String> timeBook5;
	private ArrayList<String> timeBidAsk;

	private ArrayList<PriceDataCom> priceData;
	private ArrayList<Double> price;
	private ArrayList<Double> prMin;
	private ArrayList<Double> prMax;
	private ArrayList<Long> qty;
	private ArrayList<Long> progStocks;
	private ArrayList<Long> progExchanges;
	
	private ArrayList<Book5Com> book5;
	private ArrayList<BidAskCom> bidAsk;

	private int indexPrice;
	private int indexBook5;
	private int indexBidAsk;

	private int bufferSize; // the maximal dimension of the buffer, to avoid overflow; if -1 is unlimited TODO add to DOC; default 10000 (defined in DCB);
	// private Time bufferSizeTimeLimit;
	// private boolean isSizeLimited;
	// private boolean isTimeLimited;

	// XXX - Are lock really necessary?
	// We have only two thread that use this session ideally:
	//  - StreamReader: this is the thread that read data from Darwin. It only operate in write mode over the SessionDBCom by the means of insert()
	//  - MainThread: this is the main thread that read data from the SessionDBCom; it operates in read mode through the select() and in write mode (rare)
	//		using resizeBuffer() or flush();
	// if StreamReader is writing MainThread cannot write or read;
	// if MainThread is reading/writing itself cannot read/write (it's single threaded) and MainThread cannot write.
	// So eventually simply by adding synchronize to every method we would be doing things right.
	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock(true); // we need a fair lock, even if it slower it's more reliable
	private final Lock r = rwl.readLock();
	private final Lock w = rwl.writeLock();

	/*
	 * Random notes:
	 * 	I dati che mi arrivano dallo stream possono mostrare più di una transazione nello stesso istante
	 * pertanto vengono forniti i metodi di selezione:
	 *  - puntuale in un dato secondo che ritorna i dati delle transazioni avvenute in quel secondo
	 *  - puntuale in un dato secondo che ritorna i dati aggregati in quel secondo (stile candela)
	 *  - range restituisce i dati nel range definito di tempo, non aggregati
	 *  
	 *  TODO controllare sempre nelle select che siano stati campionati dei tempi!!!
	 */
	
	/**
	 * Constructor, it call a flush over data
	 * 
	 * @param bufferMaxSize
	 * @param dcb
	 */
	SessionDBCom(int bufferMaxSize, MTClientBaseCom dcb) {

		log = dcb.log();
		log.c(this, "constructor; size: " + bufferMaxSize);

		bufferSize = bufferMaxSize;
		flush();
	}
	
	/**
	 * Empty the database deleting all data recorded up to this point
	 */
	public synchronized void flush() {
		log.i(this, "flush");
		w.lock();
		try {
			indexPriceMap = new TreeMap<String, Integer>();
			indexBook5Map = new TreeMap<String, Integer>();
			indexBidAskMap = new TreeMap<String, Integer>();
	
			timePrice = new ArrayList<String>();
			timeBook5 = new ArrayList<String>();
			timeBidAsk = new ArrayList<String>();
	
			priceData = new ArrayList<PriceDataCom>();
			price = new ArrayList<Double>(); // TODO remove this
			prMin = new ArrayList<Double>(); // TODO remove this
			prMax = new ArrayList<Double>(); // TODO remove this
			qty = new ArrayList<Long>(); // TODO remove this
			progStocks = new ArrayList<Long>(); // TODO remove this
			progExchanges = new ArrayList<Long>(); // TODO remove this
	
			book5 = new ArrayList<Book5Com>();
			bidAsk = new ArrayList<BidAskCom>();
	
			indexPrice = 0;
			indexBook5 = 0;
			indexBidAsk = 0;
		} finally {
			w.unlock();
		}
	}
	
	/**
	 * Change the size of this database
	 * 
	 * @param bufferMaxSize -the new maximal size
	 * 
	 * @see MTClientBaseCom#defaultSessionBuffer(int)
	 */
	public synchronized void resizeBuffer(int bufferMaxSize) {
		log.i(this, "resize; size: " + bufferMaxSize);
		bufferSize = bufferMaxSize;
		if(bufferSize < 0)
			bufferSize = 0;
		// isSizeLimited = true;
		// isTimeLimited = false;
		truncateBufferIfNecessary();
	}
	
	/**
	 * If buffer size is greater than limit truncate the database
	 */
	private synchronized void truncateBufferIfNecessary() {
		// check if DB is consistent, else remove everything
		if(bufferSize <= 0) {
			flush();
			return;
		}
		if( timePrice.size() != priceData.size() || priceData.size() != indexPrice ||
				timeBook5.size() != book5.size() || book5.size() != indexBook5 || 
				timeBidAsk.size() != bidAsk.size() || bidAsk.size() != indexBidAsk ) {
			log.w(this, "data size non consinstent. "
					+ "timePrice.size() = " + timePrice.size() + "; "
					+ "priceData.size() = " + priceData.size() + "; "
					+ "timeBook5.size() = " + timeBook5.size() + "; "
					+ "book5.size() = " + book5.size() + "; "
					+ "timeBidAsk.size() = " + timeBidAsk.size() + "; "
					+ "bidAsk.size() = " + bidAsk.size() + "; "
					+ "indexPrice = " + indexPrice + "; "
					+ "indexBook5 = " + indexBook5 + "; "
					+ "indexBidAsk = " + indexBidAsk + "; ");
			flush();
			return;
		}

		// get the time relative the last element that we need to delete and decide who need truncation and how much it will get truncated
		String truncateTime = null;
		
		boolean truncatePrice = false;
		boolean truncateBook5 = false;
		boolean truncateBidAsk = false;

		int deletedPriceSize = 0;
		int deletedBook5Size = 0;
		int deletedBidAskSize = 0;
		
		int lastIndex = 0;
		
		
		// TODO handle isSizeLimited/isTimeLimited
		String tempTruncateTime = null;
		
		w.lock();
		try {
			if(timePrice.size() > bufferSize * 1.1) { // we give a little tolerance of 10% just to avoid to resize the DB at every iteration
				try {
					tempTruncateTime = getTruncateTime(indexPriceMap, timePrice);
				} catch (Exception e) {
					this.flush();
					return;
				}
				truncateTime = tempTruncateTime;
				if(tempTruncateTime != null)
					truncatePrice = true;
			}
			if(timeBook5.size() > bufferSize * 1.1) { // we give a little tolerance of 10% just to avoid to resize the DB at every iteration
				try {
					tempTruncateTime = getTruncateTime(indexBook5Map, timeBook5);
				} catch (Exception e) {
					this.flush();
					return;
				}
				if(truncateTime == null)
					truncateTime = tempTruncateTime;
				else if(tempTruncateTime != null && tempTruncateTime.compareTo(truncateTime) > 0)
					truncateTime = tempTruncateTime;
				if(tempTruncateTime != null)
					truncateBook5 = true;
			}
			if(timeBidAsk.size() > bufferSize * 1.1) { // we give a little tolerance of 10% just to avoid to resize the DB at every iteration
				try {
					tempTruncateTime = getTruncateTime(indexBidAskMap, timeBidAsk);
				} catch (Exception e) {
					this.flush();
					return;
				}
				if(truncateTime == null)
					truncateTime = tempTruncateTime;
				else if(tempTruncateTime != null && tempTruncateTime.compareTo(truncateTime) > 0)
					truncateTime = tempTruncateTime;
				if(tempTruncateTime != null)
					truncateBidAsk = true;
			}
			if(truncateTime != null) {
				if(truncatePrice) {
					lastIndex = indexPriceMap.get(indexPriceMap.floorKey(truncateTime));
					deletedPriceSize = lastIndex + 1; // eval how many items we are gonna remove
					timePrice.subList(0, lastIndex + 1).clear(); // remove unnecessary items
					priceData.subList(0, lastIndex + 1).clear();
					price.subList(0, lastIndex + 1).clear();
					prMin.subList(0, lastIndex + 1).clear();
					prMax.subList(0, lastIndex + 1).clear();
					qty.subList(0, lastIndex + 1).clear();
					progStocks.subList(0, lastIndex + 1).clear();
					progExchanges.subList(0, lastIndex + 1).clear();
					indexPriceMap.headMap(truncateTime, true).clear();
					for(String key : indexPriceMap.keySet())
						indexPriceMap.put(key, indexPriceMap.get(key) - deletedPriceSize); // reset index range
					indexPrice -= deletedPriceSize;

					log.i(this, "truncate priceData; deleted: " + deletedPriceSize + "; new size: " + timePrice.size());
				}
				if(truncateBook5) {
					lastIndex = indexBook5Map.get(indexBook5Map.floorKey(truncateTime));
					deletedBook5Size = lastIndex + 1; // eval how many items we are gonna remove
					timeBook5.subList(0, lastIndex + 1).clear(); // remove unnecessary items
					book5.subList(0, lastIndex + 1).clear();
					indexBook5Map.headMap(truncateTime, true).clear();
//					for(Integer index : indexBook5Map.values())
//						index -= deletedBook5Size;	// reset index range
					for(String key : indexBook5Map.keySet())
						indexBook5Map.put(key, indexBook5Map.get(key) - deletedBook5Size); // reset index range
					indexBook5 -= deletedBook5Size;

					log.i(this, "truncate book5; deleted: " + deletedBook5Size + "; new size: " + timeBook5.size());
				}
				if(truncateBidAsk) {
					lastIndex = indexBidAskMap.get(indexBidAskMap.floorKey(truncateTime));
					deletedBidAskSize = lastIndex + 1; // eval how many items we are gonna remove
					timeBidAsk.subList(0, lastIndex + 1).clear(); // remove unnecessary items
					bidAsk.subList(0, lastIndex + 1).clear();
					indexBidAskMap.headMap(truncateTime, true).clear();
					for(String key : indexBidAskMap.keySet())
						indexBidAskMap.put(key, indexBidAskMap.get(key) - deletedBidAskSize); // reset index range
					indexBidAsk -= deletedBidAskSize;

					log.i(this, "truncate bidAsk; deleted: " + deletedBidAskSize + "; new size: " + timeBook5.size());
				}
			}
		} finally {
			w.unlock();
		}
	}
	
	/**
	 * This method is used to detect the time at which truncation of the database should occur
	 * 
	 * @param indexMap - the index map considered
	 * @param timeList - the time list considered
	 * 
	 * @return the time at which truncation should occur
	 * @throws Exception 
	 */
	private String getTruncateTime(TreeMap<String, Integer> indexMap, ArrayList<String> timeList) throws Exception {
		ArrayList<Integer[]> sectors = getSectors(indexMap, timeList, timeList.get(0), timeList.get(timeList.size() - 1));
		if(sectors == null || sectors.isEmpty()) {
			return null;
		}
		int cumSize = 0; // within this var will be stored the number of elements found
		
		int i = sectors.size();
		Integer[] sector = null;
		while(cumSize <= bufferSize && i > 0) {
			--i;
			sector = sectors.get(i);
			cumSize += sector[1] - sector[0] + 1;
		}
		if(cumSize <= bufferSize) { // it should be always false since we are in that function
			log.w(this, "Buffer should have been truncated but apparently it seems it shouldn't. "
					+ "cumSize = " + cumSize + "; bufferSize = " + bufferSize
					+ "; timeList.size() = " + timeList.size() + ". The session will be flushed.");
			throw new Exception();
		}
		return timeList.get(sector[1] + 1);
	}
	
	/**
	 * @return true if an insert can be executed
	 */
	private boolean canWeStore() {
		if(bufferSize <= 0)
			return false;
		return true;
	}

	/**
	 * Insert in the database price data
	 * 
	 * @param timePrice - timestamp at which the price has been received
	 * @param priceData - data to be saved
	 */
	synchronized void insert(String timePrice, PriceDataCom priceData) {
		if(canWeStore() == false)
			return;
		log.fff(this,"priceData insert into SessionDB");

		w.lock();
		try {
			indexPriceMap.put(timePrice, indexPrice);
			indexPrice++;
			this.timePrice.add(timePrice);
			this.priceData.add(priceData);
			this.price.add(priceData.price());
			this.prMin.add(priceData.priceMin());
			this.prMax.add(priceData.priceMax());
			this.qty.add(priceData.volume());
			this.progStocks.add(priceData.progStocks());
			this.progExchanges.add(priceData.progExchanges());
		} finally {
			w.unlock();
		}
		truncateBufferIfNecessary();
	}

	/**
	 * Insert in the database data of five levels of book
	 * 
	 * @param timeBook - timestamp at which the book5 has been received
	 * @param book - book to be saved
	 */
	synchronized void insert(String timeBook, Book5Com book) {
		if(canWeStore() == false)
			return;
		log.fff(this,"book5 insert into SessionDB");

		w.lock();
		try {
			indexBook5Map.put(timeBook, indexBook5);
			indexBook5++;
			this.timeBook5.add(timeBook);
			this.book5.add(book);
		} finally {
			w.unlock();
		}
		truncateBufferIfNecessary();
	}

	/**
	 * Insert in the database data of one levels of book
	 * 
	 * @param timeBidAsk - timestamp at which the bidAsk has been received
	 * @param bidAsk - book to be saved
	 */
	synchronized void insert(String timeBidAsk, BidAskCom bidAsk) {
		if(canWeStore() == false)
			return;
		log.fff(this,"priceData insert into SessionDB");

		w.lock();
		try {
			indexBidAskMap.put(timeBidAsk, indexBidAsk);
			indexBidAsk++;
			this.timeBidAsk.add(timeBidAsk);
			this.bidAsk.add(bidAsk);
		} finally {
			w.unlock();
		}
		truncateBufferIfNecessary();
	}
	
	// Select methods; eventually we can parametrize them. (TODO) or a least we can create
	// a parametrized method!!!!!!!!!!!!!!

	/**
	 * Select price aggregated at second averaged with volumes at a specific
	 * time. If time is null or no price information has been received at that
	 * time, null is returned.
	 * 
	 * @param time - the instant at which we are interested in analyzing data
	 * 
	 * @return the average weighted price or null if time is not valid
	 */
	public Double selectAvgWeightedPrice(String time) {
		
		//XXX To check in Matlab against american market
		
		if(time == null) return null;
//		Double avgPrice = new Double(0D);
//		for(Double price : selectPriceDetails(time, time)) {
//			avgPrice += price;
//		}
//		Long totVolume = selectVolume(time);
//		if(totVolume == null || totVolume == 0L)
//			return null;
//		return avgPrice/totVolume;
		ArrayList<Double> avgList = selectAvgWeightedPrice(time, time);
		if(avgList == null || avgList.isEmpty())
			return null;
		return avgList.get(0);
	}

	/**
	 * Select price aggregated at second averaged with volumes in a specific
	 * range of time. If time is null or no price information has been received
	 * at that time, null is returned.
	 * 
	 * @param timeB - the start time at which we are interested in analyzing data
	 * @param timeE - the end time at which we are interested in analyzing data
	 * 
	 * @return the average weighted price or null if time is not valid
	 */
	public ArrayList<Double> selectAvgWeightedPrice(String timeB, String timeE) {

		if(timeB == null && timeE == null) return new ArrayList<Double>();
		
		ArrayList<Double> result = new ArrayList<Double>();
		
		r.lock();
		try {
			ArrayList<Integer[]> sectors = getSectors(indexPriceMap, timePrice, timeB, timeE);
			
			if(sectors == null || sectors.size() == 0 || sectors.get(0) == null)
				return result;
	
			Double avgPrice;
			Long totVolume;
			Long volume;
			int i;
			for(Integer[] sector : sectors) {
				totVolume = new Long(0L);
				avgPrice = new Double(0D);
				for(i = sector[0]; i <= sector[1]; ++i) {
					volume = qty.get(i);
					totVolume += volume;
					avgPrice += price.get(i) * volume;
				}
				if(totVolume == 0)
					result.add(null);
				else
					result.add(avgPrice/totVolume);
			}
			return result;
		} finally {
			r.unlock();
		}
	}

	/**
	 * Select the last price registered at the specified time. If time is null
	 * or no price information has been received at that time, null is
	 * returned.
	 * 
	 * @param time - the instant at which we are interested in analyzing data
	 * 
	 * @return the average weighted price or null if time is not valid
	 */
	public Double selectPrice(String time) {
		if(time == null) return null;
		ArrayList<Double> list = selectPriceDetails(time);
		return list.size() > 0 ? list.get(list.size() - 1) : null;
	}

	/**
	 * Select the last price registered at every second in the specified time
	 * range. If time is null or no price information has been received in 
	 * this time range, null is returned.
	 * 
	 * @param timeB - the start time at which we are interested in analyzing data
	 * @param timeE - the end time at which we are interested in analyzing data
	 * 
	 * @return the average weighted price or null if time is not valid
	 */
	public ArrayList<Double> selectPrice(String timeB, String timeE) {

		if(timeB == null && timeE == null) return new ArrayList<Double>();
		
		ArrayList<Double> result = new ArrayList<Double>();

		r.lock();
		try {
			ArrayList<Integer[]> sectors = getSectors(indexPriceMap, timePrice, timeB, timeE);
	
			if(sectors == null || sectors.isEmpty() || sectors.get(0) == null)
				return result;
	
			for(Integer[] sector : sectors) {
				result.add(price.get(sector[1]));
			}
			return result;
		} finally {
			r.unlock();
		}
	}

	/**
	 * Select all prices registered at the specified time. If time is null
	 * or no price information has been received at that time, null is
	 * returned.
	 * 
	 * @param time - the instant at which we are interested in analyzing data
	 * 
	 * @return the average weighted price or null if time is not valid
	 */
	public ArrayList<Double> selectPriceDetails(String time) {
		return selectPriceDetails(time, time);
	}

	/**
	 * Select all prices registered at every second in the specified time
	 * range. If time is null or no price information has been received in 
	 * this time range, null is returned.
	 * 
	 * @param timeB - the start time at which we are interested in analyzing data
	 * @param timeE - the end time at which we are interested in analyzing data
	 * 
	 * @return the average weighted price or null if time is not valid
	 */
	public ArrayList<Double> selectPriceDetails(String timeB, String timeE) {
		if(timeB == null && timeE == null) return new ArrayList<Double>();
		
		ArrayList<Double> result = new ArrayList<Double>();

		r.lock();
		try {
			Integer[] limits = getFloorAndHigher(indexPriceMap, timeB, timeE);
	
			if(limits == null || limits[0] == null || limits[1] == null)
				return result;
			result.addAll(price.subList(limits[0], limits[1] + 1));
			return result;
		} finally {
			r.unlock();
		}
	}

	/**
	 * Select the last daily minimum price registered at the specified time.
	 * If time is null or no price information has been received at that time,
	 * null is returned.
	 * 
	 * @param time - the instant at which we are interested in analyzing data
	 * 
	 * @return the average weighted price or null if time is not valid
	 */
	public Double selectDailyMinPrice(String time) {

		if(time == null) return null;
		ArrayList<Double> list = selectDailyMinPriceDetails(time);
		return list.size() > 0 ? list.get(list.size() - 1) : null;
	}

	/**
	 * Select the last daily minimum price registered at every second in the
	 * specified time range. If time is null or no price information has been
	 * received in this time range, null is returned.
	 * 
	 * @param timeB - the start time at which we are interested in analyzing data
	 * @param timeE - the end time at which we are interested in analyzing data
	 * 
	 * @return the average weighted price or null if time is not valid
	 */
	public ArrayList<Double> selectDailyMinPrice(String timeB, String timeE) {

		if(timeB == null && timeE == null) return new ArrayList<Double>();
		
		ArrayList<Double> result = new ArrayList<Double>();

		r.lock();
		try {
			ArrayList<Integer[]> sectors = getSectors(indexPriceMap, timePrice, timeB, timeE);
	
			if(sectors == null || sectors.isEmpty() || sectors.get(0) == null)
				return result;
			
			for(Integer[] sector : sectors) {
				result.add(prMin.get(sector[1]));
			}
			return result;
		} finally {
			r.unlock();
		}
	}

	/**
	 * Select daily minimum price at maximal detail in the specified instant.
	 * If time is null or no price information has been received at that time,
	 * null is returned.
	 * 
	 * @param time - the instant at which we are interested in analyzing data
	 * 
	 * @return the average weighted price or null if time is not valid
	 */
	public ArrayList<Double> selectDailyMinPriceDetails(String time) {
		return selectDailyMinPriceDetails(time, time);
	}

	/**
	 * Select daily minimum price at maximal detail at every second in the
	 * specified time range. If time is null or no price information has been
	 * received in this time range, null is returned.
	 * 
	 * @param timeB - the start time at which we are interested in analyzing data
	 * @param timeE - the end time at which we are interested in analyzing data
	 * 
	 * @return the average weighted price or null if time is not valid
	 */
	public ArrayList<Double> selectDailyMinPriceDetails(String timeB, String timeE) {
		if(timeB == null && timeE == null) return new ArrayList<Double>();
		
		ArrayList<Double> result = new ArrayList<Double>();

		r.lock();
		try {
			Integer[] limits = getFloorAndHigher(indexPriceMap, timeB, timeE);
	
			if(limits == null || limits[0] == null || limits[1] == null)
				return result;
			result.addAll(prMin.subList(limits[0], limits[1] + 1));
			return result;
		} finally {
			r.unlock();
		}
	}

	/**
	 * Select the last daily maximum price registered at the specified time.
	 * If time is null or no price information has been received at that time,
	 * null is returned.
	 * 
	 * @param time - the instant at which we are interested in analyzing data
	 * 
	 * @return the average weighted price or null if time is not valid
	 */
	public Double selectDailyMaxPrice(String time) {

		if(time == null) return null;
		ArrayList<Double> list = selectDailyMaxPriceDetails(time);
		return list.size() > 0 ? list.get(list.size() - 1) : null;
	}

	/**
	 * Select the last daily maximum price registered at every second in the
	 * specified time range. If time is null or no price information has been
	 * received in this time range, null is returned.
	 * 
	 * @param timeB - the start time at which we are interested in analyzing data
	 * @param timeE - the end time at which we are interested in analyzing data
	 * 
	 * @return the average weighted price or null if time is not valid
	 */
	public ArrayList<Double> selectDailyMaxPrice(String timeB, String timeE) {

		if(timeB == null && timeE == null) return new ArrayList<Double>();
		
		ArrayList<Double> result = new ArrayList<Double>();

		r.lock();
		try {
			ArrayList<Integer[]> sectors = getSectors(indexPriceMap, timePrice, timeB, timeE);
	
			if(sectors == null || sectors.isEmpty() || sectors.get(0) == null)
				return result;
	
			for(Integer[] sector : sectors) {
				result.add(prMax.get(sector[1]));
			}
			return result;
		} finally {
			r.unlock();
		}
	}

	/**
	 * Select daily maximum price at maximal detail in the specified instant.
	 * If time is null or no price information has been received at that time,
	 * null is returned.
	 * 
	 * @param time - the instant at which we are interested in analyzing data
	 * 
	 * @return the average weighted price or null if time is not valid
	 */
	public ArrayList<Double> selectDailyMaxPriceDetails(String time) {
		return selectDailyMaxPriceDetails(time, time);
	}

	/**
	 * Select daily maximum price at maximal detail at every second in the
	 * specified time range. If time is null or no price information has been
	 * received in this time range, null is returned.
	 * 
	 * @param timeB - the start time at which we are interested in analyzing data
	 * @param timeE - the end time at which we are interested in analyzing data
	 * 
	 * @return the average weighted price or null if time is not valid
	 */
	public ArrayList<Double> selectDailyMaxPriceDetails(String timeB, String timeE) {
		if(timeB == null && timeE == null) return new ArrayList<Double>();
		
		ArrayList<Double> result = new ArrayList<Double>();

		r.lock();
		try {
			Integer[] limits = getFloorAndHigher(indexPriceMap, timeB, timeE);
	
			if(limits == null || limits[0] == null || limits[1] == null)
				return result;
			result.addAll(prMax.subList(limits[0], limits[1] + 1));
			return result;
		} finally {
			r.unlock();
		}
	}

	/**
	 * Select the total volume at the specified time
	 * 
	 * @param time - the instant at which we are interested in analyzing data
	 * 
	 * @return total volume at the specified time
	 */
	public Long selectVolume(String time) {
		if(time == null) return null;
		Long totVolume = new Long(0L);
		for(Long volume : selectVolumeDetails(time, time)) {
			totVolume += volume;
		}
		return totVolume;
	}

	/**
	 * Select volumes at maximal detail at the specified time
	 * 
	 * @param time - the instant at which we are interested in analyzing data
	 * 
	 * @return detailed volumes at the specified time
	 */
	public ArrayList<Long> selectVolumeDetails(String time) {
		return selectVolumeDetails(time, time);
	}

	/**
	 * Select the total volume at every second in the specified time range
	 * 
	 * @param timeB - the start time at which we are interested in analyzing data
	 * @param timeE - the end time at which we are interested in analyzing data
	 * 
	 * @return total volume at the specified time
	 */
	public ArrayList<Long> selectVolume(String timeB, String timeE) {
		if(timeB == null && timeE == null) return new ArrayList<Long>();
		
		ArrayList<Long> result = new ArrayList<Long>();

		r.lock();
		try {
			ArrayList<Integer[]> sectors = getSectors(indexPriceMap, timePrice, timeB, timeE);
			
			if(sectors == null || sectors.isEmpty() || sectors.get(0) == null)
				return result;
			
			Long totVolume;
			int i;
			for(Integer[] sector : sectors) {
				totVolume = new Long(0L);
				for(i = sector[0]; i <= sector[1]; ++i) {
					totVolume += qty.get(i);
				}
				result.add(totVolume);
			}
			return result;
		} finally {
			r.unlock();
		}
	}

	/**
	 * Select volume at maximal detail at every second in the specified time
	 * range
	 * 
	 * @param timeB - the start time at which we are interested in analyzing data
	 * @param timeE - the end time at which we are interested in analyzing data
	 * 
	 * @return total volume at the specified time
	 */
	public ArrayList<Long> selectVolumeDetails(String timeB, String timeE) {
		if(timeB == null && timeE == null) return new ArrayList<Long>();
		
		ArrayList<Long> result = new ArrayList<Long>();

		r.lock();
		try {
			Integer[] limits = getFloorAndHigher(indexPriceMap, timeB, timeE);
	
			if(limits == null || limits[0] == null || limits[1] == null)
				return result;
			result.addAll(qty.subList(limits[0], limits[1] + 1));
			return result;
		} finally {
			r.unlock();
		}
	}

	/**
	 * Select the progressive stock at the end of specified second
	 * 
	 * @param time - the instant at which we are interested in analyzing data
	 * 
	 * @return progressive stock at the specified time
	 */
	public Long selectProgStocks(String time) {
		if(time == null) return null;
		
		r.lock();
		try {
			Integer index = indexPriceMap.get(time);
			if(index == null)
				return null;
			return progStocks.get(index);
		} finally {
			r.unlock();
		}
	}

	/**
	 * Select the progressive stock at the end of every second in the specified
	 * range of time
	 * 
	 * @param timeB - the start time at which we are interested in analyzing data
	 * @param timeE - the end time at which we are interested in analyzing data
	 * 
	 * @return progressive stock at the specified time
	 */
	public ArrayList<Long> selectProgStocks(String timeB, String timeE) {
		if(timeB == null && timeE == null) return new ArrayList<Long>();
		
		ArrayList<Long> result = new ArrayList<Long>();

		r.lock();
		try {
			ArrayList<Integer[]> sectors = getSectors(indexPriceMap, timePrice, timeB, timeE);
			
			if(sectors == null || sectors.isEmpty() || sectors.get(0) == null)
				return result;
	
			for(Integer[] sector : sectors) {
				result.add(progStocks.get(sector[1]));
			}
			return result;
		} finally {
			r.unlock();
		}
	}

	/**
	 * Select the progressive stock at maximal detail in the specified second
	 * 
	 * @param time - the instant at which we are interested in analyzing data
	 * 
	 * @return progressive stock at the specified time
	 */
	public ArrayList<Long> selectProgStocksDetails(String time) {
		return selectProgStocksDetails(time, time);
	}

	/**
	 * Select the progressive stock at maximal detail in the specified range of
	 * time
	 * 
	 * @param timeB - the start time at which we are interested in analyzing data
	 * @param timeE - the end time at which we are interested in analyzing data
	 * 
	 * @return progressive stock at the specified time
	 */
	public ArrayList<Long> selectProgStocksDetails(String timeB, String timeE) {
		if(timeB == null && timeE == null) return new ArrayList<Long>();
		
		ArrayList<Long> result = new ArrayList<Long>();

		r.lock();
		try {
			Integer[] limits = getFloorAndHigher(indexPriceMap, timeB, timeE);
	
			if(limits == null || limits[0] == null || limits[1] == null)
				return result;
			result.addAll(progStocks.subList(limits[0], limits[1] + 1));
			return result;
		} finally {
			r.unlock();
		}
	}

	/**
	 * Select the progressive exchanges at the end of specified second
	 * 
	 * @param time - the instant at which we are interested in analyzing data
	 * 
	 * @return progressive stock at the specified time
	 */
	public Long selectProgExchanges(String time) {
		if(time == null) return null;

		r.lock();
		try {
			Integer index = indexPriceMap.get(time);
			if(index == null)
				return null;
			return progExchanges.get(index);
		} finally {
			r.unlock();
		}
	}

	/**
	 * Select the progressive exchanges at the end of every second in the specified
	 * range of time
	 * 
	 * @param timeB - the start time at which we are interested in analyzing data
	 * @param timeE - the end time at which we are interested in analyzing data
	 * 
	 * @return progressive stock at the specified time
	 */
	public ArrayList<Long> selectProgExchanges(String timeB, String timeE) {
		if(timeB == null && timeE == null) return new ArrayList<Long>();
		
		ArrayList<Long> result = new ArrayList<Long>();

		r.lock();
		try {
			ArrayList<Integer[]> sectors = getSectors(indexPriceMap, timePrice, timeB, timeE);
			
			if(sectors == null || sectors.isEmpty() || sectors.get(0) == null)
				return result;
	
			for(Integer[] sector : sectors) {
				result.add(progExchanges.get(sector[1]));
			}
			return result;
		} finally {
			r.unlock();
		}
	}

	/**
	 * Select the progressive exchanges at maximal detail in the specified second
	 * 
	 * @param time - the instant at which we are interested in analyzing data
	 * 
	 * @return progressive stock at the specified time
	 */
	public ArrayList<Long> selectProgExchangesDetails(String time) {
		return selectProgExchangesDetails(time, time);
	}

	/**
	 * Select the progressive exchanges at maximal detail in the specified range of
	 * time
	 * 
	 * @param timeB - the start time at which we are interested in analyzing data
	 * @param timeE - the end time at which we are interested in analyzing data
	 * 
	 * @return progressive stock at the specified time
	 */
	public ArrayList<Long> selectProgExchangesDetails(String timeB, String timeE) {
		if(timeB == null && timeE == null) return new ArrayList<Long>();
		
		ArrayList<Long> result = new ArrayList<Long>();

		r.lock();
		try {
			Integer[] limits = getFloorAndHigher(indexPriceMap, timeB, timeE);
	
			if(limits == null || limits[0] == null || limits[1] == null)
				return result;
			result.addAll(progExchanges.subList(limits[0], limits[1] + 1));
			return result;
		} finally {
			r.unlock();
		}
	}

	/**
	 * Select the priceData at maximal detail in the specified second
	 * 
	 * @param time - the instant at which we are interested in analyzing data
	 * 
	 * @return progressive stock at the specified time
	 */
	public ArrayList<PriceDataCom> selectPriceData(String time) {
		return selectPriceData(time, time);
	}

	/**
	 * Select the priceData at maximal detail in the specified range of time
	 * 
	 * @param timeB - the start time at which we are interested in analyzing data
	 * @param timeE - the end time at which we are interested in analyzing data
	 * 
	 * @return progressive stock at the specified time
	 */
	public ArrayList<PriceDataCom> selectPriceData(String timeB, String timeE) {
		if(timeB == null && timeE == null) return new ArrayList<PriceDataCom>();
		
		ArrayList<PriceDataCom> result = new ArrayList<PriceDataCom>();

		r.lock();
		try {
			Integer[] limits = getFloorAndHigher(indexPriceMap, timeB, timeE);
			
			if(limits == null || limits[0] == null || limits[1] == null)
				return result;
			result.addAll(priceData.subList(limits[0], limits[1] + 1));
			return result;
		} finally {
			r.unlock();
		}
	}

	/**
	 * Select the book5 at maximal detail in the specified second
	 * 
	 * @param time - the instant at which we are interested in analyzing data
	 * 
	 * @return progressive stock at the specified time
	 */
	public ArrayList<Book5Com> selectBook5(String time) {
		return selectBook5(time, time);
	}

	/**
	 * Select the book5 at maximal detail in the specified range of time
	 * 
	 * @param timeB - the start time at which we are interested in analyzing data
	 * @param timeE - the end time at which we are interested in analyzing data
	 * 
	 * @return progressive stock at the specified time
	 */
	public ArrayList<Book5Com> selectBook5(String timeB, String timeE) {
		if(timeB == null && timeE == null) return new ArrayList<Book5Com>();
		
		ArrayList<Book5Com> result = new ArrayList<Book5Com>();

		r.lock();
		try {
			Integer[] limits = getFloorAndHigher(indexBook5Map, timeB, timeE);
			
			if(limits == null || limits[0] == null || limits[1] == null)
				return result;
			result.addAll(book5.subList(limits[0], limits[1] + 1));
			return result;
		} finally {
			r.unlock();
		}
	}

	/**
	 * Select the bidAsk at maximal detail in the specified second
	 * 
	 * @param time - the instant at which we are interested in analyzing data
	 * 
	 * @return progressive stock at the specified time
	 */
	public ArrayList<BidAskCom> selectBidAsk(String time) {
		return selectBidAsk(time, time);
	}

	/**
	 * Select the bidAsk at maximal detail in the specified range of time
	 * 
	 * @param timeB - the start time at which we are interested in analyzing data
	 * @param timeE - the end time at which we are interested in analyzing data
	 * 
	 * @return progressive stock at the specified time
	 */
	public ArrayList<BidAskCom> selectBidAsk(String timeB, String timeE) {
		if(timeB == null && timeE == null) return new ArrayList<BidAskCom>();
		
		ArrayList<BidAskCom> result = new ArrayList<BidAskCom>();

		r.lock();
		try {
			Integer[] limits = getFloorAndHigher(indexBidAskMap, timeB, timeE);
			
			if(limits == null || limits[0] == null || limits[1] == null)
				return result;
			result.addAll(bidAsk.subList(limits[0], limits[1] + 1));
			return result;
		} finally {
			r.unlock();
		}
	}

	/**
	 * Select times of received prices at maximal detail in the specified second
	 * 
	 * @param time - the instant at which we are interested in analyzing data
	 * 
	 * @return progressive stock at the specified time
	 */
	public ArrayList<String> selectTimePriceDetails(String time) {
		return selectTimePriceDetails(time, time);
	}

	/**
	 * Select times of received prices at maximal detail in the specified range
	 * of time
	 * 
	 * @param timeB - the start time at which we are interested in analyzing data
	 * @param timeE - the end time at which we are interested in analyzing data
	 * 
	 * @return progressive stock at the specified time
	 */
	public ArrayList<String> selectTimePriceDetails(String timeB, String timeE) {
		
		ArrayList<String> result = new ArrayList<String>();
		
		if(timeB == null && timeE == null) return result;

		r.lock();
		try {
			Integer[] limits = getFloorAndHigher(indexPriceMap, timeB, timeE);
			if(limits == null || limits[0] == null || limits[1] == null)
				return result;
			result.addAll(timePrice.subList(limits[0], limits[1] + 1));
			return result;
		} finally {
			r.unlock();
		}
	}

	/**
	 * Select times of received book5 at maximal detail in the specified second
	 * 
	 * @param time - the instant at which we are interested in analyzing data
	 * 
	 * @return progressive stock at the specified time
	 */
	public ArrayList<String> selectTimeBook5Details(String time) {
		return selectTimeBook5Details(time, time);
	}

	/**
	 * Select times of received book5 at maximal detail in the specified range
	 * of time
	 * 
	 * @param timeB - the start time at which we are interested in analyzing data
	 * @param timeE - the end time at which we are interested in analyzing data
	 * 
	 * @return progressive stock at the specified time
	 */
	public ArrayList<String> selectTimeBook5Details(String timeB, String timeE) {
		
		ArrayList<String> result = new ArrayList<String>();
		
		if(timeB == null && timeE == null) return result;

		r.lock();
		try {
			Integer[] limits = getFloorAndHigher(indexBook5Map, timeB, timeE);
			if(limits == null || limits[0] == null || limits[1] == null)
				return result;
			result.addAll(timeBook5.subList(limits[0], limits[1] + 1));
			return result;
		} finally {
			r.unlock();
		}
	}

	/**
	 * Select times of received bidAsk at maximal detail in the specified second
	 * 
	 * @param time - the instant at which we are interested in analyzing data
	 * 
	 * @return progressive stock at the specified time
	 */
	public ArrayList<String> selectTimeBidAskDetails(String time) {
		return selectTimeBidAskDetails(time, time);
	}

	/**
	 * Select times of received bidAsk at maximal detail in the specified range
	 * of time
	 * 
	 * @param timeB - the start time at which we are interested in analyzing data
	 * @param timeE - the end time at which we are interested in analyzing data
	 * 
	 * @return progressive stock at the specified time
	 */
	public ArrayList<String> selectTimeBidAskDetails(String timeB, String timeE) {
		
		ArrayList<String> result = new ArrayList<String>();
		
		if(timeB == null && timeE == null) return result;

		r.lock();
		try {
			Integer[] limits = getFloorAndHigher(indexBidAskMap, timeB, timeE);
			if(limits == null || limits[0] == null || limits[1] == null)
				return result;
			result.addAll(timeBidAsk.subList(limits[0], limits[1] + 1));
			return result;
		} finally {
			r.unlock();
		}
	}

	/**
	 * @return the complete list of timestamp in which we received prices
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getTimePrice() {
		r.lock();
		try {
			return (ArrayList<String>) timePrice.clone();
		} finally {
			r.unlock();
		}
	}

	/**
	 * @return the complete list of timestamp in which we received book5
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getTimeBook5() {
		r.lock();
		try {
			return (ArrayList<String>) timeBook5.clone();
		} finally {
			r.unlock();
		}
	}

	/**
	 * @return the complete list of timestamp in which we received bidAsk
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getTimeBidAsk() {
		r.lock();
		try {
			return (ArrayList<String>) timeBidAsk.clone();
		} finally {
			r.unlock();
		}
	}

	/**
	 * Return an ArrayList containing couples of indexes; each couple delimits a sector
	 * of the timeList; each sector contains elements that share the same time.
	 * <p>
	 *  ie.
	 *  <pre>
	 *  	timeList = {133031, 133031, 133031, 133031, 133032, 133035, 133035};
	 *  	sectors = {{0,3},{4,4},{5,6}};
	 * </pre>
	 * @param map
	 * @param timeList
	 * @param timeB
	 * @param timeE
	 * @return an {@code ArrayList<Integer[]>} containing couples of indexes
	 */
	private ArrayList<Integer[]> getSectors(TreeMap<String,Integer> map, ArrayList<String> timeList,
			String timeB, String timeE) {

		/*
		 * XXX Maybe map.submap().values() may be a better idea
		 */
		
		ArrayList<Integer[]> result = new ArrayList<Integer[]>();
		Integer[] resultInt;

		Integer[] limit = getFloorAndHigher(map, timeB, timeE);
		
		if(limit == null || limit[0] == null || limit[1] == null) 
			return result;
		
		int i = limit[0];
		while(i <= limit[1]) {
			resultInt = new Integer[2];
			if(i == limit[1]) {
				resultInt[0] = i;
				resultInt[1] = map.get(timeList.get(i));
				result.add(resultInt);
				++i;
			} else {
				resultInt[0] = i;
				resultInt[1] = map.get(timeList.get(i));
				result.add(resultInt);
				i = resultInt[1] + 1;
			}
		}

		return result;
	}

	/**
	 * Get the first and last time in the given map between timeB and timeE
	 * 
	 * @param map
	 * @param timeB
	 * @param timeE
	 * 
	 * @return an array of Integer of size 2 with the first and last time in
	 * defined range
	 */
	private Integer[] getFloorAndHigher(TreeMap<String,Integer> map, String timeB, String timeE) {

		/*
		 * XXX Maybe map.submap() -> lastKey()/firstKey() may be a good idea
		 */

		Integer floor, higher;
		
		if(timeB != null && timeE != null && timeB.compareTo(timeE) > 0)
			return getFloorAndHigher(map, timeE, timeB);
		
		// TODO ? Check if timeB and timeE are both null?

		if(map.isEmpty()) {
			floor = higher = null;
		} else if(timeB != null && timeE != null) {
			if(timeE.compareTo(map.firstKey()) < 0) {
				floor = higher = null;
			} else if(timeB.compareTo(map.lastKey()) > 0) {
				floor = higher = null;
			} else {
				timeB = map.lowerKey(timeB);
				timeE = map.floorKey(timeE);
				if(timeB == null)
					floor = -1;
				else
					floor = map.get(timeB);
				if(timeE == null) { // It should never happens since logic structure
					timeE = map.lastKey();
					// XXX To move to an error class? throws exceptions?
					System.err.println("SessionSB Exception: getFloorAndHigher() -> timeE is null; #7306336480314541");
				}
				higher = map.get(timeE);
			}
		} else {
			if(timeB == null) {
				if(timeE.compareTo(map.firstKey()) < 0) {
					floor = higher = null;
				} else {
					floor = -1;
					timeE = map.floorKey(timeE);
					if(timeE == null) { // It should never happens since logic structure
						timeE = map.lastKey();
						// XXX To move to an error class? throws exceptions?
						System.err.println("SessionSB Exception: getFloorAndHigher() -> timeE is null; #7306336480314541");
					}
					higher = map.get(timeE);
				}
			} else {
				if(timeB.compareTo(map.lastKey()) > 0)
					floor = higher = null;
				else {
					timeB = map.lowerKey(timeB);
					if(timeB == null)
						floor = -1;
					else
						floor = map.get(timeB);
					higher = map.get(map.lastKey());
				}
			}
		}
		
		Integer[] res = new Integer[2];
		if(floor == null && higher != null || floor != null && higher == null)
			System.err.println("SessionSB Exception: getFloorAndHigher() -> floor and higher are non conformant; #5284295466036307");

		if(floor == null && higher == null) {
			res[0] = res[1] = null;
			return res;
		}
		res[0] = floor + 1;
		res[1] = higher;
		
		return res;
	}
}