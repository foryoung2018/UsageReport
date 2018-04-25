
package com.htc.lib1.cc.layout.activityhelper;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.htc.lib1.cc.test.R;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.widget.ActionBarContainer;
import com.htc.lib1.cc.widget.ActionBarExt;
import com.htc.lib1.cc.widget.ActionBarText;
import com.htc.lib1.cc.widget.HtcEmptyView;
import com.htc.lib1.cc.widget.HtcRimButton;
import com.htc.lib1.cc.widget.RefreshGestureDetector;

public class HtcEmptyViewDemo extends ActivityBase {

    private ActionBarExt actionBarExt = null;

    private ActionBarContainer actionBarContainer = null;

    ActionBarText actionBarText;

    final CharSequence DARK_MODE = "dark mode";

    final CharSequence LIGHT_MODE = "light mode";

    final CharSequence ENABLE_PULL_DOWN = "enable pull down";

    final CharSequence DISABLE_PULL_DOWN = "disable pull down";

    final CharSequence ADD_BUTTON = "add button";

    final CharSequence REMOVE_BUTTON = "remove button";

    final CharSequence AUTOMOTIVE_MODE = "automotive mode";

    final CharSequence GENERIC_MODE = "generic mode";

    RefreshGestureDetector.RefreshListener mRefreshListener;

    HtcEmptyView mEmptyView;

    HtcRimButton mButton;

    Runnable mUpdateRunnable;

    Handler mHandler;

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState
     *            If the activity is being re-initialized after previously being
     *            shut down then this Bundle contains the data it most recently
     *            supplied in onSaveInstanceState(Bundle). Notice: Otherwise it
     *            is null.
     * @see onStart() onSaveInstanceState(Bundle) onRestoreInstanceState(Bundle)
     *      onPostCreate(Bundle)
     */
    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        HtcCommonUtil.initTheme(this, mCategoryId);
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawableResource(R.drawable.common_app_bkg);
        actionBarExt = new ActionBarExt(this, getActionBar());
        actionBarContainer = actionBarExt.getCustomContainer();
        actionBarText = new ActionBarText(this);
        actionBarText.setPrimaryText("HtcEmptyView Demo");
        actionBarContainer.addCenterView(actionBarText);
        actionBarContainer
                .setUpdatingViewClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        actionBarContainer
                                .setUpdatingState(ActionBarContainer.UPDATING_MODE_NORMAL);
                        if (mUpdateRunnable != null) {
                            mHandler.removeCallbacks(mUpdateRunnable);
                        }
                    }
                });

        setContentView(R.layout.htcemptyview);

        mHandler = new Handler();

        mButton = (HtcRimButton) findViewById(R.id.button);
        mButton.setVisibility(View.GONE);

        mEmptyView = (HtcEmptyView) findViewById(R.id.empty);

        mRefreshListener = new RefreshGestureDetector.RefreshListener() {

            @Override
            public void onGapChanged(int gap, int maxGap) {
                // Follow finger to rotate the action bar title.
                if (!(actionBarContainer.getUpdatingState() == ActionBarContainer.UPDATING_MODE_UPDATING)) {
                    if (actionBarContainer.getRotationProgress() == 0) {
                        actionBarContainer.setRotationMax(maxGap);
                    }
                    actionBarContainer.setRotationProgress(gap);
                }
            }

            @Override
            public void onFinish() {
                // Only if the state is NOT updating and the action bar
                // enters the pull-down state,
                // we can start to update the list view content.
                if (!(actionBarContainer.getUpdatingState() == ActionBarContainer.UPDATING_MODE_UPDATING)
                        && actionBarContainer.getUpdatingState() == ActionBarContainer.UPDATING_MODE_PULLDOWN) {
                    actionBarContainer.setUpdatingViewText(
                            ActionBarContainer.UPDATING_MODE_UPDATING,
                            "UPDATING...");
                    actionBarContainer
                            .setUpdatingState(ActionBarContainer.UPDATING_MODE_UPDATING);
                    if (mUpdateRunnable == null) {
                        mUpdateRunnable = new Runnable() {
                            public void run() {
                                actionBarContainer
                                        .setUpdatingState(ActionBarContainer.UPDATING_MODE_NORMAL);
                                actionBarContainer
                                        .setUpdatingViewText(
                                                ActionBarContainer.UPDATING_MODE_PULLDOWN,
                                                "LAST UPDATED PM10:10");
                            }
                        };
                    }
                    mHandler.postDelayed(mUpdateRunnable, 1500);
                }
            }

            @Override
            public void onCancel() {
                if (!(actionBarContainer.getUpdatingState() == ActionBarContainer.UPDATING_MODE_UPDATING)) {
                    actionBarContainer
                            .setUpdatingState(ActionBarContainer.UPDATING_MODE_NORMAL);
                }
            }

            @Override
            public void onBoundary() {
                if (!(actionBarContainer.getUpdatingState() == ActionBarContainer.UPDATING_MODE_UPDATING)) {
                    actionBarContainer.setRotationProgress(actionBarContainer
                            .getRotationMax());
                }
            }
        };
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.htcemptyview_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.dark_mode:
                if (item.getTitle().equals(DARK_MODE)) {
                    item.setTitle(LIGHT_MODE);
                    getWindow().setBackgroundDrawableResource(
                            R.drawable.common_app_bkg_dark);
                    mEmptyView.setMode(HtcEmptyView.MODE_DARK);
                    mEmptyView.setText("DarkMode");
                } else {
                    item.setTitle(DARK_MODE);
                    getWindow().setBackgroundDrawableResource(
                            R.drawable.common_app_bkg);
                    mEmptyView.setMode(HtcEmptyView.MODE_NORMAL);
                    mEmptyView.setText("LightMode");
                }
                return true;
            case R.id.pull_down:
                if (item.getTitle().equals(ENABLE_PULL_DOWN)) {
                    item.setTitle(DISABLE_PULL_DOWN);
                    mEmptyView.setRefreshListener(mRefreshListener);
                } else {
                    item.setTitle(ENABLE_PULL_DOWN);
                    mEmptyView.setRefreshListener(null);
                }
                return true;
            case R.id.add_button:
                if (item.getTitle().equals(ADD_BUTTON)) {
                    item.setTitle(REMOVE_BUTTON);
                    mButton.setVisibility(View.VISIBLE);
                } else {
                    item.setTitle(ADD_BUTTON);
                    mButton.setVisibility(View.GONE);
                }
                return true;
            case R.id.automotive_mode:
                if (item.getTitle().equals(AUTOMOTIVE_MODE)) {
                    item.setTitle(GENERIC_MODE);
                    getWindow().setBackgroundDrawableResource(
                            R.drawable.common_app_bkg_dark);
                    mEmptyView.setMode(HtcEmptyView.MODE_AUTOMOTIVE);
                    mEmptyView.setText("AutoMotiveMode");
                } else {
                    item.setTitle(AUTOMOTIVE_MODE);
                    getWindow().setBackgroundDrawableResource(
                            R.drawable.common_app_bkg);
                    mEmptyView.setMode(HtcEmptyView.MODE_NORMAL);
                    mEmptyView.setText("GenericMode");
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void improveCoverage()
    {
        HtcEmptyView mEmptyViewNew = new HtcEmptyView(this);
        mEmptyViewNew.setText(R.string.string_name);
        mEmptyViewNew.setRefreshListener(mRefreshListener);
    }
}
