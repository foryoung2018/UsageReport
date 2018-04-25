
package com.htc.lib1.cc.layout.test;

import android.content.Intent;

import com.htc.lib1.cc.layout.activityhelper.AscentAlignLayoutActivity;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.AscentAlignLayout;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;

public class AscentAlignLayoutTest extends HtcActivityTestCaseBase {
    public AscentAlignLayoutTest() {
        super(AscentAlignLayoutActivity.class);
    }

    private void setActivityIntent(int Id) {
        Intent i = new Intent();
        i.putExtra(AscentAlignLayoutActivity.LAYOUT_ID, Id);
        setActivityIntent(i);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
        getInstrumentation().waitForIdleSync();
    }

    public void test1Basic2AlignBasic() {
        setActivityIntent(R.layout.ascentalign_1basic2alignbasic);
        ScreenShotUtil.AssertViewEqualBefore(mSolo, getAscentAlignLayout(R.id.alignbasic_basic_alignbasic), this);
    }

    public void test1Basic1AlignBasic1Other() {
        setActivityIntent(R.layout.ascentalign_1basic1alignbasic1other);
        ScreenShotUtil.AssertViewEqualBefore(mSolo, getAscentAlignLayout(R.id.basic_alignbasic_other), this);
    }

    public void testBasicViewGone() {
        setActivityIntent(R.layout.ascentalign_basicviewgone);
        ScreenShotUtil.AssertViewEqualBefore(mSolo, getAscentAlignLayout(R.id.basicviewgone), this);
    }

    public void testAlignBasicViewGone() {
        setActivityIntent(R.layout.ascentalign_alignbasicviewgone);
        ScreenShotUtil.AssertViewEqualBefore(mSolo, getAscentAlignLayout(R.id.alignbasicviewgone), this);
    }

    public void test2ViewGroup() {
        setActivityIntent(R.layout.ascentalign_2viewgroup);
        ScreenShotUtil.AssertViewEqualBefore(mSolo, getAscentAlignLayout(R.id.viewgroup_viewgroup), this);
    }

    private AscentAlignLayout getAscentAlignLayout(int id) {
        return (AscentAlignLayout) mActivity.findViewById(id);
    }

}
