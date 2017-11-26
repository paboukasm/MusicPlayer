package com.example.heartmatch.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class SpTools
{
	public class MyConstants{

		public static final String CONFIGFILE = "config";
	}

	public static void setInt(Context context, String key, int value) {
		SharedPreferences sp = context.getSharedPreferences(MyConstants.CONFIGFILE, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putInt(key, value);
		edit.commit();
	}
	
	public static int getInt(Context context, String key, int defValue){
		SharedPreferences sp = context.getSharedPreferences(MyConstants.CONFIGFILE, Context.MODE_PRIVATE);
		int value = sp.getInt(key, defValue);
		return value;
	}

	/**
	 * value= true = data is set
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void setBoolean(Context context, String key, boolean value) {
		SharedPreferences sp = context.getSharedPreferences(MyConstants.CONFIGFILE, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putBoolean(key, value);
		edit.commit();
	}

	/**
	 * value = false = data have not set
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static boolean getBoolean(Context context, String key, boolean defValue) {
		SharedPreferences sp = context.getSharedPreferences(MyConstants.CONFIGFILE, Context.MODE_PRIVATE);
		boolean value = sp.getBoolean(key, defValue);
		return value;
	}

	/**
	 * Save Internet data
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void setString(Context context, String key, String value) {
		SharedPreferences sp = context.getSharedPreferences(MyConstants.CONFIGFILE, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putString(key, value);
		edit.commit();
	}

	/**
	 * get Internet data
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static String getString(Context context, String key, String defValue) {
		SharedPreferences sp = context.getSharedPreferences(MyConstants.CONFIGFILE, Context.MODE_PRIVATE);
		String value = sp.getString(key, defValue);
		return value;
	}

	public static void setSet(Context context, String key, Set<String> values) {
		SharedPreferences sp = context.getSharedPreferences(MyConstants.CONFIGFILE, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putStringSet(key, values);
		edit.commit();
	}

	/**
	 * get internet data
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static Set<String> getSet(Context context, String key, Set<String> defValue) {
		SharedPreferences sp = context.getSharedPreferences(MyConstants.CONFIGFILE, Context.MODE_PRIVATE);
		Set<String> value = sp.getStringSet(key, defValue);
		return value;
	}

	/** the XML which store the internet info **/
	public final static String SETTING = "SharedPrefsStrList";

	/**
	 * save info(Int)
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	private static void putIntValue(Context context, String key, int value) {
		Editor sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();
		sp.putInt(key, value);
		sp.commit();
	}

	/**
	 * save info(String)
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	private static void putStringValue(Context context, String key, String value) {
		Editor sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();
		sp.putString(key, value);
		sp.commit();
	}

	/**
	 * save List<String>
	 * 
	 * @param context
	 * @param key
	 *
	 * @param strList
	 *
	 */
	public static void putStrListValue(Context context, String key, List<String> strList) {
		if (null == strList) {
			return;
		}
		removeStrList(context, key);
		int size = strList.size();
		putIntValue(context, key + "size", size);
		for (int i = 0; i < size; i++) {
			putStringValue(context, key + i, strList.get(i));
		}
	}

	/**
	 * get data（int)
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 *
	 * @return
	 */
	private static int getIntValue(Context context, String key, int defValue) {
		SharedPreferences sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
		int value = sp.getInt(key, defValue);
		return value;
	}

	/**
	 * Load data（String)
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 *
	 * @return
	 */
	private static String getStringValue(Context context, String key, String defValue) {
		SharedPreferences sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
		String value = sp.getString(key, defValue);
		return value;
	}

	/**
	 * get List<String>
	 * 
	 * @param context
	 * @param key
	 *            List<String> 对应的key
	 * @return List<String>
	 */
	public static List<String> getStrListValue(Context context, String key) {
		List<String> strList = new ArrayList<String>();
		int size = getIntValue(context, key + "size", 0);
		// Log.d("sp", "" + size);
		for (int i = 0; i < size; i++) {
			strList.add(getStringValue(context, key + i, null));
		}
		return strList;
	}

	/**
	 * Clear List<String>data
	 * 
	 * @param context
	 * @param key
	 *
	 */
	public static void removeStrList(Context context, String key) {
		int size = getIntValue(context, key + "size", 0);
		if (0 == size) {
			return;
		}
		remove(context, key + "size");
		for (int i = 0; i < size; i++) {
			remove(context, key + i);
		}
	}

	/**
	 * @Description TODO 清空List<String>单条数据
	 * @param context
	 * @param key
	 *            List<String>对应的key
	 * @param str
	 *            List<String>中的元素String
	 */
	public static void removeStrListItem(Context context, String key, String str) {
		int size = getIntValue(context, key + "size", 0);
		if (0 == size) {
			return;
		}
		List<String> strList = getStrListValue(context, key);
		List<String> removeList = new ArrayList<String>();
		for (int i = 0; i < size; i++) {
			if (str.equals(strList.get(i))) {
				if (i >= 0 && i < size) {
					removeList.add(strList.get(i));
					remove(context, key + i);
					putIntValue(context, key + "size", size - 1);
				}
			}
		}
		strList.removeAll(removeList);
		putStrListValue(context, key, strList);
	}

	/**
	 * clear corresponding key data
	 * 
	 * @param context
	 * @param key
	 */
	public static void remove(Context context, String key) {
		Editor sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();
		sp.remove(key);
		sp.commit();
	}

	/**
	 * clear data
	 * 
	 * @param context
	 */
	public static void clear(Context context) {
		Editor sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();
		sp.clear();
		sp.commit();
	}
}
