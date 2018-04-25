
package com.htc.lib1.cc.htcFooter.activityhelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.util.HtcCommonUtil;


public class FooterActivity extends ActivityBase {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HtcCommonUtil.initTheme(this, mCategoryId);
        setLayout(this);
    }

    @Override
    protected boolean isInitOrientation() {
        return false;
    }

    private static void setLayout(Activity activity) {
        Intent intent = activity.getIntent();
        int layoutId = intent.getIntExtra("layoutId", 0);
        android.util.Log.d(activity.getClass().getSimpleName(), "themeName = " + activity.getTheme().getClass().getName());
        if (0 != layoutId)
            activity.setContentView(layoutId);
    }

}
