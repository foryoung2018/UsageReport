package com.htc.lib1.HtcCalendarFramework.util.calendar.holidays;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import com.htc.lib1.HtcCalendarFramework.util.calendar.holidays.HolidayManager.OnDataReadyListener;

/**
 * @author cherry_tsai
 * 20150630 for china public holiday
 * This Util is used to get china public holiday
 * 
 */
public class ChinaHolidayUtil {
	private static final String TAG = "ChinaHolidayUtil";
	private QueryHandler mQueryHandler;
	private ContentResolver mContentResolver;

	private static final Uri CONTENT_URI = Uri.parse("content://com.htc.calendar.holiday/Holiday");

	private static Map<Integer, Integer> mPublicYear = new HashMap<Integer,Integer>();		
	private static Map<String,Boolean> mIsHoliday = new HashMap<String,Boolean>();
	private static Map<String,Boolean> mIsWorkDay = new HashMap<String,Boolean>();

	private static Map<String,String> mPublicHoliday_TW = new HashMap<String,String>();
	private static Map<String, String> mPublicHoliday_CN = new HashMap<String,String>();		
	private static Map<String, String> mWorkDayOnWeekend_TW = new HashMap<String,String>();
	private static Map<String, String> mWorkDayOnWeekend_CN = new HashMap<String,String>();
	
    private static ChinaHolidayUtil singleHolidayInstance = null;    
	private HolidayManager.OnDataReadyListener mOnDataReadyListener;

/**
 * 
 * @return ChinaHolidayUtil Instance
 */
    public static ChinaHolidayUtil getInstance() {
        if ( null == singleHolidayInstance ) {
        	singleHolidayInstance = new ChinaHolidayUtil();
        }   
        return singleHolidayInstance;
    }
    
	/**
	 * MUST call this at first time to prepare holiday data
	 * @param context
	 * @param callback to listen holiday data ready
	 */
    public void initialize(Context context, HolidayManager.OnDataReadyListener callback){
		Log.v(TAG, "initialize china holiday data");
    	cleanHashMap();

		mOnDataReadyListener=callback;
		mContentResolver=context.getContentResolver();
        mQueryHandler = new QueryHandler(mContentResolver);
        queryAllPublicHolidays(context,callback);
    }
    
	/**
	 * Must call this before getting holiday data.
	 * Noted, It's blocking code without callback
	 * @param context
	 */
    public void initialize(Context context){
		Log.v(TAG, "initialize china holiday data");
    	cleanHashMap();
    	queryPublicHolidays(context);
    }
    
    
    //Constructor
    private ChinaHolidayUtil(){
    	cleanHashMap();
    }
    
	static final String COLUMN_HOLIDAY_COLUMN_ID = "_id";
	static final String COLUMN_HOLIDAY_COLUMN_YEAR = "year";
	static final String COLUMN_HOLIDAY_COLUMN_MONTH = "month";
	static final String COLUMN_HOLIDAY_COLUMN_DAY = "day";
	static final String COLUMN_HOLIDAY_COLUMN_DATETEXT = "date_text";
	static final String COLUMN_HOLIDAY_COLUMN_TW_NAME = "tw_name";
	static final String COLUMN_HOLIDAY_COLUMN_CN_NAME = "cn_name";
	static final String COLUMN_HOLIDAY_COLUMN_IS_WORK_DAY = "is_work_day";
	

	private static final String[] PROJECTION = new String[] {
		COLUMN_HOLIDAY_COLUMN_ID, // 0
		COLUMN_HOLIDAY_COLUMN_YEAR, // 1
		COLUMN_HOLIDAY_COLUMN_DATETEXT,// 2
		COLUMN_HOLIDAY_COLUMN_TW_NAME,// 3
		COLUMN_HOLIDAY_COLUMN_CN_NAME, // 4
		COLUMN_HOLIDAY_COLUMN_IS_WORK_DAY //5
	};

	static final int INDEX_HOLIDAY_COLUMN_ID = 0;
	static final int INDEX_HOLIDAY_COLUMN_YEAR = 1;
	static final int INDEX_HOLIDAY_COLUMN_DATETEXT = 2;
	static final int INDEX_HOLIDAY_COLUMN_TW_NAME = 3;
	static final int INDEX_HOLIDAY_COLUMN_CN_NAME = 4;
	static final int INDEX_HOLIDAY_COLUMN_IS_WORK_DAY =5 ;

	
	private static final String[] PROJECTION_YEAR = new String[] {
		"DISTINCT "+ COLUMN_HOLIDAY_COLUMN_YEAR // 0
	};
	static final int INDEX_COLUMN_YEAR = 0;

    public interface OnHolidayDataReadyListener {
        public void onDataReady(boolean hasData);

    }
    
	/**
	 * To check if public holiday of china
	 * @param context
	 * @param year
	 * @param month
	 * @param date
	 * @return true : holiday
	 *  					false : workday
	 */
	public  boolean isHoliday( int year, int month, int date ){
		
		if (mOnDataReadyListener==null){
			Log.d(TAG, "Must call initialize() to get china holiday data");
		}
		
		int real_month=(month+1);
		
//		queryPublicHolidays(context, year, real_month, date);
		
		Calendar cal = Calendar.getInstance();
		cal.set(year,month,date);

        boolean  isWeekend = ( cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||  cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY);  

        String _month= (real_month<10) ?  "0"+Integer.toString(real_month) : Integer.toString(real_month);
        String _date= (date<10) ?  "0"+Integer.toString(date) : Integer.toString(date);

		String query_date = Integer.toString(year)+ _month+ _date;

        boolean isWorkOnWeekend=false;
        boolean isHolidayOnWeekday=false;

        //weekend case
        if (isWeekend){
            isWorkOnWeekend=  (isWeekend && (mIsWorkDay.containsKey(query_date)));        	
            return !isWorkOnWeekend;
        } else {
            // weekday case
             isHolidayOnWeekday=(!isWeekend &&  (mIsHoliday.containsKey(query_date)));     
     		return isHolidayOnWeekday;
        }
	
	}
	
	/**
	 * 
	 * @param context
	 * @param year
	 * @param month
	 * @param date
	 * @return
	 */
	private  boolean isWorkdayOnWeekend(Context context, int year, int month, int date){
		int real_month=(month+1);

		Calendar cal = Calendar.getInstance();
		cal.set(year,month,date);

        boolean  isWeekend = ( cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||  cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY);  

        if (!isWeekend){
        	return false;
        }
        
//		queryPublicHolidays(context, year, real_month, date);
        
        String _month= (real_month<10) ?  "0"+Integer.toString(real_month) : Integer.toString(real_month);
        String _date= (date<10) ?  "0"+Integer.toString(date) : Integer.toString(date);

		String query_date = Integer.toString(year)+ _month+ _date;

        //weekend case
        if (isWeekend&& (mIsWorkDay.containsKey(query_date))){
            return true;
        } 
        
        return false;
	}
	
	private  boolean isCNLocal(){
    	Locale locale = Locale.getDefault();   	
		String systemLanguage = locale.getLanguage();
    	String country = locale.getCountry();
		if(systemLanguage.equals("zh") && country.equals("CN")){
				return true;
		} else {
			return false;
		}
	}
	
	private void doQuery(Context context, HolidayManager.OnDataReadyListener callback){

		if (mQueryHandler== null){
			Log.w(TAG,"mQueryHandler is null");
			return;
		}
		mQueryHandler.startQuery(0, callback, CONTENT_URI, PROJECTION, null, null,	null);
	}
	
	private  void queryAllPublicHolidays(Context context, HolidayManager.OnDataReadyListener callback) {
		doQuery(context, callback);
	}
	
	
	public  String getHoliday( int year, int month, int date) {
	
		if (mOnDataReadyListener==null){
			Log.d(TAG, "Must call initialize() before getting china holiday data");
		}
		
		int real_month=(month+1);

//		queryPublicHolidays(context, year, real_month, date);
		
        String _month= (real_month<10) ?  "0"+Integer.toString(real_month) : Integer.toString(real_month);
        String _date= (date<10) ?  "0"+Integer.toString(date) : Integer.toString(date);

		String query_date = Integer.toString(year)+ _month+ _date;
		Log.v(TAG,"query_date is :"+query_date);

		if (isCNLocal()){
			if (mPublicHoliday_CN.containsKey(query_date))
				return mPublicHoliday_CN.get(query_date);
			
		} else {
			if (mPublicHoliday_TW.containsKey(query_date))
				return mPublicHoliday_TW.get(query_date);
		}
		
		return "";
	}
	
	
	public String getWorkdayString( int year, int month, int date){
		if (mOnDataReadyListener==null){
			Log.d(TAG, "Must call initialize() before getting china holiday data");
		}
		
		int real_month=(month+1);
		
        String _month= (real_month<10) ?  "0"+Integer.toString(real_month) : Integer.toString(real_month);
        String _date= (date<10) ?  "0"+Integer.toString(date) : Integer.toString(date);

		String query_date = Integer.toString(year)+ _month+ _date;
		Log.v(TAG,"query_date is :"+query_date);

		if (isCNLocal()){
			if (mWorkDayOnWeekend_CN.containsKey(query_date))
				return mWorkDayOnWeekend_CN.get(query_date);
			
		} else {
			if (mWorkDayOnWeekend_TW.containsKey(query_date))
				return mWorkDayOnWeekend_TW.get(query_date);
		}
		
		return "";
	}
	
	private  void putHoliday(boolean isWorkDay, String date, String cn_name, String tw_name){

		if (isWorkDay) {
			mIsWorkDay.put(date, true);
		} else {
			mIsHoliday.put(date, true);
		}

		if(isCNLocal()){
			if (isWorkDay){
				mWorkDayOnWeekend_CN.put(date,cn_name);
			} else {
				mPublicHoliday_CN.put(date,cn_name);
			}
		} else {
			if (isWorkDay){
				mWorkDayOnWeekend_TW.put(date,tw_name);
			} else {
				mPublicHoliday_TW.put(date,tw_name);
			}
		}
	}
	
	private void cleanHashMap(){
	 	// reset 
	 	if (mWorkDayOnWeekend_CN!=null){
	 		mWorkDayOnWeekend_CN.clear();
	 	}
 		if (mWorkDayOnWeekend_TW!=null){
 			mWorkDayOnWeekend_TW.clear();
 		}
	 	if (mPublicHoliday_TW!=null){
	 		mPublicHoliday_TW.clear();
	 	}
 		if (mPublicHoliday_CN!=null){
 			mPublicHoliday_CN.clear();
 		}
 		
	 	if (mIsHoliday!=null){
	 		mIsHoliday.clear();
	 	}
	 	if (mIsWorkDay!=null){
	 		mIsWorkDay.clear();
	 	}
	 	if (mPublicYear!=null){
	 		mPublicYear.clear();
	 	}
	}
	
	
	private class QueryHandler extends AsyncQueryHandler {
		public QueryHandler(ContentResolver cr) {
			super(cr);
		}

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        	HolidayManager.OnDataReadyListener callback= (HolidayManager.OnDataReadyListener)cookie;

    		try {

    			if (cursor != null && cursor.moveToFirst()) {
    				 do {
    						String _year = cursor.getString(INDEX_HOLIDAY_COLUMN_YEAR);
    						String _date = cursor.getString(INDEX_HOLIDAY_COLUMN_DATETEXT);
    						String tw_name = cursor.getString(INDEX_HOLIDAY_COLUMN_TW_NAME);
    						String cn_name = cursor.getString(INDEX_HOLIDAY_COLUMN_CN_NAME);
    						boolean isWorkDay = (cursor.getInt(INDEX_HOLIDAY_COLUMN_IS_WORK_DAY)== 0 ? false : true);
    						Log.v(TAG, "putHoliday year:" +_year+" date:" + _date + " tw_name: " + tw_name + " is_workday:"+ isWorkDay);
    						
    						if (!mPublicYear.containsKey(_year)){
    			    			mPublicYear.put(Integer.parseInt(_year), Integer.parseInt(_year));
    						}
    						
    						putHoliday(isWorkDay,_date,cn_name,tw_name);    						
    				 } while (cursor.moveToNext());

    	    			if (callback!=null)
    	    				callback.onDataReady(true);
    				 
    			} else {
    				Log.v(TAG, "No holiday found.");
	    			if (callback!=null)
	    				callback.onDataReady(false);
    			}
    			
    		
    		} catch (Exception e) {
    			Log.e(TAG, e.getMessage());
    			if (callback!=null)
    				callback.onDataReady(false);
    			
    		} finally {
    			if (cursor != null) {
    				if (!cursor.isClosed()) {
    					cursor.close();
    				}
    				cursor = null;
    			}
    		}
        }
    }
	
	private  void queryPublicHolidays(Context context) {
	
	if (mPublicYear!=null && mPublicYear.size()>0)
		return ;

//	String where = " year = " + year;
	Cursor cursor = null;
	try {
		ContentResolver cr = context.getContentResolver();
		cursor = cr.query(CONTENT_URI, PROJECTION, null, null, null /* sort order */);
		
		if (cursor != null && cursor.moveToFirst()) {
			
			 do {
					String _year = cursor.getString(INDEX_HOLIDAY_COLUMN_YEAR);
					String _date = cursor.getString(INDEX_HOLIDAY_COLUMN_DATETEXT);
					String tw_name = cursor.getString(INDEX_HOLIDAY_COLUMN_TW_NAME);
					String cn_name = cursor.getString(INDEX_HOLIDAY_COLUMN_CN_NAME);
					boolean isWorkDay = (cursor.getInt(INDEX_HOLIDAY_COLUMN_IS_WORK_DAY)== 0 ? false : true);
					Log.v(TAG, "putHoliday year:" +_year+" date:" + _date + " tw_name: " + tw_name + " is_workday:"+ isWorkDay);
					
					putHoliday(isWorkDay,_date,cn_name,tw_name);
					
					if (!mPublicYear.containsKey(_year)){
						mPublicYear.put( Integer.valueOf(_year),  Integer.valueOf(_year));
					}
					
			 } while (cursor.moveToNext());

		} else {
			Log.v(TAG, "No holiday found.");
		}
		

	} catch (Exception e) {
		Log.e(TAG, e.getMessage());
	} finally {
		if (cursor != null) {
			if (!cursor.isClosed()) {
				cursor.close();
			}
			cursor = null;
		}
	}
}
}