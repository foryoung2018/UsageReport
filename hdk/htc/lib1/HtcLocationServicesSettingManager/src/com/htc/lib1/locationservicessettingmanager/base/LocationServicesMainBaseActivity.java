package com.htc.lib1.locationservicessettingmanager.base;

import com.htc.lib1.cc.widget.ActionBarContainer;
import com.htc.lib1.cc.widget.ActionBarDropDown;
import com.htc.lib1.cc.widget.ActionBarExt;
import com.htc.lib1.cc.widget.HtcAlertDialog;
import com.htc.lib1.cc.widget.HtcListItem2LineText;
import com.htc.lib1.cc.widget.HtcListItemColorIcon;
import com.htc.lib1.locationservicessettingmanager.R;
import com.htc.lib1.locationservicessettingmanager.AddressNetworkActivity;
import com.htc.lib1.locationservicessettingmanager.R.drawable;
import com.htc.lib1.locationservicessettingmanager.R.id;
import com.htc.lib1.locationservicessettingmanager.R.layout;
import com.htc.lib1.locationservicessettingmanager.R.string;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LocationServicesMainBaseActivity extends HtcListActivity {
    LayoutInflater mInflater = null;
    public static final String EXTRA_LAUNCH_WHICH = "Which";
    ActionBarDropDown mTvTitle;
    ActionBarExt mActionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_location_service);

        View v = findViewById(R.id.main_view);
        if (v != null) {
            v.setFitsSystemWindows(true);
        }

        initLayout();

        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        setListAdapter(new MyListAdapter(this));

        getListView().setCacheColorHint(Color.TRANSPARENT);
        getListView().setOnItemClickListener(mOnClickListener);
    }

    private void initLayout()
    {
        mActionBar = new ActionBarExt(getWindow(), getActionBar());
        ActionBarContainer actionContainer = mActionBar.getCustomContainer();
        mTvTitle = new ActionBarDropDown(getApplicationContext());
        mTvTitle.setPrimaryText(com.htc.lib1.locationservicessettingmanager.R.string.lib_app_name);
        mTvTitle.setPrimaryVisibility(View.VISIBLE);
        if (actionContainer != null) {
            actionContainer.addCenterView(mTvTitle);
            actionContainer.setBackUpEnabled(true);
            actionContainer.setBackUpOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        // Call after ActionBar config is done
        setHeaderBarColor();

        com.htc.lib1.cc.widget.HtcListView listView = (com.htc.lib1.cc.widget.HtcListView)findViewById(id.list);
        //make it can't scroll.
        listView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });

        setDescriptionBody() ;
    }

    private void setDescriptionBody()
    {
        final String precise_location = getString(R.string.precise_location);
        final String personal_usage_data = getString(R.string.personal_usage_data);
        final String des = String.format(getString(R.string.description_label), precise_location, personal_usage_data);
        final String precise_location_description = String.format(getString(R.string.precise_location_description), precise_location);
        final String personal_usage_data_description = String.format(getString(R.string.personal_usage_data_description), personal_usage_data);

        final SpannableString spannable = SpannableString.valueOf(des);
        applySpan(spannable, precise_location, new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                HtcAlertDialog.Builder preciseAltDlg = new HtcAlertDialog.Builder(LocationServicesMainBaseActivity.this);
                preciseAltDlg.setTitle(getString(R.string.precise_location_upper));
                preciseAltDlg.setMessage(precise_location_description);
                preciseAltDlg.setPositiveButton(getString(R.string.alert_dialog_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                try {
                                    dialog.dismiss();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );

                try {
                    preciseAltDlg.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(Color.BLUE);
            }
        });
        applySpan(spannable, personal_usage_data, new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                HtcAlertDialog.Builder personalAltDlg = new HtcAlertDialog.Builder(LocationServicesMainBaseActivity.this);
                personalAltDlg.setTitle(getString(R.string.personal_usage_data_upper));
                personalAltDlg.setMessage(personal_usage_data_description);
                personalAltDlg.setPositiveButton(getString(R.string.alert_dialog_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                try {
                                    dialog.dismiss();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );

                try {
                    personalAltDlg.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(Color.BLUE);
            }
        });

        TextView txtView = (TextView) findViewById(R.id.description_body);
        txtView.setText(spannable);
        txtView.setMovementMethod(LinkMovementMethod.getInstance());
        txtView.setHighlightColor(Color.TRANSPARENT);
    }

    private void applySpan(SpannableString spannable, String target, ClickableSpan span) {
        final String spannableString = spannable.toString();
        final int start = spannableString.indexOf(target);
        if (start >= 0) {
            final int end = start + target.length();
            spannable.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    @Override
    protected void setActionBarTextureDrawable(Drawable d) {
        if (mActionBar != null) {
            mActionBar.setBackgroundDrawable(d);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        getListView().setEnabled(true);
    }

    private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

            getListView().setEnabled(false);

            if (position == 0) {
                Intent intent = new Intent(getApplicationContext(), AddressNetworkActivity.class);
                intent.putExtra(EXTRA_LAUNCH_WHICH, getResources().getString(R.string.home_label));
                startActivity(intent);
            } else if (position == 1) {
                Intent intent = new Intent(getApplicationContext(), AddressNetworkActivity.class);
                intent.putExtra(EXTRA_LAUNCH_WHICH, getResources().getString(R.string.work_label));
                startActivity(intent);
            }
        }
    };

    private class MyListAdapter extends BaseAdapter {
        Context mContext = null;

        final int layoutsHtcListItem[] = new int[]{
                R.layout.listitem_items, R.layout.listitem_items,
        };

        final int LIST_ITEM_COUNT = layoutsHtcListItem.length;

        public MyListAdapter(Context context) {
            mContext = context;
        }

        public int getCount() {
            return LIST_ITEM_COUNT;
        }

        public Object getItem(int position) {
            return layoutsHtcListItem[position];
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return LIST_ITEM_COUNT;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewGroup i = (ViewGroup) mInflater.inflate(layoutsHtcListItem[position], null);

            if (position == 0) {
                HtcListItemColorIcon image_home = (HtcListItemColorIcon) i.findViewById(R.id.photo);
                image_home.setColorIconImageResource(R.drawable.icon_btn_contextual_home_light_xl);
                image_home.setScaleType(ImageView.ScaleType.FIT_CENTER);

                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text);
                text.setPrimaryText(R.string.home_label);
                text.setSecondaryTextSingleLine(false);
                text.setSecondaryText(R.string.home_description);
            } else if (position == 1) {
                HtcListItemColorIcon image_work = (HtcListItemColorIcon) i.findViewById(R.id.photo);
                image_work.setColorIconImageResource(R.drawable.icon_btn_contextual_work_light_xl);
                image_work.setScaleType(ImageView.ScaleType.FIT_CENTER);

                HtcListItem2LineText text = (HtcListItem2LineText) i.findViewById(R.id.text);
                text.setPrimaryText(R.string.work_label);
                text.setSecondaryTextSingleLine(false);
                text.setSecondaryText(R.string.work_description);
            }

            return i;
        }
    }
}
