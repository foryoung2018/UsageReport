package com.htc.lib1.mediamanager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import com.htc.lib1.mediamanager.utils.GeoInfoHelper;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 *  MediaObject information set
 */
public class MediaObject extends CoverImage
{
    static final String TAG = "MediaObject";
    /**
     *  @hide  
     */
    protected int mServiceType = 0;
    
    /**
     *  @hide  
     */
    protected String mThumbnailPath = null; 
    
    /**
     *  @hide  
     */
    protected int mWidth = 0;
    
    /**
     *  @hide  
     */
    protected int mHeight = 0;
    
    /**
     *  @hide  
     */
    protected float mLatitude = GeoInfoHelper.INVALID_LATLNG;
    
    /**
     *  @hide  
     */
    protected float mLongitude = GeoInfoHelper.INVALID_LATLNG;
    
    /**
     *  @hide  
     */
    protected String mTitle;
    
    /**
     *  @hide  
     */
    protected String mDisplayName;
    
    /**
     *  @hide  
     */
    protected long mDuration = -1;
    
    /**
     *  @hide  
     */
    protected String mDocId;
    
    /**
     *  @hide  
     */
    protected ArrayList<Thumbnail> mThumbnailList = new ArrayList<Thumbnail>();

    /**
     *  @hide
     */
    protected boolean mIsDateFromLocalTime = false;
    
    /**
     *  @hide
     */
    protected long mDBDate_taken = 0;
    
    /**
     *  @hide
     */
    protected long mDBDate_modify = 0;
    
    /**
     *  @hide
     */
    protected long mDBDate_added = 0;
    
    /**
     *  @hide
     */
    protected long mDBdate_user = 0;
    /**
     *  @hide
     */
    protected Bundle mExtra = null; // Preserve for extend

    /**
     *  @hide
     */
    protected float mShoeboxRank = 0f;

    /**
     * Constructor
     */
    public MediaObject(
            long id, 
            int favorite, 
            int htcType, 
            int mediaType, 
            int degreeRotated, 
            long dateModified, 
            long fileSize, 
            String dataPath, 
            String mimeType,
            int serviceType,
            String thumbnailPath,
            int width,
            int height,
            float latitude,
            float longitude,
            String title,
            String displayName,
            long duration,
            String docId,
            long dbDateTaken,
            long dbDateModify,
            long dbDateAdded,
            long dbDateUser)
    {
        super(id, favorite, htcType, mediaType, degreeRotated, dateModified, fileSize, dataPath, mimeType);
        
        mServiceType = serviceType;
        mThumbnailPath = thumbnailPath;
        mWidth = width;
        mHeight = height;
        mLatitude = latitude;
        mLongitude = longitude;
        mTitle = title;
        mDisplayName = displayName;
        mDuration = duration;
        mDocId = docId;
        mDBDate_taken = dbDateTaken;
        mDBDate_modify = dbDateModify;
        mDBDate_added = dbDateAdded;
        mDBdate_user = dbDateUser;
    }

    public MediaObject(MediaObject baseImage)
    {
        super(baseImage.mId, 
                baseImage.mFavorite, 
                baseImage.mHtcType,
                baseImage.mMediaType, 
                baseImage.mDegreeRotated,
                baseImage.mDateModified, baseImage.mFileSize,
                baseImage.mDataPath, baseImage.mMimeType);
        
        mServiceType = baseImage.mServiceType;
        mThumbnailPath = baseImage.mThumbnailPath;
        mWidth = baseImage.mWidth;
        mHeight = baseImage.mHeight;
        mLatitude = baseImage.mLatitude;
        mLongitude = baseImage.mLongitude;
        mTitle = baseImage.mTitle;
        mDisplayName = baseImage.mDisplayName;
        mDuration = baseImage.mDuration;
        mDocId = baseImage.mDocId;
        mThumbnailList= (ArrayList<Thumbnail>) baseImage.mThumbnailList.clone();
        mIsDateFromLocalTime = baseImage.mIsDateFromLocalTime;
        mDBDate_taken = baseImage.mDBDate_taken;
        mDBDate_modify = baseImage.mDBDate_modify;
        mDBDate_added = baseImage.mDBDate_added;
        mDBdate_user = baseImage.mDBdate_user;
        mShoeboxRank = baseImage.mShoeboxRank;
    }
    
    
    /**
     *  Get the thumbnail
     *  @return the thumbnail http link
     */
    public String getThumbnailPath()
    {
        return mThumbnailPath;
    }
    
    /**
     *  Retrieve the service type of this media object
     *  @return service type
     */
    public int getServiceType()
    {
        return mServiceType;
    }
    
    /**
     *  Set content width
     */
    public void setWidth(int width)
    {
        mWidth = width;
    }
    
    /**
     *  Set content height
     */
    public void setHeight(int height)
    {
        mHeight = height;
    }
    
    /**
     *  Get content width
     */
    public int getWidth()
    {
        return mWidth;
    }
    
    /**
     *  Get content height
     */
    public int getHeight()
    {
        return mHeight;
    }
    
    /**
     *  Get content's longitude
     *  @return  value of longitude
     */
    public float getLongitude()
    {
        return mLongitude;
    }
    
    /**
     *  Get content's latitude
     *  @return  value of latitude
     */
    public float getLatitude()
    {
        return mLatitude;
    }
    
    /**
     *  Get content's title
     *  @return  string of title
     */
    public String getTitle()
    {
        return mTitle;
    }
    
    /**
     *  Get content's display name
     *  @return  string of display name
     */
    public String getDisplayName()
    {
        return mDisplayName;
    }
    
    /**
     *  Get content's duration
     *  @return  The duration of the content, if no duration is available, -1 is returned.
     */
    public long getDuraction()
    {
        return mDuration;
    }
    
    /**
     *  Get Document's Id
     *  @return  String of the document's Id
     */
    public String getDocId()
    {
        return mDocId;
    }
    
    /**
     *  Get Thumbnail list
     *  @return  The thumbnail object list for this MediaObject.
     */
    public List<Thumbnail> getThumbnailList() 
    {
    	return mThumbnailList;
    }
    
    public void addThumbnailItems(List<Thumbnail> thumbanilList) 
    {
    	mThumbnailList.addAll(thumbanilList);
    }
    
    /**
     * If the date of the cloud content is got from image's EXIF, time zone will be UTC
     * By default, use default time zone
     * @return Calendar for display
     */
    public Calendar getDisplayCalendar()
    {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTimeInMillis(mDateModified);
        /*
         * we will count offset while reading mediaObject from database
         * so do not need to convert to UTC time.
         */
//
//    	if (mDBdate_user > 0) 
//    	{
//    		calendar.setTimeInMillis(mDateModified);
//    	} 
//    	else 
//    	{
//    		calendar.setTimeInMillis(mDateModified);
//        	if (mIsDateFromLocalTime) 
//        	{
//        		/* By default, we should use default zone to present
//        		But if the mDateModified is saved as local time(EXIF time), use UTC time to display*/
//        		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
//        	}
//    	}
		return calendar;
    }
    
    /**
     *  Get date taken
     * @return date_taken value.
     */
    public long getDBDateTaken()
    {
        return mDBDate_taken;
    }
    
    /**
     *  Get date modify
     * @return date_modify value.
     */
    public long getDBDateModify()
    {
        return mDBDate_modify;
    }
    
    /**
     *  Get date added
     * @return date_added value.
     */
    public long getDBDateAdded()
    {
        return mDBDate_added;
    }
    
    /**
     *  Get date user
     * @return date_user value.
     */
    public long getDBDateUser()
    {
        return mDBdate_user;
    }

    /**
     *  @hide
     */    public void setShoeboxRank(float f)
    {
        mShoeboxRank = f;
    }

     /**
      *  @hide
      */
    public float getShoeboxRank()
    {
        return mShoeboxRank;
    }
    
    /**
     *  @hide
     */
    public static final Parcelable.Creator<MediaObject> CREATOR= new Parcelable.Creator<MediaObject>()
    {
        public MediaObject createFromParcel(Parcel in)
        {
            return new MediaObject(in);
        }

        public MediaObject[] newArray(int size)
        {
            return new MediaObject[size];
        }
    };

    protected MediaObject(Parcel in)
    {
        super(in);
        try
        {
            mServiceType = in.readInt();
            mThumbnailPath = in.readString();
            mWidth = in.readInt();
            mHeight = in.readInt();
            mLatitude = in.readFloat();
            mLongitude = in.readFloat();
            mTitle = in.readString();
            mDisplayName = in.readString();
            mDuration = in.readLong();
            mDocId = in.readString();
            mDBDate_taken = in.readLong();
            mDBDate_modify = in.readLong();
            mDBDate_added = in.readLong();
            mDBdate_user = in.readLong();
            in.readTypedList(mThumbnailList, Thumbnail.CREATOR);
            mIsDateFromLocalTime = in.readByte() != 0;
            mExtra = in.readBundle();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *  @hide
     */
    @Override
    public void writeToParcel(Parcel out, int flags)
    {
        try
        {
            super.writeToParcel(out, flags);
            
            out.writeInt(mServiceType);
            out.writeString(mThumbnailPath);
            out.writeInt(mWidth);
            out.writeInt(mHeight);
            out.writeFloat(mLatitude);
            out.writeFloat(mLongitude);
            out.writeString(mTitle);
            out.writeString(mDisplayName);
            out.writeLong(mDuration);
            out.writeString(mDocId);
            out.writeLong(mDBDate_taken);
            out.writeLong(mDBDate_modify);
            out.writeLong(mDBDate_added);
            out.writeLong(mDBdate_user);
            out.writeTypedList(mThumbnailList);
            out.writeByte((byte) (mIsDateFromLocalTime ? 1 : 0));
            if (null == mExtra)
            {
                mExtra = new Bundle();
            }
            if (null != mExtra)
            {
                // Preserve for extend
            }
            out.writeBundle(mExtra); // Notice: Bundle always should be last one of Parcel !!!!!!!
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Set extra information for the MediaObject.
     */
    public void setBundleExtra(Bundle extra)
    {
        mExtra = extra;
    }

    /**
     * Get extra information for the MediaObject.
     */
    public Bundle getBundleExtra()
    {
        return mExtra;
    }
    
    public static Comparator<MediaObject> COMPARATOR_BY_TIME_ASC = new Comparator<MediaObject>()
    {
        @Override
        public int compare(MediaObject lObj, MediaObject rObj)
        {
            long lt = lObj.getDateTime();
            long rt = rObj.getDateTime();
            if (lt == rt)
            {
                String ls = (lObj.getServiceType() == MediaManager.SERVICE_TYPE_LOCAL) ? lObj.getDataPath() : lObj.getDocId();
                String rs = (rObj.getServiceType() == MediaManager.SERVICE_TYPE_LOCAL) ? rObj.getDataPath() : rObj.getDocId();
                return ls.compareToIgnoreCase(rs);
            }
            else
            {
                if (lt > rt)
                    return 1;
                else
                    return -1;
            }
        }
    };
    public static Comparator<MediaObject> COMPARATOR_BY_TIME_DESC = new Comparator<MediaObject>()
    {
        @Override
        public int compare(MediaObject lObj, MediaObject rObj)
        {
            long lt = lObj.getDateTime();
            long rt = rObj.getDateTime();
            if (lt == rt)
            {
                String ls = (lObj.getServiceType() == MediaManager.SERVICE_TYPE_LOCAL) ? lObj.getDataPath() : lObj.getDocId();
                String rs = (rObj.getServiceType() == MediaManager.SERVICE_TYPE_LOCAL) ? rObj.getDataPath() : rObj.getDocId();
                return (-1) * ls.compareToIgnoreCase(rs);
            }
            else
            {
                if (lt < rt)
                    return 1;
                else
                    return -1;
            }
        }
    };
}
