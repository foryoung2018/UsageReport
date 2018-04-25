
package com.htc.lib1.cc.widget;

/**
 * A divider controller is used to control how to draw the divider below/above
 * the list item at each position.
 */
public interface IDividerController {
    /**
     * Not draw the divider.
     */
    public static final int DIVIDER_TYPE_NONE = 0;

    /**
     * Draw the divider set from {@link HtcListView#setDivider()}.
     */
    public static final int DIVIDER_TYPE_NORAML = 1;

    /**
     * Draw the section divider.
     */
    public static final int DIVIDER_TYPE_SECTION = 2;

    /**
     * Draw the section divider in automotive mode.
     */
    public static final int DIVIDER_TYPE_SECTION_AUTOMOTIVE = 3;

    /**
     * Returns the divider type which should be drawn below (above if stack from
     * bottom) the list item at the specified position. The divider types
     * include {@link #DIVIDER_TYPE_NONE}, {@link #DIVIDER_TYPE_NORAML},
     * {@link #DIVIDER_TYPE_SECTION}.
     *
     * @param position The position of the item in the adapter.
     * @return The divider type.
     */
    public int getDividerType(int position);
}
