
package com.htc.lib1.cc.adapterview.activityhelper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.app.HtcShareActivity;

public class HtcShareViaDemo extends ActivityBase {
    public static final int SHARE_MORE_ITEMS = 1;
    public static final int SHARE_FEW_ITEMS = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected boolean isInitOrientation() {
        return false;
    }

    public void shareMoreItems(int theme) {

        Intent intent2Resolve = new Intent();
        intent2Resolve.setAction(Intent.ACTION_SEND);
        intent2Resolve.setType("image/*");

        Intent[] intentData = new Intent[1];
        intentData[0] = intent2Resolve;

        Intent intent = new Intent(this, HtcShareActivity.class);
        intent.putExtra(HtcShareActivity.EXTRA_INTENT_LIST, intentData);
        intent.putExtra(HtcShareActivity.EXTRA_THEME_CATEGORY, theme);
        HtcShareActivity.startActivityForResult(intent, SHARE_MORE_ITEMS, this);
    }

    public void shareFewItems(int theme) {
        String url = "http://www.example.com";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        HtcShareActivity.startActivityForResult(intent, theme, null, SHARE_FEW_ITEMS, this);
    }

}
