
package com.htc.lib1.cc.reminderview.test;

import com.htc.lib1.cc.reminderview.activityhelper.TestActivityVideoCall;
import com.htc.test.HtcActivityTestCaseBase;

public class ReminderViewTestCase extends HtcActivityTestCaseBase {

    public ReminderViewTestCase() {
        super(TestActivityVideoCall.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        initActivity();

    }

    public void testReminderView() {

        assertEquals(true, true);
    }
}
