package com.htc.lib1.HtcEasPim.eas;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * {@exthide}
 */
public class EASResolveRecipientsResponse implements Parcelable {


    public String To;


    public int Status;


    public int RecipientCount;      // 05


		protected ArrayList<EASResolveRecipientsRecipientElement> elements;

    /**
     * Constructor
     */
    public EASResolveRecipientsResponse() {
    }


    public void setTo(String to) {
        To = to;
    }

    public String getTo() {
        return To;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getStatus() {
        return Status;
    }

    public void setRecipientCount(int recipientCount) {
        RecipientCount = recipientCount;
    }

    public int getRecipientCount() {
        return RecipientCount;
    }



    public void setElements(ArrayList<EASResolveRecipientsRecipientElement> elementList) {
        elements = elementList;
    }

   
    public ArrayList<EASResolveRecipientsRecipientElement> getElements() {
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
        out.writeString(To);
        out.writeInt(Status);
        out.writeInt(RecipientCount);
        
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
    public static final Parcelable.Creator<EASResolveRecipientsResponse> CREATOR
            = new Parcelable.Creator<EASResolveRecipientsResponse>() 
    {
        public EASResolveRecipientsResponse createFromParcel(Parcel in) {
            return new EASResolveRecipientsResponse(in);
        }

        public EASResolveRecipientsResponse[] newArray(int size) {
            return new EASResolveRecipientsResponse[size];
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
   private EASResolveRecipientsResponse(Parcel in) {
		   To = in.readString();
		   Status = in.readInt();
		   RecipientCount = in.readInt();
       
      int elementCount = in.readInt();
      if (elementCount > 0) {
          elements = new ArrayList<EASResolveRecipientsRecipientElement>(elementCount);
          for (int i = 0; i < elementCount; i++) {
              elements.add((EASResolveRecipientsRecipientElement)in.readParcelable(com.htc.lib1.HtcEasPim.eas.EASResolveRecipientsRecipientElement.class.getClassLoader()));
          }
      }
      
   }
}
