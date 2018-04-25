
package com.htc.lib1.cc.popupWindow.test.popupcontainer;

import android.app.Instrumentation;
import android.os.SystemClock;
import android.test.InstrumentationTestCase;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.SeekBar;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.popupWindow.activityhelper.popupcontainer.HtcSeekBarPopupWindowDemo;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.EventUtil;
import com.htc.test.util.EventUtil.EventCallBack;
import com.htc.test.util.ScreenShotUtil;

public class SeekBarPopupTest extends HtcActivityTestCaseBase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityIntent(null);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
    }

    public SeekBarPopupTest() {
        super(HtcSeekBarPopupWindowDemo.class);
    }

    private void myTapView(InstrumentationTestCase test, View v, int ViewX, int ViewY) {
        int[] xy = new int[2];
        v.getLocationOnScreen(xy);

        final float x = xy[0] + (float) ViewX;
        float y = xy[1] + (float) ViewY;

        Instrumentation inst = test.getInstrumentation();

        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();

        MotionEvent event = MotionEvent.obtain(downTime, eventTime,
                MotionEvent.ACTION_DOWN, x, y, 0);
        inst.sendPointerSync(event);
        inst.waitForIdleSync();

        eventTime = SystemClock.uptimeMillis();
        final int touchSlop = ViewConfiguration.get(v.getContext()).getScaledTouchSlop();
        event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_MOVE,
                x + (touchSlop / 2.0f), y + (touchSlop / 2.0f), 0);
        inst.sendPointerSync(event);
        inst.waitForIdleSync();
        mSolo.sleep(ViewConfiguration.get(v.getContext()).getTapTimeout());

        eventTime = SystemClock.uptimeMillis();
        event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, x, y, 0);
        inst.sendPointerSync(event);
        inst.waitForIdleSync();
    }

    private void testSeekBar(int id) {
        SeekBar sb = (SeekBar) mActivity.findViewById(id);
        assertNotNull(sb);

        int[] location = {
                0, 0
        };
        sb.getLocationOnScreen(location);
        int w = sb.getWidth() - sb.getPaddingLeft() - sb.getPaddingRight();

        for (int i = 0; i <= sb.getMax(); i += 10) {
            int toX = location[0] + sb.getPaddingLeft() + (int) (((float) (i * w)) / (float) sb.getMax());
            myTapView(this, sb, toX, 0);
        }

    }

    public void testTop() {
        testSeekBar(R.id.seekbar_top);
    }

    public void testMedium() {
        testSeekBar(R.id.seekbar_middle);
    }

    public void testBottom() {
        testSeekBar(R.id.seekbar_bottom);
    }

    private void longPressedPopupWindow(int id, final String captureName) {
        View v = mSolo.getView(id);
        final View[] multiView = {
                v, ((HtcSeekBarPopupWindowDemo) mActivity).getPopupWindow()
        };
        EventUtil.callLongPressed(getInstrumentation(), v, new EventCallBack() {
            @Override
            public void onPressedStatus(View view) {
                mSolo.sleep(ViewConfiguration.get(view.getContext()).getTapTimeout());
                ScreenShotUtil.AssertMultipleViewEqualBefore(mSolo, multiView, SeekBarPopupTest.this, getInstrumentation());
            }
        });
    }

    private void testPopupWindow(int id, String name) {
        getInstrumentation().waitForIdleSync();
        longPressedPopupWindow(id, name);
    }

    private void testPopupWindowAssignSize(int id, String name) {
        getInstrumentation().waitForIdleSync();
        mSolo.clickOnText("Size");
        longPressedPopupWindow(id, name);
    }

    private void testPopupWindowStart(int id, final String name) {
        getInstrumentation().waitForIdleSync();
        View v = mSolo.getView(id);
        final View[] multiView = {
                v, ((HtcSeekBarPopupWindowDemo) mActivity).getPopupWindow()
        };
        EventUtil.callLongPressedByGravity(getInstrumentation(), v, new EventCallBack() {
            @Override
            public void onPressedStatus(View view) {
                mSolo.sleep(ViewConfiguration.get(view.getContext()).getTapTimeout());
                ScreenShotUtil.AssertMultipleViewEqualBefore(mSolo, multiView, SeekBarPopupTest.this, getInstrumentation());
            }
        }, Gravity.LEFT | Gravity.TOP);
    }

    public void testBottomPopupWindow() {
        testPopupWindow(R.id.seekbar_bottom, getClass().getSimpleName() + "_" + getName());
    }

    public void testBottomPopupWindowSize300() {
        testPopupWindowAssignSize(R.id.seekbar_bottom, getClass().getSimpleName() + "_" + getName());
    }

    public void testBottomPopupWindowStart() {
        testPopupWindowStart(R.id.seekbar_bottom, getClass().getSimpleName() + "_" + getName());
    }

    public void testTopPopupWindow() {
        testPopupWindow(R.id.seekbar_top, getClass().getSimpleName() + "_" + getName());
    }

    public void testTopPopupWindowSize300() {
        testPopupWindowAssignSize(R.id.seekbar_top, getClass().getSimpleName() + "_" + getName());
    }

    public void testTopPopupWindowStart() {
        testPopupWindowStart(R.id.seekbar_top, getClass().getSimpleName() + "_" + getName());
    }

    public void testMiddlePopupWindow() {
        testPopupWindow(R.id.seekbar_middle, getClass().getSimpleName() + "_" + getName());
    }

    public void testMiddlePopupWindowSize300() {
        testPopupWindowAssignSize(R.id.seekbar_middle, getClass().getSimpleName() + "_" + getName());
    }

    public void testMiddlePopupWindowStart() {
        testPopupWindowStart(R.id.seekbar_middle, getClass().getSimpleName() + "_" + getName());
    }
}
