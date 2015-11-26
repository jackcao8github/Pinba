package com.webapp.common.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;

import com.alipay.sdk.batchtrans.util.AlipayNotify;
import com.webapp.common.util.ServiceFactory;
import com.webapp.work.dao.WorkManagerDAO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 用于接收支付宝异步返回的批量转帐通知
 * @author caojian
 *
 */
public class AlipayNotifyServlet extends AbstractServlet {
	private static final long serialVersionUID = -6984095850644368965L;
	private WorkManagerDAO workDao = null;
	public AlipayNotifyServlet() {
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		workDao = (WorkManagerDAO) ServiceFactory.getDAO("workDAO");
	}
	public void service(ServletRequest req, ServletResponse res)
			throws ServletException, IOException {
		doPost((HttpServletRequest)req,(HttpServletResponse)res);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//获取支付宝POST过来反馈信息
		Map<String,String> params = new HashMap<String,String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}
		System.out.println("AlipayNotifyServlet.params"+params.toString());
		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
		//批量付款数据中转账成功的详细信息
		/*批量付款中成功付款的信息。
		格式为：流水号^收款方账号^收款账号姓名^付款金额^成功标识(S)^成功原因(null)^支付宝内部流水号^完成时间。
		每条记录以“|”间隔。
		
		0315001^gonglei1@handsome.com.cn^龚本林^20.00^S^null^200810248427067^20081024143652|
		*/
		String success_details = null;
		if (request.getParameter("success_details")!=null){
			success_details = new String(request.getParameter("success_details").getBytes("ISO-8859-1"),"UTF-8");
		}
		
		
		System.out.println("AlipayNotifyServlet.success_details="+success_details);
		//批量付款数据中转账失败的详细信息
		/*批量付款中未成功付款的信息。
		格式为：流水号^收款方账号^收款账号姓名^付款金额^失败标识(F)^失败原因^支付宝内部流水号^完成时间。
		每条记录以“|”间隔。
		0315006^xinjie_xj@163.com^星辰公司1^20.00^F^TXN_RESULT_TRANSFER_OUT_CAN_NOT_EQUAL_IN^200810248427065^20081024143651|
		*/
		String fail_details = null;
		if (request.getParameter("fail_details")!=null){
			fail_details = new String(request.getParameter("fail_details").getBytes("ISO-8859-1"),"UTF-8");
		}
		System.out.println("AlipayNotifyServlet.fail_details="+fail_details);
		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//

		if(AlipayNotify.verify(params)){//验证成功
			System.out.println("AlipayNotifyServlet.验证成功");
			String batchNo = params.get("batch_no").toString();
			
			JSONArray payHisUpdateArray = new JSONArray();
			//发放成功
			if (!StringUtils.isEmpty(success_details)){
				String[] sucArray = success_details.split("\\|");
				System.out.println("AlipayNotifyServlet.sucArray="+sucArray.toString());
				for (int i=0;i<sucArray.length;i++){
					String[] infoArray = sucArray[i].split("\\^");
					
					String seq = infoArray[0];//支付序号
					//根据批次号+支付序号找到支付日志，并记录支付结果
					JSONObject hisBean = new JSONObject();
					hisBean.put("batchNo", batchNo);
					hisBean.put("seq", seq);
					
					hisBean.put("state", "success");
					
					payHisUpdateArray.add(hisBean);
				}
				
			}
			//发放失败
			if (!StringUtils.isEmpty(fail_details)){
				String[] failArray = fail_details.split("\\|");
				System.out.println("AlipayNotifyServlet.failArray="+failArray.toString());
				for (int i=0;i<failArray.length;i++){
					String[] infoArray = failArray[i].split("\\^");
					
					String seq = infoArray[0];//支付序号
					//根据批次号+支付序号找到支付日志，并记录支付结果
					JSONObject hisBean = new JSONObject();
					hisBean.put("batchNo", batchNo);
					hisBean.put("seq", seq);
					
					hisBean.put("state", "fail");
					hisBean.put("remark", infoArray[5]);//失败原因
					payHisUpdateArray.add(hisBean);
				}
			}
			
			//更新支付结果
			try {
				workDao.updateBatchPay(payHisUpdateArray);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("AlipayNotifyServlet.验证成功,返回success");
			response.getOutputStream().println("success");	//请不要修改或删除
		}else{//验证失败
			System.out.println("AlipayNotifyServlet.验证失败,返回fail");
			response.getOutputStream().println("fail");
		}
	}

}
