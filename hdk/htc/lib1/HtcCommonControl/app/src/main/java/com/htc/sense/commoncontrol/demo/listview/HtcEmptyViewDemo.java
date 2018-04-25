
package com.htc.sense.commoncontrol.demo.listview;

import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.htc.lib1.cc.widget.ActionBarContainer;
import com.htc.lib1.cc.widget.HtcButtonUtil;
import com.htc.lib1.cc.widget.HtcEmptyView;
import com.htc.lib1.cc.widget.HtcRimButton;
import com.htc.lib1.cc.widget.RefreshGestureDetector;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

public class HtcEmptyViewDemo extends CommonDemoActivityBase {

    private ActionBarContainer actionBarContainer = null;

    final CharSequence DARK_MODE = "dark mode";

    final CharSequence LIGHT_MODE = "light mode";

    final CharSequence ENABLE_PULL_DOWN = "enable pull down";

    final CharSequence DISABLE_PULL_DOWN = "disable pull down";

    final CharSequence ADD_BUTTON = "add button";

    final CharSequence REMOVE_BUTTON = "remove button";

    final CharSequence AUTOMOTIVE_MODE = "automotive mode";

    final CharSequence GENERIC_MODE = "generic mode";

    boolean modeIdentifier = true;

    RefreshGestureDetector.RefreshListener mRefreshListener;

    HtcEmptyView mEmptyView;

    HtcRimButton mButton;

    Runnable mUpdateRunnable;

    Handler mHandler;

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *            previously being shut down then this Bundle contains the data
     *            it most recently supplied in onSaveInstanceState(Bundle).
     *            Notice: Otherwise it is null.
     * @see onStart() onSaveInstanceState(Bundle) onRestoreInstanceState(Bundle)
     *      onPostCreate(Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawableResource(R.drawable.common_app_bkg);
        actionBarContainer = mActionBarExt.getCustomContainer();
        actionBarContainer.setUpdatingViewClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                actionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_NORMAL);
                if (mUpdateRunnable != null) {
                    mHandler.removeCallbacks(mUpdateRunnable);
                }
            }
        });

        setContentView(R.layout.htcemptyview);

        mHandler = new Handler();

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
                            ActionBarContainer.UPDATING_MODE_UPDATING, "UPDATING...");
                    actionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_UPDATING);
                    if (mUpdateRunnable == null) {
                        mUpdateRunnable = new Runnable() {
                            public void run() {
                                actionBarContainer
                                        .setUpdatingState(ActionBarContainer.UPDATING_MODE_NORMAL);
                                actionBarContainer.setUpdatingViewText(
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
                    actionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_NORMAL);
                }
            }

            @Override
            public void onBoundary() {
                if (!(actionBarContainer.getUpdatingState() == ActionBarContainer.UPDATING_MODE_UPDATING)) {
                    actionBarContainer.setRotationProgress(actionBarContainer.getRotationMax());
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
                    modeIdentifier = false;
                } else {
                    item.setTitle(DARK_MODE);
                    getWindow().setBackgroundDrawableResource(R.drawable.common_app_bkg);
                    mEmptyView.setMode(HtcEmptyView.MODE_NORMAL);
                    mEmptyView.setText("LightMode");
                    modeIdentifier = true;
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
                    if(modeIdentifier){
                        mButton = new HtcRimButton(getBaseContext(), HtcButtonUtil.BACKGROUND_MODE_LIGHT, true);
                        mButton.setText(R.string.htcrimbutton_text_light);
                    }else{
                        mButton = new HtcRimButton(getBaseContext(), HtcButtonUtil.BACKGROUND_MODE_DARK, true);
                        mButton.setText(R.string.htcrimbutton_text_dark);
                    }
                    if(mEmptyView.getChildCount()>1)
                        mEmptyView.removeViewAt(1);
                    mEmptyView.addView(mButton,1,new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
                } else {
                    item.setTitle(ADD_BUTTON);
                    if(mEmptyView.getChildCount()>1)
                        mEmptyView.removeViewAt(1);
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
                    getWindow().setBackgroundDrawableResource(R.drawable.common_app_bkg);
                    mEmptyView.setMode(HtcEmptyView.MODE_NORMAL);
                    mEmptyView.setText("GenericMode");
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
