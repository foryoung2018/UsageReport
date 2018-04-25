package com.htc.lib1.cc.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.htc.lib1.cc.R;

/**
 * HtcCheckBox
 */
public class HtcCheckBox extends HtcCompoundButton {
    private static Bitmap[] states;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.

     */
    public HtcCheckBox(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     *
     * <p>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.

     * @param attrs The attributes of the XML tag that is inflating the view.
     * @see #View(Context, AttributeSet, int)
     */
    public HtcCheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of View allows subclasses to use their own base style when
     * they are inflating. For example, a Button class's constructor would call
     * this version of the super class constructor and supply
     * <code>R.attr.buttonStyle</code> for <var>defStyle</var>; this allows
     * the theme's button style to modify all of the base view attributes (in
     * particular its background) as well as the Button class's attributes.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.

     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyle The default style to apply to this view. If 0, no style
     *        will be applied (beyond what is included in the theme). This may
     *        either be an attribute resource, whose value will be retrieved
     *        from the current theme, or an explicit style resource.
     * @see #View(Context, AttributeSet)
     */
    public HtcCheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    /**
     * Constructor to indicate the background mode.
     * @param context The Context the view is running in.
     * @param backgroundMode The background mode, default is Light mode.
     */
    public HtcCheckBox(Context context, int backgroundMode) {
        super(context, backgroundMode, true, true);
        mBackgroundMode = backgroundMode;
        init(context, null, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        mIsContentMultiplyRequired = true;
        mHasOnState = true;
        setButtonDrawables(context, attrs, defStyle);
        mIsPartialSelect = false;
        mSkipFirstUpDraw = true; //Add by Ahan due to disable animation
        mTheSameWithPressOn = true;
    }

    /**
     * To set partial selection mode with this HtcCheckBox enable/disable.
     * @param enable True to enable, false to disable.
     */
    public void setPartialSelection(boolean enable) {
        if (mIsPartialSelect != enable) {
            mIsPartialModeEnabled = enable;
            mIsPartialSelect = enable;

            if (enable) {
                mPartialSelection = mContentPress;
                if (mPartialSelection != null) {
                    mPartialSelection.mutate();
                    mPartialSelection.setColorFilter(mCategoryColor, PorterDuff.Mode.SRC_ATOP);
                }
            }

            setChecked(enable);
        }
    }

    /**
     *  Hide Automatically by SDK Team [U12000]
     *  @hide
     */
    @Override
    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        super.onPopulateAccessibilityEvent(event);
    }

    /**
     * @hide
     */
    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(android.widget.CheckBox.class.getName());
    }

    /**
     * @hide
     */
    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(android.widget.CheckBox.class.getName());
    }

    /**
     * To set the resource id for the assets used by this Button.
     * @param backgroundOuter The outer background.
     * @param backgroundPress The press-state background, it is usually used to apply the overlay/multiply color on.
     * @param background The inner background.
     * @param contentRest The rest foreground.
     * @param contentOn The on foreground.
     * @param partialSelect The partial selection mode foreground.
     * @deprecated [Not use any longer] Please do not use this API anymore, it will be removed on S50.
     */
    /**@hide*/
    public void setButtonDrawableResources(int backgroundOuter, int backgroundPress, int background, int contentRest, int contentOn, int partialSelect) {
        mPartialSelection = partialSelect == 0 ? null : getContext().getResources().getDrawable(partialSelect);
        if (mPartialSelection != null) {
            mPartialSelection.mutate();
            mPartialSelection.setColorFilter(mCategoryColor, PorterDuff.Mode.SRC_ATOP);
        }
        setButtonDrawablesInner(backgroundOuter, backgroundPress, background, contentRest, contentOn);
    }

    /**
     * To Set the Drawables for the assets used by this Button.
     * @deprecated [module internal use] Please do not use this API directly, it will become invisible on S50.
     * @param context The Context the view is running in.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyle The default style to apply to this view.
     */
    /**@hide*/
    public void setButtonDrawables(Context context, AttributeSet attrs, int defStyle) {
        Drawable[] drawables = loadSkinDrawables(context, attrs, defStyle, mBackgroundMode);

        super.setButtonDrawables(drawables[0], drawables[1], drawables[2], drawables[4], drawables[3]);

        if (mContentPress != null) mContentPress.clearColorFilter();
        if (mBackgroundRest != null) mBackgroundRest.setColorFilter(mCategoryColor, PorterDuff.Mode.SRC_ATOP);
    }

    private static Drawable[] loadSkinDrawables(Context context, AttributeSet attrs, int defStyle, int mode) {
        Drawable[] drs = new Drawable[5];

        if (context == null) throw new IllegalArgumentException("[HtcCheckBox.loadSkinDrawables] Null context passed in");

        switch (mode) {
            case HtcButtonUtil.BACKGROUND_MODE_AUTOMOTIVELIGHT:
            case HtcButtonUtil.BACKGROUND_MODE_AUTOMOTIVEDARK:
                drs[0] = context.getResources().getDrawable(R.drawable.automotive_common_circle_pressed);
                drs[1] = context.getResources().getDrawable(R.drawable.automotive_common_checkbox_rest);
                drs[2] = null;
                drs[3] = context.getResources().getDrawable(R.drawable.automotive_common_checkbox_on);
                drs[4] = context.getResources().getDrawable(mode==HtcButtonUtil.BACKGROUND_MODE_AUTOMOTIVEDARK ? R.drawable.automotive_common_b_checkbox_rest : R.drawable.automotive_common_checkbox_rest);
                //drs[5] = null;
                break;
            default: //Light and Dark mode
                drs[0] = HtcButtonUtil.getButtonDrawable(context, attrs, defStyle, HtcButtonUtil.BTNASSET_COMMON_CIRCLE_PRESSED);
                drs[1] = HtcButtonUtil.getButtonDrawable(context, attrs, defStyle, HtcButtonUtil.BTNASSET_COMMON_CHECKBOX_REST);
                drs[2] = null;
                drs[3] = HtcButtonUtil.getButtonDrawable(context, attrs, defStyle, HtcButtonUtil.BTNASSET_COMMON_CHECKBOX_ON);
                drs[4] = HtcButtonUtil.getButtonDrawable(context, attrs, defStyle, mode==HtcButtonUtil.BACKGROUND_MODE_DARK ? HtcButtonUtil.BTNASSET_COMMON_B_CHECKBOX_REST : HtcButtonUtil.BTNASSET_COMMON_CHECKBOX_REST);
                //drs[5] = HtcButtonUtil.getButtonDrawable(context, attrs, defStyle, HtcButtonUtil.BTNASSET_COMMON_CHECKBOX_PARTIAL);
                break;
        }

        return drs;
    }

    //[Ahan][2012/09/26][Override draw related methods to fit S50 design]
    /**
     * Describe how this button draws its press-on state.
     * @param canvas The canvas on which the press-state will be drawn.
     */
    @Override
    protected void drawPressOn(Canvas canvas) {
        drawPressed(canvas);
        drawFgOn(canvas);
    }

    /**
     * Describe how this button draws its outer background.
     * @param canvas The canvas on which the outer background will be drawn.
     */
    @Override
    protected void drawOuter(Canvas canvas) {
        if (mIsPartialModeEnabled) {
            if (mContentRest != null) {
                mContentRest.setAlpha(HtcButtonUtil.MAX_ALPHA);
                mContentRest.draw(canvas);
            }
        } else if (mBackgroundRest != null && isChecked() && !mIsAnimating && !mUnCheckUpAnimating) {
            mBackgroundRest.setAlpha(HtcButtonUtil.MAX_ALPHA);
            mBackgroundRest.draw(canvas);
        }
    }

    /**
     * Describe how this button draws its on foreground.
     * @param canvas The canvas on which the on foreground will be drawn.
     */
    @Override
    protected void drawFgOn(Canvas canvas) {
        if (mIsPartialModeEnabled) {
            drawPartialSelection(canvas);
        } else if (mContentPress != null && !mUnCheckUpAnimating) {
            if (isChecked() && !mIsAnimating) {
                mContentPress.clearColorFilter();
            } else if (mIsAnimating) {
                mContentPress.setAlpha(HtcButtonUtil.BASE_ALPHA);
                mContentPress.setColorFilter(mMultiplyColor, PorterDuff.Mode.SRC_ATOP);
            }
            mContentPress.draw(canvas);
        } else mUnCheckUpAnimating = false;
    }

    /**
     * Describe how this button draws its rest foreground.
     * @param canvas The canvas on which the rest foreground will be drawn.
     */
    @Override
    protected void drawFgRest(Canvas canvas) {
        if (mIsPartialModeEnabled) {
            drawPartialSelection(canvas);
        } else if (mContentRest != null) {
            mContentRest.setAlpha(HtcButtonUtil.BASE_ALPHA);
            mContentRest.draw(canvas);
        }
    }

    private void drawPartialSelection(Canvas canvas) {
        if (mPartialSelection != null) {
            mPartialSelection.setAlpha(HtcButtonUtil.BASE_ALPHA);
            mPartialSelection.draw(canvas);
        }
    }
    //[Ahan][2012/09/26]

    //Add by Ahan to avoid draw multiple times to GPU 2012/07/23
    /**
     * To get Bitmap array for all states of this button.
     * @param context The Context the view is running in
     * @deprecated [Not use any longer] This static API will be removed
     */
    /**@hide*/
    public static Bitmap[] compositeBitmap(Context context) {
        if (context == null) throw new IllegalArgumentException("[HtcCheckBox.compositeBitmap] context is null.");
        Drawable[] tmp = loadSkinDrawables(context, null, 0, HtcButtonUtil.BACKGROUND_MODE_LIGHT);
        return (tmp == null ? null : HtcCompoundButton.compositeBitmap(context, tmp, false));
    }

    //[Ahan][2012/09/26][Add for setDrawOnce API use]
    private static Bitmap[] compositeBitmap(Context context, Drawable[] drs) {
        Drawable tmpDrawable = null;
        Bitmap[] tmp = new Bitmap[STATES_COUNT];
        Drawable[] myDrs = new Drawable[DRAWABLE_COUNTS];
        Canvas tmpCanvas = new Canvas();

        if (context == null || drs == null)
            throw new IllegalArgumentException("[HtcCheckBox.compositeBitmap] Context or Drawable[] drs is null");

        if (drs.length == DRAWABLE_COUNTS) myDrs = drs;
        else throw new IllegalArgumentException("[HtcCheckBox.compositeBitmap] Length of drs is uncorrect, length=>"+drs.length);

        //set Drawables
        for (int i=0; i<DRAWABLE_COUNTS; i++) {
            if (myDrs[i] != null) {
                myDrs[i].mutate();
                // find fisrt drawable which is not null for size reference
                if (tmpDrawable == null) tmpDrawable = myDrs[i];
            }
        }

        if (tmpDrawable == null)
            throw new IllegalArgumentException("[HtcCheckBox.compositeBitmap] All elements in drs are all null");

        for (int i=0; i<STATES_COUNT; i++)
            tmp[i] = Bitmap.createBitmap(tmpDrawable.getIntrinsicWidth(), tmpDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        int overlay = HtcButtonUtil.getOverlayColor(context, null);
        if (myDrs[0] != null) myDrs[0].setColorFilter(overlay, PorterDuff.Mode.SRC_ATOP);    //Background Press
        if (myDrs[1] != null) myDrs[1].setColorFilter(overlay, PorterDuff.Mode.SRC_ATOP);    //Content press state

        //use normal size to composite first
        HtcCompoundButton.setCompositeScale(1.0f, drs, tmpDrawable);

        //Rest - Off, just draw the rest-off asset
        tmpCanvas.setBitmap(tmp[0]);
        if (myDrs[4] != null) {
            myDrs[4].draw(tmpCanvas);
        }

        //Rest - On, some controls need draw multiple assets for this state, ex: HtcCheckBox
        tmpCanvas.setBitmap(tmp[1]);
        if (myDrs[0] != null && myDrs[3] != null) {
            myDrs[0].draw(tmpCanvas);
            myDrs[3].draw(tmpCanvas);
        }

        //then composite scaled bitmaps
        HtcCompoundButton.setCompositeScale(0.9f, drs, tmpDrawable);
        if (myDrs[3] != null) myDrs[3].setColorFilter(overlay, PorterDuff.Mode.SRC_ATOP);

        //Pressed - Off, just draw the pressed-off asset
        tmpCanvas.setBitmap(tmp[2]);
        if (myDrs[1] != null && myDrs[3] != null) {
            myDrs[1].draw(tmpCanvas);
            myDrs[3].draw(tmpCanvas);
        }

        //Pressed - On, just draw the pressed-on asset
        tmpCanvas.setBitmap(tmp[3]);
        if (myDrs[1] != null && myDrs[3] != null) {
            myDrs[1].draw(tmpCanvas);
            myDrs[3].draw(tmpCanvas);
        }

        return tmp;
    }

    /**
     * This API is used to make HtcCheckbox composite Bitmaps for all state and just draw 1 bitmap for each state instead of drawing 3 or 4 drawables for each state and it just support Light mode now.
     * @param reComposite True to enable DrawOnce mode, false to disable.
     * @param custom Takes no effects now, just pass null in.
     */
    @Override
    public void setDrawOnce(boolean reComposite, Drawable[] custom) {
        mDrawOnce = true;

        if (states == null || reComposite == true) {
            Drawable[] tmp = new Drawable[5];

            tmp[0] = (mBackgroundRest != null ? mBackgroundRest : null);
            tmp[1] = (mBackgroundPress != null ? mBackgroundPress : null);
            tmp[2] = (mInnerBackground != null ? mInnerBackground : null);
            tmp[3] = (mContentPress != null ? mContentPress : null);
            tmp[4] = (mContentRest != null ? mContentRest : null);

            states = compositeBitmap(getContext(), tmp);
        }
    }

    /**
     * To get the Bitmap array for all states of this button, be sure it is in DrawOnce mode before calling this method.
     * @return Bitmap array for all states of this button, if it is not in DrawOnce mode, it returns null.
     */
    @Override
    protected Bitmap[] getStatesBitmap() {
        return states;
    }

    /**
     * @deprecated [Not use any longer] This API will be removed on S50.
     * TODO: Remove this API on S50.
     */
    /**@hide*/
    public void stopDrawOnce() {
        mDrawOnce = false;
        states = null;
    }
}
