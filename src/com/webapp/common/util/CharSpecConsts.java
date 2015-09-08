package com.webapp.common.util;

import java.util.HashMap;
import java.util.Map;

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
	
	public static String getCode(Long charId) throws Exception{
		if (charMap.containsKey(charId)){
			return charMap.get(charId);
		}else{
			throw new Exception("charid="+charId+"未定义");
		}
	}
}
