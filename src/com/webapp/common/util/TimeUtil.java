package com.webapp.common.util;

import java.text.DateFormat;
import java.util.Locale;

public class TimeUtil {
	public static String getLocalTimeString(){
		return DateFormat.getDateTimeInstance(2, 2, Locale.CHINESE).format(new java.util.Date());
	}
}
