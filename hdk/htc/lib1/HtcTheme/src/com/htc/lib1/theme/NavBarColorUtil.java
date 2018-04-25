package com.htc.lib1.theme;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.htc.lib0.customization.HtcWrapCustomizationManager;
import com.htc.lib0.customization.HtcWrapCustomizationReader;
/**
 * Created by jason_lai on 10/5/17.
 */

public class NavBarColorUtil {
    private static final String LOG_TAG = "NavBarColorUtil";

    private static final HtcWrapCustomizationReader getReader() {
        HtcWrapCustomizationReader customizeReader = null;
        try {
            HtcWrapCustomizationManager customizationManager = new HtcWrapCustomizationManager();
            customizeReader = customizationManager.getCustomizationReader("SystemUI", HtcWrapCustomizationManager.READER_TYPE_XML, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customizeReader;
    }

    /**
     * load color from ACC.
     **/
    public static final int loadNavBarBkgColor() throws Exception {
        HtcWrapCustomizationReader reader = getReader();
        String navBarBkg = null;
        if (reader != null) {
            navBarBkg = reader.readString("navigation_background", null);
        }
        return Color.parseColor(navBarBkg);
    }


    //use platform flag on API >= 26
    //View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    private static final int SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR = 0x10;

    /**
     * set navigation bar color on  window
     * We don't apply navigation bar bkg color and icon color if acc value is incorrect.
     *
     * Window.setNavigationBarColor was added in android L.
     * View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR was added in android O.
     *
     * @params window
     **/
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static final void setNavBarBkg(Window window) {
        if (window == null) {
            ThemeSettingUtil.logd(LOG_TAG, "window is null");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                int color = loadNavBarBkgColor();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setNavigationBarColor(color);
                if (isLightColor(color)) {
                    int systemFlag = window.getDecorView().getSystemUiVisibility();
                    window.getDecorView().setSystemUiVisibility(systemFlag | SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
                }
            } catch (Exception e) {
                ThemeSettingUtil.logw(LOG_TAG, "Don't need to apply nav color %s", e.getMessage());
            }

        }
    }


    /**
     * Prevent from dependency on android.support.v4.graphics.ColorUtils
     *
     * Convert RGB components to HSL (hue-saturation-lightness).
     * <ul>
     * <li>outHsl[0] is Hue [0 .. 360)</li>
     * <li>outHsl[1] is Saturation [0...1]</li>
     * <li>outHsl[2] is Lightness [0...1]</li>
     * </ul>
     *
     * @param r      red component value [0..255]
     * @param g      green component value [0..255]
     * @param b      blue component value [0..255]
     * @param outHsl 3-element array which holds the resulting HSL components
     */
    private static void RGBToHSL(int r, int g, int b, float[] outHsl) {
        final float rf = r / 255f;
        final float gf = g / 255f;
        final float bf = b / 255f;

        final float max = Math.max(rf, Math.max(gf, bf));
        final float min = Math.min(rf, Math.min(gf, bf));
        final float deltaMaxMin = max - min;

        float h, s;
        float l = (max + min) / 2f;

        if (max == min) {
            // Monochromatic
            h = s = 0f;
        } else {
            if (max == rf) {
                h = ((gf - bf) / deltaMaxMin) % 6f;
            } else if (max == gf) {
                h = ((bf - rf) / deltaMaxMin) + 2f;
            } else {
                h = ((rf - gf) / deltaMaxMin) + 4f;
            }

            s = deltaMaxMin / (1f - Math.abs(2f * l - 1f));
        }

        h = (h * 60f) % 360f;
        if (h < 0) {
            h += 360f;
        }

        outHsl[0] = constrain(h, 0f, 360f);
        outHsl[1] = constrain(s, 0f, 1f);
        outHsl[2] = constrain(l, 0f, 1f);
    }

    private static float constrain(float amount, float low, float high) {
        return amount < low ? low : (amount > high ? high : amount);
    }

    static float getLightness(int color) {
        int red   = Color.red(color);
        int green = Color.green(color);
        int blue  = Color.blue(color);

        float hsl[] = new float[3];
        RGBToHSL(red, green, blue, hsl);
        return hsl[2];
    }

    public static boolean isLightColor(int color) {
        return  getLightness(color) >= 0.5f;
    }
}
