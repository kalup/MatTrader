package com.mattrader.matlab;

import java.util.ArrayList;
import java.util.Hashtable;

import com.mattrader.common.BidAskCom;
import com.mattrader.common.Book5Com;
import com.mattrader.common.OrderCom;
import com.mattrader.common.PriceDataCom;

class Utils {
	// TODO CHECK NULL AND EMPTYLIST

	static <T extends Number> double[] arrayListToArrayDouble(ArrayList<T> list) {
		if(list == null || list.isEmpty())
			return new double[0];
		int size = list.size();
		int i;
		double[] rop = new double[list.size()];
		for(i = 0; i < size; ++i) {
			rop[i] = list.get(i).doubleValue();
		}
		return rop;
	}
	
	static <T extends Number> long[] arrayListToArrayLong(ArrayList<T> list) {
		if(list == null || list.isEmpty())
			return new long[0];
		int size = list.size();
		int i;
		long[] rop = new long[list.size()];
		for(i = 0; i < size; ++i) {
			rop[i] = list.get(i).longValue();
		}
		return rop;
	}
	
	@SuppressWarnings("unchecked")
	static <T> T[] arrayListToArray(ArrayList<T> list) {
		if(list == null || list.isEmpty())
			return (T[]) new Object[0];
			//return (T[]) Array.newInstance(list.getClass().getComponentType().getClass(), 0);
		int size = list.size();
		int i;
		T[] rop = (T[]) new Object[list.size()];
		//T[] rop = (T[]) Array.newInstance(list.getClass().getComponentType().getClass(), list.size());
		for(i = 0; i < size; ++i) {
			rop[i] = list.get(i);
		}
		return rop;
	}
	
	static BidAsk[] arrayListToArrayBidAsk(ArrayList<BidAskCom> list) {
		if(list == null || list.isEmpty())
			return new BidAsk[0];
		int size = list.size();
		int i;
		BidAsk[] rop = new BidAsk[list.size()];
		for(i = 0; i < size; ++i) {
			try {
				rop[i] = new BidAsk(list.get(i));
			} catch (Exception e) {
				rop[i] = null;
			}
		}
		return rop;
	}
	
	static Book5[] arrayListToArrayBook(ArrayList<Book5Com> list) {
		if(list == null || list.isEmpty())
			return new Book5[0];
		int size = list.size();
		int i;
		Book5[] rop = new Book5[list.size()];
		for(i = 0; i < size; ++i) {
			try {
				rop[i] = new Book5(list.get(i));
			} catch (Exception e) {
				rop[i] = null;
			}
		}
		return rop;
	}
	
	static PriceData[] arrayListToArrayPriceData(ArrayList<PriceDataCom> list) {
		if(list == null || list.isEmpty())
			return new PriceData[0];
		int size = list.size();
		int i;
		PriceData[] rop = new PriceData[list.size()];
		for(i = 0; i < size; ++i) {
			try {
				rop[i] = new PriceData(list.get(i));
			} catch (Exception e) {
				rop[i] = null;
			}
		}
		return rop;
	}
	
	static Order[] arrayListToArrayOrder(ArrayList<OrderCom> list) {
		if(list == null || list.isEmpty())
			return new Order[0];
		int size = list.size();
		int i;
		Order[] rop = new Order[list.size()];
		for(i = 0; i < size; ++i) {
			try {
				rop[i] = new Order(list.get(i));
			} catch (Exception e) {
				rop[i] = null;
			}
		}
		return rop;
	}
	
	static String[] arrayListToArrayString(ArrayList<String> list) {
		if(list == null || list.isEmpty())
			return new String[0];
		int size = list.size();
		int i;
		String[] rop = new String[list.size()];
		for(i = 0; i < size; ++i) {
			rop[i] = list.get(i);
		}
		return rop;
	}
	
	static String[][] hashtableToMatrixString(Hashtable<String,ArrayList<?>> table) {
		
		if(table == null || table.isEmpty())
			return new String[0][0];

		Hashtable<Integer, String> fields = new Hashtable<Integer, String>();
		fields.put(0,"Date");
		fields.put(1,"Open");
		fields.put(2,"High");
		fields.put(3,"Low");
		fields.put(4,"Close");
		fields.put(5,"Volume");
		
		int size = table.get("Date").size();
		int i, j;
		String[][] rop = new String[size][6];
		ArrayList<?> list;
		
		for(i = 0; i < 6; ++i) {
			list = table.get(fields.get(i));
			for(j = 0; j < size; ++j) {
				rop[j][i] = "" + list.get(j);
			}
		}
		return rop;
	}
	
	static char[][] toMatlabChar(String s) {
		if(s == null)
			return null;
//		char[][] c = new char[1][];
//		c[0] = s.toCharArray();
//		return c;
		return new char[][]{s.toCharArray()};
	}

	static char[][] arrayListToArrayMatlabChar(ArrayList<String> list) {
		if(list == null || list.isEmpty())
			return new char[0][0];
		int size = list.size();
		int i;
		char[][] rop = new char[list.size()][];
		for(i = 0; i < size; ++i) {
			rop[i] = list.get(i).toCharArray();
		}
		return rop;
	}

}
