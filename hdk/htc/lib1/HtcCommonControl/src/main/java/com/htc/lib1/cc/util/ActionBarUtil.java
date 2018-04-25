
package com.htc.lib1.cc.util;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.view.View;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.content.res.TypedArray;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.app.OnActionModeChangedListener;
import com.htc.lib1.cc.htcjavaflag.HtcBuildFlag;
import com.htc.lib1.cc.util.res.HtcResUtil;
import com.htc.lib1.cc.widget.ActionBarExt;
import com.htc.lib1.cc.widget.ActionModeFilterLayout;
import com.htc.lib1.cc.widget.HtcAlertDialog;

import android.content.res.Resources;
import android.os.Build;
import android.util.Log;
import android.util.Xml;
import android.view.ActionMode;
import android.view.ContextThemeWrapper;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.util.AttributeSet;

public class ActionBarUtil {

    /** @hide */
    public static final boolean IS_SUPPORT_RTL = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;

    // ///////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * reload the layout parameters of view from layout
     *
     * @param r the Resources instance that can load the xml specified by layoutId
     * @param v the v will be assin new layout parameters
     * @param layoutId the ID specify the layout that has only one tag "view" in the resource. The
     *            new layout parameters load from the "view" tag
     * @hide
     **/
    public static void assignLayoutParamsFromXml(View v, int layoutId) {
        if (0 == layoutId || null == v || null == v.getResources() || null == v.getParent()) return;
        Resources r = v.getResources();

        if (!(v.getParent() instanceof ViewGroup)) return;
        ViewGroup parent = (ViewGroup) v.getParent();

        XmlPullParser parser = r.getXml(layoutId);
        if (null == parser) return;

        final AttributeSet attrs = Xml.asAttributeSet(parser);
        if (null == attrs) return;

        try {
            int type;
            while ((type = parser.next()) != XmlPullParser.START_TAG &&
                    type != XmlPullParser.END_DOCUMENT) {
                // Empty
            }
            if (type != XmlPullParser.START_TAG) {
                return;
                // throw new InflateException(parser.getPositionDescription()
                // + ": No start tag found!");
            }

            if (!("view".equals(parser.getName()))) {
                return;
                // throw new InflateException(parser.getPositionDescription()
                // + ": Not start with \"view\"!");
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        ViewGroup.LayoutParams lp = parent.generateLayoutParams(attrs);
        if (null != lp) v.setLayoutParams(lp);
    }

    /* [Start][U16][Felka] porting ActionBar Menu Size */
    /**
     * To get the width of the ActionBarItemView
     *
     * @param context the relative Context that has the Resources
     * @param isAutomotive true if the mode is automotive mode, false if the mode is not the
     *            automotive mode
     * @hide
     **/
    public static int getItemWidth(Context context, boolean isAutomotive) {
        if (null == context) return 0;

        Resources r = context.getResources();
        if (null == r) return 0;

        return isOdd((int) r.getFraction(isAutomotive ? R.fraction.ab_automotive_item_width_percent : R.fraction.ab_item_width_percent,
                Math.min(r.getDisplayMetrics().widthPixels, r.getDisplayMetrics().heightPixels), 1));
    }

    /**
     * To get the width of the ActionBarItemView(not automotive mode)
     *
     * @param context the relative Context that has the Resources
     * @hide
     **/
    public static int getItemWidth(Context context) {
        return getItemWidth(context, false);
    }

    public static int getActionBarHeight(Context context, boolean isAutomotive) {
        if (null == context) return -1;

        Resources r = context.getResources();
        if (null == r) return -1;

        if (isAutomotive) return r.getDimensionPixelSize(R.dimen.ab_automotive_height);

        int height = -1;
        TypedArray a = context.obtainStyledAttributes(null,
                R.styleable.ActionBarSize, android.R.attr.actionBarSize, 0);
        height = a.getDimensionPixelSize(
                R.styleable.ActionBarSize_android_actionBarSize, 0);
        a.recycle();

        return height;
    }

    private static int isOdd(int width) {
        return ((width & 1) == 0) ? width : ++width;
    }

    /**
     * @hide
     */
    public static Drawable getActionMenuItemBackground(Context context) {
        if (null == context) return null;

        int[] attrs = {
                android.R.attr.background
        };
        TypedArray a = context.obtainStyledAttributes(null, attrs, android.R.attr.actionButtonStyle, 0);
        Drawable d = a.getDrawable(0);
        a.recycle();
        return d;
    }

    /* [End][U16][Felka] porting ActionBar Menu Size */

    /**
     * for ActionBarContainer and ActionBarExt get the theme color and panelFullBackground or
     * fullscreen background when transparent is true background is panelFullBackground when
     * fullscreen is true background is android_drawable default is theme color
     *
     * @hide
     */
    public static Drawable getActionBarBackground(Context context, int mode) {
        if (null == context || null == context.getResources()) return null;

        if (!(context instanceof ContextThemeWrapper)) android.util.Log.e("ActionBarUtil", "context" + context + "is not ContextThemeWrapper ");

        Drawable d = null;

        TypedArray a = context.obtainStyledAttributes(null,
                R.styleable.HtcActionBar, R.attr.actionBarStyle, 0);
        if (mode == ActionBarExt.MODE_TRANSPARENT) {
            // transparent is true background
            d = a.getDrawable(R.styleable.HtcActionBar_android_panelFullBackground);
        } else if (mode == ActionBarExt.MODE_GRADIENT_TRANSPARENT) {
            // fullscreen is true backgound
            d = a.getDrawable(R.styleable.HtcActionBar_android_drawable);
        } else {
            // theme color
            d = a.getDrawable(R.styleable.HtcActionBar_android_background);
        }
        a.recycle();
        return d;
    }

    /** @hide */
    public static int getChildUsedWidth(int childWidth, MarginLayoutParams childParams) {
        if (IS_SUPPORT_RTL) {
            return childWidth + childParams.getMarginStart() + childParams.getMarginEnd();
        } else {
            return childWidth + childParams.leftMargin + childParams.rightMargin;
        }
    }

    /** @hide */
    public static void setChildFrame(View view, int left, int top, int childWidth, int childHeight) {
        view.layout(left, top, left + childWidth, top + childHeight);
    }

    /** @hide */
    public static int getChildLeft(int parentWidth, int usedWidth, int childStart, int childWidth, boolean isLayoutRtl) {
        return isLayoutRtl ? parentWidth - usedWidth - childStart - childWidth : childStart + usedWidth;
    }

    /** @hide */
    public static int getStartMarginForPlatform(MarginLayoutParams layoutParams) {
        if (IS_SUPPORT_RTL) {
            return layoutParams.getMarginStart();
        } else {
            return layoutParams.leftMargin;
        }
    }

    /** @hide */
    public static void measureActionBarTextView(View view, int width, int useWidth, boolean needFadingEdge) {
        final int leftWidth = Math.max(0, width - useWidth);

        int primaryWidthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int primaryHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

        view.measure(primaryWidthMeasureSpec, primaryHeightMeasureSpec);

        if (view.getMeasuredWidth() > leftWidth) {
            primaryWidthMeasureSpec = MeasureSpec.makeMeasureSpec(leftWidth, MeasureSpec.EXACTLY);
            view.measure(primaryWidthMeasureSpec, primaryHeightMeasureSpec);
            view.setHorizontalFadingEdgeEnabled(needFadingEdge);
        } else {
            view.setHorizontalFadingEdgeEnabled(false);
        }
    }
    /**
     * This method recommend called first once you get the actionMode or after called
     * actionMode.setCustomView(View)
     * <p>
     * Sample1:
     *
     * <pre class="prettyprint">
     * View.startActionMode(new ActionMode.Callback() {
     *     &#064;Override
     *     public boolean onCreateActionMode(ActionMode mode, Menu menu) {
     *         ActionBarUtil.setActionModeBackground(this, mode, new ColorDrawable(Color.GRAY));
     *         return false;
     *     }
     * });
     *
     * </pre>
     *
     * </p>
     * <p>
     * </p>
     * <p>
     * Sample2:
     *
     * <pre class="prettyprint">
     * public class ActionModeActivity extends Activity {
     *     &#064;Override
     *     public void onActionModeStarted(ActionMode mode) {
     *         ActionBarUtil.setActionModeBackground(this, mode, new ColorDrawable(Color.GRAY));
     *     }
     * }
     * </pre>
     *
     * </p>
     *
     * @param context the context you are running in,should be a ContextThemeWrapper
     * @param actionMode the ActionMode you have got
     * @param backgroundDrawable the drawable which will be set to the ActionMode view background
     */
    public static void setActionModeBackground(ContextThemeWrapper context, ActionMode actionMode, Drawable backgroundDrawable) {
        if (context == null) {
            if (HtcBuildFlag.Htc_DEBUG_flag) Log.e("HTCActionBar", "context is null", new Exception());
            return;
        }

        if (actionMode == null) {
            if (HtcBuildFlag.Htc_DEBUG_flag) Log.e("HTCActionBar", "actionMode is null", new Exception());
            return;
        }

        View oldCustomView = actionMode.getCustomView();
        if (oldCustomView == null || oldCustomView.getParent() == null) {
            View newCustomView = new View(context);
            actionMode.setCustomView(newCustomView);
            setViewParentBackground(newCustomView, backgroundDrawable);
            actionMode.setCustomView(oldCustomView);

            if (actionMode.getTitle() != null) {
                actionMode.setTitle(actionMode.getTitle());
            }
            if (actionMode.getSubtitle() != null) {
                actionMode.setSubtitle(actionMode.getSubtitle());
            }
        } else {
            setViewParentBackground(oldCustomView, backgroundDrawable);
        }
    }

    private static void setViewParentBackground(View view, Drawable backgroundDrawable) {
        ViewParent viewParent = view.getParent();
        if (viewParent == null) return;
        ((View) viewParent).setBackground(backgroundDrawable);
    }

    /**
     * @hide
     * @deprecated [module internal use]
     */
    @Deprecated
    public static int getActionBarBackupViewWidth(Context context, boolean isAutomotive) {
        if (isAutomotive) {
            return ActionBarUtil.getItemWidth(context, true);
        } else {
            return context.getResources().getDrawable(R.drawable.icon_btn_previous_dark).getIntrinsicWidth() + HtcResUtil.getM2(context) * 2;
        }
    }

    /**
     * This method supports users to register {@link OnActionModeChangedListener} for google Dialog
     * only (such as {@link Dialog} ,{@link AlertDialog} ,etc.) !
     * <p>
     * After you got the dialog instance ,you can use this method to register
     * OnActionModeChangedListener to receive ActionModeChange events.<b>Please call this method
     * follow closely to {@linkplain Dialog#show() Dialog.show()},and do it only once until your
     * dialog instance to be null.</b>
     * </p>
     * <p>
     * <b>This is the final solution for the dialog you can not completely control,such as the
     * dialog in EditTextPreference.Otherwise please consider and refer
     * {@link HtcAlertDialog#setOnActionModeChangedListener(OnActionModeChangedListener)} or
     * {@link ActionModeFilterLayout}.</b>
     * </p>
     * <p>
     * Sample :
     *
     * <pre class="prettyprint">
     * OnActionModeChangedListener mActionModeChangedListener = new OnActionModeChangedListener() {
     *     &#064;Override
     *     public void onActionModeStarted(ActionMode mode) {
     *         ActionBarUtil.setActionModeBackground(context, mode, TheDrawableYouWantToSet);
     *     }
     * };
     * Dialog dialog = getDialog();
     * dialog.show();
     * ActionBarUtil.wrapActionModeChangeForDialog(dialog, mActionModeChangedListener); // follow closely to {@link Dialog#show()},and do it only once until your dialog instance to be null.
     * ...
     * </pre>
     *
     * </p>
     *
     * @param dialog the dialog you want to register onActionModeChangedListener,should not be null.
     * @param onActionModeChangedListener should not be null.
     * @see OnActionModeChangedListener
     * @see ActionModeFilterLayout
     */
    public static void wrapActionModeChangeForDialog(Dialog dialog, OnActionModeChangedListener onActionModeChangedListener) {
        if (dialog == null) {
            if (HtcBuildFlag.Htc_DEBUG_flag) Log.e("HTCActionBar", "dialog is null", new Exception());
            return;
        }
        if (onActionModeChangedListener == null) {
            if (HtcBuildFlag.Htc_DEBUG_flag) Log.e("HTCActionBar", "onActionModeChangedListener is null", new Exception());
            return;
        }
        wrapActionModeChangeForWindow(dialog.getWindow(), onActionModeChangedListener);
    }

    private static void wrapActionModeChangeForWindow(Window window, OnActionModeChangedListener onActionModeChangedListener) {
        if (window == null) {
            if (HtcBuildFlag.Htc_DEBUG_flag) Log.e("HTCActionBar", "window is null", new Exception());
            return;
        }
        if (onActionModeChangedListener == null) {
            if (HtcBuildFlag.Htc_DEBUG_flag) Log.e("HTCActionBar", "onActionModeChangedListener is null", new Exception());
            return;
        }

        ViewGroup rootView = (ViewGroup) window.getDecorView();
        final int count = rootView.getChildCount();

        if (count == 0) {
            if (HtcBuildFlag.Htc_DEBUG_flag) Log.e("HTCActionBar", "rootView has no child , apply failed");
            return;
        }

        if (count == 1) {
            final View view = rootView.getChildAt(0);
            if (view instanceof ActionModeFilterLayout) {
                if (HtcBuildFlag.Htc_DEBUG_flag) Log.e("HTCActionBar", "ActionModeFilterLayout already applied to window");
                return;
            }
        }

        ActionModeFilterLayout actionModeFilterLayout = new ActionModeFilterLayout(rootView.getContext());
        actionModeFilterLayout.setOnActionModeChangedListener(onActionModeChangedListener);

        View[] tempViews = new View[count];
        for (int i = 0; i < count; i++) {
            tempViews[i] = rootView.getChildAt(i);
        }
        rootView.removeAllViews();
        for (int i = 0; i < count; i++) {
            actionModeFilterLayout.addView(tempViews[i]);
        }
        rootView.addView(actionModeFilterLayout, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
    }
}
