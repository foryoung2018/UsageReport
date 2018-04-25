package com.htc.lib1.cc.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

import com.htc.lib1.cc.htcjavaflag.HtcDebugFlag;

/**
 * The HTC style QuickContactBadge used with HtcListItem.
 * <ul>
 * <li>Image Only
 *
 * <pre class="prettyprint">
 *      &lt;com.htc.widget.HtcListItemQuickContactBadge
 *       android:id="@+id/photo"/&gt;
 * </pre>
 *
 * </ul>
 */
public class HtcListItemQuickContactBadge extends HtcListItemImageComponent implements
        IHtcListItemComponentNoLeftTopMargin {
    private QuickContactBadge mBadge;
    private final static String LOG_TAG = "HtcListItemQuickContactBadge";

    private void init(Context context) {
        mBadge = new QuickContactBadge(context);

        super.setPadding(0, 0, 0, 0);

        addView(mBadge, 0, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        if (HtcDebugFlag.getHtcDebugFlag()) {
            Log.d("LOG_TAG", "current mode is " + mItemMode);
        }
    }

    /**
     * Simple constructor to use when creating a HtcListItemQuickContactBadge
     * from code.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcListItemQuickContactBadge(Context context) {
        super(context);
        init(context);
    }

    /**
     * Constructor that is called when inflating a view from XML.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the
     *            HtcListItemQuickContactBadge.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItemQuickContactBadge(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the
     *            HtcListItemQuickContactBadge.
     * @param defStyle The default style to apply to this
     *            HtcListItemQuickContactBadge. If 0, no style will be applied
     *            (beyond what is included in the theme). This may either be an
     *            attribute resource, whose value will be retrieved from the
     *            current theme, or an explicit style resource.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public HtcListItemQuickContactBadge(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void setLayoutParams(ViewGroup.LayoutParams params) {

        params.width = mComponentWidth;
        params.height = mComponentHeight;

        super.setLayoutParams(params);
    }

    /**
     * Get the LayoutParams associated with this HtcListItemQuickContactBadge.
     *
     * @return The LayoutParams associated with this view, or null if no
     *         parameters have been set yet
     */
    public ViewGroup.LayoutParams getLayoutParams() {
        if (super.getLayoutParams() != null)
            return super.getLayoutParams();
        else {
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(mComponentWidth,
                    mComponentHeight);
            super.setLayoutParams(params);
            return params;
        }
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    public void setPadding(int left, int top, int right, int bottom) {
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @Override
    protected void onMeasure(int w, int h) {
        measureChild(mBadge, w, h);
        super.onMeasure(w, h);
        setMeasuredDimension(mComponentWidth, mComponentHeight);
    }

    /**
     * Hide Automatically by SDK Team [U12000]
     *
     * @hide
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mBadge.layout(0, 0, mComponentWidth, mComponentHeight);
        super.onLayout(changed, l, t, r, b);
    }

    /**
     * @return QuickContactBadge
     */
    public QuickContactBadge getBadge() {
        return mBadge;
    }
}
