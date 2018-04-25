package com.htc.lib1.frisbee;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Service;
import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Binder;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;

import com.htc.lib1.frisbee.utils.DownloadHelper;

/**
 * HtcBackupAgent is a service that acts as a backup agent. Similar to the android.app.backup.BackupAgent 
 * in the Android data backup, it provides the center interface between an application to 
 * Frisbee. Application developer should implement this abstract class and declare this service 
 * in the manifest. Afterwards, Frisbee can recognize and support the target application. 
 * 
 * Note: 
 * 1. This Service must be 
 *      a) set as exported 
 *      b) declares permission: android.permission.BACKUP
 *      c) add action com.htc.dnatransfer.lib.backupservic in intent-filter 
 * 2. Use permission 
 *      a) com.htc.dnatransfer.permission.FEATURE_DOWNLOAD_FILE if it have to transfer large
 *         file among two devices. 
 *      b) com.htc.dnatransfer.permission.ACCESS_PROVIDER" if it have to access provider of Frisbee. 
 *    
 * See example below:
 * 
 * <uses-permission android:name="com.htc.dnatransfer.permission.FEATURE_DOWNLOAD_FILE" />
 * <uses-permission android:name="com.htc.dnatransfer.permission.ACCESS_PROVIDER" />
 * <application...>
 *       <service
 *           android:name=".MyHtcBackupAgent"
 *           android:exported="true"
 *           android:permission="android.permission.BACKUP" >
 *           <intent-filter>
 *               <action android:name="com.htc.dnatransfer.lib.backupservice" />
 *           </intent-filter>
 *       </service>
 * </application...>
 */
public abstract class HtcBackupAgent extends Service {

    private static final String TAG = HtcBackupAgent.class.getSimpleName();
    
    private static final int AGENT_OK = 0;
    private static final int AGENT_ERROR = 1;
    
    private IProgressCallback mCallback;
    private AtomicBoolean isCanceled = new AtomicBoolean(false);
    
    /**
     *  Return the Binder of this service.
     *  @param intent with optional arguments
     *  @return Binder of IDNAInternalService.Stub
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    
    private void checkPermission() {
        final int permission = checkCallingOrSelfPermission("android.permission.BACKUP");
        final boolean allowedByPermission = (permission == PackageManager.PERMISSION_GRANTED);
        if ( !allowedByPermission ) {
            throw new SecurityException("No Permission");
        }
    }
    
    private void init() {
        checkPermission();
        Binder.clearCallingIdentity();
    }

    private IDNAInternalService.Stub mBinder = new IDNAInternalService.Stub() {
        
        @Override
        public int performRestore(ParcelFileDescriptor fd, int appVersion, ParcelFileDescriptor newState)
                throws RemoteException {
            init();
            
            isCanceled.set(false);
            BackupDataInput input;
            try{
                Constructor constructor = BackupDataInput.class.getConstructor(FileDescriptor.class);
                input = (BackupDataInput) constructor.newInstance(fd.getFileDescriptor());
            } catch(Exception e){
                Log.e(TAG, e.getMessage(),e);
                return AGENT_ERROR;
            }
            
            try {
                HtcBackupAgent.this.onRestore(input, appVersion, newState);
            } catch(Exception e) {
                if ( Constants.DEBUG ) {
                    Log.e(TAG, e.getMessage(), e);
                }
                return AGENT_ERROR;
            }
            
            return AGENT_OK;
        }
        
        @Override
        public int performBackup(ParcelFileDescriptor fd, ParcelFileDescriptor newState) throws RemoteException {
            init();
            
            isCanceled.set(false);
            BackupDataOutput output;
            try{
                Constructor constructor = BackupDataOutput.class.getConstructor(FileDescriptor.class);
                output = (BackupDataOutput) constructor.newInstance(fd.getFileDescriptor());
            } catch(Exception e){
                Log.e(TAG, e.getMessage(),e);
                return AGENT_ERROR;
            }
            
            try {
                HtcBackupAgent.this.onBackup(null, output, newState);
            } catch(Exception e) {
                if ( Constants.DEBUG ) {
                    Log.e(TAG, e.getMessage(), e);
                }
                return AGENT_ERROR;
            }
            return AGENT_OK;
        }

        @Override
        public void setCallback(IProgressCallback callback)
                throws RemoteException {
            init();
            mCallback = callback;
        }

        @Override
        public void onCancel() throws RemoteException {
            init();
            HtcBackupAgent.this.onCancel();
        }

        @Override
        public long getEstimateSize() throws RemoteException {
            init();
            return HtcBackupAgent.this.getEstimateSize();
        }

        @Override
        public boolean needExternalStorage() throws RemoteException {
            init();
            return HtcBackupAgent.this.needExternalStorage();
        }
    };
    
    private void onCancel() {
        isCanceled.set(true);
    }
    
    /**
     *  Return true if current task is canceled.
     *  @return true if current task is canceled, and false on the contrary.
     */
    protected boolean isCanceled() {
        return isCanceled.get();
    }
    
    /**
     *  Frisbee will call getEstimateSize when user wants to transfer this application.
     *  Developer should override this method and return the correct size of the data
     *  that will going to transfer.
     *  @return the estimated size of data that will be generated by onBackup module in byte.
     */
    protected long getEstimateSize() {
        return 0L;
    }
    /**
     * Just a wrapper
     * @param pathList
     */
    protected void setRemoteFileList(ArrayList<String> pathList){
        setAccessList(pathList);
    }
    /**
     * Large files can be transferred directly rather than putting them into BackupData.
     * In the onBackup, put the path of those large files into the pathList, and use 
     * openRemoteFile in the onRestore to download files from backup side. Note, 
     * only file in the accessList can be download from backup side. 
     * @param pathList: the list of full file paths that will be transfered to remote side.
     */
    protected void setAccessList(ArrayList<String> pathList) {
        setAccessList(pathList, false);
    }
    /**
     * Just a wrapper
     * @param pathList
     * @param deleteAfterRestore
     */
    protected void setRemoteFileList(ArrayList<String> pathList, boolean deleteAfterRestore){
        setAccessList(pathList, deleteAfterRestore);
    }
    /**
     * Large files can be transferred directly rather than putting them into BackupData.
     * In the onBackup, put the path of those large files into the pathList, and use 
     * openRemoteFile in the onRestore to download files from backup side. Note, 
     * only file in the accessList can be download from backup side.
     * @param pathList: the list of full file paths that will be transfered to remote side.
     * @param deleteAfterRestore: true if the files in the path list have to be 
     * deleted after the transferring is finished.
     */
    protected void setAccessList(ArrayList<String> pathList, boolean deleteAfterRestore) {
        if ( pathList != null ) {
            ContentValues[] cvs = new ContentValues[pathList.size()];
            int index = 0;
            for(String path : pathList) {
                ContentValues cv = new ContentValues();
                String withscheme;
                if ( path.startsWith(ContentResolver.SCHEME_CONTENT) ||
                        path.startsWith(ContentResolver.SCHEME_FILE) ) {
                    withscheme = path;
                } else {
                    withscheme = ContentResolver.SCHEME_FILE + "://" + path;
                }
                cv.put(Constants.KEY_PATH, withscheme);
                cv.put(Constants.KEY_DELETE, deleteAfterRestore);
                cvs[index++] = cv;
            }
            getContentResolver().bulkInsert(Constants.DOWNLOAD_URI, cvs);
        }
    }
    
    /**
     * Tell Frisbee if backup agent will use external storage in the onBackup.
     * The default is false. Application developer should override this methods and return 
     * the correct value if it uses the external storage in the onBackup.
     * @return return true if the backup agent will use external storage in the onBackup.
     * Return false on the contrary.
     * 
     */
    protected boolean needExternalStorage() {
        return false;
    }
    
     /**
     * Open the stream of the remote file for download.
     * @param remotePath: the path of remote file to download, make sure if it is 
     * in the accessList. Please refer to setAccessList.
     * @return InputStream of the remote file.
     * @throws FileNotFoundException: throw if the given remotePath is illegal.
     */
    protected InputStream openRemoteFile(String remotePath) throws FileNotFoundException {
        return openRemoteFile(remotePath, false);
    }

    /**
    * Open the stream of the remote file for download.
    * @param remotePath: the path of remote file to download, make sure if it is 
    * in the accessList. Please refer to setAccessList.
    *        isSecure: encrypt the data by AES/CBC if isSecure is true
    * @return InputStream of the remote file.
    * @throws FileNotFoundException: throw if the given remotePath is illegal.
    */
   protected InputStream openRemoteFile(String remotePath, boolean isSecure) throws FileNotFoundException {
       return DownloadHelper.openFile(this, remotePath, isSecure);
   }
    
    /**
     * In this method, you read your application data from the device and pass the data you want to back up 
     * to the Frisbee.
     * @param oldState: this parameter is not used, and will be null.
     * @param data: A structured wrapper around an open, read/write file descriptor pointing
     * to the backup data destination. Typically the application will use backup helper classes to write to this file.
     * @param newState: An open, read/write ParcelFileDescriptor pointing to an empty file. The state file
     * is not used in Frisbee.
     */
    public abstract void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data,
            ParcelFileDescriptor newState);
    
    /**
     * The Frisbee calls this method during a restore operation. 
     * When this method is called, the backup data is delivered as second parameter,
     * which you then restore to the device.
     * @param data: A structured wrapper around an open, read-only file descriptor pointing to a full snapshot 
     * of the application's data. The application should consume every entity represented in this data stream.
     * @param appVersionCode: The value of the android:versionCode manifest attribute, from the application 
     * that backed up this particular data set. This makes it possible for an application's agent to distinguish 
     * among any possible older data versions when asked to perform the restore operation.
     * @param newState: An open, read/write ParcelFileDescriptor pointing to an empty file. The state file
     * is not used in Frisbee.
     */
    public abstract void onRestore(BackupDataInput data, int appVersionCode,
            ParcelFileDescriptor newState);
    
    /**
     * updateProgress will notify Frisbee to update the UI. Call this method
     * if the progress can be calculated by using processed items, 
     * and percentage is hard to calculated among items. 
     * Application developer should call this method once the progress have to be updated.
     * @param index: the number of processed file in a backup/restore task.
     * @param total: the number of total file that have to be processed.
     */
    protected void updateProgress(int index, int total) {
        try {
            if ( mCallback != null ) {
                mCallback.updateProgress(index, total);
            }
        } catch (RemoteException e) {
            if ( Constants.DEBUG ) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }
    
    /**
     * updateProgress will notify Frisbee to update the UI. Call this method
     * if the progress can be calculated by using processed items and percentage. 
     * Application developer should call this method once the progress have to be updated.
     * @param index: the number of processed file in a backup/restore task.
     * @param total: the number of total file that have to be processed.
     * @param percentage: the percentage of current progress.
     */
    protected void updateProgress(int index, int total, int percentage) {
        try {
            if ( mCallback != null ) {
                mCallback.updateFileProgress(index, total, percentage);
            }
        } catch (RemoteException e) {
            if ( Constants.DEBUG ) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }
    
    /**
     * updateProgress will notify Frisbee to update the UI. Call this method
     * if the progress can be calculated by using transferred file size.
     * Application developer should call this method once the progress have to be updated. 
     * @param transfer: the number of file size had been transferred.
     * @param total: the number of total file size that have to be processed.
     */
    protected void updateProgressBySize(long transfer, long total) {
        try {
            int percentage = (int)(transfer * 100 / total);
            if ( mCallback != null ) {
                mCallback.updateFileProgressBySize(transfer, total, percentage);
            }
        } catch (RemoteException e) {
            if ( Constants.DEBUG ) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }
    
    private BackupAgent getBackupAgent() throws NameNotFoundException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        ApplicationInfo info = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        Class<BackupAgent> clsAgent = (Class<BackupAgent>)Class.forName(info.backupAgentName);
        return clsAgent.newInstance();
    }
}
