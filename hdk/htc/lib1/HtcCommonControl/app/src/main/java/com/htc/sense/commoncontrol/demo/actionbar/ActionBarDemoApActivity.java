
package com.htc.sense.commoncontrol.demo.actionbar;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;

import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.sense.commoncontrol.demo.CommonDemoActivityBase;
import com.htc.sense.commoncontrol.demo.R;
import com.htc.lib1.cc.widget.ActionBarContainer;
import com.htc.lib1.cc.widget.ActionBarContainer.OnPlaySoundListener;
import com.htc.lib1.cc.widget.ActionBarDropDown;
import com.htc.lib1.cc.widget.ActionBarItemView;
import com.htc.lib1.cc.widget.ActionBarQuickContact;
import com.htc.lib1.cc.widget.ActionBarSearch;
import com.htc.lib1.cc.widget.ActionBarText;

public class ActionBarDemoApActivity extends CommonDemoActivityBase {

    private View mBackground = null;
    private RadioGroup mCenterViewRadio = null;

    private ActionBarContainer mActionBarContainer = null;
    private ActionBarDropDown mActionBarDropDown = null;
    private ActionBarItemView mActionBarItemViewLeft = null;
    private ActionBarItemView mActionBarItemViewRight = null;
    private ActionBarQuickContact mActionBarQuickContactLeft = null;
    private ActionBarQuickContact mActionBarQuickContactRight = null;

    private ActionBarSearch mActionBarSearch = null;
    private ActionBarContainer mActionBarSearchContainer = null;
    private ActionBarText mActionBarText = null;
    private ActionBarContainer mAutomotiveActionBarContainer = null;
    private boolean mAutomotiveFlag = false;

    private boolean mEnableMenuFlag = false;
    private boolean mEnableCounter = false;

    private ActionMode mActionMode = null;
    private SoundPoolPlayListener mPoolPlayListener;

    private void initActionBarModule() {
        ActionBarDemoUtil.clearInstance();
        ActionBarDemoUtil abdu = ActionBarDemoUtil.getInstance(this, mAutomotiveFlag);

        mActionBarContainer = mActionBarExt.getCustomContainer();
        mActionBarSearchContainer = mActionBarExt.getSearchContainer();
        mActionBarSearchContainer.setBackgroundColor(HtcCommonUtil.getCommonThemeColor(this, com.htc.lib1.cc.R.styleable.ThemeColor_multiply_color));

        mActionBarText = abdu.initActionBarText();
        mActionBarText.setSecondaryVisibility(View.GONE);
        mActionBarSearch = abdu.initActionBarSearch();
        mActionBarDropDown = abdu.initActionBarDropDown();
        mActionBarDropDown.setSecondaryVisibility(View.GONE);
        mActionBarItemViewLeft = abdu.initActionBarItemViewLeft();
        mActionBarItemViewRight = abdu.initActionBarItemViewRight();
        mActionBarQuickContactLeft = abdu.initActionBarQuickContactLeft();
        mActionBarQuickContactRight = abdu.initActionBarQuickContactRight();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.actionbar_main);

        mBackground = findViewById(R.id.background);

        initActionBarModule();

        mCenterViewRadio = (RadioGroup) findViewById(R.id.radioGroup1);

        CheckBox cb;
        cb = (CheckBox) findViewById(R.id.checkBox1);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mActionBarContainer.addStartView(getViewWithoutParent(mActionBarItemViewLeft));
                } else {
                    getViewWithoutParent(mActionBarItemViewLeft);
                }
            }
        });

        cb = (CheckBox) findViewById(R.id.checkBox2);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mActionBarContainer.addStartView(getViewWithoutParent(mActionBarQuickContactLeft));
                    setBackupViewAndPhoto();
                } else {
                    getViewWithoutParent(mActionBarQuickContactLeft);
                }
            }
        });

        cb = (CheckBox) findViewById(R.id.secondary_show_hide);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mActionBarText.setSecondaryVisibility((isChecked) ? View.VISIBLE : View.GONE);
                mActionBarDropDown.setSecondaryVisibility((isChecked) ? View.VISIBLE : View.GONE);
            }
        });

        mCenterViewRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mActionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_NORMAL);
                switch (checkedId) {
                    case R.id.radioButton1:
                        ActionBarDemoUtil.removeActionBarContainerCenterChild(mActionBarContainer);
                        if (null != mActionBarText) {
                            mActionBarContainer.addCenterView(getViewWithoutParent(mActionBarText));
                        }
                        break;
                    case R.id.radioButton2:
                        ActionBarDemoUtil.removeActionBarContainerCenterChild(mActionBarContainer);
                        if (null != mActionBarDropDown) {
                            mActionBarDropDown.setCounter(mEnableCounter);
                            mActionBarDropDown.getCounterView().setText("(123)");
                            mActionBarContainer.addCenterView(getViewWithoutParent(mActionBarDropDown));
                            mActionBarDropDown.setSupportMode(ActionBarDropDown.MODE_EXTERNAL);
                        }
                        break;
                    case R.id.subject_popup:
                        ActionBarDemoUtil.removeActionBarContainerCenterChild(mActionBarContainer);
                        if (null != mActionBarDropDown) {
                            mActionBarContainer.addCenterView(getViewWithoutParent(mActionBarDropDown));
                            mActionBarDropDown.setCounter(mEnableCounter);
                            mActionBarDropDown.getCounterView().setText("(123)");
                            mActionBarDropDown.setSupportMode(ActionBarDropDown.MODE_ONE_MULTIILINE_TEXTVIEW);
                        }
                        break;
                    case R.id.radioButton3:
                        ActionBarDemoUtil.removeActionBarContainerCenterChild(mActionBarContainer);
                        if (null != mActionBarSearch) mActionBarContainer.addCenterView(getViewWithoutParent(mActionBarSearch));
                        break;

                    case R.id.radioButton4:
                        mActionBarContainer.setUpdatingViewText(ActionBarContainer.UPDATING_MODE_PULLDOWN, "LAST UPDATED 2012/12/12 12:12 PM");
                        mActionBarContainer.setUpdatingViewText(ActionBarContainer.UPDATING_MODE_PULLDOWN_TITLE, "Release to whatever you want");
                        mActionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_PULLDOWN);
                        break;

                    case R.id.radioButton5:
                        mActionBarContainer.setUpdatingViewText(ActionBarContainer.UPDATING_MODE_UPDATING, "Updating... (123/1234)");
                        mActionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_UPDATING);
                        break;

                    case R.id.radioButton8:
                        mActionBarContainer.setUpdatingViewText(3, "TextPrimary123456789e123456789e123456789e123456789e");
                        mActionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_UPDATING_WITH_TITLE);
                        break;

                    case R.id.radioButton9:
                        mActionBarContainer.setUpdatingViewText(4, "DropDownPrimary123456789e123456789e1234567890123456789e");
                        mActionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_UPDATING_WITH_DROPDOWN);
                        break;
                }
            }
        });

        cb = (CheckBox) findViewById(R.id.sound_checkbox);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mPoolPlayListener == null) {
                        mPoolPlayListener = new SoundPoolPlayListener(ActionBarDemoApActivity.this);
                    }
                    mActionBarContainer.setOnPlaySoundListener(mPoolPlayListener);
                } else {
                    mActionBarContainer.setOnPlaySoundListener(null);
                }
            }
        });

        cb = (CheckBox) findViewById(R.id.checkBox3);

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mActionBarContainer.addEndView(getViewWithoutParent(mActionBarItemViewRight));
                } else {
                    getViewWithoutParent(mActionBarItemViewRight);
                }
            }
        });

        cb = (CheckBox) findViewById(R.id.checkBox4);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mActionBarContainer.addEndView(getViewWithoutParent(mActionBarQuickContactRight));
                } else {
                    getViewWithoutParent(mActionBarQuickContactRight);
                }
            }
        });
        cb = (CheckBox) findViewById(R.id.checkBox5);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mActionBarContainer.setBackUpEnabled(isChecked);
                setBackupViewAndPhoto();
            }
        });

        cb = (CheckBox) findViewById(R.id.checkBox6);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mActionBarContainer.setProgressVisibility(View.VISIBLE);
                } else {
                    mActionBarContainer.setProgressVisibility(View.GONE);
                }
            }
        });

        cb = (CheckBox) findViewById(R.id.checkBox7);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enableMenu(isChecked);
            }
        });
        cb = (CheckBox) findViewById(R.id.checkBox8);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mActionMode = startActionMode(new ActionMode.Callback() {

                        @Override
                        public boolean onActionItemClicked(ActionMode mode,
                                MenuItem item) {
                            return false;
                        }

                        @Override
                        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                            getMenuInflater().inflate(R.menu.actionbar_actions, menu);
                            menu.getItem(0).setTitle(ActionBarDemoUtil.ACCESSIBILITY_CONTENT_DESCRIPTION);
                            menu.getItem(1).setTitle(ActionBarDemoUtil.ACCESSIBILITY_CONTENT_DESCRIPTION);
                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode mode) {
                            buttonView.setChecked(false);
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode mode,
                                Menu menu) {
                            return false;
                        }
                    });

                    mActionMode.setTitle("12345678901234567890123456789012345678901234567890");
                } else if (mActionMode != null) {
                    mActionMode.finish();
                }
            }
        });
        cb = (CheckBox) findViewById(R.id.checkBox9);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mAutomotiveFlag = isChecked;
                if (isChecked) {
                    mAutomotiveActionBarContainer = new ActionBarContainer(ActionBarDemoApActivity.this);
                    mAutomotiveActionBarContainer.setSupportMode(ActionBarContainer.MODE_AUTOMOTIVE);
                    ((ViewGroup) mBackground).addView(getViewWithoutParent(mAutomotiveActionBarContainer), 0);
                } else {
                    getViewWithoutParent(mAutomotiveActionBarContainer);
                }
            }
        });

        RadioGroup backgroundGroup = (RadioGroup) findViewById(R.id.background_mode);
        backgroundGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.fullscreen_mode:
                        mActionBarExt.setFullScreenEnabled(true);
                        break;
                    case R.id.transparent_mode:
                        mActionBarExt.setTransparentEnabled(true);
                        break;
                    default:
                        mActionBarExt.setFullScreenEnabled(false);
                        break;
                }
            }
        });

        cb = (CheckBox) findViewById(R.id.checkBox11);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (null != mActionBarSearch.getParent()) ((ViewGroup) mActionBarSearch.getParent()).removeView(mActionBarSearch);
                if (null != mActionBarItemViewRight.getParent()) ((ViewGroup) mActionBarItemViewRight.getParent()).removeView(mActionBarItemViewRight);

                mActionBarSearchContainer.setBackUpEnabled(true);
                mActionBarSearchContainer.addCenterView(getViewWithoutParent(mActionBarSearch));
                mActionBarSearchContainer.addEndView(getViewWithoutParent(mActionBarItemViewRight));
                mActionBarExt.switchContainer();
            }
        });

        cb = (CheckBox) findViewById(R.id.checkBox12);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                if (isChecked) {
                    getActionBar().hide();
                } else {
                    getActionBar().show();
                }
            }
        });

        cb = (CheckBox) findViewById(R.id.checkBox13);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mEnableCounter = true;
                } else {
                    mEnableCounter = false;
                }
            }
        });

        SeekBar sb = (SeekBar) findViewById(R.id.seekBar1);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mActionBarContainer.setRotationProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() != seekBar.getMax()) {
                    mActionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_NORMAL);
                } else {
                    mActionBarContainer.setUpdatingViewText(ActionBarContainer.UPDATING_MODE_UPDATING_WITH_TITLE, "UpdatingTitle123456789012345678901234567890");
                    mActionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_UPDATING_WITH_TITLE);
                    mActionBarContainer.setUpdatingViewClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            mActionBarContainer.setUpdatingState(ActionBarContainer.UPDATING_MODE_NORMAL);
                        }
                    });
                }
            }
        });
    }

    public void enableMenu(boolean enable) {

        mEnableMenuFlag = enable;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        if (mEnableMenuFlag) {
            getMenuInflater().inflate(R.menu.actionbar_actions, menu);
            menu.getItem(0).setTitle(ActionBarDemoUtil.ACCESSIBILITY_CONTENT_DESCRIPTION);
            menu.getItem(1).setTitle(ActionBarDemoUtil.ACCESSIBILITY_CONTENT_DESCRIPTION);
        } else {
            if (menu != null) {
                menu.clear();
            }
        }
        return true;
    }

    private View getViewWithoutParent(View v) {
        if (null == v) return v;

        if (null == v.getParent()) return v;

        ViewGroup vg = ((ViewGroup) v.getParent());
        vg.removeView(v);

        return v;
    }

    private void setBackupViewAndPhoto() {
        CheckBox cb = (CheckBox) findViewById(R.id.checkBox2);
        if (null == cb) return;
        if (!cb.isChecked()) return;

        cb = (CheckBox) findViewById(R.id.checkBox5);
        if (null == cb) return;

        mActionBarQuickContactLeft.setLeftMarginEnabled((cb.isChecked()) ? false : true);
        mActionBarQuickContactLeft.invalidate();
        mActionBarQuickContactLeft.requestLayout();
        mActionBarQuickContactLeft.getParent().requestLayout();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPoolPlayListener != null) {
            mPoolPlayListener.release();
            mPoolPlayListener = null;
        }
    }

    private static class SoundPoolPlayListener implements OnPlaySoundListener {
        private SoundPool mSoundPool;
        private int mSoundPullDown = -1, mSoundUpdating = -1;
        private Context mContext;

        public SoundPoolPlayListener(Context context) {
            mContext = context;
        }

        @Override
        public void onPlaySournd(int type) {
            if (mSoundPool == null) {
                mSoundPool = new SoundPool(2, AudioManager.STREAM_SYSTEM, 0);
                mSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {

                    @Override
                    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                        if (status == 0) {
                            playSoundEffect(sampleId);
                        }
                    }
                });
            }

            if (type == ActionBarContainer.UPDATING_MODE_UPDATING) {
                if (mSoundUpdating == -1) {
                    mSoundUpdating = mSoundPool.load(mContext, R.raw.unlock, 1);
                    return;
                }
                playSoundEffect(mSoundUpdating);
            } else if (type == ActionBarContainer.UPDATING_MODE_PULLDOWN) {
                if (mSoundPullDown == -1) {
                    mSoundPullDown = mSoundPool.load(mContext, R.raw.lock, 1);
                    return;
                }
                playSoundEffect(mSoundPullDown);
            }
        }

        private void playSoundEffect(int soundId) {
            if (mSoundPool != null) {
                mSoundPool.play(soundId, 1, 1, 0, 0, 1);
            }
        }

        public void release() {
            if (mSoundPool != null) {
                mSoundPool.release();
                mSoundPool = null;
                mSoundUpdating = mSoundPullDown = -1;
            }
        }
    }

    @Override
    protected void applyCustomWindowFeature() {
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
    }

}
