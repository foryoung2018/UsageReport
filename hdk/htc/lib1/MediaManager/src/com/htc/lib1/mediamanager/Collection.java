package com.htc.lib1.mediamanager;

import java.util.ArrayList;
import java.util.Comparator;

import com.htc.lib1.mediamanager.utils.GeoInfoHelper;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Collection information set
 */
public class Collection implements Parcelable, Comparable<Collection>
{
    private static final int INVALID_SOURCE_TYPE = -1;
    
    //Sense65 new append >>>
    private static final String TAG = "Collection";
    private static final String KEY_INT_ONLINE_IMAGE_COUNT = "key_int_online_image_count";
    private static final String KEY_INT_ONLINE_VIDEO_COUNT = "key_int_online_video_count";
    private static final String KEY_INT_TAGGEDTYPE = "key_int_taggedtype";
    private static final String KEY_PARCELABLE_COVER_MEDIA = "key_parcelable_cover_media";
    private static final String KEY_PARCELABLEARRAY_COVER_MEDIA_LIST = "key_parcelablearray_cover_media_list";
    private static final String KEY_PARCELABLEARRAY_SUB_COLLECTION_LIST = "KEY_PARCELABLEARRAY_SUB_COLLECTION_LIST";

    //Sense65 new append <<<

    private float mVersion;
    protected int mSourceType = INVALID_SOURCE_TYPE;
    protected String mLocaleString = null;
    protected boolean mVisibility = true;
    protected String mCollectionType = null;
    protected String mId = null;
    protected String mName = null;
    protected String mDateName = null;
    protected long mTime = 0;
    protected float mLongitude = GeoInfoHelper.INVALID_LATLNG;
    protected float mLatitude = GeoInfoHelper.INVALID_LATLNG;
    protected int mImageCount = 0;
    protected int mVideoCount = 0;
    protected int mDrmImageCount = 0;
    protected int mDrmVideoCount = 0;
    protected int mMediaType = MediaManager.MEDIA_TYPE_ALL_MEDIA;
    protected int mContainsMediaType = 0;
    protected int mLevel = MediaManager.LEVEL_DEFAULT;
    protected CoverImage mCoverImage = null;
    protected Bundle mExtra = null;
    protected ArrayList<CoverImage> mCoverList = null; // will always be null
    
    //Sense65 new append >>>
    protected int mOnlineImageCount = 0;
    protected int mOnlineVideoCount = 0;
    protected MediaObject mCoverMedia = null;
    protected ArrayList<MediaObject> mCoverMediaList = null;
    protected ArrayList<Collection> mSubCollectionList = null;
    protected int mTaggedType = 0;
    //Sense65 new append <<<
    
    /**
     * The version code of Collection class
     */
    public static float getClassVersion()
    {
        return 3.0f;
    }
    
    /**
     * The version code of Collection instance
     */
    public float getVersion()
    {
        return mVersion;
    }
    
    /**
     * Implementation for Comparator interface
     */
    public static Comparator<Collection> COMPARATOR_BY_TIME_ASC = new Comparator<Collection>()
    {
        @Override
        public int compare(Collection lhs, Collection rhs)
        {
            long lt = lhs.getTime();
            long rt = rhs.getTime();
            return (lt == rt) ? 0 : ((lt > rt)? 1 : -1);
        }
    };
    /**
     * Implementation for Comparator interface
     */
    public static Comparator<Collection> COMPARATOR_BY_TIME_DESC = new Comparator<Collection>()
    {
        @Override
        public int compare(Collection lhs, Collection rhs)
        {
            long lt = lhs.getTime();
            long rt = rhs.getTime();
            return (lt == rt) ? 0 : ((lt < rt)? 1 : -1);
        }
    };
    
    /**
     * Key to retrieve the image URI for database query.
     */
    public static final String KEY_IMAGE_URI = "key_image_uri";
    /**
     * Key to retrieve the image WHERE for database query.
     */
    public static final String KEY_IMAGE_WHERE = "key_image_where";
    /**
     * Key to retrieve the image filter algorithm for database query
     */
    public static final String KEY_IMAGE_ARGS = "key_image_args";
    /**
     * Key to retrieve the video URI for database query.
     */
    public static final String KEY_VIDEO_URI = "key_video_uri";
    /**
     * Key to retrieve the video WHERE for database query.
     */
    public static final String KEY_VIDEO_WHERE = "key_video_where";
    /**
     * Key to retrieve the video filter algorithm for database query
     */
    public static final String KEY_VIDEO_ARGS = "key_video_args";
    /**
     * Key to retrieve the files URI for database query.
     */
    public static final String KEY_FILES_URI = "key_files_uri";
    /**
     * Key to retrieve the files WHERE for database query.
     */
    public static final String KEY_FILES_WHERE = "key_files_where";
    /**
     * Key to retrieve the files filter algorithm for database query
     */
    public static final String KEY_FILES_ARGS = "key_files_args";
    /**
     * Key for the bundle returned by MediaManager.exportCollectionToBundle
     */
    public static final String BKEY_COLLECTION_SOURCE_TYPE = "bkey_collection_source_type";
    /**
     * Key for the bundle returned by MediaManager.exportCollectionToBundle
     */
    public static final String BKEY_COLLECTION_TYPE = "bkey_collection_type";
    /**
     * Key for the bundle returned by MediaManager.exportCollectionToBundle
     */
    public static final String BKEY_COLLECTION_ID = "bkey_collection_id";
    /**
     * Key for the bundle returned by MediaManager.exportCollectionToBundle
     */
    public static final String BKEY_COLLECTION_NAME = "bkey_collection_name";
    /**
     * Key for the bundle returned by MediaManager.exportCollectionToBundle
     */
    public static final String BKEY_COLLECTION_DATE_NAME = "bkey_collection_date_name";
    /**
     * Key for the bundle returned by MediaManager.exportCollectionToBundle
     */
    public static final String BKEY_COLLECTION_NAME_LOCALE = "bkey_collection_name_locale";
    /**
     * Key for the bundle returned by MediaManager.exportCollectionToBundle
     */
    public static final String BKEY_COLLECTION_TIME = "bkey_collection_time";
    /**
     * Key for the bundle returned by MediaManager.exportCollectionToBundle
     */
    public static final String BKEY_COLLECTION_LATITUDE = "bkey_collection_latitude";
    /**
     * Key for the bundle returned by MediaManager.exportCollectionToBundle
     */
    public static final String BKEY_COLLECTION_LONGITUDE = "bkey_collection_longitude";
    /**
     * Key for the bundle returned by MediaManager.exportCollectionToBundle
     */
    public static final String BKEY_COLLECTION_LEVEL = "bkey_collection_level";
    /**
     * Key for the bundle returned by MediaManager.exportCollectionToBundle
     */
    public static final String BKEY_COLLECTION_TOTAL_COUNT = "bkey_collection_total_count";
    /**
     * Key for the bundle returned by MediaManager.exportCollectionToBundle
     */
    public static final String BKEY_COLLECTION_SUPPORTED_MEDIA_TYPE = "bkey_collection_supported_media_type";
    /**
     * Key for the bundle returned by MediaManager.exportCollectionToBundle
     */
    public static final String BKEY_COLLECTION_COVER_BUNDLE = "bkye_collection_cover_bundle";
    /**
     * Key for the bundle returned by MediaManager.exportCollectionToBundle
     */
    public static final String BKEY_COLLECTION_WHERE_PARAMS = "bkey_collection_where_params";
    /**
     * Key for the bundle returned by MediaManager.exportCollectionToBundle
     */
    public static final String BKEY_COVER_DATA_PATH = "bkey_cover_data_path";
    /**
     * Key for the bundle returned by MediaManager.exportCollectionToBundle
     */
    public static final String BKEY_COVER_DATE_TIME = "bkye_cover_date_time";
    /**
     * Key for the bundle returned by MediaManager.exportCollectionToBundle
     */
    public static final String BKEY_COVER_DEGREE_ROTATE = "bkey_cover_degree_rotate";
    /**
     * Key for the bundle returned by MediaManager.exportCollectionToBundle
     */
    public static final String BKEY_COVER_FAVORITE = "bkey_cover_favorite";
    /**
     * Key for the bundle returned by MediaManager.exportCollectionToBundle
     */
    public static final String BKEY_COVER_SIZE = "bkye_cover_size";
    /**
     * Key for the bundle returned by MediaManager.exportCollectionToBundle
     */
    public static final String BKEY_COVER_HTC_TYPE = "bkey_cover_htc_type";
    /**
     * Key for the bundle returned by MediaManager.exportCollectionToBundle
     */
    public static final String BKEY_COVER_MEDIA_ID = "bkey_cover_media_id";
    /**
     * Key for the bundle returned by MediaManager.exportCollectionToBundle
     */
    public static final String BKEY_COVER_MEDIA_TYPE = "bkey_cover_media_type";
    /**
     * Key for the bundle returned by MediaManager.exportCollectionToBundle
     */
    public static final String BKEY_COVER_MIME_TYPE = "bkey_cover_mime_type";
    
    /**
     * Constructor
     */
    public Collection(String collectionType, String id, String name)
    {
        mVersion = Collection.getClassVersion();
        mCollectionType = collectionType;
        mId = id;
        mName = name;
    }
    
    /**
     * Constructor
     */
    public Collection(Collection c)
    {
        clone(c);
    }

    /**
     * Duplicate a collection from another collection
     */
    public void clone(Collection collection)
    {
        mSourceType = collection.mSourceType;
        mLocaleString = collection.mLocaleString;
        mVisibility = collection.mVisibility;
        mCollectionType = collection.mCollectionType;
        mId = collection.mId;
        mName = collection.mName; 
        mDateName = collection.mDateName;
        mTime = collection.mTime;
        mLongitude = collection.mLongitude;
        mLatitude = collection.mLatitude;
        mImageCount = collection.mImageCount;
        mVideoCount = collection.mVideoCount;
        mDrmImageCount = collection.mDrmImageCount;
        mDrmVideoCount = collection.mDrmVideoCount;
        mMediaType = collection.getMediaType(); 
        mContainsMediaType = collection.getContainsMediaType();
        mLevel = collection.mLevel;
        if (collection.mCoverImage != null)
            mCoverImage = new CoverImage(collection.mCoverImage);
        if (collection.mExtra != null)
            mExtra = new Bundle(collection.mExtra);
        if (collection.mCoverList != null)
        {
            mCoverList = new ArrayList<CoverImage>();
            for (int i = 0; i<collection.mCoverList.size(); i++)
                mCoverList.add(collection.mCoverList.get(i));
        }
        
        //Sense65 new append >>>
        mOnlineImageCount = collection.mOnlineImageCount;
        mOnlineVideoCount = collection.mOnlineVideoCount;
        
        if (collection.mCoverMedia != null)
            mCoverMedia = new MediaObject(collection.mCoverMedia);
        
        if (collection.mCoverMediaList != null)
        {
            mCoverMediaList = new ArrayList<MediaObject>();
            
            for (int i = 0; i < collection.mCoverMediaList.size(); i++)
            {
                mCoverMediaList.add(collection.mCoverMediaList.get(i));
            }
        }
        
        if (collection.mSubCollectionList != null)
        {
            mSubCollectionList = new ArrayList<Collection>();
            
            for (int i = 0; i < collection.mSubCollectionList.size(); i++)
            {
                mSubCollectionList.add(collection.mSubCollectionList.get(i));
            }
        }
        mTaggedType = collection.mTaggedType;
        //Sense65 new append <<<
    }

    /**
     * Get the type of the collection
     */
    public String getCollectionType()
    {
        return mCollectionType;
    }

    /**
     * Set collection type
     */
    public void setCollectionType(String type)
    {
        mCollectionType = type;
    }
    
    /**
     * Get collection ID
     */
    public String getId()
    {
        return mId;
    }

    /**
     * Set collection ID
     */
    public void setId(String id)
    {
        mId = id;
    }

    /**
     * Get the collection display name.
     */
    public String getName()
    {
        return mName;
    }

    /**
     * Set the collection display name.
     */
    public void setName(String name)
    {
        mName = name;
    }
    
    /**
     * Get the represent time of collection.
     */
    public long getTime()
    {
        return mTime;
    }

    /**
     * Set the represent time of collection.
     */
    public void setTime(long time)
    {
        mTime = time;
    }

    /**
     * Get the represent longitude of collection, in degrees.
     */
    public float getLongitude()
    {
        return mLongitude;
    }

    /**
     * Set the represent longitude of collection, in degrees.
     */
    public void setLongitude(float longitude)
    {
        mLongitude = longitude;
    }

    /**
     * Get the represent latitude of collection, in degrees.
     */
    public float getLatitude()
    {
        return mLatitude;
    }

    /**
     * Set the represent latitude of collection, in degrees.
     */
    public void setLatitude(float latitude)
    {
        mLatitude = latitude;
    }

    /**
     * Get the image count of collection.
     */
    public int getImageCount()
    {
        return mImageCount;
    }

    /**
     * Set the image count of collection.
     */
    public void setImageCount(int imageCount)
    {
        mImageCount = imageCount;
    }

    /**
     * Get the video count of collection.
     */
    public int getVideoCount()
    {
        return mVideoCount;
    }

    /**
     * Set the video count of collection.
     */
    public void setVideoCount(int videoCount)
    {
        mVideoCount = videoCount;
    }

    /**
     * Get the DRM image count of collection.
     */
    public int getDrmImageCount()
    {
        return mDrmImageCount;
    }

    /**
     * Set the DRM image count of collection.
     */
    public void setDrmImageCount(int count)
    {
        mDrmImageCount = count;
    }

    /**
     * Get the DRM video count of collection.
     */
    public int getDrmVideoCount()
    {
        return mDrmVideoCount;
    }

    /**
     * Set the DRM video count of collection.
     */
    public void setDrmVideoCount(int count)
    {
        mDrmVideoCount = count;
    }

    /**
     * Get the DRM image count of collection.
     */
    public int getOnlineImageCount()
    {
        return mOnlineImageCount;
    }

    /**
     * Set the DRM image count of collection.
     */
    public void setOnlineImageCount(int count)
    {
        mOnlineImageCount = count;
    }

    /**
     * Get the DRM video count of collection.
     */
    public int getOnlineVideoCount()
    {
        return mOnlineVideoCount;
    }

    /**
     * Set the DRM video count of collection.
     */
    public void setOnlineVideoCount(int count)
    {
        mOnlineVideoCount = count;
    }
    
    /**
     * Get the represent level of the collection
     */
    public int getLevel()
    {
    	return mLevel;
    }
    
    /**
     * Set the represent level of the collection
     */
    public void setLevel(int level)
    {
        mLevel = level;
    }
    
    /**
     * Get the files total count of collection.
     */
    public int getTotalCount()
    {
        return mImageCount + mVideoCount + mDrmImageCount + mDrmVideoCount + mOnlineImageCount + mOnlineVideoCount;
    }

    /**
     * Get the MediaType of Collection.
     */
    public int getMediaType()
    {
        return mMediaType;
    }

    /**
     * Set the MediaType of Collection.
     */
    public void setMediaType(int mediaType)
    {
        mMediaType = mediaType;
    }
    
    /**
     * Get the collection contains mediaType
     */
    public int getContainsMediaType()
    {
        return mContainsMediaType;
    }

    /**
     * Set the collection contains mediaType
     */
    public void setContainsMediaType(int type)
    {
        mContainsMediaType = type;
    }

    /**
     * Set the visibility of the collection
     */
    public void setVisibility(boolean visibility)
    {
        mVisibility = visibility;
    }

    /**
     * Check the Collection is hiding or not.
     */
    public boolean isHiding()
    {
        return !mVisibility;
    }

    /**
     * Set the Collection display name locale information.
     */
    public void setNameLocale(String nameLocale)
    {
        mLocaleString = nameLocale;
    }

    /**
     * Get the Collection display name locale information.
     */
    public String getNameLocale()
    {
        return mLocaleString;
    }

    /**
     * Set the Collection source type.
     */
    public void setSourceType(int sourceType)
    {
        mSourceType = sourceType;
    }

    /**
     * Get the Collection source type.
     */
    public int getSourceType()
    {
        return mSourceType;
    }

    /**
     * Get the unique key which was composed by latitude and longitude.
     */
    public long getLocationKey()
    {
        return GeoInfoHelper.genLocationKey(mLatitude, mLongitude);
    }

    /**
     * Get the Cover image information of Collection.
     */
    public CoverImage getCover()
    {
        return mCoverImage;
    }
    
    /**
     * Set the Cover image information of Collection.
     */
    public void setCover(CoverImage cover)
    {
        mCoverImage = cover;
    }

    /**
     * Set CoverImage information of Collection.
     */
    public void setCover(Bundle bundleCover)
    {
        if (bundleCover != null)
        {
            long id = bundleCover.getLong(MediaManagerStore.MediaManagerColumns._ID, 0);
            int favorite = bundleCover.getInt(MediaManagerStore.MediaManagerColumns.FAVORITE, 0);
            int htcType = bundleCover.getInt(MediaManagerStore.MediaManagerColumns.HTC_TYPE, 0);
            int mediaType = bundleCover.getInt(MediaManagerStore.MediaManagerColumns.MEDIA_TYPE, 0);
            int degreeRotated = bundleCover.getInt(MediaManagerStore.MediaManagerColumns.ORIENTATION, 0);
            long dateModified = bundleCover.getLong(MediaManagerStore.MediaManagerColumns.DATE_MODIFIED, 0);
            long fileSize = bundleCover.getLong(MediaManagerStore.MediaManagerColumns.SIZE, 0);
            String dataPath = bundleCover.getString(MediaManagerStore.MediaManagerColumns.DATA, null); 
            String mimeType = bundleCover.getString(MediaManagerStore.MediaManagerColumns.MIME_TYPE, null); 
            mCoverImage = new CoverImage(id, favorite, htcType, mediaType, degreeRotated, dateModified, fileSize, dataPath, mimeType);
        }
    }

    /**
     * Implementation of Parcelable
     */
    public static final Parcelable.Creator<Collection> CREATOR = new Parcelable.Creator<Collection>()
    {
        public Collection createFromParcel(Parcel in)
        {
            return new Collection(in);
        }

        public Collection[] newArray(int size)
        {
            return new Collection[size];
        }
    };

    private Collection(Parcel in)
    {
        readFromParcel(in);
    }

    protected void readFromParcel(Parcel in)
    {
        try
        {
            mVersion = in.readFloat();
            Log.d(TAG, "[readFromParcel] pass in version = " + mVersion + " current version = " +  getClassVersion());
            
            mSourceType = in.readInt();
            mLocaleString = in.readString();
            mVisibility = in.readInt() == 1 ? true : false;
            mCollectionType = in.readString();
            mId = in.readString(); 
            mName = in.readString();
            mDateName = in.readString();
            mTime = in.readLong();
            mLongitude = in.readFloat();
            mLatitude = in.readFloat();
            mImageCount = in.readInt();
            mVideoCount = in.readInt();
            mDrmImageCount = in.readInt();
            mDrmVideoCount = in.readInt();
            mMediaType = in.readInt();
            mContainsMediaType = in.readInt();
            mLevel = in.readInt();
            
            mCoverImage = in.readParcelable(CoverImage.class.getClassLoader());
            Parcelable[] temp = in.readParcelableArray(CoverImage.class.getClassLoader());
            if (temp != null && temp.length > 0)
            {
                mCoverList = new ArrayList<CoverImage>();
                for (int i=0; i<temp.length; i++)
                {
                    if (temp[i] != null)
                        mCoverList.add((CoverImage)temp[i]);
                }
            }
            mExtra = in.readBundle();
            //Sense65 new append >>>
            if (null != mExtra)
            {
                mExtra.setClassLoader(MediaObject.class.getClassLoader());
                if (mExtra.containsKey(KEY_INT_ONLINE_IMAGE_COUNT))
                {
                    mOnlineImageCount = mExtra.getInt(KEY_INT_ONLINE_IMAGE_COUNT);
                }

                if (mExtra.containsKey(KEY_INT_ONLINE_VIDEO_COUNT))
                {
                    mOnlineVideoCount = mExtra.getInt(KEY_INT_ONLINE_VIDEO_COUNT);
                }

                if (mExtra.containsKey(KEY_PARCELABLE_COVER_MEDIA))
                {
                    mCoverMedia = (MediaObject)mExtra.getParcelable(KEY_PARCELABLE_COVER_MEDIA);
                }

                if (mExtra.containsKey(KEY_PARCELABLEARRAY_COVER_MEDIA_LIST))
                {
                    mCoverMediaList = mExtra.getParcelableArrayList(KEY_PARCELABLEARRAY_COVER_MEDIA_LIST);
                }
                
                
                mExtra.setClassLoader(Collection.class.getClassLoader());
                if (mExtra.containsKey(KEY_PARCELABLEARRAY_SUB_COLLECTION_LIST))
                {
                    mSubCollectionList = mExtra.getParcelableArrayList(KEY_PARCELABLEARRAY_SUB_COLLECTION_LIST);
                }
                
                if (mExtra.containsKey(KEY_INT_TAGGEDTYPE))
                {
                    mTaggedType = mExtra.getInt(KEY_INT_TAGGEDTYPE);
                }
            }
            // Sense65 new append <<<
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags)
    {
        out.writeFloat(mVersion);
        out.writeInt(mSourceType);
        out.writeString(mLocaleString);
        out.writeInt(mVisibility ? 1 : 0);
        out.writeString(mCollectionType);
        out.writeString(mId);
        out.writeString(mName);
        out.writeString(mDateName);
        out.writeLong(mTime);
        out.writeFloat(mLongitude);
        out.writeFloat(mLatitude);
        out.writeInt(mImageCount);
        out.writeInt(mVideoCount);
        out.writeInt(mDrmImageCount);
        out.writeInt(mDrmVideoCount);
        out.writeInt(mMediaType);
        out.writeInt(mContainsMediaType);
        out.writeInt(mLevel);
        out.writeParcelable(mCoverImage, flags);

        if (mCoverList != null && mCoverList.size() > 0)
        {
            CoverImage[] temp = new CoverImage[mCoverList.size()];
            for (int i=0; i<mCoverList.size(); i++)
                temp[i] = mCoverList.get(i);
            out.writeParcelableArray(temp, flags);
        }
        else
        {
            out.writeParcelableArray(null, flags);
        }
        
        //Sense65 new append >>>
        if (null == mExtra)
        {
            mExtra = new Bundle();
        }
        
        if (null != mExtra)
        {
            mExtra.putInt(KEY_INT_ONLINE_IMAGE_COUNT, mOnlineImageCount);
            mExtra.putInt(KEY_INT_ONLINE_VIDEO_COUNT, mOnlineVideoCount);
            if (null != mCoverMedia)
            {
                mExtra.putParcelable(
                        KEY_PARCELABLE_COVER_MEDIA, 
                        mCoverMedia);
            }
            if ((null != mCoverMediaList) && (mCoverMediaList.size() > 0))
            {
                mExtra.putParcelableArrayList(KEY_PARCELABLEARRAY_COVER_MEDIA_LIST, mCoverMediaList);
            }
            
            if ((null != mSubCollectionList) && (mSubCollectionList.size() > 0))
            {
              mExtra.putParcelableArrayList(KEY_PARCELABLEARRAY_SUB_COLLECTION_LIST, mSubCollectionList);
            }
            mExtra.putInt(KEY_INT_TAGGEDTYPE, mTaggedType);
        }
        //Sense65 new append <<<
        
        out.writeBundle(mExtra); // Notice: Bundle always should be last one of Parcel !!!!!!!
    }

    /**
     *  Compares Collection objects based on time or name.
     *  @param collection The Collection to compare to this one.
     *  @return 0 if the same; less than 0 if this Collection sorts ahead of <var>collection</var>;
     *  greater than 0 if this Collection sorts after <var>collection</var>.
     */
    @Override
    public int compareTo(Collection collection)
    {
        if (collection != null)
        {
            long mt = getTime();
            long ct = collection.getTime();
            return (mt == ct) ? 0 : ((mt < ct)? 1 : -1);
        }
        return 0;
    }

    /**
     * Set the represent date of collection.
     */
    public void setDateName(String dateName)
    {
        mDateName = dateName;
    }
    
    /**
     * Get the represent date of collection.
     */
    public String getDateName()
    {
        return mDateName;
    }
    
    /**
     * Get cover list of the collection.
     */
    public ArrayList<CoverImage> getCoverList(Bundle extra)
    {
        if (mCoverMediaList != null)
        {
            ArrayList<CoverImage> cList = new ArrayList<CoverImage>();
            for (CoverImage ci : mCoverMediaList)
            {
                if (ci != null)
                    cList.add(ci);
            }
            if (cList.size() > 0)
                return cList;
        }
        return null;
    }

    /**
     * Set cover list of the collection.
     */
    public void setCoverList(ArrayList<CoverImage> coverlist)
    {
        // mCoverList should always be null.
//        mCoverList = coverlist;
    }
    
    /**
     * Set extra information for the collection.
     */
    public void setBundleExtra(Bundle extra)
    {
        mExtra = extra;
    }

    /**
     * Get extra information for the collection.
     */
    public Bundle getBundleExtra()
    {
        return mExtra;
    }
    
    /**
     * Get MediaObject cover of the collection.
     */
    public MediaObject getCoverMedia()
    {
        return mCoverMedia;
    }
    
    /**
     * Assign MediaObject to collection. (Sense65)
     */
    public void setCoverMedia(MediaObject mediaobject)
    {
        mCoverMedia = mediaobject;
    }
    
    /**
     * Get MediaObject cover list of the collection.
     */
    public ArrayList<MediaObject> getCoverMediaList(Bundle extra)
    {
        return mCoverMediaList;
    }
    
    /**
     * Assign MediaObject arraylist to collection. (Sense65)
     */
    public void setCoverMediaList(ArrayList<MediaObject> mediaobjectlist)
    {
        mCoverMediaList = mediaobjectlist;
    }
    
    public ArrayList<Collection> getSubCollectionList()
    {
        return mSubCollectionList;
    }
    
    public void addCount(int mediaType, int serviceType)
    {
        switch (mediaType)
        {
        case MediaManager.MEDIA_TYPE_REGULAR_IMAGES:
            mImageCount++;
            break;
        case MediaManager.MEDIA_TYPE_REGULAR_VIDEOS:
            mVideoCount++;
            break;
        case MediaManager.MEDIA_TYPE_DRM_IMAGES:
            mDrmImageCount++;
            break;
        case MediaManager.MEDIA_TYPE_DRM_VIDEOS:
            mDrmVideoCount++;
            break;
        case MediaManager.MEDIA_TYPE_CLOUD_IMAGES:
            mOnlineImageCount++;
            break;
        case MediaManager.MEDIA_TYPE_CLOUD_VIDEOS:
            mOnlineVideoCount++;
            break;
        }
    }

    /**
     * Get the TaggedType of Collection.
     */
    public int getTaggedType()
    {
        return mTaggedType;
    }
    
    /**
     * Set the TaggedType of Collection.
     */
    public void setTaggedType(int nTaggedType)
    {
        mTaggedType = nTaggedType;
    }
}
