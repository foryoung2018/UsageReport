package com.htc.sense.commoncontrol.demo.htcsharevia;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.htc.lib1.cc.widget.HtcAlertDialog;
import com.htc.lib1.cc.widget.HtcPopupWindowWrapper;
import com.htc.lib1.cc.widget.HtcPopupWindowWrapper.ShareViaOnItemClickListener;
import com.htc.lib1.cc.widget.HtcShareViaAdapter;
import com.htc.lib1.cc.widget.HtcShareViaDialogOnClickListener;
import com.htc.lib1.cc.widget.HtcShareViaMultipleAdapter;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AUTHtcShareViaDemo extends CommonDemoActivityBase {
    Context mContext;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        setContentView(R.layout.htcsharevia_demo_main);

        // Android ShareVia
        Button androidshare = (Button) findViewById(R.id.android_sharevia);
        androidshare.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent i = createShareIntent();
                startActivity(i);
            }
        });

        // HtcAlertDialog ShareVia (single intent)
        Button htcalert = (Button) findViewById(R.id.android_alert);
        htcalert.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                final HtcShareViaAdapter mAdapter = new HtcShareViaAdapter(
                        createShareIntent(), mContext);

                DialogInterface.OnClickListener mCustomClickListener;
                mCustomClickListener= new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = (Intent)mAdapter.getItem(which); /* must-be */
                        dialog.dismiss(); /* must-be */

                        // TODO Apllication's TODO below.
                        Toast.makeText(mContext, "Item " + which + ", i=" + i, Toast.LENGTH_SHORT).show();
                    }
                };

                // Google
                AlertDialog d = new AlertDialog.Builder(mContext).create();
                HtcShareViaDialogOnClickListener mListener = new HtcShareViaDialogOnClickListener(
                        d, mAdapter, mCustomClickListener);
                new AlertDialog.Builder(mContext)
                .setTitle(com.htc.lib1.cc.R.string.common_string_share_title)
                .setSingleChoiceItems(mAdapter, 0, mListener)
                .show();
            }
        });

        Button alert = (Button) findViewById(R.id.alert);
        alert.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                final HtcShareViaAdapter mAdapter = new HtcShareViaAdapter(
                        createShareIntent(), mContext);

                DialogInterface.OnClickListener mCustomClickListener;
                mCustomClickListener= new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = (Intent)mAdapter.getItem(which); /* must-be */
                        dialog.dismiss(); /* must-be */

                        // TODO Apllication's TODO below.
                        Toast.makeText(mContext, "Item " + which + ", i=" + i, Toast.LENGTH_SHORT).show();
                    }
                };

                // HTC
                HtcShareViaDialogOnClickListener mListener = new HtcShareViaDialogOnClickListener(
                        mAdapter, mCustomClickListener);

               new HtcAlertDialog.Builder(mContext)
                   .setTitle(com.htc.lib1.cc.R.string.common_string_share_title)
                   .setSingleChoiceItems(mAdapter, 0, mListener)
                   .show();
            }
        });

        // HtcPopup ShareVia (single intent)
        Button popup = (Button) findViewById(R.id.popup);
        popup.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                final HtcShareViaAdapter mAdapter = new HtcShareViaAdapter(createShareIntent(), mContext);
                final HtcPopupWindowWrapper mWrapper = new HtcPopupWindowWrapper();

                AdapterView.OnItemClickListener mCustomClickListener;
                mCustomClickListener = new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                            int position, long id) {
                        Intent i = (Intent)mAdapter.getItem(position); /* must-be */
                        mWrapper.dismiss(); /* must-be */

                        // TODO Application's TODO below.
                        Toast.makeText(mContext,
                                "Item " + position + ", i=" + i, Toast.LENGTH_SHORT).show();
                    }
                };

                ShareViaOnItemClickListener mListener = mWrapper.new ShareViaOnItemClickListener(mAdapter, mCustomClickListener);

                mWrapper.setOnItemClickListener(mListener);
                mWrapper.setArchorView(v);
                mWrapper.setAdapter(mAdapter);
                mWrapper.showPopupWindow();
            }
        });

        // HtcAlertDialog Multi ShareVia (multiple intent)
        Button multi = (Button) findViewById(R.id.alert_multi);
        multi.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                List<Intent> mIntents = new ArrayList<Intent>();
                mIntents.add(createShareIntent());
                mIntents.add(createShareIntent2());

                final HtcShareViaMultipleAdapter mAdapter = new HtcShareViaMultipleAdapter(mIntents, mContext);

                DialogInterface.OnClickListener mCustomClickListener;
                mCustomClickListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ResolveInfo ri = (ResolveInfo)mAdapter.getItem(which); /* must-be */
                        dialog.dismiss(); /* must-be */

                        // TODO Application's TODO below.
                        Toast.makeText(mContext, "Item " + which + ", ri=" + ri, Toast.LENGTH_SHORT).show();
                    }
                };

                HtcShareViaDialogOnClickListener mListener = new HtcShareViaDialogOnClickListener(mAdapter, mCustomClickListener);

                new HtcAlertDialog.Builder(mContext)
                        .setTitle(com.htc.lib1.cc.R.string.common_string_share_title)
                        .setSingleChoiceItems(mAdapter, 0, mListener)
                        .show();
            }
        });

        // HtcPopup Multi ShareVia (multiple intent)
        Button popupmulti = (Button) findViewById(R.id.popup_multi);
        popupmulti.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                List<Intent> mIntents = new ArrayList<Intent>();
                mIntents.add(createShareIntent());
                mIntents.add(createShareIntent2());

                final HtcShareViaMultipleAdapter mAdapter = new HtcShareViaMultipleAdapter(
                        mIntents, mContext);
                final HtcPopupWindowWrapper mWrapper = new HtcPopupWindowWrapper();

                AdapterView.OnItemClickListener mCustomClickListener;
                mCustomClickListener= new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                            int position, long id) {
                        ResolveInfo ri = (ResolveInfo)mAdapter.getItem(position);
                        mWrapper.dismiss();

                        // TODO Application's TODO below.
                        Toast.makeText(mContext, "Item " + position + ", ri=" + ri, Toast.LENGTH_SHORT).show();
                    }
                };

                ShareViaOnItemClickListener mListener = mWrapper.new ShareViaOnItemClickListener(mAdapter, mCustomClickListener);

                mWrapper.setOnItemClickListener(mListener);
                mWrapper.setArchorView(v);
                mWrapper.setAdapter(mAdapter);
                mWrapper.showPopupWindow();
            }
        });

        // Share List Activity
        Button sharelist = (Button) findViewById(R.id.share_list_activity);
        sharelist.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent("com.htc.app.SHARE");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("SHARED_CONTENT", "ShareVia Test");
                intent.putExtra("ORIGINAL_URL", "");
                intent.putExtra("LONG_URL", "");
                startActivity(intent);
            }
        });
    }

    private Intent createShareIntent() {
        String temp = "png";
        Intent i = new Intent(Intent.ACTION_SEND);
        String strMimeType = MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(temp);
        i.setType(strMimeType);
        return i;
    }

    private Intent createShareIntent2() {
        String file = "hello.txt";
        String temp = "mp3";
        Intent i = new Intent(Intent.ACTION_SEND);
        File f = getFilesDir();
        File f2 = new File(f.toString() + "/" + file);
        String strMimeType = MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(temp);
        android.util.Log.e("PAUL", "MimeType: " + strMimeType);
        i.setType(strMimeType);
        android.util.Log.e("PAUL", "Uri: " + Uri.fromFile(f2));
        i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f2));
        return i;
    }

    private Intent createShareIntent3() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/png");
        Uri uri = null;
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        return shareIntent;
    }
}