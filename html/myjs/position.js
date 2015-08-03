
//使用baidu api实现定位功能
function getPosition(showPosition){
	var geolocation = new BMap.Geolocation();
	geolocation.getCurrentPosition(function(r) {
		if (this.getStatus() == BMAP_STATUS_SUCCESS) {
			//将经纬度缓存下来
			localStorage.latitude = r.point.lat;
			localStorage.longitude = r.point.lng;
			
			// alert('您的位置：'+r.point.lng+','+r.point.lat);
			// ak = appkey 访问次数流量有限制
			$.getJSON(
							'http://api.map.baidu.com/geocoder/v2/?callback=?&ak=71709218d45a706b9c7e3abc2f037b23&location='
									+ r.point.lat
									+ ','
									+ r.point.lng
									+ '&output=json&pois=1',
							function(res) {
								//将位置缓存下来
								localStorage.city = res.result.addressComponent.city;
								localStorage.addressComponent = res.result.addressComponent.city+res.result.addressComponent.district+res.result.addressComponent.street+res.result.addressComponent.street_number;
								//addressComponent =&gt; {city: "广州市", district: "天河区", province: "广东省", street: "广州大道", street_number: "中922号-之101-128"} 

								showPosition();
							});
		} else {
			alert('failed' + this.getStatus());
		}
	}, {
		enableHighAccuracy : true
	})
}

// 关于状态码
// BMAP_STATUS_SUCCESS 检索成功。对应数值“0”。
// BMAP_STATUS_CITY_LIST 城市列表。对应数值“1”。
// BMAP_STATUS_UNKNOWN_LOCATION 位置结果未知。对应数值“2”。
// BMAP_STATUS_UNKNOWN_ROUTE 导航结果未知。对应数值“3”。
// BMAP_STATUS_INVALID_KEY 非法密钥。对应数值“4”。
// BMAP_STATUS_INVALID_REQUEST 非法请求。对应数值“5”。
// BMAP_STATUS_PERMISSION_DENIED 没有权限。对应数值“6”。(自 1.1 新增)
// BMAP_STATUS_SERVICE_UNAVAILABLE 服务不可用。对应数值“7”。(自 1.1 新增)
// BMAP_STATUS_TIMEOUT 超时。对应数值“8”。(自 1.1 新增)
