
package com.htc.sense.commoncontrol.demo.htclistitem;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.htc.lib1.cc.widget.HtcCheckBox;
import com.htc.lib1.cc.widget.HtcImageButton;
import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcListItem1LineCenteredText;
import com.htc.lib1.cc.widget.HtcListItem2LineStamp;
import com.htc.lib1.cc.widget.HtcListItem2LineText;
import com.htc.lib1.cc.widget.HtcListItem7Badges1LineBottomStamp;
import com.htc.lib1.cc.widget.HtcListItem7Badges1LineBottomStamp.OnFlagButtonCheckedChangeListener;
import com.htc.lib1.cc.widget.HtcEditText;
import com.htc.lib1.cc.widget.HtcListItemBubbleCount;
import com.htc.lib1.cc.widget.HtcListItemColorIcon;
import com.htc.lib1.cc.widget.HtcListItemQuickContactBadge;
import com.htc.lib1.cc.widget.HtcListItemReversed2LineText;
import com.htc.lib1.cc.widget.HtcListItemSerialNumber;
import com.htc.lib1.cc.widget.HtcListItemSingleText;
import com.htc.lib1.cc.widget.HtcListItemStockBoard;
import com.htc.lib1.cc.widget.HtcListItemTileImage;
import com.htc.lib1.cc.widget.ListItem;
import com.htc.lib1.cc.widget.QuickContactBadge;
import com.htc.sense.commoncontrol.demo.R;

public class HtcListItemActivity1 extends HtcListActivity {
    LayoutInflater mInflater = null;
    private static final String ACCESSIBILITY_CONTENT_DESCRIPTION = "Should be set by AP";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setListAdapter(new MyListAdapter(this));

        if (mEnableAutomotive) {
            getWindow().setBackgroundDrawableResource(com.htc.lib1.cc.R.drawable.common_app_bkg_dark);
        } else {
            getWindow().setBackgroundDrawableResource(com.htc.lib1.cc.R.drawable.common_app_bkg);
        }

        getListView().setCacheColorHint(0x00000000);

        getListView().setOnItemClickListener(mOnClickListener);
    }

    private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            // TODO Auto-generated method stub

            HtcCheckBox checkBox = (HtcCheckBox) v.findViewById(R.id.checkBut);

            if (checkBox != null) {
                checkBox.setChecked(!checkBox.isChecked());
                checkBox.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
            }
        }

    };

    @Override
    protected void initMenu() {
        mAddMenuEnableAutomotive = true;
        mAddMenuShowListItem = true;
    }

    private class MyListAdapter extends BaseAdapter {
        Context mContext = null;

        final int layoutsHtcListItem[] = new int[] {
                R.layout.list_item01, R.layout.list_item02, R.layout.list_item03,
                R.layout.list_item04, R.layout.list_item05, R.layout.list_item06,
                R.layout.list_item07, R.layout.list_item08, R.layout.list_item09,
                R.layout.list_item10, R.layout.list_item11, R.layout.list_item12,
                R.layout.list_item13, R.layout.list_item14, R.layout.list_item15,
                R.layout.list_item16,
                R.layout.list_item17,
                R.layout.list_item18,
                // no item19
                R.layout.list_item20, R.layout.list_item21, R.layout.listitem_001,
                R.layout.listitem_001, R.layout.listitem_002, R.layout.listitem_002,
                R.layout.list_item31, R.layout.list_item32, R.layout.list_item33,
                R.layout.list_item18, R.layout.list_item27, R.layout.list_item27,
                R.layout.list_item27,
        };

        final int layoutsListItem[] = new int[] {
                R.layout.list_item01_new, R.layout.list_item02_new, R.layout.list_item03_new,
                R.layout.list_item04_new, R.layout.list_item05_new, R.layout.list_item06_new,
                R.layout.list_item07_new, R.layout.list_item08_new, R.layout.list_item09_new,
                R.layout.list_item10_new, R.layout.list_item11_new, R.layout.list_item12_new,
                R.layout.list_item13_new, R.layout.list_item14_new, R.layout.list_item15_new,
                R.layout.list_item16_new,
                R.layout.list_item17_new,
                R.layout.list_item18_new,
                // no item19
                R.layout.list_item20_new, R.layout.list_item21_new, R.layout.listitem_001_new,
                R.layout.listitem_001_new, R.layout.listitem_002_new, R.layout.listitem_002_new,
                R.layout.list_item31_new, R.layout.list_item32_new, R.layout.list_item33_new,
                R.layout.list_item18_new, R.layout.list_item27_new, R.layout.list_item27_new,
                R.layout.list_item27_new,
        };

        final int LIST_ITEM_COUNT = layoutsHtcListItem.length;

        public MyListAdapter(Context context) {
            mContext = context;
        }

        public int getCount() {
            return LIST_ITEM_COUNT;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return LIST_ITEM_COUNT;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewGroup i = (ViewGroup) mInflater.inflate(mShowListItem ? layoutsListItem[position] : layoutsHtcListItem[position], null);
            if (mShowListItem) {
                ((ListItem) i).setAutoMotiveMode(mEnableAutomotive);
            } else {
                ((HtcListItem) i).setAutoMotiveMode(mEnableAutomotive);
            }

            if (position == 0) {
                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                text.setPrimaryText("Text fLing 0, 2text");
                text.setSecondaryTextVisibility(View.GONE);

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, mMargin[M1], 0);
                }

            } else if (position == 1) {
                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                text.setPrimaryText("Text fLing 1, 2text, 2stamp, this is a long text");
                text.setSecondaryTextVisibility(View.GONE);

                HtcListItem2LineStamp stamp = (HtcListItem2LineStamp) i.findViewById(R.id.stamp1);
                stamp.setPrimaryText("aaa");
                stamp.setSecondaryTextVisibility(View.GONE);

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, mMargin[M2], 0);
                    setMargin(stamp, 0, 0, mMargin[M1], 0);
                }

            } else if (position == 2) {
                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                text.setPrimaryText("Text fLing 2");
                text.setSecondaryText("Text fLing 2, 2text");

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, mMargin[M1], 0);
                }

            } else if (position == 3) {
                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                text.setPrimaryText("Text fLing 3");
                text.setSecondaryText("Text fLing 3, 2text, 2stamp");

                HtcListItem2LineStamp stamp = (HtcListItem2LineStamp) i.findViewById(R.id.stamp1);
                stamp.setPrimaryText("Exchange");

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, mMargin[M2], 0);
                    setMargin(stamp, 0, 0, mMargin[M1], 0);
                }

            } else if (position == 4) {
                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                text.setPrimaryText("Text fLing 4");
                text.setSecondaryText("Text fLing 4, 2text, stockboard");

                HtcListItemStockBoard board = (HtcListItemStockBoard) i.findViewById(R.id.stock);

                GradientDrawable gd = new GradientDrawable();
                gd.setCornerRadius(5);
                gd.setColor(getResources().getColor(R.color.stock_green));
                // As designer's comment, the outer of FHD is 2px and it of HD
                // is 1px.
                gd.setStroke(2, getResources().getColor(R.color.stock_gray));
                board.setBoardImageDrawable(gd); // API has not ready.

                board.setFrontText("15.99");
                board.setTextLine(0, "+0.79");
                board.setTextLine(1, "+1.20%");

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, mMargin[M2], 0);
                    setMargin(board, 0, 0, mMargin[M1], 0);
                }

            } else if (position == 5) {
                HtcListItem1LineCenteredText text = (HtcListItem1LineCenteredText) i.findViewById(R.id.text1);
                text.setTextNoContentStyle();
                text.setText("no content text");

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, mMargin[M1], 0);
                }

            } else if (position == 6) {
                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                text.setPrimaryText("Text fLing 6, bubble count, image button");
                text.setSecondaryTextVisibility(View.GONE);

                HtcListItemBubbleCount bubble = (HtcListItemBubbleCount) i.findViewById(R.id.bubble);
                bubble.setBubbleCount(6);

                HtcImageButton button = (HtcImageButton) i.findViewById(R.id.imgButton);
                button.setImageResource(R.drawable.icon_btn_search_light);

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, 0, 0);
                    setMargin(bubble, mMargin[M2], 0, 0, 0);
                } else {
                    ((HtcListItem) i).setLastComponentAlign(true);
                }

            } else if (position == 7) {
                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                text.setPrimaryText("Text fLing 7,bubble count, image button, vertical divider");
                text.setSecondaryText("Text fLing 2");
                text.setIndicatorResource(R.drawable.icon_indicator_calendar);

                HtcListItemBubbleCount bubble = (HtcListItemBubbleCount) i.findViewById(R.id.bubble);
                bubble.setUpperBound(77);
                bubble.setBubbleCount(100000);

                HtcImageButton button = (HtcImageButton) i.findViewById(R.id.imgButton);
                button.setImageResource(R.drawable.icon_btn_search_light);

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, mMargin[M2], 0);
                    setMargin(bubble, 0, 0, mMargin[M2], 0);
                } else {
                    ((HtcListItem) i).setVerticalDividerEnabled(true);
                }

            } else if (position == 8) {

                HtcListItemColorIcon image = (HtcListItemColorIcon) i.findViewById(R.id.photo);
                image.setColorIconImageResource(R.drawable.icon_launcher_programs);

                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                text.setPrimaryText("Text fLing 8, ColorIcon, 2text, 2stamp");
                text.setSecondaryText("Text fLing 2");

                if (mShowListItem) {
                    setMargin(text, 0, 0, mMargin[M1], 0);
                    if (mEnableAutomotive) {
                        setMargin(image, 0, 0, 0, 0);
                    } else {
                        image.setLayoutParams(new ListItem.LayoutParams(ListItem.LayoutParams.SIZE_WRAP_CONTENT | ListItem.LayoutParams.CENTER_VERTICAL));
                        setMargin(image, mMargin[M2], 0, mMargin[M2], 0);
                    }
                } else {
                    ((HtcListItem) i).setFirstComponentAlign(true);
                }

            } else if (position == 9) {
                HtcListItemQuickContactBadge image = (HtcListItemQuickContactBadge) i.findViewById(R.id.photo);
                QuickContactBadge badge = image.getBadge();
                badge.setImageResource(R.drawable.icon_category_photo);
                badge.setContentDescription(ACCESSIBILITY_CONTENT_DESCRIPTION);

                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                text.setPrimaryText("Text fLing 9, QuickBadge, 2text");
                text.setSecondaryText("Text fLing 2");

                if (mShowListItem) {
                    setMargin(text, mMargin[M2], 0, mMargin[M1], 0);
                }

            } else if (position == 10) {
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

                if (mShowListItem) {
                    setMargin(text, mMargin[M2], 0, mMargin[M1], 0);
                }

            } else if (position == 11) {
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

                if (mShowListItem) {
                    setMargin(text, mMargin[M2], 0, 0, 0);
                    setMargin(stamp, mMargin[M2], 0, mMargin[M1], 0);
                }

            } else if (position == 12) {

                HtcListItemTileImage image = (HtcListItemTileImage) i.findViewById(R.id.photo);
                image.setTileImageResource(R.drawable.icon_category_photo);

                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                text.setPrimaryText("Text fLing 12, TileImage, 2text");
                text.setSecondaryText("Text fLing 2, ScaleType.CENTER");

                if (mShowListItem) {
                    setMargin(text, mMargin[M2], 0, mMargin[M1], 0);
                }

            } else if (position == 13) {
                HtcListItemTileImage image = (HtcListItemTileImage) i.findViewById(R.id.photo);
                image.setTileImageResource(R.drawable.head);

                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                text.setPrimaryText("Text fLing 13, TileImage, 2text, 2stamp");
                text.setSecondaryText("Text fLing 2, Dark Mode");

                HtcListItem2LineStamp stamp = (HtcListItem2LineStamp) i.findViewById(R.id.stamp1);
                stamp.setPrimaryText("Exchange");

                if (mShowListItem) {
                    setMargin(text, mMargin[M2], 0, 0, 0);
                    setMargin(stamp, mMargin[M2], 0, mMargin[M1], 0);
                }

            } else if (position == 14) {
                HtcListItemTileImage image = (HtcListItemTileImage) i.findViewById(R.id.photo);
                image.setTileImageResource(R.drawable.head);

                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                text.setPrimaryText("Text fLing 14, TileImage, 2text, 2stamp");
                text.setSecondaryText("Text fLing 2");

                HtcListItem2LineStamp stamp = (HtcListItem2LineStamp) i.findViewById(R.id.stamp1);
                stamp.setPrimaryText("Today");
                stamp.setSecondaryText("12:34 AM");

                if (mShowListItem) {
                    setMargin(text, mMargin[M2], 0, 0, 0);
                    setMargin(stamp, mMargin[M2], 0, mMargin[M1], 0);
                }

            } else if (position == 15) {
                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                text.setPrimaryText("Text fLing 15, 2text, checkbox");
                text.setSecondaryText("Text fLing 2");

                final HtcCheckBox checkBox = (HtcCheckBox) i.findViewById(R.id.checkBut);
                if (checkBox != null) {
                    checkBox.setChecked(true);
                }

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, 0, 0);
                } else {
                    ((HtcListItem) i).setLastComponentAlign(true);
                }

            } else if (position == 16) {
                HtcListItemTileImage image = (HtcListItemTileImage) i.findViewById(R.id.photo);
                image.setTileImageResource(R.drawable.head);

                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                text.setPrimaryText("Text fLing 16, TileImage, 2text, 2stamp, imagebutton");
                text.setSecondaryText("Text fLing 2");

                HtcImageButton button = (HtcImageButton) i.findViewById(R.id.imgButton);
                button.setImageResource(R.drawable.icon_btn_search_light);

                if (mShowListItem) {
                    setMargin(text, mMargin[M2], 0, mMargin[M2], 0);
                } else {
                    ((HtcListItem) i).setLastComponentAlign(true);
                    ((HtcListItem) i).setVerticalDividerEnabled(true);
                }

            } else if (position == 17) {
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
                    public void onCheckedChanged(HtcListItem7Badges1LineBottomStamp view,
                            boolean isChecked) {
                        Toast.makeText(HtcListItemActivity1.this,
                                isChecked ? "flag button checked " : "flag button unchecked",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, 0, 0);
                    setMargin(stamp, getResources().getDimensionPixelOffset(R.dimen.margin_m), 0, mMargin[M1] + mMargin[M2] - getResources().getDimensionPixelOffset(R.dimen.margin_m), 0);
                }

            } else if (position == 18) {

                HtcListItemSingleText text = (HtcListItemSingleText) i.findViewById(R.id.text2);
                text.setText("Text fLing 18, SingleText(max 2 lines), 1stamp, imgbtn. need a long.");

                text.setText("Text fLing 18, SingleText(max 2 lines), 1stamp, imgbtn. need a long text.");

                HtcImageButton button = (HtcImageButton) i.findViewById(R.id.imgButton);

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, 0, 0);
                    setMargin(button, mMargin[M2], 0, mMargin[M1], 0);
                }

            } else if (position == 19) {
                HtcListItemQuickContactBadge image = (HtcListItemQuickContactBadge) i.findViewById(R.id.photo);
                QuickContactBadge badge = image.getBadge();
                badge.setImageResource(R.drawable.icon_category_photo);
                badge.setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        Toast.makeText(HtcListItemActivity1.this, "Ah-Ha", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
                badge.setContentDescription(ACCESSIBILITY_CONTENT_DESCRIPTION);

                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                text.setPrimaryText("Text fLing 19, Click on QuickBadge!");
                text.setSecondaryText("Text fLing 2, quickbadge, 2text, 2stamp. gdsghdghsdghd dgdtgfythydg cghdghtrghdfhtrhjgfjncvnhfdhtr dfghdf");
                text.setSecondaryTextSingleLine(false);

                HtcListItem2LineStamp stamp = (HtcListItem2LineStamp) i.findViewById(R.id.stamp1);
                stamp.setPrimaryText("9:12 AM");

                if (mShowListItem) {
                    setMargin(text, mMargin[M2], 0, mMargin[M2], 0);
                    setMargin(stamp, 0, 0, mMargin[M1], 0);
                }

            } else if (position == 20) {
                HtcListItemColorIcon image = (HtcListItemColorIcon) i.findViewById(R.id.photo);
                image.setColorIconImageResource(R.drawable.icon_launcher_programs);

                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                text.setPrimaryText("Text fLing 20, ColorIcon, 2text");
                text.setSecondaryText("Text fLing 22222222222222222222222222222222222222222222222222222222222222222");

                if (mShowListItem) {
                    setMargin(text, 0, 0, mMargin[M1], 0);
                    if (mEnableAutomotive) {
                        setMargin(image, 0, 0, 0, 0);
                    } else {
                        image.setLayoutParams(new ListItem.LayoutParams(ListItem.LayoutParams.SIZE_WRAP_CONTENT | ListItem.LayoutParams.CENTER_VERTICAL));
                        setMargin(image, mMargin[M2], 0, mMargin[M2], 0);
                    }
                } else {
                    ((HtcListItem) i).setFirstComponentAlign(true);
                }

            } else if (position == 21) {
                HtcListItemColorIcon image = (HtcListItemColorIcon) i.findViewById(R.id.photo);
                image.setColorIconImageResource(R.drawable.icon_launcher_programs);

                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                text.setPrimaryText("Text fLing 21, ColorIcon, 2text");
                text.setSecondaryTextSingleLine(false);
                text.setSecondaryText("Text fLing 2222222222222222222222222222222222222222222222222222222222222");

                if (mShowListItem) {
                    setMargin(text, 0, 0, mMargin[M1], 0);
                    if (mEnableAutomotive) {
                        setMargin(image, 0, 0, 0, 0);
                    } else {
                        image.setLayoutParams(new ListItem.LayoutParams(ListItem.LayoutParams.SIZE_WRAP_CONTENT | ListItem.LayoutParams.CENTER_VERTICAL));
                        setMargin(image, mMargin[M2], 0, mMargin[M2], 0);
                    }
                } else {
                    ((HtcListItem) i).setFirstComponentAlign(true);
                    ((HtcListItem) i).setFirstComponentTopMarginFixed(true);
                }

            } else if (position == 22) {
                HtcListItemSerialNumber number = (HtcListItemSerialNumber) i.findViewById(R.id.number);
                number.setNumber(2);

                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                text.setPrimaryText("Text fLing 22, SerialNumber, 2text");
                text.setSecondaryText("default(Bright mode)");

                if (mShowListItem) {
                    setMargin(text, mMargin[M2], 0, mMargin[M1], 0);
                }

            } else if (position == 23) {
                HtcListItemSerialNumber number = (HtcListItemSerialNumber) i.findViewById(R.id.number);
                number.setNumber(3);
                number.setDarkMode(true);

                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                text.setPrimaryText("Text fLing 23, SerialNumber, 2text");
                text.setSecondaryText("Dark mode");

                if (mShowListItem) {
                    setMargin(text, mMargin[M2], 0, mMargin[M1], 0);
                }

            } else if (position == 24) {
                AutoCompleteTextView text = (AutoCompleteTextView) i.findViewById(R.id.autotext);
                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, mMargin[M1], 0);
                }

            } else if (position == 25) {
                HtcEditText text = (HtcEditText) i.findViewById(R.id.autotext);

                HtcImageButton button = (HtcImageButton) i.findViewById(R.id.imgButton);
                button.setImageResource(R.drawable.icon_btn_search_light);

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], getResources().getDimensionPixelOffset(R.dimen.margin_m), 0, getResources().getDimensionPixelOffset(R.dimen.margin_m));
                } else {
                    ((HtcListItem) i).setLastComponentAlign(true);
                }

            } else if (position == 26) {
                HtcListItemReversed2LineText text = (HtcListItemReversed2LineText) i.findViewById(R.id.text1);
                text.setPrimaryText("Text fLing 26, HtcListItemReversed2LineText primary text line");
                text.setSecondaryText("Secondary text line");

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, mMargin[M1], 0);
                }

            } else if (position == 27) {
                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text1);
                text.setPrimaryText("Text fLing 27");
                text.setSecondaryText("Text fLing 27, BubbleCount");

                HtcListItem7Badges1LineBottomStamp stamp = (HtcListItem7Badges1LineBottomStamp) i.findViewById(R.id.stamp2);
                stamp.setBubbleCount(6);

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, 0, 0);
                    setMargin(stamp, mMargin[M2], 0, mMargin[M1], 0);
                }

            } else if (position == 28) {
                HtcListItem1LineCenteredText text = (HtcListItem1LineCenteredText) i.findViewById(R.id.centered_text);
                text.setText("Text fLing 28, default centeredText.");

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, mMargin[M1], 0);
                }

            } else if (position == 29) {
                HtcListItem1LineCenteredText text = (HtcListItem1LineCenteredText) i.findViewById(R.id.centered_text);
                text.setText("Text fLing 29,LeftIndent centeredText.");

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, mMargin[M1], 0);
                    ((ListItem) i).setStartIndent(true);
                } else {
                    ((HtcListItem) i).setLeftIndent(true);
                }

            }
            else if (position == 30) {
                HtcListItem1LineCenteredText text = (HtcListItem1LineCenteredText) i.findViewById(R.id.centered_text);
                text.setGravityCenterHorizontal(true);
                text.setText(getResources().getString(R.string.centeredtext_label));

                if (mShowListItem) {
                    setMargin(text, mMargin[M1], 0, mMargin[M1], 0);
                }

            }

            return i;
        }
    }
}
