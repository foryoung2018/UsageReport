
package com.htc.lib1.cc.actionbar.test;

import android.app.ActionBar;
import android.view.View;

import com.htc.lib1.cc.widget.ActionBarContainer;
import com.htc.lib1.cc.widget.ActionBarExt;
import com.htc.lib1.cc.widget.ActionBarSearch;
import com.htc.test.HtcActivityTestCaseBase;

public class HtcActionBarActivityTestCase extends HtcActivityTestCaseBase {

    protected ActionBar mActionBar;
    protected ActionBarExt mActionBarExt;
    protected ActionBarContainer mActionBarContainer;
    protected ActionBarContainer mActionBarSearchContainer;
    protected ActionBarSearch mActionBarSearch;

    public HtcActionBarActivityTestCase(Class<?> activityClass) {
        super(activityClass);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityIntent(null);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
    }

    protected void initActionBarContainers() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mActionBar = mActivity.getActionBar();
                mActionBarExt = new ActionBarExt(mActivity, mActionBar);
                mActionBarContainer = mActionBarExt.getCustomContainer();
                mActionBarSearchContainer = mActionBarExt.getSearchContainer();
                if (null != mActionBarSearchContainer && null != mActionBarSearchContainer.getParent()) {
                    ((View) mActionBarSearchContainer.getParent()).setBackgroundResource(0);
                }
            }
        });
    }
}
