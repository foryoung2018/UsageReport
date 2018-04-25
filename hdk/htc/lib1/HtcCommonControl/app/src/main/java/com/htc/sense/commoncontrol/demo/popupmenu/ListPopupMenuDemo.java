
package com.htc.sense.commoncontrol.demo.popupmenu;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;

import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcListItem2LineStamp;
import com.htc.lib1.cc.widget.HtcListItem2LineText;
import com.htc.lib1.cc.widget.HtcListItemQuickContactBadge;
import com.htc.lib1.cc.widget.HtcListItemSeparator;
import com.htc.lib1.cc.widget.ListPopupBubbleWindow;
import com.htc.lib1.cc.widget.PopupBubbleWindow;
import com.htc.lib1.cc.widget.QuickContactBadge;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

import static com.htc.lib1.cc.util.WindowUtil.isSuitableForLandscape;

public class ListPopupMenuDemo extends CommonDemoActivityBase {
    LayoutInflater mInflater = null;
    private Context mContext;
    private ActionAdapter mAdapter;
    private Button mTopBtn;
    private Button mBottomBtn;
    private CheckBox mIsKeepShow;
    private ListPopupBubbleWindow mPopupTop, mPopupBottom;

    private OnClickListener mTopBtnListener;
    private OnClickListener mBottomBtnListener;
    private PopupBubbleWindow.OnDismissListener mDismissListener;

    private ViewTreeObserver mVto;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.drawable.common_app_bkg);
        mContext = this;
        setLayout();
        init();
    }

    private void dismissWindow() {
        if (mIsKeepShow.isChecked()) {
            return;
        }
        if (null != mPopupTop) {
            mPopupTop.dismiss();
        }
        if (null != mPopupBottom) {
            mPopupBottom.dismiss();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        dismissWindow();
        setLayout();
        init();
    }

    private void init() {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mAdapter = new ActionAdapter();

        mPopupTop = new ListPopupBubbleWindow(mContext);
        mPopupBottom = new ListPopupBubbleWindow(mContext);
        mPopupTop.setModal(true);//to prevent leak window
        mPopupBottom.setModal(true);//to prevent leak window

        mTopBtn = (Button) findViewById(R.id.keep_show_btn);
        mBottomBtn = (Button) findViewById(R.id.btn);
        mIsKeepShow = (CheckBox) findViewById(R.id.keepShow);

        setPopupWindowParams();
        setBtnClickListener();
        setExpandDirection();
        setDismissListener();

        mVto = mTopBtn.getViewTreeObserver();
    }

    private class ActionAdapter extends BaseAdapter {
        final int layouts[] = new int[] {
                R.layout.actionbarlistitem0,
                R.layout.actionbarlistitem1, R.layout.actionbarlistitem2,
                R.layout.actionbarlistitem3, R.layout.actionbarlistitem4
        };

        public int getCount() {
            return layouts.length + 1;
        }

        public Object getItem(int position) {
            return layouts[position];
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                HtcListItemSeparator s = new HtcListItemSeparator(mContext,
                        HtcListItemSeparator.MODE_DARK_STYLE, HtcListItemSeparator.MODE_DARK_STYLE);
                s.setText(HtcListItemSeparator.TEXT_LEFT, "popupMenu separator");
                return s;
            }
            else {
                HtcListItem i;
                i = (HtcListItem) mInflater.inflate(layouts[(position - 1) % layouts.length], null);

                int index = (position - 1) % getCount();

                if (index == 0) {
                    HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                    text.setPrimaryText("Text fLing 0, 2text, secondary text is gone.");
                    text.setSecondaryTextVisibility(View.GONE);

                } else if (index == 1) {
                    HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                    text.setPrimaryText("Text fLing 1, 2text primary");
                    text.setSecondaryText("Text fLing 1, 2text secondary");

                } else if (index == 2) {
                    HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                    text.setPrimaryText("Text fLing 2");
                    text.setSecondaryText("Text fLing 2, 2text with 2stamp");

                    HtcListItem2LineStamp stamp = (HtcListItem2LineStamp) i
                            .findViewById(R.id.stamp1);
                    stamp.setPrimaryText("Exchange");
                    stamp.setSecondaryText("Exchange");
                    stamp.setSecondaryTextVisibility(View.GONE);

                } else if (index == 3) {
                    HtcListItemQuickContactBadge image = (HtcListItemQuickContactBadge) i
                            .findViewById(R.id.photo);
                    QuickContactBadge badge = image.getBadge();
                    badge.setImageResource(R.drawable.icon_category_photo);

                    HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                    text.setPrimaryText("Text fLing 3");
                    text.setSecondaryText("Text fLing 3, QuickBadge, 2text");

                } else if (index == 4) {
                    HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                    text.setPrimaryText("Text fLing 4");
                    text.setSecondaryText("Text fLing 4, 2text with checkbox");

                    i.setVerticalDividerEnabled(true);
                }
                return i;
            }
        }
    }

    //20120626

    private void setDismissListener() {
        mDismissListener = new PopupBubbleWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        };

        mPopupTop.setOnDismissListener(mDismissListener);
    }

    private int measureContentWidth(Adapter adapter) {
        // Menus don't tend to be long, so this is more sane than it looks.
        int width = 0;
        int itemType = 0;
        View itemView = null;
        final int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED);
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            final int positionType = adapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }
            itemView = adapter.getView(i, null, null);
            itemView.measure(widthMeasureSpec, heightMeasureSpec);
            width = Math.max(width, itemView.getMeasuredWidth());
        }
        return width;
    }

    private void setBtnClickListener() {
        mTopBtnListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupTop.isShowing()) {
                    mPopupTop.dismiss();
                } else {
                    mPopupTop.show();
                }
            }
        };
        mBottomBtnListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupBottom.isShowing()) {
                    mPopupBottom.dismiss();
                } else {
                    mPopupBottom.show();
                }
            }
        };

        mTopBtn.setOnClickListener(mTopBtnListener);
        mBottomBtn.setOnClickListener(mBottomBtnListener);
    }

    private void setExpandDirection() {
        if (isSuitableForLandscape(mContext.getResources())) {
            mPopupTop.setExpandDirection(PopupBubbleWindow.EXPAND_RIGHT);
        } else {
            mPopupTop.setExpandDirection(PopupBubbleWindow.EXPAND_DOWN);
        }

        if (isSuitableForLandscape(mContext.getResources())) {
            mPopupBottom.setExpandDirection(PopupBubbleWindow.EXPAND_LEFT);
        } else {
            mPopupBottom.setExpandDirection(PopupBubbleWindow.EXPAND_UP);
        }
    }

    private void setLayout() {
        setContentView(R.layout.list_popup_menu_layout);
    }

    private void setPopupWindowParams() {
        mPopupTop.setAnchorView(mTopBtn);
        mPopupTop.setAdapter(mAdapter);
        // You should set content width. If you don't set it,
        // it will show up width according anchor view width.
        mPopupTop.setContentWidth(measureContentWidth(mAdapter));

        mPopupBottom.setAnchorView(mBottomBtn);
        mPopupBottom.setAdapter(mAdapter);
    }

}
