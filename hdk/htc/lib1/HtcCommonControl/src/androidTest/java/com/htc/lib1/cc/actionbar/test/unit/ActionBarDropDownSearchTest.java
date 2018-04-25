
package com.htc.lib1.cc.actionbar.test.unit;

import android.test.UiThreadTest;
import android.view.View;

import com.htc.lib1.cc.actionbar.activityhelper.ActionBarMockActivity;
import com.htc.lib1.cc.actionbar.test.ActionBarTestUtil;
import com.htc.lib1.cc.actionbar.test.FieldReflection;
import com.htc.lib1.cc.actionbar.test.HtcActionBarActivityTestCase;
import com.htc.lib1.cc.widget.ActionBarDropDownSearch;
import com.htc.test.util.NoRunTheme;

@NoRunTheme
public class ActionBarDropDownSearchTest extends HtcActionBarActivityTestCase {
    private ActionBarDropDownSearch mActionBarDropDownSearch;

    public ActionBarDropDownSearchTest() {
        super(ActionBarMockActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        initActionBarContainers();
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mActionBarDropDownSearch = new ActionBarDropDownSearch(mActivity);
                ActionBarTestUtil.addCenterViewCompat(mActionBarContainer, mActionBarDropDownSearch);
            }
        });
    }

    @UiThreadTest
    public void testGetActionBarDropDown() {
        View view = mActionBarDropDownSearch.getActionBarDropDown();
        try {
            FieldReflection actionDropDown = new FieldReflection(ActionBarDropDownSearch.class, false, "mActionBarDropDown");
            assertEquals(view, actionDropDown.get(mActionBarDropDownSearch));
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            fail(e.getMessage());
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            fail(e.getMessage());
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            fail(e.getMessage());
        }
    }

    @UiThreadTest
    public void testGetActionBarSearch() {
        View view = mActionBarDropDownSearch.getActionBarSearch();
        try {
            FieldReflection actionSearch = new FieldReflection(ActionBarDropDownSearch.class, false, "mActionBarSearch");
            assertEquals(view, actionSearch.get(mActionBarDropDownSearch));

        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            fail(e.getMessage());
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            fail(e.getMessage());
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            fail(e.getMessage());
        }
    }

    public void testSetActionBarSearchEnabled() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mActionBarDropDownSearch.setActionBarSearchEnabled(true);
            }
        });
        mSolo.sleep(800);
        assertTrue(mActionBarDropDownSearch.getActionBarSearch().getVisibility() == View.VISIBLE);
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mActionBarDropDownSearch.setActionBarSearchEnabled(false);
            }
        });
        mSolo.sleep(800);
        assertTrue(mActionBarDropDownSearch.getActionBarSearch().getVisibility() == View.GONE);
    }

}
