
package com.htc.lib1.cc.layout.activityhelper;

import android.os.Bundle;
import android.widget.TextView;
import com.htc.lib1.cc.test.R;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.widget.Vcard;

public class HtcVcardActivity extends ActivityBase {
    private Vcard mVcardWithSmallPhoto;
    private Vcard mVcardWithBigPhoto;
    private TextView mTextName;
    private TextView mSmallPhotoTextName;

    private final static String NAME = "Jennifer Signer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vcard_demo);
        mVcardWithBigPhoto = (Vcard) findViewById(R.id.vcard_bigphoto);
        mVcardWithSmallPhoto = (Vcard) findViewById(R.id.vcard_smallphoto);
        mTextName = (TextView) mVcardWithBigPhoto.findViewById(android.R.id.text1);
        mTextName.setText(NAME);
        mSmallPhotoTextName = (TextView) mVcardWithSmallPhoto.findViewById(android.R.id.text1);
        mSmallPhotoTextName.setText(NAME);
        mVcardWithSmallPhoto.setVcardBackgroundImage(getResources().getDrawable(R.drawable.ic_launcher));
        mVcardWithBigPhoto.setVcardBackgroundImage(getResources().getDrawable(R.drawable.people_detail_photo));
    }

    @Override
    protected boolean isInitOrientation() {
        return false;
    }
}
