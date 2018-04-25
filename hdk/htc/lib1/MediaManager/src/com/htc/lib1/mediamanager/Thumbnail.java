package com.htc.lib1.mediamanager;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Thumbnail object stores path, width, height, etc.
 */
public class Thumbnail implements Parcelable {
    private String mPath;
    private int mWidth;
    private int mHeight;
    
    /**
     * Constructor
     * @param thumbnail's path
     * @param thumbnail's width
     * @param thumbnail's height
     */
    public Thumbnail(final String path, int width, int height) {
    	mPath = path;
    	mWidth = width;
    	mHeight = height;
    }
    
    /**
	 * Returns the thumbnail's path
	 * @return The thumbnail's path
	 */
	public String getPath() {
		return mPath;
	}

	/**
	 * Returns the thumbnail's width
	 * @return The thumbnail's width
	 */
	public int getWidth() {
		return mWidth;
	}

	/**
	 * Returns the thumbnail's height
	 * @return The thumbnail's height
	 */
	public int getHeight() {
		return mHeight;
	}
    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPath);
        dest.writeInt(mWidth);
        dest.writeInt(mHeight);
    }

    private Thumbnail(Parcel in) {
        mPath = in.readString();
        mWidth = in.readInt();
        mHeight = in.readInt();
    }

    public static final Parcelable.Creator<Thumbnail> CREATOR
            = new Parcelable.Creator<Thumbnail>() {
        public Thumbnail createFromParcel(Parcel in) {
            return new Thumbnail(in);
        }

        public Thumbnail[] newArray(int size) {
            return new Thumbnail[size];
        }
    };
}
