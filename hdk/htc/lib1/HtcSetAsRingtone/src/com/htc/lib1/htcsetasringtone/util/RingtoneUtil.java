package com.htc.lib1.htcsetasringtone.util;

import java.lang.reflect.Method;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import com.htc.lib1.htcsetasringtone.DualModeRingtoneHelper;

public class RingtoneUtil
{
    private static final String TAG = "RingtoneTrimmer/RingtoneUtil";
    
    private Context mContext;
    
    public RingtoneUtil(Context context)
    {
        mContext = context;
    }
    
    public boolean setIsRingtoneFlag(Uri ringtoneUri)
    {
        ContentValues contentValue = new ContentValues();
        contentValue.put(MediaStore.Audio.Media.IS_RINGTONE, "1");
        try
        {
            if (1 == mContext.getContentResolver().update(ringtoneUri, contentValue, null, null))
            {
                return true;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean setSystemDefaultRingtone(Uri ringtoneUri)
    {
        Log.d(TAG, "setSystemDefaultRingtone() ringtoneUri = " + ringtoneUri);
        if (null == ringtoneUri)
        {
            return false;
        }
        
        if (setIsRingtoneFlag(ringtoneUri))
        {
            return Settings.System.putString(mContext.getContentResolver(), Settings.System.RINGTONE, ringtoneUri.toString());
        }
        
        return false;
    }

//    public boolean setSystemDefaultRingtone(Uri ringtoneUri, int slotType)
//    {
//        Log.d(TAG, "setSystemDefaultRingtone() ringtoneUri = " + ringtoneUri + ", slotType = " + slotType);
//        if (null == ringtoneUri)
//        {
//            return false;
//        }
//        
//        if (setIsRingtoneFlag(ringtoneUri))
//        {
//            switch (slotType)
//            {
//            case Constants.SLOT_TYPE_CDMA:
//                HtcWrapRingtoneManager.setActualDefaultRingtoneUri(mContext, HtcWrapRingtoneManager.TYPE_RINGTONE, ringtoneUri, DualModeRingtoneHelper.getCdmaType());
//                break;
//            case Constants.SLOT_TYPE_GSM:
//                HtcWrapRingtoneManager.setActualDefaultRingtoneUri(mContext, HtcWrapRingtoneManager.TYPE_RINGTONE, ringtoneUri, Constants.MODE_GSM);
//                break;
//            case Constants.SLOT_TYPE_BOTH:
//                HtcWrapRingtoneManager.setActualDefaultRingtoneUri(mContext, HtcWrapRingtoneManager.TYPE_RINGTONE, ringtoneUri, Constants.MODE_GSM);
//                HtcWrapRingtoneManager.setActualDefaultRingtoneUri(mContext, HtcWrapRingtoneManager.TYPE_RINGTONE, ringtoneUri, DualModeRingtoneHelper.getCdmaType());
//                break;
//            }
//            return true;
//        }
//        return false;
//    }

    public boolean hasHtcSetActualDefaultRingtoneUriMethod()
    {
        final String CLASS_NAME = "android.media.RingtoneManager";
        final String METHOD = "setActualDefaultRingtoneUri";
        try
        {
            Class c = null;
            c = Class.forName(CLASS_NAME);

            Class[] mParam1 = {Context.class, Integer.TYPE, Uri.class, String.class};
            Method setRingtone = c.getMethod(METHOD, mParam1);
        }
        catch (ClassNotFoundException e)
        {
            Log.w(TAG, "hasHtcSetActualDefaultRingtoneUriMethod", e);
            return false;
        }
        catch (NoSuchMethodException e)
        {
            Log.w(TAG, "hasHtcSetActualDefaultRingtoneUriMethod", e);
            return false;
        }
        catch (Exception e)
        {
            Log.w(TAG, "hasHtcSetActualDefaultRingtoneUriMethod", e);
            return false;
        }
        return true;
     }

    public boolean setSystemDefaultRingtone(Uri ringtoneUri, int slotType)
    {
        Log.d(TAG, "setSystemDefaultRingtone() ringtoneUri = " + ringtoneUri + ", slotType = " + slotType);
        if (null == ringtoneUri)
        {
            return false;
        }

        final String CLASS_NAME = "android.media.RingtoneManager";
        final String METHOD = "setActualDefaultRingtoneUri";

        if (setIsRingtoneFlag(ringtoneUri))
        {
            try {
                Class c = null;
                c = Class.forName(CLASS_NAME);

                Class[] mParam1 = {Context.class, Integer.TYPE, Uri.class, String.class};
                Object[] mParamObjs1 = {mContext, 1, ringtoneUri, DualModeRingtoneHelper.getCdmaType(mContext)};
                Object[] mParamObjs2 = {mContext, 1, ringtoneUri, Constants.MODE_GSM};

                Method setName = null;
                switch (slotType)
                {
                case Constants.SLOT_TYPE_CDMA:
                    setName= c.getMethod(METHOD, mParam1);
                    setName.invoke(null, mParamObjs1);
                    break;
                case Constants.SLOT_TYPE_GSM:
                    setName = c.getMethod(METHOD, mParam1);
                    setName.invoke(null, mParamObjs2);
                    break;
                case Constants.SLOT_TYPE_BOTH:
                    setName = c.getMethod(METHOD, mParam1);
                    setName.invoke(null, mParamObjs1);
                    setName.invoke(null, mParamObjs2);
                }
            } catch (ClassNotFoundException e) {
                Log.w(TAG, "setSystemDefaultRingtone", e);
                return false;
            } catch (NoSuchMethodException e){
                Log.w(TAG, "setSystemDefaultRingtone", e);
                return false;
            } catch (Exception e) {
                Log.w(TAG, "setSystemDefaultRingtone", e);
                return false;
            }
            return true;
        }
        return false;
    }

    public Uri getUriFromPhysicalPath(String path)
    {
        Log.d(TAG, "getUriFromPhysicalPath() path = " + path);
        
        ContentResolver resolver = mContext.getContentResolver();
        
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[] {MediaStore.Audio.Media._ID};
        String selection = MediaStore.Audio.Media.DATA + "=? collate NOCASE";
        String selectionArgs[] = new String[] {path};
        Cursor cursor = null;
        
        try
        {
            cursor = resolver.query(uri, projection, selection, selectionArgs, null);
            
            if ((null != cursor) && (cursor.moveToFirst()))
            {
                Uri ret = ContentUris.withAppendedId(uri, cursor.getInt(0));
                Log.d(TAG, "getUriFromPhysicalPath() ret = " + ret);
                
                return ret;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (null != cursor)
            {
                cursor.close();
            }
        }
        return null;
    }

    public boolean isRingtonetrimmerExist() {
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setClassName("com.htc.ringtonetrimmer", "com.htc.ringtonetrimmer.RingtoneSetAs");
        return appExist(mContext, intent);
    }

    public boolean isContactPickerExist() {
        Intent intent = new Intent(Constants.INTENT_PICK_MULIT_CONTACT);
        intent.setDataAndType(Uri.EMPTY, "vnd.android.cursor.dir/contact");
        return appExist(mContext, intent);
    }

    public static boolean appExist(Context context, Intent intent) {
        List<ResolveInfo> apps = context.getPackageManager().queryIntentActivities(intent, 0);
        boolean appExist = (apps != null) && (apps.size() > 0);
        return appExist;
    }
}
