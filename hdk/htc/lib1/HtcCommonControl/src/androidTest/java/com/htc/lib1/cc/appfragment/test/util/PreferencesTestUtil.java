
package com.htc.lib1.cc.appfragment.test.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.Resources;
import android.preference.DialogPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.TwoStatePreference;
import android.preference.Preference.OnPreferenceClickListener;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.htc.lib1.cc.widget.preference.HtcPreferenceActivity;
import com.htc.test.util.ScreenShotUtil;
import com.robotium.solo.Solo;

public class PreferencesTestUtil {
    private static View mView;
    private static TwoStatePreference mTwoStatePreference;
    private static TextView mTitle;

    public static void scrollToAndScreenShotTest(ActivityInstrumentationTestCase2 testCase, final int itemIndex, Solo solo) {
        final ListView hlv = (ListView) testCase.getActivity().findViewById(android.R.id.list);
        final View v = hlv.getChildAt(itemIndex);
        ScreenShotUtil.AssertViewEqualBefore(solo, v, testCase);
    }

    public static void scrollToAndScreenShotTest(final int itemIndex,
            ActivityInstrumentationTestCase2 testCase, final boolean isChecked, String tag, Solo solo, Activity act) {
        if (null != tag) {
            mTwoStatePreference = (TwoStatePreference) ((HtcPreferenceActivity) act).findPreference(tag);
        }

        final ListView hlv = (ListView) act.findViewById(android.R.id.list);
        testCase.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                hlv.setSelection(itemIndex - 1);
                if (null != mTwoStatePreference) {
                    mTwoStatePreference.setChecked(isChecked);
                }
            }
        });

        solo.sleep(3000);
        final View v = hlv.getChildAt(itemIndex);

        ScreenShotUtil.AssertViewEqualBefore(solo, v, testCase);
    }

    public static void showDialogAndScreenShotTest(String key, final int intemIndex, ActivityInstrumentationTestCase2 testCase, final Activity act, Solo solo) {
        final HtcPreferenceActivity activity = (HtcPreferenceActivity) act;
        final DialogPreference dialog = (DialogPreference) activity.findPreference(key);
        dialog.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                mView = ((DialogPreference) preference).getDialog().getWindow().getDecorView();
                hideScrollView(((DialogPreference) preference).getDialog(), act.getResources());
                return false;
            }
        });

        if (key == "htc_edittext_preference") {
            final EditTextPreference editTextPreference = (EditTextPreference) activity.findPreference(key);
            editTextPreference.getEditText().setCursorVisible(false);
        }

        solo.clickInList(intemIndex);
        solo.waitForDialogToOpen(5000);
        ScreenShotUtil.AssertViewEqualBefore(solo, mView, testCase);
    }

    public static void scrollToCodeAndScreenShotTest(final int itemIndex,
            ActivityInstrumentationTestCase2 testCase, Solo solo, Activity act) {
        final ListView hlv = (ListView) act.findViewById(android.R.id.list);
        final View child = hlv.getChildAt(itemIndex);
        if (null != child) {
            mTitle = (TextView) child.findViewById(android.R.id.title);
        }
        testCase.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                hlv.setSelection(itemIndex);
                if (null != mTitle) {
                    mTitle.setEllipsize(null);
                }
            }
        });
        solo.sleep(3000);
        final View v = hlv.getSelectedView();

        ScreenShotUtil.AssertViewEqualBefore(solo, v, testCase);
    }

    private static void hideScrollView(Dialog dialog, Resources res) {
        if (null == dialog) {
            return;
        }

        final int listId = res.getIdentifier("select_dialog_listview", "id", "android");
        final View v = (View) dialog.findViewById(listId);
        if (null != v) {
            v.setVerticalScrollBarEnabled(false);
        }
    }
}
