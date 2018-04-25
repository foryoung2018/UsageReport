
package com.htc.lib1.cc.checkablebutton.test.basic;

import android.graphics.Point;
import android.view.View;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.HtcLoveButton;
import com.htc.test.WidgetTestCaseBase;
import com.htc.test.util.NoRunTheme;

@NoRunTheme
public class BasicHtcLoveButtonTest extends WidgetTestCaseBase {
    private View mTargetView;

    public BasicHtcLoveButtonTest() {
        super(ActivityBase.class, HtcLoveButton.class);
    }

    @Override
    protected void setTargetViewByCustomer(View view) {
        super.setTargetViewByCustomer(view);
        mTargetView = view;
    }

    @Override
    protected void measureExpected(int widthMeasureSpec, int heightMeasureSpec) {
        Point size = new Point(widthMeasureSpec, heightMeasureSpec);
        BasicUtils.measureHtcCompoundExpectedSize(mTargetView, size, 0, R.drawable.common_collect_rest);
        setExpectedMeasureDimension(size.x, size.y);
    }
}
