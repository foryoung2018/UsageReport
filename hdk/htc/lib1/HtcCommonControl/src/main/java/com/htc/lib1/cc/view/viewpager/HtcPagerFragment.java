package com.htc.lib1.cc.view.viewpager;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;

import com.htc.lib1.cc.view.tabbar.TabBar;
import com.htc.lib1.cc.view.tabbar.TabReorderAdapterFactory;
import com.htc.lib1.cc.view.tabbar.TabReorderFragment;
import com.htc.lib1.cc.view.tabbar.TabReorderFragment.TabReorderAdapter;
import com.htc.lib1.cc.view.viewpager.HtcViewPager.PageTransformer;
import com.htc.lib1.cc.widget.HtcFooter;
import com.htc.lib1.cc.widget.HtcOverlapLayout;

public abstract class HtcPagerFragment extends Fragment {
    private static final String TAG = "PagerFragment";

    private ViewGroup mContent;
    private TabBar mTabBar;
    private HtcViewPager mPager;
    private HtcFooter mFooter;
    boolean mEditable = true;

    private EditingListener mEditingListener;
    private boolean mUserSpecifyBarVisibility;

    /**
     * @hide
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Context context = inflater.getContext();

        mPager = onCreatePager(inflater);
        HtcPagerAdapter adapter = onCreateAdapter(context);
        mPager.setAdapter(adapter);
        //set PageTransformer to enable layer cache during scrolling
        mPager.setPageTransformer(false, new PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
            }
        });

        //invoke getPageTitle() to check if the method is overridden
        //if yes, the field mPageTitleStrategyImplemented's value is still true
        try {
            adapter.getPageTitle(0);
        } catch (Throwable e) {
            //if there is any exception, it means user has overridden the getPageTitile();
        }
        if (adapter.mPageTitleStrategyImplemented) {
            mTabBar = onCreateTabBar(context);
            if (mTabBar != null) {
                HtcViewPager.LayoutParams params = new HtcViewPager.LayoutParams();
                params.gravity = Gravity.TOP;
                params.height = mTabBar.getBarHeight();
                params.width = HtcViewPager.LayoutParams.MATCH_PARENT;
                mPager.addView(mTabBar,params);
                mTabBar.linkWithParent(mPager);
            }
        }

        HtcOverlapLayout content = new HtcOverlapLayout(context);
        if (!getActivity().getWindow().hasFeature(Window.FEATURE_ACTION_BAR_OVERLAY)) {
            content.isActionBarVisible(false);
        }
        if (mFooter != null) {
            content.addView(mFooter);
        } else {
            View dummy = new View(inflater.getContext());
            dummy.setVisibility(View.GONE);
            content.addView(dummy);
        }
        content.addView(mPager);
        content.setId(android.R.id.tabhost);
        mPager.setId(android.R.id.tabcontent);
        return mContent = content;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if detect bundle contain hidden data, hide fragment
        if (savedInstanceState != null && savedInstanceState.getBoolean("isHidden", false)) {
            getFragmentManager()
            .beginTransaction()
            .hide(this)
            .commit();
        }
    }
    /**
     * @hide
     * @param context
     * @return
     * @deprecated [Module internal use]
     */
    protected TabBar onCreateTabBar(Context context) {
        TabBar tabBar = new TabBar(context) {
            /**
             * This method will be called when adapter change.
             * @hide
             */
            @Override
            public void populateTabStrip() {
                super.populateTabStrip();
                if (!mUserSpecifyBarVisibility) {
                    HtcPagerAdapter adapter = mPager.getAdapter();
                    if (adapter != null) {
                        //auto hide if only one tab or lesser than one or doesn't need page title
                        if (adapter.getCount() <= 1) {
                            //if adapter data not ready, check if there is visible tag
                            //and decide set Visibility gone or not
                            if(adapter.getCount() == 0 && adapter instanceof HtcTabFragmentPagerAdapter){
                                int count = ((HtcTabFragmentPagerAdapter)adapter).preGetVisibleTagCount();
                                setVisibility(count <= 1 ? View.GONE : View.VISIBLE );
                            }else{
                                setVisibility(View.GONE);
                            }
                        } else {
                            setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        };
        tabBar.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mEditable && startEditing()) {
                    return true;
                }
                return false;
            }
        });
        return tabBar;
    }

    protected HtcViewPager onCreatePager(LayoutInflater inflater) {
        Context context = inflater.getContext();
        return new HtcViewPager(context);
    }

    protected abstract HtcPagerAdapter onCreateAdapter(Context context);

    public HtcPagerAdapter getAdapter() {
        return mPager == null ? null : mPager.getAdapter();
    }

    public TabBar getTabBar() {
        return mTabBar;
    }

    public HtcViewPager getPager() {
        return mPager;
    }

    public void setFooter(HtcFooter footer) {
        mFooter = footer;
        if (mContent instanceof HtcOverlapLayout) {
            View oldFooter = mContent.getChildAt(0);
            if (mFooter != null) {
                mContent.addView(mFooter, 0);
            } else {
                View dummy = new View(mContent.getContext());
                dummy.setVisibility(View.GONE);
                mContent.addView(dummy, 0);
            }
            mContent.removeView(oldFooter);
        }
    }

    /**
     * @hide
     */
    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * @hide
     */
    @Override
    public void onPause() {
        super.onPause();
        if (getAdapter() != null && mEditable) {
            getAdapter().saveReorderData();
        }
    }

    /**
     * @hide
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //unset adapter
        mPager.setAdapter(null);
        mContent = null;
        mPager = null;
        mTabBar = null;
        mFooter = null;
    }

    private void showView(View view, Animation animation) {
        Animation ani = view.getAnimation();
        if(ani != null && ani.hasStarted() && !ani.hasEnded()) {
            ani.cancel();
            ani.setAnimationListener(null);
            view.clearAnimation();
        }

        if (view.getVisibility() == View.VISIBLE) {
            return;
        }

        if (animation != null) {
            view.setVisibility(View.VISIBLE);
            animation.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationEnd(Animation ani) {
                }
                @Override
                public void onAnimationRepeat(Animation ani) {
                }
                @Override
                public void onAnimationStart(Animation ani) {
                }
            });
            view.startAnimation(animation);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    private void hideView(final View view, Animation animation) {
        Animation ani = view.getAnimation();
        if(ani != null && ani.hasStarted() && !ani.hasEnded()) {
            ani.cancel();
            ani.setAnimationListener(null);
            view.clearAnimation();
        }

        if (view.getVisibility() == View.GONE) {
            return;
        }

        if (animation != null) {
            animation.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationEnd(Animation ani) {
                    view.setVisibility(View.GONE);
                }
                @Override
                public void onAnimationRepeat(Animation ani) {
                }
                @Override
                public void onAnimationStart(Animation ani) {
                }
            });
            view.startAnimation(animation);
        } else {
            view.setVisibility(View.GONE);
        }
    }


    private class TabBarAnimation extends Animation {
        private boolean mShow;
        private TabBarAnimation(boolean show) {
            mShow = show;
            setDuration(150);
        }
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            HtcViewPager.LayoutParams lp = (HtcViewPager.LayoutParams)mTabBar.getLayoutParams();
            if (mShow) {
                lp.decorTop = (int)(-mTabBar.getHeight() * (1-interpolatedTime));
            } else {
                lp.decorTop = (int)(-mTabBar.getHeight() * (interpolatedTime));
            }
            for (int i = 0, n = mPager.getChildCount(); i < n;i++) {
                View v = mPager.getChildAt(i);
                lp = (HtcViewPager.LayoutParams)v.getLayoutParams();
                //re-measure page views
                if (!lp.isDecor) {
                    lp.needsMeasure = true;
                }
                v.forceLayout();
            }
            mPager.requestLayout();
        }
    }

    /**
     * Show the tab bar. Please do NOT play another animation while tab bar showing.
     *
     * @param withAnimation Play animation when showing tab bar.
     *
     * @see #hideTabBar(boolean)
     */
    private void showTabBar(boolean withAnimation) {
        mUserSpecifyBarVisibility = true;
        if (mTabBar == null) {
            return;
        }
        if (!withAnimation) {
            HtcViewPager.LayoutParams lp = (HtcViewPager.LayoutParams)mTabBar.getLayoutParams();
            lp.decorTop = 0;
        }
        showView(mTabBar, withAnimation ? new TabBarAnimation(true) : null);
    }

    /**
     * Hide the tab bar. Please do NOT play another animation while tab bar hiding.
     *
     * @param withAnimation Play animation when hiding tab bar.
     *
     * @see #showTabBar(boolean)
     */
    private void hideTabBar(boolean withAnimation) {
        mUserSpecifyBarVisibility = true;
        if (mTabBar == null) {
            return;
        }
        if (!withAnimation) {
            HtcViewPager.LayoutParams lp = (HtcViewPager.LayoutParams)mTabBar.getLayoutParams();
            lp.decorTop = -mTabBar.getBarHeight();
        }
        hideView(mTabBar, withAnimation ? new TabBarAnimation(false) : null);
    }

    public void showTabBar() {
        showTabBar(false);
    }

    public void hideTabBar() {
        hideTabBar(false);
    }

    public void showFooter() {
        if (mFooter == null) {
            return;
        }
        showView(mFooter, null);
    }

    public void hideFooter() {
        if (mFooter == null) {
            return;
        }
        hideView(mFooter, null);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //if detect fragment is hidden, save value in bundle and hide again once re-create
        if(outState != null){
            outState.putBoolean("isHidden", isHidden());
        }
    }

    /**
     * Enable or disable edit mode.
     * @param editable Default value is true.
     */
    public void setEditable(boolean editable) {
        this.mEditable = editable;
    }

    private boolean canEdit() {
        if (!(getAdapter() instanceof TabReorderAdapterFactory)) {
            Log.d(TAG, "To enable editing, the adapter must implement TabReorderAdapterFactory");
            return false;
        }
        View view = getView();
        return !isEditing() &&
            //if the view detached from window, should not enter edit mode. see View.dispatchDetachedFromWindow()
            view != null && View.GONE != view.getWindowVisibility();
    }

    private boolean isEditing() {
        Fragment f = getChildFragmentManager().findFragmentByTag("TabReorderPanel");
        return f != null && f.isResumed();
    }

    /**
     * Called when enter edit mode. You should call super.startEditing() if you override this method.
     */
    public boolean startEditing() {
        if (canEdit()) {
            TabReorderFragment reorder = new PagerReorderFragment();
            reorder.setAdapter(createTabReorderAdapter());
            FragmentManager fm = getChildFragmentManager();
            fm.beginTransaction()
                    .add(android.R.id.tabhost, reorder, "TabReorderPanel")
                    .addToBackStack("TabBarEditMode")
                    .commitAllowingStateLoss();
            fm.executePendingTransactions();

//            mTabHost.mTabContent.dontCheckChildFocus();
            if (mEditingListener != null) {
                mEditingListener.onStart();
            }
            return true;
        }
        return false;
    }

    private TabReorderAdapter createTabReorderAdapter() {
        return new TabReorderAdapter() {
            private TabReorderAdapter mAdapter;
            private int mCurrent;
            {
                mAdapter = ((TabReorderAdapterFactory)getAdapter()).createTabReorderAdapter();
                int currentVisible = getPager().getCurrentItem();
                for (int i = 0, v = 0, n = getCount(); i < n; i++) {
                    if (isVisible(i)) {
                        if (currentVisible == v) {
                            mCurrent = i;
                            break;
                        }
                        v++;
                    }
                }
                Log.d(TAG, "Before reorder: [" + mCurrent + "]");
            }

            @Override
            public int getCount() {
                return mAdapter.getCount();
            }

            public void registerDataSetObserver(DataSetObserver observer) {
                mAdapter.registerDataSetObserver(observer);
            }

            public void unregisterDataSetObserver(DataSetObserver observer) {
                mAdapter.unregisterDataSetObserver(observer);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mAdapter.getPageTitle(position);
            }

            @Override
            public int getPageCount(int position) {
                return mAdapter.getPageCount(position);
            }

            @Override
            public boolean isAutomotiveMode() {
                return mAdapter.isAutomotiveMode();
            }

            @Override
            public boolean isVisible(int position) {
                return mAdapter.isVisible(position);
            }

            @Override
            public boolean isRemoveable(int position) {
                return mAdapter.isRemoveable(position);
            }

            @Override
            public void onMove(int from, int to) {
                mAdapter.onMove(from, to);
                Log.d(TAG, "[" + mCurrent + "] " + from  + "->" + to);
                //
                // ----
                // |  |
                // |  v
                // 1 2 3 c
                //or
                // ------
                // |    |
                // |    v
                // 1 2 3 c
                if (from < mCurrent && to < mCurrent) {
                    //do nothing
                    return;
                }
                //
                // --------
                // |      |
                // |      v
                // 1 2 3 c
                if (from < mCurrent && to >= mCurrent) {
                    mCurrent--;
                    Log.d(TAG, "-- " + mCurrent);
                    return;
                }
                //
                //  ------
                //  |    |
                //  v    |
                // c 4 5 6
                //or
                //    ----
                //    |  |
                //    v  |
                // c 4 5 6
                if (from > mCurrent && to > mCurrent) {
                    //do nothing
                    return;
                }
                //
                // --------
                // |      |
                // v      |
                //  c 4 5 6
                if (from > mCurrent && to <= mCurrent) {
                    mCurrent++;
                    Log.d(TAG, "++ " + mCurrent);
                    return;
                }

                if (from == mCurrent) {
                    //
                    // --------
                    // |      |
                    // v      |
                    //  1 2 3 c
                    //
                    // --------
                    // |      |
                    // |      v
                    // c 4 5 6
                    mCurrent = to;
                    Log.d(TAG, "= " + mCurrent);
                }
            }

            @Override
            public void onExit(boolean save) {
                if (save) {
                    int currentVisible = -1;
                    for (int i = 0; i <= mCurrent; i++) {
                        if (isVisible(i)) {
                            currentVisible++;
                        }
                    }
                    Log.d(TAG, "After reorder: [" + mCurrent + "]");
                    getPager().setCurrentItemOnly(currentVisible);
                }
                mAdapter.onExit(save);
            }

            @Override
            public boolean onVisibilityChanged(int position, boolean visible) {
                return mAdapter.onVisibilityChanged(position, visible);
            }

            @Override
            public boolean isCNMode() {
                return mAdapter.isCNMode();
            }
        };
    }

    /**
     * Called when exit edit mode. You should call super.stopEditing() if you override this method.
     */
    public boolean stopEditing() {
        /** In order to fix the issue that the user press home key and this fragment
         * doesn't do anything. the user can't go into reorder anymore.
         * the root cause is that isEditing() will reference the fragment and
         * the fragment is paused and the TabBarEditMode will never pop.
         **/
        if ( null != getChildFragmentManager().findFragmentByTag("TabReorderPanel") ) {
            if (getChildFragmentManager().popBackStackImmediate("TabBarEditMode", FragmentManager.POP_BACK_STACK_INCLUSIVE)) {
                if (mEditingListener != null) {
                    mEditingListener.onStop();
                }
//                mTabHost.mTabContent.checkChildFocus();
                //naeco: 20121212 KW
//                View selected = mTabHost.mTabContent.getSelectedView();
//                if(selected != null)
//                    selected.requestFocus();
                return true;
            }
        }
        return false;
    }

    /**
     * @hide
     * @deprecated [Module internal use]
     */
    public static class PagerReorderFragment extends TabReorderFragment {
        @Override
        protected void onDismiss() {
            super.onDismiss();
            ((HtcPagerFragment)getParentFragment()).stopEditing();
        }
    }

    public void setEditingListener(EditingListener editingListener) {
        this.mEditingListener = editingListener;
    }

    public static interface EditingListener {
        void onStart();

        void onStop();
    }
}
