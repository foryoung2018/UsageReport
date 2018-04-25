package com.htc.lib1.cc.widget;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.htc.lib1.cc.R;

import static com.htc.lib1.cc.util.WindowUtil.getScreenWidthPx;

/**
 * A HTC style ListPopupWindow anchors itself to a host view in header and displays a
 * list of choices.
 *
 * <p>HTC style ListPopupWindow contains a number of tricky behaviors surrounding
 * positioning, scrolling parents to fit the dropdown, interacting
 * sanely with the IME if present, and others.We follow design to setup left/right
 * margin, Min./Max. width height for PopupWindow.
 * We strong suggest do not use HTC style ListPopupWindow out of Header(ActionBar).
 * If you do it and meet UI problem, AP side should handle yourself.</p>
 */
public class ListPopupWindow extends android.widget.ListPopupWindow implements OnDismissListener, OnKeyListener, OnGlobalLayoutListener, View.OnAttachStateChangeListener {
    private ViewGroup mMeasureParent;
    private Context mContext;
    private DisplayMetrics dm;
    private ListAdapter mAdapter;
    private int mMaxWidth;
    private int mMinWidth;
    private int mMaxContentWidth;
    private int mMinContentWidth;
    private int mMinContentHeight;
    private int mMaxContentHeight;
    private int mMeasureTotalWidth;
    private int mMeasureTotalHeight;
    private int mMaxHeight;
    private int mMinHeight;
    private int mDividerHeight;
    private int mHorizontalMargin;
    private boolean mAutoDecideWidth = true;
    private boolean mAutoDecideHeight = true;
    private boolean mAutoDecideHorizontalOffset = true;
    private ViewTreeObserver mTreeObserver;
    private OnDismissListener mOnDismissListener = null;
    private Rect mTmpRect = new Rect();

    private static Context getWrapperTheme(Context context) {
        if (null == context)
            return context;

        if (!(context instanceof Activity))
            return createContextWrapper(context);

        ActionBar ab = ((Activity) context).getActionBar();
        if (null == ab)
            return createContextWrapper(context);

        return ab.getThemedContext();
    }

    private static Context createContextWrapper(Context context) {
        Context mThemedContext;

        TypedValue outValue = new TypedValue();
        Resources.Theme currentTheme = context.getTheme();
        currentTheme.resolveAttribute(android.R.attr.actionBarWidgetTheme, outValue, true);
        final int targetThemeRes = outValue.resourceId;

        if (targetThemeRes != 0) {
            mThemedContext = new ContextThemeWrapper(context, targetThemeRes);
        } else {
            mThemedContext = context;
        }

        return mThemedContext;
    }

    /**
     * Create a new, empty popup window capable of displaying items from a
     * ListAdapter. This popup widnow should be used on Header.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public ListPopupWindow(ContextThemeWrapper context) {
        this(context, android.R.attr.listPopupWindowStyle);
    }

    /**
     * Create a new, empty popup window capable of displaying items from a
     * ListAdapter. This popup widnow should be used on Header.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param defStyleAttr
     *            Attributes from inflating parent views used to style the
     *            popup.
     */
    public ListPopupWindow(ContextThemeWrapper context, int defStyleAttr) {
        super(getWrapperTheme(context), null, (defStyleAttr == android.R.attr.popupMenuStyle) ? android.R.attr.popupMenuStyle : android.R.attr.listPopupWindowStyle);
        mContext = context;
        if (mContext != null) {
            dm = mContext.getResources().getDisplayMetrics();
            TypedArray a = context.obtainStyledAttributes(null, R.styleable.HtcListItem, android.R.attr.dropDownListViewStyle, 0);
            Drawable d = a.getDrawable(R.styleable.HtcListItem_android_divider);
            a.recycle();
            if (d != null)
                mDividerHeight = (d.getIntrinsicHeight() > 0) ? d.getIntrinsicHeight() : 0;
            setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.common_popupmenu_top));
        }
        super.setOnDismissListener(this);
        setModal(true);
        setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        mHorizontalMargin = context.getResources().getDimensionPixelOffset(
                R.dimen.margin_m);
        countMaxAndMinWidth();
    }

    /**
     * 计算Popup Window的最大宽度(screenWidth - mHorizontalMargin * 2)
     * 和最小宽度(screenWidth * 0.7 - mHorizontalMargin)
     */
    private void countMaxAndMinWidth() {
        int screenWidth = Math.min(dm.widthPixels, dm.heightPixels);
        TypedValue outValue = new TypedValue();
        mContext.getResources().getValue(R.dimen.popup_window_max_width, outValue, true);
        float popupMaxWidthRatio = outValue.getFloat();
        mMaxWidth = mMinWidth = Math.max(
                (int) (screenWidth * popupMaxWidthRatio - mHorizontalMargin), 0);
        if ((screenWidth - mHorizontalMargin * 2) > mMinWidth)
            mMaxWidth = screenWidth - mHorizontalMargin * 2;
        getBackground().getPadding(mTmpRect);
        mMinContentWidth = mMinWidth - mTmpRect.right - mTmpRect.left;
        mMaxContentWidth = mMaxWidth - mTmpRect.right - mTmpRect.left;
    }

    /**
     * 计算Popup Window的最大高度(dm.heightPixels * 0.76 - bottomEdgeOfAnchor)
     * 和最小高度(HtcListItemHeight)
     */
    private void countMaxAndMinHeight() {
        mMinHeight = (Integer) HtcProperty.getProperty(mContext, "HtcListItemHeight");
        int bottomEdgeOfAnchor = 0;
        TypedValue outValue = new TypedValue();
        mContext.getResources().getValue(R.dimen.popup_window_max_height, outValue, true);
        float popupMaxHeightRatio = outValue.getFloat();

        View v = getAnchorView();
        if (null != v) {
            int[] location = { 0, 0 };
            v.getLocationOnScreen(location);
            bottomEdgeOfAnchor = location[1] + v.getHeight();
        }

        mMaxHeight = (int) ((dm.heightPixels * popupMaxHeightRatio)- bottomEdgeOfAnchor - mHorizontalMargin);

        mMinContentHeight = mMinHeight - mTmpRect.bottom - mTmpRect.top;
        mMaxContentHeight = mMaxHeight - mTmpRect.bottom - mTmpRect.top;
    }

    /**
     * 设置Dismisslistener
     * @hide
     */
    @Override
    public void setOnDismissListener(OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    /* @hide */
    @Override
    public void onDismiss() {
        View anchorView = getAnchorView();
        if (mOnDismissListener != null)
            mOnDismissListener.onDismiss();
        if (mTreeObserver != null) {
            if (!mTreeObserver.isAlive()&&(null != anchorView))
                mTreeObserver = anchorView.getViewTreeObserver();
            mTreeObserver.removeGlobalOnLayoutListener(this);
            mTreeObserver = null;
        }

        if (null != anchorView)
            anchorView.removeOnAttachStateChangeListener(this);
    }

    /* @hide */
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_MENU) {
            dismiss();
            return true;
        }
        return false;
    }
    /**
     * @param adapter 傳入ListAdapter給PopupWindow
     * 目前只支持ListAdapter，尚不支持ExpandableListAdapter
     */
    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        mAdapter = adapter;
    }

    /**
     * 把popup widnow的宽度限制在最大宽度和最小宽度之间
     * @param measureContentWidth 测量的到的popup window宽度
     * @return 限制后的结果
     */
    private int getPopupContentWidth(int measureContentWidth) {
        int width = 0;
        width = Math.max(mMinContentWidth, measureContentWidth);
        width = Math.min(mMaxContentWidth, width);

        return width;
    }

    /**
     * 把popup widnow的宽度限制在最大高度和最小高度之间
     * @param measureContentHeight 测量的到的popup window高度
     * @return 限制后的结果
     */
    private int getPopupContentHeight(int measureContentHeight) {
        int height = 0;
        height = Math.max(mMinContentHeight, measureContentHeight);
        height = Math.min(mMaxContentHeight, height);

        return height;
    }

    /**
     * 设置PopupWindow的宽度，
     * 如果，使用这个method设置PopupWindow的宽度，
     * 那么，PopupWindow的宽度将由设置的宽度决定，不再受最大最小宽度的限制
     */
    @Override
    public void setContentWidth(int width) {
        super.setContentWidth(width);
        mAutoDecideWidth = false;
    }

    /**
     * 设置PopupWindow的高度，
     * 如果，使用这个method设置PopupWindow的高度，
     * 那么，PopupWindow的高度将由设置的高度决定，不再受最大最小高度的限制
     */
    @Override
    public void setHeight(int height){
        super.setHeight(height);
        mAutoDecideHeight = false;
    }

    /**
     * Set the horizontal offset of this popup from its anchor view in pixels
     * 如果，使用这个method设置PopupWindow的HorizontalOffset，
     * 那么，PopupWindow的位置将由设置的位置决定，不再受UIGL的限制
     */
    @Override
    public void setHorizontalOffset(int offset) {
        super.setHorizontalOffset(offset);
        mAutoDecideHorizontalOffset = false;
    }

    /* @hide */
    @Override
    public void show() {
        View anchorView = getAnchorView();
        if ((null !=  anchorView) && (!isShowing())) {
            mTreeObserver = anchorView.getViewTreeObserver(); // Refresh to latest
            mTreeObserver.addOnGlobalLayoutListener(this);
            anchorView.addOnAttachStateChangeListener(this);
        }
        internalShow();
    }

    /**
     * 调整 popupwindow的大小、位置，显示popup window
     */
    private void internalShow() {
        countMaxAndMinHeight();
        measureContentWidthAndHeight(mAdapter);
        if (mAutoDecideWidth)
            super.setContentWidth(getPopupContentWidth(mMeasureTotalWidth));
        if ( mAutoDecideHeight )
             super.setHeight(getPopupContentHeight(mMeasureTotalHeight));
        adjustPopupPos();

        if (null != getAnchorView())
            super.show();
        if (null != getListView())
            getListView().setOnKeyListener(this);
    }

    /**
     * 调整popupwindow的位置
     */
    private void adjustPopupPos() {
        if (null != getAnchorView()) {
            int anchorPos[] = new int[2];
            getAnchorView().getLocationInWindow(anchorPos);

            if (mAutoDecideHorizontalOffset){
                if (mHorizontalMargin - anchorPos[0] > 0)
                    super.setHorizontalOffset(mHorizontalMargin - anchorPos[0]);

                int widthPx = getScreenWidthPx(mContext.getResources());
                int moveLeftHorizontalOffset = (widthPx - mHorizontalMargin) - (anchorPos[0] +  getWidth());

                if (moveLeftHorizontalOffset < 0)
                    super.setHorizontalOffset(moveLeftHorizontalOffset);
            }
        }
    }

    /**
     * 测量popup window的宽和高
     * @param adapter popup window 要显示的listadapter
     */
    private void measureContentWidthAndHeight(ListAdapter adapter) {
        boolean readyWidth = false;
        boolean readyHeight = false;
        mMeasureTotalWidth = 0;
        mMeasureTotalHeight = 0;
        View itemView = null;
        int itemType = 0;
        final int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

        final int count = (adapter != null) ? adapter.getCount() : 0;
        for (int i = 0; i < count; i++) {
            final int positionType = adapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }
            if (mMeasureParent == null) {
                mMeasureParent = new FrameLayout(mContext);
            }
            itemView = adapter.getView(i, itemView, mMeasureParent);
            itemView.measure(widthMeasureSpec, heightMeasureSpec);
            mMeasureTotalWidth = Math.max(mMeasureTotalWidth, itemView.getMeasuredWidth());
            //divider在listView的item外面，加上divider的高度（如果，divider在listView的item里面，这里就不应该加了）
            mMeasureTotalHeight += itemView.getMeasuredHeight();
            //因为，divider的数量比item的数量少一个
            if (0 != i)
                mMeasureTotalHeight += mDividerHeight;

            //为了performance的需要，这里做了处理，
            if (mMeasureTotalWidth >= mMaxContentWidth)
                readyWidth = true;
            if (mMeasureTotalHeight >= mMaxContentHeight)
                readyHeight = true;
            //在宽和高都超过上限的时候，跳出
            if (readyWidth && readyHeight)
                break;
        }
    }

    /* @hide */
    @Override
    public void onGlobalLayout() {
        if (isShowing()) {
            final View anchorView = getAnchorView();
            if (anchorView == null || !anchorView.isShown()) {
                dismiss();
            } else if (isShowing()) {
                // Recompute window size and position
                internalShow();
            }
        }

    }

    /* @hide */
    @Override
    public void onViewAttachedToWindow(View arg0) {
        // TODO Auto-generated method stub

    }

    /* @hide */
    @Override
    public void onViewDetachedFromWindow(View v) {
        // TODO Auto-generated method stub
        if (mTreeObserver != null) {
            if (!mTreeObserver.isAlive())
                mTreeObserver = v.getViewTreeObserver();
            mTreeObserver.removeGlobalOnLayoutListener(this);
        }
        v.removeOnAttachStateChangeListener(this);
    }
}
