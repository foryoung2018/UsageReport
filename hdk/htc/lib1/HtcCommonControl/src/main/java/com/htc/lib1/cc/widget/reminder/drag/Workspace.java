package com.htc.lib1.cc.widget.reminder.drag;

import android.os.Bundle;

/** @hide */
public abstract class Workspace {
    /**
     * CallBack
     * @author htc
     */
    public interface GestureCallBack {
        /**
         * onGestureChanged
         * @param DraggableView view
         * @param GestureEvent event
         * @param bundle bundle
         * @return
         */
        Bundle onGestureChanged(DraggableView view, GestureEvent event, Bundle bundle);
    };

    /**
     * bind DragView
     * @param view DraggableView
     */
    public abstract void bindDragView(DraggableView view);
    /**
     * unbind DragView
     * @param view DraggableView
     */
    public abstract void unbindDragView(DraggableView view);

    /**
     * register CallBack
     * @param callback callback
     */
    public abstract void registerGestureCallBack(GestureCallBack callback);
    /**
     * unregister CallBack
     * @param callback callback
     */
    public abstract void unregisterGestureCallBack(GestureCallBack callback);
}
