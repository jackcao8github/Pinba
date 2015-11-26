package com.webapp.common.servlet;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

public class WebSocketMessageInboundPool {

	//保存A类设备和B类设备客户端http长连接的MAP容器
	private static final Map<Long,WebSocketMessageInbound> connections = new HashMap();

	public static WebSocketMessageInbound getConnection(long userId){
		return connections.get(userId);
	}
	//向连接池中添加连接
	public static void addMessageInbound(WebSocketMessageInbound inbound){
		//添加连接
		long userId = inbound.getUserId();
		System.out.println("新上线userId: " +userId );
		connections.put(userId,inbound);
	}

	//（服务端收到客户端关闭websocket连接事件，触发WebSocketInbound中的onClose方法），从连接池中删除连接设备客户端的连接实例
	public static void removeMessageInbound(long userId){
		//移除连接
		System.out.println("设备离线 ,userId: " + userId);
		connections.remove(userId);
	}


	//向某一客户端发送数据
	public static void sendMessageToSingleClient(long userId,JSONObject jsonMsg){
			WebSocketMessageInbound client = getConnection(userId);
			if (client!=null){
				client.sendMessage(jsonMsg);
			}
	}

}
