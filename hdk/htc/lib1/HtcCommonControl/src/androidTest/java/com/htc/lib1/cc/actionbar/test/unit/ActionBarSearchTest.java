
package com.htc.lib1.cc.actionbar.test.unit;

import android.graphics.drawable.Drawable;
import android.test.UiThreadTest;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import com.htc.lib1.cc.actionbar.activityhelper.ActionBarMockActivity;
import com.htc.lib1.cc.actionbar.test.ActionBarWidgetsFactory;
import com.htc.lib1.cc.actionbar.test.FieldReflection;
import com.htc.lib1.cc.actionbar.test.HtcActionBarActivityTestCase;
import com.htc.lib1.cc.widget.ActionBarSearch;
import com.htc.test.util.NoRunTheme;

@NoRunTheme
public class ActionBarSearchTest extends HtcActionBarActivityTestCase {
    private ActionBarSearch mActionBarSearch;

    public ActionBarSearchTest() {
        super(ActionBarMockActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mActionBarSearch = ActionBarWidgetsFactory.createActionBarSearch(mActivity);
            }
        });
    }

    @UiThreadTest
    public void testGetAutoCompleteTextView() {
        View v = mActionBarSearch.getAutoCompleteTextView();
        try {
            FieldReflection autoCompleteTextView = new FieldReflection(ActionBarSearch.class, false, "mHtcAutoCompleteTextView");
            assertTrue(v.equals((View) autoCompleteTextView.get(mActionBarSearch)));
        } catch (NoSuchFieldException e) {
            fail(e.getMessage());
        } catch (IllegalAccessException e) {
            fail(e.getMessage());
        } catch (IllegalArgumentException e) {
            fail(e.getMessage());
        }
    }

    @UiThreadTest
    public void testSetClearIconVisibility() {

        mActionBarSearch.setClearIconVisibility(View.VISIBLE);
        View iconView = mActionBarSearch.findViewById(android.R.id.icon);
        assertNotNull(iconView);
        assertTrue(iconView.getVisibility() == View.VISIBLE);

        mActionBarSearch.setClearIconVisibility(View.INVISIBLE);
        assertTrue(iconView.getVisibility() == View.INVISIBLE);

        mActionBarSearch.setClearIconVisibility(View.GONE);
        assertTrue(iconView.getVisibility() == View.GONE);
    }

    @UiThreadTest
    public void testSetProgressVisibility() {
        try {
            FieldReflection progressView = new FieldReflection(ActionBarSearch.class, false, "mProgressView");
            mActionBarSearch.setProgressVisibility(View.VISIBLE);
            assertTrue(((View) progressView.get(mActionBarSearch)).getVisibility() == View.VISIBLE);

            mActionBarSearch.setProgressVisibility(View.INVISIBLE);
            assertTrue(((View) progressView.get(mActionBarSearch)).getVisibility() == View.INVISIBLE);

            mActionBarSearch.setProgressVisibility(View.GONE);
            assertTrue(((View) progressView.get(mActionBarSearch)).getVisibility() == View.GONE);

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
    public void testSetSupportMode() {
        FieldReflection supportMode;
        try {
            supportMode = new FieldReflection(ActionBarSearch.class, false, "mSupportMode");
            mActionBarSearch.setSupportMode(ActionBarSearch.MODE_AUTOMOTIVE);
            assertTrue(((Integer) supportMode.get(mActionBarSearch)) == ActionBarSearch.MODE_AUTOMOTIVE);
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
    public void testSetIcon_Drawable() {
        Drawable d = getInstrumentation().getTargetContext().getResources().getDrawable(android.R.drawable.ic_search_category_default);
        mActionBarSearch.setIcon(d);
        ImageView iconView = (ImageView) mActionBarSearch.findViewById(android.R.id.icon);
        assertNotNull(iconView);
        assertTrue(d.equals(iconView.getDrawable()));
    }

    @UiThreadTest
    public void testSetIcon_ResId() {
        Drawable d = getInstrumentation().getTargetContext().getResources().getDrawable(android.R.drawable.ic_search_category_default);
        mActionBarSearch.setIcon(android.R.drawable.ic_search_category_default);
        ImageView iconView = (ImageView) mActionBarSearch.findViewById(android.R.id.icon);
        assertNotNull(iconView);
    }

    @UiThreadTest
    public void testSetIconContentDescription() {
        mActionBarSearch.setIcon(android.R.drawable.ic_search_category_default);
        mActionBarSearch.setIconContentDescription("This is icon");
        ImageView iconView = (ImageView) mActionBarSearch.findViewById(android.R.id.icon);
        assertNotNull(iconView);
        assertTrue(iconView.getContentDescription().equals("This is icon"));
    }

    public void testIsImeActionSearch() {
        AutoCompleteTextView v = mActionBarSearch.getAutoCompleteTextView();
        assertEquals(v.getImeOptions() & EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_DONE);
    }

}
