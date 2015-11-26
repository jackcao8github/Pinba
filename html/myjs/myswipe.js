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
/*
 * initTouchEvents : function() { if (isPC()) { this.touchstart = "mousedown";
 * this.touchmove = "mousemove"; this.touchend = "mouseup"; } }
 */
};

function getDistanceAndDirection(endX, endY) {
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

var startX, startY;// 滑动起始的xy坐标
var endX, endY;// 滑动结束的xy坐标
var direction;// 根据起始结束坐标计算得到的滑动方各1�7�1�7
var distanceX;// 横向滑动距离
var distanceY;// 纵向滑动距离

// 滑动开始时的响应事件
/* document.addEventListener(touchEvents.touchstart, function(ev) { */
function touchStart(ev) {
	// ev.preventDefault();//阻止默认行为：页面滚动
	startX = ev.originalEvent.touches[0].pageX;
	startY = ev.originalEvent.touches[0].pageY;
}

// 滑动结束事件发生时的处理
function touchend(ev) {
	endX = ev.originalEvent.changedTouches[0].pageX;
	endY = ev.originalEvent.changedTouches[0].pageY;
	getDistanceAndDirection(endX, endY);

	if (distanceX > 30) {// 水平滑动距离
		// 滑动结束时的业务处理回调
		// myTouchEnd(ev);
		switch (direction) {
		case 3:
			// 向左滑动时的回调处理
			/*if (window.swipeLeft != null)
				swipeLeft();*/
			break;
		case 4:
			// 向右滑动时的回调处理
			/*if (window.swipeRight != null)
				swipeRight();*/
			break;
		default:
		}
	} else if (distanceY > 0) {// 垂直滑动距离
		if (ev.currentTarget.nodeName != 'BODY') {//阻止body上的上下滑动
			switch (direction) {
			case 1:
				// 向上滑动时的回调处理
				if (window.swipeUp != null){
					$(ev.currentTarget).find('.pullUpFreshText').html('加载中...');
					swipeUp();
					$(ev.currentTarget).find('.pullUpFreshText').html('加载完成');
					$(ev.currentTarget).find('.pullUpFresh').slideUp('slow');
				}
					
				break;
			case 2:
				// 向下滑动时的回调处理
				if (window.swipeDown != null){
					$(ev.currentTarget).find('.pullDownFreshText').html('加载中...');
					swipeDown();
					$(ev.currentTarget).find('.pullDownFreshText').html('加载完成');
					$(ev.currentTarget).find('.pullDownFresh').slideUp('slow');
				}
					
				break;
			default:
			}
		}
	}
}
/*
 * document.addEventListener(touchEvents.touchend, touchend, false);
 * document.addEventListener(touchEvents.toucancel, touchend, false);
 */

/* 滑动事件响应方法 */
function touchmove(ev) {
	/* document.addEventListener(touchEvents.touchmove, function(ev) { */

	// ev.stopPropagation();
	if (ev.currentTarget.nodeName != 'BODY') {//阻止body上的上下滑动

		endX = ev.originalEvent.changedTouches[0].pageX;
		endY = ev.originalEvent.changedTouches[0].pageY;

		getDistanceAndDirection(endX, endY);

		switch (direction) {
		case 1:// 向上滑动
			/*alert($(document).scrollTop());
			alert($(document).height());
			alert( $(window).height());*/
			/*if ($(document).scrollTop() == $(document).height()
					- $(window).height()) {// 滚动条到达底部
				ev.preventDefault();// 阻止默认行为：页面滚动
				$(ev.currentTarget).find('.pullUpFresh').show();
				$(ev.currentTarget).find('.pullUpFreshText').show();
				$(ev.currentTarget).find('.pullUpFreshText').html('上拉刷新');
				$(ev.currentTarget).find('.pullUpFresh').css('height', distanceY);
				
				//让滚动条滑动到底部
				$(document).scrollTop($(document).height());
			}*/
			
			break;
		case 2:// 向下滑动
			if ($(document).scrollTop() == 0) {// 滚动条到达顶部
				ev.preventDefault();// 阻止默认行为：页面滚动
				$(ev.currentTarget).find('.pullNoData').hide();//隐藏无数据提示框
				$(ev.currentTarget).find('.pullDownFresh').show();
				$(ev.currentTarget).find('.pullDownFreshText').show();
				$(ev.currentTarget).find('.pullDownFreshText').html('下拉刷新');
				$(ev.currentTarget).find('.pullDownFresh').css('height', distanceY);
			}
			
			break;
		default:
		}
	}
}

function bindSwipeEvent() {
	$('.swipe').bind(touchEvents.touchstart, touchStart).bind(
			touchEvents.touchmove, touchmove).bind(touchEvents.touchend,
			touchend);
	$('body').bind(touchEvents.touchstart, touchStart).bind(
			touchEvents.touchmove, touchmove).bind(touchEvents.touchend,
			touchend);
}