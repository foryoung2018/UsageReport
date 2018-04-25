/**
 * 
 */
package com.htc.lib1.HtcCalendarFramework.util.calendar;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Set;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.util.Log;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
import com.htc.lib1.HtcCalendarFramework.util.calendar.notes.NotesHelper;
import com.htc.lib1.HtcCalendarFramework.util.calendar.notes.LucyHelper;
import com.htc.lib1.HtcCalendarFramework.util.calendar.notes.HtcAssociatedNotesFlag;
import com.htc.lib1.HtcCalendarFramework.util.calendar.tools.UriTools;
import com.htc.lib1.HtcCalendarFramework.util.calendar.vcalendar.HtcVCalendar;
import com.htc.lib1.HtcCalendarFramework.util.calendar.vcalendar.VCalendarUtils;
import android.provider.CalendarContract.Attendees;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Instances;
import com.htc.lib1.HtcCalendarFramework.provider.HtcExCalendar;
import com.htc.lib1.HtcCalendarFramework.provider.HtcExCalendar.ExCalendars;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import com.htc.lib1.HtcCalendarFramework.provider.HtcExCalendar.ExEvents;
import android.app.Activity;
import android.text.format.Time;
import android.text.TextUtils;
import com.htc.lib1.HtcCalendarFramework.util.calendar.VersionCheckUtils;
/**
 * The Htc Calendar Manager class
 * @author thomaslin
 * {@exthide}
 */
public class HtcCalendarManager implements 
          HtcAssociatedNotesFlag
{

    private static final String TAG = "HtcCalendarManager";
    private static final boolean DEBUG = HtcWrapHtcDebugFlag.Htc_DEBUG_flag;
    
    private static HtcCalendarManager me        = null;
    private Context mContext                    = null;
    
    /**
      * The extra event uri
      */
    public static final String EXTRA_EVENT_URI = "com.htc.calendar.event_uri";
    
    private HtcCalendarManager(Context context) {
        
        if (context != null)
            Debug("HtcCalendarManager contruct by " + context.toString());
        else 
            Debug("HtcCalendarManager contruct by null??");
        
        mContext = context;

    }

    /**
     * To get the HtcCalendarManager instance
     * @param context Context The Context
     * @return HtcCalendarManager Return the HtcCalendarManager object
     */
    public static HtcCalendarManager getInstance(Context context) {
        synchronized(HtcCalendarManager.class) {
            if ( null == me ) {
                me = new HtcCalendarManager(context.getApplicationContext());
            }
        }

        if (me.mContext != context.getApplicationContext()) {
            me.mContext = context.getApplicationContext();  // always update the last context!
        } 
        
        if (me.mContext == null) {
            if (context != null)
                Log.e(TAG, "getApplicationContext() should not be null:" + context.toString());
            else
                Log.e(TAG, "getApplicationContext() should not be null: null");
            me.mContext = context;
        }
        
        return me;
    }  
    
    /**
     * To set the assoication
     * @param noteId The note id to associate.
     * @param eventUri The uni event uri to assocaite.
     * @param isSet True, for associate. False, for un-link
     * @return For un-link, return null. For associate, return association uri, format: content://com.htc.provider.notes/note_association/4
     * @deprecated [Not use after L]
     */
    public Uri setAssociation(long noteId, String eventUri, boolean isSet) {
    	if(VersionCheckUtils.afterAPI21()) {
    		throw new IllegalArgumentException("It can't support after API 21.");    		
    	} else {
    		return NotesHelper.setAssociation(mContext, noteId, eventUri, isSet);
    	}
    }
    
    /**
     * To set the assoication with Lucy
     * @param noteId The note id to associate.
     * @param eventUri The uni event uri to assocaite.
     * @param isSet True, for associate. False, for un-link
     * @return For un-link, return null. For associate, return association uri, format: content://com.htc.provider.notes/note_association/4
     * @deprecated [Not use any longer]
     */
    /**@hide*/ 
    public Uri setAssociationWithLucy(long noteId, String eventUri, boolean isSet) {
         Uri uri = LucyHelper.setAssociation(mContext, noteId, eventUri, isSet);        
        return uri;
    }
    
    /**
     * To get associated note's id
     *  
     * @param uniEventUri The uni event uri string, format: content://com.android.calendar/uni-event/4233/EventTime/1292405400000/1292409000000?type=com.htc.android.mail.eas&uid=0cca9ce0-ecdc-43c5-a8be-1289aa43db0a
     * @return -1: if the event is not associated with note. noteId for the associated with notes
     * @deprecated [Not use after L]
     */
    public long getAssociatedNotesId(String uniEventUri) {
    	if(VersionCheckUtils.afterAPI21()) {
    		throw new IllegalArgumentException("It can't support after API 21.");    		
    	} else {
    		 return NotesHelper.getAssociatedNotesId(mContext, uniEventUri);
    	}
    }
    
    /**
     * To get associated lucy id
     *  
     * @param uniEventUri The uni event uri string
     * @return -1: if the event is not associated with note. noteId for the associated with notes
     * 
     */
    
    public long getAssociatedLucyId(String uniEventUri) {
    	//[Phone team use]
        return LucyHelper.getAssociatedNotesId(mContext, uniEventUri);
    }
    
    /**
     * Check if the event is associated with note.<br><br>
     *
     *  @param uniEventUri String The event uri, like the format: 
     *  @return Long Return the ASSOCIATE_NOTE_TRUE, ASSOCIATE_NOTE_FALSE or ASSOCIATE_NOTE_UNKNOWN
     *  @deprecated [Not use after L]
     */    
    public Long checkIfAssociatedNotes(String uniEventUri) {
    	if(VersionCheckUtils.afterAPI21()) {
    		throw new IllegalArgumentException("It can't support after API 21.");    		
    	} else {
    		return NotesHelper.checkIfAssociatedNotes(mContext, uniEventUri);
    	}
    }
    
    /**
     * Check if the event is associated with note.<br><br>
     *
     *  @param uniEventUri String The event uri, like the format: 
     *  @return Long Return the ASSOCIATE_NOTE_TRUE, ASSOCIATE_NOTE_FALSE or ASSOCIATE_NOTE_UNKNOWN
     *  
     */        
    public Long checkIfAssociatedLucy(String uniEventUri) {
    	//[Phone team use]
        return LucyHelper.checkIfAssociatedNotes(mContext, uniEventUri);
    }
    
    /**
     * To check whether the event is exist or not by given uri
     * 
     * @param event uri, format:  content://com.android.calendar/events/4233/EventTime/1292405400000/1292409000000
     * 
     * @return True if the event is exist in Calendar database  
     */
    public boolean isEventExist(Uri uri) {
        return UriTools.isEventExist(mContext, uri);
    }

    /**
     * To transfer uni event uri to event uri. 
     * 
     * @return event uri, format: content://com.android.calendar/events/4233/EventTime/1292405400000/1292409000000
     */
    public Uri uniEventToEventUri(Uri uniEvent) {
        return UriTools.uniEventToEventUri(mContext, uniEvent);
    }
    
    /**
     * To generate event uri string
     * 
     * @param eventId event id in Calendar database
     * @param startTime event start time, UTC in milliseconds
     * @param endTime event end time, UTC in milliseconds
     * @return event uri, format:  content://com.android.calendar/events/4233/EventTime/1292405400000/1292409000000
     */
    public String generateEventUriString(long eventId, long startTime, long endTime) {
        return UriTools.generateEventUriString(eventId, startTime, endTime);
    }

    /**
     * To generate event uri
     * 
     * @param eventId event id in Calendar database
     * @param startTime event start time, UTC in milliseconds
     * @param endTime event end time, UTC in milliseconds
     * @return event uri, format:  content://com.android.calendar/events/4233/EventTime/1292405400000/1292409000000
     */
    public Uri generateEventUri(long eventId, long startTime, long endTime) {
        return UriTools.generateEventUri(eventId, startTime, endTime);      
    }
    
    /**
      * To generate the event uri string
      * @param eventId the event ID
      * @return the event uri string
      */
    public String generateTheEventUriString(long eventId) {
        return UriTools.generateTheEventUriString(mContext, eventId);
    }
    
   /**
     * To generate the event uri
     * @param eventId the event ID
     * @return the event uri
     */
    public Uri generateTheEventUri(long eventId) {
        return UriTools.generateTheEventUri(mContext, eventId);
    }
    
    /**
     * To generate event uri with vcalendar and event title by given eventId. If the event can't query by id eturn null
     * 
     * @param eventId event id in Calendar database
     * @return uni event uri, format: content://com.htc.calendar/vcalendar/4233/event_title
     */
    public Uri generateTheEventVCalendarEventTitleUri(long eventId) {
        Uri ret = UriTools.generateTheEventVCalendarEventTitleUri(mContext, eventId);
        
        if( ret != null){
            //String title = ret.getLastPathSegment();
            HtcVCalendar vcal = buildVCalendar(eventId);
            FileOutputStream fout = null;
            //String fileName = String.valueOf(new java.util.Date().getTime()); 

            try {
                //fout = mContext.openFileOutput(String.valueOf(eventId), Context.MODE_WORLD_READABLE);

                String pathStr = mContext.getFilesDir() + File.separator + "vcalendar" ;
                File path = new File(pathStr);
                if(!path.exists()) {
                    path.mkdir();
                }
                
                File fileout = new File(path, File.separator + String.valueOf(eventId));
                
                fout = new FileOutputStream( fileout );
                fout.write(vcal.getContent().getBytes());
                //Log.e(TAG, "fileout name " + fileout.getPath());
                
            }catch (FileNotFoundException e){
                Log.e(TAG, "generateTheEventVCalendarEventTitleUri() #1 should not be null:" + e.toString());
            } catch (IOException e){
                Log.e(TAG, "generateTheEventVCalendarEventTitleUri() #2 should not be null:" + e.toString());
                ret = null;
            }
            finally {
                try {
                	if(fout!=null) {
                		fout.close();
                	}
                }catch (IOException e){
                    ret = null;
                }
            }
        }
        return ret;
    }
    
    
    /**
     * The corresponding indices into the array of strings
     * "R.array.delete_repeating_labels" in the resource file.
     */
    public static final int DELETE_SELECTED = 0;

    /**
      * The corresponding indices into the array of strings
      * "R.array.delete_repeating_labels" in the resource file.
      */
    public static final int DELETE_ALL_FOLLOWING = 1;

    /**
      * The corresponding indices into the array of strings
      * "R.array.delete_repeating_labels" in the resource file.
      */
    public static final int DELETE_ALL = 2;
    
    /**
     * To delete event uri by given iCalGUID.
     * 
     * @param parent Activity The parent activity instance
     * @param begin long ex:1297256400000 format
     * @param end long ex:1297258200000 format
     * @param strUid String ex:CD0000008B9511D182D800C04FB1625D2B660B42A9D1974B8D900E670F2018EC format
     * @param which int DELETE_SELECTED, DELETE_ALL_FOLLOWING, DELETE_ALL
     * @deprecated [Not use any longer]
     */
    /**@hide*/ 
    public void delete(Activity parent, long begin, long end, String strUid, int which){
        
    	Log.v("DeleteEventHelper","HtcCalendarManager, strUid is : "+strUid + ", which is:"+which);
        DeleteEventHelper deletehelper = new DeleteEventHelper(parent, false /* exit when done */);
        
        final String selection = VersionCheckUtils.afterAPI21() ? (Events.SYNC_DATA7+"=?") : (ExEvents.ICALENDAR_UID+"=?");
        	
        Cursor EventCursor = mContext.getContentResolver().query(Events.CONTENT_URI, DeleteEventHelper.getDeleteProjection(), selection, new String[]{strUid}, null);
        if (EventCursor != null) {
        	EventCursor.moveToFirst();
        	deletehelper.delete(begin, end, EventCursor, which, false, true, true);
        	if(!EventCursor.isClosed()) {
                EventCursor.close();
            }
        }
        
    }
    
    /**
     * To delete event uri by given iCalGUID.
     * 
     * @param context Context The Context
     * @param begin long ex:1297256400000 format
     * @param end long ex:1297258200000 format
     * @param strUid String ex:CD0000008B9511D182D800C04FB1625D2B660B42A9D1974B8D900E670F2018EC format
     * @param which int DELETE_SELECTED, DELETE_ALL_FOLLOWING, DELETE_ALL
     */
    public void delete(Context context, long begin, long end, String strUid, int which){
    	Log.v("DeleteEventHelper","HtcCalendarManager, strUid is : "+strUid + ", which is:"+which);

    	final String selection = VersionCheckUtils.afterAPI21() ? (Events.SYNC_DATA7+"=?") : (ExEvents.ICALENDAR_UID+"=?");
    	
        DeleteEventHelper deletehelper = new DeleteEventHelper(context, false /* exit when done */);
        Cursor EventCursor = mContext.getContentResolver().query(Events.CONTENT_URI, DeleteEventHelper.getDeleteProjection(), selection, new String[]{strUid}, null);
        if (EventCursor != null) {
        	EventCursor.moveToFirst();
        	deletehelper.delete(begin, end, EventCursor, which, false, true, true);
        	if(!EventCursor.isClosed()) {
                EventCursor.close();
            }
        }
        
    }
        
        
    private static final int CLEAR_ALPHA_MASK = 0x00FFFFFF;
    private static final int ADD_ALPHA_MASK = 0xFF000000;
    
    private static final int R_MASK = 0x00FF0000;
    private static final int G_MASK = 0x0000FF00;
    private static final int B_MASK = 0x000000FF;
    private static final int Distance = 150;

    private static final int color1 = 0xd06b64;
    private static final int color2 = 0xf691b2;
    private static final int color3 = 0xcd74e6;
    private static final int color4 = 0x784bd0;
    private static final int color5 = 0x4986e7;
    private static final int color6 = 0x5476d0;
    private static final int color7 = 0x42d692;
    private static final int color8 = 0x92e1c0;
    private static final int color9 = 0x40a441;
    private static final int color10 = 0xb3dc6c;

    private static final int color11 = 0xb2b43d;
    private static final int color12 = 0xfbe983;
    private static final int color13 = 0xffad46;
    private static final int color14 = 0xff7537;
    private static final int color15 = 0xcca6ac;
    private static final int color16 = 0xb99aff;
    private static final int color17 = 0x768594;
    private static final int color18 = 0x9fc6e7;
    private static final int color19 = 0x739b94;
    private static final int color20 = 0x96986a;

    private static final int color21 = 0xb79870;
    private static final int color22 = 0xb94118;
    private static final int color23 = 0xf83a22;
    private static final int color24 = 0xb3289b;
    private static final int color25 = 0xa47ae2;
    private static final int color26 = 0x425795;
    private static final int color27 = 0x282a9d;
    private static final int color28 = 0x16a765;
    private static final int color29 = 0x4c9926;
    private static final int color30 = 0x7bd148;


    private static final int color31 = 0x97ad1f;
    private static final int color32 = 0xc38810;
    private static final int color33 = 0xc86b18;
    private static final int color34 = 0xac725e;
    private static final int color35 = 0x7d3b11;
    private static final int color36 = 0x99105e;
    private static final int color37 = 0x9a9cff;
    private static final int color38 = 0x9fe1e7;
    private static final int color39 = 0xcabdbf;
    private static final int color40 = 0x369774;


    private static final int color41 = 0xfad165;
    private static final int color42 = 0x9b1c22;


    private double compareColor(int r1, int g1, int b1, int r2, int g2, int b2){
	  double y1 = 0.299 * (double)r1 + 0.587 * (double)g1 + 0.114 * (double)b1;
	  double u1 = -0.14713 * (double)r1 + -0.28886 * (double)g1 + 0.436 * (double)b1;
	  double v1 = 0.615 * (double)r1 + -0.51499 * (double)g1 + -0.10001 * (double)b1;
	  
	  
	  double y2 = 0.299 * (double)r2 + 0.587 * (double)g2 + 0.114 * (double)b2;
	  double u2 = -0.14713 * (double)r2 + -0.28886 * (double)g2 + 0.436 * (double)b2;
	  double v2 = 0.615 * (double)r2 + -0.51499 * (double)g2 + -0.10001 * (double)b2;
	  
	  double diff_y = Math.abs(y1 - y2);
	  double diff_u = Math.abs(u1 - u2);
	  double diff_v = Math.abs(v1 - v2);
	  double distance = Math.sqrt( Math.pow(diff_y, 2) + Math.pow(diff_u, 2) + Math.pow(diff_v, 2) );
	  return distance;
    }
    
    private static final HashMap<Integer, Integer> colorMappingToIndex = new HashMap<Integer, Integer>();
    static {
      colorMappingToIndex.put(color1, 0);
      colorMappingToIndex.put(color2, 1);
      colorMappingToIndex.put(color3, 2);
      colorMappingToIndex.put(color4, 3);
      colorMappingToIndex.put(color5, 4);
      colorMappingToIndex.put(color6, 5);
      colorMappingToIndex.put(color7, 6);
      colorMappingToIndex.put(color8, 7);
      colorMappingToIndex.put(color9, 8);
      colorMappingToIndex.put(color10, 9);
      colorMappingToIndex.put(color11, 10);
      colorMappingToIndex.put(color12, 11);
      colorMappingToIndex.put(color13, 12);
      colorMappingToIndex.put(color14, 13);
      colorMappingToIndex.put(color15, 14);
      colorMappingToIndex.put(color16, 15);
      colorMappingToIndex.put(color17, 16);
      colorMappingToIndex.put(color18, 17);
      colorMappingToIndex.put(color19, 18);
      colorMappingToIndex.put(color20, 19);
      colorMappingToIndex.put(color21, 20);
      colorMappingToIndex.put(color22, 21);
      colorMappingToIndex.put(color23, 22);
      colorMappingToIndex.put(color24, 23);
      colorMappingToIndex.put(color25, 24);
      colorMappingToIndex.put(color26, 25);
      colorMappingToIndex.put(color27, 26);
      colorMappingToIndex.put(color28, 27);
      colorMappingToIndex.put(color29, 28);
      colorMappingToIndex.put(color30, 29);
      colorMappingToIndex.put(color31, 30);
      colorMappingToIndex.put(color32, 31);
      colorMappingToIndex.put(color33, 32);
      colorMappingToIndex.put(color34, 33);
      colorMappingToIndex.put(color35, 34);
      colorMappingToIndex.put(color36, 35);
      colorMappingToIndex.put(color37, 36);
      colorMappingToIndex.put(color38, 37);
      colorMappingToIndex.put(color39, 38);
      colorMappingToIndex.put(color40, 39);
      colorMappingToIndex.put(color41, 40);
      colorMappingToIndex.put(color42, 41);
    }

    private static final int[] COLOR_MATRIX_RES = {
      color1, color2, color3, color4, color5, color6, color7, color8, color9, color10,
      color11, color12, color13, color14, color15, color16, color17, color18, color19, color20,
      color21, color22, color23, color24, color25, color26, color27, color28, color29, color30,
      color31, color32, color33, color34, color35, color36, color37, color38, color39, color40,
      color41, color42
    };
    
    
    /**
     * To get drawable most close to color
     * 
     *
     * @return the most close drawable among array drawable_array
     *  Hide Automatically by SDK Team [U12000]
     *  @hide
     */
    public Integer getMappingColorAssetid(int color, int[] drawable_array){
        if( drawable_array.length < 0 || drawable_array.length != colorMappingToIndex.size() ){
            throw new IllegalArgumentException(
                    "drawable_array length does not equal to " + colorMappingToIndex.size() );
        }
        color &= CLEAR_ALPHA_MASK;
        try{
          return drawable_array[colorMappingToIndex.get(color)];
        }
        catch ( Exception e) {
          Log.e(TAG, "getAvailableColorAsset error :" + e.toString());
          double diff = 0.0;
          int matched_color_res = 0;
          for( int index = 0; index < COLOR_MATRIX_RES.length; index++){
                int color_res = COLOR_MATRIX_RES[index] ;
                double temp_diff = compareColor( (color & R_MASK) >> 16,   (color & G_MASK) >> 8 ,  color & B_MASK, (color_res & R_MASK) >> 16, (color_res & G_MASK) >> 8,  color_res & B_MASK ) ; 
                
                if(diff == 0.0 || temp_diff < diff){
                    diff = temp_diff ;
                    matched_color_res =  color_res;
                } 
          }
          return drawable_array[colorMappingToIndex.get(matched_color_res)];
        }
    }
    
    /**
     * To get drawable most close to now available color
     * 
     *
     * @return the most close drawable among array drawable_array, return -1 if fail
     *  Hide Automatically by SDK Team [U12000]
     *  @hide
     */
    public Integer getAvailableColorAsset(int[] drawable_array){
        int color = getAvailableColor();
        if( color != -1)
            return getMappingColorAssetid(color, drawable_array);
        return color ;
    }
    
      /**
     * To get available color value
     * 
     *
     * @return color value, return -1 if fail
     * @deprecated [Module internal use]
     */
    /**@hide*/ 
    public Integer getAvailableColor(){

        HashMap<Integer, Integer> colorUsage = new HashMap<Integer, Integer>();
      
        colorUsage.put(-3198430, 0 );
        colorUsage.put(-2205827, 0 );
        colorUsage.put(-6469461, 0 );
        colorUsage.put(-9419857, 0 );
        colorUsage.put(-16687993, 0 );
        colorUsage.put(-14777720, 0 );
        colorUsage.put(-14572677, 0 );
        colorUsage.put(-14247124, 0 );
        colorUsage.put(-12022556, 0 );
        colorUsage.put(-8869641, 0 );
        colorUsage.put(-4748821, 0 );
        colorUsage.put(-935678, 0 );
        colorUsage.put(-159732, 0 );
        colorUsage.put(-1677556, 0 );
        colorUsage.put(-3971504, 0 );
        colorUsage.put(-9417893, 0 );
        colorUsage.put(-13416077, 0 );
        colorUsage.put(-10200183, 0 );
        colorUsage.put(-16215719, 0);
        colorUsage.put(-8097011, 0);
        colorUsage.put(-3828424, 0);
        colorUsage.put(-15894509, 0); //PC Sync
        
        //these color below are google calendar color
        colorUsage.put(ADD_ALPHA_MASK | color1, 0);
        colorUsage.put(ADD_ALPHA_MASK | color2, 0);
        colorUsage.put(ADD_ALPHA_MASK | color3, 0);
        colorUsage.put(ADD_ALPHA_MASK | color4, 0);
        colorUsage.put(ADD_ALPHA_MASK | color5, 0);
        colorUsage.put(ADD_ALPHA_MASK | color6, 0);
        colorUsage.put(ADD_ALPHA_MASK | color7, 0);
        colorUsage.put(ADD_ALPHA_MASK | color8, 0);
        colorUsage.put(ADD_ALPHA_MASK | color9, 0);
        colorUsage.put(ADD_ALPHA_MASK | color10, 0);
        colorUsage.put(ADD_ALPHA_MASK | color11, 0);
        colorUsage.put(ADD_ALPHA_MASK | color12, 0);
        colorUsage.put(ADD_ALPHA_MASK | color13, 0);
        colorUsage.put(ADD_ALPHA_MASK | color14, 0);
        colorUsage.put(ADD_ALPHA_MASK | color15, 0);
        colorUsage.put(ADD_ALPHA_MASK | color16, 0);
        colorUsage.put(ADD_ALPHA_MASK | color17, 0);
        colorUsage.put(ADD_ALPHA_MASK | color18, 0);
        colorUsage.put(ADD_ALPHA_MASK | color19, 0);
        colorUsage.put(ADD_ALPHA_MASK | color20, 0);
        colorUsage.put(ADD_ALPHA_MASK | color21, 0);
        colorUsage.put(ADD_ALPHA_MASK | color22, 0);
        colorUsage.put(ADD_ALPHA_MASK | color23, 0);
        colorUsage.put(ADD_ALPHA_MASK | color24, 0);
        colorUsage.put(ADD_ALPHA_MASK | color25, 0);
        colorUsage.put(ADD_ALPHA_MASK | color26, 0);
        colorUsage.put(ADD_ALPHA_MASK | color27, 0);
        colorUsage.put(ADD_ALPHA_MASK | color28, 0);
        colorUsage.put(ADD_ALPHA_MASK | color29, 0);
        colorUsage.put(ADD_ALPHA_MASK | color30, 0);
        colorUsage.put(ADD_ALPHA_MASK | color31, 0);
        colorUsage.put(ADD_ALPHA_MASK | color32, 0);
        colorUsage.put(ADD_ALPHA_MASK | color33, 0);
        colorUsage.put(ADD_ALPHA_MASK | color34, 0);
        colorUsage.put(ADD_ALPHA_MASK | color35, 0);
        colorUsage.put(ADD_ALPHA_MASK | color36, 0);
        colorUsage.put(ADD_ALPHA_MASK | color37, 0);
        colorUsage.put(ADD_ALPHA_MASK | color38, 0);
        colorUsage.put(ADD_ALPHA_MASK | color39, 0);
        colorUsage.put(ADD_ALPHA_MASK | color40, 0);
        colorUsage.put(ADD_ALPHA_MASK | color41, 0);
        colorUsage.put(ADD_ALPHA_MASK | color42, 0);

        
        String [] projectArys = new String[] {
            CalendarContract.Calendars.CALENDAR_COLOR,
        };

        ContentResolver cr = mContext.getContentResolver();
        String where = CalendarContract.Calendars.CALENDAR_COLOR + " IS NOT NULL ";
        Cursor c = cr.query(android.provider.CalendarContract.Calendars.CONTENT_URI, projectArys, where, null, null);

        if(null != c) {
            int nCnt = c.getCount();
            Log.v(TAG, "total have " + nCnt + " color IS NOT NULL");
            if(nCnt > 0) {
                
                
                if(c.moveToFirst()) {
                    do {
                        
                        if( colorUsage.get(c.getInt(0)) != null ){
                             colorUsage.put(c.getInt(0), 1);
                        }
                        else{
                            Log.w(TAG, "could not find matching for color " + c.getInt(0) );
                        }
                      
                        
                    } while(c.moveToNext());
                }
                if(!c.isClosed()) {
                    c.close();
                }
                
                Iterator it=colorUsage.keySet().iterator();
                while(it.hasNext()){
                    Integer key = (Integer)it.next() ;
                    Integer value = colorUsage.get(key);
                    
                    if( value == 0 ){
                        Log.v(TAG, "Avaiable color " + key );
                        return key;
                    }
                }
            }
        }
        
        return -1;
    }
  
    /**
     * To build vCalendar object by given event uri
     * 
     * @param uniEvent Uri The uni event URI data
     * @return HtcVCalendar Return the HtcVCalendar data of the uni event
     */
    public HtcVCalendar buildVCalendar(Uri uniEvent) {
        return new HtcVCalendar().buildVCalendar(mContext, uniEvent);
    }
    
    /**
     * To build vCalendar object by given event id<br>
     * 
     * HtcVCalendar.getTitle() to get title of the vCalendar<br>
     * HtcVCalendar.getContent() to get vCal string<br>
     *    
     * @param eventId long The event ID
     * @return HtcVCalendar Return the HtcVCalendar data of the event ID
     */
    public HtcVCalendar buildVCalendar(long eventId) {
        Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, eventId);
        return buildVCalendar(uri);
    }
    
    /**
     * To build events
     * @param vCalString vCal(1.0) string
     * @return the ContentValues in ArrayList way
     *  Hide Automatically by SDK Team [U12000]
     *  @hide
     */
    public ArrayList<ContentValues> buildEvents(String vCalString) {
    	VCalendarUtils cal = new VCalendarUtils();
    	boolean isPass = cal.parseVCalendar(vCalString);
    	if (!isPass) return null;
    	return cal.getMultiEventsCV();
    }
    
    /**
     * Import vCal to Calendar database
     * 
     * @param cr ContentResolver
     * @param calendar_id
     * @param events
     * @param alarms
     * @param checker
     * @return number of events to be import
     */
    
    public int importCalendar( ContentResolver cr, long calendar_id, ArrayList<ContentValues> events, ArrayList<ContentValues> alarms, OnPCEventChecker checker) {
        int idx = 0;
        int numberCal = 0;
        for(ContentValues cv : events) {
            
            // TODO: 1. Tell me, why not using cv??
            // TODO: 2. Tell me, why cv would be null??
            ContentValues cacheContentValue = cv;
            cacheContentValue.put(CalendarContract.Events.CALENDAR_ID, calendar_id);
            
            boolean duplicated = false;
            if(checker!=null){
                 duplicated = checker.isSameEvent(cacheContentValue);
            }

            if(duplicated){
                Log.v(TAG, "duplicated happened!!!");
                continue;
            }
            // insert it in Events table
            Uri newEvent = cr.insert(getImportCalendarUri(cacheContentValue, cr), cacheContentValue);
            String newEventId = newEvent.getPathSegments().get(1);
            numberCal++;
                
            ContentValues cacheContentValue2 = alarms.get(idx);
            if(cacheContentValue2!=null){
                boolean hasAlarm = (cacheContentValue.getAsInteger(Events.HAS_ALARM) == 1);
                if (hasAlarm) {
                    // update event_id in Reminders table
                    cacheContentValue2.put(CalendarContract.Reminders.EVENT_ID, newEventId);
                    // insert it in Reminders table
                    cr.insert(CalendarContract.Reminders.CONTENT_URI, cacheContentValue2);
                }
            }
            idx++;          
        }
        
        return numberCal;
    }
    
    /**
      * The OnPCEventChecker interface
      */
    public interface OnPCEventChecker {
        /**
         * The function is called when insert event
         *
         * @param event The event ContentValues
         * @return return true if duplicated event found
         * @deprecated [Module internal use]
         */
    	/**@hide*/ 
        public boolean isSameEvent(ContentValues event);
    }
    
    /**
     * Import vCal to Calendar database\
     * used in IcsImportActivity
     * @param cr ContentResolver The content resolver
     * @param calendar_id long The calendar id
     * @param vCalString String The vCalString value
     * @return int Return the number of events to be import
     */
    public int importCalendar(ContentResolver cr, long calendar_id, String vCalString) {
        return importCalendar(cr, calendar_id, vCalString, null);
    }
    
     /**
     * Import vCal to Calendar database
     * 
     * @param cr ContentResolver The content resolver
     * @param calendar_id long The calendar id
     * @param vCalString String The vCalString value
     * @param checker Interface, called before insert event
     * @return int Return the number of events to be import
     */
    
    public int importCalendar(ContentResolver cr, long calendar_id, String vCalString, OnPCEventChecker checker) {
    	VCalendarUtils cal = new VCalendarUtils();
    	boolean isPass = cal.parseVCalendar(vCalString);
    	
    	if (!isPass) return 0;
    	
		ArrayList<ContentValues> multiEventsCv = cal.getMultiEventsCV();
		ArrayList<ContentValues> multiToDoCv = cal.getMultiToDoCV();
		ArrayList<ContentValues> multiEventsAlarmCv = cal.getMultiEventAlarmsCV();
		ArrayList<ContentValues> multiToDoAlarmCv = cal.getMultiToDoAlarmsCV();
		
		int evCal = importCalendar( cr, calendar_id, multiEventsCv, multiEventsAlarmCv, checker);
		int todoCal = importCalendar( cr, calendar_id, multiToDoCv, multiToDoAlarmCv, checker);
		
		return (evCal + todoCal);
    }
    
    /**
    * get number of events according to vcalendar format
    * @param vCalString
    * @return number of events
    * @deprecated [Not use any longer]
    */
	/**@hide*/     
    public int getNumOfEvents(String vCalString) {
        VCalendarUtils cal = new VCalendarUtils();
        boolean isPass = cal.parseVCalendar(vCalString);
    
        if (!isPass) return 0;
    
        ArrayList<ContentValues> multiEventsCv = cal.getMultiEventsCV();
        ArrayList<ContentValues> multiToDoCv = cal.getMultiToDoCV();
        return multiEventsCv.size()+multiToDoCv.size();
    }
        
    private static final String[] ATTENDEES_PROJECTION = new String[] {
        Attendees._ID,                      // 0
        Attendees.ATTENDEE_NAME,            // 1
        Attendees.ATTENDEE_EMAIL,           // 2
        Attendees.ATTENDEE_RELATIONSHIP,    // 3
        Attendees.ATTENDEE_STATUS,          // 4
    };
    
    /**
     * To get the attendee list
     * @param uri Uri The URI data
     * @param type int The attendee type, -1 means all
     * @return ArrayList Return the ArrayList of the attendees
     */
    
    public ArrayList<Attendee> getAttendees(Uri uri, int type) {
    	ArrayList<Attendee> member = null;
    	ContentResolver cr = mContext.getContentResolver();
    	long event_id = UriTools.getEventId(mContext, uri);
    	if (event_id < 0) return null;
    	
    	StringBuffer where = new StringBuffer()
    	                    .append(Attendees.EVENT_ID)
    	                    .append("=").append(event_id);
    	
    	if (  (type == Attendees.ATTENDEE_STATUS_NONE) 
    	    ||(type == Attendees.ATTENDEE_STATUS_ACCEPTED)
    	    ||(type == Attendees.ATTENDEE_STATUS_DECLINED)
    	    ||(type == Attendees.ATTENDEE_STATUS_INVITED)
    	    ||(type == Attendees.ATTENDEE_STATUS_TENTATIVE) ) {
    		
    		where.append(" and ")
    		     .append(Attendees.ATTENDEE_STATUS)
    		     .append("=").append(type);
    		
    	}
    	
    	Debug("getAttendees: where=" + where.toString());
    		
    	
    	StringBuffer sort = new StringBuffer()
    	                    .append(Attendees.ATTENDEE_NAME)
    	                    .append(" ASC, ")
    	                    .append(Attendees.ATTENDEE_EMAIL)
    	                    .append(" ASC");
    	
    	Cursor cursor = cr.query(Attendees.CONTENT_URI, ATTENDEES_PROJECTION, where.toString(), null, sort.toString());
    	
    	if (cursor == null) return null;
    	if (cursor.moveToFirst()) {
    		member = new ArrayList<Attendee>();
    		do {
                int status   = cursor.getInt(cursor.getColumnIndexOrThrow(Attendees.ATTENDEE_STATUS));
                String name  = cursor.getString(cursor.getColumnIndexOrThrow(Attendees.ATTENDEE_NAME));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(Attendees.ATTENDEE_EMAIL));
                if(ExCalendars.CALENDAR_OWNER_DEFAULT.equalsIgnoreCase(email) ||
                        ExCalendars.CALENDAR_OWNER_EXCHANGE.equalsIgnoreCase(email) ||
                        ExCalendars.CALENDAR_OWNER_OUTLOOK.equalsIgnoreCase(email)) {
                     	continue;
                }    			
    			member.add(new Attendee(name, email, status));
    			
    		} while (cursor.moveToNext());
    		
    	}
		if (!cursor.isClosed()) cursor.close();
        cursor = null;
    	
    	return member;
    }
    
    /**
      * To get the event
      * @param eventUri the event uri
      * @return the event instance
      */
    public EventInstance getEvent(Uri eventUri) {
        return UriTools.getEventInstance(mContext, eventUri);
    }
    
    private void Debug(String s) {
        if (DEBUG) {
            if (s != null) Log.i(TAG, s);
        }
    }
    /**
      * For calendar backup
      * @author Fong add
      * @param cr ContentResolver the ContentResolver
      * @param calendar_id long the Calendar ID
      * @param vCalString String the string of vCalendar
      * @param progressHandler Handler the Handler
      * @param existEvents Cursor the existing event cursor
      * @param restoreType int the type of restore
      * @return int the imported evCalendar
      */
    public int importCalendarOnBackground(ContentResolver cr, long calendar_id, String vCalString
    		,Handler progressHandler,Cursor existEvents,int restoreType) {
    	VCalendarUtils cal = new VCalendarUtils();
    	boolean isPass = cal.parseVCalendar(vCalString);
    	
    	if (!isPass) 
    		return 0;
    	
		ArrayList<ContentValues> multiEventsCv = cal.getMultiEventsCV();
		ArrayList<ContentValues> multiEventsAlarmCv = cal.getMultiEventAlarmsCV();		
				
		int evCal = importCalendarOnBackground( cr, calendar_id, multiEventsCv, multiEventsAlarmCv,progressHandler,existEvents,restoreType, null);

		return evCal;
    }
    
     /**
      * The OnProgressListener interface
      */
    public interface OnProgressListener {
        
        public boolean isCanceled();
    }
    
    
    /**
      * For calendar backup
      * @author Fong add
      * @param cr ContentResolver the ContentResolver
      * @param calendar_id long the Calendar ID
      * @param vCalString String the string of vCalendar
      * @param progressHandler Handler the Handler
      * @param existEvents Cursor the existing event cursor
      * @param restoreType int the type of restore
      * @return int the imported evCalendar
      */
    public int importCalendarOnBackground(ContentResolver cr, long calendar_id, String vCalString
    		,Handler progressHandler,Cursor existEvents,int restoreType, OnProgressListener listener) {
    	VCalendarUtils cal = new VCalendarUtils();
    	boolean isPass = cal.parseVCalendar(vCalString);
    	
    	if (!isPass) 
    		return 0;
    	
		ArrayList<ContentValues> multiEventsCv = cal.getMultiEventsCV();
		ArrayList<ContentValues> multiEventsAlarmCv = cal.getMultiEventAlarmsCV();		
				
		int evCal = importCalendarOnBackground( cr, calendar_id, multiEventsCv, multiEventsAlarmCv,progressHandler,existEvents,restoreType, listener);

		return evCal;
    }
    
    
    private static final int MSG_UI_UPDATE_PROGRESSBAR = 1;
    private static final int RESTORE_TYPE_KEEP_PHONE_CALENDAR = 1;
    private static final int RESTORE_TYPE_SD_CALENDAR = 2;
    	
    private int importCalendarOnBackground( ContentResolver cr, long calendar_id, ArrayList<ContentValues> events, ArrayList<ContentValues> alarms
    		,Handler progressHandler, Cursor existEvents,int restoreType, OnProgressListener listener) {
    	
    	if(DEBUG)
    		Log.d(TAG,"importCalendarOnBackground()");
    	    	
    	
    	if(calendar_id != 1) {
    		Log.w(TAG,"importCalendarOnBackground calendar_id != 1");
    		return 0;
    	}
    	
        int numberCal = 0;
        int size = events.size();
        String importGUID = "";
        int importEventID = -1;        
        
		Bundle bundle = new Bundle();
    
        
        for(ContentValues cv : events) {
        	
        	importGUID = cv.getAsString(getICalendarUIDField());           	
        	importEventID = getImportEventID(existEvents,importGUID);      	
        	
        	ContentValues contentValueEvent = cv;   
        	ContentValues contentValueAlarm = alarms.get(numberCal);
        	
        	contentValueEvent.put(CalendarContract.Events.CALENDAR_ID, calendar_id);
        	
        	if(importEventID != -1) {
        		//event is exist
        		switch (restoreType) {
        		case RESTORE_TYPE_KEEP_PHONE_CALENDAR:
        			//can't do anything	
        			break;
        		case RESTORE_TYPE_SD_CALENDAR:
        			updateDB(cr,importGUID,importEventID,contentValueEvent,contentValueAlarm);  
        			break;
        		}     
        	} else {
        		insertDB(cr,contentValueEvent,contentValueAlarm);
        	}        	        	
        	numberCal++; 
        	
            if(progressHandler!=null) {  
            	Message msg = new Message();
				bundle.putInt("value", numberCal);
				bundle.putInt("max", size);

				msg.setData(bundle);
				msg.what = MSG_UI_UPDATE_PROGRESSBAR;
				progressHandler.sendMessage(msg);
            } 
            
            if( listener != null){
                
                if(listener.isCanceled()){
                    Log.d(TAG,"user cancel");
                    return numberCal;
                }
                
            }
                
        }        
        return numberCal;
    }
    
    private int getImportEventID(Cursor c, String importGUID) {
    	
    	if(c == null) {
    		return -1; 		
    	}
    	
    	//always restore vCalendar when the GUID is empty string 
    	if(TextUtils.isEmpty(importGUID))
    		return -1;
    	
    	int imporEventID = -1;
    	
    	int colUID = c.getColumnIndexOrThrow(getICalendarUIDField()); 
    	int colEventID = c.getColumnIndexOrThrow(Events._ID); 
    	
    	if (c.moveToFirst()) {
    		do {    			
    			// get the event iCal UID    			   			
    			String uid = c.getString(colUID);    	        
    	        if(importGUID.equals(uid)) {    	        	
    	        	imporEventID = c.getInt(colEventID);  
    	        	break;
    	        }
			} while (c.moveToNext());
    	}    	
    	return imporEventID;    	
    }
    
    private void insertDB(ContentResolver cr, ContentValues contentValueEvent,ContentValues contentValueAlarm) {

		// insert it in Events table
		Uri newEvent = cr.insert(getImportCalendarUri(contentValueEvent, cr), contentValueEvent);
		
		String newEventId = newEvent.getPathSegments().get(1);		
		if (contentValueAlarm != null) {
			boolean hasAlarm = (contentValueEvent.getAsInteger(Events.HAS_ALARM) == 1);
			if (hasAlarm) {
				// update event_id in Reminders table
				contentValueAlarm.put(CalendarContract.Reminders.EVENT_ID,
						newEventId);
				// insert it in Reminders table
				cr.insert(CalendarContract.Reminders.CONTENT_URI, contentValueAlarm);
			}
		}
    }
    
	private void updateDB(ContentResolver cr, String importGUID, int importEventID,
			ContentValues contentValueEvent,ContentValues contentValueAlarm) {		
		final String where = getICalendarUIDField() +" = '"+ importGUID+"'" ;		
		final String alarm_where = CalendarContract.Reminders.EVENT_ID+" = "+ importEventID ;		
		
		cr.update(CalendarContract.Events.CONTENT_URI, contentValueEvent,where,null);
		
		if (contentValueAlarm != null) {
			boolean hasAlarm = (contentValueEvent.getAsInteger(Events.HAS_ALARM) == 1);
			if (hasAlarm) {
				
				contentValueAlarm.put(CalendarContract.Reminders.EVENT_ID, importEventID);
				
				int count = cr.update(CalendarContract.Reminders.CONTENT_URI,
						contentValueAlarm, alarm_where ,null);	

				//if alarm not exist in Reminders, then insert the value.
				if(count < 1 ) {
					
					if(DEBUG) {
			    		Log.d(TAG,"updateDB (insert alarm) - count:"+ count
					+" importGUID:"	+importGUID + " importEventID:"+importEventID);
			    		
					}
					cr.insert(CalendarContract.Reminders.CONTENT_URI, contentValueAlarm);
				}
			}
		}
    }
	
	/**
	  * To determine if the event conflicts other event
	  * @param GUID the event GUID
	  * @param startMillis the start millis seconds
	  * @param endMillis the end millis seconds
	  * @return boolean true when the event conflict with other event, false when the event doesn't conflict with others
	  */
	public boolean isEventConflict(String GUID, long startMillis, long endMillis) {        
		return isEventConflict(GUID, startMillis, endMillis, -1);
    }
	
	/**
	  * To determine if the event conflicts other event
	  * @param GUID the event GUID
	  * @param startMillis the start millis seconds
	  * @param endMillis the end millis seconds
	  * @param calendarId, check calendar include this calendar id whether it's visible or not
	  * @return boolean true when the event conflict with other event, false when the event doesn't conflict with others
	  */
	
	public boolean isEventConflict(String GUID, long startMillis, long endMillis, long calendarId) {        
		if(VersionCheckUtils.afterAPI21()) {
			return checkEventConflict(GUID,startMillis,endMillis,calendarId);
		} else {
			return checkEventConflictOld(GUID,startMillis,endMillis,calendarId);
		}	 
	}
	
	private boolean checkEventConflict(String GUID, long startMillis, long endMillis, long calendarId) {
		boolean isMeetingConflict = false;
        final String[] selectionArgs = new String[] { GUID, String.valueOf(startMillis), String.valueOf(endMillis) };

        ContentResolver cr = mContext.getContentResolver();
        Cursor c = null; 

        Uri AUTHORITY_URI = Uri.parse("content://" + UriTools.AUTHORITY);
        Uri uri  = Uri.withAppendedPath(AUTHORITY_URI, "check_meeting_conflict");

        try {
            c = cr.query(uri, null, null, selectionArgs, null);
            
            if(c != null && c.moveToFirst()) {
                isMeetingConflict = c.getInt(c.getColumnIndex("isEventConflict")) == 1 ? true: false;
                Log.v(TAG, "isEventConflict isMeetingConflict = " + isMeetingConflict);
            } else {
                Log.w(TAG, "isEventConflict query problem");
            }

        } catch (Exception e) {
            Log.e(TAG, "isEventConflict e = "+e.toString());
        } finally {
            if(c != null && ! c.isClosed()) {
                c.close();
            }
        }
        return isMeetingConflict;
	}
	
	private boolean checkEventConflictOld(String GUID, long startMillis, long endMillis, long calendarId) {
		boolean isMeetingConflict = false;
		ContentResolver cr = mContext.getContentResolver();
		    
		String[] proj = {Events._ID};
	    Time time = new Time();
	    time.setToNow();
	    int startJulianDay = Time.getJulianDay(startMillis, time.gmtoff);
	    //int endJulianDay = startJulianDay +1;
	    int eventEndJulianDay = Time.getJulianDay(endMillis, time.gmtoff);
	    Uri uri = Uri.withAppendedPath(Instances.CONTENT_BY_DAY_URI, startJulianDay + "/" + eventEndJulianDay ); 
	    Cursor cursor = null;
	    String includeCalendarId = "";
	    if(calendarId != -1) {
	       includeCalendarId = " OR " + Events.CALENDAR_ID + " = " + calendarId ;
	    }
	    
	    String where = "(( "+Instances.BEGIN+" < " + endMillis + " AND "+Instances.END+" > " + startMillis 
	    		   + ")OR((" + Events.ALL_DAY + "= 1) AND (startDay >= "+ startJulianDay + " AND endDay <= "+ eventEndJulianDay 
	    		   +"))) AND Events._id not in (select Events._id from Events where " 
	               + ExEvents.FACEBOOK_TYPE + "= 0 OR " + Events.ACCOUNT_TYPE + " like '%HTC_BirthdayEvent%'" 
	               + " OR " + ExEvents.ICALENDAR_UID + " like '" + GUID + "')"
	               + " AND " + Events.CALENDAR_ID + " not in (Select _id from Calendars where " 
	               + Calendars.CALENDAR_ACCESS_LEVEL  + "<" + Calendars.CAL_ACCESS_CONTRIBUTOR  + ") " 
	               + "AND ((main_visible = 1 AND " + Calendars.VISIBLE +" = 1) " + includeCalendarId +") ";      
	              
	    try {           
	       cursor = cr.query(uri, proj, where, null ,null);
	       if(cursor.getCount() > 0) {
	    	   isMeetingConflict = true;
	       } else {
	    	   isMeetingConflict = false;
	       }         
	    } catch (Exception e) {
	    	 Log.e(TAG, "checkEventConflictOld e = "+e.toString());
	    } finally {
	        if(cursor != null && !cursor.isClosed()) {
	            cursor.close();
	        }
	    }
	    return isMeetingConflict;
	}
	
	private final String getICalendarUIDField() {
		if(VersionCheckUtils.afterAPI21()) {
			return Events.SYNC_DATA7;
		} else {
			return ExEvents.ICALENDAR_UID;
		}
	}
	
	private Uri getImportCalendarUri(ContentValues cv , ContentResolver cr) {
		if(VersionCheckUtils.afterAPI21()) {
			return getSyncAdapterUriByAccount(cv, cr);
		} else {
			return CalendarContract.Events.CONTENT_URI;
		}		
	}
	
    private Uri getSyncAdapterUriByAccount(ContentValues cv , ContentResolver cr) {
    	Uri uri = Events.CONTENT_URI;
    	
    	long calendar_id = 0;
    	if(cv.containsKey(Events.CALENDAR_ID)) {
    		calendar_id = cv.getAsLong(Events.CALENDAR_ID);
    	} else {
    		Log.w(TAG, "getSyncAdapterUriByAccount, should put calendar_id to ContentValues!");
    		return uri;
    	}
    	
        boolean isCvContainsSyncData = containsSyncData(cv);

        if(isCvContainsSyncData) {
            Cursor c = null;
        	String accountName = "";
        	String accountType = "";
            try {
                c = queryAccountNameTypeByCalId(calendar_id, cr);
            	
            	if (c != null && c.moveToFirst()) {
            		accountName = c.getString(c.getColumnIndexOrThrow(Calendars.ACCOUNT_NAME));
            		accountType = c.getString(c.getColumnIndexOrThrow(Calendars.ACCOUNT_TYPE));
            	}
            }  catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (c != null) {
                    if (!c.isClosed()) {
                        c.close();
                        c = null;
                    }
                }
            }
            
            if(!TextUtils.isEmpty(accountName) && !TextUtils.isEmpty(accountType))
                uri = asSyncAdapter(uri, accountName, accountType);
        }
        
        Log.d(TAG, "getSyncAdapterUriByAccount uri  = "+uri.toString()+", isCvContainsSyncData = "+isCvContainsSyncData);
    	
    	return uri;
    }    
     
    private boolean containsSyncData(ContentValues cv) {
    	if(cv != null) {
    		if(cv.containsKey(Events.SYNC_DATA7))
    			return true;
    		else if (cv.containsKey(Events.SYNC_DATA8))
    			return true;
    		else if (cv.containsKey(Events.SYNC_DATA9))
    			return true;
    		else if (cv.containsKey(Events.SYNC_DATA10))
    			return true;
    		else
    			return false;
    	}
    	
    	return false;
    }
    
    private Cursor queryAccountNameTypeByCalId(long calendar_id, ContentResolver cr) {
        Log.d(TAG, "queryAccountNameTypeByCalId calendar_id = "+calendar_id);
        Cursor c = null;
        c = cr.query(Calendars.CONTENT_URI,   new String [] {
            Calendars.ACCOUNT_NAME, Calendars.ACCOUNT_TYPE
                }, 
                Calendars._ID +" = " + calendar_id, 
                null, 
                null);
        return c;
    }
    
    private static Uri asSyncAdapter(Uri uri, String accountName, String accountType) {
        return uri.buildUpon()
            .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER,"true")
            .appendQueryParameter(Calendars.ACCOUNT_NAME, accountName)
            .appendQueryParameter(Calendars.ACCOUNT_TYPE, accountType).build();
     }
    
    
    /** 
     * Since Sense 60 L , move main_visible from CalendarProvider Database to Preference in Calendar AP
     * If other Application want to get the main_visible and visible information, 
     * should use this function to get visible id list 
     * @return calendar id list that main_visible and visible column are true
     */
    public ArrayList<Integer> getVisibleCalendarIdList() {
        if(!VersionCheckUtils.afterAPI21()) {
            throw new IllegalArgumentException("It can't support before API 21.");
        }
        
        Log.i(TAG, "getVisibleCalendarIdList++");
        ArrayList<Integer> calendarIds = new ArrayList<Integer>();
        Cursor c = null;
        
        Uri AUTHORITY_URI = Uri.parse("content://" + UriTools.AUTHORITY);
        Uri uri  = Uri.withAppendedPath(AUTHORITY_URI, "get_visible_id_list");
        
        try {
            c = mContext.getContentResolver().query(uri, null, null, null, null);
            if(c == null) {
                Log.i(TAG, "getVisibleCalendarIdList cursor is null");
                return calendarIds; 
            }

            if(c.moveToFirst()) {
                do {
                    calendarIds.add(c.getInt(0));
                } while (c.moveToNext()); 
            }

        } catch (Exception e) {
            Log.e(TAG, "getVisibleCalendarIdList e = "+e.toString());
        } finally {
            if(c != null && ! c.isClosed()) {
                c.close();
            }
        }

        Log.d(TAG, "getVisibleCalendarIdList--");
        return calendarIds;
    }
    
    
}
