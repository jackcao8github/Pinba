package com.webapp.common.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.webapp.common.dao.CharSpecDAO;
import com.webapp.common.util.ServiceFactory;

public class CharSpecValueJSGenerator implements ServletContextListener {
	private transient static Log log = LogFactory.getLog(CharSpecValueJSGenerator.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		// generate JavaScript resource file
		// charspecvalue.js
		String folder = event.getServletContext().getRealPath(File.separator + "myjs");
		String jsFilePath = folder + File.separator + "charspecvalue.js";
		String jsTempFile = folder + File.separator + "jstemplate.js";
		File jsFile = new File(jsFilePath), folderFile = new File(folder);
		if (!folderFile.exists()) {
			boolean a = folderFile.mkdir();
			if (!a) {
				log.error("===Finishing create folderFile!====");
			}
		}
		if (jsFile.exists()) {
			boolean a = jsFile.delete();
			if (!a) {
				log.error("===Finishing create jsFile!====");
			}
		}
		FileOutputStream fw = null;

		try {
			if (jsFile.createNewFile()) {
				fw = new FileOutputStream(jsFile);
				StringBuilder resource = new StringBuilder("var charspecvalues = ");
				CharSpecDAO charDao = (CharSpecDAO) ServiceFactory.getDAO("charDAO");
				JSONObject charSpecValues = charDao.getAllCharSpecValue();

				resource.append(charSpecValues.toString());
				resource.append(";");

				resource.append(readFileByLines(jsTempFile));
				fw.write(resource.toString().getBytes(Charset.forName("UTF-8")));
				fw.flush();
				fw.close();
			}
			if (log.isInfoEnabled()) {
				log.info(" charspecvalue.js generated successfully:" + jsFilePath);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.debug("", e);
			if (log.isErrorEnabled()) {
				log.error(" charspecvalue.js generated failed:" + jsFilePath);
			}
		} finally {
			if (null != fw) {
				try {
					fw.close();
				} catch (Exception e) {
					log.error("CharSpecValueJSGenerator --->contextInitialized  can not close fw", e);
				}
			}
		}
	}

	public String readFileByLines(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		StringBuffer strBuf = new StringBuffer();
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;

			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				strBuf.append(tempString);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return strBuf.toString();
	}

	// public static void main(String[] args) throws Exception{
	// HashMap cache = CacheFactory.getAll(JavaI18nResourceCacheImpl.class);
	// System.out.print(CrmLocaleFactory.getResource("UPS00010004"));
	// }
}
