package com.htc.lib1.cc.widget;

import android.view.View;

import com.htc.lib1.cc.view.ScrollControl;
import com.htc.lib1.cc.view.table.TableLayoutParams;
import com.htc.lib1.cc.view.table.TableView;

/**
 * TableViewScrollControl
 */
public class TableViewScrollControl implements ScrollControl {

    private TableView mTableView;

    private int mOrientation;

/**
 * Gets the current center view of the table.
 * @param visibleViews views in the table.
 * @param startPosition the position of the first view.
 * @return CenterView object represents the current center view of the table.
 * @hide
 */
    public CenterView getCenterView(View[] visibleViews,
            int startPosition) {

        // If there is no visible views we will return null.
        if(visibleViews == null || visibleViews.length <= 0) return null;

        // Record the closestView and the distance between the view's center and parent center.
        View closestView = visibleViews[0];
        int closestViewCenterFromParentCenter = Integer.MAX_VALUE;

        int halfParentWidthOrHeight;
        if(mOrientation == TableLayoutParams.HORIZONTAL){
            halfParentWidthOrHeight = mTableView.getWidth() / 2;
            for(View child : visibleViews){
                int childCenterFromFromParentCenter = Math.abs(halfParentWidthOrHeight - (child.getLeft() + child.getRight()) / 2);
                // Compare which child is the closest to the center of parent.
                if(childCenterFromFromParentCenter < closestViewCenterFromParentCenter){
                    closestViewCenterFromParentCenter = childCenterFromFromParentCenter;
                    closestView = child;
                }
            }
        }
        else{
            halfParentWidthOrHeight = mTableView.getHeight() / 2;
            //Log.e("scroll", "mTableView.getHeight() = " + mTableView.getHeight());
            int i = 0;
            int center = 0;
            for(View child : visibleViews){
                int childCenterFromFromParentCenter = Math.abs(halfParentWidthOrHeight - (child.getTop() + child.getBottom()) / 2);

                // Compare which child is the closest to the center of parent.
                if(childCenterFromFromParentCenter < closestViewCenterFromParentCenter){
                    closestViewCenterFromParentCenter = childCenterFromFromParentCenter;
                    closestView = child;
                    center = i;
                }
                ++i;
            }
        }

        //initial centerView here.
        CenterView centerView = new CenterView();
        centerView.view = closestView;
        centerView.percentage = 50;

        return centerView;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setOrientation(int orientation){
        mOrientation = orientation;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setTableView(TableView tableView){
        mTableView = tableView;
    }


}
