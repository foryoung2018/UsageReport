
package com.htc.lib1.cc.checkablebutton.test.basic;

import android.graphics.Point;
import android.view.View;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.HtcRadioButton;
import com.htc.test.WidgetTestCaseBase;

public class BasicHtcRadioButtonTest extends WidgetTestCaseBase {
    private View mTargetView;

    public BasicHtcRadioButtonTest() {
        super(ActivityBase.class, HtcRadioButton.class);
    }

    @Override
    protected void setTargetViewByCustomer(View view) {
        super.setTargetViewByCustomer(view);
        mTargetView = view;
    }

    @Override
    protected void measureExpected(int widthMeasureSpec, int heightMeasureSpec) {
        Point size = new Point(widthMeasureSpec, heightMeasureSpec);
        BasicUtils.measureHtcCompoundExpectedSize(mTargetView, size, R.drawable.common_circle_outer, R.drawable.common_radio_rest_light);
        setExpectedMeasureDimension(size.x, size.y);
    }
}
