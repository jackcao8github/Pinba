package com.webapp.common.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import com.webapp.common.bean.SysSeqBean;
import com.webapp.common.util.BatchSeq;
import com.webapp.user.bean.UserBean;

public class SysSeqDAO extends AbstractDAO {
	/**根据seq名称取seq配置
	 * @param seqName
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public long getNewId(String seqName) throws InstantiationException, IllegalAccessException {
		Map<String, Object> params = new HashMap();
		params.put("SEQ_NAME", seqName);

		List result = getBeans(SysSeqBean.class, params, true);
		if (result != null && result.size() > 0) {
			SysSeqBean seqBean = (SysSeqBean) result.get(0);
			long lastNumber = seqBean.getLastNumber();
			lastNumber += 1;

			seqBean.setLastNumber(lastNumber);
			updateBean(seqBean);

			return lastNumber;
		}
		return 0;
	}

	/**
	 * 取指定表名和key字段名的seq配置
	 * @param tabName
	 * @param colName
	 * @return
	 */
	public long getNewId(String tabName, String colName) {
		Map<String, Object> params = new HashMap();
		params.put("TAB_NAME", tabName);
		params.put("COL_NAME", colName);

		List result = getBeans(SysSeqBean.class, params, true);
		if (result != null && result.size() > 0) {
			SysSeqBean seqBean = (SysSeqBean) result.get(0);
			long lastNumber = seqBean.getLastNumber();
			lastNumber += 1;

			seqBean.setLastNumber(lastNumber);
			updateBean(seqBean);

			return lastNumber;
		}
		return 0;
	}
	/**
	 * 更新seq的最大值
	 * @param seqName
	 * @param lastValue
	 */
	public void updateSysSeq(String seqName,long lastValue){
		Map<String, Object> params = new HashMap();
		params.put("SEQ_NAME", seqName);

		List result = getBeans(SysSeqBean.class, params, true);
		if (result != null && result.size() > 0) {
			SysSeqBean seqBean = (SysSeqBean) result.get(0);

			seqBean.setLastNumber(lastValue);
			updateBean(seqBean);
		}
	}
	/**
	 * 取所有的seq配置
	 * @return
	 */
	public List getAllSysSeq(){
		Map<String, Object> params = new HashMap();

		List result = getBeans(SysSeqBean.class, params, true);
		return result;
	}

	public BatchSeq getBatchSeq(String seqName) throws Exception{
		Map<String, Object> params = new HashMap();
		params.put("SEQ_NAME", seqName);
		
		List result = getBeans(SysSeqBean.class, params, true);
		if (result != null && result.size() > 0) {
			SysSeqBean seqBean = (SysSeqBean) result.get(0);

			long lastValue = seqBean.getLastNumber();
			long step = seqBean.getJVMStepBy();
			
			BatchSeq seq = new BatchSeq();
			seq.setNowValue(lastValue);
			seq.setMaxValue(lastValue+step);
			
			seqBean.setLastNumber(lastValue+step+1);
			super.updateBean(seqBean);
			
			return seq;
		}else{
			throw new Exception("无效的seqName:"+seqName);
		}
	}
	public static void main(String[] args) throws InstantiationException, IllegalAccessException {

		
		 ApplicationContext context = new
		  ClassPathXmlApplicationContext("Application-Context.xml");
		  
		  
		SysSeqDAO userDAO = (SysSeqDAO) context.getBean("seqDAOProxy");
		 userDAO.getNewId("USER$SEQ");
		 

		/*try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con;
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mres", "root", "");
			PreparedStatement p = con.prepareStatement("select * from user");
			
			p.execute();
			
			ResultSet set = p.getResultSet();
			
			System.out.println(set.toString());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

	}
}
