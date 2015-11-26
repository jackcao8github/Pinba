package com.webapp.common.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.alipay.sdk.util.BatchTransUtil;
import com.webapp.common.dao.SysSeqDAO;
import com.webapp.common.util.ExceptionUtil;
import com.webapp.common.util.ServiceFactory;
import com.webapp.work.dao.WorkManagerDAO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 用于向支付宝提交批量转帐通知
 * 
 * @author caojian
 *
 */
public class AlipayPayServlet extends AbstractServlet {
	private static final long serialVersionUID = -6984095850644368965L;
	private WorkManagerDAO workDao = null;
	public AlipayPayServlet() {
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		workDao = (WorkManagerDAO) ServiceFactory.getDAO("workDAO");
	}

	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		doPost((HttpServletRequest) req, (HttpServletResponse) res);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpServletRequest hreq = (HttpServletRequest) request;
		response.setContentType("text/html;charset=utf-8");
		String method = hreq.getParameter("method");

		if (StringUtils.isEmpty(method)) {
			returnFailResult("参数method不能为空", response);
			return;
		}

		try {
			if (method.equals("getToPayList")){//查询待支付工资列表
				JSONArray retArr = workDao.getToPayList();//从待支付清单生成支付信息
				
				super.returnSuccessResult(retArr, response);
			}
			else if (method.equals("payToStaff")) {// 调用支付宝批量转帐接口支付工资
				/*payInfo中的json信息格式，
				 *            [{targetAcctNo:x1,targetAcctName:x11,money:100,seq:1},{targetAcctNo:
				 *            x2,targetAcctName:x12,money:100,seq:2}]
				 *            accountNo为出钱支付宝帐号，accountName为出钱支付帐号所有人名称
				 *            ，receiverList为收款人列表，targetAcctNo为收款人支付宝帐号
				 *            ,targetAcctName为收款人名称,money为收款金额
				 */
				JSONArray recList = super.getJsonArrayFromReq(request, "payInfo");
				// 生成批量支付批次号
				Date nowDate = new Date();
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
				String payDate = formatter.format(nowDate);
				
				SysSeqDAO seqDao = (SysSeqDAO) ServiceFactory.getDAO("seqDAO");
				long newSeq = seqDao.getNewId("BATCH_NO$SEQ");
				StringBuffer alipayBatchNo = new StringBuffer();
				alipayBatchNo.append(payDate).append(newSeq);
				
				JSONObject receiverJson = new JSONObject();
				receiverJson.put("batchNo", alipayBatchNo.toString());
				receiverJson.put("receiverList", recList);
				
				// 生成并返回向支付宝提交使用的form表单html
				BatchTransUtil payUtil = new BatchTransUtil();
				String retHtml = payUtil.batchTrans(receiverJson);
				
				if (!StringUtils.isEmpty(retHtml)){
					//更新支付宝批次号及将状态由toPay更新为paying
					workDao.logAlipayHis(receiverJson);
				}else{
					throw new Exception("支付失败，请联系管理员!");
				}
				
				JSONObject retJson = new JSONObject();
				retJson.put("alipayUrl", retHtml);
				super.returnSuccessResult(retJson, response);
			}else if(method.equals("logBatchPay")){//生成待支付清单
				JSONArray recList = super.getJsonArrayFromReq(request, "payInfo");
				
				JSONObject receiverJson = new JSONObject();
				// 生成批量支付批次号
				Date nowDate = new Date();
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
				String payDate = formatter.format(nowDate);
				
				SysSeqDAO seqDao = (SysSeqDAO) ServiceFactory.getDAO("seqDAO");
				long newSeq = seqDao.getNewId("BATCH_NO$SEQ");
				StringBuffer newBatchNo = new StringBuffer();
				newBatchNo.append(payDate).append(newSeq);
				
				receiverJson.put("batchNo", newBatchNo.toString());
				receiverJson.put("receiverList", recList);
				//记录支付历史
				workDao.logBatchPay(receiverJson);
				
				JSONObject retJson = new JSONObject();
				super.returnSuccessResult(retJson, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			returnFailResult(ExceptionUtil.getMessage(e), response);
		}
	}

}
