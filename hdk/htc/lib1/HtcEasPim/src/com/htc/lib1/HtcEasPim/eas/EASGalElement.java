package com.htc.lib1.HtcEasPim.eas;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * {@exthide}
 */
public class EASGalElement implements Parcelable {

/**
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
    public String ClientId;

/**
 *  Hide automatically by SDK TEAM [U12000] 
 *  @hide
 */
    public String ServerID;

    //Code Page 16: GAL
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public String DisplayName;      // 05
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public String Phone;            // 06
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public String Office;           // 07
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public String Title;            // 08
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public String Company;          // 09
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public String Alias;            // 0A
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public String FirstName;        // 0B
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public String LastName;         // 0C
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public String HomePhone;        // 0D
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public String MobilePhone;      // 0E
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public String EmailAddress;     // 0F

    /**
     * Constructor
     */
    public EASGalElement() {
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setClientId(String clientId) {
        ClientId = clientId;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public String getClientId() {
        return ClientId;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void setServerID(String serverId) {
        ServerID = serverId;
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public String getServerID() {
        return ServerID;
    }

    /**
     * Set the display name.
     * 
     * @param displayName The display name
     */
    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    /**
     * Get the display name.
     * 
     * @return the string of display name
     */
    public String getDisplayName() {
        return DisplayName;
    }

    /**
     * Set the phone number.
     * 
     * @param phone The phone number
     */
    public void setPhone(String phone) {
        Phone = phone;
    }

    /**
     * Get the phone number.
     * 
     * @return The string type of phone
     */
    public String getPhone() {
        return Phone;
    }

    /**
     * Set the office location.
     * 
     * @param office the office location
     */
    public void setOffice(String office) {
        Office = office;
    }

    /**
     * Get the office location.
     * 
     * @return The string type of office location
     */
    public String getOffice() {
        return Office;
    }

    /**
     * Set title.
     * 
     * @param title The title
     */
    public void setTitle(String title) {
        Title = title;
    }

    /**
     * Get title
     * 
     * @return The string type of title
     */
    public String getTitle() {
        return Title;
    }

    /**
     * Set company.
     * 
     * @param company The company
     */
    public void setCompany(String company) {
        Company = company;
    }

    /**
     * Get the company.
     * 
     * @return The string type of company
     */
    public String getCompany() {
        return Company;
    }

    /**
     * Set alias.
     * 
     * @param alias The alias
     */
    public void setAlias(String alias) {
        Alias = alias;
    }

    /**
     * Get alias.
     * 
     * @return The string type of alias
     */
    public String getAlias() {
        return Alias;
    }

    /**
     * Set the first name.
     * 
     * @param firstname The first name
     */
    public void setFirstName(String firstname) {
        FirstName = firstname;
    }

    /**
     * Get the first name.
     * 
     * @return The string type of first name
     */
    public String getFirstName() {
        return FirstName;
    }

    /**
     * Set last name.
     * 
     * @param lastname The last name
     */
    public void setLastName(String lastname) {
        LastName = lastname;
    }

    /**
     * Set the last name.
     * 
     * @return The string type of last name
     */
    public String getLastName() {
        return LastName;
    }

    /**
     * Set home phone.
     * 
     * @param homephone The home phone
     */
    public void setHomePhone(String homephone) {
        HomePhone = homephone;
    }

    /**
     * Get the home phone.
     * 
     * @return The string type of home phone
     */
    public String getHomePhone() {
        return HomePhone;
    }

    /**
     * Set mobile phone.
     * 
     * @param mobilephone The mobile phone
     */
    public void setMobilePhone(String mobilephone) {
        MobilePhone = mobilephone;
    }

    /**
     * Get the mobile phone.
     * 
     * @return the string type of mobile phone
     */
    public String getMobilePhone() {
        return MobilePhone;
    }

    /**
     * Set the email address.
     * 
     * @param emailaddress The email address
     */
    public void setEmailAddress(String emailaddress) {
        EmailAddress = emailaddress;
    }

    /**
     * Get the email address.
     * 
     * @return The string type of email address
     */
    public String getEmailAddress() {
        return EmailAddress;
    }

/**
 *  Hide automatically by SDK TEAM [U12000] 
 *  
 *  @param out The parcel object write data
 *  @param flags The object how to be written
 *  @hide
 */
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(DisplayName);
        out.writeString(Phone);
        out.writeString(Office);
        out.writeString(Title);
        out.writeString(Company);
        out.writeString(Alias);
        out.writeString(FirstName);
        out.writeString(LastName);
        out.writeString(HomePhone);
        out.writeString(MobilePhone);
        out.writeString(EmailAddress);
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public static final Parcelable.Creator<EASGalElement> CREATOR
            = new Parcelable.Creator<EASGalElement>() 
    {
        public EASGalElement createFromParcel(Parcel in) {
            return new EASGalElement(in);
        }

        public EASGalElement[] newArray(int size) {
            return new EASGalElement[size];
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
   private EASGalElement(Parcel in) {
       DisplayName = in.readString();
       Phone = in.readString();
       Office = in.readString();
       Title = in.readString();
       Company = in.readString();
       Alias = in.readString();
       FirstName = in.readString();
       LastName = in.readString();
       HomePhone = in.readString();
       MobilePhone = in.readString();
       EmailAddress = in.readString();
   }
}
