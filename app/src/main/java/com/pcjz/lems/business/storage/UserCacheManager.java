package com.pcjz.lems.business.storage;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.pcjz.lems.business.context.AppContext;
import com.pcjz.lems.business.common.utils.StringUtils;

import java.util.HashMap;
import java.util.HashSet;

public class UserCacheManager {

	private static final String KEY_LOGIN_ID = "key_login_id";

	private static SharedPreferences preferences = AppContext.getPreferences();
	private static String userName = "";
	private static String passWord = "";

	public static void setUserName(String username) {
		userName = username;
	}

	public static String getUserName() {
		return userName;
	}

	public static void setPassword(String password) {
		passWord = password;
	}

	public static String getPassword() {
		return passWord;
	}

	public static void saveLoginInfo(HashMap<String, String> userInfo) {
		Editor editor = preferences.edit();
		/*editor.putString(UserInfoConstant.USERNAME,
				userInfo.get(UserInfoConstant.USERNAME));
		editor.putString(UserInfoConstant.PASSWORD,
				userInfo.get(UserInfoConstant.PASSWORD));
		editor.putString(UserInfoConstant.PASSWORDREMEMBERED,
				userInfo.get(UserInfoConstant.PASSWORDREMEMBERED));*/
		editor.commit();
	}

	public static HashMap<String, String> getLoginInfo() {
		HashMap<String, String> userInfo = new HashMap<String, String>();
		/*userInfo.put(UserInfoConstant.USERNAME,
				preferences.getString(UserInfoConstant.USERNAME, ""));
		userInfo.put(UserInfoConstant.PASSWORD,
				preferences.getString(UserInfoConstant.PASSWORD, ""));
		userInfo.put(UserInfoConstant.PASSWORDREMEMBERED,
				preferences.getString(UserInfoConstant.PASSWORDREMEMBERED, ""));*/
		return userInfo;
	}

	public static void saveAccount(HashSet<String> accounts) {
		Editor editor = preferences.edit();
		editor.putStringSet("accounts", accounts);
		editor.commit();
	}

	public static HashSet<String> getAccount() {
		HashSet<String> accounts = (HashSet<String>) preferences.getStringSet(
				"accounts", null);
		return accounts;
	}

	public static void setLoginUserId(String user_id) {
		Editor editor = preferences.edit();
		editor.putString(KEY_LOGIN_ID, user_id);
		editor.commit();
	}

	/**
	 * �����¼��Ϣ
	 */
	public static void cleanLoginInfo() {
		Editor editor = preferences.edit();
		editor.remove("user_id");
		editor.remove("user_name");
		editor.commit();
		setLoginUserId("");
	}

	/**
	 * �û��Ƿ��¼
	 */
	public static boolean isUserLogin() {
		return !StringUtils.isEmpty(getLoginUserId());
	}

	/**
	 * ��ȡ��¼�û�id
	 */
	public static String getLoginUserId() {
		return preferences.getString(KEY_LOGIN_ID, "");
	}

}
