
package com.htc.lib1.cc.actionbar.test.unit;

import android.test.UiThreadTest;
import android.view.View;
import android.widget.TextView;

import com.htc.lib1.cc.actionbar.activityhelper.ActionBarMockActivity;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.actionbar.test.ActionBarTestUtil;
import com.htc.lib1.cc.actionbar.test.ActionBarWidgetsFactory;
import com.htc.lib1.cc.actionbar.test.HtcActionBarActivityTestCase;
import com.htc.lib1.cc.widget.ActionBarDropDown;
import com.htc.test.util.NoRunTheme;

@NoRunTheme
public class ActionBarDropDownTest extends HtcActionBarActivityTestCase {
    private ActionBarDropDown mActionBarDropDown;

    public ActionBarDropDownTest() {
        super(ActionBarMockActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        initActionBarContainers();
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mActionBarDropDown = ActionBarWidgetsFactory.createActionBarDropDown(mActivity);
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, mActionBarDropDown);
            }
        });
    }

    @UiThreadTest
    public void testSetPrimaryText() {
        mActionBarDropDown.setPrimaryText("Test1234");
        assertTrue(mActionBarDropDown.getPrimaryText().equals("Test1234"));
    }

    @UiThreadTest
    public void testSetSecondaryTextString() {
        mActionBarDropDown.setSecondaryText("Test1234");
        assertTrue(mActionBarDropDown.getSecondaryText().equals("Test1234"));
    }

    @UiThreadTest
    public void testSetSecondaryTextRes() {
        mActionBarDropDown.setSecondaryText(android.R.string.dialog_alert_title);
        assertTrue(mActionBarDropDown.getSecondaryText().equals(getInstrumentation().getTargetContext().getResources().getString(android.R.string.dialog_alert_title)));
    }

    @UiThreadTest
    public void testSetAndGetPrimaryVisibility() {

        mActionBarDropDown.setPrimaryVisibility(View.VISIBLE);
        assertEquals(View.VISIBLE, mActionBarDropDown.getPrimaryVisibility());

        mActionBarDropDown.setPrimaryVisibility(View.INVISIBLE);
        assertEquals(View.INVISIBLE, mActionBarDropDown.getPrimaryVisibility());

        mActionBarDropDown.setPrimaryVisibility(View.GONE);
        assertEquals(View.GONE, mActionBarDropDown.getPrimaryVisibility());
    }

    @UiThreadTest
    public void testSetAndGetSecondaryVisibility() {

        mActionBarDropDown.setSecondaryVisibility(View.VISIBLE);
        assertEquals(View.VISIBLE, mActionBarDropDown.getSecondaryVisibility());

        mActionBarDropDown.setSecondaryVisibility(View.INVISIBLE);
        assertEquals(View.INVISIBLE, mActionBarDropDown.getSecondaryVisibility());

        mActionBarDropDown.setSecondaryVisibility(View.GONE);
        assertEquals(View.GONE, mActionBarDropDown.getSecondaryVisibility());
    }

    @UiThreadTest
    public void testSetArrowEnabled() {

        View arrow = mActionBarDropDown.findViewById(R.id.arrow);

        mActionBarDropDown.setArrowEnabled(true);
        assertTrue(arrow.getVisibility() == View.VISIBLE);

        mActionBarDropDown.setArrowEnabled(false);
        assertTrue(arrow.getVisibility() == View.GONE);
    }

    @UiThreadTest
    public void testGetPrimaryView() {
        TextView tv = mActionBarDropDown.getPrimaryView();

        View primaryView = mActionBarDropDown.findViewById(R.id.primary);

        assertTrue(tv.equals(primaryView));
    }

    @UiThreadTest
    public void testGetSecondaryView() {
        TextView tv = mActionBarDropDown.getSecondaryView();
        View secondaryView = mActionBarDropDown.findViewById(R.id.secondary);
        assertTrue(tv.equals(secondaryView));
    }

}
