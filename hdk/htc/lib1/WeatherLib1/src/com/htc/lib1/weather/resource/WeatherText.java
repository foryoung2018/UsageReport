
package com.htc.lib1.weather.resource;

import android.content.Context;

import com.htc.lib1.weather.R;

/**
 * Helper to get weather condition description.
 */
public class WeatherText {

    /**
     * get description text for weather conditions
     * 
     * @param context context
     * @param conditionId condition id
     * @return String description
     */
    public static String getConditionText(Context context, int conditionId) {
        String[] stringArray = context.getResources()
                .getStringArray(R.array.htc_weather_conditions);
        if (stringArray == null)
            return "";

        switch (conditionId) {
            case 1:
                return stringArray[0];
            case 2:
                return stringArray[1];
            case 3:
                return stringArray[2];
            case 4:
                return stringArray[3];
            case 5:
                return stringArray[4];
            case 6:
                return stringArray[5];
            case 7:
                return stringArray[6];
            case 8:
                return stringArray[7];
                /* Icons 9-10 have been retired */
            case 11:
                return stringArray[8];
            case 12:
                return stringArray[9];
            case 13:
                return stringArray[10];
            case 14:
                return stringArray[11];
            case 15:
                return stringArray[12];
            case 16:
                return stringArray[13];
            case 17:
                return stringArray[14];
            case 18:
                return stringArray[15];
            case 19:
                return stringArray[16];
            case 20:
                return stringArray[17];
            case 21:
                return stringArray[18];
            case 22:
                return stringArray[19];
            case 23:
                return stringArray[20];
            case 24:
                return stringArray[21];
            case 25:
                return stringArray[22];
            case 26:
                return stringArray[23];
                /* Icons 27-28 have been retired */
            case 29:
                return stringArray[24];
            case 30:
                return stringArray[25];
            case 31:
                return stringArray[26];
            case 32:
                return stringArray[27];
            case 33:
                return stringArray[28];
            case 34:
                return stringArray[29];
            case 35:
                return stringArray[30];
            case 36:
                return stringArray[31];
            case 37:
                return stringArray[32];
            case 38:
                return stringArray[33];
            case 39:
                return stringArray[34];
            case 40:
                return stringArray[35];
            case 41:
                /* if no resource for 41, just reuse 42 */
                return stringArray[36];
            case 42:
                return stringArray[37];
            case 43:
                return stringArray[38];
            case 44:
                return stringArray[39];
                // 51 52 53 54 for Huafeng
            case 51:
                return stringArray[40];
            case 52:
                return stringArray[41];
            case 53:
                return stringArray[42];
            case 54:
                return stringArray[43];
            default:
                return "";
        }
    }

}
