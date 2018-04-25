
package com.htc.lib1.cc.adapterview.test;

import android.graphics.Point;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;

import com.htc.lib1.cc.adapterview.activityhelper.HtcListViewReorderDemo;
import com.htc.lib1.cc.widget.HtcReorderListView;
import com.htc.test.HtcActivityTestCase;
import com.htc.test.util.DragUtil;
import com.htc.test.util.ScreenShotUtil;
import com.htc.test.util.ViewUtil;
import com.htc.lib1.cc.test.R;

public class HtcListViewRecorderDemoTest extends
        HtcActivityTestCase {
    public HtcListViewRecorderDemoTest() {
        super(HtcListViewReorderDemo.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
    }

    public final void testDefaultSnapShot() {
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mSolo.getView("htc_reorderlist"), this);
    }

    public final void testDrag() {
        final HtcReorderListView reorderListView = (HtcReorderListView) ((HtcListViewReorderDemo) mActivity).findViewById(R.id.htc_reorderlist);
        final int[] reorderListViewLocation = new int[2];
        reorderListView.getLocationOnScreen(reorderListViewLocation);
        final Point pStart = ViewUtil.getViewSpecPoint(reorderListView.getChildAt(0).findViewById(R.id.my_dragger), Gravity.CENTER);
        final Point pStop = new Point(pStart.x, pStart.y + reorderListView.getHeight() * 3 / 4);
        final int step = 30;
        final Point[] track = DragUtil.genDragTrack(pStart, pStop, step);
        DragUtil.dragTabByTrack(getInstrumentation(), new Point[][] {
                track
        }, new DragUtil.GestureCallBack() {

            @Override
            public void onBefore(Point[][] track, int trackIndex,
                    int iteration, int[] changeEvent) {
                mSolo.sleep(1000);
                if (iteration == step / 2) {
                    mSolo.waitForView(reorderListView);
                    View[] views = new View[2];
                    views[0] = reorderListView;
                    views[1] = mSolo.getView(RelativeLayout.class, 0);
                    ScreenShotUtil.AssertMultipleViewEqualBefore(mSolo, views, HtcListViewRecorderDemoTest.this, getInstrumentation());
                }
            }

            @Override
            public void onAfter(Point[][] track, int trackIndex, int iteration,
                    int[] changeEvent) {
            }
        });

    }

    public final void testDragAndDrop() {
        final HtcReorderListView reorderListView = (HtcReorderListView) ((HtcListViewReorderDemo) mActivity).findViewById(R.id.htc_reorderlist);
        final int[] reorderListViewLocation = new int[2];
        reorderListView.getLocationOnScreen(reorderListViewLocation);
        final int startX, endX, startY, endY;
        Point pStart = ViewUtil.getViewSpecPoint(reorderListView.getChildAt(0).findViewById(R.id.my_dragger), Gravity.CENTER);
        startX = endX = pStart.x;
        startY = pStart.y;
        endY = startY + reorderListView.getHeight() * 3 / 4;
        mSolo.drag(startX, endX, startY, endY, 20);
        mSolo.sleep(2000);
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mSolo.getView("htc_reorderlist"), this);
    }

    public final void testDragAndDropRepeatedly() {
        final HtcReorderListView reorderListView = (HtcReorderListView) ((HtcListViewReorderDemo) mActivity).findViewById(R.id.htc_reorderlist);
        final int[] reorderListViewLocation = new int[2];
        reorderListView.getLocationOnScreen(reorderListViewLocation);
        int startX, endX, startY, endY;
        Point pStart = ViewUtil.getViewSpecPoint(reorderListView.getChildAt(1).findViewById(R.id.my_dragger), Gravity.CENTER);
        startX = endX = pStart.x;
        startY = pStart.y;
        endY = reorderListViewLocation[1];
        mSolo.drag(startX, endX, startY, endY, 20);
        mSolo.sleep(2000);

        endY = reorderListViewLocation[1] - 1000;
        mSolo.drag(startX, endX, startY, endY, 20);
        mSolo.sleep(2000);

        pStart = ViewUtil.getViewSpecPoint(reorderListView.getChildAt(0).findViewById(R.id.my_dragger), Gravity.CENTER);
        startX = endX = pStart.x;
        startY = endY = pStart.y;
        mSolo.drag(startX, endX, startY, endY, 20);
        mSolo.sleep(2000);

        endY = reorderListViewLocation[1] + reorderListView.getHeight();
        mSolo.drag(startX, endX, startY, endY, 20);
        mSolo.sleep(2000);

        endY = reorderListViewLocation[1] + reorderListView.getHeight() + 1000;
        mSolo.drag(startX, endX, startY, endY, 20);
        mSolo.sleep(2000);
    }

    public final void testImproveCoverage() {
        try {
            runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((HtcListViewReorderDemo) mActivity).improveCoverage();
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
