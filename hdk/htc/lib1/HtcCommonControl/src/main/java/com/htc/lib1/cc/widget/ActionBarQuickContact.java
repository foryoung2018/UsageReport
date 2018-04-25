
package com.htc.lib1.cc.widget;

import android.view.ViewGroup;
import android.view.ViewDebug.ExportedProperty;
import android.content.Context;
import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.ActionBarUtil;
import com.htc.lib1.cc.util.CheckUtil;
import com.htc.lib1.cc.util.res.HtcResUtil;

/**
 * A widget can be used in Htc action bar.
 */
public class ActionBarQuickContact extends QuickContactBadge {

    @ExportedProperty(category = "CommonControl")
    private boolean mEnableStartMargin = true;
    @ExportedProperty(category = "CommonControl")
    private int mWidth = 0;
    @ExportedProperty(category = "CommonControl")
    private int mHeight = 0;
    @ExportedProperty(category = "CommonControl")
    private int mMeasureSpecM2;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public ActionBarQuickContact(Context context) {
        super(context);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        mWidth = mHeight = getResources().getDimensionPixelSize(R.dimen.ab_photoframe_size);
        mMeasureSpecM2 = HtcResUtil.getM2(context);

        setDarkMode();
    }

    /**
     * @hide
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth, mHeight);
    }

    /**
     * Hide Automatically by SDK Team [U12000].
     *
     * @hide
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateLayout();
    }

    private void updateLayout() {
        // reset the layout margin environment
        ViewGroup.MarginLayoutParams lparams = (ViewGroup.MarginLayoutParams) getLayoutParams();
        if (null != lparams) {
            if (ActionBarUtil.IS_SUPPORT_RTL) {
                lparams.setMarginEnd(mMeasureSpecM2);
                lparams.setMarginStart(mEnableStartMargin ? mMeasureSpecM2 : 0);
            } else {
                lparams.rightMargin = mMeasureSpecM2;
                lparams.leftMargin = mEnableStartMargin ? mMeasureSpecM2 : 0;
            }
            setLayoutParams(lparams);
        }
    }

    /**
     * Use to enable left margin.
     *
     * @param enabled the enabled state of left margin
     * @deprecated [Not use any longer]
     */
    public void setLeftMarginEnabled(boolean enabled) {
        mEnableStartMargin = enabled;
        updateLayout();
    }
}
