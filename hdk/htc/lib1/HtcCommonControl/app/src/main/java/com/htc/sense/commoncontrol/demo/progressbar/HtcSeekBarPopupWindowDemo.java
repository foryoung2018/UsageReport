package com.htc.sense.commoncontrol.demo.progressbar;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.widget.HtcPopupContainer;
import com.htc.lib1.cc.widget.HtcSeekBar;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

public class HtcSeekBarPopupWindowDemo extends CommonDemoActivityBase implements
        OnSeekBarChangeListener, OnClickListener {
    HtcPopupContainer mSeekBarPopupWindow;
    View mPopupContent;
    boolean mVisible = true;
    boolean mPopupVisible = true;
    boolean mNotes = false;
    int mProgress = 0;
    boolean mSize = true;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seekbar_popupwindow);


        mPopupContent = getLayoutInflater().inflate(R.layout.seekbar_popup_onetext, null);
        mSeekBarPopupWindow = new HtcPopupContainer(this);
        mSeekBarPopupWindow.setContentView(mPopupContent);

        SeekBar sb = (SeekBar) findViewById(R.id.seekbar_top);
        sb.setOnSeekBarChangeListener(this);

        sb = (SeekBar) findViewById(R.id.seekbar_middle);
        sb.setOnSeekBarChangeListener(this);

        sb = (SeekBar) findViewById(R.id.seekbar_bottom);
        sb.setOnSeekBarChangeListener(this);

        Button b = (Button) findViewById(R.id.cmd_bar_imgbtn_3_port);
        b.setOnClickListener(this);
        b = (Button) findViewById(R.id.cmd_bar_imgbtn_4_port);
        b.setOnClickListener(this);
        b = (Button) findViewById(R.id.cmd_bar_imgbtn_2_port);
        b.setOnClickListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromUser) {
        // TODO Auto-generated method stub
        TextView tv = (TextView) mPopupContent.findViewById(R.id.textView1);

        tv.setText(String.format("00:26/00:%02d", progress));

           mSeekBarPopupWindow.updatePopupPosition(seekBar, progress);
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
        TextView tv = (TextView)  mPopupContent.findViewById(R.id.textView1);
        tv.setText("start");


        mSeekBarPopupWindow.showAsDropDown(seekBar);
    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
        TextView tv = (TextView)  mPopupContent.findViewById(R.id.textView1);
        tv.setText("stop");
        mSeekBarPopupWindow.dismiss();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        if ( v.getId() == R.id.cmd_bar_imgbtn_5_port ) {
            HtcSeekBar hsb = (HtcSeekBar) findViewById(R.id.mediacontroller_progress_port);
            hsb.setProgress(mProgress * 10);
            mProgress = (mProgress + 1)%11;
        } else if (v.getId() == R.id.cmd_bar_imgbtn_3_port  ) {
            /* replaced by mSeekBarPopupWindow.setAlignType */
//            mNotes = ( mNotes )?false:true;
//            mSeekBarPopupWindow.enableNoteLayouter(mNotes);
        } else if (v.getId() == R.id.cmd_bar_imgbtn_4_port  ) {
            HtcSeekBar hsb = (HtcSeekBar) findViewById(R.id.seekbar_top);
            mVisible = (hsb.getThumb().isVisible())?false:true;
            hsb.getThumb().setVisible(mVisible, true);
            hsb.setThumbVisible(mVisible);
            hsb = (HtcSeekBar) findViewById(R.id.seekbar_middle);
            mVisible = (hsb.getThumb().isVisible())?false:true;
            hsb.getThumb().setVisible(mVisible, true);
            hsb.setThumbVisible(mVisible);
            hsb = (HtcSeekBar) findViewById(R.id.seekbar_bottom);
            mVisible = (hsb.getThumb().isVisible())?false:true;
            hsb.getThumb().setVisible(mVisible, true);
            hsb.setThumbVisible(mVisible);
        } else if (v.getId() == R.id.cmd_bar_imgbtn_1_port  ) {
            /* replaced by mSeekBarPopupWindow.setVisibility */
            mSeekBarPopupWindow.setVisibility(mSeekBarPopupWindow.isShown()?View.GONE:View.VISIBLE);
        } else if (v.getId() == R.id.cmd_bar_imgbtn_2_port  ) {
            mSize = ( mSize )? false:true;

            View tv = mPopupContent.findViewById(R.id.textView1);
            LayoutParams lp =  tv.getLayoutParams();
            if ( mSize ) {
                lp.width = LayoutParams.WRAP_CONTENT;
                lp.height = LayoutParams.WRAP_CONTENT;
            } else {
                lp.width = 300;
                lp.height = 300;
            }
        }
    }
}
