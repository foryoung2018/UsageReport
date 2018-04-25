
package com.htc.lib1.cs.httpclient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * URL encoding utils
 * 
 * @author autosun_li@htc.com
 */
public class URLEncodedUtils {
    private static final String UTF8 = "UTF-8";
    private static HtcLogger sLogger = new CommLoggerFactory(URLEncodedUtils.class).create();

    /**
     * Data structure of name value pair.
     * 
     * @author autosun_li@htc.com
     */
    public static class NameValuePair {
        private String mName;
        private String mValue;

        public NameValuePair(String name, String value) {
            // Test arguments.
            if (TextUtils.isEmpty(name))
                throw new IllegalArgumentException("'name' is empty or null.");

            mName = name;
            mValue = value;
        }

        public String getName() {
            return mName;
        }

        public String getValue() {
            return mValue;
        }
    }

    /**
     * Format URL encoding.
     * 
     * @param params List of NameValuePair.
     * @return Formated string.
     */
    public static String format(List<NameValuePair> params) {
        StringBuilder result = new StringBuilder();
        for (NameValuePair pair : params) {
            try {
                String encoded = URLEncoder.encode(pair.getValue(), UTF8);
                if (result.length() != 0) {
                    result.append('&');
                }
                result.append(pair.getName()).append('=').append(encoded);
            } catch (UnsupportedEncodingException e) {
                // Skip if Exception
                sLogger.warningS("Unable to encode value: ", pair.getValue());
            }
        }
        return result.toString();
    }

    /**
     * Parse URL encoding strings to name value pair.
     * 
     * @param query Raw query string.
     * @return List of name value pair.
     */
    public static List<NameValuePair> parse(String query) {
        List<NameValuePair> result = new ArrayList<NameValuePair>();
        if (!TextUtils.isEmpty(query)) {
            for (String pair : query.split("&")) {
                int posEqSign = pair.indexOf('=');
                if (posEqSign > 0) {
                    result.add(new NameValuePair(
                                pair.substring(0, posEqSign), pair.substring(posEqSign + 1)));
                }
            }
        }
        return result;
    }
}
