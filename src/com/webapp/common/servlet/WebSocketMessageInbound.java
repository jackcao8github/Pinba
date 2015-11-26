package com.webapp.common.servlet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;
import org.springframework.util.StringUtils;

import com.webapp.common.util.CachedUserPostitionUtil;
import com.webapp.common.util.MsgTypeConsts;
import com.webapp.common.util.ServiceFactory;
import com.webapp.user.dao.UserManagerDAO;

import net.sf.json.JSONObject;

public class WebSocketMessageInbound extends MessageInbound {
	private final HttpServletRequest request;
	private long userId;//当前连接的用户ID

	//get/set method
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}


	public HttpServletRequest getRequest() {
		return request;
	}

	public WebSocketMessageInbound(HttpServletRequest request) {
		this.request = request;
		String userId = request.getParameter("userId");
		this.setUserId(Long.valueOf(userId));
	}

	
	//建立连接的触发的事件
	@Override
	protected void onOpen(WsOutbound outbound) {
    	//向客户端发送回调以建立握手
//		WebSocketMessageInboundPool.sendMessageToSingleClient(this,new BaseEvent(EventConst.EVENT_A_LOGIN));

	}

	@Override
	protected void onClose(int status) {
		// 触发关闭事件，在连接池中移除连接
		WebSocketMessageInboundPool.removeMessageInbound(this.userId);
	}

	@Override
	protected void onBinaryMessage(ByteBuffer message) throws IOException {
		throw new UnsupportedOperationException("Binary message not supported.");
	}

	//客户端发送消息到服务器时触发事件 addFriend,delFriend,newMsg,getMsg,logUserPosition
	@Override
	protected void onTextMessage(CharBuffer message) throws IOException {
		//
		UserManagerDAO userDao = (UserManagerDAO) ServiceFactory.getDAO("userDAO");
		JSONObject jsonMsg = JSONObject.fromObject(message.toString());
		String msgType = jsonMsg.getString("msgType");
		if (StringUtils.isEmpty(msgType)){
			throw new UnsupportedOperationException("无效的msgType");
		}
		if (msgType.equals("sendMsg")){//发消息
			long userId = jsonMsg.getLong("userId");
			long friendId = jsonMsg.getLong("friendId");
			String msgContent = jsonMsg.getString("msgContent");
			
			//保存数据库
			try {
				userDao.sendMsg(userId, friendId, msgContent,MsgTypeConsts.FRIENDMSG);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//向friendId用户发送通知
			WebSocketMessageInbound friendConnect = WebSocketMessageInboundPool.getConnection(friendId);
			if (friendConnect!=null){
				JSONObject retJsonMsg = new JSONObject();
				retJsonMsg.put("msgType", "newMsg");
				retJsonMsg.put("friendId", userId);
				retJsonMsg.put("msgContent", msgContent);
				friendConnect.sendMessage(retJsonMsg);
			}
		}
	}
	
	//向客户端发送数据，使用Websocket WsOutbound输出流向客户端退送数据，数据格式统一为Json
	//jsonMsg中包含操作成功通知，新消息通知等
	public void sendMessage(JSONObject jsonMsg)
	{
		String eventStr = jsonMsg.toString();
		try {
			//向连接设备发送数据
			System.out.println("send message to device : " + eventStr);
			this.getWsOutbound().writeTextMessage(CharBuffer.wrap(eventStr));			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
