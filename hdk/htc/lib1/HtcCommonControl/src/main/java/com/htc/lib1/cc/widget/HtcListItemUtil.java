package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint.FontMetricsInt;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.View;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.util.res.HtcResUtil;

import java.util.Observable;
import java.util.Observer;

/**
 * This class is not recommended
 * @Deprecated Use HtcListItemManager instead.
 *
 */
@Deprecated
final class HtcListItemUtil {

    private static final Object sLockObject = new Object();

    private static final int M1 = 0;
    private static final int M2 = 1;
    private static final int M3 = 2;
    private static final int M4 = 3;
    private static final int M5 = 4;
    private static final int M6 = 5;

    static final float VERTICAL_DIVIDER_RATIO = 0.147f;
    static final float VERTICAL_DIVIDER_RATIO_AUTOMOTIVE = 0.2f;

    private static TextPaint sTextPaint = new TextPaint();

    private static int[] Margin = new int[6];

    private static boolean sArrayInit = false;
    private static int sDesiredItemHeight[] = new int[HtcListItem.NUM_ITEMMODE];
    private static int sTextTopGap[] = new int[HtcListItem.NUM_ITEMMODE];
    private static int sTextBottomGap[] = new int[HtcListItem.NUM_ITEMMODE];
    private static int sPrimaryTextSize[] = new int[HtcListItem.NUM_ITEMMODE];
    private static int sSecondaryTextSize[] = new int[HtcListItem.NUM_ITEMMODE];
    private static int sPrimaryTextBaseline[] = new int[HtcListItem.NUM_ITEMMODE];
    private static int sSecondaryTextBaseline[] = new int[HtcListItem.NUM_ITEMMODE];
    private static int sPrimaryTextViewHeight[] = new int[HtcListItem.NUM_ITEMMODE];
    private static int sSecondaryTextViewHeight[] = new int[HtcListItem.NUM_ITEMMODE];
    private static int sPhoneActionButtonWidth[] = new int[HtcListItem.NUM_ITEMMODE];
    private static boolean sContextSet[] = new boolean[HtcListItem.NUM_ITEMMODE];
    private static int sPortraitWindowWidth = 0;
    private static int mVerticalDividerWidth = 0;
    private static int mLeftIndentSpace = 0;

    private static Observer sObserver = new Observer() {

        @Override
        public void update(Observable Observable, Object date) {
            for (int i = 0; i < sPrimaryTextSize.length; i++) {
                sPrimaryTextSize[i] = 0;
            }
        }
    };

    static {
        HtcCommonUtil.addObserver(HtcCommonUtil.TYPE_THEME | HtcCommonUtil.TYPE_FONT_SIZE
                | HtcCommonUtil.TYPE_FONT_STYLE, sObserver);
    }

    static void setContextForMargins(Context c, int itemMode) {
        // work around of automotive mode. The original design sucks.
        Resources res = c.getResources();
        synchronized (sLockObject) {
            if (!sArrayInit) {
                Margin[M1] = res.getDimensionPixelOffset(R.dimen.margin_l);
                Margin[M2] = res.getDimensionPixelOffset(R.dimen.margin_m);
                Margin[M3] = res.getDimensionPixelOffset(R.dimen.margin_s);
                Margin[M4] = res.getDimensionPixelOffset(R.dimen.margin_xs);
                Margin[M5] = res.getDimensionPixelOffset(R.dimen.spacing);
                Margin[M6] = res.getDimensionPixelOffset(R.dimen.leading);
                mVerticalDividerWidth = res
                        .getDimensionPixelOffset(R.dimen.htc_list_item_vertical_divider_width);
                mLeftIndentSpace = res
                        .getDimensionPixelOffset(R.dimen.htc_list_item_left_indent_space);

                sArrayInit = true;
            }
        }

        int pfont = 0;
        if (itemMode == HtcListItem.MODE_DEFAULT) {
            pfont = res.getDimensionPixelSize(com.htc.lib1.cc.R.dimen.list_primary_m);
            if (pfont != sPrimaryTextSize[itemMode]) {
                sContextSet[itemMode] = false;
            }
        } else if (itemMode == HtcListItem.MODE_POPUPMENU) {
            pfont = res.getDimensionPixelSize(com.htc.lib1.cc.R.dimen.list_primary_s);
            if (pfont != sPrimaryTextSize[itemMode]) {
                sContextSet[itemMode] = false;
            }
        } else if (itemMode == HtcListItem.MODE_AUTOMOTIVE) {
            pfont = res
                    .getDimensionPixelSize(com.htc.lib1.cc.R.dimen.fixed_automotive_darklist_primary_m);
            if (pfont != sPrimaryTextSize[itemMode]) {
                sContextSet[itemMode] = false;
            }
        } else if (itemMode == HtcListItem.MODE_KEEP_MEDIUM_HEIGHT) {
            pfont = res.getDimensionPixelSize(com.htc.lib1.cc.R.dimen.fixed_list_primary_m);
            if (pfont != sPrimaryTextSize[itemMode]) {
                sContextSet[itemMode] = false;
            }
        }

        if (sContextSet[itemMode] && itemMode == HtcListItem.MODE_DEFAULT)
            return;

        if (sContextSet[itemMode] && itemMode == HtcListItem.MODE_POPUPMENU)
            return;

        if (sContextSet[itemMode] && itemMode == HtcListItem.MODE_AUTOMOTIVE)
            return;

        if (sContextSet[itemMode] && itemMode == HtcListItem.MODE_KEEP_MEDIUM_HEIGHT)
            return;

        if (itemMode == HtcListItem.MODE_DEFAULT) {

            sPrimaryTextSize[itemMode] = res
                    .getDimensionPixelSize(com.htc.lib1.cc.R.dimen.list_primary_m);
            sSecondaryTextSize[itemMode] = res
                    .getDimensionPixelSize(com.htc.lib1.cc.R.dimen.list_secondary_m);
            updateTextHeight(c, com.htc.lib1.cc.R.style.list_primary_m, sPrimaryTextBaseline,
                    sPrimaryTextViewHeight, itemMode);
            updateTextHeight(c, com.htc.lib1.cc.R.style.list_secondary_m, sSecondaryTextBaseline,
                    sSecondaryTextViewHeight, itemMode);
            sTextTopGap[itemMode] = (int) (res.getFraction(
                    com.htc.lib1.cc.R.fraction.listitem_top_margin_percent,
                    sPrimaryTextViewHeight[itemMode], 1) + 0.5f);
            sTextBottomGap[itemMode] = (int) (res.getFraction(
                    com.htc.lib1.cc.R.fraction.listitem_bottom_margin_percent,
                    sSecondaryTextViewHeight[itemMode], 1) + 0.5f);
        } else if (itemMode == HtcListItem.MODE_POPUPMENU) {

            sPrimaryTextSize[itemMode] = res
                    .getDimensionPixelSize(com.htc.lib1.cc.R.dimen.list_primary_s);
            sSecondaryTextSize[itemMode] = res
                    .getDimensionPixelSize(com.htc.lib1.cc.R.dimen.list_secondary_s);
            updateTextHeight(c, com.htc.lib1.cc.R.style.list_primary_s, sPrimaryTextBaseline,
                    sPrimaryTextViewHeight, itemMode);
            updateTextHeight(c, com.htc.lib1.cc.R.style.list_secondary_s, sSecondaryTextBaseline,
                    sSecondaryTextViewHeight, itemMode);
            sTextTopGap[itemMode] = (int) (res.getFraction(
                    com.htc.lib1.cc.R.fraction.listitem_popupmenu_top_margin_percent,
                    sPrimaryTextViewHeight[itemMode], 1) + 0.5f);
            sTextBottomGap[itemMode] = (int) (res.getFraction(
                    com.htc.lib1.cc.R.fraction.listitem_popupmenu_bottom_margin_percent,
                    sSecondaryTextViewHeight[itemMode], 1) + 0.5f);
        } else if (itemMode == HtcListItem.MODE_AUTOMOTIVE) {

            sPrimaryTextSize[itemMode] = res
                    .getDimensionPixelSize(com.htc.lib1.cc.R.dimen.fixed_automotive_darklist_primary_m);
            sSecondaryTextSize[itemMode] = res
                    .getDimensionPixelSize(com.htc.lib1.cc.R.dimen.fixed_automotive_darklist_secondary_m);
            updateTextHeight(c, com.htc.lib1.cc.R.style.fixed_automotive_darklist_primary_m,
                    sPrimaryTextBaseline, sPrimaryTextViewHeight, itemMode);
            updateTextHeight(c, com.htc.lib1.cc.R.style.fixed_automotive_darklist_secondary_m,
                    sSecondaryTextBaseline, sSecondaryTextViewHeight, itemMode);
            sTextTopGap[itemMode] = (int) (res.getFraction(
                    com.htc.lib1.cc.R.fraction.listitem_automotive_top_margin_percent,
                    sPrimaryTextViewHeight[itemMode], 1) + 0.5f);
            sTextBottomGap[itemMode] = (int) (res.getFraction(
                    com.htc.lib1.cc.R.fraction.listitem_automotive_bottom_margin_percent,
                    sSecondaryTextViewHeight[itemMode], 1) + 0.5f);
        } else if (itemMode == HtcListItem.MODE_KEEP_MEDIUM_HEIGHT) {

            sPrimaryTextSize[itemMode] = res
                    .getDimensionPixelSize(com.htc.lib1.cc.R.dimen.fixed_list_primary_m);
            sSecondaryTextSize[itemMode] = res
                    .getDimensionPixelSize(com.htc.lib1.cc.R.dimen.fixed_list_secondary_m);
            updateTextHeight(c, com.htc.lib1.cc.R.style.fixed_list_primary_m, sPrimaryTextBaseline,
                    sPrimaryTextViewHeight, itemMode);
            updateTextHeight(c, com.htc.lib1.cc.R.style.fixed_list_secondary_m,
                    sSecondaryTextBaseline, sSecondaryTextViewHeight, itemMode);
            sTextTopGap[itemMode] = (int) (res.getFraction(
                    com.htc.lib1.cc.R.fraction.listitem_top_margin_percent,
                    sPrimaryTextViewHeight[itemMode], 1) + 0.5f);
            sTextBottomGap[itemMode] = (int) (res.getFraction(
                    com.htc.lib1.cc.R.fraction.listitem_bottom_margin_percent,
                    sSecondaryTextViewHeight[itemMode], 1) + 0.5f);
        }

        if (itemMode >= 0 && itemMode < HtcListItem.NUM_ITEMMODE) {
            sDesiredItemHeight[itemMode] = sTextTopGap[itemMode] + sTextBottomGap[itemMode]
                    + sPrimaryTextViewHeight[itemMode] + sSecondaryTextViewHeight[itemMode];
            if (isOdd(sDesiredItemHeight[itemMode])) {
                sDesiredItemHeight[itemMode]++;
                sTextBottomGap[itemMode]++;
            }

            sContextSet[itemMode] = true;
        }
    }

    static int getM1() {
        return getMargin(M1);
    }

    static int getM2() {
        return getMargin(M2);
    }

    static int getM3() {
        return getMargin(M3);
    }

    static int getM4() {
        return getMargin(M4);
    }

    static int getM5() {
        return getMargin(M5);
    }

    static int getM6() {
        return getMargin(M6);
    }

    private static int getMargin(int marginLevel) {
        if (sArrayInit)
            return Margin[marginLevel];
        else
            return -1;
    }

    static int getDesiredListItemHeight(int itemMode) {
        if (sContextSet[itemMode]) {
            return sDesiredItemHeight[itemMode];
        }

        return -1;
    }

    static int getVerticalDividerWidth() {
        return mVerticalDividerWidth;
    }

    static int getDesiredTopGap(int itemMode) {
        return sTextTopGap[itemMode];
    }

    static int getDesiredCenterFor7Badge(int itemMode) {
        return (int) (getDesiredTopGap(itemMode) + 0.5f * sPrimaryTextBaseline[itemMode]);
    }

    static int getDesiredCenterGap(int itemMode) {
        return 0;
    }

    static int getDesiredBottomGap(int itemMode) {
        return sTextBottomGap[itemMode];
    }

    static int getPrimaryBaseLine(int itemMode) {
        return getDesiredTopGap(itemMode) + sPrimaryTextBaseline[itemMode];
    }

    static int getSecondaryBaseLine(int itemMode) {
        return getDesiredTopGap(itemMode) + sPrimaryTextViewHeight[itemMode]
                + sSecondaryTextBaseline[itemMode];
    }

    static int getPrimaryTextBaseLine(int itemMode) {
        return sPrimaryTextBaseline[itemMode];
    }

    static int getSecondaryTextBaseLine(int itemMode) {
        return sSecondaryTextBaseline[itemMode];
    }

    /**
     * get all children's vertical gap
     *
     * @return the value of gap
     */
    static int getDesiredChildrenGap() {
        return getM2();
    }

    static int getPortraitWindowWidth(Context c) {
        if (sPortraitWindowWidth == 0) {
            DisplayMetrics metrics = new DisplayMetrics();
            metrics = c.getResources().getDisplayMetrics();
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;

            if (width < height)
                sPortraitWindowWidth = width;
            else
                sPortraitWindowWidth = height;
        }
        return sPortraitWindowWidth;
    }

    static int getActionButtonWidth(Context c, int deviceMode, int itemMode) {

        if (deviceMode != HtcListItem.DEVICE_PHONE) {
            return getPhotoFrameWidth(c, itemMode);
        } else {
            if (sPhoneActionButtonWidth[itemMode] == 0) {
                sPhoneActionButtonWidth[itemMode] = changeOddToEven((int) (getPortraitWindowWidth(c) * (itemMode == HtcListItem.MODE_AUTOMOTIVE ? VERTICAL_DIVIDER_RATIO_AUTOMOTIVE
                        : VERTICAL_DIVIDER_RATIO)));
            }
            return sPhoneActionButtonWidth[itemMode];
        }
    }

    static int getPhotoFrameWidth(Context c, int itemMode) {
        return HtcListItemUtil.getDesiredListItemHeight(itemMode) + getM2();
    }

    /**
     * This API is used to update text height and text baseline.
     *
     * @param c
     * @param style
     * @param includeBelow if true, return all height, include the text height
     *            below and above baseline. if false, only the text height above
     *            baseline.
     * @param baseline
     * @param textheight
     * @param itemMode
     */
    static void updateTextHeight(Context c, int style, int[] baseline, int[] textheight,
            int itemMode) {
        if (sTextPaint != null) {
            HtcResUtil.setTextAppearance(c, style, sTextPaint, false);
            FontMetricsInt sFontMetrics = sTextPaint.getFontMetricsInt();
            textheight[itemMode] = sFontMetrics.bottom - sFontMetrics.top;
            baseline[itemMode] = -sFontMetrics.top;
        } else {
            textheight[itemMode] = 0;
            baseline[itemMode] = 0;
        }
    }

    /**
     * check the value is odd number
     *
     * @param value The value need to be checked
     * @return true: The value is odd number. false The value is even number
     */
    private static boolean isOdd(int value) {
        return (value & 1) == 1;
    }

    /**
     * check the value,if the value is odd number then change it to a even
     * number by add 1
     *
     * @param value The value need to be checked and change
     * @return A even number
     */
    private static int changeOddToEven(int value) {
        return isOdd(value) ? ++value : value;
    }

    /**
     * if enabled, set alpha to 1.0, else to 0.4
     *
     * @param view
     * @param enabled is view enabled
     */
    static void setViewOpacity(View view, boolean enabled) {
        if (enabled)
            view.setAlpha(1.0f);
        else
            view.setAlpha(0.4f);
    }

    static int getLeftIndentSpace() {
        return mLeftIndentSpace;
    }
}
