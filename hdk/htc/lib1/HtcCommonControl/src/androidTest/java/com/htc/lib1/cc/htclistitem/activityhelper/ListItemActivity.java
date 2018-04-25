
package com.htc.lib1.cc.htclistitem.activityhelper;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.widget.HtcCheckBox;
import com.htc.lib1.cc.widget.HtcEditText;
import com.htc.lib1.cc.widget.HtcImageButton;
import com.htc.lib1.cc.widget.HtcListItem1LineCenteredStamp;
import com.htc.lib1.cc.widget.HtcListItem1LineCenteredText;
import com.htc.lib1.cc.widget.HtcListItem1LineTextProgressBar;
import com.htc.lib1.cc.widget.HtcListItem2LineStamp;
import com.htc.lib1.cc.widget.HtcListItem2LineText;
import com.htc.lib1.cc.widget.HtcListItem2LineTextProgressBar;
import com.htc.lib1.cc.widget.HtcListItem7Badges1LineBottomStamp;
import com.htc.lib1.cc.widget.HtcListItem7Badges1LineBottomStamp.OnFlagButtonCheckedChangeListener;
import com.htc.lib1.cc.widget.HtcListItemBubbleCount;
import com.htc.lib1.cc.widget.HtcListItemColorIcon;
import com.htc.lib1.cc.widget.HtcListItemLabeledLayout;
import com.htc.lib1.cc.widget.HtcListItemMessageBody;
import com.htc.lib1.cc.widget.HtcListItemQuickContactBadge;
import com.htc.lib1.cc.widget.HtcListItemReversed2LineText;
import com.htc.lib1.cc.widget.HtcListItemSerialNumber;
import com.htc.lib1.cc.widget.HtcListItemSingleText;
import com.htc.lib1.cc.widget.HtcListItemStockBoard;
import com.htc.lib1.cc.widget.HtcListItemTileImage;
import com.htc.lib1.cc.widget.HtcProgressBar;
import com.htc.lib1.cc.widget.ListItem;
import com.htc.lib1.cc.widget.ListItem.LayoutParams;
import com.htc.lib1.cc.widget.QuickContactBadge;
import com.htc.lib1.cc.test.R;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ListItemActivity extends ActivityBase {
    private final String ACCESSIBILITY_CONTENT_DESCRIPTION = "Should be set by AP";
    private Boolean mIsAutomotiveMode = false;
    private int mSelectIndex = 0;
    private static final int M1 = 0;
    private static final int M2 = 1;
    private static final int M3 = 2;
    private static final int M4 = 3;
    private static final int M5 = 4;
    private static final int M6 = 5;
    private static int[] Margin = new int[6];
    final int layouts[] = new int[] {
            R.layout.list_item01_new,// 0
            R.layout.list_item02_new, R.layout.list_item03_new, // 1
            R.layout.list_item04_new, R.layout.list_item05_new, // 3
            R.layout.list_item06_new, R.layout.list_item07_new, // 5
            R.layout.list_item08_new, R.layout.list_item09_new, // 7
            R.layout.list_item10_new, R.layout.list_item11_new, // 9
            R.layout.list_item12_new, R.layout.list_item13_new, // 11
            R.layout.list_item14_new, R.layout.list_item15_new, // 13
            R.layout.list_item16_new, R.layout.list_item17_new, // 15
            R.layout.list_item18_new, // 17
            R.layout.list_item20_new, R.layout.list_item21_new, // 18
            R.layout.listitem_001_new, R.layout.listitem_001_new, // 20
            R.layout.listitem_002_new, R.layout.listitem_002_new, // 22
            R.layout.list_item31_new, R.layout.list_item32_new, // 24
            R.layout.list_item33_new, // 26
            R.layout.list_item02_new, R.layout.list_item007_new, // 27
            R.layout.list_item018_new, R.layout.list_item01_new, // 29
            R.layout.list_item018_new, R.layout.list_item018_new, // 31
            R.layout.list_item0018_new, R.layout.list_item018_new,// 33
            // HtcListItemMessageBody
            R.layout.list_item19_new, R.layout.list_item19_new, // 35
            R.layout.list_item22_new, R.layout.list_item22_new, // 37
            // progress
            R.layout.list_item18_new, R.layout.list_item27_new, // 39
            R.layout.list_item27_new, R.layout.list_item27_new, // 41
            R.layout.list_item28_new, R.layout.list_item28_new, // 43
            R.layout.list_item29_new, R.layout.list_item29_new, // 45
            R.layout.list_item_1linecenteredstamp_new, R.layout.list_item_labeledlayout_new,
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.color.grey);
        getValueFromIntent();

        initStaticValue(this, mIsAutomotiveMode);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ListItem listItem = (ListItem) inflater.inflate(layouts[mSelectIndex], null);
        initListItem(listItem, mSelectIndex, mIsAutomotiveMode);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.addView(new Button(this));// get focus
        content.addView(listItem);

        setContentView(content);
    }

    private void initListItem(ListItem i, int selectIndex, boolean isAutomotiveMode) {
        i.setId(android.R.id.list);
        i.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        i.setAutoMotiveMode(isAutomotiveMode);
        if (selectIndex == 0) {
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 0, 2text");
            text.setSecondaryText("View.GONE");
            setMargin(text, Margin[M1], 0, Margin[M1], 0);

        } else if (selectIndex == 1) {
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 1, 2text, 2stamp, this is a long text");
            text.setSecondaryTextVisibility(View.GONE);
            setMargin(text, Margin[M1], 0, Margin[M2], 0);

            HtcListItem2LineStamp stamp = (HtcListItem2LineStamp) i.findViewById(R.id.stamp1);
            stamp.setPrimaryText("aaa");
            stamp.setSecondaryTextVisibility(View.GONE);
            stamp.setPadding(0, 0, 0, 0);
            setMargin(stamp, 0, 0, Margin[M1], 0);

        } else if (selectIndex == 2) {
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 2");
            text.setSecondaryText("Text fLing 2, 2text");
            text.setEnabled(true);
            text.setPadding(0, 0, 0, 0);
            setMargin(text, Margin[M1], 0, Margin[M1], 0);

        } else if (selectIndex == 3) {
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 3");
            text.setSecondaryText("Text fLing 3, 2text, 2stamp");
            setMargin(text, Margin[M1], 0, Margin[M2], 0);

            HtcListItem2LineStamp stamp = (HtcListItem2LineStamp) i.findViewById(R.id.stamp1);
            stamp.setPrimaryText("Exchange");
            setMargin(stamp, 0, 0, Margin[M1], 0);

        } else if (selectIndex == 4) {
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 4");
            text.setSecondaryText("Text fLing 4, 2text, stockboard");
            setMargin(text, Margin[M1], 0, Margin[M2], 0);

            HtcListItemStockBoard board = (HtcListItemStockBoard) i.findViewById(R.id.stock);
            GradientDrawable gd = new GradientDrawable();
            gd.setCornerRadius(5);
            gd.setColor(getResources().getColor(R.color.stock_green));
            gd.setStroke(2, getResources().getColor(R.color.stock_gray));
            board.setBoardImageDrawable(gd); // API has not ready.
            board.setFrontText("15.99");
            board.setTextLine(0, "+0.79");
            board.setTextLine(1, "+1.20%");
            board.getBoardDrawable();
            board.getFrontText();
            board.setEnabled(true);
            board.setPadding(0, 0, 0, 0);
            setMargin(board, 0, 0, Margin[M1], 0);

        } else if (selectIndex == 5) {
            HtcListItem1LineCenteredText text = (HtcListItem1LineCenteredText) i
                    .findViewById(R.id.text1);
            text.setTextNoContentStyle();
            text.setText("no content text");
            setMargin(text, Margin[M1], 0, Margin[M1], 0);

        } else if (selectIndex == 6) {
            // i.setLastComponentAlign(true);
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 6, bubble count, image button");
            text.setSecondaryTextVisibility(View.GONE);
            setMargin(text, Margin[M1], 0, 0, 0);

            HtcListItemBubbleCount bubble = (HtcListItemBubbleCount) i.findViewById(R.id.bubble);
            bubble.setBubbleCount(6);
            bubble.setBubbleCount(bubble.getText().toString());
            bubble.setPadding(0, 0, 0, 0);
            bubble.setEnabled(true);
            setMargin(bubble, Margin[M2], 0, 0, 0);

            HtcImageButton button = (HtcImageButton) i.findViewById(R.id.imgButton);
            button.setImageResource(R.drawable.icon_btn_search_light);

        } else if (selectIndex == 7) {
            // i.setVerticalDividerEnabled(true);
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 7,bubble count, image button, vertical divider");
            text.setSecondaryText("Text fLing 2");
            text.setIndicatorResource(R.drawable.icon_indicator_calendar);
            setMargin(text, Margin[M1], 0, Margin[M2], 0);

            HtcListItemBubbleCount bubble = (HtcListItemBubbleCount) i.findViewById(R.id.bubble);
            bubble.setUpperBound(77);
            bubble.setBubbleCount(100000);
            setMargin(bubble, 0, 0, Margin[M2], 0);

            HtcImageButton button = (HtcImageButton) i.findViewById(R.id.imgButton);
            button.setImageResource(R.drawable.icon_btn_search_light);

        } else if (selectIndex == 8) {
            // i.setFirstComponentAlign(true);
            HtcListItemColorIcon image = (HtcListItemColorIcon) i.findViewById(R.id.photo);
            image.setColorIconImageResource(R.drawable.icon_launcher_programs);
            image.setPadding(0, 0, 0, 0);
            image.setColorIconImageDrawable(image.getColorIconDrawable());
            setMargin(image, 0, 0, 0, 0);

            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 8, ColorIcon, 2text, 2stamp");
            text.setSecondaryText("Text fLing 2");
            setMargin(text, 0, 0, Margin[M1], 0);
            if (!mIsAutomotiveMode) {
                image.setLayoutParams(new LayoutParams(LayoutParams.SIZE_WRAP_CONTENT | LayoutParams.CENTER_VERTICAL));
                setMargin(image, Margin[M2], 0, Margin[M2], 0);
            }

        } else if (selectIndex == 9) {
            HtcListItemQuickContactBadge image = (HtcListItemQuickContactBadge) i.findViewById(R.id.photo);
            QuickContactBadge badge = image.getBadge();
            badge.setImageResource(R.drawable.icon_category_photo);
            badge.setContentDescription(ACCESSIBILITY_CONTENT_DESCRIPTION);

            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 9, QuickBadge, 2text");
            text.setSecondaryText("Text fLing 2");
            setMargin(text, Margin[M2], 0, Margin[M1], 0);

        } else if (selectIndex == 10) {
            HtcListItemQuickContactBadge image = (HtcListItemQuickContactBadge) i.findViewById(R.id.photo);
            QuickContactBadge badge = image.getBadge();
            badge.setImageResource(R.drawable.icon_category_photo);
            badge.assignContactFromPhone("0912345678", false);
            badge.setContentDescription(ACCESSIBILITY_CONTENT_DESCRIPTION);

            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 10, QuickBadge, 2text");
            text.setSecondaryTextSingleLine(false);
            text.setSecondaryText("If the photo frame exists, "
                    + "the color bar height is photo frame width. Otherwise, the color bar height is match parent.");
            setMargin(text, Margin[M2], 0, Margin[M1], 0);

        } else if (selectIndex == 11) {
            HtcListItemQuickContactBadge image = (HtcListItemQuickContactBadge) i.findViewById(R.id.photo);
            QuickContactBadge badge = image.getBadge();
            badge.setImageResource(R.drawable.icon_category_photo);
            badge.assignContactFromPhone("0912345678", false);
            badge.setContentDescription(ACCESSIBILITY_CONTENT_DESCRIPTION);

            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 11, QuickBadge, 2text, 2stamp");
            text.setSecondaryTextVisibility(View.GONE);
            setMargin(text, Margin[M2], 0, 0, 0);

            HtcListItem2LineStamp stamp = (HtcListItem2LineStamp) i.findViewById(R.id.stamp1);
            stamp.setPrimaryText("Exchange");
            stamp.setSecondaryTextVisibility(View.GONE);
            setMargin(stamp, Margin[M2], 0, Margin[M1], 0);

        } else if (selectIndex == 12) {

            HtcListItemTileImage image = (HtcListItemTileImage) i.findViewById(R.id.photo);
            image.setTileImageResource(R.drawable.icon_category_photo);
            image.setScaleType(ScaleType.FIT_XY);
            Drawable drawable = image.getTileImageDrawable();
            image.setTileImageDrawable(drawable);
            image.setPadding(0, 0, 0, 0);

            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 12, TileImage, 2text");
            text.setSecondaryText("Text fLing 2, ScaleType.CENTER");
            setMargin(text, Margin[M2], 0, Margin[M1], 0);

        } else if (selectIndex == 13) {
            HtcListItemTileImage image = (HtcListItemTileImage) i.findViewById(R.id.photo);
            image.setTileImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.head));
            image.setTileImageResource(R.drawable.head);

            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 13, TileImage, 2text, 2stamp");
            text.setSecondaryText("Text fLing 2, Dark Mode");
            setMargin(text, Margin[M2], 0, 0, 0);

            HtcListItem2LineStamp stamp = (HtcListItem2LineStamp) i.findViewById(R.id.stamp1);
            stamp.setPrimaryText("Exchange");
            setMargin(stamp, Margin[M2], 0, Margin[M1], 0);

        } else if (selectIndex == 14) {
            HtcListItemTileImage image = (HtcListItemTileImage) i.findViewById(R.id.photo);
            image.setTileImageResource(R.drawable.head);

            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 14, TileImage, 2text, 2stamp");
            text.setSecondaryText("Text fLing 2");
            setMargin(text, Margin[M2], 0, 0, 0);

            HtcListItem2LineStamp stamp = (HtcListItem2LineStamp) i.findViewById(R.id.stamp1);
            stamp.setPrimaryText("Today");
            stamp.setSecondaryText("12:34 AM");
            setMargin(stamp, Margin[M2], 0, Margin[M1], 0);

        } else if (selectIndex == 15) {
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 15, 2text, checkbox");
            text.setSecondaryText("Text fLing 2");
            setMargin(text, Margin[M1], 0, 0, 0);

            final HtcCheckBox checkBox = (HtcCheckBox) i.findViewById(R.id.checkBut);
            if (checkBox != null) checkBox.setChecked(true);

        } else if (selectIndex == 16) {
            // i.setLastComponentAlign(true);
            // i.setVerticalDividerEnabled(true);
            HtcListItemTileImage image = (HtcListItemTileImage) i.findViewById(R.id.photo);
            image.setTileImageResource(R.drawable.head);

            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 16, TileImage, 2text, 2stamp, imagebutton");
            text.setSecondaryText("Text fLing 2");
            setMargin(text, Margin[M2], 0, Margin[M2], 0);

            HtcImageButton button = (HtcImageButton) i.findViewById(R.id.imgButton);
            button.setImageResource(R.drawable.icon_btn_search_light);
        } else if (selectIndex == 17) {
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 17");
            text.setSecondaryText("Text fLing 2, cbox+image, 2text, 7+1");
            setMargin(text, Margin[M1], 0, 0, 0);

            HtcListItem7Badges1LineBottomStamp stamp = (HtcListItem7Badges1LineBottomStamp) i.findViewById(R.id.stamp2);
            stamp.setBadgeImageResource(0, R.drawable.icon_indicator_highpriority);
            stamp.setBadgeImageResource(1, R.drawable.icon_indicator_calendar);
            stamp.setBadgeImageResource(2, R.drawable.icon_indicator_highpriority);
            stamp.setBadgeImageResource(3, R.drawable.icon_indicator_highpriority);
            stamp.setBadgeImageResource(4, R.drawable.icon_indicator_highpriority);
            stamp.setBadgeImageResource(5, R.drawable.icon_indicator_calendar);
            stamp.setBadgeState(0, true);
            stamp.setBadgeState(1, true);
            stamp.setBadgeState(2, true);
            stamp.setBadgeState(3, true);
            stamp.setBadgeState(4, true);
            stamp.setBadgeState(5, true);
            stamp.setBadgeState(6, true);
            stamp.setTextStamp("excellent");
            stamp.setBadgesVerticalCenter(true);
            stamp.setFlagButtonOnCheckedChangeListener(new OnFlagButtonCheckedChangeListener() {
                @Override
                public void onCheckedChanged(
                        HtcListItem7Badges1LineBottomStamp view,
                        boolean isChecked) {
                    Toast.makeText(
                            ListItemActivity.this,
                            isChecked ? "flag button checked "
                                    : "flag button unchecked",
                            Toast.LENGTH_SHORT).show();
                }
            });
            setMargin(stamp, getResources().getDimensionPixelOffset(R.dimen.margin_m), 0, Margin[M1] + Margin[M2] - getResources().getDimensionPixelOffset(R.dimen.margin_m), 0);

        } else if (selectIndex == 18) {
            HtcListItemSingleText text = (HtcListItemSingleText) i.findViewById(R.id.text2);
            text.setText("Text fLing 18, SingleText(max 2 lines), 1stamp, imgbtn. need a long.");
            text.setText("Text fLing 18, SingleText(max 2 lines), 1stamp, imgbtn. need a long text.");
            text.setEnabled(true);
            text.setPadding(0, 0, 0, 0);
            setMargin(text, Margin[M1], 0, 0, 0);

            HtcImageButton button = (HtcImageButton) i.findViewById(R.id.imgButton);
            setMargin(button, Margin[M2], 0, Margin[M1], 0);

        } else if (selectIndex == 19) {
            HtcListItemQuickContactBadge image = (HtcListItemQuickContactBadge) i.findViewById(R.id.photo);
            QuickContactBadge badge = image.getBadge();
            badge.setImageResource(R.drawable.icon_category_photo);
            badge.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    Toast.makeText(ListItemActivity.this, "Ah-Ha",
                            Toast.LENGTH_SHORT).show();
                }
            });
            badge.setContentDescription(ACCESSIBILITY_CONTENT_DESCRIPTION);

            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 19, Click on QuickBadge!");
            text.setSecondaryText("Text fLing 2, quickbadge, 2text, 2stamp. gdsghdghsdghd dgdtgfythydg cghdghtrghdfhtrhjgfjncvnhfdhtr dfghdf");
            text.setSecondaryTextSingleLine(false);
            setMargin(text, Margin[M2], 0, Margin[M2], 0);

            HtcListItem2LineStamp stamp = (HtcListItem2LineStamp) i.findViewById(R.id.stamp1);
            stamp.setPrimaryText("9:12 AM");
            setMargin(stamp, 0, 0, Margin[M1], 0);

        } else if (selectIndex == 20) {
            HtcListItemColorIcon image = (HtcListItemColorIcon) i.findViewById(R.id.photo);
            image.setColorIconImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_launcher_programs));
            setMargin(image, Margin[M1], 0, 0, 0);

            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 20, ColorIcon, 2text");
            text.setSecondaryText("Text fLing 22222222222222222222222222222222222222222222222222222222222222222");
            setMargin(text, Margin[M2], 0, Margin[M1], 0);

        } else if (selectIndex == 21) {
            // i.setFirstComponentTopMarginFixed(true);
            HtcListItemColorIcon image = (HtcListItemColorIcon) i.findViewById(R.id.photo);
            image.setColorIconImageResource(R.drawable.icon_launcher_programs);
            image.setLayoutParams(new LayoutParams(LayoutParams.SIZE_WRAP_CONTENT | LayoutParams.ALIGN_TOP_EDGE));
            setMargin(image, Margin[M1], 0, Margin[M2], 0);

            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 21, ColorIcon, 2text");
            text.setSecondaryTextSingleLine(false);
            text.setSecondaryText("Text fLing 2222222222222222222222222222222222222222222222222222222222222");
            setMargin(text, 0, 0, Margin[M1], 0);

        } else if (selectIndex == 22) {
            HtcListItemSerialNumber number = (HtcListItemSerialNumber) i.findViewById(R.id.number);
            number.setNumber(2);

            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 22, SerialNumber, 2text");
            text.setSecondaryText("default(Bright mode)");
            setMargin(text, Margin[M2], 0, Margin[M1], 0);

        } else if (selectIndex == 23) {
            HtcListItemSerialNumber number = (HtcListItemSerialNumber) i.findViewById(R.id.number);
            number.setNumber(3);
            number.setDarkMode(true);

            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 23, SerialNumber, 2text");
            text.setSecondaryText("Dark mode");
            setMargin(text, Margin[M2], 0, Margin[M1], 0);

        } else if (selectIndex == 24) {
            AutoCompleteTextView text = (AutoCompleteTextView) i.findViewById(R.id.autotext);
            setMargin(text, Margin[M1], 0, Margin[M1], 0);

        } else if (selectIndex == 25) {
            // i.setLastComponentAlign(true);
            HtcEditText text = (HtcEditText) i.findViewById(R.id.autotext);
            setMargin(text, Margin[M1], getResources().getDimensionPixelOffset(R.dimen.margin_m), 0, getResources().getDimensionPixelOffset(R.dimen.margin_m));

            HtcImageButton button = (HtcImageButton) i.findViewById(R.id.imgButton);
            button.setImageResource(R.drawable.icon_btn_search_light);

        } else if (selectIndex == 26) {
            HtcListItemReversed2LineText text = (HtcListItemReversed2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 26, HtcListItemReversed2LineText primary text line");
            text.setSecondaryText("Secondary text line");
            text.getPrimaryTextView();
            text.setEnabled(true);
            setMargin(text, Margin[M1], 0, Margin[M1], 0);

        } else if (selectIndex == 27) {
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 27, 2text, 2stamp, this is a long text");
            text.setSecondaryText("Secondary text line");
            setMargin(text, Margin[M1], 0, 0, 0);

            HtcListItem2LineStamp stamp = (HtcListItem2LineStamp) i.findViewById(R.id.stamp1);
            stamp.setPrimaryText("aaa");
            stamp.setSecondaryText("bbb");
            setMargin(stamp, Margin[M2], 0, Margin[M1], 0);

        } else if (selectIndex == 28) {
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 28, bubble count");
            text.setSecondaryTextVisibility(View.GONE);
            setMargin(text, Margin[M1], 0, 0, 0);

            HtcListItemBubbleCount bubble = (HtcListItemBubbleCount) i.findViewById(R.id.bubble);
            bubble.setBubbleCount(6);
            setMargin(bubble, Margin[M2], 0, Margin[M1], 0);

        } else if (selectIndex == 29) {
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 29");
            text.setSecondaryText("Text fLing 29, BubbleCount");
            setMargin(text, Margin[M1], 0, 0, 0);

            HtcListItem7Badges1LineBottomStamp stamp = (HtcListItem7Badges1LineBottomStamp) i.findViewById(R.id.stamp2);
            stamp.setBubbleCount(6);
            stamp.getStampCoordinatesInfo();
            stamp.getTextStamp();
            stamp.hasFlagButtonOnCheckedChangeListeners();
            stamp.isFlagButtonChecked();
            setMargin(stamp, getResources().getDimensionPixelOffset(R.dimen.margin_m), 0, Margin[M1] + Margin[M2] - getResources().getDimensionPixelOffset(R.dimen.margin_m), 0);

        } else if (selectIndex == 30) {
            // i.setLeftIndent(true);
            i.setStartIndent(true);
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 30, 2text");
            text.setSecondaryTextVisibility(View.GONE);
            setMargin(text, Margin[M1], 0, Margin[M1], 0);

        } else if (selectIndex == 31) {
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 31");
            text.setSecondaryText("Text fLing 31, text + indicator");
            setMargin(text, Margin[M1], 0, 0, 0);

            HtcListItem7Badges1LineBottomStamp stamp = (HtcListItem7Badges1LineBottomStamp) i.findViewById(R.id.stamp2);
            stamp.setBadgeImageResource(0, R.drawable.icon_indicator_calendar);
            stamp.setBadgeState(0, true);
            stamp.setBadgesVerticalCenter(true);
            stamp.setLayoutParams(new LayoutParams(LayoutParams.SIZE_WRAP_CONTENT | LayoutParams.CENTER_VERTICAL));
            setMargin(stamp, getResources().getDimensionPixelOffset(R.dimen.margin_m), 0, Margin[M1] + Margin[M2] - getResources().getDimensionPixelOffset(R.dimen.margin_m), 0);

        } else if (selectIndex == 32) {
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 32");
            text.setSecondaryText("Text fLing 32, text + indicator+ indicator");
            setMargin(text, Margin[M1], 0, 0, 0);

            HtcListItem7Badges1LineBottomStamp stamp = (HtcListItem7Badges1LineBottomStamp) i.findViewById(R.id.stamp2);
            stamp.setBadgeImageResource(0, R.drawable.icon_indicator_calendar);
            stamp.setBadgeState(0, true);
            stamp.setBadgeImageResource(1, R.drawable.icon_indicator_calendar);
            stamp.setBadgeState(1, true);
            stamp.setBadgesVerticalCenter(true);
            stamp.setLayoutParams(new LayoutParams(LayoutParams.SIZE_WRAP_CONTENT | LayoutParams.CENTER_VERTICAL));
            setMargin(stamp, getResources().getDimensionPixelOffset(R.dimen.margin_m), 0, Margin[M1] + Margin[M2] - getResources().getDimensionPixelOffset(R.dimen.margin_m), 0);

        } else if (selectIndex == 33) {
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 33");
            text.setSecondaryText(" 33, text + indicator+ control");
            setMargin(text, Margin[M1], 0, Margin[M2], 0);

            ImageView imageView = (ImageView) i.findViewById(R.id.imageView);
            imageView.setImageResource(R.drawable.icon_indicator_calendar);
            // i.setLastComponentAlign(true);

            HtcImageButton button = (HtcImageButton) i.findViewById(R.id.imgButton);
            button.setImageResource(R.drawable.icon_btn_search_light);
        } else if (selectIndex == 34) {
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 34");
            text.setSecondaryText("Text fLing 34, text + indicator+ indicator");
            setMargin(text, Margin[M1], 0, 0, 0);

            HtcListItem7Badges1LineBottomStamp stamp = (HtcListItem7Badges1LineBottomStamp) i.findViewById(R.id.stamp2);
            stamp.setBadgeImageResource(0, R.drawable.icon_indicator_calendar);
            stamp.setBadgeState(0, true);
            stamp.setTextStamp("stamp");
            setMargin(stamp, getResources().getDimensionPixelOffset(R.dimen.margin_m), 0, Margin[M1] + Margin[M2] - getResources().getDimensionPixelOffset(R.dimen.margin_m), 0);

        } else if (selectIndex == 35) {
            // i.setFirstComponentTopMarginFixed(true);
            HtcCheckBox cb = (HtcCheckBox) i.findViewById(R.id.checkImgBut);
            setMargin(cb, Margin[M1], 0, Margin[M2], 0);

            HtcListItemMessageBody message = (HtcListItemMessageBody) i.findViewById(R.id.message);
            message.get7Badges1LineBottomStamp().setBadgeImageResource(0, R.drawable.icon_indicator_highpriority);
            message.get7Badges1LineBottomStamp().setBadgeImageResource(1, R.drawable.icon_indicator_calendar);
            message.get7Badges1LineBottomStamp().setBadgeState(0, true);
            message.get7Badges1LineBottomStamp().setBadgeState(1, true);
            message.get7Badges1LineBottomStamp().setBadgeState(6, true);
            message.get7Badges1LineBottomStamp().setTextStamp("STAMP");
            message.setPrimaryText("Text fLing 35");
            message.setSecondaryText("Text fLing 35");
            message.setBodyText("BodyText BodyText BodyText BodyText BodyText BodyText BodyText BodyText");
            message.setBodyTextLine(2);
            message.enableBodyText(false);
            setMargin(message, 0, 0, 0, 0);

        } else if (selectIndex == 36) {
            // i.setFirstComponentTopMarginFixed(true);
            HtcCheckBox cb = (HtcCheckBox) i.findViewById(R.id.checkImgBut);
            setMargin(cb, Margin[M1], 0, Margin[M2], 0);

            HtcListItemMessageBody message = (HtcListItemMessageBody) i.findViewById(R.id.message);
            message.setPrimaryText("Text fLing 36, a very very very long title here");
            message.setSecondaryText("Text fLing 36, a very very very long content here");
            message.setBodyText("BodyText. get ready for the next battle. get ready for the next battle. get ready for the next battle. get ready for the next battle. get ready for the next battle. ");
            message.setBodyTextLine(2);
            message.get7Badges1LineBottomStamp().setBadgeImageResource(0, R.drawable.icon_indicator_highpriority);
            message.get7Badges1LineBottomStamp().setBadgeImageResource(1, R.drawable.icon_indicator_calendar);
            message.get7Badges1LineBottomStamp().setBadgeImageResource(2, R.drawable.icon_indicator_highpriority);
            message.get7Badges1LineBottomStamp().setBadgeImageResource(3, R.drawable.icon_indicator_highpriority);
            message.get7Badges1LineBottomStamp().setBadgeImageResource(4, R.drawable.icon_indicator_highpriority);
            message.get7Badges1LineBottomStamp().setBadgeImageResource(5, R.drawable.icon_indicator_calendar);
            message.get7Badges1LineBottomStamp().setBadgeState(0, true);
            message.get7Badges1LineBottomStamp().setBadgeState(1, true);
            message.get7Badges1LineBottomStamp().setBadgeState(6, true);
            message.get7Badges1LineBottomStamp().setTextStamp("STAMP");
            setMargin(message, 0, 0, 0, 0);

        } else if (selectIndex == 37) {
            HtcListItemQuickContactBadge image = (HtcListItemQuickContactBadge) i.findViewById(R.id.photo);
            QuickContactBadge badge = image.getBadge();
            badge.setImageResource(R.drawable.icon_category_photo);
            badge.setContentDescription(ACCESSIBILITY_CONTENT_DESCRIPTION);

            HtcListItemMessageBody message = (HtcListItemMessageBody) i.findViewById(R.id.message);
            message.setPrimaryText("Text fLing 37");
            message.setSecondaryText("QuickContactBadge has problem in handling mask");
            message.setBodyText("BodyText BodyText BodyText BodyText BodyText BodyText BodyText BodyText");
            message.setBodyTextLine(2);
            message.enableBodyText(false);
            setMargin(message, Margin[M2], 0, 0, 0);
            // error

        } else if (selectIndex == 38) {
            HtcListItemMessageBody message = (HtcListItemMessageBody) i.findViewById(R.id.message);
            message.setPrimaryText("Text fLing 38 primary");
            message.setSecondaryText("Text fLing 38 secondary");
            message.setBodyText("BodyText BodyText BodyText BodyText123456 BodyText BodyText BodyText BodyText123456");
            message.setBodyTextMaxLines(2);
            setMargin(message, Margin[M2], 0, 0, 0);

            HtcListItemQuickContactBadge image = (HtcListItemQuickContactBadge) i.findViewById(R.id.photo);
            QuickContactBadge badge = image.getBadge();
            badge.setImageResource(R.drawable.icon_category_photo);
            badge.setContentDescription(ACCESSIBILITY_CONTENT_DESCRIPTION);

        } else if (selectIndex == 39) {
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 39");
            text.setPrimaryTextStyle(com.htc.lib1.cc.R.style.list_primary_m_bold);
            text.setSecondaryText("Text fLing 39, cbox+image, 2text, 7+1");
            text.setSecondaryTextStyle(com.htc.lib1.cc.R.style.list_secondary_m);
            setMargin(text, Margin[M1], 0, 0, 0);

            HtcListItem7Badges1LineBottomStamp stamp = (HtcListItem7Badges1LineBottomStamp) i.findViewById(R.id.stamp2);
            stamp.setBadgeState(0, true);
            stamp.setBadgeImageResource(1, R.drawable.common_icon_callhistory_misscall);
            stamp.setBadgeState(1, true);
            stamp.setBadgeImageResource(2, R.drawable.icon_indicator_calendar);
            stamp.setBadgeState(2, true);
            stamp.setBadgeImageResource(3, R.drawable.icon_indicator_highpriority);
            stamp.setBadgeImageResource(4, R.drawable.icon_indicator_highpriority);
            stamp.setBadgeState(4, false);
            stamp.setBadgeImageResource(5, R.drawable.icon_indicator_highpriority);
            stamp.setBadgeState(5, true);
            stamp.setBadgeState(6, true);
            stamp.setBadgeState(7, true);
            stamp.setTextStamp("3/4/10");
            setMargin(stamp, Margin[M2], 0, Margin[M1], 0);

        } else if (selectIndex == 40) {
            HtcListItem1LineCenteredText text = (HtcListItem1LineCenteredText) i.findViewById(R.id.centered_text);
            text.setText("Text fLing 40, default centeredText.");
            setMargin(text, Margin[M1], 0, Margin[M1], 0);

        } else if (selectIndex == 41) {
            HtcListItem1LineCenteredText text = (HtcListItem1LineCenteredText) i.findViewById(R.id.centered_text);
            text.setGravityCenterHorizontal(true);
            text.setText("Text fLing 41, centeredText.");
            setMargin(text, Margin[M1], 0, Margin[M1], 0);

        } else if (selectIndex == 42) {
            HtcListItem1LineCenteredText text = (HtcListItem1LineCenteredText) i.findViewById(R.id.centered_text);
            text.setText("Text fLing 42, centeredText.");
            ProgressBar pb = new ProgressBar(this, null, android.R.attr.progressBarStyleSmall);
            pb.setVisibility(View.INVISIBLE);
            text.setView(pb);
            setMargin(text, Margin[M1], 0, Margin[M1], 0);

        } else if (selectIndex == 43) {
            HtcListItemColorIcon image = (HtcListItemColorIcon) i.findViewById(R.id.photo);
            image.setColorIconImageResource(R.drawable.icon_launcher_programs);
            setMargin(image, Margin[M1], 0, Margin[M2], 0);

            HtcListItem2LineTextProgressBar progress = (HtcListItem2LineTextProgressBar) i.findViewById(R.id.progress);
            progress.setPrimaryText("Text fLing 43, 2LineTextProgressBar");
            progress.setSecondaryText("Text2");
            progress.setSecondaryStampText(progress.getSecondaryStampCharSequence());
            progress.setSecondaryStampTextVisibility(View.GONE);
            progress.setPadding(0, 0, 0, 0);
            setMargin(progress, 0, 0, Margin[M1], 0);

        } else if (selectIndex == 44) {
            // i.setFirstComponentTopMarginFixed(true);
            HtcListItemColorIcon image = (HtcListItemColorIcon) i.findViewById(R.id.photo);
            image.setColorIconImageResource(R.drawable.icon_launcher_programs);
            setMargin(image, Margin[M1], 0, Margin[M2], 0);

            HtcListItem2LineTextProgressBar progress = (HtcListItem2LineTextProgressBar) i.findViewById(R.id.progress);
            progress.setPrimaryText("Text fLing 44, 2LineTextProgressBar");
            progress.setSecondaryText("Text2");
            progress.setSecondaryStampText(progress.getSecondaryStampText());
            progress.setSecondaryStampTextVisibility(View.GONE);
            HtcProgressBar mBar = (HtcProgressBar) progress.getProgressBar();
            mBar.setProgress(50);
            setMargin(progress, 0, 0, Margin[M1], 0);

        } else if (selectIndex == 45) {
            HtcListItemColorIcon image = (HtcListItemColorIcon) i.findViewById(R.id.photo);
            image.setColorIconImageResource(R.drawable.icon_launcher_programs);
            setMargin(image, Margin[M1], 0, Margin[M2], 0);

            HtcListItem1LineTextProgressBar progress = (HtcListItem1LineTextProgressBar) i.findViewById(R.id.progress);
            progress.setPrimaryText("Text fLing 45, 1LineTextProgressBar");
            progress.getPrimaryCharSequence();
            progress.getPrimaryText();
            progress.getStampCharSequence();
            progress.getStampText();
            HtcProgressBar mBar = (HtcProgressBar) progress.getProgressBar();
            mBar.setProgress(50);
            setMargin(progress, 0, 0, Margin[M2], 0);

            HtcImageButton button = (HtcImageButton) i.findViewById(R.id.imgButton);
            setMargin(button, 0, 0, Margin[M1], 0);

        } else if (selectIndex == 46) {
            // i.setVerticalDividerEnabled(true);
            HtcListItemColorIcon image = (HtcListItemColorIcon) i.findViewById(R.id.photo);
            image.setColorIconImageResource(R.drawable.icon_launcher_programs);
            setMargin(image, Margin[M1], 0, Margin[M2], 0);

            HtcListItem1LineTextProgressBar progress = (HtcListItem1LineTextProgressBar) i.findViewById(R.id.progress);
            progress.setPrimaryText("Text fLing 46, 1LineTextProgressBar");
            HtcProgressBar mBar = (HtcProgressBar) progress.getProgressBar();
            mBar.setProgress(50);
            progress.setStampText("Stamp");
            setMargin(progress, 0, 0, Margin[M2], 0);

            HtcImageButton button = (HtcImageButton) i.findViewById(R.id.imgButton);
            button.setImageResource(R.drawable.icon_btn_search_light);
            button.setLayoutParams(new LayoutParams(LayoutParams.SIZE_147 | LayoutParams.DIVIDER_START | LayoutParams.CENTER_VERTICAL));

        } else if (selectIndex == 47) {
            HtcListItem1LineCenteredText text = (HtcListItem1LineCenteredText) i.findViewById(R.id.centered_text);
            text.setText("Text fLing 40, default centeredText.");
            setMargin(text, Margin[M1], 0, 0, 0);

            HtcListItem1LineCenteredStamp stamp = (HtcListItem1LineCenteredStamp) i.findViewById(R.id.centered_stamp);
            stamp.setText("stamp");
            setMargin(stamp, Margin[M2], 0, Margin[M1], 0);

        } else if (selectIndex == 48) {
            HtcListItemLabeledLayout labeledLayout = (HtcListItemLabeledLayout) i.findViewById(R.id.text);
            labeledLayout.setLabelText(R.string.description_text);
        }
    }

    @Override
    protected boolean isInitOrientation() {
        return false;
    }

    private void getValueFromIntent() {
        Intent i = getIntent();
        if (null == i) {
            return;
        }
        mIsAutomotiveMode = i.getBooleanExtra("isAutomotiveMode", false);
        mSelectIndex = i.getIntExtra("itemIndex", 0);
    }

    private static void initStaticValue(Context c, boolean isAutomotiveMode) {
        Resources res = c.getResources();
        Margin[M1] = res.getDimensionPixelOffset(R.dimen.margin_l);
        Margin[M2] = isAutomotiveMode ? Margin[M1] : res.getDimensionPixelOffset(R.dimen.margin_m);
        Margin[M3] = res.getDimensionPixelOffset(R.dimen.margin_s);
        Margin[M4] = res.getDimensionPixelOffset(R.dimen.margin_xs);
        Margin[M5] = res.getDimensionPixelOffset(R.dimen.spacing);
        Margin[M6] = res.getDimensionPixelOffset(R.dimen.leading);
    }

    private void setMargin(View view, int start, int top, int end, int bottom) {
        LayoutParams lp = (LayoutParams) view.getLayoutParams();
        lp.setMargins(start, top, end, bottom);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            lp.setMarginEnd(end);
            lp.setMarginStart(start);
        }
    }

}
