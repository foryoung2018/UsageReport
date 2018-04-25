
package com.htc.lib1.cc.dialogpicker.test;

import android.widget.LinearLayout;
import com.htc.lib1.cc.dialogpicker.activityhelper.DataActionPickerDemo;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;

public class DataActionPickerTestCase extends HtcActivityTestCaseBase {

    public DataActionPickerTestCase() {
        super(DataActionPickerDemo.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        initActivity();
    }

    @Override
    protected void tearDown() throws Exception {
        final DataActionPickerDemo instance = (DataActionPickerDemo) getActivity();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                instance.dismissWindow();
            }
        });
        super.tearDown();
    }

    public void testOne() {
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mSolo.getView(LinearLayout.class, 0), this);
    }

    public final void testImproveCoverage() {
        try {
            runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((DataActionPickerDemo) mActivity).improveCoverage();
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
