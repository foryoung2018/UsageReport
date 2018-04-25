
package com.htc.lib1.cs.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;
import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * Special relative layout that dispatches touch event to all children.
 */
public class GroupClickRelativeLayout extends RelativeLayout {
    private HtcLogger mLogger = new CommLoggerFactory(this).create();
    private int mSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop() + 1;

    public GroupClickRelativeLayout(Context context) {
        super(context);
    }

    public GroupClickRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GroupClickRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        mLogger.verbose(ev);
        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);

            /*
             * Lie to the view that it has been touched if point in view.
             */
            if (pointInView(event.getX(), event.getY())) {
                event.setLocation(0, 0);
                event.offsetLocation(0, 0);
            }
            v.onTouchEvent(event);
        }
        return true;
    }

    private boolean pointInView(float x, float y) {
        return x >= (0 - mSlop) && x < (getRight() - getLeft() + mSlop) &&
                y >= (0 - mSlop) && y < (getBottom() - getTop() + mSlop);
    }

}
