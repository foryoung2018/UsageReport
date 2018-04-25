/**
 * For ActionBar, the primary TextView.
 */

package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.widget.TextView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.CheckUtil;

/**
 * @author felka
 * @hide
 */
final class ActionBarTextView extends TextView {

    static final int UNSPECIFICED = -1;
    static final int PRIMARY_DEFAULT_ONLY = 0x10000000;
    static final int PRIMARY_DEFAULT_TWO_TEXTVIEW = 0x10000001;
    static final int PRIMARY_UPDATE_ONLY = 0x10000002;
    static final int PRIMARY_PULLDOWN_TWO_TEXTVIEW = 0x10000003;
    static final int PRIMARY_AUTOMTIVE_ONLY = 0x10000004;
    static final int PRIMARY_AUTOMTIVE_TWO_TEXTVIEW = 0x10000005;
    static final int PRIMARY_MULTILINE_ONLY = 0x10000006;
    static final int PRIMARY_PULLDOWN = 0x10000007;

    static final int SECONDARY_DEFAULT = 0x20000000;
    static final int SECONDARY_UPDATE = 0x20000001;
    static final int SECONDARY_PULLDOWN = 0x20000002;
    static final int SECONDARY_TRANSPARENT = 0x20000003;
    static final int SECONDARY_AUTOMOTIVE = 0x20000004;
    static final int COUNTER_FOLLOW_PRIMARY = 0X2000005;
    static final int COUNTER_FOLLOW_SECONDARY = 0X2000006;
    static final int COUNTER_VERTICAL_CENTER = 0X2000007;
    static final int COUNTER_FOLLOW_PRIMARY_AUTOMOTIVE = 0X2000008;
    static final int COUNTER_FOLLOW_SECONDARY_AUTOMOTIVE = 0X2000009;

    @ExportedProperty(category = "CommonControl", resolveId = true)
    private int mFontStyle = 0;
    @ExportedProperty(category = "CommonControl")
    private int mMaxLines = 1;

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = UNSPECIFICED, to = "UNSPECIFICED"),
            @IntToString(from = PRIMARY_DEFAULT_ONLY, to = "PRIMARY_DEFAULT_ONLY"),
            @IntToString(from = PRIMARY_DEFAULT_TWO_TEXTVIEW, to = "PRIMARY_DEFAULT_TWO_TEXTVIEW"),
            @IntToString(from = PRIMARY_UPDATE_ONLY, to = "PRIMARY_UPDATE_ONLY"),
            @IntToString(from = PRIMARY_PULLDOWN_TWO_TEXTVIEW, to = "PRIMARY_PULLDOWN_TWO_TEXTVIEW"),
            @IntToString(from = PRIMARY_AUTOMTIVE_ONLY, to = "PRIMARY_AUTOMTIVE_ONLY"),
            @IntToString(from = PRIMARY_AUTOMTIVE_TWO_TEXTVIEW, to = "PRIMARY_AUTOMTIVE_TWO_TEXTVIEW"),
            @IntToString(from = PRIMARY_MULTILINE_ONLY, to = "PRIMARY_MULTILINE_ONLY"),
            @IntToString(from = PRIMARY_PULLDOWN, to = "PRIMARY_PULLDOWN"),
            @IntToString(from = SECONDARY_DEFAULT, to = "SECONDARY_DEFAULT"),
            @IntToString(from = SECONDARY_UPDATE, to = "SECONDARY_UPDATE"),
            @IntToString(from = SECONDARY_PULLDOWN, to = "SECONDARY_PULLDOWN"),
            @IntToString(from = SECONDARY_TRANSPARENT, to = "SECONDARY_TRANSPARENT"),
            @IntToString(from = SECONDARY_AUTOMOTIVE, to = "SECONDARY_AUTOMOTIVE")
    })
    private int mState = UNSPECIFICED;

    private void init(boolean isMultiline) {
        if (isMultiline) {
            setEllipsize(android.text.TextUtils.TruncateAt.END);
            setSingleLine(false);
            setMaxLines(2);
            setHorizontalFadingEdgeEnabled(false);
        } else {
            setEllipsize(null);
            setSingleLine(true);
            setMaxLines(1);
            setHorizontalFadingEdgeEnabled(true);
        }
    }

    private void init() {
        init(false);
    }

    /**
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @hide
     */
    public ActionBarTextView(Context context) {
        super(context);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        init();
    }

    /**
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs
     * @hide
     */
    public ActionBarTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        init();
    }

    /**
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs
     * @param defStyle
     * @hide
     */
    public ActionBarTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        init();
    }

    @ExportedProperty(category = "CommonControl", resolveId = true)
    protected final int getStyleResId() {
        init();
        switch (mState) {
            case PRIMARY_DEFAULT_ONLY:
                return R.style.ActionBarPrimaryTextView;
            case PRIMARY_DEFAULT_TWO_TEXTVIEW:
                return R.style.ActionBarPrimaryTextView_TwoLine;
            case PRIMARY_UPDATE_ONLY:
                return R.style.ActionBarPrimaryTextView_Update;
            case PRIMARY_PULLDOWN_TWO_TEXTVIEW:
                return R.style.ActionBarPrimaryTextView_PullDown_TwoLine;
            case PRIMARY_AUTOMTIVE_ONLY:
                return R.style.ActionBarPrimaryTextView_Automotive;
            case PRIMARY_AUTOMTIVE_TWO_TEXTVIEW:
                return R.style.ActionBarPrimaryTextView_Automotive_TwoLine;
            case PRIMARY_MULTILINE_ONLY:
                init(true);
                return R.style.ActionBarPrimaryTextView_Multiline;
            case PRIMARY_PULLDOWN:
                return R.style.ActionBarPrimaryTextView_PullDown;
            case SECONDARY_DEFAULT:
                return R.style.ActionBarSecondaryTextView;
            case SECONDARY_UPDATE:
                return R.style.ActionBarSecondaryTextView_Update;
            case SECONDARY_PULLDOWN:
                return R.style.ActionBarSecondaryTextView_PullDown;
            case SECONDARY_TRANSPARENT:
                return R.style.ActionBarSecondaryTextView_Transparent;
            case SECONDARY_AUTOMOTIVE:
                return R.style.ActionBarSecondaryTextView_Automotive;
            case COUNTER_FOLLOW_PRIMARY:
                return R.style.ActionBarCounter_FollowPrimary;
            case COUNTER_FOLLOW_SECONDARY:
                return R.style.ActionBarCounter_FollowSecondary;
            case COUNTER_VERTICAL_CENTER:
                return R.style.ActionBarCounter_VerticalCenter;
            case COUNTER_FOLLOW_PRIMARY_AUTOMOTIVE:
                return R.style.ActionBarCounter_FollowPrimary_Automotive;
            case COUNTER_FOLLOW_SECONDARY_AUTOMOTIVE:
                return R.style.ActionBarCounter_FollowSecondary_Automotive;
            default:
                break;
        }
        return R.style.ActionBarPrimaryTextView;
    }

    /**
     * @param mState the mState to set
     */
    protected void setState(int state) {
        if (mState == state) return;
        this.mState = state;
        updateSelf(getStyleResId());
    }

    private void updateSelf(int styleResId) {
        TypedArray a = getContext().obtainStyledAttributes(null, R.styleable.ActionBarTextView, 0, styleResId);
        mFontStyle = a.getResourceId(R.styleable.ActionBarTextView_android_textAppearance, 0);
        mMaxLines = a.getInt(R.styleable.ActionBarTextView_android_maxLines, 1);
        a.recycle();

        if (mMaxLines < 0) mMaxLines = 1;
        setMaxLines(mMaxLines);

        if (0 != mFontStyle) {
            setTextAppearance(getContext(), mFontStyle);
        }
    }

    /**
     * Text in ActionBar don't need press state.
     *
     * @see android.widget.TextView#setTextColor(android.content.res.ColorStateList)
     */
    @Override
    public void setTextColor(ColorStateList colors) {
        if (null != colors) {
            super.setTextColor(colors.getDefaultColor());
        } else {
            super.setTextColor(colors);
        }
    }
}
