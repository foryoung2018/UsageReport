package com.htc.lib1.cc.widget;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.graphic.MosaicsDrawable;
import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.widget.ColorPickerDialog.Builder;

/**
 * @hide
 * @deprecated try level not release
 */
public class ColorPickerAdapter extends BaseAdapter {
    private final static String LOG_TAG = "ColorPickerAdapter";
    private ArrayList<Integer> mThumbIdArrayList = new ArrayList<Integer>();
    private int mSelectedItemPosition = -1;
    private int mThemeColor;
    private Drawable mMosaicsDrawable;
    private Drawable mPressed;
    private Drawable mSelected;

    public ColorPickerAdapter(Context context, int[] arrayColors, int numColumns) {
        if (null == context) {
            Log.e(LOG_TAG, "context = null", new Exception());
            return;
        }

        final Resources res = context.getResources();
        if (null == res) {
            Log.e(LOG_TAG, "context.getResources = null", new Exception());
            return;
        }

        if (checkArrayColorsIllegal(arrayColors)) {
            return;
        }

        if (checkNumColumnsIllegal(numColumns)) {
            return;
        }

        mThemeColor = HtcCommonUtil.getCommonThemeColor(context, R.styleable.ThemeColor_overlay_color);
        mMosaicsDrawable = new MosaicsDrawable();
        mPressed = res.getDrawable(R.drawable.pressed_frame);
        mPressed.setColorFilter(mThemeColor, PorterDuff.Mode.SRC_ATOP);
        mSelected = res.getDrawable(R.drawable.colorpicker_checked);

        final int size = arrayColors.length;
        final int remainder = size % numColumns;
        for (int i = 0; i < size; i++) {
            mThumbIdArrayList.add(i, arrayColors[i % size]);
        }
        final Random random = new Random();
        if (0 != remainder) {
            Log.e(LOG_TAG, "Current remainder is " + remainder
                    + " ,Can not divisible,will random select "
                    + (numColumns - remainder)
                    + ", In order to achieve alignment effect");
            for (int i = 0; i < numColumns - remainder; i++) {
                mThumbIdArrayList.add(size - i,
                        arrayColors[random.nextInt(size)]);
            }
        }
    }

    public void setSelectedItemPosition(int selectedItemPosition) {
        this.mSelectedItemPosition = selectedItemPosition;
    }

    @Override
    public int getCount() {
        return mThumbIdArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mThumbIdArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final HtcOverlayGridItem item;
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            holder.mSelectorDrawable = getStateListDrawable(mPressed, mSelected);
            holder.mImageColor = new ColorDrawable();
            item = new HtcOverlayGridItem(parent.getContext());
            item.setTag(holder);
        } else {
            item = (HtcOverlayGridItem) convertView;
            holder = (ViewHolder) item.getTag();
        }

        final HtcGridItemOverlayImage itemOverlayImage;
        if (null != item.getImage()) {
            itemOverlayImage = (HtcGridItemOverlayImage) item.getImage();
        } else {
            Log.e(LOG_TAG, "item.getImage() = null", new Exception());
            return item;
        }

        holder.mImageColor.setColor(mThumbIdArrayList.get(position));
        itemOverlayImage.setImageDrawable(holder.mSelectorDrawable);
        itemOverlayImage
                .setBackground(holder.mImageColor.getAlpha() == 0 ? mMosaicsDrawable
                        : holder.mImageColor);
        itemOverlayImage.setSelected(position == mSelectedItemPosition);
        itemOverlayImage.setDrawOverlay(false);
        return item;
    }

    class ViewHolder {
        Drawable mSelectorDrawable;
        ColorDrawable mImageColor;
    }

    /**
     * Set the number of columns in the HtcGridView
     *
     * @param numColumns
     *            The desired number of columns.
     * @return whether the number of columns legal
     */
    public static boolean checkNumColumnsIllegal(int numColumns) {
        if (numColumns <= 0) {
            Log.e(LOG_TAG, "Current numColumns is " + numColumns
                    + " Min numColumns is 1. Use the default value of 3 to set the number of columns.", new Exception());
            return true;
        }
        if (numColumns >= 7) {
            Log.e(LOG_TAG, "Current numColumns is " + numColumns
                    + " Max numColumns is 6. Use the default value of 3 to set the number of columns.", new Exception());
            return true;
        }
        return false;
    }

    /**
     * Set the arrayColors for ColorPickerAdapter in the HtcGridView
     *
     * @param arrayColors The desired arrayColors.
     * @return whether the arrayColors legal
     */
    public static boolean checkArrayColorsIllegal(int[] arrayColors) {
        if (null == arrayColors || 0 == arrayColors.length) {
            Log.e(LOG_TAG, "Current arrayColors is null or arrayColors.length = 0 ,Please setColorArray. Use the default value of arrayColors.", new Exception());
            return true;
        }
        return false;
    }

    private Drawable getStateListDrawable(Drawable pressed, Drawable selected) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[] {
                android.R.attr.state_pressed
        }, pressed);
        stateListDrawable.addState(new int[] {
                android.R.attr.state_selected
        }, selected);
        return stateListDrawable;
    }
}
