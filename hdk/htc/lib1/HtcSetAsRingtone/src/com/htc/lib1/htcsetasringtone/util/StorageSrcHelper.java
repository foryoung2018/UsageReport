package com.htc.lib1.htcsetasringtone.util;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.htc.lib1.htcsetasringtone.R;
import com.htc.lib1.htcstoragehelper.HtcStorageHelper;
// TODO : no need to write to SD card. Please check this part of code still need or not.
//import com.htc.wrap.android.os.HtcWrapEnvironment;

public class StorageSrcHelper
{
    private static final String TAG = "RingtoneTrimmer/StorageSrcHelper";
    
    private String mRingtonesDirectory = null;
    private String mStorageDirectory = null;
    private String mStrRingtoneNumber;
    
	public static boolean isStorageMounted(Context context) {
		File dir = HtcStorageHelper.getPhoneStorageDir(context);
		if (null != dir) {
			String state = Environment.getStorageState(dir);
			if (state.equals(Environment.MEDIA_MOUNTED)) {
				return true;
			}
		}
		return false;
	}
    
    public String getUniqueFileName(String originalFilePath)
    {
        String result = null;
        if (null != originalFilePath)
        {
            File originalFile = new File(originalFilePath);
            if (originalFile.exists())
            {
                String fileName = originalFile.getName();
                int dotIndex = fileName.lastIndexOf(".");
                if (dotIndex != -1)
                {
                    String ext = fileName.substring(dotIndex).toLowerCase();
                    String originalName = fileName.substring(0, dotIndex);
                    int ringtoneNumber = -1;
                    File tempFile;
                    do
                    {
                        ++ringtoneNumber;
                        mStrRingtoneNumber = numToString(ringtoneNumber);
                        result = String.format("RT_%s_%s%s", originalName, mStrRingtoneNumber, ext);
                        tempFile = new File(mRingtonesDirectory + result);
                    } while (tempFile.exists());
                }
            }
        }
        return result;
    }
    
    public String getRingtoneNumber()
    {
        return mStrRingtoneNumber;
    }
    
    public String getRingtonesDirectory(Context context)
    {
        if (TextUtils.isEmpty(mRingtonesDirectory))
        {
            mRingtonesDirectory = getStorageDirectory(context) + "Ringtones/";
            File file = new File(mRingtonesDirectory);
            file.mkdir();
            
            Log.d(TAG, "getRingtonesDirectory() mRingtonesDirectory = " + mRingtonesDirectory);
        }
        return mRingtonesDirectory;
    }
    
    public boolean isPhoneStorageSupports(Context context)
    {
		File dir = HtcStorageHelper.getPhoneStorageDir(context);
		return (null != dir);
    }
    
    public String getStorageDirectory(Context context)
    {
		if (TextUtils.isEmpty(mStorageDirectory)) {
			File dir = HtcStorageHelper.getPhoneStorageDir(context);
			if (dir != null) {
				mStorageDirectory = dir.getPath();
			}

			Log.d(TAG, "getStorageDirectory() mStorageDirectory = "
					+ mStorageDirectory);

			if (!mStorageDirectory.endsWith("/")) {
				mStorageDirectory += "/";
			}

			Log.d(TAG, "getStorageDirectory() new mStorageDirectory = "
					+ mStorageDirectory);
		}

		return mStorageDirectory;
    }
    
    public boolean checkStorageCapacity(final Activity activity, String filePath)
    {
        boolean result = false;
        if (!isStorageMounted(activity))
        {
        	final boolean isPhoneStorageSupport = isPhoneStorageSupports(activity);
        	activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (!activity.isFinishing()) {
						Toast toast = Toast.makeText(activity, isPhoneStorageSupport ? R.string.toast_phonestorage_not_available : R.string.toast_sdcard_not_available, Toast.LENGTH_SHORT);
						toast.show();
					}
				}
        		
        	});
        }
        else
        {
            String storageDir = getStorageDirectory(activity);
            StatFs stat = new StatFs(storageDir);
            long size = (long)((float)stat.getAvailableBlocks() * (float)stat.getBlockSize());
            File file = new File(filePath);
            if (file.exists())
            {
                long fileSize = file.length();
                if (size < fileSize)
                {
                	final boolean isPhoneStorageSupport = isPhoneStorageSupports(activity);
                	activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (!activity.isFinishing()) {
								Toast toast = Toast.makeText(activity, isPhoneStorageSupport ? R.string.toast_phonestorage_is_low : R.string.toast_sdcard_is_low, Toast.LENGTH_SHORT);
								toast.show();
							}
						}
                		
                	});
                }
                else
                {
                    result = true;
                }
            }
        }
        return result;
    }
    
    private String numToString(int num)
    {
        String result = (num < 10) ? "0" + String.valueOf(num) : String.valueOf(num);
        return result;
    }
}
