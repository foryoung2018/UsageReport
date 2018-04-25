package com.htc.test.util;

import android.app.Instrumentation;
import android.graphics.Point;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public class EventUtil {
    public interface EventCallBack {
        void onPressedStatus(View view);
    }

    public static void callLongPressedByGravity(Instrumentation inst,
            View view, EventCallBack cb, int gravity) {
        final int touchSlop = ViewConfiguration.get(view.getContext())
                .getScaledTouchSlop();

        inst.waitForIdleSync();

        Point p = ViewUtil.getViewSpecPoint(view, gravity);

        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        MotionEvent event = MotionEvent.obtain(downTime, eventTime,
                MotionEvent.ACTION_DOWN, p.x, p.y, 0);
        inst.sendPointerSync(event);
        // inst.waitForIdleSync();

        try {
            Thread.sleep(ViewConfiguration.get(view.getContext())
                    .getTapTimeout());
        } catch (InterruptedException e) {
        }
        if (null != cb)
            cb.onPressedStatus(view);

        eventTime = SystemClock.uptimeMillis();
        event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP,
                p.x, p.y, 0);
        inst.sendPointerSync(event);
        // inst.waitForIdleSync();
    }

    public static void callLongPressed(Instrumentation inst, View view,
            EventCallBack cb) {
        callLongPressedByGravity(inst, view, cb, Gravity.CENTER);
    }
}
