/**
 *
 */
package com.htc.test;

import android.app.Instrumentation;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;

import com.robotium.solo.Solo;

/**
 * @author AMT
 *
 */
public class HtcActivityTestCase extends HtcActivityTestCaseBase {

    @Override
    public void setActivityIntent(Intent i) {
        initTheme(false);
        if (null == i)
            i = new Intent();
        Log.e("HtcTestRunner", "mThemeResId=" + mThemeResId);
        i.putExtra("theme", mThemeResId);

        super.setActivityIntent(i);
    }

    protected int mThemeResId = 0;;

    public HtcActivityTestCase(Class activityClass) {
        super(activityClass);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void initActivity() {
        super.initActivity();
        if (null != mActivity) {
            initTheme(true);
            if (null != mSolo) {
                initOrientation(mSolo);
            }
        }
    }

    private void initTheme(boolean initActivity) {
        Instrumentation i = getInstrumentation();
        if (i instanceof HtcTestRunner) {
            mThemeName = ((HtcTestRunner) i).getThemeName();
            if (null == mThemeName) {
                mThemeName = "HtcDeviceDefault";
            }

            Resources r = i.getTargetContext().getResources();
            mThemeResId = r.getIdentifier(mThemeName, "style", i
                    .getTargetContext().getPackageName());
            if (initActivity) {
                Log.e("HtcTestRunner", "setTheme= " + mThemeName);
                mActivity.setTheme(mThemeResId);

            }
        }
    }

    protected void initOrientation(Solo solo) {
        Instrumentation i = getInstrumentation();
        if (solo != null && i instanceof HtcTestRunner) {
            mOrientation = ((HtcTestRunner) i).getOrientation();
            mFontStyle = ((HtcTestRunner) i).getFontStyle();
            solo.setActivityOrientation(mOrientation);
            Log.d("HtcTestRunner", "setActivityOrientation=" + mOrientation);
        }
    }
}
