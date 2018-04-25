
package com.htc.lib1.cc.textview.test;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.textview.test.util.InputFieldTestUtil;
import com.htc.lib1.cc.widget.ActionBarSearch;
import com.htc.lib1.cc.widget.HtcAutoCompleteTextView;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;

public class AutoCompleteTextViewTest extends HtcActivityTestCaseBase {

    private HtcAutoCompleteTextView autoText;
    public AutoCompleteTextViewTest() throws ClassNotFoundException {
        super(
                Class.forName("com.htc.textView.aut.AutoCompleteTextViewDemo"));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityIntent(null);
        initActivity();
        autoText = (HtcAutoCompleteTextView) mSolo.getView(R.id.dark_input);
        mSolo.setActivityOrientation(getOrientation());
    }

    public void testSetPadding() {
        autoText = new HtcAutoCompleteTextView(mActivity);
        autoText.setPadding(10, 10, 10, 10);
    }

    public void testSetEnabled() {
        autoText = new HtcAutoCompleteTextView(mActivity);
        autoText.setEnabled(true);
        autoText.setMode(HtcAutoCompleteTextView.MODE_BRIGHT_BACKGROUND);
        autoText.setEnabled(false);
        autoText.setMode(HtcAutoCompleteTextView.MODE_DARK_BACKGROUND);
        autoText.setEnabled(false);
    }

    public void testSetSupportMode() {
        ActionBarSearch actionBarSearch = new ActionBarSearch(mActivity, ActionBarSearch.MODE_EXTERNAL);
        actionBarSearch = new ActionBarSearch(mActivity, ActionBarSearch.MODE_AUTOMOTIVE);
    }

    public void testEnableDrop() {
        autoText = new HtcAutoCompleteTextView(mActivity);
        autoText.enableDropDownMinWidth(true);
        autoText.setDropDownHorizontalOffset(1);
        autoText.enableDropDownMinWidth(true);
    }

    public final void test_Bright_NoInput_NoSelect() {
        test(R.id.bright_input, false, false);
    }

    public final void test_Bright_NoInput_Select() {
        test(R.id.bright_input, false, true);
    }

    public final void test_Dark_NoInput_NoSelect() {
        test(R.id.dark_input, false, false);
    }

    public final void test_Dark_NoInput_Select() {
        test(R.id.dark_input, false, true);
    }

    public final void test_Full_NoInput_NoSelect() {
        test(R.id.full_input, false, false);
    }

    public final void test_Full_NoInput_Select() {
        test(R.id.full_input, false, true);
    }

    public final void test_Bright_Input() {
        test(R.id.bright_input, true, false);
    }

    public final void test_Dark_Input() {
        test(R.id.dark_input, true, false);
    }

    public final void test_Full_Input() {
        test(R.id.full_input, true, false);
    }

    public void test_Dark_Min_Height() {
        testMinHeight(R.id.dark_input);
    }

    public void test_Bright_Min_Height() {
        testMinHeight(R.id.bright_input);
    }

    public void test_IME() {
        InputFieldTestUtil.test_IME(getInstrumentation(), autoText, mSolo, this);
    }

    public void test_Hint_LongStr() {
        InputFieldTestUtil.test_Hint_LongStr(getInstrumentation(), autoText, mSolo, this);
    }

    public void test_Hint_SpaceStr() {
        InputFieldTestUtil.test_Hint_SpaceStr(getInstrumentation(), autoText, mSolo, this);
    }

    public void test_Text_LongStr() {
        InputFieldTestUtil.test_Text_LongStr(getInstrumentation(), autoText, mSolo, this);
    }

    public void test_Text_SpaceStr() {
        InputFieldTestUtil.test_Text_SpaceStr(getInstrumentation(), autoText, mSolo, this);
    }

    public void test_Has_Focus() {
        InputFieldTestUtil.test_Has_Focus(getInstrumentation(), autoText, mSolo, this);
    }

    public void test_No_Focus() {
        InputFieldTestUtil.test_No_Focus(getInstrumentation(), autoText, mSolo, this);
    }

    public void test_Input_Type() {
        InputFieldTestUtil.test_Input_Type(getInstrumentation(), autoText, mSolo, this);
    }

    public void test_Select_Full() {
        InputFieldTestUtil.test_Select_Full(getInstrumentation(), autoText, mSolo, this);
    }

    public void testImproveCoverage() {
        HtcAutoCompleteTextView autoText = new HtcAutoCompleteTextView(getInstrumentation()
                .getTargetContext());
        MotionEvent event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 0, 0, 0);
        autoText.onTouchEvent(event);
        event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, 0, 0, 0);
        autoText.onTouchEvent(event);
        event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_MOVE, 0, 0, 0);
        autoText.onTouchEvent(event);

        ColorDrawable draw = new ColorDrawable(Color.RED);
        autoText.setBackground(draw);
        autoText.setDropDownHorizontalOffset(100);
        autoText.setDropDownVerticalOffset(100);
        autoText.setDropDownWidth(100);
        autoText.setMode(HtcAutoCompleteTextView.MODE_BRIGHT_BACKGROUND);
        autoText.setMode(HtcAutoCompleteTextView.MODE_BRIGHT_FULL_BACKGROUND);
        autoText.setMode(HtcAutoCompleteTextView.MODE_DARK_BACKGROUND);
        TestSetDrawableAlpha mTestDrawable = new TestSetDrawableAlpha(mActivity);
    }

    private void testMinHeight(int id){
        final Drawable drawRest = mActivity.getResources().getDrawable(R.drawable.common_inputfield_rest);
        int drawHeight = drawRest.getIntrinsicHeight();
        View widget = mActivity.findViewById(id);
        assertEquals(drawHeight, widget.getHeight());
    }

    private void test(int id, boolean isInput, final boolean isSelected) {
        assertNotNull(mActivity);
        final EditText et = (EditText) mActivity.findViewById(id);
        if (isInput) {
            getInstrumentation().runOnMainSync(new Runnable() {
                public void run() {
                    et.requestFocus();
                    et.setCursorVisible(false);
                }
            });
            getInstrumentation().sendStringSync("aa");
            getInstrumentation().waitForIdleSync();
            String expandItemText = mActivity.getResources().getString(
                    R.string.str_expandItemText);
            mSolo.waitForText(expandItemText);
            View[] multiView = {
                    et,
                    mSolo.getText(expandItemText).getRootView()
            };
            ScreenShotUtil.AssertMultipleViewEqualBefore(mSolo, multiView, this,
                    getInstrumentation());
            mSolo.clickOnText(expandItemText);
        } else {
            getInstrumentation().waitForIdleSync();
            getInstrumentation().runOnMainSync(new Runnable() {
                public void run() {
                    et.setCursorVisible(false);
                }
            });
            if (isSelected) {
                mSolo.clickOnView(et);
            }
            ScreenShotUtil.AssertViewEqualBefore(mSolo, et, this);
        }
    }

    private class TestSetDrawableAlpha extends HtcAutoCompleteTextView {
        public TestSetDrawableAlpha(Context context) {
            super(context);
            try{
                setDrawableAlpha(1);
            }catch(NullPointerException e){
                Log.i("TestSetDrawableAlpha", "Null");
            }
        }
    }
}
