package com.webapp.common.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.ContextLoaderListener;

import sun.misc.BASE64Decoder;

import com.webapp.common.dao.AlbumManagerDAO;
import com.webapp.user.dao.UserManagerDAO;

public class ImageServlet extends HttpServlet {
	private AlbumManagerDAO albumDao = null;

	public ImageServlet() {
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		albumDao = (AlbumManagerDAO) ContextLoaderListener
				.getCurrentWebApplicationContext().getBean("albumDAOProxy");
	}

	public void service(ServletRequest req, ServletResponse res)
			throws ServletException, IOException {
		HttpServletRequest hreq = (HttpServletRequest) req;
		HttpServletResponse hres = (HttpServletResponse) res;

		// 根据iamgeId加载图片
		String imageId = hreq.getParameter("imageId");

		String photoData;
		try {
			photoData = albumDao.getPhotoData(Long.valueOf(imageId));

			OutputStream out = hres.getOutputStream();

			photoData = URLDecoder.decode(photoData, "UTF-8");
			// photoData 前20几位是图片类型信息，格式例如：data:image/png;base64,
			//这个图片类型信息在返回图片数据时不需要，需要替换为空
			// 取图片类型
			int splitIndex = photoData.indexOf(";");
			if (splitIndex > 0) {
				String imageTypeStr = photoData.substring(0, splitIndex);
				String[] strArr = imageTypeStr.split(":");
				hres.setContentType(strArr[1]);
			}
			splitIndex = photoData.indexOf(",");
			photoData = photoData.substring(splitIndex + 1, photoData.length());
			BASE64Decoder decoder = new BASE64Decoder();
			// Base64解码
			byte[] bytes = decoder.decodeBuffer(photoData);
			for (int i = 0; i < bytes.length; ++i) {
				if (bytes[i] < 0) {// 调整异常数据
					bytes[i] += 256;
				}
			}
			out.write(bytes);
			out.flush();
			out.close();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
