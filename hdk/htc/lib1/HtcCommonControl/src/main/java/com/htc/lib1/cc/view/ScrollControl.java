package com.htc.lib1.cc.view;

import android.view.View;

/**
 * This is used to help TableColleague to control scroll behavior of TableView.
 */
public interface ScrollControl {

    /**
     * A class used to represent the center view of the table.
     */
    public class CenterView {
        /**
         * which view shall be placed in the center of a ViewGroup
         */
        public View view;

        /**
         * which percentage of the center view shall be placed in the center of a ViewGroup
         */
        public int percentage;
    }

    /**
     * Get the view that you want to put this view of horizontal center or vertical center of parent.
     * In TableLayoutParams.VERTICAL, TableView will scroll the view you return to the vertical center of parent, it will not scroll horizontal direction.
     * In TableLayoutParams.HORIZONTAL, TableView will scroll the view you return to the horizontal center of parent, it will not scroll vertical direction.
     *
     * @param visibleViews All the visible views in TableView
     * @param startPosition Index of the view at the most top of the table
     * @return The view you want to move to center.
     */
    public CenterView getCenterView(View[] visibleViews, int startPosition);
}
