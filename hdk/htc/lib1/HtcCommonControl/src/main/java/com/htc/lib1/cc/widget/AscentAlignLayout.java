
package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewDebug.ExportedProperty;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @hide
 * @deprecated try level not release
 */
public class AscentAlignLayout extends LinearLayout {
    private final static String TAG = "AscentAlignLayout";
    private final static String BASIC_LETTER = "A";
    private TextAscentCallBack mTextAscentCallBack = null;

    public AscentAlignLayout(Context context) {
        this(context, null);
    }

    public AscentAlignLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public AscentAlignLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Sets TextAscentCallBack
     *
     * @param callBack the TextAscentCallBack
     */
    public void setTextAscentCallBack(TextAscentCallBack callBack) {
        mTextAscentCallBack = callBack;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        final int count = getChildCount();
        int basicViewAscent = -1;

        // get basic TextView's ascent
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child == null || child.getVisibility() == View.GONE) {
                continue;
            }

            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp.mAlignType == LayoutParams.BASIC) {
                basicViewAscent = getAscent(child, lp.mAlignViewId);
                break;
            }
        }

        if (basicViewAscent == -1) {
            return;
        }

        // get align_basic TextView's ascent and layout its parent
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child == null || child.getVisibility() == View.GONE) {
                continue;
            }

            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp.mAlignType == LayoutParams.ALIGN_BASIC) {
                int alignBasicViewAscent = getAscent(child, lp.mAlignViewId);
                if (alignBasicViewAscent == -1) {
                    continue;
                }

                final int deltaY = basicViewAscent - alignBasicViewAscent;
                if (deltaY != 0) {
                    child.layout(child.getLeft(), child.getTop() + deltaY, child.getRight(), child.getBottom() + deltaY);
                }
            }
        }
    }

    private Rect mBounds = new Rect();

    private int getTextAscent(TextView tv) {
        if (null == tv) {
            return -1;
        }

        if (null == mBounds) {
            mBounds = new Rect();
        }

        tv.getLayout().getPaint().getTextBounds(BASIC_LETTER, 0, 1, mBounds);
        return mBounds.height();
    }

    private int getAscent(View child,int alignViewId) {
        final View v = child.findViewById(alignViewId);
        if (null == v || !(v instanceof TextView)) {
            Log.e(TAG, "not TextView or invalid id", new Exception());
            return -1;
        } else {
            final TextView alignView = (TextView) v;
            final int textAscent = mTextAscentCallBack == null ? getTextAscent(alignView) : mTextAscentCallBack.getTextAscent(alignView);
            return getLocationYOnMyself(alignView) + alignView.getBaseline() - textAscent;
        }
    }

    private int getLocationYOnMyself(View v) {
        int locationY = v.getTop();
        ViewParent viewParent = v.getParent();
        while (viewParent != this) {
            final View view = (View) viewParent;
            locationY += view.getTop();
            viewParent = view.getParent();
        }
        return locationY;
    }

    @Override
    public android.widget.LinearLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected android.widget.LinearLayout.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public static class LayoutParams extends LinearLayout.LayoutParams {
        /**
         * View with this type will be basic view
         */
        public static final int BASIC = 0;

        /**
         * View with this type will ascent align with the basic view
         */
        public static final int ALIGN_BASIC = 1;

        @ExportedProperty(category = "CommonControl", resolveId = true)
        private int mAlignViewId = -1;

        @ViewDebug.ExportedProperty(category = "CommonControl", mapping = {
                @ViewDebug.IntToString(from = BASIC, to = "BASIC"),
                @ViewDebug.IntToString(from = ALIGN_BASIC, to = "ALIGN_BASIC")
        })
        private int mAlignType = -1;

        /**
         * {@inheritDoc}
         *
         * @hide
         */
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            TypedArray a = c.obtainStyledAttributes(attrs, com.htc.lib1.cc.R.styleable.AscentAlignLayout_Layout);
            mAlignViewId = a.getResourceId(com.htc.lib1.cc.R.styleable.AscentAlignLayout_Layout_layout_alignView, -1);
            mAlignType = a.getInt(com.htc.lib1.cc.R.styleable.AscentAlignLayout_Layout_layout_alignType, -1);
            a.recycle();
        }

        /**
         * {@inheritDoc}
         *
         * @hide
         */
        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        /**
         * Set id of the view(TextView) which should be Basic view or Align Basic view.
         *
         * @param alignViewId The align view's id
         */
        public void setAlignViewId(int alignViewId) {
            mAlignViewId = alignViewId;
        }

        /**
         * Set Align Type.
         *
         * @param alignType alignType should be one of {@link #BASIC} ,{@link #ALIGN_BASIC}.
         */
        public void setAlignType(int alignType) {
            mAlignType = alignType;
        }

    }

    /**
     * A callback that let the user to return the ascent of the TextView. Don't do new in this
     * callback.
     */
    public interface TextAscentCallBack {
        public int getTextAscent(TextView tv);
    }
}
