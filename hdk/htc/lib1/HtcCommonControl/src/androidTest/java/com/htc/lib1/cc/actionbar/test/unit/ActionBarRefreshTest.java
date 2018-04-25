
package com.htc.lib1.cc.actionbar.test.unit;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.htc.lib1.cc.actionbar.activityhelper.ActionBarMockActivity;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.util.res.HtcResUtil;
import com.htc.lib1.cc.widget.ActionBarRefresh;
import com.htc.test.util.NoRunTheme;

@NoRunTheme
public class ActionBarRefreshTest extends ActivityInstrumentationTestCase2<ActionBarMockActivity> {
    private ActionBarRefresh mActionBarRefresh;

    private ImageView mIconView = null;
    private ProgressBar mProgressView = null;
    private TextView mPrimaryView = null;
    private TextView mSecondaryView = null;
    private ImageView mArrowView = null;

    public ActionBarRefreshTest() {
        super(ActionBarMockActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActionBarRefresh = new ActionBarRefresh(getActivity());
        mArrowView = (ImageView) mActionBarRefresh.findViewById(R.id.arrow);
        mIconView = (ImageView) mActionBarRefresh.findViewById(R.id.icon);
        mProgressView = (ProgressBar) mActionBarRefresh.findViewById(R.id.progress);
        mPrimaryView = (TextView) mActionBarRefresh.findViewById(R.id.primary);
        mSecondaryView = (TextView) mActionBarRefresh.findViewById(R.id.secondary);

    }

    @UiThreadTest
    public void testSetAndGetMode() {
        int mode = mActionBarRefresh.getMode();
        mActionBarRefresh.setMode(mode);
        assertEquals(mode, mActionBarRefresh.getMode());

        mActionBarRefresh.setMode(ActionBarRefresh.MODE_PULLDOWN);
        assertEquals(ActionBarRefresh.MODE_PULLDOWN, mActionBarRefresh.getMode());

        mActionBarRefresh.setMode(ActionBarRefresh.MODE_UPDATING);
        assertEquals(ActionBarRefresh.MODE_UPDATING, mActionBarRefresh.getMode());
    }

    @UiThreadTest
    public void testDefaultDisplayType() {
        int displayType = mActionBarRefresh.getDisplayType(mActionBarRefresh.getMode());
        if ((displayType & ActionBarRefresh.CATEGORY_SUBTITLE) == ActionBarRefresh.CATEGORY_SUBTITLE) {
            assertEquals(ViewGroup.VISIBLE, mSecondaryView.getVisibility());
        } else {
            assertEquals(ViewGroup.GONE, mSecondaryView.getVisibility());
        }
        if ((displayType & ActionBarRefresh.CATEGORY_TITLE) == ActionBarRefresh.CATEGORY_TITLE) {
            assertEquals(ViewGroup.VISIBLE, mPrimaryView.getVisibility());
        }
        if ((displayType & ActionBarRefresh.CATEGORY_ARROW) == ActionBarRefresh.CATEGORY_ARROW) {
            assertEquals(ViewGroup.VISIBLE, mArrowView.getVisibility());
        } else {
            assertEquals(ViewGroup.GONE, mArrowView.getVisibility());
        }
    }

    @UiThreadTest
    public void testSetAndGetModeDisplayType_pullDown_title() {
        mActionBarRefresh.setMode(ActionBarRefresh.MODE_PULLDOWN);
        mActionBarRefresh.setModeDisplayType(ActionBarRefresh.MODE_PULLDOWN, ActionBarRefresh.TYPE_PULLDOWN_WITH_TITLE);
        assertEquals(ActionBarRefresh.TYPE_PULLDOWN_WITH_TITLE, mActionBarRefresh.getDisplayType(ActionBarRefresh.MODE_PULLDOWN));
        assertEquals(ViewGroup.VISIBLE, mPrimaryView.getVisibility());
        assertEquals(ViewGroup.GONE, mArrowView.getVisibility());
        assertEquals(ViewGroup.GONE, mSecondaryView.getVisibility());
        assertEquals(ViewGroup.VISIBLE, mIconView.getVisibility());
        assertEquals(ViewGroup.GONE, mProgressView.getVisibility());
    }

    @UiThreadTest
    public void testSetAndGetModeDisplayType_pullDown_title_subtitle() {

        mActionBarRefresh.setModeDisplayType(ActionBarRefresh.MODE_PULLDOWN, ActionBarRefresh.TYPE_PULLDOWN_WITH_SUBTITLE);
        assertEquals(ActionBarRefresh.TYPE_PULLDOWN_WITH_SUBTITLE, mActionBarRefresh.getDisplayType(ActionBarRefresh.MODE_PULLDOWN));

        assertEquals(ViewGroup.VISIBLE, mPrimaryView.getVisibility());
        assertEquals(ViewGroup.GONE, mArrowView.getVisibility());
        assertEquals(ViewGroup.VISIBLE, mSecondaryView.getVisibility());
        assertEquals(ViewGroup.VISIBLE, mIconView.getVisibility());
        assertEquals(ViewGroup.GONE, mProgressView.getVisibility());
    }

    @UiThreadTest
    public void testSetAndGetModeDisplayType_updating_title() {
        mActionBarRefresh.setMode(ActionBarRefresh.MODE_UPDATING);
        mActionBarRefresh.setModeDisplayType(ActionBarRefresh.MODE_UPDATING, ActionBarRefresh.TYPE_UPDATE_WITH_TITLE);
        assertEquals(ActionBarRefresh.TYPE_UPDATE_WITH_TITLE, mActionBarRefresh.getDisplayType(ActionBarRefresh.MODE_UPDATING));

        assertEquals(ViewGroup.VISIBLE, mPrimaryView.getVisibility());
        assertEquals(ViewGroup.GONE, mArrowView.getVisibility());
        assertEquals(ViewGroup.GONE, mSecondaryView.getVisibility());
        assertEquals(ViewGroup.GONE, mIconView.getVisibility());
        assertEquals(ViewGroup.VISIBLE, mProgressView.getVisibility());
    }

    @UiThreadTest
    public void testSetAndGetModeDisplayType_updating_title_dropDown() {
        mActionBarRefresh.setMode(ActionBarRefresh.MODE_UPDATING);
        mActionBarRefresh.setModeDisplayType(ActionBarRefresh.MODE_UPDATING, ActionBarRefresh.TYPE_UPDATE_WITH_TITLE_DROPDOWN);
        assertEquals(ActionBarRefresh.TYPE_UPDATE_WITH_TITLE_DROPDOWN, mActionBarRefresh.getDisplayType(ActionBarRefresh.MODE_UPDATING));

        assertEquals(ViewGroup.VISIBLE, mPrimaryView.getVisibility());
        assertEquals(ViewGroup.VISIBLE, mArrowView.getVisibility());
        assertEquals(ViewGroup.GONE, mSecondaryView.getVisibility());
        assertEquals(ViewGroup.GONE, mIconView.getVisibility());
        assertEquals(ViewGroup.VISIBLE, mProgressView.getVisibility());
    }

    @UiThreadTest
    public void testSetAndGetModeDisplayType_updating_title_subTitle() {
        mActionBarRefresh.setMode(ActionBarRefresh.MODE_UPDATING);
        mActionBarRefresh.setModeDisplayType(ActionBarRefresh.MODE_UPDATING, ActionBarRefresh.TYPE_UPDATE_WITH_SUBTITLE);
        assertEquals(ActionBarRefresh.TYPE_UPDATE_WITH_SUBTITLE, mActionBarRefresh.getDisplayType(ActionBarRefresh.MODE_UPDATING));

        assertEquals(ViewGroup.VISIBLE, mPrimaryView.getVisibility());
        assertEquals(ViewGroup.GONE, mArrowView.getVisibility());
        assertEquals(ViewGroup.VISIBLE, mSecondaryView.getVisibility());
        assertEquals(ViewGroup.GONE, mIconView.getVisibility());
        assertEquals(ViewGroup.VISIBLE, mProgressView.getVisibility());
    }

    @UiThreadTest
    public void testSetAndGetModeDisplayType_updating_title_subTitle_dropdown() {
        mActionBarRefresh.setMode(ActionBarRefresh.MODE_UPDATING);
        mActionBarRefresh.setModeDisplayType(ActionBarRefresh.MODE_UPDATING, ActionBarRefresh.TYPE_UPDATE_WITH_SUBTITLE_DROPDOWN);
        assertEquals(ActionBarRefresh.TYPE_UPDATE_WITH_SUBTITLE_DROPDOWN, mActionBarRefresh.getDisplayType(ActionBarRefresh.MODE_UPDATING));

        assertEquals(ViewGroup.VISIBLE, mPrimaryView.getVisibility());
        assertEquals(ViewGroup.VISIBLE, mArrowView.getVisibility());
        assertEquals(ViewGroup.VISIBLE, mSecondaryView.getVisibility());
        assertEquals(ViewGroup.GONE, mIconView.getVisibility());
        assertEquals(ViewGroup.VISIBLE, mProgressView.getVisibility());
    }

    @UiThreadTest
    public void testSetModeText_defaultText() {
        String pullDownDefaultTitle = getInstrumentation().getTargetContext().getResources().getString(R.string.st_action_bar_pull_down);
        String updateSubDefaultTitle = getInstrumentation().getTargetContext().getString(R.string.st_action_bar_updating);

        mActionBarRefresh.setMode(ActionBarRefresh.MODE_PULLDOWN);
        assertEquals(HtcResUtil.toUpperCase(getInstrumentation().getTargetContext(), pullDownDefaultTitle), mPrimaryView.getText());

        mActionBarRefresh.setModeDisplayType(ActionBarRefresh.MODE_PULLDOWN, ActionBarRefresh.TYPE_PULLDOWN_WITH_SUBTITLE);
        assertEquals(HtcResUtil.toUpperCase(getInstrumentation().getTargetContext(), pullDownDefaultTitle), mPrimaryView.getText());

        mActionBarRefresh.setMode(ActionBarRefresh.MODE_UPDATING);
        assertEquals(HtcResUtil.toUpperCase(getInstrumentation().getTargetContext(), updateSubDefaultTitle), mPrimaryView.getText());

        mActionBarRefresh.setModeDisplayType(ActionBarRefresh.MODE_UPDATING, ActionBarRefresh.TYPE_UPDATE_WITH_SUBTITLE);
        assertEquals(HtcResUtil.toUpperCase(getInstrumentation().getTargetContext(), updateSubDefaultTitle), mSecondaryView.getText());

    }

    @UiThreadTest
    public void testSetModeText_pullDown_customText() {
        String customPullDownTitle = "release whatever you want custom";
        String customPullDonwText = "last updated";

        mActionBarRefresh.setMode(ActionBarRefresh.MODE_PULLDOWN);
        mActionBarRefresh.setModeDisplayType(ActionBarRefresh.MODE_PULLDOWN, ActionBarRefresh.TYPE_PULLDOWN_WITH_SUBTITLE);
        mActionBarRefresh.setModeText(ActionBarRefresh.MODE_PULLDOWN, ActionBarRefresh.CATEGORY_TITLE, customPullDownTitle);
        mActionBarRefresh.setModeText(ActionBarRefresh.MODE_PULLDOWN, ActionBarRefresh.CATEGORY_SUBTITLE, customPullDonwText);
        assertEquals(HtcResUtil.toUpperCase(getInstrumentation().getTargetContext(), customPullDownTitle), mPrimaryView.getText());
        assertEquals(HtcResUtil.toUpperCase(getInstrumentation().getTargetContext(), customPullDonwText), mSecondaryView.getText());
    }

    @UiThreadTest
    public void testSetModeText_updating_customText() {
        String customUpdatingTitle = "title primary";
        String customUpdatingText = "updating custom";

        mActionBarRefresh.setMode(ActionBarRefresh.MODE_UPDATING);
        mActionBarRefresh.setModeDisplayType(ActionBarRefresh.MODE_UPDATING, ActionBarRefresh.TYPE_UPDATE_WITH_SUBTITLE);
        mActionBarRefresh.setModeText(ActionBarRefresh.MODE_UPDATING, ActionBarRefresh.CATEGORY_TITLE, customUpdatingTitle);
        mActionBarRefresh.setModeText(ActionBarRefresh.MODE_UPDATING, ActionBarRefresh.CATEGORY_SUBTITLE, customUpdatingText);
        assertEquals(customUpdatingTitle, mPrimaryView.getText());
        assertEquals(HtcResUtil.toUpperCase(getInstrumentation().getTargetContext(), customUpdatingText), mSecondaryView.getText());

    }
}
