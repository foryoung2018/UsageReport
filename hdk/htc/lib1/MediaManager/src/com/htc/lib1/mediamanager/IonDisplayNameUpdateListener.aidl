package com.htc.lib1.mediamanager;
import com.htc.lib1.mediamanager.CollectionName;
import android.os.Bundle;

interface IonDisplayNameUpdateListener
{
    void onDisplayNameUpdated(int nTaskId, int level, in List<CollectionName> al, in Bundle extra);
    void onDisplayNameRetrieverStateChange(int nTaskId, int state);
}
