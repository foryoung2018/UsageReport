package com.htc.lib1.mediamanager;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

public class CloudTagCollectionInfo implements Parcelable
{
    protected String mTagName = null;
    protected int mTagType = 0;
    protected Bundle mExtra = null; // Preserve for extend

    /**
     * Constructor
     */
    public CloudTagCollectionInfo(String tagName, int tagType)
    {
        mTagName = tagName;
        mTagType = tagType;
    }
    
    /**
     * Constructor with Bundle
     */
    public CloudTagCollectionInfo(String tagName, int tagType, Bundle extra)
    {
        mTagName = tagName;
        mTagType = tagType;
        mExtra = extra;
    }

    /**
     * To get CloudTag encode ID
     */
    public String getBelongCollectionID()
    {
        return (mTagName != null) ? Base64.encodeToString(mTagName.getBytes(), Base64.DEFAULT) : null;
    }

    /**
     * Implementation of Parcelable
     */
    public static final Parcelable.Creator<CloudTagCollectionInfo> CREATOR = new Parcelable.Creator<CloudTagCollectionInfo>()
    {
        public CloudTagCollectionInfo createFromParcel(Parcel in)
        {
            return new CloudTagCollectionInfo(in);
        }

        public CloudTagCollectionInfo[] newArray(int size)
        {
            return new CloudTagCollectionInfo[size];
        }
    };

    private CloudTagCollectionInfo(Parcel in)
    {
        readFromParcel(in);
    }

    protected void readFromParcel(Parcel in)
    {
        try
        {
            mTagName = in.readString();
            mTagType = in.readInt();
            mExtra = in.readBundle();
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
    public void writeToParcel(Parcel out, int flag)
    {
        out.writeString(mTagName);
        out.writeInt(mTagType);

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
    
    /**
     * Set extra information for the CloudTagCollectionInfo.
     */
    public void setBundleExtra(Bundle extra)
    {
        mExtra = extra;
    }

    /**
     * Get extra information from CloudTagCollectionInfo.
     */
    public Bundle getBundleExtra()
    {
        return mExtra;
    }
    
    /**
     * Get CloudTag name from CloudTagCollectionInfo.
     */
    public String getCloudTagName()
    {
        return mTagName;
    }
    
    /**
     * Get CloudTag type from CloudTagCollectionInfo.
     */
    public int getCloudTagType()
    {
        return mTagType;
    }
}
