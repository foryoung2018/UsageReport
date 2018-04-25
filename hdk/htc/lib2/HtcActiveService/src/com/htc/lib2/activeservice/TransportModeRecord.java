package com.htc.lib2.activeservice;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class representing a transport record including timestamp, mode and steps information.
 */

public class TransportModeRecord implements Parcelable {
    /**
     * Transport mode is still(stop).
     * */
    public final static int MODE_STILL = 0;
    /**
     * Transport mode is walk.
     * */
    public final static int MODE_WALK = 1;
    /**
     * Transport mode is run.
     * */
    public final static int MODE_RUN = 2;
    /**
     * Transport mode is bicycle.
     * */
    public final static int MODE_BICYCLE = 3;
    /**
     * Transport mode is vehicle.
     * */
    public final static int MODE_VEHICLE = 4;

    /**
     * Transport mode is upstairs.
     * */
    public final static int MODE_UPSTAIRS = 5;
    /**
     * Transport mode is downstairs.
     * */
    public final static int MODE_DOWNSTAIRS = 6;

    /**
     * Transport mode is unknown.
     * */
    public final static int MODE_UNKNOWN = 7;

    private long timestamp;
    private int mode;
    private int steps;
    private int period;
    private float met;
    private float barometer;

    /**
     * @hide
     * */
    public TransportModeRecord(TransportModeRecord r) {
        this.timestamp = r.getTimestamp();
        this.mode = r.getMode();
        this.steps = r.getSteps();
        this.period = r.getPeriod();
        this.met = r.getMET();
        this.barometer = r.getBarometerData();
    }

    /**
     * @hide
     * */
    public TransportModeRecord(long timestamp, int mode, int steps, int period, float barometer, float met) {
        this.timestamp = timestamp;
        this.mode = mode;
        this.steps = steps;
        this.period = period;
        this.met = met;
        this.barometer = barometer;
    }
    /**
     * @hide
     * */
    public TransportModeRecord(long timestamp, int mode, int steps, int period, float barometer, int accLevel) {
        this.timestamp = timestamp;
        this.mode = mode;
        this.steps = steps;
        this.period = period;
        this.met = accLevel2MET(accLevel, period);
        this.barometer = barometer;
    }

    /**
     * @hide
     * */
    public TransportModeRecord(Parcel in) {
        timestamp = in.readLong();
        mode = in.readInt();
        steps = in.readInt();
        period = in.readInt();
        met = in.readFloat();
        barometer = in.readFloat();
    }

    /**
     * @hide
     * */
    public TransportModeRecord(String str) {
        String[] strs = str.split(" ");
        timestamp = Long.parseLong(strs[0]);
        mode = Integer.parseInt(strs[1]);
        steps = Integer.parseInt(strs[2]);
        period = Integer.parseInt(strs[3]);
        met = Float.parseFloat(strs[4]);
        barometer = Float.parseFloat(strs[5]);
    }

    /**
     * @hide
     * */
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @hide
     * */
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        dest.writeLong(timestamp);
        dest.writeInt(mode);
        dest.writeInt(steps);
        dest.writeInt(period);
        dest.writeFloat(met);
        dest.writeFloat(barometer);
    }

    /**
     * @hide
     * */
    public static final Parcelable.Creator<TransportModeRecord> CREATOR = new Parcelable.Creator<TransportModeRecord>() {

        @Override
        public TransportModeRecord createFromParcel(Parcel in) {
            return new TransportModeRecord(in);
        }
        @Override
        public TransportModeRecord[] newArray(int size) {
            return new TransportModeRecord[size];
        }

    };

    /**
     * @return the Unix time (epoch time) of this record.
     * The time in milliseconds since January 1, 1970 00:00:00.0 UTC.
     * This timestamp is the end time of this record.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @return the transport mode of this record
     */
    public int getMode() {
        return mode;
    }

    /**
     * @return the steps of this record
     */
    public int getSteps() {
        return steps;
    }

    /**
     * @return the period(ms) of this record
     */
    public int getPeriod() {
        return period;
    }

    /**
     * @return the MET(metabolic equivalent) of this record.
     * The energy cost of an activity can be measured in units called METS, which are multiples of your basal metabolic rate.
     * One MET is defined as 1 kcal/kg/hour.
     */
    public float getMET() {
        return met;
    }

    /**
     * @deprecated
     * @return the barometer data. Atmospheric pressure in hPa (millibar)
     */
    public float getBarometerData() {
        return barometer;
    }

    //ms
    private float accLevel2MET(int movelevel, int peroid) {
        float MET = 0.688f * movelevel + 1.0522f;
        return MET;
    }

    public String toString() {
        String result = timestamp + " " + mode + " " + steps + " " + period + " " + met + " " + barometer;
        return result;
    }
}
