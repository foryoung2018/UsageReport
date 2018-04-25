
package com.htc.lib1.cc.imagebutton.test;

import android.provider.ContactsContract.QuickContact;
import android.view.View;

import com.htc.lib1.cc.imagebutton.activityhelper.QuickContactDemo;
import com.htc.lib1.cc.widget.QuickContactBadge;
import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;
import com.htc.lib1.cc.test.R;

public class QuickContactTest extends HtcActivityTestCaseBase {

    public QuickContactTest() {
        super(QuickContactDemo.class);
    }

    public final void testOnCreateBundle() {
        assertNotNull(mActivity);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityIntent(null);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
    }

    public void testLight() {
        test(R.id.light);
    }

    public void testLight_Contact() {
        test(R.id.light_contact);
    }

    public void testLight_Image() {
        test(R.id.light_image);
    }

    public void testLight_Contact_Image() {
        test(R.id.light_contact_image);
    }

    public void testDark() {
        test(R.id.dark);
    }

    public void testDark_Contact() {
        test(R.id.dark_contact);
    }

    public void testDark_Image() {
        test(R.id.dark_image);
    }

    public void testDark_Contact_Image() {
        test(R.id.dark_contact_image);
    }

    public void testSetAutoMotive() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                QuickContactBadge mQuickContact = new QuickContactBadge(mActivity);
                mQuickContact.setAutoMotiveMode(true);
            }
        });
    }

    public void testSetCallBack() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                QuickContactBadge mQuickContact = new QuickContactBadge(mActivity);
                mQuickContact.setCallback(null);
            }
        });
    }

    public void testSetOnClickListener() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                QuickContactBadge mQuickContact = new QuickContactBadge(mActivity);
                mQuickContact.setDefaultOnClickListener(true);
            }
        });
    }

    public void testSetExcludeMimes() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                QuickContactBadge mQuickContact = new QuickContactBadge(mActivity);
                mQuickContact.setExcludeMimes(null);
            }
        });
    }

    public void testSetIconForImageRes() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                QuickContactBadge mQuickContact = new QuickContactBadge(mActivity);
                mQuickContact.setIconForImageRes(true);
            }
        });
    }

    public void testSetImageMatrix() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                QuickContactBadge mQuickContact = new QuickContactBadge(mActivity);
                mQuickContact.setImageMatrix(null);
            }
        });
    }

    public void testSetMode() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                QuickContactBadge mQuickContact = new QuickContactBadge(mActivity);
                mQuickContact.setMode(QuickContact.MODE_MEDIUM);
            }
        });
    }

    public void testSetSecondaryImage() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                QuickContactBadge mQuickContact = new QuickContactBadge(mActivity);
                mQuickContact.setSecondaryImageBitmap(null);
                mQuickContact.setSecondaryImageDrawable(null);            }
        });

    }

    public void testSetSelected() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                QuickContactBadge mQuickContact = new QuickContactBadge(mActivity);
                mQuickContact.setSelectedContactsAppTabIndex(1);
            }
        });
    }

    private void test(int id) {
        assertNotNull(mActivity);
        getInstrumentation().waitForIdleSync();
        ScreenShotUtil.AssertViewEqualBefore(mSolo, mSolo.getView(id), this);
    }
}
