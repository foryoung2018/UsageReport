
package com.htc.lib1.cs.account;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * {@link Account}-equivalent implementation for HTC Account authenticator.
 */
public class HtcAccount implements Parcelable {

    /**
     * Creator for {@link HtcAccount}.
     */
    public static final Creator<HtcAccount> CREATOR = new Creator<HtcAccount>() {
        public HtcAccount createFromParcel(Parcel source) {
            return new HtcAccount(source);
        }

        public HtcAccount[] newArray(int size) {
            return new HtcAccount[size];
        }
    };

    public final String name;
    public final String type;

    /**
     * Construct an {@link HtcAccount} from an {@link Account}.
     * 
     * @param account
     */
    public HtcAccount(Account account) {
        this(account.name, account.type);
    }

    /**
     * Construct an {@link HtcAccount} with given name and type.
     * 
     * @param name
     * @param type
     */
    public HtcAccount(String name, String type) {
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("the name must not be empty: " + name);
        }
        if (TextUtils.isEmpty(type)) {
            throw new IllegalArgumentException("the type must not be empty: " + type);
        }
        this.name = name;
        this.type = type;
    }

    /**
     * Construct an {@link HtcAccount} from a {@link Parcel}
     * 
     * @param in
     */
    public HtcAccount(Parcel in) {
        this.name = in.readString();
        this.type = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(type);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof HtcAccount))
            return false;
        final HtcAccount other = (HtcAccount) o;
        return name.equals(other.name) && type.equals(other.type);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + name.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "HtcAccount {name=" + name + ", type=" + type + "}";
    }

    /**
     * Create an equivalent {@link Account} instance.
     * 
     * @return {@link Account}
     */
    public Account toAccount() {
        return new Account(name, type);
    }
}
