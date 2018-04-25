
package com.htc.lib1.cc.progressbarseekbar.test;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ProgressBar;

import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.HtcProgressBar;
import com.htc.lib1.cc.widget.HtcSeekBar;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;
import com.htc.test.util.WidgetUtil;

public class ProgressBarSeekBarDemoTest extends HtcActivityTestCaseBase {

    public ProgressBarSeekBarDemoTest() throws ClassNotFoundException {
        super(
                Class.forName("com.htc.lib1.cc.progressbarseekbar.activityhelper.ProgressBarSeekBarDemo"));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityIntent(null);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
    }

    public final void testOnCreateBundleWhite() {
        ScreenShotUtil.AssertViewEqualBefore(mSolo,
                mSolo.getView("HtcSeekBar_thin"), this);
    }

    public final void testOnCreateBundleBlack() {
        ScreenShotUtil.AssertViewEqualBefore(mSolo,
                mSolo.getView("HtcSeekBar_thick"), this);
    }

    public final void test_HPB_Line_Light() {
        ScreenShotUtil.AssertViewEqualBefore(mSolo,
                mSolo.getView("progressBar_light"), this);
    }

    public final void test_HPB_Line_Dark() {
        ScreenShotUtil.AssertViewEqualBefore(mSolo,
                mSolo.getView("progressBar_dark"), this);
    }

    public final void test_HPB_IndeMed_Light() {
        testProgressBarIndeterminate(R.id.ProgressBar_medium_light);
    }

    public final void test_HPB_IndeMed_Dark() {
        testProgressBarIndeterminate(R.id.ProgressBar_medium_dark);
    }

    public final void test_HPB_IndeSma_Light() {
        testProgressBarIndeterminate(R.id.ProgressBar_small_light);
    }

    public final void test_HPB_IndeSma_Dark() {
        testProgressBarIndeterminate(R.id.ProgressBar_small_dark);
    }

    public final void testImproveCoverage() {
        HtcProgressBar progressBar = new HtcProgressBar(mActivity);
        HtcProgressBar hpb = (HtcProgressBar) mActivity.findViewById(R.id.progressBar_light);
        AccessibilityEvent accessEvent = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_VIEW_CLICKED);
        hpb.onInitializeAccessibilityEvent(accessEvent);
        hpb.onPopulateAccessibilityEvent(accessEvent);
        MotionEvent event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 0, 0, 0);
        hpb.onTouchEvent(event);
        AccessibilityNodeInfo info = AccessibilityNodeInfo.obtain();
        hpb.onInitializeAccessibilityNodeInfo(info);

        HtcSeekBar seekBar = new HtcSeekBar(mActivity);
        final HtcSeekBar sb = (HtcSeekBar) mActivity.findViewById(R.id.HtcSeekBar_default);
        sb.setFocusable(true);
        sb.setFocusable(false);
        sb.getDisplayMode();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ColorDrawable draw = new ColorDrawable(Color.RED);
                sb.setThumb(draw);
                sb.setEnabled(false);
                sb.setEnabled(true);
                Parcelable state = sb.onSaveInstanceState();
                sb.onRestoreInstanceState(state);
                sb.setThumbVisible(true);
                sb.setThumbVisible(false);
                sb.setVisibility(View.INVISIBLE);
                sb.setVisibility(View.VISIBLE);
                sb.setVisibility(View.GONE);
                MotionEvent event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 0, 0, 0);
                sb.onTouchEvent(event);
                event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, 0, 0, 0);
                sb.onTouchEvent(event);
                event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_MOVE, 0, 0, 0);
                sb.onTouchEvent(event);
            }
        });
    }

    private void testProgressBarIndeterminate(int id) {
        final ProgressBar pb = (ProgressBar) getActivity().findViewById(id);
        WidgetUtil.setProgressBarIndeterminatedStopRunning(this, pb);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                pb.setVisibility(View.VISIBLE);
            }
        });
        ScreenShotUtil.AssertViewEqualBefore(mSolo, pb, this);
    }
}
