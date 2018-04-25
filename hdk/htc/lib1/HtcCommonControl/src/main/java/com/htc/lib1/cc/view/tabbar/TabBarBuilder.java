package com.htc.lib1.cc.view.tabbar;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.htc.lib1.cc.R;
import com.htc.lib1.cc.util.res.HtcResUtil;
import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcListItem2LineText;
import com.htc.lib1.cc.widget.HtcListItemBubbleCount;
import com.htc.lib1.cc.widget.HtcListView;

/**
 * @hide
 * @deprecated [Module internal use]
 */
public class TabBarBuilder {

    private final SparseArray<TextView>       mWidgetRecycler = new SparseArray<TextView>();
    private final SparseArray<CharSequence> mTabTitleRecycler = new SparseArray<CharSequence>();

    private Context mContext;
    private TabBar.TabAdapter mAdapter;
    private Paint paint = null;
    private int mMaxTitleStringLength = 0;
    private int selected = -1;
    private boolean isCarMode = false;

    public TabBarBuilder(Context context, TabBar.TabAdapter adapter) {
        mContext = context;
        mAdapter = adapter;
        isCarMode = adapter.isAutomotiveMode();
    }

    public void setPrimaryItem(View container, int position, Object object) {
        selected = position;
    }

    public void clear() {
        mWidgetRecycler.clear();
        mTabTitleRecycler.clear();
        mMaxTitleStringLength = 0;
    }

    private CharSequence makeTabBarItemString(int position) {
        CharSequence tab = mTabTitleRecycler.get(position);
        if(tab == null) {
            CharSequence t = mAdapter.getPageTitle(position);
            String label = t == null? null: t.toString();
            int count = mAdapter.getPageCount(position);

            if(paint == null) {
                paint = new Paint();

                Theme theme = mContext.getTheme();
                TypedArray appearance = theme.obtainStyledAttributes(R.style.b_separator_secondary_xl,
                        new int[] {
                        android.R.attr.textSize,
                        android.R.attr.fontFamily,
                        android.R.attr.typeface,
                        android.R.attr.textStyle}
                        );

                if (appearance != null) {
                    int textSize = appearance.getDimensionPixelSize(0, 0);
                    String fontFamily = appearance.getString(1);
                    int typeface = appearance.getInt(2, -1);
                    int textStyle = appearance.getInt(3, -1);

                    if(textSize != 0)
                        paint.setTextSize(textSize);

                    Typeface tf = null;
                    if (fontFamily != null)
                        tf = Typeface.create(fontFamily, textStyle);
                    if (tf == null) {
                        switch (typeface) {
                        case 1: //TextView.SANS:
                            tf = Typeface.create(Typeface.SANS_SERIF, textStyle);
                            break;
                        case 2: //TextView.SERIF:
                            tf = Typeface.create(Typeface.SERIF, textStyle);
                            break;
                        case 3: //TextView.MONOSPACE:
                            tf = Typeface.create(Typeface.MONOSPACE, textStyle);
                            break;
                        }
                    }
                    if (tf != null)
                        paint.setTypeface(tf);

                    appearance.recycle();
                }
            }

            int labelLength = label == null? 0: (int) (paint.measureText(label) + 0.5f);
            int m2 = TabBarUtils.dimen.m2(mContext);

            if(HtcResUtil.isInAllCapsLocale(mContext) && label != null) {
                label = label.toUpperCase();
            }

            if(count > 0) {
                String countString;
                //TODO upper bound always = 100?
                if (count < 100) {
                    countString = "(" + count + ")";
                } else {
                    countString = "(99+)";
                }
                Spannable labelNew = new SpannableString(label + "  " + countString);
                int length = label == null ? 0 : label.length();
                labelNew.setSpan(
                        new ForegroundColorSpan(TabBarUtils.color.category(mContext)),
                        length, length + countString.length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tab = labelNew;

                int countLength = (int) (paint.measureText(countString) + 0.5f);
                mMaxTitleStringLength = Math.max(labelLength + countLength + m2*3, mMaxTitleStringLength);
            } else {
                tab = label;

                mMaxTitleStringLength = Math.max(labelLength + m2*2, mMaxTitleStringLength);
            }

            if(tab != null)
                mTabTitleRecycler.put(position, tab);
        }
        return tab;
    }

    private View getTabBarItem(int position) {
        TextView tv = mWidgetRecycler.get(position);
        if(tv == null) {
            tv = new TextView(mContext) {
                private Drawable mFocusIndicator;
                @Override
                protected void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    if (isFocused()) {
                        drawFocusIndicator(canvas);
                    }
                }

                protected void drawFocusIndicator(Canvas canvas) {
                    if (mFocusIndicator == null) {
                        mFocusIndicator = getContext().getResources().getDrawable(R.drawable.common_focused);
                        mFocusIndicator.mutate();
                        mFocusIndicator.setColorFilter(new PorterDuffColorFilter(TabBarUtils.color.category(getContext()), PorterDuff.Mode.SRC_ATOP));
                    }
                    mFocusIndicator.setBounds(canvas.getClipBounds());
                    mFocusIndicator.draw(canvas);
                }

                @Override
                protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
                    super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
                    invalidate();
                }
            };
            tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            tv.setText(makeTabBarItemString(position)/*, TextView.BufferType.SPANNABLE*/);
            tv.setTextAppearance(mContext, isCarMode? R.style.fixed_automotive_b_separator_primary_s: R.style.b_separator_secondary_xs);
            tv.setSingleLine();
            //20130709 garywu, use 4dp as padding value
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, mContext.getResources().getDisplayMetrics());
            tv.setPadding(padding, 0, padding, 0);
            //naeco: accessibility - TalkBack
            tv.setFocusable(true);
            tv.setContentDescription(tv.getText());

            mWidgetRecycler.put(position, tv);
        }
        return tv;
    }

    private View getPopupBubbleItem(int position, View convertView) {
        HtcListItem item = new HtcListItem(mContext, HtcListItem.MODE_POPUPMENU);
        item.setAutoMotiveMode(isCarMode, true);
        HtcListItem2LineText label = new HtcListItem2LineText(mContext, HtcListItem2LineText.MODE_DARK_LIST);
        item.addView(label);
        HtcListItemBubbleCount count = new HtcListItemBubbleCount(mContext);
        item.addView(count);

        label.setPrimaryText(mAdapter.getPageTitle(position));
        label.setSecondaryTextVisibility(View.GONE);
        //TODO upper bound always = 100?
        count.setUpperBound(100);
        count.setBubbleCount(mAdapter.getPageCount(position));

        if (position == selected && label.getPrimaryTextView() != null) {
            label.getPrimaryTextView().setTextColor(TabBarUtils.color.category(mContext));
        }

        return item;
//        RelativeLayout item = null;
//        if(item == null) {
//            int m2 = TabBarUtils.dimen.m2(mContext);
//
//            RelativeLayout.LayoutParams labelLp = new RelativeLayout.LayoutParams(
//                    RelativeLayout.LayoutParams.WRAP_CONTENT,
//                    RelativeLayout.LayoutParams.MATCH_PARENT);
//
//            TextView label = new TextView(mContext);
//            label.setLayoutParams(labelLp);
//            label.setSingleLine();
//            label.setEllipsize(TruncateAt.END);
//            label.setGravity(Gravity.CENTER_VERTICAL);
//            label.setText(mAdapter.getPageTitle(position));
//
//            int listItemHeight = TabBarUtils.dimen.listItemHeight(mContext, isCarMode);
//
//            item = new RelativeLayout(mContext);
//            item.setLayoutParams(new HtcAbsListView.LayoutParams(
//                    HtcAbsListView.LayoutParams.MATCH_PARENT,
//                    listItemHeight));
//            item.setPadding(m2, 0, m2, 0);
//            item.addView(label);
//
//            //naeco: accessibility - TalkBack
//            item.setContentDescription(label.getText());
//
//            int c = mAdapter.getPageCount(position);
//            if(c > 0) {
//                CharSequence countString = "(" + c + ")";
//                RelativeLayout.LayoutParams countLp = new RelativeLayout.LayoutParams(
//                        RelativeLayout.LayoutParams.WRAP_CONTENT,
//                        RelativeLayout.LayoutParams.MATCH_PARENT);
//                countLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//                countLp.leftMargin = m2;
//
//                labelLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//                labelLp.addRule(RelativeLayout.LEFT_OF, android.R.id.text2);
//
//                TextView count = new TextView(mContext);
//                count.setId(android.R.id.text2);
//                count.setLayoutParams(countLp);
//                count.setGravity(Gravity.CENTER_VERTICAL);
//                count.setText(countString);
//
//                //TODO: car mode
//                count.setTextAppearance(mContext, isCarMode? R.style.automotive_darklist_primary_m: R.style.notification_info_m);
//                count.setTextColor(TabBarUtils.color.overlay(mContext));
//
//                item.addView(count);
//            }
//        }
//
//        // highlight selected tab
//        TextView t = (TextView) item.getChildAt(0);
//        //TODO: car mode
//        t.setTextAppearance(mContext, isCarMode? R.style.automotive_darklist_primary_m: R.style.b_separator_secondary_xl);
//        if(position == selected)
//            t.setTextColor(TabBarUtils.color.overlay(mContext));
//
//        return item;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if(parent instanceof TabBar) {
            return getTabBarItem(position);
        } else if(parent instanceof HtcListView || parent == null) {
            // naeco: ListPopupBubbleWindow.measureContentWidth will set parent to null...
            return getPopupBubbleItem(position, convertView);
        } else {
            //TODO: error
            return null;
        }
    }

    public int getItemViewType(int position) {
        return mAdapter.getPageCount(position) > 0 ? 1 : 0;
    }

    public int getViewTypeCount() {
        return 2;
    }

    public void setAutomotiveMode(boolean enabled) {
        if(isCarMode != enabled) {
            isCarMode = enabled;
            clear();
        }
    }
}
