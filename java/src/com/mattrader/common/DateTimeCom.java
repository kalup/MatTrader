package com.mattrader.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

class DateTimeCom implements Comparable<DateTimeCom> {

	private GregorianCalendar calDate;

	private DateFormat parserDF;

	DateTimeCom(String timeS) throws ParseException {
		this(timeS, new SimpleDateFormat("yyyyMMddHHmmss"));
	}

	DateTimeCom(String timeS, DateFormat parserDF) throws ParseException {
		this.parserDF = parserDF;
		calDate = new GregorianCalendar();
		calDate.setTime(parserDF.parse(timeS));
	}
	
	public void set(GregorianCalendar cal) {
		calDate = cal;
	}
	
	public void setNow() {
		calDate = new GregorianCalendar();
	}
	
	public boolean isFuture() {
		return calDate.after(new GregorianCalendar());
	}
	
	public GregorianCalendar calendar() {
		return calDate;
	}

	@Override
	public String toString() {
		return parserDF.format(calDate.getTime());
	}

	@Override
	public int compareTo(DateTimeCom arg0) {
		return calDate.compareTo(arg0.calendar());
	}
}
