
package com.htc.sense.commoncontrol.demo.view;

import android.os.Bundle;
import android.widget.TextView;

import com.htc.lib1.cc.widget.Vcard;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;

public class VCardDemoActivity extends CommonDemoActivityBase {
    private Vcard mVcardWithSmallPhoto;
    private Vcard mVcardWithBigPhoto;
    private TextView mTextName;
    private TextView mSmallPhotoTextName;

    private final static String NAME = "Jennifer Signer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vcard_layout);
        mVcardWithBigPhoto = (Vcard) findViewById(R.id.vcard_bigphoto);
        mVcardWithSmallPhoto = (Vcard) findViewById(R.id.vcard_smallphoto);

        mTextName = (TextView) mVcardWithBigPhoto.findViewById(android.R.id.text1);
        mTextName.setText(NAME);
        mSmallPhotoTextName = (TextView) mVcardWithSmallPhoto.findViewById(android.R.id.text1);
        mSmallPhotoTextName.setText(NAME);

        mVcardWithSmallPhoto.setVcardBackgroundImage(getResources().getDrawable(R.drawable.ic_launcher));

        mVcardWithBigPhoto.setVcardBackgroundImage(getResources().getDrawable(R.drawable.people_detail_photo));
    }
}
