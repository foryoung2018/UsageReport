
package com.htc.test;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htc.test.util.NoRunTheme;
import com.htc.test.util.ScreenShotUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class TextViewTestCaseBase<T extends TextView> extends HtcActivityTestCaseBase {

    private Class<T> mTargetClass;
    private T mInstance = null;

    private static final int STRING_TYPE_NULL = 0;
    private static final int STRING_TYPE_EMPTY = 1;
    private static final int STRING_TYPE_HINT = 2;
    private static final int STRING_TYPE_EMAIL = 3;
    private static final int STRING_TYPE_ENGLISHSTRING = 4;
    private static final int STRING_TYPE_SIMPLIFIED_CHINESE = 5;
    private static final int STRING_TYPE_TRADITIONAL_CHINESE = 6;
    private static final int STRING_TYPE_ARABICSTRING = 7;
    private static final int STRING_TYPE_ISRAELSTRING = 8;
    private static final int STRING_TYPE_NUMBERSTRING = 9;

    private static final int STRING_LENGTH_TYPE_SHORT = 0;
    private static final int STRING_LENGTH_TYPE_LONG = 1;
    private static final int STRING_LENGTH_TYPE_MULTILINE = 2;

    private static final int TIMES = 50;

    private static final int INPUT_TYPE_NORMAL = EditorInfo.TYPE_CLASS_TEXT;

    private static final int INPUT_TYPE_MULTI_LINE = EditorInfo.TYPE_CLASS_TEXT
            | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE;

    private static final int INPUT_TYPE_EMAIL = EditorInfo.TYPE_CLASS_TEXT
            | EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            | EditorInfo.TYPE_TEXT_FLAG_AUTO_COMPLETE;

    private static final int INPUT_TYPE_PASSWORD = EditorInfo.TYPE_CLASS_TEXT
            | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD;

    public TextViewTestCaseBase(Class<?> activityClass, Class<T> targetClass) {
        super(activityClass);
        mTargetClass = targetClass;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        initActivity();
        assertNotNull(mActivity);
        initTargetView();
    }

    private void initTargetView() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                try {
                    final Constructor constructor = mTargetClass.getConstructor(
                            Context.class);
                    mInstance = (T) constructor.newInstance(mActivity);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @NoRunTheme
    public void testNullString() {
        test(STRING_TYPE_NULL, STRING_LENGTH_TYPE_SHORT, true, false,
                INPUT_TYPE_NORMAL, false);
    }

    @NoRunTheme
    public void testEmptyString() {
        test(STRING_TYPE_EMPTY, STRING_LENGTH_TYPE_SHORT, true, false,
                INPUT_TYPE_NORMAL, false);
    }

    @NoRunTheme
    public void testHint() {
        test(STRING_TYPE_HINT, STRING_LENGTH_TYPE_SHORT, true, false,
                INPUT_TYPE_NORMAL, false);
    }

    @NoRunTheme
    public void testFocus() {
        test(STRING_TYPE_ENGLISHSTRING, STRING_LENGTH_TYPE_SHORT, true, false,
                INPUT_TYPE_NORMAL,
                false);
    }

    @NoRunTheme
    public void testNotFocus() {
        test(STRING_TYPE_ENGLISHSTRING, STRING_LENGTH_TYPE_SHORT, false, false,
                INPUT_TYPE_NORMAL,
                false);
    }

    @NoRunTheme
    public void testSelectAll() {
        test(STRING_TYPE_ENGLISHSTRING, STRING_LENGTH_TYPE_SHORT, true, true,
                INPUT_TYPE_NORMAL,
                false);
    }

    @NoRunTheme
    public void testInputTypeEmail() {
        test(STRING_TYPE_EMAIL, STRING_LENGTH_TYPE_SHORT, true, false,
                INPUT_TYPE_EMAIL, false);
    }

    @NoRunTheme
    public void testInputTypePassword() {
        test(STRING_TYPE_ENGLISHSTRING, STRING_LENGTH_TYPE_SHORT, true, false,
                INPUT_TYPE_PASSWORD,
                false);
    }

    // STRING_TYPE_ENGLISHSTRING
    @NoRunTheme
    public void testEnglishShortLTR() {
        test(STRING_TYPE_ENGLISHSTRING, STRING_LENGTH_TYPE_SHORT, true, false, INPUT_TYPE_NORMAL,
                false);
    }

    @NoRunTheme
    public void testEnglishLongLTR() {
        test(STRING_TYPE_ENGLISHSTRING, STRING_LENGTH_TYPE_LONG, true, false, INPUT_TYPE_NORMAL,
                false);
    }

    @NoRunTheme
    public void testEnglishMultiLineLTR() {
        test(STRING_TYPE_ENGLISHSTRING, STRING_LENGTH_TYPE_MULTILINE, true, false,
                INPUT_TYPE_MULTI_LINE,
                false);
    }

    @NoRunTheme
    public void testEnglishShortRTL() {
        test(STRING_TYPE_ENGLISHSTRING, STRING_LENGTH_TYPE_SHORT, true, false, INPUT_TYPE_NORMAL,
                true);
    }

    @NoRunTheme
    public void testEnglishLongRTL() {
        test(STRING_TYPE_ENGLISHSTRING, STRING_LENGTH_TYPE_LONG, true, false, INPUT_TYPE_NORMAL,
                true);
    }

    @NoRunTheme
    public void testEnglishMultiLineRTL() {
        test(STRING_TYPE_ENGLISHSTRING, STRING_LENGTH_TYPE_MULTILINE, true, false,
                INPUT_TYPE_MULTI_LINE,
                true);
    }

    // STRING_TYPE_SIMPLIFIED_CHINESE
    @NoRunTheme
    public void testSimplifiedChineseShortLTR() {
        test(STRING_TYPE_SIMPLIFIED_CHINESE, STRING_LENGTH_TYPE_SHORT, true, false,
                INPUT_TYPE_NORMAL,
                false);
    }

    @NoRunTheme
    public void testSimplifiedChineseLongLTR() {
        test(STRING_TYPE_SIMPLIFIED_CHINESE, STRING_LENGTH_TYPE_LONG, true, false,
                INPUT_TYPE_NORMAL,
                false);
    }

    @NoRunTheme
    public void testSimplifiedChineseMultiLineLTR() {
        test(STRING_TYPE_SIMPLIFIED_CHINESE, STRING_LENGTH_TYPE_MULTILINE, true, false,
                INPUT_TYPE_MULTI_LINE,
                false);
    }

    @NoRunTheme
    public void testSimplifiedChineseShortRTL() {
        test(STRING_TYPE_SIMPLIFIED_CHINESE, STRING_LENGTH_TYPE_SHORT, true, false,
                INPUT_TYPE_NORMAL,
                true);
    }

    @NoRunTheme
    public void testSimplifiedChineseLongRTL() {
        test(STRING_TYPE_SIMPLIFIED_CHINESE, STRING_LENGTH_TYPE_LONG, true, false,
                INPUT_TYPE_NORMAL,
                true);
    }

    @NoRunTheme
    public void testSimplifiedChineseMultiLineRTL() {
        test(STRING_TYPE_SIMPLIFIED_CHINESE, STRING_LENGTH_TYPE_MULTILINE, true, false,
                INPUT_TYPE_MULTI_LINE,
                true);
    }

    // STRING_TYPE_TRADITIONAL_CHINESE
    @NoRunTheme
    public void testTraditionalChineseShortLTR() {
        test(STRING_TYPE_TRADITIONAL_CHINESE, STRING_LENGTH_TYPE_SHORT, true, false,
                INPUT_TYPE_NORMAL,
                false);
    }

    @NoRunTheme
    public void testTraditionalChineseLongLTR() {
        test(STRING_TYPE_TRADITIONAL_CHINESE, STRING_LENGTH_TYPE_LONG, true, false,
                INPUT_TYPE_NORMAL,
                false);
    }

    @NoRunTheme
    public void testTraditionalChineseMultiLineLTR() {
        test(STRING_TYPE_TRADITIONAL_CHINESE, STRING_LENGTH_TYPE_MULTILINE, true, false,
                INPUT_TYPE_MULTI_LINE,
                false);
    }

    @NoRunTheme
    public void testTraditionalChineseShortRTL() {
        test(STRING_TYPE_TRADITIONAL_CHINESE, STRING_LENGTH_TYPE_SHORT, true, false,
                INPUT_TYPE_NORMAL,
                true);
    }

    @NoRunTheme
    public void testTraditionalChineseLongRTL() {
        test(STRING_TYPE_TRADITIONAL_CHINESE, STRING_LENGTH_TYPE_LONG, true, false,
                INPUT_TYPE_NORMAL,
                true);
    }

    @NoRunTheme
    public void testTraditionalChineseMultiLineRTL() {
        test(STRING_TYPE_TRADITIONAL_CHINESE, STRING_LENGTH_TYPE_MULTILINE, true, false,
                INPUT_TYPE_MULTI_LINE,
                true);
    }

    // STRING_TYPE_ARABICSTRING
    @NoRunTheme
    public void testArabicShortLTR() {
        test(STRING_TYPE_ARABICSTRING, STRING_LENGTH_TYPE_SHORT, true, false, INPUT_TYPE_NORMAL,
                false);
    }

    @NoRunTheme
    public void testArabicLongLTR() {
        test(STRING_TYPE_ARABICSTRING, STRING_LENGTH_TYPE_LONG, true, false, INPUT_TYPE_NORMAL,
                false);
    }

    @NoRunTheme
    public void testArabicMultiLineLTR() {
        test(STRING_TYPE_ARABICSTRING, STRING_LENGTH_TYPE_MULTILINE, true, false,
                INPUT_TYPE_MULTI_LINE,
                false);
    }

    @NoRunTheme
    public void testArabicShortRTL() {
        test(STRING_TYPE_ARABICSTRING, STRING_LENGTH_TYPE_SHORT, true, false, INPUT_TYPE_NORMAL,
                true);
    }

    @NoRunTheme
    public void testArabicLongRTL() {
        test(STRING_TYPE_ARABICSTRING, STRING_LENGTH_TYPE_LONG, true, false, INPUT_TYPE_NORMAL,
                true);
    }

    @NoRunTheme
    public void testArabicMultiLineRTL() {
        test(STRING_TYPE_ARABICSTRING, STRING_LENGTH_TYPE_MULTILINE, true, false,
                INPUT_TYPE_MULTI_LINE,
                true);
    }

    // STRING_TYPE_ISRAELSTRING
    @NoRunTheme
    public void testIsraelShortLTR() {
        test(STRING_TYPE_ISRAELSTRING, STRING_LENGTH_TYPE_SHORT, true, false, INPUT_TYPE_NORMAL,
                false);
    }

    @NoRunTheme
    public void testIsraelLongLTR() {
        test(STRING_TYPE_ISRAELSTRING, STRING_LENGTH_TYPE_LONG, true, false, INPUT_TYPE_NORMAL,
                false);
    }

    @NoRunTheme
    public void testIsraelMultiLineLTR() {
        test(STRING_TYPE_ISRAELSTRING, STRING_LENGTH_TYPE_MULTILINE, true, false,
                INPUT_TYPE_MULTI_LINE,
                false);
    }

    @NoRunTheme
    public void testIsraelShortRTL() {
        test(STRING_TYPE_ISRAELSTRING, STRING_LENGTH_TYPE_SHORT, true, false, INPUT_TYPE_NORMAL,
                true);
    }

    @NoRunTheme
    public void testIsraelLongRTL() {
        test(STRING_TYPE_ISRAELSTRING, STRING_LENGTH_TYPE_LONG, true, false, INPUT_TYPE_NORMAL,
                true);
    }

    @NoRunTheme
    public void testIsraelMultiLineRTL() {
        test(STRING_TYPE_ISRAELSTRING, STRING_LENGTH_TYPE_MULTILINE, true, false,
                INPUT_TYPE_MULTI_LINE,
                true);
    }

    // STRING_TYPE_NUMBERSTRING
    @NoRunTheme
    public void testNumberShortLTR() {
        test(STRING_TYPE_NUMBERSTRING, STRING_LENGTH_TYPE_SHORT, true, false, INPUT_TYPE_NORMAL,
                false);
    }

    @NoRunTheme
    public void testNumberLongLTR() {
        test(STRING_TYPE_NUMBERSTRING, STRING_LENGTH_TYPE_LONG, true, false, INPUT_TYPE_NORMAL,
                false);
    }

    @NoRunTheme
    public void testNumberMultiLineLTR() {
        test(STRING_TYPE_NUMBERSTRING, STRING_LENGTH_TYPE_MULTILINE, true, false,
                INPUT_TYPE_MULTI_LINE,
                false);
    }

    @NoRunTheme
    public void testNumberShortRTL() {
        test(STRING_TYPE_NUMBERSTRING, STRING_LENGTH_TYPE_SHORT, true, false, INPUT_TYPE_NORMAL,
                true);
    }

    @NoRunTheme
    public void testNumberLongRTL() {
        test(STRING_TYPE_NUMBERSTRING, STRING_LENGTH_TYPE_LONG, true, false, INPUT_TYPE_NORMAL,
                true);
    }

    @NoRunTheme
    public void testNumberMultiLineRTL() {
        test(STRING_TYPE_NUMBERSTRING, STRING_LENGTH_TYPE_MULTILINE, true, false,
                INPUT_TYPE_MULTI_LINE,
                true);
    }

    private void test(final int stringType, final int stringLengthType,
            final boolean isFocus, final boolean isSelectAll, final int inputType,
            final boolean isRTL) {

        final T targetView = getTargetView();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                final Resources res = getInstrumentation().getContext().getResources();
                assertNotNull(res);
                String str = null;
                String hintStr = null;

                switch (stringType) {
                    case STRING_TYPE_NULL:
                        break;

                    case STRING_TYPE_EMPTY:
                        str = "";
                        break;

                    case STRING_TYPE_HINT:
                        hintStr = res.getString(com.htc.lib1.cc.test.R.string.hintString);
                        break;

                    case STRING_TYPE_EMAIL:
                        str = res.getString(com.htc.lib1.cc.test.R.string.emailString);
                        break;

                    case STRING_TYPE_ENGLISHSTRING:
                        str = res.getString(com.htc.lib1.cc.test.R.string.englishString);
                        break;

                    case STRING_TYPE_SIMPLIFIED_CHINESE:
                        str = res.getString(com.htc.lib1.cc.test.R.string.simplifiedChineseString);
                        break;

                    case STRING_TYPE_TRADITIONAL_CHINESE:
                        str = res.getString(com.htc.lib1.cc.test.R.string.traditionalChineseString);
                        break;

                    case STRING_TYPE_ARABICSTRING:
                        str = res.getString(com.htc.lib1.cc.test.R.string.arabicString);
                        break;

                    case STRING_TYPE_ISRAELSTRING:
                        str = res.getString(com.htc.lib1.cc.test.R.string.israelString);
                        break;

                    case STRING_TYPE_NUMBERSTRING:
                        str = res.getString(com.htc.lib1.cc.test.R.string.numberString);
                        break;

                    default:
                        break;
                }

                targetView.setHint(hintStr);

                switch (stringLengthType) {
                    case STRING_LENGTH_TYPE_SHORT:
                        targetView.setText(str);
                        break;

                    case STRING_LENGTH_TYPE_LONG:
                        targetView.setSingleLine(true);
                        targetView.setText(genLongString(str));
                        break;

                    case STRING_LENGTH_TYPE_MULTILINE:
                        targetView.setSingleLine(false);
                        targetView.setText(genMultiString(str));
                        break;

                    default:
                        break;
                }

                targetView.setInputType(inputType);

                targetView.setCursorVisible(false);

                if (isFocus) {
                    targetView.requestFocus();
                } else {
                    targetView.clearFocus();
                }

                if (isSelectAll) {
                    if (targetView instanceof EditText) {
                        ((EditText) targetView).selectAll();
                    }
                }

                final LinearLayout ll = new LinearLayout(mActivity);

                if (isRTL) {
                    targetView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    ll.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                }

                ll.addView(targetView, new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                mActivity.setContentView(ll, new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        });

        mSolo.sleep(2000);
        ScreenShotUtil.AssertViewEqualBefore(mSolo, targetView, this);
    }

    private String genString(String sourceStr, boolean isMultiLine) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < TIMES; i++) {
            sb.append(sourceStr);
            if (isMultiLine && 0 == i % 5) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private String genLongString(String sourceStr) {
        return genString(sourceStr, false);
    }

    private String genMultiString(String sourceStr) {
        return genString(sourceStr, true);
    }

    protected T getTargetView() {
        return mInstance;
    }
}
