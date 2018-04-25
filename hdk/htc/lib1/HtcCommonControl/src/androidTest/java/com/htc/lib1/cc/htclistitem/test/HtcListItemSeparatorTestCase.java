package com.htc.lib1.cc.htclistitem.test;

import junit.framework.TestCase;
import android.content.Intent;
import android.util.Log;
import com.htc.lib1.cc.htclistitem.activityhelper.HtcListItemSeparatorActivity;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.HtcListItemSeparator;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;

public class HtcListItemSeparatorTestCase extends HtcActivityTestCaseBase {
    public HtcListItemSeparatorTestCase() {
        super(HtcListItemSeparatorActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Log.e("HtcListItemSeparatorDemoTest", "setUp");

    }

    private void setActivityIntent(int Id) {
        Intent i = new Intent();
        i.putExtra("layoutid", Id);
        setActivityIntent(i);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
        getInstrumentation().waitForIdleSync();
    }

    public void test1Text() {
        setActivityIntent(R.layout.separator_1_text);
        screenShot(this);
    }

    public void test1TextDarkList() {
        setActivityIntent(R.layout.separator_1_text_dark);
        screenShot(this);
    }

    public void test2Text() {
        setActivityIntent(R.layout.separator_2_text);
        screenShot(this);
    }

    public void test2TextDarkList() {
        setActivityIntent(R.layout.separator_2_text_dark);
        screenShot(this);
    }

    public void test3Text() {
        setActivityIntent(R.layout.separator_3_text);
        screenShot(this);
    }

    public void test3TextDarkList() {
        setActivityIntent(R.layout.separator_3_text_dark);
        screenShot(this);
    }

    public void testTextAndImageButtom() {
        setActivityIntent(R.layout.separator_text_imagebutton);
        screenShot(this);
    }

    public void testTextAndImageButtonDarkList() {
        setActivityIntent(R.layout.separator_text_imagebutton_dark);
        screenShot(this);
    }

    public void testIconAndText() {
        setActivityIntent(R.layout.separator_text_icon);
        screenShot(this);
    }

    public void testIconAndTextDarkList() {
        setActivityIntent(R.layout.separator_text_icon_dark);
        screenShot(this);
    }

    public void testTextPopupMenuMode() {
        setActivityIntent(R.layout.separator_1_text_popupmenu);
        screenShot(this);
    }

    public void testSetSeparatorWithPowerBy() {
        setActivityIntent(R.layout.separator_text_icon_dark);
        screenShot(this, true);
    }

    private void screenShot(TestCase test) {
        screenShot(test, false);
    }

    private void screenShot(TestCase test, boolean isSetSeparatorWithPowerBy) {
        final HtcListItemSeparator hlis = (HtcListItemSeparator) mActivity.findViewById(R.id.sep_0);
        if (isSetSeparatorWithPowerBy) {
            try {
                runTestOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hlis.setSeparatorWithPowerBy();
                    }
                });
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        ScreenShotUtil.AssertViewEqualBefore(mSolo, hlis, test);
    }

}
