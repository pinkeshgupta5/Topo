package com.dhivi.inc.topo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class StoreDetails {


	private static final String DEMOAPP = "demoapp";

	private static final String TUTORIAL = "tutorial";
	private static final String USER_ID = "userId";
	private static final String PASSWORD = "password";
	private static final String LOGINKEY = "login_key";
	private static final String CONTACTSKEY = "contacts_key";
	private static final String USERNAME = "user_name";
	private static final String UID = "uid";
	private static final String USERCONTRY = "contry";
	private static final String FCMDEVICEKEY = "contry";
	private static final String BID = "bid";
	private static final String FNAME = "f_name";
	private static final String LNAME = "l_name";
	private static final String EMAILID = "email";
	private static final String PROFILEPIC = "profile_pic";
	private static final String TODAYSDATE = "today_date";
	private static final String ACCOUNT_STATUS = "account_status";
	private static final String NOOFTOPOS = "numberoftopos";

	public static boolean setTutorialData(Context context, String tutorial) {
		return saveString(context, TUTORIAL, tutorial);
	}

	public static String getTutorialData(Context context) {
		return getString(context, TUTORIAL);
	}


	public static boolean setTopoUserId(Context context, String username) {
		return saveString(context, USER_ID, username);
	}

	public static String getTopoUserId(Context context) {
		return getString(context, USER_ID);
	}
	
	public static boolean setPassword(Context context, String username) {
		return saveString(context, PASSWORD, username);
	}

	public static String getPassword(Context context) {
		return getString(context, PASSWORD);
	}

	
	public static boolean setLoginKey(Context context, String username) {
		return saveString(context, LOGINKEY, username);
	}

	public static String getLoginkey(Context context) {
		return getString(context, LOGINKEY);
	}

	public static boolean setContactsKey(Context context, String username) {
		return saveString(context, CONTACTSKEY, username);
	}

	public static String getContactsKey(Context context) {
		return getString(context, CONTACTSKEY);
	}

	public static boolean setUsernameValue(Context context, String username) {
		return saveString(context, USERNAME, username);
	}

	public static String getUsernameValue(Context context) {
		return getString(context, USERNAME);
	}

	public static boolean setUid(Context context, String initValue) {
		return saveString(context, UID, initValue);
	}

	public static String getUid(Context context) {
		return getString(context, UID);
	}

	public static boolean setUserContry(Context context, String initValue) {
		return saveString(context, USERCONTRY, initValue);
	}

	public static String getUserContry(Context context) {
		return getString(context, USERCONTRY);
	}

	public static boolean setNumberOfTopos(Context context, int initValue) {
		return saveInteger(context, NOOFTOPOS, initValue);
	}

	public static int getNumberOfTopos(Context context) {
		return getInteger(context, NOOFTOPOS);
	}

	public static boolean setFcmDeviceKey(Context context, String initValue) {
		return saveString(context, FCMDEVICEKEY, initValue);
	}

	public static String getFcmDeviceKey(Context context) {
		return getString(context, FCMDEVICEKEY);
	}

	public static boolean setBidValue(Context context, String initValue) {
		return saveString(context, BID, initValue);
	}

	public static String getBidalue(Context context) {
		return getString(context, BID);
	}
	
	public static boolean setFname(Context context, String initValue) {
		return saveString(context, FNAME, initValue);
	}

	public static String getFname(Context context) {
		return getString(context, FNAME);
	}
	
	public static boolean setLname(Context context, String initValue) {
		return saveString(context, LNAME, initValue);
	}

	public static String getLname(Context context) {
		return getString(context, LNAME);
	}
	
	public static boolean setEmailId(Context context, String initValue) {
		return saveString(context, EMAILID, initValue);
	}

	public static String getEmailId(Context context) {
		return getString(context, EMAILID);
	}
	
	public static boolean setProfilePic(Context context, String initValue) {
		return saveString(context, PROFILEPIC, initValue);
	}

	public static String getProfilePic(Context context) {
		return getString(context, PROFILEPIC);
	}
	
	public static boolean setLoginDay(Context context, String initValue) {
		return saveString(context, TODAYSDATE, initValue);
	}

	public static String getLoginDay(Context context) {
		return getString(context, TODAYSDATE);
	}
	
	public static boolean setAccountStatus(Context context, String username) {
		return saveString(context, ACCOUNT_STATUS, username);
	}

	public static String getAccountStatus(Context context) {
		return getString(context, ACCOUNT_STATUS);
	}
	
	// for saving and getting the result....
	private static boolean saveString(Context context, String type, String value) {
		Editor editor = context.getSharedPreferences(DEMOAPP,
				Context.MODE_PRIVATE).edit();
		editor.putString(type, value);
		return editor.commit();
	}

	private static String getString(Context context, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				DEMOAPP, Context.MODE_PRIVATE);
		return sharedPreferences.getString(key, null);
	}

	private static boolean saveInteger(Context context, String type, int value) {
		Editor editor = context.getSharedPreferences(DEMOAPP,
				Context.MODE_PRIVATE).edit();
		editor.putInt(type, value);
		return editor.commit();
	}

	private static int getInteger(Context context, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				DEMOAPP, Context.MODE_PRIVATE);
		return sharedPreferences.getInt(key, 0);
	}

}
