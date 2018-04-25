package com.htc.lib1.cc.util.res;
/* Copyright (C) 2008 HTC Corp. All Rights Reserved. */
import java.lang.reflect.Field;
import java.util.Locale;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.FontMetricsInt;
import android.os.Environment;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.htcjavaflag.HtcBuildFlag;

/**
 * A convenient utility class used to handle resource-related tasks.
 */
public class HtcResUtil {
    private static final String TAG = "HtcResUtil";

    /**
     * AllCaps in Roman character based languages:
     *  en - English
     *  es - Spanish
     *  da - Danish
     *  de - German
     *  fi - Finnish
     *  fr - French
     *  it - Italian
     *  nl - Dutch
     *  nb - Norwegian
     *  pl - Polish
     *  pt - Portuguese
     *  sv - Swedish
     *
     * No AllCaps in other languages:
     *  bg - Bulgarian
     *  el - Greek
     *  kk - Kazakhstan
     *  ru - Russian
     *  uk - Ukrainian
     *  ...
     */
    private static final String[] ALL_CAPS_LOCALE = { "en", "es", "da", "de",
            "fi", "fr", "it", "nl", "nb", "pl", "pt", "sv" };

    /**
     * This API is used to access the non-public resource in the
     * framework/common control. The usage MUST do the error handling. The usage
     * MUST do the performance problem of the java class reflection
     *
     * @param res
     *            The string format of the resource id that you want
     * @return the resource id that you want. return 0 if there are any
     *         exception/error/problem.
     * @deprecated
     */
    private static int getPrivateResID(String res) {

        String[] TokenList = res.split("\\.");
        if (3 > TokenList.length)
            return 0;

        int nRstart = res.indexOf("R.");
        if (-1 == nRstart)
            return 0;

        String ClassName = res.substring(0, nRstart + 1);
        String ResName = TokenList[TokenList.length - 1];
        String ResType = TokenList[TokenList.length - 2];

        try {
            Class c = Class.forName(ClassName);
            Class[] internalclasses = c.getDeclaredClasses();

            for (Class i : internalclasses) {
                if (ResType.equals(i.getSimpleName())) {
                    Field declaredField = i.getDeclaredField(ResName);
                    Integer id = (Integer) declaredField.get(null);
                    internalclasses = null;
                    return id.intValue();
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e(HtcResUtil.class.getSimpleName(), e.toString());
        }
        return 0;
    }

    /**
     * Sets the text default color, size, style
     * from the specified TextAppearance resource to the text paint.
     *
     * @param context the context the view is running in
     * @param resid the resource id of the font label
     * @param textPaint the text paint which need to set the style
     * @param enableColorStateList if ColorStateList is enabled
     */
    /**@hide*/
    public static void setTextAppearance(Context context, int resid, TextPaint textPaint, boolean enableColorStateList) {
        setTextAppearance(context, null, resid, textPaint, enableColorStateList);
    }

    /**
     * Sets the text default color, size, style from the specified TextAppearance resource to the
     * text paint.
     *
     * @param context the context the view is running in
     * @param typeface the typeface used by textpaint
     * @param resid the resource id of the font label
     * @param textPaint the text paint which need to set the style
     * @param enableColorStateList if ColorStateList is enabled
     * @hide
     */
    public static void setTextAppearance(Context context, Typeface typeface, int resid, TextPaint textPaint, boolean enableColorStateList) {
        if (textPaint == null) {
            throw new RuntimeException("textPaint can NOT be null!");
        }

        TypedArray appearance =
            context.obtainStyledAttributes(resid,
                                           R.styleable.TextAppearance);

        int ts;
        if (enableColorStateList)
        {
            ColorStateList colors;

            colors = appearance.getColorStateList(R.styleable.
                                              TextAppearance_android_textColor);
            if (colors != null) {
                textPaint.setColor(colors.getDefaultColor());
            }
        }

        ts = appearance.getDimensionPixelSize(R.styleable.
                                              TextAppearance_android_textSize, 0);
        if (ts != 0) {
            textPaint.setTextSize(ts);
        }
        if (typeface != null) {
            textPaint.setTypeface(typeface);
        } else {
            String familyName = appearance.getString(R.styleable.TextAppearance_android_fontFamily);
            int typefaceIndex = appearance.getInt(R.styleable.TextAppearance_android_typeface, -1);
            int styleIndex = appearance.getInt(R.styleable.TextAppearance_android_textStyle, -1);
            setTypefaceFromAttrs(familyName, typefaceIndex, styleIndex, textPaint);
        }
        appearance.recycle();
    }

    /**
     * Sets the text default color, size, style
     * from the specified TextAppearance resource to the text paint.
     *
     * @param context the context the view is running in
     * @param resid the resource id of the font label
     * @param textPaint the text paint which need to set the style
     */
    /**@hide*/
    public static void setTextAppearance(Context context, int resid, TextPaint textPaint) {
        setTextAppearance(context, resid, textPaint, true);
    }

    private static final int SANS = 1;
    private static final int SERIF = 2;
    private static final int MONOSPACE = 3;

    private static void setTypefaceFromAttrs(String familyName, int typefaceIndex, int styleIndex, TextPaint textPaint) {
        Typeface tf = null;
        if (familyName != null) {
            tf = Typeface.create(familyName, styleIndex);
            if (tf != null) {
                textPaint.setTypeface(tf);
                return;
            }
        }
        switch (typefaceIndex) {
            case SANS:
                tf = Typeface.SANS_SERIF;
                break;

            case SERIF:
                tf = Typeface.SERIF;
                break;

            case MONOSPACE:
                tf = Typeface.MONOSPACE;
                break;
        }

        setTypeface(tf, styleIndex, textPaint);
    }

    /**
     * Sets the typeface and style in which the text should be displayed,
     * and turns on the fake bold and italic bits in the Paint if the
     * Typeface that you provided does not have all the bits in the
     * style that you specified.
     *
     * @attr ref android.R.styleable#TextView_typeface
     * @attr ref android.R.styleable#TextView_textStyle
     */
    private static void setTypeface(Typeface tf, int style, TextPaint textPaint) {
        if (style > 0) {
            if (tf == null) {
                tf = Typeface.defaultFromStyle(style);
            } else {
                tf = Typeface.create(tf, style);
            }

            textPaint.setTypeface(tf);
            // now compute what (if any) algorithmic styling is needed
            int typefaceStyle = tf != null ? tf.getStyle() : 0;
            int need = style & ~typefaceStyle;
            textPaint.setFakeBoldText((need & Typeface.BOLD) != 0);
            textPaint.setTextSkewX((need & Typeface.ITALIC) != 0 ? -0.25f : 0);
        } else {
            textPaint.setFakeBoldText(false);
            textPaint.setTextSkewX(0);
            textPaint.setTypeface(tf);
        }
    }

    /**
     * Used for following widgets to determine the "all caps" feature:
     *   HtcAlertController, HtcAlertDialog, ProgressDialog
     *   HtcMultiSeekBarDialog
     *   HtcFooterButton
     *   HtcFastScroller
     *   HtcListItemLabeledLayout
     *   HtcListItemSeparator
     *   ActionBarRefresh
     *   CarouselHost
     *   CarouselContentAdapter
     *   HtcStatusView
     *   HtcDatePicker, HtcTimePicker
     *   HtcPreference, HtcDialogPreference
     *
     * For example:
     *   textView.setAllCaps(HtcResUtil.isInAllCapsLocale(mContext));
     *
     * PS1. don't forget to check the XML attribute "android:textAllCaps"
     * PS2. do setAllCaps before setText
     *
     * @param context the application context
     * @return true if you have to transform the text to All Caps
     */
    public static boolean isInAllCapsLocale(Context context) {
        if (context !=  null) {
            Resources res = context.getResources();
            if (res != null) {
                Configuration config = res.getConfiguration();
                if (config != null) {
                    Locale locale = config.locale;
                    if (locale != null) {
                        String language = locale.getLanguage();
                        if (language != null) {
                            for (String tmp : ALL_CAPS_LOCALE) {
                                if (language.equals(tmp)) {
                                    return true;
                                }
                            }
                        } else {
                            Log.e(TAG, "Fail to get language.");
                        }
                    } else {
                        Log.e(TAG, "Fail to get locale.");
                    }
                } else {
                    Log.e(TAG, "Fail to get configuration.");
                }
            } else {
                Log.e(TAG, "Fail to get res.");
            }
        } else {
            Log.e(TAG, "Context is null.");
        }

        return false;
    }

    /**
     * Used to apply the "all caps" feature for a string object.
     *
     * @param context the application
     * @param str the string
     * @return a new upper case string, or the original input string if "all caps" is not applied.
     *
     * @hide
     */
    public static String toUpperCase(Context context, String str) {
        if (str == null)
            return null;

        return (isInAllCapsLocale(context) == true) ? str.toUpperCase() : str;
    }

    /**
     * Get pixels of a dimension resource under different fontsize configurations.
     * This is used by Font Size Selector of HtcAlertDialog at Sense 5.5 (new feature).
     * PS. This depends on the fontsize customization in Configuration.
     *     Please keep an eye on it or contact Greg Tsai if any question.
     *
     * @param context the context containing the following dimension resource.
     *        For example, if the id is from the common control, please use the context of
              package "com.htc", or you'll get an exception.
     * @param id a dimension resource id that you are asking the sizes of.
     * @return an array listing sizes regarding to the dimension resource in
     *         different fontsize configurations.
     *
     * @hide
     */
    public static int[] getDimensionsInDifferentFontSizeConfig(Context context, int id) {
        //final int FONT_SIZE_LEVELS = 7; // Fontsizes are defined in HTC-customized Configuration.
                                        // See there for details.
        // Sense6 design change, fontsize is not supported anymore.
        // fallback to font scale.
        // following scales are defined in Settings (both java and res)
        // but we can not get it directly, so maintain a duplicate here.
        final float[] FONT_SCALE_LEVELS = { 0.85f, 1.0f, 1.15f, 1.3f, 1.45f };
        int[] dimensions = new int[FONT_SCALE_LEVELS.length];

        AssetManager am = context.getAssets();
        DisplayMetrics metrics = new DisplayMetrics();
        metrics.setTo(context.getResources().getDisplayMetrics());
        Configuration config = new Configuration(context.getResources().getConfiguration());
        Resources res = new Resources(am, metrics, config);
        for (int i = 0; i < dimensions.length; ++i) {
            // Sense6 design change, remove 0:undefined
            // Loop starts from 1, because 0 is FONTSIZE_UNDEFINED.
            // See Configuration for details.
            config.fontScale = FONT_SCALE_LEVELS[i];
            res.updateConfiguration(config, metrics);
            dimensions[i] = res.getDimensionPixelSize(id);
        }
        return dimensions;
    }

    /**
     * @hide
     */
    public static FontMetricsInt getFontStyleMetrics(Context context, int styleId) {
        if (null == context)
            return null;
        TextPaint mTextPaint = new TextPaint();
        setTextAppearance(context, createDefaultFontFromFile(), styleId, mTextPaint, false);
        FontMetricsInt textFontMetrics = mTextPaint.getFontMetricsInt();
        return textFontMetrics;
    }

    private static Typeface sRobotoTypeface = null;

    private static Typeface createDefaultFontFromFile() {
        if (sRobotoTypeface != null) {
            return sRobotoTypeface;
        }
        sRobotoTypeface = createFontFromFile("Roboto-Regular.ttf");
        return sRobotoTypeface;
    }

    /**
     * @hide
     */
    public static Typeface createFontFromFile(String fontName) {
        Typeface tf = null;
        String fontPath = Environment.getRootDirectory().getAbsolutePath();
        fontPath += "/fonts/" + fontName;
        try {
            tf = Typeface.createFromFile(fontPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tf;
    }

    /*
     * @hide
     */
    public static int getHeightOfChar(TextView v, String s) {
        if (null != v && View.VISIBLE == v.getVisibility()) {
            Paint p = v.getPaint();
            if (null == p)
                return 0;

            Rect mTmpRect = new Rect();
            p.getTextBounds(s, 0, s.length(), mTmpRect);
            return mTmpRect.height();
        }
        return 0;
    }

    /**
     * @hide
     * @deprecated [ Internal use only]
     */
    public static int getM1(Context context) {
        if (context == null || context.getResources() == null) {
            if (HtcBuildFlag.Htc_DEBUG_flag) Log.d(TAG, "getM1 context/res is null", new Exception());
            // Return xxhdpi value if error.
            return 46;
        }

        return context.getResources().getDimensionPixelOffset(R.dimen.margin_l);
    }

    /**
     * @hide
     * @deprecated [Internal use only]
     */
    public static int getM2(Context context) {
        if (context == null || context.getResources() == null) {
            if (HtcBuildFlag.Htc_DEBUG_flag) Log.d(TAG, "getM2 context/res is null", new Exception());
            // Return xxhdpi value if error.
            return 30;
        }

        return context.getResources().getDimensionPixelOffset(R.dimen.margin_m);
    }

}