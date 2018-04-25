package com.htc.lib1.cc.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.TextView;

import com.htc.lib1.cc.R;

/**
 * Created by henry on 10/3/14.
 *
 * for HtcShareActivity
 * to adjust selector's bound
 *
 * do NOT use this except HtcShareActivity
 * @hide
 */
public class HtcShareTextView extends TextView implements AbsListView.SelectionBoundsAdjuster {

    public HtcShareTextView(Context context) {
        super(context);
    }

    public HtcShareTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HtcShareTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * adjust top bound for grid item's top padding
     *
     * @param rect
     */
    @Override
    public void adjustListItemSelectionBounds(Rect rect) {
        rect.top += getResources().getDimensionPixelSize(R.dimen.paddingTop_shareGridItem) / 2;
    }
}
