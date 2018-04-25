
package com.htc.sense.commoncontrol.demo;

import android.graphics.drawable.ColorDrawable;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;

import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.widget.ActionBarContainer;
import com.htc.lib1.cc.widget.ActionBarSearch;

public abstract class CommonDemoActivityBase2 extends CommonDemoActivityBase {

    private ActionBarContainer mSearchContainer;
    private boolean mSeachVisible = false;
    private boolean mEnableSearch = true;

    protected static final int MENU_ITEM_ID_SEARCH = 0x8888;

    private AutoCompleteTextView mAutoCompleteTextView;
    private HtcListIntentFragment mListIntentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mListIntentFragment = (HtcListIntentFragment) getFragmentManager().findFragmentByTag("HtcListIntentFragment");
        }
        if (mListIntentFragment == null) {
            if (getActivityIntent() != null) {
                mListIntentFragment = new HtcListIntentFragment(getActivityIntent());
            } else {
                mListIntentFragment = new HtcListIntentFragment(getActivityList());
            }
        }

        if (!mListIntentFragment.isAdded()) {
            getFragmentManager().beginTransaction().replace(android.R.id.content, mListIntentFragment, "HtcListIntentFragment").commit();
        }
        if (shouldApplyHtcActionBar()) {
            mSearchContainer = mActionBarExt.getSearchContainer();
            mSearchContainer.setBackground(new ColorDrawable(HtcCommonUtil.getCommonThemeColor(this, R.styleable.ThemeColor_multiply_color)));
            mSearchContainer.setBackUpEnabled(true);
            mSearchContainer.setBackUpOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    toggleSearch();
                }
            });
            ActionBarSearch actionBarSearch = new ActionBarSearch(this);
            mSearchContainer.addCenterView(actionBarSearch);
            mAutoCompleteTextView = actionBarSearch.getAutoCompleteTextView();
            mAutoCompleteTextView.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (mListIntentFragment != null) {
                        ListAdapter adapter = mListIntentFragment.getListAdapter();
                        if (adapter == null || !(adapter instanceof Filterable)) {
                            return;
                        }
                        Filter filter = ((Filterable) adapter).getFilter();
                        if (filter != null) {
                            filter.filter(s);
                        }
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }
    /**
     * The List of Activity shoud be showed in HtcListView.
     *
     * @return Array of activity.
     */
    protected Class<?>[] getActivityList() {
        return null;
    }

    protected Intent getActivityIntent() {
        return null;
    }

    public void setSearchEnabled(boolean enableSeach) {
        if (enableSeach == mEnableSearch) {
            return;
        }

        mEnableSearch = enableSeach;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mEnableSearch && shouldApplyHtcActionBar()) {
            MenuItem searchItem = menu.add(MENU_ITEM_ID_SEARCH, MENU_ITEM_ID_SEARCH, 0, "Search");
            searchItem.setIcon(R.drawable.icon_btn_search_dark);
            searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_ITEM_ID_SEARCH) {
            if (!mSeachVisible) {
                toggleSearch();
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mSeachVisible) {
            toggleSearch();
        } else {
            super.onBackPressed();
        }
    }

    private void toggleSearch() {
        mSeachVisible = !mSeachVisible;
        if (!mSeachVisible) {
            mAutoCompleteTextView.setText("");
        }
        mActionBarExt.switchContainer();
    }
}
