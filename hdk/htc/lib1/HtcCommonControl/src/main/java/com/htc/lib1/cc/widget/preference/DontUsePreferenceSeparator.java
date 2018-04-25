
package com.htc.lib1.cc.widget.preference;

import com.htc.lib1.cc.util.CheckUtil;
import com.htc.lib1.cc.widget.HtcListItemSeparator;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 *
 * @deprecated Common Control internal used
 * Only use for replacing Preference by HtcLIstItem.
 * This can't be used by applications.
 * @hide
 */
@Deprecated
public class DontUsePreferenceSeparator extends HtcListItemSeparator {

    /**
     * Set View Resource ID.
     * Set the TextView Resource ID to Google'ID.
     * @return none
     */
    private void init() {
        TextView v = (TextView) getChildAt(0);
        if (null != v) {
            v.setId(android.R.id.title);
        }
    }

    /**
     * Simple constructor to use when creating this widget from code. It will
     * new a DontUsePreferenceSeparator with default style.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc.
     */
    /** @hide */
    public DontUsePreferenceSeparator(Context context) {
        super(context);
        CheckUtil.isUIThread(context);
        CheckUtil.isContextThemeWrapper(context);
        init();
    }

    /**
     * Constructor that is called when inflating this widget from code. It will
     * new a DontUsePreferenceSeparator with specified style, mode.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc.
     * @param mode to indicate item mode for HtcListItem.
     */
    /** @hide */
    public DontUsePreferenceSeparator(Context context, int textMode) {
        super(context,textMode);
        CheckUtil.isUIThread(context);
        CheckUtil.isContextThemeWrapper(context);
        init();
    }

    /**
     * Constructor that is called when inflating this widget from XML. This is
     * called when a view is being constructed from an XML file, supplying
     * attributes that were specified in the XML file.It will new a
     * DontUsePreferenceSeparator with default style.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating this widget.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public DontUsePreferenceSeparator(Context context, AttributeSet attrs) {
        super(context, attrs);
        CheckUtil.isUIThread(context);
        CheckUtil.isContextThemeWrapper(context);
        init();
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of this widget allows subclasses to use their own base style
     * when they are inflating. It will a widget with style defStyle.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating this widget.
     * @param defStyle The default style to apply to this widget.
     * @deprecated [Module internal use]
     */
    /** @hide */
    public DontUsePreferenceSeparator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        CheckUtil.isUIThread(context);
        CheckUtil.isContextThemeWrapper(context);
        init();
    }

    /**
     * Constructor that is called when inflating this widget from code. It will
     * new a DontUsePreferenceSeparator with specified style, item mode, and text
     * mode.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc.
     * @param mode to indicate item mode for HtcListItem.
     */
    /** @hide */
    public DontUsePreferenceSeparator(Context context, int itemMode, int textMode) {
        super(context, itemMode, textMode);
        CheckUtil.isUIThread(context);
        CheckUtil.isContextThemeWrapper(context);
        init();
    }
}
