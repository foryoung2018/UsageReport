
package com.htc.lib1.cc.actionbar.test.ui;

import android.app.ActionBar;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.htc.lib1.cc.actionbar.activityhelper.ActionBarMockActivity;
import com.htc.lib1.cc.actionbar.test.ActionBarTestUtil;
import com.htc.lib1.cc.actionbar.test.ActionBarWidgetsFactory;
import com.htc.lib1.cc.actionbar.test.HtcActionBarActivityTestCase;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.ActionBarContainer;
import com.htc.lib1.cc.widget.ActionBarDropDown;
import com.htc.lib1.cc.widget.ActionBarQuickContact;
import com.htc.lib1.cc.widget.ActionBarRefresh;
import com.htc.lib1.cc.widget.ActionBarSearch;
import com.htc.test.util.ScreenShotUtil;
import com.htc.test.util.WidgetUtil;

/**
 * BK=BackUpView
 * <p>
 * ABS=ActionBarSearch
 * </p>
 * <p>
 * ABT1=ActionBarTextView 1line ; ABT2=ActionBarTextView 2lines
 * </p>
 * <p>
 * ABD1=ActionBarDropDown 1line ; ABD2=ActionBarDropDown 2lines ; ABDS=ActionBarDropSubject
 * </p>
 * <p>
 * ABIL=ActionBarItemView Left ; ABIR=ActionBarItemView Right
 * </p>
 * <p>
 * ABQ=ActionBarQuickContact ; ABP=ActionBarProgressView
 * </p>
 */
public class ActionBarUIGLTest extends HtcActionBarActivityTestCase {

    public ActionBarUIGLTest() {
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

    private void assertSnapShot(View[] excludeChild) {
        int id = mActivity.getResources().getIdentifier("action_bar_container", "id", "android");
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mSolo.getView(id), ScreenShotUtil.getScreenShotName(this), excludeChild);
    }

    private void stopProgressViewForContainer() {
        ProgressBar progressBar = (ProgressBar) mActionBarContainer.getChildAt(0);
        WidgetUtil.setProgressBarIndeterminatedStopRunning(progressBar);
    }

    private void enableActivityMenu() {
        ((ActionBarMockActivity) mActivity).enableMenu(true);
        mActivity.invalidateOptionsMenu();
    }

    public final void testABD2_Menu() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                ActionBarDropDown actionBarDropDown = ActionBarWidgetsFactory.createActionBarDropDown(mActivity);
                actionBarDropDown.setPrimaryText("ActiveSync");
                actionBarDropDown.setSecondaryText("jennifer_signger@htc.com");
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, actionBarDropDown);
                enableActivityMenu();
            }
        });
        assertSnapShot();
    }

    public final void testABIL_ABS_Menu() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                ActionBarTestUtil.addLeftViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarItemView(mActivity, true));
                ActionBarSearch actionBarSearch = ActionBarWidgetsFactory.createActionBarSearch(mActivity);
                actionBarSearch.setProgressVisibility(View.GONE);
                actionBarSearch.getAutoCompleteTextView().setHint("Name");
                ImageView imageView = (ImageView) actionBarSearch.findViewById(android.R.id.icon);
                imageView.setImageResource(android.R.drawable.ic_menu_crop);
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, actionBarSearch);
                enableActivityMenu();
            }
        });
        assertSnapShot();
    }

    public final void testBK_ABS_Menu() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mActionBarContainer.setBackUpEnabled(true);
                ActionBarSearch actionBarSearch = ActionBarWidgetsFactory.createActionBarSearch(mActivity);
                actionBarSearch.setProgressVisibility(View.GONE);
                actionBarSearch.setClearIconVisibility(View.GONE);
                actionBarSearch.getAutoCompleteTextView().setHint("TO");
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, actionBarSearch);
                enableActivityMenu();
            }
        });
        assertSnapShot();
    }

    public final void testBK_ABS_ABIR() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mActionBarContainer.setBackUpEnabled(true);
                ActionBarSearch actionBarSearch = ActionBarWidgetsFactory.createActionBarSearch(mActivity);
                actionBarSearch.setProgressVisibility(View.GONE);
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, actionBarSearch);
                ActionBarTestUtil.addRightViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarItemView(mActivity, false));
            }
        });
        getInstrumentation().waitForIdleSync();
        mSolo.typeText(0, "P");
        assertSnapShot();
    }

    public final void testABT1() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                ActionBarDropDown actionBarDropDown = ActionBarWidgetsFactory.createActionBarDropDown(mActivity);
                actionBarDropDown.setSecondaryVisibility(View.GONE);
                actionBarDropDown.setArrowEnabled(false);
                actionBarDropDown.setPrimaryText("Primary text line");
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, actionBarDropDown);
            }
        });
        assertSnapShot();
    }

    public final void testABT2() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                ActionBarDropDown actionBarDropDown = ActionBarWidgetsFactory.createActionBarDropDown(mActivity);
                actionBarDropDown.setArrowEnabled(false);
                actionBarDropDown.setPrimaryText("Primary text line");
                actionBarDropDown.setSecondaryText("Secondary text line");
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, actionBarDropDown);
            }
        });
        assertSnapShot();
    }

    public final void testBK_ABD1() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mActionBarContainer.setBackUpEnabled(true);
                ActionBarDropDown actionBarDropDown = ActionBarWidgetsFactory.createActionBarDropDown(mActivity);
                actionBarDropDown.setArrowEnabled(true);
                actionBarDropDown.setPrimaryText("Primary text line");
                actionBarDropDown.setSecondaryVisibility(View.GONE);
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, actionBarDropDown);
            }
        });
        assertSnapShot();
    }

    public final void testABIL_ABT1() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                ActionBarDropDown actionBarDropDown = ActionBarWidgetsFactory.createActionBarDropDown(mActivity);
                actionBarDropDown.setSecondaryVisibility(View.GONE);
                actionBarDropDown.setArrowEnabled(false);
                actionBarDropDown.setPrimaryText("Primary text line");
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, actionBarDropDown);
                ActionBarTestUtil.addLeftViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarItemView(mActivity, true));
            }
        });
        assertSnapShot();
    }

    public final void testABT1_ABIR() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                ActionBarDropDown actionBarDropDown = ActionBarWidgetsFactory.createActionBarDropDown(mActivity);
                actionBarDropDown.setSecondaryVisibility(View.GONE);
                actionBarDropDown.setArrowEnabled(false);
                actionBarDropDown.setPrimaryText("Primary text line");
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, actionBarDropDown);
                ActionBarTestUtil.addRightViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarItemView(mActivity, false));
            }
        });
        assertSnapShot();
    }

    public final void testBK_ABQ_ABD2_ABIR() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mActionBarContainer.setBackUpEnabled(true);
                ActionBarDropDown actionBarDropDown = ActionBarWidgetsFactory.createActionBarDropDown(mActivity);
                actionBarDropDown.setPrimaryText("Primary text line");
                actionBarDropDown.setSecondaryText("Secondary text line");
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, actionBarDropDown);
                ActionBarTestUtil.addRightViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarItemView(mActivity, false));

                ActionBarQuickContact actionBarQuickContact = ActionBarWidgetsFactory.createActionBarQuickContact(mActivity);
                actionBarQuickContact.setLeftMarginEnabled(false);
                ActionBarTestUtil.addLeftViewCompat(mActionBarContainer, actionBarQuickContact);
            }
        });
        assertSnapShot();
    }

    public final void testIcon_ABT2() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                ImageView imageView = new ImageView(mActivity);
                imageView.setImageResource(R.drawable.common_rearrange_rest);
                ActionBar.LayoutParams params = new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMarginStart(mActivity.getResources().getDimensionPixelSize(R.dimen.margin_m));
                params.setMarginEnd(mActivity.getResources().getDimensionPixelSize(R.dimen.margin_m));
                imageView.setLayoutParams(params);
                ActionBarTestUtil.addLeftViewCompat(mActionBarContainer, imageView);
                ActionBarDropDown actionBarDropDown = ActionBarWidgetsFactory.createActionBarDropDown(mActivity);
                actionBarDropDown.setPrimaryText("Primary text line");
                actionBarDropDown.setSecondaryText("Secondary text line");
                actionBarDropDown.setArrowEnabled(false);
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, actionBarDropDown);
            }
        });
        assertSnapShot();
    }

    public final void testBK_ABD2_ABP() {
        runProgressView();
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mActionBarContainer.setBackUpEnabled(true);
                ActionBarDropDown actionBarDropDown = ActionBarWidgetsFactory.createActionBarDropDown(mActivity);
                actionBarDropDown.setPrimaryText("Primary text line");
                actionBarDropDown.setSecondaryText("Secondary text line");
                actionBarDropDown.setArrowEnabled(true);
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, actionBarDropDown);
            }
        });
        mSolo.sleep(1000);
        if (Build.VERSION.SDK_INT >= 21) {
            assertSnapShot(new View[] {
                    (ProgressBar) mActionBarContainer.getChildAt(1)
            });
        } else {
            assertSnapShot();
        }

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

    public final void testABT1_ABP_Menu() {
        runProgressView();
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                ActionBarDropDown actionBarDropDown = ActionBarWidgetsFactory.createActionBarDropDown(mActivity);
                actionBarDropDown.setPrimaryText("Primary text line");
                actionBarDropDown.setArrowEnabled(false);
                actionBarDropDown.setSecondaryVisibility(View.GONE);
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, actionBarDropDown);
                enableActivityMenu();
            }
        });
        mSolo.sleep(1000);
        if (Build.VERSION.SDK_INT >= 21) {
            assertSnapShot(new View[] {
                    (ProgressBar) mActionBarContainer.getChildAt(0)
            });
        } else {
            assertSnapShot();
        }

    }

    public final void testBK_ABS() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mActionBarContainer.setBackUpEnabled(true);
                ActionBarSearch actionBarSearch = ActionBarWidgetsFactory.createActionBarSearch(mActivity);
                actionBarSearch.setClearIconVisibility(View.GONE);
                actionBarSearch.setProgressVisibility(View.GONE);
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, actionBarSearch);
            }
        });
        assertSnapShot();
    }

    public final void testABS_ABIR() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                ActionBarSearch actionBarSearch = ActionBarWidgetsFactory.createActionBarSearch(mActivity);
                actionBarSearch.setClearIconVisibility(View.GONE);
                actionBarSearch.setProgressVisibility(View.GONE);
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, actionBarSearch);
                ActionBarTestUtil.addRightViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarItemView(mActivity, false));
            }
        });
        assertSnapShot();
    }

    public final void testBK_ABDS_ABIR() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mActionBarContainer.setBackUpEnabled(true);
                ActionBarDropDown actionBarDropDown = ActionBarWidgetsFactory.createActionBarDropDown(mActivity);
                actionBarDropDown.setSupportMode(ActionBarDropDown.MODE_ONE_MULTIILINE_TEXTVIEW);
                actionBarDropDown.setArrowEnabled(true);
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, actionBarDropDown);
                ActionBarTestUtil.addRightViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarItemView(mActivity, false));
            }
        });
        assertSnapShot();
    }

    public final void testIcon_Div_ABT1_Button() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                ImageView imageView = new ImageView(mActivity);
                imageView.setImageResource(android.R.drawable.ic_dialog_info);
                ActionBar.LayoutParams params = new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMarginStart(mActivity.getResources().getDimensionPixelSize(R.dimen.margin_m));
                params.setMarginEnd(mActivity.getResources().getDimensionPixelSize(R.dimen.margin_m));
                imageView.setLayoutParams(params);
                ActionBarTestUtil.addLeftViewCompat(mActionBarContainer, imageView);

                ImageView divider = new ImageView(mActivity);
                divider.setBackgroundColor(mActivity.getResources().getColor(android.R.color.white));
                ActionBar.LayoutParams dividerParams = new ActionBar.LayoutParams(2, ViewGroup.LayoutParams.MATCH_PARENT);
                int m2 = mActivity.getResources().getDimensionPixelSize(R.dimen.margin_m);
                dividerParams.setMargins(0, m2, m2, m2);
                divider.setLayoutParams(dividerParams);
                ActionBarTestUtil.addLeftViewCompat(mActionBarContainer, divider);

                ActionBarDropDown actionBarDropDown = ActionBarWidgetsFactory.createActionBarDropDown(mActivity);
                actionBarDropDown.setSecondaryVisibility(View.GONE);
                actionBarDropDown.setPrimaryText("Primary text line");
                actionBarDropDown.setArrowEnabled(false);

                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, actionBarDropDown);

                LinearLayout linearLayout = new LinearLayout(mActivity);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setGravity(Gravity.CENTER_VERTICAL);
                ActionBar.LayoutParams linearLayoutParams = new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                linearLayoutParams.setMarginStart(mActivity.getResources().getDimensionPixelSize(R.dimen.margin_m));
                linearLayout.setLayoutParams(linearLayoutParams);
                ActionBarTestUtil.addRightViewCompat(mActionBarContainer, linearLayout);

                TextView textView = new TextView(mActivity);
                textView.setTextAppearance(mActivity, R.style.ActionBarPrimaryTextView);
                textView.setText("Text");
                linearLayout.addView(textView);

                ImageView rightImageView = new ImageView(mActivity);
                rightImageView.setImageResource(android.R.drawable.ic_dialog_info);
                LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                linearParams.setMarginStart(mActivity.getResources().getDimensionPixelSize(R.dimen.margin_m));
                linearParams.setMarginEnd(mActivity.getResources().getDimensionPixelSize(R.dimen.margin_m));
                rightImageView.setLayoutParams(linearParams);
                linearLayout.addView(rightImageView);

            }
        });
        assertSnapShot();
    }

    public final void testIcon_ABQ_ABS_ABIR() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                ImageView imageView = new ImageView(mActivity);
                imageView.setImageResource(android.R.drawable.ic_dialog_info);
                ActionBar.LayoutParams params = new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMarginStart(mActivity.getResources().getDimensionPixelSize(R.dimen.margin_m));
                params.setMarginEnd(mActivity.getResources().getDimensionPixelSize(R.dimen.margin_m));
                imageView.setLayoutParams(params);
                ActionBarTestUtil.addLeftViewCompat(mActionBarContainer, imageView);

                ActionBarQuickContact actionBarQuickContact = ActionBarWidgetsFactory.createActionBarQuickContact(mActivity);
                actionBarQuickContact.setLeftMarginEnabled(false);
                ActionBarTestUtil.addLeftViewCompat(mActionBarContainer, actionBarQuickContact);

                ActionBarSearch actionBarSearch = ActionBarWidgetsFactory.createActionBarSearch(mActivity);
                actionBarSearch.setClearIconVisibility(View.GONE);
                actionBarSearch.setProgressVisibility(View.GONE);
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, actionBarSearch);
                ActionBarTestUtil.addRightViewCompat(mActionBarContainer, ActionBarWidgetsFactory.createActionBarItemView(mActivity, false));
            }
        });
        assertSnapShot();
    }

    public final void testABD1() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                ActionBarDropDown actionBarDropDown = ActionBarWidgetsFactory.createActionBarDropDown(mActivity);
                actionBarDropDown.setArrowEnabled(true);
                actionBarDropDown.setPrimaryText("Primary text line");
                actionBarDropDown.setSecondaryVisibility(View.GONE);
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, actionBarDropDown);
            }
        });
        assertSnapShot();
    }

    public final void testABD2() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                ActionBarDropDown actionBarDropDown = ActionBarWidgetsFactory.createActionBarDropDown(mActivity);
                actionBarDropDown.setArrowEnabled(true);
                actionBarDropDown.setPrimaryText("Primary text line");
                actionBarDropDown.setSecondaryText("Secondary text line");
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, actionBarDropDown);
            }
        });
        assertSnapShot();
    }

    public final void testUpdaingWithTitle() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mActionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_UPDATING_WITH_TITLE);
                mActionBarContainer.setUpdatingViewText(ActionBarContainer.UPDATING_MODE_UPDATING_WITH_TITLE, "Primary text line");
                stopProgressBarRunningInActionBarRefresh();
            }
        });
        assertSnapShot();
    }

    private void stopProgressBarRunningInActionBarRefresh() {
        ActionBarRefresh actionBarRefresh = (ActionBarRefresh) mActionBarContainer.getChildAt(0);
        ProgressBar progressBar = (ProgressBar) actionBarRefresh.getChildAt(1);
        WidgetUtil.setProgressBarIndeterminatedStopRunning(progressBar);
    }

    public final void testABT1_ABP() {
        runProgressView();
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                ActionBarDropDown actionBarDropDown = ActionBarWidgetsFactory.createActionBarDropDown(mActivity);
                actionBarDropDown.setSecondaryVisibility(View.GONE);
                actionBarDropDown.setArrowEnabled(false);
                actionBarDropDown.setPrimaryText("Primary text line");
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, actionBarDropDown);
            }
        });
        mSolo.sleep(1000);
        if (Build.VERSION.SDK_INT >= 21) {
            assertSnapShot(new View[] {
                    (ProgressBar) mActionBarContainer.getChildAt(0)
            });
        } else {
            assertSnapShot();
        }

    }

    public final void testABS() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mActionBarSearch = ActionBarWidgetsFactory.createActionBarSearch(mActivity, true);
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, mActionBarSearch);
            }
        });
        mSolo.sleep(1000);
        if (Build.VERSION.SDK_INT >= 21) {
            assertSnapShot(new View[] {
                    (ProgressBar) mActionBarSearch.getChildAt(2)
            });
        } else {
            assertSnapShot();
        }
    }

    public final void testPullDownWithTitle() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mActionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_PULLDOWN);
                mActionBarContainer.setUpdatingViewText(ActionBarContainer.UPDATING_MODE_PULLDOWN, "LAST UPDATE 10:08AM");
            }
        });
        /* wait for layout animation */
        mSolo.sleep(2000);
        assertSnapShot();
    }

}
