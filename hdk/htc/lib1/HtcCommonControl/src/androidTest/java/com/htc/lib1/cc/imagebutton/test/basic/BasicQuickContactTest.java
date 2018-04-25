
package com.htc.lib1.cc.imagebutton.test.basic;

import android.view.View;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.QuickContactBadge;
import com.htc.test.WidgetTestCaseBase;

public class BasicQuickContactTest extends WidgetTestCaseBase {
    private View mTargetView;

    public BasicQuickContactTest() {
        super(ActivityBase.class, QuickContactBadge.class);
    }

    @Override
    protected void measureExpected(int widthMeasureSpec, int heightMeasureSpec) {
        setExpectedMeasureDimension(View.resolveSize(mTargetView.getMinimumWidth(), widthMeasureSpec), View.resolveSize(mTargetView.getMinimumHeight(), heightMeasureSpec));
    }

    @Override
    protected void setTargetViewByCustomer(View view) {
        mTargetView = view;
    }

    @Override
    protected void setUpTargetViewForTestState(View view) {
        QuickContactBadge quickContactBadge = (QuickContactBadge) view;
        quickContactBadge.setImageResource(R.drawable.icon_category_photo);
        quickContactBadge.assignContactFromEmail("a@b.c.com", true);
    }

}
