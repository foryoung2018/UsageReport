package com.htc.lib1.cc.widget;

public interface OnPullDownListener {
    /**
     * Callback method to be invoked when the list or grid is pulled down to the boundary.
     */
    public void onPullDownToBoundary();

   /**
     * Callback method to be invoked when the list or grid items are recovered to original positions.
     */
    public void onPullDownFinish();

    /**
     * Callback method to be invoked when the pull-down action is cancelled.
     * (For Example, users pull down the list and slowly push the items back to original positions)
     */
    public void onPullDownCancel();

    /**
     * Callback method to be invoked when the list or grid is pulled down and the gap between items is changed.
     * @param gap The current gap between items when the list or grid is pulled down.
     * @param maxGap The maximum gap between items when the list or grid is pulled down.
     */
    public void onGapChanged(int gap, int maxGap);

   /**
     * Callback method to be invoked when the list or grid has been pulled down to the boundary and users release the finger.
     */
    public void onPullDownRelease();
}
