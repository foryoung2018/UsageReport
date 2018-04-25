package com.htc.lib1.cc.view;

/**
 * FocusSelection
 */
public interface FocusSelection {

    /**
     * Calculate the next selected position after 'left' keypad has been pressed
     * @param position
     * @return -1 for default mechanism
     * @hide
     */
    public int left(int position);

    /**
     * Calculate the next selected position after 'right' keypad has been pressed
     *
     * @param position
     * @return -1 for default mechanism
     * @hide
     */
    public int right(int position);

    /**
     * Calculate the next selected position after 'top' keypad has been pressed
     *
     * @param position
     * @return -1 for default mechanism
     * @hide
     */
    public int top(int position);

    /**
     * Calculate the next selected position after 'bottom' keypad has been pressed
     *
     * @param position
     * @return -1 for default mechanism
     * @hide
     */
    public int bottom(int position);
}
