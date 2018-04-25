
package com.htc.lib1.cc.drawer.test.slidingmenu;

import android.content.Intent;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.ViewConfiguration;
import android.widget.TextView;

import com.htc.lib1.cc.drawer.activityhelper.slidingmenu.SlidingMenuActivity;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.view.viewpager.HtcViewPager;
import com.htc.lib1.cc.widget.SlidingMenu;
import com.htc.lib1.cc.widget.SlidingMenu.OnCloseListener;
import com.htc.lib1.cc.widget.SlidingMenu.OnClosedListener;
import com.htc.lib1.cc.widget.SlidingMenu.OnOpenListener;
import com.htc.lib1.cc.widget.SlidingMenu.OnOpenedListener;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.DragUtil;
import com.htc.test.util.ScreenShotUtil;

import java.lang.reflect.Field;

public class SlidingMenuTest extends HtcActivityTestCaseBase {
    private static final int STEP_COUNT = 20;
    private static final int STEP_COUNT_TINY = 5;
    private static final int OFFSET = 10;
    private static final int LTR = 0;
    private static final int RTL = 1;

    private static int mScreenWidth = 0;
    private static int mScreenHeight = 0;

    private static final float DIM_MIDDLE_SCALE = 0.9f;

    private static final int TOGGLE_ONCE = 1;
    private static final int TOGGLE_TWICE = 2;

    private static int mTouchMode = -1;

    public SlidingMenuTest() {
        super(SlidingMenuActivity.class);
    }

    /* Test Matrix Start */

    public void test_M_LEFT_W_T_L() {
        test(SlidingMenu.TOUCHMODE_MARGIN, SlidingMenu.LEFT, SlidingMenu.SLIDING_WINDOW, true, LTR);
    }

    public void test_M_LEFT_W_T_R() {
        test(SlidingMenu.TOUCHMODE_MARGIN, SlidingMenu.LEFT, SlidingMenu.SLIDING_WINDOW, true, RTL);
    }

    public void test_M_LEFT_W_F_L() {
        test(SlidingMenu.TOUCHMODE_MARGIN, SlidingMenu.LEFT, SlidingMenu.SLIDING_WINDOW, false, LTR);
    }

    public void test_M_LEFT_W_F_R() {
        test(SlidingMenu.TOUCHMODE_MARGIN, SlidingMenu.LEFT, SlidingMenu.SLIDING_WINDOW, false, RTL);
    }

    public void test_M_LEFT_C_T_L() {
        test(SlidingMenu.TOUCHMODE_MARGIN, SlidingMenu.LEFT, SlidingMenu.SLIDING_CONTENT, true, LTR);
    }

    public void test_M_LEFT_C_T_R() {
        test(SlidingMenu.TOUCHMODE_MARGIN, SlidingMenu.LEFT, SlidingMenu.SLIDING_CONTENT, true, RTL);
    }

    public void test_M_LEFT_C_F_L() {
        test(SlidingMenu.TOUCHMODE_MARGIN, SlidingMenu.LEFT, SlidingMenu.SLIDING_CONTENT, false,
                LTR);
    }

    public void test_M_LEFT_C_F_R() {
        test(SlidingMenu.TOUCHMODE_MARGIN, SlidingMenu.LEFT, SlidingMenu.SLIDING_CONTENT, false,
                RTL);
    }

    public void test_M_RIGHT_W_T_L() {
        test(SlidingMenu.TOUCHMODE_MARGIN, SlidingMenu.RIGHT, SlidingMenu.SLIDING_WINDOW, true, LTR);
    }

    public void test_M_RIGHT_W_T_R() {
        test(SlidingMenu.TOUCHMODE_MARGIN, SlidingMenu.RIGHT, SlidingMenu.SLIDING_WINDOW, true, RTL);
    }

    public void test_M_RIGHT_W_F_L() {
        test(SlidingMenu.TOUCHMODE_MARGIN, SlidingMenu.RIGHT, SlidingMenu.SLIDING_WINDOW, false,
                LTR);
    }

    public void test_M_RIGHT_W_F_R() {
        test(SlidingMenu.TOUCHMODE_MARGIN, SlidingMenu.RIGHT, SlidingMenu.SLIDING_WINDOW, false,
                RTL);
    }

    public void test_M_RIGHT_C_T_L() {
        test(SlidingMenu.TOUCHMODE_MARGIN, SlidingMenu.RIGHT, SlidingMenu.SLIDING_CONTENT, true,
                LTR);
    }

    public void test_M_RIGHT_C_T_R() {
        test(SlidingMenu.TOUCHMODE_MARGIN, SlidingMenu.RIGHT, SlidingMenu.SLIDING_CONTENT, true,
                RTL);
    }

    public void test_M_RIGHT_C_F_L() {
        test(SlidingMenu.TOUCHMODE_MARGIN, SlidingMenu.RIGHT, SlidingMenu.SLIDING_CONTENT, false,
                LTR);
    }

    public void test_M_RIGHT_C_F_R() {
        test(SlidingMenu.TOUCHMODE_MARGIN, SlidingMenu.RIGHT, SlidingMenu.SLIDING_CONTENT, false,
                RTL);
    }

    public void test_M_LEFT_RIGHT_W_T_L() {
        test(SlidingMenu.TOUCHMODE_MARGIN, SlidingMenu.LEFT_RIGHT, SlidingMenu.SLIDING_WINDOW,
                true, LTR);
    }

    public void test_M_LEFT_RIGHT_W_T_R() {
        test(SlidingMenu.TOUCHMODE_MARGIN, SlidingMenu.LEFT_RIGHT, SlidingMenu.SLIDING_WINDOW,
                true, RTL);
    }

    public void test_M_LEFT_RIGHT_W_F_L() {
        test(SlidingMenu.TOUCHMODE_MARGIN, SlidingMenu.LEFT_RIGHT, SlidingMenu.SLIDING_WINDOW,
                false, LTR);
    }

    public void test_M_LEFT_RIGHT_W_F_R() {
        test(SlidingMenu.TOUCHMODE_MARGIN, SlidingMenu.LEFT_RIGHT, SlidingMenu.SLIDING_WINDOW,
                false, RTL);
    }

    public void test_M_LEFT_RIGHT_C_T_L() {
        test(SlidingMenu.TOUCHMODE_MARGIN, SlidingMenu.LEFT_RIGHT, SlidingMenu.SLIDING_CONTENT,
                true, LTR);
    }

    public void test_M_LEFT_RIGHT_C_T_R() {
        test(SlidingMenu.TOUCHMODE_MARGIN, SlidingMenu.LEFT_RIGHT, SlidingMenu.SLIDING_CONTENT,
                true, RTL);
    }

    public void test_M_LEFT_RIGHT_C_F_L() {
        test(SlidingMenu.TOUCHMODE_MARGIN, SlidingMenu.LEFT_RIGHT, SlidingMenu.SLIDING_CONTENT,
                false, LTR);
    }

    public void test_M_LEFT_RIGHT_C_F_R() {
        test(SlidingMenu.TOUCHMODE_MARGIN, SlidingMenu.LEFT_RIGHT, SlidingMenu.SLIDING_CONTENT,
                false, RTL);
    }

    public void test_F_LEFT_W_T_L() {
        test(SlidingMenu.TOUCHMODE_FULLSCREEN, SlidingMenu.LEFT, SlidingMenu.SLIDING_WINDOW, true,
                LTR);
    }

    public void test_F_LEFT_W_T_R() {
        test(SlidingMenu.TOUCHMODE_FULLSCREEN, SlidingMenu.LEFT, SlidingMenu.SLIDING_WINDOW, true,
                RTL);
    }

    public void test_F_LEFT_W_F_L() {
        test(SlidingMenu.TOUCHMODE_FULLSCREEN, SlidingMenu.LEFT, SlidingMenu.SLIDING_WINDOW, false,
                LTR);
    }

    public void test_F_LEFT_W_F_R() {
        test(SlidingMenu.TOUCHMODE_FULLSCREEN, SlidingMenu.LEFT, SlidingMenu.SLIDING_WINDOW, false,
                RTL);
    }

    public void test_F_LEFT_C_T_L() {
        test(SlidingMenu.TOUCHMODE_FULLSCREEN, SlidingMenu.LEFT, SlidingMenu.SLIDING_CONTENT, true,
                LTR);
    }

    public void test_F_LEFT_C_T_R() {
        test(SlidingMenu.TOUCHMODE_FULLSCREEN, SlidingMenu.LEFT, SlidingMenu.SLIDING_CONTENT, true,
                RTL);
    }

    public void test_F_LEFT_C_F_L() {
        test(SlidingMenu.TOUCHMODE_FULLSCREEN, SlidingMenu.LEFT, SlidingMenu.SLIDING_CONTENT,
                false, LTR);
    }

    public void test_F_LEFT_C_F_R() {
        test(SlidingMenu.TOUCHMODE_FULLSCREEN, SlidingMenu.LEFT, SlidingMenu.SLIDING_CONTENT,
                false, RTL);
    }

    public void test_F_RIGHT_W_T_L() {
        test(SlidingMenu.TOUCHMODE_FULLSCREEN, SlidingMenu.RIGHT, SlidingMenu.SLIDING_WINDOW, true,
                LTR);
    }

    public void test_F_RIGHT_W_T_R() {
        test(SlidingMenu.TOUCHMODE_FULLSCREEN, SlidingMenu.RIGHT, SlidingMenu.SLIDING_WINDOW, true,
                RTL);
    }

    public void test_F_RIGHT_W_F_L() {
        test(SlidingMenu.TOUCHMODE_FULLSCREEN, SlidingMenu.RIGHT, SlidingMenu.SLIDING_WINDOW,
                false, LTR);
    }

    public void test_F_RIGHT_W_F_R() {
        test(SlidingMenu.TOUCHMODE_FULLSCREEN, SlidingMenu.RIGHT, SlidingMenu.SLIDING_WINDOW,
                false, RTL);
    }

    public void test_F_RIGHT_C_T_L() {
        test(SlidingMenu.TOUCHMODE_FULLSCREEN, SlidingMenu.RIGHT, SlidingMenu.SLIDING_CONTENT,
                true, LTR);
    }

    public void test_F_RIGHT_C_T_R() {
        test(SlidingMenu.TOUCHMODE_FULLSCREEN, SlidingMenu.RIGHT, SlidingMenu.SLIDING_CONTENT,
                true, RTL);
    }

    public void test_F_RIGHT_C_F_L() {
        test(SlidingMenu.TOUCHMODE_FULLSCREEN, SlidingMenu.RIGHT, SlidingMenu.SLIDING_CONTENT,
                false, LTR);
    }

    public void test_F_RIGHT_C_F_R() {
        test(SlidingMenu.TOUCHMODE_FULLSCREEN, SlidingMenu.RIGHT, SlidingMenu.SLIDING_CONTENT,
                false, RTL);
    }

    public void test_F_LEFT_RIGHT_W_T_L() {
        test(SlidingMenu.TOUCHMODE_FULLSCREEN, SlidingMenu.LEFT_RIGHT, SlidingMenu.SLIDING_WINDOW,
                true, LTR);
    }

    public void test_F_LEFT_RIGHT_W_T_R() {
        test(SlidingMenu.TOUCHMODE_FULLSCREEN, SlidingMenu.LEFT_RIGHT, SlidingMenu.SLIDING_WINDOW,
                true, RTL);
    }

    public void test_F_LEFT_RIGHT_W_F_L() {
        test(SlidingMenu.TOUCHMODE_FULLSCREEN, SlidingMenu.LEFT_RIGHT, SlidingMenu.SLIDING_WINDOW,
                false, LTR);
    }

    public void test_F_LEFT_RIGHT_W_F_R() {
        test(SlidingMenu.TOUCHMODE_FULLSCREEN, SlidingMenu.LEFT_RIGHT, SlidingMenu.SLIDING_WINDOW,
                false, RTL);
    }

    public void test_F_LEFT_RIGHT_C_T_L() {
        test(SlidingMenu.TOUCHMODE_FULLSCREEN, SlidingMenu.LEFT_RIGHT, SlidingMenu.SLIDING_CONTENT,
                true, LTR);
    }

    public void test_F_LEFT_RIGHT_C_T_R() {
        test(SlidingMenu.TOUCHMODE_FULLSCREEN, SlidingMenu.LEFT_RIGHT, SlidingMenu.SLIDING_CONTENT,
                true, RTL);
    }

    public void test_F_LEFT_RIGHT_C_F_L() {
        test(SlidingMenu.TOUCHMODE_FULLSCREEN, SlidingMenu.LEFT_RIGHT, SlidingMenu.SLIDING_CONTENT,
                false, LTR);
    }

    public void test_F_LEFT_RIGHT_C_F_R() {
        test(SlidingMenu.TOUCHMODE_FULLSCREEN, SlidingMenu.LEFT_RIGHT, SlidingMenu.SLIDING_CONTENT,
                false, RTL);
    }

    public void test_N_LEFT_W_T_L() {
        test(SlidingMenu.TOUCHMODE_NONE, SlidingMenu.LEFT, SlidingMenu.SLIDING_WINDOW, true, LTR);
    }

    public void test_N_LEFT_W_T_R() {
        test(SlidingMenu.TOUCHMODE_NONE, SlidingMenu.LEFT, SlidingMenu.SLIDING_WINDOW, true, RTL);
    }

    public void test_N_LEFT_W_F_L() {
        test(SlidingMenu.TOUCHMODE_NONE, SlidingMenu.LEFT, SlidingMenu.SLIDING_WINDOW, false, LTR);
    }

    public void test_N_LEFT_W_F_R() {
        test(SlidingMenu.TOUCHMODE_NONE, SlidingMenu.LEFT, SlidingMenu.SLIDING_WINDOW, false, RTL);
    }

    public void test_N_LEFT_C_T_L() {
        test(SlidingMenu.TOUCHMODE_NONE, SlidingMenu.LEFT, SlidingMenu.SLIDING_CONTENT, true, LTR);
    }

    public void test_N_LEFT_C_T_R() {
        test(SlidingMenu.TOUCHMODE_NONE, SlidingMenu.LEFT, SlidingMenu.SLIDING_CONTENT, true, RTL);
    }

    public void test_N_LEFT_C_F_L() {
        test(SlidingMenu.TOUCHMODE_NONE, SlidingMenu.LEFT, SlidingMenu.SLIDING_CONTENT, false, LTR);
    }

    public void test_N_LEFT_C_F_R() {
        test(SlidingMenu.TOUCHMODE_NONE, SlidingMenu.LEFT, SlidingMenu.SLIDING_CONTENT, false, RTL);
    }

    public void test_N_RIGHT_W_T_L() {
        test(SlidingMenu.TOUCHMODE_NONE, SlidingMenu.RIGHT, SlidingMenu.SLIDING_WINDOW, true, LTR);
    }

    public void test_N_RIGHT_W_T_R() {
        test(SlidingMenu.TOUCHMODE_NONE, SlidingMenu.RIGHT, SlidingMenu.SLIDING_WINDOW, true, RTL);
    }

    public void test_N_RIGHT_W_F_L() {
        test(SlidingMenu.TOUCHMODE_NONE, SlidingMenu.RIGHT, SlidingMenu.SLIDING_WINDOW, false, LTR);
    }

    public void test_N_RIGHT_W_F_R() {
        test(SlidingMenu.TOUCHMODE_NONE, SlidingMenu.RIGHT, SlidingMenu.SLIDING_WINDOW, false, RTL);
    }

    public void test_N_RIGHT_C_T_L() {
        test(SlidingMenu.TOUCHMODE_NONE, SlidingMenu.RIGHT, SlidingMenu.SLIDING_CONTENT, true, LTR);
    }

    public void test_N_RIGHT_C_T_R() {
        test(SlidingMenu.TOUCHMODE_NONE, SlidingMenu.RIGHT, SlidingMenu.SLIDING_CONTENT, true, RTL);
    }

    public void test_N_RIGHT_C_F_L() {
        test(SlidingMenu.TOUCHMODE_NONE, SlidingMenu.RIGHT, SlidingMenu.SLIDING_CONTENT, false, LTR);
    }

    public void test_N_RIGHT_C_F_R() {
        test(SlidingMenu.TOUCHMODE_NONE, SlidingMenu.RIGHT, SlidingMenu.SLIDING_CONTENT, false, RTL);
    }

    public void test_N_LEFT_RIGHT_W_T_L() {
        test(SlidingMenu.TOUCHMODE_NONE, SlidingMenu.LEFT_RIGHT, SlidingMenu.SLIDING_WINDOW, true,
                LTR);
    }

    public void test_N_LEFT_RIGHT_W_T_R() {
        test(SlidingMenu.TOUCHMODE_NONE, SlidingMenu.LEFT_RIGHT, SlidingMenu.SLIDING_WINDOW, true,
                RTL);
    }

    public void test_N_LEFT_RIGHT_W_F_L() {
        test(SlidingMenu.TOUCHMODE_NONE, SlidingMenu.LEFT_RIGHT, SlidingMenu.SLIDING_WINDOW, false,
                LTR);
    }

    public void test_N_LEFT_RIGHT_W_F_R() {
        test(SlidingMenu.TOUCHMODE_NONE, SlidingMenu.LEFT_RIGHT, SlidingMenu.SLIDING_WINDOW, false,
                RTL);
    }

    public void test_N_LEFT_RIGHT_C_T_L() {
        test(SlidingMenu.TOUCHMODE_NONE, SlidingMenu.LEFT_RIGHT, SlidingMenu.SLIDING_CONTENT, true,
                LTR);
    }

    public void test_N_LEFT_RIGHT_C_T_R() {
        test(SlidingMenu.TOUCHMODE_NONE, SlidingMenu.LEFT_RIGHT, SlidingMenu.SLIDING_CONTENT, true,
                RTL);
    }

    public void test_N_LEFT_RIGHT_C_F_L() {
        test(SlidingMenu.TOUCHMODE_NONE, SlidingMenu.LEFT_RIGHT, SlidingMenu.SLIDING_CONTENT,
                false, LTR);
    }

    public void test_N_LEFT_RIGHT_C_F_R() {
        test(SlidingMenu.TOUCHMODE_NONE, SlidingMenu.LEFT_RIGHT, SlidingMenu.SLIDING_CONTENT,
                false, RTL);
    }

    /* Test Matrix End */

    private void test(int touchMode, int displayMode, int slideStyle, boolean actionbarOverlay,
            int dragDirection) {
        initActivityWithIntent(touchMode, displayMode, slideStyle, actionbarOverlay);
        mSolo.setActivityOrientation(getOrientation());
        getInstrumentation().waitForIdleSync();
        drag(dragDirection);
        mSolo.sleep(2000);
        getInstrumentation().waitForIdleSync();
        ScreenShotUtil.AssertViewEqualBefore(mSolo,
                mActivity.getWindow().getDecorView(), this);
    }

    private void initActivityWithIntent(int touchMode, int displayMode, int slideStyle,
            boolean actionbarOverlay) {
        final Intent i = new Intent();
        i.putExtra(SlidingMenuActivity.KEY_TOUCH_MODE, touchMode);
        i.putExtra(SlidingMenuActivity.KEY_DISPLAY_MODE, displayMode);
        i.putExtra(SlidingMenuActivity.KEY_SLIDE_STYLE, slideStyle);
        i.putExtra(SlidingMenuActivity.KEY_ACTIONBAR_OVERLAY, actionbarOverlay);
        setActivityIntent(i);
        initActivity();
    }

    private void drag(int dragDirection) {
        final DisplayMetrics dm = mActivity.getResources().getDisplayMetrics();
        final int mDisplayHeight = dm.heightPixels;
        final int mDisplayWidth = dm.widthPixels;
        int height = mDisplayHeight / 2;
        final Point fromLeft = new Point(0, height);
        final Point toRight = new Point(mDisplayWidth * 5 / 6, height);
        final Point fromRight = new Point(mDisplayWidth, height);
        final Point toLeft = new Point(mDisplayWidth / 6, height);
        if (RTL == dragDirection) {
            mSolo.drag(fromRight.x - OFFSET, toLeft.x, fromRight.y, toLeft.y, STEP_COUNT);
        } else {
            mSolo.drag(fromLeft.x, toRight.x, fromLeft.y, toRight.y, STEP_COUNT);
        }
    }

    public void testIgnore() {
        initActivity();
        final SlidingMenu slidingMenu = mSolo.getView(SlidingMenu.class, 0);
        final HtcViewPager viewPage = mSolo.getView(HtcViewPager.class, 0);
        try {
            runTestOnUiThread(new Runnable() {
                public void run() {
                    slidingMenu.addIgnoredView(viewPage);
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
        getInstrumentation().waitForIdleSync();
        drag(RTL);
        mSolo.sleep(2000);
        getInstrumentation().waitForIdleSync();
        ScreenShotUtil.AssertViewEqualBefore(mSolo,
                mActivity.findViewById(android.R.id.content), this);
    }

    public void testToggle() {
        toggle(TOGGLE_ONCE);
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mActivity.findViewById(android.R.id.content),
                this);
    }

    private void toggle(final int toggleTimes) {
        initActivity();
        getScreenSize();
        final SlidingMenu slidingmenu = mSolo.getView(SlidingMenu.class, 0);
        try {
            runTestOnUiThread(new Runnable() {
                public void run() {
                    for (int i = 0; i < toggleTimes; i++) {
                        slidingmenu.toggle();
                    }
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
        getInstrumentation().waitForIdleSync();
        mSolo.sleep(2000);
    }

    private void getScreenSize() {
        if (0 == mScreenWidth || 0 == mScreenHeight) {
            final DisplayMetrics dm = mActivity.getResources().getDisplayMetrics();
            mScreenWidth = dm.widthPixels;
            mScreenHeight = dm.heightPixels;
        }
    }

    private void clickDim(final int toggleTimes) {
        toggle(toggleTimes);
        final SlidingMenuActivity sma = (SlidingMenuActivity) mActivity;
        final int menuWidth = sma.mSlidingMenu.getMenu().getWidth();
        mSolo.clickOnScreen(menuWidth + (mScreenWidth - menuWidth) / 2, mScreenHeight / 2);
        mSolo.sleep(2000);
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mActivity.findViewById(android.R.id.content),
                this);
    }

    public void testClickDim() {
        clickDim(TOGGLE_ONCE);
    }

    public void testClickDimWithToggleTwice() {
        clickDim(TOGGLE_TWICE);
    }

    public void testDragAndClickViewInMenu() {
        toggle(TOGGLE_ONCE);

        final SlidingMenuActivity sma = (SlidingMenuActivity) mActivity;
        final int menuWidth = sma.mSlidingMenu.getMenu().getWidth();

        final int xStart = (int) (Math.min(mScreenHeight, mScreenWidth) * DIM_MIDDLE_SCALE);
        final int yStart = mScreenHeight / 10;
        final int xMiddle = xStart;
        final int yMiddle = mScreenHeight / 2;
        final int xEnd = menuWidth / 2;
        final int yEnd = mScreenHeight;

        final Point[] pointArray = new Point[] {
                new Point(xStart, yStart), new Point(xMiddle, yMiddle), new Point(xEnd, yEnd)
        };
        final int[] numberArray = new int[] {
                STEP_COUNT, STEP_COUNT
        };
        DragUtil.dragTabByTrack(getInstrumentation(),
                DragUtil.genDragTrack(pointArray, numberArray), null);

        mSolo.sleep(1000);

        final TextView tv = (TextView) mActivity.findViewById(android.R.id.text1);
        mSolo.clickOnView(tv);

        mSolo.sleep(1000);

        String expected = mActivity.getString(R.string.clickTest);
        String actual = tv.getText().toString();
        assertEquals(expected, actual);
    }

    public void testClickViewInMenuAndDragTinyRangeFullScreen() {
        testClickViewInMenuAndDragTinyRange(SlidingMenu.TOUCHMODE_FULLSCREEN);
    }

    public void testClickViewInMenuAndDragTinyRangeMargin() {
        testClickViewInMenuAndDragTinyRange(SlidingMenu.TOUCHMODE_MARGIN);
    }

    public void testClickViewInMenuAndDragTinyRangeNone() {
        testClickViewInMenuAndDragTinyRange(SlidingMenu.TOUCHMODE_NONE);
    }

    private void testClickViewInMenuAndDragTinyRange(int mode) {
        toggle(TOGGLE_ONCE);
        final SlidingMenuActivity sma = (SlidingMenuActivity) mActivity;
        final SlidingMenu sm = sma.mSlidingMenu;
        if (-1 == mTouchMode) {
            try {
                Field f = sm.getClass().getDeclaredField("mSlidingMenuView");
                f.setAccessible(true);
                final Object menuView = f.get(sm);
                f = menuView.getClass().getDeclaredField("mTouchMode");
                f.setAccessible(true);
                mTouchMode = f.getInt(menuView);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        if (SlidingMenu.TOUCHMODE_FULLSCREEN == mTouchMode) {
            sm.setTouchModeBehind(mode);
            final TextView tv = (TextView) mActivity.findViewById(android.R.id.text1);
            int[] location = new int[2];
            tv.getLocationOnScreen(location);

            final ViewConfiguration configuration = ViewConfiguration.get(mActivity);
            final int touchSlop = configuration.getScaledPagingTouchSlop();

            int x = location[0] + tv.getWidth() / 2;
            int y = location[1] + tv.getHeight() / 2;
            Point pStart = new Point(x, y);
            x = x - (touchSlop / 2);
            Point pEnd = new Point(x, y);
            DragUtil.dragTabByTrack(getInstrumentation(),
                    DragUtil.genDragTrack(pStart, pEnd, STEP_COUNT_TINY), null);

            mSolo.sleep(1000);
            String expected = mActivity.getString(R.string.clickTest);
            String actual = tv.getText().toString();
            assertEquals(expected, actual);
        }
    }

    public void testIncreaseCoverage() {
        initActivity();
        try {
            runTestOnUiThread(new Runnable() {
                public void run() {
                    SlidingMenu slidingmenu = new SlidingMenu(mActivity,
                            SlidingMenu.SLIDING_CONTENT);

                    slidingmenu.setOnClosedListener(new OnClosedListener() {
                        @Override
                        public void onClosed() {
                        }
                    });

                    slidingmenu.setOnCloseListener(new OnCloseListener() {
                        @Override
                        public void onClose() {
                        }
                    });

                    slidingmenu.setOnOpenedListener(new OnOpenedListener() {
                        @Override
                        public void onOpened() {
                        }
                    });

                    slidingmenu.setOnOpenListener(new OnOpenListener() {
                        @Override
                        public void onOpen() {
                        }
                    });

                    slidingmenu.setAboveOffset(0);
                    slidingmenu
                            .setAboveOffsetRes(android.R.dimen.app_icon_size);
                    slidingmenu.setContent(android.R.layout.simple_list_item_1);
                    slidingmenu.setMenu(android.R.layout.simple_list_item_1);
                    slidingmenu.setSecondaryMenu(new TextView(mActivity));
                    slidingmenu.setSecondaryShadowDrawable(R.drawable.ic_launcher);
                    slidingmenu.setSelectedView(new TextView(mActivity));
                    slidingmenu.setSelectorDrawable(R.drawable.ic_launcher);
                    slidingmenu.setShadowDrawable(R.drawable.ic_launcher);
                    slidingmenu.setSlidingEnabled(true);
                    slidingmenu.setStatic(false);
                    slidingmenu.setBehindWidthRes(R.dimen.common_dimen_m1);
                    slidingmenu.setShadowWidthRes(R.dimen.common_dimen_m1);
                    slidingmenu.setBehindOffsetRes(R.dimen.common_dimen_m1);

                    slidingmenu.getBehindOffset();
                    slidingmenu.getSecondaryMenu();
                    slidingmenu.getBehindScrollScale();
                    slidingmenu.getMode();
                    slidingmenu.getTouchModeAbove();

                    slidingmenu.isMenuShowing();
                    slidingmenu.isSecondaryMenuShowing();
                    slidingmenu.isSlidingEnabled();

                    slidingmenu.showMenu();
                    slidingmenu.showSecondaryMenu();

                    slidingmenu.manageLayers(0);
                    slidingmenu.clearIgnoredViews();

                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

}
