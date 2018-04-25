package com.htc.sense.commoncontrol.demo.htclistitem;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;

import com.htc.lib1.cc.widget.HtcImageButton;
import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcListItem1LineCenteredText;
import com.htc.lib1.cc.widget.HtcListItem1LineTextProgressBar;
import com.htc.lib1.cc.widget.HtcListItem2LineTextProgressBar;
import com.htc.lib1.cc.widget.HtcListItemColorIcon;
import com.htc.lib1.cc.widget.HtcProgressBar;
import com.htc.lib1.cc.widget.ListItem;
import com.htc.sense.commoncontrol.demo.R;

public class HtcListItemActivity12 extends HtcListActivity {
    LayoutInflater mInflater = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setListAdapter(new MyListAdapter(this));
        getWindow().setBackgroundDrawableResource(com.htc.lib1.cc.R.drawable.common_app_bkg);
        getListView().setCacheColorHint(0x00000000);
    }

    @Override
    protected void initMenu() {
        mAddMenuShowListItem = true;
    }

    private class MyListAdapter extends BaseAdapter {
        Context mContext = null;

        final int layoutsHtcListItem[] = new int[] {
                R.layout.list_item27, R.layout.list_item28, R.layout.list_item28,
                R.layout.list_item29, R.layout.list_item29
        };

        final int layoutsListItem[] = new int[] {
                R.layout.list_item27_new, R.layout.list_item28_new, R.layout.list_item28_new,
                R.layout.list_item29_new, R.layout.list_item29_new
        };

        final int LIST_ITEM_COUNT = layoutsHtcListItem.length;

        public MyListAdapter(Context context) {
            mContext = context;
        }

        public int getCount() {
            return LIST_ITEM_COUNT;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return LIST_ITEM_COUNT;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewGroup i = (ViewGroup) mInflater.inflate(mShowListItem ? layoutsListItem[position] : layoutsHtcListItem[position], null);

            if (position == 0) {
                HtcListItem1LineCenteredText text = (HtcListItem1LineCenteredText) i.findViewById(R.id.centered_text);
                text.setText("Text fLing 3, centeredText.");
                ProgressBar pb = new ProgressBar(mContext, null, android.R.attr.progressBarStyleSmall);
                Drawable progressDrawable = getResources().getDrawable(R.drawable.progress_light);
                pb.setIndeterminateDrawable(progressDrawable);
                pb.setVisibility(View.VISIBLE);
                text.setView(pb);

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, mMargin[M1], 0);
                }

            } else if (position == 1) {
                HtcListItemColorIcon image = (HtcListItemColorIcon) i.findViewById(R.id.photo);
                image.setColorIconImageResource(R.drawable.icon_launcher_programs);

                HtcListItem2LineTextProgressBar progress = (HtcListItem2LineTextProgressBar) i.findViewById(R.id.progress);
                progress.setPrimaryText("Text fLing 4, 2LineTextProgressBar");
                progress.setSecondaryText("Text2");

                if (mShowListItem) {
                    setMargin(image, mMargin[M1], 0, mMargin[M2], 0);
                    setMargin(progress, 0, 0, mMargin[M1], 0);
                }

            } else if (position == 2) {
                HtcListItemColorIcon image = (HtcListItemColorIcon) i.findViewById(R.id.photo);
                image.setColorIconImageResource(R.drawable.icon_launcher_programs);

                HtcListItem2LineTextProgressBar progress = (HtcListItem2LineTextProgressBar) i.findViewById(R.id.progress);
                progress.setPrimaryText("Text fLing 5, 2LineTextProgressBar");
                progress.setSecondaryText("Text2");
                HtcProgressBar mBar = (HtcProgressBar) progress.getProgressBar();
                mBar.setProgress(50);

                if (mShowListItem) {
                    setMargin(image, mMargin[M1], 0, mMargin[M2], 0);
                    setMargin(progress, 0, 0, mMargin[M1], 0);
                } else {
                    ((HtcListItem) i).setFirstComponentTopMarginFixed(true);
                }

            } else if (position == 3) {
                HtcListItemColorIcon image = (HtcListItemColorIcon) i.findViewById(R.id.photo);
                image.setColorIconImageResource(R.drawable.icon_launcher_programs);

                HtcListItem1LineTextProgressBar progress = (HtcListItem1LineTextProgressBar) i.findViewById(R.id.progress);
                progress.setPrimaryText("Text fLing 6, 1LineTextProgressBar");
                HtcProgressBar mBar = (HtcProgressBar) progress.getProgressBar();
                mBar.setProgress(50);

                HtcImageButton button = (HtcImageButton) i.findViewById(R.id.imgButton);
                if (mShowListItem) {
                    setMargin(image, mMargin[M1], 0, mMargin[M2], 0);
                    setMargin(progress, 0, 0, mMargin[M2], 0);
                    setMargin(button, 0, 0, mMargin[M1], 0);
                }

            } else if (position == 4) {
                HtcListItemColorIcon image = (HtcListItemColorIcon) i.findViewById(R.id.photo);
                image.setColorIconImageResource(R.drawable.icon_launcher_programs);

                HtcListItem1LineTextProgressBar progress = (HtcListItem1LineTextProgressBar) i.findViewById(R.id.progress);
                progress.setPrimaryText("Text fLing 7, 1LineTextProgressBar");
                HtcProgressBar mBar = (HtcProgressBar) progress.getProgressBar();
                mBar.setProgress(50);
                progress.setStampText("Stamp");

                HtcImageButton button = (HtcImageButton) i.findViewById(R.id.imgButton);
                button.setImageResource(R.drawable.icon_btn_search_light);

                if (mShowListItem) {
                    setMargin(image, mMargin[M1], 0, mMargin[M2], 0);
                    setMargin(progress, 0, 0, mMargin[M2], 0);
                    button.setLayoutParams(new ListItem.LayoutParams(ListItem.LayoutParams.SIZE_147 | ListItem.LayoutParams.CENTER_VERTICAL | ListItem.LayoutParams.DIVIDER_START));
                    setMargin(button, 0, 0, 0, 0);
                } else {
                    ((HtcListItem) i).setVerticalDividerEnabled(true);
                }

            }
            return i;
        }

    }
}
