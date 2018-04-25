
package com.htc.lib1.cc.htcFooter.test;

import android.content.Intent;
import android.view.View;

import com.htc.lib1.cc.htcFooter.activityhelper.FooterActivity;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.HtcFooterTextButton;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.EventUtil;
import com.htc.test.util.ScreenShotUtil;

public class FooterTextButtonTest extends HtcActivityTestCaseBase {
    public FooterTextButtonTest() {
        super(FooterActivity.class);
    }

    public final void test_light_noicon_selector() {
        test(R.layout.footertextbutton_light_noicon, R.id.footertextbutton_light_noicon, true);
    }

    public final void test_light_withicon_selector() {
        test(R.layout.footertextbutton_light_withicon, R.id.footertextbutton_light_withicon, true);
    }

    public final void test_dark_noicon_selector() {
        test(R.layout.footertextbutton_dark_noicon, R.id.footertextbutton_dark_noicon, true);
    }

    public final void test_dark_withicon_selector() {
        test(R.layout.footertextbutton_dark_withicon, R.id.footertextbutton_dark_withicon, true);
    }

    public final void test_trans_noicon_selector() {
        test(R.layout.footertextbutton_tran_noicon, R.id.footertextbutton_tran_noicon, true);
    }

    public final void test_trans_withicon_selector() {
        test(R.layout.footertextbutton_tran_withicon, R.id.footertextbutton_tran_withicon, true);
    }

    public final void test_light_noicon_unselector() {
        test(R.layout.footertextbutton_light_noicon, R.id.footertextbutton_light_noicon, false);
    }

    public final void test_light_withicon_unselector() {
        test(R.layout.footertextbutton_light_withicon, R.id.footertextbutton_light_withicon, false);
    }

    public final void test_dark_noicon_unselector() {
        test(R.layout.footertextbutton_dark_noicon, R.id.footertextbutton_dark_noicon, false);
    }

    public final void test_dark_withicon_unselector() {
        test(R.layout.footertextbutton_dark_withicon, R.id.footertextbutton_dark_withicon, false);
    }

    public final void test_trans_noicon_unselector() {
        test(R.layout.footertextbutton_tran_noicon, R.id.footertextbutton_tran_noicon, false);
    }

    public final void test_trans_withicon_unselector() {
        test(R.layout.footertextbutton_tran_withicon, R.id.footertextbutton_tran_withicon, false);
    }

    private void test(int layoutId, final int widgetId, boolean hasSelector) {
        Intent i = new Intent();
        i.putExtra("layoutId", layoutId);
        setActivityIntent(i);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
        getInstrumentation().waitForIdleSync();
        HtcFooterTextButton hftb = (HtcFooterTextButton) mActivity.findViewById(R.id.footertextbutton_left);
        if(null != hftb){
            hftb.useSelectorWhenPressed(hasSelector);
            EventUtil.callLongPressed(getInstrumentation(), hftb, new EventUtil.EventCallBack() {
                @Override
                public void onPressedStatus(View view) {
                    mSolo.sleep(4000);
                    ScreenShotUtil.AssertViewEqualBefore(mSolo, mSolo.getView(widgetId), FooterTextButtonTest.this);
                }
            });
            mSolo.clickLongOnView(hftb, 5000);
        }
    }
}
