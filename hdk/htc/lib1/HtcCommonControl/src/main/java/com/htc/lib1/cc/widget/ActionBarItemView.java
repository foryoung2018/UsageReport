
package com.htc.lib1.cc.widget;

import android.view.Gravity;
import android.view.View;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.graphics.drawable.Drawable;
import android.content.res.Configuration;

import com.htc.lib1.cc.util.ActionBarUtil;
import com.htc.lib1.cc.util.CheckUtil;
import com.htc.lib1.cc.util.res.HtcResUtil;

import com.htc.lib1.cc.R;

/**
 * A widget can be used in Htc action bar.
 */
public class ActionBarItemView extends LinearLayout implements View.OnLongClickListener {
    /**
     * EXTERNAL mode.
     */
    public static final int MODE_EXTERNAL = 1;

    /**
     * AUTOMOTIVE mode.
     */
    public static final int MODE_AUTOMOTIVE = 2;

    /**
     * Bits of support mode, link with Sense7 UIGL1.0 Page107. Use with {@link #setSupportMode(int)}
     * .
     * <p>
     * Sample:
     *
     * <pre class="prettyprint">
     * ActionBarItemView.setSupportMode(ActionBarItemView.MODE_EXTERNAL | ActionBarItemView.FLAG_M2_IMG_M2);
     * </pre>
     *
     * </p>
     */
    public static final int FLAG_M2_IMG_M2 = 0x80000000;

    private ImageButton mImageButton = null;

    CharSequence mTitle;
    private OnLongClickListener mOnLongClickListener;

    @ExportedProperty(category = "CommonControl")
    private int mItemWidth = 0;
    @ExportedProperty(category = "CommonControl")
    private int mItemHeight = 0;
    @ExportedProperty(category = "CommonControl")
    private int mMeasureSpecM2;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can access the current
     *            theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public ActionBarItemView(Context context) {
        super(context);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        mMeasureSpecM2 = HtcResUtil.getM2(context);

        setClickable(true);
        super.setOnLongClickListener(this);
        setFocusable(true);
        setGravity(Gravity.CENTER);
        setOrientation(LinearLayout.VERTICAL);

        setupLayoutParameters();

        // inflate external layout and merge to current layout
        LayoutInflater.from(context).inflate(R.layout.action_itemview, this, true);

        mImageButton = (ImageButton) findViewById(R.id.imageButton);
        // check the layout resource correctness
        if (mImageButton == null) throw new RuntimeException("inflate layout resource incorrect");

        mImageButton.setFocusable(false);

        setBackground(ActionBarUtil.getActionMenuItemBackground(context));
    }

    /**
     * Get action bar item icon.
     *
     * @return the icon drawable of this item
     * @deprecated [Not use any longer]
     * @hide
     */
    @Deprecated
    public Drawable getIcon() {
        return mImageButton == null ? null : mImageButton.getDrawable();
    }

    /**
     * Set action bar item icon.
     *
     * @param drawable the drawable you want to show
     */
    public void setIcon(Drawable drawable) {
        if (mImageButton != null) mImageButton.setImageDrawable(drawable);
    }

    /**
     * Set action bar item icon.
     *
     * @param bitmap the bitmap you want to show
     */
    public void setIcon(Bitmap bitmap) {
        if (mImageButton != null) mImageButton.setImageBitmap(bitmap);
    }

    /**
     * Set action bar item icon.
     *
     * @param resid the drawable resource id you want to show
     */
    public void setIcon(int resid) {
        if (mImageButton != null) mImageButton.setImageResource(resid);
    }

    private static final float DISABLE_ALPHA = 0.4f;
    private static final float ENABLE_ALPHA = 1.0f;

    @Override
    public void setEnabled(boolean enable) {
        super.setEnabled(enable);
        if (mImageButton != null) {
            mImageButton.setEnabled(enable);
            mImageButton.setAlpha(enable ? ENABLE_ALPHA : DISABLE_ALPHA);
        }
    }

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = MODE_EXTERNAL, to = "MODE_EXTERNAL"),
            @IntToString(from = MODE_AUTOMOTIVE, to = "MODE_AUTOMOTIVE"),
            @IntToString(from = MODE_EXTERNAL | FLAG_M2_IMG_M2, to = "MODE_EXTERNAL | FLAG_M2_IMG_M2"),
            @IntToString(from = MODE_AUTOMOTIVE | FLAG_M2_IMG_M2, to = "MODE_AUTOMOTIVE|FLAG_M2_IMG_M2")
    })
    private int mSupportMode = MODE_EXTERNAL;

    /**
     * support special usage for automotive mode only.
     *
     * @param mode support mode
     */
    public void setSupportMode(int mode) {
        // skip to avoid useless operation
        if (mSupportMode == mode) return;

        mSupportMode = mode;

        if (getInternalMode() == MODE_AUTOMOTIVE) {
            setupAutomotiveMode();
        }
        if (getInternalFlag() == FLAG_M2_IMG_M2) {
            setPadding(mMeasureSpecM2, 0, mMeasureSpecM2, 0);
        } else {
            setPadding(0, 0, 0, 0);
        }
    }

    // special support for automotive usage
    private void setupAutomotiveMode() {
        setupLayoutParameters();
        requestLayout();
    }

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = Configuration.ORIENTATION_UNDEFINED, to = "ORIENTATION_UNDEFINED"),
            @IntToString(from = Configuration.ORIENTATION_PORTRAIT, to = "ORIENTATION_PORTRAIT"),
            @IntToString(from = Configuration.ORIENTATION_LANDSCAPE, to = "ORIENTATION_LANDSCAPE")
    })
    private int mScreenHeightDp;
    private int mScreenWidthtDp;

    /**
     * [Module internal use].
     *
     * @deprecated
     * @hide
     */
    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if ((mScreenHeightDp == newConfig.screenHeightDp) && (mScreenWidthtDp == newConfig.screenWidthDp)) return;
        mScreenHeightDp = newConfig.screenHeightDp;
        mScreenWidthtDp = newConfig.screenWidthDp;
        setupLayoutParameters();
        requestLayout();
    }

    /**
     * @hide
     */
    @Override
    public boolean onLongClick(View v) {
        if (null != mTitle) {
            final int[] screenPos = new int[2];
            final Rect displayFrame = new Rect();
            getLocationInWindow(screenPos);
            getWindowVisibleDisplayFrame(displayFrame);

            final Context context = getContext();
            final int width = getWidth();
            final int height = getHeight();
            final int midy = screenPos[1] + height / 2;
            final int screenWidth = context.getResources().getDisplayMetrics().widthPixels;

            Toast cheatSheet = Toast.makeText(context, mTitle, Toast.LENGTH_SHORT);
            if (midy < displayFrame.height()) {
                // Show along the top; follow action buttons
                cheatSheet.setGravity(Gravity.TOP | Gravity.END,
                        screenWidth - screenPos[0] - width / 2, height);
            } else {
                // Show along the bottom center
                cheatSheet.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, height);
            }
            cheatSheet.show();

        }

        if (null != mOnLongClickListener) mOnLongClickListener.onLongClick(v);

        return true;
    }

    /**
     * set title for LongClick Toast.
     */
    public void setTitle(CharSequence title) {
        mTitle = title;
    }

    /**
     * @hide
     */
    @Override
    public void setOnLongClickListener(OnLongClickListener listener) {
        mOnLongClickListener = listener;
    }

    /**
     * (non-Javadoc).
     *
     * @see android.widget.LinearLayout#onMeasure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getInternalFlag() == FLAG_M2_IMG_M2) {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(mItemHeight, MeasureSpec.EXACTLY));
        } else {
            super.onMeasure(MeasureSpec.makeMeasureSpec(mItemWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mItemHeight, MeasureSpec.EXACTLY));
        }
    }

    private void setupLayoutParameters() {
        mItemWidth = ActionBarUtil.getItemWidth(getContext(), (getInternalMode() == MODE_AUTOMOTIVE));
        mItemHeight = ActionBarUtil.getActionBarHeight(getContext(), (getInternalMode() == MODE_AUTOMOTIVE));
    }

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = MODE_EXTERNAL, to = "MODE_EXTERNAL"),
            @IntToString(from = MODE_AUTOMOTIVE, to = "MODE_AUTOMOTIVE")
    })
    private int getInternalMode() {
        return mSupportMode & ~FLAG_M2_IMG_M2;
    }

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = FLAG_M2_IMG_M2, to = "FLAG_M2_IMG_M2")
    })
    private int getInternalFlag() {
        return mSupportMode & FLAG_M2_IMG_M2;
    }
}
