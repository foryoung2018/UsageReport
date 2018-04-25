
package com.htc.lib1.cc.actionbar.test.ui;

import android.app.ActionBar;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.htc.lib1.cc.actionbar.activityhelper.ActionBarMockActivity;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.actionbar.test.ActionBarTestUtil;
import com.htc.lib1.cc.actionbar.test.ActionBarWidgetsFactory;
import com.htc.lib1.cc.actionbar.test.HtcActionBarActivityTestCase;
import com.htc.lib1.cc.util.ActionBarUtil;
import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.widget.ActionBarContainer;
import com.htc.lib1.cc.widget.ActionBarDropDown;
import com.htc.lib1.cc.widget.ActionBarDropDownSearch;
import com.htc.lib1.cc.widget.ActionBarItemView;
import com.htc.lib1.cc.widget.ActionBarQuickContact;
import com.htc.lib1.cc.widget.ActionBarRefresh;
import com.htc.lib1.cc.widget.ActionBarText;
import com.htc.test.util.ScreenShotUtil;
import com.htc.test.util.WidgetUtil;

public class ActionBarPhoneUiTest extends HtcActionBarActivityTestCase {

    public ActionBarPhoneUiTest() {
        super(ActionBarMockActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        initActionBarContainers();
    }

    private void assertSnapShot() {
        getInstrumentation().waitForIdleSync();
        assertSnapShot(null);
    }

    private void assertSnapShotForSearchProgress() {
        if (Build.VERSION.SDK_INT >= 21) {
            mSolo.sleep(1000);
            assertSnapShot(new View[] {
                    (ProgressBar) mActionBarSearch.getChildAt(2)
            });
        } else {
            assertSnapShot();
        }
    }

    private void assertSnapShotForProgressView() {
        mSolo.sleep(1000);
        assertSnapShot(new View[] {
                (ProgressBar) mActionBarContainer.getChildAt(1)
        });
    }

    private void assertSnapShot(View[] excludeChild) {
        int id = mActivity.getResources().getIdentifier("action_bar_container", "id", "android");
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mSolo.getView(id), ScreenShotUtil.getScreenShotName(this), excludeChild);
    }

    private void runLeftItemView() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ActionBarTestUtil.addLeftViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarItemView(mActivity, true));
            }
        });
    }

    public final void testLeftItemView() {
        runLeftItemView();
        assertSnapShot();
    }

    private void runRightItemView() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ActionBarTestUtil.addRightViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarItemView(mActivity, false));
            }
        });
    }

    public final void testRightItemView() {
        runRightItemView();
        assertSnapShot();
    }

    private void runLeftQuickContact() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ActionBarTestUtil.addLeftViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarQuickContact(mActivity));
            }
        });
    }

    public final void testLeftQuickContact() {
        runLeftQuickContact();
        assertSnapShot();
    }

    private void runRightQuickContact() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ActionBarTestUtil.addRightViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarQuickContact(mActivity));
            }
        });
    }

    public final void testRightQuickContact() {
        runRightQuickContact();
        assertSnapShot();
    }

    private void runTextView2Lines() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarText(mActivity));
            }
        });
    }

    public final void testTextView2Lines() {
        runTextView2Lines();
        assertSnapShot();
    }

    private void runTextView1Line() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ActionBarText actionBarText = ActionBarWidgetsFactory.createActionBarText(mActivity);
                actionBarText.setSecondaryVisibility(View.GONE);
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, actionBarText);
            }
        });
    }

    public final void testTextView1Line() {
        runTextView1Line();
        assertSnapShot();
    }

    private void runDropDown1Line() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ActionBarDropDown actionBarDropDown = ActionBarWidgetsFactory.createActionBarDropDown(mActivity);
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, actionBarDropDown);
                actionBarDropDown.setSecondaryVisibility(View.GONE);
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
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarDropDown(mActivity));
            }
        });
    }

    public final void testDropDown2Lines() {
        runDropDown2Lines();
        assertSnapShot();
    }

    private void runDropDownMultiLines() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ActionBarDropDown actionBarDropDown = ActionBarWidgetsFactory.createActionBarDropDown(mActivity);
                actionBarDropDown.setSupportMode(ActionBarDropDown.MODE_ONE_MULTIILINE_TEXTVIEW);
                actionBarDropDown
                        .setPrimaryText("DropDownPrimary123456789012345678901234567890123456789012345678901234567890DropDownPrimary123456789012345678901234567890123456789012345678901234567890");
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, actionBarDropDown);
            }
        });
    }

    public final void testDropDownMultiLines() {
        runDropDownMultiLines();
        assertSnapShot();
    }

    private void runSearchAutoIcon(final boolean from, final boolean to, final boolean typeText, final boolean clearText) {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mActionBarSearch = ActionBarWidgetsFactory.createActionBarSearch(mActivity);
                mActionBarSearch.setProgressVisibility(View.GONE);
                mActionBarSearch.setClearIconVisibility(View.VISIBLE);
                mActionBarSearch.setAutoShowClearIcon(from);
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, mActionBarSearch);
            }
        });

        if (typeText) {
            mSolo.sleep(1000);
            mSolo.typeText(0, "ActionBarSearch12345678901234567890");
        }

        mSolo.sleep(1000);
        getInstrumentation().waitForIdleSync();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                if (from != to) {
                    mActionBarSearch.setAutoShowClearIcon(to);
                }
                if (clearText) {
                    mActionBarSearch.getAutoCompleteTextView().setText("");
                }
            }
        });
    }

    public final void testSearchAutoClearIcon_autoTrue_textTrue() {
        runSearchAutoIcon(true, false, true, false);
        assertSnapShotForSearchProgress();
    }

    public final void testSearchAutoClearIcon_autoTrue_textFalse() {
        runSearchAutoIcon(true, false, false, false);
        assertSnapShotForSearchProgress();
    }

    public final void testSearchAutoClearIcon_autoFalse_textTrue() {
        runSearchAutoIcon(false, true, true, false);
        assertSnapShotForSearchProgress();
    }

    public final void testSearchAutoClearIcon_autoFalse_textFalse() {
        runSearchAutoIcon(false, true, false, false);
        assertSnapShotForSearchProgress();
    }

    public final void testSearchAutoClearIcon_autoTrue_clearTrue() {
        runSearchAutoIcon(true, true, true, true);
        assertSnapShotForSearchProgress();
    }

    private void runSearch(final boolean hasIcon, final boolean hasProgressView, boolean typeText) {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mActionBarSearch = ActionBarWidgetsFactory.createActionBarSearch(mActivity, hasProgressView);
                mActionBarSearch.setClearIconVisibility(hasIcon ? View.VISIBLE : View.GONE);
                mActionBarSearch.setProgressVisibility(hasProgressView ? View.VISIBLE : View.GONE);
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
        assertSnapShotForSearchProgress();
    }

    public final void testSearch_typeText() {
        runSearch(true, true, true);
        assertSnapShotForSearchProgress();
    }

    public final void testSearch_noIcon() {
        runSearch(false, true, true);
        assertSnapShotForSearchProgress();
    }

    public final void testSearch_noProgress() {
        runSearch(true, false, true);
        assertSnapShot();
    }

    public final void testSearch_noIcon_noProgress() {
        runSearch(false, false, true);
        assertSnapShot();
    }

    public final void testActionBarDropdownSearch() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ActionBarDropDownSearch mActionBarDropDownSearch = new ActionBarDropDownSearch(mActivity);
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, mActionBarDropDownSearch);
                mActionBarDropDownSearch.setActionBarSearchEnabled(true);
                ActionBarDropDown actionBarDropDown = mActionBarDropDownSearch.getActionBarDropDown();
                actionBarDropDown.setPrimaryText("DropDownPrimary123456789012345678901234567890123456789012345678901234567890");
                actionBarDropDown.setSecondaryText("DropDownSecondary123456789012345678901234567890123456789012345678901234567890");
                actionBarDropDown.setArrowEnabled(true);
            }
        });
        mSolo.sleep(500);
        assertSnapShot();
    }

    private void runPullDownMode(final int progress) {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarDropDown(mActivity));
                mActionBarContainer.setRotationProgress(progress);
            }
        });
        /* wait for layout animation */
        mSolo.sleep(2000);
    }

    public final void testPullDownModeDegree0() {
        runPullDownMode(0);
        assertSnapShot();
    }

    public final void testPullDownModeDegree50() {
        runPullDownMode(50);
        assertSnapShot();
    }

    public final void testPullDownModeDegreeMax() {
        runPullDownMode(mActionBarContainer.getRotationMax());
        assertSnapShot();
    }

    public final void testPullDownModeAnimation() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarDropDown(mActivity));
                mActionBarContainer.setUpdatingViewText(ActionBarContainer.UPDATING_MODE_PULLDOWN, "LAST UPDATED 2012/12/12 12:12 PM");
                mActionBarContainer.setUpdatingViewText(ActionBarContainer.UPDATING_MODE_PULLDOWN_TITLE, "Release to whatever you want");
                mActionBarContainer.setRotationMax(200);
                mActionBarContainer.setRotationProgress(70, 150);
            }
        });

        /* wait for layout animation */
        mSolo.sleep(2000);
        assertSnapShot();
    }

    private void runPullDownMode(final int pullDownMode, final String pullDownTitle, final String pullDownText) {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mActionBarContainer.setUpdatingState(pullDownMode);
                if (pullDownTitle != null) {
                    mActionBarContainer.setUpdatingViewText(ActionBarContainer.UPDATING_MODE_PULLDOWN_TITLE, pullDownTitle);
                }
                mActionBarContainer.setUpdatingViewText(ActionBarContainer.UPDATING_MODE_PULLDOWN, pullDownText);
            }
        });
        /* wait for layout animation */
        mSolo.sleep(2000);
    }

    /**
     * d=Default;c=Custom;b=Blank;n=Null
     */
    public final void testPullDownMode_dTitle_hText() {
        runPullDownMode(ActionBarContainer.UPDATING_MODE_PULLDOWN, null, "Last upated 2012/12/12 12:12 pm");
        assertSnapShot();
    }

    public final void testPullDownMode_dTitle_bText() {
        runPullDownMode(ActionBarContainer.UPDATING_MODE_PULLDOWN, null, "");
        assertSnapShot();
    }

    public final void testPullDownMode_dTitle_nText() {
        runPullDownMode(ActionBarContainer.UPDATING_MODE_PULLDOWN, null, null);
        assertSnapShot();
    }

    public final void testPullDownMode_hTitle_hText() {
        runPullDownMode(ActionBarContainer.UPDATING_MODE_PULLDOWN, "Release to whatever you want custom title", "Last upated 2012/12/12 12:12 pm");
        assertSnapShot();
    }

    public final void testPullDownMode_hTitle_bText() {
        runPullDownMode(ActionBarContainer.UPDATING_MODE_PULLDOWN, "Release to whatever you want custom title", "");
        assertSnapShot();
    }

    public final void testPullDownMode_hTitle_nText() {
        runPullDownMode(ActionBarContainer.UPDATING_MODE_PULLDOWN, "Release to whatever you want custom title", null);
        assertSnapShot();
    }

    public final void testPullDownMode_bTitle_hText() {
        runPullDownMode(ActionBarContainer.UPDATING_MODE_PULLDOWN, "", "Last upated 2012/12/12 12:12 pm");
        assertSnapShot();
    }

    public final void testPullDownMode_bTitle_bText() {
        runPullDownMode(ActionBarContainer.UPDATING_MODE_PULLDOWN, "", "");
        assertSnapShot();
    }

    public final void testPullDownMode_bTitle_nText() {
        runPullDownMode(ActionBarContainer.UPDATING_MODE_PULLDOWN, "", null);
        assertSnapShot();
    }

    public final void testPullDownMode_changeDisplayType() {
        runPullDownMode(ActionBarContainer.UPDATING_MODE_PULLDOWN, null, "LAST UPDATED 2012/12/12 12:12 PM");
        runPullDownMode(ActionBarContainer.UPDATING_MODE_PULLDOWN, null, null);
        runPullDownMode(ActionBarContainer.UPDATING_MODE_PULLDOWN, null, "LAST UPDATED 2012/12/12 12:12 PM");
        assertSnapShot();
    }

    private void stopProgressBarRunningInActionBarRefresh() {
        ActionBarRefresh actionBarRefresh = (ActionBarRefresh) mActionBarContainer.getChildAt(1);
        ProgressBar progressBar = (ProgressBar) actionBarRefresh.getChildAt(1);
        WidgetUtil.setProgressBarIndeterminatedStopRunning(progressBar);
    }

    private void runUpdatingMode(final int updatingMode, final String updatingTitle, final String updatingText) {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarDropDown(mActivity));
                if (updatingMode == ActionBarContainer.UPDATING_MODE_UPDATING_WITH_DROPDOWN) {
                    mActionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_UPDATING_WITH_DROPDOWN);
                    mActionBarContainer.setUpdatingViewText(ActionBarContainer.UPDATING_MODE_UPDATING_WITH_DROPDOWN, updatingTitle);
                } else if (updatingMode == ActionBarContainer.UPDATING_MODE_UPDATING_WITH_TITLE) {
                    mActionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_UPDATING_WITH_TITLE);
                    mActionBarContainer.setUpdatingViewText(ActionBarContainer.UPDATING_MODE_UPDATING_WITH_TITLE, updatingTitle);
                } else {
                    mActionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_UPDATING);
                }
                if (updatingText != null) {
                    mActionBarContainer.setUpdatingViewText(ActionBarContainer.UPDATING_MODE_UPDATING, updatingText);
                }
                stopProgressBarRunningInActionBarRefresh();
            }
        });
    }

    public final void testUpdatingMode_changeDisplayType() {
        runUpdatingMode(ActionBarContainer.UPDATING_MODE_UPDATING, null, null);
        mSolo.sleep(1000);
        runUpdatingMode(ActionBarContainer.UPDATING_MODE_UPDATING_WITH_TITLE, "TextPrimary1234567890123456789012345678901234567890", null);
        mSolo.sleep(1000);
        runUpdatingMode(ActionBarContainer.UPDATING_MODE_UPDATING_WITH_DROPDOWN, "DropDownPrimary1234567890123456789012345678901234567890", null);
        mSolo.sleep(1000);
        runUpdatingMode(ActionBarContainer.UPDATING_MODE_UPDATING_WITH_TITLE, "TextPrimary1234567890123456789012345678901234567890", null);
        mSolo.sleep(1000);
        runUpdatingMode(ActionBarContainer.UPDATING_MODE_UPDATING, null, null);
        mSolo.sleep(1000);
        assertSnapShot();
    }

    /**
     * d=Default;h=has;b=Blank;n=Null
     */
    public final void testUpdatingMode_hText() {
        runUpdatingMode(ActionBarContainer.UPDATING_MODE_UPDATING, null, "Updating... (1233456789/1234567890)");
        assertSnapShot();
    }

    public final void testUpdatingMode_dText() {
        runUpdatingMode(ActionBarContainer.UPDATING_MODE_UPDATING, null, null);
        assertSnapShot();
    }

    public final void testUpdatingMode_bText() {
        runUpdatingMode(ActionBarContainer.UPDATING_MODE_UPDATING, null, "");
        assertSnapShot();
    }

    public final void testUpdatingModeWithTitle_hText() {
        runUpdatingMode(ActionBarContainer.UPDATING_MODE_UPDATING_WITH_TITLE, "TextPrimary1234567890123456789012345678901234567890", "Updating... (1233456789/1234567890)");
        assertSnapShot();
    }

    public final void testUpdatingModeWithTitle_dText() {
        runUpdatingMode(ActionBarContainer.UPDATING_MODE_UPDATING_WITH_TITLE, "TextPrimary1234567890123456789012345678901234567890", null);
        assertSnapShot();
    }

    public final void testUpdatingModeWithTitle_bText() {
        runUpdatingMode(ActionBarContainer.UPDATING_MODE_UPDATING_WITH_TITLE, "TextPrimary1234567890123456789012345678901234567890", "");
        assertSnapShot();
    }

    public final void testUpdatingModeWithDropDown_hTitle_hText() {
        runUpdatingMode(ActionBarContainer.UPDATING_MODE_UPDATING_WITH_DROPDOWN, "DropDownPrimary1234567890123456789012345678901234567890", "Updating... (1233456789/1234567890)");
        assertSnapShot();
    }

    public final void testUpdatingModeWithDropDown_hTitle_dText() {
        runUpdatingMode(ActionBarContainer.UPDATING_MODE_UPDATING_WITH_DROPDOWN, "DropDownPrimary1234567890123456789012345678901234567890", null);
        assertSnapShot();
    }

    public final void testUpdatingModeWithDropDown_hTitle_bText() {
        runUpdatingMode(ActionBarContainer.UPDATING_MODE_UPDATING_WITH_DROPDOWN, "DropDownPrimary1234567890123456789012345678901234567890", "");
        assertSnapShot();
    }

    public final void testUpdatingModeWithDropDown_bTitle_hText() {
        runUpdatingMode(ActionBarContainer.UPDATING_MODE_UPDATING_WITH_DROPDOWN, "", "Updating... (1233456789/1234567890)");
        assertSnapShot();
    }

    public final void testUpdatingModeWithDropDown_bTitle_dText() {
        runUpdatingMode(ActionBarContainer.UPDATING_MODE_UPDATING_WITH_DROPDOWN, "", null);
        assertSnapShot();
    }

    public final void testUpdatingModeWithDropDown_bTitle_bText() {
        runUpdatingMode(ActionBarContainer.UPDATING_MODE_UPDATING_WITH_DROPDOWN, "", "");
        assertSnapShot();
    }

    private void runSwitchContainer() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mActionBarSearch = ActionBarWidgetsFactory.createActionBarSearch(mActivity, true);
                ActionBarTestUtil.addCenterViewCompat(mActionBarSearchContainer, mActionBarSearch);
                mActionBarExt.switchContainer();
            }
        });
        mSolo.sleep(2000);
        mSolo.waitForView(mActionBarSearchContainer, 5000, false);
        mSolo.typeText(mActionBarSearch.getAutoCompleteTextView(), "ActionBarSearch12345678901234567890");
    }

    public final void testSwitchContainer() {
        runSwitchContainer();
        assertSnapShotForSearchProgress();
    }

    private final void runBackUpView() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mActionBarContainer.setBackUpEnabled(true);
            }
        });
    }

    public final void testBackUpView() {
        runBackUpView();
        assertSnapShot();
    }

    private final void runProgressView() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mActionBarContainer.setProgressVisibility(View.VISIBLE);
                stopProgressViewForContainer();
                mActionBarContainer.setBackUpEnabled(true);
            }
        });
    }

    private void stopProgressViewForContainer() {
        ProgressBar progressBar = (ProgressBar) mActionBarContainer.getChildAt(0);
        WidgetUtil.setProgressBarIndeterminatedStopRunning(progressBar);
    }

    public final void testProgressView() {
        runProgressView();
        if (Build.VERSION.SDK_INT >= 21) {
            assertSnapShotForProgressView();
        } else {
            assertSnapShot();
        }
    }

    private void runMenu() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                enableActivityMenu();
            }
        });
        mSolo.sleep(1000);
    }

    public final void testMenu() {
        runMenu();
        assertSnapShot();
    }

    private final void runActionMode() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mActivity.startActionMode(new ActionMode.Callback() {

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        return false;
                    }

                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        mode.setTitle("12345678901234567890123456789012345678901234567890");
                        ActionBarUtil.setActionModeBackground(mActivity, mode, new ColorDrawable(HtcCommonUtil.getCommonThemeColor(mActivity, R.styleable.ThemeColor_multiply_color)));
                        mActivity.getMenuInflater().inflate(R.menu.actionbar_actions, menu);
                        return true;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {

                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                });
            }
        });
    }

    public final void testActionMode() {
        runActionMode();
        mSolo.sleep(2000);
        assertSnapShot();
    }

    private final void runTransparent() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mActionBarExt.setTransparentEnabled(true);
            }
        });
    }

    public final void testTransparent() {
        runTransparent();
        assertSnapShot();
    }

    private final void runTextViewWithBackMenu() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                enableActivityMenu();
                mActionBarContainer.setBackUpEnabled(true);
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarText(mActivity));
            }
        });
    }

    public final void testTextViewWithBackMenu() {
        runTextViewWithBackMenu();
        assertSnapShot();
    }

    private void runDropDownWithBackMenu() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                enableActivityMenu();
                mActionBarContainer.setBackUpEnabled(true);
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarDropDown(mActivity));
            }
        });
    }

    public final void testDropDownWithBackMenu() {
        runDropDownWithBackMenu();
        assertSnapShot();
    }

    private void runSearchWithBackMenu() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                enableActivityMenu();
                mActionBarContainer.setBackUpEnabled(true);
                mActionBarSearch = ActionBarWidgetsFactory.createActionBarSearch(mActivity, true);
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, mActionBarSearch);
            }
        });
    }

    public final void testSearchWithBackMenu() {
        runSearchWithBackMenu();
        assertSnapShotForSearchProgress();
    }

    private void runTextViewWithLeftRightView() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ActionBarTestUtil.addLeftViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarQuickContact(mActivity));
                ActionBarTestUtil.addRightViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarItemView(mActivity, false));
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarText(mActivity));
            }
        });
    }

    public final void testTextViewWithLeftRightView() {
        runTextViewWithLeftRightView();
        assertSnapShot();
    }

    private void runDropDownWithLeftRightView() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ActionBarTestUtil.addLeftViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarQuickContact(mActivity));
                ActionBarTestUtil.addRightViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarItemView(mActivity, false));
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarDropDown(mActivity));
            }
        });
    }

    public final void testDropDownWithLeftRightView() {
        runDropDownWithLeftRightView();
        assertSnapShot();
    }

    private void runSearchWithLeftRightView() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ActionBarTestUtil.addLeftViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarQuickContact(mActivity));
                ActionBarTestUtil.addRightViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarItemView(mActivity, false));
                mActionBarSearch = ActionBarWidgetsFactory.createActionBarSearch(mActivity, true);
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, mActionBarSearch);
            }
        });
    }

    public final void testSearchWithLeftRightView() {
        runSearchWithLeftRightView();
        assertSnapShotForSearchProgress();
    }

    private void runTextViewTransparent() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mActionBarExt.setTransparentEnabled(true);
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarText(mActivity));
            }
        });
    }

    public final void testTextViewTransparent() {
        runTextViewTransparent();
        assertSnapShot();
    }

    private void runDropDownTransparent() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mActionBarExt.setTransparentEnabled(true);
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarDropDown(mActivity));
            }
        });
        mSolo.sleep(1000);
    }

    public final void testDropDownTransparent() {
        runDropDownTransparent();
        assertSnapShot();
    }

    private void runQuickContactWithBackMenu() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                enableActivityMenu();
                mActionBarContainer.setBackUpEnabled(true);
                ActionBarQuickContact actionBarQuickContactLeft = ActionBarWidgetsFactory.createActionBarQuickContact(mActivity);
                actionBarQuickContactLeft.setLeftMarginEnabled(false);
                ActionBarTestUtil.addLeftViewCompat(mActionBarContainer, actionBarQuickContactLeft);
                ActionBarTestUtil.addRightViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarQuickContact(mActivity));
            }
        });
    }

    public final void testQuickContactWithBackMenu() {
        runQuickContactWithBackMenu();
        assertSnapShot();
    }

    private void runItemViewWithBackMenu() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                enableActivityMenu();
                mActionBarContainer.setBackUpEnabled(true);
                ActionBarTestUtil.addLeftViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarItemView(mActivity, true));
                ActionBarTestUtil.addRightViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarItemView(mActivity, false));
            }
        });
    }

    public final void testItemViewWithBackMenu() {
        runItemViewWithBackMenu();
        assertSnapShot();
    }

    private void enableActivityMenu() {
        ((ActionBarMockActivity) mActivity).enableMenu(true);
        mActivity.invalidateOptionsMenu();
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

                ActionBarItemView leftActionBarItemView = ActionBarWidgetsFactory.createActionBarItemView(mActivity, true);
                mActionBarContainer.addView(leftActionBarItemView);

                ActionBarQuickContact leftActionBarQuickContact = ActionBarWidgetsFactory.createActionBarQuickContact(mActivity);
                mActionBarContainer.addView(leftActionBarQuickContact);

                ActionBarDropDown mActionBarDropDown = ActionBarWidgetsFactory.createActionBarDropDown(mActivity);
                mActionBarContainer.addView(mActionBarDropDown, new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER));

                ActionBarItemView rightActionBarItemView = ActionBarWidgetsFactory.createActionBarItemView(mActivity, false);
                if (supportRTL) {
                    mActionBarContainer.addView(rightActionBarItemView, new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.END));
                } else {
                    mActionBarContainer.addView(rightActionBarItemView, new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.RIGHT));
                }

                ActionBarQuickContact rightActionBarQuickContact = ActionBarWidgetsFactory.createActionBarQuickContact(mActivity);
                if (supportRTL) {
                    mActionBarContainer.addView(rightActionBarQuickContact, new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.END));
                } else {
                    mActionBarContainer.addView(rightActionBarQuickContact, new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.RIGHT));
                }
            }
        });
    }
}
