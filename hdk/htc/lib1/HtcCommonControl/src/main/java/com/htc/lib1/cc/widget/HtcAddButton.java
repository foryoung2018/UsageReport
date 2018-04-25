package com.htc.lib1.cc.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.graphics.drawable.Drawable;

import com.htc.lib1.cc.R;

/**
 * @deprecated [Not use any longer] These button does not exist on S50
 */
/**@hide*/
public class HtcAddButton extends HtcCompoundButton {
     public HtcAddButton(Context context) {
         this(context, null);
     }

     public HtcAddButton(Context context, int backgroundMode) {
         super(context, backgroundMode, true, false);
         init(context, null, 0);
     }

     public HtcAddButton(Context context, AttributeSet attrs) {
         this(context, attrs, 0);
     }

     public HtcAddButton(Context context, AttributeSet attrs, int defStyle) {
         super(context, attrs, defStyle);
         init(context, attrs, defStyle);
     }

     private void init(Context context, AttributeSet attrs, int defStyle){
            mIsContentMultiplyRequired = true;
            mHasOnState = false;
            setButtonDrawables(context, attrs, defStyle);
     }

        //Add by Ahan for skin change on S4+
    public void setButtonDrawables(Context context, AttributeSet attrs, int defStyle) {
            Drawable bkgOuter = null, bkgPressed = null, bkgRest = null, fgOn = null, fgRest = null;

            bkgPressed = HtcButtonUtil.getButtonDrawable(context, attrs, defStyle, HtcButtonUtil.BTNASSET_COMMON_COLLECT_REST);
            fgOn = HtcButtonUtil.getButtonDrawable(context, attrs, defStyle, HtcButtonUtil.BTNASSET_COMMON_COLLECT_REST);
            fgRest = HtcButtonUtil.getButtonDrawable(context, attrs, defStyle, HtcButtonUtil.BTNASSET_COMMON_COLLECT_REST);

            super.setButtonDrawables(bkgOuter, bkgPressed, bkgRest, fgRest, fgOn);
    }
}
