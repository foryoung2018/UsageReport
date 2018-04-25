package com.htc.lib1.HtcCalendarFramework.util.calendar.tools;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;

/**
  * The time display utility class
  * {@exthide}
  */
public class TimeDisplayUtils {
    
/**
 * Define the system date format string
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public static String system_date_format;

/**
 * Define the system date format short string
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public static String system_date_format_short;

/**
 * Define the default date format short string
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public static String DEFAULT_DATE_FORMAT_SHORT = "EE, MMM d";

/**
 * Define the default date format string
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public static String DEFAULT_DATE_FORMAT = "EE, MMM d, yyyy";
    
    /**
      * Fetch the system date format
      * @param context the Context
      */
    public static void fetchSystemDateFormat(Context context) {
        system_date_format = Settings.System.getString(context.getContentResolver(), Settings.System.DATE_FORMAT);
        system_date_format_short = Settings.System.getString(context.getContentResolver(), "date_format_short"/*Settings.System.DATE_FORMAT_SHORT*/);//gerald todo
    }
    
    // +GB Porting, GB's formatDateRange
    /**
     * Formats a date or a time range according to the local conventions.
     *
     * @param context the context is required only if the time is shown
     * @param startMillis the start time in UTC milliseconds
     * @param endMillis the end time in UTC milliseconds
     * @param flags a bit mask of options See
     * {@link #formatDateRange(Context, Formatter, long, long, int, String) formatDateRange}
     * @return a string containing the formatted date/time range.
     */
    public static String formatDateRange(Context context, long startMillis,
            long endMillis, int flags) {
        //return mTZUtils.formatDateRange(context, startMillis, endMillis, flags);
        return formatDateRange_HTC(context, startMillis, endMillis, flags);
    }
    // -GB Porting    
        
    private static boolean showDate(long begin, long end) {
        Time beginTime = new Time();
        beginTime.set(begin);

        Time endTime = new Time();
        endTime.set(end);

        int julianDayBegin = Time.getJulianDay(begin, beginTime.gmtoff);
        int julianDayEnd = Time.getJulianDay(end, endTime.gmtoff);

        if (end - begin > DateUtils.DAY_IN_MILLIS) {
            return true;
        } else if (julianDayBegin != julianDayEnd) {
            return true;
        } else {
            return false;
        }
    }

/**
  * Format the date range
  * @param context the Context
  * @param begin the begin time
  * @param end the end time
  * @param flags the flags
  * @return the date string
  *  Hide Automatically by SDK Team [U12000]
  *  @hide
  */
    public static String formatDateRange_HTC(Context context, long begin, long end, int flags) {
        if (context == null) {
            return "";
        }
        final int FORMAT_SHOW_TIME = 0x00001;
        final int FORMAT_SHOW_WEEKDAY = 0x00002;

        boolean showTime = (flags & FORMAT_SHOW_TIME) != 0;
        boolean showWeekDay = (flags & FORMAT_SHOW_WEEKDAY) != 0;

        String strReturn = "";

        if (showTime) {
            // show time
            if(end == Long.MIN_VALUE) {
                strReturn = getDateString(begin, true) + " " + DateFormat.getTimeFormat(context).format(begin);
            } else {
                if (begin == end) {
                    strReturn = DateFormat.getTimeFormat(context).format(begin);
                } else {
                    String strBegin = DateFormat.getTimeFormat(context).format(begin);
                    String strEnd = DateFormat.getTimeFormat(context).format(end);

                    if (showDate(begin, end)) {
                        strBegin = getDateString(begin, false) + " " + strBegin;
                        strEnd = getDateString(end, false) + " " + strEnd;
                    }
                    strReturn = strBegin + " - " + strEnd;
                }
            }
        } else if(showWeekDay) {
                // show day of week
                strReturn = DateUtils.formatDateRange(context,begin, end, flags).toString();  
        } else {
            // show date
            strReturn = getDateString(begin, true);
        }
        return strReturn;
    }

    
    /**
     * Get date string
     * @param millis the milli seconds
     * @param fully_date_format boolean if it is fully date formatted
     * @return the date string
     */
    public static String getDateString(long millis, boolean fully_date_format) {
        //Log.d(TAG, "fully_date_format:"+fully_date_format+" system_date_format:"+system_date_format+" system_date_format_short:"+system_date_format_short);
        String date_format = "";
        if(fully_date_format) {
            if(TextUtils.isEmpty(system_date_format)) {
                system_date_format = DEFAULT_DATE_FORMAT;
            }
            date_format = system_date_format;
        } else {
            if(TextUtils.isEmpty(system_date_format_short)) {
                system_date_format_short = DEFAULT_DATE_FORMAT_SHORT;
            }
            date_format = system_date_format_short;
        }
        //Log.v(TAG, "date_format: "+date_format);
        CharSequence resultDateFormat = DateFormat.format(date_format, millis);
        //Log.v(TAG, "resultDateFormat: "+resultDateFormat);
        return resultDateFormat.toString();
    }

    /**
      * Format the date time
      * @param context the Context
      * @param time the timestamp
      * @param flags the flags
      * @return the format date range string
      */
    public static String formatDateTime( Context context, long time, int flags) {
        if(context == null) {
            return "";
        }
        return formatDateRange(context, time, time, flags);
    }
    
    /**
     * Formats a date or a time range according to the local conventions.
     *
     * @param context the context is required only if the time is shown
     * @param millis the time in UTC milliseconds
     * @param isAllday the event is allday or not
     * @return a string containing the formatted date/time range.
     *  Hide Automatically by SDK Team [U12000]
     *  @hide
     */
    public static String formatDateRange(Context context, long millis, boolean isAllday) {
        int flags;
        if (isAllday) {
            flags = DateUtils.FORMAT_UTC | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_DATE;
        } else {
            flags = DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_DATE; 
            if (DateFormat.is24HourFormat(context)) {
                flags |= DateUtils.FORMAT_24HOUR;
            }
        }
        return formatDateRange_HTC(context, millis, Long.MIN_VALUE, flags);
    }       
}
