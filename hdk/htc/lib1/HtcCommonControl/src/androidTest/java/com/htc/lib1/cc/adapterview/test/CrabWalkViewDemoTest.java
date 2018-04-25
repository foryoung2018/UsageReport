
package com.htc.lib1.cc.adapterview.test;

import android.view.KeyEvent;
import android.widget.TextView;

import com.htc.lib1.cc.adapterview.activityhelper.CrabWalkViewDemo;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.CrabWalkView;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;

public class CrabWalkViewDemoTest extends HtcActivityTestCaseBase {
    CrabWalkView mCrabWalkView = null;

    public CrabWalkViewDemoTest() {
        super(CrabWalkViewDemo.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
    }

    public void testDefaultSnapShot() {
        mCrabWalkView = (CrabWalkView) getActivity().findViewById(R.id.cwv);
        mSolo.waitForView(mCrabWalkView);
        mSolo.sleep(1000);
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mCrabWalkView, this);
    }

    public void testFocusException() {
        final TextView tv = (TextView) mActivity.findViewById(R.id.tv);
        getInstrumentation().waitForIdle(new Runnable() {
            public void run() {
                tv.requestFocus();
            }
        });
        mSolo.sleep(1000);
        mSolo.sendKey(KeyEvent.KEYCODE_TAB);
    }
}
