package com.htc.lib1.cc.view.tabbar;

import java.lang.reflect.Method;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.ActionBarUtil;
import com.htc.lib1.cc.util.HtcCommonUtil;


/**
 * @hide
 * @deprecated [Module internal use]
 */
public class TabBarUtils {
    public static final String TAG = "TabBar";

    private static ScreenInfo screenInfo;

    /**
     * @hide
     * @deprecated [Module internal use]
     */
    private static class ScreenInfo {
        private Display display;
        private float ratio;

        private ScreenInfo(Context ctx) {
            this(ctx, ctx.getResources().getDisplayMetrics());
        }

        private ScreenInfo(Context ctx, DisplayMetrics dm) {
            display = ((WindowManager) ctx.getSystemService (android.content.Context.WINDOW_SERVICE)).getDefaultDisplay();

            //xxhdpi is baseline in UIGL
            ratio = (float)dm.densityDpi / DisplayMetrics.DENSITY_XXHIGH;

            Log.d(TAG, "[screen info] density=" + dm.density + " dpi=" + dm.densityDpi + " " + dm.widthPixels + "x" + dm.heightPixels + " " + dm.xdpi + "x" + dm.ydpi);
            Log.d(TAG, "[screen info] ratio=" + ratio);
        }

        private int scale(int portraitValue, int landscapeValue) {
            return scale(value(portraitValue, landscapeValue));
        }

        private int scale(int baseValue) {
            int value = (int)(ratio*baseValue);
            if (value%2 == 1) {
                return value+1;
            }
            return value;
        }

        private int value(int portraitValue, int landscapeValue) {
            int baseValue = portraitValue;
            int r = display.getRotation();
            if (r == Surface.ROTATION_0 || r == Surface.ROTATION_180) {
                baseValue = portraitValue;
            } else {
                baseValue = landscapeValue;
            }

            return baseValue;
        }
    }

    private static ScreenInfo getScreenInfo(Context ctx) {
        if(screenInfo == null) {
            screenInfo = new ScreenInfo(ctx);
        }
        return screenInfo;
    }

    public static int value(Context ctx, int portraitValue, int landscapeValue) {
        return getScreenInfo(ctx).value(portraitValue, landscapeValue);
    }

    public static int scale(Context ctx, int portraitValue, int landscapeValue) {
        return getScreenInfo(ctx).scale(portraitValue, landscapeValue);
    }



    /**
     * @hide
     * @deprecated [Module internal use]
     */
    public static class dimen {
        public static int m1(Context ctx) {
            return ctx.getResources().getDimensionPixelSize(R.dimen.margin_l);
        }
        public static int m2(Context ctx) {
            return ctx.getResources().getDimensionPixelSize(R.dimen.margin_m);
        }
        public static int m3(Context ctx) {
            return ctx.getResources().getDimensionPixelSize(R.dimen.margin_s);
        }
        public static int headerHeight(Context ctx, boolean automotive) {
            return ActionBarUtil.getActionBarHeight(ctx, automotive);
        }
        public static int height(Context ctx, boolean automotive) {
            if (automotive) {
                return ctx.getResources().getDimensionPixelOffset(R.dimen.tabbar_height_car);
            } else {
                return ctx.getResources().getDimensionPixelOffset(R.dimen.tabbar_height);
            }
        }
        public static int indicatorThickness(Context ctx, boolean automotive) {
            if (automotive) {
                return ctx.getResources().getDimensionPixelOffset(R.dimen.tab_indicator_height_car);
            } else {
                return ctx.getResources().getDimensionPixelOffset(R.dimen.tab_indicator_height);
            }
        }
    }

    /**
     * @hide
     * @deprecated [Module internal use]
     */
    public static class drawable {
        public static Drawable headerBackground(Context context) {
            return new ColorDrawable(HtcCommonUtil.getCommonThemeColor(
            		context, R.styleable.ThemeColor_multiply_color));
        }
        public static Drawable background(Context context) {
            Resources res = context.getResources();
            return res.getDrawable(R.color.list_item_bg_bottom_color);
        }
        public static Drawable popupDivider(Context context) {
            Resources res = context.getResources();
            return res.getDrawable(R.drawable.common_tab_div);
        }
        public static Drawable darkTextSeletor(Context context) {
            Resources res = context.getResources();
            return res.getDrawable(R.drawable.list_selector_dark);
        }
        public static Drawable lightTextSeletor(Context context) {
            Resources res = context.getResources();
            return res.getDrawable(R.drawable.list_selector_light);
        }
    }

    /**
     * @hide
     * @deprecated [Module internal use]
     */
    public static class color {
        public static int overlay(Context context) {
            return HtcCommonUtil.getCommonThemeColor(
            		context, R.styleable.ThemeColor_overlay_color);
        }
        public static int multiply(Context context) {
            return HtcCommonUtil.getCommonThemeColor(
            		context, R.styleable.ThemeColor_multiply_color);
        }
        public static int category(Context context) {
            //category_color
            return HtcCommonUtil.getCommonThemeColor(
            		context, R.styleable.ThemeColor_category_color);
        }
        public static int categoryLight(Context context) {
            return HtcCommonUtil.getCommonThemeColor(
            		context, R.styleable.ThemeColor_light_category_color);
        }
        public static int backgroundLight(Context context) {
            return context.getResources().getColor(R.color.ap_background_color);
        }
        public static int landscapeBackground(Context context) {
            return context.getResources().getColor(R.color.list_item_bg_bottom_color);
        }
        public static int portriatTextColor(Context context) {
            return context.getResources().getColor(R.color.dark_primaryfont_color);
        }
        public static int landscapeTextColor(Context context) {
            return context.getResources().getColor(R.color.tabfont_color);
        }
        public static int backgroundColor(Context context, boolean automotive) {
            return TabBarUtils.color.multiply(context);
        }
    }
    /**
     * @hide
     * @deprecated [Module internal use]
     */
    public static class trace {
        private static boolean notSupport = true;
        private static Method begin;
        private static Method end;

        static {
            try {
                Class clazz = TabBarUtils.class.getClassLoader().loadClass("android.os.Trace");
                begin = clazz.getDeclaredMethod("traceBegin", long.class, String.class);
                end = clazz.getDeclaredMethod("traceEnd", long.class);
                notSupport = !Log.isLoggable(TAG, Log.VERBOSE);
            } catch (Throwable e) {
                Log.d(TAG, "", e);
            }
        }

        public static void begin(String name){
            if (notSupport) {
                return;
            }
            try{
                //Trace.traceBegin(Trace.TRACE_TAG_VIEW, name)
                begin.invoke(null, (1L << 3), name);
            } catch (Throwable e) {
                Log.d(TAG, "", e);
            }
        }
        public static void end(){
            if (notSupport) {
                return;
            }
            try{
                //Trace.traceEnd(Trace.TRACE_TAG_VIEW)
                end.invoke(null, (1L << 3));
            } catch (Throwable e) {
                Log.d(TAG, "", e);
            }
        }
        public static void label(String name){
            if (notSupport) {
                return;
            }
            try{
                begin.invoke(null, (1L << 3), name);
                end.invoke(null, (1L << 3));
            } catch (Throwable e) {
                Log.d(TAG, "", e);
            }
        }
    }
}
