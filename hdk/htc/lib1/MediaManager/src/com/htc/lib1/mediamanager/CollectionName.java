package com.htc.lib1.mediamanager;

import com.htc.lib1.mediamanager.utils.GeoInfoHelper;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The implementation of Parcelable for CollectionName. 
 */
public class CollectionName implements Parcelable
{
    String mCollectionId;
    String mCollectionType;
    String mName;
    String mLocale;
    float mLatitude = GeoInfoHelper.INVALID_LATLNG;
    float mLongitude = GeoInfoHelper.INVALID_LATLNG;
    String mDateName;
    /**
     * The implementation of Parcelable for CollectionName. 
     */
    public static final Parcelable.Creator<CollectionName> CREATOR = new Parcelable.Creator<CollectionName>()
    {
        public CollectionName createFromParcel(Parcel in)
        {
            return new CollectionName(in);
        }

        public CollectionName[] newArray(int size)
        {
            return new CollectionName[size];
        }
    };

    private CollectionName(Parcel in)
    {
        readFromParcel(in);
    }
    
    /*
     * Constructor.
     */
    public CollectionName(String id, String type , String name, String locale, float lat, float lng, String dateName)
    {
        mCollectionId = id;
        mCollectionType = type;
        mName = name;
        mLocale = locale;
        mLatitude = lat;
        mLongitude = lng;
        mDateName = dateName;
    }
    
    @Override
    public void writeToParcel(Parcel out, int flags)
    {
        out.writeString(mCollectionId);
        out.writeString(mCollectionType);
        out.writeString(mName);
        out.writeString(mLocale);
        out.writeFloat(mLatitude);
        out.writeFloat(mLongitude);
        out.writeString(mDateName);
    }
    
    @Override
    public int describeContents()
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    protected void readFromParcel(Parcel in)
    {
        mCollectionId = in.readString();
        mCollectionType = in.readString();
        mName = in.readString();
        mLocale = in.readString();
        mLatitude = in.readFloat();
        mLongitude = in.readFloat();
        mDateName = in.readString();
    }
    
    /*
     * Get the collcetion type.
     */
    public String getCollectionType()
    {
        return mCollectionType;
    }
    
    /**
     * Get the collection ID.
     */
    public String getCollectionID()
    {
        return mCollectionId;
    }

    /**
     * Get the display name of the collection.
     */
    public String getDisplayName()
    {
        return mName;
    }
    
    /**
     * Get the locale of the collection name.
     */
    public String getLocale()
    {
    	return mLocale;
    }
    
    /**
     * Get the represent latitude of the collection.
     */
    public float getLatitude()
    {
        return mLatitude;
    }
    
    /**
     * Get the represent longitude of the collection.
     */
    public float getLongitude()
    {
        return mLongitude;
    }
    
    public String getDateName()
    {
        return mDateName;
    }
};
