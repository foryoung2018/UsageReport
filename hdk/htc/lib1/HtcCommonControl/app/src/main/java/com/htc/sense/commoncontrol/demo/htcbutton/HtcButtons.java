package com.htc.sense.commoncontrol.demo.htcbutton;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.htc.lib1.cc.widget.HtcButtonUtil;
import com.htc.lib1.cc.widget.HtcIconButton;
import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcListItem1LineCenteredText;
import com.htc.lib1.cc.widget.HtcPopupWindowWrapper;
import com.htc.lib1.cc.widget.HtcRimButton;
import com.htc.lib1.cc.widget.PopupBubbleWindow;


public class HtcButtons extends CommonDemoActivityBase implements OnItemClickListener, PopupBubbleWindow.OnDismissListener {
    private HtcIconButton iconbtn_coloron, iconbtn_inpress, icon_popup;
    private HtcRimButton rimbtnDark, rim_coloron, rim_inpress, rim_popup;
    private LayoutInflater mInflater=null;

    // Add by Ahan
    private AdapterView.OnItemClickListener mOnItemClickListener;
    private PopupBubbleWindow.OnDismissListener mOnDissmissListener;
    // Add by Ahan

    private int mThemeId = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.htcbutton_demos_button);

        rimbtnDark = (HtcRimButton) findViewById(R.id.rim_dark);
        rimbtnDark.setIconResource(R.drawable.icon_btn_phone_dark);

        iconbtn_coloron = (HtcIconButton) findViewById(R.id.icon_coloron);
        iconbtn_inpress = (HtcIconButton) findViewById(R.id.icon_stayinpress);

        iconbtn_coloron.setColorOn(true);
        iconbtn_inpress.stayInPress(true);

        rim_coloron = (HtcRimButton) findViewById(R.id.rim_coloron);
        rim_inpress = (HtcRimButton) findViewById(R.id.rim_stayinpress);

        rim_coloron.setColorOn(true);
        rim_inpress.stayInPress(true);

        icon_popup = (HtcIconButton) findViewById(R.id.icon_popup);
        rim_popup = (HtcRimButton) findViewById(R.id.rim_popup);

        mOnItemClickListener = this;
        mOnDissmissListener = this;
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Never show PopupWindow at this time.
        icon_popup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showPopupWindow(v);
                Toast.makeText(v.getContext(), "onClickListener: Never show Popup Window at this time.", Toast.LENGTH_SHORT).show();
            }
        });

        //It is a good time to show PopupWindow.
        rim_popup.setOnPressAnimationListener(new HtcButtonUtil.OnPressAnimationListener() {

            @Override
            public void onAnimationStarts(View view) {
            }

            @Override
            public void onAnimationEnds(View view) {
                showPopupWindow(view);
                Toast.makeText(view.getContext(), "onPressAnimationListener: It's a good time to show Popup Window.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationCancels(View view) {
            }
        });
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
        ((HtcIconButton)v).setColorOn(true);
    }

    private class ActionAdapter extends BaseAdapter {
        private String[] ItemStrings = new String[] { "listitem1", "listitem2", "listitem3", "listitem4", "listitem5", "listitem6" };

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
            HtcListItem listitem = (HtcListItem)mInflater.inflate(R.layout.actionbarlistitem, null);
            HtcListItem1LineCenteredText text = (HtcListItem1LineCenteredText)listitem.findViewById(R.id.text1);
            text.setText(ItemStrings[position]);
            listitem.setBackgroundDrawable(null);
            return listitem;
        }
    }

    public void onDismiss() {
        if (icon_popup != null) icon_popup.setColorOn(false);
        if (rim_popup != null) rim_popup.setColorOn(false);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    }
    //Aboves are all about showing PopupWindow

    @Override
    protected void onResume() {
        super.onResume();
        int newTheme = getIntent().getIntExtra("theme", R.style.HtcDeviceDefault);
        android.util.Log.i("HtcButton", "newTheme " + newTheme);
    }
}
