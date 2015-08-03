/*切换子页面*/

/*页面列表*/
var idHtmlMap = {
	"myWork" : "myWork.html",
	"myOrder" : "myOrder.html",
	"myMoneyBag" : "myMoneyBag.html",
	"myPublish" : "myPublish.html",
	"myFocus" : "myFocus.html",
	"mySub" : "mySub.html",
	"myPromotion" : "myPromotion.html",
	"verifyOfferVoucher" : "verifyOfferVoucher.html",
	"myLookHis" : "myLookHis.html",
	"serviceCenter" : "serviceCenter.html",
	"tobeBusiness" : "tobeBusiness.html",
	"tobeVIPMemeber" : "tobeVIPMemeber.html",
	"login" : "login.html",
	"userInfo" : "userInfo.html",
	"search" : "search.html",
	"foodMain" : "foodMain.html",
	"workMain" : "workMain.html",
	"entertainmentMain" : "entertainmentMain.html",
	"foodDetail" : "foodDetail.html",
	"changeCity" : "changeCity.html",
	"message" : "message.html",
	"friendsMsg":"friendsMsg.html",
	"servNoticeMsg":"servNoticeMsg.html",
	"servRemindMsg":"servRemindMsg.html",
	"subscribeMsg":"subscribeMsg.html",
	"systemMsg":"systemMsg.html"
};

/* 显示子页面，隐藏原页面 */
function changeSubPage(newpageid, oldpageid,params) {
	if (parent.$('#indexPage').length>0){
		parent.$('#footNavBar').css('display','none');
		//刷新parent.displayFrameId的高度
		var newh =parent.window.innerHeight
		|| parent.document.documentElement.clientHeight
		|| parent.document.body.clientHeight;
		parent.$('#'+parent.displayFrameId).css('height',newh+'px');
		
	}
	//如果没有otherpage iframe,则在body里创建一个iframe
	if ($('#otherPage').length==0){
		$('<iframe src="#" style="display: none;width:100%;height:100%;" id="otherPage"/>').appendTo('body');
		var newsrc = idHtmlMap[newpageid];
		if (params!=null){
			newsrc = newsrc + '?' + params;
		}
		$('#otherPage').attr('src', newsrc);
	}else{
		var oldsrc = $('#otherPage').attr('src');	
		var newsrc = idHtmlMap[newpageid];
		if (oldsrc != newsrc) {
			if (params!=null){
				newsrc = newsrc + '?' + params;
			}
			$('#otherPage').remove();
			$('<iframe src="#" style="display: none;width:100%;height:100%;" id="otherPage"/>').appendTo('body');
			$('#otherPage').attr('src', newsrc);
		}
	}
	
	$('#otherPage').css('display', 'block');
	$('#' + oldpageid).css('display', 'none');
}

/* 关闭子页面 */
function closeSubPage(destpageid) {
	//显示主页面上的导航栏，并重新设置当前iframe的高度
	if (parent.parent.$('#indexPage').length>0){
		parent.parent.$('#footNavBar').css('display','block');
		
		var newh =parent.window.innerHeight
		|| parent.document.documentElement.clientHeight
		|| parent.document.body.clientHeight;
		var navbarh = parent.parent.$('#footNavBar').height();
		
		parent.parent.$('#'+parent.parent.displayFrameId).css('height',newh-navbarh +'px');
	}
	parent.$('#otherPage').css('display', 'none');
	if (destpageid == null) {
		destpageid = parent.$('[data-role="page"]').attr('id');
	}
	parent.$('#' + destpageid).css('display', 'block');
}

/*$(document).bind('swipeleft',function(){
	alert('swipeleft');
})
$(document).bind('swiperight',function(){
	alert('swiperight');
})*/