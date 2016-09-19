package com.mattrader.matlab;

import com.mattrader.common.BookCom;

public class Book {
	
	private final BookCom b;
	
	Book(BookCom b) throws Exception {
		this.b = b;
		if(b == null)
			throw new Exception();
	}

	/**
	 * @return a matrix 5x2 containing in the first column bid prices and in the second column volumes
	 */
	public double[][] bid() {
		return b.bid();
	}

	/**
	 * @return a matrix 5x2 containing in the first column ask prices and in the second column volumes
	 */
	public double[][] ask() {
		return b.ask();
	}

	/**
	 * @return price and volume of last exchange
	 */
	public double[][] price() {
		return b.price();
	}

}
