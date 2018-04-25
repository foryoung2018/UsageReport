package com.htc.lib1.cc.view.util;

import android.widget.ListAdapter;

/**
 * ProxyListAdapter
 */
abstract public class ProxyListAdapter implements ListAdapter {

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    protected ListAdapter target;

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setTarget(ListAdapter target) {
        this.target = target;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public ListAdapter getTarget() {
        return this.target;
    }

}
