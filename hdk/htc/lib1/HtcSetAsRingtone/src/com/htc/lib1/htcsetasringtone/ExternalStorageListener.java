package com.htc.lib1.htcsetasringtone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

public class ExternalStorageListener
{
    private OnMountListener mOnMountListener;
    private BroadcastReceiver mReceiver;
    
    public ExternalStorageListener(OnMountListener listener)
    {
        mOnMountListener = listener;
    }
    
    public void registerReceiver(Context context)
    {
        if (mReceiver == null)
        {
            mReceiver = new BroadcastReceiver()
            {
                @Override
                public void onReceive(Context context, Intent intent)
                {
                    String action = intent.getAction();
                    
                    if (TextUtils.equals(Intent.ACTION_MEDIA_UNMOUNTED, action) || TextUtils.equals(Intent.ACTION_MEDIA_EJECT, action))
                    {
                        mOnMountListener.onUnMount();
                    }
                    else if (TextUtils.equals(Intent.ACTION_MEDIA_MOUNTED, action))
                    {
                        mOnMountListener.onMount();
                    }
                }
            };
            
            IntentFilter iFilter = new IntentFilter();
            iFilter.addAction(Intent.ACTION_MEDIA_EJECT);
            iFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
            iFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
            iFilter.addDataScheme("file");
            context.registerReceiver(mReceiver, iFilter);
        }
    }
    
    public void unregisterReceiver(Context context)
    {
        if (mReceiver != null)
        {
            context.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }
    
}

interface OnMountListener
{
    
    public void onMount();
    
    public void onUnMount();
}