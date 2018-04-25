
package com.htc.lib1.cc.adapterview.test;

import android.animation.AnimatorSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.htc.lib1.cc.adapterview.activityhelper.HtcGridViewOverFlingDemo;
import com.htc.lib1.cc.widget.HtcGridView;
import com.htc.test.HtcActivityTestCaseBase;

import java.lang.reflect.Field;

public class HtcGridViewOverFlingDemoTest extends
        HtcActivityTestCaseBase {

    private HtcGridView mHtcGridView;
    private GridView mGridView;
    private AnimatorSet mOverFlingBouncing;

    public HtcGridViewOverFlingDemoTest() {
        super(HtcGridViewOverFlingDemo.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        initActivity();
        mHtcGridView = (HtcGridView) ((HtcGridViewOverFlingDemo) mActivity).getHtcGridView();
        mGridView = (GridView) ((HtcGridViewOverFlingDemo) mActivity).getGridView();
    }

    public void testHtcGridViewDetachedDuringOverFling() {
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                mGridView.setVisibility(View.GONE);
                mHtcGridView.setVisibility(View.VISIBLE);
            }
        });
        mSolo.waitForView(mHtcGridView);
        mHtcGridView.smoothScrollToPositionFromTop(5, 0);
        getInstrumentation().waitForIdleSync();
        mHtcGridView.smoothScrollBy(-5000, 200);

        removeViewDuringOverFling(mHtcGridView);

        getInstrumentation().waitForIdleSync();
    }

    //
    void removeViewDuringOverFling(Object grid) {
        final View gridview = (View) grid;
        gridview.postOnAnimation(new Runnable() {
            public void run() {
                mOverFlingBouncing = getBouncingAnimatorSet(gridview);
                if (mOverFlingBouncing != null && mOverFlingBouncing.isStarted()) {
                    ((ViewGroup) gridview.getParent()).removeView(gridview);
                    return;
                }
                gridview.postOnAnimation(this);
            }
        });
    }

    //
    AnimatorSet getBouncingAnimatorSet(Object list) {
        Class klass = null;
        try {
            //            if (list instanceof HtcAbsListView) {
            //                klass = Class.forName("com.htc.widget.HtcAbsListView");
            //            } else if (list instanceof AbsListView) {
            //                klass = Class.forName("android.widget.AbsListView");
            //            }
            klass = Class.forName("com.htc.lib1.cc.widget.HtcGridView");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        AnimatorSet set = null;
        if (klass != null) {
            Field field = null;
            try {
                field = klass.getDeclaredField("mOverFlingBouncing");
                field.setAccessible(true);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            if (field != null) {
                try {
                    set = (AnimatorSet) field.get(list);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return set;
    }
}
