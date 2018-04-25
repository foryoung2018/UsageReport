package com.htc.lib1.HtcCalendarFramework.util.calendar;

import java.util.Calendar;
import android.content.Context;
import android.content.res.Resources;
import android.text.format.Time;
import android.text.format.DateUtils;
import android.text.TextUtils;
import android.util.Log;

import com.htc.lib1.HtcCalendarFramework.calendarcommon2.EventRecurrence;
import com.htc.lib1.HtcCalendarFramework.util.calendar.tools.TimeDisplayUtils;

public class EventRecurrenceUtils {
	
	private static final String TAG = "EventRecurrenceUtils";
	
	private String mRepeatString = "";
	
	private String mEndsAtString = "";
	
	private static int[] mMonthRepeatByDayOfWeekIds;
	
	private static String[][] mMonthRepeatByDayOfWeekStrs;
	
	/**
     * EventRecurrenceUtils Construct
     * Used in parsing rrule to readable string
     * 
     * @param context a context for getting resource
     * @param rRule rRule of event defined by rfc2445
     * @param startMillis dtStart of the event
     * @param isAllDay is it an all day event
     * @return repeat string after parsing
     */
	public  EventRecurrenceUtils(Context context, String rRule, long startMillis, boolean isAllDay, String timezone) {    	
    	
        if (context != null && !TextUtils.isEmpty(rRule) && startMillis != -1) {
        	try {
	        	EventRecurrence eventRecurrence = new EventRecurrence();
	        	eventRecurrence.parse(rRule);
	            Time date = new Time(timezone);  
	            if (isAllDay) {
	                date.timezone = Time.TIMEZONE_UTC;
	            }
	            date.set(startMillis);
	            eventRecurrence.setStartDate(date);
	            parsingRule(context, eventRecurrence, timezone);	            
        	} catch(Exception e) {
            	Log.w(TAG, "Can't handle RRULE: "+rRule + "; ", e);            	
            }
        }
    }	
	
	private void parsingRule(Context context, EventRecurrence recurrence, String timezone) {       
		if(context == null) return;
		String quantityString = "";
		Resources r;
		try {
			r = context.getResources();
		} catch(Exception e) {
			Log.e(TAG, "can not get the resources for app");
			return;
		}
		//1. Parse repeat string first.
		if(r == null) return;
		int interval = recurrence.interval <= 1 ? 1 : recurrence.interval;
        switch (recurrence.freq) {
            case EventRecurrence.DAILY:
                quantityString = r.getQuantityString(com.htc.lib1.HtcCalendarFramework.R.plurals.recurrence_interval_daily, interval);
                mRepeatString = String.format(java.util.Locale.US, quantityString, interval);
                break;
            case EventRecurrence.WEEKLY: {
				//gerald to do
                if (recurrence.repeatsOnEveryWeekDay() && recurrence.interval <= 1) {
                    mRepeatString = r.getString(com.htc.lib1.HtcCalendarFramework.R.string.every_weekday);
                } else {
                	String string;

                    int dayOfWeekLength = DateUtils.LENGTH_MEDIUM;
                    if (recurrence.bydayCount == 1) {
                        dayOfWeekLength = DateUtils.LENGTH_LONG;
                    }

                    StringBuilder days = new StringBuilder();

                    // Do one less iteration in the loop so the last element is added out of the
                    // loop. This is done so the comma is not placed after the last item.

                    if (recurrence.bydayCount > 0) {
                        int count = recurrence.bydayCount - 1;
                        for (int i = 0 ; i < count ; i++) {
                            days.append(dayToString(recurrence.byday[i], dayOfWeekLength));
                            days.append(", ");
                        }
                        days.append(dayToString(recurrence.byday[count], dayOfWeekLength));

                        string = days.toString();
                    } else {
                        // There is no "BYDAY" specifier, so use the day of the
                        // first event.  For this to work, the setStartDate()
                        // method must have been used by the caller to set the
                        // date of the first event in the recurrence.
                        if (recurrence.startDate == null) {
                            return;
                        }

                        int day = EventRecurrence.timeDay2Day(recurrence.startDate.weekDay);
                        string = dayToString(day, DateUtils.LENGTH_LONG);
                    }
                    mRepeatString =  r.getQuantityString(com.htc.lib1.HtcCalendarFramework.R.plurals.weekly, interval, interval, string);                	
                }
                break;
            }
            case EventRecurrence.MONTHLY: {
                quantityString = r.getQuantityString(com.htc.lib1.HtcCalendarFramework.R.plurals.recurrence_interval_monthly, interval);
                mRepeatString = String.format(java.util.Locale.US, quantityString, interval);
                if(recurrence.bydayCount == 1) {
                    int weekday = recurrence.startDate.weekDay;
                    cacheMonthRepeatStrings(r, context,weekday);
                    int dayNumber = (recurrence.startDate.monthDay - 1) / 7;
                    StringBuilder sb = new StringBuilder();
                    sb.append(mRepeatString);
                    sb.append(" (");
                    sb.append(mMonthRepeatByDayOfWeekStrs[weekday][dayNumber]);
                    sb.append(")");
                    mRepeatString = sb.toString();
                }
            	break;
            }
            case EventRecurrence.YEARLY:
                quantityString = r.getQuantityString(com.htc.lib1.HtcCalendarFramework.R.plurals.recurrence_interval_yearly, interval);
                mRepeatString = String.format(java.util.Locale.US, quantityString, interval);
            	break;
        }  
        
        //2. Parse EndsAt string.
        if(recurrence.count != 0) {
        	mEndsAtString = r.getQuantityString(com.htc.lib1.HtcCalendarFramework.R.plurals.endByCount, recurrence.count, recurrence.count);
    	} else if(!TextUtils.isEmpty(recurrence.until)) {
    		Time untilTime = new Time(timezone);
            untilTime.parse(recurrence.until);
            untilTime.normalize(true);
            long millis = untilTime.toMillis(false);
            mEndsAtString = TimeDisplayUtils.formatDateRange(context, millis, millis, DateUtils.FORMAT_SHOW_DATE);
    	}
    }

	/**
     * Converts day of week to a String.
     * @param day a EventRecurrence constant
     * @return day of week as a string
     */
    private String dayToString(int day, int dayOfWeekLength) {
        return DateUtils.getDayOfWeekString(dayToUtilDay(day), dayOfWeekLength);
    }

    /**
     * Converts EventRecurrence's day of week to DateUtil's day of week.
     * @param day of week as an EventRecurrence value
     * @return day of week as a DateUtil value.
     */
    private int dayToUtilDay(int day) {
        switch (day) {
	        case EventRecurrence.SU: return Calendar.SUNDAY;
	        case EventRecurrence.MO: return Calendar.MONDAY;
	        case EventRecurrence.TU: return Calendar.TUESDAY;
	        case EventRecurrence.WE: return Calendar.WEDNESDAY;
	        case EventRecurrence.TH: return Calendar.THURSDAY;
	        case EventRecurrence.FR: return Calendar.FRIDAY;
	        case EventRecurrence.SA: return Calendar.SATURDAY;
	        default: throw new IllegalArgumentException("bad day argument: " + day);
        }
    }   
    
    /**
     * Get the readable repeat string from rRule
     * @return readable repeat string
     */
    public String getRepeatString() {
    	return mRepeatString;
    }
    
    /**
     * Get the readable ends on string from rRule
     * @return readable ends on string
     */
    public String getEndsAtString() {
    	return mEndsAtString;
    }
    
    private static void cacheMonthRepeatStrings(Resources r, Context c, int weekday) {
        if (mMonthRepeatByDayOfWeekIds == null) {
            mMonthRepeatByDayOfWeekIds = new int[7];
            
            mMonthRepeatByDayOfWeekIds[0] = com.htc.lib1.HtcCalendarFramework.R.array.repeat_by_nth_sun;
            mMonthRepeatByDayOfWeekIds[1] = com.htc.lib1.HtcCalendarFramework.R.array.repeat_by_nth_mon;
            mMonthRepeatByDayOfWeekIds[2] = com.htc.lib1.HtcCalendarFramework.R.array.repeat_by_nth_tues;
            mMonthRepeatByDayOfWeekIds[3] = com.htc.lib1.HtcCalendarFramework.R.array.repeat_by_nth_wed;
            mMonthRepeatByDayOfWeekIds[4] = com.htc.lib1.HtcCalendarFramework.R.array.repeat_by_nth_thurs;
            mMonthRepeatByDayOfWeekIds[5] = com.htc.lib1.HtcCalendarFramework.R.array.repeat_by_nth_fri;
            mMonthRepeatByDayOfWeekIds[6] = com.htc.lib1.HtcCalendarFramework.R.array.repeat_by_nth_sat;
        }
        if (mMonthRepeatByDayOfWeekStrs == null) {
            mMonthRepeatByDayOfWeekStrs = new String[7][];
        }
        if (mMonthRepeatByDayOfWeekStrs[weekday] == null) {
            mMonthRepeatByDayOfWeekStrs[weekday] =
                    r.getStringArray(mMonthRepeatByDayOfWeekIds[weekday]);
        }
    }
}
