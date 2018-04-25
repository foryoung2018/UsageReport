package com.htc.lib1.cc.widget.quicktips;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.HtcCommonUtil;

import static com.htc.lib1.cc.util.WindowUtil.getScreenHeightPx;
import static com.htc.lib1.cc.util.WindowUtil.getScreenWidthPx;

public class QuickTipPopup extends PopupBubbleWindow{

    private static final String TAG = "QuickTipPopup";

    private byte mScreenMode = -1;
    public static final byte SCREEN_MODE_PORTRAIT = 0;
    public static final byte SCREEN_MODE_LANDSCAPE = 1;
    public static final byte SCREEN_MODE_IPORTRAIT = 2;
    public static final byte SCREEN_MODE_ILANDSCAPE = 3;

    Context mContext;

    private ImageView mTipImage;
    private TextView mTipText;
    private ImageView mCloseIcon;
    private ImageView mCloseIconForTextOnly;
    private LinearLayout mVisible_panel;

    private RotateRelativeLayout mContentView;
    private RotateRelativeLayout mRotateRelativeLayout;
    private FrameLayout mImageSection;

    private static int mImageHeight = 0;
    private int mMaxWidth;

    public QuickTipPopup(Context context) {
        super(context);
        mContext = context;
        init(context);
    }

    private void init(Context context) {
        Resources res = context.getResources();
        mImageHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                (float) 109, res.getDisplayMetrics());

        // init limitation
        mMaxWidth = Math.min(getScreenWidthPx(res), getScreenHeightPx(res)) - 6 * M4;

        // init views
        LayoutInflater inflater = LayoutInflater.from(context);
        mContentView = (RotateRelativeLayout) inflater.inflate(R.layout.layout_quicktips, null);
        mRotateRelativeLayout = (RotateRelativeLayout)mContentView.findViewById(R.id.quicktip_frame);
        mImageSection = (FrameLayout)mContentView.findViewById(R.id.quicktip_image_section);
        mVisible_panel = (LinearLayout)mContentView.findViewById(R.id.visible_panel);

        mMultiplyColor = HtcCommonUtil.getCommonThemeColor(mContext,
                R.styleable.ThemeColor_multiply_color);
        initTriangle(mContext.getResources());
        initBackgroundColor();

        mTipText = (TextView) mContentView.findViewById(R.id.quicktip_text);
        mTipText.setTextAppearance(context, R.style.fixed_darklist_primary_m);
        mTipImage = (ImageView) mContentView.findViewById(R.id.quicktip_image);
        mCloseIconForTextOnly = (ImageView) mContentView.findViewById(R.id.quicktip_close_text);
        mCloseIconForTextOnly.setImageResource(R.drawable.icon_btn_cancel_dark_s);
        mCloseIcon = (ImageView) mContentView.findViewById(R.id.quicktip_close_image);
        mCloseIcon.setImageResource(R.drawable.icon_btn_cancel_dark_s);
        String closeString = res.getString(R.string.va_close);
        mCloseIconForTextOnly.setContentDescription(closeString);
        mCloseIcon.setContentDescription(closeString);

        mClearIconWidth = res.getDrawable(R.drawable.icon_btn_cancel_dark_s).getIntrinsicWidth();
        int mClearIconHeight =res.getDrawable(R.drawable.icon_btn_cancel_dark_s).getIntrinsicHeight();
        mCloseIconForTextOnly.getLayoutParams().height =  mClearIconHeight;
        mCloseIconForTextOnly.getLayoutParams().width =  mClearIconWidth;
        mCloseIcon.getLayoutParams().height =  mClearIconHeight;
        mCloseIcon.getLayoutParams().width =  mClearIconWidth;

        mCloseIconForTextOnly.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mOnUserDismissListener != null) {
                    android.util.Log.i(TAG, "call onUserDismissListener");
                    mOnUserDismissListener.onDismiss();
                }
            }
        });

        mCloseIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mOnUserDismissListener != null) {
                    android.util.Log.i(TAG, "call onUserDismissListener");
                    mOnUserDismissListener.onDismiss();
                }
            }
        });

        setPopupWindowParams();

    }

    private void initBackgroundColor() {
        Drawable background = mContext.getResources().getDrawable(R.drawable.common_popupmenu);
        background = applyColorMultiply(background, mMultiplyColor);
        mVisible_panel.setBackground(background);
    }

    /**
     * Set the background color of quick tips.
     *
     * @param color the background color of quick tips.
     *
     */
    public void setBackgroundColor(int color) {
        mMultiplyColor = color;
        initTriangle(mContext.getResources());
        initBackgroundColor();
    }

    private View mAnchorView = null;
    /**
     * <p>
     * The anchor information is needed for estimating the size of rotate layout.
     * If setOrientation before the first showing time, it's better to setup anchor view to prevent truncate issue.
     *
     * Set the orientation type for this quick tip. Should be one of these four TYPEs
     * @see #SCREEN_MODE_PORTRAIT
     * @see #SCREEN_MODE_LANDSCAPE
     * @see #SCREEN_MODE_IPORTRAIT
     * @see #SCREEN_MODE_ILANDSCAPE
     * </p>
     *
     * @param screen_mode
     *            the orientation type for this quick tip
     * @param anchor
     *            the anchor view of this quick tip
     *
     */
    public void setOrientation(byte screen_mode, View anchor){
        mAnchorView = anchor;
        setOrientation(screen_mode);
    }

    /**
     * When the anchor position changes,please call updatePosition(anchor),
     * QuickTipsPopup adjusted to the appropriate location.
     *
     * @param anchor
     *           the anchor view of this quick tip
     */
    public void updatePosition(View anchor) {
        updatePositionByAnchor(anchor);
    }

    /**
     * <p>
     * Set the orientation type for this quick tip. Should be one of these four TYPEs
     * @see #SCREEN_MODE_PORTRAIT
     * @see #SCREEN_MODE_LANDSCAPE
     * @see #SCREEN_MODE_IPORTRAIT
     * @see #SCREEN_MODE_ILANDSCAPE
     * </p>
     *
     * @param screen_mode
     *            the orientation type for this quick tip
     *
     */
    public void setOrientation(byte screen_mode){

        mRotateRelativeLayout.setRotation(screen_mode , mExpandDirection != PopupBubbleWindow.EXPAND_NO_ANCHOR);

        if(!isOrientationChanged(screen_mode))   return;

        // dismiss popup first to reset the popup size
        View anchor = mParent != null ? mParent.get() : mAnchorView;
        boolean showAgain = false;

        if(this.isShowing()){
            this.dismiss();
            showAgain = true;
        }

        int ori_width = mContentView.getMeasuredWidth();
        int ori_height = mContentView.getMeasuredHeight();


        LinearLayout.LayoutParams text_params = (LinearLayout.LayoutParams) mTipText.getLayoutParams();
        int widthSpec = MeasureSpec.makeMeasureSpec(ori_height, MeasureSpec.EXACTLY);
        int heightSpec = MeasureSpec.makeMeasureSpec(ori_width, MeasureSpec.EXACTLY);
        boolean hasImage = (mImageSrc != null);
        int clear_icon_width = (mCloseIconForTextOnly!=null && mCloseIconForTextOnly.getVisibility()==View.VISIBLE) ? mClearIconWidth : 0;
        int right_margin = clear_icon_width == 0 ? M1 : 2*M2;
        boolean isLandscape= (screen_mode == SCREEN_MODE_LANDSCAPE || screen_mode == SCREEN_MODE_ILANDSCAPE);
        int ori_img_height = hasImage? mImageSection.getMeasuredHeight()+M1 :0;

        if(isLandscape){

                if(null != anchor && mExpandDirection != PopupBubbleWindow.EXPAND_NO_ANCHOR){
                boolean isHeightChange = false;

                final Rect displayFrame = new Rect();
                if (mContext != null)
                    ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRectSize(displayFrame);
                int[] mDrawingLocation = new int[2];
                anchor.getLocationInWindow(mDrawingLocation);

                int status_bar_height = HtcCommonUtil.getStatusBarHeight(mContext);

                if (mExpandDirection == PopupBubbleWindow.EXPAND_DEFAULT) {
                    int[] mScreenLocation = new int[2];
                    anchor.getLocationInWindow(mScreenLocation);
                    mExpandDirection = ((displayFrame.bottom - mScreenLocation[1]
                            - anchor.getHeight() - mYoff) < (mScreenLocation[1] - mYoff - displayFrame.top)) ? PopupBubbleWindow.EXPAND_UP
                            : PopupBubbleWindow.EXPAND_DOWN;
                }

                int mPopupHeight = ori_width + mPopupShadowBottom + mPopupShadowTop;
                // check height
                if (mExpandDirection == PopupBubbleWindow.EXPAND_UP
                        && mDrawingLocation[1] - (mBubbleHeadOffset - mPopupShadowBottom) - (mBubbleBodyOffset - mPopupShadowTop) - status_bar_height < mPopupHeight) {
                    ori_height = mDrawingLocation[1] - displayFrame.top - (mBubbleHeadOffset )  -(mBubbleBodyOffset )
                            - mYoff - status_bar_height;
                    isHeightChange = true;
                } else if (mExpandDirection == PopupBubbleWindow.EXPAND_DOWN
                        &&  mDrawingLocation[1]+ anchor.getMeasuredHeight()+ (mBubbleHeadOffset - mPopupShadowTop) + (mBubbleBodyOffset - mPopupShadowBottom) + mPopupHeight > displayFrame.bottom) {
                    //should take vertical offset into consideration when computing window height
                    ori_height = displayFrame.bottom -  (mBubbleHeadOffset ) - (mBubbleBodyOffset ) - mDrawingLocation[1] - anchor.getMeasuredHeight();
                    isHeightChange = true;
                }

                if(isHeightChange){
                  int textWidth = hasImage ? (ori_height - 2*M1) : (ori_height - M1 - right_margin - clear_icon_width);
                  int measureSpec_w  = MeasureSpec.makeMeasureSpec( textWidth, MeasureSpec.EXACTLY);
                  int measureSpec_h  = LayoutParams.WRAP_CONTENT;
                  text_params.width = measureSpec_w;
                  text_params.height = measureSpec_h;
                  mTipText.measure(measureSpec_w, measureSpec_h);
                  ori_height = mTipText.getMeasuredHeight() + ori_img_height+M2+M3;
                  widthSpec = MeasureSpec.makeMeasureSpec(ori_height, MeasureSpec.EXACTLY);
                }
            }

        }else{
            int textWidth = mMaxWidth - 2*M1;
            if(!hasImage && mTipText.getText()!=null && !"".equals(mTipText.getText().toString()))
                textWidth = (int) Math.min(mMaxWidth - M1 - right_margin - clear_icon_width,
                        mTipText.getPaint().measureText(mTipText.getText().toString()));
            int measureSpec_w   = hasImage ? LayoutParams.MATCH_PARENT : MeasureSpec.makeMeasureSpec( textWidth, MeasureSpec.EXACTLY);
            int measureSpec_h  = LayoutParams.WRAP_CONTENT;
            text_params.width = measureSpec_w;
            text_params.height = measureSpec_h;

            int contentWidth = hasImage ? mMaxWidth : (textWidth + M1 + right_margin + clear_icon_width);
            LayoutParams params = mContentView.getLayoutParams();
            params.width = contentWidth;
            params.height = LayoutParams.WRAP_CONTENT;

            widthSpec = MeasureSpec.makeMeasureSpec(contentWidth, MeasureSpec.AT_MOST);
            heightSpec = LayoutParams.WRAP_CONTENT;
        }

        mContentView.measure(widthSpec, heightSpec);

        // add background margin
        Rect rect = new Rect();
        int content_width = mContentView.getMeasuredWidth();
        int content_height = mContentView.getMeasuredHeight();
        if (mBackground != null) {
            mBackground.getPadding(rect);
            content_width += (rect.left+rect.right);
            content_height += (rect.top+rect.bottom);
        }

       this.setWidth(content_width);
       this.setHeight(content_height);

        // show popup again after reseting the size
        if (null != anchor && anchor.isShown() && showAgain) {
            if(mExpandDirection == PopupBubbleWindow.EXPAND_NO_ANCHOR)
                this.showAtLocation(anchor, Gravity.CENTER, 0, 0);
            else
                this.showAsDropDown(anchor);
        }
    }

    private boolean isOrientationChanged( byte screen_mode){
        if (mScreenMode == -1){
            mScreenMode = screen_mode;
            return true;
        }

        byte previous_screen_mode = mScreenMode;
        mScreenMode = screen_mode;

        if(screen_mode == SCREEN_MODE_PORTRAIT || screen_mode == SCREEN_MODE_IPORTRAIT){
            return (previous_screen_mode != SCREEN_MODE_PORTRAIT && previous_screen_mode != SCREEN_MODE_IPORTRAIT);
        }else if (screen_mode == SCREEN_MODE_LANDSCAPE || screen_mode == SCREEN_MODE_ILANDSCAPE) {
            return (previous_screen_mode == SCREEN_MODE_PORTRAIT || previous_screen_mode == SCREEN_MODE_IPORTRAIT);
        }

        return false;

    }

    private void resetLayoutParam() {
        boolean hasImage = (mImageSrc != null);
       LayoutParams text_params = mTipText.getLayoutParams();

       int clear_icon_width = (mCloseIconForTextOnly!=null && mCloseIconForTextOnly.getVisibility()==View.VISIBLE) ? mClearIconWidth : 0;
       int right_margin = clear_icon_width == 0 ? M1 : 2*M2;

        if(hasImage ||  mCloseIconForTextOnly == null || (mCloseIconForTextOnly != null && mCloseIconForTextOnly.getVisibility() != View.VISIBLE))
            ((MarginLayoutParams) text_params).setMargins(M1,M3,M1,M2);
        else
            ((MarginLayoutParams) text_params).setMargins(M1,M3,M2,M2);

        int textWidth = mMaxWidth - 2*M1;
        if(!hasImage && mTipText.getText()!=null && !"".equals(mTipText.getText().toString())){
            textWidth = (int) Math.min(mMaxWidth - M1 - right_margin - clear_icon_width,
                    mTipText.getPaint().measureText(mTipText.getText().toString()));

            // check limit
            if(mParent!=null && mExpandDirection == PopupBubbleWindow.EXPAND_LEFT){
                View anchor = mParent.get();
                if(mContext != null && null != anchor){
                    final Rect displayFrame = new Rect();
                    ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRectSize(displayFrame);
                    int[] mDrawingLocation = new int[2];
                    anchor.getLocationInWindow(mDrawingLocation);

                    int mPopupWidth = textWidth + M1 + right_margin + clear_icon_width + mPopupShadowLeft + mPopupShadowRight;
                    int mX = mDrawingLocation[0] - mPopupWidth - (mBubbleLandHeadOffset - mPopupShadowRight) + mXoff;

                    // check left
                    if (mX < displayFrame.left + (mBubbleLandBodyOffset - mPopupShadowLeft)) {
                        mPopupWidth =  mDrawingLocation[0] - mBubbleLandBodyOffset - mBubbleLandHeadOffset - mXoff ;
                        textWidth = mPopupWidth - ( M1 + right_margin + clear_icon_width );
                    }
                }
            }
            // end : check limit
        }

        int measureSpec_w  = MeasureSpec.makeMeasureSpec( textWidth, MeasureSpec.EXACTLY);
        int measureSpec_h  = ViewGroup.LayoutParams.WRAP_CONTENT;
        text_params.width = measureSpec_w;
        text_params.height = measureSpec_h;

        LayoutParams params = mContentView.getLayoutParams();
        int contentWidth = hasImage ? mMaxWidth : (textWidth + M1 + right_margin + clear_icon_width);
        if(params==null){
            params = new LayoutParams(contentWidth,  LayoutParams.WRAP_CONTENT);
            mContentView.setLayoutParams(params);
        }else{
            params.width = contentWidth;
            params.height = LayoutParams.WRAP_CONTENT;
        }

        int widthSpec = MeasureSpec.makeMeasureSpec(contentWidth, MeasureSpec.AT_MOST);
        int heightSpec = ViewGroup.LayoutParams.WRAP_CONTENT;

        mContentView.measure(widthSpec, heightSpec);

        // add background margin
        Rect rect = new Rect();
        int content_width = mContentView.getMeasuredWidth();
        int content_height = mContentView.getMeasuredHeight();
        if (mBackground != null) {
            mBackground.getPadding(rect);
            content_width += (rect.left+rect.right);
            content_height += (rect.top+rect.bottom);
        }

       this.setWidth(content_width);
       this.setHeight(content_height);

    }

    /**@hide*/
    @Override
    protected void setTouchDelegate(){
        if (mVisible_panel == null)
            return;

        if (mIsCloseVisible && mCloseIconForTextOnly != null && mCloseIconForTextOnly.getVisibility() == View.VISIBLE){
            mVisible_panel.post(new Runnable() {
                public void run() {
                    final Rect r = new Rect();
                    mCloseIconForTextOnly.getHitRect(r);
                    r.top -= mIncreasedTouchSize;
                    r.bottom += mIncreasedTouchSize;
                    r.left -= mIncreasedTouchSize;
                    r.right += mIncreasedTouchSize;
                    mVisible_panel.setTouchDelegate(new TouchDelegate( r, mCloseIconForTextOnly));
                }
            });
        }else if (mIsCloseVisible && mCloseIcon != null && mCloseIcon.getVisibility() == View.VISIBLE){
            mVisible_panel.post(new Runnable() {
                public void run() {
                    final Rect r = new Rect();
                    mCloseIcon.getHitRect(r);
                    r.top -= mIncreasedTouchSize;
                    r.bottom += mIncreasedTouchSize;
                    r.left -= mIncreasedTouchSize;
                    r.right += mIncreasedTouchSize;
                    mVisible_panel.setTouchDelegate(new TouchDelegate( r, mCloseIcon));
                }
            });
        }

        mVisible_panel.setTouchDelegate(null);
    }

    /**@hide*/
    @Override
    protected void releaseTouchDelegate(){
        if(mVisible_panel!=null)
            mVisible_panel.setTouchDelegate(null);
    }

    @Override
    protected void resetHeight( WindowManager.LayoutParams params){

        boolean hasImage = (mImageSrc != null);
        LinearLayout.LayoutParams text_params = (android.widget.LinearLayout.LayoutParams) mTipText.getLayoutParams();
        int clear_icon_width = (mCloseIconForTextOnly!=null && mCloseIconForTextOnly.getVisibility()==View.VISIBLE) ? mClearIconWidth : 0;
        int right_margin = clear_icon_width == 0 ? M1 : 2*M2;
        int content_width = Math.min(mMaxWidth, params.width);
        int textWidth = content_width - 2*M1;
        if(!hasImage && mTipText.getText()!=null && !"".equals(mTipText.getText().toString())) {
            textWidth = (int) Math.min(content_width - M1 - right_margin - clear_icon_width,
                    mTipText.getPaint().measureText(mTipText.getText().toString()));
        }
        text_params.width = MeasureSpec.makeMeasureSpec( textWidth, MeasureSpec.EXACTLY);

        int widthSpec = MeasureSpec.makeMeasureSpec(content_width, MeasureSpec.AT_MOST);
        int heightSpec = ViewGroup.LayoutParams.WRAP_CONTENT;

        mContentView.measure(widthSpec, heightSpec);

        // add background margin
        Rect rect = new Rect();
        int content_height = mContentView.getMeasuredHeight();
        if (mBackground != null) {
            mBackground.getPadding(rect);
            content_width += rect.left+rect.right;
            content_height += rect.top+rect.bottom;
        }
        params.width = content_width;
        params.height = content_height;
    }

    private void setPopupWindowParams() {
        resetLayoutParam();
        this.setContentView(mContentView);
        this.setFocusable(false);
        this.setOutsideTouchable(false);
        this.setClipToScreenEnabled(true);
    }

    /**
     * <p>
     * Set the text for quick tips.
     * </p>
     *
     * @param text
     *            the text of quick tips
     */
    public void setText(CharSequence text){
        if(mTipText!=null){
            mTipText.setText(text);
            resetLayoutParam();
        }
    }

    /**
     * Set the visibility of the close button.
     *
     * @param visibility if false, the close button won't be shown.
     * Please notice that maybe there is no way to let user dismiss quick tips directly.
     * For example, user should do something right to dismiss the tips.
     */
    public void setCloseVisibility(boolean visibility){
        mIsCloseVisible = visibility;
        if(mCloseIcon!=null){
            if(mIsCloseVisible)
                mCloseIcon.setVisibility(View.VISIBLE);
            else
                mCloseIcon.setVisibility(View.GONE);
        }
        if(mCloseIconForTextOnly!=null){
            if(mIsCloseVisible)
                mCloseIconForTextOnly.setVisibility(View.VISIBLE);
            else
                mCloseIconForTextOnly.setVisibility(View.GONE);
        }
        resetLayoutParam();
    }

    /**
     * <p>
     * Set the image of image section for quick tips. The background can be set to null.
     * The vertical height of image section is default size.
     * </p>
     *
     * @param drawable
     *            the image of quick tips
     */
    public void setImage(Drawable drawable){
        setImage(drawable, mImageHeight);
    }

    /**
     * <p>
     * Set the image and the vertical height of image section for quick tips. The background can be set to null.
     * </p>
     *
     * @param drawable
     *            the image of quick tips
     * @param image_section_height
     *            the height of image section
     */
    public void setImage(Drawable drawable, int image_section_height){
        if(mTipImage!=null && drawable!=null){
            mImageSrc = drawable;
            mTipImage.setImageDrawable(drawable);
            mImageSection.setVisibility(View.VISIBLE);
            mCloseIconForTextOnly.setVisibility(View.GONE);

            if(image_section_height > 0 && image_section_height > drawable.getIntrinsicHeight()){
                LayoutParams params = mImageSection.getLayoutParams();
                if(params==null){
                    params = new LayoutParams(LayoutParams.MATCH_PARENT,  image_section_height);
                    mImageSection.setLayoutParams(params);
                }else{
                    params.width = LayoutParams.MATCH_PARENT;
                    params.height = image_section_height;
                }
            }

            resetLayoutParam();
        }else if(drawable==null){
            mImageSection.setVisibility(View.GONE);
            if(mIsCloseVisible) {
                mCloseIconForTextOnly.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * <p>
     * Set the max width for quick tips.
     * If this is a text only tip and the text length is short than max width, the width will wrap content.
     * </p>
     *
     * @param width
     *            the max width of quick tips
     */
    public void setMaxWidth(int width){
        mMaxWidth = width;
    }

}
