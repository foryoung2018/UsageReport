package com.htc.lib1.HtcCalendarFramework.util.calendar.holidays;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.text.TextUtils;

/**
 * Holiday Manager
 */
public abstract class HolidayManager {
	private static HolidayManager holidaymanager;
	private OnDataReadyListener mOnDataReadyListener;
	protected Context mContext;
	private static final String TAG = "HolidayManager_Log";

	/**
	 * @deprecated [Not use any longer]
	 */
	/** @hide */
	@Deprecated
	protected static Resources mResources;

	/**
	 * The HolidayManager Locale
	 */
	protected static String mLocale;

	/**
	 * @deprecated [Not use any longer]
	 */
	/** @hide */
	@Deprecated
	protected Resources mResource;

	/**
	 * To get the holiday name
	 * 
	 * @param year
	 *            indicate in which year
	 * @param month
	 *            indicate in which month
	 * @param date
	 *            indicate in which date
	 * @return the holiday name
	 */
	public abstract String getHoliday(int year, int month, int date);

	/**
	 * To get the Calendar
	 * 
	 * @param year
	 *            indicate in which year
	 * @param month
	 *            indicate in which month
	 * @param date
	 *            indicate in which date
	 * @return the Calendar map in <key, value>
	 */
	public abstract Map<String, String> getCalendar(int year, int month,
			int date);

	/**
	 * 20150610 China public holiday check if it's holiday
	 * 
	 * @param year
	 *            indicate in which year
	 * @param month
	 *            indicate in which month, refer to Calendar.get(Calendar.MONTH)
	 * @param date
	 *            indicate in which date
	 * @return true if holiday false if work day
	 */
	public abstract boolean isHoliday(int year, int month, int date);

	/**
	 * The HolidayManager constructor
	 */
	@Deprecated
	protected HolidayManager() {
	}

	protected HolidayManager(Context context) {
		mContext = context;
	}

	/**
	 * @deprecated [Not use any longer]
	 */
	/** @hide */
	@Deprecated
	public String getHolidays(int year, int month, int date) {
		return "";
	}

	public interface OnDataReadyListener {
		public void onDataReady(boolean hasData);
	}

	/**
	 * To get the instance of HolidayManager
	 * 
	 * @param context
	 *            the Context
	 * @return the HolidayManager instance
	 */
	public static synchronized HolidayManager getInstance(Context context) {

		if (isLocaleChanged()) {
			if (HolidayUtils.isChinaHoildayEnable(context)) {
				holidaymanager = new ChinaHoliday(context, true);
			} else if (HolidayUtils.isJapanHoildayEnable()) {
				holidaymanager = new JapanHoliday(context, true);
			}
		} else {
			if (holidaymanager != null)
				return holidaymanager;

			// check what instance ..
			if (HolidayUtils.isChinaHoildayEnable(context)) {
				holidaymanager = new ChinaHoliday(context);
			} else if (HolidayUtils.isJapanHoildayEnable()) {
				holidaymanager = new JapanHoliday(context);
			}
		}

		return holidaymanager;
	}

	/**
	 * MUST call this at first time to prepare holiday data
	 * @param callback to listen holiday data ready
	 */
	public void initialize(OnDataReadyListener callback) {
		mOnDataReadyListener=callback;
				
		if (HolidayUtils.isChinaHoildayEnable(mContext)) {
			ChinaHolidayUtil.getInstance().initialize(mContext, callback);
		} else {
			if (mOnDataReadyListener!=null)
				mOnDataReadyListener.onDataReady(true);
		
		}
	}
	
	
	/**
	 * Must call this before getting holiday data.
	 * Noted: It's blocking code without callback .
	 */
	public void initialize() {	
		if (HolidayUtils.isChinaHoildayEnable(mContext)) {
			ChinaHolidayUtil.getInstance().initialize(mContext);
		} 
	}
	
	
	private static boolean isLocaleChanged() {
		Locale locale = Locale.getDefault();
		String language = "";
		String systemLanguage = locale.getLanguage();
		String country = locale.getCountry();

		language = systemLanguage + "_" + country;

		if (TextUtils.isEmpty(mLocale)) {
			mLocale = language;
			return false;
		}

		if (!TextUtils.isEmpty(mLocale) && !mLocale.equals(language)) {
			mLocale = language;
			return true;
		}
		return false;
	}
}

/**
 * The China Holiday Strategy
 */
class ChinaHoliday extends HolidayManager {

	private LunarUtils lunar = null;


	/**
	 * The ChinaHoliday constructor
	 * 
	 * @param forceUpdate
	 *            force to update instance
	 */
	public ChinaHoliday(Context context, boolean forceUpdate) {
		super(context);
		lunar = LunarUtils.getInstance(context, forceUpdate);
	}

	/**
	 * The ChinaHoliday constructor
	 */
	public ChinaHoliday(Context context) {
		super(context);
		lunar = LunarUtils.getInstance(context);
	}

	/**
	 * To get the Chinese holiday name
	 * 
	 * @param year
	 *            indicate in which year
	 * @param month
	 *            indicate in which month
	 * @param date
	 *            indicate in which date
	 * @return the holiday name
	 */
	@Override
	public String getHoliday(int year, int month, int date) {
        // Don't use getLunarCalendarSetting after M.
        // We should use Build.VERSION_CODES.M but we can't since it is
        // introduced after M. Thus we have to hardcoding 23 for backward
        // compatibility.
	    if (Build.VERSION.SDK_INT < 23 && !HolidayUtils.getLunarCalendarSetting(mContext)) {
	        return "";
	    }
	    
	    if(!HolidayUtils.isChinaLanguage()) {
	        return "";
	    }
	    
		lunar.set(year, month, date);
		String solarHolidays = lunar.getSolarHoliday();
		String specailHolidays = lunar.getSpecialHoliday();

		if (HolidayUtils.isChinaSku() && solarHolidays != "") {
			return solarHolidays;
		} else if (HolidayUtils.isChinaSku() && specailHolidays != "") {
			return specailHolidays;
		} else {
			return lunar.getLunarDate();
		}
	}

	/**
	 * To get the Chinese Calendar
	 * 
	 * @param year
	 *            indicate in which year
	 * @param month
	 *            indicate in which month
	 * @param date
	 *            indicate in which date
	 * @return the Calendar map in <key, value>
	 */
	@Override
	public Map<String, String> getCalendar(int year, int month, int date) {
		lunar.set(year, month, date);
		Map<String, String> chinaCalendar = new HashMap<String, String>();
		chinaCalendar = lunar.getChinaCalendar();
		return chinaCalendar;
	}

	/**
	 * To check if china public holiday
	 * 
	 * @param year
	 *            indicate in which year
	 * @param month
	 *            indicate in which month; refer to Calendar.get(Calendar.MONTH)
	 * @param date
	 *            indicate in which date
	 * @return true it's holiday false it's work day.
	 */
	@Override
	public boolean isHoliday(int year, int month, int date) {
		return ChinaHolidayUtil.getInstance().isHoliday( year, month,date);
	}
}

/**
 * Japanese Holiday Strategy
 */
class JapanHoliday extends HolidayManager {
	private JapanHolidayUtils Utils = null;
	private Resources mResources;

	/**
	 * The JapanHoliday constructor
	 */
	public JapanHoliday(Context context) {
		super(context);
		mResources = context.getResources();
		Utils = JapanHolidayUtils.getInstance(mResources);
	}

	/**
	 * The JapanHoliday constructor
	 * 
	 * @param forceUpdate
	 *            force to update instance
	 */
	public JapanHoliday(Context context, boolean forceUpdate) {
		super(context);
		mResources = context.getResources();
		Utils = JapanHolidayUtils.getInstance(mResources, forceUpdate);
	}

	/**
	 * To get the Japanese holiday name
	 * 
	 * @param year
	 *            indicate in which year
	 * @param month
	 *            indicate in which month
	 * @param date
	 *            indicate in which date
	 * @return the holiday name
	 */
	@Override
	public String getHoliday(int year, int month, int date) {

		Utils.set(year, month, date);
		return Utils.getHolidays();
	}

	/**
	 * To get the Japanese Calendar
	 * 
	 * @param year
	 *            indicate in which year
	 * @param month
	 *            indicate in which month
	 * @param date
	 *            indicate in which date
	 * @return the Calendar map in <key, value>
	 */
	@Override
	public Map<String, String> getCalendar(int year, int month, int date) {
		return null;
	}

	@Override
	public boolean isHoliday(int year, int month, int date) {
		// TODO Auto-generated method stub
		return false;
	}

}
