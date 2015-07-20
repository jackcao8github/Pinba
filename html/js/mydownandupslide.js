//返回角度
function GetSlideAngle(dx, dy) {
	return Math.atan2(dy, dx) * 180 / Math.PI;
}

// 根据起点和终点返回方向 1：向上，2：向下，3：向左，4：向右,0：未滑动

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
var startX, startY;

document.addEventListener(touchEvents.touchstart, function(ev) {
	console.log("touchstart");
	startX = ev.touches[0].pageX;
	startY = ev.touches[0].pageY;
	
	console.log("touchstart,startX="+startX+"startY="+startY);
}, false);

var timerId;
document.addEventListener(touchEvents.touchmove, function(ev) {
	window.clearInterval(timerId);
	
	timerId = window.setTimeout(myTouchEnd, 200);  
}, false);