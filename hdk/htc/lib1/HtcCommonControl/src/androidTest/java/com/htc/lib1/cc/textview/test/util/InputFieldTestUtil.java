package com.htc.lib1.cc.textview.test.util;

import android.app.Instrumentation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.htc.test.util.ScreenShotUtil;
import com.htc.lib1.cc.test.R;
import com.robotium.solo.Solo;

import junit.framework.Assert;
import junit.framework.TestCase;

public class InputFieldTestUtil {

    public static void test_IME(Instrumentation inst, final EditText editText, Solo solo, TestCase test) {
        Assert.assertNotNull(editText);
        inst.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                editText.setImeOptions(EditorInfo.IME_FLAG_NO_FULLSCREEN);
            }
        });
        ScreenShotUtil.AssertViewEqualBefore(solo, editText, test);
    }

    public static void test_Hint_LongStr(Instrumentation inst, final EditText editText, Solo solo, TestCase test) {
        Assert.assertNotNull(editText);
        inst.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                editText.setHint(R.string.str_long);
            }
        });
        ScreenShotUtil.AssertViewEqualBefore(solo, editText, test);
    }

    public static void test_Hint_SpaceStr(Instrumentation inst, final EditText editText, Solo solo, TestCase test) {
        Assert.assertNotNull(editText);
        inst.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                editText.setHint(R.string.str_space);
            }
        });
        ScreenShotUtil.AssertViewEqualBefore(solo, editText, test);
    }

    public static void test_Text_LongStr(Instrumentation inst, final EditText editText, Solo solo, TestCase test) {
        Assert.assertNotNull(editText);
        inst.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                editText.setText(R.string.str_long);
            }
        });
        ScreenShotUtil.AssertViewEqualBefore(solo, editText, test);
    }

    public static void test_Text_SpaceStr(Instrumentation inst, final EditText editText, Solo solo, TestCase test) {
        Assert.assertNotNull(editText);
        inst.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                editText.setText(R.string.str_space);
            }
        });

        ScreenShotUtil.AssertViewEqualBefore(solo, editText, test);
    }

    public static void test_Has_Focus(Instrumentation inst, final EditText editText, Solo solo, TestCase test) {
        Assert.assertNotNull(editText);
        inst.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                editText.requestFocus();
            }
        });
        Assert.assertTrue(editText.isFocused());
    }

    public static void test_No_Focus(Instrumentation inst, final EditText editText, Solo solo, TestCase test) {
        Assert.assertNotNull(editText);
        inst.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                editText.clearFocus();
            }
        });
        Assert.assertFalse(editText.isFocused());
    }

    public static void test_Input_Type(Instrumentation inst, final EditText editText, Solo solo, TestCase test) {
        Assert.assertNotNull(editText);
        inst.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                editText.setInputType(EditorInfo.TYPE_CLASS_TEXT
                        | EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        | EditorInfo.TYPE_TEXT_FLAG_AUTO_COMPLETE);
            }
        });
        ScreenShotUtil.AssertViewEqualBefore(solo, editText, test);
    }

    public static void test_Select_Full(Instrumentation inst, final EditText editText, Solo solo, TestCase test) {
        Assert.assertNotNull(editText);
        inst.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                editText.setText(R.string.hello_world);
                editText.requestFocus();
                editText.selectAll();
            }
        });
        solo.sleep(1000);
        ScreenShotUtil.AssertViewEqualBefore(solo, editText, test);
    }
}
