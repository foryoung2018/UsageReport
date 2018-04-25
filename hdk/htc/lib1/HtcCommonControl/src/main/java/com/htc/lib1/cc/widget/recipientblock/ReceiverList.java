package com.htc.lib1.cc.widget.recipientblock;

import android.view.View;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class ReceiverList implements Parcelable {
    private static final String TAG = "ReceiverList";
    private static final int SQLITE_MAX_VARIABLE_NUMBER = 999;

    public long id = -1;
    public long contactId = -1;
    public long methodId;
    public long typeId;
    public String addr;
    public String name;
    public int group;
    int width;
    public boolean haveDisplayName;
    public boolean canAddToGroup;
    public Bitmap contactPhoto = null;
    public Uri fromEmailUri;
    public boolean isContactUpdated = false;
    public long mPhotoId = -1;

    public View view;

    public static final Parcelable.Creator<ReceiverList> CREATOR = new Parcelable.Creator<ReceiverList>() {
        public ReceiverList createFromParcel(Parcel in) {
            return new ReceiverList(in);
        }

        public ReceiverList[] newArray(int size) {
            return new ReceiverList[size];
        }
    };

    private ReceiverList(Parcel in) {
        readFromParcel(in);
    }

    public ReceiverList() {
    }

    public ReceiverList(Uri fromEmailUri, Bitmap contactPhoto) {
        this.fromEmailUri = fromEmailUri;
        this.contactPhoto = contactPhoto;
    }

    public ReceiverList(boolean hasDisplayName, String name, String addr, int methodId, int contactId) {
        this.name = name;
        this.addr = addr;
        this.methodId = methodId;
        this.contactId = contactId;
    }

    public ReceiverList(long id, String name, String addr, long methodId, long contactId) {
        this.id = id;
        this.name = name;
        this.addr = addr;
        this.methodId = methodId;
        this.contactId = contactId;
    }

    public ReceiverList(ReceiverList receiver) {
        this.id = receiver.id;
        this.name = receiver.name;
        this.addr = receiver.addr;
        this.methodId = receiver.methodId;
        this.contactId = receiver.contactId;
        this.typeId = receiver.typeId;
        this.group = receiver.group;
        this.width = receiver.width;
        this.haveDisplayName = receiver.haveDisplayName;
        this.canAddToGroup = receiver.canAddToGroup;
        this.contactPhoto = receiver.contactPhoto;
        this.fromEmailUri = receiver.fromEmailUri;
        this.isContactUpdated = receiver.isContactUpdated;
    }

    public boolean contactExists() {
        if(contactId == -1) {
            return false;
        }
        return true;
    }

    public boolean hasDisplayName() {
        if(name != null && !"".equals(name)) {
            return true;
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(contactId);
        dest.writeLong(methodId);
        dest.writeLong(typeId);
        dest.writeString(addr);
        dest.writeString(name);
        dest.writeInt(group);
        dest.writeInt(width);
        dest.writeByte(haveDisplayName? (byte)1 : (byte)0);
        dest.writeByte(canAddToGroup? (byte)1 : (byte)0);
        dest.writeParcelable(contactPhoto, 0);
    }

    public void readFromParcel(Parcel in) {
        id = in.readLong();
        contactId = in.readLong();
        methodId = in.readLong();
        typeId = in.readLong();
        addr = in.readString();
        name = in.readString();
        group = in.readInt();
        width = in.readInt();
        haveDisplayName = in.readByte() == 1;
        canAddToGroup = in.readByte() == 1;
        contactPhoto = in.readParcelable(null);
    }
}

