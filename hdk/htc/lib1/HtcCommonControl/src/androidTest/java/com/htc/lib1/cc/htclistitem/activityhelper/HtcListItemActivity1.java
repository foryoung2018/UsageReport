
package com.htc.lib1.cc.htclistitem.activityhelper;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.widget.HtcCheckBox;
import com.htc.lib1.cc.widget.HtcIconButton;
import com.htc.lib1.cc.widget.HtcImageButton;
import com.htc.lib1.cc.widget.HtcListItem;
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
import com.htc.lib1.cc.widget.HtcListItemSeparator;
import com.htc.lib1.cc.widget.HtcListItemSerialNumber;
import com.htc.lib1.cc.widget.HtcListItemSingleText;
import com.htc.lib1.cc.widget.HtcListItemStockBoard;
import com.htc.lib1.cc.widget.HtcListItemTileImage;
import com.htc.lib1.cc.widget.HtcProgressBar;
import com.htc.lib1.cc.widget.QuickContactBadge;
import com.htc.lib1.cc.test.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class HtcListItemActivity1 extends ActivityBase {
    private final String ACCESSIBILITY_CONTENT_DESCRIPTION = "Should be set by AP";
    private Boolean mIsAutomotiveMode = false;
    private int mSelectIndex = 0;
    private static final int layouts[] = new int[] {
            R.layout.list_item01,// 0
            R.layout.list_item02, R.layout.list_item03, // 1
            R.layout.list_item04, R.layout.list_item05, // 3
            R.layout.list_item06, R.layout.list_item07, // 5
            R.layout.list_item08, R.layout.list_item09, // 7
            R.layout.list_item10, R.layout.list_item11, // 9
            R.layout.list_item12, R.layout.list_item13, // 11
            R.layout.list_item14, R.layout.list_item15, // 13
            R.layout.list_item16, R.layout.list_item17, // 15
            R.layout.list_item18, // 17
            R.layout.list_item20, R.layout.list_item21, // 18
            R.layout.listitem_001, R.layout.listitem_001, // 20
            R.layout.listitem_002, R.layout.listitem_002, // 22
            R.layout.list_item31, R.layout.list_item32, // 24
            R.layout.list_item33, // 26
            R.layout.list_item02, R.layout.list_item007, // 27
            R.layout.list_item018, R.layout.list_item01, // 29
            R.layout.list_item018, R.layout.list_item018, // 31
            R.layout.list_item0018, R.layout.list_item018,// 33
            R.layout.list_item19, R.layout.list_item19, // 35
            R.layout.list_item22, R.layout.list_item22, // 37
            R.layout.list_item18, R.layout.list_item27, // 39
            R.layout.list_item27, R.layout.list_item27, // 41
            R.layout.list_item28, R.layout.list_item28, // 43
            R.layout.list_item29, R.layout.list_item29, // 45
            R.layout.list_item_1linecenteredstamp, R.layout.list_item_labeledlayout,
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.color.grey);
        getValueFromIntent();

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        HtcListItem listItem = (HtcListItem) inflater.inflate(layouts[mSelectIndex], null);
        initHtcListItem(listItem, mSelectIndex, mIsAutomotiveMode);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.addView(new Button(this));//get focus
        content.addView(listItem);

        setContentView(content);
    }

    private void initHtcListItem(HtcListItem i, int selectIndex, boolean isAutomotiveMode) {
        i.setId(android.R.id.list);
        i.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        i.setAutoMotiveMode(isAutomotiveMode, true);

        if (selectIndex == 0) {
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 0, 2text");
            text.setSecondaryText("View.GONE");

        } else if (selectIndex == 1) {
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 1, 2text, 2stamp, this is a long text");
            text.setSecondaryTextVisibility(View.GONE);

            HtcListItem2LineStamp stamp = (HtcListItem2LineStamp) i.findViewById(R.id.stamp1);
            stamp.setPrimaryText("aaa");
            stamp.setSecondaryTextVisibility(View.GONE);
            stamp.setPadding(0, 0, 0, 0);

        } else if (selectIndex == 2) {
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 2");
            text.setSecondaryText("Text fLing 2, 2text");
            text.setEnabled(true);
            text.setPadding(0, 0, 0, 0);

        } else if (selectIndex == 3) {
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 3");
            text.setSecondaryText("Text fLing 3, 2text, 2stamp");

            HtcListItem2LineStamp stamp = (HtcListItem2LineStamp) i.findViewById(R.id.stamp1);
            stamp.setPrimaryText("Exchange");

        } else if (selectIndex == 4) {
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 4");
            text.setSecondaryText("Text fLing 4, 2text, stockboard");

            HtcListItemStockBoard board = (HtcListItemStockBoard) i.findViewById(R.id.stock);
            GradientDrawable gd = new GradientDrawable();
            gd.setCornerRadius(5);
            gd.setColor(getResources().getColor(R.color.stock_green));
            gd.setStroke(2, getResources().getColor(R.color.stock_gray));
            board.setBoardImageDrawable(gd);
            board.setFrontText("15.99");
            board.setTextLine(0, "+0.79");
            board.setTextLine(1, "+1.20%");
            board.getBoardDrawable();
            board.getFrontText();
            board.setEnabled(true);
            board.setPadding(0, 0, 0, 0);

        } else if (selectIndex == 5) {
            HtcListItem1LineCenteredText text = (HtcListItem1LineCenteredText) i.findViewById(R.id.text1);
            text.setTextNoContentStyle();
            text.setText("no content text");

        } else if (selectIndex == 6) {
            i.setLastComponentAlign(true);

            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 6, bubble count, image button");
            text.setSecondaryTextVisibility(View.GONE);

            HtcListItemBubbleCount bubble = (HtcListItemBubbleCount) i.findViewById(R.id.bubble);
            bubble.setBubbleCount(6);
            bubble.setBubbleCount(bubble.getText().toString());
            bubble.setPadding(0, 0, 0, 0);
            bubble.setEnabled(true);
            HtcImageButton button = (HtcImageButton) i
                    .findViewById(R.id.imgButton);
            button.setImageResource(R.drawable.icon_btn_search_light);

        } else if (selectIndex == 7) {
            i.setVerticalDividerEnabled(true);

            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 7,bubble count, image button, vertical divider");
            text.setSecondaryText("Text fLing 2");
            text.setIndicatorResource(R.drawable.icon_indicator_calendar);

            HtcListItemBubbleCount bubble = (HtcListItemBubbleCount) i.findViewById(R.id.bubble);
            bubble.setUpperBound(77);
            bubble.setBubbleCount(100000);

            HtcImageButton button = (HtcImageButton) i.findViewById(R.id.imgButton);
            button.setImageResource(R.drawable.icon_btn_search_light);

        } else if (selectIndex == 8) {
            i.setFirstComponentAlign(true);

            HtcListItemColorIcon image = (HtcListItemColorIcon) i.findViewById(R.id.photo);
            image.setColorIconImageResource(R.drawable.icon_launcher_programs);
            image.setPadding(0, 0, 0, 0);
            image.setColorIconImageDrawable(image.getColorIconDrawable());

            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 8, ColorIcon, 2text, 2stamp");
            text.setSecondaryText("Text fLing 2");

        } else if (selectIndex == 9) {
            HtcListItemQuickContactBadge image = (HtcListItemQuickContactBadge) i.findViewById(R.id.photo);
            QuickContactBadge badge = image.getBadge();
            badge.setImageResource(R.drawable.icon_category_photo);
            badge.setContentDescription(ACCESSIBILITY_CONTENT_DESCRIPTION);

            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 9, QuickBadge, 2text");
            text.setSecondaryText("Text fLing 2");

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

        } else if (selectIndex == 11) {
            HtcListItemQuickContactBadge image = (HtcListItemQuickContactBadge) i.findViewById(R.id.photo);
            QuickContactBadge badge = image.getBadge();
            badge.setImageResource(R.drawable.icon_category_photo);
            badge.assignContactFromPhone("0912345678", false);
            badge.setContentDescription(ACCESSIBILITY_CONTENT_DESCRIPTION);

            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 11, QuickBadge, 2text, 2stamp");
            text.setSecondaryTextVisibility(View.GONE);

            HtcListItem2LineStamp stamp = (HtcListItem2LineStamp) i.findViewById(R.id.stamp1);
            stamp.setPrimaryText("Exchange");
            stamp.setSecondaryTextVisibility(View.GONE);

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

        } else if (selectIndex == 13) {
            HtcListItemTileImage image = (HtcListItemTileImage) i.findViewById(R.id.photo);
            image.setTileImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.head));
            image.setTileImageResource(R.drawable.head);

            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 13, TileImage, 2text, 2stamp");
            text.setSecondaryText("Text fLing 2, Dark Mode");

            HtcListItem2LineStamp stamp = (HtcListItem2LineStamp) i.findViewById(R.id.stamp1);
            stamp.setPrimaryText("Exchange");

        } else if (selectIndex == 14) {
            HtcListItemTileImage image = (HtcListItemTileImage) i.findViewById(R.id.photo);
            image.setTileImageResource(R.drawable.head);

            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 14, TileImage, 2text, 2stamp");
            text.setSecondaryText("Text fLing 2");

            HtcListItem2LineStamp stamp = (HtcListItem2LineStamp) i.findViewById(R.id.stamp1);
            stamp.setPrimaryText("Today");
            stamp.setSecondaryText("12:34 AM");

        } else if (selectIndex == 15) {
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 15, 2text, checkbox");
            text.setSecondaryText("Text fLing 2");

            final HtcCheckBox checkBox = (HtcCheckBox) i.findViewById(R.id.checkBut);
            if (checkBox != null) checkBox.setChecked(true);
            i.setLastComponentAlign(true);

        } else if (selectIndex == 16) {
            i.setLastComponentAlign(true);
            i.setVerticalDividerEnabled(true);

            HtcListItemTileImage image = (HtcListItemTileImage) i.findViewById(R.id.photo);
            image.setTileImageResource(R.drawable.head);

            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 16, TileImage, 2text, 2stamp, imagebutton");
            text.setSecondaryText("Text fLing 2");

            HtcImageButton button = (HtcImageButton) i.findViewById(R.id.imgButton);
            button.setImageResource(R.drawable.icon_btn_search_light);

        } else if (selectIndex == 17) {
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 17");
            text.setSecondaryText("Text fLing 2, cbox+image, 2text, 7+1");

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
                            HtcListItemActivity1.this,
                            isChecked ? "flag button checked "
                                    : "flag button unchecked",
                            Toast.LENGTH_SHORT).show();
                }
            });

        } else if (selectIndex == 18) {
            HtcListItemSingleText text = (HtcListItemSingleText) i.findViewById(R.id.text2);
            text.setText("Text fLing 18, SingleText(max 2 lines), 1stamp, imgbtn. need a long.");
            text.setText("Text fLing 18, SingleText(max 2 lines), 1stamp, imgbtn. need a long text.");
            text.setEnabled(true);
            text.setPadding(0, 0, 0, 0);

        } else if (selectIndex == 19) {
            HtcListItemQuickContactBadge image = (HtcListItemQuickContactBadge) i.findViewById(R.id.photo);
            QuickContactBadge badge = image.getBadge();
            badge.setImageResource(R.drawable.icon_category_photo);
            badge.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    Toast.makeText(HtcListItemActivity1.this, "Ah-Ha", Toast.LENGTH_SHORT).show();
                }
            });
            badge.setContentDescription(ACCESSIBILITY_CONTENT_DESCRIPTION);

            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 19, Click on QuickBadge!");
            text.setSecondaryText("Text fLing 2, quickbadge, 2text, 2stamp. gdsghdghsdghd dgdtgfythydg cghdghtrghdfhtrhjgfjncvnhfdhtr dfghdf");
            text.setSecondaryTextSingleLine(false);

            HtcListItem2LineStamp stamp = (HtcListItem2LineStamp) i.findViewById(R.id.stamp1);
            stamp.setPrimaryText("9:12 AM");

        } else if (selectIndex == 20) {
            HtcListItemColorIcon image = (HtcListItemColorIcon) i.findViewById(R.id.photo);
            image.setColorIconImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_launcher_programs));

            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 20, ColorIcon, 2text");
            text.setSecondaryText("Text fLing 22222222222222222222222222222222222222222222222222222222222222222");

        } else if (selectIndex == 21) {
            i.setFirstComponentTopMarginFixed(true);

            HtcListItemColorIcon image = (HtcListItemColorIcon) i.findViewById(R.id.photo);
            image.setColorIconImageResource(R.drawable.icon_launcher_programs);

            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 21, ColorIcon, 2text");
            text.setSecondaryTextSingleLine(false);
            text.setSecondaryText("Text fLing 2222222222222222222222222222222222222222222222222222222222222");

        } else if (selectIndex == 22) {
            HtcListItemSerialNumber number = (HtcListItemSerialNumber) i.findViewById(R.id.number);
            number.setNumber(2);

            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 22, SerialNumber, 2text");
            text.setSecondaryText("default(Bright mode)");

        } else if (selectIndex == 23) {
            HtcListItemSerialNumber number = (HtcListItemSerialNumber) i.findViewById(R.id.number);
            number.setNumber(3);
            number.setDarkMode(true);

            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 23, SerialNumber, 2text");
            text.setSecondaryText("Dark mode");

        } else if (selectIndex == 24) {

        } else if (selectIndex == 25) {
            i.setLastComponentAlign(true);

            HtcImageButton button = (HtcImageButton) i.findViewById(R.id.imgButton);
            button.setImageResource(R.drawable.icon_btn_search_light);

        } else if (selectIndex == 26) {
            HtcListItemReversed2LineText text = (HtcListItemReversed2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 26, HtcListItemReversed2LineText primary text line");
            text.setSecondaryText("Secondary text line");
            text.getPrimaryTextView();
            text.setEnabled(true);

        } else if (selectIndex == 27) {
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 27, 2text, 2stamp, this is a long text");
            text.setSecondaryText("Secondary text line");

            HtcListItem2LineStamp stamp = (HtcListItem2LineStamp) i.findViewById(R.id.stamp1);
            stamp.setPrimaryText("aaa");
            stamp.setSecondaryText("bbb");

        } else if (selectIndex == 28) {
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 28, bubble count");
            text.setSecondaryTextVisibility(View.GONE);

            HtcListItemBubbleCount bubble = (HtcListItemBubbleCount) i.findViewById(R.id.bubble);
            bubble.setBubbleCount(6);

        } else if (selectIndex == 29) {
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 29");
            text.setSecondaryText("Text fLing 29, BubbleCount");

            HtcListItem7Badges1LineBottomStamp stamp = (HtcListItem7Badges1LineBottomStamp) i.findViewById(R.id.stamp2);
            stamp.setBubbleCount(6);
            stamp.getStampCoordinatesInfo();
            stamp.getTextStamp();
            stamp.hasFlagButtonOnCheckedChangeListeners();
            stamp.isFlagButtonChecked();

        } else if (selectIndex == 30) {
            i.setLeftIndent(true);
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 30, 2text");
            text.setSecondaryTextVisibility(View.GONE);

        } else if (selectIndex == 31) {
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 31");
            text.setSecondaryText("Text fLing 31, text + indicator");

            HtcListItem7Badges1LineBottomStamp stamp = (HtcListItem7Badges1LineBottomStamp) i.findViewById(R.id.stamp2);
            stamp.setBadgeImageResource(0, R.drawable.icon_indicator_calendar);
            stamp.setBadgeState(0, true);
            stamp.setBadgesVerticalCenter(true);

        } else if (selectIndex == 32) {
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 32");
            text.setSecondaryText("Text fLing 32, text + indicator+ indicator");

            HtcListItem7Badges1LineBottomStamp stamp = (HtcListItem7Badges1LineBottomStamp) i.findViewById(R.id.stamp2);
            stamp.setBadgeImageResource(0, R.drawable.icon_indicator_calendar);
            stamp.setBadgeState(0, true);
            stamp.setBadgeImageResource(1, R.drawable.icon_indicator_calendar);
            stamp.setBadgeState(1, true);
            stamp.setBadgesVerticalCenter(true);

        } else if (selectIndex == 33) {
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 33");
            text.setSecondaryText(" 33, text + indicator+ control");

            ImageView imageView = (ImageView) i.findViewById(R.id.imageView);
            imageView.setImageResource(R.drawable.icon_indicator_calendar);
            i.setLastComponentAlign(true);

            HtcImageButton button = (HtcImageButton) i.findViewById(R.id.imgButton);
            button.setImageResource(R.drawable.icon_btn_search_light);

        } else if (selectIndex == 34) {
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 34");
            text.setSecondaryText("Text fLing 34, text + indicator+ indicator");

            HtcListItem7Badges1LineBottomStamp stamp = (HtcListItem7Badges1LineBottomStamp) i.findViewById(R.id.stamp2);
            stamp.setBadgeImageResource(0, R.drawable.icon_indicator_calendar);
            stamp.setBadgeState(0, true);
            stamp.setTextStamp("stamp");

        } else if (selectIndex == 35) {
            i.setFirstComponentTopMarginFixed(true);

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

        } else if (selectIndex == 36) {
            i.setFirstComponentTopMarginFixed(true);

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

        } else if (selectIndex == 38) {
            HtcListItemQuickContactBadge image = (HtcListItemQuickContactBadge) i.findViewById(R.id.photo);
            QuickContactBadge badge = image.getBadge();
            badge.setImageResource(R.drawable.icon_category_photo);
            badge.setContentDescription(ACCESSIBILITY_CONTENT_DESCRIPTION);

            HtcListItemMessageBody message = (HtcListItemMessageBody) i.findViewById(R.id.message);
            message.setPrimaryText("Text fLing 38 primary");
            message.setSecondaryText("Text fLing 38 secondary");
            message.setBodyText("BodyText BodyText BodyText BodyText123456 BodyText BodyText BodyText BodyText123456");
            message.setBodyTextMaxLines(2);
        } else if (selectIndex == 39) {
            HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
            text.setPrimaryText("Text fLing 39");
            text.setPrimaryTextStyle(com.htc.lib1.cc.R.style.list_primary_m_bold);
            text.setSecondaryText("Text fLing 39, cbox+image, 2text, 7+1");
            text.setSecondaryTextStyle(com.htc.lib1.cc.R.style.list_secondary_m);

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

        } else if (selectIndex == 40) {
            HtcListItem1LineCenteredText text = (HtcListItem1LineCenteredText) i.findViewById(R.id.centered_text);
            text.setText("Text fLing 40, default centeredText.");

        } else if (selectIndex == 41) {
            HtcListItem1LineCenteredText text = (HtcListItem1LineCenteredText) i.findViewById(R.id.centered_text);
            text.setGravityCenterHorizontal(true);
            text.setText("Text fLing 41, centeredText.");

        } else if (selectIndex == 42) {
            HtcListItem1LineCenteredText text = (HtcListItem1LineCenteredText) i.findViewById(R.id.centered_text);
            text.setText("Text fLing 42, centeredText.");
            ProgressBar pb = new ProgressBar(this, null, android.R.attr.progressBarStyleSmall);
            pb.setVisibility(View.INVISIBLE);
            text.setView(pb);

        } else if (selectIndex == 43) {
            HtcListItemColorIcon image = (HtcListItemColorIcon) i.findViewById(R.id.photo);
            image.setColorIconImageResource(R.drawable.icon_launcher_programs);

            HtcListItem2LineTextProgressBar progress = (HtcListItem2LineTextProgressBar) i.findViewById(R.id.progress);
            progress.setPrimaryText("Text fLing 43, 2LineTextProgressBar");
            progress.setSecondaryText("Text2");
            progress.setSecondaryStampText(progress.getSecondaryStampCharSequence());
            progress.setSecondaryStampTextVisibility(View.GONE);
            progress.setPadding(0, 0, 0, 0);

        } else if (selectIndex == 44) {
            i.setFirstComponentTopMarginFixed(true);

            HtcListItemColorIcon image = (HtcListItemColorIcon) i.findViewById(R.id.photo);
            image.setColorIconImageResource(R.drawable.icon_launcher_programs);
            HtcListItem2LineTextProgressBar progress = (HtcListItem2LineTextProgressBar) i.findViewById(R.id.progress);
            progress.setPrimaryText("Text fLing 44, 2LineTextProgressBar");
            progress.setSecondaryText("Text2");
            progress.setSecondaryStampText(progress.getSecondaryStampText());
            progress.setSecondaryStampTextVisibility(View.GONE);
            HtcProgressBar mBar = (HtcProgressBar) progress.getProgressBar();
            mBar.setProgress(50);

        } else if (selectIndex == 45) {
            HtcListItemColorIcon image = (HtcListItemColorIcon) i.findViewById(R.id.photo);
            image.setColorIconImageResource(R.drawable.icon_launcher_programs);

            HtcListItem1LineTextProgressBar progress = (HtcListItem1LineTextProgressBar) i.findViewById(R.id.progress);
            progress.setPrimaryText("Text fLing 45, 1LineTextProgressBar");
            progress.getPrimaryCharSequence();
            progress.getPrimaryText();
            progress.getStampCharSequence();
            progress.getStampText();
            HtcProgressBar mBar = (HtcProgressBar) progress.getProgressBar();
            mBar.setProgress(50);

        } else if (selectIndex == 46) {
            i.setVerticalDividerEnabled(true);

            HtcListItemColorIcon image = (HtcListItemColorIcon) i.findViewById(R.id.photo);
            image.setColorIconImageResource(R.drawable.icon_launcher_programs);

            HtcListItem1LineTextProgressBar progress = (HtcListItem1LineTextProgressBar) i.findViewById(R.id.progress);
            progress.setPrimaryText("Text fLing 46, 1LineTextProgressBar");
            HtcProgressBar mBar = (HtcProgressBar) progress.getProgressBar();
            mBar.setProgress(50);
            progress.setStampText("Stamp");

            HtcImageButton button = (HtcImageButton) i.findViewById(R.id.imgButton);
            button.setImageResource(R.drawable.icon_btn_search_light);

        } else if (selectIndex == 47) {
            HtcListItem1LineCenteredText text = (HtcListItem1LineCenteredText) i.findViewById(R.id.centered_text);
            text.setText("Text fLing 40, default centeredText.");

            HtcListItem1LineCenteredStamp stamp = (HtcListItem1LineCenteredStamp) i.findViewById(R.id.centered_stamp);
            stamp.setText("stamp");

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

    public void improveCoverage() {
        final CharSequence charSequence = "ImproveCoverage";
        final int stringId = R.string.app_name;
        final int defStyle = -1;
        final AttributeSet attrs = null;
        final int drawableId = R.drawable.icon_category_photo;
        final Drawable drawable = getResources().getDrawable(drawableId);
        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawableId);
        // HtcListItem
        HtcListItem listItem = new HtcListItem(this);
        listItem = new HtcListItem(this, attrs, defStyle);
        listItem.setAutoMotiveMode(true);
        listItem.addView(new TextView(this), 0, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        listItem.enableSectionDivider(true);
        listItem.getBackgroundMode();
        listItem.setColorBarEnabled(true);
        listItem.setColorBarStyle(Color.BLACK, true, true);
        listItem.setColorBarStyle(Color.BLACK, false, true);
        listItem.setEnabled(true);
        listItem.setLeftIndent(false);
        listItem.setOnHierarchyChangeListener(null);

        // HtcListItem1LineCenteredStamp
        HtcListItem1LineCenteredStamp stamp = new HtcListItem1LineCenteredStamp(this);
        stamp = new HtcListItem1LineCenteredStamp(this, attrs, defStyle);
        stamp.getLayoutParams();
        stamp.notifyItemMode(HtcListItem.MODE_AUTOMOTIVE);
        stamp.setAutoMotiveMode(true);
        stamp.setAutoMotiveMode(false);
        stamp.setEnabled(true);
        stamp.setEnabled(false);
        stamp.setText(stringId);

        // HtcListItem1LineCenteredText
        HtcListItem1LineCenteredText text = new HtcListItem1LineCenteredText(this);
        text = new HtcListItem1LineCenteredText(this, attrs, defStyle);
        text.getLayoutParams();
        text.getText();
        text.notifyItemMode(HtcListItem.MODE_AUTOMOTIVE);
        text.setAutoMotiveMode(true);
        text.setAutoMotiveMode(false);
        text.setEnabled(true);
        text.setEnabled(false);
        text.setGravityCenterHorizontal(false);
        text.setText(stringId);
        text.setView(new TextView(this));

        // HtcListItem1LineTextProgressBar
        HtcListItem1LineTextProgressBar progressBar = new HtcListItem1LineTextProgressBar(this);
        progressBar = new HtcListItem1LineTextProgressBar(this, attrs, defStyle);
        progressBar.getStampCharSequence();
        progressBar.getStampText();
        progressBar.setStampText(null);
        progressBar.setStampText("");
        progressBar.setStampText(stringId);
        progressBar.getStampCharSequence();
        progressBar.getStampText();
        progressBar.setEnabled(false);
        progressBar.setEnabled(true);
        progressBar.setPrimaryText(charSequence);
        progressBar.setStampText(charSequence);
        progressBar.setStampTextVisibility(View.VISIBLE);

        // HtcListItem2LineStamp
        HtcListItem2LineStamp twoLineStamp = new HtcListItem2LineStamp(this);
        twoLineStamp = new HtcListItem2LineStamp(this, attrs, defStyle);
        twoLineStamp = new HtcListItem2LineStamp(this, HtcListItem.MODE_DEFAULT);
        twoLineStamp.getLayoutParams();
        twoLineStamp.notifyItemMode(HtcListItem.MODE_AUTOMOTIVE);

        // HtcListItem2LineText
        HtcListItem2LineText twoLineText = new HtcListItem2LineText(this);
        twoLineText = new HtcListItem2LineText(this, attrs, defStyle);
        twoLineText = new HtcListItem2LineText(this, HtcListItem.MODE_DEFAULT);
        twoLineText.getLayoutParams();
        twoLineText.setAutoMotiveMode(true);
        twoLineText.setAutoMotiveMode(false);
        twoLineText.notifyItemMode(HtcListItem.MODE_AUTOMOTIVE);
        twoLineText.setIndicatorBitmap(null);
        twoLineText.setIndicatorBitmap(bitmap);
        twoLineText.setIndicatorResource(0);
        twoLineText.setIndicatorDrawable(null);
        twoLineText.setSecondaryTextSingleLine(false);

        // HtcListItem2LineTextProgressBar
        HtcListItem2LineTextProgressBar twoLineTextProgressBar = new HtcListItem2LineTextProgressBar(this);
        twoLineTextProgressBar = new HtcListItem2LineTextProgressBar(this, attrs, defStyle);
        twoLineTextProgressBar = new HtcListItem2LineTextProgressBar(this, HtcListItem.MODE_DEFAULT);
        twoLineTextProgressBar.getLayoutParams();
        twoLineTextProgressBar.getSecondaryStampCharSequence();
        twoLineTextProgressBar.getSecondaryStampText();

        // HtcListItem7Badges1LineBottomStamp
        HtcListItem7Badges1LineBottomStamp badges = new HtcListItem7Badges1LineBottomStamp(this, HtcListItem.MODE_DEFAULT);
        badges = new HtcListItem7Badges1LineBottomStamp(this, attrs, defStyle);
        badges.getBadge(0);
        badges.notifyItemMode(HtcListItem.MODE_AUTOMOTIVE);

        // badges.onTouchEvent(null);
        badges.setEnabled(false);
        badges.setEnabled(true);
        badges.setFlagButtonChecked(true);
        badges.setFlagButtonClickable(false);
        badges.setStampTextStyle(-1);
        badges.setTextPaintStyle(this, null, -1);
        badges.setTextStamp(stringId);
        badges.setUpperBound(99);

        // HtcListItemBubbleCount
        HtcListItemBubbleCount bubbleCount = new HtcListItemBubbleCount(this);
        bubbleCount = new HtcListItemBubbleCount(this, attrs, defStyle);
        bubbleCount.setEnabled(false);
        bubbleCount.setEnabled(true);

        // HtcListItemColorIcon
        HtcListItemColorIcon colorIcon = new HtcListItemColorIcon(this);
        colorIcon = new HtcListItemColorIcon(this, attrs, defStyle);
        colorIcon.getLayoutParams();
        colorIcon.setScaleType(ScaleType.FIT_CENTER);

        // HtcListItemLabeledLayout
        HtcListItemLabeledLayout labeledLayout = new HtcListItemLabeledLayout(this);
        labeledLayout.getLabelText();
        labeledLayout.notifyItemMode(HtcListItem.MODE_AUTOMOTIVE);
        labeledLayout.setLabelText(null);
        labeledLayout.setLabelText(new SpannableStringBuilder());

        // HtcListItemMessageBody
        HtcListItemMessageBody body = new HtcListItemMessageBody(this);
        body = new HtcListItemMessageBody(this, HtcListItem.MODE_DEFAULT);
        body = new HtcListItemMessageBody(this, attrs, defStyle);
        body.enableBodyText(false);
        body.enableBodyText(true);
        body.getBadge(-1);
        body.getBodyTextContent();
        body.getColorBarImageDrawable();
        body.getLayoutParams();
        body.getPrimaryText();
        body.getPrimaryTextVisibility();
        body.getSecondaryText();
        body.getSecondaryTextVisibility();
        body.getStampVisibility();
        body.getTextStamp();
        body.notifyItemMode(HtcListItem.MODE_AUTOMOTIVE);
        body.setBadgeState(-1, true);
        body.setBodyText(charSequence);
        body.setBodyTextMinLines(1);
        body.setBodyTextResource(stringId);
        body.setBodyVisibility(-1);
        body.setColorBarImageBitmap(null);
        body.setColorBarImageDrawable(null);
        body.setColorBarImageResource(-1);
        body.setEnabled(false);
        body.setEnabled(true);
        body.setPrimaryText(charSequence);
        body.setPrimaryText(stringId);
        body.setPrimaryTextVisibility(-1);
        body.setSecondaryText(charSequence);
        body.setSecondaryText(stringId);
        body.setSecondaryTextVisibility(-1);
        body.setStampTextStyle(-1);
        body.setStampVisibility(-1);
        body.setTextStamp(charSequence);
        body.setTextStamp("test");
        body.setTextStamp(stringId);

        // HtcListItemQuickContactBadge
        HtcListItemQuickContactBadge quickContactBadge = new HtcListItemQuickContactBadge(this);
        quickContactBadge = new HtcListItemQuickContactBadge(this, attrs, defStyle);
        quickContactBadge.getLayoutParams();

        // HtcListItemReversed2LineText
        HtcListItemReversed2LineText reversed2LineText = new HtcListItemReversed2LineText(this);
        reversed2LineText = new HtcListItemReversed2LineText(this, attrs, defStyle);
        reversed2LineText.getLayoutParams();
        reversed2LineText.notifyItemMode(-1);
        reversed2LineText.setEnabled(false);
        reversed2LineText.setEnabled(true);
        reversed2LineText.setPrimaryText(charSequence);
        reversed2LineText.setPrimaryText(stringId);
        reversed2LineText.setSecondaryText(charSequence);
        reversed2LineText.setSecondaryText(stringId);
        reversed2LineText.setPrimaryText(null);
        reversed2LineText.setPrimaryText("");

        // HtcListItemSeparator
        HtcListItemSeparator separator = new HtcListItemSeparator(this);
        separator = new HtcListItemSeparator(this, HtcListItemSeparator.MODE_WHITE_STYLE);
        separator = new HtcListItemSeparator(this, attrs, defStyle);
        separator = new HtcListItemSeparator(this, HtcListItem.MODE_DEFAULT, HtcListItemSeparator.MODE_WHITE_STYLE);
        separator.setBackgroundStyle(HtcListItemSeparator.MODE_WHITE_STYLE);
        separator.setIcon(HtcListItemSeparator.ICON_LEFT, drawable);
        separator.setIcon(HtcListItemSeparator.ICON_LEFT, drawableId);
        separator.setIconButton(new HtcIconButton(this));
        separator.setImageButton(new HtcImageButton(this));
        separator.setSeparatorWithPowerBy();
        separator.setText(new Bundle());
        separator.setText(HtcListItemSeparator.TEXT_LEFT, stringId);
        separator.setToggleButton(new ToggleButton(this));

        // HtcListItemSerialNumber
        HtcListItemSerialNumber serialNumber = new HtcListItemSerialNumber(this);
        serialNumber = new HtcListItemSerialNumber(this, attrs, defStyle);
        serialNumber.getNumber();
        serialNumber.setDarkMode(true);
        serialNumber.setDarkMode(false);

        // HtcListItemSingleText
        HtcListItemSingleText singleText = new HtcListItemSingleText(this);
        singleText = new HtcListItemSingleText(this, attrs, defStyle);
        singleText.getLayoutParams();
        singleText.setAutoMotiveMode(true);
        singleText.setAutoMotiveMode(false);
        singleText.notifyItemMode(HtcListItem.MODE_AUTOMOTIVE);
        singleText.setEnabled(false);
        singleText.setEnabled(true);
        singleText.setText(stringId);
        singleText.setUseFontSizeInStyle(true);

        // HtcListItemStockBoard
        HtcListItemStockBoard stockBoard = new HtcListItemStockBoard(this);
        stockBoard.getLayoutParams();
        stockBoard.getTextLineContent(0);
        stockBoard.getTextLineContent(1);
        stockBoard.notifyItemMode(HtcListItem.MODE_AUTOMOTIVE);
        stockBoard.setBoardBackgroundDrawable(drawable);
        stockBoard.setBoardBackgroundResource(drawableId);
        stockBoard.setBoardImageBitmap(bitmap);
        stockBoard.setBoardImageResource(drawableId);
        stockBoard.setBoardSize(30, 30);
        stockBoard.setEnabled(false);
        stockBoard.setEnabled(true);
        stockBoard.setFrontText(stringId);
        stockBoard.setTextLineResource(0, stringId);

        // HtcListItemTileImage
        HtcListItemTileImage tileImage = new HtcListItemTileImage(this);
        tileImage = new HtcListItemTileImage(this, attrs, defStyle);
        tileImage.getLayoutParams();
        tileImage.setSecondaryImageBitmap(bitmap);
        tileImage.setSecondaryImageDrawable(drawable);
        tileImage.setSecondaryImageResource(drawableId);
    }
}
