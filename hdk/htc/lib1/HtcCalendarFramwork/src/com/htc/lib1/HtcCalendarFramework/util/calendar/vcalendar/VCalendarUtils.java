package com.htc.lib1.HtcCalendarFramework.util.calendar.vcalendar;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;

import android.net.Uri;

import android.content.ContentValues;
import android.content.Context;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.database.Cursor;
import android.text.format.Time;
import com.htc.lib1.HtcCalendarFramework.calendarcommon2.ICalendar;
import com.htc.lib1.HtcCalendarFramework.calendarcommon2.Duration;
import com.htc.lib1.HtcCalendarFramework.calendarcommon2.ICalendar.Property;

import android.text.TextUtils;
import android.util.Log;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.QuotedPrintableCodec;
import org.apache.commons.codec.binary.Base64;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.zip.CRC32;
import java.util.List;
import com.htc.lib1.HtcCalendarFramework.provider.HtcExCalendar.ExEvents;
//import com.htc.util.mail.MailUtils;gerald todo
import java.util.Locale;
import com.htc.lib1.HtcCalendarFramework.util.calendar.holidays.HolidayUtils;
import com.htc.lib1.HtcCalendarFramework.util.calendar.tools.UriTools;
import com.htc.lib1.HtcCalendarFramework.util.calendar.VersionCheckUtils;

/**
 * A Event item
 * {@exthide}
 */
public class VCalendarUtils implements HashItem {

	private static final String TAG = "VCalendarUtils";
	private static final String VBEGIN = "BEGIN:VCALENDAR";
	private static final String VEND = "END:VCALENDAR";

	/**
     * Event fields declaration
     */

	// Event UID in local database
    private String _id;
    // Event UID from external source

/**
 * The uid
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
    public String uid;

/**
 * The categories
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
    public String categories;

/**
 * The type
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
    public String type;

/**
 * The strDTEnd
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
    public String strDTEnd;

/**
 * The strDTStart
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
    public String strDTStart;

/**
 * The strAlarm
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
    public String strAlarm;

/**
 * The dtEnd
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
    public Time dtEnd;

/**
 * The dtStart
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
    public Time dtStart;
    
    
/**
 * The dtStamp
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
    public Time dtStamp;

/**
 * The alarm
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
    public long alarm;

/**
 * The timezone
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
    public String timezone;

/**
 * The endTimezone
 *  Hide automatically by SDK TEAM [U12000]
 *  @hide
 */
    public String endTimezone;

/**
 * The description
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
    public String description;

/**
 * The summary
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
    public String summary;

/**
 * The location
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
    public String location;

/**
 * The duration
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
    public String duration;

/**
 * The rRule
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
    public String rRule;

/**
 * Thr rDate
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
    public String rDate;

/**
 * The exRule
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
    public String exRule;

/**
 * The exDate
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
    public String exDate;

/**
 * The isAllDay
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
    public boolean isAllDay;

/**
 * The hasAlarm
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
    public boolean hasAlarm;

/**
 * The status
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
    public String status;
    
/**
 * The priority
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public String priority;

/**
 * The WKST
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
    public final String WKST = "SU";
    /*private final String FREQ = "FREQ";
    private final String UNTIL = "UNTIL";
    private final String COUNT = "COUNT";
    private final String INTERVAL = "INTERVAL";
    private final String BYDAY = "BYDAY";
    private final String BYMONTHDAY = "BYMONTHDAY";
    private final String BYMONTH = "BYMONTH";
    private final String BYSETPOS = "BYSETPOS";*/

/**
  * The reminders where
  *  Hide Automatically by SDK Team [U12000]
  *  @hide
  */
    public static final String REMINDERS_WHERE = Reminders.EVENT_ID + "=%d AND (" +
    	Reminders.METHOD + "=" + Reminders.METHOD_ALERT + " OR " + Reminders.METHOD + "=" +
    	Reminders.METHOD_DEFAULT + ")";
    	
/**
 * The reminders projection
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public static final String[] REMINDERS_PROJECTION = new String[] {
        Reminders._ID,      // 0
        Reminders.MINUTES,  // 1
        Reminders.METHOD	// 2
    };

    private final static String default_charset = "UTF-8";

    private final static String RULE_SEPARATOR = "\n";

    private final boolean DEBUG = HtcWrapHtcDebugFlag.Htc_DEBUG_flag;

    // Up-to-dated vCalendar data
    // Dynamically changed in getHash()
    private String vCalendar;


    /*HMS calendar, drop the database column
    public String last_update_time;
    */
    final static HashMap<String, Integer> weekDayAry = new HashMap<String, Integer>();
    static {
    	weekDayAry.put("SU", Integer.valueOf(0));
    	weekDayAry.put("MO", Integer.valueOf(1));
    	weekDayAry.put("TU", Integer.valueOf(2));
    	weekDayAry.put("WE", Integer.valueOf(3));
    	weekDayAry.put("TH", Integer.valueOf(4));
    	weekDayAry.put("FR", Integer.valueOf(5));
    	weekDayAry.put("SA", Integer.valueOf(6));
    };

    final static String [] weekDays = {"SU", "MO", "TU", "WE", "TH", "FR", "SA"};

    private ArrayList<ContentValues> mMultiVCal_Event_ContentValue = new ArrayList<ContentValues>();
    private ArrayList<ContentValues> mMultiVCal_ToDo_ContentValue = new ArrayList<ContentValues>();
    private ArrayList<ContentValues> mMultiVCal_EventAlarm_ContentValue = new ArrayList<ContentValues>();
    private ArrayList<ContentValues> mMultiVCal_ToDoAlarm_ContentValue = new ArrayList<ContentValues>();
    
    boolean m_bHas_dtEnd = false;

    /**
      * The VCalendarUtils constructor
      * used in Calendar IcsImportActivity
      */
    public VCalendarUtils() {
    	//...
    	// it need to call parseVCalendar after create VCalendarUtils object.
    }

    /**
      * To parse the VCalendar
      * @param vcalendar the vcalendar string
      * @return boolean if init vcalendar suceess or not
      */
    public boolean parseVCalendar(String vcalendar) {
    	return init(vcalendar);
    }

    /**
      * for get an Event
      * @param eventCur the event cursor
      * @param c the Context
      * @deprecated [Module internal use]
      */
    /**@hide*/ 
    public VCalendarUtils(Cursor eventCur, Context c) {
        init(eventCur, c);
    }
    
    /**
      * for get an Instance
      * @param c the Context
      * @param uniEventUri the uni event Uri
      * @deprecated [Module internal use]
      */
    /**@hide*/ 
    public VCalendarUtils(Context c, Uri uniEventUri) {
        
    }

/**
 * Set the event identifier
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setId(String id) {
        _id = id;
    }

/**
 * Get the event identifier
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public int getId() {
        return Integer.parseInt(_id);
    }

/**
  * To determine if it has an alarm
  * @return boolean if it has an alarm
  *  Hide Automatically by SDK Team [U12000]
  *  @hide
  */
    public boolean gethasAlarm() {
    	return hasAlarm;
    }

    private boolean isNullContentValues(ContentValues cv) {
        ContentValues nullCv = new ContentValues();
        nullCv.clear();
        
        if(cv == null) {
            return true;
        } else if(nullCv.equals(cv)) {
            return true;
        }
                
        return false;
    }
    
    /**
     * Parse the vCalendar string into the events fields
     */
    private boolean init(final String vCalRawData) {
    	String rawData = vCalRawData;
    	int nestCount = 0;
    	ArrayList<String> vCalList = new ArrayList<String>();
    	while (true) {
    		int nBegin = rawData.indexOf(VBEGIN);
    		int nEnd = rawData.indexOf(VEND);
    		if(-1 == nBegin || -1 == nEnd) {
    			break;
    		}
    		String body = rawData.substring(0, nEnd+VEND.length()).trim();
    		rawData = rawData.substring(nEnd+VEND.length());
    		// if(DEBUG) {
    		//	Log.d(TAG, "<Body>");
    		//	Log.i(TAG, body);
        	//	Log.d(TAG, "</Body>");
    		// }

		   //M7C_DTU_JB_50_S ITS#1634, Fix other deivce can not import to our device, 
           //Root cause : dulipcate "=", so replace it.
		   body = body.replaceAll("=\r\n=", "\r\n=");
    		vCalList.add(body);
    		nestCount++;
    	}

    	for(String s : vCalList) {
            boolean oneResult = initLocked(s);
            Log.v(TAG, "IMPORT-->" + oneResult);                 
    	}
    	return true;
    }

    // parse the one section
    private boolean initLocked(final String vCalRawData) {
    	int nBegin = vCalRawData.indexOf(VBEGIN);
    	int nEnd = vCalRawData.indexOf(VEND);
    	if (-1 == nBegin || -1 == nEnd)
    		return false;

        this.vCalendar = vCalRawData.substring(nBegin, nEnd + VEND.length());;

		ICalendar.Component cal;

		try {
			cal = ICalendar.parseCalendar(vCalendar);
		} catch (ICalendar.FormatException fe) {
			Log.e(TAG,"There is ICalendar.FormatException",fe);
			return false;
		}

		if (cal.getComponents() == null) {
			return false;
		}

		return setEventValues(cal);
    }

    private static String extractValue(ICalendar.Component component,
			String propertyName) {
		ICalendar.Property property = component.getFirstProperty(propertyName);
		if (property != null) {
			return property.getValue();
		}
		return null;
	}

    private static String flattenProperties(ICalendar.Component component,
			String name) {
		List<ICalendar.Property> properties = component.getProperties(name);
		if (properties == null || properties.isEmpty()) {
			return null;
		}

		if (properties.size() == 1) {
			return properties.get(0).getValue();
		}
		
		final List<Property> list = component.getProperties(name);
		
		if(list!=null) {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			
			for (ICalendar.Property property : list) {
				if (first) {
					first = false;
				} else {
					// TODO: use commas. our RECUR parsing should handle that
					// anyway.
					sb.append(RULE_SEPARATOR);
				}
				sb.append(property.getValue());
			}
			return sb.toString();	
		} else {
			Log.d(TAG,"flattenProperties(), property list is null!");
			return "";
		}

	}

	private static String extractDates(ICalendar.Property recurrence) {
		if (recurrence == null) {
			return null;
		}
		ICalendar.Parameter tzidParam = recurrence.getFirstParameter("TZID");
		if (tzidParam != null) {
			return tzidParam.value + ";" + recurrence.getValue();
		}
		return recurrence.getValue();
	}

	private static String extractString(ICalendar.Component component,
			String propertyName) {
		String val = extractString_Impl(component, propertyName);

		if(val == null)
			return null;

		val = val.replace("\r\n", "\n");

		if(isShift_JIS()) {
			val = val.replace("\\\\", "\\");
			val = val.replace("\\;", ";");
		}
		return val;
	}

	private static String extractString_Impl(ICalendar.Component component,
			String propertyName) {
		ICalendar.Property property = component.getFirstProperty(propertyName);
		if (property != null) {

			ICalendar.Parameter enc = property.getFirstParameter("ENCODING");
			ICalendar.Parameter charSet = property.getFirstParameter("CHARSET");

			//if ((enc == null) || (charSet == null))
			if (enc == null)
				return property.getValue();

			if (enc.value.equalsIgnoreCase("QUOTED-PRINTABLE")) {
				QuotedPrintableCodec qpdec = new QuotedPrintableCodec();
				try {
					if (charSet == null) {
						return qpdec.decode(property.getValue(), getDefaultCharSet());
					}else {
						return qpdec.decode(property.getValue(), charSet.value);
					}
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
			else if (enc.value.equalsIgnoreCase("BASE64")) {
				return new String(Base64.decodeBase64(property.getValue().getBytes()));
			}
		}

		return null;
	}

	private static long extractDurationMinutes(String t) {
		int pos1 = 0, pos2 = 0;
		boolean negative = false;
		int day = 0, hour = 0, minute = 0, second = 0, week = 0;
		while (true) {
			// Check +/- sign.
			if (t.indexOf('-') != -1)
				negative = true;
			pos1 = t.indexOf('P');
			if (pos1 == -1) {
				// Invalid, 'P' is required.
				break;
			}
			++pos1;
			// Extract dur-date.
			pos2 = t.indexOf('D', pos1);
			if (pos2 != -1) {
				day = Integer.parseInt(t.substring(pos1, pos2));
			}
			// Extract dur-time.
			pos2 = t.indexOf('T');
			if (pos2 != -1) {
				pos1 = pos2 + 1;
				pos2 = t.indexOf('H', pos1);
				if (pos2 != -1) {
					hour = Integer.parseInt(t.substring(pos1, pos2));
					pos1 = pos2 + 1;
				}
				pos2 = t.indexOf('M', pos1);
				if (pos2 != -1) {
					minute = Integer.parseInt(t.substring(pos1, pos2));
					pos1 = pos2 + 1;
				}
				pos2 = t.indexOf('S', pos1);
				if (pos2 != -1) {
					second = Integer.parseInt(t.substring(pos1, pos2));
					pos1 = pos2 + 1;
				}
			}
			// Extract dur-week.
			pos2 = t.indexOf('W');
			if (pos2 != -1) {
				week = Integer.parseInt(t.substring(pos1, pos2));
				day = week * 7;
			}
			break;
		}

		return minute + hour * 60 + day * 24 * 60;
	}

	private static long extractDurationMinutes(Time t) {
		return t.toMillis(false) / 1000 / 60;
	}
	
	private boolean setDtStart(String val) {
        strDTStart = val;

        // Log.d(TAG, "parse strDTStart: "+strDTStart);
        if( checkTimeObj(strDTStart)) {
            dtStart.parse(strDTStart);
        } else {
            Log.d(TAG, "strDTStart: "+strDTStart+" error");
            return false;
        }
        
        return true;
	}

    private boolean setEventValues(ICalendar.Component cal) {
		String val;

    	boolean isEvent = false; // else isTodo
		try {
			for (ICalendar.Component component : cal.getComponents()) {
			    clear();			    
			    if ("VEVENT".equals(component.getName())||"VTODO".equals(component.getName())) {
			        
			        isEvent = "VEVENT".equals(component.getName());
                    
                    String strdtstamp = extractValue(component, "DTSTAMP");
                    dtStamp = new Time();
                     
			        if (!TextUtils.isEmpty(strdtstamp)) {
                        if( checkTimeObj(strdtstamp)) {
                           
                            dtStamp.parse(strdtstamp);
                        } else {
                            Log.e(TAG, "strdtstamp: "+strdtstamp+" error");
                           
                        }
                    }
                    
                    else
                        Log.d(TAG, " DTSTAMP is empty. ");

			        uid = extractString(component, "UID");
			        val = extractValue(component, "DTSTART");
			        
			        dtStart = new Time();
			        dtEnd = new Time();
			        
			        //Log.v(TAG, "DTSTART: "+val);
			        if (!TextUtils.isEmpty(val)) {
			            
			            if (!setDtStart(val)) continue;
			            
			        } else {
			            // only for VTODO
			            if (!isEvent) {
			                val = extractValue(component, "DUE");
			                if (TextUtils.isEmpty(val)) continue;
			                
			                if (!setDtStart(val)) continue; // set due as dtstart
			                
			            }
			        }

			        val = extractValue(component, "DTEND");
			        //Log.v(TAG, "DTEND: "+val);
                    m_bHas_dtEnd = false;
                    
			        if (!TextUtils.isEmpty(val)) {
                        
                        m_bHas_dtEnd = true;
                        
			            strDTEnd = val;
			            //Log.d(TAG, "parse strDTEnd: "+strDTEnd);
			            if( checkTimeObj(strDTEnd)) {
			                dtEnd.parse(strDTEnd);
			            } else {
			                Log.d(TAG, "strDTEnd: "+strDTEnd+" error");
							
			                continue;
							
			            }
			        } else{
			            // only for VTODO
			            if (!isEvent) {
			                dtEnd.set(dtStart.toMillis(false));
			            }
                        else{            
                            //# duration --------------------------------------------
                            val = extractValue(component, "DURATION");
                            if (!TextUtils.isEmpty(val)){
                                duration = val;
//                                try {
//                                    duration = val;
//                                    Duration d = new Duration();
//                                    d.parse(duration);
//                                    dtEnd.set(dtStart.toMillis(false) + d.getMillis() );
//                                }   catch(Exception e) {
//                                    dtEnd.set(dtStart.toMillis(false));
//                                    e.printStackTrace();
//                                }
                            } else{
//                                dtEnd.set(dtStart.toMillis(false));
                                Log.e(TAG, "DTEND and DURATION is null!!!");
                            }
                        }
			        }
                    
                    if((dtEnd.toMillis(false) - dtStart.toMillis(false)) == 86400000) {
			            isAllDay = true;
			        } else if((dtEnd.toMillis(false) == dtStart.toMillis(false))
						   && dtStart.hour == 0
						   && dtStart.minute == 0
						   && dtStart.second == 0
						   && !isShift_JIS()) {
			            isAllDay = true;
					} else if((dtEnd.toMillis(false) == dtStart.toMillis(false))
							   && !isShift_JIS()&& isEvent) {
			            isAllDay = true;
			        } else {
			            isAllDay = false;
			        }
                    
			        val = extractValue(component, "AALARM");
			        if (!TextUtils.isEmpty(val)) {
			            val = val.replaceAll(";", "");
			            strAlarm = val;
			            Time alarmTime = new Time();

			            boolean validAlarmTime = checkTimeObj(strAlarm);
			            debug("validAlarmTime:"+validAlarmTime);
			            alarmTime.parse(strAlarm);
                        
                        if(isAllDay){
                            Time curr = new Time();    	
                            curr.setToNow();
                            Log.d(TAG, " current gmtoff-->"+(curr.gmtoff*1000));
                            
                            //t.set(t.toMillis(false)+curr.gmtoff*1000);                            
                            alarm = (dtStart.toMillis(false) - (curr.gmtoff*1000) - alarmTime.toMillis(false))/60000;
                        }

                        else
                            alarm = (dtStart.toMillis(false) - alarmTime.toMillis(false))/60000;

			            if(alarm >= 0 && validAlarmTime) {
			                hasAlarm = true;
			            } else {
			                hasAlarm = false;
			            }
			        } else {
			            hasAlarm = false;
			        }
			        Log.v(TAG,"hasAlarm is :"+hasAlarm);			        

			        
			        //# dtStart, dtEnd, timezone --------------------------------------------.
			        if (isAllDay) {
			            // note ......
			            if (dtStart.hour > 11) {
			                dtStart.set(dtStart.toMillis(false) + (24- dtStart.hour) * 60 * 60 * 1000);
			                dtEnd.set(dtEnd.toMillis(false) + (24- dtEnd.hour)	* 60 * 60 * 1000);
			            } else {
			                dtStart.set(dtStart.toMillis(false)-dtStart.hour * 60 * 60 * 1000);
			                dtEnd.set(dtEnd.toMillis(false)- dtEnd.hour * 60 * 60 * 1000);
			            }
			            dtStart.timezone = Time.TIMEZONE_UTC;
			            dtEnd.timezone = Time.TIMEZONE_UTC;
			            timezone = "UTC";
			        } else {
			            // timezone = Time.getCurrentTimezone(); Delete by Henry and add the followings for eventEndTimezone
			            ICalendar.Property dtStartProperty = component.getFirstProperty("DTSTART");
			            ICalendar.Property dtEndProperty = component.getFirstProperty("DTEND");
			            			            
			            ICalendar.Parameter dtStartTimeZoneIdParameter = null;
			            
			            if(dtStartProperty != null) {
			            	dtStartTimeZoneIdParameter = dtStartProperty.getFirstParameter("TZID");
			            } else {
			            	Log.d(TAG, "dtStartProperty is null");
			            }			            			            
			            
			            if(dtStartTimeZoneIdParameter != null) {
			            	final String tempDtStart = extractValue(component, "DTSTART");			
			            				            	
							if (tempDtStart != null) {
								if (tempDtStart.indexOf("Z") > 0) {
									dtStart.switchTimezone(dtStartTimeZoneIdParameter.value);
								} else {
									dtStart.timezone = dtStartTimeZoneIdParameter.value;
								}
								timezone = dtStart.timezone;
							} else {
								Log.d(TAG,"tmpDtStart is null!");
							}
    			              
			            } else {
			                timezone = Time.getCurrentTimezone();
			            }
			            
			            ICalendar.Parameter dtEndTimeZoneIdParameter = null;
			            
			            if(dtEndProperty != null) {
			            	dtEndTimeZoneIdParameter = dtEndProperty.getFirstParameter("TZID");
			            } else {
			            	Log.d(TAG, "dtEndProperty is null");
			            }	
			            
			            if(dtEndTimeZoneIdParameter != null) {
			            	final String tempDtEnd = extractValue(component, "DTEND");
			            	
							if (tempDtEnd != null) {
								if (tempDtEnd.indexOf("Z") > 0) {
									dtEnd.switchTimezone(dtEndTimeZoneIdParameter.value);
								} else {
									dtEnd.timezone = dtEndTimeZoneIdParameter.value;
								}
								endTimezone = dtEnd.timezone;
							} else {
								Log.d(TAG,"tempDtEnd is null!");
							}
			            } else {
			                endTimezone = Time.getCurrentTimezone();
			            }
			        }

			      //# duration --------------------------------------------
			      // It duplicate judgment in "DTEND"
//			        val = extractValue(component, "DURATION");
//			        if (!TextUtils.isEmpty(val))
//			            duration = val;
//			        else {
//			            long mDur = (dtEnd.toMillis(false) - dtStart.toMillis(false))/1000;
//
//			            duration = "P"+String.valueOf(mDur)+"S";
//
//			        }

			        location = extractString(component, "LOCATION");
			        summary = extractString(component, "SUMMARY");
			        description = extractString(component, "DESCRIPTION");

			        val = extractValue(component, "CATEGORIES");
			        if (!TextUtils.isEmpty(val))
			            categories = val;

			        val = extractValue(component, "CLASS");
			        if (!TextUtils.isEmpty(val))
			            type = val;

			        val = extractValue(component, "STATUS");
			        if (!TextUtils.isEmpty(val))
			            status = val;
			        val = extractValue(component, "PRIORITY");
			        if (!TextUtils.isEmpty(val))
			            priority = getPriorityString(Integer.parseInt(val));

			        rRule = flattenProperties(component, "RRULE");
			        rRule = RRule_VCalToICal(rRule);

			        rDate = extractDates(component.getFirstProperty("RDATE"));
			        exRule = flattenProperties(component, "EXRULE");
			        exDate = extractDates(component.getFirstProperty("EXDATE"));
			        			        
			        ContentValues cv = getEventCV();
			        
			        if (!isNullContentValues(cv)) {
			            if (isEvent) {
			                mMultiVCal_Event_ContentValue.add(cv);
	                        cv = getAlarmCV();
	                        mMultiVCal_EventAlarm_ContentValue.add(cv);
			            } else { // isTodo 
			                mMultiVCal_ToDo_ContentValue.add(cv);
			                cv = getAlarmCV();
			                mMultiVCal_ToDoAlarm_ContentValue.add(cv);			                
			            }
			        }            
			    }
			}
		} catch(Exception e) {
			Log.e(TAG, "Error when set event values.", e);
			return false;
		}
		return true;
    }

    private void clear() {
        uid = null;
        strDTStart = null;
        dtStart = null;
        dtStamp = null;
        strDTEnd = null;
        dtEnd = null;
        strAlarm = null;
        hasAlarm = false;
        isAllDay = false;
        duration = null;
        location = null;
        summary = null;
        description = null;
        rRule = null;
        rDate = null;
        exRule = null;
        exDate = null;    	
    }

/**
    Description: The priority is specified as an integer in the range
    zero to nine. A value of zero (US-ASCII decimal 48) specifies an
    undefined priority. A value of one (US-ASCII decimal 49) is the
    highest priority. A value of two (US-ASCII decimal 50) is the second
    highest priority. Subsequent numbers specify a decreasing ordinal
    priority. A value of nine (US-ASCII decimal 58) is the lowest
    priority.

    A CUA with a three-level priority scheme of "HIGH", "MEDIUM" and
    "LOW" is mapped into this property such that a property value in the
    range of one (US-ASCII decimal 49) to four (US-ASCII decimal 52)
    specifies "HIGH" priority. A value of five (US-ASCII decimal 53) is
    the normal or "MEDIUM" priority. A value in the range of six (US-
    ASCII decimal 54) to nine (US-ASCII decimal 58) is "LOW" priority.
**/    
    private String getPriorityString(int p) {
        String result;
        switch (p) {
            case 1:
            case 2:
            case 3:
            case 4:
                result = "HIGH";
                break;
            case 5:
                result = "MEDIUM";
                break;
            case 6:
            case 7:
            case 8:
            case 9:
                result = "LOW";
                break;
        default:
            result = "undefined priority";
        }
        return result;
        
    }
    
    /**
      * Converts rrule from VCal to ICal
      * @param rrule the string of the rrule
      * @return the ICal rrule string
      */
    String RRule_VCalToICal(String rrule) {
    	if(rrule == null)
    		return null;

    	if (rrule.toUpperCase().indexOf("FREQ=") >= 0) {
    		// already is ical
    		return rrule;
    	}

    	Log.d(TAG, "RRule_VCalToICal: "+rrule);

    	String array[] = new String[]{};
    	StringBuffer result = new StringBuffer("FREQ=");
    	String interval = null;
    	String until = null;
    	String count = null;

    	ArrayList<String> weekday = new ArrayList<String>();
    	ArrayList<String> monthday = new ArrayList<String>();
    	ArrayList<String> month = new ArrayList<String>();
    	ArrayList<String> setpos = new ArrayList<String>();

    	array = rrule.split(" ", -1);

    	if(array[0].indexOf("YM") >= 0) {
    		interval = array[0].substring(2, array[0].length());
    		result.append("YEARLY;").append("INTERVAL=").append(interval).append(";");
    		result.append("WKST=").append(WKST).append(";");

    		for(int i=1;i<array.length;i++) {
    			if(MatchMonth(array[i]))
        			month.add(array[i]);

    			if(array[i].indexOf("#") >= 0) {
    				count = array[i].substring(1, array[i].length());
    				if(!count.equals("0"))
    					result.append("COUNT=").append(count).append(";");
    			}

    			if(array[i].indexOf("T") >= 0 && array[i].indexOf("Z") >= 0) {
    				until = array[i];
    				result.append("UNTIL=").append(until).append(";");
    			}
    		}

    		if(month.size() > 0) {
    			result.append("BYMONTH=");
    			for(int i=0;i<month.size();i++)
    				result.append(month.get(i)).append(",");

    			result.setCharAt(result.length()-1, ';');
    			month.clear();
    		}

    	} else if(array[0].indexOf("YD") >= 0) {
            interval = array[0].substring(2, array[0].length());
            result.append("YEARLY;").append("INTERVAL=").append(interval).append(";");
            result.append("WKST=").append(WKST).append(";");

            for(int i=1;i<array.length;i++) {

                if(MatchYearDay(array[i])) {
                    month.add(array[i]);
                }

                if(array[i].indexOf("#") >= 0) {
                    count = array[i].substring(1, array[i].length());
                    if(!count.equals("0"))
                        result.append("COUNT=").append(count).append(";");
                }

                if(array[i].indexOf("T") >= 0 && array[i].indexOf("Z") >= 0) {
                    until = array[i];
                    result.append("UNTIL=").append(until).append(";");
                }
            }

            Time t = new Time();
            t.parse(strDTStart); //to get in which year.
            t.switchTimezone(Time.getCurrentTimezone());
			t.normalize(false);
			Log.v(TAG, "eventStar Time: "+t);
			Log.v(TAG, "YD event.yearDay:"+t.yearDay);

			Time tUTC = new Time(Time.TIMEZONE_UTC);
			tUTC.parse(strDTStart);
			tUTC.normalize(false);
			Log.v(TAG, "tUTC: "+tUTC);
			int dayOffset = t.yearDay - tUTC.yearDay;
			int maxYearDay = t.getActualMaximum(Time.YEAR_DAY);

			if(month.size()>0) {
				for(int i=0;i<month.size();i++) {
					String ydYearDay = month.get(i);
					Log.v(TAG, "ydYearDay:"+ydYearDay+" maxYearDay:"+maxYearDay+" dayOffset: "+dayOffset);
					int nYearDay = Integer.parseInt(ydYearDay);
					if(isShift_JIS()) {
						// ingore the RRULE date, only use dtStart.
						nYearDay = t.yearDay + 1;
					}
					// check YD number whether valid
					if((nYearDay-1) > maxYearDay) {
						// reset this repeat event to non-repeat event.
						return "";
					}

					Time correctTime = getTimeByYearDay(t, nYearDay);
					Log.v(TAG, "correctly yearDay:"+correctTime.yearDay+" correctTime:"+correctTime);

					result.append("BYMONTH=");
					result.append(++correctTime.month);
					result.append(";");
					result.append("BYMONTHDAY=");
					result.append(correctTime.monthDay);
					result.append(";");
				}
			}

    		//Log.d(TAG, "interval: "+interval+" result: "+result);
    	} else if (array[0].indexOf("MP") >= 0 || array[0].indexOf("MD") >= 0) {
            interval = array[0].substring(2, array[0].length());
            result.append("MONTHLY;").append("INTERVAL=").append(interval).append(";");
            result.append("WKST=").append(WKST).append(";");

            // Event in Current local device
            Time eventTimeLocal = new Time(Time.TIMEZONE_UTC);
            eventTimeLocal.parse(strDTStart);
            checkTimeStringInAllDay(strDTStart, eventTimeLocal);
            if(checkTimeUTC(strDTStart)){
                eventTimeLocal.switchTimezone(Time.getCurrentTimezone());
            }
			eventTimeLocal.normalize(false);

			// Event in UTC
			Time eventTimeUTC = new Time(Time.TIMEZONE_UTC);
			eventTimeUTC.parse(strDTStart);
			checkTimeStringInAllDay(strDTStart, eventTimeUTC);
			eventTimeUTC.normalize(false);
			int dayOffset = eventTimeLocal.yearDay - eventTimeUTC.yearDay;
			Log.d(TAG, "dayOffset: "+dayOffset);

    		for(int i=1;i<array.length;i++) {
    			if(array[i].indexOf("+") >= 0) {
    				setpos.add(array[i].substring(0, array[i].length()-1));

    			} else if(array[i].indexOf("-") >= 0) {
    				setpos.add("-" + array[i].substring(0, array[i].length()-1));

    			} else if(array[i].indexOf("T") >=0 && array[i].indexOf("Z") >= 0) {
    				until = array[i];
    				result.append("UNTIL=").append(until).append(";");

    			} else if(array[i].indexOf("#") >= 0) {
    				count = array[i].substring(1, array[i].length());
    				if(!count.equals("0"))
    					result.append("COUNT=").append(count).append(";");

    			} else if(MatchWeekDay(array[i])) {
    				weekday.add(array[i]);

    			} else if(MatchMonthDay(array[i])) {
    				monthday.add(array[i]);
    			}
    		}

    		/*if(setpos.size() > 0) {
    			result.append("BYSETPOS=");
    			for(int i=0;i<setpos.size();i++)
    				result.append(setpos.get(i)).append(",");

    			result.setCharAt(result.length()-1, ';');
    			setpos.clear();
    		}*/

    		if(weekday.size() > 0) {
    			result.append("BYDAY=");
    			if(setpos.size() > 0)
    				result.append(setpos.get(0));

    			for(int i=0;i<weekday.size();i++)
    				result.append(weekday.get(i)).append(",");

    			result.setCharAt(result.length()-1, ';');
    			weekday.clear();
    		}

    		String alreadyFixMonthDayOffset = "";
    		if(monthday.size() > 0) {
    			result.append("BYMONTHDAY=");
    			for(int i=0;i<monthday.size();i++) {
    				alreadyFixMonthDayOffset = checkMonthDayWithOffset(dayOffset, eventTimeUTC, monthday.get(i));
    				//alreadyFixMonthDayOffset = monthday.get(i);
    				result.append(alreadyFixMonthDayOffset).append(",");
    			}

    			result.setCharAt(result.length()-1, ';');
    			monthday.clear();
    		}

    	} else if(array[0].indexOf("W") >= 0) {
            interval = array[0].substring(1, array[0].length());
            result.append("WEEKLY;").append("INTERVAL=").append(interval).append(";");
            result.append("WKST=").append(WKST).append(";");

            // Event in Current local device
            Time eventTimeLocal = new Time(Time.TIMEZONE_UTC);
            eventTimeLocal.parse(strDTStart);
            checkTimeStringInAllDay(strDTStart, eventTimeLocal);
            if(checkTimeUTC(strDTStart)){
                eventTimeLocal.switchTimezone(Time.getCurrentTimezone());
            }
			eventTimeLocal.normalize(false);

			// Event in UTC
			Time eventTimeUTC = new Time(Time.TIMEZONE_UTC);
			eventTimeUTC.parse(strDTStart);
			checkTimeStringInAllDay(strDTStart, eventTimeUTC);
			eventTimeUTC.normalize(false);
			int dayOffset = eventTimeLocal.yearDay - eventTimeUTC.yearDay;
			Log.d(TAG, "dayOffset: "+dayOffset);

			String alreadyFixDayOffset = "";
    		for(int i=1;i<array.length;i++) {
    			if(MatchWeekDay(array[i])) {
    				alreadyFixDayOffset = checkWeekDayWithOffset(dayOffset, array[i]);
    				weekday.add(alreadyFixDayOffset);
    			}

    			if(array[i].indexOf("#") >= 0) {
    				count = array[i].substring(1, array[i].length());
    				if(!count.equals("0"))
    					result.append("COUNT=").append(count).append(";");
    			}

    			if(array[i].indexOf("T") >= 0 && array[i].indexOf("Z") >= 0) {
    				until = array[i];
    				result.append("UNTIL=").append(until).append(";");
    			}
    		}

    		if(weekday.size() > 0) {
    			result.append("BYDAY=");
    			for(int i=0;i<weekday.size();i++)
    				result.append(weekday.get(i)).append(",");

    			result.setCharAt(result.length()-1, ';');
    			weekday.clear();
    		}

    	} else if(array[0].indexOf("D") >= 0) {
    		interval = array[0].substring(1, array[0].length());
    		result.append("DAILY;").append("INTERVAL=").append(interval).append(";");
    		result.append("WKST=").append(WKST).append(";");

    		for(int i=0;i<array.length;i++) {
    			if(array[i].indexOf("#") >= 0) {
    				count = array[i].substring(1, array[i].length());
    				if(!count.equals("0"))
    					result.append("COUNT=").append(count).append(";");
    			}

    			if(array[i].indexOf("T") >= 0 && array[i].indexOf("Z") >= 0) {
    				until = array[i];
    				result.append("UNTIL=").append(until).append(";");
    			}
    		}
    	}

    	if(result.length() > 0) {
    		result.deleteCharAt(result.length()-1);
    	}
    	Log.v(TAG, "result: "+result.toString());
    	return result.toString();
    }

    /**
      * To determine if match the weekday
      * @param weekday the day of the week
      * @return boolean if it is a weekday or not
      */
    boolean MatchWeekDay(String weekday) {
    	if(weekday.equalsIgnoreCase("MO"))
    		return true;
    	else if(weekday.equalsIgnoreCase("TU"))
    		return true;
    	else if(weekday.equalsIgnoreCase("WE"))
    		return true;
    	else if(weekday.equalsIgnoreCase("TH"))
    		return true;
    	else if(weekday.equalsIgnoreCase("FR"))
    		return true;
    	else if(weekday.equalsIgnoreCase("SA"))
    		return true;
    	else if(weekday.equalsIgnoreCase("SU"))
    		return true;
    	else
    		return false;
    }

    /**
      * To determine if match the month day
      * @param monthday the day of the month
      * @return boolean if match the month day
      */
    boolean MatchMonthDay(String monthday) {
    	try {
    		if(Integer.parseInt(monthday) >= 1 && Integer.parseInt(monthday) <= 31)
    			return true;
    		else
    			return false;
    	} catch(Exception e) {
			e.printStackTrace();
    		return false;
    	}
    }

    /**
      * To determine if match the month
      * @param month the day of the month
      * @return boolean if match the month
      */
    boolean MatchMonth(String month) {
    	try {
    		if(Integer.parseInt(month) >= 1 && Integer.parseInt(month) <= 12)
    			return true;
    		else
    			return false;
    	} catch (Exception e) {
			e.printStackTrace();
    		return false;
    	}
    }

    /**
      * To determine if match the year day
      * @param year day the day of the year day
      * @return boolean if match the year day
      */
    boolean MatchYearDay(String yearDay) {
    	try {
    		if(Integer.parseInt(yearDay) >= 1 && Integer.parseInt(yearDay) <= 366)
    			return true;
    		else
    			return false;
    	} catch (Exception e) {
			e.printStackTrace();
    		return false;
    	}
    }
    
    
    void setDtstamp(CalendarStruct.EventStruct evtStruct){
           
        Time current = new Time();
        current.setToNow();
        current.switchTimezone("UTC");
        evtStruct.dtstamp = convertLongToRFC2445DateTime(current);
        
        Log.v(TAG, "evtStruct.dtstamp: "+evtStruct.dtstamp);
        
    }

/**
 * Get the event vCalendar string
 * @return the contet string
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public String getContent() {

		CalendarStruct calStruct = new CalendarStruct();

		calStruct.timezone = timezone;
		calStruct.endTimezone = endTimezone;

		CalendarStruct.EventStruct evtStruct = new CalendarStruct.EventStruct();

		evtStruct.uid = uid;
		evtStruct.title = summary;
		evtStruct.description = description;
		evtStruct.dtstart = strDTStart;
		evtStruct.dtend = strDTEnd;
		evtStruct.duration = duration;
		evtStruct.event_location = location;
		evtStruct.rrule = rRule;
		evtStruct.rdate = rDate;
		evtStruct.exrule = exRule;
		evtStruct.exdate = exDate;
		evtStruct.status = status;
		evtStruct.alarm = strAlarm;
		evtStruct.isAllday = isAllDay;
        setDtstamp(evtStruct);
        
        //HMS calendar, drop the database column
		//evtStruct.last_update_time = last_update_time;

		calStruct.addEventList(evtStruct);

		try {
			VCalComposer composer = new VCalComposer();
			return composer.createVCal(calStruct, VCalComposer.VERSION_VCAL10_INT);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
    
    /**
     * Get the event vCalendar string
     * @return the contet string
     *  Hide Automatically by SDK Team [U12000]
     *  @hide
     */
    protected String getEvent() {

    	CalendarStruct calStruct = new CalendarStruct();

    	calStruct.timezone = timezone;
    	calStruct.endTimezone = endTimezone;

    	CalendarStruct.EventStruct evtStruct = new CalendarStruct.EventStruct();

    	evtStruct.uid = uid;
    	evtStruct.title = summary;
    	evtStruct.description = description;
    	evtStruct.dtstart = strDTStart;
    	evtStruct.dtend = strDTEnd;
    	evtStruct.duration = duration;
    	evtStruct.event_location = location;
    	evtStruct.rrule = rRule;
    	evtStruct.rdate = rDate;
    	evtStruct.exrule = exRule;
    	evtStruct.exdate = exDate;
    	evtStruct.status = status;
    	evtStruct.alarm = strAlarm;
    	evtStruct.isAllday = isAllDay;
        //HMS calendar, drop the database column
    	//evtStruct.last_update_time = last_update_time;
        setDtstamp(evtStruct);
        
    		
    	calStruct.addEventList(evtStruct);
    	
    	try {
    		VCalComposer composer = new VCalComposer();    		
    		return composer.createVCalEvent(calStruct);

    	} catch (Exception e) {
			e.printStackTrace();
    		return null;
    	}
    }
    
    /**
     * Get the event vCalendar header
     * @return the header string
     * @deprecated [Module internal use]
     */
    /**@hide*/ 
    public static String getVHeader() {
    	StringBuilder headerStr = new StringBuilder();
    	headerStr.append(VBEGIN).append("\r\n");
    	headerStr.append("VERSION:2.0").append("\r\n");
    	headerStr.append("PRODID:").append(android.os.Build.MODEL).append("\r\n");;
    	return headerStr.toString();
    }
    
    /**
     * Get the event vCalendar tail
     * @return the tile string
     * @deprecated [Module internal use]
     */
    /**@hide*/ 
    public static String getVTail() {
    	StringBuilder tailStr = new StringBuilder();
    	tailStr.append(VEND).append("\r\n");
    	return tailStr.toString();
    }

/**
 * Get the Contact hash
 * @return the hash number
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public long getHash() {

    	vCalendar = getContent();

        CRC32 crc = new CRC32();

        if (vCalendar != null) {
            crc.update(vCalendar.getBytes());
        }

        return crc.getValue();
    }

    private boolean isInteger(String s) {
		char ch;
		for(int i=0; i<s.length(); i++) {
			ch = s.charAt(i);
			//if(PSCommon.debug) Log.e(TAG, "(int) ch = " + String.valueOf((int) ch));
			if((int) ch < 48 || (int) ch > 57) {
				return false;
			}
		}

		return true;
    }

    private void init(Cursor cur, Context c) {
    	int colID;
    	long alldayStart = 0;

        // Set the event id
        colID = cur.getColumnIndexOrThrow(Events._ID);
        long id = cur.getLong(colID);
        _id = String.valueOf(id);

        // Set the event iCal UID 
        String accountType = "";
        if(VersionCheckUtils.afterAPI21()) {
            colID = cur.getColumnIndexOrThrow(Events.ACCOUNT_TYPE);
            accountType = cur.getString(colID);
            
            // PC sync type event will use guid
            if(UriTools.isHTCPCSyncEvent(accountType)) {
                colID = cur.getColumnIndexOrThrow(Events.SYNC_DATA7);
                uid = cur.getString(colID);
            } else {
            	uid = null;
            }        	
        } else {
            colID = cur.getColumnIndexOrThrow(ExEvents.ICALENDAR_UID);
            uid = cur.getString(colID);
        }

        // Set the calendar id [internal use only]
        colID = cur.getColumnIndexOrThrow(Events.CALENDAR_ID);
//        String calID = String.valueOf(cur.getInt(colID));

        // Set the event title
        colID = cur.getColumnIndexOrThrow(Events.TITLE);
        summary = cur.getString(colID);
		if (summary != null) {
			summary = summary.replace("\r", "\r\n").replace("\n", "\r\n");
			summary = qpEncoded(summary);
		}

        // Set the all-day flag
        colID = cur.getColumnIndexOrThrow(Events.ALL_DAY);
        isAllDay = cur.getInt(colID) != 0;

        // Set the event DTSTART
        colID = cur.getColumnIndexOrThrow(Events.DTSTART);
        long start = cur.getLong(colID);
        TimeZone __timezone = java.util.Calendar.getInstance().getTimeZone();
		Time t = new Time();
		t.set(start);
		t.switchTimezone("UTC");
		int daylightOffset=0;
        java.util.Date dt = new java.util.Date(t.toMillis(false));
        if(__timezone.inDaylightTime(dt))
            daylightOffset = __timezone.getDSTSavings();

		if(isAllDay) {
            t.set(t.toMillis(false) - __timezone.getRawOffset());
            if(daylightOffset!=0)
                t.set(t.toMillis(false) - daylightOffset);
            alldayStart = t.toMillis(false);
		}
		strDTStart = convertLongToRFC2445DateTime(t);

		/*if(isAllDay) {
        	TimeZone timezone = java.util.Calendar.getInstance().getTimeZone();
        	start -= timezone.getRawOffset();
        }
        dtStart = new Time();
        dtStart.set(start);
        dtStart.switchTimezone("UTC");
        strDTStart = convertLongToRFC2445DateTime(dtStart);*/

        // Set the event DTEND / DURATION
        colID = cur.getColumnIndexOrThrow(Events.DTEND);
        long end = cur.getLong(colID);
        boolean bNoDTEND = false;
		if (end == 0) {
            bNoDTEND = true;
			colID = cur.getColumnIndexOrThrow(Events.DURATION);
        	duration = cur.getString(colID);
        	try {
                if(!TextUtils.isEmpty(duration)) {
                    Duration d = new Duration();
                    d.parse(duration);
                    end = start + d.getMillis();     
                } 
        	} catch(Exception e) {
        		e.printStackTrace();
        	}
		}
		t.set(end);
		t.switchTimezone("UTC");
		if(isAllDay) {
            t.set(t.toMillis(false) - __timezone.getRawOffset());
            if(daylightOffset!=0)
                t.set(t.toMillis(false) - daylightOffset);
		}
        
        if(!bNoDTEND)
            strDTEnd = convertLongToRFC2445DateTime(t);
        /*if (0 != end) {
        	if(isAllDay) {
            	TimeZone timezone = java.util.Calendar.getInstance().getTimeZone();
            	end-= timezone.getRawOffset();
            }
        	dtEnd = new Time();
            dtEnd.set(end);
            dtEnd.switchTimezone("UTC");
            strDTEnd = convertLongToRFC2445DateTime(dtEnd);
        }
        else {
        	colID = cur.getColumnIndexOrThrow(Events.DURATION);
        	duration = cur.getString(colID);
        }*/

        // Set the event alarms
        colID = cur.getColumnIndexOrThrow(Events.HAS_ALARM);
        hasAlarm = cur.getInt(colID) != 0;
        if (hasAlarm) {
        	Uri uri = Reminders.CONTENT_URI;
        	String where = String.format(Locale.US,REMINDERS_WHERE, Integer.parseInt(_id));
    		Cursor reminderCursor = null;
        	try {
        		reminderCursor = c.getContentResolver().query(uri, REMINDERS_PROJECTION, where, null, null);
        		if(reminderCursor.moveToFirst()) {
        			colID = reminderCursor.getColumnIndexOrThrow(Reminders.MINUTES);
		        	if(isAllDay)
		        		alarm = alldayStart - reminderCursor.getInt(colID)*60000;
		        	else
		        		alarm = start - reminderCursor.getInt(colID)*60000;
		        	Time alarmTime = new Time();
		        	alarmTime.set(alarm);
		        	alarmTime.switchTimezone("UTC");
		        	strAlarm = convertLongToRFC2445DateTime(alarmTime);
		        }
        	} finally {
        		if (reminderCursor != null) {
        			if(!reminderCursor.isClosed()) {
        				reminderCursor.close();
        			}
        			reminderCursor = null;
        		}
        	}
        }

        // Set the event description
        colID = cur.getColumnIndexOrThrow(Events.DESCRIPTION);
        description = cur.getString(colID);
        Log.d(TAG,"isHTML:");
        
        if(description != null) {
        	boolean isHTML = false;
        	if(VersionCheckUtils.afterAPI21()) {
        		if(UriTools.isHTCExchangeEvent(accountType)) {
            		colID = cur.getColumnIndexOrThrow(Events.SYNC_DATA9);
            		isHTML = (cur.getInt(colID) == ExEvents.MIME_TYPE_TEXT_HTML); 
            	}        		 
        	} else {
        		 colID = cur.getColumnIndexOrThrow(ExEvents.DESC_MIME_TYPE);
                 isHTML = (cur.getInt(colID) == 1);
        	}
            
            if(isHTML) {
                //gerald todo
            	//description = MailUtils.convertHTMLtoPlainText(description.trim());        	       	
                description = description.trim();
            }
        	description = description.replace("\r", "\r\n").replace("\n", "\r\n");
        	description = qpEncoded(description);
        }

        // Set the event location
        colID = cur.getColumnIndexOrThrow(Events.EVENT_LOCATION);
        location = cur.getString(colID);
        if(location != null) {
        	  location = location.replace("\r", "\r\n").replace("\n", "\r\n");
        	  location = qpEncoded(location);
           }

        // Set the time zone, THIS IS MUST GET BEFORE RRule_ICalToVCal
        colID = cur.getColumnIndexOrThrow(Events.EVENT_TIMEZONE);
        timezone = cur.getString(colID);
        colID = cur.getColumnIndexOrThrow(Events.EVENT_END_TIMEZONE);
        endTimezone = cur.getString(colID);

        // Set the event recurrence rule
        colID = cur.getColumnIndexOrThrow(Events.RRULE);
        rRule = cur.getString(colID);
        //rRule = RRule_ICalToVCal(rRule);

        // Set the event recurrence date
        colID = cur.getColumnIndexOrThrow(Events.RDATE);
        rDate = cur.getString(colID);

        // Set the event exception recurrence rule
        colID = cur.getColumnIndexOrThrow(Events.EXRULE);
        exRule = cur.getString(colID);

        // Set the event exception date
        colID = cur.getColumnIndexOrThrow(Events.EXDATE);
        exDate = cur.getString(colID);

        // Set the last update time of event
        // HMS calendar, drop the database column
        /*
        colID = cur.getColumnIndexOrThrow("last_update_time");
        last_update_time = cur.getString(colID);
        if(!TextUtils.isEmpty(last_update_time)) {
        	if(last_update_time.indexOf("T") < 0 && last_update_time.indexOf("Z") < 0) {
        		last_update_time = last_update_time.replace("-", "").replace(" ", "T").replace(":", "")+"Z";
        	}
        }*/
    }

    String RRule_ICalToVCal(String rrule){
    	if(rrule == null)
    		return null;

    	Log.d(TAG, "RRule_ICalToVCal: "+rrule);
    	StringBuffer result = new StringBuffer();
    	String array[] = new String[]{};
    	String tempstr = null;
    	String temparr[] = new String[]{};
    	String freq = null;
    	String interval = null;
    	String until = null;
    	String count = null;
    	ArrayList<String> byday = new ArrayList<String>();
    	ArrayList<String> bymonthday = new ArrayList<String>();
    	ArrayList<String> bymonth = new ArrayList<String>();
    	ArrayList<String> bysetpos = new ArrayList<String>();

    	if(rrule.indexOf("FREQ") < 0)
    		return null;

    	array = rrule.split(";", -1);

    	boolean bYDMode = false;

    	for(int i=0;i<array.length;i++) {

    		if(array[i].indexOf("FREQ") >= 0) {
    			freq = array[i].substring(array[i].indexOf("=") + 1, array[i].length());


    			if(freq.equalsIgnoreCase("YEARLY")
    				&& rrule.contains("BYMONTH") && rrule.contains("BYMONTHDAY")) {
    				freq = "YD";
    				bYDMode = true;

    			} else if(freq.equalsIgnoreCase("YEARLY")) {
    				freq = "YM";

    			} else if(freq.equalsIgnoreCase("MONTHLY")) {
    				if(rrule.indexOf("BYDAY") >= 0) {
    					freq = "MP";

    				} else if(rrule.indexOf("BYMONTHDAY") >= 0) {
    					freq = "MD";

    				}
    			} else if(freq.equalsIgnoreCase("WEEKLY")) {
    				freq = "W";

    			} else if(freq.equalsIgnoreCase("DAILY")) {
    				freq = "D";
    			}

    		} else if(array[i].indexOf("INTERVAL") >= 0) {
    			interval = array[i].substring(array[i].indexOf("=") + 1, array[i].length());

    		} else if(array[i].indexOf("UNTIL") >= 0) {
    			until = array[i].substring(array[i].indexOf("=") + 1, array[i].length());

    		} else if(array[i].indexOf("COUNT") >= 0) {
    			count = array[i].substring(array[i].indexOf("=") + 1, array[i].length());

    		} else if(array[i].indexOf("BYDAY") >= 0) {
    			tempstr = array[i].substring(array[i].indexOf("=") + 1, array[i].length());

    			// Event in DB
    			Time eventTimeDB = new Time(Time.TIMEZONE_UTC);
    			eventTimeDB.parse(strDTStart);
    			checkTimeStringInAllDay(strDTStart, eventTimeDB);
    			eventTimeDB.switchTimezone(timezone);
    			eventTimeDB.normalize(false);

    			// Event in UTC
    			Time eventTimeUTC = new Time(Time.TIMEZONE_UTC);
    			eventTimeUTC.parse(strDTStart);
    			checkTimeStringInAllDay(strDTStart, eventTimeUTC);
    			eventTimeUTC.normalize(false);
    			int dayOffset = getMonthDayOffset(eventTimeUTC, eventTimeDB);
    			Log.d(TAG, "dayOffset: "+dayOffset);

    			char ch = 0;
    			String setpostemp = "";
    			for(int j=0;j<tempstr.length();j++) {
    				ch = tempstr.charAt(j);
    				Log.d(TAG, "  ch:"+((int)ch));
    				if( ((int)ch >= 48 && (int)ch <=57)
    					|| (int)ch==45) {
    					setpostemp += ch;
    				} else
    					break;
    			}

    			if(setpostemp.length() > 0) {
    				Log.d(TAG, "BYDAY setpostemp: "+setpostemp);
    				bysetpos.add(setpostemp);
    				byday.add(tempstr.substring(setpostemp.length()));

    			} else {
    				Log.d(TAG, "BYDAY setpostemp len<0  "+setpostemp);
    				temparr = tempstr.split(",", -1);
    				String alreadyFixOffset = "";
    				for(int j=0;j<temparr.length;j++) {
    					alreadyFixOffset = checkWeekDayWithOffset(dayOffset, temparr[j]);
    					//alreadyFixOffset = temparr[j];
    					byday.add(alreadyFixOffset);
    				}
    			}
    			tempstr = null;
    			temparr = null;

    		} else if(array[i].indexOf("BYMONTHDAY") >= 0) {
    			tempstr = array[i].substring(array[i].indexOf("=") + 1, array[i].length());

    			// Event in DB
    			Time eventTimeDB = new Time(Time.TIMEZONE_UTC);
    			eventTimeDB.parse(strDTStart);
    			checkTimeStringInAllDay(strDTStart, eventTimeDB);
    			eventTimeDB.switchTimezone(timezone);
    			eventTimeDB.normalize(false);

    			// Event in UTC
    			Time eventTimeUTC = new Time(Time.TIMEZONE_UTC);
    			eventTimeUTC.parse(strDTStart);
    			checkTimeStringInAllDay(strDTStart, eventTimeUTC);
    			eventTimeUTC.normalize(false);

    			int dayOffset = getMonthDayOffset(eventTimeUTC, eventTimeDB);
    			Log.d(TAG, "dayOffset: "+dayOffset);

    			temparr = tempstr.split(",", -1);
    			String alreadyFixMonthDay = "";
    			for(int j=0;j<temparr.length;j++) {
    				alreadyFixMonthDay = checkMonthDayWithOffset(dayOffset, eventTimeDB, temparr[j]);
    				//alreadyFixMonthDay = temparr[j];
    				bymonthday.add(alreadyFixMonthDay);
    			}
    			tempstr = null;
    			temparr = null;

    		} else if(array[i].indexOf("BYMONTH") >= 0) {

    			// Event in DB
    			Time eventTimeDB = new Time(Time.TIMEZONE_UTC);
    			eventTimeDB.parse(strDTStart);
    			checkTimeStringInAllDay(strDTStart, eventTimeDB);
    			eventTimeDB.switchTimezone(timezone);
    			eventTimeDB.normalize(false);

    			// Event in UTC
    			Time eventTimeUTC = new Time(Time.TIMEZONE_UTC);
    			eventTimeUTC.parse(strDTStart);
    			checkTimeStringInAllDay(strDTStart, eventTimeUTC);
    			eventTimeUTC.normalize(false);
    			int dayOffset = getMonthDayOffset(eventTimeUTC, eventTimeDB);
    			Log.d(TAG, "dayOffset: "+dayOffset);

    			boolean bFindByMonthDay = false;
    			String tempByMonthDay = null;
    			String tempByMonthDayAry[] = new String[]{};
    			for(int idx=0;idx<array.length;idx++) {
    				if(array[idx].indexOf("BYMONTHDAY") >= 0) {
    					tempByMonthDay = array[idx].substring(array[idx].indexOf("=") + 1, array[idx].length());
    					tempByMonthDayAry = tempByMonthDay.split(",", -1);
    					bFindByMonthDay = true;
    					break;
    				}
    			}

    			tempstr = array[i].substring(array[i].indexOf("=") + 1, array[i].length());
    			temparr = tempstr.split(",", -1);
    			String alreadyFixOffset = "";
    			for(int j=0;j<temparr.length;j++) {
    				if(bFindByMonthDay) {
    					alreadyFixOffset = checkMonthWithOffset(dayOffset, eventTimeDB, temparr[j], tempByMonthDayAry[j]);
    					bymonth.add(alreadyFixOffset);
    				} else {
    					bymonth.add(temparr[j]);
    				}
    			}
    			tempstr = null;
    			temparr = null;

    		} else if(array[i].indexOf("BYSETPOS") >= 0) {
    			tempstr = array[i].substring(array[i].indexOf("=") + 1, array[i].length());
    			temparr = tempstr.split(",", -1);
    			for(int j=0;j<temparr.length;j++)
    				bysetpos.add(temparr[j]);
    			tempstr = null;
    			temparr = null;
    		}

    	}

    	if(interval == null)
    		interval = "1";

    	result.append(freq).append(interval).append(" ");

    	if(!bysetpos.isEmpty()) {
    		for(int j=0;j<bysetpos.size();j++) {
    			try {
    				if(Integer.parseInt(bysetpos.get(j)) >= 0)
    					result.append(bysetpos.get(j)).append("+").append(" ");
    				else
    					result.append(bysetpos.get(j).substring(1)).append("-").append(" ");
    			}catch (Exception e){
					e.printStackTrace();
    			}
    		}
    		bysetpos.clear();
    	}
    	Log.d(TAG, "bYDMode: "+bYDMode);

    	if(bYDMode) {
    		int month = 1;    //[0-11]
    		int monthDay = 1; //[1-31]

    		if(!bymonth.isEmpty()) {
    			month = Integer.parseInt( bymonth.get(0));
    			bymonth.clear();
    		}

    		if(!bymonthday.isEmpty()) {
    			monthDay = Integer.parseInt(bymonthday.get(0));
        		bymonthday.clear();
        	}
    		month -= 1; //[0-11]

    		Log.d(TAG, "strDTStart: "+strDTStart);

    		Time origTime = new Time(Time.TIMEZONE_UTC);
    		origTime.parse(strDTStart);
    		origTime.month = month;
    		origTime.switchTimezone(Time.getCurrentTimezone());
    		origTime.monthDay = monthDay;
    		origTime.normalize(false);

    		Log.d(TAG, "origTime: "+origTime);
    		result.append(++origTime.yearDay).append(" ");
    	} else {
        	if(!bymonth.isEmpty()) {
        		for(int j=0;j<bymonth.size();j++)
        			result.append(bymonth.get(j)).append(" ");
        		bymonth.clear();
        	}

        	if(!byday.isEmpty()) {
        		for(int j=0;j<byday.size();j++)
        			result.append(byday.get(j)).append(" ");
        		byday.clear();
        	}

        	if(!bymonthday.isEmpty()) {
        		for(int j=0;j<bymonthday.size();j++)
        			result.append(bymonthday.get(j)).append(" ");
        		bymonthday.clear();
        	}
    	}

    	if(until != null && count == null)
    		result.append(until);
    	else if(until == null && count != null)
    		result.append("#").append(count);
    	else if(until == null && count == null)
    		result.append("#0");
    	String sOut = result.toString();
    	Log.d(TAG, "result: "+sOut);
    	return sOut;
    }

    private static String convertLongToRFC2445DateTime(Time time) {
         return time.format("%Y%m%dT%H%M00Z");
    }

/**
  * To get the multi events CV
  * @return the ContentValues in the ArrayList
  */
    public ArrayList<ContentValues> getMultiEventsCV() {
    	return mMultiVCal_Event_ContentValue;
    }

/**
  * To get the multi to-do CV
  * @return the ContentValues in the ArrayList
  */
    public ArrayList<ContentValues> getMultiToDoCV() {
    	return mMultiVCal_ToDo_ContentValue;
    }

    /**
      * To get the event CV
      * @return the ContentValues
      */
    public ContentValues getEventCV() {
        ContentValues cv = new ContentValues();

        // Use temporary Calendar ID for experiment only.
        // TODO: Should use "Active" calendar ID or something else.
        // cv.put(Events.CALENDAR_ID, Calendar.PCSC_CAL_ID);

        if(VersionCheckUtils.afterAPI21()) {
            if(!TextUtils.isEmpty(uid))
            	cv.put(Events.SYNC_DATA7, uid);
        } else {
        	cv.put(ExEvents.ICALENDAR_UID, uid);
        }        

        // What
        cv.put(Events.TITLE, summary);
        // When
        cv.put(Events.DTSTART, dtStart.toMillis(false));
        if (dtEnd != null && m_bHas_dtEnd) {
        	cv.put(Events.DTEND, dtEnd.toMillis(false));
        }

        if (duration != null) {
        	cv.put(Events.DURATION, duration);
        }
        // Where
        cv.put(Events.EVENT_LOCATION, location);
        // Description
        if(description==null)
            description="";

        if(categories != null)
            description=description+"\ncategories: "+categories;

        if (priority != null)
        	description=description+"\npriority: "+priority;

        if (status != null)
        	description=description+"\nstatus: "+status;

        if(description=="")
        	description=null;
        
		cv.put(Events.DESCRIPTION, description);	

        cv.put(Events.ALL_DAY, isAllDay == true ? 1 : 0);

        if (timezone != null) {
            cv.put(Events.EVENT_TIMEZONE, timezone);
        }

        if (endTimezone != null) {
             cv.put(Events.EVENT_END_TIMEZONE, endTimezone);
        }

        if(hasAlarm) {
        	cv.put(Events.HAS_ALARM, (int)1);
        } else {
            cv.put(Events.HAS_ALARM, (int)0);
        }

        if (rRule != null)
        	cv.put(Events.RRULE, rRule);

        if (rDate != null)
        	cv.put(Events.RDATE, rDate);

        if (exRule != null)
        	cv.put(Events.EXRULE, exRule);

        if (exDate != null) {
        	exDate = exDate.replace(";", ",");
        	cv.put(Events.EXDATE, exDate);
        }

        return cv;
    }

/**
  * To get the multi event alarms CV
  * @return the ContentValues in the ArrayList
  */
    public ArrayList<ContentValues> getMultiEventAlarmsCV() {
    	return mMultiVCal_EventAlarm_ContentValue;
    }
    
/**
  * To get the multi todo alarms CV
  * @return the ContentValues in the ArrayList
  */
    public ArrayList<ContentValues> getMultiToDoAlarmsCV() {
        return mMultiVCal_ToDoAlarm_ContentValue;
    }

/**
  * To get the alarms CV
  * @return the ContentValues in the ArrayList
  *  Hide Automatically by SDK Team [U12000]
  *  @hide
  */
    public ContentValues getAlarmCV() {
    	return getAlarmCV(-1);  // -1 is just an initial, will be re-assign in inport! 
    }

/**
  * To get the alarms CV
  * @param eventId the event ID
  * @return the ContentValues in the ArrayList
  * @deprecated [Module internal use]
  */
    /**@hide*/ 
    public ContentValues getAlarmCV(long eventId) {
    	ContentValues cv = new ContentValues();
    	if(eventId > 0) {
    		cv.put(Reminders.EVENT_ID, eventId);
    	}
        cv.put(Reminders.METHOD, 1);
        cv.put(Reminders.MINUTES, alarm);

    	return cv;
    }

/**
  * To get the encoded
  * @param str the string to be encoded
  * @return The qp-encoded string
  *  Hide Automatically by SDK Team [U12000]
  *  @hide
  */
    public static String qpEncoded(String str) {
    	String result = null;

		if(isShift_JIS()) {
			str = escapeCharacters(str);
		}

    	QuotedPrintableCodec qpcodec;
    	qpcodec = new QuotedPrintableCodec(getDefaultCharSet());
    	try {
			result = qpcodec.encode(str);
		} catch (EncoderException e) {
			e.printStackTrace();
		}

		return result;
    }

/**
  * To get the default charset
  * @return the default charset string
  *  Hide Automatically by SDK Team [U12000]
  *  @hide
  */
    public static String getDefaultCharSet() {
    	String charSet;    	
    	if(isShift_JIS()) {    	
    		//language or SBM build flag
    		charSet = "Shift_JIS";
    	} else if (isBig5()) {    
    		//language & country
    		charSet = "BIG5";
    	} /*else if (isGB2312()) {
    		//Other device is using UTF-8, they can not recognize our VCalendar  
    		//language & country
    		charSet = "GB2312";
    	}*/ else if (isISO8859_8()) {
    		//language
    		charSet = "ISO-8859-8";
    	} else {
    		charSet = default_charset; //default_charset;
    	} 
    	return charSet;
    }

    private String checkWeekDayWithOffset(int dayOffset, String weekDay) {    	
    	//{"SU", "MO", "TU", "WE", "TH", "FR", "SA"};
    	//{ 0     1     2     3     4     5     6  };

    	int nIdx = 0;
    	try {
    		Integer tmpe_idx = weekDayAry.get(weekDay.substring(weekDay.length()-2, weekDay.length()));
    		
    		if(tmpe_idx != null) {
    			nIdx = tmpe_idx;
    		} else {
    			Log.d(TAG, "checkWeekDayWithOffset, tmpe_idx is null !");
    		}
    	} catch(Exception e) {
    		Log.e(TAG, "checkWeekDayWithOffset : "+weekDay);
    	}
    	Log.d(TAG, "dayOffset: "+dayOffset+" weekDay: "+weekDay+" nIdx: "+nIdx);
    	nIdx += dayOffset;

    	if(nIdx < 0) {
    		nIdx = 6;
    	} else if(nIdx > 6) {
    		nIdx = 0;
    	}
    	Log.d(TAG, "nIdx: "+nIdx);
    	return weekDays[nIdx];
    }

    private String checkMonthDayWithOffset(int dayOffset, Time dbStartTime, String monthDay) {
    	int nDay = Integer.parseInt(monthDay);
    	int MAX_DAY = dbStartTime.getActualMaximum(Time.MONTH_DAY);
    	int tempDay = nDay + dayOffset;

    	debug("nDay: "+nDay);
    	debug("MAX_DAY: "+MAX_DAY);
    	debug("tempDay: "+tempDay);
    	debug("dbStartTime: "+dbStartTime);
    	if(tempDay - MAX_DAY > 0) {
    		//debug("1");
    		nDay = 1 + (tempDay - MAX_DAY - 1);
    	} else if(tempDay == 0) {
    		//debug("2");
    		if(dbStartTime.month == 0) {
    			dbStartTime.set(dbStartTime.monthDay, 11, --dbStartTime.year);
    		} else {
    			dbStartTime.set(dbStartTime.monthDay, --dbStartTime.month, dbStartTime.year);
    		}
    		dbStartTime.normalize(false);
    		nDay = dbStartTime.getActualMaximum(Time.MONTH_DAY);
    	} else if(nDay == dbStartTime.monthDay+dayOffset) {
    		// this case unnecessary to fix offset month day
    	} else {
    		//debug("3");
    		nDay = tempDay;
    	}
    	debug(">> nDay: "+nDay);
    	return String.valueOf(nDay);
    }

    private String checkMonthWithOffset(int dayOffset, Time dbStartTime, String month, String monthDay) {
    	int nMonth = Integer.parseInt(month);
    	int nDay = Integer.parseInt(monthDay);

    	int MAX_DAY = dbStartTime.getActualMaximum(Time.MONTH_DAY);
    	int tempDay = nDay + dayOffset;

    	debug("nMonth, nDay: "+nMonth+","+nDay);
    	debug("MAX_DAY: "+MAX_DAY);
    	debug("tempDay: "+tempDay);

    	if(tempDay - MAX_DAY > 0) {
    		//debug("1");
    		nDay = 1 + (tempDay - MAX_DAY - 1);
    		nMonth++;

    		if(nMonth > 12) {
    			nMonth = 1;
    		}
    	} else if(tempDay <= 0) {
    		//debug("2");
    		nMonth--;

    		if(nMonth <= 0) {
    			nMonth = 12;
    		}
    	}
    	debug(">> nMonth: "+nMonth);
    	return String.valueOf(nMonth);
    }

    private boolean checkTimeObj(String strTime) {
    	debug("check time obj - Date: "+strTime);

    	boolean bHaveZ = false;
    	boolean bInUTC = true;

    	Time t = new Time();
    	bInUTC = t.parse(strTime);

    	bHaveZ = strTime.contains("Z");

    	debug("bHaveZ:"+bHaveZ+" bInUTC:"+bInUTC);
    	if( bInUTC || !bHaveZ) {
    		debug("t year "+t.year);

    		debug("t month "+t.month);
    		// check month [0-11]
    		if(t.month < 0 || t.month > 11) {
    			Log.e(TAG, "month out of range ["+t.month+"]");
    			return false;
    		}

    		debug("t monthDay "+t.monthDay);
    		// check monthDay [1-31]
    		if(t.monthDay < 1 || t.monthDay > 31) {
    			Log.e(TAG, "monthDay out of range ["+t.monthDay+"]");
    			return false;
    		}

    		debug("t hour "+t.hour);
    		// check hour [0-23]
    		if(t.hour < 0 || t.hour > 23) {
    			Log.e(TAG, "hour out of range ["+t.hour+"]");
    			return false;
    		}

    		debug("t minute "+t.minute);
    		// check minute [0-59]
    		if(t.minute < 0 || t.minute > 59) {
    			Log.e(TAG, "minute out of range ["+t.minute+"]");
    			return false;
    		}

    		debug("t second "+t.second);
    		// check second [0-59]
    		if(t.second < 0 || t.second > 59) {
    			Log.e(TAG, "second out of range ["+t.second+"]");
    			return false;
    		}
    	} else {
    		Log.e(TAG, "Parse :"+strTime+" error");
    		return false;
    	}
    	return true;
    }

    private void debug(String msg) {
    	if(DEBUG) {
    		Log.d(TAG, msg);
    	}
    }

    private int getMonthDayOffset(Time t1, Time t2) {
    	Log.d(TAG, "t1:"+t1);
    	Log.d(TAG, "t2:"+t2);

    	if(t1.year > t2.year) {
    		return 1;
    	} else if(t1.year < t2.year) {
    		return -1;
    	} else {
    		return t1.yearDay - t2.yearDay;
    	}
    }

    private void checkTimeStringInAllDay(String strDTStart, Time eventTime) {
    	if(strDTStart.length()<=9) {
    		eventTime.allDay = false;
		}
    }

    private Time getTimeByYearDay(Time t, int yearDay) {
    	int year = t.year;
    	Log.v(TAG, "getTimeByYearDay inWhichYear:"+year+" yearDay:"+yearDay);
    	int inWhichMonth = 0;
    	int inWhichDay = 0;
    	int dayCount = 0;

    	for(int i=0; i<12; i++) {
    		inWhichMonth = i;
    		t.set(1, i, year);
    		t.normalize(false);
    		int num = t.getActualMaximum(Time.MONTH_DAY);
    		dayCount += num;
    		if(dayCount > yearDay) {
    			inWhichDay = yearDay - (dayCount - num);
    			break;
    		} else if(dayCount == yearDay) {
    			inWhichDay = num;
    			break;
    		} else {
    			//Log.v(TAG, String.format("     [%d]:%d  dayCount:%d", i, num, dayCount));
    		}
    	}

    	Log.v(TAG, "getTimeByYearDay inWhichMonth:"+(inWhichMonth+1)+" inWhichDay:"+inWhichDay);
    	t.set(inWhichDay, inWhichMonth, year);
    	t.normalize(false);
    	return t;
    }

    private static String escapeCharacters(final String unescaped) {
        if (TextUtils.isEmpty(unescaped)) {
            return "";
        }
        //Log.v(TAG, "escapeCharacters: "+unescaped);
        final StringBuilder tmpBuilder = new StringBuilder();
        final int length = unescaped.length();
        for (int i = 0; i < length; i++) {
            char ch = unescaped.charAt(i);
            if((int)ch == 0xFF0D) {
            	ch = '-';
            }
            //Log.w(TAG, String.format("%02x ## %c", (int)ch, ch));
            switch (ch) {
                case ';': {
                    tmpBuilder.append('\\');
                    tmpBuilder.append(';');
                    break;
                }
//                case '\r': {
//                    if (i + 1 < length) {
//                        char nextChar = unescaped.charAt(i);
//                        if (nextChar == '\n') {
//                            break;
//                        } else {
//                            // fall through
//                        }
//                    } else {
//                        // fall through
//                    }
//                }
//                case '\n': {
//                    tmpBuilder.append("\\n");
//                    break;
//                }
                case '\\': {

                    tmpBuilder.append("\\\\");
                    break;
                }
                default: {
                    tmpBuilder.append(ch);
                    break;
                }
            }
        }
        return tmpBuilder.toString();
    }

/**
  * To determine if SBM
  * @return boolean if SBM
  * @deprecated [Not use any longer]
  *  Hide Automatically by SDK Team [U12000]
  *  @hide
  */
    /**@hide*/ 
    public static boolean isSBM() {
    	return false;
    }
    private boolean checkTimeUTC(String strTime) {
        debug("check time UTC - Date: "+strTime);
        boolean bHaveZ = false;
        bHaveZ = strTime.contains("Z");
        return bHaveZ;
    }
    
    /**
     * To determine if Shift_JIS
     * @return boolean if Shift_JIS
     * @deprecated [Module internal use]
     */
    /**@hide*/ 
	public static boolean isShift_JIS() {
		Locale locale = Locale.getDefault();
		String language = locale.getLanguage();

		if (language.equals("ja")) {
			return true;
		} else {
			// check is SBM project
			return HolidayUtils.isJapanSku();
		}
	}
    
    
    /**
      * To determine if Big5
      * @return boolean if Big5
      * @deprecated [Module internal use]
      */
	/**@hide*/ 
    public static boolean isBig5() {   	
    	Locale locale = Locale.getDefault();   	
    	String language = locale.getLanguage();    
    	String country = locale.getCountry();
       	
    	if(language.equals("zh") && country.equals("TW")) {
    		return true;    
    	} else {
    		return false;
    	}    
    }
    
    /**
      * To determine if GB2312
      * @return boolean if GB2312
      * @deprecated [Not use any longer]
      */
	/**@hide*/ 
    public static boolean isGB2312() {
    	Locale locale = Locale.getDefault();  
    	String language = locale.getLanguage();   
    	String country = locale.getCountry();    	
    	
    	if(language.equals("zh") && country.equals("CN")) {
    		return true;    
    	}
    	else {
    		return false;   
    	}
    }
    
    /**
      * To determine if ISO8859_8
      * @return boolean if ISO8859_8
      * @deprecated [Module internal use]
      */
	/**@hide*/ 
    public static boolean isISO8859_8() {
    	Locale locale = Locale.getDefault();  
    	String language = locale.getLanguage();   
    	
    	if(language.equals("iw")) {
    		return true;    
    	}else {
    		return false;   
    	}
    }
}
