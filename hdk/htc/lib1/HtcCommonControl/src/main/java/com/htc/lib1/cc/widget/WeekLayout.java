
package com.htc.lib1.cc.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewDebug;
import android.view.ViewDebug.ExportedProperty;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htc.lib1.cc.R;

/**
 * @hide
 * @deprecated try level not release
 */
public class WeekLayout extends RelativeLayout {
    private final static String TAG = "WeekLayout";

    private final static int MODE_LIGHT = 0;
    private final static int MODE_DARK = 1;

    private final static int SECONDARY_TEXT_LENGTH = 7;

    private final static boolean SUPPORT_RTL = Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN;

    @ViewDebug.ExportedProperty(category = "CommonControl", mapping = {
            @ViewDebug.IntToString(from = MODE_LIGHT, to = "MODE_LIGHT"),
            @ViewDebug.IntToString(from = MODE_DARK, to = "MODE_DARK")
    })
    private int mMode = -1;
    @ExportedProperty(category = "CommonControl", resolveId = true)
    private int[] mSecondaryTextViewIds;

    public WeekLayout(Context context) {
        this(context, null);
    }

    public WeekLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public WeekLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(com.htc.lib1.cc.R.layout.weeklayout, this, true);
        mSecondaryTextViewIds = new int[] {
                R.id.secondary1, R.id.secondary2, R.id.secondary3, R.id.secondary4, R.id.secondary5, R.id.secondary6, R.id.secondary7
        };
        setGravity(Gravity.CENTER_VERTICAL);
        setSecondaryText(getResources().getStringArray(R.array.weeklayout_secondary_text));
        setMode(MODE_LIGHT);
    }

    /**
     * Set style to all secondary text.
     *
     * @param style The font style.
     */
    public void setSecondaryTextStyle(int style) {
        for (int i = 0; i < SECONDARY_TEXT_LENGTH; i++) {
            final TextView tv = (TextView) findViewById(mSecondaryTextViewIds[i]);
            if (checkTextViewNotNull(tv)) {
                tv.setTextAppearance(getContext(), style);
            }
        }
    }

    /**
     * Sets all secondary TextView's end margin except the last one.
     *
     * @param margin The end margin size
     */
    public void setSecondaryTextEndMargin(int margin) {
        for (int i = 0; i < SECONDARY_TEXT_LENGTH - 1; i++) {
            final TextView tv = (TextView) findViewById(mSecondaryTextViewIds[i]);
            if (!checkTextViewNotNull(tv)) {
                return;
            }

            final RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tv.getLayoutParams();
            if (null == lp) {
                Log.e(TAG, "TextView's LayoutParams is null");
                return;
            }

            if (SUPPORT_RTL) {
                lp.setMarginEnd(margin);
            } else {
                lp.rightMargin = margin;
            }
        }
    }

    /**
     * Sets the string value of all Secondary TextView.
     *
     * @param text The String array.
     */
    public void setSecondaryText(String[] text) {
        if (null == text || text.length != SECONDARY_TEXT_LENGTH) {
            return;
        }

        for (int i = 0; i < SECONDARY_TEXT_LENGTH; i++) {
            final TextView tv = (TextView) findViewById(mSecondaryTextViewIds[i]);
            if (checkTextViewNotNull(tv)) {
                tv.setText(text[i]);
            }
        }
    }

    private void setMode(int mode) {
        if (mode < MODE_LIGHT || mode > MODE_DARK) {
            Log.e(TAG, "wrong mode");
            return;
        }

        if (mMode == mode) {
            return;
        }

        mMode = mode;
        setDefaultTextStyle(mode);
        setSecondaryTextEndMargin(getResources().getDimensionPixelOffset(R.dimen.margin_m));
    }

    private void setDefaultTextStyle(int mode) {
        if (mode == MODE_DARK) {
            setTextStyle(R.style.fixed_darklist_primary_m, R.style.fixed_darklist_secondary_xxs);
        } else {
            setTextStyle(R.style.fixed_list_primary_m, R.style.fixed_list_secondary_xxs);
        }
    }

    private void setTextStyle(int primaryStyle, int secondaryStyle) {
        final TextView tv = (TextView) findViewById(android.R.id.primary);
        if (checkTextViewNotNull(tv)) {
            tv.setTextAppearance(getContext(), primaryStyle);
        }

        setSecondaryTextStyle(secondaryStyle);
    }

    private boolean checkTextViewNotNull(TextView tv) {
        if (null == tv) {
            Log.e(TAG, "TextView is null ");
            return false;
        } else {
            return true;
        }
    }
}
