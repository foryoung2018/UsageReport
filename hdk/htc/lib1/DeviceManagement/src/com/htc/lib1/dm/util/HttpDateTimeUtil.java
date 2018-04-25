package com.htc.lib1.dm.util;

import com.htc.lib1.dm.logging.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HttpDateTimeUtil {

    private static final Logger LOGGER = Logger.getLogger("[DM]", HttpDateTimeUtil.class);

    private static final String FORMAT_RFC_1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
    private static final String FORMAT_RFC_1036 = "EEEEEE, dd-MMM-yy HH:mm:ss zzz";
    private static final String FORMAT_ASC_TIME = "EEE MMMM d HH:mm:ss yyyy";

    private static final String[] FORMATS = new String[]{FORMAT_RFC_1123, FORMAT_RFC_1036, FORMAT_ASC_TIME};

    /**
     * See: http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.3
     * <p>
     * Sun, 06 Nov 1994 08:49:37 GMT  ; RFC 822, updated by RFC 1123
     * Sunday, 06-Nov-94 08:49:37 GMT ; RFC 850, obsoleted by RFC 1036
     * Sun Nov  6 08:49:37 1994       ; ANSI C's asctime() format
     */
    public static long parseDateTime(String date) throws IllegalArgumentException {
        for (String format : FORMATS) {
            Date dateTime = parse(date, getDateFormat(format));
            if (null != dateTime) {
                return dateTime.getTime();
            }
        }

        throw new IllegalArgumentException("[DM] HTTP Date Time Format Invalid");
    }

    private static Date parse(String date, SimpleDateFormat sdf) {
        try {
            return sdf.parse(date);
        } catch (Exception e) {
            LOGGER.warning(e);
            return null;
        }
    }

    private static SimpleDateFormat getDateFormat(String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);

        if (FORMAT_RFC_1036.equals(format)) {
            dateFormat.set2DigitYearStart(Calendar.getInstance().getTime());
        }

        return dateFormat;
    }

}
