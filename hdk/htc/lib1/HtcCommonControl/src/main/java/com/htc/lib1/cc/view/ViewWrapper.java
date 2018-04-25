package com.htc.lib1.cc.view;

import android.view.View;

/**
 * @deprecated [Not use any longer] Not support class
 */
/**@hide*/
public class ViewWrapper {

    /**
     * enable or disable rounded corner at a view; default is disable; if enable; it will do clippath at the 4 corners
     * @param view the target view
     * @param roundCorner true: enable; false: disable
     */
    public static void setRoundedCornerEnabled(View view, boolean roundCorner) {
        //vincent view.setRoundedCornerEnabled(roundCorner);
    }

    /**
     * enable or disable top/bottom rounded corner on a view; default is disable
     * @param view the target view
     * @param topRoundCorners true enable; false disable
     * @param bottomRoundCorners true: enable; false: disable
     */
    public static void setRoundedCornerEnabled(View view, boolean topRoundCorners, boolean bottomRoundCorners) {
        //vincent view.setRoundedCornerEnabled(topRoundCorners, bottomRoundCorners);
    }

    /**
     * check rounded corner;
     * @param view the target
     * @return true: top or bottom is rounded; false: top and bottom are not rounded
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public static boolean isRoundedCornerEnabled(View view) {
        return false; //vincent view.isRoundedCornerEnabled();
    }

    /**
     * check top rounded or not
     * @param view the target
     * @return true: top rounded; false: disable top rounded
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public static boolean isTopCornerRounded(View view) {
        return false; //vincent view.isTopCornerRounded();
    }

    /**
     * check bottom rounded or not
     * @param view the target
     * @return true: bottom rounded; false: disable bottom rounded
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public static boolean isBottomCornerRounded(View view) {
        return false; //vincent view.isBottomCornerRounded();
    }

    /**
     * to set specific Radius
     * @param view the target view
     * @param radius the give radius
     */
    public static void setCornerRadius(View view, int radius) {
        //vincent view.setCornerRadius(radius);
    }

    /**
     * to get the radius
     * @param view the target view
     * @return the radius
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public static int getCornerRadius(View view) {
        //vincent return view.getCornerRadius();
        return 0;
    }

    /**
     * to set stroke width
     * @param view target view
     * @param width the given width
     */
    public static void setCornerStrokeWidth(View view, int width) {
        //vincent view.setCornerStrokeWidth(width);
    }

    /**
     * to set stroke color (color around the rounded corner)
     * @param view target
     * @param color the given color
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public static void setCornerStrokeColor(View view, int color) {
        //vincent view.setCornerStrokeColor(color);
    }

    /**
     * to enable anti-alias
     * @param view target
     * @param aa true: enable anti-alias; false: disable anti-alias
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public static void setCornerAntiAliased(View view, boolean aa) {
        //vincent view.setCornerAntiAliased(aa);
    }
}
