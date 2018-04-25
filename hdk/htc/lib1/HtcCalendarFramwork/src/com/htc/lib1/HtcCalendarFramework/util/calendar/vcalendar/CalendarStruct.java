/*
* Copyright (C) 2007 Google Inc.
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
package com.htc.lib1.HtcCalendarFramework.util.calendar.vcalendar;

import java.util.List;
import java.util.ArrayList;

/**
 * Same comment as ContactStruct.
 * {@exthide}
 */
public class CalendarStruct {

    /**
      * define the event struct
      */
    public static class EventStruct {

/**
  * Declare the description string
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
        public String description;

/**
  * Declare the dtend string
  *  Hide automatically by SDK TEAM [U12000] 
  *  @hide
  */
        public String dtend;

/**
  * Declare the dtstart string
  *  Hide automatically by SDK TEAM [U12000] 
  *  @hide
  */
        public String dtstart;

/**
  * Declare the duration string
  *  Hide automatically by SDK TEAM [U12000] 
  *  @hide
  */
        public String duration;

/**
  * Declare the has alarm boolean
  *  Hide automatically by SDK TEAM [U12000] 
  *  @hide
  */
        public boolean has_alarm;

/**
  * Declare the is all day boolean
  *  Hide automatically by SDK TEAM [U12000] 
  *  @hide
  */
        public boolean isAllday;

/**
  * Declare the last date string
  *  Hide automatically by SDK TEAM [U12000] 
  *  @hide
  */
        public String last_date;

/**
  * Declare the rrule string
  *  Hide automatically by SDK TEAM [U12000] 
  *  @hide
  */
        public String rrule;

/**
  * Declare the rdate string
  *  Hide automatically by SDK TEAM [U12000] 
  *  @hide
  */
        public String rdate;

/**
  * Declare the exrule string
  *  Hide automatically by SDK TEAM [U12000] 
  *  @hide
  */
        public String exrule;

/**
  * Declare the exdate string
  *  Hide automatically by SDK TEAM [U12000] 
  *  @hide
  */
        public String exdate;

/**
  * Declare the status string
  *  Hide automatically by SDK TEAM [U12000] 
  *  @hide
  */
        public String status;

/**
  * Declare the title string
  *  Hide automatically by SDK TEAM [U12000] 
  *  @hide
  */
        public String title;

/**
  * Declare the event location string
  *  Hide automatically by SDK TEAM [U12000] 
  *  @hide
  */
        public String event_location;

/**
  * Declare the uid string
  *  Hide automatically by SDK TEAM [U12000] 
  *  @hide
  */
        public String uid;


        /* HMS calendar, drop the database column
        public String last_update_time;
        * /

/**
  * Declare the alarm string
  *  Hide automatically by SDK TEAM [U12000] 
  *  @hide
  */
        public String alarm;
        
        
        /**
  * Declare the dtstamp string
  *  Hide automatically by SDK TEAM [U12000] 
  *  @hide
  */
        public String dtstamp;

/**
  * The reminder infomation
  */
        public static class reminderInfo {

/**
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
            public final long minutes;            

/**
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
            public final int method;

/**
  * The reminderInfo constructor
  * @param minutes how many minutes
  * @param method the method
  *  Hide automatically by SDK TEAM [U12000] 
  *  @hide
  */
            public reminderInfo(long minutes, int method) {
                this.minutes = minutes;
                this.method = method;
             }
        }
        

/**
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
        public ArrayList<reminderInfo> reminderList;

/**
  * To set the reminders
  * @param reminders the reminders to be set
  *  Hide Automatically by SDK Team [U12000]
  *  @hide
  */
        public void setReminders(ArrayList<reminderInfo> reminders) {
            if (reminderList == null)
                reminderList = new ArrayList<reminderInfo>();
            for (int i = 0; i < reminders.size(); i++) {
            	reminderList.add(reminders.get(i));
            }
        }
    }


/**
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
    public String timezone;

/**
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
    public String endTimezone;

/**
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
    public List<EventStruct> eventList;

/**
  * To add the event list
  * @param stru the event struct
  *  Hide automatically by SDK TEAM [U12000] 
  *  @hide
  */
    public void addEventList(EventStruct stru){
        if(eventList == null)
            eventList = new ArrayList<EventStruct>();
        eventList.add(stru);
    }
}
