
package com.htc.lib1.cs.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Extension of {@link ScrollView} which notifies a listener on scrolling.
 * 
 * @author samael_wang@htc.com
 */
public class ScrollViewExt extends ScrollView {
    /**
     * Listener for scroll change event.
     * 
     * @author samael_wang@htc.com
     */
    public interface OnScrollChangedListener {
        /**
         * Invoked when the scroll view has been scrolled.
         * 
         * @param view The scroll view instance.
         * @param scrollRange The scroll range of a scroll view is the overall
         *            height of all of its children.
         * @param l Current horizontal scroll origin.
         * @param t Current vertical scroll origin.
         * @param oldl Previous horizontal scroll origin.
         * @param oldt Previous vertical scroll origin.
         */
        public void onScrollChanged(ScrollView view, int scrollRange, int l, int t, int oldl,
                int oldt);
    }

    /**
     * Listener for size change event.
     * 
     * @author samael_wang@htc.com
     */
    public interface OnSizeChangedListener {
        /**
         * Invoked when the scroll view size has been changed.
         * 
         * @param view The scroll view instance.
         * @param w Current width of this view.
         * @param h Current height of this view.
         * @param oldw Old width of this view.
         * @param oldh Old height of this view.
         */
        public void onSizeChanged(ScrollView view, int w, int h, int oldw, int oldh);
    }

    private OnScrollChangedListener mScrollListener;
    private OnSizeChangedListener mSizeListener;

    public ScrollViewExt(Context context) {
        super(context);
    }

    public ScrollViewExt(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollViewExt(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Set the listener for scroll change.
     * 
     * @param listener
     */
    public synchronized void setOnScrollChangedListener(OnScrollChangedListener listener) {
        mScrollListener = listener;
    }

    /**
     * Set the listener for size change.
     * 
     * @param listener
     */
    public synchronized void setOnSizeChangedListener(OnSizeChangedListener listener) {
        mSizeListener = listener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        synchronized (this) {
            if (mScrollListener != null)
                mScrollListener.onScrollChanged(this, computeVerticalScrollRange(), l, t, oldl,
                        oldt);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        synchronized (this) {
            if (mSizeListener != null)
                mSizeListener.onSizeChanged(this, w, h, oldw, oldh);
        }
    }

}
