package com.htc.lib1.cc.widget.preference;

import com.htc.lib1.cc.util.CheckUtil;
import com.htc.lib1.cc.widget.HtcListItemColorIcon;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
*
* @deprecated Common Control internal used
* Only use for replacing Preference by HtcLIstItem.
* This can't be used by applications.
* @hide
*/
@Deprecated
public class DontUsePreferenceColorIcon extends HtcListItemColorIcon{

    /**
     * Set View Resource ID
     *    Set the ImageView Resource ID to Google'ID
     * @return none
     */
    private void init() {
        ImageView v = (ImageView) getChildAt(1);
        if (null != v) {
            v.setId(android.R.id.icon);
        }
    }

    /**
     * Simple constructor to use when creating a DontUsePreferenceColorIcon from code.
     *
     * @param context The Context the DontUsePreferenceColorIcon is running in,
     *            through which it can access the current theme, resources, etc.
     */
    /** @hide */
    public DontUsePreferenceColorIcon(Context context) {
        super(context);
        CheckUtil.isUIThread(context);
        CheckUtil.isContextThemeWrapper(context);
        init();
    }

    /**
     * Constructor that is called when inflating a view from XML.
     *
     * @param context The Context the DontUsePreferenceColorIcon is running in,
     *            through which it can access the current theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the
     *            DontUsePreferenceColorIcon.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public DontUsePreferenceColorIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        CheckUtil.isUIThread(context);
        CheckUtil.isContextThemeWrapper(context);
        init();
    }

    /**
     * Perform inflation from XML and apply a class-specific base style.
     *
     * @param context The Context the DontUsePreferenceColorIcon is running in,
     *            through which it can access the current theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the
     *            DontUsePreferenceColorIcon.
     * @param defStyle The default style to apply to this DontUsePreferenceColorIcon.
     *            If 0, no style will be applied (beyond what is included in the
     *            theme). This may either be an attribute resource, whose value
     *            will be retrieved from the current theme, or an explicit style
     *            resource.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public DontUsePreferenceColorIcon(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        CheckUtil.isUIThread(context);
        CheckUtil.isContextThemeWrapper(context);
        init();
    }
}
