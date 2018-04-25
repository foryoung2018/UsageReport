/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// GB Porting, no need

package com.htc.lib1.HtcCalendarFramework.util.calendar;

import java.util.ArrayList; //hTC, add

import android.app.Activity;
import android.content.AsyncQueryHandler; //hTC, add
import android.content.ContentProviderOperation; //hTC, add
import android.content.ContentProviderResult; //hTC, add
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException; //hTC, add
import android.content.ContentProviderOperation.Builder; //hTC, add
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message; //hTC, add
import android.os.RemoteException; //hTC, add
import android.provider.CalendarContract;
import android.provider.CalendarContract.CalendarAlerts; //hTC, add
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.widget.Button;
import com.htc.lib1.HtcCalendarFramework.calendarcommon2.EventRecurrence;

//import com.htc.android.pim.eas.EASManager;

import com.htc.lib1.HtcCalendarFramework.provider.HtcExCalendar;
import com.htc.lib1.HtcCalendarFramework.provider.HtcExCalendar.ExEvents;

import com.htc.lib1.HtcCalendarFramework.util.calendar.tools.UriTools;
import android.content.Context;
import com.htc.lib1.HtcCalendarFramework.util.calendar.VersionCheckUtils;

/**
 * A helper class for deleting events.  If a normal event is selected for
 * deletion, then this pops up a confirmation dialog.  If the user confirms,
 * then the normal event is deleted.
 *
 * <p>
 * If a repeating event is selected for deletion, then this pops up dialog
 * asking if the user wants to delete just this one instance, or all the
 * events in the series, or this event plus all following events.  The user
 * may also cancel the delete.
 * </p>
 *
 * <p>
 * To use this class, create an instance, passing in the parent activity
 * and a boolean that determines if the parent activity should exit if the
 * event is deleted.  Then to use the instance, call one of the
 * {@link delete()} methods on this class.
 *
 * An instance of this class may be created once and reused (by calling
 * {@link #delete()} multiple times).
 */
public class DeleteEventHelper {

    private static final String TAG = "DeleteEventHelper";
    private /*final*/ Activity mParent = null;
    private /*final*/ ContentResolver mContentResolver;

    private long mStartMillis;
    private long mEndMillis;
    private Cursor mCursor;

    /**
     * If true, then call finish() on the parent activity when done.
     */
    private boolean mExitWhenDone;

    /**
     * These are the corresponding indices into the array of strings
     * "R.array.delete_repeating_labels" in the resource file.
     */
    //static final int DELETE_SELECTED = 0;
    //static final int DELETE_ALL_FOLLOWING = 1;
    //static final int DELETE_ALL = 2;
    
    
    private String mWhichAccountType = HtcExCalendar.getGoogleAccountType(); 
    private String mWhichAccount = "";
    private static final boolean DEBUG = false; //hTC, add
    private QueryHandler mQueryHandler = null; //hTC, add

    /**
      * Query event items
      */
    private static final String[] EVENT_PROJECTION_OLD = new String[] {
        Events._ID,              // 0
        Events.TITLE,            // 1
        Events.ALL_DAY,          // 2
        Events.CALENDAR_ID,      // 3
        Events.RRULE,            // 4
        Events.DTSTART,          // 5
        Events._SYNC_ID,         // 6
        Events.EVENT_TIMEZONE,   // 7
		//[hTC]+, add
//        ExEvents.PARENT_ID,	     // 8
//        Events.EXDATE,           // 9
        Events.DTEND,            // 10
        Events.ACCOUNT_TYPE, // 11
        Events.ACCOUNT_NAME,    // 12
        ExEvents.ICALENDAR_UID,
        Events.ORIGINAL_SYNC_ID, //13
        Events.ORIGINAL_ID //14
		//[hTC]-, add
    };
    
    private static final String[] EVENT_PROJECTION_NEW = new String[] {
        Events._ID,              // 0
        Events.TITLE,            // 1
        Events.ALL_DAY,          // 2
        Events.CALENDAR_ID,      // 3
        Events.RRULE,            // 4
        Events.DTSTART,          // 5
        Events._SYNC_ID,         // 6
        Events.EVENT_TIMEZONE,   // 7
		//[hTC]+, add
//        ExEvents.PARENT_ID,	     // 8
//        Events.EXDATE,           // 9
        Events.DTEND,            // 10
        Events.ACCOUNT_TYPE, // 11
        Events.ACCOUNT_NAME,    // 12
        Events.SYNC_DATA7,
        Events.ORIGINAL_SYNC_ID, //13
        Events.ORIGINAL_ID //14
		//[hTC]-, add
    };
    
    public static final String[] getDeleteProjection() {
    	if(VersionCheckUtils.afterAPI21()) {
    		return EVENT_PROJECTION_NEW;
    	} else {
    		return EVENT_PROJECTION_OLD;
    	}    	
    }

    private int mEventIndexId;
    private int mEventIndexRrule;
    private String mSyncId;   
    
    /**
      * The delete event helper
      * @param parent Activity, the parent Activity object
      * @param exitWhenDone boolean, true if exit when done, false not exit when done
      * @deprecated [Module internal use] 
      */
    /**@hide*/ 
    public DeleteEventHelper(Activity parent, boolean exitWhenDone) {
        mParent = parent;
        mContentResolver = mParent.getContentResolver();
        mExitWhenDone = exitWhenDone;
        
        mQueryHandler = new QueryHandler(mContentResolver); //hTC, add
    }

    /**
      * The delete event helper
      * @param context The Context
      * @param exitWhenDone boolean, true if exit when done, false not exit when done
      * @deprecated [Module internal use]
      */
    /**@hide*/ 
    public DeleteEventHelper(Context context, boolean exitWhenDone) {
        mContentResolver = context.getContentResolver();
        mExitWhenDone = exitWhenDone;
    }

    /**
      * set exit when done
      * @param exitWhenDone boolean, true if exit when done, false not exit when done
      * @deprecated [Not use any longer]
      */
   /**@hide*/ 
    public void setExitWhenDone(boolean exitWhenDone) {
        mExitWhenDone = exitWhenDone;
    }
    
    /**
      * Delete normal event
      * @param synchExchange boolean, true when sync source is from exchange, false when sync source is not from exchange
      * @param updateNotification boolean, true when update notification, false when not update notification
      */
    private void DeleteNormalEvent(boolean synchExchange, boolean updateNotification){
    	Log.v(TAG,"Delete Normal Event.");
    	if(mCursor != null && !mCursor.isFirst()) {
            mCursor.moveToFirst();
            Log.d(TAG, "mCursor move to first");
        }
        try {
            long id = mCursor.getInt(mEventIndexId);
            Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, id);
            mContentResolver.delete(uri, null /* where */, null /* selectionArgs */);
        } catch(Exception e) {
            Log.w(TAG, "Delete event fail", e);
        }

    }

    /**
     * Does the required processing for deleting an event, which includes
     * first popping up a dialog asking for confirmation (if the event is
     * a normal event) or a dialog asking which events to delete (if the
     * event is a repeating event).  The "which" parameter is used to check
     * the initial selection and is only used for repeating events.  Set
     * "which" to -1 to have nothing selected initially.
     *
     * @param begin long The begin time of the event, in UTC milliseconds
     * @param end long The end time of the event, in UTC milliseconds
     * @param eventId long The event id
     * @param which int One of the values {@link DELETE_SELECTED},
     *  {@link DELETE_ALL_FOLLOWING}, {@link DELETE_ALL}, or -1
     * @deprecated [Not use any longer]
     */
   /**@hide*/ 
    public void delete(long begin, long end, long eventId, int which) {
        Log.v(TAG, "Delete event directly eventId is :"+eventId);

        Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, eventId);
                        
        if(mQueryHandler != null) {
        	EventInfo ei = new EventInfo(eventId, begin, end, which);
        	mQueryHandler.startQuery(0, ei, uri, getDeleteProjection(), null, null, null);
        }        
    }    
    
    private CharSequence[] mDelItems = null;
    
    
//    private static String getEventRRule(Context context, long event_id) {
//    	//Log.i(TAG, "getEventRRule event_id:"+event_id);
//    	String [] PROJECT = new String [] {
//    		android.provider.CalendarContract.Events.RRULE, // 0
//    	};
//    	int INDEX_EVENT_RRULE = 0;
//
//    	ContentResolver cr = context.getContentResolver();
//    	Cursor cursor = cr.query(android.provider.CalendarContract.Events.CONTENT_URI,
//    			PROJECT, "_ID="+event_id,
//    			null, null);
//
//    	int nCount = 0;
//    	String rrule = "";
//    	if( cursor != null) {
//    		nCount = cursor.getCount();
//    		if(nCount != 0) {
//    			cursor.moveToPosition(-1);
//    			while( cursor.moveToNext()) {
//    				rrule = cursor.getString(INDEX_EVENT_RRULE);
//    			}
//    		}
//        	if(!cursor.isClosed()) {
//        		cursor.close();
//        	}
//			cursor = null;
//    	}
//    	return rrule;
//    }

    /*
     * doDelete
     * 1. DeleteNormalEvent => set parent's deleted as 1
     * 2. DeleteExceptionEvent => update exception event's EVENT_STATUS as 2
     * 3. DeleteRepeatingEvent 
     */
    private void doDelete(long begin, long end, Cursor cursor, int which, boolean enableUI, boolean synchExchange, boolean updateNotification) {
        Log.v(TAG, "doDelete");

    	if (cursor == null) return;
    	
        mStartMillis = begin;
        mEndMillis = end;
        mCursor = cursor;
        mEventIndexId = mCursor.getColumnIndexOrThrow(Events._ID);
        mEventIndexRrule = mCursor.getColumnIndexOrThrow(Events.RRULE);

        
        //[hTC]+, add
        int sync_account_type_idx = mCursor.getColumnIndexOrThrow(Events.ACCOUNT_TYPE);
        String sync_account_type = mCursor.getString(sync_account_type_idx);
        mWhichAccountType = sync_account_type;
        
        int sync_account_idx = mCursor.getColumnIndexOrThrow(Events.ACCOUNT_NAME);
        String sync_account = mCursor.getString(sync_account_idx);
        mWhichAccount = sync_account;
        //[hTC]-, add
        
        
        int eventIndexSyncId = mCursor.getColumnIndexOrThrow(Events._SYNC_ID);
        mSyncId = mCursor.getString(eventIndexSyncId);

//        int eventIndexParentId = mCursor.getColumnIndexOrThrow(ExEvents.PARENT_ID);
        
//	int index_original_sync_id = mCursor.getColumnIndexOrThrow(Events.ORIGINAL_SYNC_ID);
//	String originalEventId = mCursor.getString(index_original_sync_id);

	String rRule = mCursor.getString(mEventIndexRrule);

        //Log.i(TAG, "eventIndexParentId:"+eventIndexParentId);
//        long parentID = -1;
//        if( eventIndexParentId != -1) {
//            if( !mCursor.isNull(eventIndexParentId)) {
//                parentID = mCursor.getLong(eventIndexParentId);
//            }
//        }

//        Log.i(TAG, "parentID:"+parentID);
       
        // If this is a repeating event, then pop up a dialog asking the
        // user if they want to delete all of the repeating events or
        // just some of them.
//        String rRule = mCursor.getString(mEventIndexRrule);
//        if( TextUtils.isEmpty(rRule) && parentID != -1) {
//            String parentRrule = getEventRRule(mParent, parentID);
//            if( !TextUtils.isEmpty(parentRrule)) {
//                rRule = parentRrule;
//                //Log.i(TAG, "parentRrule:"+parentRrule);
//            }
//        }

        int id = mCursor.getInt(mEventIndexId); // event id      
		Log.v(TAG, "event id is :"+id);

		if (TextUtils.isEmpty(rRule)) {
			Log.v(TAG, "start to DeleteNormalEvent");
			// Normal event, delete it directly. set deleted as 1.
			DeleteNormalEvent(synchExchange, updateNotification);
		} else {
			
			long exception_event_id = getExceptionEventId(id);

			if (exception_event_id !=-1 && which==0) { // delete single exception
				// This is an exception event.
				Log.v(TAG, "start to deleteExceptionEvent");
				deleteExceptionEvent(exception_event_id);
			} else {
				// This is a repeating event.
				try {
					Log.v(TAG, "start to deleteRepeatingEvent which is " + which);
					deleteRepeatingEvent(which);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}
    
    /**
     * Does the required processing for deleting an event.  This method
     * takes a {@link Cursor} object as a parameter, which must point to
     * a row in the Events table containing the required database fields.
     * The required fields for a normal event are:
     *
     * <ul>
     *   <li> Events._ID </li>
     *   <li> Events.TITLE </li>
     *   <li> Events.RRULE </li>
     * </ul>
     *
     * The required fields for a repeating event include the above plus the
     * following fields:
     *
     * <ul>
     *   <li> Events.ALL_DAY </li>
     *   <li> Events.CALENDAR_ID </li>
     *   <li> Events.DTSTART </li>
     *   <li> Events._SYNC_ID </li>
     *   <li> Events.EVENT_TIMEZONE </li>
     * </ul>
     *
     * @param begin the begin time of the event, in UTC milliseconds
     * @param end the end time of the event, in UTC milliseconds
     * @param cursor the database cursor containing the required fields
     * @param which one of the values {@link DELETE_SELECTED},
     *  {@link DELETE_ALL_FOLLOWING}, {@link DELETE_ALL}, or -1
     * @param enableUI or not
     * @param synchExchange or not
     * @param updateNotification or not
     * @deprecated [Not use any longer]
     */
    /**@hide*/ 
    public void delete(final long begin, final long end, final Cursor cursor, final int which, final boolean enableUI, final boolean synchExchange, final boolean updateNotification) {
    	
    	if (cursor == null) return;
    	
            try {
            	Log.v(TAG,"start to doDelete.");
                doDelete(begin, end, cursor, which, enableUI, synchExchange, updateNotification);
            } catch (Exception e) {
	            Log.w(TAG, "delete exception!");
	            e.printStackTrace();
            }
    }

    /**
      * Delete the exchange event
      * @param del_event_id long, the delete event id
      * @param onlyThis boolean, true when only this, false when not only this
      * @deprecated [Module internal use]
      */
    /**@hide*/ 
    public void deleteExchangeEvent(long del_event_id, boolean onlyThis) {
    	Log.i(TAG, "deleteExchangeEvent del_event_id:"+del_event_id+" onlyThis: "+onlyThis);
   
    	if(del_event_id < 0) {
    		Log.i(TAG, "del_event_id < 0");
    		return;
    	}
    	String[] PROJECTION = new String[] {
    			Events._ID,	   // 0
    			Events.ORIGINAL_ID, // 1
		    };
    	int INDEX_EVENT_ID = 0;

    	String where = "";
    	if(onlyThis) {
    		where = "_ID="+del_event_id;
    	} else {
    		where = "_ID="+del_event_id+" OR original_id="+del_event_id;
    	}
        Cursor cursor = mContentResolver.query(Events.CONTENT_URI,
        			PROJECTION,
        			where,
        			null, null);

        int nCount = 0;
        long event_id = 0;
        if(cursor != null) {
        	nCount = cursor.getCount();
        	//Log.i(TAG, "have child #"+nCount);
        	if( nCount != 0) {
        		cursor.moveToPosition(-1);
        		// delete all child event
				//[hTC]+, mod
        		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        		Uri eventUri;
        		while(cursor.moveToNext()) {
        			event_id = cursor.getLong(INDEX_EVENT_ID);
        			eventUri = ContentUris.withAppendedId(Events.CONTENT_URI, event_id);
        			Builder b = ContentProviderOperation.newDelete(eventUri);
        			ops.add(b.build());
        			//Log.i(TAG, "event_id:"+event_id);
        			//deleteEventById(event_id);
        		}

        		try {
                    ContentProviderResult[] results =
                    	mContentResolver.applyBatch(CalendarContract.AUTHORITY, ops);
                    if (DEBUG) {
                        for (int i = 0; i < results.length; i++) {
                            Log.v(TAG, "results = " + results[i].toString());
                        }
                    }
                } catch (RemoteException e) {
                    Log.w(TAG, "Ignoring unexpected remote exception", e);
                } catch (OperationApplicationException e) {
                    Log.w(TAG, "Ignoring unexpected exception", e);
                }
				//[hTC]-, mod
        	}

        	// close cursor
        	if(!cursor.isClosed()) {
        		cursor.close();
        	}
			cursor = null;
        } else {
        	// cursor is null
        }
    }

    private void deleteEventById(long event_id) {
        Log.v(TAG, "deleteEventById event_id is :"+event_id );

    	Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, event_id);
        mContentResolver.delete(uri, null /* where */, null /* selectionArgs */);
    }

    
    /*
     * deleteRepeatingEvent
     * 1. DELETE_SELECTED => Insert New exception event
     * 2. DELETE_ALL  
     *    2-1 for exchange event: delete all child events and set parent's deleted as 1.
     *    2-2 others: delete directly and set parent's deleted as 1.
     * 3. DELETE_ALL_FOLLOWING => update parent's rrule and modify the repeating event to end just before this event time
     */
    private void deleteRepeatingEvent(int which) {
    	if (mCursor==null) return;
    	
    	Log.i(TAG, "deleteRepeatingEvent -which: "+which);
    	
    	if(mCursor != null && !mCursor.isFirst()) {
    		mCursor.moveToFirst();
    		Log.d(TAG, "mCursor move to first");
    	}
        int indexDtstart = mCursor.getColumnIndexOrThrow(Events.DTSTART);
        int indexAllDay = mCursor.getColumnIndexOrThrow(Events.ALL_DAY);
        int indexTitle = mCursor.getColumnIndexOrThrow(Events.TITLE);
        int indexTimezone = mCursor.getColumnIndexOrThrow(Events.EVENT_TIMEZONE);
        int indexCalendarId = mCursor.getColumnIndexOrThrow(Events.CALENDAR_ID);
        int indexDtend = mCursor.getColumnIndexOrThrow(Events.DTEND);

        String rRule = mCursor.getString(mEventIndexRrule);
        boolean allDay = mCursor.getInt(indexAllDay) != 0;
        long dtstart = mCursor.getLong(indexDtstart);
//	long dtend = mCursor.getLong(indexDtend);
        long id = mCursor.getInt(mEventIndexId);
        
//	int index_original_sync_id = mCursor.getColumnIndexOrThrow(Events.ORIGINAL_SYNC_ID);
//	String originalEventId = mCursor.getString(index_original_sync_id);
        int index_original_id = mCursor.getColumnIndexOrThrow(Events.ORIGINAL_ID);
	long original_Id = mCursor.getInt(index_original_id);
		
		
        String title = mCursor.getString(indexTitle);
        String timezone = mCursor.getString(indexTimezone);

//        int eventIndexParentId = mCursor.getColumnIndexOrThrow(ExEvents.PARENT_ID);
//        Log.i(TAG, "eventIndexParentId:"+eventIndexParentId);
//        long parentID = -1;
//        if( eventIndexParentId != -1) {
//        	if( !mCursor.isNull(eventIndexParentId)) {
//        		parentID = mCursor.getLong(eventIndexParentId);
//        	}
//    	}
//        Log.i(TAG, "parentID:"+parentID);

        // If the repeating event has not been given a sync id from the server
        // yet, then we can't delete a single instance of this event.  (This is
        // a deficiency in the CalendarProvider and sync code.) We checked for
        // that when creating the list of items in the dialog and we removed
        // the first element ("DELETE_SELECTED") from the dialog in that case.
        // The "which" value is a 0-based index into the list of items, where
        // the "DELETE_SELECTED" item is at index 0.
//        if (mSyncId == null && ( UriTools.isHTCPCSyncEvent(mWhichAccountType)||
//        		                 UriTools.isGoogleEvent(mWhichAccountType))) {
//        	which += 1;
//        }
//        if ( UriTools.isHTCExchangeEvent(mWhichAccountType)) {
//        	// this very suck.
//        	if(which == 1) {
//        		which = 2;
//        	}
//        }
//        Log.i(TAG, "deleteRepeatingEvent +which: "+which);
        switch (which) {
            case HtcCalendarManager.DELETE_SELECTED:
            {
                Log.v(TAG, "deleteRepeatingEvent DELETE_SELECTED" );

//            	if(UriTools.isGoogleEvent(mWhichAccountType)) {
                	//Log.i(TAG, "DELETE_SELECTED");
                    // If we are deleting the first event in the series, then
                    // instead of creating a recurrence exception, just change
                    // the start time of the recurrence.
//                    if (dtstart == mStartMillis) {
                        // TODO
//                    }

                    // Create a recurrence exception by creating a new event
                    // with the status "cancelled".
                    ContentValues values = new ContentValues();

                    // The title might not be necessary, but it makes it easier
                    // to find this entry in the database when there is a problem.
                    values.put(Events.TITLE, title);


//                    if(allDay) {
//                    	// All day, shit event time according to TimeZone, ex: TW +8
//                    	timezone = Time.getCurrentTimezone();
//                    	
//                    	Time t = new Time(timezone);
//                    	t.setToNow();
//                    	t.normalize(false);
//                    	long adjustOffset = t.gmtoff * 1000;
//                    	Log.d(TAG, "deleteRepeatingEvent allDay event :: timezone:"+timezone+" adjustOffset: "+adjustOffset);
//                    	mStartMillis += adjustOffset;
//                    	mEndMillis += adjustOffset;
//
//                    	Log.d(TAG, String.format(java.util.Locale.US,  "mStartMillis=%d mEndMillis=%d", mStartMillis, mEndMillis));
//                    	values.put(Events.ORIGINAL_ALL_DAY, 1);
//                    }

                    
                    if(UriTools.isGoogleEvent(mWhichAccountType)) {
                        if(allDay) {
                        	timezone = Time.getCurrentTimezone();
                        	
                        	Time t = new Time(timezone);
                        	t.setToNow();
                        	t.normalize(false);
                        	long adjustOffset = t.gmtoff * 1000;
                        	Log.d(TAG, "deleteRepeatingEvent allDay event :: timezone:"+timezone+" adjustOffset: "+adjustOffset);
                        	mStartMillis += adjustOffset;
                        	mEndMillis += adjustOffset;

                        	Log.d(TAG, String.format(java.util.Locale.US,  "mStartMillis=%d mEndMillis=%d", mStartMillis, mEndMillis));
                        }
                	} else {
                  		if(allDay) {
                			Time localTime = new Time();
                			localTime.setToNow();

                			mStartMillis = mStartMillis + localTime.gmtoff * 1000;
                			Log.d(TAG, "all day offset: "+localTime.gmtoff * 1000);
                		}
                	}

                    int calendarId = mCursor.getInt(indexCalendarId);
//                    values.put(Events.EVENT_TIMEZONE, allDay ? "UTC" : timezone);
//                    values.put(Events.ALL_DAY, allDay ? 1 : 0);
//                    values.put(Events.CALENDAR_ID, calendarId);
//                    values.put(Events.DTSTART, mStartMillis);
//                    values.put(Events.DTEND, mEndMillis);
//                    values.put(Events.ORIGINAL_SYNC_ID, mSyncId);
//            		// Cherry + JB, update original id 
//                    values.put(Events.ORIGINAL_ID, id);
//                    // Cherry + JB update original id
//                    values.put(Events.ORIGINAL_INSTANCE_TIME, mStartMillis);
//                    values.put(Events.STATUS, Events.STATUS_CANCELED);
//
//                    mContentResolver.insert(Events.CONTENT_URI, values);          		
//  
//                    Log.v(TAG, "deleteRepeatingEvent DELETE_SELECTED event." );
                    
                    Uri event_uri = Events.CONTENT_URI;
        			Log.d(TAG, "allDay: "+allDay);
//        			if (UriTools.isHTCExchangeEvent(mWhichAccountType)){
//        				values.put(Events.ORIGINAL_ALL_DAY, 0);
//        				values.put(Events.SYNC_DATA3, allDay ? 1 : 0);
//        				values.put(Events.ALL_DAY, 0); // for exchange account! all day is false
//        				values.put(Events.EVENT_TIMEZONE, timezone);
//        				event_uri=addCallerIsHtcExtensionParameter(event_uri);
//        				event_uri=addCallerIsSyncAdapterParameter(event_uri);
//
//        			} else {
                        values.put(Events.ALL_DAY, allDay ? 1 : 0);
        				values.put(Events.ORIGINAL_ALL_DAY, allDay ? 1 : 0);
        				values.put(Events.EVENT_TIMEZONE, allDay ? "UTC" : timezone);
//        			}

        			values.put(Events.CALENDAR_ID, calendarId);
        			values.put(Events.DTSTART, mStartMillis);
        			values.put(Events.DTEND, mEndMillis);
        			values.put(Events.ORIGINAL_SYNC_ID, mSyncId);
        			values.put(Events.ORIGINAL_ID, id);
        			values.put(Events.ORIGINAL_INSTANCE_TIME, mStartMillis);
        			values.put(Events.STATUS, Events.STATUS_CANCELED);
        			mContentResolver.insert(event_uri, values);
        			Log.d(TAG, String.format(java.util.Locale.US,"mStartMillis=%d mEndMillis=%d", mStartMillis,	mEndMillis));

                    
                break;
            }
            case HtcCalendarManager.DELETE_ALL: {
            	/* --------------------------------- */
            	/* for Exchange delete child event   */
            	/* --------------------------------- */
                Log.v(TAG, "deleteRepeatingEvent  DELETE_ALL." );

            	if( UriTools.isHTCExchangeEvent(mWhichAccountType) || UriTools.isGoogleExchangeEvent(mWhichAccountType)) {
            		deleteExchangeEvent(((original_Id!=0)?original_Id:id), false);
            	} else {
            		deleteEventById(id);
            	}
                break;
            }
            case HtcCalendarManager.DELETE_ALL_FOLLOWING: {
                Log.v(TAG, "deleteRepeatingEvent  DELETE_ALL_FOLLOWING." );

            	Time dbStartTime = new Time();
            	dbStartTime.set(dtstart);

            	Time startTime = new Time();

            	if(allDay) {
            		Time localTime = new Time();
            		localTime.setToNow();
            		startTime.set(mStartMillis+localTime.gmtoff*1000);
            	} else {
            		startTime.set(mStartMillis);
            	}

            	if(allDay) {
            		if(dbStartTime.year == startTime.year &&
            			dbStartTime.month == startTime.month &&
            			dbStartTime.monthDay == startTime.monthDay) {
          			  	Log.v(TAG,"delete allday");
   
          			  Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, id);
                           mContentResolver.delete(uri, null /* where */, null /* selectionArgs */);
                           break;
            		}
            	} else {
//                     If we are deleting the first event in the series and all
//                     following events, then delete them all.
                    if (dtstart == mStartMillis) {
          			  	Log.v(TAG,"deleting the first event in the series and all following events");
                        Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, id);
                        mContentResolver.delete(uri, null /* where */, null /* selectionArgs */);
                        break;
                    }
            	}

                // Modify the repeating event to end just before this event time
                EventRecurrence eventRecurrence = new EventRecurrence();
                eventRecurrence.parse(rRule);
                Time date = new Time();
                if (allDay) {
                    date.timezone = Time.TIMEZONE_UTC;
                }
                date.set(mStartMillis);
                date.second--;
                date.normalize(false);

                // Google calendar seems to require the UNTIL string to be
                // in UTC.
                date.switchTimezone(Time.TIMEZONE_UTC);
                eventRecurrence.until = date.format2445();

                ContentValues values = new ContentValues();
                values.put(Events.DTSTART, dtstart);
                values.put(Events.RRULE, eventRecurrence.toString());
                Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, id);
                mContentResolver.update(uri, values, null, null);
                break;
            }
        }
        if (mExitWhenDone) {
            mParent.finish();
        }
    }
    
    private class EventInfo {
    	public long mEventId;
    	public long mBegin;
    	public long mEnd;
    	public int mWhich;
    	
    	EventInfo(long eventId, long begin, long end, int which) {
    		mEventId = eventId;
    		mBegin = begin;
    		mEnd = end;
    		mWhich = which;
    	}
    }
    
    private class QueryHandler extends AsyncQueryHandler {
		public QueryHandler(ContentResolver cr) {
			super(cr);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {	        
	        if (cursor == null) {
	            return;
	        }
	        
	        if (cookie == null) {
	        	return;
	        }
	        
	        EventInfo ei = (EventInfo) cookie;
	        if (cursor.getCount() == 0) {
	        	Log.d(TAG, "delete - cursor is empty, so skip it. eventId: "+ei.mEventId);
	        	return;
	        }	       
	        
	        cursor.moveToFirst();
	        delete(ei.mBegin, ei.mEnd, cursor, ei.mWhich, true, true, true);			              
		}
    }  
    
    /**
      * Cancel the AlertDialog
      * @deprecated [Not use any longer]
      */
   /**@hide*/ 
    public void cancel() {

    }
    
    /**
      * Release the QueryHandler, AlertDialog, ContentResolver, and the Parent.
      * @deprecated [Not use any longer]
      */
  /**@hide*/ 
    public void release() {                        
        mQueryHandler.cancelOperation(0);
        mQueryHandler = null;
        mContentResolver = null;
        mParent = null;
    }


	
    private void deleteExceptionEvent(long eventid) {

		Log.i(TAG, "deleteExceptionEvent eventid is :"+eventid);

		// Cherry +, for JB, delete exception event, update event status to event canceled
		ContentValues values = new ContentValues();
		values.put(Events.STATUS, Events.STATUS_CANCELED);
		Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, eventid);
		mContentResolver.update(uri, values, null, null);
    }
    
    
    private Cursor queryExceptionEvent( int parentid){
        Log.v(TAG,"queryExceptionEvent parentid is :" +parentid);

    	if (parentid==-1) return null;
    	
    	String where = Events.DTSTART+ "=" + mStartMillis + " AND "
		+ Events.DTEND + "=" + mEndMillis +  " AND "
		+ Events.ORIGINAL_ID + "=" +parentid ;

		try {
			Cursor EventCursor = mContentResolver.query(Events.CONTENT_URI, getDeleteProjection(), where, null, null);
			if (EventCursor != null) {
				EventCursor.moveToFirst();
				return EventCursor;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

    }
    
    private long getExceptionEventId(int parentid){
    	
		Cursor c = queryExceptionEvent(parentid);

    	if (c == null) return -1;

    	Log.v (TAG,"c count is"+c.getCount());
    	if (c.getCount()==0) return -1;
    	
        int eventid = c.getColumnIndexOrThrow(Events._ID);
		long id = c.getInt(eventid);
        Log.v(TAG,"isExceptionEvent id is :"+ id);
        closeCursor(c);
        return id;

    }
    
    private void closeCursor(Cursor c) {
        if (c != null) {
            if (!c.isClosed()) {
                c.close();
            }
            c = null;
        }    
    }

}
