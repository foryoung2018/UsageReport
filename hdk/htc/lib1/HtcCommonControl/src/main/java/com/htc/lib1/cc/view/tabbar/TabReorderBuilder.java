package com.htc.lib1.cc.view.tabbar;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.widget.HtcCheckBox;
import com.htc.lib1.cc.widget.HtcCompoundButton;
import com.htc.lib1.cc.widget.HtcListItem;

/**
 * @hide
 * @deprecated [Module internal use]
 */
public class TabReorderBuilder {

    private Context mContext;
    private TabReorderFragment.TabReorderAdapter mAdapter;
    private boolean isCarMode = false;

    public TabReorderBuilder(Context context, TabReorderFragment.TabReorderAdapter adapter) {
        mContext = context;
        mAdapter = adapter;
        isCarMode = adapter.isAutomotiveMode();
    }

    public View getView (int position, View convertView, ViewGroup parent) {
        CharSequence label = mAdapter.getPageTitle(position);
        int m2 = TabBarUtils.dimen.m2(mContext);
        int c = mAdapter.getPageCount(position);
        boolean hasCount = c > 0;
        final int pos = position;

        //TODO: use convertView
        TextView count = null;
        if(hasCount) {
            RelativeLayout.LayoutParams lp0 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            lp0.addRule(RelativeLayout.LEFT_OF, android.R.id.edit);
            count = new TextView(mContext);
            count.setId(android.R.id.message);
            //TODO upper bound always = 100?
            if (c < 100) {
                count.setText("(" + c + ")");
            } else {
                count.setText("(99+)");
            }
            //TODO: car mode
            count.setTextAppearance(mContext, isCarMode? R.style.notification_info_m: R.style.notification_info_m);
            count.setTextColor(TabBarUtils.color.category(mContext));
            count.setGravity(Gravity.CENTER_VERTICAL);
            count.setLayoutParams(lp0);
            count.setPadding(0, 0, m2, 0);
        }

        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        lp1.addRule(RelativeLayout.RIGHT_OF, android.R.id.checkbox);
        lp1.addRule(RelativeLayout.LEFT_OF, hasCount? android.R.id.message: android.R.id.edit);
        TextView tv = new TextView(mContext);
        tv.setText(label);
        tv.setSingleLine();
        tv.setEllipsize(TruncateAt.END);
        //TODO: car mode
        tv.setTextAppearance(mContext, isCarMode? R.style.list_primary_m: R.style.list_primary_m);
        tv.setGravity(Gravity.CENTER_VERTICAL);
        tv.setLayoutParams(lp1);
        tv.setPadding(m2, 0, m2, 0);

        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lp2.addRule(RelativeLayout.CENTER_VERTICAL);
        HtcCheckBox cb = new HtcCheckBox(mContext);
        cb.setId(android.R.id.checkbox);
        cb.setLayoutParams(lp2);
        cb.setChecked(mAdapter.isVisible(position));
        cb.setEnabled(mAdapter.isRemoveable(position));
        cb.setFocusable(false);
        cb.setOnCheckedChangeListener(new HtcCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(HtcCompoundButton buttonView, boolean isChecked) {
                if(!mAdapter.onVisibilityChanged(pos, isChecked))
                    buttonView.setChecked(mAdapter.isVisible(pos));
            }
        });

        RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp3.addRule(RelativeLayout.CENTER_VERTICAL);
        ImageView drag = new ImageView(mContext);
        drag.setId(android.R.id.edit);
        drag.setLayoutParams(lp3);
        drag.setImageResource(R.drawable.common_rearrange_rest);

        RelativeLayout root = new RelativeLayout(mContext);
        root.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        root.addView(cb);
        root.addView(tv);
        if(hasCount)
            root.addView(count);
        root.addView(drag);

        HtcListItem contenter = new HtcListItem(mContext);
        contenter.addView(root);

        //naeco: accessibility - TalkBack
        contenter.setContentDescription(label);

        //naeco: size of TouchDelegate
        // can't access: contenter.getCustomLayoutParams();
        // can't access: com.htc.lib1.cc.widget.HtcListItemUtil.getDesiredListItemHeight(HtcListItem.MODE_DEFAULT);
        contenter.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                // if item height changed
                if((bottom - top) != (oldBottom - oldTop)) {
                    ViewGroup vg;
                    // HtcListItem -> RelativeLayout -> HtcCheckBox
                    if(v instanceof HtcListItem) {
                        vg = (ViewGroup) v;
                        if(vg.getChildCount() > 0 && vg.getChildAt(0) instanceof RelativeLayout) {
                            vg = (ViewGroup) vg.getChildAt(0);
                            if(vg.getChildCount() > 0 && vg.getChildAt(0) instanceof HtcCheckBox)
                                v.setTouchDelegate(new TouchDelegate(new Rect(0, 0, bottom - top, bottom - top), vg.getChildAt(0)));
                        }
                    }
                }
            }
        });
        return contenter;
    }

    public void setAutomotiveMode(boolean enabled) {
        isCarMode = enabled;
    }
}
