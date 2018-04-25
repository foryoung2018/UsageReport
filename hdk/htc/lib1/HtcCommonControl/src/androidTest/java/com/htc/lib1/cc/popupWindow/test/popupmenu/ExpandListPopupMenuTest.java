package com.htc.lib1.cc.popupWindow.test.popupmenu;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ListView;
import com.htc.lib1.cc.test.R;

import com.htc.lib1.cc.widget.ExpandableListPopupBubbleWindow;
import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcPopupWindowWrapper;
import com.htc.lib1.cc.widget.HtcPopupWindowWrapper.HtcPopupMaxContentWidthListener;
import com.htc.lib1.cc.widget.PopupBubbleWindow;
import com.htc.lib1.cc.popupWindow.activityhelper.popupmenu.ExpandableListPopupMenuDemo;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;

public class ExpandListPopupMenuTest extends HtcActivityTestCaseBase {

    private static final String EXPANDABLE_POPUP_MENU = "ExpandablePopupMenu";
    private static final String WRAPPER_EXPANDABLE = "Wrapper/Expandable";
    private static final String WRAPPER_LIST_POPUP_MENU = "Wrapper/ListPopupMenu";
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityIntent(null);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
    }

    @Override
    protected void tearDown() throws Exception {
        final ExpandableListPopupMenuDemo instance = (ExpandableListPopupMenuDemo) getActivity();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                instance.dismissWindow();
            }
        });
        super.tearDown();
    }

    public ExpandListPopupMenuTest() {
        super(ExpandableListPopupMenuDemo.class);
    }

    public final void testTopExpandUIInit() {
        View[] multView = getWindowRootView(EXPANDABLE_POPUP_MENU, false);
        assertNotNull(multView);
        getInstrumentation().waitForIdleSync();
        ScreenShotUtil.AssertMultipleViewEqualBefore(mSolo, multView, this, getInstrumentation());
    }

    public final void testTopExpandleUIExpanded() {
        View[] multView = getWindowRootView(EXPANDABLE_POPUP_MENU, true);
        assertNotNull(multView);
        getInstrumentation().waitForIdleSync();
        ScreenShotUtil.AssertMultipleViewEqualBefore(mSolo, multView, this, getInstrumentation());
    }

    public final void testBottomExpandUIInit() {
        View[] multView = getWindowRootView(WRAPPER_EXPANDABLE, false);
        assertNotNull(multView);
        getInstrumentation().waitForIdleSync();
        ScreenShotUtil.AssertMultipleViewEqualBefore(mSolo, multView, this, getInstrumentation());
    }

    public final void testBottomExpandUIExpended() {
        View[] multView = getWindowRootView(WRAPPER_EXPANDABLE, true);
        assertNotNull(multView);
        getInstrumentation().waitForIdleSync();
        ScreenShotUtil.AssertMultipleViewEqualBefore(mSolo, multView, this, getInstrumentation());
    }

    public final void testWrapperListPopupMenu() {
        assertNotNull(mActivity);

        mSolo.clickOnText(WRAPPER_LIST_POPUP_MENU);
        for (int i = 1; i <= 5; i++) {
            mSolo.clickOnText("listitem" + i);
        }
        mSolo.goBack();
    }

    private View[] getWindowRootView(String btnText, boolean isExpand) {
        assertNotNull(mActivity);
        View btn = null;
        if(EXPANDABLE_POPUP_MENU.equals(btnText)){
            btn = mActivity.findViewById(R.id.top_btn);
        }else if(WRAPPER_EXPANDABLE.equals(btnText)){
            btn = mActivity.findViewById(R.id.btn);
        }
        if(null == btn){
            return null;
        }
        mSolo.clickOnView(btn);
        mSolo.waitForText("People Names");
        if(isExpand){
            mSolo.clickOnText("People Names");
        }
        View rootView = getRootView();
        View[] viewArray = {
                btn, rootView
        };
        return viewArray;
    }

    private View getRootView() {
        final View itemView = mSolo.getView(HtcListItem.class, 0);
        if (null != itemView) {
            getInstrumentation().runOnMainSync(new Runnable() {
                @Override
                public void run() {
                    mSolo.getView(ListView.class, 0).setVerticalScrollBarEnabled(false);
                }
            });
            return itemView.getRootView();
        } else {
            return null;
        }
    }
    private void toTestButton(String buttonText) {
        String[] itemList = { "People Names", "Dog Names", "Cat Names", "Fish Names" };
        String[][] subItemList = { { "Arnold", "Barry", "David" }, { "Ace", "Bandit", "Deuce"}, { "Fluffy", "Snuggles" }, { "Goldy", "Bubbles" }};
        mSolo.clickOnText(buttonText);
        for (int i = 0, len = itemList.length; i < len; i++) {
            mSolo.clickOnText(itemList[i]);
            for (int j = 0, len2 = subItemList[i].length; j < len2; j++) {
                mSolo.clickOnText(subItemList[i][j]);
            }
            mSolo.scrollToTop();
            mSolo.clickOnText(itemList[i]);
        }
        mSolo.goBack();
    }

    public final void testWrapperExpand() {
        toTestButton("Wrapper/Expandable");
    }

    public final void testExpandPopupMenu() {
        toTestButton("ExpandablePopupMenu");
    }

    public void testImproveCoverage() {
        ExpandableListPopupBubbleWindow expandPopup = new ExpandableListPopupBubbleWindow(getActivity());
        expandPopup.getSelectedItem();
        expandPopup.getSelectedItemId();
        expandPopup.getSelectedItemPosition();
        expandPopup.getSelectedView();
        expandPopup.performItemClick(1);
        expandPopup.setOnGroupCollapseListener(new OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int arg0) {
            }
        });
        expandPopup.setOnGroupExpandListener(new OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int arg0) {
            }
        });
        HtcPopupWindowWrapper mExpListPopupWrapper = new HtcPopupWindowWrapper();
        mExpListPopupWrapper.getPopupExpandableListView();
        mExpListPopupWrapper.getPopupListView();
        mExpListPopupWrapper.setDropDownListPosition(1);
        mExpListPopupWrapper.setExpandDirection(PopupBubbleWindow.EXPAND_DEFAULT);
        mExpListPopupWrapper.setExpandGroup(0);
        mExpListPopupWrapper.setFixedListItemDimen(true,100,100);
        mExpListPopupWrapper.setFooterView(new View(getActivity()));
        mExpListPopupWrapper.setHeaderView(new View(getActivity()));
        mExpListPopupWrapper.setTaggleView(new View(getActivity()));
        mExpListPopupWrapper.setTouchInterceptor(new OnTouchListener(){

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                return false;
            }});

        mExpListPopupWrapper.setHtcPopupMaxContentWidthListener(new HtcPopupMaxContentWidthListener(){

            @Override
            public void updateMaxContentWidth(int maxContentWidth) {
            }});
        mExpListPopupWrapper.setOnChildClickListener(new OnChildClickListener(){

            @Override
            public boolean onChildClick(ExpandableListView arg0, View arg1, int arg2, int arg3, long arg4) {
                return false;
            }});
        mExpListPopupWrapper.setOnDismissListener(new PopupBubbleWindow.OnDismissListener(){

            @Override
            public void onDismiss() {
            }});
        mExpListPopupWrapper.setOnGroupClickListener(new OnGroupClickListener(){

            @Override
            public boolean onGroupClick(ExpandableListView arg0, View arg1, int arg2, long arg3) {
                return false;
            }});
        mExpListPopupWrapper.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            }});
    }
}
