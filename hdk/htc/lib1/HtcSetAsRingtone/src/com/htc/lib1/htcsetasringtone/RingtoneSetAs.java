package com.htc.lib1.htcsetasringtone;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Contacts.People;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.cc.widget.HtcAlertDialog;
import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcListItem2LineText;
import com.htc.lib1.cc.widget.HtcRimButton;
import com.htc.lib1.htcsetasringtone.util.Constants;
import com.htc.lib1.htcsetasringtone.util.LogConfig;
import com.htc.lib1.htcsetasringtone.util.RingtoneUtil;
import com.htc.lib1.htcsetasringtone.util.StorageSrcHelper;
import com.htc.lib1.theme.ThemeType;
import com.htc.lib2.configuration.HtcWrapConfiguration;

public class RingtoneSetAs extends Activity implements OnMountListener
{
    private static final String TAG = "RingtoneTrimmer/RingtoneSetAs";
    private static final String ALREADY_WORKING = "already_working";
    private static final String DUAL_MODE_DIALOG_SHOWING = "dual_mode_dialog_showing";
    
    private boolean mAlreadyWorking;
    private boolean mDualModeDialogShowing;
    private ExternalStorageListener mStorageListener;
    private RingtoneUtil mRingtoneUtil;
    
    private HtcAlertDialog mDialog = null;
    
    private ListViewAdapter mListViewAdapter;
    private DialogInterface.OnClickListener mOnItemClickListener;
    
    // Htc font scale
    private float mHtcFontScale = 0.0f;
    
    boolean mIsDualMode = false;
    boolean mIsContactPickerExist = false;
    boolean mIsRingtonetrimmerExist = false;
    boolean mHasHtcSetRingtoneUriMethod = false;

    private boolean mIsThemeChanged = false;
    
    private StorageSrcHelper mStorageControl;
    
    private HtcCommonUtil.ThemeChangeObserver mThemeChangeObserver = new HtcCommonUtil.ThemeChangeObserver()
    {
        @Override
        public void onThemeChange(int type)
        {
            if (type == ThemeType.HTC_THEME_FULL || type == ThemeType.HTC_THEME_CC)
            {
                mIsThemeChanged = true;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        Log.d(TAG, "onCreate +++++");
        mAlreadyWorking = false;
        mDualModeDialogShowing = false;
        if (savedInstanceState != null)
        {
            mAlreadyWorking = savedInstanceState.getBoolean(ALREADY_WORKING, false);
            mDualModeDialogShowing = savedInstanceState.getBoolean(DUAL_MODE_DIALOG_SHOWING, false);
        }
        if (!mAlreadyWorking)
        {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            mStorageListener = new ExternalStorageListener(this);
            mStorageListener.registerReceiver(this);
            
            mRingtoneUtil = new RingtoneUtil(this);
            
            mStorageControl = new StorageSrcHelper();
            init();
        }
        else
        {
            // when activity has finished already and orientation changes,
            // finish new created activity
            mAlreadyWorking = false;
            this.finish();
        }
        if (mDualModeDialogShowing)
        {
            showDualModeDialog();
        }
    }
    
    @Override
    public void onPause()
    {
        super.onPause();
        if (null != mDialog)
        {
            mDialog.dismiss();
        }
    }
    
    @Override
    public void onResume()
    {
        super.onResume();
//        int themeId = HtcCommonUtil.getHtcThemeId(this, HtcCommonUtil.CATEGORYTHREE);
        
        if (mIsThemeChanged)
        {
            getWindow().getDecorView().postOnAnimation(new Runnable()
            {
                @Override
                public void run()
                {
                    HtcCommonUtil.notifyChange(RingtoneSetAs.this, HtcCommonUtil.TYPE_THEME);
                    recreate();
                }
            });
            mIsThemeChanged = false;
        }
        
        if (HtcWrapConfiguration.checkHtcFontscaleChanged(this, mHtcFontScale))
        {
            Window win;
            View decorView;
            if ((win = getWindow()) != null && (decorView = win.getDecorView()) != null)
            {
                decorView.postOnAnimation(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        recreate();
                    }
                });
            }
        }
        if (mDialog != null && !mDialog.isShowing())
        {
            mDialog.show();
        }
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mStorageListener.unregisterReceiver(this);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        if (outState == null)
        {
            outState = new Bundle();
        }
        outState.putBoolean(ALREADY_WORKING, mAlreadyWorking);
        outState.putBoolean(DUAL_MODE_DIALOG_SHOWING, mDualModeDialogShowing);
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        HtcWrapConfiguration.applyHtcFontscale(this);
    }
    
    private String mAudioId;
    private boolean mIsPlaying;
    private String mFilePath;
    private String mAudioTitle;
    private long mAudioDuration;
    private boolean mIsDRM;
    private int slotType = Constants.SLOT_TYPE_GSM;
    
    private boolean isCanTrim()
    {
        boolean result = false;
        
        if (mFilePath != null)
        {
            if (mAudioDuration == 0)
            {
                loadAudio(mFilePath);
            }
            String filePath = mFilePath.toLowerCase();
            if (filePath.endsWith("mp3") && !mIsDRM)
            {
                result = true;
            }
            else if (filePath.endsWith("aac"))
            {
                FileInputStream fis = null;
                try
                {
                    fis = new FileInputStream(mFilePath);
                    byte[] syncWord = new byte[2];
                    
                    int readResult = -1;
                    readResult = fis.read(syncWord);
                    if (-1 != readResult)
                    {
                        syncWord[1] |= Byte.parseByte("-1");
                        if (Arrays.equals(syncWord, new byte[] {-1, -1}))
                        {
                            result = true;
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if (null != fis)
                    {
                        try
                        {
                            fis.close();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return result;
    }
    
    private void loadAudio(String filePath)
    {
        MediaPlayer mediaPlayer = new MediaPlayer();
        FileInputStream fis = null;
        if (null != mediaPlayer)
        {
            try
            {
                if (filePath.startsWith("content://"))
                {
                    Log.d(TAG, "loadAudio starts with content://");
                    mediaPlayer.setDataSource(RingtoneSetAs.this, Uri.parse(filePath));
                }
                else
                {
                    Log.d(TAG, "loadAudio starts with others");
                    File file = new File(filePath);
                    fis = new FileInputStream(file);
                    mediaPlayer.setDataSource(fis.getFD());
                }
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepare();
                mAudioDuration = mediaPlayer.getDuration();
                mediaPlayer.reset();
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
            catch (IllegalArgumentException ex)
            {
                ex.printStackTrace();
            }
            finally
            {
                mediaPlayer.release();
                try
                {
                    if (null != fis)
                    {
                        fis.close();
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void init()
    {
        queryAudioInfo();

        mIsDualMode = DualModeRingtoneHelper.isDualModeExists(this);
        mIsContactPickerExist = mRingtoneUtil.isContactPickerExist();
        mIsRingtonetrimmerExist = mRingtoneUtil.isRingtonetrimmerExist();
        Log.d(TAG, "isDualMode = " + mIsDualMode + " ,isContactPickerExist = " + mIsContactPickerExist + " ,isRingtonetrimmerExist = " + mIsRingtonetrimmerExist);

        if (!mIsDualMode && !mIsContactPickerExist && !mIsRingtonetrimmerExist)
        {
            Log.d(TAG, "Not dual mode, no rington and people, so set rington directly");
            Uri ringtoneUri = mRingtoneUtil.getUriFromPhysicalPath(mFilePath);
            setPhoneRingtone(ringtoneUri);
            return;
        }

        mDialog = createDialog();
        mDialog.show();
    }
    
    private HtcAlertDialog createDialog()
    {
        if (null == mListViewAdapter)
        {
            mListViewAdapter = new ListViewAdapter();
        }

        if (null == mOnItemClickListener)
        {
            Log.d(TAG, "mOnItemClickListener == null");
            mOnItemClickListener = new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    Log.d(TAG, "mOnItemClickListener which = " + which);

                    switch (which)
                    {
                    case Constants.SET_AS_RINGTONE_DIALOG_PHONE_RINGTONE:
                    {
                        mBtnSetAsDefaultClickListener.onClick(null);
                        break;
                    }
                    case Constants.SET_AS_RINGTONE_DIALOG_CONTACT_RINGTONE:
                    {
                        mBtnSetAsContactClickListener.onClick(null);
                        break;
                    }
                    case Constants.SET_AS_RINGTONE_DIALOG_TRIM_THE_RINGTONE:
                    {
                        mBtnTrimmerClickListener.onClick(null);
                        break;
                    }
                    }
                }
            };
        }
        
        applyHtcTheme();
        applyHtcFontScale();
        
        HtcAlertDialog.Builder builder = new HtcAlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_set_as_title);
        builder.setAdapter(mListViewAdapter, mOnItemClickListener);
        
        HtcAlertDialog dialog = builder.create();
        dialog.setOnCancelListener(new OnCancelListener()
        {
            public void onCancel(DialogInterface d)
            {
                finish();
            }
        });
        return dialog;
    }
    
    private void applyHtcFontScale()
    {
        HtcWrapConfiguration.applyHtcFontscale(this);
        Resources res = getResources();
        if (res != null)
        {
            Configuration config = res.getConfiguration();
            if (config != null)
            {
                mHtcFontScale = config.fontScale;
            }
        }
    }
    
    private void applyHtcTheme()
    {
        HtcCommonUtil.initTheme(this, HtcCommonUtil.TYPE_THEME);
        HtcCommonUtil.registerThemeChangeObserver(this, ThemeType.HTC_THEME_FULL, mThemeChangeObserver);
        HtcCommonUtil.registerThemeChangeObserver(this, ThemeType.HTC_THEME_CC, mThemeChangeObserver);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }
    
    private class ListViewAdapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            Log.d(TAG, "isCanTrim = " + isCanTrim());

            if (mIsContactPickerExist)
            {
                if (isCanTrim() && mIsRingtonetrimmerExist)
                    return Constants.SET_AS_RINGTONE_DIALOG_ITEM_COUNT_HAS_PEOPLE_AND_RINGTONTRIMMER_CAN_TRIM;
                else
                    return Constants.SET_AS_RINGTONE_DIALOG_ITEM_COUNT_HAS_PEOPLE_BUT_NO_RINGTONTRIMMER_OR_CANT_TRIM;
            }
            else
            {
                if (isCanTrim() && mIsRingtonetrimmerExist)
                    return Constants.SET_AS_RINGTONE_DIALOG_ITEM_COUNT_HAS_RINGTONTRIMMER_AND_CAN_TRIM_BUT_NO_PEOPLE;
                else
                    return Constants.SET_AS_RINGTONE_DIALOG_ITEM_COUNT_HAS_NO_PEOPLE_AND_NO_RINGTONTRIMMER_OR_CANT_TRIM;
            }
        }
        
        @Override
        public Object getItem(int position)
        {
            return null;
        }
        
        @Override
        public long getItemId(int position)
        {
            return 0;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = getLayoutInflater();
            HtcListItem listItem = null;

            int correctPosition = position;
            if (!mIsContactPickerExist)
            {
                if (isCanTrim() && mIsRingtonetrimmerExist && correctPosition == 2)
                {
                    Log.d(TAG, "Show ringtontrimmer but not show people");
                    correctPosition = correctPosition + 1;
                }
            }

            switch (correctPosition)
            {
            case Constants.SET_AS_RINGTONE_DIALOG_INFO:
            {
                listItem = (HtcListItem)inflater.inflate(R.layout.common_list_item_2linetext, null);
                HtcListItem2LineText text = (HtcListItem2LineText)listItem.findViewById(R.id.text);
                text.setPrimaryText(mAudioTitle);
                text.setSecondaryText(ddTime(mAudioDuration));
                break;
            }
            case Constants.SET_AS_RINGTONE_DIALOG_PHONE_RINGTONE:
            {
                listItem = (HtcListItem)inflater.inflate(R.layout.common_list_item_rimbutton, null);
                HtcRimButton rimButton = (HtcRimButton)listItem.findViewById(R.id.button);
                rimButton.setText(R.string.dialog_btn_set_default_text);
                rimButton.setOnClickListener(mBtnSetAsDefaultClickListener);
                rimButton.setFocusable(false);
                break;
            }
            case Constants.SET_AS_RINGTONE_DIALOG_CONTACT_RINGTONE:
            {
                listItem = (HtcListItem)inflater.inflate(R.layout.common_list_item_rimbutton, null);
                HtcRimButton rimButton = (HtcRimButton)listItem.findViewById(R.id.button);
                rimButton.setText(R.string.dialog_btn_set_contact_text);
                rimButton.setOnClickListener(mBtnSetAsContactClickListener);
                rimButton.setFocusable(false);
                break;
            }
            case Constants.SET_AS_RINGTONE_DIALOG_TRIM_THE_RINGTONE:
            {
                listItem = (HtcListItem)inflater.inflate(R.layout.common_list_item_rimbutton, null);
                HtcRimButton rimButton = (HtcRimButton)listItem.findViewById(R.id.button);
                rimButton.setText(R.string.dialog_btn_trimmer_text);
                rimButton.setOnClickListener(mBtnTrimmerClickListener);
                rimButton.setFocusable(false);
                break;
            }
            }

            if (null != listItem)
                listItem.setClickable(true);
            else
                Log.d(TAG, "Adapter listItem is null");

            return listItem;
        }
        
        @Override
        public boolean isEnabled(int position)
        {
            return true;
        }
    }
    
    private void queryAudioInfo()
    {
        mAudioId = getIntent().getStringExtra("audio_id");
        if (mAudioId == null)
        {
            Log.d(TAG, "mAudioId = "  + mAudioId);
            return;
        }
        ContentResolver resolver = getContentResolver();
        
        String where = MediaStore.Audio.Media._ID + "=?";
        String selectionArgs[] = new String[] {mAudioId};
        Cursor cursor = null;
        String[] cursorCols = new String[] {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DURATION, "is_drm"};
        
        try
        {
            cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursorCols, where, selectionArgs, null);
            
            if (cursor != null && cursor.getCount() > 0)
            {
                cursor.moveToFirst();
                mFilePath = cursor.getString(0);
                mAudioTitle = cursor.getString(1);
                mAudioDuration = cursor.getLong(2);
                mIsDRM = (0 != cursor.getInt(3));
            }
            Log.d(TAG, "mFilePath = "  + mFilePath);
            Log.d(TAG, "mAudioTitle = "  + mAudioTitle);
            Log.d(TAG, "mAudioDuration = "  + mAudioDuration);
            Log.d(TAG, "mIsDRM = "  + mIsDRM);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (cursor != null)
            {
                cursor.close();
            }
        }
    }
    
    private String dd(long value)
    {
        if (value < 10)
            return "0" + value;
        else
            return String.valueOf(value);
    }
    
    public String ddTime(long value)
    {
        float valueSec = ((float)value) / 1000;
        return dd((long)(valueSec / 60)) + ":" + dd((long)(valueSec % 60));
    }
    
    private void showToast(int strId)
    {
        Toast.makeText(this, strId, Toast.LENGTH_SHORT).show();
    }
    
    private OnClickListener mBtnSetAsDefaultClickListener = new OnClickListener()
    {
        public void onClick(View v)
        {
            if (mFilePath == null || mFilePath.length() == 0)
            {
                Log.d(TAG, "Content file path is null or length is 0");

                if (mDialog != null)
                {
                    mDialog.dismiss();
                    mDialog = null;
                }

                showToast(R.string.ringtone_trimmer_set_as_default_fail);
                RingtoneSetAs.this.finish();
                return;
            }

            if (mIsDualMode)
            {
                mHasHtcSetRingtoneUriMethod = mRingtoneUtil.hasHtcSetActualDefaultRingtoneUriMethod();
                if (mHasHtcSetRingtoneUriMethod)
                {
                    Log.d(TAG, "Set rington, is dual mode and use htc set rington method");
                    mDualModeDialogShowing = true;
                    showDualModeDialog();
                }
                else
                {
                    Log.d(TAG, "Set rington, is dual mode but no htc set rington method");
                    Uri ringtoneUri = mRingtoneUtil.getUriFromPhysicalPath(mFilePath);
                    setPhoneRingtone(ringtoneUri);
                }
            }
            else
            {
                Log.d(TAG, "Set rington, not dual mode and no htc set rington method");
                Uri ringtoneUri = mRingtoneUtil.getUriFromPhysicalPath(mFilePath);
                setPhoneRingtone(ringtoneUri);
            }
        }
    };
    
    private void showDualModeDialog()
    {
        HtcAlertDialog dialog = DualModeRingtoneHelper.createDualModeDialog(RingtoneSetAs.this, mDualModeDialogListener);
        dialog.setOnDismissListener(new OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface arg0)
            {
                mDualModeDialogShowing = false;
            }
        });
        dialog.show();
    }

    private DialogInterface.OnClickListener mDualModeDialogListener = new DialogInterface.OnClickListener()
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            slotType = which;
            mDualModeDialogShowing = false;
            dialog.dismiss();

            Uri ringtoneUri = mRingtoneUtil.getUriFromPhysicalPath(mFilePath);
            setPhoneRingtone(ringtoneUri);
        }
    };

    private OnClickListener mBtnSetAsContactClickListener = new OnClickListener()
    {
        public void onClick(View v)
        {
            showContactPicker();
        }
    };
    
    public void showContactPicker()
    {
        Intent intent = new Intent(Constants.INTENT_PICK_MULIT_CONTACT);
        intent.setDataAndType(Uri.EMPTY, "vnd.android.cursor.dir/contact");
        intent.putExtra("filter_account_mode", 4);
        
        setVisible(false);
        startActivityForResult(intent, 21);
    }
    
    private OnClickListener mBtnTrimmerClickListener = new OnClickListener()
    {
        public void onClick(View v)
        {
            if (mAudioDuration > Constants.MINIMUM_TRIM_TIME)
            {
                startTrimmer();
            }
            else
            {
                showToast(R.string.toast_minimum_length_error);
            }
        }
    };
    
    private void startTrimmer()
    {
    	if (mStorageControl.checkStorageCapacity(this, mFilePath)) {
	        Intent intent = new Intent();
	        intent.setClassName("com.htc.ringtonetrimmer", "com.htc.ringtonetrimmer.RingtoneTrimmer");
	        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        intent.putExtra("audio_id", mAudioId);
	        startActivity(intent);
	        finish();
    	}
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        setVisible(false);
        if (null != mDialog)
        {
            mDialog.dismiss();
            mDialog = null;
        }
        if (LogConfig.LOGD)
            Log.d(TAG, "Request:" + requestCode + " Result:" + resultCode + ", " + RESULT_OK);
        
        if (resultCode != RESULT_OK)
        {
            setActivityResult();
            return;
        }
        
        String[] fileList = (intent == null) ? null : intent.getStringArrayExtra("output");
        if (fileList != null && fileList.length > 0)
        {
            Uri uri = mRingtoneUtil.getUriFromPhysicalPath(fileList[0]);
            mAudioId = String.valueOf(ContentUris.parseId(uri));
            init();
            return;
        }
        Uri ringtoneUri;
        /* Set as contact ringtone from Contacts picker */
        ArrayList<Integer> contactList = (intent == null) ? null : intent.getIntegerArrayListExtra("SELECTED_ID");
        if (contactList != null && contactList.size() > 0 && mFilePath != null)
        {
            // {2011/02/12 CY Tseng begin
            if (mFilePath.startsWith("content://drm/"))
            {
                ringtoneUri = Uri.parse(mFilePath);
            }
            else
            {
                // {2011/02/07 CY Tseng begin
                ringtoneUri = mRingtoneUtil.getUriFromPhysicalPath(mFilePath);
                // }2011/02/07 CY Tseng end
            }
            // }2011/02/12 CY Tseng end
            
            new Thread(new SetContactRinetoneRunnable(ringtoneUri, contactList)).start();
        }
        else
        {
            showToast(R.string.toast_contact_picker_failed);
            setActivityResult();
        }
    }
    
    private class SetContactRinetoneRunnable implements Runnable
    {
        private Uri ringtoneUri;
        private ArrayList<Integer> contactList;
        
        public SetContactRinetoneRunnable(Uri ringtoneUri, ArrayList<Integer> contactList)
        {
            this.ringtoneUri = ringtoneUri;
            this.contactList = contactList;
        }
        
        public void run()
        {
            Log.d(TAG, "setContactRingtone thread");
            setContactRingtone(ringtoneUri, contactList);
        }
    }
    
    private void setPhoneRingtone(Uri ringtoneUri)
    {
        boolean result;
        
        if (mIsDualMode && mHasHtcSetRingtoneUriMethod)
        {
            result = mRingtoneUtil.setSystemDefaultRingtone(ringtoneUri, slotType);
        }
        else
        {
            result = mRingtoneUtil.setSystemDefaultRingtone(ringtoneUri);
        }

        if (!result)
        {
            showToast(R.string.ringtone_trimmer_set_as_default_fail);
            RingtoneSetAs.this.finish();
            return;
        }

        showToast(R.string.ringtone_trimmer_set_as_default_ok);
        setActivityResult();
    }
    
    private void setActivityResult()
    {
        Intent data = RingtoneSetAs.this.getIntent().putExtra("isRingtoneBack", mIsPlaying);
        RingtoneSetAs.this.setResult(RESULT_OK, data);
        
        final Runnable r = new Runnable()
        {
            public void run()
            {
                RingtoneSetAs.this.finish();
            }
        };
        mMessageHandler.postDelayed(r, 100);
    }
    
    Handler mMessageHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
            case SHOW_ERROR:
                showToast(R.string.ringtone_trimmer_set_as_contact_fail);
                break;
            case SHOW_NO_PEOPLE:
                showToast(R.string.toast_contact_picker_failed);
                break;
            case SET_CONTACT_OK:
                showToast(R.string.ringtone_trimmer_set_as_contact_ok);
                break;
            }
        }
        
    };
    
    /**
     * setContactRingtone() tengi 2008/12/23
     * 
     * Set ringtone as contact ringtone
     * 
     * @param ringtoneUri
     * @param contactList
     */
    private final int SHOW_ERROR = 0;
    private final int SHOW_NO_PEOPLE = 1;
    private final int SET_CONTACT_OK = 2;
    
    private void setContactRingtone(Uri ringtoneUri, ArrayList<Integer> contactList)
    {
        if (ringtoneUri == null)
        {
            Log.v(TAG, "setContactRingtone() null ringtoneUri");
            mMessageHandler.sendEmptyMessage(SHOW_ERROR);
            RingtoneSetAs.this.finish();
            return;
        }
        
        mAlreadyWorking = true;
        
        if (LogConfig.LOGD)
            Log.d(TAG, "setContactRingtone():" + ringtoneUri);
        
        if (contactList == null || contactList.size() == 0)
        {
            mMessageHandler.sendEmptyMessage(SHOW_NO_PEOPLE);
            setActivityResult();
            return;
        }
        
        // Performance Improvement
        StringBuilder where = new StringBuilder();
        where.append("_id IN (");
        for (int i = 0; i < contactList.size(); i++)
        {
            if (i > 0)
            {
                where.append(",");
            }
            where.append(contactList.get(i));
        }
        where.append(")");
        ContentValues values = new ContentValues();
        
        values.put(People.CUSTOM_RINGTONE, ringtoneUri.toString());
        
        int nRet = 0;
        
        try
        {
            nRet = getContentResolver().update(Uri.parse("content://com.android.contacts/contacts"), values, where.toString(), null);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        
        if (nRet == 0)
        {
            mMessageHandler.sendEmptyMessage(SHOW_ERROR);
        }
        else
        {
            /* 20110120 modified by CY Tseng begin */
            mRingtoneUtil.setIsRingtoneFlag(ringtoneUri);
            /* 20110120 modified by CY Tseng end */
            
            mMessageHandler.sendEmptyMessage(SET_CONTACT_OK);
        }
        
        setActivityResult();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            Intent data = RingtoneSetAs.this.getIntent().putExtra("isRingtoneBack", mIsPlaying);
            RingtoneSetAs.this.setResult(RESULT_OK, data);
            RingtoneSetAs.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    public void onUnMount()
    {
        setActivityResult();
    }
    
    @Override
    public void onMount()
    {
        
    }
}
