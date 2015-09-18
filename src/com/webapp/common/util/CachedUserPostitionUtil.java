package com.webapp.common.util;

import net.sf.json.JSONObject;

public class CachedUserPostitionUtil {
	// 内存存储区，用于存放缓存数据
	private static JSONObject memBuf = new JSONObject();

	// 向缓存中增加数据
	public static void addCacheData(long userId,String cityName, String latitude,String longitude) {
		JSONObject data = new JSONObject();
		data.put("latitude", latitude);
		data.put("longitude", longitude);
		data.put("cityName", cityName);
		data.put("time", System.currentTimeMillis());//登陆时间
		
		//查找同城市的，距离userId最近的其它500个用户
		JSONObject nearByUsers = new JSONObject();
		
		data.put("nearByUsers", nearByUsers);
		
		memBuf.put(userId, data);
	}

	// 从缓存中取得距离userId最近的其它用户
	public static JSONObject getNearByUsers(long userId) {
		JSONObject nearByUsers = new JSONObject();
		
		
		return nearByUsers;
	}
	
	private long emulateDistance(long userIdA,long userIdB){
		return userIdB;
	}
	
	public static void main(String[] args) throws InterruptedException {
	}
}
