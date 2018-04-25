package com.htc.test;

import android.app.Activity;
import android.content.Intent;
import android.os.BatteryManager;
import android.provider.Settings;
import android.test.ActivityInstrumentationTestCase2;

import com.htc.test.util.ApplicationUtils;
import com.htc.test.util.ScreenShotUtil;
import com.robotium.solo.Solo;

/**
 * @author BiWei
 */
public class HtcActivityTestCaseBase extends ActivityInstrumentationTestCase2 {

    protected String mThemeName = null;
    protected int mOrientation = Solo.PORTRAIT;
    protected String mFontStyle = null;
    protected int mCategoryId = 0;
    protected Activity mActivity;
    protected Solo mSolo;
    protected int mDensity = 0;

    @Override
    public void setActivityIntent(Intent i) {
        if (null == i)
            i = new Intent();
        i.putExtra("themeName", mThemeName);
        i.putExtra("orientation", mOrientation);
        i.putExtra("fontStyle", mFontStyle);
        i.putExtra("categoryId", mCategoryId);
        i.putExtra("density", mDensity);

        super.setActivityIntent(i);
    }

    protected boolean isInitOrientation() {
        return true;
    }

    /**
     * Call this method after call to setActivityIntent(Intent i) to inject a
     * customized intent into the Activity under test
     */
    public void initActivity() {
        mActivity = getActivity();
        if (null != mActivity) {
            mSolo = new Solo(super.getInstrumentation(), mActivity);
            if ( isInitOrientation() ) {
                mSolo.setActivityOrientation(getOrientation());
                getInstrumentation().waitForIdleSync();
            }
        }
    }

    public HtcActivityTestCaseBase(Class activityClass) {
        super(activityClass);

    }

    protected void setUp() throws Exception {
        super.setUp();

        initValues();

        ScreenShotUtil.initSnapshotService(getInstrumentation().getContext());
        ScreenShotUtil.EXTRA_INFO = null;
        ScreenShotUtil.setOrientationMark(true);
        ScreenShotUtil.setThemeMask(true);

        Settings.System.putInt(getInstrumentation().getContext()
                .getContentResolver(),
                Settings.System.STAY_ON_WHILE_PLUGGED_IN,
                BatteryManager.BATTERY_PLUGGED_AC
                        | BatteryManager.BATTERY_PLUGGED_USB);

        // Hold wakelock
        ApplicationUtils.acquireWakeLock(getInstrumentation().getContext());

        // Unlock device
        ApplicationUtils.unlockDevice(getInstrumentation().getContext());
    }

    public void initValues() {
        HtcTestRunner i = (HtcTestRunner) getInstrumentation();
        mThemeName = i.getThemeName();
        mOrientation = i.getOrientation();
        mFontStyle = i.getFontStyle();
        mCategoryId = i.getCategoryId();
        mDensity = i.getDensity();
    }

    public String getThemeName() {
        return mThemeName;
    }

    public int getOrientation() {
        return mOrientation;
    }

    public String getFontStyle() {
        return mFontStyle;
    }

    public int getCategoryId() {
        return mCategoryId;
    }

    public int getDensity() {
        return mDensity;
    }

    protected void tearDown() throws Exception {

        ScreenShotUtil.freeSnapshotService(getInstrumentation().getContext());
        ScreenShotUtil.EXTRA_INFO = null;
        ScreenShotUtil.setOrientationMark(true);
        ScreenShotUtil.setThemeMask(true);

        if (!mActivity.isFinishing()) {
            mActivity.finish();
        }
        mActivity = null;
        if (null != mSolo) {
            mSolo.finishOpenedActivities();
            try {
                mSolo.finalize();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        mSolo = null;

        // Release wakelock
        ApplicationUtils.releaseWakeLock();

        super.tearDown();
    }
}
