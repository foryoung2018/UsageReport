package com.htc.lib1.HtcCalendarFramework.util.calendar.vcalendar;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.QuotedPrintableCodec;

import com.htc.lib1.HtcCalendarFramework.util.calendar.vcalendar.VCalendarUtils;
import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
import com.htc.lib1.HtcCalendarFramework.util.calendar.tools.UriTools;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.Events;
import android.util.Log;
import com.htc.lib1.HtcCalendarFramework.provider.HtcExCalendar.ExEvents;
import com.htc.lib1.HtcCalendarFramework.util.calendar.VersionCheckUtils;

/**
  * Htc VCalendar class
  * {@exthide}
  */
public final class HtcVCalendar {
    
    private static final String TAG = "HtcVCalendar";
    private static final boolean DEBUG = HtcWrapHtcDebugFlag.Htc_DEBUG_flag;
    private long mDtStart      = -1;
    private long mDtEnd        = -1;
    private boolean bIsAllDay  = false;
    private String mDuration   = "";
    private String mTitle      = "";
    private String mLocation   = "";    
    private String mRrule      = "";
    private String mOrganizer  = "";
    private String mVEventString =""; 
    
    /**
      * The HtcVCalendar constructor
      * used in CalendarBackupAgent
      */
    public HtcVCalendar() {
        
    }

    private static void Debug(String s) {
        if (DEBUG) {
            if (s != null) Log.i(TAG, s);
        }
    }
    
    private static final String[] getProjection() {
    	if(VersionCheckUtils.afterAPI21()) {
    		return EVENT_PROJECTION;
    	} else {
    		return EVENT_PROJECTION_OLD;
    	}
    }
    
    private static final String[] EVENT_PROJECTION = new String[] {
        Events.DESCRIPTION,         // 0
        Events.DTEND,               // 1
        Events.DTSTART,             // 2
        Events.DURATION,            // 3
        Events.HAS_ALARM,           // 4
        Events.LAST_DATE,           // 5
        Events.RRULE,               // 6
        Events.RDATE,               // 7
        Events.EXRULE,              // 8
        Events.EXDATE,              // 9
        Events.STATUS,              // 10
        Events.TITLE,               // 11
        Events.EVENT_LOCATION,      // 12
        Events.SYNC_DATA7,       // 13
        Events.CALENDAR_TIME_ZONE,            // 14
        Events._ID,                 // 15
        Events.CALENDAR_ID,         // 16
        Events.ALL_DAY,             // 17
        Events.EVENT_TIMEZONE,      // 18
        Events.SYNC_DATA9,   // 19
        Events.EVENT_END_TIMEZONE,
        Events.ORGANIZER, 
        Events.ACCOUNT_TYPE
    };
    
    /**
     * Event projection
     * 
     * @deprecated [Not use after L]
     */
    private static final String[] EVENT_PROJECTION_OLD = new String[] {
        Events.DESCRIPTION,         // 0
        Events.DTEND,               // 1
        Events.DTSTART,             // 2
        Events.DURATION,            // 3
        Events.HAS_ALARM,           // 4
        Events.LAST_DATE,           // 5
        Events.RRULE,               // 6
        Events.RDATE,               // 7
        Events.EXRULE,              // 8
        Events.EXDATE,              // 9
        Events.STATUS,              // 10
        Events.TITLE,               // 11
        Events.EVENT_LOCATION,      // 12
        ExEvents.ICALENDAR_UID,       // 13
        ExEvents.CALENDAR_TIME_ZONE,            // 14
        Events._ID,                 // 15
        Events.CALENDAR_ID,         // 16
        Events.ALL_DAY,             // 17
        ExEvents.LAST_UPDATETIME,     // 18
        Events.EVENT_TIMEZONE,      // 19
        ExEvents.DESC_MIME_TYPE,   // 20
        Events.EVENT_END_TIMEZONE,
        Events.ORGANIZER
    };
    
    /**
      * To get the title
      * @return the title
      */
    public String getTitle() {
        return mTitle;
    }
    
	 /**
     * To get the location
     * @return the location
     */
   public String getLocation() {
       return mLocation;
   }
   
   /**
    * To get the duraion
    * @return the duraion
    */
   public String getDuration() {
      return mDuration;
   }
   
   /**
    * To get the dtStart
    * @return the dtStart
    */
   public long getDtStart() {
      return mDtStart;
   }
   
   /**
    * To get the dtEnd
    * @return the dtEnd
    */
   public long getDtEnd() {
      return mDtEnd;
   }
   
   /**
    * To get the rrule
    * @return the rrule
    */
   public String getRule() {
      return mRrule;
   }
   
   /**
    * To get the organizer
    * @return the organizer
    */
   public String getOrganizer() {
      return mOrganizer;
   }
   
   /**
    * To check if the event is allay
    * @return the allday
    */
   public boolean isAllDay() {
      return bIsAllDay;
   }
	
   /**
    * To get the content
      * @return the VCalendar content string
      */
    public String getContent() {
        return VCalendarUtils.getVHeader() + mVEventString + VCalendarUtils.getVTail();
    }
    
    /**
     * To get the VCalendar's event body
     * @return the VCalendar event string
     */
    public String getVEvent() {
    	return mVEventString;
    }

/**
  * To build VCalendar
  * @param context the Context
  * @param uniEvent the uri of the uni event
  * @return the HtcVCalendar
  *  Hide Automatically by SDK Team [U12000]
  *  @hide
  */
    public HtcVCalendar buildVCalendar(Context context, Uri uniEvent) {
        Uri uri = UriTools.getEventsUri(context, uniEvent);
        if (uri == null) return null;

        ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(uri, getProjection(), null, null, null);
        if(c!=null && c.moveToFirst()) {
            VCalendarUtils myCal = new VCalendarUtils(c,context);
            mVEventString = myCal.getEvent();

            QuotedPrintableCodec quotedPrintableCodec = new QuotedPrintableCodec(myCal.getDefaultCharSet());
            try {
                mTitle = quotedPrintableCodec.decode(myCal.summary);
                mLocation = quotedPrintableCodec.decode(myCal.location);
                bIsAllDay = myCal.isAllDay;
                mDuration = myCal.duration;
                int colID = c.getColumnIndexOrThrow(Events.DTSTART);
                mDtStart = c.getLong(colID);
                colID = c.getColumnIndexOrThrow(Events.DTEND);
                mDtEnd = c.getLong(colID);
                colID = c.getColumnIndexOrThrow(Events.RRULE);
                mRrule = c.getString(colID);
                colID = c.getColumnIndexOrThrow(Events.ORGANIZER);
                mOrganizer = c.getString(colID);                
            } catch (DecoderException e) {
                e.printStackTrace();
            }
            Debug("Title is: " + mTitle);
            Debug("Summary: " + myCal.summary);
            Debug("VCAL:-->" + mVEventString);
            
            if(!c.isClosed()) {
                c.close();
            }
            return this;
        } else {
            return null;
        }
    }
    
    /**
      * To build the VCalendar from cursor
      * @param context the Context
      * @param c the Cursor
      * @return the HtcVCalendar
      */
    public HtcVCalendar buildVCalendarFromCursor(Context context, Cursor c) {
        if(c!=null) {
            VCalendarUtils myCal = new VCalendarUtils( c,context);
            mVEventString = myCal.getEvent();

            QuotedPrintableCodec quotedPrintableCodec = new QuotedPrintableCodec(myCal.getDefaultCharSet());
            try {
                mTitle = quotedPrintableCodec.decode(myCal.summary);
                mLocation = quotedPrintableCodec.decode(myCal.location);
                bIsAllDay = myCal.isAllDay;
                mDuration = myCal.duration;
                int colID = c.getColumnIndexOrThrow(Events.DTSTART);
                mDtStart = c.getLong(colID);
                colID = c.getColumnIndexOrThrow(Events.DTEND);
                mDtEnd = c.getLong(colID);
                colID = c.getColumnIndexOrThrow(Events.RRULE);
                mRrule = c.getString(colID);
              
            } catch (DecoderException e) {
                e.printStackTrace();
            }
            Debug("Title is: " + mTitle);
            Debug("Summary: " + myCal.summary);
            Debug("VCAL:-->" + mVEventString);

            return this;
        } else {
            return null;
        }
    }
}
