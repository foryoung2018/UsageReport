package com.htc.lib1.cc.app;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.util.HtcWrapConfigurationUtil;
import com.htc.lib1.cc.widget.HtcAlertDialog;
import com.htc.lib1.cc.widget.HtcShareViaAdapter;
import com.htc.lib1.cc.widget.HtcShareViaDialogOnClickListener;

/**
  * This is an activity to show HtcAlertDialog for HtcShareVia used in text selection.
  */
/**@hide*/
public class ShareListActivity extends Activity {
    private final String TAG = ShareListActivity.class.getSimpleName();
    private HtcAlertDialog mDialog;
    private String mSharedContent;
    private String mShortenedUrl;
    private String mOriginalUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HtcWrapConfigurationUtil.applyHtcFontscale(this); // Htc font scale
        setTheme(HtcCommonUtil.getHtcThemeId(this, HtcCommonUtil.BASELINE)); // multiple theme

        Intent from = getIntent();
        mSharedContent = from.getStringExtra("SHARED_CONTENT");
        mShortenedUrl = from.getStringExtra("SHORTENED_URL");
        mOriginalUrl = from.getStringExtra("ORIGINAL_URL");
        if (mSharedContent == null)
            mSharedContent = "";
        if (mShortenedUrl == null)
            mShortenedUrl = "";
        if (mOriginalUrl == null)
            mOriginalUrl = "";

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, mShortenedUrl);
        intent.putExtra(Intent.EXTRA_TEXT, mSharedContent);

        List<String> excludes = null;
        if (mOriginalUrl == null || mOriginalUrl.length() == 0) {
            // Remove com.facebook.katana.ShareLinkActivity if URL is null or empty.
            excludes = new ArrayList<String>();
            excludes.add("com.facebook.katana");
        }

        final HtcShareViaAdapter mAdapter = new HtcShareViaAdapter(intent, null,
                excludes, getApplicationContext());
        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent i = (Intent)mAdapter.getItem(which); /* must-be */
                dialog.dismiss(); /* must-be */

                // TODO Application's TODO below.
                if (i != null) {
                    String packageName = i.getComponent().getPackageName();
                    String name = i.getComponent().getClassName();

                    if (packageName.equals("com.htc.sense.htctwitter") ||
                        packageName.equals("com.htc.sense.friendstream") ||
                        packageName.equals("com.htc.sense.socialnetwork.plurk")) {

                        i.setAction("share");
                        i.putExtra(Intent.EXTRA_TITLE, mSharedContent);
                        i.putExtra(Intent.EXTRA_TEXT, mShortenedUrl);

                    } else if (name.startsWith("com.facebook.katana"/*.ShareLinkActivity"*/)) {
                        i.putExtra(Intent.EXTRA_TEXT, mOriginalUrl);
                        i.putExtra(Intent.EXTRA_SUBJECT, mSharedContent);
                    }

                    try {
                        startActivity(i);
                    } catch (ActivityNotFoundException e) {
                        Log.w(TAG, "Unable to launch an activity " + i);
                    }
                } else {
                    Log.e(TAG, "Unable to get intent from the adapter!");
                }

                finish();
            }
        };
        DialogInterface.OnCancelListener cancel = new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        };
        HtcShareViaDialogOnClickListener listener = new HtcShareViaDialogOnClickListener(
                mAdapter, clickListener);
        mDialog = new HtcAlertDialog.Builder(this)
            .setTitle(com.htc.lib1.cc.R.string.quickselection_share)
            .setSingleChoiceItems(mAdapter, 0, listener)
            .setOnCancelListener(cancel)
            .create();
        mDialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        // Since the database is supposed to reset when the dialog shows up,
        // ShareListActivity cannot be launched as a single task. Therefore,
        // finish() is called forcibly.
        finish();
    }
}
