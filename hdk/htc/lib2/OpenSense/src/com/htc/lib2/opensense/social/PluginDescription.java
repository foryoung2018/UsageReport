package com.htc.lib2.opensense.social;

import android.accounts.AuthenticatorDescription;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @hide
 */
public class PluginDescription implements Parcelable {

    private final String type;
    private final String packageName;
    private final AuthenticatorDescription authDescription;

    /**
     * Gets the account type
     * 
     * @return the account type
     * 
     * @hide
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the package name
     * 
     * @return the package name
     * 
     * @hide
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Gets a authenticator description
     * 
     * @return a authenticator description
     * 
     * @hide
     */
    public AuthenticatorDescription getAuthDescription() {
        return authDescription;
    }

    /**
     * Creates a PluginDescription
     * 
     * @param type the given account type
     * @param packageName the given package name
     * 
     * @hide
     */
    public PluginDescription(String type, String packageName) {
        this(type, packageName, null);
    }

    /**
     * Creates a PluginDescription
     * 
     * @param type the given account type
     * @param packageName the given package name
     * @param authDescription the given authenticator description
     * 
     * @hide
     */
    public PluginDescription(String type, String packageName, AuthenticatorDescription authDescription) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
        if (packageName == null) {
            throw new IllegalArgumentException("packageName cannot be null");
        }
        this.type = type;
        this.packageName = packageName;
        this.authDescription = authDescription;
    }

    /**
     * @hide
     */
    public static PluginDescription newKey(String type) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
        return new PluginDescription(type);
    }

    private PluginDescription(String type) {
        this.type = type;
        this.packageName = null;
        this.authDescription = null;
    }

    private PluginDescription(Parcel source) {
        this.type = source.readString();
        this.packageName = source.readString();
        this.authDescription = source
                .readParcelable(AuthenticatorDescription.class.getClassLoader());
    }

    /**
     * Please refer to {@link Parcelable#writeToParcel(Parcel, int)}
     * 
     * @hide
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(packageName);
        dest.writeParcelable(authDescription, flags);
    }

    /**
     * Please refer to {@link Parcelable#describeContents()}
     * 
     * @hide
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Please refer to {@link Object#hashCode()}
     * 
     * @hide
     */
    @Override
    public int hashCode() {
        return type.hashCode();
    }

    /**
     * Please refer to {@link Object#equals(Object)}
     * 
     * @hide
     */
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof PluginDescription))
            return false;
        final PluginDescription other = (PluginDescription) o;
        return type.equals(other.type);
    }

    /**
     * Please refer to {@link Object#toString()}
     * 
     * @hide
     */
    @Override
    public String toString() {
        return "PluginDescription {type=" + type + "}";
    }

    /**
     * This is a standard parcelable skeleton.
     * <p>
     * Please refer to {@link Parcelable.Creator} for more information.
     * 
     * @hide
     */
    public static final Creator<PluginDescription> CREATOR = new Creator<PluginDescription>() {

        /**
         * This is a standard parcelable skeleton.
         * <p>
         * Please refer to {@link Parcelable.Creator#createFromParcel(Parcel)} for more information.
         * 
         * @hide
         */
        @Override
        public PluginDescription createFromParcel(Parcel source) {
            return new PluginDescription(source);
        }

        /**
         * This is a standard parcelable skeleton.
         * <p>
         * Please refer to {@link Parcelable.Creator#newArray(int)} for more information.
         * 
         * @hide
         */
        @Override
        public PluginDescription[] newArray(int size) {
            return new PluginDescription[size];
        }
    };
}
