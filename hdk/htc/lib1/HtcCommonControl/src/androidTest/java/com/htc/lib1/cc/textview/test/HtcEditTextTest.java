
package com.htc.lib1.cc.textview.test;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.htc.lib1.cc.widget.HtcEditText;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;
import com.htc.lib1.cc.textview.activityhelper.HtcEditTextDemo;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.textview.test.util.InputFieldTestUtil;

public class HtcEditTextTest extends HtcActivityTestCaseBase {

    private HtcEditText editText;

    public HtcEditTextTest() {
        super(HtcEditTextDemo.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityIntent(null);
        initActivity();
        editText = (HtcEditText) mSolo.getView(R.id.dark_input);
        mSolo.setActivityOrientation(getOrientation());
    }

    private void testEditTextView(int id) {
        final EditText et = (EditText) mActivity.findViewById(id);
        getInstrumentation().runOnMainSync(new Runnable(){
            public void run(){
                et.requestFocus();
                et.setCursorVisible(false);
            }
        });
        getInstrumentation().sendStringSync("aa");
        getInstrumentation().waitForIdleSync();

    }

    public final void testOnCreateBundle() {
        getInstrumentation().waitForIdleSync();
        assertNotNull(mActivity);

        testEditTextView(R.id.bright_input);
        testEditTextView(R.id.dark_input);

    }

    public void test_Bright_Init() {
        test(R.id.bright_input, false);
    }

    public void test_Dark_Init() {
        test(R.id.dark_input, false);
    }

    public void test_Full_Init() {
        test(R.id.full_input, false);
    }

    public void test_Bright_Focus() {
        test(R.id.bright_input, true);
    }

    public void test_Dark_Focus() {
        test(R.id.dark_input, true);
    }

    public void test_Full_Focus() {
        test(R.id.full_input, true);
    }

    public void test_Disable_Focus() {
        testDisable(true);
    }

    public void test_Disable_Init() {
        testDisable(false);
    }

    public void test_Dark_Min_Height() {
        testMinHeight(R.id.dark_input);
    }

    public void test_Bright_Min_Height() {
        testMinHeight(R.id.bright_input);
    }

    public void test_IME() {
        InputFieldTestUtil.test_IME(getInstrumentation(), editText, mSolo, this);
    }

    public void test_Hint_LongStr() {
        InputFieldTestUtil.test_Hint_LongStr(getInstrumentation(), editText, mSolo, this);
    }

    public void test_Hint_SpaceStr() {
        InputFieldTestUtil.test_Hint_SpaceStr(getInstrumentation(), editText, mSolo, this);
    }

    public void test_Text_LongStr() {
        InputFieldTestUtil.test_Text_LongStr(getInstrumentation(), editText, mSolo, this);
    }

    public void test_Text_SpaceStr() {
        InputFieldTestUtil.test_Text_SpaceStr(getInstrumentation(), editText, mSolo, this);
    }

    public void test_Has_Focus() {
        InputFieldTestUtil.test_Has_Focus(getInstrumentation(), editText, mSolo, this);
    }

    public void test_No_Focus() {
        InputFieldTestUtil.test_No_Focus(getInstrumentation(), editText, mSolo, this);
    }

    public void test_Input_Type() {
        InputFieldTestUtil.test_Input_Type(getInstrumentation(), editText, mSolo, this);
    }

    public void test_Select_Full() {
        InputFieldTestUtil.test_Select_Full(getInstrumentation(), editText, mSolo, this);
    }

    public void testImproveCoverage() {
        HtcEditText et = new HtcEditText(getInstrumentation().getTargetContext());
        MotionEvent event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 0, 0, 0);
        et.onTouchEvent(event);
        event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, 0, 0, 0);
        et.onTouchEvent(event);
        event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_MOVE, 0, 0, 0);
        et.onTouchEvent(event);
        ColorDrawable draw = new ColorDrawable(Color.RED);
        et.setBackground(draw);
        et.setBackgroundColor(Color.GREEN);
        et.setEnabled(false);
        et.setEnabled(true);
        et.setMode(HtcEditText.MODE_BRIGHT_BACKGROUND);
        et.setMode(HtcEditText.MODE_BRIGHT_FULL_BACKGROUND);
        et.setMode(HtcEditText.MODE_DARK_BACKGROUND);

        int[] colorArrayOne = {
                Color.GRAY, Color.RED
        };
        int[] colorArrayTwo = {
                Color.GRAY, Color.RED, Color.GREEN
        };
        et.updateCustomThemeColor(colorArrayOne);
        et.updateCustomThemeColor(colorArrayTwo);
    }

    private void testMinHeight(int id){
        final Drawable drawRest = mActivity.getResources().getDrawable(R.drawable.common_inputfield_rest);
        int drawHeight = drawRest.getIntrinsicHeight();
        View widget = mActivity.findViewById(id);
        assertEquals(drawHeight, widget.getHeight());
    }

    private void testDisable(boolean isFocus) {
        getInstrumentation().waitForIdleSync();
        assertNotNull(mActivity);
        final EditText et = (EditText) mSolo.getView(HtcEditText.class, 2);
        if (isFocus) {
            getInstrumentation().runOnMainSync(new Runnable() {
                public void run() {
                    et.requestFocus();
                    et.setCursorVisible(false);
                }
            });
            ScreenShotUtil.AssertViewEqualBefore(mSolo, et, this);
        } else {
            ScreenShotUtil.AssertViewEqualBefore(mSolo, et, this);
        }
    }

    private void test(int id, boolean isFocus) {
        getInstrumentation().waitForIdleSync();
        assertNotNull(mActivity);
        final EditText et = (EditText) mSolo.getView(id);
        if (isFocus) {
            getInstrumentation().runOnMainSync(new Runnable() {
                public void run() {
                    et.requestFocus();
                    et.setCursorVisible(false);
                }
            });
            ScreenShotUtil.AssertViewEqualBefore(mSolo, et, this);
        } else {
            final EditText tmp = (EditText) mSolo.getView(HtcEditText.class, 2);
            getInstrumentation().runOnMainSync(new Runnable() {
                public void run() {
                    tmp.requestFocus();
                    tmp.setCursorVisible(false);
                }
            });
            ScreenShotUtil.AssertViewEqualBefore(mSolo, et, this);
        }

    }
}
