package com.htc.lib1.upm.uploader;

import android.content.Context;

import com.htc.lib1.upm.HtcUPLocalStore;
import com.htc.lib1.upm.Common;
import com.htc.lib1.upm.Log;
import com.htc.lib1.upm.uploader.budget.BudgetManager;
import com.htc.xps.pomelo.log.HandsetLogPKT;
import com.squareup.wire.Wire;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

public final class ReportManager {
    
    private static final String TAG = "ReportUploader";
    private static final int MAX_RETRY = 2;
    private static ReportManager mInstance;
    private final Context mContext;
    private final BudgetManager mBudgetManager;
    private static HtcUPLocalStore sStore;
    private static CSUploader sCSUploader;
    private ReportManager(Context context) {
        mContext = context;
        mBudgetManager = new BudgetManager(mContext);
        sStore = HtcUPLocalStore.getInstance(mContext);
        sCSUploader = new CSUploader(mContext, mBudgetManager);
    }
    
    public static ReportManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ReportManager(context);
        }
        return mInstance;
    }
    
    public void onUpload() {
        Log.d(TAG, "Ready to upload...");
        HandsetLogCreator logCreator = new HandsetLogCreator(mContext);
        sStore.delivery(logCreator);
        if (logCreator.getDataCount() > 0) {
            HandsetLogPKT envelope = logCreator.toHandsetLog();
            Log.d(TAG, "HandsetLogPKT size: "+envelope.getSerializedSize());
            upload(envelope);
        } else {
        	logCreator = null;
            Log.d(TAG, "No data needs to be uploaded.");
        }
    }
    
    private void upload(HandsetLogPKT envelope) {
        if(UploadUtils.isNetworkAllowed(mContext)){
        	
            long length = envelope.getSerializedSize();
            
            if ( false == mBudgetManager.isAvailableByCurrentNetwork(1L, length) ) {
                Log.d(TAG, "upload() fail due to no budget for current network");
            } else {
                boolean succeedToUploadFirstLog = false;
                int retry = MAX_RETRY; //We don't support policy for stand alone uploader, so we hard code retry times.
                
                //We don't use wake lock here due to it needs to request app to grant specific permission.     
                for(int i=0; i<=retry; i++) {
                    if(!UploadUtils.isNetworkAllowed(mContext)){
                        if (Common._DEBUG) Log.d(TAG, "[upload] Stop upload to Pomelo server due to no proper network.");
                        continue;
                    }
                    Log.d(TAG,"Uploaded file size: "+length); //must showed it at begin,added by Ricky 2012.06.27
                    if (Common._DEBUG) Log.d(TAG, "[upload] run "+i);
                    if(sCSUploader.putReport(envelope)){
                        succeedToUploadFirstLog = true;
                        break;
                    }
                }
                          
                if(succeedToUploadFirstLog) {
                    resumeCSCachedReport();
                    return; // must return or the first log will be stored in cache file
                }
            }                        
        }
        
        storeCSReport(envelope, Common.SRT_UP_TAG);
    }
    
    private void storeCSReport(HandsetLogPKT envelope, String tag) {
        // TODO: deflate it with zip format
        LogCacheManager.getInstance().putFile(mContext, envelope.toByteArray(), tag);
    }
    
    private void resumeCSCachedReport() {
        // No need to check the network status again since it's checked outside??
        // To fulfill Policy changed event to upload, this is needed 
        if (!UploadUtils.isNetworkAllowed(mContext))
            return;
        
        EntryFile [] fileList = LogCacheManager.getInstance().getFiles(mContext);
        Log.d(TAG, "Start upload resuming queue files. file count: "+fileList.length);
        for(EntryFile file : fileList) {
            if (file != null) {
                
                Log.d(TAG,"resume file: "+file.getName());
                InputStream is;
                try {
                    is = file.getFileInputStreamEx(mContext);
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "Resume cached log failed.", e);
                    file.delete();
                    continue;
                } catch (IOException e) {
                	Log.e(TAG, "Resume cached log failed.", e);
                	file.delete();
                	continue;
                } catch (GeneralSecurityException e) {
                	Log.e(TAG, "Resume cached log failed.", e);
                	file.delete();
                	continue;
                }
                
                HandsetLogPKT envelope = null;
                if(is != null) {
                    try {
                        Wire wire = new Wire();
                        envelope = wire.parseFrom(is, HandsetLogPKT.class);
                        is.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        // TODO: Do we need to delete the file directly???
                        continue; // keep this file and just leave
                    } finally {
                        try{
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            continue;
                        }
                    }

                    if(envelope != null) {
                    	
                    	/*
                         * Check budget limitation before send
                         * 
                         * TODO: Use real size to calculate
                         */
                        long length = envelope.getSerializedSize();
                    	if ( false == mBudgetManager.isAvailableByCurrentNetwork(1L, length) ) {
                            Log.d(TAG, "resumeCSCachedReport() fail due to no budget for current network");
                            return;
                        }
                    	
                        if(sCSUploader.putReport(envelope)) {
                            file.delete();
                        }
                        else {
                            Log.d("break resuming queue files");
                            break; // [Power Consumption] if cache files contain too many files, it potentially take long time to upload with unstable network
                        }
                    }
                }
            }
        }
    }
}
