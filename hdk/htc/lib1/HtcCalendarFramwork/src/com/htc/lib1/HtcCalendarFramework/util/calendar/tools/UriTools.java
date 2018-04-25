package com.htc.lib1.HtcCalendarFramework.util.calendar.tools;

import java.util.List;

import android.text.format.Time;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.text.TextUtils;
import android.util.Log;
import com.htc.lib1.HtcCalendarFramework.util.calendar.EventInstance;
import com.htc.lib1.HtcCalendarFramework.util.calendar.VersionCheckUtils;
//import com.htc.util.mail.MailUtils;//gerald todo
import com.htc.lib1.HtcCalendarFramework.provider.HtcExCalendar;
/**
 * the uri tools class
 * @author thomaslin
 * {@exthide}
 */
public final class UriTools {
    
    private static final String TAG = "UriTools";
    private static final boolean DEBUG = HtcWrapHtcDebugFlag.Htc_DEBUG_flag;
    public static final String AUTHORITY = "com.htc.calendar";
    
    private static void Debug(String s) {
        if (DEBUG) {
            if (s != null) Log.i(TAG, s);
        }
    }
    

    private static final int EVENT_WITH_TIME     = 1;
    private static final int EVENTS_ID           = 2;
    private static final int THE_EVENT_WITH_GUID_TYPE = 3;
    private static final int THE_EVENT_WITH_MEETMAIL = 4;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(CalendarContract.AUTHORITY, "events/#/EventTime/#/#", EVENT_WITH_TIME);
        sUriMatcher.addURI(CalendarContract.AUTHORITY, "events/#", EVENTS_ID);
        sUriMatcher.addURI(CalendarContract.AUTHORITY, "the-event/", THE_EVENT_WITH_GUID_TYPE);
        /*
         * Sense 5 new feature: [3.6.9] Straight talk support:
         * first # is meeting mail _id of messages table.
         * second # is meeting mail _id of parts table.
         */
        sUriMatcher.addURI("mail", "messages/#/#", THE_EVENT_WITH_MEETMAIL); 
    }
    
    /**
      * Determine if HTC exchange event
      * @param type the string of the type
      * @return boolean true if it is the HTC's exchange event, else return false
      * @deprecated [Module internal use]
      */
    /**@hide*/ 
    public static boolean isHTCExchangeEvent(String type) {
        return TextUtils.equals(type, HtcExCalendar.getHtcEasAccountType())
                || TextUtils.equals(type,
                        HtcExCalendar.getHtcWindowsLiveAccountType());
    }
    
    /**
     * Determine if Google exchange event
     * @param type the string of the type
     * @return boolean true if it is the Google exchange event, else return false
     * @deprecated [Module internal use]
     */
   /**@hide*/ 
   public static boolean isGoogleExchangeEvent(String type) {
	   return TextUtils.equals(type, "com.google.android.exchange");
   }
    
    // Change isHTCFacebookEvent(String type) to isHTCFacebookEvent(Context context, String type)
    private static boolean isHTCFacebookEvent(Context context, String type) {
        return TextUtils.equals(type, HtcExCalendar.getHtcFacebookAccountType(context));
    }

    public static boolean isHTCPCSyncEvent(String type) {
        return TextUtils.equals(type, HtcExCalendar.getHtcPcSyncAccountType());
    }
    
    private static boolean isLocalAccountEvent(String type) {
        return TextUtils.equals(type, CalendarContract.ACCOUNT_TYPE_LOCAL);
    }
    
    /**
      * Determine if Google event
      * @param type the string of the type
      * @return boolean true if it is the Google's event, else return false
      * @deprecated [Module internal use]
      */
    /**@hide*/ 
    public static boolean isGoogleEvent(String type) {
        return TextUtils.equals(type, HtcExCalendar.getGoogleAccountType());
    }
    
    /**
      * return Event Uri format:
      * content://com.android.calendar/events/4233/EventTime/1292405400000/1292409000000
      *  Hide Automatically by SDK Team [U12000]
      *  @hide
      */
    public static Uri generateEventUri(long eventId, long startTime, long endTime) {
        
        return Uri.parse(generateEventUriString(eventId, startTime, endTime));
    }
    
    /**
      * To generate the event uri string
      * @param context the Context
      * @param eventId the event id
      * @return the event uri string
      *  Hide Automatically by SDK Team [U12000]
      *  @hide
      */
    public static String generateTheEventUriString(Context context, long eventId) {
        try {
            return generateTheEventUri(context, eventId).toString();
        } catch(Exception e) {
            // uri is null!!
            Log.e(TAG, "event not exist:" + eventId);
            return "";
        }
    }
    
/**
  * To converts the uni event to the event uri
  * @param context the Context
  * @param uniEvent the uni event uri
  *  Hide Automatically by SDK Team [U12000]
  *  @hide
  */
    public static Uri uniEventToEventUri(Context context, Uri uniEvent) {
        int match = sUriMatcher.match(uniEvent);
        if (match == EVENT_WITH_TIME) 
        	return uniEvent;
        else
        	return null;  
        
        /* Fong removed, uni-event not used for Note afterJB.
        if (match != UNI_EVENT_WITH_TIME) 
        	return null;
        
        Uri desUri = null;
        
        String eventId = uniEvent.getPathSegments().get(1);
        long startTime = Long.parseLong(uniEvent.getPathSegments().get(3));
        long endTime = Long.parseLong(uniEvent.getPathSegments().get(4));
        
        String accountType = uniEvent.getQueryParameter("type");
        String uid = uniEvent.getQueryParameter("uid");
        String selection = null;
        if (isHTCExchangeEvent(accountType)) {
            selection = Events.SYNC_DATA7;
        } else if (isGoogleEvent(accountType)) {
            selection = CalendarContract.Calendars._SYNC_ID;
        } else if (isHTCFacebookEvent(context, accountType)) {//(isHTCFacebookEvent(accountType)) {
            selection = Events.SYNC_DATA1;
        } else { //if (isHTCPCSyncEvent(accountType)) {
            selection = Events.SYNC_DATA7;
        }
        
        Cursor c = null;
        try {
            ContentResolver cr = context.getContentResolver();
            Uri uri = Uri.withAppendedPath(CalendarContract.CONTENT_URI, "events");
            c = cr.query(uri, new String [] {CalendarContract.Calendars._ID, 
                                             CalendarContract.Events.ALL_DAY}, 
                          selection+"=?", //selection, 
                          new String [] {uid},  //selectionArgs, 
                          null); //sortOrder)
            
            if (c.moveToFirst()) {
                Long _id = c.getLong(c.getColumnIndexOrThrow(CalendarContract.Calendars._ID));
                int    isAllDay = c.getInt(c.getColumnIndexOrThrow(CalendarContract.Events.ALL_DAY));
                
                if (isAllDay == 1) {
                    
                }
                
                desUri = generateEventUri(_id, startTime, endTime);
            } else {
                desUri = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            desUri = null;
        } finally {
            if (c != null) {
                if (!c.isClosed()) {
                    c.close();
                    c = null;
                }
            }
        }
            
        return desUri;*/
    }

/**
  * To generate the event uri string
  * @param eventId the event id
  * @param startTime the start timestamp
  * @param endTime the end timestamp
  * @return the event uri string
  *  Hide Automatically by SDK Team [U12000]
  *  @hide
  */
    public static String generateEventUriString(long eventId, long startTime, long endTime) {
        StringBuilder sb = new StringBuilder();
        sb.append("content://")
          .append(CalendarContract.AUTHORITY)
          .append("/events/")
          .append(eventId)
          .append("/EventTime/")
          .append(startTime)
          .append("/")
          .append(endTime);

        return sb.toString();
    }

    private static EventInstance doMeetingEvent(Context context, Uri uri) {
        EventInstance ei = null;
        String[] messProj = new String [] { "_subject", "_startTime", "_endTime", "_location"};
        Cursor messCursor = null;

        List<String> meetingUriSegments = uri.getPathSegments(); 
        if (meetingUriSegments.size() < 2) {
            Log.e(TAG, "doMeetingEvent() meetingUriSegments size illegal " + uri);
            return null;
        }
        String messageId = meetingUriSegments.get(1);
        String partId = meetingUriSegments.get(2);
        
        if (TextUtils.isEmpty(messageId) || TextUtils.isEmpty(partId)) {
            Log.e(TAG, "doMeetingEvent() Uri segments exception, uri is " + uri);
            return null;
        }
        try {
            messCursor = context.getContentResolver().query(Uri.parse("content://mail/messages"),
                    messProj, "_id=?", new String [] {messageId}, null);
            if ((messCursor != null) && (messCursor.moveToFirst())) {
                //message
                String title = messCursor.getString( messCursor.getColumnIndexOrThrow("_subject") );
                String location = messCursor.getString( messCursor.getColumnIndexOrThrow("_location") );
                String beginStr = messCursor.getString( messCursor.getColumnIndexOrThrow("_startTime") );
                String endStr = messCursor.getString( messCursor.getColumnIndexOrThrow("_endTime") );

                //parts
                String description = null;
                String htmlDescription = null;
                int mimeType = 0;
                Cursor partsCursor = null;
                try {
                    String[] partsProj = new String [] { "_mimetype", "_text"};
                    partsCursor = context.getContentResolver().query(Uri.parse("content://mail/parts"),
                            partsProj, "_id=?", new String [] {partId}, null);
                    if ((partsCursor != null) && (partsCursor.moveToFirst())) {
                        String mimetypeStr = partsCursor.getString( partsCursor.getColumnIndexOrThrow("_mimetype") );
                        String text = partsCursor.getString( partsCursor.getColumnIndexOrThrow("_text") );
                        if (mimetypeStr.equalsIgnoreCase("text/html")) {
                            htmlDescription = text;
                            mimeType = 1;
                            if(text != null) {
                                //gerald todo
                                //description = MailUtils.convertHTMLtoPlainText(text.trim());
                                description = text.trim();
                                description = description.replace("\r", "\r\n").replace("\n", "\r\n");
                            }
                        } else {
                            description = text;
                            mimeType = 0;
                        }
                    } else {
                        Log.i(TAG, "doMeetingEvent(), Cursor is null when query content://mail/parts, partId is " + partId);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "doMeetingEvent(), Exception occur when query content://mail/parts, parts is " + partId);
                    return null;
                } finally {
                    if (partsCursor != null) {
                        if (!partsCursor.isClosed()) {
                            partsCursor.close();
                            partsCursor = null;
                        }
                    }
                }

                long startTime = 0;
                long endTime = 0;
                Time t = new Time();
                if (!TextUtils.isEmpty(beginStr)) {
                    t.parse3339(beginStr);
                    startTime =  t.toMillis(false);
                }
                if (!TextUtils.isEmpty(endStr)) {
                    t.parse3339(endStr);
                    endTime =  t.toMillis(false);
                }
                /*
                 * [Sense 5] [3.6.9] Straight talk support:
                 * The EventInstance will take id -1 means meeting event. 
                 */
                long eventId = -1; 
                ei = new EventInstance(eventId, title, location, description, startTime, endTime, mimeType, htmlDescription);
            } else {
                Log.i(TAG, "doMeetingEvent(), Cursor is null when query content://mail/messages, messageId is " + messageId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "doMeetingEvent(), Exception occur when query content://mail/messages, messageId is " + messageId);
            return null;
        } finally {
            if (messCursor != null) {
                if (!messCursor.isClosed()) {
                    messCursor.close();
                    messCursor = null;
                }
            }
        }
        return ei;
    } 
    
    private static EventInstance doCheckEventExistForNotes(Context context, Uri uri) {
        EventInstance ei = null;        
        String title = "";           
        String syncAccountType = uri.getQueryParameter("type");
        String gUid = uri.getQueryParameter("uid");
        String[] selectionArgs= {syncAccountType, gUid};
        String selection = getCheckEventForNoteSelection(context, syncAccountType);
        
        String where = "account_type = (?) AND " + selection +" = (?) AND deleted <> 1";
        long   eventId; 
        long   begin;
        long   end;
        ContentResolver cr = context.getContentResolver();       
        Cursor c = null;
        try {
            c = cr.query(CalendarContract.Events.CONTENT_URI,   new String [] { CalendarContract.Events._ID, 
                                                CalendarContract.Events.TITLE,                                                
                                                CalendarContract.Events.DTSTART, 
                                                CalendarContract.Events.DTEND}, //projection, 
                                                where,                   //selection, 
                                                selectionArgs,           //selectionArgs,
                                                null);                   // sortOrder)
            if (c.moveToFirst()) {
                do {
                    eventId = c.getLong( c.getColumnIndexOrThrow(CalendarContract.Instances._ID) );
                    title = c.getString( c.getColumnIndexOrThrow(CalendarContract.Instances.TITLE) );  
                    begin = c.getLong( c.getColumnIndexOrThrow(CalendarContract.Events.DTSTART) );
                    end = c.getLong( c.getColumnIndexOrThrow(CalendarContract.Events.DTEND) );
                   
                    ei = new EventInstance(eventId, title, begin, end);
                    
                    break;
                  
                } while (c.moveToNext());
            } 
        } catch (Exception e) {
            e.printStackTrace();
            
        } finally {
            if (c != null) {
                if (!c.isClosed()) {
                    c.close();
                    c = null;
                }
            }
        }
        return ei;
    } 
    
    private static String getCheckEventForNoteSelection(Context context, String syncAccountType) {
    	String selection;
    	if(VersionCheckUtils.afterAPI21()) {
            if (isHTCExchangeEvent(syncAccountType) || isHTCPCSyncEvent(syncAccountType)) {
                selection = Events.SYNC_DATA7;
            } else if (isGoogleEvent(syncAccountType)) {
                selection = Events._SYNC_ID;
            } else if (isHTCFacebookEvent(context, syncAccountType)) {
                selection = Events.SYNC_DATA1;
            } else {
                selection = Events._ID;
            }
    	} else {            
            if (isHTCExchangeEvent(syncAccountType)) {
                selection = HtcExCalendar.ExEvents.ICALENDAR_UID;
            } else if (isGoogleEvent(syncAccountType)) {
                selection = CalendarContract.Calendars._SYNC_ID;
            } else if (isHTCFacebookEvent(context, syncAccountType)) {//(isHTCFacebookEvent(syncAccountType)) {
                selection = HtcExCalendar.ExEvents.FACEBOOK_SOURCE_ID;
            } else { //if (isHTCPCSyncEvent(accountType)) {
                selection = HtcExCalendar.ExEvents.ICALENDAR_UID;            
            }
    	}
    	
    	return selection;    	
    }
    
    private static EventInstance doCheckEventExist(Context context, long eventId, long startTime, long endTime) {
    	if(VersionCheckUtils.afterAPI21()) {
    		return doCheckEventExistL(context, eventId, startTime, endTime);
    	} else {
    		return doCheckEventExistOld(context, eventId, startTime, endTime);
    	}
    }
    
    private static EventInstance doCheckEventExistL(Context context, long eventId, long startTime, long endTime) {
        EventInstance ei = null;
        String title = "";
        String location = "";
        String description = "";
        String htmlDescription ="";
        long   begin = startTime;
        long   end = endTime;
        int    descMineType = 0;        
        
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(CalendarContract.Instances.CONTENT_URI, startTime + "/" + endTime);
        Cursor c = null;
        Cursor sCursor = null;
        try {
            c = cr.query(uri,   new String [] { CalendarContract.Instances._ID, 
                                                CalendarContract.Instances.TITLE,
                                                CalendarContract.Instances.EVENT_LOCATION,
                                                CalendarContract.Instances.DESCRIPTION,
                                                CalendarContract.Instances.BEGIN, 
                                                CalendarContract.Instances.END
                                                }, //projection, 
                                "event_id=" + eventId,            //selection, 
                                null,                             //selectionArgs,
                                null);                            // sortOrder)
            if (c.moveToFirst()) {
                do {
                    title = c.getString( c.getColumnIndexOrThrow(CalendarContract.Instances.TITLE) );
                    location = c.getString( c.getColumnIndexOrThrow(CalendarContract.Instances.EVENT_LOCATION) );
                    description = c.getString( c.getColumnIndexOrThrow(CalendarContract.Instances.DESCRIPTION) );
                    begin = c.getLong( c.getColumnIndexOrThrow(CalendarContract.Instances.BEGIN) );
                    end = c.getLong( c.getColumnIndexOrThrow(CalendarContract.Instances.END) );
                    sCursor = querySyncHtmlDescriptionByEventId(eventId, cr);
                    if(sCursor != null && sCursor.moveToFirst()) {
                            descMineType = sCursor.getInt(sCursor.getColumnIndexOrThrow(Events.SYNC_DATA9));
                            htmlDescription = sCursor.getString(sCursor.getColumnIndexOrThrow(Events.SYNC_DATA10) );
                    }
                    closeCursor(sCursor);
                    if ((begin==startTime) && (end==endTime)) {
                        ei = new EventInstance(eventId, title, location, description, begin, end, descMineType, htmlDescription);
                        break;
                    }
                    
                    descMineType = 0;
                    htmlDescription = "";
                } while (c.moveToNext());
            } 
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	closeCursor(c);
        	closeCursor(sCursor);
        }
        
        Debug("event is exist: " + (ei != null) + ", id:" + eventId +", title:" + title + ", begin:" + begin + ", end:" + end);
        
        return ei;
    }
    
    private static void closeCursor(Cursor c) {
        if (c != null) {
            if (!c.isClosed()) {
                c.close();
                c = null;
            }
        }
    }
    
    private static Cursor querySyncHtmlDescriptionByEventId(long eventId, ContentResolver cr) {     
        Cursor c = cr.query(CalendarContract.Events.CONTENT_URI,   new String [] {
                Events.SYNC_DATA9,
                Events.SYNC_DATA10 
                }, //projection, 
                Events._ID + "=" + eventId + " AND account_type = 'com.htc.android.mail.eas'",            //selection, 
                null,                             //selectionArgs,
                null);
        return c;
    }
    
    /**
     *  @deprecated [Not use after L]
     * */
    private static EventInstance doCheckEventExistOld(Context context, long eventId, long startTime, long endTime) {
        EventInstance ei = null;
        String title = "";
        String location = "";
        String description = "";
        String htmlDescription ="";
        long   begin = startTime;
        long   end = endTime;
        int    descMineType = 0;        
        
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(CalendarContract.Instances.CONTENT_URI, startTime + "/" + endTime);
        Cursor c = null;
        try {
            c = cr.query(uri,   new String [] { CalendarContract.Instances._ID, 
                                                CalendarContract.Instances.TITLE,
                                                CalendarContract.Instances.EVENT_LOCATION,
                                                CalendarContract.Instances.DESCRIPTION,
                                                CalendarContract.Instances.BEGIN, 
                                                CalendarContract.Instances.END,
                                                HtcExCalendar.ExEvents.DESC_MIME_TYPE,
                                                HtcExCalendar.ExEvents.HTML_DESCRIPTION
                                                }, //projection, 
                                "event_id=" + eventId,            //selection, 
                                null,                             //selectionArgs,
                                null);                            // sortOrder)
            if (c.moveToFirst()) {
                do {
                    title = c.getString( c.getColumnIndexOrThrow(CalendarContract.Instances.TITLE) );
                    location = c.getString( c.getColumnIndexOrThrow(CalendarContract.Instances.EVENT_LOCATION) );
                    description = c.getString( c.getColumnIndexOrThrow(CalendarContract.Instances.DESCRIPTION) );
                    begin = c.getLong( c.getColumnIndexOrThrow(CalendarContract.Instances.BEGIN) );
                    end = c.getLong( c.getColumnIndexOrThrow(CalendarContract.Instances.END) );
                    descMineType = c.getInt( c.getColumnIndexOrThrow(HtcExCalendar.ExEvents.DESC_MIME_TYPE) );
                    htmlDescription = c.getString( c.getColumnIndexOrThrow(HtcExCalendar.ExEvents.HTML_DESCRIPTION) );
                    
                    if ((begin==startTime) && (end==endTime)) {
                        ei = new EventInstance(eventId, title, location, description, begin, end, descMineType, htmlDescription);
                        break;
                    }
                } while (c.moveToNext());
            } 
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                if (!c.isClosed()) {
                    c.close();
                    c = null;
                }
            }
        }
        
        Debug("event is exist: " + (ei != null) + ", id:" + eventId +", title:" + title + ", begin:" + begin + ", end:" + end);
        
        return ei;
    }

/**
  * To get the event id
  * @param context the Context
  * @param uri the Uri
  * @return the event id
  *  Hide Automatically by SDK Team [U12000]
  *  @hide
  */
    public static long getEventId(Context context, Uri uri) {
    	long id = -1;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case EVENT_WITH_TIME: {
                Uri uriCheck = uniEventToEventUri(context, uri);
                if (uriCheck == null) break;
                
                id   = Long.parseLong(uriCheck.getPathSegments().get(1));

                break;
            }
            case THE_EVENT_WITH_GUID_TYPE: {
                EventInstance ei = doCheckEventExistForNotes(context, uri);
                if(ei != null) {
                    id = ei.getId();
                }                
                break;
            }
        }   
        
        return id;
    }
    
    private static Uri adjustEventUri(Context context, Uri eventUri) {
        int match = sUriMatcher.match(eventUri);
        if (match != EVENT_WITH_TIME) return null;
        
        long eventId   = Long.parseLong(eventUri.getPathSegments().get(1));
        long startTime = Long.parseLong(eventUri.getPathSegments().get(3));
        long endTime   = Long.parseLong(eventUri.getPathSegments().get(4));
        Uri desUri = null;
        
        Cursor c = null;
        try {
            ContentResolver cr = context.getContentResolver();
            Uri uri = Uri.withAppendedPath(CalendarContract.CONTENT_URI, "events");
            c = cr.query(uri, new String [] {CalendarContract.Events.ALL_DAY}, 
                          "_id=" + eventId, //selection, 
                          null,  //selectionArgs, 
                          null); //sortOrder)
            
            if (c.moveToFirst()) {
                int    isAllDay = c.getInt(c.getColumnIndexOrThrow(CalendarContract.Events.ALL_DAY));
                
                if (isAllDay == 1) {
                    startTime = offsetToUTCDay(startTime);
                    endTime = offsetToUTCDay(endTime);
                }
                
                desUri = generateEventUri(eventId, startTime, endTime);
            } else {
                desUri = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            desUri = null;
        } finally {
            if (c != null) {
                if (!c.isClosed()) {
                    c.close();
                    c = null;
                }
            }
        }
            
        return desUri;
    }

/**
  * To get the event instance
  * @param context the Context
  * @param uri the Uri
  * @return the event instance
  *  Hide Automatically by SDK Team [U12000]
  *  @hide
  */
    public static EventInstance getEventInstance(Context context, Uri uri) {
        EventInstance ei = null;
        Uri eventUri = null;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case EVENT_WITH_TIME: {
                eventUri = adjustEventUri(context, uri); 
                break;
            }

            case THE_EVENT_WITH_GUID_TYPE :{
                return doCheckEventExistForNotes(context, uri);
            }
            case THE_EVENT_WITH_MEETMAIL :{
                return doMeetingEvent(context, uri);
            }
        }   
        
        if (eventUri != null) {
            long eventId   = Long.parseLong(eventUri.getPathSegments().get(1));
            long startTime = Long.parseLong(eventUri.getPathSegments().get(3));
            long endTime   = Long.parseLong(eventUri.getPathSegments().get(4));
            
            ei = doCheckEventExist(context, eventId, startTime, endTime);
        }
        
        return ei;
    }

/**
  * To determine if the event exist or not
  * @param context the Context
  * @param uri the Uri
  * @return boolean true if the event exists else return false
  *  Hide Automatically by SDK Team [U12000]
  *  @hide
  */
    public static boolean isEventExist(Context context, Uri uri) {
        EventInstance ei = getEventInstance(context, uri);
        
        return (ei != null);
    }
    
    /**
      * return format:
      * content://com.android.calendar/events/4233
      *  Hide Automatically by SDK Team [U12000]
      *  @hide
      */
    public static Uri getEventsUri(Context context, Uri eventUri) {
        int match = sUriMatcher.match(eventUri);
        if (EVENTS_ID == match) return eventUri;
        if ( (EVENT_WITH_TIME == match)  ) {
            Uri uriCheck = uniEventToEventUri(context, eventUri);
            if (uriCheck == null) return null;
            
            long eventId   = Long.parseLong(uriCheck.getPathSegments().get(1));
            if(eventId == -1) return null;
            return ContentUris.withAppendedId(Events.CONTENT_URI, eventId);
        }
        if(THE_EVENT_WITH_GUID_TYPE == match) {            
            long eventId = getEventId(context, eventUri);
            if(eventId == -1) return null;
            return ContentUris.withAppendedId(Events.CONTENT_URI, eventId);                   
        }
        
        return null;        
    }
    
    // input localTime : Thu May 05 00:00:00 CST 2011
    // output            Thu May 05 08:00:00 CST 2011
    private static long offsetToUTCDay(long localTime) {
        Time t = new Time();  // local time
        t.set(localTime);
        int y = t.year;
        int m = t.month;
        int d = t.monthDay;
        
        t.switchTimezone(Time.TIMEZONE_UTC);
        t.set(0, 0, 0, d, m, y);
        
        long ret = t.normalize(true);
        Log.i(TAG, "offsetToUTCday: " + localTime + " to " + ret);
        return ret;
    }
    
/**
  * To generate the event uri
  * @param context the Context
  * @param eventId the event id
  *  Hide Automatically by SDK Team [U12000]
  *  @hide
  */
    public static Uri generateTheEventUri(Context context,  long eventId) { 
    	if(VersionCheckUtils.afterAPI21()) {
    		return generateTheEventUriNew(context, eventId);
    	} else {
    		return generateTheEventUriOld(context, eventId);
    	}     
    }
    
    private static Uri generateTheEventUriNew(Context context,  long eventId) { 
        StringBuilder sb = new StringBuilder();
        sb.append("content://")
          .append(CalendarContract.AUTHORITY)
          .append("/the-event");         
        
        Uri desUri = null;
        Uri uri = Uri.withAppendedPath(CalendarContract.CONTENT_URI, "events/" + eventId);
        Cursor c = null;
        try {
            ContentResolver cr = context.getContentResolver();           
            c = cr.query(uri, new String[] { CalendarContract.Calendars.ACCOUNT_TYPE,  // which account! 
                                         CalendarContract.Calendars._SYNC_ID,                      // google use this as uni-id
                                         Events.SYNC_DATA7,                     // exchange use this as uni-id
                                         Events.SYNC_DATA1 }
                                        , null, null, null);
            if (c.moveToFirst()) {
                Debug("generateTheEventUri(), event count: " + c.getCount());
                
                String accountType = c.getString(c.getColumnIndexOrThrow(CalendarContract.Calendars.ACCOUNT_TYPE));
                String syncId = c.getString(c.getColumnIndexOrThrow(CalendarContract.Calendars._SYNC_ID));
                
                String iUid = "";
                String facebookId = "";

                if(isHTCExchangeEvent(accountType) || isHTCPCSyncEvent(accountType)) {
                    iUid = c.getString(c.getColumnIndexOrThrow(Events.SYNC_DATA7));
                } else if (isHTCFacebookEvent(context, accountType)) {
                    facebookId = c.getString(c.getColumnIndexOrThrow(Events.SYNC_DATA1));
                } else {
                    iUid = Long.toString(eventId);
                }
                
                desUri = buildQueryParamterForNotesNew(context, sb.toString(), accountType, iUid, syncId, facebookId);       
            } else {
                desUri = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            desUri = null;
        } finally {
            if (c != null && !c.isClosed()) {               
                c.close();
                c = null;               
            }
        }
        return desUri;        
    }
    
    /**
     *  @deprecated [Not use after L]
     * */
    private static Uri generateTheEventUriOld(Context context,  long eventId) { 
    	   StringBuilder sb = new StringBuilder();
           sb.append("content://")
             .append(CalendarContract.AUTHORITY)
             .append("/the-event");         
           
           Uri desUri = null;
           Uri uri = Uri.withAppendedPath(CalendarContract.CONTENT_URI, "events/" + eventId);
           Cursor c = null;
           try {
               ContentResolver cr = context.getContentResolver();           
               c = cr.query(uri, new String[] { CalendarContract.Calendars.ACCOUNT_TYPE,  // which account! 
                                            CalendarContract.Calendars._SYNC_ID,                      // google use this as uni-id
                                            HtcExCalendar.ExEvents.ICALENDAR_UID,                     // exchange use this as uni-id
                                            HtcExCalendar.ExEvents.FACEBOOK_SOURCE_ID }  
                                           , null, null, null);
               if (c.moveToFirst()) {
                   Debug("generateTheEventUri(), event count: " + c.getCount());
                   
                   String accountType = c.getString(c.getColumnIndexOrThrow(CalendarContract.Calendars.ACCOUNT_TYPE));
                   String syncId = c.getString(c.getColumnIndexOrThrow(CalendarContract.Calendars._SYNC_ID));
                   String iUid   = c.getString(c.getColumnIndexOrThrow(HtcExCalendar.ExEvents.ICALENDAR_UID));
                   String facebookId = c.getString(c.getColumnIndexOrThrow(HtcExCalendar.ExEvents.FACEBOOK_SOURCE_ID));
                                   
                   desUri = buildQueryParamterForNotesOld(context, sb.toString(), accountType, iUid, syncId, facebookId);       
               } else {
                   desUri = null;
               }
           } catch (Exception e) {
               e.printStackTrace();
               desUri = null;
           } finally {
               if (c != null && !c.isClosed()) {                 
                   c.close();
                   c = null;   
               }
           }
           return desUri;        
    }
    
    /**
      * To generate the event VCalendar event title uri
      * @param context the Context
      * @param eventId the event id
      * @return the event VCalendar event title uri
      *  Hide Automatically by SDK Team [U12000]
      *  @hide
      */
    public static Uri generateTheEventVCalendarEventTitleUri(Context context,  long eventId) { 
        StringBuilder sb = new StringBuilder();
        sb.append("content://")
          .append(AUTHORITY)
          .append("/vcalendar")
          .append("/" + eventId);        
        
        Uri desUri = null;
        Uri uri = Uri.withAppendedPath(CalendarContract.CONTENT_URI, "events/" + eventId);
        Cursor c = null;
        try {
            ContentResolver cr = context.getContentResolver();           
            c = cr.query(uri, new String[] { Events.TITLE }, null, null, null);
            if (c.moveToFirst()) {
                Debug("generateTheEventVCalendarEventTitleUri(), event count: " + c.getCount());
                
                String title = c.getString(c.getColumnIndexOrThrow(Events.TITLE));
                title = title.replace('/', '_');
                title += ".vcs";
                sb.append("/" + title);
                desUri = Uri.parse(sb.toString());
                desUri = desUri.buildUpon().build();
            } else {
                desUri = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            desUri = null;
        } finally {
            if (c != null) {
                if (!c.isClosed()) {
                    c.close();
                    c = null;
                }
            }
        }
        return desUri;
        
    }
    
/**
  * Convert to event instance
  * @param context the Context
  * @param uri the string of the uri
  * @return the event instance
  *  Hide Automatically by SDK Team [U12000]
  *  @hide
  */
    public static EventInstance toEventInstance(Context context, String uri) {
    	if (uri == null) return null;
    	
    	Uri uriValue = Uri.parse(uri);
        int match = sUriMatcher.match(uriValue);
        switch (match) {
            case EVENT_WITH_TIME: {
                Uri uriCheck = uniEventToEventUri(context, uriValue);
                if (uriCheck == null) break;
                
                long eventId   = Long.parseLong(uriCheck.getPathSegments().get(1));
                long startTime = Long.parseLong(uriCheck.getPathSegments().get(3));
                long endTime   = Long.parseLong(uriCheck.getPathSegments().get(4));
                
                return new EventInstance(eventId, startTime, endTime);
            }
            case THE_EVENT_WITH_GUID_TYPE: {
                return doCheckEventExistForNotes(context, uriValue);
            }
            default :
                Log.v(TAG, "No uri match, Uri : " + uri);
        }   
        return null;
    }
    
    //Appending query parameter for creating uri associating with Notes 
    private static Uri buildQueryParamterForNotesNew(Context context, String uriString, String accountType, 
            String iUid, String syncId, String facebookId) {
        Log.v(TAG,"buildQueryParamterForNotes");
        Uri resultUri = Uri.parse(uriString);
        if (isHTCExchangeEvent(accountType)) {
            resultUri = resultUri.buildUpon().appendQueryParameter("type", accountType)
                                       .appendQueryParameter("uid", iUid).build();
        } else if (isGoogleEvent(accountType)) {
            resultUri = resultUri.buildUpon().appendQueryParameter("type", accountType)
                                       .appendQueryParameter("uid", syncId).build();
        } else if (isHTCFacebookEvent(context, accountType)) {
            resultUri = resultUri.buildUpon().appendQueryParameter("type", accountType)
                                       .appendQueryParameter("uid", facebookId).build();
        } else {
            resultUri = resultUri.buildUpon().appendQueryParameter("type", accountType)
                                       .appendQueryParameter("uid", iUid).build();            
        }
        
        return resultUri;
    }
    
    /**
     *  Appending query parameter for creating uri associating with Notes 
     *  @deprecated [Not use after L]
     * */
    //
    private static Uri buildQueryParamterForNotesOld(Context context, String uriString, String accountType, 
            String iUid, String syncId, String facebookId) {
        Log.v(TAG,"buildQueryParamterForNotes");
        Uri resultUri = Uri.parse(uriString);
        if (isHTCExchangeEvent(accountType)) {
            resultUri = resultUri.buildUpon().appendQueryParameter("type", accountType)
                                       .appendQueryParameter("uid", iUid).build();
        } else if (isGoogleEvent(accountType)) {
            resultUri = resultUri.buildUpon().appendQueryParameter("type", accountType)
                                       .appendQueryParameter("uid", syncId).build();
        } else if (isHTCFacebookEvent(context, accountType)) {
            resultUri = resultUri.buildUpon().appendQueryParameter("type", accountType)
                                       .appendQueryParameter("uid", facebookId).build();
        } else { //if (isHTCPCSyncEvent(accountType)) {
            resultUri = resultUri.buildUpon().appendQueryParameter("type", accountType)
                                       .appendQueryParameter("uid", iUid).build();            
        }
     
        return resultUri;
    }
}

