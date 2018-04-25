
package com.htc.lib1.cc.progressbarseekbar.test;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.TrimSeekBar;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;

public class TrimSeekBarTest extends HtcActivityTestCaseBase {

    private static final int STEP_COUNT = 60;
    private static final int TARGET_PROGRESS = 25;

    public TrimSeekBarTest() throws ClassNotFoundException {
        super(Class.forName("com.htc.lib1.cc.progressbarseekbar.activityhelper.TrimSeekBarDemo"));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityIntent(null);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
    }

    public final void testInit() {
        View seekBar = mActivity.findViewById(R.id.trim_seek_bar);
        ScreenShotUtil.AssertViewEqualBefore(mSolo, seekBar, this);
    }

    public final void testDrag() {
        TrimSeekBar seekBar = (TrimSeekBar) mActivity.findViewById(R.id.trim_seek_bar);
        TextView textStart = (TextView) mActivity.findViewById(R.id.popup_view_start);
        TextView textEnd = (TextView) mActivity.findViewById(R.id.popup_view_end);

        Drawable trimDrawable = mActivity.getResources().getDrawable(
                com.htc.lib1.cc.R.drawable.seekbar_trim);
        int trimWidth = trimDrawable.getIntrinsicWidth();
        int[] location = new int[2];
        seekBar.getLocationOnScreen(location);

        int startX, endX;
        endX = startX = trimWidth / 2;
        int startY, endY;
        endY = startY = (int) (location[1] + seekBar.getHeight() / 2);

        final int deltaX = (seekBar.getWidth() / 100) * (TARGET_PROGRESS - 1);
        endX += deltaX;
        mSolo.drag(startX, endX, startY, endY, 30);

        while (seekBar.getStartTrimProgress() < TARGET_PROGRESS) {
            startX = endX;
            endX += 1;
            mSolo.drag(startX, endX, startY, endY, 1);
        }

        mSolo.sleep(2000);
        View[] multiView = {
                seekBar,
                textStart,
                textEnd
        };
        ScreenShotUtil.AssertMultipleViewEqualBefore(mSolo, multiView, this, getInstrumentation());
    }

    public final void testSetTrimProgress() {
        TrimSeekBar seekBar = (TrimSeekBar) mActivity.findViewById(R.id.trim_seek_bar);
        View start = mActivity.findViewById(R.id.popup_view_start);
        View end = mActivity.findViewById(R.id.popup_view_end);

        final View startBtn = mActivity.findViewById(R.id.start_btn);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                startBtn.performClick();
            }
        });
        mSolo.sleep(1000);
        View[] multiView = {
                seekBar,
                start,
                end
        };
        ScreenShotUtil.AssertMultipleViewEqualBefore(mSolo, multiView, this, getInstrumentation());
    }
}
