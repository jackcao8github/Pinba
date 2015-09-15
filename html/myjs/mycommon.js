document.write('<script type="text/javascript" src="../js/json2.js"></script>');// 引入json库
document.write('<script type="text/javascript" src="../myjs/charspecvalue.js"></script>');// 引入json库

//取当前登陆用户信息
function getLoginUserInfo(){
	var cachedUserInfo = str2Json(localStorage.userInfo);
	if (cachedUserInfo != null) {
		return cachedUserInfo;
	}else{
		//转到登陆页面
		changeSubPage('login');
	}
}


// 取url带入的所有页面参数
function getRequest() {
	var url = location.search; // 获取url中"?"符后的字串
	var theRequest = new Object();
	if (url.indexOf("?") != -1) {
		var str = url.substr(1);
		strs = str.split("&");
		for ( var i = 0; i < strs.length; i++) {
			theRequest[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
		}
	}
	return theRequest;
}

//取url带入的指定名称的页面参数
function getQueryString(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
	var r = window.location.search.substr(1).match(reg);
	if (r != null)
		return unescape(r[2]);
	return null;
}

// 校验不可空控件
/*
 * function checkFormNullable(parentControlId){ var checkMsg = ''; $("#" +
 * parentControlId + " input[name],#" + parentControlId + " select[name],#" +
 * parentControlId + " textarea[name]").each(function(index,item){ if
 * ($(item).val()==''){ var controlId=$(item).attr('id'); var controlName =
 * $('[for="'+controlId+'"').html(); if (controlName!=null) controlName =
 * controlName.replace(':','');
 * 
 * if (checkMsg!=''){ checkMsg = checkMsg + ','; } checkMsg =
 * checkMsg+controlName; } }); if (checkMsg!=''){ alert(checkMsg+"不能为空!");
 * return false; } return true; }
 */
/* json对象转换成字符串 */
function json2Str(jsonData) {
	if (jsonData == null)
		return null;
	return JSON.stringify(jsonData);
}

function str2Json(str) {
	if (str == null || str.length == 0)
		return null;
	return JSON.parse(str);
}

/* 取随机数 */
function GetRandomNum(Min, Max) {
	var Range = Max - Min;
	var Rand = Math.random();
	return (Min + Math.round(Rand * Range));
}
/* 获取contextpath */
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
	"privateUserInfo" : "my/privateUserInfo.html",
	"companyUserInfo" : "my/companyUserInfo.html",
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
	"register4Private" : "my/register4Private.html",
	"register4Company" : "my/register4Company.html",
	"changePass" : "my/changePass.html",
	"work" : "publish/work.html",
	"reqwork" : "publish/reqwork.html",
	"reqWorkDetail" : "home/reqWorkDetail.html",
	"userContract" : "my/userContract.html",
	"resumeDetail" : "my/resumeDetail.html",
	"editResumeDetail" : "my/editResumeDetail.html",
	"reqwork" : "publish/reqwork.html",
	"changeCompanyUserInfo" : "my/changeCompanyUserInfo.html",
	"changePrivateUserInfo" : "my/changePrivateUserInfo.html",
	"resume" : "my/resume.html"
};

/* 显示子页面，隐藏原页面 */
function changeSubPage(newpageid, oldpageid, params) {
	if (parent.$('#indexPage').length > 0) {
		// 刷新parent.displayFrameId的高度
		var newh = parent.window.innerHeight
				|| parent.document.documentElement.clientHeight
				|| parent.document.body.clientHeight;
		
		parent.$('#' + parent.displayFrameId).css('height', newh-10 + 'px');
		parent.$('#footNavBar').hide();
	}
	if (oldpageid==null){
		oldpageid = $('[data-role="page"]').attr('id');
	}
	$('#' + oldpageid).hide();
	
	
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
				'<iframe src="#" style="border:0;margin:0;display: none;width:'
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
					'<iframe src="#" style="border:0;margin:0;display: none;width:'
							+ clientWindowWidth + 'px;height:'
							+ clientWindowHeight + 'px;" id="otherPage"/>')
					.appendTo('body');
			$('#otherPage').attr('src', newsrc);
		}
	}

	$('#otherPage').show();
}

/* 关闭子页面 */
function closeSubPage(destpageid) {
	// 显示上级页面上的footer导航栏，并重新设置当前iframe的高度
	if (parent.parent.$('#indexPage').length > 0) {
		parent.parent.$('#footNavBar').show();

		var newh = parent.window.innerHeight
				|| parent.document.documentElement.clientHeight
				|| parent.document.body.clientHeight;

		parent.parent.$('#' + parent.parent.displayFrameId).css('height',
				newh-10 + 'px');
	}
	parent.$('#otherPage').hide();
	if (destpageid == null) {
		destpageid = parent.$('[data-role="page"]').attr('id');
	}
	parent.$('#' + destpageid).show();
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

/*
 * 显示信息项修改对话框 infoCode为信息项编码，也是控件id
 */
function showModDialog(infoCode) {
	// 删除已有提示框
	if ($('#modDialog').length > 0) {
		$('#modDialog').remove();
	}
	// 创建信息项修改对话框
	var html = '<div data-role="popup" id="modDialog" data-confirmed="no" data-transition="pop" data-dismissible="false">\
		<div role="main" class="ui-content">+CONTENT+\
		</div></div>';

	// 根据infoCode指定的信息项来决定是显示下拉框，还是显示输入框
	var infoType = '';
	var maxInfoLength = 10;
	var oldValue = $('#infoCode').html();

	var formHtml = '<form action="" onsubmit="return modConfirm()">';
	if (infoType == 'enum') {
		var isMulti = 'yes';

		var selectHtml = '<fieldset data-role="controlgroup"> ';

		if (isMulti == 'yes') {
			selectHtml += '<input type="checkbox" name="checkbox-1a" id="checkbox-1a" checked="">\
			    <label for="checkbox-1a">Cheetos</label> \
			    <input type="checkbox" name="checkbox-2a" id="checkbox-2a">\
			    <label for="checkbox-2a">Doritos</label>\
			    <input type="checkbox" name="checkbox-3a" id="checkbox-3a">\
			    <label for="checkbox-3a">Fritos</label>\
			    <input type="checkbox" name="checkbox-4a" id="checkbox-4a">\
			    <label for="checkbox-4a">Sun Chips</label>';
		} else {
			selectHtml += '<input type="radio" name="dialogCtrlId" id="checkbox-1a" checked="">\
				    <label for="checkbox-1a">Cheetos</label> \
				    <input type="radio" name="dialogCtrlId" id="checkbox-2a">\
				    <label for="checkbox-2a">Doritos</label>\
				    <input type="radio" name="dialogCtrlId" id="checkbox-3a">\
				    <label for="checkbox-3a">Fritos</label>\
				    <input type="radio" name="dialogCtrlId" id="checkbox-4a">\
				    <label for="checkbox-4a">Sun Chips</label>';
		}

		selectHtml += '</fieldset>';
		formHmtl += selectHtml;
	} else if (infoType == 'string') {
		if (maxInfoLength > 1024) {// textarea
			formHmtl += '<textarea cols="40" rows="8" type="textarea" name="dialogCtrlId" id="dialogCtrlId" required="required" placeholder=""></textarea>';
		} else {// 普通输入框
			formHmtl += '<input type="text" name="dialogCtrlId" id="dialogCtrlId" value="+OLDVALUE+" required="required" placeholder="">';
		}
	} else if (infoType == 'number') {
		formHmtl += '<input type="text" name="dialogCtrlId" id="dialogCtrlId" value="+OLDVALUE+" required="required" placeholder="必须是整数" pattern="[0-9]{1,3}" title="必须是整数">';
	} else if (infoType == 'date') {
		formHmtl += '<input type="date" name="dialogCtrlId" id="dialogCtrlId" value="+OLDVALUE+" required="required">';
	}

	formHmtl += '<button class="ui-btn ui-corner-all ui-shadow ui-btn-inline ui-btn-b optionConfirm" type="submmit">提交</button>';
	formHmtl += '<a href="#" class="ui-btn ui-corner-all ui-shadow ui-btn-inline ui-btn-b optionCancel" data-rel="back" data-transition="flow">取消</a>';
	formHmtl += '</form>';

	$(html).appendTo('body');
	var popObj = $('#modDialog');
	popObj.popup();
	$('#modDialog').popup("open");
}

function modConfirm() {
	var newValue = '';
	newValue = $('#dialogCtrlId').html();

	$('#infoCode').html(newValue);
	$('#modDialog').popup("close");
}