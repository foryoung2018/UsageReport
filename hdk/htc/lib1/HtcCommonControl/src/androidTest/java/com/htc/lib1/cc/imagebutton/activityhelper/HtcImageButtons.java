
package com.htc.lib1.cc.imagebutton.activityhelper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.test.R;
import com.htc.lib1.cc.widget.HtcButtonUtil;
import com.htc.lib1.cc.widget.HtcImageButton;
import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcListItem1LineCenteredText;
import com.htc.lib1.cc.widget.HtcPopupWindowWrapper;
import com.htc.lib1.cc.widget.HtcProgressButton;
import com.htc.lib1.cc.widget.HtcRimImageButton;
import com.htc.lib1.cc.widget.PopupBubbleWindow;

public class HtcImageButtons extends ActivityBase implements OnItemClickListener, PopupBubbleWindow.OnDismissListener {
    private HtcImageButton imgbtn, imgbtn_coloron, imgbtn_inpress, imgbtn_popup;
    private HtcRimImageButton rimimgbtn, rimimgbtnDark, rimimgbtn_coloron, rimimgbtn_inpress, rimimgbtn_popup;
    private LayoutInflater mInflater = null;
    private HtcProgressButton prgbtn;

    // Add by Ahan
    private OnItemClickListener mOnItemClickListener;
    private PopupBubbleWindow.OnDismissListener mOnDissmissListener;
    // Add by Ahan

    boolean mIsPlaying = false;
    private float mCurrentProgress;
    private float mMax = 210f;
    private long mUpdateCycle = 100;
    private Handler mMainHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getIntent().getIntExtra("theme", R.style.HtcDeviceDefault));
        setContentView(R.layout.htcbutton_demos_imagebutton);
        mOnItemClickListener = this;
        mOnDissmissListener = this;
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        imgbtn = (HtcImageButton) findViewById(R.id.img_light_disable);
        Drawable drLight = getResources().getDrawable(R.drawable.icon_btn_phone_light);
        if (drLight != null) {
            imgbtn.setIconDrawable(drLight);
            imgbtn.setEnabled(false);
        }

        rimimgbtn = (HtcRimImageButton) findViewById(R.id.rimimg_light_disable);
        rimimgbtn.setImageResource(R.drawable.icon_btn_phone_light);
        rimimgbtn.setEnabled(false);

        rimimgbtnDark = (HtcRimImageButton) findViewById(R.id.rimimg_dark_disable);
        rimimgbtnDark.setIconResource(R.drawable.icon_btn_phone_dark);
        rimimgbtnDark.setEnabled(false);

        imgbtn_coloron = (HtcImageButton) findViewById(R.id.img_coloron);
        imgbtn_coloron.setColorOn(true);

        imgbtn_inpress = (HtcImageButton) findViewById(R.id.img_inpress);
        imgbtn_inpress.stayInPress(true);

        rimimgbtn_coloron = (HtcRimImageButton) findViewById(R.id.rimimg_coloron);
        rimimgbtn_coloron.setColorOn(true);

        rimimgbtn_inpress = (HtcRimImageButton) findViewById(R.id.rimimg_inpress);
        rimimgbtn_inpress.stayInPress(true);

        imgbtn_popup = (HtcImageButton) findViewById(R.id.img_popup);
        imgbtn_popup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showPopupWindow(view);
                Toast.makeText(view.getContext(), "Never show PopupWindow at this time.", Toast.LENGTH_SHORT).show();
            }
        });

        rimimgbtn_popup = (HtcRimImageButton) findViewById(R.id.rimimg_popup);
        rimimgbtn_popup.setOnPressAnimationListener(new HtcButtonUtil.OnPressAnimationListener() {

            @Override
            public void onAnimationStarts(View view) {
            }

            @Override
            public void onAnimationEnds(View view) {
                showPopupWindow(view);
                Toast.makeText(view.getContext(), "It's a good time to show PopupWindow.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationCancels(View view) {
            }
        });

        //HtcProgressButton
        prgbtn = (HtcProgressButton) findViewById(R.id.prgtbn);
        prgbtn.setButtonSizeMode(HtcButtonUtil.BUTTON_SIZE_MODE_MIDDLE);
        prgbtn.setIconResource(R.drawable.icon_btn_play_dark);
        prgbtn.setMax(mMax);

        //set click listener HtcProgressButton
        prgbtn.setOnClickListener(new View.OnClickListener() {
            int mPlayIcon = R.drawable.icon_btn_play_dark;
            int mPauseIcon = R.drawable.icon_btn_pause_dark;

            @Override
            public void onClick(View v) {
                if (mIsPlaying = !mIsPlaying)
                    mMainHandler.sendMessage(mMainHandler.obtainMessage(0));
                prgbtn.setIconResource(mIsPlaying ? mPauseIcon : mPlayIcon);
            }
        });

        //set handler
        mMainHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (mIsPlaying) {
                    mCurrentProgress = mCurrentProgress + 1f > mMax ? 0f : mCurrentProgress + 1;
                    prgbtn.setProgress(mCurrentProgress);
                    mMainHandler.sendMessageDelayed(mMainHandler.obtainMessage(0), mUpdateCycle);
                }
            }
        };
    }

    //Belows are all about showing PopupWindow
    private void showPopupWindow(View v) {
        ActionAdapter adapter = new ActionAdapter();
        HtcPopupWindowWrapper hpww = new HtcPopupWindowWrapper();
        hpww.setAdapter(adapter);
        hpww.setArchorView(v);
        hpww.setOnItemClickListener(mOnItemClickListener);
        hpww.setOnDismissListener(mOnDissmissListener);
        hpww.showPopupWindow();
        ((HtcImageButton) v).setColorOn(true);
    }

    private class ActionAdapter extends BaseAdapter {
        private String[] ItemStrings = new String[] {
                "listitem1", "listitem2", "listitem3", "listitem4", "listitem5", "listitem6"
        };

        public int getCount() {
            return ItemStrings.length;
        }

        public Object getItem(int position) {
            return ItemStrings[position];
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            HtcListItem listitem = (HtcListItem) mInflater.inflate(R.layout.actionbarlistitem, null);
            HtcListItem1LineCenteredText text = (HtcListItem1LineCenteredText) listitem.findViewById(R.id.text1);
            text.setText(ItemStrings[position]);
            listitem.setBackgroundDrawable(null);
            return listitem;
        }
    }

    public void onDismiss() {
        if (imgbtn_popup != null)
            imgbtn_popup.setColorOn(false);
        if (rimimgbtn_popup != null)
            rimimgbtn_popup.setColorOn(false);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    }
    //Aboves are all about showing PopupWindow
}
