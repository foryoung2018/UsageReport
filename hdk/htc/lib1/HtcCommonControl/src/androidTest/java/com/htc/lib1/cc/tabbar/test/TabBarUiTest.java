
package com.htc.lib1.cc.tabbar.test;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.htc.lib1.cc.view.tabbar.TabBar;
import com.htc.lib1.cc.view.tabbar.TabBarUtils;
import com.htc.lib1.cc.view.viewpager.HtcPagerAdapter;
import com.htc.lib1.cc.view.viewpager.HtcViewPager;
import com.htc.lib1.cc.tabbar.activityhelper.TabBarAutActivity;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;

import java.util.ArrayList;
import java.util.List;

public class TabBarUiTest extends HtcActivityTestCaseBase {

    private HtcViewPager mHtcViewPager;
    private TabBar mTabBar;

    public TabBarUiTest() {
        super(TabBarAutActivity.class);
    }

    private void assertSnapShot() {
        getInstrumentation().waitForIdleSync();
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mTabBar, this);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        initActivity();
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mHtcViewPager = new HtcViewPager(mActivity);
                mHtcViewPager.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.MATCH_PARENT));
                mActivity.setContentView(mHtcViewPager);

                mTabBar = new TabBar(mActivity);
                mTabBar.linkWithParent(mHtcViewPager);
                HtcViewPager.LayoutParams params = new HtcViewPager.LayoutParams();
                params.gravity = Gravity.TOP;
                params.height = mTabBar.getBarHeight();
                mHtcViewPager.addView(mTabBar, params);

            }
        });
    }

    public final void testPhoneMode() {
        initTabBar(false, false);
        assertSnapShot();
    }

    public final void testPhoneMode_CNMode() {
        initTabBar(false, true);
        assertSnapShot();
    }

    public final void testAutoMode() {
        initTabBar(true, false);
        assertSnapShot();
    }

    public final void testAutoMode_CNMode() {
        initTabBar(true, true);
        assertSnapShot();
    }

    public final void testSetBarHeight() {
        initTabBar(false, false);

        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                TypedValue typedValue = new TypedValue();
                mTabBar.setBarHeight((int) typedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, mActivity.getResources().getDisplayMetrics()));
            }
        });
        assertSnapShot();

    }

    public final void testGetBarHeight_PhoneMode() {
        initTabBar(false, false);

        getInstrumentation().waitForIdleSync();
        mSolo.sleep(1000);
        assertEquals(TabBarUtils.dimen.height(mActivity, false), mTabBar.getHeight());

        assertSnapShot();

    }

    public final void testGetBarHeight_AutoMode() {
        initTabBar(true, false);

        getInstrumentation().waitForIdleSync();
        mSolo.sleep(1000);
        assertEquals(TabBarUtils.dimen.height(mActivity, true), mTabBar.getHeight());

        assertSnapShot();

    }

    public final void testGetBarHeight_Phone_CustomValue() {
        initTabBar(false, false);

        TypedValue typedValue = new TypedValue();
        final int height = (int) typedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, mActivity.getResources().getDisplayMetrics());
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mTabBar.setBarHeight(height);
            }
        });

        getInstrumentation().waitForIdleSync();
        mSolo.sleep(1000);
        assertEquals(height, mTabBar.getHeight());

        assertSnapShot();
    }

    public final void testGetBarHeight_Auto_CustomValue() {
        initTabBar(true, false);

        TypedValue typedValue = new TypedValue();
        final int height = (int) typedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, mActivity.getResources().getDisplayMetrics());
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mTabBar.setBarHeight(height);
            }
        });

        getInstrumentation().waitForIdleSync();
        mSolo.sleep(1000);
        assertEquals(height, mTabBar.getHeight());

        assertSnapShot();
    }

    public final void testClickTabBar_PhoneMode() {
        initTabBar(false, false);
        runMockClickTabBar();
    }

    public final void testClickTabBar_AutoMode() {
        initTabBar(true, false);
        runMockClickTabBar();
    }

    private void runMockClickTabBar() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mHtcViewPager.setCurrentItem(1, false);
            }
        });
        getInstrumentation().waitForIdleSync();
        mSolo.sleep(1000);
        assertSnapShot();
    }

    private void initTabBar(final boolean isAutoMode, final boolean isCNMode) {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                MyAdapter adapter = new MyAdapter(isAutoMode, isCNMode);
                mHtcViewPager.setAdapter(adapter);
            }
        });
    }

    private static class MyAdapter extends HtcPagerAdapter {
        private List<String> data = new ArrayList<String>();
        private boolean mIsAutomotive = false;
        private boolean mIsCNMode = false;

        public MyAdapter(boolean isAutomotive, boolean isCNMode) {
            mIsAutomotive = isAutomotive;
            mIsCNMode = isCNMode;
            for (int i = 0; i < 5; i++) {
                data.add("Title" + (i + 1));
            }
        }

        @Override
        public boolean isAutomotiveMode() {
            return mIsAutomotive;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return data.get(position);
        }

        @Override
        public int getPageCount(int position) {
            return position;
        }

        @Override
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

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public boolean isCNMode() {
            return mIsCNMode;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

}
