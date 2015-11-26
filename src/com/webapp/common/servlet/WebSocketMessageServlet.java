package com.webapp.common.servlet;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.StreamInbound;

//如果要接收浏览器的ws://协议的请求就必须实现WebSocketServlet这个类
public class WebSocketMessageServlet extends org.apache.catalina.websocket.WebSocketServlet {

	private static final long serialVersionUID = 1L;
    @Override
    protected StreamInbound createWebSocketInbound(String subProtocol,HttpServletRequest request) {
    	WebSocketMessageInbound newClientConn =  new WebSocketMessageInbound(request);
        WebSocketMessageInboundPool.addMessageInbound(newClientConn);
        return newClientConn;
    }
}
