package com.htc.lib1.cc.widget.preference;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.widget.HtcListView;
import com.htc.lib1.cc.widget.IHtcAbsListView;

public class HtcPreferenceActivity extends PreferenceActivity {

    private ArrayList<Header> mHeaders = new ArrayList<Header>();
    private static final String HEADERS_TAG = ":android:headers";
    /**
     * @hide
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mHeaders = savedInstanceState.getParcelableArrayList(HEADERS_TAG);
            if (mHeaders != null) {
                setListAdapter(getListAdapter());
            }
        }
    }

    /**
     * @hide
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onContentChanged() {
        ViewGroup container= (ViewGroup) getWindow().getDecorView();
        Resources res = getResources();
        int margin_l = res.getDimensionPixelSize(R.dimen.margin_l);
        ListView list = (ListView) container.findViewById(android.R.id.list);
        if(list != null){
            LayoutParams params = list.getLayoutParams();
            HtcListView htclist = new HtcListView(this);
            htclist.setId(android.R.id.list);
            htclist.setLayoutParams(params);
            htclist.setCacheColorHint(Color.TRANSPARENT);
            htclist.enableAnimation(IHtcAbsListView.ANIM_OVERSCROLL, false);
            htclist.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
            PreferenceUtil.applyHtcListViewStyle(htclist);
            ViewGroup parent = (ViewGroup) list.getParent();
            parent.setPadding(0, 0, 0, 0);
            int x = parent.indexOfChild(list);
            parent.addView(htclist, x);
            parent.removeView(list);
            list.setBackgroundResource(com.htc.lib1.cc.R.drawable.common_app_bkg);
        }
        super.onContentChanged();
    }
    /**
     * @hide
     */
    @Override
    public void onBuildHeaders(List<Header> target) {
        mHeaders = (ArrayList<Header>) target;
        if(mHeaders !=null && mHeaders.size() > 0){
            super.onBuildHeaders(mHeaders);
        }else{
            super.onBuildHeaders(target);
        }
    }
    /**
     * @hide
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mHeaders !=null && mHeaders.size() > 0) {
            outState.putParcelableArrayList(HEADERS_TAG, mHeaders);
        }
    }
    /**
     * @hide
     */
    @Override
    public void setListAdapter(ListAdapter adapter) {
        if(mHeaders !=null && mHeaders.size() > 0){
            adapter = new HeaderAdapter(new ContextThemeWrapper(this, R.style.Preference), mHeaders);
        }
        super.setListAdapter(adapter);
    }
    private static class HeaderAdapter extends ArrayAdapter<Header> {
        private static class HeaderViewHolder {
            ImageView icon;
            TextView title;
            TextView summary;
        }

        private LayoutInflater mInflater;

        public HeaderAdapter(Context context, List<Header> objects) {
            super(context, 0, objects);
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HeaderViewHolder holder;
            View view;

            if (convertView == null) {
                view = mInflater.inflate(com.htc.lib1.cc.R.layout.preference_header_item,
                        parent, false);
                holder = new HeaderViewHolder();
                holder.icon = (ImageView) view.findViewById(android.R.id.icon);
                holder.title = (TextView) view.findViewById(android.R.id.title);
                holder.summary = (TextView) view.findViewById(android.R.id.summary);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (HeaderViewHolder) view.getTag();
            }

            // All view fields must be updated every time, because the view may be recycled
            Header header = getItem(position);
            holder.icon.setImageResource(header.iconRes);
            holder.title.setText(header.getTitle(getContext().getResources()));
            CharSequence summary = header.getSummary(getContext().getResources());
            if (!TextUtils.isEmpty(summary)) {
                holder.summary.setVisibility(View.VISIBLE);
                holder.summary.setText(summary);
            } else {
                holder.summary.setVisibility(View.GONE);
            }

            return view;
        }
    }
}
