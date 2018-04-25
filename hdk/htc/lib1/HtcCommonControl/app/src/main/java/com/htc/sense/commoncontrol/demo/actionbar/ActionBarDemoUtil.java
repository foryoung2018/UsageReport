/**
 *
 */

package com.htc.sense.commoncontrol.demo.actionbar;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.htc.sense.commoncontrol.demo.R;
import com.htc.sense.commoncontrol.demo.util.WidgetDataPreparer;
import com.htc.lib1.cc.widget.ActionBarContainer;
import com.htc.lib1.cc.widget.ActionBarDropDown;
import com.htc.lib1.cc.widget.ActionBarItemView;
import com.htc.lib1.cc.widget.ActionBarQuickContact;
import com.htc.lib1.cc.widget.ActionBarSearch;
import com.htc.lib1.cc.widget.ActionBarText;
import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcListItem1LineCenteredText;
import com.htc.lib1.cc.widget.ListPopupWindow;

/**
 * @author felka
 */
public class ActionBarDemoUtil implements OnClickListener,
        OnChildClickListener, OnGroupClickListener {
    public static final String ACCESSIBILITY_CONTENT_DESCRIPTION = "Should be set by AP";

    Activity mActivity = null;
    private ActionBarContainer mActionBarContainer = null;
    private ActionBarDropDown mActionBarDropDown = null;
    private ActionBarItemView mActionBarItemViewLeft = null;
    private ActionBarItemView mActionBarItemViewRight = null;
    private ActionBarQuickContact mActionBarQuickContactLeft = null;
    private ActionBarQuickContact mActionBarQuickContactRight = null;
    private ActionBarSearch mActionBarSearch = null;
    private ActionBarText mActionBarText = null;
    private boolean mAutomotiveFlag;

    private String[] mDropDownStrings = new String[] {
            "listitem1", "listitem2", "listitem3", "listitem4", "listitem5", "listitem6"
    };

    private ActionBarDemoUtil(Activity activity, boolean automotiveFlag) {
        mActivity = activity;
        mAutomotiveFlag = automotiveFlag;
    }

    static private ActionBarDemoUtil sActionBarDemoUtil = null;

    synchronized static public ActionBarDemoUtil getInstance(Activity activity,
            boolean automotiveFlag) {
        if (null == sActionBarDemoUtil) {
            sActionBarDemoUtil = new ActionBarDemoUtil(activity, automotiveFlag);
        }
        return sActionBarDemoUtil;
    }

    synchronized static public void clearInstance() {
        if (null != sActionBarDemoUtil) {
            sActionBarDemoUtil = null;
        }
    }

    static int changeTextLength(ViewGroup vg, int lengthState) {
        ArrayList<View> list = new ArrayList<View>();
        vg.findViewsWithText(list, "1234567890", View.FIND_VIEWS_WITH_TEXT);
        boolean isFirst = true;
        for (View v : list) {
            if (v instanceof TextView) {
                if (isFirst) {
                    if (lengthState == 2) {
                        ((TextView) v).setText("TextPrimary1234567890");
                    } else if (lengthState == 1) {
                        ((TextView) v).setText("TextPrimary123456789012345678901234567890123456789012345678901234567890");
                    } else {
                        ((TextView) v)
                                .setText("TextPrimary123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
                    }

                    isFirst = false;
                } else {
                    if (lengthState == 2) {
                        ((TextView) v).setText("TextSecondary1234567890");
                    } else if (lengthState == 1) {
                        ((TextView) v).setText("TextSecondary123456789012345678901234567890123456789012345678901234567890");
                    } else {
                        ((TextView) v)
                                .setText("TextSecondary123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
                    }
                }
            }
        }
        if (lengthState == 2) {
            lengthState = 1;
        } else if (lengthState == 1) {
            lengthState = 0;
        } else {
            lengthState = 2;
        }

        return lengthState;
    }

    public void onClick(View v) {
        final ListPopupWindow lpw = new ListPopupWindow(mActivity, android.R.attr.popupMenuStyle);
        ActionMenuAdapter dropDownAdapter = new ActionMenuAdapter(mActivity, mDropDownStrings, mAutomotiveFlag);
        lpw.setAdapter(dropDownAdapter);
        lpw.setAnchorView(v);
        lpw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lpw.dismiss();
            }
        });
        lpw.show();

        android.util.Log.e("ActionBarDemoUtil", "lpw.getVerticalOffset() = " + lpw.getVerticalOffset());
    }

    public ActionBarContainer initActionBarContainer() {
        mActionBarContainer = new ActionBarContainer(mActivity);
        if (mAutomotiveFlag) mActionBarContainer.setSupportMode(ActionBarContainer.MODE_AUTOMOTIVE);
        mActionBarContainer.setBackUpEnabled(true);
        mActionBarContainer.setBackUpOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });
        return mActionBarContainer;
    }

    public ActionBarDropDown initActionBarDropDown() {
        if (mAutomotiveFlag) {
            mActionBarDropDown = new ActionBarDropDown(mActivity, ActionBarDropDown.MODE_AUTOMOTIVE);
        } else {
            mActionBarDropDown = new ActionBarDropDown(mActivity);
        }

        mActionBarDropDown
                .setPrimaryText("DropDownPrimary12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
        mActionBarDropDown.setSecondaryText("DropDownSecondary123456789012345678901234567890123456789012345678901234567890");
        mActionBarDropDown.setArrowEnabled(true);
        mActionBarDropDown.setOnClickListener(this);

        return mActionBarDropDown;
    }

    public ActionBarDropDown initActionBarDropDownExpand() {
        if (mAutomotiveFlag) {
            mActionBarDropDown = new ActionBarDropDown(mActivity, ActionBarDropDown.MODE_AUTOMOTIVE);
        } else {
            mActionBarDropDown = new ActionBarDropDown(mActivity);
        }

        mActionBarDropDown
                .setPrimaryText("DropDownPrimary12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
        mActionBarDropDown.setSecondaryText("DropDownSecondary123456789012345678901234567890123456789012345678901234567890");
        mActionBarDropDown.setArrowEnabled(true);
        mActionBarDropDown.setOnClickListener(this);

        return mActionBarDropDown;
    }

    public ActionBarItemView initActionBarItemViewLeft() {
        mActionBarItemViewLeft = new ActionBarItemView(mActivity);
        mActionBarItemViewLeft.setIcon(R.drawable.icon_btn_camera_dark);
        mActionBarItemViewLeft.setOnClickListener(this);
        if (mAutomotiveFlag) {
            mActionBarItemViewLeft.setIcon(R.drawable.icon_btn_down_dark);
            mActionBarItemViewLeft.setSupportMode(ActionBarItemView.MODE_AUTOMOTIVE);
        }
        mActionBarItemViewLeft.setContentDescription(ACCESSIBILITY_CONTENT_DESCRIPTION);

        return mActionBarItemViewLeft;
    }

    public ActionBarItemView initActionBarItemViewRight() {
        mActionBarItemViewRight = new ActionBarItemView(mActivity);
        mActionBarItemViewRight.setIcon(R.drawable.icon_btn_brightness_dark);
        mActionBarItemViewRight.setOnClickListener(this);
        if (mAutomotiveFlag) {
            mActionBarItemViewRight.setSupportMode(ActionBarItemView.MODE_AUTOMOTIVE);
            mActionBarItemViewRight.setIcon(R.drawable.icon_btn_delete_dark);
        }
        mActionBarItemViewRight.setContentDescription(ACCESSIBILITY_CONTENT_DESCRIPTION);
        return mActionBarItemViewRight;
    }

    private ActionBarQuickContact initActionBarQuickContact() {
        ActionBarQuickContact abqc = new ActionBarQuickContact(mActivity);
        abqc.setImageResource(R.drawable.icon_category_photo);
        abqc.assignContactFromEmail("a@b.c.com", true);
        abqc.setContentDescription(ACCESSIBILITY_CONTENT_DESCRIPTION);
        return abqc;

    }

    public ActionBarQuickContact initActionBarQuickContactLeft() {
        mActionBarQuickContactLeft = initActionBarQuickContact();
        return mActionBarQuickContactLeft;
    }

    public ActionBarQuickContact initActionBarQuickContactRight() {
        mActionBarQuickContactRight = initActionBarQuickContact();
        return mActionBarQuickContactRight;
    }

    public ActionBarSearch initActionBarSearch() {
        if (mAutomotiveFlag) {
            mActionBarSearch = new ActionBarSearch(mActivity, ActionBarSearch.MODE_AUTOMOTIVE);
        } else {
            mActionBarSearch = new ActionBarSearch(mActivity);
        }
        mActionBarSearch.setClearIconVisibility(View.VISIBLE);
        mActionBarSearch.setProgressVisibility(View.VISIBLE);
        mActionBarSearch.setClearIconOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mActionBarSearch.getAutoCompleteTextView().setText("");
            }
        });
        WidgetDataPreparer.prepareAdapater(mActionBarSearch.getContext(), mActionBarSearch.getAutoCompleteTextView());
        return mActionBarSearch;
    }

    public ActionBarText initActionBarText() {
        if (mAutomotiveFlag) {
            mActionBarText = new ActionBarText(mActivity, ActionBarText.MODE_AUTOMOTIVE);
        } else {
            mActionBarText = new ActionBarText(mActivity);
        }
        mActionBarText.setPrimaryText("TextPrimary123456789012345678901234567890123456789012345678901234567890");
        mActionBarText.setSecondaryText("TextSecondary123456789012345678901234567890123456789012345678901234567890");

        mActionBarText.setOnClickListener(new OnClickListener() {
            int mLengthState = 0;

            @Override
            public void onClick(View v) {
                mLengthState = changeTextLength((ViewGroup) v, mLengthState);
            }
        });
        return mActionBarText;
    }

    private class ExpandActionAdapter extends BaseExpandableListAdapter {

        private String[] groupItme = new String[] {
                "Group1", "Group2", "Group3", "Group4", "Group5", "Group6"
        };

        private String[] subItem = new String[] {
                "subItem1", "subItem2",
                "subItem3", "subItem4", "subItem5", "subItem6", "subItem7",
                "subItem8", "subItem9", "subItem10", "subItem11", "subItem12",
                "subItem13", "subItem14", "subItem15", "subItem16",
                "subItem17", "subItem18", "subItem19", "subItem20",
                "subItem21", "subItem22", "subItem23", "subItem24",
                "subItem25", "subItem26", "subItem27", "subItem28",
                "subItem29", "subItem30"
        };

        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        public int getChildrenCount(int groupPosition) {
            return 5;
        }

        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            HtcListItem listitem = (HtcListItem) mActivity.getLayoutInflater().inflate(R.layout.actionbarlistitem, null);
            HtcListItem1LineCenteredText text = (HtcListItem1LineCenteredText) listitem.findViewById(R.id.text1);
            text.setText(subItem[5 * groupPosition + childPosition]);
            listitem.setBackgroundDrawable(null);
            if (mAutomotiveFlag) listitem.setAutoMotiveMode(true);
            return listitem;
        }

        public Object getGroup(int groupPosition) {
            return groupPosition;
        }

        public int getGroupCount() {
            return groupItme.length;
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            HtcListItem listitem = (HtcListItem) mActivity.getLayoutInflater().inflate(R.layout.actionbarlistitem, null);
            HtcListItem1LineCenteredText text = (HtcListItem1LineCenteredText) listitem.findViewById(R.id.text1);
            text.setText(groupItme[groupPosition]);
            listitem.setBackgroundDrawable(null);
            if (mAutomotiveFlag) listitem.setAutoMotiveMode(true);
            return listitem;
        }

        public boolean hasStableIds() {
            return false;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }

    int mLengthState = 0;

    @Override
    public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
        Toast.makeText(mActivity, "groupPosition=" + groupPosition + "childPosition=" + childPosition, 300).show();
        if (null == mActionBarDropDown) return false;

        mLengthState = changeTextLength((ViewGroup) mActionBarDropDown, mLengthState);
        return false;
    }

    public boolean onGroupClick(ExpandableListView parent, View view, int GroupPosition, long id) {
        Toast.makeText(mActivity, "groupPosition=" + GroupPosition, 300).show();
        return false;
    }

    // static void setSecondaryShowHideToggle(Activity activity) {
    // CheckBox cb = (CheckBox)activity.findViewById(R.id.secondary_show_hide);
    // cb.setOnCheckedChangeListener(new
    // CompoundButton.OnCheckedChangeListener() {
    // @Override
    // public void onCheckedChanged(CompoundButton buttonView,
    // boolean isChecked) {
    //
    // mActionBarText.setSecondaryVisibility((isChecked)?View.VISIBLE:View.GONE);
    // mActionBarDropDown.setSecondaryVisibility((isChecked)?View.VISIBLE:View.GONE);
    // }
    // });
    // }

    public static void removeActionBarContainerCenterChild(ViewGroup parent) {
        if (parent != null) {
            List<View> centerViews = new ArrayList<View>();
            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);
                LayoutParams layoutParams = child.getLayoutParams();
                if (layoutParams == null || !(layoutParams instanceof ActionBar.LayoutParams)) {
                    continue;
                }
                ActionBar.LayoutParams params = (ActionBar.LayoutParams) layoutParams;
                if ((params.gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.CENTER_HORIZONTAL) {
                    centerViews.add(child);
                }
            }
            for (View view : centerViews) {
                parent.removeView(view);
            }
            centerViews.clear();
        }
    }
}
