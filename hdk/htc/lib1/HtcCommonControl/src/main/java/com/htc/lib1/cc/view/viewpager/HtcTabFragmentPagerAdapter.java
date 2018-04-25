package com.htc.lib1.cc.view.viewpager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.view.tabbar.TabBarUtils;
import com.htc.lib1.cc.view.tabbar.TabReorderAdapterFactory;
import com.htc.lib1.cc.view.tabbar.TabReorderFragment.TabReorderAdapter;


/**
 *
 *
 */
public abstract class HtcTabFragmentPagerAdapter extends HtcPagerAdapter implements TabReorderAdapterFactory {
    private static final String TAG = "TabAdapter";
    private static final boolean DEBUG = false;

    private final FragmentManager mFragmentManager;

    private List<String> mTabs = new ArrayList<String>();
    private List<String> mVisibleTabs = new ArrayList<String>();
    private Map<String, TabSpec> mTabSpecs = new HashMap<String, TabSpec>();
    private Set<String> mChanging = new HashSet<String>();
    private TabSpec mCurrentPrimary = null;

    private Context mContext;
    private Fragment mHost;
    private String mLogPrefix;

    public HtcTabFragmentPagerAdapter(Fragment host) {
        mFragmentManager = host.getChildFragmentManager();
        mContext = host.getActivity();
        mHost = host;
        mLogPrefix = "[" + Integer.toString(hashCode(), Character.MAX_RADIX) + "]";
        if(isPagerFragmentEditable())
            restoreReorderData();
    }

    /**
     * Return the Fragment associated with a specified tag name.
     */
    public abstract Fragment getItem(String tag);

    private boolean mInstantiatingItem;
    /**
     * @hide
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        mInstantiatingItem = true;
        String tag = mVisibleTabs.get(position);
        TabSpec tabSpec = mTabSpecs.get(tag);

        //instantiate tab container if it doesn't exist
        TabContainer tabContainer = tabSpec.mContainer;
        if (tabContainer == null) {
            int containerId = getTabContainerViewId(container, tag);
            Log.d(TAG, mLogPrefix + " instantiate [" + position + "]: tag=" + tag + "[" + Integer.toHexString(containerId) + "] " + tabSpec);
            tabContainer = (TabContainer)container.findViewById(containerId);
            if (tabContainer == null) {
                tabContainer = new TabContainer(mContext);
                tabContainer.setId(containerId);
                tabContainer.setBackground(tabSpec.mBackground);
            }
            tabSpec.setContainer(tabContainer);
        }else if(tabContainer.getVisibility() == View.GONE)
            tabContainer.setVisibility(View.VISIBLE);

        if (tabContainer.getWindowToken() == null) {
            container.addView(tabContainer);
        } else {
            //ViewPager only layouts its children in mItems. Other children won't be layouted especially after rotating
            if (tabContainer.getWidth() != tabContainer.getMeasuredHeight()
                    || tabContainer.getHeight() != tabContainer.getMeasuredHeight()) {
                tabContainer.requestLayout();
            }
        }

        //add the view of restoring fragment to tab container
        //to recover view after reorder
        Fragment fragment = mFragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            View view = fragment.getView();
            if (view != null) {
                if (view.getWindowToken() == null) {
                    tabContainer.addView(view);
                }
            }
        }

        mInstantiatingItem = false;
        return tabSpec;
    }

    /**
     * @hide
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        TabSpec tabSpec = (TabSpec)object;
        TabContainer tabContainer = tabSpec.mContainer;
        if(tabContainer != null && tabContainer.getVisibility()==View.VISIBLE)
            tabContainer.setVisibility(View.GONE);
    }

    private Runnable mSetPrimaryItemCallback;
    /**
     * @hide
     */
    @Override
    public void setPrimaryItem(final ViewGroup container, final int position, final Object object) {
        TabSpec tabSpec = (TabSpec)object;
        if (((HtcViewPager)container).isScrolling()) {
            if (mSetPrimaryItemCallback != null) {
                container.removeCallbacks(mSetPrimaryItemCallback);
            }

            if (tabSpec != null) {
                final String tag = tabSpec.mTag;
                container.post(mSetPrimaryItemCallback = new Runnable() {
                    @Override
                    public void run() {
                        TabSpec spec = mTabSpecs.get(tag);
                        if (spec != null) {
                            setPrimaryItem(container, position, spec);
                        }
                    }
                });
            }
            return;
        }

        if (tabSpec != mCurrentPrimary) {
            Log.d(TAG, mLogPrefix + " primary [" + position + "]: " + mCurrentPrimary + " -> " + tabSpec);
            if (tabSpec != null) {
                String tag = tabSpec.mTag;
                TabContainer tabContainer = tabSpec.mContainer;
                Fragment fragment = mFragmentManager.findFragmentByTag(tag);
                if (fragment == null) {
                    fragment = getItem(tag);
                    if (fragment == null) {
                        throw new RuntimeException(getClass().getName() + ".getItem(" + tag + ") returns null");
                    }
                    Log.d(TAG, mLogPrefix + " got " + fragment + " by " + tag);
                    int containerId = tabContainer.getId();
                    mFragmentManager
                        .beginTransaction()
                        .add(containerId, fragment, tag)
                        .commitAllowingStateLoss();
                    mFragmentManager.executePendingTransactions();
                } else {
                    Log.d(TAG, mLogPrefix + " found " + fragment + " by " + tag);
                    View view = fragment.getView();
                    if (view != null && view.getWindowToken() == null) {
                        //if the fragment is started, add its view back
                        tabContainer.addView(view);
                    }
                }
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }

            if (mCurrentPrimary != null) {
                Fragment lastFragment = mFragmentManager.findFragmentByTag(mCurrentPrimary.mTag);
                if (lastFragment != null) {
                    lastFragment.setMenuVisibility(false);
                    lastFragment.setUserVisibleHint(false);
                }
            }

            onTabChanged(mCurrentPrimary != null ? mCurrentPrimary.mTag : null, tabSpec != null ? tabSpec.mTag : null);
            mCurrentPrimary = tabSpec;
        }
    }

    /**
     * @hide
     */
    @Override
    public void startUpdate(ViewGroup container) {
        if (mSetPrimaryItemCallback != null) {
            container.removeCallbacks(mSetPrimaryItemCallback);
        }
    }

    /**
     * @hide
     */
    @Override
    public void finishUpdate(ViewGroup container) {
        mChanging.clear();
        //finishUpdate after unregister pager observer
        if (mUnsetFromViewPager) {
            unsetFromViewPager();
        }
    }

    /**
     * @hide
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        TabSpec tabSpec = (TabSpec)object;
        return tabSpec.mContainer == view;
    }

    /**
     * @hide
     */
    @Override
    public Parcelable saveState() {
        Bundle state = new Bundle();
        ArrayList<String> tabs = new ArrayList(mTabs);
        state.putStringArrayList("tabs", tabs);

        boolean[] menuVisibility = new boolean[tabs.size()];
        String currentTag = mCurrentPrimary != null ? mCurrentPrimary.mTag : null;
        if (currentTag != null) {
            for (int i = 0, n = tabs.size(); i < n; i++) {
                String s = tabs.get(i);
                if (currentTag.equals(s)) {
                    menuVisibility[i] = true;
                    break;
                }
            }
        }
        state.putBooleanArray("tabs.menu.visibility", menuVisibility);

        return state;
    }

    /**
     * @hide
     */
    @Override
    public void restoreState(Parcelable p, ClassLoader loader) {
        Bundle state = (Bundle)p;
        List<String> tabs = state.getStringArrayList("tabs");

        boolean[] menuVisibility = state.getBooleanArray("tabs.menu.visibility");
        for (int i = 0, n = tabs.size(); i < n; i++) {
            String s = tabs.get(i);
            Fragment f = mFragmentManager.findFragmentByTag(s);
            if (f != null) {
                f.setMenuVisibility(menuVisibility[i]);
            }
        }
    }

    /**
     * @hide
     */
    @Override
    public int getItemPosition(Object object) {
        TabSpec tabSpec = (TabSpec)object;
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, mLogPrefix + " item pos: " + tabSpec + " " + mChanging.contains(tabSpec.mTag));
        }
        return mChanging.contains(tabSpec.mTag) ? POSITION_NONE : super.getItemPosition(object);
    }

    /**
     * @hide
     */
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    private boolean mUnsetFromViewPager;
    /**
     * @hide
     */
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);

        //TODO if there is a better way to determine the adapter is attached to pager
        if (observer instanceof HtcViewPager.PagerObserver) {
            //To unregister HtcViewPager.PagerObserver means the view pager replace this adapter with a new one
            mUnsetFromViewPager = false;
        }
    }

    /**
     * @hide
     */
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        super.unregisterDataSetObserver(observer);

        //TODO if there is a better way to determine the adapter is detached from pager
        if (observer instanceof HtcViewPager.PagerObserver) {
            //To unregister HtcViewPager.PagerObserver means the view pager replace this adapter with a new one
            mUnsetFromViewPager = true;
        }
    }

    private void unsetFromViewPager() {
        //if the adapter is removed from pager, cancel all action such as notifyDataSetChanged(), setPrimaryItem()
        mHandler.removeCallbacks(mNotifyDataSetChangedCallback);
        if (mSetPrimaryItemCallback != null) {
            mHandler.removeCallbacks(mSetPrimaryItemCallback);
        }

        FragmentTransaction tx = mFragmentManager.beginTransaction();
        for (String s : mTabs) {
            Fragment fragment = mFragmentManager.findFragmentByTag(s);
            if (fragment != null) {
                tx.remove(fragment);
            }
        }
        tx.commitAllowingStateLoss();
        mFragmentManager.executePendingTransactions();
        mTabs.clear();
        mVisibleTabs.clear();
        mTabSpecs.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCount() {
        return mVisibleTabs.size();
    }

    /**
     * @hide
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return mTabSpecs.get(mVisibleTabs.get(position)).mTitle;
    }

    /**
     * @hide
     */
    @Override
    public int getPageCount(int position) {
        return mTabSpecs.get(mVisibleTabs.get(position)).mCount;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mNotifyDataSetChangedCallback = new Runnable() {
        @Override
        public void run() {
            notifyDataSetChanged();
        }
    };

    private Map<String, PrefData> mPrefDatas;
    private boolean isCarouselPrefExist;
    private String PREFERENCE_NAME = "pager.data";
    private String CAROUSEL_PREFERENCE_NAME = "carousel.tasks";
    private SharedPreferences mCarouselPref = null;
    private SharedPreferences mPagerPref = null;
    private int mPreVisibleTagCount = 0;

    private void goingToNotifyDataSetChanged() {
        mHandler.removeCallbacks(mNotifyDataSetChangedCallback);
        mHandler.post(mNotifyDataSetChangedCallback);
    }

    private boolean isPagerFragmentEditable(){
        if(mHost != null && mHost instanceof HtcPagerFragment)
            return ((HtcPagerFragment)mHost).mEditable;
        else
            return true;
    }

    /**
     * This method apply to application decide whether this setting can be migrated or not
     * @param tabId the date id from carousel
     * @param tabTag the new sharedPreferences tag
     * @return if migrated or not
     */
    protected boolean onDecideMigrate(String tabId, String tabTag) {
        return tabId.equals(tabTag);
    }

    /**
     * restore reorder data; if there is reorder sequence data from previous version,
     * read from preference and adjust page sequence
     */
    private void restoreReorderData() {
        mPrefDatas = new HashMap<String, PrefData>();
        mCarouselPref = mContext.getSharedPreferences(CAROUSEL_PREFERENCE_NAME, Context.MODE_PRIVATE);
        mPagerPref = mContext.getSharedPreferences(mHost.getTag() == null? PREFERENCE_NAME:mHost.getTag(), Context.MODE_PRIVATE);

        Map<String, ?> all =mCarouselPref.getAll();
        isCarouselPrefExist = all.isEmpty() ? false:true;
        if(!isCarouselPrefExist){
            all =mPagerPref.getAll();
        }
        for (String s : all.keySet()) {
            Object v = all.get(s);
            if (v instanceof String) {
                String sv = (String)v;
                String[] tokens = new String[5];
                for (int i = 0, n = tokens.length, x = 0; i < n; i++) {
                    if (i == n - 1) {
                        tokens[i] = sv.substring(x);
                    } else {
                        int y = sv.indexOf(' ', x);
                        if (y > -1) {
                            tokens[i] = sv.substring(x, y);
                            x = y + 1;
                        } else {
                            tokens[i] = sv.substring(x);
                            break;
                        }
                    }
                }
                if(isCarouselPrefExist){
                    String tabId = s.substring(0,s.indexOf(':'));
                    if(onDecideMigrate(tabId, mHost.getTag())){
                        PrefData p = new PrefData();
                        String taskTag = s.substring(s.indexOf(':')+1,s.length());
                        p.order = Integer.parseInt(tokens[0])-1;
                        p.visible = Integer.parseInt(tokens[1]);
                        mPrefDatas.put(taskTag, p);
                    }
                }else{
                    PrefData p = new PrefData();
                    String taskTag = s;
                    p.order = Integer.parseInt(tokens[0]);
                    p.visible = Integer.parseInt(tokens[1]);
                    if(p.visible == 1){
                        mPreVisibleTagCount++;
                    }
                    mPrefDatas.put(taskTag, p);
                }
            }
        }
    }

    /**
     * @hide
     */
    @Override
    public void saveReorderData() {
        super.saveReorderData();
        Editor prefEdit = mPagerPref.edit();
        for(String s: mTabs){
            int visible = mTabSpecs.get(s).mVisible ? 1:0;
            prefEdit.putString(s, mTabs.indexOf(s)+ " " + visible+" ");
        }
        prefEdit.apply();
        if(isCarouselPrefExist){
            Editor carouselEdit = mCarouselPref.edit();
            carouselEdit.clear();
            carouselEdit.apply();
        }
        mPreVisibleTagCount = 0;
    }
    /**
     * @hide
     * @deprecated [Module internal use]
     * get tag visibility count from preference which be set from fragment destroy abnormal
     * @return
     */
    public int preGetVisibleTagCount(){
        return mPreVisibleTagCount ;
    }
    /**
     * @param tag
     * @param spec
     */
    public void addTab(String tag, TabSpec spec) {
        checkThread();
        checkTag(tag, false, false);
        Log.d(TAG, mLogPrefix + " add " + tag + " " + spec);

        if(isPagerFragmentEditable())
            adjustTabByOrder(tag,spec);
        else{
            mTabs.add(tag);
            if (spec.mVisible) {
                mVisibleTabs.add(tag);
            }
            mTabSpecs.put(tag, spec.setTag(tag));
        }
        goingToNotifyDataSetChanged();
    }

    private void adjustTabByOrder(String tag, TabSpec spec) {

        if(mPrefDatas !=null && mPrefDatas.containsKey(tag)){
            if(mTabs.isEmpty()){
                mTabs.add(tag);
            }else{
                int curOrder = mPrefDatas.get(tag).order;
                int insertIndex = -1;
                for(String s:mTabs){
                    if(mPrefDatas.get(s) != null)
                    {
                        if(curOrder < mPrefDatas.get(s).order ){
                            insertIndex = mTabs.indexOf(s);
                            break;
                        }else
                            insertIndex = mTabs.indexOf(s)+1;
                    }
                }
                insertIndex = insertIndex >= 0 ? insertIndex:0;
                mTabs.add(insertIndex, tag);
            }
            spec.mVisible = mPrefDatas.get(tag).visible==0 ? false:true;
        }
        else
            mTabs.add(mTabs.size(),tag);

        mTabSpecs.put(tag, spec.setTag(tag));
        mVisibleTabs.clear();
        for(String s:mTabs){
            if(mTabSpecs.get(s).mVisible)
                mVisibleTabs.add(s);
        }
    }
    /**
     * @param tag
     */
    public void removeTab(String tag) {
        checkThread();
        checkTag(tag, true, false);
        Log.d(TAG, mLogPrefix + " remove " + tag);

        for (int i = mVisibleTabs.indexOf(tag), n = mVisibleTabs.size(); i < n; i++) {
            mChanging.add(mVisibleTabs.get(i));
        }

        mTabs.remove(tag);
        mVisibleTabs.remove(tag);

        TabSpec tabSpec = mTabSpecs.remove(tag);

        if (removeContainer(tabSpec, true)) {
            goingToNotifyDataSetChanged();
        }
    }
    private boolean removeContainer(TabSpec tabSpec, boolean removeFragment) {
        if (tabSpec != null) {
            Fragment fragment = mFragmentManager.findFragmentByTag(tabSpec.mTag);
            if (fragment != null) {
                if (removeFragment) {
                    mFragmentManager
                        .beginTransaction()
                        .remove(fragment)
                        .commitAllowingStateLoss();
                    mFragmentManager.executePendingTransactions();
                } else {
                    //just remove fragment's view from container
                    if (tabSpec.mContainer != null) {
                        tabSpec.mContainer.removeView(fragment.getView());
                    }
                }
            }
            if (tabSpec.mContainer != null) {
                ViewGroup parent = (ViewGroup)tabSpec.mContainer.getParent();
                if (parent != null) {
                    parent.removeView(tabSpec.mContainer);
                }
            }
            tabSpec.setContainer(null);
            tabContainerViewIds.remove(tabSpec.mTag);
            return true;
        }
        return false;
    }

    /**
     * @param tag
     */
    public void stopTab(String tag) {
        checkThread();
        checkTag(tag, true, false);
        Log.d(TAG, mLogPrefix + " stop " + tag);

        TabSpec tabSpec = mTabSpecs.get(tag);

        if (tabSpec != null) {
            Fragment fragment = mFragmentManager.findFragmentByTag(tabSpec.mTag);
            if (fragment != null) {
                mFragmentManager
                    .beginTransaction()
                    .remove(fragment)
                    .commitAllowingStateLoss();
                mFragmentManager.executePendingTransactions();
            }
        }
        if(mCurrentPrimary != null && mCurrentPrimary.mTag.equals(tag)){
            mCurrentPrimary = null;
        }
    }

    /**
     * @param tag
     * @param title
     */
    public void setTitle(String tag, String title) {
        checkThread();
        checkTag(tag, true, false);
        mTabSpecs.get(tag).setTitle(title);
        goingToNotifyDataSetChanged();
    }

    /**
     * @param tag
     * @param count
     */
    public void setCount(String tag, int count) {
        checkThread();
        checkTag(tag, true, false);
        mTabSpecs.get(tag).setCount(count);
        goingToNotifyDataSetChanged();
    }

    /**
     * @param tag
     */
    public int getPagePosition(String tag) {
        checkTag(tag, true, true);
        return mVisibleTabs.indexOf(tag);
    }

    /**
     * @param position
     */
    public String getPageTag(int position) {
        int count = getCount();
        if (position < 0 || position >= count) {
            throw new IllegalArgumentException("The position '" + position + "' is out of bound [0, " + count + "]");
        }
        return mVisibleTabs.get(position);
    }

    /**
     * @param previousTag
     * @param currentTag
     */
    public void onTabChanged(String previousTag, String currentTag) {
    }

    /**
     * @param tag the tag name
     * @param exist check tag if exists
     * @param checkVisibleOnly check visible tabs only
     */
    private void checkTag(String tag, boolean exist, boolean checkVisibleOnly) {
        if (tag == null || tag.trim().length() == 0) {
            throw new IllegalArgumentException("Tag name should not be blank");
        }
        List<String> tabs = checkVisibleOnly ? mVisibleTabs : mTabs;
        if (exist) {
            int x = tabs.indexOf(tag);
            if (x < 0) {
                throw new IllegalArgumentException("Tag '" + tag + "' doesn't exist in " + (checkVisibleOnly ? "visible tabs" : "all tabs"));
            }
        } else {
            if (tabs.contains(tag)) {
                throw new RuntimeException("Tag '" + tag + "' already exists in " + (checkVisibleOnly ? "visible tabs" : "all tabs"));
            }
        }
    }

    private void checkThread() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalStateException("Must be called from main thread of process");
        }
    }

    /**
     * {@inheritDoc}
     * @see com.htc.lib1.cc.view.tabbar.TabReorderAdapterFactory#createTabReorderAdapter()
     */
    public TabReorderAdapter createTabReorderAdapter() {
        return new TabReorderAdapter() {
            private DataSetObservable mObservable = new DataSetObservable();
            private List<String> mEditingTabs = new ArrayList<String>(mTabs);
            private Map<String, Boolean> mVisiblityChanged = new HashMap<String, Boolean>();
            private Toast mToast;

            @Override
            public int getCount() {
                return mEditingTabs.size();
            }

            public void notifyDataSetChanged() {
                mObservable.notifyChanged();
            }

            public void registerDataSetObserver(DataSetObserver observer) {
                mObservable.registerObserver(observer);
            }

            public void unregisterDataSetObserver(DataSetObserver observer) {
                mObservable.unregisterObserver(observer);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTabSpecs.get(mEditingTabs.get(position)).mTitle;
            }

            @Override
            public int getPageCount(int position) {
                return mTabSpecs.get(mEditingTabs.get(position)).mCount;
            }

            @Override
            public boolean isAutomotiveMode() {
                return HtcTabFragmentPagerAdapter.this.isAutomotiveMode();
            }

            @Override
            public boolean isVisible(int position) {
                String tag = mEditingTabs.get(position);
                return mVisiblityChanged.containsKey(tag) ? mVisiblityChanged.get(tag) : mTabSpecs.get(tag).mVisible;
            }

            @Override
            public boolean isRemoveable(int position) {
                return mTabSpecs.get(mEditingTabs.get(position)).mRemovable;
            }

            @Override
            public void onMove(int from, int to) {
                mEditingTabs.add(to, mEditingTabs.remove(from));
                notifyDataSetChanged();
            }

            @Override
            public void onExit(boolean save) {
                if (save) {
                    //update order
                    mTabs.clear();
                    mTabs.addAll(mEditingTabs);
                    //update visible
                    mVisibleTabs.clear();
                    for (String s : mTabs) {
                        TabSpec tabSpec = mTabSpecs.get(s);
                        if (mVisiblityChanged.containsKey(s)) {
                            tabSpec.mVisible = mVisiblityChanged.get(s);
                            //remove all invisible fragments
                            if (!tabSpec.mVisible) {
                                removeContainer(tabSpec, true);
                            }
                        }
                        if (tabSpec.mVisible) {
                            mVisibleTabs.add(s);
                        }
                        //mark all tabs are changed
                        mChanging.add(s);
                        //and remove all containers
                        removeContainer(tabSpec, false);
                    }

                    HtcTabFragmentPagerAdapter.this.notifyDataSetChanged();
                }
            }

            @Override
            public boolean onVisibilityChanged(int position, boolean visible) {
                String tag = mEditingTabs.get(position);

                if (!visible) {
                    int visibleCount = 0;
                    for (int i = 0, n = getCount(); i < n; i++) {
                        if (isVisible(i)) {
                            visibleCount++;
                        }
                    }
                    if (visibleCount <= 1) {
                        if (mToast == null) {
                            mToast = Toast.makeText(
                                    mContext,
                                    R.string.unremovable_warning_text, Toast.LENGTH_LONG);
                        } else {
                            mToast.setText(R.string.unremovable_warning_text);
                        }
                        mToast.show();
                        return false;
                    } else if (visibleCount == 2) {
                        if (mToast == null) {
                            mToast = Toast.makeText(
                                    mContext,
                                    R.string.tab_remove_dialog_message, Toast.LENGTH_LONG);
                        } else {
                            mToast.setText(R.string.tab_remove_dialog_message);
                        }
                        mToast.show();
                    }
                }

                if (mVisiblityChanged.containsKey(tag)) {
                    mVisiblityChanged.remove(tag);
                } else {
                    mVisiblityChanged.put(tag, visible);
                }
                return true;
            }

            @Override
            public boolean isCNMode() {
                return HtcTabFragmentPagerAdapter.this.isCNMode();
            }
        };
    }

    //manage TabContainer's ID
    private int tabViewId = 0x40000001;
    private Map<String, Integer> tabContainerViewIds = new HashMap<String, Integer>();

    private int createTabContainerViewId(ViewGroup container) {
        while ((container.findViewById(tabViewId) != null)
                || (mFragmentManager.findFragmentById(tabViewId) != null)
                || tabContainerViewIds.containsValue(tabViewId)
                ) {
            tabViewId++;
        }
        return tabViewId;
    }

    private int getTabContainerViewId(ViewGroup container, String tag) {
        //if the fragment was added to activity, its container id will > 0
        Fragment f = mFragmentManager.findFragmentByTag(tag);
        if (f != null) {
            int containerId = f.getId();
            if (containerId > 0) {
                tabContainerViewIds.put(tag, containerId);
                return containerId;
            }
        }

        Integer id = tabContainerViewIds.get(tag);
        if (id == null) {
            tabContainerViewIds.put(tag, id = createTabContainerViewId(container));
        }
        return id;
    }

    /**
     *
     */
    public static class TabSpec {
        private String mTag;
        private String mTitle;
        private int mCount;
        private Drawable mBackground;
        private TabContainer mContainer;
        private boolean mRemovable = true;
        private boolean mVisible = true;

        /**
         * @param title
         */
        public TabSpec(String title) {
            mTitle = title;
        }

        private TabSpec setTag(String tag) {
            this.mTag = tag;
            return this;
        }

        private TabSpec setTitle(String title) {
            this.mTitle = title;
            return this;
        }

        private TabSpec setCount(int count) {
            this.mCount = count;
            return this;
        }

        /**
         * Set the background of tab
         * @param background drawable
         * @return self instance
         */
        public TabSpec setBackground(Drawable background) {
            this.mBackground = background;
            return this;
        }

        private TabSpec setContainer(TabContainer container) {
            this.mContainer = container;
            return this;
        }

        /**
         * @param removable
         */
        public TabSpec setRemovable(boolean removable) {
            this.mRemovable = removable;
            return this;
        }

        /**
         * @param visible
         */
        public TabSpec setVisible(boolean visible) {
            this.mVisible = visible;
            return this;
        }

        /**
         * @hide
         */
        @Override
        public String toString() {
            return "[tag=" + mTag + ", title=" + mTitle + "]";
        }
    }

    public class TabContainer extends FrameLayout {
        Parcelable mAbsListState = null ;
        public TabContainer(Context context) {
            super(context);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            TabBarUtils.trace.begin("TabContainer onMeasure");
            try {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            } finally{
                TabBarUtils.trace.end();
            }
        }
        @Override
        protected void onLayout(boolean changed, int left, int top, int right,
                int bottom) {
            TabBarUtils.trace.begin("TabContainer onLayout");
            try {
                super.onLayout(changed, left, top, right, bottom);
            } finally {
                TabBarUtils.trace.end();
            }
        }
        @Override
        public void draw(Canvas canvas) {
            TabBarUtils.trace.begin("TabContainer Draw");
            try {
                super.draw(canvas);
            } finally {
                TabBarUtils.trace.end();
            }
        }
        @Override
        public void requestLayout() {
            super.requestLayout();
            HtcViewPager pager = (HtcViewPager)getParent();
            if (!mInstantiatingItem && pager != null && pager.isScrolling() && getApplicationWindowToken() != null) {
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    pager.performaceWarning();
                    Log.w(TAG, "requestLayout(): DO NOT request layout when the pager is scrolling: " + this, new RuntimeException());
                } else {
                    Log.w(TAG, "requestLayout(): DO NOT request layout when the pager is scrolling: " + this);
                }
            }
        }
        @Override
        protected Parcelable onSaveInstanceState() {
            Bundle bundle = new Bundle();
            bundle.putParcelable("super", super.onSaveInstanceState());
            //if child contain AbsListview, manual handle AbsListView SaveSate
            AbsListView abslistview = findAbsListView(this);
            if(abslistview != null){
                Parcelable state = abslistview.onSaveInstanceState();
                if(state != null){
                    int containerId = getId();
                    bundle.putParcelable("container:"+containerId, state);
                }
            }
            return bundle;
        }
        @Override
        protected void onRestoreInstanceState(Parcelable state) {
            Bundle bundle = (Bundle) state;
            if(bundle != null){
                AbsListView abslistview = findAbsListView(this);
                if(abslistview != null){
                //if child contain AbsListview, manual handle AbsListView RestoreState
                    mAbsListState = bundle.getParcelable("container:"+getId());
                }
                super.onRestoreInstanceState(bundle.getParcelable("super"));
            }else{
                super.onRestoreInstanceState(state);
            }
        }
        private AbsListView findAbsListView(View v){
            if(v instanceof ViewGroup){
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    return vg.getChildAt(i) instanceof AbsListView ?
                            (AbsListView) vg.getChildAt(i):findAbsListView(vg.getChildAt(i));
                }
                return null;
            }else{
                return null;
            }
        }
    }
    private static class PrefData {
        int order;
        int visible = 0;
    }
}
