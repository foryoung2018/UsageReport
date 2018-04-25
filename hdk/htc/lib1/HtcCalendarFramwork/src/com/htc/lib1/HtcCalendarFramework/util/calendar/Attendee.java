package com.htc.lib1.HtcCalendarFramework.util.calendar;

import android.provider.CalendarContract.Attendees;

/**
 * Attendee
 * {@exthide}
 */
public class Attendee {
	
    private String mName = null;
    private String mEmail = null;
    private int    mStatus = 0; 

    Attendee(String name, String email, int status) {
        mName = name;
        mEmail = email;
        mStatus = status;
    }
   
   /**
     * To get the Name
     * @return the name of string
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public String getName() {
    	return mName;
    }
    
    /**
      * get the attendees' email
      * @return the attendees' email
      */
    public String getEmail() {
    	return mEmail;
    }
    

    /**
     * To get the status
     * @return android.provider.Calendar.Attendees.ATTENDEE_STATUS_NONE
     *         android.provider.Calendar.Attendees.ATTENDEE_STATUS_ACCEPTED
     *         android.provider.Calendar.Attendees.ATTENDEE_STATUS_DECLINED
     *         android.provider.Calendar.Attendees.ATTENDEE_STATUS_INVITED
     *         android.provider.Calendar.Attendees.ATTENDEE_STATUS_TENTATIVE         
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public int getStatus() {
    	return mStatus;
    }

}
