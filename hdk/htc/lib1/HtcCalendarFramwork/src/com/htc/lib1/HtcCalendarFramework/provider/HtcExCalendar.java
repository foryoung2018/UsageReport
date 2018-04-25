package com.htc.lib1.HtcCalendarFramework.provider;

import android.net.Uri;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.provider.BaseColumns;
import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.content.ContentValues;
import android.provider.CalendarContract;

/**
 * The CalendarContract extension class
 * @exthide
 */
public final class HtcExCalendar extends HtcCalendarContract {

	private static final String AUTHORITY = CalendarContract.AUTHORITY;

    /**
     * get the definition constant string of action event changed
     * @deprecated [Not use after L]
     */
    public static final String ACTION_EVENT_CHANGED = "com.htc.calendar.event_changed";

    /**
     * get the type of Google account
     * @return the "com.google" string
     */
    public static String getGoogleAccountType() {
        return "com.google";
    }

    /**
     *  get the account type of Htc Eas
     *  @return "com.htc.android.mail.eas" string
     */
    public static String getHtcEasAccountType() {
        return "com.htc.android.mail.eas";
    }

    /**
     *  get the account type of Htc PC sync
     *  @return "com.htc.pcsc" string
     */
    public static String getHtcPcSyncAccountType() {
        return "com.htc.pcsc";
    }

    /**
     *  get the account type of Htc Task
     *  @return "com.htc.android.mail.eas/task" string
     */
    public static String getHtcTaskAccountType() {
        return "com.htc.android.mail.eas/task";
    }  

    /**
     *  get the account type of Htc Window Live
     *  @return "com.htc.android.windowslive" string
     */
    public static String getHtcWindowsLiveAccountType() {
        return "com.htc.android.windowslive";
    }

    /**
     *  get the account type of Htc Local Task
     *  @return "com.htc.task/task" string
     */
    public static String getHtcLocalTaskAccountType() {
        return "com.htc.task/task";
    }
    
    /**
     *  get the account type of Htc Greeting
     *  @return "HTC_BirthdayEvent" string
     */
    public static String getHtcGreetingAccountType(){
		return "HTC_BirthdayEvent";
    }

    /**
     *  get the account type of Htc Windows Live Task
     *  @return "com.htc.android.windowslive/task" string
     */
    public static String getHtcWindowsLiveTaskAccountType(){
        return "com.htc.android.windowslive/task";
    }
    
    /**
     *  get the account type of Htc Google Task
     *  @return "com.google/task" string
     */
    public static String getHtcGooleTaskAccountType(){
		return "com.google/task";
    }
    /**
     *  get the account type of Htc Google Task
     *  @return "com.htc.family" string
     */
    public static String getHtcFamilyAccountType(){
		return "com.htc.family";
    }

    private static int isFacebookAccountTypeChecked = -1;   // -1: never did this, 0: not sso, 1: sso

    /**
     *  get the account type of Htc Facebook
     *  @param context The Context
     *  @return "com.facebook.auth.login" when Facebook account type is checked, "com.htc.socialnetwork.facebook" when Facebook account type is unchecked
     */
    public static String getHtcFacebookAccountType(Context context) {
         
           String FacebookSSOType= "com.facebook.auth.login";
           String FacebookType = "com.htc.socialnetwork.facebook";
         
         if (isFacebookAccountTypeChecked == -1) {
             isFacebookAccountTypeChecked = 0;
             AuthenticatorDescription[] authDescs;
             authDescs = AccountManager.get(context).getAuthenticatorTypes();
             int len = authDescs.length;
             for (int i = 0; i < len; i++) {
//                String tt =authDescs[i].type;   
		// TODO: to check is value
                if (FacebookSSOType.equals(authDescs[i].type)) {
                    isFacebookAccountTypeChecked = 1;
                }
             }
         }     
         return ((isFacebookAccountTypeChecked == 1) ? FacebookSSOType : FacebookType);
    }
  

    /**
     *  get the content provider URI for myphonebook tracking
     */
    public static final Uri MYPHONEBOOK_TRACKING_URI = 
        Uri.parse("content://"+AUTHORITY+"/myphonebook_tracking");

    /**
     * get the definition constant value for pcsc cal ID
     */
    public static final int PCSC_CAL_ID = 1;       

    /**
     *get the definition constant value for myphonebook cal id
     */
    public static final int MYPHONEBOOK_CAL_ID = 5;

    /**
     * Display order
     * @deprecated [Not use after L]
     */
    protected interface CalendarsDisplayOrder 
    {
        /**
        * This calendar would be display on top position
        * <P>Type: INTEGER</P>
        * @deprecated [Not use after L]
        */
        public static final int DISPLAY_ORDER_TOP = 0;

        /**
        * Those Google calendar would be display on middle position
        * <P>Type: INTEGER</P>
        * @deprecated [Not use after L]
        */      

        public static final int DISPLAY_ORDER_GOOGLE = 10;

        /**
        * Those exchange calendar would be display on last two position.
        * <P>Type: INTEGER</P>
        * @deprecated [Not use after L]
        */      
        public static final int DISPLAY_ORDER_EXCHANGE = 50;
        
        /**
        * Those outlook calendar would be display on last one position.
        * <P>Type: INTEGER</P>
        * @deprecated [Not use after L]
        */      

        public static final int DISPLAY_ORDER_OUTLOOK = 51;
        
        /**
         * Those people event calendar would be display on last position.
         * <P>Type: INTEGER</P>
         * @deprecated [Not use after L]
         */
        public static final int DISPLAY_ORDER_PEOPLE_EVENT = 52;
    }
    
    /**
     * Event column extension interface
     */
    protected interface ExEventsColumns {
        
        //[hTC]+, add. HTC: PIMSYNC:
        
        /**
         * The GUID of this event for vCalendar / iCalendar
         * <P>Type: TEXT</P>
         * @deprecated [Not use after L]
         */
        public static final String ICALENDAR_UID = "iCalGUID";
        
        /**
          * Parent id of extend event column
          * @deprecated [Not use after L]
          */
        public static final String PARENT_ID = "parentID";

        /**
          * Last update time of extend event column
          * @deprecated [Not use after L]
          */
        public static final String LAST_UPDATETIME = "last_update_time";

        /**
          * Meeting status of extend event column
          * @deprecated [Not use after L]
          */
        public static final String MeetingStatus = "MeetingStatus"; 

        /**
          * Importance of extend event column
          * @deprecated [Not use after L]
          */
        public static final String IMPORTANCE = "importance";
        

        /**
          * Importance low of extend event column
          * @deprecated [Not use after L]
          */
        public static final int IMPORTANCE_LOW = 0;

        /**
          * Importance normal of extend event column
          * @deprecated [Not use after L]
          */
        public static final int IMPORTANCE_NORMAL = 1;

        /**
          * Importance high of extend event column
          * @deprecated [Not use after L]
          */
        public static final int IMPORTANCE_HIGH = 2;
        
        /**
          * description mime type of extend event column
          * @deprecated [Not use after L]
          */
        public static final String DESC_MIME_TYPE = "desc_mime_type";
        
        /**
         * description html description type of extend event column
         * @deprecated [Not use after L]
         */
        public static final String HTML_DESCRIPTION = "html_description";

        /**
          * the mime type text plain of extend event column
          */
        public static final int MIME_TYPE_TEXT_PLAIN = 0;

        /**
          * the mime type text html of extend event column
          */
        public static final int MIME_TYPE_TEXT_HTML = 1;
        //[hTC]-
        
    }
    
    /**
     * Calendar column extension interface
     */
    protected interface ExCalendarsColumns {
        //[hTC]+, add. HTC: PIMSYNC:       
        /**
         * Should the calendar be hide declined events in the calendar,
         * default value is 0 (do not hide), otherwise 1.
         * <P>Type: INTEGER (boolean)</P>
         * @deprecated [Not use after L]
         */
        public static final String HIDE_DECLINED = "hide_declined";
        
        /**
         * What type does calendar shown reminder?
         * <p>"0" means alert.</p> 
         * <p>"1" means status bar.</p>
         * <p>"2" means off.</p>
         * <P>Type: TEXT</P>
         * @deprecated [Not use after L]
         */        
        public static final String REMINDER_TYPE = "reminder_type";
        
        /**
         * How long would reminder you?
         * <P>Type: TEXT</P>
         * @deprecated [Not use after L]
         */  
        public static final String REMINDER_DURATION = "reminder_duration";
        
        /**
         * Does it needs vibrate when reminder?
         * <P>Type: INTEGER (boolean)</P>
         * @deprecated [Not use after L]
         */
        public static final String ALERTS_VIBRATE = "alerts_vibrate";
        
        /**
         * What is ringtone when reminder?
         * <P>Type: TEXT</P>
         * @deprecated [Not use after L]
         */
        public static final String ALERTS_RINGTONE = "alerts_ringtone"; 

        /**
         * sync source from Google
         * @deprecated [Not use after L]
         */
        public static final int SYNC_SOURCE_GOOGLE = 0;

        /**
         * sync source from exchange
         * @deprecated [Not use after L]
         */
        public static final int SYNC_SOURCE_EXCHANGE = 1;

        /**
         * sync source from Calendar app
         * @deprecated [Not use after L]
         */
        public static final int SYNC_SOURCE_CALENDAR_APP = 10;

        /**
         * sync source from Contacts app
         * @deprecated [Not use after L]
         */
        public static final int SYNC_SOURCE_CONTACTS_APP = 11;        

        /**
         * no sync source
         * @deprecated [Not use after L]
         */
        public static final int SYNC_SOURCE_NULL = 99;
        
        /**
         * the definition constant string for Calendar owner default
         */
        public static final String CALENDAR_OWNER_DEFAULT = "default@htc.calendar";

        /**
         * the definition constant string for Calendar owner exchange
         */
        public static final String CALENDAR_OWNER_EXCHANGE = "exchange@htc.calendar";

        /**
         * the definition constant string for Calendar owner outlook
         */
        public static final String CALENDAR_OWNER_OUTLOOK = "Outlook";

        //[hTC]-
    }

    /**
     * The interface for Facebook columns
     */    
    //[hTC]+ ,add for Facebook
    protected interface FacebookColumns {
        /**
         * A key to uniquely identify the set of Facebook event.
         * <P>Type: INTEGER (STRING)</P>
         * @deprecated [Not use after L]
         */
        public static final String FACEBOOK_SOURCE_ID = "facebook_source_id";

        /**
         * A key to identify the set of Facebook event's type.
         * <P>Type: INTEGER (STRING)</P>
         * @deprecated [Not use after L]
         */
        public static final String FACEBOOK_TYPE = "facebook_type";

        /**
         * the definition constant value for the birthday of Facebook type
         */
        public static final int FACEBOOK_TYPE_BIRTHDAY = 0;

        /**
         * the definition constant value for the event read only of Facebook type
         */
        public static final int FACEBOOK_TYPE_EVENT_READ_ONLY = 1;

        /**
         * the definition constant value for the event editor of Facebook type
         */
        public static final int FACEBOOK_TYPE_EVENT_EDITOR = 2;

        /**
         * the definition constant value for the event owner of Facebook type
         */
        public static final int FACEBOOK_TYPE_EVENT_OWNER = 3;

        /**
         * A key to identify the set of Facebook event's large image URL path.
         * <P>Type: TEXT</P>
         * @deprecated [Not use after L]
         */
        public static final String FACEBOOK_AVATAR_LARGE = "facebook_avatar_large";

        /**
         * A key to identify the set of Facebook event's small image URL path.
         * <P>Type: TEXT</P>
         * @deprecated [Not use after L]
         */
        public static final String FACEBOOK_AVATAR_SMALL = "facebook_avatar_small";

        /**
         * A key to identify the set of Facebook event's local image URL path.
         * <P>Type: TEXT</P>
         * @deprecated [Not use after L]
         */
        public static final String FACEBOOK_AVATAR_LOCAL = "facebook_avatar_local";
    }
    //[hTC]-, add for Facebook

    /**
     * The reminder column extensions
     */    
    protected interface ExRemindersColumns {
    
        /**
         * the definition contant value for method none
         * @deprecated [Not use after L]
         */
        public static final int METHOD_NONE = 100; //hTC, add

        /**
         * the definition contant value for method htc sms
         * @deprecated [Not use after L]
         */
        public static final int METHOD_HTC_SMS = 101; //hTC, add
    }
    
    /**
     * Event extensions
     */
    public static final class ExEvents extends HtcCalendarContract.Events implements ExEventsColumns, FacebookColumns, HtcCalendarContract.CalendarColumns, ExCalendarsColumns{
        /**
          * The ExEvents Constructor
          * @deprecated [Not use any longer]
          */
    	/**@hide*/ 
        public ExEvents() {
        }
    }
    
    /**
     * Calendar extensions
     */
    public static final class ExCalendars extends HtcCalendarContract.Calendars 
	        implements ExCalendarsColumns, CalendarsDisplayOrder {
        /**
         * Should the calendar be hidden in the calendar selection panel?
         * <P>Type: INTEGER (boolean)</P>
         * @deprecated [Not use after L]
         */
        
        //[hTC]+, add. HTC: PIMSYNC:      
        public static final Uri SETTING_ID_URI = 
            Uri.parse("content://" + AUTHORITY + "/calendar_settings");
        
        /**
         * The display sort order for this table
         * <P>Type: INTEGER</P>
         * @deprecated [Not use after L]
         */
        public static final String DISPLAY_SORT_ORDER = "displayOrder";

        /**
         * The calendar id sort order for this table
         * @deprecated [Not use after L]
         */
        public static final String ID_SORT_ORDER = "_id";
        
        /**
         * The calendar is customized by hTC or not
         * <P>Type: INTEGER</P>
         * <p>"0" means no.</p> 
         * <p>"1" means yes.</p>
         * @deprecated [Not use after L]
         */
        public static final String IS_HTC_CUSTOMIZED_CALENDAR = "ishTCCustomizedCalendar";
        
        /**
          * Constructor of ExCalendars
          * @deprecated [Not use any longer]
          */
        /**@hide*/ 
        public ExCalendars() {
        }
        //[hTC]-
    }
    
    /**
     * Calendar alert extensions
     */
    public static final class ExCalendarAlerts extends CalendarAlerts implements ExCalendarsColumns {
    
        /**
          * The constructor of ExCalendarAlerts
          * @deprecated [Not use any longer]
          */
    	/**@hide*/ 
        public ExCalendarAlerts() {
        }
    }
    
    /**
     * Event reminder extensions
     */
    public static final class ExReminders extends Reminders implements ExRemindersColumns {
    
      /**
        * The ExReminders constructor
        * @deprecated [Not use any longer]
        */
      /**@hide*/ 
      public ExReminders() {
      }
    }
    
    /**
     * Event instance extensions
	 */
    public static final class ExInstances extends Instances {
        //[hTC]+, add. HTC: PIMSYNC:
        /**
         * The content:// style URL for this table
         * @deprecated [Not use after L]
         */
        public static final Uri CONTENT_BY_JULIANDAY_URI = 
            Uri.parse("content://" + AUTHORITY + "/instances/julianday");
        //[hTC]-, add. HTC: PIMSYNC:        
        
        /**
          * The ExInstances constructor
          * @deprecated [Not use any longer]
          */
        /**@hide*/ 
        public ExInstances() {
        }
    }
    
    /**
     * A table for store CalendarSmsAlerts
     * @deprecated [Not use after L]
     */
    public static final class CalendarSmsAlerts implements BaseColumns,
                    CalendarAlertsColumns, EventsColumns, CalendarColumns  {

        /**
          * The table name of CalendarSmsAlerts
          * @deprecated [Not use after L]
          */
        public static final String TABLE_NAME = "CalendarSmsAlerts";
        
        /**
          * The content uri of CalendarSmsAlerts
          * @deprecated [Not use after L]
          */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY +
                "/calendar_sms_alerts");

        /**
          * Where alarm exists of CalendarSmsAlerts
          * @deprecated [Not use after L]
          */
        private static final String WHERE_ALARM_EXISTS = EVENT_ID + "=?";

        /**
          * The content uri by instance of CalendarSmsAlerts
          * @deprecated [Not use after L]
          */
        public static final Uri CONTENT_URI_BY_INSTANCE =
            Uri.parse("content://" + AUTHORITY + "/calendar_sms_alerts/by_instance");

        /**
         * insert the SmsAlert
         * @param cr the ContentResolver object
         * @param eventId the event ID
         * @param begin the begin timestamp of the alarm
         * @param end the end timestamp of the alarm
         * @param alarmTime the alarm's time
         * @param minutes the alarm's period
         * @return the insert Uri data
         * @deprecated [Not use after L]
         */
        public static final Uri insert(ContentResolver cr, long eventId,
                long begin, long end, long alarmTime, int minutes) {
            ContentValues values = new ContentValues();
            values.put(CalendarAlerts.EVENT_ID, eventId);
            values.put(CalendarAlerts.BEGIN, begin);
            values.put(CalendarAlerts.END, end);
            values.put(CalendarAlerts.ALARM_TIME, alarmTime);
            long currentTime = System.currentTimeMillis();
            values.put(CalendarAlerts.CREATION_TIME, currentTime);
            values.put(CalendarAlerts.RECEIVED_TIME, 0);
            values.put(CalendarAlerts.NOTIFY_TIME, 0);
            values.put(CalendarAlerts.STATE, STATE_SCHEDULED);
            values.put(CalendarAlerts.MINUTES, minutes);
            return cr.insert(CONTENT_URI, values);
        }

        /**
         * query the SmsAlert
         * @param cr the ContentResolver object
         * @param projection the query items
         * @param selection the selection clause
         * @param selectionArgs the selection arguments
         * @param sortOrder the sql sort caluse
         * @return the Cursor object
         * @deprecated [Not use after L]
         */
        public static final Cursor query(ContentResolver cr, String[] projection,
                String selection, String[] selectionArgs, String sortOrder) {
            return cr.query(CONTENT_URI, projection, selection, selectionArgs,
                    sortOrder);
        }

        /**
         * to know if the alarm exists
         * @param cr the ContentResolver object
         * @param eventId the event ID
         * @return true when alarm exists, false when alarm doesn't exist
         * @deprecated [Not use after L]
         */
        public static final boolean alarmExists(ContentResolver cr, long eventId) {          
            String[] projection = new String[] { ALARM_TIME };
            Cursor cursor = query(cr,
                    projection,
                    WHERE_ALARM_EXISTS,
                    new String[] {
                        Long.toString(eventId),
                    },
                    null);
            boolean found = false;
            try {
                if (cursor != null && cursor.getCount() > 0) {
                    found = true;
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            return found;
        }
        

        /**
         * delete the alarm
         * @param cr the ContentResolver object
         * @param eventId the event ID
         * @deprecated [Not use any longer]
         */
        /**@hide
         * @deprecated [Not use after L]*/ 
        public static final void deleteAlarm(ContentResolver cr, long eventId) {
            // TODO, need this ??            
        }
    }        
}
