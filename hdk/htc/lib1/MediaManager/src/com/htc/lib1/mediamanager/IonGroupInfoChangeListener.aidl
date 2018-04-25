package com.htc.lib1.mediamanager;
import com.htc.lib1.mediamanager.Collection;
import android.os.Bundle;

interface IonGroupInfoChangeListener
{
    void onGroupUpdated(int nTaskId, in List<Collection> l, int mode, int level, in Bundle extra);
    void onGroupStatusChanged(int nTaskId, int state, int level, in Bundle extra);
}
