package com.webapp.common.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public final class ExceptionUtil {
	public static String getMessage(Exception e){
		if (e.getMessage()==null){
	    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
			e.printStackTrace(new PrintStream(baos));
			String exception = baos.toString();
			return exception;
	    }else{
	    	return e.getMessage();
	    }
	}
}
