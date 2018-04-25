package com.htc.lib1.cc.view.tabbar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.view.tabbar.TabBar.TabAdapter;
import com.htc.lib1.cc.widget.HtcCheckBox;
import com.htc.lib1.cc.widget.HtcFooter;
import com.htc.lib1.cc.widget.HtcFooterTextButton;
import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcOverlapLayout;
import com.htc.lib1.cc.widget.HtcReorderListView;

/**
 * Use for tab reorder
 *
 */
public class TabReorderFragment extends Fragment {
    private TabReorderAdapterImpl mTabListAdapter = null;
    private TabReorderAdapter mTabReorderAdapter = null;
    private HtcReorderListView mTabList = null;
    private TextView mHeader = null;
    private Context mContext;

    private PopupWindow mPop;

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);
        onExit(false, true);
    }

    /**
     * Use for tab reorder
     */
    public interface TabReorderAdapter extends TabAdapter {
        public boolean isVisible(int position);
        public boolean isRemoveable(int position);
        public void onMove(int from, int to);
        public void onExit(boolean save);
        public boolean onVisibilityChanged(int position, boolean visible);
    }

    /**
     * Set a adapter for reorder list view in this fragment.
     * @param adapter
     */
    public void setAdapter(TabReorderAdapter adapter) {
        //We assign mContext in onCreateView, if call setAdapter() before onCreateView() the assignment will be delay to onCreateView()
        if (mContext != null) {
            mTabReorderAdapter = null;
            setAdapter(new TabReorderAdapterImpl(mContext, adapter));
            int headerHeight = TabBarUtils.dimen.headerHeight(mContext,mTabListAdapter.isAutomotiveMode());
            mHeader.setLayoutParams(new HtcOverlapLayout.LayoutParams(HtcOverlapLayout.LayoutParams.MATCH_PARENT, headerHeight));
        } else {
            mTabReorderAdapter = adapter;
        }
    }

    private DataSetObserver mAdapterObserver = new DataSetObserver() {
        public void onChanged() {
            mTabList.setAdapter(mTabListAdapter);
        }
        public void onInvalidated() {
            mTabList.setAdapter(mTabListAdapter);
        }
    };

    private void setAdapter(TabReorderAdapterImpl adapter) {
        if (mTabListAdapter != null) {
            mTabListAdapter.unregisterDataSetObserver(mAdapterObserver);
        }
        mTabListAdapter = adapter;
        mTabListAdapter.registerDataSetObserver(mAdapterObserver);
        mTabList.setAdapter(mTabListAdapter);
        mTabList.setSelector((mTabListAdapter != null && mTabListAdapter.isAutomotiveMode()) ? R.drawable.list_selector_dark : R.drawable.list_selector_light);
    }

    private class Listener implements HtcReorderListView.DropListener {
        @Override
        public void drop(int from, int to) {
            if(from != to) {
                mTabListAdapter.onMove(from, to);
                mTabList.invalidate();
            }
        }
    }

    /**
     * {@inheritDoc}
     * @hide
     * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        Context ctx = inflater.getContext();
        int headerPadding = TabBarUtils.dimen.m1(ctx);

        if(mTabList == null) {
            Listener listener = new Listener();
            mTabList = new HtcReorderListView(ctx, null);
            mTabList.setDivider(ctx.getResources().getDrawable(R.drawable.inset_list_divider));
            mTabList.setDropListener(listener);
            mTabList.setAllItemFocusable(false);
            mTabList.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    HtcListItem item = (HtcListItem)view;
                    RelativeLayout root = (RelativeLayout)item.getChildAt(0);
                    HtcCheckBox cb = (HtcCheckBox)root.getChildAt(0);
                    if (cb.isEnabled()) {
                        cb.performClick();
                    }
                }
            });
            mTabList.setDraggerId(android.R.id.edit);

            mHeader = new TextView(ctx) {
                @Override
                public boolean dispatchTouchEvent(MotionEvent event) {
                    //20130702 garywu, prevent to dispatch touch event to the view in back of is
                    return true;
                }
            };
            mHeader.setBackground(TabBarUtils.drawable.headerBackground(ctx));
            mHeader.setPadding(headerPadding, 0, headerPadding, 0);
            mHeader.setGravity(Gravity.CENTER_VERTICAL);
            mHeader.setTextAppearance(ctx, R.style.fixed_title_primary_m);
            mHeader.setText(R.string.carousel_title);
            //naeco: fadding edge
            mHeader.setSingleLine(true);
            mHeader.setHorizontalFadingEdgeEnabled(true);
            mHeader.setEllipsize(TruncateAt.MARQUEE);
            mHeader.setMarqueeRepeatLimit(0);
        }

        if (mContext == null) {
            mContext = ctx;
            //If there is an assignment before onCreateView(), it will be finished here.
            if (mTabReorderAdapter != null) {
                setAdapter(new TabReorderAdapterImpl(mContext, mTabReorderAdapter));
                int headerHeight = TabBarUtils.dimen.headerHeight(ctx,mTabListAdapter.isAutomotiveMode());
                mHeader.setLayoutParams(new HtcOverlapLayout.LayoutParams(HtcOverlapLayout.LayoutParams.MATCH_PARENT, headerHeight));
                mTabReorderAdapter = null;
            }
        }

        if(mTabListAdapter == null) {
            throw new RuntimeException("Please call setAdapter(TabReorderAdapter) before onCreateView()");
        }

        HtcFooterTextButton cancel = new HtcFooterTextButton(ctx);
        cancel.setText(android.R.string.cancel);
        cancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                onExit(false, true);
            }
        });
        setButtonDescription(cancel, android.R.string.cancel);

        HtcFooterTextButton save = new HtcFooterTextButton(ctx);
        save.setText(R.string.done);
        save.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                onExit(true, true);
            }
        });
        setButtonDescription(save, android.R.string.ok);

        HtcFooter footer = new HtcFooter(ctx);
        footer.addView(cancel);
        footer.addView(save);
        footer.ReverseLandScapeSequence(true);

        HtcOverlapLayout overlap = new HtcOverlapLayout(ctx);
        overlap.isActionBarVisible(false);
        overlap.setFocusable(true);
        overlap.setFocusableInTouchMode(true);
        //20130625 garywu, enhance focus transition support
        overlap.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        overlap.setBackgroundColor(TabBarUtils.color.backgroundLight(ctx));
        overlap.addView(mTabList, android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        overlap.addView(footer);

        LinearLayout ll = new LinearLayout(ctx);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(mHeader);
        ll.addView(overlap);

        mPop = new PopupWindow(ll, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        mPop.setFocusable(true);
        mPop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPop.setAnimationStyle(R.style.AnimationTabReorder);
        mPop.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                onExit(false, true);
            }
        });
        mPop.showAtLocation(container.getRootView(), Gravity.LEFT|Gravity.TOP, 0, 0);

        if (mHeader != null) {
        mHeader.setSystemUiVisibility(mHeader.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_FULLSCREEN);
        if ( Build.VERSION.SDK_INT > Build.VERSION_CODES.M && false == getActivity().isInMultiWindowMode() ) {
            mHeader.setSystemUiVisibility(mHeader.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
         }
     }

        return null;
    }

    private void setButtonDescription(View view, int resourceId){
        String s = getResources().getString(resourceId);
        if ( null == s ) {
            Log.i("TabReorderFragment", "string of " + resourceId + " is null");
        } else {
            view.setContentDescription(s);
        }
    }

    private boolean isExiting = false;
    /**
     * @hide
     * @deprecated [Module internal use]
     */
    protected void onExit(boolean save, boolean withAnimation) {
        if (isExiting) {
            return;
        }
        isExiting = true;

        mTabListAdapter.onExit(save);

        onDismiss();
    }
    /**
     * @hide
     * @deprecated [Module internal use]
     */
    protected void onDismiss() {
        if (mPop != null && mPop.isShowing()) {
            mPop.dismiss();
        }
    }

    private int[] mSiblingFocusability = null;
    private boolean[] mSiblingFocusable = null;
    /**
     * {@inheritDoc}
     * @hide
     * @see android.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        isExiting = false;
        super.onResume();

        // for rosie or other ap without actionbar
//        if(getActivity().getActionBar() != null) {
////FRAMEWORK INTERNAL API
////            ((ActionBarImpl) getActivity().getActionBar()).setShowHideAnimationEnabled(false);
//            getActivity().getActionBar().hide();
//        }

//        getView().requestFocus();
//        //20130704 garywu, set other views as invisible to prevent wrong focus transition
//        View view = getView();
//        ViewGroup parent = (ViewGroup)view.getParent();
//        mSiblingFocusability = new int[parent.getChildCount()];
//        mSiblingFocusable = new boolean[mSiblingFocusability.length];
//        for (int i = 0, n = mSiblingFocusability.length; i < n; i++) {
//            View c = parent.getChildAt(i);
//            if (c != view) {
//                if (c instanceof ViewGroup) {
//                    ViewGroup g = (ViewGroup) c;
//                    mSiblingFocusability[i] = g.getDescendantFocusability();
//                    mSiblingFocusable[i] = g.isFocusable();
//                    g.setFocusable(false);
//                    g.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
//                }
//            }
//        }
    }

    /**
     * {@inheritDoc}
     * @hide
     * @see android.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
//        //20130704 garywu, set other views as invisible to prevent wrong focus transition
//        View view = getView();
//        if (mSiblingFocusability != null) {
//            ViewGroup parent = (ViewGroup)view.getParent();
//            for (int i = 0, n = mSiblingFocusability.length; i < n; i++) {
//                View c = parent.getChildAt(i);
//                if (c != view) {
//                    if (c instanceof ViewGroup) {
//                        ViewGroup g = (ViewGroup) c;
//                        g.setFocusable(mSiblingFocusable[i]);
//                        g.setDescendantFocusability(mSiblingFocusability[i]);
//                    }
//                }
//            }
//            mSiblingFocusability = null;
//            mSiblingFocusable = null;
//        }

        super.onPause();

        onExit(false, false);
        // for rosie or other ap without actionbar
//        if(getActivity().getActionBar() != null) {
//            getActivity().getActionBar().show();
////FRAMEWORK INTERNAL API
////            ((ActionBarImpl) getActivity().getActionBar()).setShowHideAnimationEnabled(true);
//        }
    }

//    /**
//     * {@inheritDoc}
//     * @hide
//     * @see android.app.Fragment#onCreateAnimator(int, boolean, int)
//     */
//    @Override
//    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
//        Animator set;
//        float offy = TabBarUtils.dimen.screenHeight(getActivity()) - TabBarUtils.dimen.headerHeight(getActivity());
//        if(enter) {
//            set = ObjectAnimator.ofFloat(this, "translationY", offy, 0f).setDuration(300);
//            set.setInterpolator(new DecelerateInterpolator(1.5f));
//        } else {
//            set = ObjectAnimator.ofFloat(this, "translationY", 0f, offy).setDuration(300);
//            set.setInterpolator(new AccelerateInterpolator(1.5f));
//        }
//        return set;
//    }

    /**
     * @hide
     * @deprecated [Module internal use]
     */
    public static class TabReorderAdapterImpl implements ListAdapter, TabReorderAdapter {
        private TabReorderAdapter mAdapter;
        private TabReorderBuilder mViewGenerator;
        public TabReorderAdapterImpl(Context context, TabReorderAdapter adapter) {
            mAdapter = adapter;
            mViewGenerator = new TabReorderBuilder(context, this);
        }

        @Override
        public int getCount() {
            return mAdapter.getCount();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public int getItemViewType(int arg0) {
            return 0;
        }

        @Override
        public View getView (int position, View convertView, ViewGroup parent) {
            return mViewGenerator.getView(position, convertView, parent);
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return getCount() == 0;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int arg0) {
            return true;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            mAdapter.registerDataSetObserver(observer);
        }

        @Override
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
        }

        @Override
        public void onExit(boolean save) {
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
    }
}
