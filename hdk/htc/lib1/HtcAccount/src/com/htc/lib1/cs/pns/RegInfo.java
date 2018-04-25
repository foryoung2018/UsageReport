
package com.htc.lib1.cs.pns;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class RegInfo implements Parcelable {
    private static final int REG_ID_SENTITIZE_LENGTH = 8;
    public final String regId;
    public final String pushProvider;

    public static final Creator<RegInfo> CREATOR = new Creator<RegInfo>() {
        public RegInfo createFromParcel(Parcel source) {
            return null;
        }

        public RegInfo[] newArray(int size) {
            return null;
        }
    };

    /**
     * Construct an instance.
     * 
     * @param regId PNS registration id.
     * @param pushProvider Underlying push provider in use.
     */
    public RegInfo(String regId, String pushProvider) {
        if (TextUtils.isEmpty(regId))
            throw new IllegalArgumentException("'regId' is null or empty.");
        if (TextUtils.isEmpty(pushProvider))
            throw new IllegalArgumentException("'pushProvider' is null or empty.");

        this.regId = regId;
        this.pushProvider = pushProvider;
    }

    /**
     * Construct an instance from {@link Parcel}.
     * 
     * @param in {@link Parcel} generated from
     *            {@link #writeToParcel(Parcel, int)}.
     */
    public RegInfo(Parcel in) {
        if (in == null)
            throw new IllegalArgumentException("'in' is null.");

        this.regId = in.readString();
        this.pushProvider = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(regId);
        dest.writeString(pushProvider);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof RegInfo))
            return false;
        final RegInfo other = (RegInfo) o;
        return regId.equals(other.regId) && pushProvider.equals(other.pushProvider);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + regId.hashCode();
        result = 31 * result + pushProvider.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "RegInfo {regId=" + santitizeRegId(regId) + ", pushProvider=" + pushProvider + "}";
    }

    /**
     * Keep only the first {@value #REG_ID_SENTITIZE_LENGTH} characters of
     * {@code regId}.
     */
    private String santitizeRegId(String regId) {
        if (!TextUtils.isEmpty(regId) && regId.length() > REG_ID_SENTITIZE_LENGTH) {
            return regId.substring(0, REG_ID_SENTITIZE_LENGTH)
                    + regId.substring(REG_ID_SENTITIZE_LENGTH).replaceAll(".", "*");
        }

        return regId;
    }
}
