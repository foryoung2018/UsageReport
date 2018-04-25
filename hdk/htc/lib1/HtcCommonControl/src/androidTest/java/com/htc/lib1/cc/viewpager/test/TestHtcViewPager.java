
package com.htc.lib1.cc.viewpager.test;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.htc.lib1.cc.view.tabbar.TabBar;
import com.htc.lib1.cc.view.viewpager.HtcPagerAdapter;
import com.htc.lib1.cc.view.viewpager.HtcViewPager;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;
import com.htc.lib1.cc.viewpager.activityhelper.HtcViewPagerCombineTabbar;

public class TestHtcViewPager extends HtcActivityTestCaseBase {
    private HtcViewPager mHtcViewPager;

    public TestHtcViewPager() {
        super(HtcViewPagerCombineTabbar.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityIntent(null);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());

        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mHtcViewPager = new HtcViewPager(mActivity);
                mHtcViewPager.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.MATCH_PARENT));
                mActivity.setContentView(mHtcViewPager);
            }
        });

    }

    private void assertSnapShot() {
        getInstrumentation().waitForIdleSync();
        mSolo.sleep(1000);
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mHtcViewPager, this);
    }

    private void testSetAdapter(final int orientation) {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                MyAdapter adapter = new MyAdapter();
                mHtcViewPager.setAdapter(adapter);
                mHtcViewPager.setOrientation(orientation);
            }
        });
        assertSnapShot();
    }

    public final void testSetAdapterHorizontal() {
        testSetAdapter(HtcViewPager.ORIENTATION_HORIZONTAL);
    }

    public final void testSetAdapterVertical() {
        testSetAdapter(HtcViewPager.ORIENTATION_VERTICAL);
    }

    public final void testSetOnAdapterChangeListener() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                MyAdapter adapter = new MyAdapter();
                mHtcViewPager.setAdapter(adapter);
                mHtcViewPager.setOnPageChangeListener(adapter);
            }
        });
        assertSnapShot();
    }

    private void testSetCurrentItem(final int orientation) {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                MyAdapter adapter = new MyAdapter();
                mHtcViewPager.setAdapter(adapter);
                mHtcViewPager.setOrientation(orientation);
                mHtcViewPager.setOnPageChangeListener(adapter);
                mHtcViewPager.setCurrentItem(2);
            }
        });
        assertSnapShot();
    }

    public final void testSetCurrentItemHorizontal() {
        testSetCurrentItem(HtcViewPager.ORIENTATION_HORIZONTAL);
    }

    public final void testSetCurrentItemVertical() {
        testSetCurrentItem(HtcViewPager.ORIENTATION_VERTICAL);
    }

    private void testsetCurrentItem2(final int orientation) {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                MyAdapter adapter = new MyAdapter();
                mHtcViewPager.setAdapter(adapter);
                mHtcViewPager.setOrientation(orientation);
                mHtcViewPager.setOnPageChangeListener(adapter);
                mHtcViewPager.setCurrentItem(2, true);
            }
        });
        assertSnapShot();
    }

    public final void testsetCurrentItem2Horizontal() {
        testsetCurrentItem2(HtcViewPager.ORIENTATION_HORIZONTAL);
    }

    public final void testsetCurrentItem2Vertical() {
        testsetCurrentItem2(HtcViewPager.ORIENTATION_VERTICAL);
    }

    private void testsetCurrentItem3(final int orientation) {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                MyAdapter adapter = new MyAdapter();
                mHtcViewPager.setAdapter(adapter);
                mHtcViewPager.setOrientation(orientation);
                mHtcViewPager.setOnPageChangeListener(adapter);
                mHtcViewPager.setCurrentItem(2, 3);
            }
        });
        assertSnapShot();
    }

    public final void testsetCurrentItem3Horizontal() {
        testsetCurrentItem3(HtcViewPager.ORIENTATION_HORIZONTAL);
    }

    public final void testsetCurrentItem3Vertical() {
        testsetCurrentItem3(HtcViewPager.ORIENTATION_VERTICAL);
    }

    private void testGetCurrentItem(final int orientation) {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                MyAdapter adapter = new MyAdapter();
                mHtcViewPager.setAdapter(adapter);
                mHtcViewPager.setOrientation(orientation);
                mHtcViewPager.setOnPageChangeListener(adapter);
                mHtcViewPager.setCurrentItem(2);
                assertEquals(2, mHtcViewPager.getCurrentItem());
            }
        });
        assertSnapShot();

    }

    public final void testGetCurrentItemHorizontal() {
        testGetCurrentItem(HtcViewPager.ORIENTATION_HORIZONTAL);
    }

    public final void testGetCurrentItemVertical() {
        testGetCurrentItem(HtcViewPager.ORIENTATION_VERTICAL);
    }

    public final void testSetOnPageChangeListener() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                MyAdapter adapter = new MyAdapter();
                mHtcViewPager.setAdapter(adapter);
                mHtcViewPager.setOnPageChangeListener(adapter);
            }
        });
        assertSnapShot();
    }

    public final void testSetInternalPageChangeListener() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                MyAdapter adapter = new MyAdapter();
                mHtcViewPager.setAdapter(adapter);
                mHtcViewPager.setInternalPageChangeListener(adapter);
            }
        });
        assertSnapShot();

    }

    public final void testGetOffscreenPageLimit() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                MyAdapter adapter = new MyAdapter();
                mHtcViewPager.setAdapter(adapter);
                mHtcViewPager.setOffscreenPageLimit(3);
                assertEquals(3, mHtcViewPager.getOffscreenPageLimit());
            }
        });
        assertSnapShot();

    }

    public final void testSetOffscreenPageLimit() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                MyAdapter adapter = new MyAdapter();
                mHtcViewPager.setAdapter(adapter);
                mHtcViewPager.setOffscreenPageLimit(3);
            }
        });
        assertSnapShot();
    }

    private void testSetPageMargin(final int orientation) {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                MyAdapter adapter = new MyAdapter();
                mHtcViewPager.setAdapter(adapter);
                mHtcViewPager.setOrientation(orientation);
                mHtcViewPager.setPageMargin(100);
            }
        });
        assertSnapShot();
    }

    public final void testSetPageMarginHorizontal() {
        testSetPageMargin(HtcViewPager.ORIENTATION_HORIZONTAL);
    }

    public final void testSetPageMarginVertical() {
        testSetPageMargin(HtcViewPager.ORIENTATION_VERTICAL);
    }

    public final void testGetPageMargin() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                MyAdapter adapter = new MyAdapter();
                mHtcViewPager.setAdapter(adapter);
                mHtcViewPager.setPageMargin(100);
                assertEquals(100, mHtcViewPager.getPageMargin());
            }
        });
        assertSnapShot();

    }

    private void testSetPageMarginColorDrawable(final int orientation) {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                MyAdapter adapter = new MyAdapter();
                mHtcViewPager.setAdapter(adapter);
                mHtcViewPager.setOrientation(orientation);
                mHtcViewPager.setPageMargin(100);
                mHtcViewPager.setPageMarginDrawable(new ColorDrawable(Color.YELLOW));
            }
        });
        assertSnapShot();
    }

    public final void testSetPageMarginColorDrawableHorizontal() {
        testSetPageMarginColorDrawable(HtcViewPager.ORIENTATION_HORIZONTAL);
    }

    public final void testSetPageMarginColorDrawableVertical() {
        testSetPageMarginColorDrawable(HtcViewPager.ORIENTATION_VERTICAL);
    }

    private void testSetPageMarginDrawable(final int orientation) {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                MyAdapter adapter = new MyAdapter();
                mHtcViewPager.setAdapter(adapter);
                mHtcViewPager.setOrientation(orientation);
                mHtcViewPager.setPageMargin(100);
                mHtcViewPager.setPageMarginDrawable(android.R.drawable.gallery_thumb);
            }
        });
        assertSnapShot();
    }

    public final void testSetPageMarginDrawableHorizontal() {
        testSetPageMarginDrawable(HtcViewPager.ORIENTATION_HORIZONTAL);
    }

    public final void testSetPageMarginDrawableVertical() {
        testSetPageMarginDrawable(HtcViewPager.ORIENTATION_VERTICAL);
    }

    public final void testAddView(final int orientation) {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {

                TabBar tabbar = new TabBar(mActivity);
                tabbar.linkWithParent(mHtcViewPager);
                HtcViewPager.LayoutParams params = new HtcViewPager.LayoutParams();
                params.gravity = Gravity.TOP;
                mHtcViewPager.addView(tabbar, params);

                MyAdapter adapter = new MyAdapter();
                mHtcViewPager.setOrientation(orientation);
                mHtcViewPager.setAdapter(adapter);

            }
        });
        assertSnapShot();
    }

    public final void testAddViewHorizontal() {
        testAddView(HtcViewPager.ORIENTATION_HORIZONTAL);
    }

    public final void testAddViewVertical() {
        testAddView(HtcViewPager.ORIENTATION_VERTICAL);
    }

    private void testRemoveView(final int orientation) {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                MyAdapter adapter = new MyAdapter();
                mHtcViewPager.setAdapter(adapter);
                mHtcViewPager.setOrientation(orientation);

                TabBar tabbar = new TabBar(mActivity);
                HtcViewPager.LayoutParams params = new HtcViewPager.LayoutParams();
                params.gravity = Gravity.TOP;
                params.width = HtcViewPager.LayoutParams.MATCH_PARENT;
                tabbar.setLayoutParams(params);
                mHtcViewPager.addView(tabbar);
                mHtcViewPager.removeView(tabbar);
            }
        });
        assertSnapShot();
    }

    public final void testRemoveViewHorizontal() {
        testRemoveView(HtcViewPager.ORIENTATION_HORIZONTAL);
    }

    public final void testRemoveViewVertical() {
        testRemoveView(HtcViewPager.ORIENTATION_VERTICAL);
    }

    public final void testComputeScroll(final int orientation) {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                MyAdapter adapter = new MyAdapter();
                mHtcViewPager.setAdapter(adapter);
                mHtcViewPager.setOrientation(orientation);
                mHtcViewPager.setCurrentItem(2);
                mHtcViewPager.computeScroll();
            }
        });
        assertSnapShot();
    }

    public final void testComputeScrollHorizontal() {
        testComputeScroll(HtcViewPager.ORIENTATION_HORIZONTAL);
    }

    public final void testComputeScrollVertical() {
        testComputeScroll(HtcViewPager.ORIENTATION_VERTICAL);
    }

    private void testBeginFakeDrag(final int orientation) {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                MyAdapter adapter = new MyAdapter();
                mHtcViewPager.setAdapter(adapter);
                mHtcViewPager.setOrientation(orientation);
                mHtcViewPager.setCurrentItem(2);
                mHtcViewPager.beginFakeDrag();
            }
        });
        assertSnapShot();
    }

    public final void testBeginFakeDragHorizontal() {
        testBeginFakeDrag(HtcViewPager.ORIENTATION_HORIZONTAL);
    }

    public final void testBeginFakeDragVertical() {
        testBeginFakeDrag(HtcViewPager.ORIENTATION_VERTICAL);
    }

    public final void testEndFakeDrag(final int orientation) {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                MyAdapter adapter = new MyAdapter();
                mHtcViewPager.setAdapter(adapter);
                mHtcViewPager.setOrientation(orientation);
                mHtcViewPager.setCurrentItem(2);
                mHtcViewPager.beginFakeDrag();
                mHtcViewPager.endFakeDrag();
            }
        });
        assertSnapShot();

    }

    public final void testEndFakeDragHorizontal() {
        testEndFakeDrag(HtcViewPager.ORIENTATION_HORIZONTAL);
    }

    public final void testEndFakeDragVertical() {
        testEndFakeDrag(HtcViewPager.ORIENTATION_VERTICAL);
    }

    private void testFakeDragBy(final int orientation) {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                MyAdapter adapter = new MyAdapter();
                mHtcViewPager.setAdapter(adapter);
                mHtcViewPager.setOrientation(orientation);
                mHtcViewPager.setCurrentItem(2);
                mHtcViewPager.beginFakeDrag();
                mHtcViewPager.fakeDragBy(30);
            }
        });
        assertSnapShot();
    }

    public final void testFakeDragByHorizontal() {
        testFakeDragBy(HtcViewPager.ORIENTATION_HORIZONTAL);
    }

    public final void testFakeDragByVertical() {
        testFakeDragBy(HtcViewPager.ORIENTATION_VERTICAL);
    }

    private void testIsFakeDragging(final int orientation) {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                MyAdapter adapter = new MyAdapter();
                mHtcViewPager.setAdapter(adapter);
                mHtcViewPager.setOrientation(orientation);
                mHtcViewPager.setCurrentItem(2);
                mHtcViewPager.beginFakeDrag();
                assertTrue(mHtcViewPager.isFakeDragging());
            }
        });
        assertSnapShot();

    }

    public final void testIsFakeDraggingHorizontal() {
        testIsFakeDragging(HtcViewPager.ORIENTATION_HORIZONTAL);
    }

    public final void testIsFakeDraggingVertical() {
        testIsFakeDragging(HtcViewPager.ORIENTATION_VERTICAL);
    }

    public final void testArrowScroll(final int orientation) {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                MyAdapter adapter = new MyAdapter();
                mHtcViewPager.setAdapter(adapter);
                mHtcViewPager.setOrientation(orientation);
                mHtcViewPager.setCurrentItem(2);
                mHtcViewPager.arrowScroll(View.FOCUS_RIGHT);
            }
        });
        assertSnapShot();
    }

    public final void testArrowScrollHorizontal() {
        testArrowScroll(HtcViewPager.ORIENTATION_HORIZONTAL);
    }

    public final void testArrowScrollVertical() {
        testArrowScroll(HtcViewPager.ORIENTATION_VERTICAL);
    }

    public final void testSetAutoRequestFocus() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                MyAdapter adapter = new MyAdapter();
                mHtcViewPager.setAdapter(adapter);
                mHtcViewPager.setCurrentItem(2);
                mHtcViewPager.setAutoRequestFocus(true);
            }
        });
        assertSnapShot();
    }

    public final void testGetOrientation() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                MyAdapter adapter = new MyAdapter();
                mHtcViewPager.setAdapter(adapter);
                mHtcViewPager.setCurrentItem(2);
                mHtcViewPager.setOrientation(HtcViewPager.ORIENTATION_VERTICAL);
                assertEquals(HtcViewPager.ORIENTATION_VERTICAL, mHtcViewPager.getOrientation());
            }
        });
        assertSnapShot();

    }

    private static class MyAdapter extends HtcPagerAdapter implements HtcViewPager.OnPageChangeListener {
        public MyAdapter() {
        }

        @Override
        public int getPageCount(int position) {
            return position;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public CharSequence getPageTitle(int position) {
            return "test " + position;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            Context ctx = container.getContext();
            TextView content = new TextView(ctx);
            content.setTextSize(50);
            content.setGravity(Gravity.CENTER);
            content.setBackgroundColor(position % 2 == 0 ? Color.RED : Color.BLUE);
            content.setText("Page " + position);

            container.addView(content);
            return content;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int i) {
        }
    }

}
