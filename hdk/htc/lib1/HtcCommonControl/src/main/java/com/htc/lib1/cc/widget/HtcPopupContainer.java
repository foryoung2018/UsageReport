package com.htc.lib1.cc.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewDebug.ExportedProperty;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.htc.lib1.cc.graphic.PopupWindowDrawable;
import com.htc.lib1.cc.util.DrawableUtil;
import com.htc.lib1.cc.R;

/**
 * This class will show a container above one seekbar. HtcSeekBarPopupWindowListener and HtcPopupWindow is replaced by HtcPopupContainer.
 * This class only for fill Sense50. Before Sense50, it was implemented by extends {@link android.widget.PopupWindow} but now is was implemented by extends
 * {@link ViewGroup}.
 *
 * @author felka
 */
public class HtcPopupContainer extends FrameLayout {
    private final static String TAG = "HtcPopupContainer";

    @ViewDebug.ExportedProperty(category = "CommonControl")
    private int mHeightOfProgressButton;

    private PopupWindowDrawable mPopupWindowDrawable;
    private Drawable mBoxDrawable;

    @ViewDebug.ExportedProperty(category = "CommonControl")
    private int mMinPaddingLeft;
    @ViewDebug.ExportedProperty(category = "CommonControl")
    private int mMargin1;
    @ViewDebug.ExportedProperty(category = "CommonControl")
    private int mMargin3;

    @ViewDebug.ExportedProperty(category = "CommonControl")
    private int mMinPaddingRight;

    private static final int[] ABOVE_ANCHOR_STATE_SET = new int[] { android.R.attr.state_above_anchor };

    @ExportedProperty(category = "CommonControl")
    private int mAlignType = 0;
    private View mLastAnchor = null;
    private Rect mTempRect = new Rect();
    private MoveInfo mMoveInfo = new MoveInfo();

    /**
     * Construct by context
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.

     */
    public HtcPopupContainer(Context context) {
        this(context, null);
    }

    /**
     * Construct by context and attributes
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.

     * @param attrs
     *            the attributes inflate from XML
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcPopupContainer(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.htcPopupContainerStyle);
    }

    /**
     * Construct by context, attributes and Theme default style. Read the background drawable and the gap distance between
     * {@link ProgressBar} or {@link SeekBar}.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.

     * @param attrs
     *            the attributes inflate from XML
     * @param defStyle
     *            the default style in Theme
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcPopupContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        /* To keep the original drawable */
        mBoxDrawable = getBackground();

        int[] queryAttrs = { android.R.attr.padding, android.R.attr.layout_margin, android.R.attr.minHeight, android.R.attr.drawable, };

        int defThemeStyle = R.attr.htcPopupContainerStyle;
        int nStyle = R.style.HTCPopupContainerStyle;
        TypedArray a = context.obtainStyledAttributes(attrs, queryAttrs, defThemeStyle, nStyle);
        int nShift = a.getDimensionPixelSize(0, 6);
        int nMargin = a.getDimensionPixelSize(1, 6);
        mHeightOfProgressButton = a.getDimensionPixelSize(2, 0);
        int nResID = a.getResourceId(3, 0);
        a.recycle();

        mPopupWindowDrawable = new PopupWindowDrawable(context.getResources());
        DrawableUtil.parseXML2Drawable(context.getResources(), "PopupWindowDrawable", nResID, mPopupWindowDrawable);
        mPopupWindowDrawable.setShift(nShift);
        mPopupWindowDrawable.setMargin(nMargin);
        if (0 != (getAlignType() & Gravity.HORIZONTAL_GRAVITY_MASK))
            setBackground(mPopupWindowDrawable);

        Rect margin = new Rect();
        mPopupWindowDrawable.getMinScreenMargin(margin);

        mMinPaddingLeft = margin.left;
        mMinPaddingRight = margin.right;

        mMargin1 = context.getResources().getDimensionPixelSize(R.dimen.margin_l);
        mMargin3 = context.getResources().getDimensionPixelSize(R.dimen.margin_s);
    }

    /**
     * Set the container content to an explicit view. This view is placed directly into the container's view hierarchy. It can itself be a
     * complex view hierarchy. When calling this method, the layout parameters of the specified view are ignored. Both the width and the
     * height of the view are set by default to WRAP_CONTENT.
     *
     * @param view
     *            the content show in container
     */
    public void setContentView(View view) {
        removeAllViews();
        addView(view, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    /**
     * Show the container by spec x offset and y offset
     *
     * @param anchor
     *            the container position will according to
     * @param xoff
     *            X offset
     * @param yoff
     *            Y Offset
     */
    private void showAsDropDown(View anchor, int xoff, int yoff) {
        if (null == anchor)
            return;

        ViewGroup vg = (ViewGroup) anchor.getRootView();
        ShowAsDropDown(vg, anchor, xoff, yoff);
    }

    /**
     * Show the container according to the anchor's position
     *
     * @param anchor
     *            the container will focus on
     */
    public void showAsDropDown(View anchor) {
        showAsDropDown(anchor, 0, 0);
    }

    /**
     * Get the information used to move container's position
     *
     * @param anchor
     *            the container will focus on
     */
    private void showbyMoveInfo(View anchor) {
        if (null == anchor)
            return;

        setTranslationX(mMoveInfo.x + mMoveInfo.xoff);
        setTranslationY(mMoveInfo.y + mMoveInfo.yoff);
        if (0 != (getAlignType() & Gravity.HORIZONTAL_GRAVITY_MASK))
            setArchorOff(mMoveInfo.anchorOffset);
    }

    /**
     * To make the container attachment into the root view of the window
     *
     * @param container
     *            the root view of the window that anchor attached on is expected
     * @param anchor
     *            the view that you want HtcPopupContainer to attached on
     * @param xoff
     *            the X offset that you want to add
     * @param yoff
     *            the Y offset that you want to add
     */
    private void ShowAsDropDown(ViewGroup container, View anchor, int xoff, int yoff) {
        if (null == container)
            return;
        dismiss();

        mMoveInfo.xoff = xoff;
        mMoveInfo.yoff = yoff;

        setLastAnchor(anchor);
        container.addView(this, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        if (anchor instanceof ProgressBar)
            updatePopupPosition((ProgressBar) anchor, ((ProgressBar) anchor).getMax(), ((ProgressBar) anchor).getProgress(), true);
        else
            updatePopupPosition(anchor, 100, 0, true);
        showbyMoveInfo(anchor);
    }

    /**
     * To make the container removed from parent view(expect removed from the root view of the window)
     */
    public void dismiss() {
        if (null != ((ViewGroup) getParent()))
            ((ViewGroup) getParent()).removeView(this);
        setLastAnchor(null);
    }

    /**
     * get the offset of the Anchor
     *
     * @return the mArchorOff
     */
    @ExportedProperty(category = "CommonControl")
    private int getArchorOff() {
        if (null == mPopupWindowDrawable)
            return 0;
        return mPopupWindowDrawable.getOffset();
    }

    /**
     * Set the offset of the Anchor
     *
     * @param mArchorOff
     *            the mArchorOff to set
     */
    private void setArchorOff(int archorOff) {
        if (mPopupWindowDrawable != null) {
            mPopupWindowDrawable.setOffset(archorOff);
            invalidateDrawable(mPopupWindowDrawable);
        }
    }

    /**
     * Get the last anchor that HtcPopupContainer focus on
     *
     * @return the last Anchor
     */
    private View getLastAnchor() {
        synchronized (this) {
            return mLastAnchor;
        }
    }

    /**
     * Set the last anchor that HtcPopupContainer focus on
     *
     * @param anchor
     *            the anchor that last focus on
     */
    private void setLastAnchor(View anchor) {
        synchronized (this) {
            mLastAnchor = anchor;
        }
    }

    /**
     * the class only used internal to keep the information used to move container's position
     *
     * @author felka
     *
     */
    private class MoveInfo {
        private int xoff;
        private int yoff;
        private int x;
        private int y;
        private int anchorOffset;
        private boolean bAbove;
        private int[] mAnchorLocation;
        private int[] mContainerLocation;

        MoveInfo() {
            mAnchorLocation = new int[2];
            mContainerLocation = new int[2];
            bAbove = true;
        }

        /**
         * force to delete the reference of the int [].
         *
         * @see java.lang.Object#finalize()
         */
        @Override
        protected void finalize() throws Throwable {
            // TODO Auto-generated method stub
            super.finalize();
            mAnchorLocation = null;
            mContainerLocation = null;
        }
    }

    /**
     * To find out where to put the container.
     *
     * @param anchor
     *            the container position will according to
     * @param forceUpdate
     *            force update
     */
    @TargetApi(16)
    private void findDropDownPosition(View anchor, boolean forceUpdate) {
        int y = mMoveInfo.mAnchorLocation[1] - mMoveInfo.mContainerLocation[1];

        int topTranslation;
        int top;
        //      Log.e(TAG, "anchor.getHeight() ="+anchor.getHeight()+" getMeasuredHeight() = "+getMeasuredHeight());
        int nGap = (0 != (getAlignType() & Gravity.HORIZONTAL_GRAVITY_MASK)) ? mMargin3 : (3 * mMargin1);

        if (anchor instanceof SeekBar) {
            SeekBar sb = (SeekBar) anchor;
            int nThumbHeight = (sb.getThumb().getIntrinsicHeight() > 0) ? sb.getThumb().getIntrinsicHeight() : 0;
            int nHalfViewPadding = (anchor.getHeight() - anchor.getPaddingTop() - anchor.getPaddingBottom() - nThumbHeight) >> 1;
            topTranslation = y + anchor.getPaddingTop() + nHalfViewPadding - (nGap + getMeasuredHeight());
            top = (topTranslation >= 0) ? topTranslation : y + anchor.getPaddingTop() + nHalfViewPadding + nThumbHeight + (nGap);
        } else if (anchor instanceof ProgressBar) {
            topTranslation = y + (anchor.getHeight() >> 1) - (mHeightOfProgressButton + mMargin1 + getMeasuredHeight());
            top = (topTranslation >= 0) ? topTranslation : (y + (anchor.getHeight() >> 1) + mHeightOfProgressButton + mMargin1);
        } else {
            topTranslation = y - (mMargin1 + getMeasuredHeight());
            top = (topTranslation >= 0) ? topTranslation : (y + anchor.getHeight() + mMargin1);
        }

        boolean bAbove = (topTranslation < 0) ? false : true;
        boolean bUpdate = (bAbove != mMoveInfo.bAbove || forceUpdate) ? true : false;
        mMoveInfo.bAbove = bAbove;
        if (bUpdate)
            refreshDrawableState();

        mMoveInfo.y = top;
    }

    /**
     * Get all of information that used to decide container's position
     *
     * @param anchor
     *            the container position will according to
     * @return return true if prepare data success
     */
    private boolean prepareData(View anchor) {
        View myContainer = (View) getParent();
        if (null == myContainer)
            return false;
        myContainer.getLocationInWindow(mMoveInfo.mContainerLocation);

        if (null == anchor) {
            mMoveInfo.mAnchorLocation[0] = 0;
            mMoveInfo.mAnchorLocation[1] = 0;
        } else {
            anchor.getLocationInWindow(mMoveInfo.mAnchorLocation);
        }

        return true;
    }

    /**
     * used internal
     *
     * @param anchor
     *            the container position will according to
     * @param max
     *            the max value of the progressbar
     * @param progress
     *            the value of the progressbar
     * @param forceUpdate
     *            force update
     */
    private void getPosition(View anchor, int max, int progress, boolean forceUpdate) {
        int ww = getMeasuredWidth();
        int hww = ww >> 1;
        findDropDownPosition(anchor, forceUpdate);
        int alignType = getAlignType();
        if (Gravity.DISPLAY_CLIP_HORIZONTAL == alignType) {
            mMoveInfo.x = (anchor.getRootView().getWidth() >> 1) - hww;
            return;
        } else if (0 == (alignType & Gravity.HORIZONTAL_GRAVITY_MASK)) {
            mMoveInfo.x = (mMoveInfo.mAnchorLocation[0] - mMoveInfo.mContainerLocation[0]) + (anchor.getWidth() >> 1) - hww;
            return;
        }

        int w = anchor.getWidth() - anchor.getPaddingLeft() - anchor.getPaddingRight();
        float posProgress = max > 0 ? (float) progress * w / (float) max : 0;
        int position = (mMoveInfo.mAnchorLocation[0] - mMoveInfo.mContainerLocation[0]) + anchor.getPaddingLeft() + (int) posProgress;
        int alignPadding = 0;

        if (Gravity.LEFT == (alignType & Gravity.HORIZONTAL_GRAVITY_MASK)) {
            position -= mMinPaddingLeft;
            alignPadding = (hww - mMinPaddingLeft);
        } else if (Gravity.RIGHT == (alignType & Gravity.HORIZONTAL_GRAVITY_MASK)) {
            position -= (ww - mMinPaddingRight);
            alignPadding = -(hww - mMinPaddingRight);
        } else {
            position -= hww;
            alignPadding = 0;
        }

        int xoff = 0;
        int anchorOffset = 0;
        int nLeftest = 0;
        int nRightest = ((View) getParent()).getWidth() - ww;

        if (nLeftest > position) {
            xoff = nLeftest;
            anchorOffset = nLeftest - position + alignPadding;
        } else if (position > nRightest) {
            xoff = nRightest;
            anchorOffset = nRightest - position + alignPadding;
        } else {
            xoff = position;
            anchorOffset = alignPadding;
        }

        mMoveInfo.x = xoff;
        mMoveInfo.anchorOffset = anchorOffset;
    }

    /**
     * Update the container's position according to the anchor, max, progress
     *
     * @param anchor
     *            the container position will according to
     * @param max
     *            if the anchor that extends {@link ProgressBar}, the container will change its position according to max and progress.
     * @param progress
     *            the progress value
     * @param forceUpdate
     *            force update the container position
     */
    private void updatePopupPosition(View anchor, int max, int progress, boolean forceUpdate) {
        if (!prepareData(anchor)) {
            return;
        }

        setLastAnchor(anchor);

        getPosition(anchor, max, progress, forceUpdate);

        setTranslationX(mMoveInfo.x);
        if (0 != (getAlignType() & Gravity.HORIZONTAL_GRAVITY_MASK))
            setArchorOff(mMoveInfo.anchorOffset);
    }

    /**
     * Update the container position according the progress's value
     *
     * @param anchor
     *            the container will focus on
     * @param progress
     *            the progress value
     */
    public void updatePopupPosition(View anchor, int progress) {
        if (anchor instanceof ProgressBar)
            updatePopupPosition((ProgressBar) anchor, ((ProgressBar) anchor).getMax(), ((ProgressBar) anchor).getProgress(), false);
        else
            updatePopupPosition(anchor, 100, 0, false);
    }

    /**
     * We must change the position of the container when the content size is changed.
     *
     * @param w
     *            the new width
     * @param h
     *            the new height
     * @param oldw
     *            the old width
     * @param oldh
     *            the old height
     * @see android.widget.FrameLayout#onSizeChanged(int, int, int, int)
     * @hide
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh);

        int max = 100;
        int progress = 0;

        View view = getLastAnchor();
        if ((null != view) && (view instanceof ProgressBar)) {
            ProgressBar progressBar = (ProgressBar) view;
            max = progressBar.getMax();
            progress = progressBar.getProgress();
        }

        updatePopupPosition(view, max, progress, true);
        showbyMoveInfo(view);
    }

    /**
     * Create the new state when the space between seek and window upper bound is enough to put the container.
     *
     * @see android.view.ViewGroup#onCreateDrawableState(int)
     * @hide
     */
    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        Log.e("HtcPopupContainter", "onCreateDrawableState " + extraSpace);
        if (mMoveInfo.bAbove) {
            // 1 more needed for the above anchor state
            final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
            View.mergeDrawableStates(drawableState, ABOVE_ANCHOR_STATE_SET);
            return drawableState;
        } else {
            return super.onCreateDrawableState(extraSpace);
        }
    }

    /**
     * Update the padding after the drawable state is changed because drawable state change will change the padding
     *
     * @see android.widget.FrameLayout#drawableStateChanged()
     * @hide
     */
    @Override
    protected void drawableStateChanged() {
        // TODO Auto-generated method stub
        super.drawableStateChanged();
        Drawable d = getBackground();
        if (d != null)
            d.getPadding(mTempRect);
        setPadding(mTempRect.left, mTempRect.top, mTempRect.right, mTempRect.bottom);
    }

    /**
     * Get the type of align. See {@link android.view.Gravity}
     *
     * @param gravity
     *            the type of the alignment. enum {@link Gravity#CENTER}, {@link Gravity#LEFT} and {@link Gravity#RIGHT}.
     */
    public void setAlignType(int gravity) {
        synchronized (this) {
            this.mAlignType = gravity;
            if (0 == (mAlignType & Gravity.HORIZONTAL_GRAVITY_MASK))
                setBackground(mBoxDrawable);
            else
                setBackground(mPopupWindowDrawable);
        }
    }

    /**
     * Set the type of align. See {@link Gravity}
     *
     * @return the type of the alignment. enum {@link Gravity.CENTER}, {@link Gravity.LEFT} and {@link Gravity.RIGHT}
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public int getAlignType() {
        synchronized (this) {
            return mAlignType;
        }
    }
}
