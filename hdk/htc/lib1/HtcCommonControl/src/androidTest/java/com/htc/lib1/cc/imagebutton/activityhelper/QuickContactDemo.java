
package com.htc.lib1.cc.imagebutton.activityhelper;

import android.net.Uri;
import android.os.Bundle;
import com.htc.lib1.cc.test.R;

import com.htc.aut.ActivityBase;
import com.htc.lib1.cc.widget.QuickContactBadge;

public class QuickContactDemo extends ActivityBase {

    private final String ACCESSIBILITY_CONTENT_DESCRIPTION = "Should be set by AP";
    private static final int MODE_LIGHT = 1;
    private static final int MODE_DARK = 2;
    private static final boolean WITH_SECONDIMAGE = true;
    private static final boolean NO_SECONDIMAGE = false;
    private static final int CONTACTMODE_PHONE = 1;
    private static final int CONTACTMODE_EMAIL = 2;
    private static final int CONTACTMODE_URI = 3;
    private QuickContactBadge mQuickContactBadge;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qc_main);
        /*
         * test_Light_NoImage_NoContact
         */
        setAppearanceParams(R.id.light, MODE_LIGHT, NO_SECONDIMAGE, 0);
        /*
         * test_Light_NoImage_WithContact
         */
        setAppearanceParams(R.id.light_contact, MODE_LIGHT, NO_SECONDIMAGE, CONTACTMODE_URI);
        /*
         * test_Light_WithImage_NoContact
         */
        setAppearanceParams(R.id.light_image, MODE_LIGHT, WITH_SECONDIMAGE, 0);
        /*
         * test_Light_WithImage_WithContact
         */
        setAppearanceParams(R.id.light_contact_image, MODE_LIGHT, WITH_SECONDIMAGE, CONTACTMODE_PHONE);
        /*
         * test_Dark_NoImage_NoContact
         */
        setAppearanceParams(R.id.dark, MODE_DARK, NO_SECONDIMAGE, 0);
        /*
         * test_Dark_NoImage_WithContact
         */
        setAppearanceParams(R.id.dark_contact, MODE_DARK, NO_SECONDIMAGE, CONTACTMODE_URI);
        /*
         * test_Dark_WithImage_NoContact
         */
        setAppearanceParams(R.id.dark_image, MODE_DARK, WITH_SECONDIMAGE, 0);
        /*
         * test_Drak_WithImage_WithContact
         */
        setAppearanceParams(R.id.dark_contact_image, MODE_DARK, WITH_SECONDIMAGE, CONTACTMODE_EMAIL);
    }

    public void setAppearanceParams(int id, int mode, boolean image, int contactMode) {
        mQuickContactBadge = (QuickContactBadge) findViewById(id);
        mQuickContactBadge.setImageResource(R.drawable.icon_category_photo);
        mQuickContactBadge.setContentDescription(ACCESSIBILITY_CONTENT_DESCRIPTION);
        if (mode == MODE_DARK) {
            mQuickContactBadge.setDarkMode();
        }
        if (image == WITH_SECONDIMAGE) {
            mQuickContactBadge.setSecondaryImageResource(R.drawable.icon_indicator_facebook_s);
        }
        switch (contactMode) {
            case CONTACTMODE_PHONE:
                mQuickContactBadge.assignContactFromPhone("131", true);
                break;
            case CONTACTMODE_EMAIL:
                mQuickContactBadge.assignContactFromEmail("a@b.c.com", true);
                break;
            case CONTACTMODE_URI:
                Uri uri = Uri.parse("http://www.baidu.com");
                mQuickContactBadge.assignContactUri(uri);
            default:
                break;
        }
    }

    @Override
    protected boolean isInitOrientation() {
        return false;
    }
}
