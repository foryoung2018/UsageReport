
package com.htc.lib1.cc.actionbar.test.unit;

import android.app.ActionBar;
import android.content.Context;
import android.test.UiThreadTest;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.htc.lib1.cc.actionbar.activityhelper.ActionBarMockActivity;
import com.htc.lib1.cc.actionbar.test.FieldReflection;
import com.htc.lib1.cc.actionbar.test.HtcActionBarActivityTestCase;
import com.htc.lib1.cc.widget.ActionBarContainer;
import com.htc.test.util.NoRunTheme;

@NoRunTheme
public class ActionBarContainerTest extends HtcActionBarActivityTestCase {

    public ActionBarContainerTest() {
        super(ActionBarMockActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        initActionBarContainers();
    }

    @UiThreadTest
    public void testAddStartView() {
        View v = new View(getInstrumentation().getTargetContext());
        v.setLayoutParams(new LayoutParams(0, 0));
        v.setTag("start");
        mActionBarContainer.addStartView(v);
        v = mActionBarContainer.findViewWithTag("start");
        ActionBar.LayoutParams lp = (ActionBar.LayoutParams) v.getLayoutParams();
        assertTrue(lp.gravity == Gravity.START);
    }

    @UiThreadTest
    public void testAddEndView() {
        View v = new View(this.getInstrumentation().getTargetContext());
        v.setLayoutParams(new LayoutParams(0, 0));
        v.setTag("end");
        mActionBarContainer.addEndView(v);
        v = mActionBarContainer.findViewWithTag("end");
        ActionBar.LayoutParams lp = (ActionBar.LayoutParams) v.getLayoutParams();
        assertTrue(lp.gravity == Gravity.END);
    }

    @UiThreadTest
    public void testAddCenterView() {
        View v = new View(this.getInstrumentation().getTargetContext());
        v.setLayoutParams(new LayoutParams(0, 0));
        v.setTag("center");
        mActionBarContainer.addCenterView(v);
        v = mActionBarContainer.findViewWithTag("center");
        ActionBar.LayoutParams lp = (ActionBar.LayoutParams) v.getLayoutParams();
        assertTrue(lp.gravity == Gravity.CENTER);
    }

    @UiThreadTest
    public void testSetAndGetProgressVisibility() {
        mActionBarContainer.setProgressVisibility(View.VISIBLE);
        assertEquals(View.VISIBLE, mActionBarContainer.getProgressVisibility());
        mActionBarContainer.setProgressVisibility(View.INVISIBLE);
        assertEquals(View.INVISIBLE, mActionBarContainer.getProgressVisibility());
        mActionBarContainer.setProgressVisibility(View.GONE);
        assertEquals(View.GONE, mActionBarContainer.getProgressVisibility());
    }

    @UiThreadTest
    public void testSetBackUpEnabled() {
        try {
            FieldReflection backupView = new FieldReflection(ActionBarContainer.class, false, "mBackupView");
            mActionBarContainer.setBackUpEnabled(true);
            View imgView = (View) backupView.get(mActionBarContainer);
            assertNotNull(imgView);
            assertTrue(View.VISIBLE == imgView.getVisibility());
            mActionBarContainer.setBackUpEnabled(false);
            imgView = (View) backupView.get(mActionBarContainer);
            assertNotNull(imgView);
            assertTrue(View.GONE == imgView.getVisibility());
        } catch (NoSuchFieldException e) {
            fail(e.getMessage());
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            fail(e.getMessage());
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            fail(e.getMessage());
        }

    }

    private boolean isClick = false;

    public void testSetBackUpOnClickListener() {

        try {
            this.runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mActionBarContainer.setBackUpEnabled(true);
                    mActionBarContainer.setBackUpOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            isClick = true;
                        }
                    });
                }
            });
        } catch (Throwable e) {
            fail(e.getMessage());
        }

        mSolo.clickOnImage(0);
        mSolo.sleep(1000);
        getInstrumentation().waitForIdleSync();
        assertTrue(isClick);
    }

    @UiThreadTest
    public void testSetSupportMode() {
        try {
            FieldReflection supportMode = new FieldReflection(ActionBarContainer.class, false, "mSupportMode");
            mActionBarContainer.setSupportMode(ActionBarContainer.MODE_AUTOMOTIVE);

            assertTrue(ActionBarContainer.MODE_AUTOMOTIVE == (Integer) supportMode.get(mActionBarContainer));

            mActionBarContainer.setSupportMode(ActionBarContainer.MODE_EXTERNAL);

            assertTrue(ActionBarContainer.MODE_EXTERNAL == (Integer) supportMode.get(mActionBarContainer));

        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            fail(e.getMessage());
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            fail(e.getMessage());
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            fail(e.getMessage());
        }

    }

    @UiThreadTest
    public void testSetUpdatingState() {
        mActionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_NORMAL);
        assertTrue(mActionBarContainer.getUpdatingState() == ActionBarContainer.UPDATING_MODE_NORMAL);

        mActionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_PULLDOWN);
        assertTrue(mActionBarContainer.getUpdatingState() == ActionBarContainer.UPDATING_MODE_PULLDOWN);

        mActionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_PULLDOWN_TITLE);
        assertTrue(mActionBarContainer.getUpdatingState() == ActionBarContainer.UPDATING_MODE_PULLDOWN_TITLE);

        mActionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_UPDATING);
        assertTrue(mActionBarContainer.getUpdatingState() == ActionBarContainer.UPDATING_MODE_UPDATING);

        mActionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_UPDATING_WITH_DROPDOWN);
        assertTrue(mActionBarContainer.getUpdatingState() == ActionBarContainer.UPDATING_MODE_UPDATING_WITH_DROPDOWN);

        mActionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_UPDATING_WITH_TITLE);
        assertTrue(mActionBarContainer.getUpdatingState() == ActionBarContainer.UPDATING_MODE_UPDATING_WITH_TITLE);

        mActionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_NORMAL);
        assertTrue(mActionBarContainer.getUpdatingState() == ActionBarContainer.UPDATING_MODE_NORMAL);

    }

    public void testSetUpdatingViewClickListener() {

        try {
            this.runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mActionBarContainer.setUpdatingViewText(ActionBarContainer.UPDATING_MODE_UPDATING_WITH_DROPDOWN, "TEST1234");
                    mActionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_UPDATING_WITH_DROPDOWN);

                    mActionBarContainer.setUpdatingViewClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            isClick = true;
                        }
                    });
                }
            });
        } catch (Throwable e) {
            fail(e.getMessage());
        }

        mSolo.clickOnText("TEST1234");
        mSolo.sleep(1000);
        getInstrumentation().waitForIdleSync();
        assertTrue(isClick);
    }

    private String toUpperCase(Context context, String text) {
        Class<?> cls;
        try {
            cls = Class.forName("com.htc.lib1.cc.util.res.HtcResUtil");
            Object obj = cls.newInstance();
            return (String) cls.getDeclaredMethod("toUpperCase", Context.class, String.class).invoke(obj, context, text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    @UiThreadTest
    public void testSetUpdatingViewText_String() {

        final String testString1 = "Abcd1234";
        final String testString2 = "Efgh5678";

        mActionBarContainer.setUpdatingViewText(ActionBarContainer.UPDATING_MODE_UPDATING, testString2);
        mActionBarContainer.setUpdatingViewText(ActionBarContainer.UPDATING_MODE_UPDATING_WITH_TITLE, testString1);
        mActionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_UPDATING_WITH_TITLE);

        TextView tv = (TextView) mActionBarContainer.findViewById(com.htc.lib1.cc.R.id.primary);
        assertTrue(tv.getText().equals(testString1));

        tv = (TextView) mActionBarContainer.findViewById(com.htc.lib1.cc.R.id.secondary);
        assertTrue(tv.getText().equals(toUpperCase(getInstrumentation().getTargetContext(), testString2)));
    }

    @UiThreadTest
    public void testSetUpdatingViewText_ResId() {

        final String testString1 = getInstrumentation().getTargetContext().getString(com.htc.lib1.cc.R.string.st_action_bar_pull_down);
        final String testString2 = getInstrumentation().getTargetContext().getString(com.htc.lib1.cc.R.string.st_action_bar_updating);

        mActionBarContainer.setUpdatingViewText(ActionBarContainer.UPDATING_MODE_UPDATING, com.htc.lib1.cc.R.string.st_action_bar_updating);
        mActionBarContainer.setUpdatingViewText(ActionBarContainer.UPDATING_MODE_UPDATING_WITH_TITLE, com.htc.lib1.cc.R.string.st_action_bar_pull_down);
        mActionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_UPDATING_WITH_TITLE);

        TextView tv = (TextView) mActionBarContainer.findViewById(com.htc.lib1.cc.R.id.primary);
        assertTrue(tv.getText().equals(testString1));

        tv = (TextView) mActionBarContainer.findViewById(com.htc.lib1.cc.R.id.secondary);
        assertTrue(tv.getText().equals(toUpperCase(getInstrumentation().getTargetContext(), testString2)));
    }

    private boolean mPlayPullDownSound = false;
    private boolean mPlayUpdatingSound = false;

    /**
     * TODO
     * Note : java.lang.SecurityException: com.htc.lib1.cc.test was not granted  this permission: android.permission.WRITE_SETTINGS.
     */
//    public void testSetOnPlaySoundListener_PullDown() {
//        final Lock lock = new Lock();
//        getInstrumentation().runOnMainSync(new Runnable() {
//
//            @Override
//            public void run() {
//                Settings.System.putInt(getInstrumentation().getTargetContext().getContentResolver(), "htc_pull_to_fresh_sound_enabled", 1);
//                mActionBarContainer.setOnPlaySoundListener(new OnPlaySoundListener() {
//
//                    @Override
//                    public void onPlaySournd(int type) {
//                        if (type == ActionBarContainer.UPDATING_MODE_PULLDOWN) {
//                            mPlayPullDownSound = true;
//                            lock.unlockAndNotify();
//                        }
//                    }
//                });
//                mPlayPullDownSound = false;
//                mActionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_PULLDOWN);
//            }
//        });
//        lock.waitUnlock(5000);
//        assertTrue(mPlayPullDownSound);
//    }

//    public void testSetOnPlaySoundListener_PullDown2Update() {
//        final Lock lock = new Lock();
//        getInstrumentation().runOnMainSync(new Runnable() {
//
//            @Override
//            public void run() {
//                Settings.System.putInt(getInstrumentation().getTargetContext().getContentResolver(), "htc_pull_to_fresh_sound_enabled", 1);
//                mActionBarContainer.setOnPlaySoundListener(new OnPlaySoundListener() {
//
//                    @Override
//                    public void onPlaySournd(int type) {
//                        if (type == ActionBarContainer.UPDATING_MODE_UPDATING) {
//                            mPlayUpdatingSound = true;
//                            lock.unlockAndNotify();
//                        } else if (type == ActionBarContainer.UPDATING_MODE_PULLDOWN) {
//                            mPlayPullDownSound = true;
//                            lock.unlockAndNotify();
//                        }
//                    }
//                });
//                mPlayPullDownSound = mPlayUpdatingSound = false;
//                mActionBarContainer.setRotationProgress(0, mActionBarContainer.getRotationMax());
//            }
//        });
//        lock.waitUnlock(5000);
//        getInstrumentation().runOnMainSync(new Runnable() {
//
//            @Override
//            public void run() {
//                mActionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_UPDATING);
//            }
//        });
//        lock.waitUnlock(5000);
//        assertTrue(mPlayUpdatingSound);
//        assertTrue(mPlayPullDownSound);
//    }

    @UiThreadTest
    public void testSetRotationMax() {
        mActionBarContainer.setRotationMax(0);
        assertTrue(0 == mActionBarContainer.getRotationMax());

        mActionBarContainer.setRotationMax(99);
        assertTrue(99 == mActionBarContainer.getRotationMax());
    }

    @UiThreadTest
    public void testSetRotationProgress() {
        mActionBarContainer.setRotationProgress(0);
        assertTrue(0 == mActionBarContainer.getRotationProgress());

        mActionBarContainer.setRotationProgress(mActionBarContainer.getRotationMax());
        assertTrue(mActionBarContainer.getRotationMax() == mActionBarContainer.getRotationProgress());
    }
}
