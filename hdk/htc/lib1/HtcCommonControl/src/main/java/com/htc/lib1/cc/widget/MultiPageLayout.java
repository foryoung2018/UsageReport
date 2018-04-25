package com.htc.lib1.cc.widget;

import android.view.View;
import android.view.ViewGroup;

public interface MultiPageLayout {
    /**
     * add a page to be the last child in the group
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void addPage(View view);

    /**
     * add a page at specific position in the group
     * Will scroll the group so that the added item won't affect the display
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void addPage(View view, int index);

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void addPage(View view, int index, boolean shift);

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void removePage(int index);


/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setPagesParams(ViewGroup.LayoutParams[] sizes,
                    int[][] paddings);

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setPagesSize(ViewGroup.LayoutParams[] portraitSizes,
                    ViewGroup.LayoutParams[] landscapeSizes);

    /**
     * run an animation to let the next page to show
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void animateToNextPage();

    /**
     * run an animation to let the previous page to show
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void animateBackPreviousPage();

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void rotationChanged();

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setOnPageAnimateListener(OnPageAnimateListener l);

    public interface OnPageAnimateListener {
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public void onToNextPage();
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
        public void onBackPreviousPage();
    }
}
