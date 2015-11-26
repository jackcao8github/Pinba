package com.alipay.sdk.util;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.alipay.sdk.batchtrans.util.AlipaySubmit;
import com.alipay.sdk.config.AlipayConfig;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class BatchTransUtil {
	/**
	 * 调用支付宝接口完成批量转帐
	 * 
	 * @param msg
	 *            json格式的信息，{accountNo:xx@163.com,accountName:xx公司,receiverList:
	 *            [{targetAcctNo:x1,targetAcctName:x11,money:100},{targetAcctNo:
	 *            x2,targetAcctName:x12,money:100}]}
	 *            accountNo为出钱支付宝帐号，accountName为出钱支付帐号所有人名称
	 *            ，receiverList为收款人列表，targetAcctNo为收款人支付宝帐号
	 *            ,targetAcctName为收款人名称,money为收款金额
	 * @return
	 */
	public String batchTrans(JSONObject jsonMsg) {
		try {
			JSONArray receiverList = jsonMsg.getJSONArray("receiverList");

			float totalMoney = 0.0f;
			StringBuffer detail = new StringBuffer();
			for (int i=0;i<receiverList.size();i++){
				JSONObject obj= receiverList.getJSONObject(i);
				
				totalMoney += Float.valueOf(obj.getString("money"));
				
				if (detail.length()>0){
					detail.append("|");
				}
				detail.append(obj.getString("seq")).append("^").append(obj.getString("targetAcctNo")).append("^").append(obj.getString("targetAcctName")).append("^").append(obj.getString("money")).append("^工资");
			}
			// 从msg中解析出以下参数
			String batchNo = jsonMsg.getString("batchNo");
			String batchFee = String.valueOf(totalMoney);
			String batchNum = String.valueOf(receiverList.size());
			

			Date nowDate = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			String payDate = formatter.format(nowDate);
			// 把请求参数打包成数组
			Map<String, String> sParaTemp = new HashMap<String, String>();
			sParaTemp.put("service", "batch_trans_notify");
			sParaTemp.put("partner", AlipayConfig.PARTNER);
			sParaTemp.put("_input_charset",  AlipayConfig.input_charset);
			sParaTemp.put("notify_url", AlipayConfig.batch_trans_notify_url);
			sParaTemp.put("email", AlipayConfig.SELLER);
			sParaTemp.put("account_name", AlipayConfig.SELLER_NAME);
			sParaTemp.put("pay_date", payDate);
			sParaTemp.put("batch_no", batchNo);
			sParaTemp.put("batch_fee", batchFee);
			sParaTemp.put("batch_num", batchNum);
			sParaTemp.put("detail_data", detail.toString());
			
			// 建立请求
			String alipayUrl = AlipaySubmit.buildRequestUrl( sParaTemp);
			return alipayUrl;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public static void main(String[] args) {
		BatchTransUtil util = new BatchTransUtil();
		String msg = "{\"accountNo\":\"huangjihui41@163.com\",\"accountName\":\"南京尊尚网络科技有限公司\",\"receiverList\":[{\"targetAcctNo\":\"jack.cao@163.com\",\"targetAcctName\":\"曹建\",\"money\":0.01}]}";
		//util.batchTrans(msg);
	}
}
