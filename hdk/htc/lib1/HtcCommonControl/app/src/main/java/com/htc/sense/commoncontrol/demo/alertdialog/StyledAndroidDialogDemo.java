package com.htc.sense.commoncontrol.demo.alertdialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// about check box:
// states: 1. unchecked+rest 2. pressed 3. unchecked+rest

public class StyledAndroidDialogDemo extends CommonDemoActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.android_alert_demo);

        View b1302 = findViewById(R.id.button1302);
        b1302.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(1302);
            }
        });
        View b1502 = findViewById(R.id.button1502);
        b1502.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(1502);
            }
        });
        View b1112 = findViewById(R.id.button1112);
        b1112.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(1112);
            }
        });
        View b1000 = findViewById(R.id.button1000);
        b1000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(1000);
            }
        });
        View b3 = findViewById(R.id.button3);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(3);
            }
        });
        View b4 = findViewById(R.id.button4);
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(4);
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 4:
                // progress dialog "Android style"
                final ProgressDialog qDialog = new ProgressDialog(this);
                qDialog.setMessage("TO RESTART YOUR PHONE, PRESS AND HOLD THE POWER AND VOLUME UP BUTTONS FOR 10 SECONDS.");
                qDialog.setIndeterminate(true);
                return qDialog;

            case 3:
                // progress dialog "Htc style"
                final ProgressDialog pDialog = new ProgressDialog(this, R.style.HtcAlertDialogTheme);
                pDialog.setMessage("TO RESTART YOUR PHONE, PRESS AND HOLD THE POWER AND VOLUME UP BUTTONS FOR 10 SECONDS.");
                pDialog.setIndeterminate(true);
                pDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        TextView message = (TextView) pDialog.findViewById(android.R.id.message);
                        message.setTextAppearance(StyledAndroidDialogDemo.this, R.style.fixed_b_button_primary_m);

                        View view = pDialog.findViewById(android.R.id.progress);
                        view.setPadding(0, getResources().getDimensionPixelSize(R.dimen.margin_m_2), 0, 0);
                    }
                });
                return pDialog;

            case 1000:
                String[] from = {"icon", "action", "description"};
                int[] to = {R.id.icon, R.id.action, R.id.description};
                List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
                Map<String, Object> tmp;
                // 0
                tmp = new HashMap<String, Object>();
                tmp.put(from[0], R.drawable.icon_launcher_power_off);
                tmp.put(from[1], "Power off");
                tmp.put(from[2], "Shut phone down");
                data.add(tmp);

                // 1
                tmp = new HashMap<String, Object>();
                tmp.put(from[0], R.drawable.icon_launcher_airplane);
                tmp.put(from[1], "Airplane mode");
                tmp.put(from[2], "Airplane mode is OFF");
                data.add(tmp);

                // 2
//                tmp = new HashMap<String, Object>();
//                tmp.put(from[0], android.R.drawable.ic_media_ff);
//                tmp.put(from[1], "Kid Mode");
//                tmp.put(from[2], "Enter Kid Mode");
//                data.add(tmp);

                // 1
                tmp = new HashMap<String, Object>();
                tmp.put(from[0], R.drawable.icon_launcher_restart);
                tmp.put(from[1], "Restart");
                tmp.put(from[2], "Close all apps and restart phone");
                data.add(tmp);


                ListAdapter adapter = new SimpleAdapter(this, data, R.layout.phone_options_item, from, to);
                return new AlertDialog.Builder(this, R.style.HtcAlertDialogTheme)
                        .setTitle("Phone options")
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("henry", "AndroidAlertDemo..1000: item clicked. which=" + which);
                            }
                        })
                        .create();

            case 1112:
                View checkView = getLayoutInflater().inflate(R.layout.check_panel, null);
                return new AlertDialog.Builder(this, R.style.HtcAlertDialogTheme)
                        .setTitle("存取要求")
                        //.setMessage("您是否要授予 Now SMS 權限讀取簡訊？")
                        // use custom content layout to adjust margin between text message and check-box
                        .setView(checkView)
                        .setNegativeButton("允許", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("henry", "AndroidAlertDemo..1112: 允許");
                            }
                        })
                        .setPositiveButton("拒絕(3)", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("henry", "AndroidAlertDemo..1112: 拒絕(3)");
                            }
                        })
                        .create();

            case 1502:
                return new AlertDialog.Builder(this, R.style.HtcAlertDialogTheme)
                        .setTitle("Take bug report")
                        .setMessage("This will collect information about your current device state, to send as an e-mail message. It will take a little time from starting the bug report until it is ready to be sent; please be patient.")
                        .setNegativeButton("REPORT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("henry", "AndroidAlertDemo..1502: REPORT");
                            }
                        })
                        .setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("henry", "AndroidAlertDemo..1502: CANCEL");
                            }
                        })
                        .create();

            case 1302:
//                View customTitle = getLayoutInflater().inflate(R.layout.htc_alert_dialog_custom_title, null);
//                        .setCustomTitle(customTitle)
                AlertDialog ret = new AlertDialog.Builder(this, R.style.HtcAlertDialogTheme)
                        .setTitle("Restart phone?")
                        .setMessage("Restarting your phone will clear temporary files out of memory. Would you like to restart your phone now?")
                        .setNegativeButton("RESTART", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("henry", "AndroidAlertDemo..1302: RESTART");
                            }
                        })
                        .setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("henry", "AndroidAlertDemo..1302: CANCEL");
                            }
                        })
                        .create();
                DialogInterface.OnShowListener onShowListener = new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Resources res = getResources();
                        int id;
                        View v;

                        // adjust header height
                        id = res.getIdentifier("android:id/topPanel", null, null);
                        v = ((Dialog) dialog).findViewById(id);
//                        if (null != v) v.setMinimumHeight(0);

                        // tint header color
                        Drawable d = null == v ? null : v.getBackground();
//                        if (null != d) d.setTint(res.getColorStateList(R.color.htc_alertdialog_header), PorterDuff.Mode.SRC_ATOP); // you may also use color filter
//                        d.setColorFilter(0xff000000, PorterDuff.Mode.SRC_ATOP);
                        d.setColorFilter(0xffff0000, PorterDuff.Mode.SRC_ATOP);

//                        // remove divider between header and content
//                        id = res.getIdentifier("android:id/titleDivider", null, null);
//                        v = ((Dialog) dialog).findViewById(id);
//                        if (null != v) v.setVisibility(View.GONE);
//
//                        // remove footer padding and minHeight
//                        id = res.getIdentifier("android:id/buttonPanel", null, null);
//                        v = ((Dialog) dialog).findViewById(id);
//                        if (null != v) v.setMinimumHeight(0);
//                        if (null != v) v.setPadding(0, 0, 0, 0);

                    }
                };
                ret.setOnShowListener(onShowListener);
                return ret;
        }
        return super.onCreateDialog(id);
    }
}
