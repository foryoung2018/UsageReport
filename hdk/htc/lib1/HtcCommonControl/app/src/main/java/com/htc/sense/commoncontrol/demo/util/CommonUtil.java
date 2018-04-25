
package com.htc.sense.commoncontrol.demo.util;

import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.util.HtcCommonUtil.ObtainThemeListener;
import com.htc.lib1.cc.widget.ActionBarDropDown;
import com.htc.lib1.cc.widget.ActionBarExt;
import com.htc.lib1.cc.widget.ActionBarText;
import com.htc.sense.commoncontrol.demo.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

public class CommonUtil {
    private static final String TAG = "CommonUtil";

    public static final String EXTRA_THEME_KEY = "theme_id";
    public static final String EXTRA_CATEGORY_KEY = "category_id";
    public static final String EXTRA_THEME_BUNDLE_KEY = "theme_bundle";

    public static final int MODE_LIGHT = 0;
    public static final int MODE_DARK = 1;

    public static void reloadDemoTheme(final Activity activity, final int themeId, int categoryId) {
        activity.setTheme(themeId);
        HtcCommonUtil.initTheme(activity, categoryId);
    }

    /**
     * accord to themeId, reload right theme for the activity.
     *
     * @param activity
     * @param themeId
     */
    public static void reloadDemoTheme(Activity activity, int themeId) {
        reloadDemoTheme(activity, themeId, HtcCommonUtil.BASELINE);
    }

    public static Bundle applyDemoTheme(Activity activity, Bundle savedInstanceState) {
        int themeId = R.style.HtcDeviceDefault;
        int categoryId = HtcCommonUtil.BASELINE;
        Bundle themeBundle = null;
        if (savedInstanceState != null) {
            themeBundle = savedInstanceState.getBundle(EXTRA_THEME_BUNDLE_KEY);
            if (themeBundle != null) {
                themeId = themeBundle.getInt(CommonUtil.EXTRA_THEME_KEY, R.style.HtcDeviceDefault);
                categoryId = themeBundle.getInt(CommonUtil.EXTRA_CATEGORY_KEY, HtcCommonUtil.BASELINE);
            }
        }
        if (activity != null && activity.getIntent() != null) {
            Bundle intentBundle = activity.getIntent().getBundleExtra(EXTRA_THEME_BUNDLE_KEY);
            if (intentBundle != null) {
                themeId = intentBundle.getInt(CommonUtil.EXTRA_THEME_KEY);
                categoryId = intentBundle.getInt(CommonUtil.EXTRA_CATEGORY_KEY);
            }
        }

        reloadDemoTheme(activity, themeId, categoryId);

        if (themeBundle == null) themeBundle = new Bundle();
        themeBundle.putInt(EXTRA_THEME_KEY, themeId);
        themeBundle.putInt(EXTRA_CATEGORY_KEY, categoryId);
        return themeBundle;
    }

    /**
     * @deprecated please use {@link #applyDemoTheme(Activity, Bundle)} instead.
     */
    public static int reloadDemoTheme(Activity activity, Bundle savedInstanceState) {
        Bundle themeBundle = applyDemoTheme(activity, savedInstanceState);
        return themeBundle.getInt(CommonUtil.EXTRA_THEME_KEY);
    }

    /**
     * @deprecated just to demo
     */
    public static void setThemeListener(final Activity activity) {
        HtcCommonUtil.setObtianThemeListener(new ObtainThemeListener() {
            @Override
            public int onObtainThemeColor(int index, int themeColor) {
                TypedArray typedArray = activity.getTheme().obtainStyledAttributes(R.styleable.ThemeColor);
                int retColor = typedArray.getColor(index, themeColor);
                typedArray.recycle();
                return retColor;
            }
        });
    }

    public static ActionBarExt initHtcActionBar(final Activity activity, boolean enableBackup, boolean enableTitle) {
        ActionBar actionBar = activity.getActionBar();
        if (actionBar == null) return null;

        ActionBarExt actionBarExt = new ActionBarExt(activity, actionBar);
        actionBarExt.setBackgroundDrawable(new ColorDrawable(HtcCommonUtil.getCommonThemeColor(activity, R.styleable.ThemeColor_multiply_color)));

        if (enableBackup) {
            actionBarExt.getCustomContainer().setBackUpEnabled(true);
            actionBarExt.getCustomContainer().setBackUpOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    activity.onBackPressed();
                }
            });
        }
        if (enableTitle && !TextUtils.isEmpty(activity.getTitle())) {
            updateCommonTitle(activity, actionBarExt, null, activity.getTitle());
        }
        return actionBarExt;
    }

    public static ActionBarDropDown updateCommonTitle(Context context, ActionBarExt actionBarExt, ActionBarDropDown actionBarDropDown, CharSequence title) {
        if (context == null || actionBarExt == null) return null;

        ActionBarDropDown dropDown = actionBarDropDown;
        if (actionBarDropDown == null) {
            dropDown = new ActionBarText(context);
            actionBarExt.getCustomContainer().addCenterView(dropDown);
        }
        dropDown.setPrimaryText(resolveCommonTitle(title.toString()));
        return dropDown;
    }

    public static String resolveCommonTitle(String title) {
        if (TextUtils.isEmpty(title)) return null;

        int lastIndex = title.lastIndexOf("/");
        if (lastIndex > 0) {
            title = title.substring(lastIndex + 1);
        }
        return title;
    }

    /**
     * apply style for ListView
     *
     * @param listView The ListView which should be applied style
     * @param style The Style Which ListView should apply,such as HTCLISTVIEW_STYLE_LIGHT or
     *            HTCLISTVIEW_STYLE_DARK
     */
    public static void applyHtcListViewStyle(ListView listView, int style) {
        if (MODE_LIGHT == style) {
            applyHtcListViewStyle(listView, com.htc.lib1.cc.R.drawable.inset_list_divider, com.htc.lib1.cc.R.drawable.list_selector_light, Color.WHITE);
        } else if (MODE_DARK == style) {
            applyHtcListViewStyle(listView, com.htc.lib1.cc.R.drawable.inset_list_divider_dark, com.htc.lib1.cc.R.drawable.list_selector_dark, Color.BLACK);
        } else {
            Log.e(TAG, "The style is woring,it should be HTCLISTVIEW_STYLE_LIGHT or HTCLISTVIEW_STYLE_DARK", new Exception());
        }
    }

    private static void applyHtcListViewStyle(ListView listView, int dividerId, int selectorId, int backgroundColor) {
        if (null == listView) {
            Log.e(TAG, "listView cannot be null", new Exception());
            return;
        }
        final Resources res = listView.getResources();
        if (null == res) {
            Log.e(TAG, "listView.getResources() cannot be null", new Exception());
            return;
        }
        listView.setDivider(res.getDrawable(dividerId));
        listView.setSelector(selectorId);
        listView.setBackgroundColor(backgroundColor);
    }

    public static int getApBackgroundColor(Context context, int mode) {
        if (mode == MODE_DARK) {
            return HtcCommonUtil.getCommonThemeColor(context, com.htc.lib1.cc.R.styleable.ThemeColor_dark_ap_background_color);
        } else {
            return HtcCommonUtil.getCommonThemeColor(context, com.htc.lib1.cc.R.styleable.ThemeColor_ap_background_color);
        }
    }
}
