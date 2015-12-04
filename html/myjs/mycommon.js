document.write('<script type="text/javascript" src="'+getContextPath()+'/js/json2.js"></script>');// 引入json库
document.write('<script type="text/javascript" src="'+getContextPath()+'/myjs/charspecvalue.js"></script>');// 引入json库
document.write('<link rel="stylesheet" href="'+getContextPath()+'/mycss/mycommon.css" />');// 引入json库

// 生成一个随机数
function GetRandomNum(min, max) {
	var r = Math.random() * (max - min);
	var re = Math.round(r + min);
	re = Math.max(Math.min(re, max), min)

	return re;
}
// 替换所有的回车换行
function transferString(content)  
{  
    var string = content;  
    try{  
        string=string.replace(/\r\n/g,"<BR>")  
        string=string.replace(/\n/g,"<BR>");  
    }catch(e) {  
        alert(e.message);  
    }  
    return string;  
}  
//计算时间差，单位小时
function getHours(startTime,endTime){
	var begintime_ms = Date.parse(new Date(
			startTime
					.replace(/-/g, "/"))); //begintime 为开始时间

	var endtime_ms = Date.parse(new Date(
			endTime.replace(/-/g,
					"/"))); // endtime 为结束时间
	var date3 = endtime_ms - begintime_ms; //时间差的毫秒数

	//计算出相差天数
	//var days=Math.floor(date3/(24*3600*1000));
	 
	//计算出小时数
	var hours = Math.round(date3 / (3600 * 1000)
			* 100) / 100;
	
	return hours;
}

// 生成图片url
function createImageUrl(imageId) {
	return getContextPath() + '/image?imageId=' + imageId;
}

// 加载并显示图形验证码，并绑定点击刷新事件
function loadVerifyImage(verifyImgId) {
	var randnum = GetRandomNum(1000, 2000);
	$('#' + verifyImgId).attr(
			'src',
			getContextPath() + '/verifyImage?width=70&height=21&length=4&v='
					+ randnum);
	$('#' + verifyImgId).bind('touchstart', function() {
		loadVerifyImage('verifyImg');
	});
}

// 替换字符串str中所有指定的字符串
// replaceValueArr格式为{'a':'a1','b':'b1'}
// 元字符: ( [ { \ ^ $ | ) ? * + 任何时候要使用这些元字符 ,都必须对它们进行转义
function replaceAll(str, replaceValueArr) {
	var newStr = '' + str;
	for ( var item in replaceValueArr) {
		var regExp = new RegExp(item, 'gm');

		newStr = newStr.replace(regExp, replaceValueArr[item]);
	}
	return newStr;
}

function logUserPosition(latitude, longitude, cityName) {
	var cachedUserInfo = str2Json(localStorage.userInfo);
	if (cachedUserInfo != null) {
		userInfo = {};
		userInfo.userId = cachedUserInfo.userId;
		userInfo.latitude = latitude;
		userInfo.longitude = longitude;
		userInfo.cityName = cityName;

		ajaxReq('userAction', {
			method : 'logUserPosition',
			userInfo : json2Str(userInfo)
		}, function(data) {
			if (data.flag == 'success') {// 登陆成功后返回主页面
			} else {
			}
		});
	}
}

// 取当前登陆用户信息
function getLoginUserInfo(otLoginFlag) {
	var cachedUserInfo = str2Json(localStorage.userInfo);
	
	if (otLoginFlag==false){
		return cachedUserInfo;
	}
	if (cachedUserInfo != null) {
		return cachedUserInfo;
	} else {
		// 转到登陆页面
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
		for (var i = 0; i < strs.length; i++) {
			theRequest[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
		}
	}
	return theRequest;
}

// 取url带入的指定名称的页面参数
function getQueryString(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
	var r = localStorage.changePageParams.match(reg);
	if (r != null)
		return decodeURI(r[2]);

	r = window.location.search.substr(1).match(reg);
	if (r != null)
		return decodeURI(r[2]);
	return null;
}

function isMobile() {
	if (/(iPhone|iPad|iPod|iOS)/i.test(navigator.userAgent)) {
		return true;
	} else if (/(Android)/i.test(navigator.userAgent)) {
		return true;
	} else {
		return false;
	}
}

// 校验form不可空控件
function checkFormNullable(parentControlId) {
	// 是桌面环境，则直接返回true,执行html5表单校验，
	// 如果移动环境，html5表单校验功能失效，需要执行下面的js校验
	var checkMsg = '';
	$('#' + parentControlId + ' input[name][required],#' + parentControlId
					+ ' select[name][required],#' + parentControlId + ' textarea[name][required]')
			.each(function(index, item) {
				var nullFlag = false;
				if ($(item)[0].type=="checkbox"){
					if ($(item)[0].checked==null||$(item)[0].checked==false){
						nullFlag = true;
					}
				}else if ($(item)[0].type=="radio"){
					
				}
				else if ($(item).val() == '') {
					nullFlag = true;
				}
				if (nullFlag==true){
					$(item).css('border','1px solid red');
					var controlId = $(item).attr('id');
					var label = $('label[for="' + controlId + '"]');
					var controlName = '';
					if (label.length>0){
						controlName = label[0].innerText;
					}
					if (controlName != null)
						controlName = controlName.replace(':', '');

					if (checkMsg != '') {
						checkMsg = checkMsg + ',';
					}
					checkMsg = checkMsg + controlName;
				}else{
					$(item).css('border','0');
				}
			});
	if (checkMsg != '') {
		// showTipDialog( "以下信息项不能为空!<br>" + checkMsg );
		showTipDialog("以下信息项不能为空!<br>" + checkMsg);
		return false;
	}
	return true;
}

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

/* 获取contextpath */
function getContextPath() {
	var parentSrc = window.location.pathname;
	parentSrc = parentSrc.substring(1, parentSrc.length);
	var contextPath = parentSrc.substring(0, parentSrc.indexOf('/'));
	return '/' + contextPath;
}

//显示加载器  
function showLoader() {  
    //显示加载器.for jQuery Mobile 1.2.0  
   /* $.mobile.loading('show', {  
        text: '加载中...', //加载器中显示的文字  
        textVisible: true, //是否显示文字  
        theme: 'a',        //加载器主题样式a-e  
        textonly: false,   //是否只显示文字  
        html: ""           //要显示的html内容，如图片等  
    });  */
}  
  
//隐藏加载器.for jQuery Mobile 1.2.0  
function hideLoader()  
{  
    //隐藏加载器  
  //  $.mobile.loading('hide');  
}  

function ajaxReq(url, data, successCallback, errorCallback, beforeSendCallback,
		completeCallback) {

	var params = '';
	for ( var o in data) {
		if (params != '') {
			params = params + '&'
		}
		params = params + o + '='
				+ encodeURIComponent(encodeURIComponent(data[o]));
	}
	$.ajax({
		type : "POST",
		// contentType : "application/json",
		dataType : "json",
		url : getContextPath() + '/' + url,// + '?' + params,
		data : params,
		beforeSend : function(){showLoader();if (beforeSendCallback)beforeSendCallback();},
		complete : function(){ hideLoader();if (completeCallback)completeCallback()},
		success : successCallback,
		error : errorCallback,
		async : true
	});
}
/* 切换子页面 */
/* 页面列表 */
var idHtmlMap = {
	"myWork" : "my/myWork.html",
	"myMoneyBag" : "my/myMoneyBag.html",
	"myPublish" : "my/myPublish.html",
	"myFocus" : "my/myFocus.html",
	"myLookHis" : "my/myLookHis.html",
	"serviceCenter" : "my/serviceCenter.html",
	"login" : "my/login.html",
	"privateUserInfo" : "my/privateUserInfo.html",
	"companyUserInfo" : "my/companyUserInfo.html",
	"search" : "home/search.html",
	"workDetail" : "home/workDetail.html",
	"changeCity" : "home/changeCity.html",
	"friendsMsg":"message/friendsMsg.html",
	"message" : "message/message.html",
	"searchResult" : "home/searchResult.html",
	"searchCompanyResult" : "home/searchCompanyResult.html",//查询企业用户
	"register4Private" : "my/register4Private.html",/* 个人用户注册 */
	"register4Company" : "my/register4Company.html",/* 企业用户注册 */
	"changePass" : "my/changePass.html",/* 修改密码 */
	"work" : "publish/work.html",/* 发布招聘 */
	"reqWorkDetail" : "home/reqWorkDetail.html",/* 求职展示 */
	"userContract" : "my/userContract.html",/* 用户协议 */
	"resumeDetail" : "my/resumeDetail.html",/* 简历展示 */
	"reqwork" : "publish/reqwork.html",/* 发布求职 */
	"changeCompanyUserInfo" : "my/changeCompanyUserInfo.html",/* 修改企业用户信息 */
	"changePrivateUserInfo" : "my/changePrivateUserInfo.html",/* 修改个人用户信息 */
	"resumeListInWork" : "my/resumeListInWork.html",/* 收到的工作简历 */
	"bookInterview" : "my/bookInterview.html",/* 预约面试 */
	"hiredStaff" : "my/hiredStaff.html",/* 员工录用 */
	"resume" : "my/resume.html",
	"queryAddStaff" : "my/queryAddStaff.html",/* 查询一个员工并录用 */
	"bindAlipayNo" : "my/bindAlipayNo.html",// 绑定支付宝帐号
	"feelingList" : "my/feelingList.html",// 心情列表
	"topup" : "my/topup.html",// 支付宝转帐方式充值
	"payDetail" : "my/payDetail.html",// 支付宝转帐发工资
	"addFeeling":"my/addFeeling.html",//增加心情记录
	"videoCenter":"home/videoCenter.html",
	"randomWork":"home/randomWork.html",//随机工作
	"collectPoints":"home/collectPoints.html",//领积分
	"lauchSplash":"lauchSplash.html",
	"workAssessment":"my/workAssessment.html",//工作评价
	"addCellphoneFriend":"message/addCellphoneFriend.html",//根据手机号增好友
	"addNearbyFriend":"message/addNearbyFriend.html",//查找附近的人并加好友
	"changeCellphone":"my/changeCellphone.html",//修改手机号码
	"report":"home/report.html",//不良信息举报
	"companyHomePage":"my/companyHomePage.html",//公司主页
	"privateHomePage":"my/privateHomePage.html",//个人主页
	"modCheckInOutTime":"my/modCheckInOutTime.html",//修改签到签退时间
	"batchPayWithAlipay":"my/batchPayWithAlipay.html",//通过支付宝指发工资
	"currentWork":"home/currentWork.html",//当前 工作
	"uploadAuthFile" : "my/uploadAuthFile.html"// 用户认证信息上传
};
//打开子页面时，默认为删已有再创建新的
var removeOldSubPage = true;
/* 显示子页面，隐藏原页面 */
function changeSubPage(newpageid, oldpageid, params) {
	var clientWindowHeight = parent.window.innerHeight;

	var clientWindowWidth = parent.window.innerWidth;

	
	if (oldpageid == null) {
		oldpageid = $('[data-role="page"]').attr('id');
	}
	
	var newZIndex = 10000;
	var oldZIndex = parent.$('iframe').css('z-index');
	if (oldZIndex!='auto'){
		newZIndex ++;
	}
	
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
	var newHeight = clientWindowHeight;
	var iframeHtml = '<iframe style="position:fixed;z-index:'+newZIndex+';border:0;width:'
	+ clientWindowWidth + 'px;height:' + newHeight
	+ 'px;max-height:'+newHeight+'px;overflow:hidden;" id="otherPage" src="'+newsrc+'"/>';
	// 如果没有otherpage iframe,则在body里创建一个iframe
	if ($('#otherPage').length == 0) {
		$(iframeHtml).appendTo('body');
		if (params != null) {
			// newsrc = newsrc + '?' + params;
			localStorage.changePageParams = params;
		}
		//$('#otherPage').attr('src', newsrc);
	} else {
		var oldsrc = $('#otherPage').attr('src');
		if (oldsrc != newsrc||removeOldSubPage==true) {
			if (params != null) {
				// newsrc = newsrc + '?' + params;
				localStorage.changePageParams = params;
			}
			$('#otherPage').remove();
			$(iframeHtml).appendTo('body');
			//$('#otherPage').attr('src', newsrc);
		} else {
			if (params != null) {
				localStorage.changePageParams = params;
				// 执行页面的初始化方法
				if ($('#otherPage')[0].contentWindow.pageInit != null)
					$('#otherPage')[0].contentWindow.pageInit();
			}
		}
	}
	if (parent.$('#indexPage').length > 0) {
		parent.$('#indexPage').parent('body').css('overflow-y','hidden');
		// 刷新parent.displayFrameId的高度
		parent.$('#' + parent.displayFrameId).css('height', clientWindowHeight + 'px');
		parent.$('#footNavBar').hide();
	}
	$('#' + oldpageid).css('position','absolute');
	$('#' + oldpageid).parent('body').css('overflow-y','hidden');
	/*$('#otherPage').show();
	$('#otherPage').css('display','block');*/
	
	//记录页面点击日志
	var cachedUserInfo = str2Json(localStorage.userInfo);
	if (cachedUserInfo != null) {
		var logInfo = {};
		logInfo.userId = cachedUserInfo.userId;
		logInfo.newPageId = newpageid;
		logInfo.oldPageId = oldpageid;

		ajaxReq('userAction', {
			method : 'logPageOpenHis',
			logInfo : json2Str(logInfo)
		});
	}
}

/* 关闭子页面 */
function closeSubPage(destpageid) {
	// 显示上级页面上的footer导航栏，并重新设置当前iframe的高度
	if (parent.parent.$('#indexPage').length > 0) {
		parent.parent.$('#footNavBar').show();

		var navBarHeight = 0;
		if (parent.footHeight!=null&&parent.footHeight!=0){
			navBarHeight = parent.footHeight;
		}else{
			navBarHeight = parseInt(parent.parent.$('#footNavBar').css('height'));
		}
		
		var newh = parent.window.innerHeight
				|| parent.document.documentElement.clientHeight
				|| parent.document.body.clientHeight;

		newh = newh - navBarHeight;
		parent.parent.$('#' + parent.parent.displayFrameId).css('height',
				newh + 'px');
	}
	
	if (destpageid == null) {
		destpageid = parent.$('[data-role="page"]').attr('id');
	}
	//parent.$('#' + destpageid).show();
	parent.$('#' + destpageid).parent('body').css('overflow-y','scroll');
	parent.$('#' + destpageid).css('position','');
	// 调用上级页面中的页面初始化方法
	if (parent.pageInit) {
		parent.pageInit();
	}
	parent.$('#otherPage').remove();
}

/*
 * 显示提示对话框
 * tipMsg 提示内容
 * seconds 显示时长
 */
function showTipDialog(tipMsg, seconds) {
	if ($('#tipMsgDialog').length > 0) {
		$('#tipMsgDialog').remove();
	}

	$('<div id="tipMsgDialog" class="alert alert-info" role="alert">'
					+ tipMsg + '</div>').appendTo('body');
	if (seconds == null) {
		seconds = 2000;
	}
	setTimeout(function() {
		$('#tipMsgDialog').hide();
	}, seconds);
}

/*
 * 显示确认对话框 okFun为yes的回调函数
 */
function showConfirmDialog(confirmMsg, okFun,noFun) {
	// 删除已有提示框
	if ($('#confirmMsgDialog').length > 0) {
		$('#confirmMsgDialog').remove();
	}
	 
	// 创建新提示枉
	var htmlTemplate = '<div id="confirmMsgDialog" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"> \
		<div class="modal-dialog" role="document"><div class="modal-content"><div class="modal-header"> \
		<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button> \
		<h4 class="modal-title" id="myModalLabel">操作确认</h4></div><div class="modal-body"> \
		<p>-confirmMsg-</p></div><div class="modal-footer"><button type="button" class="btn btn-default" data-dismiss="modal" id="noBtn">Close</button> \
		<button type="button" class="btn btn-primary" id="okBtn">Save changes</button></div></div></div></div>';
	
	var html = htmlTemplate.replace('-confirmMsg-',confirmMsg);
	$(html).appendTo('body');
	$('#confirmMsgDialog #okBtn').click(function(){
		$('#confirmMsgDialog').modal('hide');
		if (okFun!=null){
			okFun();
		}
	});
	$('#confirmMsgDialog #noBtn').click(function(){
		$('#confirmMsgDialog').modal('hide');
		if (noFun!=null){
			noFun();
		}
	});
	$('#confirmMsgDialog').modal('show');
}

// 向求职列表中增加求职信息
function fillReqWorkList(workListId, data) {
	var newhtml = '<li  onclick="openReqWorkDetail(this)" workId="+workId+"><a> \
    <div style="overflow: hidden;">\
        <p style="float: left; font-weight: bold;">+workName+</p>\
        <p style="color: black; float: right">+time+</p>\
    </div><p>+basicInfo+</p><p>+condition+</p><p >+req+</p></a></li>';

	for (var i = 0; data.length > i; i++) {
		var workObj = data[i];
		var basicInfo = '';
		basicInfo += workObj.resumeInfo.realName;
		basicInfo += '&nbsp;|&nbsp;';
		basicInfo += getDisplayEnumValue('sex', workObj.resumeInfo.sex);
		basicInfo += '&nbsp;|&nbsp;';
		basicInfo += workObj.resumeInfo.age;

		var conditionStr = '';
		conditionStr += getDisplayEnumValue('degree', workObj.resumeInfo.degree)
				+ '学历';
		conditionStr += '&nbsp;|&nbsp;';
		conditionStr += getDisplayEnumValue('experience',
				workObj.resumeInfo.experience)
				+ '年工作经验';

		var reqInfo = '';
		reqInfo += '期望薪资:'
				+ workObj.resumeInfo.sallaryValue
				+ '￥/'
				+ getDisplayEnumValue('sallaryType',
						workObj.resumeInfo.sallaryType);
		reqInfo += '&nbsp;|&nbsp;';
		reqInfo += '期望工作地:'
				+ getDisplayEnumValue('expectWorkArea',
						workObj.resumeInfo.expectWorkArea);

		// '纪云&nbsp;|&nbsp;男&nbsp;|&nbsp;25岁&nbsp;|&nbsp;促销员'
		// '学历：本科&nbsp;|&nbsp;工作经验：3年'
		// '期望日薪:100-200&nbsp;|&nbsp;期望工作地：江宁'
		var html = newhtml.replace('+basicInfo+', basicInfo).replace(
				'+workId+', workObj.workId).replace('+workName+',
				workObj.workName).replace('+time+', workObj.effDate).replace(
				'+condition+', conditionStr).replace('+req+', reqInfo);
		$('#' + workListId).append(html);
	}
}

// 向工作列表中增加工作
function fillWorkList(workListId, data) {
	var newhtml = '<li  onclick="openWork(this)" workId="+workId+" class="list-group-item">\
        <div style="overflow: hidden;">\
        <p style="float: left;font-weight:bold;">+title+</p>\
        <p style="color: black; float: right">+time+</p>\
        </div><div style="overflow: hidden;">\
                    <p style="float: left">+condition+</p>\
                    <p style="color: red; float: right">薪资:+money+</p>\
                </div><div style="overflow: hidden;">\
                    <p style="float: left">+username+</p>\
                    <div style="float: right;line-height:30px; color: #f63">+userCredit+\
                    </div></div></li>';

	for (var i = 0; data.length > i; i++) {
		var workObj = data[i];
		var conditionStr = '';
		conditionStr += getDisplayEnumValue('expectDegree',
				workObj.expectDegree)
				+ '学历';
		conditionStr += '&nbsp;|&nbsp;';
		conditionStr += getDisplayEnumValue('expectExperience',
				workObj.expectExperience)
				+ '经验';
		conditionStr += '&nbsp;|&nbsp;';
		conditionStr += getDisplayEnumValue('expectAge', workObj.expectAge)
				+ '年龄';

		var userCreditHtml = createCreditHtml(workObj.userCredit);

		var html = newhtml.replace('+username+', workObj.userName).replace(
				'+title+', workObj.workName).replace('+time+', workObj.effDate)
				.replace('+condition+', conditionStr).replace(
						'+money+',
						workObj.sallaryValue
								+ '￥/'
								+ getDisplayEnumValue('sallaryType',
										workObj.sallaryType)).replace(
						'+workId+', workObj.workId).replace('+userCredit+',
						userCreditHtml);

		$('#' + workListId).append(html);
	}
}

function createCreditHtml(userCredit) {
	var strArr = [];
	var mod = userCredit / 100;
	mod = parseInt(mod);
	var left = userCredit % 100;
	if (mod > 0) {
		for (var i = 0; mod > i; i++) {
			strArr.push('<i class="fa fa-star"></i>');
		}
	}
	if (left > 50) {
		strArr.push('<i class="fa fa-star-o"></i>');
	}
	if (strArr.length != 5) {
		var c = 5 - strArr.length;
		for (var j = 0; c > j; j++)
			strArr.push('<i class="fa fa-star-o"></i>');
	}

	return strArr.join('');
}