package com.htc.lib1.HtcEasPim.eas;

import java.util.ArrayList;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * {@exthide}
 */
public class EASResolveRecipientsResult implements Parcelable {


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
     * The status for the searching operation
     */
    protected int nStatus;
    
    
		protected int nResolveReturnStatus;
   
    protected ArrayList<EASResolveRecipientsResponse> elements;

    /**
     * Constructor
     */
    public EASResolveRecipientsResult() {
       
        nStatus = STATUS_FAIL;
      
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
    
    
   
    public void setResolveReturnStatus(int resolvereturnstatus) {
        nResolveReturnStatus = resolvereturnstatus;
    }

    public int getResolveReturnStatus() {
        return nResolveReturnStatus;
    }

    /**
     * Set the element list.
     * 
     * @param elementList Set the EASResolveRecipientsResponse list
     */
    public void setElements(ArrayList<EASResolveRecipientsResponse> elementList) {
        elements = elementList;
    }

    /**
     * Get the element list.
     * 
     * @return The list of EASResolveRecipientsResponses
     */
    public ArrayList<EASResolveRecipientsResponse> getElements() {
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
         out.writeInt(nStatus);
         out.writeInt(nResolveReturnStatus);
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
     public static final Parcelable.Creator<EASResolveRecipientsResult> CREATOR
             = new Parcelable.Creator<EASResolveRecipientsResult>()
     {
         public EASResolveRecipientsResult createFromParcel(Parcel in) {
             return new EASResolveRecipientsResult(in);
         }

         public EASResolveRecipientsResult[] newArray(int size) {
             return new EASResolveRecipientsResult[size];
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
    private EASResolveRecipientsResult(Parcel in) {
        
        nStatus = in.readInt();
				nResolveReturnStatus = in.readInt();
				
        int elementCount = in.readInt();
        if (elementCount > 0) {
            elements = new ArrayList<EASResolveRecipientsResponse>(elementCount);
            for (int i = 0; i < elementCount; i++) {
                elements.add((EASResolveRecipientsResponse)in.readParcelable(com.htc.lib1.HtcEasPim.eas.EASResolveRecipientsResponse.class.getClassLoader()));
            }
        }
    }
}

