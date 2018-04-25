package com.htc.lib1.mediamanager;

import java.util.Comparator;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *  CoverImage information set
 */
public class CoverImage implements Parcelable
{          
    /**
     *  @hide
     */
    protected long mId = 0;
    /**
     *  @hide
     */
    protected int mFavorite = 0;
    /**
     *  @hide
     */
    protected int mHtcType = 0;
    /**
     *  @hide
     */
    protected int mMediaType = -1;
    /**
     *  @hide
     */
    protected int mDegreeRotated = 0;
    /**
     *  @hide
     */
    protected long mDateModified = 0;
    /**
     *  @hide
     */
    protected long mFileSize = 0;
    /**
     *  @hide
     */
    protected String mDataPath = null;
    /**
     *  @hide
     */
    protected String mMimeType = null;

    /**
     * Constructor
     */
    public CoverImage(long id, int favorite, int htcType, int mediaType, int degreeRotated, 
                        long dateModified, long fileSize, String dataPath, String mimeType)
    {
        update(id, favorite, htcType, mediaType, degreeRotated, 
                dateModified, fileSize, dataPath, mimeType);
    }

    public CoverImage(CoverImage baseImage)
    {
        update(baseImage.mId, baseImage.mFavorite, baseImage.mHtcType, baseImage.mMediaType, baseImage.mDegreeRotated, 
                baseImage.mDateModified, baseImage.mFileSize, baseImage.mDataPath, baseImage.mMimeType);
    }
    
    /**
     *  @hide
     */
    public void update(long id, int favorite, int htcType, int mediaType, int degreeRotated, 
                        long dateModified, long fileSize, String dataPath, String mimeType)
    {
        mId = id;
        mFavorite = favorite;
        mHtcType = htcType;
        mMediaType = mediaType;
        mDegreeRotated = degreeRotated;
        mDateModified = dateModified;
        mFileSize = fileSize;
        mDataPath = dataPath;
        mMimeType = mimeType;
    }

    /**
     *  Get the cover image media ID.
     *  @return the file _ID value in MediaStore.
     */
    public long getId()
    {
        return mId;
    }

    /**
     *  @hide
     */
    public void setFavorite(int favorite){
        mFavorite = favorite;
    }

    /**
     *  Get the cover image favorite value for ranking.
     *  @return the file favorite value in HtcWrapMediaStore.
     */
    public int getFavorite()
    {
        return mFavorite;
    }

    /**
     *  Get the cover image HtcType.
     *  @return the file HtcType value in HtcWrapMediaStore.
     */
    public int getHtcType()
    {
        return mHtcType;
    }

    /**
     *  Get the cover image MediaType.
     *  @return the MediaType:</br>
     *  {@link com.htc.mediacollection.MediaCollectionManager#MEDIA_TYPE_ALL_IMAGES}</br>
     *  {@link com.htc.mediacollection.MediaCollectionManager#MEDIA_TYPE_ALL_MEDIA}</br>
     *  {@link com.htc.mediacollection.MediaCollectionManager#MEDIA_TYPE_ALL_VIDEOS}</br>
     *  {@link com.htc.mediacollection.MediaCollectionManager#MEDIA_TYPE_DRM_IMAGES}</br>
     *  {@link com.htc.mediacollection.MediaCollectionManager#MEDIA_TYPE_DRM_MEDIA}</br>
     *  {@link com.htc.mediacollection.MediaCollectionManager#MEDIA_TYPE_DRM_VIDEOS}</br>
     *  {@link com.htc.mediacollection.MediaCollectionManager#MEDIA_TYPE_REGULAR_IMAGES}</br>
     *  {@link com.htc.mediacollection.MediaCollectionManager#MEDIA_TYPE_REGULAR_MEDIA}</br>
     *  {@link com.htc.mediacollection.MediaCollectionManager#MEDIA_TYPE_REGULAR_VIDEOS}</br>
     */
    public int getMediaType()
    {
        return mMediaType;
    }

    /**
     *  Get the cover image rotate angles, in degrees.
     *  @return the file rotate angles.
     */
    public int getDegreeRotated()
    {
        return mDegreeRotated;
    }

    /**
     *  Get the cover image last modified time, units are seconds since 1970.
     *  @return the file last modified time.
     */
    public long getDateTime()
    {
        return mDateModified;
    }

    /**
     *  Get the cover image file size.
     *  @return the file size.
     */
    public long getFileSize()
    {
        return mFileSize;
    }

    /**
     *  Get the cover image file path.
     *  @return the file path.
     */
    public String getDataPath()
    {
        return mDataPath;
    }

    /**
     *  Get the cover image file MIME type.
     *  @return the file MIME type.
     */
    public String getMimeType()
    {
        return mMimeType;
    }

    /**
     *  Hide Automatically by Justin
     *  @hide
     */
    public static final Parcelable.Creator<CoverImage> CREATOR= new Parcelable.Creator<CoverImage>()
    {
        public CoverImage createFromParcel(Parcel in)
        {
            return new CoverImage(in);
        }

        public CoverImage[] newArray(int size)
        {
            return new CoverImage[size];
        }
    };

    protected CoverImage(Parcel in)
    {
        readFromParcel(in);
    }

    private void readFromParcel(Parcel in)
    {
        try
        {
            mId = in.readLong();
            mFavorite = in.readInt();
            mHtcType = in.readInt();
            mMediaType = in.readInt();
            mDegreeRotated = in.readInt();
            mDateModified = in.readLong();
            mFileSize = in.readLong();
            mDataPath = in.readString();
            mMimeType = in.readString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *  Hide Automatically by Justin
     *  @hide
     */
    @Override
    public int describeContents()
    {
        return 0;
    }

    /**
     *  Hide Automatically by Justin
     *  @hide
     */
    @Override
    public void writeToParcel(Parcel out, int flags)
    {
        out.writeLong(mId);
        out.writeInt(mFavorite);
        out.writeInt(mHtcType);
        out.writeInt(mMediaType);
        out.writeInt(mDegreeRotated);
        out.writeLong(mDateModified);
        out.writeLong(mFileSize);
        out.writeString(mDataPath);
        out.writeString(mMimeType);
    }
    public static Comparator<CoverImage> COMPARATOR_BY_TIME_ASC = new Comparator<CoverImage>()
    {
        @Override
        public int compare(CoverImage lhs, CoverImage rhs)
        {
            long lt = lhs.mDateModified;
            long rt = rhs.mDateModified;
            if (lt == rt)
            {
                return lhs.mDataPath.compareToIgnoreCase(rhs.mDataPath);
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
    public static Comparator<CoverImage> COMPARATOR_BY_TIME_DESC = new Comparator<CoverImage>()
    {
        @Override
        public int compare(CoverImage lhs, CoverImage rhs)
        {
            long lt = lhs.mDateModified;
            long rt = rhs.mDateModified;
            if (lt == rt)
            {
                return (-1) * lhs.mDataPath.compareToIgnoreCase(rhs.mDataPath);
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