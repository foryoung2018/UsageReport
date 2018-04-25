package com.htc.lib1.cc.widget.preference;

import android.content.res.Resources;
import android.graphics.Color;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.widget.HtcListView;
import com.htc.lib1.cc.widget.IHtcAbsListView;
public class HtcPreferenceFragment extends PreferenceFragment {

    /**
     * @hide
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        ViewGroup content = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        ListView list = (ListView) content.findViewById(android.R.id.list);
        Resources res = getResources();
        int margin_l = res.getDimensionPixelSize(R.dimen.margin_l);
        if(list != null){
            LayoutParams params = list.getLayoutParams();
            HtcListView htclist = new HtcListView(getActivity());
            htclist.setId(android.R.id.list);
            htclist.setPadding(margin_l, 0, margin_l, 0);
            htclist.setLayoutParams(params);
            htclist.setCacheColorHint(Color.TRANSPARENT);
            htclist.setBackgroundResource(com.htc.lib1.cc.R.drawable.common_app_bkg);
            htclist.setDivider(getResources().getDrawable(R.drawable.common_list_divider));
            htclist.setSelector(R.drawable.list_selector_light);
            htclist.enableAnimation(IHtcAbsListView.ANIM_OVERSCROLL, false);
            htclist.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
            ViewGroup parent = (ViewGroup) list.getParent();
            int x = parent.indexOfChild(list);
            parent.addView(htclist, x);
            parent.removeView(list);
        }
        TextView  emptyView = (TextView) content.findViewById(android.R.id.empty);
        if(emptyView != null){
            emptyView.setTextAppearance(getActivity(), R.style.list_body_secondary_l);
            emptyView.setBackgroundResource(com.htc.lib1.cc.R.drawable.common_app_bkg);
        }

        return content;
    }
}
