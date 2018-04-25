
package com.htc.lib1.cc.actionbar.test.ui;

import android.app.ActionBar;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.htc.lib1.cc.actionbar.activityhelper.ActionBarMockActivity;
import com.htc.lib1.cc.actionbar.test.ActionBarTestUtil;
import com.htc.lib1.cc.actionbar.test.ActionBarWidgetsFactory;
import com.htc.lib1.cc.actionbar.test.HtcActionBarActivityTestCase;
import com.htc.test.util.ScreenShotUtil;
import com.htc.test.util.WidgetUtil;
import com.htc.lib1.cc.widget.ActionBarContainer;
import com.htc.lib1.cc.widget.ActionBarDropDown;
import com.htc.lib1.cc.widget.ActionBarItemView;
import com.htc.lib1.cc.widget.ActionBarSearch;
import com.htc.lib1.cc.widget.ActionBarText;

public class ActionBarAutomotiveUiTest extends HtcActionBarActivityTestCase {
    public static final String TAG = "ActionBarAutomotiveDemoTest";

    public ActionBarAutomotiveUiTest() {
        super(ActionBarMockActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        initActionBarContainers();
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mActionBarContainer.setSupportMode(ActionBarContainer.MODE_AUTOMOTIVE);
                mActionBarContainer.setBackUpEnabled(true);
            }
        });
    }

    private void assertSnapShot() {
        getInstrumentation().waitForIdleSync();
        assertSnapShot(null);
    }

    private void assertSnapShotForSearchProgress() {
        mSolo.sleep(1000);
        assertSnapShot(new View[] {
                (ProgressBar) mActionBarSearch.getChildAt(2)
        });
    }

    private void assertSnapShotForProgressView() {
        mSolo.sleep(1000);
        assertSnapShot(new View[] {
                (ProgressBar) mActionBarContainer.getChildAt(1)
        });
    }

    private void assertSnapShot(View[] excludeChild) {
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mActionBarContainer, ScreenShotUtil.getScreenShotName(this), excludeChild);
    }

    private void runDropDown1Line() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ActionBarDropDown actionBarDropDown = ActionBarWidgetsFactory.createActionBarDropDown(mActivity, ActionBarDropDown.MODE_AUTOMOTIVE);
                actionBarDropDown.setSecondaryVisibility(View.GONE);
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, actionBarDropDown);
            }
        });
    }

    public final void testDropDown1Line() {
        runDropDown1Line();
        assertSnapShot();
    }

    private void runDropDown2Lines() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarDropDown(mActivity, ActionBarDropDown.MODE_AUTOMOTIVE));
            }
        });
    }

    public final void testDropDown2Lines() {
        runDropDown2Lines();
        assertSnapShot();
    }

    private void runDropDownWithRightView() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ActionBarTestUtil.addRightViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarItemView(mActivity, false, ActionBarItemView.MODE_AUTOMOTIVE));
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarDropDown(mActivity, ActionBarDropDown.MODE_AUTOMOTIVE));
            }
        });
    }

    public final void testDropDownWithRightView() {
        runDropDownWithRightView();
        assertSnapShot();
    }

    private void runRightItemView() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ActionBarTestUtil.addRightViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarItemView(mActivity, false, ActionBarItemView.MODE_AUTOMOTIVE));
            }
        });
    }

    public final void testRightItemView() {
        runRightItemView();
        assertSnapShot();
    }

    private void runSearch(final boolean hasIcon, final boolean hasProgressView, boolean typeText) {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mActionBarSearch = ActionBarWidgetsFactory.createActionBarSearch(mActivity, ActionBarSearch.MODE_AUTOMOTIVE, true);
                if (!hasIcon) {
                    mActionBarSearch.setClearIconVisibility(View.GONE);
                }
                if (!hasProgressView) {
                    mActionBarSearch.setProgressVisibility(View.GONE);
                }
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, mActionBarSearch);
            }
        });
        if (typeText) {
            mSolo.sleep(1000);
            mSolo.typeText(0, "ActionBarSearch12345678901234567890ActionBarSearch12345678901234567890");
        }
    }

    public final void testSearch() {
        runSearch(true, true, false);
        if (Build.VERSION.SDK_INT >= 21) {
            assertSnapShotForSearchProgress();
        } else {
            assertSnapShot();
        }
    }

    public final void testSearch_typeText() {
        runSearch(true, true, true);
        if (Build.VERSION.SDK_INT >= 21) {
            assertSnapShotForSearchProgress();
        } else {
            assertSnapShot();
        }
    }

    public final void testSearch_noIcon() {
        runSearch(false, true, true);
        if (Build.VERSION.SDK_INT >= 21) {
            assertSnapShotForSearchProgress();
        } else {
            assertSnapShot();
        }
    }

    public final void testSearch_noProgress() {
        runSearch(true, false, true);
        assertSnapShot();
    }

    public final void testSearch_noIcon_noProgress() {
        runSearch(false, false, true);
        assertSnapShot();
    }

    private void stopProgressViewForContainer() {
        ProgressBar progressBar = (ProgressBar) mActionBarContainer.getChildAt(1);
        WidgetUtil.setProgressBarIndeterminatedStopRunning(progressBar);
    }

    private void runProgressView() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mActionBarContainer.setProgressVisibility(View.VISIBLE);
                stopProgressViewForContainer();
            }
        });
    }

    public final void testProgressView() {
        runProgressView();
        if (Build.VERSION.SDK_INT >= 21) {
            assertSnapShotForProgressView();
        } else {
            assertSnapShot();
        }
    }

    private void runSearchWithRightView() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mActionBarSearch = ActionBarWidgetsFactory.createActionBarSearch(mActivity, ActionBarSearch.MODE_AUTOMOTIVE, true);
                ActionBarTestUtil.addRightViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarItemView(mActivity, false, ActionBarItemView.MODE_AUTOMOTIVE));
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, mActionBarSearch);
            }
        });
    }

    public final void testSearchWithRightView() {
        runSearchWithRightView();
        if (Build.VERSION.SDK_INT >= 21) {
            assertSnapShotForSearchProgress();
        } else {
            assertSnapShot();
        }
    }

    private void runTextView1Line() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ActionBarText actionBarText = ActionBarWidgetsFactory.createActionBarText(mActivity, ActionBarText.MODE_AUTOMOTIVE);
                actionBarText.setSecondaryVisibility(View.GONE);
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, actionBarText);
            }
        });
    }

    public final void testTextView1Line() {
        runTextView1Line();
        assertSnapShot();
    }

    private void runTextView2Lines() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarText(mActivity, ActionBarText.MODE_AUTOMOTIVE));
            }
        });
    }

    public final void testTextView2Lines() {
        runTextView2Lines();
        assertSnapShot();
    }

    private void runTextViewWithRightView() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ActionBarTestUtil.addRightViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarItemView(mActivity, false, ActionBarItemView.MODE_AUTOMOTIVE));
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarText(mActivity, ActionBarText.MODE_AUTOMOTIVE));
            }
        });
    }

    public final void testTextViewWithRightView() {
        runTextViewWithRightView();
        assertSnapShot();
    }

    public final void testAllGroupPhoto_LTR() {
        runAllGroupPhoto(false);
        if (Build.VERSION.SDK_INT >= 21) {
            assertSnapShotForProgressView();
        } else {
            assertSnapShot();
        }

    }

    public final void testAllGroupPhoto_RTL_support() {
        runAllGroupPhoto(true);
        if (Build.VERSION.SDK_INT >= 21) {
            assertSnapShotForProgressView();
        } else {
            assertSnapShot();
        }
    }

    private void runAllGroupPhoto(final boolean supportRTL) {
        runProgressView();
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mActionBarContainer.setBackUpEnabled(true);

                ActionBarItemView leftActionBarItemView = ActionBarWidgetsFactory.createActionBarItemView(mActivity, true, ActionBarContainer.MODE_AUTOMOTIVE);
                mActionBarContainer.addView(leftActionBarItemView);

                ActionBarDropDown mActionBarDropDown = ActionBarWidgetsFactory.createActionBarDropDown(mActivity, ActionBarContainer.MODE_AUTOMOTIVE);
                mActionBarContainer.addView(mActionBarDropDown, new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER));

                ActionBarItemView rightActionBarItemView = ActionBarWidgetsFactory.createActionBarItemView(mActivity, false, ActionBarContainer.MODE_AUTOMOTIVE);
                if (supportRTL) {
                    mActionBarContainer.addView(rightActionBarItemView, new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.END));
                } else {
                    mActionBarContainer.addView(rightActionBarItemView, new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.RIGHT));
                }
            }
        });
    }
}
