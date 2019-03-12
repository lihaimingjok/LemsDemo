package com.pcjz.lems.business.webapi;

import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.context.AppContext;

public class ApiClientHelper {
	public static String getUserAgent(AppContext appContext) {
		StringBuilder ua = new StringBuilder(AppConfig.WEBSERVICE_URL);
		return ua.toString();
	}
}
