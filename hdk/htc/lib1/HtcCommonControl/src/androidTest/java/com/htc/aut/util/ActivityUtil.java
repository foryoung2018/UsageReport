
package com.htc.aut.util;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ActivityUtil {

    public static final String THEMENAME = "themeName";
    public static final String ORIENTATION = "orientation";
    public static final String FONT = "font";
    public static final String CATEGORYID = "categoryId";
    public static final String DENSITY = "density";

    public static void setDensity(Resources res, int density) {
        if (null == res || 0 >= density) {
            return;
        }

        final Configuration conf = res.getConfiguration();
        conf.densityDpi = density;
        final DisplayMetrics dm = res.getDisplayMetrics();
        dm.densityDpi = density;
        res.updateConfiguration(conf, dm);

        final Class c = android.graphics.Bitmap.class;
        try {
            final Field f = c.getDeclaredField("sDefaultDensity");
            f.setAccessible(true);
            f.setInt(null, density);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void initCategory(Activity act, int categoryId) {
        try {
            final Class c = Class.forName("com.htc.lib1.cc.util.HtcCommonUtil");
            final Method method = c.getDeclaredMethod("initTheme", ContextThemeWrapper.class,
                    int.class);
            method.invoke(null, act, categoryId);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void disableDynamicTheme(Activity act) {
        try {
            final Class c = Class
                    .forName("com.htc.lib1.cc.util.HtcThemeUtils$CommonCategoryResources");
            Field f = c.getDeclaredField("mCategoryRes");
            f.setAccessible(true);
            f.set(null, act.getResources());

            f = c.getDeclaredField("mCategoryTheme");
            f.setAccessible(true);
            f.set(null, act.getTheme());

            final Method method = c.getDeclaredMethod("refreshColorTable");
            method.setAccessible(true);
            method.invoke(null);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static int initTheme(String themeName, Activity act) {
        if (null == themeName) {
            themeName = "HtcDeviceDefault";
        }

        final Resources res = act.getResources();
        final int id = res.getIdentifier(themeName, "style", act.getPackageName());
        if (0 != id) {
            act.setTheme(id);
        }
        return id;
    }
}
