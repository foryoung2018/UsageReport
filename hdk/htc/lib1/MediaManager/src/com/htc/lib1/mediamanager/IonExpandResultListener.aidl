package com.htc.lib1.mediamanager;
import com.htc.lib1.mediamanager.MediaObject;
import com.htc.lib1.mediamanager.Collection;

interface IonExpandResultListener
{
    void onExpandResult(int nTaskId, in List<MediaObject> mediaObjectList, int mode, in Bundle extra);
    
    void onCollectionUpdated(int nTaskid, in Collection c, in Bundle extra);
        
    void onExpandStatusChanged(int nTaskId, int state, in Bundle extra);
}