package com.htc.lib1.cc.setupwizard.test.util;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.htc.test.util.ScreenShotUtil;
import com.robotium.solo.Solo;

import java.util.ArrayList;

public class SetupWizardTestUtil {

    private static String packageName;

    public static void testDescriptTextSize(Activity act, Solo solo,
            ActivityInstrumentationTestCase2 testCase, String viewName) {
        TextView mDesc = (TextView) solo.getView(viewName);
        packageName = testCase.getInstrumentation().getTargetContext()
                .getPackageName();
        int resID = act.getResources().getIdentifier("list_body_primary_m",
                "dimen", packageName);
        int fontSize = act.getResources().getDimensionPixelSize(resID);
        testCase.assertEquals(fontSize, (int) mDesc.getTextSize());
    }

    public static void testSubTitleTextSize(Activity act, Solo solo,
            ActivityInstrumentationTestCase2 testCase, String viewName) {
        TextView mTip = (TextView) solo.getView(viewName);
        packageName = testCase.getInstrumentation().getTargetContext()
                .getPackageName();
        int resID = act.getResources().getIdentifier(
                "fixed_list_body_primary_s", "dimen", packageName);
        int fontSize = act.getResources().getDimensionPixelOffset(resID);
        testCase.assertEquals(fontSize, (int) mTip.getTextSize());
    }

    public static void testImageMarginMeasure(Activity act, Solo solo,
            ActivityInstrumentationTestCase2 testCase, String viewName) {
        hideScrollView(solo);
        ImageView mImage = (ImageView) solo.getView(viewName);
        packageName = testCase.getInstrumentation().getTargetContext()
                .getPackageName();
        int mMargin_l_ID = act.getResources().getIdentifier("margin_l",
                "dimen", packageName);
        int mMargin_l = act.getResources().getDimensionPixelSize(mMargin_l_ID);
        MarginLayoutParams mlp = (MarginLayoutParams) mImage
                .getLayoutParams();
        ScreenShotUtil.AssertViewEqualBefore(solo, mImage.getRootView(),
                testCase);
        testCase.assertEquals(mMargin_l, mlp.topMargin);
        testCase.assertEquals(mMargin_l, mlp.bottomMargin);
    }

    public static void testDescriptMarginMeasure(Activity act, Solo solo,
            ActivityInstrumentationTestCase2 testCase, String viewName) {
        packageName = testCase.getInstrumentation().getTargetContext()
                .getPackageName();
        int mSpacing_2_ID = act.getResources().getIdentifier("spacing_2",
                "dimen", packageName);
        int mMargin_l_ID = act.getResources().getIdentifier("margin_l",
                "dimen", packageName);
        int mMargin_xs_2_ID = act.getResources().getIdentifier("margin_xs_2",
                "dimen", packageName);
        int mSpacing_2 = act.getResources()
                .getDimensionPixelSize(mSpacing_2_ID);
        int mMargin_l = act.getResources().getDimensionPixelSize(mMargin_l_ID);
        int mMargin_xs_2 = act.getResources().getDimensionPixelSize(
                mMargin_xs_2_ID);
        TextView mDesc = (TextView) solo.getView("desc");
        ViewGroup parent = (ViewGroup) mDesc.getParent();
        testCase.assertEquals(mSpacing_2, mDesc.getPaddingTop());
        testCase.assertEquals(mMargin_xs_2, mDesc.getPaddingBottom());
        testCase.assertEquals(mMargin_l, parent.getPaddingLeft());
        testCase.assertEquals(mMargin_l, parent.getPaddingRight());
    }

    public static void hideScrollView(Solo solo) {
        ArrayList<View> list = solo.getViews();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            View v = list.get(i);
            if (v instanceof ScrollView) {
                ((ScrollView) v).setVerticalScrollBarEnabled(false);
            }
        }
    }
}
