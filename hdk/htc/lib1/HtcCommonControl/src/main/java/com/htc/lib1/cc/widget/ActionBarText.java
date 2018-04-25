
package com.htc.lib1.cc.widget;

import android.content.Context;

import com.htc.lib1.cc.util.CheckUtil;

/**
 * A widget can be used in Htc action bar.
 *
 * @deprecated Because of {@link ActionBarText} is all the same with {@link ActionBarDropDown} , and
 *             the only difference is the Focusable&Clickable&Background.you can use
 *             {@link ActionBarDropDown} to instead of {@link ActionBarText} by the following code:
 *
 *             <pre class="prettyprint">
 * ActionBarDropDown actionBarDropDown = new ActionBarDropDown(context);
 * actionBarDropDown.setClickable(false);
 * actionBarDropDown.setFocusable(false);
 * actionBarDropDown.setBackground(null);
 * </pre>
 */
public class ActionBarText extends ActionBarDropDown {
    /**
     * EXTERNAL mode.
     *
     * @deprecated please use {@link ActionBarDropDown#MODE_EXTERNAL} instead.
     */
    public static final int MODE_EXTERNAL = 1;
    /**
     * AUTOMOTIVE mode.
     *
     * @deprecated please use {@link ActionBarDropDown#MODE_AUTOMOTIVE} instead.
     */
    public static final int MODE_AUTOMOTIVE = 2;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param mode contruct with specific mode
     */
    public ActionBarText(Context context, int mode) {
        this(context);

        if (mode == MODE_AUTOMOTIVE) {
            setSupportMode(MODE_AUTOMOTIVE);
            setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }
    }

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public ActionBarText(Context context) {
        super(context);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        setClickable(false);
        setFocusable(false);
        setBackground(null);
    }
}
