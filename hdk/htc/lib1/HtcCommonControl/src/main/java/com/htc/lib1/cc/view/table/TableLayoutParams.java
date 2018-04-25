package com.htc.lib1.cc.view.table;

/**
 * A layout for TableView.
 */
public class TableLayoutParams {

    /**
     * Horizontal layout
     */
    public static final int HORIZONTAL = 0;
    /**
     * Vertical layout
     */
    public static final int VERTICAL = 1;

    /**
     * The direction of scrolling function
     */
    private int mOrientation = HORIZONTAL;
    private boolean isScrollOverBoundary = true;
    private boolean initialWithScrollControl = true;

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public int getOrientation() {
        return mOrientation;
    }

        /**
         * To assign the orientation of this table.
         * @param orientation Constant to indicate the orientation
         */
    public void setOrientation(int orientation) {
        this.mOrientation = orientation;
    }

        /**
         * To enable or disable the over scroll feature of this table.
         * @param enable Set true to enable overscroll, false to disable
         */
    public void enableScrollOverBoundary(boolean enable){
        isScrollOverBoundary = enable;
    }

        /**
         * To get the enable/disable status of over scroll feature.
         * @return true for enabled, false for disabled
         * @hide
         */
    public boolean isScrollOverBoundary(){
        return isScrollOverBoundary;
    }

        /**
         * To set if initialWithScrollControl.
         * @param yes Set true to init with ScrollControl
         */
    public void initialWithScrollControl(boolean yes){
        initialWithScrollControl = yes;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public boolean isInitialWithScrollControl(){
        return initialWithScrollControl;
    }

}
