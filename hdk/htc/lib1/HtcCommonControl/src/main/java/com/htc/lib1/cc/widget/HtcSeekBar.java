package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.SeekBar;
import android.view.MotionEvent;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.graphics.Canvas;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.HtcCommonUtil;

/**
 * HtcSeekBar extends {@link <a href="http://developer.android.com/reference/android/widget/SeekBar.html">SeekBar</a>}. The difference between them is that HtcSeekBar will change it's visual status by it's pressed status.
 *
 *  The usage of HtcSeekBar is the same with android.widget.SeekBar.
 * @author felka
 * @version %I%, %G%
 * @see <a href="http://developer.android.com/reference/android/widget/SeekBar.html">SeekBar</a>
 *
 */
public class HtcSeekBar extends SeekBar {

    /**
     * Display mode for HtcSeekBar, default value is 0.
     */
    public static final int DISPLAY_MODE_DEFAULT = 0x00;

    /**
     * Display mode for HtcSeekBar, white mode is the same as default.
     */
    public static final int DISPLAY_MODE_WHITE = DISPLAY_MODE_DEFAULT ;

    /**
     * Display mode for HtcSeekBar, black mode value is 1.
     */
    public static final int DISPLAY_MODE_BLACK = 0x01;

    private static final int LAYER_INDEX_TRACK = 0;

    private static final int LAYER_INDEX_BUFFER = 1;

    private static final int LAYER_INDEX_PROGRESS = 2;
    @ExportedProperty(category = "CommonControl")
    int mProgressHeight;
    Drawable mBlackDrawable;
    Drawable mNormalDrawable;
    Drawable mPressedThumb;
    boolean mDisplayThumb = true;
    @ExportedProperty(category = "CommonControl")
    int mMinDrawableHeight = 10;
    @ExportedProperty(category = "CommonControl", resolveId = true)
    private int mWhiteResId = 0;
    @ExportedProperty(category = "CommonControl", resolveId = true)
    private int mBlackResId = 0;
    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = DISPLAY_MODE_DEFAULT, to = "DISPLAY_MODE_DEFAULT"),
            @IntToString(from = DISPLAY_MODE_BLACK, to = "DISPLAY_MODE_BLACK"),
            @IntToString(from = DISPLAY_MODE_WHITE, to = "DISPLAY_MODE_WHITE")
    })
    int mDisplayMode = DISPLAY_MODE_DEFAULT;
    private Drawable mFocusIndicator;
    @ExportedProperty(category = "CommonControl")
    private boolean mDrawFocusIndicator = false;
    @ExportedProperty(category = "CommonControl")
    private boolean isClicked = false;

    /**
     * Create a new HtcSeekBar and initial progress is 0.
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcSeekBar(Context context) {
        this(context, null);
        // TODO Auto-generated constructor stub
    }

    /**
     * Create a new HtcSeekBar and initial progress is 0.
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs attributeSet
     */
    public HtcSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, com.htc.lib1.cc.R.attr.htcSeekBarStyle);
        // TODO Auto-generated constructor stub
    }

    /**
     * Create a new HtcSeekBar and initial progress is 0.
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs attributeSet
     * @param defStyle default style for HtcSeekBar
     */
    public HtcSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub

        TypedArray a = context.obtainStyledAttributes(attrs,
                com.htc.lib1.cc.R.styleable.HtcSeekBar, defStyle,
                com.htc.lib1.cc.R.style.HTCSeekBarStyle);
        int nArrayID = a.getResourceId(
                com.htc.lib1.cc.R.styleable.HtcSeekBar_android_entries, 0);
        int userPaddingLeft = a.getDimensionPixelSize(
                com.htc.lib1.cc.R.styleable.HtcSeekBar_android_paddingLeft,
                getPaddingLeft());
        int userPaddingRight = a.getDimensionPixelSize(
                com.htc.lib1.cc.R.styleable.HtcSeekBar_android_paddingRight,
                getPaddingRight());
        mPressedThumb = a
                .getDrawable(com.htc.lib1.cc.R.styleable.HtcSeekBar_android_thumb);
        int mThumbOffset = a.getDimensionPixelOffset(
                com.htc.lib1.cc.R.styleable.HtcSeekBar_android_thumbOffset,
                getThumbOffset());
        int nDisplayMode = a.getInt(
                com.htc.lib1.cc.R.styleable.HtcSeekBar_displayMode,
                DISPLAY_MODE_DEFAULT);
        a.recycle();

        mProgressHeight = getContext().getResources().getDimensionPixelOffset(com.htc.lib1.cc.R.dimen.htc_progressbar_height);

       //setThumb(mPressedThumb);
       setThumbOffset(mThumbOffset);

       int paddingLeft = (mThumbOffset > userPaddingLeft) ? mThumbOffset : userPaddingLeft;
       int paddingRight = (mThumbOffset > userPaddingRight) ? mThumbOffset : userPaddingRight;
       setPadding(paddingLeft, getPaddingTop(), paddingRight, getPaddingBottom());


       if ( 0 != nArrayID ) {
           a = getResources().obtainTypedArray(nArrayID);
           mWhiteResId = a.getResourceId(0, 0);
           mBlackResId = a.getResourceId(1, 0);
           a.recycle();
       }

        setMinHeightByDrawable(mPressedThumb);

        setDisplayMode(nDisplayMode);
    }

    private Drawable getSkinDrawable(int nResId) {
        if ( 0 == nResId )
            return null;
        return getResources().getDrawable(nResId);
    }


    /**
     * Deprecated. Not support anymore.
     * Internal use.
     * @param bThick
     * @param drawable
     * @param clip
     * @param nID
     * @return drawable for SeekBar which already been tileify.
     * @deprecated [Not use any longer]
     */
    /**@hide*/
    public Drawable HtcTileify(boolean bThick, Drawable drawable, boolean clip, int nID) {
        return drawable;
    }

    /**
     *  Force progress and secondary progress to redraw itself after changing the drawable.
     *  Copy from
     * @param id
     * @param progress
     * @param fromUser
     */
    private synchronized void doForceRefreshProgress(int id, int progress, boolean fromUser) {
        float scale = this.getMax() > 0 ? (float) progress / (float) this.getMax() : 0;
        final Drawable d = this.getProgressDrawable();
        if (d != null) {
            Drawable progressDrawable = null;

            if (d instanceof LayerDrawable) {
                progressDrawable = ((LayerDrawable) d).findDrawableByLayerId(id);
            }

            final int level = (int) (scale * 10000);
            (progressDrawable != null ? progressDrawable : d).setLevel(level);
        } else {
            invalidate();
        }
    }

    private void setSeekBarDrawableBundary(Drawable d) {
        if (null == d)
            return;
        LayerDrawable ld = (LayerDrawable) d;
        int nDH = mProgressHeight;
        int nVH = getHeight();
        int nTH = getThumb().getIntrinsicHeight();
        Rect r_ld = ld.getBounds();

        Rect r = getThumb().getBounds();
        r.top = (nVH - getPaddingTop() - getPaddingBottom() - nTH) / 2;
        r.bottom = (nVH - getPaddingTop() - getPaddingBottom() + nTH ) / 2;
        getThumb().setBounds(r);

        Rect r_background = new Rect();;
        r_background.right = r_ld.right;
        r_background.left = r_ld.left;
        r_background.top = (nVH - getPaddingTop() - getPaddingBottom() -nDH) / 2;
        r_background.bottom = (nVH - getPaddingTop() - getPaddingBottom() + nDH ) / 2;
        d = ld.findDrawableByLayerId(android.R.id.background);
        if (null != d) d.setBounds(r_background);
        d = ld.findDrawableByLayerId(android.R.id.secondaryProgress);
        if (null != d) d.setBounds(r_background);
        d = ld.findDrawableByLayerId(android.R.id.progress);
        if (null != d) d.setBounds(r_background);
    }

    /** After super class onSizeChange, HtcSeekBar needs to compute each boundary of each drawable.
     * @param arg0 Current width of this HtcSeekBar.
     * @param arg1 Current height of this HtcSeekBar.
     * @param arg2 Old width of this HtcSeekBar.
     * @param arg3 Old height of this HtcSeekBar.
     * @see android.widget.AbsSeekBar#onSizeChanged(int, int, int, int)
     */
    @Override
    protected void onSizeChanged(int arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
        super.onSizeChanged(arg0, arg1, arg2, arg3);

        LayerDrawable ld;
        if ( mNormalDrawable instanceof LayerDrawable ) {
            ld = (LayerDrawable)mNormalDrawable;
            setSeekBarDrawableBundary(mNormalDrawable);
        }

        if ( mBlackDrawable instanceof LayerDrawable ) {
            ld = (LayerDrawable)mBlackDrawable;
            setSeekBarDrawableBundary(mBlackDrawable);
        }
    }

    private void setMinHeightByDrawable(Drawable drawable) {
        if ( null == drawable ) {
            return ;
        }

        if ( drawable instanceof BitmapDrawable ) {
            final int nDrawableHeight = ((BitmapDrawable) drawable).getIntrinsicHeight();
            if ( mMinDrawableHeight < nDrawableHeight ) {
                mMinDrawableHeight = nDrawableHeight;
            }
        }

        if ( drawable instanceof NinePatchDrawable ) {
            final int nDrawableHeight = ((NinePatchDrawable) drawable).getIntrinsicHeight();
            if ( mMinDrawableHeight < nDrawableHeight ) {
                mMinDrawableHeight = nDrawableHeight;
            }
        }
    }


    /** Refresh progress and secondary progress if the progress drawable was changed.
     * @param d drawable for HtcSeekBar progressDrawable.
     * @see android.widget.ProgressBar#setProgressDrawable(android.graphics.drawable.Drawable)
     * @deprecated [Module internal use]
     */
    /**@hide*/
    @Override
    public void setProgressDrawable(Drawable d) {
        // TODO Auto-generated method stub
        super.setProgressDrawable(d);
        this.doForceRefreshProgress(android.R.id.progress, getProgress(), false);
        this.doForceRefreshProgress(android.R.id.secondaryProgress, getSecondaryProgress(), false);
    }

    /**
     * Get HtcSeekBar display mode.
     * @return the Mode
     * @deprecated [Module internal use]
     */
    /**@hide*/
    public int getDisplayMode() {
        return mDisplayMode;
    }

    /** This control can't accept odd number of height, will force height into even number.
     * @param widthMeasureSpec horizontal space requirements as imposed by the parent
     * @param heightMeasureSpec vertical space requirements as imposed by the parent.
     * @see android.widget.AbsSeekBar#onMeasure(int, int)
     * @deprecated [Module internal use]
     */
    /**@hide*/
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec,
            int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // TODO Auto-generated method stub


        int dw = getMeasuredWidth();
        int dh = getMeasuredHeight();
        int drawableHeight = ( null != mPressedThumb )?mPressedThumb.getIntrinsicHeight():0;
        dh = Math.max(dh,  drawableHeight);


       if ( 1 == (dh & 0x00000001) ) {
           dh += 1;
        }

       int specMode = MeasureSpec.getMode(heightMeasureSpec);
       int specSize =  MeasureSpec.getSize(heightMeasureSpec);
       specSize = Math.max(specSize,  drawableHeight);
       if ( 1 == (specSize & 0x00000001) ) {
           heightMeasureSpec = MeasureSpec.makeMeasureSpec(specMode, specSize+1);
        }

       setMeasuredDimension(resolveSize(dw, widthMeasureSpec),
                resolveSize(dh, heightMeasureSpec));

    }

    /** Set the HtcSeekBar display mode.
     * @param mode the mode that how to display seekbar.
     */
    public void setDisplayMode(int mode) {
        Drawable realBackground;
        if (DISPLAY_MODE_BLACK == mode) {
            if ( null == mBlackDrawable ) {
                mBlackDrawable = getSkinDrawable(mBlackResId);
            }
            realBackground = getDisplayRealBackground(getContext(), (LayerDrawable)mBlackDrawable, mode);
            this.mDisplayMode = mode;
        } else {
            if (null == mNormalDrawable) {
                mNormalDrawable = getSkinDrawable(mWhiteResId);
            }
            realBackground = getDisplayRealBackground(getContext(), (LayerDrawable)mNormalDrawable, mode);
            this.mDisplayMode = DISPLAY_MODE_DEFAULT;
        }
        setProgressDrawable(realBackground);
        mPressedThumb = getDisplayModeThumb(mode);
        int offset = getThumbOffset();
        super.setThumb(mPressedThumb);
        setThumbOffset(offset);
    }

    private LayerDrawable getDisplayRealBackground(Context context, LayerDrawable layer, int mode) {
        int colorTrack, colorProgress, colorBuffer;
        if (DISPLAY_MODE_BLACK == mode) {
            colorTrack = HtcCommonUtil.getCommonThemeColor(context,
                    R.styleable.ThemeColor_progress_track_end_color);
        } else {
            colorTrack = HtcCommonUtil.getCommonThemeColor(context,
                    R.styleable.ThemeColor_progress_track_start_color);
        }
        colorProgress = HtcCommonUtil.getCommonThemeColor(context,
                R.styleable.ThemeColor_category_color);
        colorBuffer = HtcCommonUtil.getCommonThemeColor(context,
                R.styleable.ThemeColor_progress_track_center_color);
        if (null != layer) {
            Drawable track = layer.getDrawable(LAYER_INDEX_TRACK);
            Drawable buffer = layer.getDrawable(LAYER_INDEX_BUFFER);
            Drawable progress = layer.getDrawable(LAYER_INDEX_PROGRESS);

            (track.mutate()).setColorFilter(colorTrack, PorterDuff.Mode.SRC_ATOP);
            (buffer.mutate()).setColorFilter(colorBuffer, PorterDuff.Mode.SRC_ATOP);
            (progress.mutate()).setColorFilter(colorProgress, PorterDuff.Mode.SRC_ATOP);
        }
        return layer;
    }
    private Drawable getDisplayModeThumb(int nDisplayMode) {
        if (DISPLAY_MODE_BLACK == nDisplayMode) {
            return getContext().getResources().getDrawable(com.htc.lib1.cc.R.drawable.htcthumb_b);
        } else {
            return getContext().getResources().getDrawable(com.htc.lib1.cc.R.drawable.htcthumb);
        }
    }

     /**
      * Set HtcSeekBar thumb visibility
      * @param bDisplay display the thumb or not, default is true
      */
     public void setThumbVisible(boolean bDisplay) {
         mDisplayThumb = bDisplay;
       if ( null != mPressedThumb )
          mPressedThumb.setAlpha(( false == bDisplay )?0:255);
       invalidate();
     }

    /**Because application will call setThumbVisible to hide the thumb, we need to keep the thumb drawable
     * @param thumb drawable of HtcSeekBar thumb.
     * @see android.widget.AbsSeekBar#setThumb(android.graphics.drawable.Drawable)
     * @deprecated [Module internal use]
     */
     /**@hide*/
     public void setThumb(Drawable thumb) {
       super.setThumb(thumb);
         mPressedThumb = thumb;
       if (getThumbOffset() > getPaddingLeft())
           setPadding(getThumbOffset(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
       if (getThumbOffset() > getPaddingRight())
           setPadding(getPaddingLeft(), getPaddingTop(), getThumbOffset(), getPaddingBottom());
         invalidate();
     }

    /**Prevent from RD to set the padding less then the ThumbOffset
     * set the right boundaries for every drawables after setting padding
     * @param left padding of left.
     * @param top padding of top.
     * @param right padding of right.
     * @param bottom padding of bottom.
     * @see android.view.View#setPadding(int, int, int, int)
     */
    @Override
    public void setPadding(int left, int top, int right, int bottom) {
       // TODO Auto-generated method stub

       if ( getThumbOffset() > left  )
          left = getThumbOffset();
       if ( getThumbOffset() > right  )
          right = getThumbOffset();


       super.setPadding(left, top, right, bottom);

         int r= getWidth() - getPaddingLeft() - getPaddingRight();
         int b= getHeight() - getPaddingBottom() - getPaddingTop();

         if ( null != mNormalDrawable ) {
             mNormalDrawable.setBounds(0, mNormalDrawable.getBounds().top, r, mNormalDrawable.getBounds().bottom);
         }
         if ( null != mBlackDrawable ){
             mBlackDrawable.setBounds(0, mBlackDrawable.getBounds().top, r, mBlackDrawable.getBounds().bottom);
         }
    }

    static class SavedState extends BaseSavedState {
        int mDisplayThumb;

        /**
         * Constructor called from {@link ProgressBar#onSaveInstanceState()}
         */
        SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            mDisplayThumb = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mDisplayThumb);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    /**
     * Save the State of HtcSeekBar and the visibility of thumb.
     * @return Returns a Parcelable object containing the view's current dynamic state.
     * @see android.view.View#onSaveInstanceState()
     * @deprecated [Module internal use]
     */
    /**@hide*/
    @Override
    public Parcelable onSaveInstanceState() {
        // Force our ancestor class to save its state
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);

        ss.mDisplayThumb = (mDisplayThumb)?1:0;

        return ss;
    }

    /**
     * Re-apply state that had previously been generated by onSaveInstanceState().
     * @param state State previously generated by onSaveInstanceState().
     * @deprecated [Module internal use]
     */
    /**@hide*/
    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        setThumbVisible((0 == ss.mDisplayThumb)?false:true);
    }

    /** Implement this method to handle touch screen motion events.
     * @param event The motion event.
     * @deprecated [Module internal use]
     */
    /**@hide*/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean b = super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (isClicked) {
                  int progress = (getMax() > 0) ? (int)(getProgress() * 100 / getMax()) : 0;
                  announceForAccessibility(progress + "%");
                  isClicked = false;
                }
                break;

            case MotionEvent.ACTION_DOWN:
                isClicked = true;
                break;

            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_CANCEL:
                isClicked = false;
                break;
        }
        return b;
    }

    //------------ Overriding below methods for API backward compatible -------------
    /**
     * This is called when the view is detached from a window.
     * @deprecated [Not use any longer]
     */
    /**@hide*/
    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }


    /**
     * Set the enabled state of this view.
     * @param visibility should be VISIBLE, INVISIBLE, or GONE.
     * @deprecated [Not use any longer]
     */
    /**@hide*/
    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
    }


    /**
     * Set the enabled state of this view.
     * @param enabled True if this view is enabled.
     * @deprecated [Not use any longer]
     */
    /**@hide*/
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }


    /**
     * Called when current configuration of the resources being used by the application have changed.
     * @param newConfig The new configuration.
     * @deprecated [Not use any longer]
     */
    /**@hide*/
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    /**
     * Sets a listener to receive notifications of changes to the SeekBar's progress level.
     * @param l listener for HtcSeekBar
     * @deprecated [Not use any longer]
     */
    /**@hide*/
    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        super.setOnSeekBarChangeListener(l);
    }

    /**
     * Called by the view system when the focus state of this view changes.
     * @param gainFocus True if the View has focus; false otherwise.
     * @param direction The direction focus has moved when requestFocus() is called to give this view focus.
     * @param previouslyFocusedRect The rectangle, in this view's coordinate system, of the previously focused view.
     * @deprecated [Module internal use]
     */
    /**@hide*/
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect){
        mDrawFocusIndicator = gainFocus;
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    /**
     * Implement drawing when focus.
     * @param canvas the canvas on which the background will be drawn
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mDrawFocusIndicator)
            drawIndicator(canvas);
    }

    private void drawIndicator(Canvas canvas) {
        if (null == mFocusIndicator) {
            mFocusIndicator = getContext().getResources().getDrawable(com.htc.lib1.cc.R.drawable.common_focused);
            mFocusIndicator.mutate();
            mFocusIndicator.setColorFilter(new PorterDuffColorFilter(getContext().getResources().getColor(com.htc.lib1.cc.R.color.overlay_color), PorterDuff.Mode.SRC_ATOP));
        }
       canvas.save();
       canvas.translate(getPaddingLeft() - getThumbOffset(), getPaddingTop());

       mFocusIndicator.setBounds(getThumb().getBounds());
       mFocusIndicator.draw(canvas);

       canvas.restore();
    }
}
