package com.htc.lib1.cc.widget.reminder.drag;

import android.graphics.Point;
import android.view.MotionEvent;

import com.htc.lib1.cc.widget.reminder.debug.MyLog;

/** @hide */
public class TouchFilter {
    /** Need API8. filer the first Touch */
    private static final String TAG = "TouchFilter";

    private TouchListener mTouchListener;
    private static final int SENSITIVE = 1;
    private int m_pointId = -1;
    private boolean mPressed = false;
    private int mX = -1000;
    private int mY = -1000;
    private int mAction = -1;

    public interface TouchListener {
        public boolean onTouchEvent(MotionEvent event, int id, int x, int y);
    }

    public void setCallback(TouchListener touchListener) {
        synchronized(this) {
            mTouchListener = touchListener;
        }
    }

    public Point getPoint(int index) {
        Point point = new Point();
        return point;
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int actionMasked = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            mAction = -1;
            mX = -1000;
            mY = -1000;
            m_pointId = event.getPointerId(0);
            mPressed = true;
            return sendTouchEventToListener(MotionEvent.ACTION_DOWN, event);
        }
        else if (mPressed) {
            if (action == MotionEvent.ACTION_CANCEL
                    || action == MotionEvent.ACTION_UP) {
                mPressed = false;
                return sendTouchEventToListener(action, event);
            }
            else if (actionMasked == MotionEvent.ACTION_POINTER_UP) {
                int actionIndex = event.getActionIndex();
                int pointId = event.getPointerId(actionIndex);
                if (pointId == m_pointId) {
                    mPressed = false;
                    return sendTouchEventToListener(MotionEvent.ACTION_UP, event);
                }
                else {
                    return false;
                }
            }
            else if (action == MotionEvent.ACTION_MOVE) {
                return sendTouchEventToListener(action, event);
            }
        }
        return false;
    }

    public boolean sendTouchEventToListener(int action, MotionEvent event) {
        TouchListener listener = mTouchListener;
        if (listener == null) {
            return false;
        }
        if (m_pointId < 0) {
            return false;
        }

        int index = event.findPointerIndex(m_pointId);
        //add workaround for it
        if (index < 0 || index >= event.getPointerCount()) {
            //must some error from input dispatch
            MyLog.w(TAG, "sendTouchEventToListener not found pointId:" + m_pointId);
            int originalAction = event.getAction();
            int x = (int)event.getX();
            int y = (int)event.getY();
            if (originalAction == MotionEvent.ACTION_UP
                    || originalAction == MotionEvent.ACTION_CANCEL) {
                MotionEvent out = MotionEvent.obtain(event);
                out.setAction(MotionEvent.ACTION_CANCEL);
                listener.onTouchEvent(out, m_pointId, x, y);
            }
            return false;
        }
        int x = (int)event.getX(index);
        int y = (int)event.getY(index);
        if (mAction != action
                || Math.abs(x - mX) > SENSITIVE
                || Math.abs(y - mY) > SENSITIVE) {
            mX = x;
            mY = y;
            mAction = action;
            MotionEvent out = event;
            if (action == MotionEvent.ACTION_UP
                    || action == MotionEvent.ACTION_CANCEL) {
                out = MotionEvent.obtain(event);
                out.setAction(action);
            }
            return listener.onTouchEvent(out, m_pointId, x, y);
        }
        return false;
    }
}
