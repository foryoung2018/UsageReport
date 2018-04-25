package com.htc.lib1.HtcCalendarFramework.util.calendar.notes;

import java.util.ArrayList;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
// Currently Mark
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

/**
 * Notes Helper
 * @author thomaslin
 * {@exthide}
 * @deprecated [Not use after L]
 */
public final class NotesHelper {

    private static final String TAG = "NotesHelper";
    private static final boolean DEBUG = HtcWrapHtcDebugFlag.Htc_DEBUG_flag;
    
    private static void Debug(String s) {
        if (DEBUG) {
            if (s != null) Log.i(TAG, s);
        }
    }
    
    
     /** Notes Authority 
      * @deprecated [Not use after L]*/
    static final String AUTHORITY = "com.htc.provider.notes";
	
	/** 
     * A content:// style uri to the authority for the contacts provider 
     * @deprecated [Not use after L]
     */
    static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
    
    /**
     * @deprecated [Not use after L]
     * */
    static final Uri ASSOCIATIONS_CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, "note_association");
    
    
    
    /**
      * Get the associated events
      * @param context the Context
      * @return the associated event IDs in the ArrayList
      * @deprecated [Not use after L]
      */
    public static ArrayList<Long> getAssociatedEvents2(Context context) {
        Cursor cursor = null;
        try {
            ContentResolver cr = context.getContentResolver();
            cursor = cr.query(ASSOCIATIONS_CONTENT_URI
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
                String eventUri = cursor.getString( cursor.getColumnIndexOrThrow("association_uri") );
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
      * @deprecated [Not use after L]
      */
    public static ArrayList<EventInstance> getAssociatedEvents(Context context) {
		try {
            ContentResolver cr = context.getContentResolver();
            Cursor cursor = cr.query(ASSOCIATIONS_CONTENT_URI
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
                String eventUri = cursor.getString( cursor.getColumnIndexOrThrow("association_uri") );
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
  *  Hide Automatically by SDK Team [U12000]
  *  @hide
  *  @deprecated [Not use after L]
  */
    public static Uri setAssociation(Context context, long noteId, String eventUri, boolean isSet) {
        Uri uri = null;
        try {
            ContentResolver cr = context.getContentResolver();
            if (isSet) {
                ContentValues cv = new ContentValues();
                cv.put("note_id",  noteId);
                cv.put("association_uri", eventUri);
                uri = cr.insert(ASSOCIATIONS_CONTENT_URI, cv);
            } else {
                cr.delete(ASSOCIATIONS_CONTENT_URI, 
                        "note_id" + "=" + noteId + " AND " +
                        "association_uri" + "=? " , new String[]{eventUri});
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
  *  Hide Automatically by SDK Team [U12000]
  *  @hide
  *  @deprecated [Not use after L]
  */
    public static long getAssociatedNotesId(Context context, String uniEventUri) {
		try {
            
            ContentResolver cr = context.getContentResolver();
            Cursor cursor = cr.query(ASSOCIATIONS_CONTENT_URI
                    , null /*projections*/
                    , "association_uri" + " = ? " /*selection*/
                    , new String [] { uniEventUri } /*args*/
                    , null /*order*/);
            
            if (cursor == null) {
                return -1;
            }
            
            long notesId = -1;
            if (cursor.moveToFirst()) {
                notesId = cursor.getLong( cursor.getColumnIndexOrThrow("note_id") );
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
  *  Hide Automatically by SDK Team [U12000]
  *  @hide the associated notes status
  *  @deprecated [Not use after L]
  */
    public static Long checkIfAssociatedNotes(Context context, String uniEventUri) {
	    try {
            
            ContentResolver cr = context.getContentResolver();
            Cursor cursor = cr.query(ASSOCIATIONS_CONTENT_URI
                    , null /*projections*/
                    , "association_uri" + " = ? " /*selection*/
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
