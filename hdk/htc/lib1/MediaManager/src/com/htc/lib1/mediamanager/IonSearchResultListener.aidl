package com.htc.lib1.mediamanager;
import com.htc.lib1.mediamanager.Collection;

interface IonSearchResultListener
{
    void onSearchResult(int nTaskId, in List<Collection> collectionList, int mode, in Bundle extra);
    void onSearchStatusChanged(int nTaskId, int state, in Bundle extra);
}