
package com.htc.lib1.cc.layout.test;

import android.view.View;

import com.htc.lib1.cc.layout.activityhelper.HtcEmptyViewDemo;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.HtcEmptyView;
import com.htc.lib1.cc.widget.HtcRimButton;
import com.htc.lib1.cc.widget.RefreshGestureDetector;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;

public class LayoutTest extends HtcActivityTestCaseBase {

    final CharSequence DARK_MODE = "dark mode";

    final CharSequence LIGHT_MODE = "light mode";

    final CharSequence ENABLE_PULL_DOWN = "enable pull down";

    final CharSequence DISABLE_PULL_DOWN = "disable pull down";

    final CharSequence ADD_BUTTON = "add button";

    final CharSequence REMOVE_BUTTON = "remove button";

    final CharSequence AUTOMOTIVE_MODE = "automotive mode";

    final CharSequence GENERIC_MODE = "generic mode";

    public LayoutTest() {
        super(HtcEmptyViewDemo.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityIntent(null);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
    }

    /**
     * add TestCase for Dark_Light Mode,Automotive mode,Pull Down,Add Button
     */
    public void testByDarkMode() {
         testLayout(DARK_MODE);
    }

    public void testByLightMode() {
        testLayout(LIGHT_MODE);
    }

    public void testByAutomotiveMode() {
        testLayout(AUTOMOTIVE_MODE);
    }

    public void testByGenericMode() {
        testLayout(GENERIC_MODE);
    }

    public void testByEnablePullDown() {
        testLayout(ENABLE_PULL_DOWN);
    }

    public void testByDisablePullDown() {
        testLayout(DISABLE_PULL_DOWN);
    }

    public void testByAddButton() {
        testLayout(ADD_BUTTON);
    }

    public void testByRemoveButton() {
        testLayout(REMOVE_BUTTON);
    }

//    public void testLayout() {
//        ScreenShotUtil.AssertViewEqualBefore(mSolo,
//                mActivity.findViewById(R.id.empty), this);
//    }

    public void testLayout(final CharSequence string) {
        final HtcEmptyView htcEmptyView = (HtcEmptyView) mActivity
                .findViewById(R.id.empty);
        final HtcRimButton htcRimButton = (HtcRimButton) mActivity.findViewById(R.id.button);
        setActivityIntent(null);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());

        try {
            runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(DARK_MODE.equals(string)){
                        mActivity.getWindow().setBackgroundDrawableResource(
                                R.drawable.common_app_bkg_dark);
                        htcEmptyView.setMode(HtcEmptyView.MODE_DARK);
                        htcEmptyView.setText("DarkMode");
                    }else if(LIGHT_MODE.equals(string)){
                        mActivity.getWindow().setBackgroundDrawableResource(
                                R.drawable.common_app_bkg);
                        htcEmptyView.setMode(HtcEmptyView.MODE_NORMAL);
                        htcEmptyView.setText("LightMode");
                    }else if(AUTOMOTIVE_MODE.equals(string)){
                        mActivity.getWindow().setBackgroundDrawableResource(
                                R.drawable.common_app_bkg_dark);
                        htcEmptyView.setMode(HtcEmptyView.MODE_AUTOMOTIVE);
                        htcEmptyView.setText("AutoMotiveMode");
                    }else if(GENERIC_MODE.equals(string)){
                        mActivity.getWindow().setBackgroundDrawableResource(
                                R.drawable.common_app_bkg);
                        htcEmptyView.setMode(HtcEmptyView.MODE_NORMAL);
                        htcEmptyView.setText("GenericMode");
                    }else if(ENABLE_PULL_DOWN.equals(string)){
                        htcEmptyView.setRefreshListener(new RefreshGestureDetector.RefreshListener() {

                            @Override
                            public void onBoundary() {
                                // TODO Auto-generated method stub
                            }

                            @Override
                            public void onFinish() {
                                // TODO Auto-generated method stub
                            }

                            @Override
                            public void onCancel() {
                                // TODO Auto-generated method stub
                            }

                            @Override
                            public void onGapChanged(int gap, int maxGap) {
                                // TODO Auto-generated method stub
                            }});
                    }else if(DISABLE_PULL_DOWN.equals(string)){
                        htcEmptyView.setRefreshListener(null);
                    }else if(ADD_BUTTON.equals(string)){
                        htcRimButton.setVisibility(View.VISIBLE);
                    }else{
                        htcRimButton.setVisibility(View.GONE);
                    }
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
        getInstrumentation().waitForIdleSync();
        ScreenShotUtil.AssertViewEqualBefore(mSolo,
                mActivity.findViewById(R.id.empty), this);
    }

    public final void testImproveCoverage() {
        initActivity();
        try {
            runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((HtcEmptyViewDemo) mActivity).improveCoverage();
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


}
