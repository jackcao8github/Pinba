package com.webapp.common.bean;

import java.sql.Date;
import java.sql.Timestamp;

public class DataTypeTrans {
	public static Long transToLong(Object value){
		return (Long)value;
	}
	
	public static String transToString(Object value){
		if (value==null){
			return "";
		}
		if (value instanceof String){
			return String.valueOf(value);
		}
		if (value instanceof Timestamp)
			return value.toString();
		return value.toString();
	}
	
	public static Date transToDate(Object value){
		return (Date)value;
	}
}
