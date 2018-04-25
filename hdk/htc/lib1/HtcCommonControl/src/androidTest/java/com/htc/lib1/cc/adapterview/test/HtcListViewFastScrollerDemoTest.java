
package com.htc.lib1.cc.adapterview.test;

import android.graphics.Point;
import android.util.DisplayMetrics;

import com.htc.lib1.cc.adapterview.activityhelper.HtcListViewFastScrollerDemo;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.HtcListView;
import com.htc.test.HtcActivityTestCase;
import com.htc.test.util.DragUtil;

public class HtcListViewFastScrollerDemoTest extends
        HtcActivityTestCase {
    private HtcListView mListView;

    public HtcListViewFastScrollerDemoTest() {
        super(HtcListViewFastScrollerDemo.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        initActivity();
        mListView = (HtcListView) ((HtcListViewFastScrollerDemo) mActivity).findViewById(R.id.list);
    }

    public final void testOnCreateBundle() {
        assertNotNull(mActivity);

        DisplayMetrics dm = mActivity.getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        mSolo.drag(200, 200, height - 10, height - 400, 20);
        mSolo.drag(200, 200, height - 400, height - 10, 20);

        switch (width) {
            case 1080: // FULLHD
                mSolo.drag(width - 10, width - 10, 300, height - 50, 40);
                mSolo.sleep(500);
                break;
            case 720: // HD
                mSolo.drag(width - 10, width - 10, 200, height - 50, 40);
                mSolo.sleep(500);
                break;
            default: // QHD, WVGA
                mSolo.drag(width - 10, width - 10, 150, height - 50, 40);
                mSolo.sleep(500);
        }
    }

    public void testMultiFingerScroll() {
        mSolo.waitForView(mListView);

        int[] xy = new int[2];
        mListView.getLocationOnScreen(xy);

        int scrollYDistanceUnit = mListView.getHeight() >> 2; // 1/4 * height
        int scrollXDistanceUnit = mListView.getWidth() >> 2; // 1/4 * width

        Point pRightStart = new Point(xy[0] + scrollXDistanceUnit * 3, xy[1]);
        Point pRightStop = new Point(pRightStart.x, pRightStart.y + scrollYDistanceUnit * 3);
        Point[] rightTrack = DragUtil.genDragTrack(pRightStart, pRightStop, 50);

        Point pLeftStart = new Point(xy[0] + scrollXDistanceUnit, xy[1] + scrollYDistanceUnit);
        Point pLeftStop = new Point(pLeftStart.x, pLeftStart.y + scrollYDistanceUnit * 3);
        Point[] leftTrack = DragUtil.genDragTrack(pLeftStart, pLeftStop, 50);

        DragUtil.dragTabByTrack(getInstrumentation(), new Point[][] {
                leftTrack, rightTrack
        }, new DragUtil.GestureCallBack() {

            @Override
            public void onBefore(Point[][] track, int trackIndex,
                    int iteration, int[] changeEvent) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAfter(Point[][] track, int trackIndex, int iteration,
                    int[] changeEvent) {
                // TODO Auto-generated method stub

            }
        });
    }

    public final void testImproveCoverage() {
        initActivity();
        try {
            runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((HtcListViewFastScrollerDemo) mActivity).improveCoverage();
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
