package com.webapp.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CachedUserPostitionUtil {
	// 内存存储区，用于存放缓存数据,key=城市名称,value=处在此城市的用户id
	private static Map<String,Set> cityUsers = new HashMap();
	//
	private static Map<Long,JSONObject> userPosMap = new HashMap();
	// 向缓存中增加数据
	public static void addCacheData(long userId,String cityName, String latitude,String longitude) {
		if (userPosMap.containsKey(userId)){//如果已有记录，且城市不一样，则先删除原城市里的用户id
			JSONObject oldPosData = userPosMap.get(userId);
			String oldCity = oldPosData.getString("cityName");
			if (!oldCity.equals(cityName)){
				Set userIds = cityUsers.get(cityName);
				if (userIds!=null){
					userIds.remove(userId);
				}
			}
		}
		//增加新的位置信息
		JSONObject posdata = new JSONObject();
		posdata.put("latitude", latitude);
		posdata.put("longitude", longitude);
		posdata.put("cityName", cityName);
		posdata.put("loginTime", System.currentTimeMillis());//登陆时间
		userPosMap.put(userId, posdata);
		
		//将用户id加入到城市用户id列表中
		Set userIds = cityUsers.get(cityName);
		if (userIds==null){
			userIds = new HashSet();
			userIds.add(userId);
			cityUsers.put(cityName, userIds);
		}else{
			userIds.add(userId);
		}
	}

	public static class Comp{
		public long userId;
		public double distance;
	}
	// 从缓存中取得距离userId最近的其它用户
	public static List getNearByUsers(long userId,String cityName) {
		Set userIds = cityUsers.get(cityName);
		List<JSONObject> userList = new ArrayList();
		if (userIds!=null){
			List<Comp> arr = new ArrayList();
			JSONObject cuser = userPosMap.get(userId);
			Iterator it = userIds.iterator();
			while (it.hasNext()){
				long nuserId = (Long)it.next();
				if (userId!=nuserId){
					JSONObject nuser = userPosMap.get(nuserId);
					
					double distance = emulateDistance(cuser,nuser);
					Comp com = new Comp();
					com.distance = distance;
					com.userId = nuserId;
					arr.add(com);
				}
			}
			
			if (arr.size()>0){
				//按照distance从小到大进行排序
				Comp[] comArr =  arr.toArray(new Comp[]{});
				bubbleSort(comArr);
				
				for (int i=0;i<comArr.length;i++){
					JSONObject obj = new JSONObject();
					obj.put("userId", comArr[i].userId);
					obj.put("distance", comArr[i].distance);
					userList.add(obj);
				}
			}
		}
		return userList;
	}
	//冒泡法排序 
    public static void bubbleSort(Comp[] numbers) {   
    	Comp temp; // 记录临时中间值   
        int size = numbers.length; // 数组大小   
        for (int i = 0; i < size - 1; i++) {   
            for (int j = i + 1; j < size; j++) {   
                if (numbers[i].distance > numbers[j].distance) { // 交换两数的位置   
                    temp = numbers[i];   
                    numbers[i] = numbers[j];   
                    numbers[j] = temp;   
                }   
            }   
        }   
    }   
	private static double emulateDistance(JSONObject userA,JSONObject userB){
		
		return getDistance(userA.getDouble("longitude"),userA.getDouble("latitude"),userB.getDouble("longitude"),userB.getDouble("latitude"));
	}
	/**
     * 计算两经纬度点之间的距离（单位：米）
     * @param lng1  经度
     * @param lat1  纬度
     * @param lng2
     * @param lat2
     * @return
     */
    public static double getDistance(double lng1,double lat1,double lng2,double lat2){
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);
        double a = radLat1 - radLat2;
        double b = Math.toRadians(lng1) - Math.toRadians(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1)
                * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * 6378137.0;// 取WGS84标准参考椭球中的地球长半径(单位:m)
        s = Math.round(s * 10000) / 10000;
        return s;
    }
    
    public static double getDistance(long userIdA,long userIdB){
    	if (!userPosMap.containsKey(userIdA)){
    		return 0;
    	}
    	if (!userPosMap.containsKey(userIdB)){
    		return 0;
    	}
    	JSONObject a = userPosMap.get(userIdA);
    	JSONObject b = userPosMap.get(userIdB);
    	
    	return emulateDistance(a,b);
    }
	public static void main(String[] args) throws InterruptedException {
		CachedUserPostitionUtil.addCacheData(1, "南京", "118.20000012", "32.9001891");
		CachedUserPostitionUtil.addCacheData(2, "南京", "118.10000012", "32.8001891");
		CachedUserPostitionUtil.addCacheData(3, "南京", "117.10000012", "31.8001891");
		CachedUserPostitionUtil.getNearByUsers(3,  "南京");
	}
}
