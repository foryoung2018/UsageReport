package com.htc.lib2.activeservice;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;
/**
 * Class representing a transport record query result.
 */
public class TransportRecordsQueryResult implements Parcelable {

    /**
     * The max number of queried transport records.
     */
    public static final int MAX_NUMBER_QUERY_TRANSPORT_RECORDS = 1000;

    /**
     * @deprecated if service is not connected, {@link com.htc.lib2.activeservice.ActiveNotConnectedException} will be thrown.
     * A constant describing the status which
     * the active service is disconnected.
     *
     * */
    public static final int STATUS_SERVICE_DISCONNECTED = 0;

    /**
     * A constant describing the status which
     * the query gets complete transport records.
     * */
    public static final int STATUS_COMPLETE_RECORDS = 1;

    /**
     * A constant describing the status which
     * the query gets incomplete transport records.
     * If you need the complete records, you may query
     * again from the latest records in query result
     * to the end time you want.
     * */
    public static final int STATUS_INCOMPLETE_RECORDS = 2;

    private int status;
    private ArrayList<TransportModeRecord> records;

    public TransportRecordsQueryResult(int status, ArrayList<TransportModeRecord> records) {
        this.status = status;
        if (records != null) {
            this.records = records;
        } else {
            this.records = null;
        }
    }

    /**
     * @hide
     * */
    @SuppressWarnings("unchecked")
    TransportRecordsQueryResult(Parcel in) {
        status = in.readInt();
        records = in.readArrayList(TransportModeRecord.class.getClassLoader());
    }

    /**
     * @return the query status
     * */
    public int getStatus() {
        return status;
    }

    /**
     * @return the query transport records
     * */
    public ArrayList<TransportModeRecord> getRecords() {
        return records;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        dest.writeInt(status);
        dest.writeList(records);
    }

    /**
     * @hide
     * */
    protected static final Parcelable.Creator<TransportRecordsQueryResult> CREATOR = new Parcelable.Creator<TransportRecordsQueryResult>() {

        @Override
        public TransportRecordsQueryResult createFromParcel(Parcel in) {
            return new TransportRecordsQueryResult(in);
        }
        @Override
        public TransportRecordsQueryResult[] newArray(int size) {
            return new TransportRecordsQueryResult[size];
        }

    };
}
