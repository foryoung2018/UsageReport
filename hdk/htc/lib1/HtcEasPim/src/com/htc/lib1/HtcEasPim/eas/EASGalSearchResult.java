package com.htc.lib1.HtcEasPim.eas;

import java.util.ArrayList;

import com.htc.lib1.HtcEasPim.eas.EASGalElement;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * {@exthide}
 */
public class EASGalSearchResult implements Parcelable {

    /**
     * Search result status value.
     * 
     * @see EASGalSearchResult#getStatus()
     * @see #STATUS_SERVER_ERROR
     * @see #STATUS_FAIL
     */
    public static final int STATUS_OK = 0;

    /**
     * Search result status value.
     * 
     * @see EASGalSearchResult#getStatus()
     * @see #STATUS_OK
     * @see #STATUS_SERVER_ERROR
     */
    public static final int STATUS_FAIL = -1;

    /**
     * Search result status value.
     * 
     * @see EASGalSearchResult#getStatus()
     * @see #STATUS_OK
     * @see #STATUS_FAIL
     */
    public static final int STATUS_SERVER_ERROR = -2;

    /**
     * The total counts of match the searching operation.
     */
    protected int nTotal;

    /**
     * The status for the searching operation
     */
    protected int nStatus;

    /**
     * Status code for the child element
     */
    protected int nStoreReturnCode;

    /**
     * Status code for the searching node
     */
    protected int nSearchReturnCode;

    /**
     * The EASGalElement list
     */
    protected ArrayList<EASGalElement> elements;

    /**
     * Constructor
     */
    public EASGalSearchResult() {
        nTotal = 0;
        nStatus = STATUS_FAIL;
        nStoreReturnCode  = -1;
        nSearchReturnCode = -1;
    }

    /**
     * Set the total counts.
     * 
     * @param total The counts match the searching operation
     */
    public void setTotal(int total) {
        nTotal = total;
    }

    /**
     * Get the total counts.
     * 
     * @return The total counts which server response
     */
    public int getTotal() {
        return nTotal;
    }

    /**
     * Set the searching return code.
     * 
     * @param returnCode the search return code
     */
    public void setSearchReturnCode(int returnCode) {
        nSearchReturnCode = returnCode;
    }

    /**
     * Get the searching return code.
     * 
     * @return the search return code
     */
    public int getSearchReturnCode() {
        return nSearchReturnCode;
    }

    /**
     * Set the store return code.
     * 
     * @param storeReturnCode the store return code
     */
    public void setStoreReturnCode(int storeReturnCode) {
        nStoreReturnCode = storeReturnCode;
    }

    /**
     * Get the store return code.
     * 
     * @return the store return code
     */
    public int getStoreReturnCode() {
        return nStoreReturnCode;
    }

    /**
     * Set the status.
     * 
     * @param status The status value response from Exchange server
     */
    public void setStatus(int status) {
        nStatus = status;
    }

    /**
     * Get the status.
     * 
     * @return The status value of searching operation
     */
    public int getStatus() {
        return nStatus;
    }

    /**
     * Set the element list.
     * 
     * @param elementList Set the EASGalElement list
     */
    public void setElements(ArrayList<EASGalElement> elementList) {
        elements = elementList;
    }

    /**
     * Get the element list.
     * 
     * @return The list of EASGalElements
     */
    public ArrayList<EASGalElement> getElements() {
        return elements;
    }

/**
 *  Hide automatically by SDK TEAM [U12000] 
 *  
 *  @param out The parcel object write data
 *  @param flags The object how to be written
 *  @hide
 */
     public void writeToParcel(Parcel out, int flags) {
         out.writeInt(nTotal);
         out.writeInt(nSearchReturnCode);
         out.writeInt(nStoreReturnCode);
         out.writeInt(nStatus);
         if ((elements != null) && (elements.size() > 0)) {
            int elementCount = elements.size();
            out.writeInt(elementCount);
            for (int i = 0; i < elementCount; i++) {
                out.writeParcelable(elements.get(i), flags);
            }
         }
     }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
     public static final Parcelable.Creator<EASGalSearchResult> CREATOR
             = new Parcelable.Creator<EASGalSearchResult>()
     {
         public EASGalSearchResult createFromParcel(Parcel in) {
             return new EASGalSearchResult(in);
         }

         public EASGalSearchResult[] newArray(int size) {
             return new EASGalSearchResult[size];
         }
     };
     

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
     * @deprecated [Module internal use]
     */
    /**@hide*/ 
    private EASGalSearchResult(Parcel in) {
        nTotal = in.readInt();
        nSearchReturnCode = in.readInt();
        nStoreReturnCode = in.readInt();
        nStatus = in.readInt();

        int elementCount = in.readInt();
        if (elementCount > 0) {
            elements = new ArrayList<EASGalElement>(elementCount);
            for (int i = 0; i < elementCount; i++) {
                elements.add((EASGalElement)in.readParcelable(EASGalElement.class.getClassLoader()));
            }
        }
    }
}

