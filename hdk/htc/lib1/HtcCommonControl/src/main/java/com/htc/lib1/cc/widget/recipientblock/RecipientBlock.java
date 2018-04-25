package com.htc.lib1.cc.widget.recipientblock;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.CheckUtil;
import com.htc.lib1.cc.util.WindowUtil;
import com.htc.lib1.cc.util.res.HtcResUtil;
import com.htc.lib1.cc.widget.HtcAutoCompleteTextView;
import com.htc.lib1.cc.widget.HtcImageButton;

import java.lang.ref.WeakReference;

public class RecipientBlock extends LinearLayout {

    private ComposeRecipientArea mComposeRecipientArea = null;
    private WeakReference<Activity> mWeakComposeActivity;
    private LinearLayout mInputField = null;
    private LinearLayout mRecipientBlock = null;
    private HtcAutoCompleteTextView mImeAutoCompleteTextView = null;
    private HtcImageButton mImageButton = null;
    private TextView mLabelTextView = null;
    private boolean bIsInputFieldVisible = false;
    private String mLabelText= "";
    private static final boolean IS_RTL_ENABLE = Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public RecipientBlock(Context context) {
        super(context);
        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);
        init(context);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file.
     *
     * <p>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @see #View(Context, AttributeSet, int)
     */
    public RecipientBlock(Context context, AttributeSet attrs) {
        super(context, attrs);
        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);
        init(context);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of View allows subclasses to use their own base style when
     * they are inflating.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyle The default style to apply to this view. If 0, no style
     *        will be applied (beyond what is included in the theme). This may
     *        either be an attribute resource, whose value will be retrieved
     *        from the current theme, or an explicit style resource.
     * @see #View(Context, AttributeSet)
     */
    public RecipientBlock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflator.inflate(R.layout.compose_recipient_block, this, true);
        mRecipientBlock = (LinearLayout) findViewById(R.id.compose_recipient_block);

        mLabelTextView = (TextView) findViewById(R.id.label);
        mLabelTextView.setAllCaps(HtcResUtil.isInAllCapsLocale(context));
        mInputField = (LinearLayout) findViewById(R.id.input_field);

        // init input text view first to let app to replace
        mImeAutoCompleteTextView = (HtcAutoCompleteTextView) findViewById(R.id.receiverList_inputfield_to);

    }

    /**
     * setup the type of RecipientBlock
     * @param activity reference activity.
     * @param labelText RecipientBlock's labelText
     * @param isFooterExist not use anymore.
     * @param isInputFieldVisible visibility of input field.
     */
    public void setup(Activity activity, String labelText, boolean isFooterExist, boolean isInputFieldVisible) {
        mWeakComposeActivity = new WeakReference<Activity>(activity);
        mLabelText = labelText;
        bIsInputFieldVisible = isInputFieldVisible;

        setupInputField();

        initRecipientUI();
    }

    private void setupInputField(){
        // find input text view again to let app to replace
        mImeAutoCompleteTextView = (HtcAutoCompleteTextView) findViewById(R.id.receiverList_inputfield_to);

        // set up input text view
        final int margin = ResUtils.getDimenMarginM1(getContext());
        LinearLayout.LayoutParams txtParams =
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        txtParams.setMargins(margin, 0, 0, 0);
        if(IS_RTL_ENABLE) {
            txtParams.setMarginStart(margin);
            txtParams.setMarginEnd(0);
        }
        mImeAutoCompleteTextView.setLayoutParams(txtParams);
        mImeAutoCompleteTextView.setTextAppearance(getContext(), R.style.input_default_m);
        mImeAutoCompleteTextView.setId(R.id.receiverList_inputfield_to);
        mImeAutoCompleteTextView.setGravity(Gravity.CENTER_VERTICAL);
        mImeAutoCompleteTextView.setSingleLine(true);
        mImeAutoCompleteTextView.enableDropDownMinWidth(false);
        //mImeAutoCompleteTextView.textCursorDrawable(null);

        // set up picker button
        LinearLayout.LayoutParams imgParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        imgParams.gravity=Gravity.CENTER_VERTICAL;
        Drawable peopleIcon = getResources().getDrawable(R.drawable.icon_btn_people_light);
        mImageButton = new HtcImageButton(getContext()){
            @Override // to control the input field width
            protected void onVisibilityChanged(View changedView, int visibility) {
              super.onVisibilityChanged(changedView, visibility);
              mImageButton.getLayoutParams().width = (visibility==View.VISIBLE) ?
                      ResUtils.getInputFieldActionButtonWidth(getContext()) : 0;
            }
        };
        mImageButton.setLayoutParams(imgParams);
        mImageButton.setId(R.id.receiverList_img_to);
        mImageButton.setPadding(0, 0, 0, 0);
        mImageButton.setIconDrawable(peopleIcon);
        mImageButton.setScaleType(ScaleType.CENTER);
        mInputField.addView(mImageButton, imgParams);

        //set input field width here to ensure correct footer bar status
        mImageButton.getLayoutParams().width = ResUtils.getInputFieldActionButtonWidth(getContext());

    }

    /**
     *  get the IMEAutoCompleteTextView on the top of RecipientBlock.
     *  @return IMEAutoCompleteTextView on the top of RecipientBlock.
     */
    public HtcAutoCompleteTextView getInputTextView() {
        return mImeAutoCompleteTextView;
    }

    /**
     * To get the HtcImageButton on the top of RecipientBlock.
     * @return the HtcImageButton on the top of RecipientBlock.
     */
    public HtcImageButton getPickerButton() {
        return mImageButton;
    }

    private void initRecipientUI() {
        if(mInputField != null) {
            if(bIsInputFieldVisible) {
                mInputField.setVisibility(View.VISIBLE);
                if(!TextUtils.isEmpty(mLabelText)) {
                    mLabelTextView.setText(mLabelText);
                    mLabelTextView.setVisibility(View.VISIBLE);
                    /*
                     * Input field and label exist: set top margin M2 and bottom margin M1 to RecipientBlock.
                     *
                     * +--------------------------------------------------------+
                     * |                        ↑ M2 ↑                          |
                     * +--------------------------------------------------------+
                     * | ←M1→ label                                        ←M1→ |
                     * |     ↓ M2 ↓                                             |
                     * +------+-------------------------------------+--+-----+--+
                     * | ←M1→ | input field...                      |  | img |  |
                     * +------+-------------------------------------+--+-----+--+
                     * |                        ↓ M1 ↓                          |
                     * +--------------------------------------------------------+
                     */
                    mRecipientBlock.setPadding(0, ResUtils.getDimenMarginM2(getContext()), 0, ResUtils.getDimenMarginM1(getContext()));
                } else {
                    mLabelTextView.setVisibility(View.GONE);

                    /*
                     * Only input field exist: set top and bottom margin M1 to RecipientBlock.
                     *
                     * +--------------------------------------------------------+
                     * |                        ↑ M1 ↑                          |
                     * +------+-------------------------------------+--+-----+--+
                     * | ←M2→ | input field...                      |  | img |  |
                     * +------+-------------------------------------+--+-----+--+
                     * |                        ↓ M1 ↓                          |
                     * +--------------------------------------------------------+
                     */
                    mRecipientBlock.setPadding(0, ResUtils.getDimenMarginM1(getContext()), 0, ResUtils.getDimenMarginM1(getContext()));
                }

            } else {
                mInputField.setVisibility(View.GONE);
                mLabelTextView.setVisibility(View.GONE);

                //Input field or label doesn't exist: set all margin 0 to RecipientBlock.
                mRecipientBlock.setPadding(0, 0, 0, 0);
            }
        }
        int resoureId = R.id.recipient_container_to;
        mComposeRecipientArea = (ComposeRecipientArea) findViewById(resoureId);
        mComposeRecipientArea.setup(mWeakComposeActivity, false, bIsInputFieldVisible, !TextUtils.isEmpty(mLabelText));


    }

    /**
     *  get the ComposeRecipientArea.
     *  @return ComposeRecipientArea in RecipientBlock.
     */
    public ComposeRecipientArea getComposeRecipientArea() {
        return mComposeRecipientArea;
    }

    @ExportedProperty(category = "CommonControl")
    private int getInputFieldWidth(Context context , int actionButtonWidth, int groupwidth) {
        int width = -1;
        int paddings = (actionButtonWidth == 0) ? 2*ResUtils.getDimenMarginM1(context) : ResUtils.getDimenMarginM1(context);
        if (WindowUtil.isSuitableForLandscape(context.getResources())) { // landscape
            width = groupwidth - actionButtonWidth - paddings;
        } else { // portrait
            width = groupwidth  - actionButtonWidth - paddings;
        }
        return (int) width;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mImageButton.getLayoutParams().width = mImageButton.isShown() ?
                ResUtils.getInputFieldActionButtonWidth(getContext()) : 0;
    }

}
