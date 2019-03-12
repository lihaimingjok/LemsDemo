package com.pcjz.lems.business.webapi;


import com.pcjz.lems.business.common.utils.TLog;
import com.pcjz.lems.business.common.utils.StringUtils;

import org.json.JSONObject;

public class WebUtils {

	public static String produceRequestParams(String username, String password,
			String emapsn, String jsonStr) {
		try {
			JSONObject user = new JSONObject();
			user.put("user", username);
			user.put("pwd", password);
			user.put("emapsn", emapsn);
			try {
				JSONObject sysbiz = new JSONObject();
				sysbiz.put("sys", user);
				if (!StringUtils.isEmpty(jsonStr)) {
					sysbiz.put("biz", StringUtils.stringToBase64(jsonStr));
					TLog.log("strJson:" + jsonStr);
					TLog.log("strBase64:" + StringUtils.stringToBase64(jsonStr));
				}
				TLog.log("sysbiz" + sysbiz.toString());
				return sysbiz.toString();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String produceRequestParams(String emapsn, String jsonStr) {
		try {
			JSONObject user = new JSONObject();
			/*user.put("user", UserCacheManager.getUserName());
			user.put("pwd", UserCacheManager.getPassword());*/
			user.put("emapsn", emapsn);
			try {
				JSONObject sysbiz = new JSONObject();
				sysbiz.put("sys", user);
				if (!StringUtils.isEmpty(jsonStr)) {
					sysbiz.put("biz", StringUtils.stringToBase64(jsonStr));
					TLog.log("strJson:" + jsonStr);
					TLog.log("strBase64:" + StringUtils.stringToBase64(jsonStr));
				}
				TLog.log("sysbiz" + sysbiz.toString());
				return sysbiz.toString();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String produceRequestParams(String jsonStr) {
		try {
			JSONObject user = new JSONObject();
			/*user.put("user", UserCacheManager.getUserName());
			user.put("pwd", UserCacheManager.getPassword());*/
			user.put("emapsn", "emapsntest");
			try {
				JSONObject sysbiz = new JSONObject();
				sysbiz.put("sys", user);
				if (!StringUtils.isEmpty(jsonStr)) {
					sysbiz.put("biz", StringUtils.stringToBase64(jsonStr));
					TLog.log("strJson:" + jsonStr);
					TLog.log("strBase64:" + StringUtils.stringToBase64(jsonStr));
				}
				TLog.log("sysbiz" + sysbiz.toString());
				return sysbiz.toString();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
