package com.webapp.common.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.AlbumBean;
import com.webapp.common.bean.AlbumPhotoBean;
import com.webapp.common.dao.AbstractDAO;
import com.webapp.common.dao.CharSpecDAO;
import com.webapp.common.util.TimeUtil;
import com.webapp.user.bean.UserProdCharBean;
import com.webapp.user.bean.UserResumeBean;
import com.webapp.user.bean.UserResumeCharBean;
import com.webapp.user.bean.UserResumeLookHisBean;
import com.webapp.user.bean.UserBean;
import com.webapp.user.bean.UserProdBean;
import com.webapp.work.bean.BatchPayHisBean;
import com.webapp.work.dao.WorkManagerDAO;

public class AlbumManagerDAO extends AbstractDAO {
	private static CharSpecDAO charSpecDao = null;

	public AlbumManagerDAO() {
		charSpecDao = new CharSpecDAO();
		charSpecDao.setDataSource(this.getDataSource());
	}

	// 新增相册
	public long newAlbum(String name, String type,String[] photoDatas) throws Exception {
		AlbumBean albumBean = (AlbumBean) super.newBean(AlbumBean.class);

		albumBean.setAlbumName(name);
		albumBean.setAlbumType(type);
		albumBean.setCreateDate(TimeUtil.getLocalTimeString());
		long albumId = (long) super.insertBean(albumBean);
		
		if (photoDatas!=null&&photoDatas.length>0){
			for (int i=0;i<photoDatas.length;i++){
				this.addPhoto(albumId, photoDatas[i]);
			}
		}

		return albumId;
	}

	// 新增相册
	public void modAlbum(long albumId, String name) throws Exception {

		Map<String, Object> params = new HashMap();
		params.put("ALBUM_ID", albumId);

		AlbumBean albumBean = (AlbumBean) super.getBean(AlbumBean.class, params);
		if (albumBean == null) {
			throw new Exception("相册不存在,id=" + albumId);
		}
		albumBean.setAlbumName(name);
		super.updateBean(albumBean);
	}
	
	//向相册中增加照片
	public long addPhoto(long albumId,String photoData) throws Exception{
		AlbumPhotoBean albumBean = (AlbumPhotoBean) super.newBean(AlbumPhotoBean.class);

		albumBean.setAlbumId(albumId);
		albumBean.setPhotoData(photoData);
		albumBean.setCreateDate(TimeUtil.getLocalTimeString());
		long photoId = (long) super.insertBean(albumBean);

		return photoId;
	}
	public void delPhoto(long photoId) throws Exception{
		Map<String, Object> params = new HashMap();
		params.put("PHOTO_ID", photoId);

		AlbumPhotoBean albumBean = (AlbumPhotoBean) super.getBean(AlbumPhotoBean.class, params);
		if (albumBean != null) {
			super.deleteBean(albumBean);
		}
	}
	public String getPhotoData(long photoId) throws Exception{
		Map<String, Object> params = new HashMap();
		params.put("PHOTO_ID", photoId);

		AlbumPhotoBean albumBean = (AlbumPhotoBean) super.getBean(AlbumPhotoBean.class, params);
		if (albumBean == null) {
			throw new Exception("图片不存在,id=" + photoId);
		}
		
		return albumBean.getPhotoData();
	}
	
	public List<AbstractBean> getPhotos(long albumId){
		Map<String, Object> params = new HashMap();
		params.put("ALBUM_ID", albumId);

	    List<AbstractBean> list =  super.getBeans(AlbumPhotoBean.class, params);
		return list;
	}
}
