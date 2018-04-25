package com.htc.lib1.HtcEasPim.hux;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * {@exthide}
 */
public class HuxProvResult implements Parcelable{
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public int status;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public int errorCode;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public String errorMessage;
    
    /**
     * Constructor.
     * 
     * @param status The status code
     * @param errorCode The error code
     * @param errorMessage The error message
     */
    public HuxProvResult(int status, int errorCode,String errorMessage){
        this.status =  status;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * Get status.
     * 
     * @return The integer type of status
     */
    public int getStatus() {
        return status;
    }

    /**
     * Get error code.
     * 
     * @return The integer type of error code
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Get error message.
     * 
     * @return The error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * @deprecated [Module internal use]
     */
    /**@hide*/ 
    public HuxProvResult(Parcel source){
        byte b = 0;
        
        //Reconstruct from the Parcel
        status = source.readInt();
        errorCode  = source.readInt();
        
        b = source.readByte();
        if(b == 0) {
            errorMessage = "";
        }else{
            errorMessage = source.readString();
        }
    }   
    
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override

/**
 *  Hide automatically by SDK TEAM [U12000] 
 *
 *  @return The integer type of contents
 *  @hide
 */
    public int describeContents() {
        return 0;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    @Override

/**
 *  Hide automatically by SDK TEAM [U12000] 
 *
 *  @param dest The parcel object write data
 *  @param flags The object how to be written
 *  @hide
 */
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(status);
        dest.writeInt(errorCode);
        
        if (errorMessage == null || errorMessage.isEmpty()) {
            dest.writeByte((byte)0);
        } else {
            dest.writeByte((byte)1);
            dest.writeString(errorMessage);
        }
           
    }


/**
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
    public static final Parcelable.Creator<HuxProvResult> CREATOR = new Parcelable.Creator<HuxProvResult>() {
        public HuxProvResult createFromParcel(Parcel source) {
            return new HuxProvResult(source);
        }
        public HuxProvResult[] newArray(int size) {
            return new HuxProvResult[size];
        }
    };

    /**
     * Check whether is successful.
     * 
     * @param provResult The provision result
     * @return true if success
     */
    public boolean isSuccessful(HuxProvResult provResult) {
        int status = provResult.status;
        if((status < 200) || (status >= 300)) {
            if(status < 0 ) {
                provResult.errorCode = status;
                provResult.errorMessage = "Cannot connect to server. Your server is not responding.";
            } else if(status == 401) {
                provResult.errorMessage = " Unauthorized";
            }
            return false;
        } else {
            return true;
        }
    }
    
}
