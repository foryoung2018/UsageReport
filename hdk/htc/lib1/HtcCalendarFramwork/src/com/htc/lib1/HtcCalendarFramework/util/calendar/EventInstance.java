package com.htc.lib1.HtcCalendarFramework.util.calendar;

import android.os.Parcel;
import android.os.Parcelable;

/**
  * The Event Instance class
  * {@exthide}
  */
public class EventInstance implements Parcelable {
    /* NOTE: any variables added here, remember to add it to writeToParcel(Parcel dest, int flags)
             and readFromParcel(Parcel in) since this class is Parcelable. */
    private long id;
    private String title;
    private String location;
    private String description;
    private String htmlDescription;
    private long begin;
    private long end;
    private int startTime;
    private int endTime;
    private int startDay;
    private int endDay;
    private int color;
    private int descMineType;
    private boolean isAllday = false;
    
    /**
      * The EventInstance constructor
      * @param id long, the instance id
      * @param begin long, the instance begin timestamp
      * @param end long, the instance end timestamp
      * @deprecated [Module internal use]
      */
    /**@hide*/ 
    public EventInstance(long id, long begin, long end) {
        this.id = id;
        this.title = null;
        this.begin = begin;
        this.end = end;
    }
    
    /**
      * The EventInstance constructor
      * @param id long, the instance id
      * @param title String, the instance title
      * @param begin long, the instance begin timestamp
      * @param end long, the instance end timestamp
      * @deprecated [Module internal use]
      */
    /**@hide*/ 
    public EventInstance(long id, String title, long begin, long end) {
        this.id = id;
        this.title = title;
        this.begin = begin;
        this.end = end;
    }
    
    /**
      * The EventInstance constructor
      * @param id long, the instance id
      * @param title String, the instance title
      * @param location String, the instance location
      * @param description String, the instance description
      * @param begin long, the instance begin timestamp
      * @param end long, the instance end timestamp
      * @deprecated [Not use any longer]
      */
   /**@hide*/ 
    public EventInstance(long id, String title, String location, String description, long begin, long end) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.description = description;
        this.begin = begin;
        this.end = end;
    }
    
    /**
     * The EventInstance constructor
     * @param id long, the instance id
     * @param title String, the instance title
     * @param location String, the instance location
     * @param description String, the instance description
     * @param begin long, the instance begin timestamp
     * @param end long, the instance end timestamp
     * @param descMineType int, the type of description
     * @param htmlDescription String, html format of description
     * @deprecated [Module internal use]
     */
   /**@hide*/ 
   public EventInstance(long id, String title, String location, String description, long begin, long end, int descMineType, String htmlDescription) {
       this.id = id;
       this.title = title;
       this.location = location;
       this.description = description;
       this.begin = begin;
       this.end = end;
       this.descMineType = descMineType;
       this.htmlDescription = htmlDescription;
   }
   
    
    /**
     * The EventInstance constructor
     * @param id long, the instance id
     * @param title String, the instance title
     * @param location String, the instance location
     * @param description String, the instance description
     * @param begin long, the instance begin timestamp
     * @param end long, the instance end timestamp
     * @param startTime int, the instance start time in minutes
     * @param endTime int, the instance end time in minutes
     * @param startDay int, the instance start Julian day
     * @param endDay int, the instance end Julian day
     * @param color int, the instance color
     * @param isAllday boolean, the instance allday flag
     * @deprecated [Not use any longer]
     */
    public EventInstance(long id, String title, String location, String description, long begin, 
            long end, int startTime, int endTime, int startDay, int endDay, int color, 
            boolean isAllday) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.description = description;
        this.begin = begin;
        this.end = end;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDay = startDay;
        this.endDay = endDay;
        this.color = color;
        this.isAllday = isAllday;
    }
    
    /**
      *  The EventInstance constructor
      * @param in The Parcel data
      * @deprecated [Module internal use]
      */
    /**@hide*/ 
    public EventInstance(Parcel in) {
        readFromParcel(in);
    }

    /**
      * Get the id
      * @return the id value
      */
    public long getId() { 
        return id;
    }
    
    /**
      * Get the title
      * @return the title value
      */
    public String getTitle() {
        return title;
    }

    /**
      * Get the location
      * @return the location value
      */    
    public String getLocation() {
        return location;
    }
    
    /**
      * Get the description
      * @return the description value
      */
    public String getDescription() {
        return description;
    }

    /**
      * Get the begin
      *@return the begin value
      */
    public long getBegin() {
        return begin;
    }

    /**
      * Get the end
      * @return the end value
      */
    public long getEnd() {
        return end;
    }
    
    /**
     * Get start minutes.
     * @return start minutes.
     * @deprecated [Module internal use]
     */
    /**@hide*/ 
    public int getStartTime() {
        return startTime;
    }
    
    /**
     * Get end minutes.
     * @return end minutes.
     * @deprecated [Module internal use]
     */
    /**@hide*/ 
    public int getEndTime() {
        return endTime;
    }
    
    /**
     * Get start Julian day.
     * @return start Julian day.
     */
    public int getStartDay() {
        return startDay;
    }
    
    /**
     * Get end Julian day.
     * @return end Julian day.
     */
    public int getEndDay() {
        return endDay;
    }
    
    /**
     * Get color value.
     * @return color value.
     */
    public int getColor() {
        return color;
    }
    
    /**
     * Is allday event?
     * @return true if it is allday event; otherwise false.
     */
    public boolean isAllday() {
        return isAllday;
    }
    
    /**
     * Get description type?
     * @return 1, if it's html format; 0, is plaintext
     */
    public int getDescMineType() {
        return descMineType;
    }
    
    /**
     * Get html format of description ?
     * @return html of description 
     */
    public String getHtmlDescription() {
        return htmlDescription;
    }

    /**
     * Get describeContents 
     * @return describeContents 
     * @deprecated [Not use any longer]
     */
   /**@hide*/ 
    public int describeContents() {
        return 0;
    }
    
    /**
     * Write event's data to parcel      
     * @param dest Parcel, the destination parcel.   
     * @param flags int, flags for the parcel.
     * @deprecated [Not use any longer]
     */    
  /**@hide*/ 
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(location);
        dest.writeString(description);
        dest.writeString(htmlDescription);
        dest.writeLong(begin);
        dest.writeLong(end);
        dest.writeInt(startTime);
        dest.writeInt(endTime);
        dest.writeInt(startDay);
        dest.writeInt(endDay);
        dest.writeInt(color);
        dest.writeInt(descMineType);
        dest.writeByte(isAllday ? (byte) 1 : (byte) 0);
    }
    
    /**
     * Read event's data from parcel      
     * @param in Parcel, the parcel to read.
     */
    private void readFromParcel(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.location = in.readString();
        this.description = in.readString();
        this.htmlDescription = in.readString();
        this.begin = in.readLong();
        this.end = in.readLong();
        this.startTime = in.readInt();
        this.endTime = in.readInt();
        this.startDay = in.readInt();
        this.endDay = in.readInt();
        this.color = in.readInt();
        this.descMineType = in.readInt();
        this.isAllday = in.readByte() == 1;
    }
    
    /**
     * The EventInstance Parcelable Creator
     */
    public static final Parcelable.Creator<EventInstance> CREATOR = 
            new Parcelable.Creator<EventInstance>() {
        /**
         * Create an EventInstace from parcel.
         * @param in Parcel, create an EventInsatce from this parcel.
         * @return the event instance.
         */
        public EventInstance createFromParcel(Parcel in) {
            return new EventInstance(in);
        }

        /**
         * new an EventInstance array.
         * @param size int, the size of the array.
         * @return the event instance array.
         */    
        public EventInstance[] newArray(int size) {
            return new EventInstance[size];
        }
    };
}
