package com.htc.lib1.cc.widget.reminder.drag;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation.AnimationListener;

/**
 * DragAnimation
 */
public class DragAnimation {
    /** @hide */
    public static final String KEY_ORIGINAL_X      = "OriginalX";      // Original View X Position.
    /** KEY_ORIGINAL_Y */
    public static final String KEY_ORIGINAL_Y      = "OriginalY";      // Original View Y Position.
    /** @hide */
    public static final String KEY_DRAG_END_X      = "DragEndX";       // Finger X position.
    /** @hide */
    public static final String KEY_DRAG_END_Y      = "DragEndY";       // Finger Y position.
    /** @hide */
    public static final String KEY_DRAGVIEW_WIDTH  = "DragViewWidth";  // Drag View Width.
    /** @hide */
    public static final String KEY_DRAGVIEW_HEIGHT = "DragViewHeight"; // Drag View Height.
    /** KEY_DRAGVIEW_TOP */
    public static final String KEY_DRAGVIEW_TOP    = "DragViewTop";    // Drag View X Position.
    /** @hide */
    public static final String KEY_DRAGVIEW_LEFT   = "DragViewLeft";   // Drag View Y Position.
    /** @hide */
    public static final String KEY_DRAG_HORIZONTAL = "DragHorizontal"; // Drag Horizontal.
    /** @hide */
    public static final String KEY_DRAG_VERTICAL   = "DragVertical";   // Drag Vertical.
    /** @hide */
    public static final String KEY_STATUSBAR_HEIGHT   = "StatusBarHeight"; // Status Bar Height.
    /** @hide */
    public static final String KEY_CLICK_VIBRATION    = "vibration";       // Click Vibration.

    /**
     * play Drop Animation
     * @param view view
     * @param listener listener
     * @param extras extras
     * @return false
     */
    public boolean playDropAnimation(View view, AnimationListener listener, Bundle extras) {
        return false;
    }
    /**
     * play Drag Back Animation
     * @param view view
     * @param listener listener
     * @param extras extras
     * @return false
     */
    public boolean playDragBackAnimation(View view, AnimationListener listener, Bundle extras) {
        return false;
    }
    /**
     * play Click Animation
     * @param view view
     * @param listener listener
     * @param extras extras
     * @return false
     */
    public boolean playClickAnimation(View view, AnimationListener listener, Bundle extras) {
        return false;
    }

}
