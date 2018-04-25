
package com.htc.lib1.cc.checkablebutton.test.basic;

import android.graphics.Point;
import android.view.View;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.HtcFlagButton;
import com.htc.test.WidgetTestCaseBase;

public class BasicHtcFlagButtonTest extends WidgetTestCaseBase {
    private View mTargetView;

    public BasicHtcFlagButtonTest() {
        super(ActivityBase.class, HtcFlagButton.class);
    }

    @Override
    protected void setTargetViewByCustomer(View view) {
        super.setTargetViewByCustomer(view);
        mTargetView = view;
    }

    @Override
    protected void measureExpected(int widthMeasureSpec, int heightMeasureSpec) {
        Point size = new Point(widthMeasureSpec, heightMeasureSpec);
        BasicUtils.measureHtcCompoundExpectedSize(mTargetView, size, 0, R.drawable.common_flag_on);
        setExpectedMeasureDimension(size.x, size.y);
    }
}
