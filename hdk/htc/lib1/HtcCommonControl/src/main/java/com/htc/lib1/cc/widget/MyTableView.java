package com.htc.lib1.cc.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.htc.lib1.cc.view.table.TableView;

/**
 * MyTableView
 */
public class MyTableView extends TableView {

    private boolean mEnabled;
    private static final boolean LOG = false;
    private static final String TAG = "MyTableView";

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public MyTableView(Context context) {
        super(context);
        setFocusable(false);
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
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @see #View(Context, AttributeSet, int)
     */
    public MyTableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(false);
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
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyle The default style to apply to this view. If 0, no style
     *        will be applied (beyond what is included in the theme). This may
     *        either be an attribute resource, whose value will be retrieved
     *        from the current theme, or an explicit style resource.
     * @see #View(Context, AttributeSet)
     */
    public MyTableView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setVerticalScrollBarEnabled(false);
        setFocusable(false);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setCenterView(int pos){
        super.setCenterView(pos);
                //We don't do scrollIntoSlot after setCenterView any more to avoid unexpected scrolling after layout.
        //super.scrollIntoSlots();
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setCenterView(int pos, int targetHeight){
        super.setCenterView(pos, targetHeight);
                //We don't do scrollIntoSlot after setCenterView any more to avoid unexpected scrolling after layout.
        //super.scrollIntoSlots();
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected void setSelectionInt(int position) {
    super.setSelectionInt(position);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setTableEnabled(boolean enabled) {
        mEnabled = enabled;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mEnabled) {
            return super.onTouchEvent(event);
        } else {
            return false;
        }
    }

    /**
     *  Hide Automatically by SDK Team [U12000]
     *  @hide
     */
    public void slideWithOffset(int offset) {
        reportScrollStateChange(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
        tableColleague.scrollWithConstrain(0, -offset, true);
        this.scrollIntoSlots();
        reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int childHeight = getMyTableChildHeight();

        for (int i=0; i<getChildCount(); i++) {
            final View child = (View)getChildAt(i);
            if (child.getHeight() != childHeight)
                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY));
        }
    }

    void setMyTableViewSlideOffset(int offset) {
        super.setTableViewSlideOffset(offset);
    }

    void setMyTableChildHeight(int height) {
        super.setTableChildHeight(height);
    }

    int getMyTableChildHeight() {
        return super.getTableChildHeight();
    }

    void setKeyOfMyTableView(String key) {
        super.setKeyOfTableView(key);
    }

    String getKeyOfMyTableView() {
        return super.getKeyOfTableView();
    }
}
