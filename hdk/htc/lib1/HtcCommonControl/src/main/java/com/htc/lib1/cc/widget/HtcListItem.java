package com.htc.lib1.cc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.htc.lib1.cc.htcjavaflag.HtcDebugFlag;
import com.htc.lib1.cc.util.CheckUtil;
import com.htc.lib1.cc.util.LogUtil;

/**
 * HtcListItem is designed as a single item for HtcListView. You can add
 * multiple components to it. HtcListItem adds the item sequentially and
 * vertically centered the children from left end to right end. The width of
 * HtcListItem is MATCH_PARENT and the value of height references to a value
 * defined by HTC UI common guideline. There are 2 modes for HtcListItem-
 * default and customized. For the default mode, the height of HtcListItem is
 * exactly as the height defined by UI guideline. For the customized mode, the
 * height of HtcListItem is as the max height of its children Example:
 * <ul>
 * <li>XML Side
 *
 * <pre class="prettyprint">
 *  &lt;?xml version="1.0" encoding="utf-8"?&gt;
 *      &lt;com.htc.widget.HtcListItem
 *           xmlns:android="http://schemas.android.com/apk/res/android"
 *           xmlns:htc="http://schemas.android.com/apk/res/com.htc"
 *           android:layout_width="match_parent"
 *           android:layout_height="match_parent"&gt;
 *      &lt;com.htc.widget.HtcListItemColorBar android:id="@+id/colorbar"/&gt;
 *      &lt;com.htc.widget.HtcListItemQuickContactBage android:id="@+id/photo"/&gt;
 *      &lt;com.htc.widget.HtcListItem2LineText android:id="@+id/text1"/&gt;
 *      &lt;com.htc.widget.HtcListItem2LineStamp android:id="@+id/stamp"/&gt;
 *      &lt;com.htc.widget.HtcListItemRadioButton android:id="@+id/button"/&gt;
 *      &lt;/com.htc.widget.HtcListItem&gt;
 *      </pr>
 *
 * <li> Java Side - In your adapter
 *      <pre class="prettyprint">
 *      public View getView(int position, View convertView, ViewGroup parent) {
 *          HtcListItem listItem = null;
 *          if(convertView == null){
 *              HtcListItem listItem = (HtcListItem) mInflater.inflate(com.htc.lib1.cc.R.layout.list_item, null);
 *          } else {
 *              listItem = (HtcListItem) convertView;
 *          }
 *
 *          //set your rounded corner
 *          if(position % 4 == 0){
 *              listItem.setRoundCorner(HtcListItem.TOP_ROUND_CORNER_ONLY);
 *          } else if(position % 4 == 1) {
 *              listItem.setRoundCorner(HtcListItem.BOTTOM_ROUND_CORNER_ONLY);
 *          } else if(position % 4 == 2) {
 *               listItem.setRoundCorner(HtcListItem.ALL_ROUND_CORNER);
 *          } else {
 *               listItem.setRoundCorner(HtcListItem.NO_ROUND_CORNER);
 *          }
 *          //add a new component at the end of the item
 *          HtcListItemCheckBox c = new HtcListItemCheckBox(mContext);
 *          listItem.addView(c);
 *
 *          //set the text of the 2 line text component
 *          HtcListItem2LineText stamp = (HtcListItem2LineText) listItem.findViewById(com.htc.lib1.cc.R.id.text1);
 *          stamp.setPrimaryText("Line 1 Text");
 *          stamp.setSecondaryText("Line 2 Text");
 *
 *          //set the text of the 2 line stamp component
 *          HtcListItem2LineStamp stamp2 = (HtcListItem2LineStamp) listItem.findViewById(com.htc.lib1.cc.R.id.stamp);
 *          stamp2.setPrimaryText("stamp12345");
 *          stamp2.setSecondaryText("stamp23456");
 *
 *          //If your text style is not the default one, you can set it here
 *          if(position % 3 == 0){
 *              stamp.setPrimaryTextStyle(0, com.htc.lib1.cc.R.style.list_primary_m_bold);
 *          }
 *
 *          HtcListItemColorBar bar = (HtcListItemColorBar)listItem.findViewById(com.htc.lib1.cc.R.id.colorbar);
 *          bar.setImageResource(com.htc.lib1.cc.R.drawable.red_bar);
 *
 *          return listItem;
 *      }
 *     </pre>
 *
 * </ul>
 * 2 Modes: (If you don't specify the mode, item will be in defaultMode)
 *
 * <pre class="prettyprint">
 *  &lt;?xml version="1.0" encoding="utf-8"?&gt;
 *      &lt;com.htc.widget.HtcListItem
 *           xmlns:android="http://schemas.android.com/apk/res/android"
 *           xmlns:htc="http://schemas.android.com/apk/res/com.htc"
 *           android:layout_width="match_parent"
 *           android:layout_height="match_parent"
 *          htc:itemMode="defaultMode"&gt;
 *
 *   &lt;/com.htc.widget.HtcListItem&gt;
 *
 *   &lt;?xml version="1.0" encoding="utf-8"?&gt;
 *      &lt;com.htc.widget.HtcListItem
 *            xmlns:android="http://schemas.android.com/apk/res/android"
 *           xmlns:htc="http://schemas.android.com/apk/res/com.htc"
 *           android:layout_width="match_parent"
 *           android:layout_height="match_parent"
 *          htc:itemMode="customizedMode"&gt;
 *
 *   &lt;/com.htc.widget.HtcListItem&gt;
 * </pre>
 */

public class HtcListItem extends FrameLayout {
    @ExportedProperty(category = "CommonControl")
    private int mDesiredMinHeight;

    // note: 2/3, 1/3
    private static final float TEXTCOMPONENT_SEGMENT_WEIGHT = 2.0f;
    private static final float STAMPCOMPONENT_SEGMENT_WEIGHT = 1.0f;

    private ColorDrawable mColorBar = null;
    private Drawable mVirticalDivider = null;

    private static final int HEIGHT_OF_TOP_SPACE = 1;

    private static final int BG_MODE_TEXTURE = 0;
    private static final int BG_MODE_ONLY_GRADIENT = 1;
    private static final int BG_MODE_NONE = 2;
    private static final int BG_MODE_CUSTOMISED = 3;
    private static final int ASSET_COMMON_DIV = 1000; // C
    private static final int ASSET_COMMON_B_DIV = 1001; // D
    private LayerDrawable mLayerDrawable = null;
    static final int DEVICE_PHONE = 2000;
    static final int DEVICE_TAB_1_PANEL = 2001;
    static final int DEVICE_TAB_2_PANEL_LEFT = 2002;
    static final int DEVICE_TAB_2_PANEL_RIGHT = 2003;
    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = DEVICE_PHONE, to = "DEVICE_PHONE"),
            @IntToString(from = DEVICE_TAB_1_PANEL, to = "DEVICE_TAB_1_PANEL"),
            @IntToString(from = DEVICE_TAB_2_PANEL_LEFT, to = "DEVICE_TAB_2_PANEL_LEFT"),
            @IntToString(from = DEVICE_TAB_2_PANEL_RIGHT, to = "DEVICE_TAB_2_PANEL_RIGHT")
    })
    private int mDeviceMode = DEVICE_PHONE;

    // treat these fields as extra paddings, their setters are pkg-private.
    // will be used within C.C. team.
    @ExportedProperty(category = "CommonControl")
    private int mTopSpace = 0;
    @ExportedProperty(category = "CommonControl")
    private int mBottomSpace = 0;
    @ExportedProperty(category = "CommonControl")
    private int mRightSpace = 0;
    @ExportedProperty(category = "CommonControl")
    private int mLeftSpace = 0;

    @ExportedProperty(category = "CommonControl")
    private int mListItemMargin;
    @ExportedProperty(category = "CommonControl")
    private int mListItemStartMargin;
    @ExportedProperty(category = "CommonControl")
    private int mListItemEndMargin;
    // this need to be float, consider 1.3*2=2.6, this will be 3
    @ExportedProperty(category = "CommonControl")
    private float mSegmentLength = 0;

    // Default mode will use the height defined in UIGL.
    // Customized mode will use the max height of its children
    // (usually used when MessageBody exist).
    // Whatever, I don't care about these anymore.
    final static int NUM_ITEMMODE = 5;
    /**
     * This is used for default case
     */
    public static final int MODE_DEFAULT = 0;
    /** @deprecated */
    @Deprecated
    static final int MODE_CUSTOMIZED = 1;
    /**
     * This is used for 2D widget case
     */
    public static final int MODE_KEEP_MEDIUM_HEIGHT = 2;
    /**
     * This is used for automotive case
     */
    public static final int MODE_AUTOMOTIVE = 3;
    /**
     * This is used for popup menu case
     */
    public static final int MODE_POPUPMENU = 4;

    @ExportedProperty(category = "CommonControl", mapping = {
            @IntToString(from = MODE_DEFAULT, to = "MODE_DEFAULT"),
            @IntToString(from = MODE_CUSTOMIZED, to = "MODE_CUSTOMIZED"),
            @IntToString(from = MODE_KEEP_MEDIUM_HEIGHT, to = "MODE_KEEP_MEDIUM_HEIGHT"),
            @IntToString(from = MODE_AUTOMOTIVE, to = "MODE_AUTOMOTIVE"),
            @IntToString(from = MODE_POPUPMENU, to = "MODE_POPUPMENU")
    })
    private int mMode = MODE_DEFAULT;
    @ExportedProperty(category = "CommonControl")
    private int mFinalHeight = 0;

    private ViewGroup.LayoutParams mCustomLayoutParam = null;

    final static boolean LOG = false;
    final static String LOG_TAG = "HtcListItem";

    @ExportedProperty(category = "CommonControl")
    private boolean mUseCustomHeight = false;
    private HtcListItemManager mHtcListItemManager;
    /**
     * Simple constructor to use when creating this widget from code. It will
     * new a HtcListItem with default style.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     */
    public HtcListItem(Context context) {
        this(context, -1);
    }

    /**
     * Constructor that is called when inflating this widget from XML. This is
     * called when a view is being constructed from an XML file, supplying
     * attributes that were specified in the XML file.It will new a HtcListItem
     * with default style.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating this widget.
     * @deprecated [Module internal use]
     */
    @Deprecated
    public HtcListItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        mHtcListItemManager = HtcListItemManager.getInstance(context);
        initDrawablesAndItemMode(context, attrs, -1);
        initHeights();
        initListener();
    }

    /**
     * Constructor that is called when inflating this widget from code. It will
     * new a HtcListItem with specified style, mode.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param mode to indicate item mode for HtcListItem.
     */
    public HtcListItem(Context context, int mode) {
        super(context);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        mHtcListItemManager = HtcListItemManager.getInstance(context);
        initDrawablesAndItemMode(context, null, mode);
        initHeights();
        initListener();
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of this widget allows subclasses to use their own base style
     * when they are inflating. It will a widget with style defStyle.
     *
     * @param context The Context this widget is running in, through which it
     *            can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @param attrs The attributes of the XML tag that is inflating this widget.
     * @param defStyle The default style to apply to this widget.
     * @deprecated [Module internal use]
     */
    @Deprecated
    public HtcListItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        CheckUtil.isContextThemeWrapper(context);
        CheckUtil.isUIThread(context);

        mHtcListItemManager = HtcListItemManager.getInstance(context);
        initDrawablesAndItemMode(context, attrs, -1);
        initHeights();
        initListener();
    }

    private void initDrawablesAndItemMode(Context context, AttributeSet attrs, int mode) {

        TypedArray a = context.obtainStyledAttributes(attrs,
                com.htc.lib1.cc.R.styleable.HtcListItem, com.htc.lib1.cc.R.attr.htcListItemStyle,
                com.htc.lib1.cc.R.style.htcListItem);

        mLayerDrawable = (LayerDrawable) a
                .getDrawable(com.htc.lib1.cc.R.styleable.HtcListItem_android_divider);

        if (mode == -1) {
            mMode = a.getInt(com.htc.lib1.cc.R.styleable.HtcListItem_itemMode, MODE_DEFAULT);
            // for customize is deprecated
            if (mMode == MODE_CUSTOMIZED)
                mMode = MODE_DEFAULT;

        } else {
            if (mode == MODE_DEFAULT || mode == MODE_KEEP_MEDIUM_HEIGHT || mode == MODE_POPUPMENU) {
                mMode = mode;
            } else if (mode == MODE_AUTOMOTIVE) {
                mMode = mode;
            } else {
                mMode = MODE_DEFAULT;
            }
        }

        initVirticalDivider();

        a.recycle();
        if (HtcDebugFlag.getHtcDebugFlag()) {
            Log.d(LOG_TAG, "Current mode is " + mMode);
        }
    }

    private void initVirticalDivider() {
        if (mMode == MODE_POPUPMENU || mMode == MODE_AUTOMOTIVE)
            mVirticalDivider = getDrawable(ASSET_COMMON_B_DIV);
        else
            mVirticalDivider = getDrawable(ASSET_COMMON_DIV);
    }

    private void initHeights() {
        final Context context = getContext();
        if (mMode == MODE_AUTOMOTIVE) {
            mListItemMargin = HtcListItemManager.getM1(context);
            mListItemStartMargin = mListItemEndMargin = HtcListItemManager.getM1(context);
        } else {
            mListItemMargin = HtcListItemManager.getDesiredChildrenGap(context);
            mListItemStartMargin = mListItemEndMargin = HtcListItemManager.getM1(context);
        }
        mDesiredMinHeight = mHtcListItemManager.getDesiredListItemHeight(mMode);
        mCustomLayoutParam = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                mDesiredMinHeight);
    }

    private void initListener() {
        super.setOnHierarchyChangeListener(mOnHierarchyChangeListener);
    }

    private Drawable getDrawable(int asset) {
        Drawable drawable = null;
        switch (asset) {
            case ASSET_COMMON_DIV:
                if (mLayerDrawable != null)
                    drawable = mLayerDrawable.getDrawable(0);
                else
                    drawable = getContext().getResources().getDrawable(
                            com.htc.lib1.cc.R.drawable.common_list_divider);
                break;
            case ASSET_COMMON_B_DIV:
                if (mLayerDrawable != null)
                    drawable = mLayerDrawable.getDrawable(1);
                else
                    drawable = getContext().getResources().getDrawable(
                            com.htc.lib1.cc.R.drawable.common_b_div_land);
                break;
            default:
                Log.e("HtcListItem", "fail to getDrawable.");
                drawable = getContext().getResources().getDrawable(
                        com.htc.lib1.cc.R.drawable.common_list_divider);
        }

        return drawable;
    }

    /**
     * Returns a new set of layout parameters based on the supplied attributes
     * set.
     *
     * @param attrs the attributes to build the layout parameters from
     * @return an instance of {@link android.view.ViewGroup.LayoutParams} or one
     *         of its descendants
     * @deprecated [Module internal use]
     */
    @Deprecated
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        try {
            return super.generateLayoutParams(attrs);
        } catch (Exception e) {
            return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    /**
     * Adds a child view. If no layout parameters are already set on the child,
     * the default parameters for this ViewGroup are set on the child.
     *
     * @param child the child view to add
     * @param index the position at which to add the child
     * @param params layout parameters
     * @deprecated [Not use any longer]
     */
    @Deprecated
    public void addView(View child, int index, LayoutParams params) {
        ViewGroup.LayoutParams childLayoutParams = child.getLayoutParams();
        if (childLayoutParams == null) {
            super.addView(child, index, params);
        } else {
            super.addView(child, index, new LayoutParams(childLayoutParams));
        }
    }

    OnHierarchyChangeListener mChildOnHierarchyChangeListener;

    @Override
    public void setOnHierarchyChangeListener(OnHierarchyChangeListener listener) {
        mChildOnHierarchyChangeListener = listener;
    }

    OnHierarchyChangeListener mOnHierarchyChangeListener = new OnHierarchyChangeListener() {

        @Override
        public void onChildViewAdded(View parent, View child) {
            if (mChildOnHierarchyChangeListener != null) {
                mChildOnHierarchyChangeListener.onChildViewAdded(parent, child);
            }

            onChildAdded(child);
        }

        @Override
        public void onChildViewRemoved(View parent, View child) {
            if (mChildOnHierarchyChangeListener != null) {
                mChildOnHierarchyChangeListener.onChildViewRemoved(parent, child);
            }

        }
    };

    /**
     * To notify all child the item mode when mode is MODE_AUTOMOTIVE or
     * MODE_KEEP_MEDIUM_HEIGHT
     *
     * @param child the child view to add
     */
    private void onChildAdded(View child) {
        if (HtcDebugFlag.getHtcDebugFlag()) {
            Log.d(LOG_TAG, child.getClass().getName() + " is added");
        }
        if (child instanceof HtcListItemQuickContactBadge || child instanceof HtcListItemTileImage) {
            isPhotoFrameExist = true;
        }

        if (mMode == MODE_AUTOMOTIVE) {
            if (child instanceof IHtcListItemAutoMotiveControl) {
                ((IHtcListItemAutoMotiveControl) child).setAutoMotiveMode(true);
            }
        } else if (mMode == MODE_KEEP_MEDIUM_HEIGHT || mMode == MODE_POPUPMENU) {
            if (child instanceof IHtcListItemComponent) {
                ((IHtcListItemComponent) child).notifyItemMode(mMode);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMeasure(int w, int h) {
        mFinalHeight = 0;
        if (LOG)
            Log.d(LOG_TAG,
                    "width mode=" + MeasureSpec.getMode(w) + " width size="
                            + MeasureSpec.getSize(w) + " height mode=" + MeasureSpec.getMode(h)
                            + " height size=" + MeasureSpec.getSize(h));

        // 0, if width is not UNSPECIFIED, set listitem's width.
        // 1, check how much space is left for TEXTs & STAMPs, then calculate
        // the segment.
        // 2, measure them, then get the highest TEXT's or STAMP's height.
        // then use the height.
        // if other widgets are taller -- it's easy to handle.
        step0(w, h);
    }

    private void step0(int w, int h) {
        switch (MeasureSpec.getMode(w)) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                // here the height is less important. we just need to know the
                // max width.
                setMeasuredDimension(MeasureSpec.getSize(w), MeasureSpec.getSize(h));
                step1(w, h);
                step2(w, h);
                break;
            case MeasureSpec.UNSPECIFIED:
            default:
                stepX(w, h);
                break;
        }
    }

    // measure them all, them add their width together, then add M2 if needed.
    // only pop-up window will use UNSPECIFIED.
    private void stepX(int w, int h) {
        super.onMeasure(w, h);

        final int count = getChildCount();
        View child;
        int totalWidth = 0;
        int maxHeight = mDesiredMinHeight;
        for (int i = 0; i < count; i++) {
            child = getChildAt(i);

            if (child == null)
                continue;

            maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
            if (i == 0) {
                if (mFirstComponentAlign) {
                    totalWidth += mHtcListItemManager.getDesiredListItemHeight(mMode);
                } else {
                    totalWidth += mListItemStartMargin;
                    totalWidth += child.getMeasuredWidth();
                }
                if (i == count - 1)
                    totalWidth += mListItemEndMargin;
                else
                    totalWidth += mListItemMargin;
            } else if (i == count - 1) {
                if (mLastComponentAlign)
                    totalWidth += HtcListItemManager
                            .getActionButtonWidth(getContext(), mMode);
                else {
                    totalWidth += child.getMeasuredWidth();
                    totalWidth += mListItemEndMargin;
                }
            } else {
                totalWidth += child.getMeasuredWidth();
                totalWidth += mListItemMargin;
            }
        }

        mFinalHeight = maxHeight;
        setMeasuredDimension(totalWidth, mFinalHeight);
    }

    private int mTextTotalLength = 0;
    private int mStampTotalLength = 0;
    private int countOfText = 0;
    private int countOfStamp = 0;
    private int mTextStampWidth = 0;

    /**
     * calculate the place remained for TEXT and STAMP, not including margin.
     * then calculate the count of them and segment.
     *
     * @param w
     * @param h
     * @return
     */
    private void step1(int w, int h) {
        float countOfSegmentOfTextAndStamp = 0;
        mSegmentLength = 0;
        mTextTotalLength = 0;
        mStampTotalLength = 0;
        countOfText = 0;
        countOfStamp = 0;
        int usedWidth = 0;
        usedWidth += mLeftSpace + mRightSpace;
        LayoutParams lp;
        int width = w;

        View lastChild = null;
        final Context context = getContext();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            if (child == null || child.getVisibility() == View.GONE) {
                if (i == 0)
                    // first component is gone.
                    usedWidth += mListItemStartMargin;
                else if (i == getChildCount() - 1)
                    usedWidth += mListItemEndMargin - mListItemMargin;

                continue;
            }

            if (child instanceof IHtcListItemTextComponent
                    || child instanceof IHtcListItemStampComponent) {
                width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            }
            measureChild(child, width, h);

            lp = (LayoutParams) child.getLayoutParams();
            // add start margin M1
            if (i == 0)
                usedWidth += mListItemStartMargin;
            // add margin M2 between components
            usedWidth += mListItemMargin;
            // add margin M1 for the last components
            if (i == getChildCount() - 1) {
                usedWidth += mListItemEndMargin - mListItemMargin;
                if (!mLastComponentAlign) {
                    lp.setMargins(lp.leftMargin, lp.topMargin, mListItemEndMargin, lp.bottomMargin);
                }
            }

            // measure it.
            if (child instanceof IHtcListItemTextComponent) {
                if (child instanceof HtcListItemMessageBody)
                    usedWidth -= (i == getChildCount() - 1) ? mListItemEndMargin : mListItemMargin;
                else if (i == 0)
                    lp.setMargins(mListItemStartMargin, lp.topMargin, lp.rightMargin,
                            lp.bottomMargin);
                countOfText++;
                countOfSegmentOfTextAndStamp += TEXTCOMPONENT_SEGMENT_WEIGHT;
                mTextTotalLength += child.getMeasuredWidth();
            } else if (child instanceof IHtcListItemStampComponent) {
                countOfStamp++;
                countOfSegmentOfTextAndStamp += STAMPCOMPONENT_SEGMENT_WEIGHT;
                mStampTotalLength += child.getMeasuredWidth();
            } else if (child instanceof EditText) {
                countOfText++;
                countOfSegmentOfTextAndStamp += TEXTCOMPONENT_SEGMENT_WEIGHT;
                mTextTotalLength += child.getMeasuredWidth();
                ((LayoutParams) child.getLayoutParams()).topMargin = ((LayoutParams) child
                        .getLayoutParams()).bottomMargin = context.getResources()
                        .getDimensionPixelOffset(com.htc.lib1.cc.R.dimen.margin_m);

            } else {
                // remember here we don't need to measure TEXT & STAMP
                // if the child want to be match parent, add M2 to its both side
                // as margin.
                // if there are more than 1 child is MP, whatever, this is not
                // LinearLayout.
                if (lp.width == LayoutParams.MATCH_PARENT)
                    lp.setMargins(lp.leftMargin == 0 ? mListItemStartMargin : lp.leftMargin,
                            lp.topMargin,
                            lp.rightMargin == 0 ? mListItemEndMargin : lp.rightMargin,
                            lp.bottomMargin);

                // ---old---
                // we need to ignore margin(android margin, not gap)(by not
                // using measureChildWithMargins)
                // and listitem's padding(by passing in 0 in
                // getChildMeasureSpec()).
                // ---old---
                // fix: max height is fixed when only contains LinearLayout
                if (child instanceof LinearLayout) {
                    // this is what we do first to fix
                    // "sometimes child's height is 1".
                    // copy it back to handle LinearLayout, very tricky.
                    child.measure(getChildMeasureSpec(w, lp.leftMargin + lp.rightMargin, lp.width),
                            getChildMeasureSpec(h, 0, lp.height));
                } else {
                    child.measure(
                    // use margin, padding is internal to the child
                            getChildMeasureSpec(w, lp.leftMargin + lp.rightMargin, lp.width),

                            // the height is at most mDesiredMinHeight, fix:
                            // sometimes child's height is 1.
                            getChildMeasureSpec(MeasureSpec.makeMeasureSpec(mDesiredMinHeight,
                                    lp.height == LayoutParams.MATCH_PARENT ? MeasureSpec.EXACTLY
                                            : MeasureSpec.AT_MOST),
                            // we'll honor child's top & bottom
                            // paddings,
                            // sometimes AP want to use paddings in
                            // ImageView
                                    child.getPaddingTop() + child.getPaddingBottom(), lp.height));
                }
                if (i == 0) {
                    if (mFirstComponentAlign) {
                        usedWidth -= mListItemStartMargin;
                        usedWidth -= mListItemMargin;
                        // automotive mode : 20% of PortraitWindowWidth
                        // else mode: HtcListItem Height + M2
                        if (mMode == MODE_AUTOMOTIVE)
                            usedWidth += HtcListItemManager.getActionButtonWidth(context, mMode);
                        else
                            usedWidth += mHtcListItemManager.getPhotoFrameWidth(context, mMode);
                    } else if (child instanceof IHtcListItemComponentNoLeftTopMargin) {
                        usedWidth += child.getMeasuredWidth();
                        usedWidth -= mListItemStartMargin;
                    } else {
                        // margin M2
                        usedWidth += child.getMeasuredWidth();
                    }
                } else if (i == getChildCount() - 1 && mLastComponentAlign) {
                    usedWidth -= mListItemEndMargin;
                    usedWidth += HtcListItemManager.getActionButtonWidth(context, mMode);
                    if (bVerticalDivider)
                        usedWidth += HtcListItemManager.getVerticalDividerWidth(context);
                    else
                        usedWidth -= mListItemMargin;
                } else {
                    if (child instanceof ToggleButton) {
                        usedWidth -= mListItemMargin;
                        if (i == getChildCount() - 1)
                            usedWidth -= mListItemEndMargin;
                        usedWidth += child.getMeasuredWidth();
                    } else {
                        usedWidth += child.getMeasuredWidth();
                    }
                }
                // measure HtcCompoundButton again to support
                // "enlarge touch area of HtcCompoundButton"
                // the rule is: every compound button will obtain its left &
                // right M2
                // this will affect the onLayout()
                if (child instanceof HtcCompoundButton) {
                    child.measure(MeasureSpec.makeMeasureSpec(child.getMeasuredWidth()
                            + mListItemMargin * 2, MeasureSpec.EXACTLY), MeasureSpec
                            .makeMeasureSpec(mHtcListItemManager.getDesiredListItemHeight(mMode),
                                    MeasureSpec.EXACTLY));
                    // here, add the extra M2 to usedWidth
                    if (lastChild instanceof HtcCompoundButton
                            || lastChild instanceof HtcListItem7Badges1LineBottomStamp)
                        usedWidth += mListItemMargin;
                } else if (child instanceof HtcListItem7Badges1LineBottomStamp) {
                    // Measure HtcListItem7Badges1LineBottomStamp
                    // add M2 in the left and M2 in the right
                    // but if 7Badges is the last child. should add M1 in the
                    // right
                    ((HtcListItem7Badges1LineBottomStamp) child).setM2Enable(true);
                    child.measure(MeasureSpec.makeMeasureSpec(child.getMeasuredWidth()
                            + mListItemMargin
                            + (i == getChildCount() - 1 ? mListItemEndMargin : mListItemMargin),
                            MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                            mHtcListItemManager.getDesiredListItemHeight(mMode), MeasureSpec.EXACTLY));
                }
            }
            lastChild = child;
        }

        // + tablet start
        if (mMode == MODE_DEFAULT || mMode == MODE_KEEP_MEDIUM_HEIGHT) {

            int margin = 0;
            if (mDeviceMode == DEVICE_TAB_1_PANEL || mDeviceMode == DEVICE_TAB_2_PANEL_RIGHT) {
                margin = HtcListItemManager.getM1(context) * 2;
            } else if (mDeviceMode == DEVICE_TAB_2_PANEL_LEFT) {
                margin = HtcListItemManager.getM2(context) * 2;
            }

            if (margin != 0) {
                View firstChild = getChildAt(0);
                if (firstChild == null
                        || firstChild.getVisibility() == View.GONE
                        || (!mFirstComponentAlign && !(firstChild instanceof IHtcListItemComponentNoLeftTopMargin)))
                    usedWidth += margin - mListItemMargin;

                lastChild = getChildAt(getChildCount() - 1);
                if (lastChild == null || lastChild.getVisibility() == View.GONE
                        || !mLastComponentAlign)
                    usedWidth += margin - mListItemMargin;
            }
        }
        // - tablet end

        if (MeasureSpec.getMode(w) == MeasureSpec.UNSPECIFIED)
            mSegmentLength = 0;
        else
            mSegmentLength = (getMeasuredWidth() - usedWidth) / countOfSegmentOfTextAndStamp;

        mTextStampWidth = getMeasuredWidth() - usedWidth;
        if (mTextStampWidth < 0) {
            LogUtil.logE(LOG_TAG,
                    "getMeasuredWidth() - usedWidth < 0 :",
                    " getMeasuredWidth() = ", getMeasuredWidth(),
                    ", usedWidth = ", usedWidth);
            mTextStampWidth = 0;
            mSegmentLength = 0;
        }

    }

    // these fields are added to optimize step2, try to make it quick

    private void step2(int w, int h) {
        int widthSpecForText = 0;
        int widthSpecForStamp = 0;
        int segmentOfText = (int) (mSegmentLength * TEXTCOMPONENT_SEGMENT_WEIGHT);
        int segmentOfStamp = (int) (mSegmentLength * STAMPCOMPONENT_SEGMENT_WEIGHT);

        // 1, use EXACTLY, not UNSPECIFIED, so the total width can be used,
        // and no need to care about unused space.
        // 2, reduce the times of calculate

        int maxHeight = mDesiredMinHeight;

        int totalWidth = 0;
        LayoutParams lp = null;

        // --------------------------------------------------------------------------------------------------------
        // determine text & stamp measure spec
        if (mStampTotalLength <= segmentOfStamp) // 3.4, ok
        {
            int freeSpace = (int) (mTextStampWidth - mTextTotalLength - mStampTotalLength);

            int widthText = (countOfText == 0) ? 0 : mTextTotalLength / countOfText + freeSpace
                    / countOfText;
            int widthStamp = (countOfStamp == 0) ? 0 : mStampTotalLength / countOfStamp;

            widthSpecForText = MeasureSpec.makeMeasureSpec(widthText, MeasureSpec.EXACTLY);
            widthSpecForStamp = MeasureSpec.makeMeasureSpec(widthStamp, MeasureSpec.EXACTLY); // //
        } else if (mTextTotalLength >= segmentOfText && mStampTotalLength > segmentOfStamp) {
            widthSpecForText = MeasureSpec.makeMeasureSpec(segmentOfText, MeasureSpec.EXACTLY);
            widthSpecForStamp = MeasureSpec.makeMeasureSpec(segmentOfStamp, MeasureSpec.EXACTLY);
        } else if (mTextTotalLength < segmentOfText && mStampTotalLength > segmentOfStamp) {
            if (mTextTotalLength + mStampTotalLength <= mTextStampWidth) {
                int freeSpace = (int) (mTextStampWidth - mTextTotalLength - mStampTotalLength);
                int widthText = (countOfText == 0) ? 0 : mTextTotalLength / countOfText + freeSpace
                        / countOfText;
                int widthStamp = (countOfStamp == 0) ? 0 : mStampTotalLength / countOfStamp;

                widthSpecForText = MeasureSpec.makeMeasureSpec(widthText, MeasureSpec.EXACTLY);
                widthSpecForStamp = MeasureSpec.makeMeasureSpec(widthStamp, MeasureSpec.EXACTLY); // //
            } else if (mTextTotalLength + mStampTotalLength > mTextStampWidth) {

                int freeSpace = (int) (mTextStampWidth - mTextTotalLength - mStampTotalLength);
                int widthText = (countOfText == 0) ? 0 : mTextTotalLength / countOfText;
                int widthStamp = (countOfStamp == 0) ? 0 : mStampTotalLength / countOfStamp
                        + freeSpace / countOfStamp;

                widthSpecForText = MeasureSpec.makeMeasureSpec(widthText, MeasureSpec.EXACTLY); // //
                widthSpecForStamp = MeasureSpec.makeMeasureSpec(widthStamp, MeasureSpec.EXACTLY);
            } else {
                Log.e(LOG_TAG, " onMeasure error.");
            }
        } else {
            Log.e(LOG_TAG, " onMeasure error..");
        }
        // --------------------------------------------------------------------------------------------------------

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            if (child == null || child.getVisibility() == View.GONE)
                continue;

            lp = (LayoutParams) child.getLayoutParams();

            // measure TEXT and STAMP again use new widthSpec.
            if (child instanceof IHtcListItemTextComponent || child instanceof EditText) {
                child.measure(widthSpecForText, getChildMeasureSpec(h, 0, lp.height));
            } else if (child instanceof IHtcListItemStampComponent) {
                child.measure(widthSpecForStamp, getChildMeasureSpec(h, 0, lp.height));
            }

            totalWidth += child.getMeasuredWidth();
            maxHeight = Math.max(maxHeight, child.getMeasuredHeight() + lp.topMargin
                    + lp.bottomMargin);
        }

        // --------------------------------------------------------------------------------------------------------

        maxHeight += mTopSpace + mBottomSpace;

        if (MeasureSpec.getMode(w) == MeasureSpec.UNSPECIFIED) {
            // TODO this had never happened, but maybe we should use screen's
            // width other than totalWidth
            if (mUseCustomHeight)
                setMeasuredDimension(totalWidth, mCustomLayoutParam.height);
            else
                setMeasuredDimension(totalWidth, maxHeight);
        } else {
            if (mUseCustomHeight)
                setMeasuredDimension(MeasureSpec.getSize(w), mCustomLayoutParam.height);
            else
                setMeasuredDimension(MeasureSpec.getSize(w), maxHeight);
        }

        mFinalHeight = maxHeight;
    }

    /**
     * Called from layout when this view should assign a size and position to
     * each of its children. Derived classes with children should override this
     * method and call layout on each of their children.
     *
     * @param changed This is a new size or position for this view
     * @param l Left position, relative to parent
     * @param t Top position, relative to parent
     * @param r Right position, relative to parent
     * @param b Bottom position, relative to parent
     */
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int height = mFinalHeight;
        int minHeight = mDesiredMinHeight;
        int left = 0;
        int top = 0;
        int textTop = 0;

        left += mLeftSpace;

        View firtChild = getChildAt(0);
        final boolean isLayoutRtl = (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) ? getLayoutDirection() == View.LAYOUT_DIRECTION_RTL : false;
        final Context context = getContext();

        int photoFrameAndM2Width = mHtcListItemManager.getPhotoFrameWidth(context, mMode);
        int actionButtonWidth = HtcListItemManager.getActionButtonWidth(context, mMode);

        // + Add the first left margin
        if (firtChild != null && firtChild.getVisibility() != View.GONE && mFirstComponentAlign) {
            // when set first component align ,automotive mode child width is
            // 20% of PortraitWindowWidth
            // else mode child width is HtcListItem Height + M2
            if (mMode == MODE_AUTOMOTIVE)
                left += (actionButtonWidth - firtChild.getMeasuredWidth()) / 2;
            else
                left += (photoFrameAndM2Width - firtChild.getMeasuredWidth()) / 2;
        } else if (firtChild != null && firtChild.getVisibility() != View.GONE
                && (firtChild instanceof IHtcListItemComponentNoLeftTopMargin)) {
            // no left margin if component is HtcQuickContactBadge or
            // HtcTileImage
        } else {
            if ((mMode == MODE_DEFAULT || mMode == MODE_KEEP_MEDIUM_HEIGHT)) {
                if (mDeviceMode == DEVICE_TAB_1_PANEL || mDeviceMode == DEVICE_TAB_2_PANEL_RIGHT)
                    left += HtcListItemManager.getM1(context) * 2;
                else if (mDeviceMode == DEVICE_TAB_2_PANEL_LEFT)
                    left += HtcListItemManager.getM2(context) * 2;
                else
                    left += mListItemStartMargin;
            } else {
                left += mListItemStartMargin;
            }
        }
        // - Add the first left margin

        View lastChild = null;
        LayoutParams lp;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            lp = (LayoutParams) child.getLayoutParams();

            if (child == null || child.getVisibility() == View.GONE)
                continue;

            if (mFixedTopMargin && i == 0)
                top = (minHeight + mTopSpace - mBottomSpace - (child.getMeasuredHeight()
                        + lp.topMargin + lp.bottomMargin)) / 2;
            else if ((child instanceof IHtcListItemComponentNoLeftTopMargin)) {
                top = mTopSpace;
            } else if (child instanceof HtcListItem7Badges1LineBottomStamp
                    && !((HtcListItem7Badges1LineBottomStamp) child).isBadgesVerticalCenter())
                top = mTopSpace;
            else if (child instanceof HtcListItem2LineStamp
                    && lastChild instanceof HtcListItem2LineText)
                top = textTop;
            else {
                top = (height + mTopSpace - mBottomSpace - (child.getMeasuredHeight()
                        + lp.topMargin + lp.bottomMargin)) / 2;
                textTop = top;
            }
            top += lp.topMargin;

            if (i == getChildCount() - 1 && mLastComponentAlign) {
                if (bVerticalDivider)
                    left += HtcListItemManager.getVerticalDividerWidth(context);
                else
                    left -= mListItemMargin;
                left += (HtcListItemManager.getActionButtonWidth(context, mMode) - child
                        .getMeasuredWidth()) / 2;
            } else if (child instanceof HtcCompoundButton
                    || (lastChild != null && lastChild instanceof HtcCompoundButton)
                    || child instanceof HtcListItem7Badges1LineBottomStamp
                    || child instanceof ToggleButton)
                left -= mListItemMargin;

            if (isLayoutRtl) {
                int childR = r - l - left;
                child.layout(childR - child.getMeasuredWidth(), top, childR,
                        top + child.getMeasuredHeight());

            } else {

                child.layout(left, top, left + child.getMeasuredWidth(),
                        top + child.getMeasuredHeight());
            }

            left += child.getMeasuredWidth();

            if (firtChild != null && firtChild.getVisibility() != View.GONE && i == 0
                    && mFirstComponentAlign) {
                // when set first component align ,automotive mode child width
                // is 20% of PortraitWindowWidth
                // else mode child width is (HtcListItem Height + M2)
                if (mMode == MODE_AUTOMOTIVE)
                    left += (actionButtonWidth - firtChild.getMeasuredWidth()) / 2;
                else
                    left += (photoFrameAndM2Width - firtChild.getMeasuredWidth()) / 2;
            } else
                left += mListItemMargin;
            lastChild = child;
        }
    }

    /**
     * @deprecated [Module internal use]
     */
    /** @hide */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        final boolean isLayoutRtl = (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) ? getLayoutDirection() == View.LAYOUT_DIRECTION_RTL : false;
        if (bColorBar && mColorBar != null) {
            if (isPhotoFrameExist)
                mColorBar.setBounds(mLeftSpace, 0, mLeftSpace + mColorBarWidth,
                        mHtcListItemManager.getDesiredListItemHeight(mMode));
            else
                mColorBar.setBounds(mLeftSpace, 0, mLeftSpace + mColorBarWidth, mFinalHeight);

            mColorBar.draw(canvas);
        }

        if (bVerticalDivider && mVirticalDivider != null) {
            final Context context = getContext();
            int left = 0;
            int right = 0;
            if (isLayoutRtl) {
                left = HtcListItemManager.getActionButtonWidth(context, mMode);
                right = left + HtcListItemManager.getVerticalDividerWidth(context);
            } else {
                left = (int) (getWidth()
                        - HtcListItemManager.getActionButtonWidth(context, mMode) - HtcListItemManager
                        .getVerticalDividerWidth(context));
                right = (int) (getWidth() - HtcListItemManager.getActionButtonWidth(context, mMode));
            }
            int top = HtcListItemManager.getM1(context);
            int bottom = mFinalHeight - HtcListItemManager.getM1(context);

            mVirticalDivider.setBounds(left, top, right, bottom);
            mVirticalDivider.draw(canvas);
        }
    }

    void setSpaces(int leftSpace, int topSpace, int rightSpace, int bottomSpace) {
        mTopSpace = topSpace;
        mBottomSpace = bottomSpace;
        mRightSpace = rightSpace;
        mLeftSpace = leftSpace;
    }

    void setTopSpace(int height) {
        mTopSpace = height;
        requestLayout();
    }

    void setBottomSpace(int height) {
        mBottomSpace = height;
        requestLayout();
    }

    int getTopSpace() {
        return mTopSpace;
    }

    int getBottomSpace() {
        return mBottomSpace;
    }

    void setRightSpace(int width) {
        mRightSpace = width;
    }

    void setLeftSpace(int width) {
        mLeftSpace = width;
    }

    ViewGroup.LayoutParams getCustomLayoutParams() {
        return mCustomLayoutParam;
    }

    void setUseCustomHeight(boolean useCustomHeight) {
        mUseCustomHeight = useCustomHeight;
    }

    void setCustomLayoutParams(ViewGroup.LayoutParams param) {
        mCustomLayoutParam.width = param.width;
        mCustomLayoutParam.height = param.height;
        mDesiredMinHeight = mCustomLayoutParam.height;
        requestLayout();
    }

    // Josh said he need this.
    void setCustomLayoutParamsWithoutReLayout(ViewGroup.LayoutParams param) {
        mCustomLayoutParam.width = param.width;
        mCustomLayoutParam.height = param.height;
        mDesiredMinHeight = mCustomLayoutParam.height;
    }

    /**
     * if false, views will be disabled and alpha value will be set to 0.4
     *
     * @param enabled True if this view is enabled, false otherwise.
     */
    public void setEnabled(boolean enabled) {
        if (isEnabled() != enabled) {
            super.setEnabled(enabled);
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child != null)
                    child.setEnabled(enabled);
            }
            setColorBarOpacityIfNeeded();
        }
    }

    /**
     * @deprecated [Not use any longer] This API will no longer be supported in
     *             Sense 5.0
     */
    /** @hide */
    public void enableSectionDivider(boolean enabled) {
    }

    /**
     * if true, <b>common_list_item_background</b> will be used, other wise
     * <b>common_list_item_gradient</b> will be used. if you want to use
     * <b>common_list_item_background_activated</b>, you need to call
     * setBackgroundResource
     * (com.htc.lib1.cc.R.drawable.common_list_item_background_activated)
     * explicitly, use these 3 resources as drawable is not recommended.
     *
     * @param useTexture
     */
    /**
     * @deprecated [Not use any longer]
     */
    /** @hide */
    public void enableTexture(boolean useTexture) {
    }

    /**
     * @deprecated [Not use any longer] This API will no longer be supported in
     *             Sense 5.0
     */
    /** @hide */
    public String getBackgroundMode() {
        return null;
    }

    /**
     * This method will change the background & height of HtcListitem.</br>The
     * M4 in UI guideline will also be applied if an HtcListItemImageComponent
     * is used, otherwise, keep using M2.</br>e.g., using an ImageView instead
     * of HtcListItemTileImage
     *
     * @param enable enable automotive mode
     * @deprecated [Alternative solution] use setAutoMotiveMode(boolean enable,
     *             boolean enableDivider)
     */
    @Deprecated
    public void setAutoMotiveMode(boolean enable) {
        setAutoMotiveMode(enable, true);
    }

    /**
     * This method will change the background & height of HtcListitem.</br> If
     * you use this method in HtcListView, please set enableDivider true. If you
     * use this method in Popup Menu, please set enableDivider false.</br>The M4
     * in UI guideline will also be applied if an HtcListItemImageComponent is
     * used, otherwise, keep using M2.</br>e.g., using an ImageView instead of
     * HtcListItemTileImage
     *
     * @param enable Is automotive mode enabled
     * @param enableDivider Is divider enabled
     */
    public void setAutoMotiveMode(boolean enable, boolean enableDivider) {
        if (enable) {
            if (mMode != MODE_AUTOMOTIVE) {
                mMode = MODE_AUTOMOTIVE;
                initHeights();
            }
        } else {
            mMode = MODE_DEFAULT;
            initHeights();
            enableTexture(true);
        }
        int ncount = getChildCount();
        for (int i = 0; i < ncount; i++) {
            View child = getChildAt(i);
            if (child instanceof IHtcListItemAutoMotiveControl) {
                ((IHtcListItemAutoMotiveControl) child).setAutoMotiveMode(enable);
            }
        }

        initVirticalDivider();
        requestLayout();
        if (HtcDebugFlag.getHtcDebugFlag()) {
            Log.d(LOG_TAG, "setAutoMotiveMode " + enable + ", current mode is " + mMode);
        }
    }

    @ExportedProperty(category = "CommonControl")
    private boolean bColorBar = false;
    @ExportedProperty(category = "CommonControl")
    private int mColorBarWidth = 0;
    @ExportedProperty(category = "CommonControl")
    private boolean isPhotoFrameExist = false;

    /**
     * Use this API to show/hide color bar.
     *
     * @param isColorBarEnabled Is color bar enabled
     */
    public void setColorBarEnabled(boolean isColorBarEnabled) {
        bColorBar = isColorBarEnabled;
        if (bColorBar && mColorBar == null)
            mColorBar = new ColorDrawable();
        setColorBarOpacityIfNeeded();
        if (HtcDebugFlag.getHtcDebugFlag() && isColorBarEnabled) {
            Log.d(LOG_TAG, "ColorBar enabled ");
        }
    }

    /**
     * Use this API to indicate color bar style
     *
     * @param color color of color bar
     * @param isBold Is it unread bar
     * @param isHeightMatchParent Is height of color bar match parent
     */
    public void setColorBarStyle(int color, boolean isBold, boolean isHeightMatchParent) {
        if (mColorBar == null)
            mColorBar = new ColorDrawable();

        if (mColorBar != null)
            mColorBar.setColor(color);

        if (isBold)
            mColorBarWidth = getContext().getResources().getDimensionPixelOffset(
                    com.htc.lib1.cc.R.dimen.htc_list_item_color_bar_bold_width);
        else
            mColorBarWidth = getContext().getResources().getDimensionPixelOffset(
                    com.htc.lib1.cc.R.dimen.htc_list_item_color_bar_width);

        setColorBarOpacityIfNeeded();
    }

    private void setColorBarOpacityIfNeeded() {
        if (!bColorBar || mColorBar == null || mColorBar.getColor() == 0)
            return;

        if (mColorBar.getAlpha() != 255 && isEnabled())
            mColorBar.setAlpha(255);
        else if (mColorBar.getAlpha() != 102 && !isEnabled())
            mColorBar.setAlpha(102);
    }

    @ExportedProperty(category = "CommonControl")
    private boolean mFirstComponentAlign = false;

    /**
     * Use this API to align the first component. The width of first component
     * is set to width of photoframe + M2.
     *
     * @param align Does the first component align
     */
    public void setFirstComponentAlign(boolean align) {
        if (mFirstComponentAlign != align) {
            mFirstComponentAlign = align;
            requestLayout();
        }
        if (HtcDebugFlag.getHtcDebugFlag() && mFirstComponentAlign) {
            Log.d(LOG_TAG, "First component align ");
        }
    }

    @ExportedProperty(category = "CommonControl")
    private boolean mLastComponentAlign = false;

    /**
     * Use this API to align the last component. The width of last component is
     * set to width of 14.7% screen width of portrait mode.
     *
     * @param align Does the last component align
     */
    public void setLastComponentAlign(boolean align) {
        if (mLastComponentAlign != align) {
            mLastComponentAlign = align;
            requestLayout();
        }
        if (HtcDebugFlag.getHtcDebugFlag() && mLastComponentAlign) {
            Log.d(LOG_TAG, "Last component align ");
        }
    }

    @ExportedProperty(category = "CommonControl")
    private boolean bVerticalDivider = false;

    /**
     * Use this API to enable vertical divider. If true, the last component is
     * in the horizontally center of width which occupy 14.7% screen width on
     * portrait mode.
     *
     * @param isVirticalDividerEnabled Is vertical divider enabled
     */
    public void setVerticalDividerEnabled(boolean isVirticalDividerEnabled) {
        bVerticalDivider = isVirticalDividerEnabled;
        setLastComponentAlign(isVirticalDividerEnabled);
        if (HtcDebugFlag.getHtcDebugFlag() && bVerticalDivider) {
            Log.d(LOG_TAG, "Vertical divider enabled ");
        }
    }

    @ExportedProperty(category = "CommonControl")
    private boolean mFixedTopMargin = false;

    /**
     * The top margin of first component is set to fixed. It will only base on
     * default list item height but not final list item height.
     *
     * @param isTopMarginFixed True if the top margin of first component is
     *            fixed, false otherwise.
     */
    public void setFirstComponentTopMarginFixed(boolean isTopMarginFixed) {
        mFixedTopMargin = isTopMarginFixed;
        if (HtcDebugFlag.getHtcDebugFlag() && mFixedTopMargin) {
            Log.d(LOG_TAG, "First component top margin fixed ");
        }
    }

    /**
     * Use this API to set Orientation for Automotive mode. The default value is
     * portrait. If set API setLastComponentAlign to true, The width of the last
     * component is 20% of the screen width on portrait mode and 13% of the
     * screen width on landscape mode.
     *
     * @param isPortrait True if the orientation is portrait, false otherwise.
     */
    public void setPortrait(boolean isPortrait) {
    }

    /**
     * Use this API to enable left indent. if true,the left of HtcListItem will
     * be indented and the space is M2*3
     *
     * @param enable True if enable indent the left of HtcListItem
     */
    public void setLeftIndent(boolean enable) {
        if (enable) {
            setLeftSpace(HtcListItemManager.getLeftIndentSpace(getContext()));
            if (HtcDebugFlag.getHtcDebugFlag()) {
                Log.d(LOG_TAG, "Set left indent");
            }
        } else {
            setLeftSpace(0);
        }
    }

}
