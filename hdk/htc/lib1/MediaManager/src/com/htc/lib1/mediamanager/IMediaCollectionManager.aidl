package com.htc.lib1.mediamanager;

import com.htc.lib1.mediamanager.Collection;
import com.htc.lib1.mediamanager.MediaObject;
import com.htc.lib1.mediamanager.ServiceObject;
import com.htc.lib1.mediamanager.CloudTagCollectionInfo;

import com.htc.lib1.mediamanager.IonDisplayNameUpdateListener;
import com.htc.lib1.mediamanager.IonGroupInfoChangeListener;
import com.htc.lib1.mediamanager.IonExpandResultListener;
import com.htc.lib1.mediamanager.IonSearchResultListener;
import com.htc.lib1.mediamanager.IonServiceStateListener;
import com.htc.lib1.mediamanager.IonGrantPermissionListener;
import com.htc.lib1.mediamanager.IonShouldShowRationaleListener;

import android.os.Bundle;

interface IMediaCollectionManager
{
    int startGrouping(String strUuid, int sourceType, int mediaType, int level, in Bundle extra);
    
    void stopGrouping(String strUuid);
    
    int getGroupingStatus(String strUuid);
    
    void setGroupInfoChangeListener(String strUuid, IonGroupInfoChangeListener l);
    
    void setDisplayNameUpdateListener(String strUuid, IonDisplayNameUpdateListener l);
    
    void clear(String strUuid);
    
    Collection createCollection(int sourceType, String collectionType, String name, int level, in Bundle extra);
    
    int deleteCollection(in Collection target, in long[] media, in Bundle extra);
    
    Collection getCollection(int sourceType, String collectionType, String id, in Bundle extra);
    
    int hideCollections(in List<Collection> collections, in Bundle extra);
    
    void renameCollection(in Collection target, String name, in Bundle extra);
    
    int showCollections(in List<Collection> collections, in Bundle extra);
    
    int addToCollection(in long[] media, in Collection target, in Bundle extra);
    
    int removeFromCollection(in long[] media, in Collection target, in Bundle extra);

    String[] queryCollectionID(int sourceType, int level, in long[] id, in Bundle extra);
    
    void enableCache(String strUuid, boolean isEnable);
    
    boolean isServiceAlive();
    
    boolean requestUpdateCollectionName(String strUuid, int sourceType, int level, in Bundle extras);
    
    Bundle exportCollectionToBundle(in Collection collection, int mediatype, in Bundle extras);
    
    Bundle getWhereParameters(in Collection c, int mediaType, in Bundle extra);
    
    String getAlbumHideSQLWhereString(in Bundle extra);
    
    //Sense65 new appended begin
    
    void setExpandResultListener(String strUuid, IonExpandResultListener l);
    
    void setSearchResultListener(String strUuid, IonSearchResultListener l);
    
    int addToCollection_Cloud(in String[] media, in Collection target, in Bundle extra);
    
    int removeFromCollection_Cloud(in String[] media, in Collection target, in Bundle extra);
    
    int expand(String strUuid, in Collection c, int mediaType, in Bundle extra);
    
    void cancelExpand(String strUuid, in Bundle extra);
    
    int search(String strUuid, String keyword, in Bundle extra);
    
    void cancelSearch(String strUuid, int taskId, in Bundle extra);
    
    boolean setHome(double latitude, double longitude);
    
    void resetHome();
    
    double getHomeLatitude();
    
    double getHomeLongitude();
    
    int getSupportedServiceTypes();
    
    List<ServiceObject> getServices(int serviceType, in Bundle extras);
    
    void setServiceFiltered(int beShownServices, int beHidedServices, in Bundle extras);
    
    void setServiceStateListener(String strUuid, IonServiceStateListener listener);
    
    boolean updateCollectionDateTime(in Collection collection, int year, int month, int day, inout Bundle extras);
    
    boolean updateMediaObjectDateTime(in MediaObject[] mediaObjects, int year, int month, int day, inout Bundle extras);
    
    String[] getVirtualAlbumSortList();
    
    List<ServiceObject> getDupServices(in MediaObject mediaObj, in Bundle extras);
    
    void connectPhotoPlatform(String strUuid);
    
    void disConnectPhotoPlatform(String strUuid);
    
    boolean isPPServiceConnected();
    
    List<CloudTagCollectionInfo> getMediaTags(in MediaObject mo, in Bundle extras);
    
    boolean updateTagByCollection(in Collection sourceCollection, String targetName, in Bundle extras);
    
    boolean updateTagByMediaObjects(in MediaObject[] mediaObjects, String sourceName, String targetName, in Bundle extras);
    
    List<MediaObject> getMediaObjectsByDocIds(in String[] docs);
    
    int genTaskId(in Bundle extras);
    
    MediaObject getMediaObjectById(long mediaId, in Bundle extras);

    void grantPermissions(in String[] permissions);

    void setGrantPermissionListener(String strUuid, IonGrantPermissionListener l);

    void shouldShowRequestPermissionRationale();

    void setShouldShowRationaleListener(String strUuid, IonShouldShowRationaleListener l);

    void grantPermissionsEx(in String[] permissions, in Bundle extras);
}
