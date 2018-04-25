package com.htc.lib1.dm.util;

import com.htc.lib1.dm.constants.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Joe_Wu on 12/22/14.
 */
public class FormatUtil {
    public static String timestampToDate(Long timestamp) {
        if(timestamp==null)
            return "null";

        try {
            Date date = new Date();
            date.setTime(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATETIME_FORMAT);
            return sdf.format(date);
        }catch(Exception ex){
            ex.printStackTrace();
            return timestamp.toString();
        }

    }
}
