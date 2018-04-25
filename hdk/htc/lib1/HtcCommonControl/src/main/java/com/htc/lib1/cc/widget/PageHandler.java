package com.htc.lib1.cc.widget;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.htc.lib1.cc.widget.MultiPageLayout.OnPageAnimateListener;

/**
 *  @hide
 */
public class PageHandler {
    static final String TAG = "PageHandler";
    private MultiPageLayout mMultipageLayout;

    public PageHandler(MultiPageLayout pageLayout) {
        mMultipageLayout = pageLayout;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void addPage(View view) {
        mMultipageLayout.addPage(view);
    }

    /**
     * add a page at specific position in the group
     * Will scroll the group so that the added item won't affect the display
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void addPage(View view, int index) {
        mMultipageLayout.addPage(view, index);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void addPage(View view, int index, boolean shift) {
        mMultipageLayout.addPage(view, index, shift);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void removePage(int index) {
        mMultipageLayout.removePage(index);
    }


/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setPagesParams(ViewGroup.LayoutParams[] sizes,
                    int[][] paddings) {
        mMultipageLayout.setPagesParams(sizes, paddings);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setPagesSize(ViewGroup.LayoutParams[] portraitSizes,
                    ViewGroup.LayoutParams[] landscapeSizes) {
        mMultipageLayout.setPagesSize(portraitSizes, landscapeSizes);
    }

    /**
     * run an animation to let the next page to show
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void animateToNextPage() {
        mMultipageLayout.animateToNextPage();
    }

    /**
     * run an animation to let the previous page to show
     */
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void animateBackPreviousPage() {
        mMultipageLayout.animateBackPreviousPage();
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void rotationChanged() {
        mMultipageLayout.rotationChanged();
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setOnPageAnimateListener(OnPageAnimateListener l) {
        mMultipageLayout.setOnPageAnimateListener(l);
    }
}
