
package com.htc.lib1.cc.textview.test;

import android.text.TextUtils.TruncateAt;
import android.view.ViewGroup;
import android.widget.TextView;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.test.R;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;

public class TextViewTest extends HtcActivityTestCaseBase {
    public TextViewTest() {
        super(ActivityBase.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityIntent(null);
        initActivity();
    }

    public void testEnablefadingEdge() {
        final TextView targetView = new TextView(mActivity);
        targetView.setText(mActivity.getResources().getString(R.string.str_expandItemText));
        targetView.setHorizontalFadingEdgeEnabled(true);
        targetView.setSingleLine();
        targetView.setEllipsize(TruncateAt.MARQUEE);
        targetView.setPadding(10, 0, 10, 0);
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mActivity.setContentView(targetView, new ViewGroup.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        });
        ScreenShotUtil.AssertViewEqualBefore(mSolo, targetView, this);
    }
}
