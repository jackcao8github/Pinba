package com.xx.common;

import java.sql.Date;

public class DataTypeTrans {
	public static Long transToLong(Object value){
		return (Long)value;
	}
	
	public static String transToString(Object value){
		return (String)value;
	}
	
	public static Date transToDate(Object value){
		return (Date)value;
	}
}
