package com.htc.lib1.HtcEasPim.eas;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * {@exthide}
 */
public class EASResolveRecipientsRecipientElement implements Parcelable {


    public int Type;

    public String DisplayName;


    public String EmailAddress;      

    public int Status;            

    public String MergedFreeBusy;           

    public EASResolveRecipientsRecipientElement() {
    }


    public void setType(int type) {
        Type = type;
    }

    public int getType() {
        return Type;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setEmailAddress(String emailAddress) {
        EmailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return EmailAddress;
    }


    public void setStatus(int status) {
        Status = status;
    }

    public int getStatus() {
        return Status;
    }

 
    public void setMergedFreeBusy(String mergedfreebusy) {
        MergedFreeBusy = mergedfreebusy;
    }

    public String getMergedFreeBusy() {
        return MergedFreeBusy;
    }




/**
 *  Hide automatically by SDK TEAM [U12000] 
 *  
 *  @param out The parcel object write data
 *  @param flags The object how to be written
 *  @hide
 */
    public void writeToParcel(Parcel out, int flags) {  
        out.writeInt(Type);
        out.writeString(DisplayName);
        out.writeString(EmailAddress);
        out.writeInt(Status);
        out.writeString(MergedFreeBusy);
       
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public static final Parcelable.Creator<EASResolveRecipientsRecipientElement> CREATOR
            = new Parcelable.Creator<EASResolveRecipientsRecipientElement>() 
    {
        public EASResolveRecipientsRecipientElement createFromParcel(Parcel in) {
            return new EASResolveRecipientsRecipientElement(in);
        }

        public EASResolveRecipientsRecipientElement[] newArray(int size) {
            return new EASResolveRecipientsRecipientElement[size];
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
   private EASResolveRecipientsRecipientElement(Parcel in) {
   	        
       Type = in.readInt();
       DisplayName = in.readString();
       EmailAddress = in.readString();
       Status = in.readInt();
       MergedFreeBusy = in.readString();
      
   }
}
