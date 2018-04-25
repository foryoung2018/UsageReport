package com.htc.lib1.cc.widget;

import java.lang.ref.WeakReference;
import android.util.Log;
import android.view.animation.Animation.AnimationListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import com.htc.lib1.cc.widget.ExpandableListPopupBubbleWindow;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import com.htc.lib1.cc.widget.ListPopupBubbleWindow;
import com.htc.lib1.cc.widget.PopupBubbleWindow;
import com.htc.lib1.cc.R;

/**
 * <p>
 * HtcPopupWindowWrapper wrap ListPopupBubbleWindow and ExpandableListPopupBubbleWindow to show It Only use to show ListPopupBubbleWindow(or
 * ExpandableListPopupBubbleWindow) in a easy way. In order to show ListPopupBubbleWindow(or ExpandableListPopupBubbleWindow), you need to
 * call <b>setArchorView(view); </b> to setup the View that popupwindow will archor <b>setAdapter(adapter); </b> to setup the content that
 * popupwindow will show and call <b>showPopupWindow()</b> to show the popup window.
 * </p>
 *
 * The following is the example:
 *
 * <pre>
 *     &lt;com.htc.HtcFooter android:layout_height="wrap_content" android:layout_width="match_parent" android:id="@+id/footer" "&gt;
 *     &lt;com.htc.HtcFooterButton android:text="XML Button 0" android:id="@+id/button1"/&gt;
 *     &lt;com.htc.HtcFooterButton                             android:id="@+id/button2" htcfooter:footerButtonImage="@drawable/icon"/&gt;
 *     &lt;com.htc.HtcFooterButton android:text="XML Button 2" android:id="@+id/button3" htcfooter:footerButtonImage="@drawable/icon"/&gt;
 *     &lt;/com.htc.HtcFooter&gt;
 *
 * </pre>
 *
 *
 * @author Felka Chang
 * @version %I% %G%
 *
 */

public class HtcPopupWindowWrapper implements View.OnKeyListener, ViewTreeObserver.OnGlobalLayoutListener, PopupBubbleWindow.OnDismissListener, AdapterView.OnItemClickListener {

    private WeakReference<ListAdapter> mAdapter;
    private ListPopupBubbleWindow mPopup;
    private OnItemClickListener mItemClickListener;
    private View mArchorView;
    private View mTaggleView;

    private OnChildClickListener clickListenerChd = null;
    private OnGroupClickListener clickListenerGrp = null;

    private AnimationListener mAnimationListener = null;

    private int mCustomizeTriangleOffset;

    private ShareViaOnItemClickListener mShareViaOnItemClickListener;

    //expandable list view bubble window usage
    private ExpandableListPopupBubbleWindow mPopupBubbleExp = null;
    private WeakReference<ExpandableListAdapter> mPopupAdapterExp = null;
    private PopupBubbleWindow.OnDismissListener mDismissListener = null;
    private OnTouchListener mCustomizedTouchInterceptor;
    private int mExpandDirection;

    /**
     * Simple constructor to use when creating a HtcPopupWindowWrapper from code.
     */
    public HtcPopupWindowWrapper() {
        this(null, null);
    }

    /**
     * Simple constructor to use when creating a HtcPopupWindowWrapper from code.
     *
     * @param view
     *            the popup's content
     * @param adapter
     *            The adapter currently used to display data in this popup window.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcPopupWindowWrapper(View view, ListAdapter adapter) {
        setArchorView(view);
        setAdapter(adapter);
    }

    /**
     * Set the anchor view to pin the popup window
     *
     * @param view
     *            the target view that PopupWindow display according to
     */
    public void setArchorView(View view) {
        mArchorView = view;
    }

    /**
     * Get the anchor view on which to pin the popup window
     *
     * @return the view view that PopupWindow display according to
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public View getArchorView() {
        return mArchorView;
    }

    /**
     * When PopoupWindow is showing, the background of taggleView will be changed. When PopupWindow is dismiss, the background of taggleView
     * will be changed back. If not set TaggleView, the background of ArchorView will be the substitution.
     *
     * @param view
     *            the view that will be changed background, default is ArchorView
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void setTaggleView(View view) {
        mTaggleView = view;
    }

    /**
     * Get the taggle view.
     *
     * @return the object that application want to keep or pass to
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public View getTaggleView() {
        return mTaggleView;
    }

    /**
     * Sets the adapter that provides the data and the views to represent the data in this popup window.
     *
     * @param adapter
     *            The adapter to use to create this window's content.
     */
    //set the bubble list view adapter
    public void setAdapter(ListAdapter adapter) {
        mAdapter = null;
        mAdapter = (adapter != null) ? new WeakReference<ListAdapter>(adapter) : null;

        if (mPopup != null && adapter == null)
            mPopup.setAdapter(adapter);

        //clear another expandable bubble and adapter
        if (mPopupAdapterExp != null || mPopupBubbleExp != null) {
            if (mPopupBubbleExp != null) {
                mPopupBubbleExp.dismiss();
                mPopupBubbleExp.setAdapter(null);
            }

            mPopupBubbleExp = null;
            mPopupAdapterExp = null;
        }
    }

    /**
     * set the bubble expandable list view adapter
     *
     * @param adapter
     *            An adapter that links a {@link ExpandableListView} with the underlying data. The implementation of this interface will
     *            provide access to the data of the children (categorized by groups), and also instantiate {@link View}s for children and
     *            groups.
     */
    public void setAdapter(ExpandableListAdapter adapter) {
        mPopupAdapterExp = null;
        mPopupAdapterExp = adapter != null ? new WeakReference<ExpandableListAdapter>(adapter) : null;

        if (mPopupBubbleExp != null && adapter == null)
            mPopupBubbleExp.setAdapter(adapter);

        //clear another bubble and adapter
        if (mAdapter != null || mPopup != null) {
            if (mPopup != null) {
                mPopup.dismiss();
                mPopup.setAdapter(null);
            }

            mPopup = null;
            mAdapter = null;
        }
    }

    /**
     * Set the triangle offset.
     *
     * @return Returns triangle offset.
     */
    void setTriangleOffset(int offset) {
        mCustomizeTriangleOffset = offset;
    }

    /**
     * Get the triangle offset.
     *
     * @return Returns triangle offset.
     */
    int getTriangleOffset() {
        return mCustomizeTriangleOffset;
    }

    /* @hide */
    /**
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void showPopupWindow(boolean isWrapper) {
        if (!tryShow(isWrapper)) {
            throw new IllegalStateException("MenuPopupHelper cannot be used without an anchor");
        }
    }

    public void showPopupWindow() {
        showPopupWindow(true);
    }

    /**
     * Popup window will be show at indicate direction: one of {@link PopupBubbleWindow#EXPAND_UP}, {@link PopupBubbleWindow#EXPAND_DOWN},
     * {@link PopupBubbleWindow#EXPAND_LEFT} , or {@link PopupBubbleWindow#EXPAND_RIGHT}.
     *
     * <p>
     * If the popup is showing, calling this method will take effect only the next time.
     *
     * @param direction
     *            Set direction which window want to expand
     * @see PopupBubbleWindow#getExpandDirection()
     */
    public void setExpandDirection(int direction) {
        mExpandDirection = direction;
    }

    private int mExpandGroupPos = -1;

    /**
     * Expand a group in the grouped list view
     *
     * @param groupPos
     *            the group to be expanded
     */
    public void setExpandGroup(int groupPos) {
        mExpandGroupPos = groupPos;
    }

    /**
     * Return the current expand direction in {@link PopupBubbleWindow#setExpandDirection(int)}.
     *
     * @return the current expand direction.
     *
     * @see #setExpandDirection(int)
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public int getExpandDirection() {
        if (mPopup != null) {
            mExpandDirection = mPopup.getExpandDirection();
        } else if (mPopupBubbleExp != null) {
            mExpandDirection = mPopupBubbleExp.getExpandDirection();
        }
        return mExpandDirection;
    }

    private boolean setOnGlobalLayoutListener(boolean bEnable) {
        View v = getArchorView();
        if (null == v)
            return false;

        final ViewTreeObserver vto;
        vto = v.getViewTreeObserver();

        if (null == vto)
            return false;

        if (bEnable) {
            vto.addOnGlobalLayoutListener(this);
        } else {
            vto.removeGlobalOnLayoutListener(this);
        }

        return true;
    }

    /**
     * <p>
     * Binds an animation listener to this animation. The animation listener is notified of animation events such as the end of the
     * animation or the repetition of the animation.
     * </p>
     *
     * @param listener
     *            the animation listener to be notified
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void setAnimationListener(AnimationListener listener) {
        mAnimationListener = listener;
    }

    private boolean setArchorViewBackground(boolean bPressed) {
        View v = getTaggleView();
        if (null == v) {
            v = getArchorView();
        }
        if (null == v)
            return false;

        int paddingLeft = v.getPaddingLeft();
        int paddingRight = v.getPaddingRight();

        v.setPressed(bPressed);
        //change the background to press state
        /*
         * if ( bPressed ) { v.setBackgroundResource( HtcSkinUtil.getDrawableResIdentifier(v.getContext(), "common_titlebar_btn_press",
         * R.drawable.common_titlebar_btn_press)); } else { v.setBackgroundResource(
         * HtcSkinUtil.getDrawableResIdentifier(v.getContext(), "common_titlebar_btn",R.drawable.common_titlebar_btn)); }
         */
        v.setPadding(paddingLeft, 0, paddingRight, 0);

        return true;
    }

    private boolean setPopupShow(boolean bShow) {
        if (null != mPopup) {
            if (false == bShow && mPopup.isShowing())
                mPopup.dismiss();
            if (mShareViaOnItemClickListener != null) {
                mShareViaOnItemClickListener.shrinkAdapter();
            }
            return true;
        }
        return false;
    }

    private boolean setPopupExpShow(boolean bShow) {
        if (null != mPopupBubbleExp) {
            if (false == bShow && mPopupBubbleExp.isShowing())
                mPopupBubbleExp.dismiss();
            return true;
        }
        return false;
    }

    /**
     * Set a callback for all touch events being dispatched to the popup window.
     *
     * @param l
     *            a callback for all touch events being dispatched to the popup window.
     */
    public void setTouchInterceptor(OnTouchListener l) {
        mCustomizedTouchInterceptor = l;

        if (mCustomizedTouchInterceptor != null) {
            if (mPopup != null)
                mPopup.setTouchInterceptor(mCustomizedTouchInterceptor);
            if (mPopupBubbleExp != null)
                mPopupBubbleExp.setTouchInterceptor(mCustomizedTouchInterceptor);

        }

    }

    private ListPopupBubbleWindow getPopupWindow() {
        View v = getArchorView();
        if (null == v)
            return null;

        if (null == mPopup) {
            mPopup = new ListPopupBubbleWindow(v.getContext());
        }
        return mPopup;
    }

    private ExpandableListPopupBubbleWindow getPopupExpWindow() {
        View v = getArchorView();
        if (null == v)
            return null;

        if (null == mPopupBubbleExp) {
            mPopupBubbleExp = new ExpandableListPopupBubbleWindow(v.getContext());
        }
        return mPopupBubbleExp;
    }

    interface PopupWindowWrapperListener {
        void onShow_2();
    }

    public interface HtcPopupMaxContentWidthListener {

        /**
         * This method is called to notify max content width before showing
         */
        public void updateMaxContentWidth(int maxContentWidth);

    }

    interface OnArchorInfoListener {
        int getVerticalOffset();

    }

    private View.OnKeyListener mOnKeyListener = null;
    private PopupWindowWrapperListener mListener = null;
    private HtcPopupMaxContentWidthListener mMaxWidthListener = null;
    private OnArchorInfoListener oaiListener = null;
    private View mFooterView = null;
    private View mHeaderView = null;
    private int mDropDownListPosition = 0;
    private boolean mIsWidthHeightFixed;
    private int mWidth;
    private int mHeight;

    /**
     * @hide
     * @param listener
     */
    public void setOnKeyListener(View.OnKeyListener listener) {
        mOnKeyListener = listener;
    }

    /**
     * @hide
     * @param listener
     */
    public void setPopupWindowWrapperListener(PopupWindowWrapperListener listener) {
        mListener = listener;
    }

    /**
     * To get max content width before showing
     *
     * @param listener
     */
    public void setHtcPopupMaxContentWidthListener(HtcPopupMaxContentWidthListener listener) {
        mMaxWidthListener = listener;
    }

    /**
     * @hide
     * @param listener
     */
    public void setOnArchorInfoListener(OnArchorInfoListener listener) {
        oaiListener = listener;
    }

    /**
     * @hide
     * @param headerView
     */
    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
    }

    /**
     * @hide
     * @param footerView
     */
    public void setFooterView(View footerView) {
        mFooterView = footerView;
    }

    /**
     * @hide
     * @param dropDownListPosition
     */
    public void setDropDownListPosition(int dropDownListPosition) {
        mDropDownListPosition = dropDownListPosition;
    }

    /**
     * @hide
     * @param isWidthHeightFixed
     * @param width
     * @param height
     */
    public void setFixedListItemDimen(boolean isWidthHeightFixed, int width, int height) {
        mIsWidthHeightFixed = isWidthHeightFixed;
        mWidth = width;
        mHeight = height;
    }

    /**
     * @hide
     */
    private boolean showPopup(boolean isInWrapper) {
        //dismiss list view bubble window
        setPopupShow(false);

        ListAdapter adapter;

        //handle list view bubble window
        adapter = mAdapter != null ? mAdapter.get() : null;
        View v = getArchorView();
        if (null == v)
            return false;

        if (null != adapter || mShareViaOnItemClickListener != null) {
            setOnGlobalLayoutListener(true);
            ListPopupBubbleWindow lpbw = getPopupWindow();
            if (null != lpbw) {
                //setup the maximum width
                int maxContentWidth = lpbw.getMaxContentWidth();

                //for footer bar width limit
                int minFooterContentWidth = lpbw.getMinFooterContentWidth();

                /**
                 * ListView limitation: addHeaderView must before set adapter move code to "before set adapter. call addheaderView after
                 * call setAdapter will cause exception.
                 **/
                if (mHeaderView != null)
                    lpbw.addHeaderView(mHeaderView);
                if (mFooterView != null)
                    lpbw.addFooterView(mFooterView);

                if (mShareViaOnItemClickListener != null) {
                    lpbw.setOnItemClickListener(mShareViaOnItemClickListener);
                    lpbw.setAdapter(mShareViaOnItemClickListener.getAdapter());
                    if (mMaxWidthListener != null)
                        mMaxWidthListener.updateMaxContentWidth(maxContentWidth);
                    if (mExpandDirection == PopupBubbleWindow.EXPAND_LEFT)
                        lpbw.setContentWidth(Math.max(minFooterContentWidth, Math.min(measureContentWidth(mShareViaOnItemClickListener.getAdapter()), maxContentWidth)));
                    else
                        lpbw.setContentWidth(Math.min(measureContentWidth(mShareViaOnItemClickListener.getAdapter()), maxContentWidth));
                } else {
                    lpbw.setOnItemClickListener(mItemClickListener);
                    lpbw.setAdapter(adapter);
                    if (mMaxWidthListener != null)
                        mMaxWidthListener.updateMaxContentWidth(maxContentWidth);
                    if (mExpandDirection == PopupBubbleWindow.EXPAND_LEFT)
                        lpbw.setContentWidth(Math.max(minFooterContentWidth, Math.min(measureContentWidth(adapter), maxContentWidth)));
                    else
                        lpbw.setContentWidth(Math.min(measureContentWidth(adapter), maxContentWidth));
                }
                if (oaiListener != null) {
                    lpbw.setVerticalOffset(oaiListener.getVerticalOffset());
                    if (mIsWidthHeightFixed)
                        lpbw.setFixedListItemDimension(mIsWidthHeightFixed, mWidth, mHeight);
                }
                if (mAnimationListener != null) {
                    lpbw.setAnimationListener(mAnimationListener);
                }
                lpbw.setAnchorView(v);
                lpbw.setOnDismissListener(this);
                lpbw.setModal(true);

                //setup the input mode
                if (isInWrapper) {
                    lpbw.setExpandDirection(mExpandDirection);///use parameter
                    lpbw.setTriangleOffset(mCustomizeTriangleOffset);///use parameter
                    setArchorViewBackground(true);///use parameter
                }
                lpbw.setInputMethodMode(PopupBubbleWindow.INPUT_METHOD_NOT_NEEDED);
                lpbw.show();
                lpbw.getListView().setOnKeyListener((null == mOnKeyListener) ? this : mOnKeyListener);
                if (mCustomizedTouchInterceptor != null)
                    lpbw.setTouchInterceptor(mCustomizedTouchInterceptor);

                if (mDropDownListPosition != 0 && mDropDownListPosition > 0) {
                    lpbw.getListView().setSelection(mDropDownListPosition);
                }
                if (mListener != null)
                    mListener.onShow_2();

            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * @hide
     *
     */
    private boolean showPopupExp(boolean isInWrapper) {
        //dismiss expandable list view bubble window
        setPopupExpShow(false);

        ExpandableListAdapter adapterExp;

        //handle expandable list view bubble window
        adapterExp = mPopupAdapterExp != null ? mPopupAdapterExp.get() : null;
        View v = getArchorView();

        if (null == v)
            return false;

        if (null != adapterExp) {
            setOnGlobalLayoutListener(true);

            ExpandableListPopupBubbleWindow elpbw = getPopupExpWindow();
            if (null != elpbw) {
                elpbw.setOnChildClickListener(clickListenerChd);
                elpbw.setOnGroupClickListener(clickListenerGrp);
                elpbw.setOnItemClickListener(mItemClickListener);

                if (mAnimationListener != null) {
                    elpbw.setAnimationListener(mAnimationListener);
                }
                elpbw.setAnchorView(v);
                elpbw.setOnDismissListener(this);
                elpbw.setAdapter(adapterExp);
                elpbw.setModal(true);
                if (mExpandGroupPos > -1 && mExpandGroupPos < adapterExp.getGroupCount())
                    elpbw.setExpandGroup(mExpandGroupPos);

                //setup the maximum width and input mode
                int maxContentWidth = elpbw.getMaxContentWidth();
                if (mMaxWidthListener != null)
                    mMaxWidthListener.updateMaxContentWidth(maxContentWidth);
                elpbw.setContentWidth(Math.min(measureContentWidth(adapterExp), maxContentWidth));
                if (oaiListener != null) {
                    elpbw.setVerticalOffset(oaiListener.getVerticalOffset());
                    if (mIsWidthHeightFixed)
                        elpbw.setFixedListItemDimension(mIsWidthHeightFixed, mWidth, mHeight);
                }
                elpbw.setInputMethodMode(PopupBubbleWindow.INPUT_METHOD_NOT_NEEDED);
                if (isInWrapper) {
                    elpbw.setTriangleOffset(mCustomizeTriangleOffset);///use parameters
                    elpbw.setExpandDirection(mExpandDirection);///use parameters
                    setArchorViewBackground(true);///use parameters
                }
                elpbw.show();
                elpbw.getExpandableListView().setOnKeyListener((null == mOnKeyListener) ? this : mOnKeyListener);
                if (mCustomizedTouchInterceptor != null)
                    elpbw.setTouchInterceptor(mCustomizedTouchInterceptor);

            }
            if (mListener != null)
                mListener.onShow_2();
        }
        return true;
    }

    /**
     * @hide
     */
    public HtcListView getPopupListView() {
        if (mPopup != null)
            return mPopup.getListView();
        else {
            Log.d("HtcPopupWindowWrapper", "need to new instance before getPopupListView() , the usage of application is invalidated");
            return null;
        }
    }

    /**
     * Use to get expandablelistview.
     *
     * @hide
     */
    public ExpandableListView getPopupExpandableListView() {
        if (mPopupBubbleExp != null)
            return mPopupBubbleExp.getExpandableListView();
        else {
            Log.d("HtcPopupWindowWrapper", "need to new instance before getPopupExpandableListView() , the usage of application is invalidated");
            return null;
        }
    }

    private boolean tryShow(boolean isWrapper) {
        boolean bShowPopup = showPopup(isWrapper);
        boolean bShowPopupExp = showPopupExp(isWrapper);

        return (bShowPopup || bShowPopupExp) ? true : false;
    }

    /**
     * Set the listener to be called when this popup window is dismissed.
     *
     * @param listener
     *            callback to be invoked when this popup window is dismissed.
     */
    public void setOnDismissListener(PopupBubbleWindow.OnDismissListener listener) {
        mDismissListener = listener;
    }

    /**
     * Called when this popup window is dismissed.
     *
     * @deprecated [Module internal use]
     */
    /** @hide */
    public void onDismiss() {
        if (mPopup != null)
            mPopup.setAdapter(null);
        mPopup = null;

        if (mShareViaOnItemClickListener != null)
            mShareViaOnItemClickListener.shrinkAdapter();

        setOnGlobalLayoutListener(false);
        setArchorViewBackground(false);

        if (mPopupBubbleExp != null)
            mPopupBubbleExp.setAdapter(null);
        mPopupBubbleExp = null;

        if (null != mDismissListener)
            mDismissListener.onDismiss();
    }

    /**
     * <p>
     * Dispose of the popup window. This method can be invoked only after {@link #showPopupWindow()} has been executed. Failing that,
     * calling this method will have no effect.
     * </p>
     *
     * @see #showPopupWindow()
     */
    public void dismiss() {
        setOnGlobalLayoutListener(false);

        setPopupShow(false);
        setPopupExpShow(false);
    }

    /**
     * <p>
     * Dispose of the popup window without animation. This method can be invoked only after {@link #showPopupWindow()} has been executed.
     * Failing that, calling this method will have no effect.
     * </p>
     *
     * @see #showPopupWindow()
     */
    public void dismissWithoutAnimation() {
        setOnGlobalLayoutListener(false);

        if (mPopup != null) {
            if (mPopup.isShowing()) {
                mPopup.dismissWithoutAnimation();
            }
        }

        if (null != mPopupBubbleExp) {
            if (mPopupBubbleExp.isShowing()) {
                mPopupBubbleExp.dismissWithoutAnimation();
            }
        }
    }

    /**
     * <p>
     * Indicate whether this list-popup-window is showing on screen.
     * </p>
     *
     * @return true if the list-popup-window is showing, false otherwise
     */
    public boolean isPopupShowing() {
        return (null != mPopup && mPopup.isShowing());
    }

    /**
     * <p>
     * Indicate whether this expandable-list-popup-window is showing on screen.
     * </p>
     *
     * @return true if the expandable-list-popup-window is showing, false otherwise
     */
    public boolean isPopupExpShowing() {
        return (null != mPopupBubbleExp && mPopupBubbleExp.isShowing());
    }

    /**
     * Callback method to be invoked when the global layout state or the visibility of views within the view tree changes
     *
     * @deprecated [Module internal use]
     */
    /** @hide */
    public void onGlobalLayout() {
        final View v = getArchorView();

        if (null == v)
            return;

        if (!(isPopupShowing() || isPopupExpShowing())) {
            setOnGlobalLayoutListener(false);
        } else {
            if (null != v && !v.isShown()) {
                dismiss();
            } else {
                if (mPopup != null) {
                    if (oaiListener != null) {
                        mPopup.setVerticalOffset(oaiListener.getVerticalOffset());
                    }
                    mPopup.show();
                }

                if (mPopupBubbleExp != null) {
                    if (oaiListener != null) {
                        mPopupBubbleExp.setVerticalOffset(oaiListener.getVerticalOffset());
                    }
                    mPopupBubbleExp.show();
                }
            }
        }
    }

    /**
     * Called when a hardware key is dispatched to a view. This allows listeners to get a chance to respond before the target view.
     * <p>
     * Key presses in software keyboards will generally NOT trigger this method, although some may elect to do so in some situations. Do not
     * assume a software input method has to be key-based; even if it is, it may use key presses in a different way than you expect, so
     * there is no way to reliably catch soft input key presses.
     *
     * @param v
     *            The view the key has been dispatched to.
     * @param keyCode
     *            The code for the physical key that was pressed
     * @param event
     *            The KeyEvent object containing full information about the event.
     * @return True if the listener has consumed the event, false otherwise.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

    /**
     * Set the callback to be invoked when an item in this AdapterView has been clicked.
     *
     * @param listener
     *            callback to be invoked when an item in this AdapterView has been clicked.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;

        if (mPopup != null)
            mPopup.setOnItemClickListener(listener);

        if (mPopupBubbleExp != null)
            mPopupBubbleExp.setOnItemClickListener(listener);
    }

    /**
     * Set the callback to be invoked when an ShareVia item in this AdapterView has been clicked.
     *
     * @param shareViaClickListener
     *            callback to be invoked when an item in this AdapterView has been clicked.
     */
    public void setOnItemClickListener(ShareViaOnItemClickListener shareViaClickListener) {
        mShareViaOnItemClickListener = shareViaClickListener;
    }

    /**
     * set the callback to be invoked when a child in this expandable list has been clicked.
     *
     * @param listener
     *            callback method to be invoked when a child in this expandable list has been clicked.
     */
    public void setOnChildClickListener(OnChildClickListener listener) {
        clickListenerChd = listener;

        if (mPopupBubbleExp != null)
            mPopupBubbleExp.setOnChildClickListener(listener);
    }

    /**
     * Set the callback to be invoked when the global layout state or the visibility of views within the view tree changes.
     *
     * @param listener
     *            callback to be invoked when the global layout state or the visibility of views within the view tree changes
     */
    public void setOnGroupClickListener(OnGroupClickListener listener) {
        clickListenerGrp = listener;

        if (mPopupBubbleExp != null)
            mPopupBubbleExp.setOnGroupClickListener(listener);
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has been clicked.
     * <p>
     * Implementers can call getItemAtPosition(position) if they need to access the data associated with the selected item.
     *
     * @param parent
     *            The AdapterView where the click happened.
     * @param view
     *            The view within the AdapterView that was clicked (this will be a view provided by the adapter)
     * @param position
     *            The position of the view in the adapter.
     * @param id
     *            The row id of the item that was clicked.
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
    }

    private int measureContentWidth(Adapter adapter) {
        // Menus don't tend to be long, so this is more sane than it looks.
        int width = 0;
        int itemType = 0;
        View itemView = null;
        final int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            final int positionType = adapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }
            itemView = adapter.getView(i, null, null);
            itemView.measure(widthMeasureSpec, heightMeasureSpec);
            width = Math.max(width, itemView.getMeasuredWidth());
        }

        //additional measure for header/footer
        if (mHeaderView != null) {
            mHeaderView.measure(widthMeasureSpec, heightMeasureSpec);
            width = Math.max(width, mHeaderView.getMeasuredWidth());
        }

        if (mFooterView != null) {
            mFooterView.measure(widthMeasureSpec, heightMeasureSpec);
            width = Math.max(width, mFooterView.getMeasuredWidth());
        }
        return width;
    }

    /**
     * Measure content width.
     *
     * @param adapter
     *            The adapter that provides data to this view.
     * @return Returns the width of the content.
     * @deprecated [Not use any longer]
     */
    /** @hide */
    protected int measureContentWidth(ExpandableListAdapter adapter) {
        // Menus don't tend to be long, so this is more sane than it looks.
        int width = 0;
        View itemView = null;
        final int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        final int count = adapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            itemView = adapter.getGroupView(i, false, null, null);
            itemView.measure(widthMeasureSpec, heightMeasureSpec);
            width = Math.max(width, itemView.getMeasuredWidth());
        }
        return width;
    }

    /**
     * AdapterView.OnItemClickListener interface for HtcShareVia.
     * @deprecated please use HtcShareActivity instead
     */
    @Deprecated
    public class ShareViaOnItemClickListener implements AdapterView.OnItemClickListener {
        private IHtcShareViaAdapter mShareViaAdapter;
        private AdapterView.OnItemClickListener mShareViaClickListener;

        /**
         * for HTC popup window
         *
         * @param adapter
         *            The adapter to use to create this popup window's content.
         * @param listener
         *            The onClickListener used to run some code when an item on the popup window is clicked.
         */
        public ShareViaOnItemClickListener(IHtcShareViaAdapter adapter, AdapterView.OnItemClickListener listener) {
            mShareViaAdapter = adapter;
            mShareViaClickListener = listener;
            mShareViaAdapter.setListItemTextAppearance(R.style.darklist_primary_s);
        }

        IHtcShareViaAdapter getAdapter() {
            return mShareViaAdapter;
        }

        /**
         * Shrink the list.
         *
         * @deprecated
         */
        @Deprecated
        void shrinkAdapter() {
            mShareViaAdapter.shrink();
        }

        /**
         * {@inheritDoc}
         *
         * @deprecated [Module internal use]
         */
        /** @hide */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mShareViaAdapter != null && mShareViaAdapter.isDataReady() && !mShareViaAdapter.isDataEmpty()) {
                if ((IHtcShareViaAdapter.NEED_EXPAND == mShareViaAdapter.isExpanded()) && (position == IHtcShareViaAdapter.INDEX_OF_MORE)) {
                    mShareViaAdapter.expand();
                    mShareViaAdapter.setIsDimissOk(false);
                    mShareViaAdapter.notifyDataSetChanged();
                    // [CC] paul.wy_wang, 20131021, Remove for UI static library.
                    /*
                     * // Request accessibility focus to the list. if (null != parent) { parent.requestAccessibilityFocus(); }
                     */
                } else {
                    mShareViaAdapter.setIsDimissOk(true);
                    mShareViaClickListener.onItemClick(parent, view, position, id);
                }
            }
        }
    }
}
