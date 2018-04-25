package com.htc.lib1.phonecontacts;

import java.lang.StringBuilder;

public class PhoneUtils {
    
	public static final char WILD_1 = '?';
    public static final char PAUSE_1 = 'p';
    public static final char WAIT_1 =  'w';


	/**
     * Create a new function for smart search. This function is used to extract
     * available char supporting smart search.
     * @param number The input number
     * @return the pure number
     */
    public static String extractPureNumber(String number) {
        if (number == null) {
            return null;
        }

        int len = number.length();
        StringBuilder pureNumber = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            char c = number.charAt(i);

            if( (i == 0 && c == '+') ||
                        ('0' <= c && c <= '9') ||
                        (c == '*') ||
                        (c == '#') ||
                        (c == '+') ||
                        (c == WILD_1) ||
                        (c == android.telephony.PhoneNumberUtils.WILD) ||
                        (c == PAUSE_1) ||
                        (c == android.telephony.PhoneNumberUtils.PAUSE) ||
                        (c == WAIT_1) ||
                        (c == android.telephony.PhoneNumberUtils.WAIT)) {
                    pureNumber.append(c);
            }            
        }

        return pureNumber.toString();
    }
}
