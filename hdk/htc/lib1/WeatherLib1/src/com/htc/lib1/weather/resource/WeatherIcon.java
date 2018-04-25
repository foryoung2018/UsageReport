
package com.htc.lib1.weather.resource;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.htc.lib1.weather.R;

/**
 * Helper to get weather icon drawable.
 */
public class WeatherIcon {

    /**
     * @param context context
     * @param conditionId condition id
     * @return Drawable drawable
     */
    public Drawable getConditionIconDark(Context context, int conditionId) {
        try {
            int resId = getConditionIconDark(conditionId);

            return context.getResources().getDrawable(resId);
        } catch (NotFoundException e) {
            Log.d(WeatherIcon.class.getSimpleName(), "Resource not found: dark_" + conditionId);
        }

        return null;
    }

    /**
     * @param conditionId condition id
     * @return int resource id
     */
    public int getConditionIconDark(int conditionId) {
        switch (conditionId) {
            case 1:
                return R.drawable.weather_vectorgraphic_dark_xl_01;
            case 2:
                return R.drawable.weather_vectorgraphic_dark_xl_02;
            case 3:
                return R.drawable.weather_vectorgraphic_dark_xl_03;
            case 4:
                return R.drawable.weather_vectorgraphic_dark_xl_04;
            case 5:
                return R.drawable.weather_vectorgraphic_dark_xl_05;
            case 6:
                return R.drawable.weather_vectorgraphic_dark_xl_06;
            case 7:
                return R.drawable.weather_vectorgraphic_dark_xl_07;
            case 8:
                return R.drawable.weather_vectorgraphic_dark_xl_08;
                /* Icons 9-10 have been retired */
            case 11:
                return R.drawable.weather_vectorgraphic_dark_xl_11;
            case 12:
                return R.drawable.weather_vectorgraphic_dark_xl_12;
            case 13:
                return R.drawable.weather_vectorgraphic_dark_xl_13;
            case 14:
                return R.drawable.weather_vectorgraphic_dark_xl_14;
            case 15:
                return R.drawable.weather_vectorgraphic_dark_xl_15;
            case 16:
                return R.drawable.weather_vectorgraphic_dark_xl_16;
            case 17:
                return R.drawable.weather_vectorgraphic_dark_xl_17;
            case 18:
                return R.drawable.weather_vectorgraphic_dark_xl_18;
            case 19:
                return R.drawable.weather_vectorgraphic_dark_xl_19;
            case 20:
                return R.drawable.weather_vectorgraphic_dark_xl_20;
            case 21:
                return R.drawable.weather_vectorgraphic_dark_xl_21;
            case 22:
                return R.drawable.weather_vectorgraphic_dark_xl_22;
            case 23:
                return R.drawable.weather_vectorgraphic_dark_xl_23;
            case 24:
                return R.drawable.weather_vectorgraphic_dark_xl_24;
            case 25:
                return R.drawable.weather_vectorgraphic_dark_xl_25;
            case 26:
                return R.drawable.weather_vectorgraphic_dark_xl_26;
                /* Icons 27-28 have been retired */
            case 29:
                return R.drawable.weather_vectorgraphic_dark_xl_29;
            case 30:
                return R.drawable.weather_vectorgraphic_dark_xl_30;
            case 31:
                return R.drawable.weather_vectorgraphic_dark_xl_31;
            case 32:
                return R.drawable.weather_vectorgraphic_dark_xl_32;
            case 33:
                return R.drawable.weather_vectorgraphic_dark_xl_33;
            case 34:
                return R.drawable.weather_vectorgraphic_dark_xl_34;
            case 35:
                return R.drawable.weather_vectorgraphic_dark_xl_35;
            case 36:
                return R.drawable.weather_vectorgraphic_dark_xl_36;
            case 37:
                return R.drawable.weather_vectorgraphic_dark_xl_37;
            case 38:
                return R.drawable.weather_vectorgraphic_dark_xl_38;
            case 39:
                return R.drawable.weather_vectorgraphic_dark_xl_39;
            case 40:
                return R.drawable.weather_vectorgraphic_dark_xl_40;
            case 41:
                return R.drawable.weather_vectorgraphic_dark_xl_41;
            case 42:
                return R.drawable.weather_vectorgraphic_dark_xl_42;
            case 43:
                return R.drawable.weather_vectorgraphic_dark_xl_43;
            case 44:
                return R.drawable.weather_vectorgraphic_dark_xl_44;
                // 51 52 53 54 for Huafeng
            case 51:
                return R.drawable.weather_vectorgraphic_dark_xl_51;
            case 52:
                return R.drawable.weather_vectorgraphic_dark_xl_52;
            case 53:
                return R.drawable.weather_vectorgraphic_dark_xl_53;
            case 54:
                return R.drawable.weather_vectorgraphic_dark_xl_54;
            default:
                return R.drawable.weather_vectorgraphic_dark_xl_07;
        }
    }

    /**
     * @param context context
     * @param conditionId condition id
     * @return Drawable drawable
     */
    public Drawable getConditionIconLight(Context context, int conditionId) {
        try {
            int resId = getConditionIconLight(conditionId);

            return context.getResources().getDrawable(resId);
        } catch (NotFoundException e) {
            Log.d(WeatherIcon.class.getSimpleName(), "Resource not found: light_" + conditionId);
        }

        return null;
    }

    /**
     * @param conditionId condition id
     * @return int resource id
     */
    public int getConditionIconLight(int conditionId) {
        switch (conditionId) {
            case 1:
                return R.drawable.weather_vectorgraphic_light_xl_01;
            case 2:
                return R.drawable.weather_vectorgraphic_light_xl_02;
            case 3:
                return R.drawable.weather_vectorgraphic_light_xl_03;
            case 4:
                return R.drawable.weather_vectorgraphic_light_xl_04;
            case 5:
                return R.drawable.weather_vectorgraphic_light_xl_05;
            case 6:
                return R.drawable.weather_vectorgraphic_light_xl_06;
            case 7:
                return R.drawable.weather_vectorgraphic_light_xl_07;
            case 8:
                return R.drawable.weather_vectorgraphic_light_xl_08;
                /* Icons 9-10 have been retired */
            case 11:
                return R.drawable.weather_vectorgraphic_light_xl_11;
            case 12:
                return R.drawable.weather_vectorgraphic_light_xl_12;
            case 13:
                return R.drawable.weather_vectorgraphic_light_xl_13;
            case 14:
                return R.drawable.weather_vectorgraphic_light_xl_14;
            case 15:
                return R.drawable.weather_vectorgraphic_light_xl_15;
            case 16:
                return R.drawable.weather_vectorgraphic_light_xl_16;
            case 17:
                return R.drawable.weather_vectorgraphic_light_xl_17;
            case 18:
                return R.drawable.weather_vectorgraphic_light_xl_18;
            case 19:
                return R.drawable.weather_vectorgraphic_light_xl_19;
            case 20:
                return R.drawable.weather_vectorgraphic_light_xl_20;
            case 21:
                return R.drawable.weather_vectorgraphic_light_xl_21;
            case 22:
                return R.drawable.weather_vectorgraphic_light_xl_22;
            case 23:
                return R.drawable.weather_vectorgraphic_light_xl_23;
            case 24:
                return R.drawable.weather_vectorgraphic_light_xl_24;
            case 25:
                return R.drawable.weather_vectorgraphic_light_xl_25;
            case 26:
                return R.drawable.weather_vectorgraphic_light_xl_26;
                /* Icons 27-28 have been retired */
            case 29:
                return R.drawable.weather_vectorgraphic_light_xl_29;
            case 30:
                return R.drawable.weather_vectorgraphic_light_xl_30;
            case 31:
                return R.drawable.weather_vectorgraphic_light_xl_31;
            case 32:
                return R.drawable.weather_vectorgraphic_light_xl_32;
            case 33:
                return R.drawable.weather_vectorgraphic_light_xl_33;
            case 34:
                return R.drawable.weather_vectorgraphic_light_xl_34;
            case 35:
                return R.drawable.weather_vectorgraphic_light_xl_35;
            case 36:
                return R.drawable.weather_vectorgraphic_light_xl_36;
            case 37:
                return R.drawable.weather_vectorgraphic_light_xl_37;
            case 38:
                return R.drawable.weather_vectorgraphic_light_xl_38;
            case 39:
                return R.drawable.weather_vectorgraphic_light_xl_39;
            case 40:
                return R.drawable.weather_vectorgraphic_light_xl_40;
            case 41:
                return R.drawable.weather_vectorgraphic_light_xl_41;
            case 42:
                return R.drawable.weather_vectorgraphic_light_xl_42;
            case 43:
                return R.drawable.weather_vectorgraphic_light_xl_43;
            case 44:
                return R.drawable.weather_vectorgraphic_light_xl_44;
                // 51 52 53 54 for Huafeng
            case 51:
                return R.drawable.weather_vectorgraphic_light_xl_51;
            case 52:
                return R.drawable.weather_vectorgraphic_light_xl_52;
            case 53:
                return R.drawable.weather_vectorgraphic_light_xl_53;
            case 54:
                return R.drawable.weather_vectorgraphic_light_xl_54;
            default:
                return R.drawable.weather_vectorgraphic_light_xl_07;
        }
    }
}
