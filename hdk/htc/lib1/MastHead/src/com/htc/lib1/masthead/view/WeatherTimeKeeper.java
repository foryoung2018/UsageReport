/**
 * HTC Corporation Proprietary Rights Acknowledgment
 *
 * Copyright (C) 2010 HTC Corporation
 *
 * All Rights Reserved.
 *
 * The information contained in this work is the exclusive property of HTC Corporation
 * ("HTC").  Only the user who is legally authorized by HTC ("Authorized User") has
 * right to employ this work within the scope of this statement.  Nevertheless, the 
 * Authorized User shall not use this work for any purpose other than the purpose 
 * agreed by HTC.  Any and all addition or modification to this work shall be 
 * unconditionally granted back to HTC and such addition or modification shall be 
 * solely owned by HTC.  No right is granted under this statement, including but not 
 * limited to, distribution, reproduction, and transmission, except as otherwise 
 * provided in this statement.  Any other usage of this work shall be subject to the 
 * further written consent of HTC.
 */
package com.htc.lib1.masthead.view;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.text.style.StyleSpan;

import com.htc.lib1.HtcCalendarFramework.util.calendar.holidays.HolidayManager;
import com.htc.lib1.settings.provider.HtcWrapSettings;
import com.htc.lib2.weather.WeatherConsts;
import com.htc.lib2.weather.WeatherLocation;
import com.htc.lib2.weather.WeatherRequest;
import com.htc.lib2.weather.WeatherUtility;

class WeatherTimeKeeper extends BroadcastReceiver {
	private static final String LOG_TAG = WeatherTimeKeeper.class.getSimpleName();
	// some broadcast to listen to
	private static final String ACTION_DATE_FORMAT_CHANGE = "com.htc.intent.action.TIMEFORMAT_CHANGED";
	private static final String ACTION_DELETE_CURRENT_LOCATION = "com.htc.Weather.delete_current_location";
    private static final String ACTION_CUSTOMIZATION_CHANGED = "com.htc.intent.action.CUSTOMIZATION_CHANGE";
    
    private static final int MESSAGE_REFRESH_WEATHER = 0xF1;
    private static final int MESSAGE_REFRESH_TIME = 0xF2;
    private static final int MESSAGE_CHANGE_TIMEFORMAT = 0xF3;
    private static final int MESSAGE_NOTIFY_TIME_SCHEDULE = 0xF4; // replace ACTION_TIME_TICK function
    private static final int MESSAGE_NOTIFY_WEATHER_SCHEDULE = 0xF5;

    private static final String TIME_FORMAT_12HR = "h:mm";
    private static final String TIME_FORMAT_24HR = "k:mm";
    
    private static String sHtcDateFormat = null;

    private static final String LOCATION_SERVICE_NAME = "com.htc.htclocationservice";
    
    private CharSequence mCurrentTimeString = "";
    private int mAmPm = -1;
    private CharSequence mCurrentDateString = "";
    
    private long mCurrentTimeMillis;
    private long mTriggerTimeMillis = -1;
    
    private boolean mIsEnableWeather = true;

    private boolean mPauseWeather;
    private boolean mPauseDateTime;    
    private boolean mWeatherChanged = false;
    private boolean mIsWeatherRunning; // check weather data is updating or not.
    private WeatherInfo mWeather = new WeatherInfo();
    private WeatherTimeChangedListener mWeatherTimeChangedListener;

	private void runRefreshWeather() {
		final Context context = mContext;		
		WeatherInfo weather = mWeather;
		weather.mIsLocationEnabled = Settings.Secure
				.isLocationProviderEnabled(mContext.getContentResolver(),
						LocationManager.NETWORK_PROVIDER);
		Logger.d(LOG_TAG, "locationEnabled=" + weather.mIsLocationEnabled);
		weather.mIsAutoSync = WeatherUtility.isSyncAutomatically(mContext);

		weather.mText2 = ResourceHelper
				.getSharedString(
						context,
						com.htc.lib1.masthead.R.string.masthead_st_location_service_off);
		weather.mText3 = ResourceHelper.getSharedString(context,
				com.htc.lib1.masthead.R.string.masthead_st_auto_sync_off);

		weather.mCity = null;
		if (weather.mIsLocationEnabled) {
			WeatherLocation[] locations = null;
			try {
				locations = WeatherUtility.loadLocations(
						context.getContentResolver(), LOCATION_SERVICE_NAME);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (locations != null && locations.length > 0) {
				String city = locations[0].getName();
				if (city != null && city.length() > 0) {
					weather.mCity = city;
				} else {
					weather.mCity = ResourceHelper
							.getSharedString(
									context,
									com.htc.lib1.masthead.R.string.masthead_st_current_location);
				}
			}
		}

		weather.mTemperature = null;
		weather.mTempSymbol = null;
		weather.mWeatherWebLink = null;
		// retrieve weather data for current location
		Bundle wspData = null;
		Bundle data = null;
		if (weather.mIsLocationEnabled) {
			// api change
			// wspData = WeatherUtility.request(context,
			// WeatherRequest.generateWeatherRequestForCurrentLocation());
			try {
				wspData = WeatherRequest.request(context, WeatherRequest
						.generateWeatherRequestForCurrentLocation());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (wspData != null) {
				// api change
				// data = wspData.getCurWeatherDataInfo(context, null);
				data = wspData
						.getParcelable(WeatherConsts.KEY_OUT_CURWEATHER_DATA);
			}
		}
		if (data != null && !data.isEmpty()) {
			if (wspData != null) {
				weather.mWeatherWebLink = wspData
						.getString(WeatherConsts.KEY_OUT_CITY_WEB_URL);
				Logger.w(LOG_TAG, "[refreshWeatherData] weather.mWeatherWebLink %s", weather.mWeatherWebLink);
			}
			int conditionIconId = data.getInt(
					WeatherConsts.KEY_OUT_CURR_COND_ID, WeatherInfo.NO_WEATHER);
			if (conditionIconId == WeatherInfo.NO_WEATHER) {
				Logger.w(LOG_TAG,
						"[refreshWeatherData] weather condition icon resource invalid or not found");
				weather.mId = WeatherInfo.NO_WEATHER;
				weather.mIcon = ResourceHelper.getNoWeatherIcon(context);
				weather.mText = context
						.getString(com.htc.lib1.masthead.R.string.masthead_st_weather_unavailable);
			} else if (conditionIconId != weather.mId) {
				weather.mId = conditionIconId;
				weather.mIcon = ResourceHelper.getWeatherIcon(context,
						conditionIconId);
				// always return text to support TalkBack
				weather.mText = ResourceHelper.getWeatherText(context,
						conditionIconId);
			} // else: same weather condition as last time

			// Show temperature only when weather condition is available
			if (conditionIconId != WeatherInfo.NO_WEATHER) {
				if (WeatherUtility.isTemperatureCelsius(context)) { // TODO
																	// don't ask
																	// this
																	// every
																	// time
					weather.mTempSymbol = "C"; // Celsius
					weather.mTemperature = data.getString(
							WeatherConsts.KEY_OUT_CURR_TEMP_C, null);
				} else {
					weather.mTempSymbol = "F"; // Fahrenheit
					weather.mTemperature = data.getString(
							WeatherConsts.KEY_OUT_CURR_TEMP_F, null);
				}
				if (weather.mTemperature != null
						&& weather.mTemperature.length() > 0) {
					SpannableString bold = SpannableString
							.valueOf(weather.mTemperature);
					bold.setSpan(new StyleSpan(Typeface.BOLD), 0,
							weather.mTemperature.length(),
							Spanned.SPAN_INCLUSIVE_INCLUSIVE);
				}
			}
			mTriggerTimeMillis = data.getLong(WeatherConsts.KEY_OUT_TRIGGER_TIME, -1);
			Logger.w(LOG_TAG, "[refreshWeatherData] trigger time = " + mTriggerTimeMillis);
			notifyWeatherSchedule(mTriggerTimeMillis);
			// No schedule alarm, since the weather data will be refresh by
			// onWindowFocusChanged
		} else {
			Logger.w(LOG_TAG, "[refreshWeatherData] no weather data");
			weather.mId = WeatherInfo.NO_WEATHER;
			weather.mIcon = ResourceHelper.getNoWeatherIcon(context);
			weather.mText = context
					.getString(com.htc.lib1.masthead.R.string.masthead_st_weather_unavailable);
			mTriggerTimeMillis = -1; // clear trigger time
		}

		Logger.d(LOG_TAG, "[refreshWeatherData] " + weather.toString());

		if (mWeatherTimeChangedListener != null) {
			mWeatherTimeChangedListener.onWeatherChanged(weather);
		} else {
			Logger.w(LOG_TAG, "frefreshing weather w/o listener");
		}
	}

	//Because several variables are used in bg worker, if bFromWorker==false, we don't check the variables
	private void runRefreshTime(boolean bFromWorker) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(mCurrentTimeMillis);
		String holiday = null;
		final CharSequence currentDateString = formatDateString(calendar);
		boolean dateStringChanged = true;
		if (bFromWorker) {
			if (mCurrentDateString == null || !mCurrentDateString.equals(currentDateString))
				mCurrentDateString = currentDateString;
			else
				dateStringChanged = false;
		}
	
		if (dateStringChanged) {
			holiday = getHolidayString(calendar, mbSupportHoliday);
			if (mWeatherTimeChangedListener != null) {
				mWeatherTimeChangedListener.onDateChanged(currentDateString,
						holiday);
			}
		}
		
		Logger.d(LOG_TAG, "[refreshTime] date: %s, holi: %s", mCurrentDateString, holiday);
		
		// set AM/PM string before time so we can get complete description when
		// calling onTimeChanged() later
		final boolean is24h = DateFormat.is24HourFormat(mContext);
		final int newAmPm = is24h ? -1 : calendar.get(Calendar.AM_PM);
		
		boolean ampmChanged = true;
		if (bFromWorker) {
			if (mAmPm != newAmPm)
				mAmPm = newAmPm;
			else
				ampmChanged = false;
		}
		
		if (ampmChanged) {
			if (mWeatherTimeChangedListener != null) {
				mWeatherTimeChangedListener.onTimeOfDayChanged(newAmPm, is24h);
			}
		}

		final CharSequence currentTimeString = DateFormat.format(
				is24h ? TIME_FORMAT_24HR : TIME_FORMAT_12HR, calendar);
		
		boolean timeChanged = true;
		if (bFromWorker) {
			if (mCurrentTimeString == null || !mCurrentTimeString.equals(currentTimeString))
				mCurrentTimeString = currentTimeString;
			else
				timeChanged = false;
		}
		
		if (ampmChanged || timeChanged) {
			if (mWeatherTimeChangedListener != null) {
				mWeatherTimeChangedListener.onTimeChanged(currentTimeString, !bFromWorker);
			}
		}
		
		if (mWeatherTimeChangedListener != null) {
			mWeatherTimeChangedListener.onTimeRefreshed();
		} else {
			Logger.w(LOG_TAG, "frefreshing time w/o listener");
		}
		Logger.d(LOG_TAG, "[refreshTime] Time tick:%s, is24h:%b, AM/PM:%d",
				currentTimeString, is24h, newAmPm);
	}

	private void runRefreshTimeImediately() {
		runRefreshTime(false); //not from worker
	}
	
	private CharSequence formatDateString(Calendar calendar) {
		CharSequence lowerCase = null;

		if (sHtcDateFormat == null) {
			java.text.DateFormat dateFormat = DateFormat.getDateFormat(mContext);
			Logger.d(LOG_TAG, "[refreshTime] Date format:%s, %s",((SimpleDateFormat) dateFormat).toPattern(), dateFormat.getTimeZone());
			FieldPosition pos = new FieldPosition(
					java.text.DateFormat.DATE_FIELD);
			StringBuffer sb = new StringBuffer();
			dateFormat.format(calendar.getTime(), sb, pos);
			int dateStart = pos.getBeginIndex();
			int dateEnd = pos.getEndIndex();
			// format date and trim year
			pos = new FieldPosition(java.text.DateFormat.YEAR_FIELD);
			sb = new StringBuffer();
			dateFormat.format(calendar.getTime(), sb, pos);
			int nOld = sb.toString().length();
			if (pos.getBeginIndex() > 0 || pos.getEndIndex() > 0) {
				sb.replace(pos.getBeginIndex(), pos.getEndIndex() + 1, "ZBBZ");
			}
			String dateString = sb.toString().replaceFirst(
					"\\s*[,/-]*\\s*ZBBZ\\s*[,/-]*\\s*", "");
			int nNew = dateString.length();
			if (pos.getBeginIndex() < dateStart) {
				dateStart -= nOld - nNew;
				dateEnd -= nOld - nNew;
			}
			// bold date
			if (dateStart < 0) { // no 'd' in format string...
				return dateString;
			}

			lowerCase = dateString;
		} else {
			Logger.d(LOG_TAG, "[refreshTime] sHtcDateFormat: %s", sHtcDateFormat.toString());
			final int dateStart = sHtcDateFormat.indexOf('d'); // DateFormat.DATE
			final int dateEnd = sHtcDateFormat.lastIndexOf('d') + 1; // DateFormat.DATE
			if (dateStart == -1) { // no 'd' in format string...
				return DateFormat.format(sHtcDateFormat, calendar);
			}

			lowerCase = DateFormat.format(sHtcDateFormat.toString(), calendar);
		}
		Logger.d(LOG_TAG, "[refreshTime] lowerCase: %s", lowerCase.toString());
		return lowerCase;
	}

	private String getHolidayString(Calendar calendar, boolean bSupportHoliday) {
		if (!bSupportHoliday) {
			Logger.d(LOG_TAG, "[refreshTime] not support holiday");
			return null;
		}

		HolidayManager manager = HolidayManager.getInstance(mContext);
		if (manager == null) {
			Logger.d(LOG_TAG, "[refreshTime] manager null");
			return null;
		}

		String holiday;
		int yy = calendar.get(Calendar.YEAR);
		int mm = calendar.get(Calendar.MONTH);
		int dd = calendar.get(Calendar.DATE);

		holiday = manager.getHoliday(yy, mm, dd);
		Logger.d(LOG_TAG, "[refreshTime] holiday %s, %s-%s-%s", holiday, yy,
				mm, dd);
		return holiday;
	}

	private void runChangeTimeFormat() {
		Context context = mContext;
		String format = null;
		if (mbIsHep) {
			/*
			 * In fact, even though HTC has its date format style, the following
			 * two formats are the same in current sense60 build, even though
			 * the string is different between
			 * HtcWrapSettings.System.DATE_FORMAT_SHORT and
			 * android.provider.Settings.System.DATE_FORMA " format =
			 * android.provider
			 * .Settings.System.getString(context.getContentResolver(),
			 * HtcWrapSettings.System.DATE_FORMAT_SHORT); format =
			 * android.provider
			 * .Settings.System.getString(context.getContentResolver(),
			 * android.provider.Settings.System.DATE_FORMAT); "
			 */
			try {
				format = android.provider.Settings.System.getString(
						context.getContentResolver(),
						HtcWrapSettings.System.DATE_FORMAT_SHORT);
			} catch (Exception e) {
				Logger.w(LOG_TAG, "err when get DATE_FORMAT_SHORT %s",
						e.getMessage());
			}
			sHtcDateFormat = format;
			Logger.d(LOG_TAG, "[refreshTime] HtcDate format:%s ", format);			
		}
	}
	
    private Context mContext;
	private Handler mWorker = null;
	boolean mbIsHep = true;
	boolean mbSupportHoliday = false;
	
    public WeatherTimeKeeper(Context context, WeatherTimeChangedListener listener, boolean supportHoliday) {
        mContext = context;
        mWeatherTimeChangedListener = listener;
    	// create background handler
		HandlerThread thread = new HandlerThread(Masthead.class.getSimpleName(), Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();
		mWorker = new MessageHandler(thread.getLooper());
		mbIsHep = com.htc.lib0.HDKLib0Util.isHEPDevice(context); 
		mbSupportHoliday = supportHoliday;
		if (!mbIsHep) {
			mFormatChangeObserver = new FormatChangeObserver();
			mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.DATE_FORMAT), true, mFormatChangeObserver);
		}
		
        refreshTimeFormat(false);
    }

    /* package */
    public void destroy() {
    	mUIHandler.removeMessages(MESSAGE_NOTIFY_TIME_SCHEDULE);
    	mUIHandler.removeMessages(MESSAGE_NOTIFY_WEATHER_SCHEDULE);
    	if (mWorker == null) {
    		return;
    	}
    	mWorker.removeMessages(MESSAGE_REFRESH_WEATHER);
    	mWorker.removeMessages(MESSAGE_REFRESH_TIME);
    	mWorker.removeMessages(MESSAGE_CHANGE_TIMEFORMAT);
    	if (mFormatChangeObserver != null) {
    		mContext.getContentResolver().unregisterContentObserver(mFormatChangeObserver);
    	}    	
    	// quit in worker thread to avoid ANR (M7_UL_JB_50 ITS#7408, 8015)
    	final android.os.Looper l = mWorker.getLooper();
		mWorker.post(new Runnable() {
			public void run() {
					l.quit();
			}
		});
    	mWorker = null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(mWorker == null || intent == null) {
            return;
        }

        final long now = android.os.SystemClock.elapsedRealtime();
        final String action = intent.getAction();
		if (action.equals(ACTION_DATE_FORMAT_CHANGE) || action.equals(ACTION_CUSTOMIZATION_CHANGED)) {
			refreshTimeFormat(false);
			refreshDateTime(false);
        } else if (action.equals(Intent.ACTION_TIME_CHANGED) || 
        		action.equals(Intent.ACTION_DATE_CHANGED) ||
        		action.equals(Intent.ACTION_TIMEZONE_CHANGED) ||
        		action.equals(Intent.ACTION_LOCALE_CHANGED)) {
            refreshDateTime(false);
            refreshWeatherData(false);
        } else if (action.equals(ACTION_DELETE_CURRENT_LOCATION) ||
        		action.equals(WeatherConsts.SYNC_SERVICE_RESULT_INTENT_ACTION_NAME) ||
        		(action.equals(WeatherConsts.SETTING_INTENT_ACTION_NAME) && intent.hasCategory(WeatherConsts.SETTING_KEY_TEMPERATURE_UNIT))) {
        	refreshWeatherData(false);
        } else if (action.equals("com.android.sync.SYNC_CONN_STATUS_CHANGED") ||
            action.equals("com.htc.sync.provider.weather.NOTIFY_AUTOSYNC_AGENT") ||
            action.equals("android.location.PROVIDERS_CHANGED")) {
        	refreshWeatherData(false);
        }
        Logger.d(LOG_TAG, "[onReceive] %s took %d", action, android.os.SystemClock.elapsedRealtime() - now);
    }
  
    /* package */
    static IntentFilter getBroadcastFilter(Context context) {
		// listen to time changes
		IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_CHANGED); // including 12/24 hour format change
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        filter.addAction(ACTION_DATE_FORMAT_CHANGE); // date format change
        filter.addAction(ACTION_CUSTOMIZATION_CHANGED);
        filter.addAction(ACTION_DELETE_CURRENT_LOCATION); // current location deletion
        filter.addAction("com.android.sync.SYNC_CONN_STATUS_CHANGED"); // auto sync
        filter.addAction("com.htc.sync.provider.weather.NOTIFY_AUTOSYNC_AGENT"); // auto sync
        filter.addAction("android.location.PROVIDERS_CHANGED"); // location setting
        
        filter.addAction(Intent.ACTION_LOCALE_CHANGED );
        // for weather sync
        filter.addAction(WeatherConsts.SYNC_SERVICE_RESULT_INTENT_ACTION_NAME);
		WeatherRequest req = WeatherRequest.generateWeatherRequestForCurrentLocation();
        filter.addCategory(req.toString());

        // for temperature degree format change
        filter.addAction(WeatherConsts.SETTING_INTENT_ACTION_NAME);
        filter.addCategory(WeatherConsts.SETTING_KEY_TEMPERATURE_UNIT);
        
        return filter;
    }

    public void refreshWeatherData(boolean immediately) {
    	if (!mIsEnableWeather) {
    		Logger.d(LOG_TAG, "skip weather %b, %b, %b, %b", mPauseWeather, !mIsEnableWeather, mWorker==null, immediately);
    		return;
    	}
    	if (!immediately && (mPauseWeather || mWorker == null)) {
    		Logger.d(LOG_TAG, "skip weather %b, %b, %b, %b", mPauseWeather, !mIsEnableWeather, mWorker==null, immediately);
    		if (mPauseWeather) {
    			mWeatherChanged = true;
    		}
    		return;
    	}
    	if (!immediately) {
    		mWorker.removeMessages(MESSAGE_REFRESH_WEATHER);
    		mWorker.sendEmptyMessage(MESSAGE_REFRESH_WEATHER);
    	} else {
    		mWorker.removeMessages(MESSAGE_REFRESH_WEATHER);
    		mWorker.sendMessageAtFrontOfQueue(Message.obtain(mWorker, MESSAGE_REFRESH_WEATHER));
    	}
    }

    public void resumeWeather() {
    	mPauseWeather = false;
        if (mTriggerTimeMillis == -1 || mWeatherChanged ) {
            Logger.d(LOG_TAG, "resumeWeather: trigger time = " + mTriggerTimeMillis + ", weather changed = " + mWeatherChanged);
            refreshWeatherData(false); // error handling to check no weather case
            return;
        }
        if(System.currentTimeMillis() >  mTriggerTimeMillis) {
            Logger.d(LOG_TAG, "resumeWeather: refresh, trigger time = " + mTriggerTimeMillis);
            refreshWeatherData(false);
        } else {
            Logger.d(LOG_TAG, "resumeWeather: newest, trigger time = " + mTriggerTimeMillis);
            notifyWeatherSchedule(mTriggerTimeMillis);
        }
    }

    public void pauseWeather() {
    	Logger.d(LOG_TAG, "pauseWeather");
    	mPauseWeather = true;
    	mUIHandler.removeMessages(MESSAGE_NOTIFY_WEATHER_SCHEDULE);
        if (mWorker != null) {
            mWorker.removeMessages(MESSAGE_REFRESH_WEATHER);
        }
    }
    
    public void refreshDateTime(boolean immediately) {
    	if (!immediately && (mPauseDateTime || mWorker == null)) {
    		Logger.d(LOG_TAG, "skip time %b, %b, %b", mPauseDateTime, mWorker==null, immediately);
    		return;
    	}    
    	Logger.d(LOG_TAG, "refreshDateTime: immediately = "+ immediately + " ,mIsWeatherRunning = "+mIsWeatherRunning);
    	mCurrentTimeMillis = System.currentTimeMillis();
    	if (!immediately || mIsWeatherRunning) {
        	if (mWorker.hasMessages(MESSAGE_REFRESH_TIME)) {
            	Logger.d(LOG_TAG, "msg refresh time exists");
        	} else {
            	Logger.d(LOG_TAG, "will refresh time");
        		mWorker.sendEmptyMessage(MESSAGE_REFRESH_TIME);
        	}
    	} else {
    		runRefreshTimeImediately();
    	}
    }

    private void notifyWeatherSchedule(long triggerTime) {
        if(triggerTime == -1) {
            Logger.w(LOG_TAG, "notifyWeatherSchedule: fail");
            return;
        }
        mUIHandler.removeMessages(MESSAGE_NOTIFY_WEATHER_SCHEDULE);
        mUIHandler.sendEmptyMessageDelayed(MESSAGE_NOTIFY_WEATHER_SCHEDULE,  (triggerTime - System.currentTimeMillis())); // next trigger time
    }

    public void notifyTimeSchedule() {
        //schedule the next one update time stamp
        long now=System.currentTimeMillis();
        long delay=(now/60000+1)*60000-now;

        Logger.d(LOG_TAG, "notifyTimeSchedule: now="+now+" next="+delay);
        mUIHandler.removeMessages(MESSAGE_NOTIFY_TIME_SCHEDULE);
        mUIHandler.sendEmptyMessageDelayed(MESSAGE_NOTIFY_TIME_SCHEDULE, delay);
    }

    public void resumeDateTime() {
    	Logger.d(LOG_TAG, "resumeDateTime");
    	mPauseDateTime = false;
    	refreshDateTime(false);
    }
    
    public void pauseDateTime() {
    	Logger.d(LOG_TAG, "pauseDateTime");
    	mPauseDateTime = true;
    	mUIHandler.removeMessages(MESSAGE_NOTIFY_TIME_SCHEDULE);
		if (mWorker != null) {
			mWorker.removeMessages(MESSAGE_REFRESH_TIME);
		}    	
    }
    
    private void refreshTimeFormat(boolean immediately) {
    	if (mWorker == null) {
    		Logger.d(LOG_TAG, "skip timeformat %b", mWorker==null);
    		return;
    	}       	
    	
    	if (!immediately) {    	    
        	if (mWorker.hasMessages(MESSAGE_CHANGE_TIMEFORMAT)) {
            	Logger.d(LOG_TAG, "msg change timeformat exists");
        	} else {
        		Logger.d(LOG_TAG, "will change time format");
        		mWorker.sendEmptyMessage(MESSAGE_CHANGE_TIMEFORMAT);
        	}
    	} else { // already in worker thread, or there is no worker
    		mWorker.removeMessages(MESSAGE_CHANGE_TIMEFORMAT);
    		mWorker.sendMessageAtFrontOfQueue(Message.obtain(mWorker, MESSAGE_CHANGE_TIMEFORMAT));
    	}
    }
    
    public static class WeatherInfo {
    	public static final int NO_WEATHER = 0;
    	public int mId;
    	public CharSequence mCity;
		public CharSequence mTemperature;
    	public CharSequence mTempSymbol;
    	public CharSequence mText;
    	public CharSequence mText2;
    	public CharSequence mText3;
    	public Drawable mIcon;
    	public boolean mIsLocationEnabled;
    	public boolean mIsAutoSync;
    	public String mWeatherWebLink;
    	
    	public WeatherInfo() {
    		mId = NO_WEATHER;
    		mCity = mText = mText2 = mText3 = mTemperature = mTempSymbol = null;
    		mIcon = null;
    		mIsLocationEnabled = false;
    		mIsAutoSync = true;
    	}
    	
    	@Override
    	public String toString() {
    		return String.format("weather info: city: %s, id: %d, condition: %s, temperature: %s",
    				mCity, mId, mText, mTemperature);
    	}
    }
	
	void enableWeather(boolean bEnable) {
		mIsEnableWeather = bEnable;
	}
	
	public interface WeatherTimeChangedListener {

        public void onDateChanged(CharSequence dateString, CharSequence holidayString);
        
        public void onTimeChanged(CharSequence timeString, boolean immediately);

        public void onTimeOfDayChanged(int amPm, boolean is24H);

        public void onTimeRefreshed();

        public void onWeatherChanged(WeatherInfo weather);
    }

	private FormatChangeObserver mFormatChangeObserver;
	
	private class FormatChangeObserver extends ContentObserver {
		public FormatChangeObserver() {
			super(new Handler());
		}

		@Override
		public void onChange(boolean selfChange) {
			refreshTimeFormat(false);
		}
	}	
	
	private class MessageHandler extends Handler {
				
		public MessageHandler(Looper looper) {
			super(looper);
		}
		
		@Override
		 public void handleMessage(Message msg) {
			if (msg == null) {
				Logger.d(LOG_TAG, "nul msg");
				return;
			}
			int what = msg.what;
			switch(what) {
				case MESSAGE_REFRESH_WEATHER:
					mIsWeatherRunning = true;
					runRefreshWeather();
					mWeatherChanged = false;
					mIsWeatherRunning = false;
					break;
				case MESSAGE_REFRESH_TIME:
					runRefreshTime(true);//from worker
					break;
				case MESSAGE_CHANGE_TIMEFORMAT:
					runChangeTimeFormat();
					break;					
			}
		}
	}

	private Handler mUIHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg == null) {
				Logger.d(LOG_TAG, "nul msg");
				return;
			}
			int what = msg.what;
			switch (what) {
			case MESSAGE_NOTIFY_TIME_SCHEDULE:
				refreshDateTime(false);
				if (mTriggerTimeMillis == -1) {
					Logger.d(LOG_TAG, "time schedule to check weather");
					refreshWeatherData(false); // error handling to check no weather case
				}
				notifyTimeSchedule();
				break;
			case MESSAGE_NOTIFY_WEATHER_SCHEDULE:
				refreshWeatherData(false);
				break;
			}
		}
	};
}
