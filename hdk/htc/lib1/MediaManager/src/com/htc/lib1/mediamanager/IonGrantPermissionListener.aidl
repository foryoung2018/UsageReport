// IonGrantPermissionListerner.aidl
package com.htc.lib1.mediamanager;

// Declare any non-default types here with import statements

interface IonGrantPermissionListener {
    void onGrantPermissionResult(in String[] permission, in int[] grantResults);
}
