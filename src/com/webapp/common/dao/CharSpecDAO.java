package com.webapp.common.dao;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.CharSpecBean;
import com.webapp.common.bean.CharSpecValueBean;

public class CharSpecDAO extends AbstractDAO {
	private static List<AbstractBean> charSpecList = null;
	private static List<AbstractBean> charSpecValueList = null;
	private static Map<Long,String> idCodeMap = null;
	public void getAllCharSpec(){
		if (charSpecList==null){
			charSpecList = getBeans(CharSpecBean.class,null);
		}
		if (charSpecValueList==null){
			charSpecValueList = getBeans(CharSpecValueBean.class,null);
		}
	}
	
	public Map<Long,String> getAllCharIdCodeMap(){
		if (idCodeMap==null){
			getAllCharSpec();
			idCodeMap = new HashMap();
			if (charSpecList!=null){
				for (AbstractBean charBean:charSpecList){
					idCodeMap.put(((CharSpecBean)charBean).getCharSpecId(), ((CharSpecBean)charBean).getCode());
				}
			}
		}
		
		return idCodeMap;
	}
	
	//返回特征的枚举值取值
	public JSONObject getAllCharSpecValue(){
		JSONObject charSpecValueMap = new JSONObject();
		if (charSpecList==null){
			getAllCharSpec();
		}
		if (charSpecList!=null){
			for (AbstractBean charBean:charSpecList){
				CharSpecBean charSpecBean = (CharSpecBean) charBean;
				if ("enum".equals(charSpecBean.getType())){
					long charSpecId = charSpecBean.getCharSpecId();
					String charSpecCode = charSpecBean.getCode();
					JSONObject values = new JSONObject();
					
					for (AbstractBean charValueBean:charSpecValueList){
						CharSpecValueBean charSpecValueBean = (CharSpecValueBean) charValueBean;
						long charSpecId2 = charSpecValueBean.getCharSpecId();
						if (charSpecId2==charSpecId){
							JSONObject value = new JSONObject();
							value.put("value", charSpecValueBean.getDisplayValue());
							if (charSpecValueBean.getSelected()!=null&&charSpecValueBean.getSelected().length()>0){
								value.put("selected",charSpecValueBean.getSelected());
							}
							value.put("effCond", charSpecValueBean.getEffCond());
							values.put(charSpecValueBean.getRealValue(),value);
						}
					}
					charSpecValueMap.put(charSpecCode, values);
				}
			}
		}
		return charSpecValueMap;
	}
	public String getCode(Long charId) throws Exception{
		if (getAllCharIdCodeMap().containsKey(charId)){
			return getAllCharIdCodeMap().get(charId);
		}
		return "";
	}
	
	public Long getCharId(String code) throws Exception{
		if (getAllCharIdCodeMap().containsValue(code)){
			Iterator it = getAllCharIdCodeMap().entrySet().iterator();
			while(it.hasNext()){
				Entry ent = (Entry) it.next();
				
				if (code.equals(ent.getValue())){
					return (Long)ent.getKey();
				}
			}
		}
		return 0L;
	}
	
	public String getDisplayValue(String charCode,String realValue){
		JSONObject charMap = getAllCharSpecValue();
		if (charMap.containsKey(charCode)){
			JSONObject values = charMap.getJSONObject(charCode);
				Iterator it = values.entrySet().iterator();
				while (it.hasNext()){
					Entry ent = (Entry) it.next();
					if (ent.getKey().equals(realValue)){
						return ((JSONObject) ent.getValue()).getString("value");
					}
				}
				return realValue;
		}else{
			return realValue;
		}
	}
	
	public String getRealValue(String charCode,String disValue){
		JSONObject charMap = getAllCharSpecValue();
		if (charMap.containsKey(charCode)){
			JSONObject values = charMap.getJSONObject(charCode);
				Iterator it = values.entrySet().iterator();
				while (it.hasNext()){
					Entry ent = (Entry) it.next();
					JSONObject subValue = (JSONObject) ent.getValue();
					if (subValue.getString("value").equals(disValue)){
						return ent.getKey().toString();
					}
				}
				return disValue;
		}else{
			return disValue;
		}
	}
}
