var websocket=window.WebSocket || window.MozWebSocket; 
var isConnected = false;
//处理打开webSocket
function doOpen(){
     isConnected = true;
}

//处理关闭webSocket
function doClose(){
	//连接断开
	isConnected = false;
}

//处理webSocket Error
function doError() {
	alert("连接异常!");
	isConnected = false;
}

//处理websockett服务端返回(注意后台返回的message为json字符串)
function doMessage(message){
	var event = $.parseJSON(message.data);
	console.log(event);
	if (event.msgType=='newMsgCount'){//新消息数量通知
		if (window.showNewMsgCount != null){
			showNewMsgCount(event.msgCount);
		}
	}else if (event.msgType=='newMsg'){//新消息到达
		if (window.showNewMsg != null){
			showNewMsg(event.msgContent);
		}
	}
}

//处理发送消息(注意message是javascript Obj对象)
function doSend(message) {
	if (websocket != null||isConnected==false) {
		websocket.send(JSON.stringify(message));
	} else {
		console.log("您已经掉线，无法与服务器通信!");
	}
}

//初始话WebSocket
function initWebSocket() {
	var cachedUserInfo = str2Json(localStorage.userInfo);
	if (cachedUserInfo==null){
		console.log('未登陆,无法创建websocket');
	}else{
		if (window.WebSocket) {
			var wcUrl = 'ws://'+location.host+getContextPath()+'/websocket?userId=';
			wcUrl += cachedUserInfo.userId;
			
			websocket = new WebSocket(encodeURI(wcUrl));
			websocket.onopen = doOpen;
			websocket.onerror = doError;
			websocket.onclose = doClose;
			websocket.onmessage = doMessage;
		}
		else{
			alert("您的设备不支持webSocket!");
		}
	}
};

