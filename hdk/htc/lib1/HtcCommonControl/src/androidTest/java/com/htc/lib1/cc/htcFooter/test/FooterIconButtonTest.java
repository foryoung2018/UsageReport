
package com.htc.lib1.cc.htcFooter.test;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.htc.lib1.cc.htcFooter.activityhelper.FooterActivity;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.HtcFooterIconButton;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.EventUtil;
import com.htc.test.util.ScreenShotUtil;

public class FooterIconButtonTest extends HtcActivityTestCaseBase {
    public FooterIconButtonTest() {
        super(FooterActivity.class);
    }

    public final void test_light_noicon_selector() {
        test(R.layout.footericonbutton_light_noicon, R.id.footericonbutton_light_noicon, true);
    }

    public final void test_light_withicon_selector() {
        test(R.layout.footericonbutton_light_withicon, R.id.footericonbutton_light_withicon, true);
    }

    public final void test_trans_noicon_selector() {
        test(R.layout.footericonbutton_tran_noicon, R.id.footericonbutton_tran_noicon, true);
    }

    public final void test_trans_withicon_selector() {
        test(R.layout.footericonbutton_tran_withicon, R.id.footericonbutton_tran_withicon, true);
    }

    public final void test_dark_noicon_selector() {
        test(R.layout.footericonbutton_dark_noicon, R.id.footericonbutton_dark_noicon, true);
    }

    public final void test_dark_withicon_selector() {
        test(R.layout.footericonbutton_dark_withicon, R.id.footericonbutton_dark_withicon, true);
    }

    public final void test_light_noicon_unselector() {
        test(R.layout.footericonbutton_light_noicon, R.id.footericonbutton_light_noicon, false);
    }

    public final void test_light_withicon_unselector() {
        test(R.layout.footericonbutton_light_withicon, R.id.footericonbutton_light_withicon, false);
    }

    public final void test_trans_noicon_unselector() {
        test(R.layout.footericonbutton_tran_noicon, R.id.footericonbutton_tran_noicon, false);
    }

    public final void test_trans_withicon_unselector() {
        test(R.layout.footericonbutton_tran_withicon, R.id.footericonbutton_tran_withicon, false);
    }

    public final void test_dark_noicon_unselector() {
        test(R.layout.footericonbutton_dark_noicon, R.id.footericonbutton_dark_noicon, false);
    }

    public final void test_dark_withicon_unselector() {
        test(R.layout.footericonbutton_dark_withicon, R.id.footericonbutton_dark_withicon, false);
    }
    private void test(int layoutId, final int widgetId, final boolean hasSelector) {
        Intent i = new Intent();
        i.putExtra("layoutId", layoutId);
        setActivityIntent(i);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
        getInstrumentation().waitForIdleSync();
        HtcFooterIconButton hfib = (HtcFooterIconButton) mActivity.findViewById(R.id.footericonbutton_left);
        if(null != hfib){
            hfib.useSelectorWhenPressed(hasSelector);
            EventUtil.callLongPressed(getInstrumentation(), hfib, new EventUtil.EventCallBack() {
                @Override
                public void onPressedStatus(View view) {
                    mSolo.sleep(4000);
                    ScreenShotUtil.AssertViewEqualBefore(mSolo, mSolo.getView(widgetId), FooterIconButtonTest.this);
                }
            });
            mSolo.clickLongOnView(hfib, 5000);
        }
    }
}
