
package com.htc.lib1.cc.checkablebutton.test.basic;

import android.graphics.Point;
import android.view.View;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.HtcCheckBox;
import com.htc.test.WidgetTestCaseBase;
import com.htc.test.util.NoRunTheme;

@NoRunTheme
public class BasicHtcCheckBoxTest extends WidgetTestCaseBase {
    private View mTargetView;

    public BasicHtcCheckBoxTest() {
        super(ActivityBase.class, HtcCheckBox.class);
    }

    @Override
    protected void setTargetViewByCustomer(View view) {
        super.setTargetViewByCustomer(view);
        mTargetView = view;
    }

    @Override
    protected void measureExpected(int widthMeasureSpec, int heightMeasureSpec) {
        Point size = new Point(widthMeasureSpec, heightMeasureSpec);
        BasicUtils.measureHtcCompoundExpectedSize(mTargetView, size, R.drawable.common_circle_pressed, R.drawable.common_checkbox_on);
        setExpectedMeasureDimension(size.x, size.y);
    }
}
