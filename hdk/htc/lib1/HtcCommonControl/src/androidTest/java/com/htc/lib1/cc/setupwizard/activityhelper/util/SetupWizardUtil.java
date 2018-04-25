package com.htc.lib1.cc.setupwizard.activityhelper.util;

import android.app.Activity;
import android.content.Intent;

import com.htc.aut.util.ActivityUtil;

public class SetupWizardUtil {

    public static void initThemeAndCategory(Activity act) {

        String themeName = null;
        int categoryId = 0;

        if (null != act) {
            final Intent i = act.getIntent();
            if (null != i) {
                themeName = i.getStringExtra(ActivityUtil.THEMENAME);
                categoryId = i.getIntExtra(ActivityUtil.CATEGORYID, 0);
            }
        }

        ActivityUtil.initTheme(themeName, act);
        ActivityUtil.initCategory(act, categoryId);
    }
}
