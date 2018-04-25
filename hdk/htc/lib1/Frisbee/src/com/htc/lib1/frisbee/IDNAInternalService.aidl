package com.htc.lib1.frisbee;
import android.os.ParcelFileDescriptor;
import com.htc.lib1.frisbee.IProgressCallback;

interface IDNAInternalService {

    int performBackup(in ParcelFileDescriptor fd, in ParcelFileDescriptor newState);

    int performRestore(in ParcelFileDescriptor fd, int appVersion, in ParcelFileDescriptor newState);
    
    void setCallback(IProgressCallback callback);
    
    void onCancel();
    
    long getEstimateSize();
    
    boolean needExternalStorage();
}