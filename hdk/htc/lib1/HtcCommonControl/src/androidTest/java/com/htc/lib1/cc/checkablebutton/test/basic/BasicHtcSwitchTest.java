
package com.htc.lib1.cc.checkablebutton.test.basic;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.MeasureSpec;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.HtcSwitch;
import com.htc.test.WidgetTestCaseBase;

public class BasicHtcSwitchTest extends WidgetTestCaseBase {
    private View mTargetView;
    private Drawable mSwitchDrawable;

    public BasicHtcSwitchTest() {
        super(ActivityBase.class, HtcSwitch.class);
    }

    @Override
    protected void measureExpected(int widthMeasureSpec, int heightMeasureSpec) {
        final Resources res = getInstrumentation().getTargetContext().getResources();
        mSwitchDrawable = res.getDrawable(R.drawable.common_switch_rest);

        int widthExpected = calculateSize(widthMeasureSpec, mSwitchDrawable.getIntrinsicWidth() / 2);
        int heightExpected = calculateSize(heightMeasureSpec, mSwitchDrawable.getIntrinsicHeight());

        setExpectedMeasureDimension(BasicUtils.getFixedMeasureSize(widthExpected), BasicUtils.getFixedMeasureSize(heightExpected));
    }

    @Override
    protected void setTargetViewByCustomer(View view) {
        mTargetView = view;
    }

    private int calculateSize(int measureSpec, int drawableSzie) {
        return (MeasureSpec.getMode(measureSpec) == MeasureSpec.EXACTLY) ? Math.max(MeasureSpec.getSize(measureSpec), drawableSzie) : drawableSzie;
    }

}
