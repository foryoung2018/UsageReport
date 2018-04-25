package com.htc.sense.commoncontrol.demo.footerbar;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.htc.lib1.cc.widget.HtcFooter;
import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcListItem1LineCenteredText;
import com.htc.lib1.cc.widget.HtcListItem2LineText;
import com.htc.lib1.cc.widget.HtcPopupWindowWrapper;
import com.htc.lib1.cc.widget.HtcPopupWindowWrapper.ShareViaOnItemClickListener;
import com.htc.lib1.cc.widget.HtcShareViaAdapter;
import com.htc.lib1.cc.widget.PopupBubbleWindow;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

public class FooterbarSpeical extends CommonDemoActivityBase implements OnClickListener,
        ExpandableListView.OnChildClickListener,
        ExpandableListView.OnGroupClickListener, OnItemClickListener,
        PopupBubbleWindow.OnDismissListener {

    private LayoutInflater mInflater=null;
    private Context mContext = null;
    private static final int M1 = Menu.FIRST;
    private static final int M2 = Menu.FIRST + 1;
    private HtcFooter mHtcFooterArrary[] = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.footerbarspecial);
        mContext = this;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mHtcFooterArrary = new HtcFooter[5];
        mHtcFooterArrary[0] = (HtcFooter) findViewById(R.id.htcfooter1);
        mHtcFooterArrary[1] = (HtcFooter) findViewById(R.id.htcfooter2);
        mHtcFooterArrary[2] = (HtcFooter) findViewById(R.id.htcfooter3);
        mHtcFooterArrary[3] = (HtcFooter) findViewById(R.id.htcfooter4);
        mHtcFooterArrary[4] = (HtcFooter) findViewById(R.id.htcfooter5);

    }

    private boolean isHorizontal() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (isHorizontal()) {
            for(int i= 0; i < 5; i++){
                mHtcFooterArrary[i].enableThumbMode(false);
            }
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem thumbModeItem = menu.add(0, M1, Menu.NONE, "EnableThumbMode");
        MenuItem pureModeItem = menu.add(0, M2, Menu.NONE, "EnablePurebMode");
        thumbModeItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        pureModeItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        thumbModeItem.setCheckable(true);
        pureModeItem.setCheckable(true);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case M1:
                if (item.isChecked()) {
                    item.setChecked(false);
                    for(int i = 0; i < 5; i++){
                        mHtcFooterArrary[i].enableThumbMode(false);
                    }
                } else {
                    item.setChecked(true);
                    for(int i = 0; i < 5; i++){
                        mHtcFooterArrary[i].enableThumbMode(true);
                    }
                }
                break;
            case M2:
                if (item.isChecked()) {
                    item.setChecked(false);
                    for(int i = 0; i < 5; i++){
                        mHtcFooterArrary[i].setBackgroundStyleMode(HtcFooter.STYLE_MODE_LIGHT);
                    }
                } else {
                    item.setChecked(true);
                    for(int i = 0; i < 5; i++){
                        mHtcFooterArrary[i].setBackgroundStyleMode(HtcFooter.STYLE_MODE_PURELIGHT);
                    }
                }
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if(v.getId() == R.id.showpopupwindow){
            ActionAdapter adapter = new ActionAdapter();

            HtcPopupWindowWrapper hpww = new HtcPopupWindowWrapper();
            hpww.setAdapter(adapter);
            hpww.setArchorView(v);
            hpww.setOnItemClickListener(this);
            hpww.setOnDismissListener(this);

            try {
                hpww.showPopupWindow();
            } catch (Exception e) {
            }
        }else if (v.getId() == R.id.showpopupexpwindow) {

            ExpandActionAdapter adapterExp = new ExpandActionAdapter();
            HtcPopupWindowWrapper hpww = new HtcPopupWindowWrapper();
            hpww.setAdapter(adapterExp);
            hpww.setArchorView(v);
            hpww.setOnChildClickListener(this);
            hpww.setOnGroupClickListener(this);
            hpww.setOnDismissListener(this);

            try {
                hpww.showPopupWindow();
            } catch (Exception e) {
            }
        }else if (v.getId() == R.id.showhtcshareviapopup) {
            final HtcShareViaAdapter mAdapter = new HtcShareViaAdapter(createShareIntent(), mContext);
            final HtcPopupWindowWrapper mWrapper = new HtcPopupWindowWrapper();

            AdapterView.OnItemClickListener mCustomClickListener;
            mCustomClickListener = new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                        int position, long id) {
                    Intent i = (Intent)mAdapter.getItem(position); /* must-be */
                    mWrapper.dismiss(); /* must-be */

                    // TODO Application's TODO below.
                    Toast.makeText(mContext,
                            "Item " + position + ", i=" + i, Toast.LENGTH_SHORT).show();
                }
            };

            ShareViaOnItemClickListener mListener = mWrapper.new ShareViaOnItemClickListener(mAdapter, mCustomClickListener);

            mWrapper.setOnItemClickListener(mListener);
            mWrapper.setArchorView(v);
            mWrapper.setAdapter(mAdapter);
            mWrapper.showPopupWindow();
        }
    }

    private Intent createShareIntent() {
        String temp = "png";
        Intent i = new Intent(Intent.ACTION_SEND);
        String strMimeType = MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(temp);
        i.setType(strMimeType);
        return i;
    }

    private class ActionAdapter extends BaseAdapter{

        private String[] ItemStrings = new String[] { "listitem1", "listitem2", "listitem3",
                "listitem4", "listitem5", "listitem6" };

        public int getCount() {
            return ItemStrings.length;
        }

        public Object getItem(int position) {
            return ItemStrings[position];
        }


        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            HtcListItem listitem = (HtcListItem)mInflater.inflate(R.layout.actionbarlistitem, null);
            HtcListItem1LineCenteredText text = (HtcListItem1LineCenteredText)listitem.findViewById(R.id.text1);
            text.setText(ItemStrings[position]);
            listitem.setBackgroundDrawable(null);
            return listitem;

        }

    }

    private class ExpandActionAdapter extends BaseExpandableListAdapter{


        private String[] GroupItme = new String[] { "Group1", "Group2", "Group3",
                "Group4", "Group5", "Group6" };

        private String[] SubItem = new String[] {"subItem1","subItem2","subItem3","subItem4","subItem5",
                "subItem6","subItem7","subItem8","subItem9","subItem10",
                "subItem11","subItem12","subItem13","subItem14","subItem15",
                "subItem16","subItem17","subItem18","subItem19","subItem20",
                "subItem21","subItem22","subItem23","subItem24","subItem25",
                "subItem26","subItem27","subItem28","subItem29","subItem30"};

        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        public View getChildView(int groupPosition, int childPosition,
                boolean isLastChild, View convertView, ViewGroup parent) {
            HtcListItem listitem = (HtcListItem)mInflater.inflate(R.layout.actionbarlistitem, null);
            HtcListItem1LineCenteredText text = (HtcListItem1LineCenteredText)listitem.findViewById(R.id.text1);
            text.setText(SubItem[5*groupPosition + childPosition]);
            listitem.setLeftIndent(true);
            listitem.setBackgroundDrawable(null);
            return listitem;
        }

        public int getChildrenCount(int groupPosition) {
            return 5;
        }

        public Object getGroup(int groupPosition) {
            return groupPosition;
        }

        public int getGroupCount() {
            return GroupItme.length;
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded,
                View convertView, ViewGroup parent) {
            HtcListItem listitem = (HtcListItem)mInflater.inflate(R.layout.htclistview_group_item_popup, null);
            HtcListItem2LineText text = (HtcListItem2LineText)listitem.findViewById(R.id.text1);
            text.setPrimaryText(GroupItme[groupPosition]);
            text.setSecondaryText(null);
            return listitem;
        }

        public boolean hasStableIds() {
            return false;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }





    @Override
    public void onDismiss() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
            long arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onGroupClick(ExpandableListView arg0, View arg1,
            int arg2, long arg3) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onChildClick(ExpandableListView arg0, View arg1,
            int arg2, int arg3, long arg4) {
        // TODO Auto-generated method stub
        return false;
    }


}
