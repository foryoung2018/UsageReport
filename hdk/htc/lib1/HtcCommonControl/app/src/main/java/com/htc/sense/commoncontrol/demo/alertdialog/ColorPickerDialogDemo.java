
package com.htc.sense.commoncontrol.demo.alertdialog;

import com.htc.lib1.cc.widget.ColorPickerDialog;
import com.htc.lib1.cc.widget.ColorPickerDialog.ColorSelectedListener;
import com.htc.lib1.cc.widget.HtcGridView;
import com.htc.lib1.cc.widget.ColorPickerDialog.Builder;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class ColorPickerDialogDemo extends CommonDemoActivityBase implements
        android.view.View.OnClickListener {

    private static final int DIALOG_ONEBUTTON = 1;
    private static final int DIALOG_TWOBUTTONS = 2;
    private static final int DIALOG_NOBUTTON = 3;
    private static final int DIALOG_PADDING = 4;
    private static final int DIALOG_ITEM = 5;
    private static final int DIALOG_NONUMCOL = 6;
    private static final int DIALOG_NULLARRAYCOLOR = 7;
    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.colorpicker_dialog);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.onebutton:
                myShowDialog(DIALOG_ONEBUTTON);
                break;

            case R.id.twobutton:
                myShowDialog(DIALOG_TWOBUTTONS);
                break;

            case R.id.nobutton:
                myShowDialog(DIALOG_NOBUTTON);
                break;

            case R.id.nopadding:
                myShowDialog(DIALOG_PADDING);
                break;

            case R.id.itemclick:
                myShowDialog(DIALOG_ITEM);
                break;

            case R.id.nonumcol:
                myShowDialog(DIALOG_NONUMCOL);
                break;

            case R.id.nullarraycolor:
                myShowDialog(DIALOG_NULLARRAYCOLOR);
                break;
        }
    }

    private void myShowDialog(int id) {
        final String va_ok = getString(R.string.va_ok);
        final String va_cancel = getString(R.string.va_cancel);
        int[] oneBtn = {
                0x00FF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF,
                0xFFFFFF00, 0xFFFF0300, 0xFFFF00FF, 0x00FF0000,
                0xFFFFFF00, 0xFFFF0300, 0xFFFF00FF, 0x00FF0000
        };

        int[] twoBtn = {
                0x00FF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF,
                0xFFFFFF00, 0xFFFF0300, 0xFFFF00FF, 0x00FF0000
        };

        int[] noBtn = {
                0x00FF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF,
                0xFFFFFF00, 0xFFFF0300, 0xFFFF00FF, 0x00FF0000,
                0xFFFF00FF, 0x00FF0000, 0xFFFFFF00, 0xFFFF00FF,
                0xFFFFFF00, 0xFFFF0300, 0xFFFF00FF, 0x00FF0000,
        };
        switch (id) {

            case DIALOG_ONEBUTTON:
                new ColorPickerDialog.Builder(mContext)
                        .setNumColumns(3)
                        .setColorArray(oneBtn)
                        .setTitle(R.string.colorPickerdialog)
                        .setNegativeButton(va_cancel, null)
                        .create().show();
                break;

            case DIALOG_TWOBUTTONS:
                new ColorPickerDialog.Builder(mContext)
                        .setNumColumns(4)
                        .setColorArray(twoBtn)
                        .setTitle(R.string.colorPickerdialog)
                        .setPositiveButton(va_ok, null)
                        .setNegativeButton(va_cancel, null)
                        .create().show();
                break;

            case DIALOG_NOBUTTON:
                new ColorPickerDialog.Builder(mContext)
                        .setNumColumns(4)
                        .setColorArray(noBtn)
                        .setTitle(R.string.colorPickerdialog)
                        .create().show();
                break;

            case DIALOG_PADDING:
                ColorPickerDialog.Builder builder = new Builder(mContext);
                builder.setTitle(R.string.colorPickerdialog_setpadding);
                builder.setNumColumns(4);
                builder.setColorArray(noBtn);
                ColorPickerDialog dialog = builder.show();
                HtcGridView gv = (HtcGridView) dialog.findViewById(android.R.id.list);
                int gap = mContext.getResources().getDimensionPixelOffset(R.dimen.leading);
                gv.setPadding(gap, gap, gap, 0);
                break;

            case DIALOG_ITEM:
                new ColorPickerDialog.Builder(mContext)
                        .setNumColumns(4)
                        .setColorArray(noBtn)
                        .setOnColorClickListener(new ColorSelectedListener() {

                            @Override
                            public void setSelectedColor(int color) {
                                Toast.makeText(mContext, "The value of color = " + color, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setTitle(R.string.colorPickerdialog)
                        .create().show();
                break;

            case DIALOG_NONUMCOL:
                new ColorPickerDialog.Builder(mContext)
                        .setNumColumns(7)
                        .setColorArray(noBtn)
                        .setTitle(R.string.colorPickerdialog)
                        .create().show();
                break;

            case DIALOG_NULLARRAYCOLOR:
                new ColorPickerDialog.Builder(mContext)
                        .setNumColumns(4)
                        .setColorArray(null)
                        .setTitle(R.string.colorPickerdialog)
                        .create().show();
                break;
        }
    }
}
