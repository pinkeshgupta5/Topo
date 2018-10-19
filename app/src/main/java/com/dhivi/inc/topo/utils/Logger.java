package com.dhivi.inc.topo.utils;

public class Logger {

	private static final boolean LOG = true;

	private static final String TAG = "Teacher Hunt";

	/**
	 * 
	 * @param tag
	 * @param string
	 */
	public static void i(String tag, String string) {
		if (LOG) {
			string = tag + ":: " + string;
			android.util.Log.i(TAG, string);
		}
	}

	/**
	 * 
	 * @param tag
	 * @param string
	 */
	public static void e(String tag, String string) {
		if (LOG) {
			string = tag + ":: " + string;
			android.util.Log.e(TAG, string);
		}
	}

	/**
	 * 
	 * @param tag
	 * @param string
	 */
	public static void d(String tag, String string) {
		if (LOG) {
			string = tag + ":: " + string;
			android.util.Log.d(TAG, string);
		}
	}

	/**
	 * 
	 * @param tag
	 * @param string
	 */
	public static void v(String tag, String string) {
		if (LOG) {
			string = tag + ":: " + string;
			android.util.Log.v(TAG, string);
		}
	}

	/**
	 * 
	 * @param tag
	 * @param string
	 */
	public static void w(String tag, String string) {
		if (LOG) {
			string = tag + ":: " + string;
			android.util.Log.w(TAG, string);
		}
	}

}