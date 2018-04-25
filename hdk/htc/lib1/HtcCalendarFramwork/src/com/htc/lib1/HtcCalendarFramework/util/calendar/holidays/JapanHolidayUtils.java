package com.htc.lib1.HtcCalendarFramework.util.calendar.holidays;

import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Array;

/**
  * Japan Holiday Utility Class
  */
public class JapanHolidayUtils {
	
	
	private static final String TAG = "JapanHolidayUtils";
	private static GregorianCalendar mCurrentDate;
	private static String[] JapanHolidays;
	private static String[] JapanHolidays_date;
	private static Map<String, String> mJapanHolidays = new HashMap<String,String>();	
	private static Map<String, String> mSpecailHoliday = new HashMap<String,String>();		
	private static Map<Integer, Integer> mYear = new HashMap<Integer,Integer>();		
	private static Map<String, String> mCompensatoryHoliday = new HashMap<String,String>();		
	
	// Exception table, to record date + holiday index. e.g., national day index is 7
	private static final int[][] exception={{2009,9,22,7},{2015,9, 22,7},{2026,9,22,7}};

	private boolean isVernalEquinox = false;
	private boolean isAutumnalEquinox = false;
	private boolean isCompensatoryDay = false;
	private boolean isAgedDay = false;
	private boolean isMarineDay = false;
	private boolean isAdultDay = false;
	private boolean isSportsDay = false;
	private boolean isSpecialHoloiday = false;
	private boolean isCompensatory = false;
	private boolean isExceptionHoliDay = false;

	
	private static final int INDEX_ADULT_DAY = 0;
	private static final int INDEX_MARINE_DAY = 1;
	private static final int INDEX_AGED_DAY = 2;
	private static final int INDEX_SPORT_DAY = 3;
	private static final int INDEX_VERNAL= 4;
	private static final int INDEX_AUTUMAL = 5;
	private static final int INDEX_STR_COMPENSATORY = 6;
	private static final int INDEX_STR_NATIONAL_DAY = 7;

	private static final String STR_ADULT_DAY = "AdultDay";
	private static final String STR_MARINE_DAY = "MarineDay";
	private static final String STR_AGED_DAY = "AgedDay";
	private static final String STR_SPORT_DAY = "SportsDay";
	private static final String STR_VERNAL_DAY= "VernalDay";
	private static final String STR_AUTUMAL_DAY = "AutumnalDay";
	private static final String STR_COMPENSATORY = "Compensatory";

	/** Japan Holiday Rules:
	  *	1.國定假日
	  *	    春分日（３月X日）	X = INT(20.8431+0.242194*(Year-1980)-INT((Year-1980)/4)) japan Vernal Equinox Day
	  *		秋分日（９月Y日）	Y = INT(23.2488+0.242194*(Year-1980)-INT((Year-1980)/4)) japan Autumnal Equinox Day
	  *	2."振替休日"
	  *	   如果國定假日在週日, 則找下一個非國定假日補休
	  *	   如果週二和週四是假日, 週三也自動變為假日
	  * 3. Holiday defined in xml
	  * 4. Exception table (date+holiday index), 国民の休日
	  * @param year int The year value
	  * @param month int The month value
	  * @param date int the date value
	  */
	
	protected void set(int year, int month, int date){

		mCurrentDate.set(year, month, date, 0, 0, 0);

		
		if (!mYear.containsKey(mCurrentDate.get(Calendar.YEAR)))
		{
			Log.v(TAG,"cacularSpecialDay for year:"+ mCurrentDate.get(Calendar.YEAR));
			calculateSpecialDay(mCurrentDate);
			mYear.put(year,year);
		}

		//check and set boolean for special holidays, just like 春分日,成人の日,振替休日
		checkSpecialHoliday(mCurrentDate.get(Calendar.YEAR),mCurrentDate.get(Calendar.MONTH)
				,mCurrentDate.get(Calendar.DATE));
		checkException(mCurrentDate);
		
		checkCompensatory();
		
		
		
//		 //Get Map in Set interface to get key and value
//	     Set specailHoliday=mSpecailHoliday.entrySet();
//
//	        //Move next key and value of Map by iterator
//	        Iterator it=specailHoliday.iterator();
//	     
//	        while(it.hasNext()) 
//	        {
//	            // key=value separator this by Map.Entry to get key and value
//	            Map.Entry m =(Map.Entry)it.next();
//
//	          // getKey is used to get key of Map
//	            String key=(String) m.getKey();
//
//	          // getValue is used to get value of key in Map
//	            String value=(String)m.getValue();
//
//	            Log.v(TAG,"Key :"+key+"  Value :"+value);
//
//	        }
		
	}
	
    private static JapanHolidayUtils singleHolidayUtils = null;    

    /**
      * Get the Japan holiday utility class instance
      * @param res the Resources
      * @param forceUpdate force to update instance
      * @return the Japan holiday utility class instance
      * @deprecated [Module internal use]
      */
    /**@hide*/ 
    public static JapanHolidayUtils getInstance(Resources res, boolean forceUpdate) {
        if (forceUpdate || null == singleHolidayUtils ) {
        	singleHolidayUtils = new JapanHolidayUtils(res);
        }   
        return singleHolidayUtils;
    }
    
    /**
     * Get the Japan holiday utility class instance
     * @param res the Resources
     * @return the Japan holiday utility class instance
     * @deprecated [Module internal use]
     */
    /**@hide*/ 
   public static JapanHolidayUtils getInstance(Resources res) {
       if (null == singleHolidayUtils ) {
       	singleHolidayUtils = new JapanHolidayUtils(res);
       }   
       return singleHolidayUtils;
   }
    

    // VCalendarSupportmanager Constructor
    private JapanHolidayUtils(Resources rec) {
        
//		 Log.d(TAG,"HolidayUtils instance!");

			mCurrentDate = new GregorianCalendar();
			mCurrentDate.set(Calendar.HOUR, 0);
			mCurrentDate.set(Calendar.MINUTE, 0);
			mCurrentDate.set(Calendar.SECOND, 0);
			JapanHolidays =rec.getStringArray( com.htc.lib1.HtcCalendarFramework.R.array.htc_Japanease_Holidays);
			JapanHolidays_date = rec.getStringArray( com.htc.lib1.HtcCalendarFramework.R.array.htc_Japanease_Holidays_date);
		
			int length = JapanHolidays.length;
		 	for(int i = 0 ; i < length ; i++){
		 		mJapanHolidays.put(JapanHolidays_date[i],JapanHolidays[i]);
//				Log.d(TAG,"Array == JapanHolidays_date is :"+JapanHolidays_date[i] + " && JapanHolidays is" + JapanHolidays[i]);
		 	}
    }     

        /**
          * To get the holidays
          * @return the holiday string
          */
	protected String getHolidays() {
		if (isSpecialHoloiday || isCompensatory || isExceptionHoliDay) {
			// Log.d(TAG,"getSpecialHoliday!");
			return getSpecialHoliday();
		} else {
			// Log.d(TAG,"getSolarHoliday!");
			return getSolarHoliday(mCurrentDate);
		}
	}	
	
	/**
	 * return Strings for solar holiday
	 * @return
	 */
	private String getSolarHoliday(GregorianCalendar cal) {
		String lmon = (cal.get(Calendar.MONTH)<9) ?
				"0"+Integer.toString(cal.get(Calendar.MONTH)+1) 
				: Integer.toString(cal.get(Calendar.MONTH)+1);
		String lday = (cal.get(Calendar.DATE)<10) ?
				"0"+Integer.toString(cal.get(Calendar.DATE)) 
				: Integer.toString(cal.get(Calendar.DATE));
		
		/*
		 * The Calendar class set Sunday to be 1, then Saturday is 7
		 * DAY_OF_WEEK_IN_MONTH set the first day of month as the first day of the first week, 
		 * so it can be interpreted as the result of the appearance in the month
		 */
		String aOtherFormat = "x" + lmon + 
			Integer.toString(cal.get(Calendar.DAY_OF_WEEK_IN_MONTH)) + 
			Integer.toString(cal.get(Calendar.DAY_OF_WEEK)-1);

		if(mJapanHolidays.containsKey(lmon+lday)){
//			 Log.d(TAG,"mJapanHolidays.get(lmon+lday) is :"+ mJapanHolidays.get(lmon+lday));
			/*
			 * In May 2014, the Japanese Diet announced that Mountain Day (山の日) will be celebrated as a public holiday every August 11,
			 * beginning in 2016. Supporters of the holiday included legislator Seishiro Eto and the Japanese Alpine Club.
			 * The legislation states that the holiday is to provide “opportunities to get familiar with mountains and appreciate blessings from mountains.
			 */
			if((lmon+lday).compareTo("0811")==0 && cal.get(Calendar.YEAR) < 2016)
				return "";
			return mJapanHolidays.get(lmon+lday);
		}
		else if(mJapanHolidays.containsKey(aOtherFormat)){
//			 Log.d(TAG,"mJapanHolidays.get(aOtherFormat) is :"+ mJapanHolidays.get(aOtherFormat));
			return mJapanHolidays.get(aOtherFormat);}
		
		else return "";
	}


	/**
	 * return the Special date strings, if it's a special day, use the holiday text
	 * @return
	 */
	
	private String getSpecialHoliday() {

	
//		成人の日	1月第二個星期一	
//		春分の日	春分日	
//		海の日	7月第三個星期一	
//		敬老の日	9月第三個星期一	
//		10月第二個星期一
//		秋分の日	秋分日	
		
		String specialholiday="";

		if(isAutumnalEquinox) {
			specialholiday= "秋分の日";
			isAutumnalEquinox=false;

		} 
		
		if (isVernalEquinox) {
			specialholiday= "春分の日";
			isVernalEquinox=false;

		} 

		if(isCompensatoryDay) {
			specialholiday= "振替休日";
			isCompensatoryDay=false;
			isCompensatory=false;
		} 
		
		if(isMarineDay) {
			specialholiday= "海の日";
			isMarineDay=false;

		} 
		
		if (isAgedDay){

			specialholiday= "敬老の日";
			isAgedDay=false;

		} 

		if (isAdultDay){
			
			specialholiday= "成人の日";
			isAdultDay=false;

		} 

		if (isSportsDay){
			
			specialholiday= "体育の日";
			isSportsDay=false;
		}  
		
		
		if (isExceptionHoliDay){
			specialholiday= getExceptionHoliday(mCurrentDate);
			isExceptionHoliDay=false;
		}  
	
		return specialholiday;
	}
	

	private void checkSpecialHoliday(int year, int month, int date) {
		
		// 成人の日 1月第二個星期一
		// March 春分日
		// 海の日 7月第三個星期一
		// 敬老の日 9月第三個星期一
		// Spetember 秋分日
		isSpecialHoloiday=false;

		int monthOffset = month+1;

		String strdate=String.valueOf(date);
		String strmonth=String.valueOf(monthOffset);
		String stryear=String.valueOf(year);

		String strDay= stryear + '-' + strmonth + "-" + strdate;
		
		
		// Calculate Special holiday for mCurrentDate
		String AdultDay = getSpecialDate(mCurrentDate,INDEX_ADULT_DAY);// 1-16
		String MarineDay = getSpecialDate(mCurrentDate,INDEX_MARINE_DAY); //  7-16 
		String AgedDay = getSpecialDate(mCurrentDate,INDEX_AGED_DAY);// 9-17
		String SportDay = getSpecialDate(mCurrentDate,INDEX_SPORT_DAY);// 10-8
		String VernalDay = getSpecialDate(mCurrentDate,INDEX_VERNAL);// 1-16
		String AutumnalDay = getSpecialDate(mCurrentDate,INDEX_AUTUMAL);// 1-16
//		String CompensatoryDay = getSpecialDate(mCurrentDate,INDEX_STR_COMPENSATORY);// 1-16
		String CompensatoryDay=mCompensatoryHoliday.get(strDay+STR_COMPENSATORY);

		Log.d(TAG,"Special holiday AdultDay is :"+AdultDay
				+ " & MarineDay is :" +MarineDay + " & AgedDay is :" + AgedDay 
				+ " & SportDay is :" + SportDay 
				+ " & VernalDay is :" + VernalDay 
				+ " & AutumnalDay is :" + AutumnalDay 
				+ " & CompensatoryDay is :" + CompensatoryDay 
				+ " & strDay is :" + strDay
		);


		// 成人の日 
		if (strDay.equals(AdultDay)) {
			isAdultDay = true;
			isSpecialHoloiday=true;
		}
		// 海の日
		if (strDay.equals(MarineDay)) {
			isMarineDay = true;
			isSpecialHoloiday=true;
		}
		// 敬老の日
		if (strDay.equals(AgedDay)) {
			isAgedDay = true;
			isSpecialHoloiday=true;
		}
		
		// 体育の日
		if (strDay.equals(SportDay)) {
			isSportsDay = true;
			isSpecialHoloiday=true;
		}	
//		// 振替休日
//		if (isCompensatoryHoliday()){
//			isCompensatoryDay = true;
//			isCompensatory=true;
//			}
		// 振替休日
		if (strDay.equals(CompensatoryDay)) {
			isCompensatoryDay = true;
			isCompensatory=true;
			}
		
		
		// 体育の日
		if (strDay.equals(SportDay)) {
			isSportsDay = true;
			isSpecialHoloiday=true;
		}
		
		
		// 春分日 March
		if (strDay.equals(VernalDay)) {
			isVernalEquinox = true;		 
			isSpecialHoloiday=true;			

		}
		// 秋分日 September
		if (strDay.equals(AutumnalDay)) {
			isAutumnalEquinox = true;
			isSpecialHoloiday=true;
		}
	}
	
	private void calculateSpecialDay(GregorianCalendar cal)
	{

		String stryear=String.valueOf(cal.get(Calendar.YEAR));

		if (!mYear.containsKey(stryear))
		{
			String holiday="";
			holiday =getMondaybyMonth(cal.get(Calendar.YEAR), 0,7);
			mSpecailHoliday.put( stryear+STR_ADULT_DAY, holiday);

			holiday = getMondaybyMonth(cal.get(Calendar.YEAR), 6, 14);
			mSpecailHoliday.put( stryear+STR_MARINE_DAY, holiday);

			holiday = getMondaybyMonth(cal.get(Calendar.YEAR), 8, 14);
			mSpecailHoliday.put( stryear+STR_AGED_DAY, holiday);

			holiday = getMondaybyMonth(cal.get(Calendar.YEAR), 9, 7);
			mSpecailHoliday.put( stryear+STR_SPORT_DAY, holiday);
			
			int VernalDay = (int) (20.8431 + 0.242194 * (cal.get(Calendar.YEAR) - 1980) - (int) ((cal.get(Calendar.YEAR) - 1980) / 4));
			holiday= String.valueOf(cal.get(Calendar.YEAR))+"-"+"3"+"-"+VernalDay;
			mSpecailHoliday.put( stryear+STR_VERNAL_DAY, holiday);

			int AutumnalDay = (int) (23.2488 + 0.242194 * (cal.get(Calendar.YEAR) - 1980) - (int) ((cal.get(Calendar.YEAR) - 1980) / 4)); 
			holiday = String.valueOf(cal.get(Calendar.YEAR))+"-"+"9"+"-"+AutumnalDay;
			mSpecailHoliday.put( stryear+STR_AUTUMAL_DAY, holiday);

		}
	}
	
	private boolean checkException(GregorianCalendar cal){
		int year=(cal.get(Calendar.YEAR));
		int season = cal.get(Calendar.MONTH)+1;
		int date = cal.get(Calendar.DATE);
		for(int i=0;i<Array.getLength(exception);i++){
    	    if(year==exception[i][0] && season==exception[i][1] && date==exception[i][2]){
    	    	isExceptionHoliDay=true;
    	    	return true;
    	    }
    	}
		return false;
	}
	
	private String getExceptionHoliday(GregorianCalendar cal){
		// exception
		int year=(cal.get(Calendar.YEAR));
		int season = cal.get(Calendar.MONTH)+1;
		int date = cal.get(Calendar.DATE);
    	for(int i=0;i<Array.getLength(exception);i++){
    	    if(year==exception[i][0]&& season==exception[i][1] && date==exception[i][2]){

        		switch(exception[i][3]) {
        		case INDEX_STR_NATIONAL_DAY:{
        			return "国民の休日";
        		}

                default: return "";
        		}
    	    }
    	}
    	 return "";
	}

	

	private void checkCompensatory()
	{
		int month = mCurrentDate.get(Calendar.MONTH);
		int date = mCurrentDate.get(Calendar.DATE);
		int year = mCurrentDate.get(Calendar.YEAR);
		if (mCurrentDate.get(Calendar.DAY_OF_WEEK) == 1 && isHoliday(mCurrentDate)) {

			GregorianCalendar cal = new GregorianCalendar();
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(year, month, date, 0, 0, 0);

			for (int i = 1; i < 6; i++) {

				cal.add(Calendar.DATE, +1);

				if (isHoliday(cal)){
					continue;
				}
				else
				{
					String com = cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)
					+"-"+cal.get(Calendar.DATE);
					mCompensatoryHoliday.put( com+STR_COMPENSATORY, com);
					break;
				}

			}
		}
	}

	// 振替休日 compensatory holiday;
	private boolean isCompensatoryHoliday() {
		// 如果國定假日在週日, 則找下一個非國定假日補休
		// 如果週二和週四是假日, 週三也自動變為假日
		int month = (mCurrentDate.get(Calendar.MONTH)+1);
		int date = mCurrentDate.get(Calendar.DATE);
		int year = mCurrentDate.get(Calendar.YEAR);

		// Calendar.SUNDAY是1，到Calendar.SATURDAY是7
		// 今天星期三, check 周二周四是否為 假日 && check 國定假日

		if (mCurrentDate.get(Calendar.DAY_OF_WEEK) == 4) {

			// case 1
			GregorianCalendar calThuesday = new GregorianCalendar();

			calThuesday.set(year, month, date, 0, 0, 0);
			calThuesday.add(Calendar.DATE, -1);

			GregorianCalendar calThursday = new GregorianCalendar();
			calThursday.set(Calendar.HOUR, 0);
			calThursday.set(Calendar.MINUTE, 0);
			calThursday.set(Calendar.SECOND, 0);

			calThursday.set(year, month, date, 0, 0, 0);
			calThursday.add(Calendar.DATE, +1);

			if (isHoliday(calThuesday) && isHoliday(calThursday)) {

				return true;
			}
		}
		else
		{
			String strDay= year + '-' + month + "-" + date;

			String CompensatoryDay=mCompensatoryHoliday.get(strDay+STR_COMPENSATORY);

			if (!TextUtils.isEmpty(CompensatoryDay))
				return true;

		}

		return false;
	}
	
	private boolean isSunday(GregorianCalendar cal){
		if (cal.get(Calendar.DAY_OF_WEEK) == 1) {
			return true;	
		} else return false;
		
	}
	private boolean isHoliday(GregorianCalendar cal){

		String year = String.valueOf(cal.get(Calendar.YEAR));
		String month = String.valueOf((cal.get(Calendar.MONTH) + 1));
		String date = String.valueOf(cal.get(Calendar.DATE));
	
		String tempmonth = (((cal.get(Calendar.MONTH) + 1))<9) ?
				"0"+Integer.toString(cal.get(Calendar.MONTH)+1) 
				: Integer.toString(cal.get(Calendar.MONTH)+1);
	
				String tempdate = (((cal.get(Calendar.DATE)))<9) ?
						"0"+Integer.toString(cal.get(Calendar.DATE)) 
						: Integer.toString(cal.get(Calendar.DATE));
				
				
				String temp=tempmonth+tempdate;
				
//				//Get Map in Set interface to get key and value
//			     Set specailHoliday=mSpecailHoliday.entrySet();
//		
//			        //Move next key and value of Map by iterator
//			        Iterator it=specailHoliday.iterator();
//			     
//			        while(it.hasNext()) 
//			        {
//			            // key=value separator this by Map.Entry to get key and value
//			            Map.Entry m =(Map.Entry)it.next();
//		
//			          // getKey is used to get key of Map
//			            String key=(String) m.getKey();
//		
//			          // getValue is used to get value of key in Map
//			            String value=(String)m.getValue();
//		
////			            Log.v(TAG,"Key :"+key+"  Value :"+value);
//		
//			        }
				
		if (mSpecailHoliday.containsValue(year+"-"+month+"-"+date)) {
			return true;
		}
		
		if (mJapanHolidays.containsKey(temp)) {
			return true;
		}
		
		boolean isException =checkException(cal);
		if (isException){
			return true;
		}
		
		return false;	
	}
	
	private static String getMondaybyMonth(int year, int month, int week_days){  
  
    	Calendar cal = Calendar.getInstance();  
        cal.set(year, month, 1);//base 日期設為 ? 月1日  

        int weekDay = cal.get(Calendar.DAY_OF_WEEK);  
        int monDay=Calendar.MONDAY;
        int sunDay=Calendar.SUNDAY;

        int sumOfDay = 0;

        //如果weekDay =2 是周一  
        if (weekDay == monDay) {  
        	sumOfDay = week_days;  

        } 
        //如果weekDay =1 是周日  

        else if (weekDay==sunDay)
        	sumOfDay = (7-weekDay+monDay)+week_days-7;   

        else{  
        	sumOfDay=(7-weekDay+monDay)+week_days;  

        }  	
     
        cal.add(Calendar.DAY_OF_MONTH, sumOfDay);  
        
        
        return  cal.get(Calendar.YEAR) + "-"+ (cal.get(Calendar.MONTH) + 1) + "-"  
        + cal.get(Calendar.DAY_OF_MONTH);  
    }

	

	//取得一月的第二個星期一	  成人の日
    private static String getAdultDay(GregorianCalendar cal){  
        return getMondaybyMonth(mCurrentDate.get(Calendar.YEAR),0,7);  
    }
	
	//取得7月的第三個星期一	  海の日
    private static String getMarineDay(GregorianCalendar cal){  

    	return getMondaybyMonth(cal.get(Calendar.YEAR),6,14);  

    }
    
	//取得九月的第三個星期一	  敬老の日
    private static String getAgedDay(GregorianCalendar cal){  

    	return getMondaybyMonth(cal.get(Calendar.YEAR),8,14);  
 
    }
    
	// 取得十月的第二個星期一 体育の日
	private static String getSportsDay(GregorianCalendar cal) {
			return getMondaybyMonth(cal.get(Calendar.YEAR), 9, 7);
	}
	

	
	private static String getSpecialDate(GregorianCalendar cal, int index) {
		String year = String.valueOf(cal.get(Calendar.YEAR));
		 String month = String.valueOf((cal.get(Calendar.MONTH) + 1));
		 String date = String.valueOf(cal.get(Calendar.DATE));

		 String strdate = year + '-' + month + "-" + date;

		String holiday = "";
		// Log.v(TAG, " mSpecailHoliday strdate is :" + strdate);

		switch (index) {

		case INDEX_ADULT_DAY:

			holiday = year + STR_ADULT_DAY;

			if (mSpecailHoliday.containsKey(holiday)) {
				return mSpecailHoliday.get(holiday);
			}

			holiday = getMondaybyMonth(mCurrentDate.get(Calendar.YEAR), 0, 7);

			return holiday;

		case INDEX_MARINE_DAY:

			holiday = year + STR_MARINE_DAY;

			if (mSpecailHoliday.containsKey(holiday)) {
				holiday = mSpecailHoliday.get(holiday);
			} else {
				holiday = getMondaybyMonth(cal.get(Calendar.YEAR), 6, 14);
				// Log.v(TAG, "put mSpecailHoliday MarineDay is :" + holiday);
			}

			return holiday;

		case INDEX_AGED_DAY:
			holiday = year + STR_AGED_DAY;
			if (mSpecailHoliday.containsKey(holiday)) {
				holiday = mSpecailHoliday.get(holiday);
			} else {
				holiday = getMondaybyMonth(cal.get(Calendar.YEAR), 8, 14);
				// Log.v(TAG, "put mSpecailHoliday AgedDay is :" + holiday);
			}
			return holiday;

		case INDEX_SPORT_DAY:
			holiday = year + STR_SPORT_DAY;
			if (mSpecailHoliday.containsKey(holiday)) {
				holiday = mSpecailHoliday.get(holiday);
			} else {
				holiday = getMondaybyMonth(cal.get(Calendar.YEAR), 9, 7);
				// Log.v(TAG, "put mSpecailHoliday SportDay is :" + holiday);
			}
			return holiday;

		case INDEX_VERNAL:
			holiday = year + STR_VERNAL_DAY;
			if (mSpecailHoliday.containsKey(holiday)) {
				holiday = mSpecailHoliday.get(holiday);
			} else {
				int VernalDay = (int) (20.8431 + 0.242194 * (cal
						.get(Calendar.YEAR) - 1980) - (int) ((cal
						.get(Calendar.YEAR) - 1980) / 4));

				holiday = year + "-" + "3" + "-" + VernalDay;
			}
			return holiday;

		case INDEX_AUTUMAL:
			holiday = year + STR_AUTUMAL_DAY;
			if (mSpecailHoliday.containsKey(holiday)) {
				holiday = mSpecailHoliday.get(holiday);
			} else {
				int AutumnalDay = (int) (23.2488 + 0.242194 * (cal
						.get(Calendar.YEAR) - 1980) - (int) ((cal
						.get(Calendar.YEAR) - 1980) / 4));
				holiday = year + "-" + "9" + "-" + AutumnalDay;

			}
			return holiday;

		case INDEX_STR_COMPENSATORY:
			holiday = strdate + STR_COMPENSATORY;
			if (mSpecailHoliday.containsKey(holiday)) {
				holiday = mSpecailHoliday.get(holiday);
			}
	
			return holiday;
		}

		return "";
	}
	
    //取得五月的第二個星期日  
    private static String getSecondSunDayOfMay(){  
        Calendar cal = Calendar.getInstance();  
        cal.set(cal.get(Calendar.YEAR), 4, 1);//日期设置为今年的5月1日  
        // 如果weekDay =1 是周日  
        int weekDay = cal.get(Calendar.DAY_OF_WEEK);  
        int sunDay=Calendar.SUNDAY;  
        int sumDay = 0;  
        if (weekDay == sunDay) {  
            sumDay = 7;  
        }   
        else{  
            sumDay=(7-weekDay+sunDay)+7;  
        }  
        cal.add(Calendar.DAY_OF_MONTH, sumDay);  
        return cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-"  
        + cal.get(Calendar.DAY_OF_MONTH);  
    }  
	
}
