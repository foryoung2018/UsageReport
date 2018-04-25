
package com.htc.lib1.cc.layout.test;

import com.htc.lib1.cc.layout.activityhelper.WeekLayoutActivity;
import com.htc.lib1.cc.widget.ListItem;
import com.htc.lib1.cc.widget.WeekLayout;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;
import com.htc.lib1.cc.test.R;

public class WeekLayoutTest extends HtcActivityTestCaseBase {

    public WeekLayoutTest() {
        super(WeekLayoutActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
    }

    public void testDefault() {
        final WeekLayout weekLayout = (WeekLayout) getActivity().findViewById(R.id.weeklayout_default);
        ScreenShotUtil.AssertViewEqualBefore(mSolo, weekLayout, this);
    }

    public void testCustom() {
        final WeekLayout weekLayout = (WeekLayout) getActivity().findViewById(R.id.weeklayout_custom);
        ScreenShotUtil.AssertViewEqualBefore(mSolo, weekLayout, this);
    }

    public void testInListItem() {
        final ListItem item = (ListItem) getActivity().findViewById(R.id.listitem);
        ScreenShotUtil.AssertViewEqualBefore(mSolo, item, this);
    }
}
