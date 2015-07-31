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
	"workMain" : "workMain.html",
	"entertainmentMain" : "entertainmentMain.html"
};

/* 显示子页面，隐藏原页面 */
function changeSubPage(newpageid, oldpageid,params) {
	//如果没有otherpage iframe,则在body里创建一个iframe
	if ($('#otherPage').length==0){
		$('<iframe src="#" style="display: none;width:100%;height:100%;" id="otherPage" />').appendTo('body'); 
	}
	var oldsrc = $('#otherPage').attr('src');
	var newsrc = idHtmlMap[newpageid];
	if (oldsrc != newsrc) {
		if (params!=null){
			newsrc = newsrc + '?' + params;
		}
		$('#otherPage').attr('src', newsrc);
	}
	$('#otherPage').css('display', 'block');
	$('#' + oldpageid).css('display', 'none');
}

/* 关闭子页面 */
function closeSubPage(destpageid) {
	parent.$('#otherPage').css('display', 'none');
	if (destpageid == null) {
		destpageid = parent.$('[data-role="page"]').attr('id');
	}
	parent.$('#' + destpageid).css('display', 'block');
}