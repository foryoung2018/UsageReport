package com.htc.lib1.cc.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.graphics.drawable.Drawable;
import android.content.res.TypedArray;
import android.view.View;

import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.R;

/**
 * HtcButtonUtil
 */
public class HtcButtonUtil {
    static final int ANIMATION_MODE_SCALE = 0;
    static final int ANIMATION_MODE_MULTIPLY = 1;
    static final int ANIMATION_MODE_SCREEN = 2;

    /** The callback used to indicate the state of button press animation. */
    public interface OnPressAnimationListener {

        /**
         * Callback when the press animation of this button starts.
         * @param view The view associated with this listener.
         */
        void onAnimationStarts(View view);

        /**
         * Callback when the press animation of this button ends.
         * @param view The view associated with this listener.
         */
        void onAnimationEnds(View view);

        /**
         * Callback when the press animation of this button cancels.
         * @param view The view associated with this listener.
         */
        void onAnimationCancels(View view);
    }

    /** Constant to indicate Light background mode, it is default mode. */
    public static final int BACKGROUND_MODE_LIGHT = 0;

    /** Constant to indicate Dark background mode. */
    public static final int BACKGROUND_MODE_DARK = 1;

    /** Constant to indicate Automotive Dark background mode. */
    public static final int BACKGROUND_MODE_AUTOMOTIVEDARK = 2;

    /** Constant to indicate Automotive Light background mode. */
    public static final int BACKGROUND_MODE_AUTOMOTIVELIGHT = 3;

    /** Constant to indicate colorful background mode. */
    public static final int BACKGROUND_MODE_COLORFUL = 7;

    /** Constant to indicate Porgress button in small size mode. */
    public static final int BUTTON_SIZE_MODE_SMALL = 0;

    /** Constant to indicate progress button in middle size mode. */
    public static final int BUTTON_SIZE_MODE_MIDDLE = 1;

    static final float MAX_SCALE = 1.0f;
    static final float MIN_SCALE = 0.9f;
    static final float DISABLE_ALPHA = 0.5f;
    static final float DISABLE_ALPHA_DARK = 0.4f;
    static final float VISIBLE_ALPHA = 1.0f;
    static final int MAX_ALPHA = 255;
    static final int MIN_ALPHA = 0;
    static final int BASE_ALPHA = 255;

    private static boolean sIsAnimationEnabled = true;

    /** Constant to indicate the out-rim multiply should be played. */
    public static final int EXT_ANIMATE_NONE = 0x0000;

    /** Constant to indicate the out-rim multipy will not be played. */
    public static final int EXT_ANIMATE_NORIMMULTIPLY = 0x0001;

    //All 17 skinable assets below
    /**@hide*/
    static final int BTNASSET_COMMON_BUTTON_REST = 0;
    /**@hide*/
    static final int BTNASSET_COMMON_B_BUTTON_REST = 1;
    /**@hide*/
    static final int BTNASSET_COMMON_CIRCLE_OUTER = 2;
    /**@hide*/
    static final int BTNASSET_COMMON_B_CIRCLE_OUTER = 3;
    /**@hide*/
    static final int BTNASSET_COMMON_CIRCLE_PRESSED = 4;
    /**@hide*/
    static final int BTNASSET_COMMON_CHECKBOX_ON = 5;
    /**@hide*/
    static final int BTNASSET_COMMON_CHECKBOX_REST = 6;
    /**@hide*/
    static final int BTNASSET_COMMON_CHECKBOX_PARTIAL = 7;
    /**@hide*/
    static final int BTNASSET_COMMON_RADIO_REST_LIGHT = 8;
    /**@hide*/
    static final int BTNASSET_COMMON_RADIO_REST_DARK = 9;
    /**@hide*/
    static final int BTNASSET_COMMON_DELETE_ON = 10;
    /**@hide*/
    static final int BTNASSET_COMMON_DELETE_REST = 11;
    /**@hide*/
    static final int BTNASSET_COMMON_COLLECT_REST = 12;
    /**@hide*/
    static final int BTNASSET_COMMON_RATING_REST = 13;
    /**@hide*/
    static final int BTNASSET_COMMON_FLAG_REST = 14;
    /**@hide*/
    static final int BTNASSET_COMMON_FLAG_ON = 15;
    /**@hide*/
    static final int BTNASSET_COMMON_B_CHECKBOX_REST = 16;
    //All 17 skinable assets above

    private static final int BUTTON_THEME_COLOR_OVERLAY = 0;
    private static final int BUTTON_THEME_COLOR_CATEGORY = 1;

    static int getMultiplyColor(Context context, AttributeSet attrs) {
        return getOverlayColor(context, attrs);
    }

    static int getOverlayColor(Context context, AttributeSet attrs) {
        return getThemeColor(context, BUTTON_THEME_COLOR_OVERLAY);
    }

    static int getCategoryColor(Context context, AttributeSet attrs) {
        return getThemeColor(context, BUTTON_THEME_COLOR_CATEGORY);
    }

    static int getSelectorColor(int backgroundMode) {
        return getSelectorColor(isDarkMode(backgroundMode));
    }

    static int getSelectorColor(boolean isDarkMode) {
        return isDarkMode ? 0x33FFFFFF : 0x19000000;
    }

    static boolean isDarkMode(int backgroundMode) {
        switch (backgroundMode) {
        case BACKGROUND_MODE_DARK:
        case BACKGROUND_MODE_AUTOMOTIVEDARK:
        case BACKGROUND_MODE_COLORFUL:
            return true;
        default:
            return false;
        }
    }

    private static int getThemeColor(Context context, int type) {
        //+ [ahan_wu] Use HtcThemeUtils to fit theme change.
        int target = (type==BUTTON_THEME_COLOR_CATEGORY ? R.styleable.ThemeColor_category_color : R.styleable.ThemeColor_overlay_color);
        int color = HtcCommonUtil.getCommonThemeColor(context, target);
        //- [ahan_wu] Use HtcThemeUtils to fit theme change.
        return color;
    }

    static Drawable getButtonDrawable(Context context, AttributeSet attrs, int defStyle, int index) {
        if (index < 0) throw new IllegalArgumentException("[HtcButtonUtil.getButtonDrawable] Invalid index passed in");
        if (context == null) throw new IllegalArgumentException("[HtcButtonUtil.getButtonDrawable] Null context passed in");
        return loadSkinableResources(context, attrs, defStyle, index);
    }

    private static Drawable loadSkinableResources(Context context, AttributeSet attrs, int defStyle, int index) {
        Drawable tmp = null;
        TypedArray a, b;

        a = context.obtainStyledAttributes(attrs, R.styleable.HtcButtonStyle, R.attr.buttonStyle, defStyle);
        b = context.getResources().obtainTypedArray(a.getResourceId(R.styleable.HtcButtonStyle_android_drawable, R.array.htcbutton_drawables));

        tmp = context.getResources().getDrawable(b.getResourceId(index, 0));

        a.recycle();
        b.recycle();

        return tmp;
    }

    static int getMultiplyTextColor(Context context) {
        return context.getResources().getColor(R.color.dark_secondaryfont_color);
    }

    static void setEnableAnimation(boolean isEnabled) {
        sIsAnimationEnabled = isEnabled;
    }

    static boolean getEnableAnimation() { return sIsAnimationEnabled; }
}
