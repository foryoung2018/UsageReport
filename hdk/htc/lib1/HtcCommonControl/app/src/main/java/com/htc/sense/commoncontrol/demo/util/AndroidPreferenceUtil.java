package com.htc.sense.commoncontrol.demo.util;

import android.app.Activity;
import android.content.Intent;

import com.htc.sense.commoncontrol.demo.util.CommonUtil;

public class AndroidPreferenceUtil {

    public static void setAndroidTheme(Activity activity) {
        if (null == activity)
            return;
        Intent intent = activity.getIntent();
        int themeID = activity.getResources().getIdentifier("HtcDeviceDefault.DevelopDontUse", "style", "com.htc.sense.commoncontrol.demo");
        if (0 != themeID) {
            activity.setTheme(themeID);
        } else if (null != intent) {
            CommonUtil.reloadDemoTheme(activity, null);
        }
    }
}
