package com.webapp.common.util;

import net.sf.json.JSONObject;

public class CachedMsgVerifyCodeUtil {
	// 内存存储区，用于存放缓存数据
	private static JSONObject memBuf = new JSONObject();
	// 内存块的最后一次清理时间
	private static long lastCleanTime = System.currentTimeMillis();

	private static void cleanMemBuf(long nowTime) {
		Object[] keyArr =  memBuf.keySet().toArray();
		for(int i=0;i<keyArr.length;i++){
			String key = (String) keyArr[i];
			JSONObject data = (JSONObject) memBuf.getJSONObject(key);
			long time = data.getLong("time");
			if (nowTime-time>5000){
				memBuf.remove(key);
			}
		}
	}

	// 向缓存中增加验证码
	public static void addCachedData(String number, String verifyCode) {
		// 清除时间到期的数据
		long nowTime = System.currentTimeMillis();
		if (nowTime - 5000 >= lastCleanTime) {
			lastCleanTime = nowTime;
			cleanMemBuf(nowTime);
		}
		JSONObject data = new JSONObject();
		data.put("verifyCode", verifyCode);
		data.put("time", System.currentTimeMillis());
		memBuf.put(number, data);
	}

	// 从缓存中取得验证码
	public static String getCachedData(String number) {
		// 清除时间到期的数据
		long nowTime = System.currentTimeMillis();
		if (nowTime - 5000 >= lastCleanTime) {
			lastCleanTime = nowTime;
			cleanMemBuf(nowTime);
		}
		if (memBuf.containsKey(number)) {
			JSONObject data = memBuf.getJSONObject(number);
			String verifyCode = data.getString("verifyCode");
			return verifyCode;
		}
		return null;
	}
	
	public static void main(String[] args) throws InterruptedException {
		CachedMsgVerifyCodeUtil.addCachedData("13914736924", "1113");
		CachedMsgVerifyCodeUtil.addCachedData("13914736925", "1113");
		CachedMsgVerifyCodeUtil.addCachedData("13914736926", "1113");
		CachedMsgVerifyCodeUtil.addCachedData("13914736927", "1113");
		Thread.sleep(10000);
		
		CachedMsgVerifyCodeUtil.getCachedData("13914736924");
	}
}
