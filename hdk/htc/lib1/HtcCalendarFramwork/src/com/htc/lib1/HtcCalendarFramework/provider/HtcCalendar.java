package com.htc.lib1.HtcCalendarFramework.provider;

import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.CalendarContract;
import android.provider.CalendarContract.CalendarAlerts;

/**
 * HtcCalendar
 */
/**
 * {@exthide}
 * @deprecated [Not use after L]
 */
public class HtcCalendar {

/**
 * The logcat tag string
 */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public static final String TAG = "HtcCalendar";
    
    /** 
     * The authority string for the calendar provider, same as com.htc.provider.Calendar's AUTHORITY 
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public static final String AUTHORITY = CalendarContract.AUTHORITY;
    
    /** 
     * The uri to the authority for the calendar provider 
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
    
        /**
          * The HtcCalendar constructor
          * @deprecated [Not use any longer]          
          * */
    	/**@hide*/ 
        public HtcCalendar() {
        }
        
    /**
      * The associated with the notes
      */
    public final static class AssociatedNotes {

        /**
          * The associate notes at the time
          */
        public static final String ASSOCIATE_NOTES_AT_TIME         = "AssociatedNotesAtTime";

        /**
          * Not associate notes at the time
          */
        public static final String NOT_ASSOCIATE_NOTES_AT_TIME     = "NotAssociatedNotesAtTime";

        /**
          * The all events at the time
          */
        public static final String ALL_EVENTS_AT_TIME              = "AllEventsAtTime";
        /**
         * private constructor can't be instantiated
         */
        private AssociatedNotes()  {}
        
        /**
         * To query if any event is associated with note at current time
         */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public static Uri ASSOCIATE_NOTES_AT_TIME_CONTENT_URI     = Uri.withAppendedPath(AUTHORITY_URI, ASSOCIATE_NOTES_AT_TIME); 

        
        /**
         * To query if any event is not associated with note at the given time
         */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public static Uri NOT_ASSOCIATE_NOTES_AT_TIME_CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, NOT_ASSOCIATE_NOTES_AT_TIME);
        
        
        /**
         * To query all events is not associated with note at the given time
         */
        public static Uri ALL_EVENTS_AT_TIME_CONTENT_URI          = Uri.withAppendedPath(AUTHORITY_URI, ALL_EVENTS_AT_TIME);
        
        /**
          * The event columns
          */
        public static final class EventsColumns {
        
            /**
              * The EventColumns constructor
              * @deprecated [Not use any longer]
              */
        	/**@hide*/ 
            public EventsColumns() {
            }
            
            /**
              * The event column id
              */
            public static final String ID          = "_id";           // long type

            /**
              * The event column calendar id
              */
            public static final String CALENDAR_ID = "calendar_id";   // long type

            /**
              * The event column title
              */
            public static final String TITLE       = "title";         // String 

            /**
              * The event column begin time
              */
            public static final String BEGIN_TIME  = "begine_time";   // long

            /**
              * The event column end time
              */
            public static final String END_TIME    = "end_time";      // long

            /**
              * The event column is all day
              */
            public static final String IS_ALL_DAY  = "is_all_day";    // int

            /**
              * The event column event uri
              */
            public static final String EVENT_URI   = "event_uri";     // String

            /**
              * The event column is associated note
              */
            public static final String IS_ASSOCIATED_NOTE = "is_associated_note";  // long
        }
        
        /**
          * The event column items
          */
        public static final String[] EVENTS_PROJECTION =
        {
            EventsColumns.ID,
            EventsColumns.CALENDAR_ID,
            EventsColumns.TITLE,
            EventsColumns.BEGIN_TIME,
            EventsColumns.END_TIME,
            EventsColumns.IS_ALL_DAY,
            EventsColumns.EVENT_URI,
            EventsColumns.IS_ASSOCIATED_NOTE
        };        
    }
}  
    
