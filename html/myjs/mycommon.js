//校验不可空控件
/*function checkFormNullable(parentControlId){
	var checkMsg = '';
	$("#" + parentControlId + " input[name],#" + parentControlId + " select[name],#" + parentControlId + " textarea[name]").each(function(index,item){
		if ($(item).val()==''){
			var controlId=$(item).attr('id');
			var controlName = $('[for="'+controlId+'"').html();
			if (controlName!=null)
				controlName = controlName.replace(':','');
			
			if (checkMsg!=''){
				checkMsg = checkMsg + ',';
			}
			checkMsg = checkMsg+controlName;
		}
	});
	if (checkMsg!=''){
		alert(checkMsg+"不能为空!");
		return false;
	}
	return true;
}
*/
/*json对象转换成字符串*/
function json2Str(jsonData){
	var str = '{';
	var i=0;
	for ( var o in jsonData) {
		if (i!=0){
			str = str + ',';
		}
		str = str + o;
		str = str + ':"';
		str = str + jsonData[o];
		str = str + '"';
		i = i +1 ;
	}
	str = str + '}';
	return str;
}
/*取随机数*/
function GetRandomNum(Min, Max) {
	var Range = Max - Min;
	var Rand = Math.random();
	return (Min + Math.round(Rand * Range));
}
/*获取contextpath*/
function getContextPath() {
	var parentSrc = window.location.pathname;
	parentSrc = parentSrc.substring(1, parentSrc.length);
	var contextPath = parentSrc.substring(0, parentSrc.indexOf('/'));
	return '/' + contextPath;
}
function ajaxReq(url, data, successCallback, errorCallback, beforeSendCallback,
		completeCallback) {

	var params = '';
	for ( var o in data) {
		if (params != '') {
			params = params + '&'
		}
		params = params + o + '=' + data[o];
	}
	$.ajax({
		type : "POST",
		contentType : "application/json",
		dataType : "json",
		url : getContextPath() + '/' + url + '?' + params,
		beforeSend : beforeSendCallback,
		complete : completeCallback,
		success : successCallback,
		error : errorCallback,
		async : true
	});
}
function ajaxSyncReq(url, data, successCallback) {

	var params = '';
	for ( var o in data) {
		if (params != '') {
			params = params + '&'
		}
		params = params + o + '=' + data[o];
	}
	$.ajax({
		type : "POST",
		contentType : "application/json",
		dataType : "json",
		url : getContextPath() + '/' + url + '?' + params,
		async : false,
		success : successCallback
	});
}
/* 切换子页面 */

/* 页面列表 */
var idHtmlMap = {
	"myWork" : "my/myWork.html",
	"myOrder" : "my/myOrder.html",
	"myMoneyBag" : "my/myMoneyBag.html",
	"myPublish" : "my/myPublish.html",
	"myFocus" : "my/myFocus.html",
	"mySub" : "my/mySub.html",
	"myPromotion" : "my/myPromotion.html",
	"verifyOfferVoucher" : "my/verifyOfferVoucher.html",
	"myLookHis" : "my/myLookHis.html",
	"serviceCenter" : "my/serviceCenter.html",
	"tobeBusiness" : "my/tobeBusiness.html",
	"tobeVIPMemeber" : "my/tobeVIPMemeber.html",
	"login" : "my/login.html",
	"userInfo" : "my/userInfo.html",
	"search" : "home/search.html",
	"foodMain" : "home/foodMain.html",
	"workMain" : "home/workMain.html",
	"workDetail" : "home/workDetail.html",
	"entertainmentMain" : "home/entertainmentMain.html",
	"foodDetail" : "home/foodDetail.html",
	"changeCity" : "home/changeCity.html",
	"message" : "message/message.html",
	"friendsMsg" : "message/friendsMsg.html",
	"servNoticeMsg" : "message/servNoticeMsg.html",
	"servRemindMsg" : "message/servRemindMsg.html",
	"subscribeMsg" : "message/subscribeMsg.html",
	"systemMsg" : "message/systemMsg.html",
	"searchResult" : "home/searchResult.html",
	"register" : "my/register.html",
	"changePass" : "my/changePass.html",
	"work" : "publish/work.html",
	"reqwork" : "publish/reqwork.html",
	"userContract" : "my/userContract.html",
	"resumeDetail" : "my/resumeDetail.html",
	"editResumeDetail" : "my/editResumeDetail.html",
	"reqwork" : "publish/reqwork.html",
	"resume" : "my/resume.html"
};

/* 显示子页面，隐藏原页面 */
function changeSubPage(newpageid, oldpageid, params) {
	if (parent.$('#indexPage').length > 0) {
		parent.$('#footNavBar').css('display', 'none');
		// 刷新parent.displayFrameId的高度
		var newh = parent.window.innerHeight
				|| parent.document.documentElement.clientHeight
				|| parent.document.body.clientHeight;
		parent.$('#' + parent.displayFrameId).css('height', newh + 'px');

	}

	var clientWindowHeight = window.innerHeight
			|| document.documentElement.clientHeight
			|| document.body.clientHeight;

	var clientWindowWidth = window.innerWidth
			|| document.documentElement.clientWidth
			|| document.body.clientWidth;
	// 取上级页面的srcPath,而window.location.pathname得到的是/PinBa/home/wode.html,而我们只需要home/wode.html,所以去掉/PinBa/
	var parentSrc = window.location.pathname;
	parentSrc = parentSrc.substring(1, parentSrc.length);
	parentSrc = parentSrc.substring(parentSrc.indexOf('/') + 1,
			parentSrc.length);

	// 上级页面路径
	var pSrcPath = parentSrc.substring(0, parentSrc.lastIndexOf('/'));

	// 子页面的src
	var newsrc = idHtmlMap[newpageid];
	// 取子页面路径
	var srcPath = newsrc.substring(0, newsrc.lastIndexOf('/'));

	if (pSrcPath == srcPath) {
		// 是同一目录下的页面
		newsrc = newsrc.substring(newsrc.lastIndexOf('/') + 1, newsrc.length);
	} else {
		// 不是同一目录下的页面
		newsrc = '../' + newsrc;
	}
	// 如果没有otherpage iframe,则在body里创建一个iframe
	if ($('#otherPage').length == 0) {
		$(
				'<iframe src="#" style="border:0;display: none;width:'
						+ clientWindowWidth + 'px;height:' + clientWindowHeight
						+ 'px;" id="otherPage"/>').appendTo('body');
		if (params != null) {
			newsrc = newsrc + '?' + params;
		}
		$('#otherPage').attr('src', newsrc);
	} else {
		var oldsrc = $('#otherPage').attr('src');
		if (oldsrc != newsrc) {
			if (params != null) {
				newsrc = newsrc + '?' + params;
			}
			$('#otherPage').remove();
			$(
					'<iframe src="#" style="border:0;display: none;width:'
							+ clientWindowWidth + 'px;height:'
							+ clientWindowHeight + 'px;" id="otherPage"/>')
					.appendTo('body');
			$('#otherPage').attr('src', newsrc);
		}
	}

	$('#otherPage').css('display', 'block');
	$('#' + oldpageid).css('display', 'none');
}

/* 关闭子页面 */
function closeSubPage(destpageid) {
	// 显示上级页面上的footer导航栏，并重新设置当前iframe的高度
	if (parent.parent.$('#indexPage').length > 0) {
		parent.parent.$('#footNavBar').css('display', 'block');

		var newh = parent.window.innerHeight
				|| parent.document.documentElement.clientHeight
				|| parent.document.body.clientHeight;
		var navbarh = parent.parent.$('#footNavBar').height();

		parent.parent.$('#' + parent.parent.displayFrameId).css('height',
				newh - navbarh + 'px');
	}
	parent.$('#otherPage').css('display', 'none');
	if (destpageid == null) {
		destpageid = parent.$('[data-role="page"]').attr('id');
	}
	parent.$('#' + destpageid).css('display', 'block');
	// 调用上级页面中的页面初始化方法
	if (parent.pageInit) {
		parent.pageInit();
	}
}

/*
 * 显示提示对话框
 */
function showTipDialog(tipMsg) {
	// 删除已有提示框
	if ($('#tipMsgDialog').length > 0) {
		$('#tipMsgDialog').remove();
	}
	// 创建新提示枉
	$(
			'<div data-role="popup" id="tipMsgDialog" class="ui-content"><a href="#" data-rel="back" class="ui-btn ui-corner-all ui-shadow ui-btn-a ui-icon-delete ui-btn-icon-notext ui-btn-right">Close</a><p style="color: red;">'
					+ tipMsg + '</p></div>').appendTo('body');
	$('#tipMsgDialog').popup();
	$('#tipMsgDialog').popup("open");
	// 1秒后关闭
	setTimeout(function() {
		$('#tipMsgDialog').popup("close");
	}, 2000);
}

/*
 * 显示确认对话框 okFun为yes的回调函数
 */
function showConfirmDialog(confirmMsg, okFun) {
	// 删除已有提示框
	if ($('#confirmMsgDialog').length > 0) {
		$('#confirmMsgDialog').remove();
	}
	// 创建新提示枉
	var html = '<div data-role="popup" id="confirmMsgDialog" data-confirmed="no" data-transition="pop" data-dismissible="false">\
		<div role="main" class="ui-content"><h3 class="ui-title">'
			+ confirmMsg
			+ '</h3>'
			+ '<a href="#" class="ui-btn ui-corner-all ui-shadow ui-btn-inline ui-btn-b optionConfirm" data-rel="back" onclick="'
			+ okFun
			+ '()">Yes</a>'
			+ '<a href="#" class="ui-btn ui-corner-all ui-shadow ui-btn-inline ui-btn-b optionCancel" data-rel="back" data-transition="flow">No</a>\
		</div>\
		</div>';
	$(html).appendTo('body');
	var popObj = $('#confirmMsgDialog');
	popObj.popup();
	$('#confirmMsgDialog').popup("open");
}
