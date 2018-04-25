
package com.htc.lib1.cc.actionbar.test.unit;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.View;

import com.htc.lib1.cc.actionbar.activityhelper.ActionBarMockActivity;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.ActionBarText;
import com.htc.test.util.NoRunTheme;

@NoRunTheme
public class ActionBarTextTest extends ActivityInstrumentationTestCase2<ActionBarMockActivity> {
    private ActionBarText mActionBarText;

    public ActionBarTextTest() {
        super(ActionBarMockActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mActionBarText = new ActionBarText(getInstrumentation().getTargetContext());
    }

    @UiThreadTest
    public void testSetPrimaryText() {
        mActionBarText.setPrimaryText("Test1234");
        assertTrue(mActionBarText.getPrimaryText().equals("Test1234"));
    }

    @UiThreadTest
    public void testSetSecondaryText() {
        mActionBarText.setSecondaryText("Test1234");
        assertTrue(mActionBarText.getSecondaryText().equals("Test1234"));
    }

    @UiThreadTest
    public void testSetPrimaryVisibility() {

        View tv = mActionBarText.findViewById(R.id.primary);
        mActionBarText.setPrimaryVisibility(View.VISIBLE);
        assertTrue(tv.getVisibility() == View.VISIBLE);

        mActionBarText.setPrimaryVisibility(View.INVISIBLE);
        assertTrue(tv.getVisibility() == View.INVISIBLE);

        mActionBarText.setPrimaryVisibility(View.GONE);
        assertTrue(tv.getVisibility() == View.GONE);
    }

    @UiThreadTest
    public void testSetSecondaryVisibility() {

        View tv = mActionBarText.findViewById(R.id.secondary);

        mActionBarText.setSecondaryVisibility(View.VISIBLE);
        assertTrue(tv.getVisibility() == View.VISIBLE);

        mActionBarText.setSecondaryVisibility(View.INVISIBLE);
        assertTrue(tv.getVisibility() == View.INVISIBLE);

        mActionBarText.setSecondaryVisibility(View.GONE);
        assertTrue(tv.getVisibility() == View.GONE);
    }

}
