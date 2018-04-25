
package com.htc.test.util;

import android.graphics.Point;
import android.view.Gravity;
import android.view.View;

import com.robotium.solo.Solo;

import junit.framework.TestCase;

public class ViewUtil {
    public static Point getViewSpecPoint(View view, int gravity) {
        if (null == view)
            return null;

        int[] xy = new int[2];
        view.getLocationOnScreen(xy);

        float x = xy[0] + (view.getWidth() >> 1);
        float y = xy[1] + (view.getHeight() >> 1);
        switch (gravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.LEFT:
                x = xy[0];
                break;
            case Gravity.RIGHT:
                x = xy[0] + (view.getWidth() - 1);
                break;
            case Gravity.CENTER_HORIZONTAL:
            default:
                x = xy[0] + (view.getWidth() >> 1);
                break;
        }
        switch (gravity & Gravity.VERTICAL_GRAVITY_MASK) {
            case Gravity.TOP:
                y = xy[1];
                break;
            case Gravity.BOTTOM:
                y = xy[1] + (view.getHeight() - 1);
                break;
            case Gravity.CENTER_VERTICAL:
            default:
                y = xy[1] + (view.getHeight() >> 1);
                break;
        }
        return new Point((int) x, (int) y);
    }

    public static void AssertViewSizeAndLocationEqualBefore(Solo solo,
            final View view, TestCase testcase) {

        int width = view.getWidth();
        int height = view.getHeight();
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];

        StringBuffer sb = new StringBuffer();
        sb.append(width).append("#").append(height).append("#").append(x)
                .append("#").append(y);

        FileUtil.AssertInfoEqualBefore(solo, view, sb.toString(), testcase);
    }

}
