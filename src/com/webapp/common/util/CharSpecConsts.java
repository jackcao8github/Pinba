package com.webapp.common.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class CharSpecConsts {
	public final static long CELLPHONE = 1L;//手机
	public final static long SEX = 2L;//性别
	public final static long EMAIL = 3L;//EMAIL
	public final static long ADDRESS = 4L;//地址
	public final static long COORDS = 5L;//坐标
	public final static long URL = 6L;//网址
	public final static long FIXPHONE = 7L;//固话
	public final static long COMPANY_SIZE = 8L;//公司规模
	public final static long COMPANY_INDUSTRY = 9L;//公司行业
	public final static long REAL_NAME = 22L;//真实姓名
	public final static long BATCH_NO = 56L;//工资发放批次号
	public final static long PAY_MONEY = 57L;//工资发放金额
	public final static long PAY_SEQ = 58L;//工资发放序号
	
	private static Map<Long,String> charMap= new HashMap();
	static{
		charMap.put(CELLPHONE, "cellphone");
		charMap.put(SEX, "sex");
		charMap.put(EMAIL, "email");
		charMap.put(ADDRESS, "address");
		charMap.put(COORDS, "coords");
		charMap.put(URL, "url");
		charMap.put(COMPANY_SIZE, "companySize");
		charMap.put(COMPANY_INDUSTRY, "companyIndustry");
	}
	
	public static Map getCharMap(){
		return charMap;
	}
	
	public static String getCode(Long charId) throws Exception{
		if (charMap.containsKey(charId)){
			return charMap.get(charId);
		}
		return "";
	}
	
	public static Long getCharId(String code) throws Exception{
		if (charMap.containsValue(code)){
			Iterator it = charMap.entrySet().iterator();
			while(it.hasNext()){
				Entry ent = (Entry) it.next();
				
				if (code.equals(ent.getValue())){
					return (Long)ent.getKey();
				}
			}
		}
		return 0L;
	}
}
