package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Checkable;

import com.htc.lib1.cc.widget.HtcCheckBox;
import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcListItemSingleText;
import com.htc.lib1.cc.widget.HtcRadioButton;
import com.htc.lib1.cc.R;
/*
 * this class is used for HtcAlertDialog ListView only.
 *
 * to use this class, you should create an XML layout with this class
 * as the root element.
 *
 * and add TextView(s) or any HtcListItem controls as children.
 *
 * to support Checkable interface, add an HtcCheckBox or an HtcRadioButton
 * and set the id to "android.R.id.checkbox".
 *
 * @author henrycy_lee
 *
 * @hide
 */
public class CheckableHtcListItem extends HtcListItem implements Checkable {

    private /*final*/ HtcCheckBox mCheckBox;
    private /*final*/ HtcRadioButton mRadioButton;
    private /*final*/ int mMode;
    //private /*final*/ int mTextureResId;
    private static final int[] CHECKED_STATE_SET = {
        android.R.attr.state_checked
    };

    @SuppressWarnings("deprecation")
    public CheckableHtcListItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    @SuppressWarnings("deprecation")
    public CheckableHtcListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public CheckableHtcListItem(Context context) {
        super(context);
        init(context, null, 0);
    }

    @Override
    protected void onFinishInflate () {
        View compound = findViewById(android.R.id.checkbox);
        if (compound instanceof HtcCheckBox) {
            mCheckBox = (HtcCheckBox) compound;
            mCheckBox.setClickable(false);
            mCheckBox.setFocusable(false);
        } else if (compound instanceof HtcRadioButton) {
            mRadioButton = (HtcRadioButton) compound;
            mRadioButton.setClickable(false);
            mRadioButton.setFocusable(false);
        }
        if (null != compound) {
            setLastComponentAlign(true);
        }

        if (MODE_AUTOMOTIVE == mMode) {
            setupAutomotiveMode();
        } else {
            View text = findViewById(android.R.id.text1);
            if (text instanceof HtcListItemSingleText) {
                ((HtcListItemSingleText) text).setUseFontSizeInStyle(true);
                //((HtcListItemSingleText) text).setTextStyle(R.style.list_primary_m_bold);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HtcListItem, R.attr.htcListItemStyle, R.style.htcListItem);
        mMode = a.getInt(R.styleable.HtcListItem_itemMode, MODE_DEFAULT);
        //mTextureResId = a.getResourceId(R.styleable.HtcListItem_android_background, R.drawable.common_list_item_background);
        a.recycle();

        //for customize is deprecated
        if(mMode == MODE_CUSTOMIZED) mMode = MODE_DEFAULT;
    }

    private void setupAutomotiveMode() {
        // this is a work-around for that the list item does not support
        // automotive light mode

        // set list item background
        //setBackgroundResource(mTextureResId); Sense60 design change
        // set text style to FL12
        View text = findViewById(android.R.id.text1);
        if (text instanceof HtcListItemSingleText) {
            ((HtcListItemSingleText) text).setUseFontSizeInStyle(true);
            ((HtcListItemSingleText) text).setTextStyle(R.style.fixed_automotive_darklist_primary_m);
        }
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }

    @ViewDebug.ExportedProperty
    @Override
    public boolean isChecked() {
        if (null != mCheckBox) {
            return mCheckBox.isChecked();
        } else if (null != mRadioButton) {
            return mRadioButton.isChecked();
        }
        return false;
    }

    @Override
    public void setChecked(boolean checked) {
        boolean changed = false;
        if (null != mCheckBox) {
            changed = mCheckBox.isChecked() != checked;
            mCheckBox.setChecked(checked);
        } else if (null != mRadioButton) {
            changed = mRadioButton.isChecked() != checked;
            mRadioButton.setChecked(checked);
        }

        if (changed) {
            refreshDrawableState();
            //notifyAccessibilityStateChanged(); TODO check this
        }
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(CheckableHtcListItem.class.getName()); // TODO check if set to CheckedTextView?
        event.setChecked(isChecked());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(CheckableHtcListItem.class.getName());
        info.setCheckable(true);
        info.setChecked(isChecked());
    }
}
