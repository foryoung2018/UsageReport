package com.htc.lib1.HtcCalendarFramework.util.calendar.notes;

import java.util.ArrayList;

import java.util.ArrayList;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
//import com.htc.provider.Notes;
//import com.htc.provider.Notes.NoteAssociationColumns;
import com.htc.lib1.HtcCalendarFramework.util.calendar.HtcCalendarManager;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import com.htc.lib1.HtcCalendarFramework.util.calendar.EventInstance;
import com.htc.lib1.HtcCalendarFramework.util.calendar.tools.UriTools;

public class LucyHelper {


    private static final String TAG = "LucyHelper";
    private static final boolean DEBUG = HtcWrapHtcDebugFlag.Htc_DEBUG_flag;    
    private static final Uri LUCY_UPDATE_ASSOCIATE_CONTENT_URI= Uri.parse("content://com.htc.lucy/calendar_event");
    private static final Uri LUCY_REMOVE_ASSOCIATE_CONTENT_URI= Uri.parse("content://com.htc.lucy/calendar_event/remove");
    /**
     * Lucy's content uri
     * */
    public static final Uri LUCY_CONTENT_URI= Uri.parse("content://com.htc.lucy/notes");
    /**
     * Column "id" in Lucy's db table
     * */
    public static final String NOTE_ID = "id";
    /**
     * Column "calendar_event" in Lucy's db table
     * */
    public static final String CALENDAR_EVENT = "calendar_event";
    /**
     * Column "id" in Lucy's db table
     * */
    public static final String THUMBNAIL_PATH = "thumbnail_path";
    private static void Debug(String s) {
        if (DEBUG) {
            if (s != null) Log.i(TAG, s);
        }
    }
    
    /**
      * Get the associated events
      * @param context the Context
      * @return the associated event IDs in the ArrayList
      *  @deprecated [Not use any longer]
      */
    /**@hide*/ 
    public static ArrayList<Long> getAssociatedEvents2(Context context) {
        Cursor cursor = null;
        try {
            ContentResolver cr = context.getContentResolver();            
            cursor = cr.query(LUCY_CONTENT_URI
                    , null /*projections*/
                    , null //selection
                    , null //args
                    , null );//order
            
            if (cursor == null) {
                Debug("No association events");
                return null;
            }
            
            if (!cursor.moveToFirst()) {
                Debug("No association events");
                cursor.close();
                return null;
            }
            
            ArrayList<Long> eventIds = new ArrayList<Long>();
            do {
                String eventUri = cursor.getString( cursor.getColumnIndexOrThrow(CALENDAR_EVENT) );
                EventInstance ei = UriTools.toEventInstance(context, eventUri);
                
                if (ei != null) {
                    long event_id = ei.getId();
                    Debug("Associated event: " + ei.getId() + ", begin: " + ei.getBegin() + ", end: " + ei.getEnd());
                    eventIds.add(event_id);
                }
                
            } while (cursor.moveToNext());            
            return eventIds;
        } catch (Exception e) {            
            Log.e(TAG, "checkIfAssociatedNotes: " + e.toString());             
            return null;
        } finally {
            if(cursor != null && !cursor.isClosed()) {                
                cursor.close();
                Log.v(TAG,"close cursor in NotesHelper");
            } 
        }
    }
    
    /**
      * Get the associated events
      * @param context the Context
      * @return the associated event instances in the ArrayList
      *  @deprecated [Not use any longer]
      */
    /**@hide*/ 
    public static ArrayList<EventInstance> getAssociatedEvents(Context context) {
        try {
            ContentResolver cr = context.getContentResolver();
            Cursor cursor = cr.query(LUCY_CONTENT_URI
                    , null /*projections*/
                    , null //selection
                    , null //args
                    , null );//order
            
            if (cursor == null) {
                Debug("No association events");
                return null;
            }
            
            if (!cursor.moveToFirst()) {
                Debug("No association events");
                cursor.close();
                return null;
            }
            
            ArrayList<EventInstance> events = new ArrayList<EventInstance>();
            do {
                String eventUri = cursor.getString( cursor.getColumnIndexOrThrow(CALENDAR_EVENT) );
                EventInstance ei = UriTools.toEventInstance(context, eventUri);
                
                if (ei != null) {
                    Debug("Associated event: " + ei.getId() + ", begin: " + ei.getBegin() + ", end: " + ei.getEnd());
                    events.add(ei);
                }
                
            } while (cursor.moveToNext());
            cursor.close();
            
            return events;
            
        } catch (Exception e) {
            Log.e(TAG, "checkIfAssociatedNotes: " + e.toString());
            
            return null;
        }
    
    }


/**
  * Set the association
  * @param context the Context
  * @param noteId the note ID
  * @param eventUri the string of the event uri
  * @param isSet boolean true when set, false when not set
  */
    public static Uri setAssociation(Context context, long noteId, String eventUri, boolean isSet) {
        Uri uri = null;
        try {
            ContentResolver cr = context.getContentResolver();
            if (isSet) {
                ContentValues cv = new ContentValues();
                Uri insertUri = Uri.withAppendedPath(LUCY_UPDATE_ASSOCIATE_CONTENT_URI, Long.toString(noteId));
                cv.put(CALENDAR_EVENT, eventUri);
                cr.update(insertUri, cv, null, null);
            } else {
                Uri deleteUri = Uri.withAppendedPath(LUCY_REMOVE_ASSOCIATE_CONTENT_URI, Long.toString(noteId));
                cr.delete(deleteUri, null, null);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "setAssociation:" + e.toString());
        }
        
        if (uri != null) 
            Debug("setAssociation : " + uri.toString());
        
        return uri;    
    }
    
/**
  * Get the associated notes ID
  * @param context the Context
  * @param uniEventUri the string of the event uni
  * @return the associated notes ID
  */
    public static long getAssociatedNotesId(Context context, String uniEventUri) {
        try {
            
            ContentResolver cr = context.getContentResolver();
            Cursor cursor = cr.query(LUCY_CONTENT_URI
                    , null /*projections*/
                    , CALENDAR_EVENT + " = ? " /*selection*/
                    , new String [] { uniEventUri } /*args*/
                    , null /*order*/);
            
            if (cursor == null) {
                return -1;
            }
            
            long notesId = -1;
            if (cursor.moveToFirst()) {
                notesId = cursor.getLong( cursor.getColumnIndexOrThrow(NOTE_ID) );
            } 

            Debug(uniEventUri + " associated with note id: " + notesId);
            
            cursor.close();
         
            return notesId;
            
        } catch (Exception e) {
            Log.e(TAG, "getAssociatedNotesId: " + e.toString());
            
            return -1;
        }
    }

/**
  * Check if associated notes
  * @param context the Context
  * @param uniEventUri the string of the event uni
  * @return 
  */
    public static Long checkIfAssociatedNotes(Context context, String uniEventUri) {
        try {
            
            ContentResolver cr = context.getContentResolver();
            Cursor cursor = cr.query(LUCY_CONTENT_URI
                    , null /*projections*/
                    , CALENDAR_EVENT + " = ? " /*selection*/
                    , new String [] { uniEventUri } /*args*/
                    , null );//order
            
            if (cursor == null) {
                Debug(uniEventUri + " NOT associated with note!");
                return HtcCalendarManager.ASSOCIATE_NOTE_FALSE;
            }
            
            boolean isAssociated = false;
            if (cursor.moveToFirst()) {
                isAssociated = true;
            } 
            Debug(uniEventUri + " associated with note : " + isAssociated);
            cursor.close();
            
            if (isAssociated)
                return HtcCalendarManager.ASSOCIATE_NOTE_TRUE;
            else 
                return HtcCalendarManager.ASSOCIATE_NOTE_FALSE;
            
        } catch (Exception e) {
            Log.e(TAG, "checkIfAssociatedNotes: " + e.toString());
            
            return HtcCalendarManager.ASSOCIATE_NOTE_UNKNOWN;
        }
        
    }
    

}
