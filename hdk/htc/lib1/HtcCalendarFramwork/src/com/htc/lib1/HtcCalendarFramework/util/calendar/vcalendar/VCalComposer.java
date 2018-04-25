/*
 * Copyright (C) 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.htc.lib1.HtcCalendarFramework.util.calendar.vcalendar;


import java.io.UnsupportedEncodingException;

import android.text.format.Time;
import android.util.Log;

import com.htc.lib1.HtcCalendarFramework.util.calendar.vcalendar.CalendarStruct.EventStruct;
import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;

/**
 * vCalendar string composer class
 * {@exthide}
 */
public class VCalComposer {

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public final static String VERSION_VCALENDAR10 = "vcalendar1.0";
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public final static String VERSION_VCALENDAR20 = "vcalendar2.0";

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public final static int VERSION_VCAL10_INT = 1;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public final static int VERSION_VCAL20_INT = 2;
    
    private final static String AND_ICAL_SYNC_PROD_ID = "-//HTC//AND PIM Sync Client//";

    private static String mNewLine = "\r\n";
    private String mVersion = "";
    private final int foldingLength = 75;

    /**
      * The VCalComposer constructor
      * @deprecated [Module internal use]
      */
    /**@hide*/ 
    public VCalComposer() {
    }

    /**
     * Create a vCalendar String.
     * @param struct see more from CalendarStruct class
     * @param vcalversion MUST be VERSION_VCAL10 /VERSION_VCAL20
     * @return vCalendar string
     * @throws VCalException if version is invalid or create failed
     *  Hide Automatically by SDK Team [U12000]
     *  @hide
     */
    public String createVCal(CalendarStruct struct, int vcalversion)
                                                throws VCalException {

        StringBuilder returnStr = new StringBuilder();

        //Version check
        if(vcalversion != 1 && vcalversion != 2)
            throw new VCalException("version not match 1.0 or 2.0.");
        if (vcalversion == 1)
            mVersion = VERSION_VCALENDAR10;
        else
            mVersion = VERSION_VCALENDAR20;

        //Build vCalendar:
        returnStr.append("BEGIN:VCALENDAR").append(mNewLine);

       if(vcalversion == VERSION_VCAL10_INT)
            returnStr.append("VERSION:1.0").append(mNewLine);
        else
            returnStr.append("VERSION:2.0").append(mNewLine);

        returnStr.append("PRODID:");
        returnStr.append(android.os.Build.MODEL).append(mNewLine);

        // We use UTC in both directions, skip TimeZone fields composing.     
        //Build VEVNET
       returnStr.append(createVCalEvent(struct));

        //Build VTODO
        //TODO

        returnStr.append("END:VCALENDAR").append(mNewLine);

        return returnStr.toString();
    }
    
    /**
     * Create a only vCalendar event body String.
     * @param struct see more from CalendarStruct class
     * @return vCalendar event body string
     * @throws VCalException if version is invalid or create failed
     *  Hide Automatically by SDK Team [U12000]
     *  @hide
     */
    protected String createVCalEvent(CalendarStruct struct)
    									throws VCalException {

        StringBuilder returnStr = new StringBuilder();      

        // We use UTC in both directions, skip TimeZone fields composing.     
        //Build VEVNET
        for(int i = 0; i < struct.eventList.size(); i++){
            String str = buildEventStr(struct.eventList.get(i));
            returnStr.append(str);
        }

        return returnStr.toString();
    }

    private String buildEventStr(CalendarStruct.EventStruct stru){

        StringBuilder strbuf = new StringBuilder();
        String charSet = VCalendarUtils.getDefaultCharSet();

        try {
            strbuf.append("BEGIN:VEVENT").append(mNewLine);
            
            if(!isNull(stru.dtstamp))
                strbuf.append("DTSTAMP:").append(stru.dtstamp).append(mNewLine);

//            if(!stru.isAllday) {
            	strbuf.append("X-OBJECTTYPE:APPOINTMENT").append(mNewLine);
//            } else{
//            	strbuf.append("X-OBJECTTYPE:EVENT").append(mNewLine);
//            }

            //HMS calendar, drop the database column
            /*if(!isNull(stru.last_update_time))
            	strbuf.append("LAST-MODIFIED:").append(stru.last_update_time).append(mNewLine);*/
            
            if(!isNull(stru.uid))
                strbuf.append("UID:").append(stru.uid).append(mNewLine);

            if(!isNull(stru.description)) {
            	strbuf.append(foldingString("DESCRIPTION;ENCODING=QUOTED-PRINTABLE;CHARSET="+charSet+":" + stru.description) + mNewLine);
            }
            
            if(stru.isAllday) {
                strbuf.append("X-ALLDAY:1").append(mNewLine);
            } else {
                strbuf.append("X-ALLDAY:0").append(mNewLine);
            }
            
            if(!isNull(stru.dtend)) {
            	if(stru.isAllday) {
            		String end = convertLongToRFC2445DateTime(stru.dtend);
            		strbuf.append("DTEND;TZID=UTC:").append(end).append(mNewLine);
            	} else {
            		strbuf.append("DTEND:").append(stru.dtend).append(mNewLine);
            	}
            }

            if(!isNull(stru.dtstart)) {
            	if(stru.isAllday) {
            		String start = convertLongToRFC2445DateTime(stru.dtstart);
            		strbuf.append("DTSTART;TZID=UTC:").append(start).append(mNewLine);
            	} else {
            		strbuf.append("DTSTART:").append(stru.dtstart).append(mNewLine);
            	}
            } 

            if(!isNull(stru.alarm)) {
            	strbuf.append("DALARM:").append(stru.alarm).append(mNewLine);
            	strbuf.append("AALARM:").append(stru.alarm).append(mNewLine);
            }            	
            
            if(!isNull(stru.duration))
                strbuf.append("DURATION:").append(stru.duration).append(mNewLine);

            if(!isNull(stru.event_location)) {
            	strbuf.append(foldingString("LOCATION;ENCODING=QUOTED-PRINTABLE;CHARSET="+charSet+":" + stru.event_location) + mNewLine);
            }

            if(!isNull(stru.last_date))
                strbuf.append("COMPLETED:").append(stru.last_date).append(mNewLine);

            if(!isNull(stru.rrule))
                strbuf.append("RRULE:").append(stru.rrule).append(mNewLine);

            if(!isNull(stru.rdate))
                strbuf.append("RDATE:").append(stru.rdate).append(mNewLine);

            if(!isNull(stru.exrule))
                strbuf.append("EXRULE:").append(stru.exrule).append(mNewLine);

            if(!isNull(stru.exdate)) {
            	stru.exdate = stru.exdate.replace(",", ";");
                strbuf.append("EXDATE:").append(stru.exdate).append(mNewLine);
            }
            
            if(!isNull(stru.title)) {            	
            	strbuf.append(foldingString("SUMMARY;ENCODING=QUOTED-PRINTABLE;CHARSET="+charSet+":" + stru.title) + mNewLine);
            }

            if(!isNull(stru.status)){
                String stat = "TENTATIVE";
                switch (Integer.parseInt(stru.status)){
                case 0://Calendar.Calendars.STATUS_TENTATIVE
                    stat = "TENTATIVE";
                    break;
                case 1://Calendar.Calendars.STATUS_CONFIRMED
                    stat = "CONFIRMED";
                    break;
                case 2://Calendar.Calendars.STATUS_CANCELED
                    stat = "CANCELLED";
                    break;
                }
                strbuf.append("STATUS:").append(stat).append(mNewLine);
            }
            
            if (stru.has_alarm && stru.reminderList != null
    				&& stru.reminderList.size() > 0) {

    			for (int i = 0; i < stru.reminderList.size(); i++) {
    				EventStruct.reminderInfo r = stru.reminderList.get(i);
    				if (mVersion.equals(VERSION_VCALENDAR10)) {
    					String prefix = "";
    					switch (r.method) {
    						case 0:
    							prefix = "DALARM";
    							break;
    						case 1:
    							prefix = "AALARM";
    							break;
    						case 2:
    							prefix = "MALARM";
    							break;
    						case 3:
    						default:
    							prefix = "DALARM";
    							break;
    						}
    						//strbuf.append(prefix).append(":ReminderMSG").append(mNewLine);
    				} else {
    					// Android only support "DISPLAY" alarm at this moment (20080415).
    					// The displayed message is not configurable yet.
    					strbuf.append("BEGIN:VALARM").append(mNewLine).append(
    							"ACTION:DISPLAY").append(mNewLine).append(
    							"DESCRIPTION:ReminderMSG").append(mNewLine).append(
    							"TRIGGER:PT").append(r.minutes).append("M").append(mNewLine).append(
    							"END:VALARM").append(mNewLine);
    				}
    			}
    		}
            
            strbuf.append("END:VEVENT").append(mNewLine);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return strbuf.toString();
    }

    /** Alter str to folding supported format. */
    private String foldingString(String str) {
        return str;
    /*
		int start = 0;
		int end = 0;
		int length = str.length();
		String result = "";
		
		if(str == null)
			return null;
		
		if(str.length() <= foldingLength)
			return str;
		
		do {
			start = end;
			end = end + foldingLength;
			if(end >= length) {
				result = result + str.substring(start, length);
			} else {
				result = result + str.substring(start, end) + "=" + mNewLine;
			}
		} while(end < length);
		
		return result;
		*/
    }

    /** is null */
    private boolean isNull(String str){
        if(str == null || str.trim().equals("") || str.equals("null"))
            return true;
        return false;
    }
    
    private String convertLongToRFC2445DateTime(String time) {
    	Time t = new Time();
    	t.parse(time);
    	
    	Time curr = new Time();    	
    	curr.setToNow();
    	Log.d("ddd", "-->"+(curr.gmtoff*1000)+" "+t.toMillis(false));
    	
    	t.set(t.toMillis(false)+curr.gmtoff*1000);
        return t.format("%Y%m%dT%H%M00Z");
   }
}
