package com.htc.lib2.opensense.social;

import android.os.Bundle;

/**
 * The response callback interface called when social network plugin operations are finished.
 * 
 * @hide
 */
oneway interface ISocialPluginResponse {

    /**
     * Called when the operation finished
     * 
     * @param value the operation result
     * @throws android.os.RemoteException if other remote exception occurred
     * 
     * @hide
     */
    void onResult(in Bundle value);

    /**
     * Called when the operation failed
     * 
     * @param errorCode the operation error code
     * @param errorMessage the operation error message
     * @throws android.os.RemoteException if other remote exception occurred
     * 
     * @hide
     */
    void onError(int errorCode, String errorMessage);
}
