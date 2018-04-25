
package com.htc.lib1.cc.button.test.basic;

import android.view.View;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.widget.HtcIconButton;
import com.htc.test.WidgetTestCaseBase;

public class BasicHtcIconButtonTest extends WidgetTestCaseBase {
    private View mTargetView;

    public BasicHtcIconButtonTest() {
        super(ActivityBase.class, HtcIconButton.class);
    }

    @Override
    protected void measureExpected(int widthMeasureSpec, int heightMeasureSpec) {
        setExpectedMeasureDimension(mTargetView.getMeasuredWidth(), mTargetView.getMeasuredHeight());
    }

    @Override
    protected void setTargetViewByCustomer(View view) {
        mTargetView = view;
    }

    @Override
    protected void setUpTargetViewForTestState(View view) {
        ((HtcIconButton) view).setText("TEST STATE");
    }

}
