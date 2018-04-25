
package com.htc.lib1.cc.checkablebutton.test.basic;

import android.graphics.Point;
import android.view.View;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.HtcStarButton;
import com.htc.test.WidgetTestCaseBase;

public class BasicHtcStarButtonTest extends WidgetTestCaseBase {
    private View mTargetView;

    public BasicHtcStarButtonTest() {
        super(ActivityBase.class, HtcStarButton.class);
    }

    @Override
    protected void setTargetViewByCustomer(View view) {
        super.setTargetViewByCustomer(view);
        mTargetView = view;
    }

    @Override
    protected void measureExpected(int widthMeasureSpec, int heightMeasureSpec) {
        Point size = new Point(widthMeasureSpec, heightMeasureSpec);
        BasicUtils.measureHtcCompoundExpectedSize(mTargetView, size, 0, R.drawable.common_rating_rest);
        setExpectedMeasureDimension(size.x, size.y);
    }

}
