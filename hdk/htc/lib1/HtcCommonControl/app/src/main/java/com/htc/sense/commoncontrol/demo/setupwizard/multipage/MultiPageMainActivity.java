package com.htc.sense.commoncontrol.demo.setupwizard.multipage;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.util.CommonUtil;
import com.htc.lib1.cc.widget.ActionBarExt;
import com.htc.lib1.cc.widget.setupwizard.HtcWizardActivity;
import com.htc.lib1.cc.widget.setupwizard.WizardConstants;

/*
 * This is the multiple page control center
 */
public class MultiPageMainActivity extends HtcWizardActivity {
    private static final String TAG = "MultiPageMainActivity";

    private static String mActivityActionList[] =
        {"com.htc.sense.intent.action.custom.setupwizard.multipageactivity1","com.htc.sense.intent.action.custom.setupwizard.multipageactivity2",
         "com.htc.sense.intent.action.custom.setupwizard.multipageactivity3","com.htc.sense.intent.action.custom.setupwizard.multipageactivity4",
         "com.htc.sense.intent.action.custom.setupwizard.multipageactivity5"};
    private static final int ACTIVITY_ID[] = {101, 102, 103, 104, 105};
    private int mPageSize = mActivityActionList.length;

    private final static int DEFAULT_VALUE = -1;
    private int mnCurStep = DEFAULT_VALUE;

    private int mPrevRequestCode = -1;

    // page slide transition
    private boolean mbBack = false;
    private boolean mIsFirstTransition = true;

    private boolean mBackToPrevious = false;
    private Bundle mThemeBundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThemeBundle = CommonUtil.applyDemoTheme(this, savedInstanceState);
        CommonUtil.initHtcActionBar(this, true, true);

        hideProgress(true);
        hideBackBtn(true);
        hideNextBtn(true);

        Log.d(TAG, "mPageSize = " + mPageSize);
        nextActivity(true);
   }

    private void executeCurrentActivity()
    {
        Bundle options = null;
        // mnCurStep > 0, to skip first page to keep default zoom animation on
        // first page
        if (!mIsFirstTransition || mnCurStep > 0) {
            if (mIsFirstTransition) {
                mIsFirstTransition = false;
            }
            options = getAnimationOptions(this, mbBack);
        }

        // add boundary check before execute the activity
        if(mnCurStep >= 0 && mnCurStep < mPageSize) {
            Intent intent = new Intent(mActivityActionList[mnCurStep]);
            intent.putExtra(CommonUtil.EXTRA_THEME_BUNDLE_KEY, mThemeBundle);
            startActivityForResult(intent, ACTIVITY_ID[mnCurStep], options);
        }
        else {
            Log.w(TAG, "!!! Index out of bound: mnCurStep = " + mnCurStep);
            finish();
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int nRequestCode, Bundle options)
    {
        Log.i(TAG,"startActivityForResult nRequestCode == " + nRequestCode +
                  ", mnCurStep == " + mnCurStep);
            try {
                super.startActivityForResult(intent, nRequestCode, options);
            } catch (ActivityNotFoundException e) {
                Log.w(TAG, "ActivityNotFoundException, nRequestCode = " + nRequestCode);
                nextActivity(!mBackToPrevious);
            }
            addProgressBarExtra(intent);
    }

    @Override
    protected void onActivityResult(int nRequestCode, int nResultCode, Intent intent)
    {
        Log.i(TAG, "onActivityResult: requestCode = " + nRequestCode + ", resultCode = " + nResultCode );

        // prevent the dual re-launched page
        if(nRequestCode == mPrevRequestCode) {
            Log.d(TAG, "skip dual page: " + mPrevRequestCode);
            return;
        }
        else {
            mPrevRequestCode = nRequestCode;
        }

        mbBack = (nResultCode == WizardConstants.RESULT_BACK_KEY);

        if(nResultCode == Activity.RESULT_CANCELED) {
            Log.w(TAG, "Activity RESULT_CANCELED");
            nextActivity(true);
            return;
        }

        switch (nResultCode) {
            default:
                if (WizardConstants.RESULT_BACK_KEY == nResultCode) {
                    nextActivity(false);
                } else {
                    nextActivity(true);
                }
                break;
        }
    }

    protected void nextActivity(boolean bNext)
    {
        synchronized(this) {
            mBackToPrevious = !bNext;
            if (bNext) {
                mnCurStep++;
            }
            else {
                mnCurStep--;
                if (mnCurStep < 0) {
                    Log.d(TAG, "press back key and reach to first page");
                    finish();
                    return;
                }
            }

            if (mnCurStep >= 0 && mnCurStep < mPageSize) {
//                Log.v(TAG, "package name = " + mActivityList[mnCurStep][0]);
//                Log.v(TAG, "class name = " + mActivityList[mnCurStep][1]);
//                Log.v(TAG, "Id = " + ACTIVITY_ID[mnCurStep]);

                executeCurrentActivity();
            }
            else {
                // index out of bound
                Log.d(TAG, "mnCurStep = "+ mnCurStep + ", mActivityList length = " + mPageSize);
                finish();
            }
        }
    }

    private void addProgressBarExtra(Intent intent) {
        int progressBarNumber = mnCurStep + 1;
        intent.putExtra(WizardConstants.INTENT_STRING_PROGRESS_BAR_NUMBER, progressBarNumber);
        intent.putExtra(WizardConstants.INTENT_STRING_PROGRESS_BAR_MAX_NUMBER, mPageSize);

        Log.v(TAG, " setProgressBar " +
                   " ProgressBarNumber = " + progressBarNumber +
                   " ProgressBarMaxNumber = " + mPageSize);
    }

    /** get custom animation activity options */
    public static Bundle getAnimationOptions(Context context, boolean bBack) {
        if (bBack) {
            return ActivityOptions.makeCustomAnimation(context, R.anim.wizard_slide_in_left, R.anim.wizard_slide_out_right).toBundle();
        } else {
            return ActivityOptions.makeCustomAnimation(context, R.anim.wizard_slide_in_right, R.anim.wizard_slide_out_left).toBundle();
        }
    }
}
