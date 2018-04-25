package com.htc.sense.commoncontrol.demo.progressbar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;

import com.htc.lib1.cc.widget.HtcMultiSeekBarDialog;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

public class HtcMultiSeekBarDialogDemo extends CommonDemoActivityBase implements
        OnClickListener {
    static final int DIALOG_SINGLE_SEEKBAR = 0;
    static final int DIALOG_MULTI_SEEKBAR = 1;

    private OnSeekBarChangeListener mSeekBarListener1;
    private OnSeekBarChangeListener mSeekBarListener2;

    private HtcMultiSeekBarDialog mMultiSeekBarDialog;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Boolean light = getIntent().getBooleanExtra("Light", false);
        setContentView(R.layout.multiseekbar_dialog);

        Switch toggle = (Switch) findViewById(R.id.backgroundMode);
        if(light)toggle.setChecked(true);

        toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton v, boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked){
                    int dialogThemeType = AlertDialog.THEME_HOLO_LIGHT;
                    Intent intent = getIntent();
                    intent.putExtra("dialogThemeType", dialogThemeType);
                    intent.putExtra("Light", true);
                    finish();
                    startActivity(intent);
                }else{
                    int dialogThemeType = AlertDialog.THEME_HOLO_DARK;
                    Intent intent = getIntent();
                    intent.putExtra("dialogThemeType", dialogThemeType);
                    intent.putExtra("Light", false);
                    finish();
                    startActivity(intent);
                }
            }
        });

        Button b;
        b = (Button) findViewById(R.id.show_one_seekbardialog);
        b.setOnClickListener(this);
        b = (Button) findViewById(R.id.show_multiseekbardialog);
        b.setOnClickListener(this);
        b = (Button) findViewById(R.id.show_one_seekbardialog_bydefault);
        b.setOnClickListener(this);
        b = (Button) findViewById(R.id.show_multiseekbardialog_bydefault);
        b.setOnClickListener(this);

        mSeekBarListener1 = new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                // TODO Auto-generated method stub
                SeekBar sb = (SeekBar) findViewById(R.id.seekBar1);
                sb.setProgress(arg0.getProgress());
            }
        };

        mSeekBarListener2 = new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                // TODO Auto-generated method stub
                SeekBar sb = (SeekBar) findViewById(R.id.seekBar2);
                sb.setProgress(arg0.getProgress());

            }
        };
    }

    private HtcMultiSeekBarDialog initHtcMultiSeekBarDialog() {
        if (null == mMultiSeekBarDialog) {
            HtcMultiSeekBarDialog hmsd = new HtcMultiSeekBarDialog(this);
            hmsd.addNewSuite();
            mMultiSeekBarDialog = hmsd;
        }

        return mMultiSeekBarDialog;
    }

    private Dialog initSingleSeekBarDialog() {
        EditText et1 = (EditText) findViewById(R.id.editText1);
        SeekBar sb1 = (SeekBar) findViewById(R.id.seekBar1);

        initHtcMultiSeekBarDialog();
        mMultiSeekBarDialog.setSuiteVisibilty(1, View.GONE);

        mMultiSeekBarDialog.setTextViewText(0, et1.getText().toString());
        mMultiSeekBarDialog.getSeekbar(0).setProgress(sb1.getProgress());
        mMultiSeekBarDialog.setSeekbarSeekListener(0, mSeekBarListener1);
        mMultiSeekBarDialog.getImageView(0).setImageResource(
                R.drawable.common_icon_all_songs_on);

        return mMultiSeekBarDialog;

    }

    private Dialog initMultiSeekBarDialog() {

        EditText et1 = (EditText) findViewById(R.id.editText1);
        EditText et2 = (EditText) findViewById(R.id.editText2);
        SeekBar sb1 = (SeekBar) findViewById(R.id.seekBar1);
        SeekBar sb2 = (SeekBar) findViewById(R.id.seekBar2);

        initHtcMultiSeekBarDialog();
        mMultiSeekBarDialog.setSuiteVisibilty(1, View.VISIBLE);

        mMultiSeekBarDialog.setTextViewText(0, et1.getText().toString());
        mMultiSeekBarDialog.setTextViewText(1, et2.getText().toString());
        mMultiSeekBarDialog.getSeekbar(0).setProgress(sb1.getProgress());
        mMultiSeekBarDialog.getSeekbar(1).setProgress(sb2.getProgress());
        mMultiSeekBarDialog.getImageView(0).setImageResource(
                R.drawable.common_icon_all_songs_on);
        mMultiSeekBarDialog.getImageView(1).setImageResource(
                R.drawable.common_icon_all_songs_on);

        mMultiSeekBarDialog.setSeekbarSeekListener(0, mSeekBarListener1);
        mMultiSeekBarDialog.setSeekbarSeekListener(1, mSeekBarListener2);

        return mMultiSeekBarDialog;

    }

    protected Dialog onCreateDialog(int id) {
        Dialog dialog;

        switch (id) {
        case DIALOG_SINGLE_SEEKBAR:
            // do the work to define the pause Dialog
            dialog = initSingleSeekBarDialog();
            break;
        case DIALOG_MULTI_SEEKBAR:
            // do the work to define the game over Dialog
            dialog = initMultiSeekBarDialog();
            break;
        default:
            dialog = null;
        }
        return dialog;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (R.id.show_one_seekbardialog == v.getId()) {
            Dialog dialog = initSingleSeekBarDialog();
            dialog.show();
        } else if (R.id.show_multiseekbardialog == v.getId()) {
            Dialog dialog = initMultiSeekBarDialog();
            dialog.show();
        } else if (R.id.show_one_seekbardialog_bydefault == v.getId()) {
            showDialog(DIALOG_SINGLE_SEEKBAR);
        } else if (R.id.show_multiseekbardialog_bydefault == v.getId()) {
            showDialog(DIALOG_MULTI_SEEKBAR);
        }
    }

    @Override
    protected void applyCustomWindowFeature() {
        int dialogThemeType = getIntent().getIntExtra("dialogThemeType", 0);
        switch(dialogThemeType){
            case AlertDialog.THEME_HOLO_DARK:
                setTheme(android.R.style.Theme_Holo);
                break;
            case AlertDialog.THEME_HOLO_LIGHT:
                setTheme(android.R.style.Theme_Holo_Light);
                break;
            default:
                setTheme(android.R.style.Theme_Holo);
                break;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onPrepareDialog(int, android.app.Dialog,
     * android.os.Bundle)
     */
    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
        // TODO Auto-generated method stub
        super.onPrepareDialog(id, dialog, args);
        if (DIALOG_SINGLE_SEEKBAR == id) {
            initSingleSeekBarDialog();
        } else if (DIALOG_MULTI_SEEKBAR == id) {
            initMultiSeekBarDialog();
        }
    }
}
