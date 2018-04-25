
package com.htc.test;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Xml;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Checkable;

import com.htc.test.util.EventUtil;
import com.htc.test.util.NoRunTheme;
import com.htc.test.util.ScreenShotUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class WidgetTestCaseBase<T extends View> extends HtcActivityTestCaseBase implements
        TestDPadFocus {

    private Class<T> mTargetClass;
    private T mTargetView;

    private static final int STATE_REST = 0;
    private static final int STATE_PRESS = 1;
    private static final int STATE_DISABLE = 2;

    private static int mScreenWidth = 1080;
    private static int mScreenHeight = 1920;

    private final static int SIZE_TYPE_ZERO = 0;
    private final static int SIZE_TYPE_ERROR = 1;
    private final static int SIZE_TYPE_SCREEN_WIDTH = 2;
    private final static int SIZE_TYPE_SCREEN_HEIGHT = 3;

    private int mWidthExpected;
    private int mHeightExpected;
    private boolean mIsSetExpectedMeasureDimension;

    public WidgetTestCaseBase(Class<?> activityClass, Class<T> targetClass) {
        super(activityClass);
        mTargetClass = targetClass;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mWidthExpected = 0;
        mHeightExpected = 0;
        mIsSetExpectedMeasureDimension = false;
    }

    public void testDisableState() {
        testState(STATE_DISABLE, false);
    }

    public void testRestState() {
        testState(STATE_REST, false);
    }

    public void testPressState() {
        testState(STATE_PRESS, false);
    }

    public void testDisableState_Checked() {
        testState(STATE_DISABLE, true);
    }

    public void testRestState_Checked() {
        testState(STATE_REST, true);
    }

    private void testState(int state, boolean isChecked) {

        final T targetView = initActivityWithTargetView(false, isChecked);

        switch (state) {
            case STATE_REST:
                getInstrumentation().waitForIdleSync();
                ScreenShotUtil.AssertViewEqualBefore(mSolo, targetView, this);
                break;
            case STATE_PRESS:
                EventUtil.callLongPressed(getInstrumentation(), targetView,
                        new EventUtil.EventCallBack() {
                            @Override
                            public void onPressedStatus(View view) {
                                getInstrumentation().waitForIdleSync();
                                mSolo.sleep(3000);
                                ScreenShotUtil.AssertViewEqualBefore(mSolo, targetView,
                                        WidgetTestCaseBase.this);
                            }
                        });
                break;
            case STATE_DISABLE:
                getInstrumentation().runOnMainSync(new Runnable() {
                    @Override
                    public void run() {
                        targetView.setEnabled(false);
                    }
                });
                getInstrumentation().waitForIdleSync();
                ScreenShotUtil.AssertViewEqualBefore(mSolo, targetView, this);
                break;
        }
    }

    private void launchActivity() {
        if (null == mActivity) {
            initActivity();
        }
        assertNotNull(mActivity);
        initScreenSize();
    }

    private void initScreenSize() {
        if (0 == mScreenWidth || 0 == mScreenHeight) {
            final DisplayMetrics dm = new DisplayMetrics();
            final WindowManager wm = (WindowManager) mActivity
                    .getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(dm);

            if (null != dm) {
                mScreenWidth = dm.widthPixels;
                mScreenHeight = dm.heightPixels;
            }
        }
    }

    @NoRunTheme
    public void testWidthMeasure_SZ_MU() {
        testMeasure(SIZE_TYPE_ZERO, MeasureSpec.UNSPECIFIED, SIZE_TYPE_SCREEN_HEIGHT,
                MeasureSpec.EXACTLY);
    }

    @NoRunTheme
    public void testWidthMeasure_SZ_ME() {
        testMeasure(SIZE_TYPE_ZERO, MeasureSpec.EXACTLY, SIZE_TYPE_SCREEN_HEIGHT,
                MeasureSpec.EXACTLY);
    }

    @NoRunTheme
    public void testWidthMeasure_SZ_MA() {
        testMeasure(SIZE_TYPE_ZERO, MeasureSpec.AT_MOST, SIZE_TYPE_SCREEN_HEIGHT,
                MeasureSpec.EXACTLY);
    }

    @NoRunTheme
    public void testWidthMeasure_SE_MU() {
        testMeasure(SIZE_TYPE_ERROR, MeasureSpec.UNSPECIFIED, SIZE_TYPE_SCREEN_HEIGHT,
                MeasureSpec.EXACTLY);
    }

    @NoRunTheme
    public void testWidthMeasure_SE_ME() {
        testMeasure(SIZE_TYPE_ERROR, MeasureSpec.EXACTLY, SIZE_TYPE_SCREEN_HEIGHT,
                MeasureSpec.EXACTLY);
    }

    @NoRunTheme
    public void testWidthMeasure_SE_MA() {
        testMeasure(SIZE_TYPE_ERROR, MeasureSpec.AT_MOST, SIZE_TYPE_SCREEN_HEIGHT,
                MeasureSpec.EXACTLY);
    }

    @NoRunTheme
    public void testWidthMeasure_SW_MU() {
        testMeasure(SIZE_TYPE_SCREEN_WIDTH, MeasureSpec.UNSPECIFIED, SIZE_TYPE_SCREEN_HEIGHT,
                MeasureSpec.EXACTLY);
    }

    @NoRunTheme
    public void testWidthMeasure_SW_ME() {
        testMeasure(SIZE_TYPE_SCREEN_WIDTH, MeasureSpec.EXACTLY, SIZE_TYPE_SCREEN_HEIGHT,
                MeasureSpec.EXACTLY);
    }

    @NoRunTheme
    public void testWidthMeasure_SW_MA() {
        testMeasure(SIZE_TYPE_SCREEN_WIDTH, MeasureSpec.AT_MOST, SIZE_TYPE_SCREEN_HEIGHT,
                MeasureSpec.EXACTLY);
    }

    @NoRunTheme
    public void testHeightMeasure_SZ_MU() {
        testMeasure(SIZE_TYPE_SCREEN_WIDTH, MeasureSpec.EXACTLY, SIZE_TYPE_ZERO,
                MeasureSpec.UNSPECIFIED);
    }

    @NoRunTheme
    public void testHeightMeasure_SZ_ME() {
        testMeasure(SIZE_TYPE_SCREEN_WIDTH, MeasureSpec.EXACTLY, SIZE_TYPE_ZERO,
                MeasureSpec.EXACTLY);
    }

    @NoRunTheme
    public void testHeightMeasure_SZ_MA() {
        testMeasure(SIZE_TYPE_SCREEN_WIDTH, MeasureSpec.EXACTLY, SIZE_TYPE_ZERO,
                MeasureSpec.AT_MOST);
    }

    @NoRunTheme
    public void testHeightMeasure_SE_MU() {
        testMeasure(SIZE_TYPE_SCREEN_WIDTH, MeasureSpec.EXACTLY, SIZE_TYPE_ERROR,
                MeasureSpec.UNSPECIFIED);
    }

    @NoRunTheme
    public void testHeightMeasure_SE_ME() {
        testMeasure(SIZE_TYPE_SCREEN_WIDTH, MeasureSpec.EXACTLY, SIZE_TYPE_ERROR,
                MeasureSpec.EXACTLY);
    }

    @NoRunTheme
    public void testHeightMeasure_SE_MA() {
        testMeasure(SIZE_TYPE_SCREEN_WIDTH, MeasureSpec.EXACTLY, SIZE_TYPE_ERROR,
                MeasureSpec.AT_MOST);
    }

    @NoRunTheme
    public void testHeightMeasure_SH_MU() {
        testMeasure(SIZE_TYPE_SCREEN_WIDTH, MeasureSpec.EXACTLY, SIZE_TYPE_SCREEN_HEIGHT,
                MeasureSpec.UNSPECIFIED);
    }

    @NoRunTheme
    public void testHeightMeasure_SH_ME() {
        testMeasure(SIZE_TYPE_SCREEN_WIDTH, MeasureSpec.EXACTLY, SIZE_TYPE_SCREEN_HEIGHT,
                MeasureSpec.EXACTLY);
    }

    @NoRunTheme
    public void testHeightMeasure_SH_MA() {
        testMeasure(SIZE_TYPE_SCREEN_WIDTH, MeasureSpec.EXACTLY, SIZE_TYPE_SCREEN_HEIGHT,
                MeasureSpec.AT_MOST);
    }

    private void testMeasure(final int widthSizeType,
            final int widthMode,
            final int heightSizeType, final int heightMode) {
        launchActivity();

        final View targetView = initTargetView();
        assertNotNull(targetView);
        setTargetViewByCustomer(targetView);

        final int widthSpec = MeasureSpec.makeMeasureSpec(getSizeByType(widthSizeType), widthMode);
        final int heightSpec = MeasureSpec.makeMeasureSpec(getSizeByType(heightSizeType),
                heightMode);

        targetView.measure(widthSpec, heightSpec);
        final int widthActual = targetView.getMeasuredWidth();
        final int heightActual = targetView.getMeasuredHeight();

        measureExpected(widthSpec, heightSpec);
        assertTrue("setExpectedMeasureDimension must be called", mIsSetExpectedMeasureDimension);
        assertEquals("Width is incorrect", mWidthExpected, widthActual);
        assertEquals("Height is incorrect", mHeightExpected, heightActual);

    }

    private T initTargetView() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                try {
                    final Constructor<T> constructor = mTargetClass.getConstructor(
                            Context.class);
                    mTargetView = (T) constructor.newInstance(mActivity);

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
        getInstrumentation().waitForIdleSync();
        return mTargetView;
    }

    private T initTargetView(final AttributeSet as) {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                try {
                    final Constructor<? extends View> constructor = mTargetClass.getConstructor(
                            Context.class, AttributeSet.class);
                    mTargetView = (T) constructor.newInstance(mActivity, as);

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
        getInstrumentation().waitForIdleSync();;
        return mTargetView;
    }

    private int getSizeByType(int sizeType) {
        switch (sizeType) {
            case SIZE_TYPE_ZERO:
                return 0;
            case SIZE_TYPE_ERROR:
                return -1;
            case SIZE_TYPE_SCREEN_WIDTH:
                return mScreenWidth;
            case SIZE_TYPE_SCREEN_HEIGHT:
                return mScreenHeight;
            default:
                return SIZE_TYPE_ZERO;
        }
    }

    private AttributeSet getAttributeSetFromXml(int resId) {
        final XmlPullParser parser = getInstrumentation().getContext().getResources().getXml(resId);
        if (null == parser) {
            return null;
        }

        final AttributeSet as = Xml.asAttributeSet(parser);
        try {
            int type;
            while ((type = parser.next()) != XmlPullParser.START_TAG &&
                    type != XmlPullParser.END_DOCUMENT) {
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return as;
    }

    public void testConstructor_attributeSet() {
        launchActivity();

        final View targetView = initTargetView(getAttributeSetFromXml(com.htc.lib1.cc.test.R.xml.attributeset));
        assertNotNull(targetView);

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                final LayoutParams lp = targetView.getLayoutParams();
                if (lp != null) {
                    mActivity.setContentView(targetView, lp);
                } else {
                    mActivity.setContentView(targetView);
                }
            }
        });
        getInstrumentation().waitForIdleSync();
        ScreenShotUtil.AssertViewEqualBefore(mSolo, targetView, this);
    }

    /*
     * owner should override measureExpected(), and call setExpectedMeasureDimension() to set
     * expected width & height back
     */
    abstract protected void measureExpected(int widthMeasureSpec, int heightMeasureSpec);

    protected void setExpectedMeasureDimension(int width, int height) {
        mWidthExpected = width;
        mHeightExpected = height;
        mIsSetExpectedMeasureDimension = true;
    }

    protected void setTargetViewByCustomer(View view) {
    }

    protected void setUpTargetViewForTestState(View view) {
    }

    @Override
    public void testDpadFocus() {
        final T targetView = initActivityWithTargetView(true, false);
        mSolo.sleep(3000);
        ScreenShotUtil.AssertViewEqualBefore(mSolo, targetView,
                WidgetTestCaseBase.this);
    }

    private T initActivityWithTargetView(final boolean isFocus, final boolean isChecked) {
        launchActivity();

        final T targetView = initTargetView();
        assertNotNull(targetView);

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                if (isChecked && targetView instanceof Checkable) {
                    ((Checkable) targetView).setChecked(true);
                }
                mActivity.setContentView(targetView, new LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                setUpTargetViewForTestState(targetView);
                if (isFocus) {
                    targetView.requestFocus();
                }
            }
        });
        return targetView;
    }
}
