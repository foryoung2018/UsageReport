package com.htc.lib1.HtcCalendarFramework.util.calendar.vcalendar;

/**
 * Hash Item interface Declare methods used by the HashManager
 * 
 * {@exthide}
 */
public interface HashItem {

/**
  * To get the hash item id
  * @return the hash item id
  *  Hide Automatically by SDK Team [U12000]
  *  @hide
  */
    public abstract int getId();

/**
  * To get the hash
  * @return the hash number
  *  Hide Automatically by SDK Team [U12000]
  *  @hide
  */
    public abstract long getHash();

/**
  * To get the content
  * @return the content string
  *  Hide Automatically by SDK Team [U12000]
  *  @hide
  */
    public abstract String getContent();
}
