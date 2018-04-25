
package com.htc.lib1.cc.popupWindow.activityhelper.popupmenu;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcListItem1LineCenteredText;
import com.htc.lib1.cc.widget.HtcListView;
import com.htc.lib1.cc.widget.PopupBubbleWindow;
import com.htc.lib1.cc.test.R;

public class PopupMenuDemo extends ActivityBase implements OnGlobalLayoutListener {
    LayoutInflater mInflater = null;
    private Context mContext;
    private ActionAdapter mAdapter;
    private final int POPUP_WINDOW_HEIGHT = 300;

    private Button mTopBtn;
    private Button mBottomBtn;
    private HtcListView mListView;
    private PopupBubbleWindow mPopupTop;
    private PopupBubbleWindow mPopupBottom;

    private OnClickListener mTopBtnListener;
    private OnClickListener mBottomBtnListener;

    private ViewTreeObserver mVto;

    private String[] mStrings = {
            "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi", "Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre",
            "Allgauer Emmentaler",
            "Alverca", "Ambert", "American Cheese", "Ami du Chambertin", "Anejo Enchilado", "Anneau du Vic-Bilh", "Anthoriro", "Appenzell", "Aragon", "Ardi Gasna", "Ardrahan", "Armenian String",
    };

    private int mMinWidth;
    private int mFooter_width;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.drawable.common_app_bkg);
        setContentView(R.layout.popup_menu);
        final ActionBar actionBar = getActionBar();
        final ColorDrawable background = new ColorDrawable(Color.GRAY);
        actionBar.setBackgroundDrawable(background);
        mContext = this;
        init();
    }

    @Override
    protected boolean isInitOrientation() {
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setExpandDirection();
    }

    private void init() {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListView = new HtcListView(mContext, null, com.htc.lib1.cc.R.attr.dropDownListViewStyle);
        mAdapter = new ActionAdapter();
        mListView.setAdapter(mAdapter);
        mPopupTop = new PopupBubbleWindow(mContext);
        mPopupBottom = new PopupBubbleWindow(mContext);

        mTopBtn = (Button) findViewById(R.id.keep_show_btn);
        mBottomBtn = (Button) findViewById(R.id.btn);

        setPopupWindowParams();
        setBtnClickListener();
        setExpandDirection();

        mVto = mTopBtn.getViewTreeObserver();
        mVto.addOnGlobalLayoutListener(this);

        mFooter_width = mContext.getResources().getDrawable(com.htc.lib1.cc.R.drawable.common_app_bkg_down_src).getIntrinsicHeight();
    }

    private void hideScrollView() {
        if (null != mListView) {
            mListView.setVerticalScrollBarEnabled(false);
        }
    }

    public PopupBubbleWindow getTopPopupWindow() {
        return mPopupTop;
    }

    public HtcListView getHtcListViewInWindow() {
        return mListView;
    }

    private void initWidthLimit() {
        Rect rect = new Rect();
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getRectSize(rect);

        boolean isPortrait = rect.right < rect.bottom;
        int width = rect.right;
        int margin_m2 = (int) mContext.getResources().getDimension(com.htc.lib1.cc.R.dimen.margin_m);

        mMinWidth = isPortrait ? (int) (width * 0.7 - margin_m2) : (int) (width * 0.6 - mFooter_width - margin_m2);

    }

    private int checkWidthLimit(int width) {
        initWidthLimit();
        width = Math.max(width, mMinWidth);
        return width;
    }

    private class ActionAdapter extends BaseAdapter {

        private String[] ItemStrings = new String[] {
                "listitem1", "listitem2", "listitem3", "listitem4", "listitem5", "listitem6", "listitem7", "listitem8", "listitem9", "listitem10"
        };

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
            HtcListItem listitem = (HtcListItem) mInflater.inflate(R.layout.actionbarlistitem, null);
            HtcListItem1LineCenteredText text = (HtcListItem1LineCenteredText) listitem.findViewById(R.id.text1);
            text.setText(ItemStrings[position]);
            return listitem;

        }

    }

    @Override
    public void onGlobalLayout() {
        if (mPopupTop.isShowing()) {
            if (null != mTopBtn && !mTopBtn.isShown()) {
                mPopupTop.dismiss();
            } else {
                mPopupTop.dismiss();
                mPopupTop.showAsDropDown(mTopBtn);
                hideScrollView();
            }
        }

        if (mPopupBottom.isShowing()) {
            mPopupBottom.dismiss();
        }
    }

    public void dismissWindow() {
        if (null != mPopupTop) {
            mPopupTop.dismiss();
        }
        if (null != mPopupBottom) {
            mPopupBottom.dismiss();
        }
    }
    private int measureContentWidth(Adapter adapter) {
        // Menus don't tend to be long, so this is more sane than it looks.
        int width = 0;
        int itemType = 0;
        View itemView = null;
        final int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
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

    private void setPopupWindowParams() {
        mPopupTop.setWindowLayoutMode(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int width = checkWidthLimit(measureContentWidth(mAdapter));
        mPopupTop.setWidth(width);
        mPopupBottom.setWidth(width);

        mPopupTop.setContentView(mListView);
        mPopupTop.setFocusable(true);
        mPopupTop.setOutsideTouchable(true);
        mPopupTop.setClipToScreenEnabled(true);

        mPopupBottom.setHeight((int) (POPUP_WINDOW_HEIGHT * mContext.getResources().getDisplayMetrics().density));
        mPopupBottom.setContentView(mListView);
        mPopupBottom.setFocusable(true);
        mPopupBottom.setOutsideTouchable(true);
        mPopupBottom.setClipToScreenEnabled(true);
    }

    private void setExpandDirection() {
        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mPopupTop.setExpandDirection(PopupBubbleWindow.EXPAND_LEFT);
        } else {
            mPopupTop.setExpandDirection(PopupBubbleWindow.EXPAND_DOWN);
        }

        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mPopupBottom.setExpandDirection(PopupBubbleWindow.EXPAND_RIGHT);
        } else {
            mPopupBottom.setExpandDirection(PopupBubbleWindow.EXPAND_UP);
        }
    }

    private void setBtnClickListener() {
        mTopBtnListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupTop.isShowing()) {
                    mPopupTop.dismiss();
                } else {
                    mPopupTop.showAsDropDown(mTopBtn);
                    hideScrollView();
                }
            }
        };

        mBottomBtnListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupBottom.isShowing()) {
                    mPopupBottom.dismiss();
                } else {
                    mPopupBottom.showAsDropDown(mBottomBtn);
                    hideScrollView();
                }
            }
        };

        mTopBtn.setOnClickListener(mTopBtnListener);
        mBottomBtn.setOnClickListener(mBottomBtnListener);
    }

}
