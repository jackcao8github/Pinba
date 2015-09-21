//返回角度
function GetSlideAngle(dx, dy) {
	return Math.atan2(dy, dx) * 180 / Math.PI;
}

// 根据起点和终点返回方各1�7�1�71：向上，2：向下，3：向左，4：向叄1�7�1�70：未滑动

function GetSlideDirection(startX, startY, endX, endY) {
	var dy = startY - endY;
	var dx = endX - startX;
	var result = 0;
	// 如果滑动距离太短
	if (Math.abs(dx) < 2 && Math.abs(dy) < 2) {
		return result;
	}

	var angle = GetSlideAngle(dx, dy);
	if (angle >= -45 && angle < 45) {
		result = 4;
	} else if (angle >= 45 && angle < 135) {
		result = 1;
	} else if (angle >= -135 && angle < -45) {
		result = 2;
	} else if ((angle >= 135 && angle <= 180)
			|| (angle >= -180 && angle < -135)) {
		result = 3;
	}
	return result;
}

// 滑动处理
var touchEvents = {
	touchstart : "touchstart",
	touchmove : "touchmove",
	touchend : "touchend",
	toucancel : "touchcancel",
	/**
	 * @desc:判断是否pc设备，若是pc，需要更改touch事件为鼠标事件，否则默认触摸事件
	 */
	initTouchEvents : function() {
		if (isPC()) {
			this.touchstart = "mousedown";
			this.touchmove = "mousemove";
			this.touchend = "mouseup";
		}
	}
};
var startX, startY;// 滑动起始的xy坐标
// 滑动开始时的响应事件
document.addEventListener(touchEvents.touchstart, function(ev) {
	console.log("touchstart");
	startX = ev.touches[0].pageX;
	startY = ev.touches[0].pageY;

	console.log("touchstart,startX=" + startX + "startY=" + startY);
}, false);

var endX, endY;// 滑动结束的xy坐标
var direction;// 根据起始结束坐标计算得到的滑动方各1�7�1�7
var distanceX;// 横向滑动距离
var distanceY;// 纵向滑动距离

function getDistanceAndDirection(endX,endY){
	direction = GetSlideDirection(startX, startY, endX, endY);
	if (endX > startX) {
		distanceX = endX - startX;
	} else {
		distanceX = startX - endX;
	}

	if (endY > startY) {
		distanceY = endY - startY;
	} else {
		distanceY = startY - endY;
	}
}
// 滑动结束事件发生时的处理
function swipeOver(ev) {
	endX = ev.changedTouches[0].pageX;
	endY = ev.changedTouches[0].pageY;
	getDistanceAndDirection(endX,endY);
	console.log("touchend,endX=" + endX + "endY=" + endY);
	if (distanceX > 100) {// 水平滑动距离
		// 滑动结束时的业务处理回调
		// myTouchEnd(ev);
		switch (direction) {
		case 3:
			console.log("向左");
			// 向左滑动时的回调处理
			if (window.swipeLeft != null)
				swipeLeft();
			break;
		case 4:
			console.log("向右");
			// 向右滑动时的回调处理
			if (window.swipeRight != null)
				swipeRight();
			break;
		default:
		}
	} else if (distanceY > 30) {// 垂直滑动距离
		switch (direction) {
		case 1:
			console.log("向上");
			// 向上滑动时的回调处理
			if (window.swipeUp != null)
				swipeUp();
			break;
		case 2:
			console.log("向下");
			
			// 向下滑动时的回调处理
			if (window.swipeDown != null)
				swipeDown();
			break;
		default:
		}
	}

}
document.addEventListener(touchEvents.touchend, swipeOver, false);
document.addEventListener(touchEvents.toucancel, swipeOver, false);

/*滑动事件响应方法*/
document.addEventListener(touchEvents.touchmove, function(ev) {
	endX = ev.changedTouches[0].pageX;
	endY = ev.changedTouches[0].pageY;
	console.log("touchmove,endX=" + endX + "endY=" + endY);
	
	getDistanceAndDirection(endX,endY);
	switch (direction) {
	case 1:
		console.log("向上");
		
		$('#pullUpFresh').show();
		$('#pullUpFreshText').html('上拉刷新');
		$('#pullUpFresh').css('height',distanceY);
		break;
	case 2:
		
		$('#pullDownFresh').show();
		$('#pullDownFreshText').html('下拉刷新');
		$('#pullDownFresh').css('height',distanceY);
		break;
	default:
	}
}, false);