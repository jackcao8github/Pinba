package com.webapp.common.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.StringUtils;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.SysSeqBean;
import com.webapp.common.dao.SysSeqDAO;

/*
 * seq缓存工具类
 */
public class CachedSeqUtil {
	private static SysSeqDAO seqDAO = null;
	private static Map<String,String> tabSeqMap = new HashMap();//key=表名_字段名,value=seq名称
	private static Map<String,BatchSeq> seqCfgMap = new HashMap();//key=seq名称,value=seq配置
	static{
		ApplicationContext context = new ClassPathXmlApplicationContext("Application-Context.xml");
		seqDAO = (SysSeqDAO) context.getBean("seqDAOProxy");
			
		List<AbstractBean> allSeq = seqDAO.getAllSysSeq();
		for (AbstractBean abBean : allSeq){
			SysSeqBean realBean = (SysSeqBean) abBean;
			String seqName = realBean.getSeqName();
			String tabName = realBean.getTabName();
			String colName = realBean.getColName();
			//long lastValue = realBean.getLastNumber();
			long step = realBean.getJVMStepBy();
			
			BatchSeq batchSeq = new BatchSeq(seqDAO);
			batchSeq.setSeqName(seqName);
			batchSeq.setStep(step);
			seqCfgMap.put(seqName, batchSeq);
			
			if (!StringUtils.isEmpty(tabName)){
				tabSeqMap.put(tabName+"_"+colName, seqName);
			}
		}
	}
	
	/**根据表名及主键字段名取seq
	 * @param tabName
	 * @param keColName
	 * @return
	 * @throws Exception 
	 */
	public static long getNewId(String tabName,String keColName) throws Exception{
		String key = tabName+"_"+keColName;
		String seqName = tabSeqMap.get(key);
		return getNewId(seqName);
	}
	/**
	 * 根据seq名称取seq
	 * @param seqName
	 * @return
	 * @throws Exception 
	 */
	public static long  getNewId(String seqName) throws Exception{
		if (!seqCfgMap.containsKey(seqName)){
			throw new Exception("无效的seqName:"+seqName);
		}
		
		BatchSeq batchSeq = seqCfgMap.get(seqName);
		long nowValue = batchSeq.getNewId();//取当前值
		return nowValue;
	}
	
	public static void main(String[] args) throws Exception {
		for (int i=0;i<100;i++){
			new Thread(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					long newId = 0;
					try {
						newId = CachedSeqUtil.getNewId("BATCH_NO$SEQ");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(newId);
				}
				
			}).start();
			new Thread(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					long newId = 0;
					try {
						newId = CachedSeqUtil.getNewId("ORDER_ITEM$SEQ");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(newId);
				}
				
			}).start();
		}
		
		
		System.out.println("xxxxx");
		System.out.println(CachedSeqUtil.getNewId("BATCH_NO$SEQ"));
	}
}
