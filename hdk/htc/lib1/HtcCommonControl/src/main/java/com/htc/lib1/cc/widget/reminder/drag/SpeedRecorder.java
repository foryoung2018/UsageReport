package com.htc.lib1.cc.widget.reminder.drag;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.widget.reminder.debug.MyLog;
import com.htc.lib1.cc.widget.reminder.util.MyUtil;

/** @hide */
public class SpeedRecorder {

    private static final String TAG = "SpeedRecorder";

    private static boolean sInit = false;
    private static float sDistance = 200;
    private static int sMinY = 50;

    private int mStartX = 0;
    private int mStartY = 0;

    private int mEndX = 0;
    private int mEndY = 0;
    private boolean mOnlyVerical = false;
    public static void init(Context context) {
        if (!sInit) {
            sInit = true;
            Resources res = MyUtil.getResourceFormResApp(context);
            int distanceMM = 0;
            if (res != null) {
//                distanceMM = res.getInteger(
//                        MyUtil.getIdFromRes(context, ReminderResWrap.INTEGER_UNLOCK_DISTANCE_MM));
                distanceMM = res.getInteger(R.integer.unlock_distance_mm);
//                sDistance = res.getDimensionPixelSize(MyUtil.getIdFromRes(context, ReminderResWrap.DIMEN_UNLOCK_DISTANCE));
                sDistance = res.getDimensionPixelSize(R.dimen.unlock_distance);
                if (sDistance <= 0) {
                    sDistance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, distanceMM,
                        res.getDisplayMetrics());
                }
                sDistance = sDistance * sDistance;
//                sMinY = res.getDimensionPixelSize(MyUtil.getIdFromRes(context, ReminderResWrap.DIMEN_UNLOCK_MINY));
                sMinY = res.getDimensionPixelSize(R.dimen.unlock_minY);
            }
        }
    }

    public int getMinY() {
        return sMinY;
    }

    public void start(int x, int y) {
        mStartX = x;
        mStartY = y;
        mOnlyVerical = false;
        MyLog.i(TAG, "start mStartX:" + mStartX
                + " mStartY:" + mStartY);
    }

    public void setVerticalOnly(boolean verticalOnly) {
        mOnlyVerical = verticalOnly;
    }

    public void move(int x, int y) {
        updatePosition(x, y);
    }

    public boolean end(int x, int y) {
        updatePosition(x, y);
        mEndX = x;
        mEndY = y;
        MyLog.i(TAG, "end mEndX:" + mEndX
                + " mEndY:" + mEndY);
        return isDrop();
    }

    public boolean isDrop() {
        boolean drop = false;
        int disX = 0;
        if (!mOnlyVerical) {
            disX = mEndX - mStartX;
        }
        int disY = mEndY - mStartY;

        if (disY < 0) {
            if (sMinY > mEndY) {
                drop = true;
            } else {
                int distance = (disY * disY) + (disX * disX);
                if (distance > sDistance) {
                    drop = true;
                }
            }
        }
        MyLog.i(TAG, "isDrop " + drop
                + " disX:" + disX
                + " disY:" + disY
                + " mOnlyVerical:" + mOnlyVerical
                + " sDistance:" + sDistance
                + " sMinY:" + sMinY
                + " mEndY:" + mEndY);

        return drop;

    }

    private void updatePosition(int x, int y) {
    }
}
