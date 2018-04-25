package com.htc.lib1.cc.widget.recipientblock;

import com.htc.lib1.cc.R;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.util.Log;

/*@hide*/
public class ResUtils {
    private static int sMarginXSInPixel = Integer.MIN_VALUE;
    private static int sMarginSInPixel = Integer.MIN_VALUE;
    private static int sMarginMInPixel = Integer.MIN_VALUE;
    private static int sMarginLInPixel = Integer.MIN_VALUE;
    private static int sMarginSpacingInPixel = Integer.MIN_VALUE;
    private static int sHtcFooterBarLandWidth = Integer.MIN_VALUE;
    private static int sHtcFooterBarHeight = Integer.MIN_VALUE;
    private static int sDevicePortraitWidth = Integer.MIN_VALUE;
    private static int sListItemHeight  = Integer.MIN_VALUE;
    private static int sCompoundDrawablePadding = Integer.MIN_VALUE;
    /**
    * +--------------------------------------------------------+
    * |            ↑ M2 ↑                               |      |
    * |      +-----------------+      +-----------------+      |
    * | ←M1→ | ( aaa@htc.com ) | ←M2→ | ( aaa@htc.com ) | ←M1→ | <----mRecipientContainerRightPadding = M1
    * |------+-----------------+------+-----------------+------|
    * |        ↓ mRecipientContainerRightPadding = M1↓  |      |
    * +--------------------------------------------------------+
    */
    private static int sRecipientContainerPadding = Integer.MIN_VALUE;

    protected static int getListItemHeight(Context context) {
        if (sListItemHeight <= 0) {
            sListItemHeight = (Integer) com.htc.lib1.cc.widget.HtcProperty.getProperty(context, "HtcListItemHeight");
        }
        return sListItemHeight;
    }

    protected static int getCompoundDrawablePadding(Context context) {//M4
        if (sCompoundDrawablePadding <= 0) {
            sCompoundDrawablePadding = context.getResources().getDimensionPixelSize(R.dimen.margin_xs);
        }
        return sCompoundDrawablePadding;
    }

    protected static int getRecipientContainerPadding(Context context) {//M1
        if (sRecipientContainerPadding <= 0) {
            sRecipientContainerPadding = context.getResources().getDimensionPixelSize(R.dimen.margin_l);
        }
        return sRecipientContainerPadding;
    }

    protected static int getDimenMarginM1(Context context) {
        if (sMarginLInPixel <= 0) {
            sMarginLInPixel = context.getResources().getDimensionPixelSize(R.dimen.margin_l);
        }
        return sMarginLInPixel;
    }

    protected static int getDimenMarginM2(Context context) {
        if (sMarginMInPixel <= 0) {
            sMarginMInPixel = context.getResources().getDimensionPixelSize(R.dimen.margin_m);
        }
        return sMarginMInPixel;
    }

    protected static int getDimenMarginM3(Context context) {
        if (sMarginSInPixel <= 0) {
            sMarginSInPixel = context.getResources().getDimensionPixelSize(R.dimen.margin_s);
        }
        return sMarginSInPixel;
    }

    protected static int getDimenMarginM4(Context context) {
        if (sMarginXSInPixel <= 0) {
            sMarginXSInPixel = context.getResources().getDimensionPixelSize(R.dimen.margin_xs);
        }
        return sMarginXSInPixel;
    }

    protected static int getDimenMarginM5(Context context) {
        if (sMarginSpacingInPixel <= 0) {
             sMarginSpacingInPixel = context.getResources().getDimensionPixelSize(R.dimen.spacing);
        }
        return sMarginSpacingInPixel;
    }

    protected static int getDimenHtcFooterBarHeight(Context context) {
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (sHtcFooterBarLandWidth <= 0) {
                sHtcFooterBarLandWidth = context.getResources().getDimensionPixelSize(R.dimen.htc_footer_width);
            }
            return sHtcFooterBarLandWidth;
        } else {
            if (sHtcFooterBarHeight <= 0) {
                sHtcFooterBarHeight = context.getResources().getDimensionPixelSize(R.dimen.htc_footer_height);
            }
            return sHtcFooterBarHeight;
        }
    }

    protected static int getInputFieldActionButtonWidth(Context context) {
        return (int) (getDevicePortraitWidth(context) * 0.147); // define in Sense 5 UIGL v1.8 p32.
    }

    protected static int getDevicePortraitWidth(Context context) {
        if (sDevicePortraitWidth <= 0) {
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) { // landscape
                sDevicePortraitWidth = context.getResources().getDisplayMetrics().heightPixels;
            } else { // portrait
                sDevicePortraitWidth = context.getResources().getDisplayMetrics().widthPixels;
            }
        }
        return sDevicePortraitWidth;
    }
}
