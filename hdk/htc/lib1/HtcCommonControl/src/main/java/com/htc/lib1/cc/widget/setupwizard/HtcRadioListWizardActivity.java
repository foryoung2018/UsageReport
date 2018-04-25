/*
 * HTC Corporation Proprietary Rights Acknowledgment
 *
 * Copyright (C) 2008 HTC Corporation
 *
 * All Rights Reserved.
 *
 * The information contained in this work is the exclusive property of HTC Corporation
 * ("HTC").  Only the user who is legally authorized by HTC ("Authorized User") has
 * right to employ this work within the scope of this statement.  Nevertheless, the
 * Authorized User shall not use this work for any purpose other than the purpose
 * agreed by HTC.  Any and all addition or modification to this work shall be
 * unconditionally granted back to HTC and such addition or modification shall be
 * solely owned by HTC.  No right is granted under this statement, including but not
 * limited to, distribution, reproduction, and transmission, except as otherwise
 * provided in this statement.  Any other usage of this work shall be subject to the
 * further written consent of HTC.
 */

package com.htc.lib1.cc.widget.setupwizard;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.htc.lib1.cc.util.HtcWrapConfigurationUtil;
import com.htc.lib1.cc.util.WindowUtil;
import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcListItem2LineText;
import com.htc.lib1.cc.widget.HtcListView;
import com.htc.lib1.cc.widget.HtcRadioButton;

import java.util.Locale;

/**
 * SetupWizard common UI instead subContentView of HtcWizardActivity origin one
 * subContentView with one image+two HtcListitem with Radio
 * button+TextBody+customized View
 *
 * @author chris_wang@htc.com
 *
 */
public class HtcRadioListWizardActivity extends HtcWizardActivity {
    // UI components
    private HtcItemContainerListView mListView;
    private View mImageLayout;
    private ImageView mImage;
    private View mDivider;
    private LinearLayout mContentLayout;
    private TextView mText;
    private View mCustomView;

    // Radio list
    private CharSequence mItem1PrimaryText;
    private CharSequence mItem2PrimaryText;
    private CharSequence mItem1SecondaryText;
    private CharSequence mItem2SecondaryText;
    private boolean mbItem1Enabled = true;
    private boolean mbItem2Enabled = true;
    private static final boolean mIsSupportRTL = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;

    private int mImageResId;
    private Drawable mImageDrawable;
    private CharSequence mDescTextString;
    private int mDescTextResId;

    private static final int UNKNOWN = -1;
    /**
     * specify HtcListItem1 with Radio Button
     */
    public static final int ITEM1 = 0;
    /**
     * specify HtcListItem2 with Radio Button
     */
    public static final int ITEM2 = 1;
    private int mCheckedItem = UNKNOWN;

    private BaseAdapter mAdapter;
    private OnItemClickListener mOnItemClickListener;

    private Configuration mConfig;
    private boolean mMinorFontStyle = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConfig = new Configuration(getResources().getConfiguration());
    }

    @Override
    public void onDelayUIUpdate() {
        initialize();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        HtcWrapConfigurationUtil.applyHtcFontscale(this);
        if (isOrientationChanged(newConfig, mConfig)) {
            initialize();
        }
        mConfig = new Configuration(newConfig);
    }

    private void initialize() {
        setSubContentView(com.htc.lib1.cc.R.layout.wizard_radio_list_activity);

        if (mContentLayout != null && mCustomView != null) {
            mContentLayout.removeView(mCustomView);
        }

        mContentLayout = (LinearLayout) findViewById(com.htc.lib1.cc.R.id.content_layout);
        mImageLayout = findViewById(com.htc.lib1.cc.R.id.image_layout);
        mImage = (ImageView) findViewById(com.htc.lib1.cc.R.id.image);
        mDivider = findViewById(com.htc.lib1.cc.R.id.divider);
        mText = (TextView) findViewById(com.htc.lib1.cc.R.id.desc);

        if (mImageResId != 0) {
            setImage(mImageResId);
        } else if (mImageDrawable != null) {
            setImage(mImageDrawable);
        }

        if (mDescTextResId != 0) {
            setDescriptionText(mDescTextResId);
        } else if (mDescTextString != null) {
            setDescriptionText(mDescTextString);
        }

        if (mCustomView != null) {
            addCustomBottomView(mCustomView);
        }
        if (mMinorFontStyle)
            setMinorDescriptionStyle(mMinorFontStyle);

        initList();
    }

    private void initList() {
        mListView = (HtcItemContainerListView) findViewById(android.R.id.list);
        mListView.enableAnimation(HtcListView.ANIM_OVERSCROLL, false);
        mListView.setFocusable(true);
        if (mAdapter == null) {
            mAdapter = new HtcItemContainerAdapter(this);
        }
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                if (mAdapter instanceof HtcItemContainerAdapter) {
                    ((HtcItemContainerAdapter) mAdapter).onItemClick(position);
                }
                if (mOnItemClickListener != null) {
                    mOnItemClickListener
                            .onItemClick(parent, view, position, id);
                }
            }
        });
    }

    private class HtcItemContainerAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private static final int ITEM1_INDEX = 0;
        private static final int ITEM2_INDEX = 1;
        private static final int ITEM_COUNT = 2;

        HtcItemContainerAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return ITEM_COUNT;
        }

        @Override
        public Object getItem(int idx) {
            return null;
        }

        @Override
        public long getItemId(int idx) {
            return idx;
        }

        private class ViewTag {
            public HtcListItem mItem;
            public HtcListItem2LineText mTitle;
            public HtcRadioButton mRadio;

            public ViewTag(HtcListItem item, HtcListItem2LineText title,
                    HtcRadioButton radio) {
                mItem = item;
                mTitle = title;
                mRadio = radio;
            }
        }

        @Override
        public View getView(int idx, View convertView, ViewGroup parent) {
            Object viewTag = null;
            if (convertView == null) {
                convertView = mInflater.inflate(
                        com.htc.lib1.cc.R.layout.wizard_radio_listitem, null);

                HtcListItem2LineText title = (HtcListItem2LineText) convertView
                        .findViewById(com.htc.lib1.cc.R.id.title);
                if (title != null) {
                    title.setSecondaryTextSingleLine(false);
                }
                HtcRadioButton radio = (HtcRadioButton) convertView
                        .findViewById(com.htc.lib1.cc.R.id.radio);
                viewTag = new ViewTag((HtcListItem) convertView, title, radio);
                convertView.setTag(viewTag);
            } else {
                viewTag = convertView.getTag();
            }

            if (idx == ITEM1_INDEX) {
                ViewTag tag = (ViewTag) viewTag;
                if (tag.mItem != null && tag.mTitle != null
                        && tag.mRadio != null) {
                    tag.mItem.setEnabled(mbItem1Enabled);
                    tag.mTitle.setPrimaryText(mItem1PrimaryText);
                    tag.mTitle.setSecondaryText(mItem1SecondaryText);
                    tag.mRadio.setChecked(ITEM1 == mCheckedItem ? true : false);
                    if (mIsSupportRTL && View.LAYOUT_DIRECTION_RTL != TextUtils
                            .getLayoutDirectionFromLocale(Locale.getDefault())) {
                        tag.mItem.setLastComponentAlign(true);
                    }
                }
            } else if (idx == ITEM2_INDEX) {
                ViewTag tag = (ViewTag) viewTag;
                if (tag.mItem != null && tag.mTitle != null
                        && tag.mRadio != null) {
                    tag.mItem.setEnabled(mbItem2Enabled);
                    tag.mTitle.setPrimaryText(mItem2PrimaryText);
                    tag.mTitle.setSecondaryText(mItem2SecondaryText);
                    tag.mRadio.setChecked(ITEM2 == mCheckedItem ? true : false);
                    if (mIsSupportRTL && View.LAYOUT_DIRECTION_RTL != TextUtils
                            .getLayoutDirectionFromLocale(Locale.getDefault())) {
                        tag.mItem.setLastComponentAlign(true);
                    }
                }
            }

            return convertView;
        }

        @Override
        public boolean isEnabled(int idx) {
            return (ITEM1_INDEX == idx) ? mbItem1Enabled : mbItem2Enabled;
        }

        public void onItemClick(int idx) {
            mCheckedItem = (idx == ITEM1_INDEX) ? ITEM1 : ITEM2;
            notifyDataSetChanged();
        }
    }

    /**
     * set check of HtcListitem with Radio Button
     *
     * @param item
     *            the input item can be
     *            HtcRadioListWizardActivity.ITEM1/HtcRadioListWizardActivity
     *            .ITEM2
     */
    protected void setCheckedItem(int item) {
        switch (item) {
        case ITEM1:
            mCheckedItem = ITEM1;
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
            break;
        case ITEM2:
            mCheckedItem = ITEM2;
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
            break;
        }
    }

    /**
     * get inner HtcListItem which be set checked
     *
     * @return return item which be set check
     */
    protected int getCheckedListItem() {
        return mCheckedItem;
    }

    /**
     * set inner HtcListItem1 enable
     *
     * @param enabled
     *            true is set item1 enable
     */
    protected void setItem1Enabled(boolean enabled) {
        mbItem1Enabled = enabled;
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * set inner HtcListItem2 enable
     *
     * @param enabled
     *            true is set item2 enable
     */
    protected void setItem2Enabled(boolean enabled) {
        mbItem2Enabled = enabled;
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    static final int MODE_ITEM1_PRIMARY_TEXT = 0;
    static final int MODE_ITEM2_PRIMARY_TEXT = 1;
    static final int MODE_ITEM1_SECONDARY_TEXT = 2;
    static final int MODE_ITEM2_SECONDARY_TEXT = 3;

    private void setItemText(int mode, CharSequence charSequence) {
        switch (mode) {
        case MODE_ITEM1_PRIMARY_TEXT:
            mItem1PrimaryText = charSequence;
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
            break;
        case MODE_ITEM2_PRIMARY_TEXT:
            mItem2PrimaryText = charSequence;
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
            break;
        case MODE_ITEM1_SECONDARY_TEXT:
            mItem1SecondaryText = charSequence;
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
            break;
        case MODE_ITEM2_SECONDARY_TEXT:
            mItem2SecondaryText = charSequence;
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
            break;
        }
    }

    /**
     * Set item1 primary text content
     *
     * @param resId
     *            resource id of item1 primary text content.
     */
    // primary text
    protected void setItem1PrimaryText(int resId) {
        String text = (resId == 0) ? null : getString(resId);
        setItemText(MODE_ITEM1_PRIMARY_TEXT, text);
    }

    /**
     * Set item2 primary text content
     *
     * @param resId
     *            resource id of item2 primary text content.
     */
    protected void setItem2PrimaryText(int resId) {
        String text = (resId == 0) ? null : getString(resId);
        setItemText(MODE_ITEM2_PRIMARY_TEXT, text);
    }

    /**
     * Set item1 primary text content
     *
     * @param charSequence
     *            primary text content of item1
     */
    protected void setItem1PrimaryText(CharSequence charSequence) {
        setItemText(MODE_ITEM1_PRIMARY_TEXT, charSequence);
    }

    /**
     * Set item2 primary text content
     *
     * @param charSequence
     *            primary text content of item2
     */
    protected void setItem2PrimaryText(CharSequence charSequence) {
        setItemText(MODE_ITEM2_PRIMARY_TEXT, charSequence);
    }

    /**
     * Set item1 secondary text content
     *
     * @param resId
     *            resource id of item1 secondary text content.
     */
    // secondary text
    protected void setItem1SecondaryText(int resId) {
        String text = (resId == 0) ? null : getString(resId);
        setItemText(MODE_ITEM1_SECONDARY_TEXT, text);
    }

    /**
     * Set item2 secondary text content
     *
     * @param resId
     *            resource id of item2 secondary text content.
     */
    protected void setItem2SecondaryText(int resId) {
        String text = (resId == 0) ? null : getString(resId);
        setItemText(MODE_ITEM2_SECONDARY_TEXT, text);
    }

    /**
     * Set item1 secondary text content
     *
     * @param charSequence
     *            secondary text content of item1
     */
    protected void setItem1SecondaryText(CharSequence charSequence) {
        setItemText(MODE_ITEM1_SECONDARY_TEXT, charSequence);
    }

    /**
     * Set item2 secondary text content
     *
     * @param charSequence
     *            secondary text content of item2
     */
    protected void setItem2SecondaryText(CharSequence charSequence) {
        setItemText(MODE_ITEM2_SECONDARY_TEXT, charSequence);
    }

    private float getScaleRatio(int resid) {
        Drawable drawable = getResources().getDrawable(resid);
        return getScaleRatio(drawable);
    }

    private float getScaleRatio(Drawable drawable) {
        Display display = ((WindowManager) getSystemService(android.content.Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        float ratio = (float) point.x / drawable.getIntrinsicWidth();
        return ratio;
    }

    /**
     * set image of ImageBody which under subTitle and upper two ListItem
     *
     * @param id
     *            resource id of image content
     */
    protected void setImage(int id) {
        mImageDrawable = null;
        mImageResId = id;
        if (mImageLayout == null || mImage == null) {
            return;
        }
        boolean isImageSet = (id != 0);
        mImageLayout.setVisibility(isImageSet ? View.VISIBLE : View.GONE);
        if (mDivider != null) {
            mDivider.setVisibility(isImageSet ? View.VISIBLE : View.GONE);
        }
        if (!WindowUtil.isSuitableForLandscape(getResources())) {
            LayoutParams params = (LayoutParams) mImage.getLayoutParams();
            params.height = (int) (getResources().getDrawable(id)
                    .getIntrinsicHeight() * getScaleRatio(id));
            mImage.setLayoutParams(params);
        }
        mImage.setImageResource(id);
    }

    /**
     * set image of ImageBody which under subTitle and upper two ListItem
     *
     * @param drawable
     *            drawable of image content
     */
    protected void setImage(Drawable drawable) {
        mImageResId = 0;
        mImageDrawable = drawable;
        if (mImageLayout == null || mImage == null) {
            return;
        }
        boolean isImageSet = (drawable != null);
        mImageLayout.setVisibility(isImageSet ? View.VISIBLE : View.GONE);
        if (mDivider != null) {
            mDivider.setVisibility(isImageSet ? View.VISIBLE : View.GONE);
        }
        if (!WindowUtil.isSuitableForLandscape(getResources())) {
            LayoutParams params = (LayoutParams) mImage.getLayoutParams();
            params.height = (int) (drawable.getIntrinsicHeight() * getScaleRatio(drawable));
            mImage.setLayoutParams(params);
        }
        mImage.setImageDrawable(drawable);
    }

    /**
     * set description of TextBody
     *
     * @param id
     *            resource id of TextBody description content
     */
    protected void setDescriptionText(int id) {
        mDescTextString = null;
        mDescTextResId = id;
        if (mText == null) {
            return;
        }

        boolean isTextSet = (id != 0);
        mText.setVisibility(isTextSet ? View.VISIBLE : View.GONE);
        mText.setText(id);
    }

    /**
     * set description of TextBody
     *
     * @param str
     *            TextBody description content
     */
    protected void setDescriptionText(CharSequence str) {
        mDescTextResId = 0;
        mDescTextString = str;
        if (mText == null) {
            return;
        }

        boolean isTextSet = (str != null);
        mText.setVisibility(isTextSet ? View.VISIBLE : View.GONE);
        mText.setText(str);
    }

    /**
     * set minor description font style
     *
     * @param isMinorStyle
     *            decide set description as minor style
     */
    public void setMinorDescriptionStyle(boolean isMinorStyle) {
        mMinorFontStyle = isMinorStyle;
        if (mText == null) {
            return;
        }
        mText.setVisibility(View.VISIBLE);
        mText.setTextAppearance(this,
                com.htc.lib1.cc.R.style.list_body_primary_xs);
    }

    /**
     * set adapter for HtcListView allow user change HtcListem contain
     *
     * @param adapter
     *            The ListAdapter which is responsible for maintaining the data
     *            backing this list and for producing a view to represent an
     *            item in that data set.
     */
    protected void setListViewAdapter(BaseAdapter adapter) {
        mAdapter = adapter;
        if (mListView != null) {
            mListView.removeAllViews();
            mListView.setAdapter(adapter);
        }
    }

    /**
     * set item listener of two HtcListItem
     *
     * @param listener
     *            the item listener of inner Listitem
     */
    protected void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    /**
     * set custom View/ViewGroup under TextBody
     *
     * @param view
     *            custom view
     */
    protected void addCustomBottomView(View view) {
        if (mCustomView != null && mCustomView.getParent() != null) {
            ((ViewGroup) mCustomView.getParent()).removeView(mCustomView);
        }
        mCustomView = view;
        if (mContentLayout != null) {
            mContentLayout.addView(view);
        }
    }
}

class HtcItemContainerListView extends HtcListView {
    public HtcItemContainerListView(Context context) {
        super(context);
    }

    public HtcItemContainerListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HtcItemContainerListView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
