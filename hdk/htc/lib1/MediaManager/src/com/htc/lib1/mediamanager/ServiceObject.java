package com.htc.lib1.mediamanager;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class ServiceObject implements Parcelable
{

    public ServiceObject(Parcel in)
    {
        readFromParcel(in);
    }

    private void readFromParcel(Parcel in)
    {
        try
        {
            mState = in.readInt();
        	mServiceType = in.readInt();
        	mIcon = in.readParcelable(Bitmap.class.getClassLoader());
        	mLabel = in.readString();
        	mAccountName = in.readString();
        	mIsFiltered = in.readInt();
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
    public void writeToParcel(Parcel out, int flags)
    {
    	try
        {
    		out.writeInt(mState);
    		out.writeInt(mServiceType);
    		out.writeParcelable(mIcon, flags);
    		out.writeString(mLabel);
    		out.writeString(mAccountName);
    		out.writeInt(mIsFiltered);
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
    
    public static final Parcelable.Creator<ServiceObject> CREATOR = new Parcelable.Creator<ServiceObject>()
    {
        public ServiceObject createFromParcel(Parcel in)
        {
            return new ServiceObject(in);
        }

        public ServiceObject[] newArray(int size)
        {
            return new ServiceObject[size];
        }
    };
    
    public static final int STATE_NO_AUTH = 0;
    public static final int STATE_AUTHED = 1;
    public static final int STATE_LINKED = 2;
    public static final int STATE_UNLINKED = 3;
    public static final int STATE_LOGOUT = 4;

    private int mState;
    private int mServiceType;
    private Bitmap mIcon;
    private String mLabel;
    private String mAccountName;
    private int mIsFiltered;
    private Bundle mExtra = null; // Preserve for extend
    
    public ServiceObject(int state, int serviceType, Bitmap icon, String label, String accountName, int isFiltered)
    {
    	mState = state;
    	mServiceType = serviceType;
    	mIcon = icon;
    	mLabel = label;
    	mAccountName = accountName;
    	mIsFiltered = isFiltered;
    }
    
    public ServiceObject(int state, int serviceType, Bitmap icon, String label, String accountName, int isFiltered, Bundle extra)
    {
        mState = state;
        mServiceType = serviceType;
        mIcon = icon;
        mLabel = label;
        mAccountName = accountName;
        mIsFiltered = isFiltered;
        mExtra = extra;
    }
    // Add more state if you needed.
    /**
     * Return service current state. It could be STATE_HAS_TOKEN, STATE_NO_TOKEN,
     * ...etc.
     */
    public int getState()
    {
        return mState;
    }

    /**
     * Return the serviceType of this object. It could be
     * SERVICE_TYPE_ONLINE_DROPBOX, SERVICE_TYPE_ONLINE_FACEBOOK ...etc
     */
    public int getType()
    {
        return mServiceType;
    }

    /**
     * Return the service icon.
     */
    public Bitmap getIcon()
    {
        return mIcon;
    }

    /**
     * Return the service name.
     */
    public String getName()
    {
        return mLabel;
    }

    /**
     * If the service's state is STATE_HAS_TOKEN, return it's account name.
     * Otherwise, return null
     */
    public String getAccountName()
    {
        return mAccountName;
    }

    /**
     * Return the service is filter or not.
     */
    public boolean isFiltered()
    {
        return (1 == mIsFiltered ? true : false);
    }
    
    /**
     * Set extra information for the ServiceObject.
     */
    public void setBundleExtra(Bundle extra)
    {
        mExtra = extra;
    }

    /**
     * Get extra information for the ServiceObject.
     */
    public Bundle getBundleExtra()
    {
        return mExtra;
    }
}
