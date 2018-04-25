package com.htc.lib1.HtcCalendarFramework.util.calendar.holidays;

import android.content.res.Resources;
import android.text.TextUtils;
import android.content.Context;
import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

// TODO
// The CnYearCodes needs a limit, may need to throw an exception if outside the limit
// current it's not handled
/**
  * Lunar Utilitys class
  */
public class LunarUtils {
	private static final int LUNARYEAR = -1;
	private static final int LUNARMONTH = -2;
	private static final int LUNARDATE = -3;
	private static final String LOG_TAG = "CnDate";
	
	/**
	  * Define the key of the solar term
	  */
	public static final String KEY_SOLAR_TERM ="SolarTerm";

    /**
      * Define the key of the lunar day
      */
    public static final String KEY_LUNAR_DAY ="LunarDay";

    /**
      * Define the key of the solar holiday
      */
    public static final String KEY_SOLAR_HOLIDAY ="SolarHoliday";

    /**
      * Define the key of the lunar holiday
      */
    public static final String KEY_LUNAR_HOLIDAY ="LunarHoliday";

        /**
          * Define the key of the special holiday
          */
	public static final String KEY_SPECIAL_HOLIDAY = "SpecialHoliday";
	
    /**
     * 20150610 new feature
     * Define the key of the public holiday of China
     */
public static final String KEY_PUBLIC_HOLIDAY = "PublicHoliday";

/**
 * 20150610 new feature
 * Define the key of the workday string of China
 */
public static final String KEY_WORKDAY_STRING = "WorkdayString";
	
	// The exception table of solar terms (2000~2030)
	// {year,the index of 24 solar terms(0~23),offset}
	private static final int[][] exception={{2011,11, 1},{2015,11, 1},{2000,17,1},{2001,13,1},{2002,14,1},{2003,18,1},{2004,14,1},{2004,17,1}
	,{2005,13,1},{2007,18,1},{2008,9,1},{2008,12,1},{2008,15,1},{2009,13,1},{2010,16,1},{2012,12,1},{2012,15,1},{2014,16,1}
	,{2016,12,1},{2016,15,1},{2017,2,-1},{2018,16,1},{2021,23,-1},{2022,10,1},{2023,19,1},{2026,3,-1},{2027,14,1},{2029,17,1}};	//2011 Solar Terms:12 offset:+1 and 2015 Solar Terms:12 offset:+1
	
	private static final int[] mCnYearCodes = {
			0x04bd8, 0x04ae0, 0x0a570, 0x054d5, 0x0d260, // 1904
			0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2, // 1909
			0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, // 1914
			0x0b540, 0x0d6a0, 0x0ada2, 0x095b0, 0x14977, // 1919
			0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40, // 1924
			0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970, // 1929
			0x06566, 0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, // 1934
			0x02b60, 0x186e3, 0x092e0, 0x1c8d7, 0x0c950, // 1939
			0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, // 1944
			0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557, // 1949
			0x06ca0, 0x0b550, 0x15355, 0x04da0, 0x0a5d0, // 1954
			0x14573, 0x052d0, 0x0a9a8, 0x0e950, 0x06aa0, // 1959
			0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, // 1964
			0x05260, 0x0f263, 0x0d950, 0x05b57, 0x056a0, // 1969
			0x096d0, 0x04dd5, 0x04ad0, 0x0a4d0, 0x0d4d4, // 1974
			0x0d250, 0x0d558, 0x0b540, 0x0b5a0, 0x195a6, // 1979
			0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, // 1984
			0x06a50, 0x06d40, 0x0af46, 0x0ab60, 0x09570, // 1989
			0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50, // 1994
			0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0, // 1999
			0x0c960, 0x0d954, 0x0d4a0, 0x0da50, 0x07552, // 2004
			0x056a0, 0x0abb7, 0x025d0, 0x092d0, 0x0cab5, // 2009
			0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, // 2014
			0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930, // 2019
			0x07954, 0x06aa0, 0x0ad50, 0x05b52, 0x04b60, // 2024
			0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65, 0x0d530, // 2029
			0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0, // 2034
			0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520, 0x0dd45, // 2039
			0x0b5a0, 0x056d0, 0x055b2, 0x049b0, 0x0a577, // 2044
			0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0, // 2049
			0x14b63, 0x09370, 0x049f8, 0x04970, 0x064b0, // 2054
			0x168a6, 0x0ea50, 0x06aa0, 0x1a6c4, 0x0aae0, // 2059
			0x092e0, 0x0d2e3, 0x0c960, 0x0d557, 0x0d4a0, // 2064
			0x0da50, 0x05d55, 0x056a0, 0x0a6d0, 0x055d4, // 2069
			0x052d0, 0x0a9b8, 0x0a950, 0x0b4a0, 0x0b6a6, // 2074
			0x0ad50, 0x055a0, 0x0aba4, 0x0a5b0, 0x052b0, // 2079
			0x0b273, 0x06930, 0x07337, 0x06aa0, 0x0ad50, // 2084
			0x14b55, 0x04b60, 0x0a570, 0x054e4, 0x0d160, // 2089
			0x0e968, 0x0d520, 0x0daa0, 0x16aa6, 0x056d0, // 2094
			0x04ae0, 0x0a9d4, 0x0a2d0, 0x0d150, 0x0f252  // 2099
	};
	
	private static final int MAX_YEAR = 2099;
	private static final int MIN_YEAR = 1900;
	// The upper bound is Jan 31, 2008
	private static final Calendar DATE_MAX_BOUND_OBJ = 
		new GregorianCalendar(2100, 0, 31, 0, 0, 0);
	// The lower bound is Mar 1, 1900
	private static final Calendar DATE_MIN_BOUND_OBJ = 
		new GregorianCalendar(1900, 2, 1, 0, 0, 0);
	
	private static String[] mCnMonths;
	
	private static String[] mCnDays ;
	
	private static String[] mCnDaysSmall ;
	
	private static String[] mCnGans;
	
	private static String[] mCnZhis;
	
	private static String[] mCnZodiacs;	
	
	private static String[] mSolarTerms;
	
	private static String[] SolarHolidays;
	private static String[] SolarHolidays_date;
	
	private static Map<String, String> mSolarHolidays = new HashMap<String,String>();		
	
	private static String[] LunarHolidays;
	private static String[] LunarHolidays_date;
	private static Map<String,String> mLunarHolidays = new HashMap<String,String>() ;

	private static final int[] mSolarDaysInMonth = {
		31,28,31,30,31,30,31,31,30,31,30,31
	};
	
	private static Map<Integer, Integer> mYear = new HashMap<Integer,Integer>();		
	private static Map<String, String> mSpecailHoliday = new HashMap<String,String>();		

	
	// store the lunar days of every month
	private int[] mLunarDaysInMonth = new int[13];
	// store 节气 information of this year
	// every item is the 节气 day offsets from the BaseDate
	private long[] mSolarTermsThisYear = new long[24];
	
	private static final int GANZHI_BASE_YEAR = 1864; // 1864年是农历癸亥年
	// 1900年1月31日星期三（庚子年正月初一壬寅日）
	private static final int BASE_YEAR = 1900;
	private static final GregorianCalendar mBaseDate = new GregorianCalendar(1900, 0, 31, 0, 0);
	
	// The Offsets of first lunar day at some years from the BaseDate
	private static final int[] mCnBaseDateOffsets = {
		0,384,738,1093,1476,1830,2185,2569,2923,3278, // 1900-1909
		3662,4016,4400,4754,5108,5492,5846,6201,6585,6940,
		7324,7678,8032,8416,8770,9124,9509,9863,10218,10602,
		10956,11339,11693,12048,12432,12787,13141,13525,13879,14263,
		14617,14971,15355,15710,16064,16449,16803,17157,17541,17895,
		18279,18633,18988,19372,19726,20081,20465,20819,21202,21557,
		21911,22295,22650,23004,23388,23743,24096,24480,24835,25219,
		25573,25928,26312,26666,27020,27404,27758,28142,28496,28851,
		29235,29590,29944,30328,30682,31066,31420,31774,32158,32513,
		32868,33252,33606,33960,34343,34698,35082,35436,35791,36175,
		36529,36883,37267,37621,37976,38360,38714,39099,39453,39807,
		40191,40545,40899,41283,41638,42022,42376,42731,43115,43469,
		43823,44207,44561,44916,45300,45654,46038,46392,46746,47130,
		47485,47839,48223,48578,48962,49316,49670,50054,50408,50762,
		51146,51501,51856,52240,52594,52978,53332,53686,54070,54424,
		54779,55163,55518,55902,56256,56610,56993,57348,57702,58086,
		58441,58795,59179,59533,59917,60271,60626,61010,61364,61719,
		62103,62457,62841,63195,63549,63933,64288,64642,65026,65381,
		65735,66119,66473,66857,67211,67566,67950,68304,68659,69042,
		69396,69780,70134,70489,70873,71228,71582,71966,72320,72674, // 2090-2099
		73058 // 2100
	};
	
	private GregorianCalendar mCurrentDate;
	private long mCurrentDateOffset; // date offset to the base date
	private int mYearCache = -1; // year cache
	private int mLunarYearCache = -1; // lunar year cache
	
	private int mLunarYear;
	private int mLunarMonth; // mLunarMonth is 0-based
	private int mLunarDate;
	private boolean mLunarLeapMonth; // whether it's a lunar leap month
	private static final long MILLISECS_PER_DAY = 24L * 60L * 60L * 1000L;
	
	// whether it's a special lunar day, should be used after calling getLunarDate() 
	private boolean mLunarSpecialDay; 
		
	private String mLeapTitle ="";
	/**
	 * CnDate default constructor
	 * Set the time to 00:00:00(HH:MM:SS)
	 */
	private LunarUtils() {
		mCurrentDate = new GregorianCalendar();
		mCurrentDate.set(Calendar.HOUR, 0);
		mCurrentDate.set(Calendar.MINUTE, 0);
		mCurrentDate.set(Calendar.SECOND, 0);
				
		solarToLunar();
	}
	
    private static LunarUtils singleLunar = null;    

    /**
      * To get the instance of the LunarUtils
      * @param res the Resources
      * @param forceUpdate force to update instance
      * @return the instance of the LunarUtils
      * @deprecated [Module internal use]
      */
    /**@hide*/ 
    public static LunarUtils getInstance(Context context, boolean forceUpdate) {
        if ( forceUpdate || null == singleLunar ) {
        	singleLunar = new LunarUtils(context);
        }   
        return singleLunar;
    }
    
    /**
     * To get the instance of the LunarUtils
     * @param res the Resources
     * @return the instance of the LunarUtils
     * @deprecated [Module internal use]
     */
   /**@hide*/ 
   public static LunarUtils getInstance(Context context) {
       if ( null == singleLunar ) {
       		singleLunar = new LunarUtils(context);
       }   
       return singleLunar;
   }
   
    private LunarUtils(Context context) {
    	Resources res = context.getResources();
		mCurrentDate = new GregorianCalendar();
		mCurrentDate.set(Calendar.HOUR, 0);
		mCurrentDate.set(Calendar.MINUTE, 0);
		mCurrentDate.set(Calendar.SECOND, 0);
		mCnMonths = res.getStringArray( com.htc.lib1.HtcCalendarFramework.R.array.htc_mCnMonths);	
		mCnDays = res.getStringArray( com.htc.lib1.HtcCalendarFramework.R.array.htc_mCnDays);
		mCnDaysSmall = res.getStringArray( com.htc.lib1.HtcCalendarFramework.R.array.htc_mCnDaysSmall);
		mCnGans = res.getStringArray( com.htc.lib1.HtcCalendarFramework.R.array.htc_mCnGans);
		mCnZhis = res.getStringArray( com.htc.lib1.HtcCalendarFramework.R.array.htc_mCnZhis);
		mCnZodiacs = res.getStringArray( com.htc.lib1.HtcCalendarFramework.R.array.htc_mCnZodiacs);
		mSolarTerms = res.getStringArray( com.htc.lib1.HtcCalendarFramework.R.array.htc_mSolarTerms);
	 	SolarHolidays = res.getStringArray( com.htc.lib1.HtcCalendarFramework.R.array.htc_SolarHolidays);
	 	SolarHolidays_date = res.getStringArray( com.htc.lib1.HtcCalendarFramework.R.array.htc_SolarHolidays_date);
	 	
	 	cleanHashMap();
	 	
	 	int length = SolarHolidays.length;
	 	for(int i = 0 ; i < length ; i++){
	 		mSolarHolidays.put(SolarHolidays_date[i],SolarHolidays[i]);
	 	}
	
	 	LunarHolidays = res.getStringArray( com.htc.lib1.HtcCalendarFramework.R.array.htc_LunarHolidays);
	 	LunarHolidays_date = res.getStringArray( com.htc.lib1.HtcCalendarFramework.R.array.htc_LunarHolidays_date);
	 	length = LunarHolidays_date.length;
	 	for(int i = 0 ; i < length ; i++){
	 		mLunarHolidays.put(LunarHolidays_date[i],LunarHolidays[i]);
	 	}
	 	
		solarToLunar();
	}
	/**
	 * CnDate constructor, Set the time to 00:00:00(HH:MM:SS)
	 * @param year int The value used to set the YEAR time field.
	 * @param month int The value used to set the MONTH time field. Month value is 0-based. e.g., 0 for January.
	 * @param day int The value used to set the DATE time field.
	 */
    private LunarUtils(int year, int month, int date) {
		mCurrentDate = new GregorianCalendar(year, month, date, 0, 0, 0);
		
		solarToLunar();
	}

	/**
	 * constructor from a Calendar object
	 * @param c1 Calendar The Calendar data to set
	 */
	private LunarUtils(Calendar c1){
		mCurrentDate = new GregorianCalendar(c1.get(Calendar.YEAR), 
				c1.get(Calendar.MONTH), 
				c1.get(Calendar.DATE), 0, 0, 0);
		
		solarToLunar();
	}
	
	/**
	 * constructor from a CnDate object
	 * @param c1
	 */
	private LunarUtils(LunarUtils c1){
		mCurrentDate = new GregorianCalendar(c1.get(Calendar.YEAR), 
				c1.get(Calendar.MONTH), 
				c1.get(Calendar.DATE), 0, 0, 0);
		
		solarToLunar();
	}
	
	/**
	 * Adds the specified (signed) amount of time to the given time field.
	 * hour, minute and second values are filtered out.
	 * @param field
	 * @param value
	 */
	private void add(int field, int value) {
		switch(field) {
		case Calendar.HOUR:
		case Calendar.MINUTE:
		case Calendar.SECOND: return;
		default: mCurrentDate.add(field, value); 
		}

		if(Calendar.DATE == field) {
			int aNewDate = mLunarDate + value;
			int theDaysInThisMonth = getDaysInCurrentLunarMonth();
			
			// If it's still in the same lunarMonth, don't go into solarToLunar()
			if(aNewDate > 0 && aNewDate <= theDaysInThisMonth) {
				mLunarDate = aNewDate;
				mCurrentDateOffset += value;
			} else
				solarToLunar();				
		}
		else
			solarToLunar();
	}
	
	/**
	 * Sets the time field with the given value, HH:MM:SS is not allowed to change
	 * @param field the given time field (conform to Calendar class)
	 * @param value the value to be set for the given time field (conform to Calendar class)
	 */
	private void set(int field, int value) {
		switch(field) {
		case Calendar.HOUR:
		case Calendar.MINUTE:
		case Calendar.SECOND: return;
		default: mCurrentDate.set(field, value); 
		}

		solarToLunar();
	}
	
	/**
	 * Set the Calendar field
	 * @param year int The value used to set the YEAR time field.
	 * @param month int The value used to set the MONTH time field. Month value is 0-based. e.g., 0 for January.
	 * @param date int The value used to set the DATE time field.
	 */
	protected void set(int year, int month, int date){
		mCurrentDate.set(year, month, date, 0, 0, 0);

		solarToLunar();
		// calculate special day: Mother's day, Father's day
		if (!mYear.containsKey(year))
		{
			cacularSpecialDay(mCurrentDate);
			mYear.put(year,year);
		}
		
	}
	
	/**
	 * Set the Calendar field using another Calendar object
	 * @param c1
	 */
	private void set(Calendar c1){
		mCurrentDate.set(c1.get(Calendar.YEAR), 
				c1.get(Calendar.MONTH), 
				c1.get(Calendar.DATE), 0, 0, 0);
		
		solarToLunar();
	}
	
	/**
	 * Set the Calendar field using another CnDate object
	 * @param c1
	 */
	private void set(LunarUtils c1){
		mCurrentDate.set(c1.get(Calendar.YEAR), 
				c1.get(Calendar.MONTH), 
				c1.get(Calendar.DATE), 0, 0, 0);
		
		solarToLunar();
	}
	
	// error codes for the return value of setLunarDate
	private static final String[] mErrArray = {
			"Out of Year Bounds",
			"Incorrect Month Value",
			"Not A Leap Month",
			"Incorrect Date Value",
			"Out of Date Bounds"			
			};
	
	/**
	 * set the current date to a lunar date
	 * @param year lunar year
	 * @param month lunar month, 0-based
	 * @param date lunar date
	 * @param leap whether it's a leap month
	 * @return -1 if no error, else return a value >=0
	 */
	private int setLunarDate(int year, int month, int date, boolean leap) {
		if(year < MIN_YEAR || year > MAX_YEAR)
			return 0;
		if(month < 0 || month > 11)
			return 1;
		if(leap && (lunarLeapMonth(year) != (month + 1)))
			return 2;
		if(date < 1 || date > 30)
			return 3;
		
		Calendar aBaseCalDate = Calendar.getInstance();
		int index = year - 1900;
		aBaseCalDate.setTimeInMillis(mBaseDate.getTimeInMillis());
		aBaseCalDate.add(Calendar.DATE, mCnBaseDateOffsets[index]);
		
		int[] theLunarDaysInMonth = new int[13];
		calcLunarDaysInMonthHelper(year, theLunarDaysInMonth);
		for(index=0; index<month; index++){
			aBaseCalDate.add(Calendar.DATE, theLunarDaysInMonth[index]);
		}
		if(leap || 
				(lunarLeapMonth(year) > 0 && lunarLeapMonth(year) <= month))
			aBaseCalDate.add(Calendar.DATE, theLunarDaysInMonth[index++]);
		if(date > theLunarDaysInMonth[index])
			return 3;
		else
			aBaseCalDate.add(Calendar.DATE, date - 1);
		
		if(aBaseCalDate.after(DATE_MAX_BOUND_OBJ) ||
				aBaseCalDate.before(DATE_MIN_BOUND_OBJ))
			return 4;
		
		mCurrentDate.setTimeInMillis(aBaseCalDate.getTimeInMillis());
		mLunarYear = year;
		mLunarMonth = month;
		mLunarDate = date;
		if(leap)
			mLunarLeapMonth = true;
		else
			mLunarLeapMonth = false;
		
		if(mLunarYear != mLunarYearCache) {
			mLunarDaysInMonth = theLunarDaysInMonth;
			mLunarYearCache = mLunarYear;
		}
		calcSolarTerms();
		
		mCurrentDateOffset = solarDaysFromBaseDate(); // re-calculate the dateoffset variable
		
		return -1;
	}
	
	/**
	 * Gets the value for a given time field
	 * @param field the given time field
	 * @return the value for the given time field
	 */
	private int get(int field) {
		switch(field) {
		case LunarUtils.LUNARYEAR:
			return mLunarYear;
		case LunarUtils.LUNARMONTH:
			return mLunarMonth;
		case LunarUtils.LUNARDATE:
			return mLunarDate;
		default: 
			return mCurrentDate.get(field);
		}
	}
	
	/**
	  * Convert the current date to the string
	  * @return the current date string
	  * @deprecated [Module internal use]
	  */
	/**@hide*/ 
	public String toString() {
		return mCurrentDate.getTime().toString();
	}
	
	/**
	 * Determine if it is a solar leap year
	 * @return true/false
	 */
	private boolean isSolarLeapYear() {
		int year = mCurrentDate.get(Calendar.YEAR);
		return (year%4==0 && year%100!=0) || year%400==0;
	}
	
	/**
	 * Return the total days in the current month
	 * @return
	 */
	private int solarDaysInMonth() {
		if(isSolarLeapYear() && (mCurrentDate.get(Calendar.MONTH)==1))
			return 29;
		else
			return mSolarDaysInMonth[mCurrentDate.get(Calendar.MONTH)];
	}
	
	/**
	 * Return the offset days from base date (01/31/1900)
	 * @return
	 */
	private long solarDaysFromBaseDate() {
		long endl=mCurrentDate.getTimeInMillis() + mCurrentDate.getTimeZone().getOffset(mCurrentDate.getTimeInMillis());
		long startl=mBaseDate.getTimeInMillis() + mBaseDate.getTimeZone().getOffset(mBaseDate.getTimeInMillis());
		return (endl-startl)/MILLISECS_PER_DAY;
	}
	
	/**
	 * 
	 * @return the offset days from a given calendar date
	 */
	private long solarDaysFromDate(Calendar cc) {
		long endl=mCurrentDate.getTimeInMillis() + mCurrentDate.getTimeZone().getOffset(mCurrentDate.getTimeInMillis());
		long startl=cc.getTimeInMillis() + cc.getTimeZone().getOffset(cc.getTimeInMillis());
		return (endl-startl)/MILLISECS_PER_DAY;
	}

	/**
	 * translate solar date to lunar date
	 * should be called in the constructor file
	 */
	
	private void solarToLunar(){
		
		long offset = solarDaysFromBaseDate();
		mCurrentDateOffset = offset;
		
/*		for(int iYear=0; iYear<mCnYearCodes.length; iYear++) {
			iDaysInYear = CnDate.lunarYearDays(1900+iYear);
			
			if(offset < iDaysInYear) {
				mLunarYear = iYear + 1900;
				break;
			}
			offset -= iDaysInYear;
		}*/
		
		int index = mCurrentDate.get(Calendar.YEAR) - 1900;
		if(mCnBaseDateOffsets[index] > offset) {
			offset -= mCnBaseDateOffsets[index - 1];
			mLunarYear = 1900 + (index-1);
		}
		else {
			offset -= mCnBaseDateOffsets[index];
			mLunarYear = 1900 + index;
		}
		
		calcLunarDaysInMonth();
		calcSolarTerms();
		
		for(int iMonth=0; iMonth<13; iMonth++) {
			if (offset < mLunarDaysInMonth[iMonth]) {
				mLunarMonth = iMonth;
				break;
			}
			offset -= mLunarDaysInMonth[iMonth];
		}
				
		if(lunarLeapMonth(mLunarYear)>0) {
			// determine whether it's a leap month
			if(mLunarMonth == lunarLeapMonth(mLunarYear))
				mLunarLeapMonth = true;
			else
				mLunarLeapMonth = false;
			
			// if it passes a leap month, the month should subtract 1
			if(mLunarMonth >= lunarLeapMonth(mLunarYear))
				mLunarMonth -= 1;
		}	
		else
			mLunarLeapMonth = false;
				
		// It's because the base date is 初一, not 初零
		mLunarDate = (int) offset+1;
	}
	
	/**
	 * Return which month is lunar leap month;
	 * @return 0 if no lunar leap month
	 */
	private static int lunarLeapMonth(int year) {
		return mCnYearCodes[year - BASE_YEAR] & 0xf;
	}
	
	/**
	 * Calculate days of lunar month in a given year
	 * make it static
	 */
	private void calcLunarDaysInMonth() {		
		if(mLunarYearCache == mLunarYear)
			return;
		else
			mLunarYearCache = mLunarYear;
		
		calcLunarDaysInMonthHelper(mLunarYear, mLunarDaysInMonth);
	}
	
	/**
	 * helper function for calculation of lunar days in months
	 * @param year 
	 * @param daysinmonth int[13] arrays
	 */
	private static void calcLunarDaysInMonthHelper(int year, int[] daysinmonth) {
		
		int[] aLunarMonthDay = {29,30};
		int code = mCnYearCodes[year - BASE_YEAR];
		
		code >>= 4;
		for(int iMonth=0; iMonth<12; iMonth++) {
			daysinmonth[11-iMonth]=aLunarMonthDay[code&0x1];
			code >>= 1;
		}
		
		if(lunarLeapMonth(year) > 0) {
			int lm=lunarLeapMonth(year);
			for(int iMonth=12; iMonth>0; iMonth--) {
				if(iMonth > lm)
					daysinmonth[iMonth] = daysinmonth[iMonth-1];
				else {
					daysinmonth[iMonth] = aLunarMonthDay[code&0x1];
					break;
				}
			}
		}
	}

	/**
	 * Calculate the 节气 array for this year
	 */
	private void calcSolarTerms() {
	/* 把当天和1900年1月0日（星期日）的差称为积日，
	 * 那么第y年（1900年算第0年）第x 个节气的积日是
	 * 　F = 365.242 * y + 6.2 + 15.22 * x - 1.9 * sin(0.262 * x)
　　 * 这个公式的误差在0.05天左右。
 	 * Since the base date chosen in this program is 1900年1月31日
 	 * The formula should change to
 	 *   F = 365.242 * y + 6.2 + 15.22 * x - 1.9 * sin(0.262 * x) - 31
 	 *     = 365.242 * y - 24.8 + 15.22 * x - 1.9 * sin(0.262 * x) 
	*/	
		if(mYearCache == mCurrentDate.get(Calendar.YEAR))
			return;
		else
			mYearCache = mCurrentDate.get(Calendar.YEAR);
			
		double f=0;
		for(int i=0; i<24; i++){
			f = 365.242 * (mCurrentDate.get(Calendar.YEAR) - BASE_YEAR) - 24.8 + 15.22 * i - 1.9 * Math.sin(0.262 * i);
			mSolarTermsThisYear[i] = correct( (long) f, mCurrentDate.get(Calendar.YEAR), i);
		}
	}
    private long correct( long offset, int year, int season) {
    	for(int i=0;i<Array.getLength(exception);i++){
    	    if(year==exception[i][0]&&season==exception[i][1])
    	       return offset=offset+exception[i][2];
    	}
    	return offset;
    }
	/**
	 * A static function to return the total days of a given lunar year.
	 * @return total days
	 */
    private static int lunarYearDays(int year) {
		int totaldays=0;
		int[] aLunarMonthDay = {29,30};
		int code = mCnYearCodes[year - BASE_YEAR];

		code >>= 4;
		for(int i=0; i<12; i++) {
			totaldays += aLunarMonthDay[code&0x1];
			code >>= 1;
		}
		
		if(lunarLeapMonth(year)>0)
			totaldays += aLunarMonthDay[code&0x1];
		return totaldays;
	}
	
	/**
	 * return lunar month names in Strings
	 */
	private String getLunarMonth() {
		if(mLunarLeapMonth)
			return getLeapTitle()+mCnMonths[mLunarMonth];
		else
			return mCnMonths[mLunarMonth];
	}
	
	/**
	 * return lunar month names in big5 Strings
	 * @return lunar plain date string.
	 */
	private final String getLunarPlainDate() {
		return mCnDays[mLunarDate-1];
	}
	
	/**
	 * return the lunar date strings, if it's a special day, use the holiday text
	 * @return the lunar date string
	 */
	protected String getLunarDate() {

		// check lunar holiday, skip if it's a lunar leap month
		if(!mLunarLeapMonth) {
			String lmon = (mLunarMonth<9) ?
					"0"+Integer.toString(mLunarMonth+1) : Integer.toString(mLunarMonth+1);
			String lday = (mLunarDate<10) ?
					"0"+Integer.toString(mLunarDate) : Integer.toString(mLunarDate);
			if(mLunarHolidays.containsKey(lmon+lday)) {
					mLunarSpecialDay = true;		
					return mLunarHolidays.get(lmon+lday);
			}
		}
		
		// check if it's the last day of this year
		if(mLunarMonth==11 && mLunarDate==getDaysInCurrentLunarMonth()) {
			mLunarSpecialDay = true;		
			return "除夕";
		}
		
		// check 节气
		for(int i=0; i<24; i++){
			if (mCurrentDateOffset == mSolarTermsThisYear[i]) {
				mLunarSpecialDay = true;	
				return mSolarTerms[i];
			}
			else if (mCurrentDateOffset < mSolarTermsThisYear[i])
				break;
		}
		
		// if it's the first day
		if (mLunarDate==1) {
			mLunarSpecialDay = true;	
			if(mLunarLeapMonth)
				return getLeapTitle()+mCnMonths[mLunarMonth];
			else
				return mCnMonths[mLunarMonth];
		}
		
		mLunarSpecialDay = false;	
		return mCnDays[mLunarDate-1];
	}
	
	/**
	 * return the lunar date strings in big5 format, if it's a special day, use the holiday text
	 * @return
	 */

		// check lunar holiday, skip if it's a lunar leap month
		
		// check if it's the last day of this year
		
		// check 节气
		
		// if it's the first day
		
	
	/**
	 * return Strings for solar holiday
	 * @return strings for solar holiday
	 */
	protected String getSolarHoliday() {
		
		if (!HolidayUtils.isChinaSku()) return "";
		
		String lmon = (mCurrentDate.get(Calendar.MONTH)<9) ?
				"0"+Integer.toString(mCurrentDate.get(Calendar.MONTH)+1) 
				: Integer.toString(mCurrentDate.get(Calendar.MONTH)+1);
		String lday = (mCurrentDate.get(Calendar.DATE)<10) ?
				"0"+Integer.toString(mCurrentDate.get(Calendar.DATE)) 
				: Integer.toString(mCurrentDate.get(Calendar.DATE));
		
		/*
		 * The Calendar class set Sunday to be 1, then Saturday is 7
		 * DAY_OF_WEEK_IN_MONTH set the first day of month as the first day of the first week, 
		 * so it can be interpreted as the result of the appearance in the month
		 */
		String aOtherFormat = "x" + lmon + 
			Integer.toString(mCurrentDate.get(Calendar.DAY_OF_WEEK_IN_MONTH)) + 
			Integer.toString(mCurrentDate.get(Calendar.DAY_OF_WEEK)-1);
		
		// Log.d(LOG_TAG,aOtherFormat);
		
		if(mSolarHolidays.containsKey(lmon+lday))
			return mSolarHolidays.get(lmon+lday);
		else if(mSolarHolidays.containsKey(aOtherFormat))
			return mSolarHolidays.get(aOtherFormat);
		else
			return "";
	}
	
	/**
	 * return Strings for solar holiday in big5 format
	 * @return
	 */
		
		/*
		 * The Calendar class set Sunday to be 1, then Saturday is 7
		 * DAY_OF_WEEK_IN_MONTH set the first day of month as the first day of the first week, 
		 * so it can be interpreted as the result of the appearance in the month
		 */
		
		// Log.d(LOG_TAG,aOtherFormat);
		
	
	/**
	 * get the 天干 information
	 * @return 天干 String
	 */
	private String getTianGan() {
		return mCnGans[(mLunarYear-GANZHI_BASE_YEAR)%10];		
	}
	
	/**
	 * get the 地支 information
	 * @return 地支 String
	 */
	private String getDiZhi() {
		return mCnZhis[(mLunarYear-GANZHI_BASE_YEAR)%12];
	}
	
	/**
	 * get the lunar zodiac
	 * @return lunar zodiac string
	 */
	private String getLunarZodiac() {
		return mCnZodiacs[(mLunarYear-GANZHI_BASE_YEAR)%12];
	}
	
	/**
	 * get the lunar zodiac in big5 format
	 * @return lunar zodiac string
	 */

	
	/**
	 * return the first Sunday of the week where the first day is contained.
	 * @return
	 */
	private LunarUtils getFirstSundayOfMonth() {
		LunarUtils tmp = new LunarUtils(mCurrentDate.get(Calendar.YEAR),
				mCurrentDate.get(Calendar.MONTH), 1);
		// tmp.debugInfo();
		// because Sunday has DAY_OF_WEEK=1
		tmp.add(Calendar.DATE, 1-tmp.get(Calendar.DAY_OF_WEEK));
		return tmp;
	}
		
	/**
	 * get the total days of the current lunar month
	 * @return
	 */
	private int getDaysInCurrentLunarMonth() {
		if(lunarLeapMonth(mLunarYear) > 0 &&
				mLunarMonth >= lunarLeapMonth(mLunarYear))
			return mLunarDaysInMonth[mLunarMonth + 1];
		else
			return mLunarDaysInMonth[mLunarMonth];
	}
	
	/**
	 * get month string symbols of the current lunar year.
	 * @return
	 */
	private String[] getLunarMonthSymbolsOfCurrentYear() {
		int aLeapMonth = lunarLeapMonth(mLunarYear);
		if(aLeapMonth == 0)
			return mCnMonths;
		else {
			String[] theLunarMonthSymbols = new String[13];
			for(int i=0,j=0; i<12; i++, j++){
				theLunarMonthSymbols[j] = mCnMonths[i];
				if(aLeapMonth == (i+1)) 
					theLunarMonthSymbols[++j] = getLeapTitle() + mCnMonths[i];
			}
			return theLunarMonthSymbols;
		}
	}
	
	/**
	 * get month string symbols of the current lunar year in big5 characters.
	 * @return
	 */

	
	/**
	 * get date string symbols of the current lunar month
	 * @return
	 */
	private String[] getLunarDateSymbolsOfCurrentMonth() {
		if(getDaysInCurrentLunarMonth() == 30)
			return mCnDays;
		else 
			return mCnDaysSmall;
	}
	
	/**
	 * whether current lunar month is leap 
	 * @return true if current lunar month is a leap month
	 */
	private boolean isLunarLeapMonth() {
		return mLunarLeapMonth;
	}
	
	/**
	 * whether two dates are the same day
	 */
	private boolean isTheSameDay(Calendar c1) {
		return (mCurrentDate.get(Calendar.YEAR) == c1.get(Calendar.YEAR)) &&
			(mCurrentDate.get(Calendar.MONTH) == c1.get(Calendar.MONTH)) &&
			(mCurrentDate.get(Calendar.DATE) == c1.get(Calendar.DATE));
	}
	
	/**
	 * whether two dates are the same day, using CnDate format
	 */
	private boolean isTheSameDay(LunarUtils c1) {
		return (mCurrentDate.get(Calendar.YEAR) == c1.get(Calendar.YEAR)) &&
			(mCurrentDate.get(Calendar.MONTH) == c1.get(Calendar.MONTH)) &&
			(mCurrentDate.get(Calendar.DATE) == c1.get(Calendar.DATE));
	}
	
	/**
	 * output some debug information
	 */
	private void debugInfo() {
		System.out.println("Calendar Date: " + this);
//		System.out.println("DAYS_OF_WEEK: " + mCurrentDate.get(Calendar.DAY_OF_WEEK));
//		System.out.println("isLeapYear: " + isSolarLeapYear());
//		System.out.print("mLunarDaysInMonth: ");
//		for(int i=0; i<13; i++){
//			System.out.print(mLunarDaysInMonth[i]+" ");
//		}
//		System.out.println();
		System.out.println("lunar dates: " + mLunarYear +" " + getLunarMonth() + " " + getLunarDate());
		System.out.println("天干地支属相：" + getTianGan() + getDiZhi() + getLunarZodiac());
		System.out.println("节日：" + getSolarHoliday());
		System.out.println();
	}
	
	private static final void main(String[] argv) {
		LunarUtils c1 = new LunarUtils();
		
		c1.debugInfo();
		String[] lms = c1.getLunarMonthSymbolsOfCurrentYear();
		for(int j=0; j<lms.length; j++)
			System.out.println(lms[j]);
		
		int i = c1.setLunarDate(2008, 5, 15, false);
		if(i == -1) {
			c1.debugInfo();
			lms = c1.getLunarMonthSymbolsOfCurrentYear();
			for(int j=0; j<lms.length; j++)
				System.out.println(lms[j]);
		}
		else
			System.out.println(mErrArray[i]);
		
		
	}
	
	/**
     * return the lunar date strings, if it's a special day, use the holiday text
     * @return
     */
    
    private Map<String,String> mHolidays = new HashMap<String,String>() ;
    
    /**
      * Get the China Calendar
      * @return the Calendar map in <key, value>
      * @deprecated [Module internal use]
      */
    /**@hide*/ 
    public Map<String,String> getChinaCalendar() {

        mHolidays.clear();
        mLunarSpecialDay = false;

        String lunarDay="";
        String solarHoliday ="";
        String solarTerm ="";
        String lunarHoliday ="";
		String specialHoliday ="";
        // check if it's the last day of this year
       if(mLunarMonth==11 && mLunarDate==getDaysInCurrentLunarMonth()) {            
            mLunarSpecialDay = true;        
            lunarHoliday= "除夕";
            lunarDay= mCnMonths[mLunarMonth]+mCnDays[mLunarDate-1];
            
        // check lunar holiday, skip if it's a lunar leap month
       } else if(!mLunarLeapMonth) {
            String lmon = (mLunarMonth<9) ?
                    "0"+Integer.toString(mLunarMonth+1) : Integer.toString(mLunarMonth+1);
            String lday = (mLunarDate<10) ?
                    "0"+Integer.toString(mLunarDate) : Integer.toString(mLunarDate);
            if(mLunarHolidays.containsKey(lmon+lday)) {
                    mLunarSpecialDay = true;        
                    lunarHoliday= mLunarHolidays.get(lmon+lday);
                    lunarDay= mCnMonths[mLunarMonth]+mCnDays[mLunarDate-1];
            } else {
                lunarDay= mCnMonths[mLunarMonth]+mCnDays[mLunarDate-1];
            }

        // if it's Lunar Leap Month
        } else if(mLunarLeapMonth){
                    lunarDay= getLeapTitle()+mCnMonths[mLunarMonth]+mCnDays[mLunarDate-1];
        } else {
            lunarDay= mCnMonths[mLunarMonth]+mCnDays[mLunarDate-1];
        }

        // check 节气
        for(int i=0; i<24; i++){
            if (mCurrentDateOffset == mSolarTermsThisYear[i]) {
                mLunarSpecialDay = true;    
                solarTerm= mSolarTerms[i];
            }
            else if (mCurrentDateOffset < mSolarTermsThisYear[i])
                break;
        }

		// Cherry To-Do in 4.5 separate china holiday from LunarUtils  
		solarHoliday= getSolarHoliday();
		
		//Special holiday
		specialHoliday=getSpecialHoliday();

		//20150615 Public holiday
	     String publicHoliday = getPublicHoliday();
	     
		//20150615 WorkDay String
	     String workdayString = getWorkdayString();
	     
		mHolidays.put(KEY_SOLAR_HOLIDAY,solarHoliday);
		mHolidays.put(KEY_SOLAR_TERM,solarTerm);
		mHolidays.put(KEY_LUNAR_DAY,lunarDay);
		mHolidays.put(KEY_LUNAR_HOLIDAY,lunarHoliday);
		mHolidays.put(KEY_SPECIAL_HOLIDAY,specialHoliday);
		
		if (!TextUtils.isEmpty(publicHoliday)){
			mHolidays.put(KEY_PUBLIC_HOLIDAY, publicHoliday);
		}
		
		if (!TextUtils.isEmpty(workdayString)){
			mHolidays.put(KEY_WORKDAY_STRING, workdayString);
		}
		
		return mHolidays;
	}
    
    /**
     * 20160610 New feature : china public holidays
     * @return public string
     */
    private String getPublicHoliday(){	
		if (!HolidayUtils.isChinaSku()) return "";

		if (mCurrentDate!=null){			
			int year = mCurrentDate.get(Calendar.YEAR);
			int month = (mCurrentDate.get(Calendar.MONTH));
			int date = mCurrentDate.get(Calendar.DATE);
			return  ChinaHolidayUtil.getInstance().getHoliday(year, month, date);
		}
		
		return "";
    }
    
    
    /**
     * 20160610 New feature : china workday string
     * @return workday string
     */
    private String getWorkdayString(){	
		if (!HolidayUtils.isChinaSku()) return "";

		if (mCurrentDate!=null){			
			int year = mCurrentDate.get(Calendar.YEAR);
			int month = (mCurrentDate.get(Calendar.MONTH));
			int date = mCurrentDate.get(Calendar.DATE);
			return  ChinaHolidayUtil.getInstance().getWorkdayString(year, month, date);
		}
		
		return "";
    }
	
	private String getLeapTitle(){
		
		if (TextUtils.isEmpty(mLeapTitle)){
	    	Locale locale = Locale.getDefault();   	
			String systemLanguage = locale.getLanguage();
	    	String country = locale.getCountry();
			if(systemLanguage.equals("zh") && country.equals("CN")){
				mLeapTitle= "闰";
			}
			else{ 
				mLeapTitle="閏";
			}
		}
		return mLeapTitle;
	}
	
    //取得某月的第某個星期日 : Mother's day, month is 4 and week_days is 2
	// Father's day: month is 5 and week_days is 3
    private static String getSundayOfWeekByMonth(int year,int month,int week_days){  
        Calendar cal = Calendar.getInstance();  
//        cal.set(cal.get(Calendar.YEAR), 4, 1); 
        cal.set(year, month, 1);
        // 如果weekDay =1 是周日  
        int weekDay = cal.get(Calendar.DAY_OF_WEEK);  
        int sunDay=Calendar.SUNDAY;  
        int sumDay = 0;  
        if (weekDay == sunDay) {  
            sumDay = week_days;  
        }   
        else{  
            sumDay=(7-weekDay+sunDay)+week_days;  
        }  
        cal.add(Calendar.DAY_OF_MONTH, sumDay);  
        
        return cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-"  
        + cal.get(Calendar.DAY_OF_MONTH);  
    }

    //取得某年某月的第某個星期幾 : Thanksgiving day 11月的第四個星期四
    private static String getDateOfWeekByMonth(int year,int month,int week_days, int weekday){

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);
        //1. 先找到第一個禮拜的星期幾
		while(cal.get(Calendar.DAY_OF_WEEK) != weekday) {
							cal.set(year, month, cal.get(Calendar.DATE)+1);
		}
		//2. 再往後加剩幾個禮拜 * 7 天  ,  ThanksGivingday -> Add 3 weeks.
		if ((week_days-1)>0) {
			int days =0;
			days= (week_days-1) *7 ; // 3*7=21
			cal.set(year, month, cal.get(Calendar.DATE)+days);
		}

		return cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-"  
        + cal.get(Calendar.DAY_OF_MONTH);  
    }


    
        /**
          * Get the special holiday
          * @return the string of the special holiday name
          * @deprecated [Module internal use]
          */
    /**@hide*/ 
	public String getSpecialHoliday() {

		if (!HolidayUtils.isChinaSku()) return "";

		if (mCurrentDate!=null){
			String year = String.valueOf(mCurrentDate.get(Calendar.YEAR));
			String month = String.valueOf((mCurrentDate.get(Calendar.MONTH) + 1));
			String date = String.valueOf(mCurrentDate.get(Calendar.DATE));
			
			String strdate = year + '-' + month + "-" + date;
			if (mSpecailHoliday!=null && mSpecailHoliday.containsKey(strdate)) {
				return mSpecailHoliday.get(strdate);
			} else return "";
		}
		return "";
	}
	
	private void cacularSpecialDay(GregorianCalendar cal){
		// China : Mother's Day & Father's Day
		// China Sense : Thanksgiving Day
		
//		String stryear=String.valueOf(cal.get(Calendar.YEAR));
		if (mYear!=null && !mYear.containsKey( cal.get(Calendar.YEAR)))
		{
	    	Locale locale = Locale.getDefault();   	
			String systemLanguage = locale.getLanguage();
	    	String country = locale.getCountry();
	    	
			String holiday="";
			
			//Mother's Day
			holiday =getSundayOfWeekByMonth(cal.get(Calendar.YEAR), 4,7);
			if(systemLanguage.equals("zh") && country.equals("CN")){
				mSpecailHoliday.put(holiday,"母亲节");
			} else {
				mSpecailHoliday.put(holiday,"母親節");
			}
			
			//Father's Day
			holiday = getSundayOfWeekByMonth(cal.get(Calendar.YEAR), 5, 14);
			if(systemLanguage.equals("zh") && country.equals("CN")){
				mSpecailHoliday.put(holiday,"父亲节");
			} else {
				mSpecailHoliday.put(holiday,"父親節");
			}
			
			//Thanksgiving Day 每年11月的第四個星期四
			holiday = getDateOfWeekByMonth(cal.get(Calendar.YEAR), 10, 4, Calendar.THURSDAY);
			if(systemLanguage.equals("zh") && country.equals("CN")){
				mSpecailHoliday.put(holiday,"感恩节");
			} else {
				mSpecailHoliday.put(holiday,"感恩節");
			}
			
		}
	} 

	private void cleanHashMap(){
	 	// reset Special holiday
	 	if (mYear!=null){
	 		mYear.clear();
	 	}
 		if (mSpecailHoliday!=null){
 			mSpecailHoliday.clear();
 		}
	 	if (mLunarHolidays!=null){
	 		mLunarHolidays.clear();
	 	}
	 	if (mSolarHolidays!=null){
	 		mSolarHolidays.clear();
	 	}
	}
}
