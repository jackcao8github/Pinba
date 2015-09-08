package com.webapp.common.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import com.webapp.common.bean.SysSeqBean;
import com.webapp.user.bean.UserBean;

public class SysSeqDAO extends AbstractDAO {
	public long getNewId(String seqName) throws InstantiationException, IllegalAccessException {
		Map<String, String> params = new HashMap();
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

	public long getNewId(String tabName, String colName) {
		Map<String, String> params = new HashMap();
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
