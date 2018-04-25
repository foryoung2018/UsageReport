
package com.htc.lib1.cs.account.restobj;

import java.net.URL;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.htc.lib1.cs.RestObject;

/**
 * Represents an HTC Account's profile retrieved from identity server.
 * 
 * @author samael_wang@htc.com
 */
public class HtcAccountProfile extends RestObject {
    private static final long serialVersionUID = 1L;

    /**
     * Global Unique ID of the account.
     */
    @SerializedName("Id")
    public String accountId;

    /**
     * Email verification status of the account.
     */
    @SerializedName("IsVerified")
    public boolean isEmailVerified;

    /**
     * User's first name.
     */
    @SerializedName("FirstName")
    public String firstName;

    /**
     * User's last name.
     */
    @SerializedName("LastName")
    public String lastName;

    /**
     * Language code the user used to register.
     */
    @SerializedName("LanguageCode")
    public String languageCode;

    /**
     * The URL of the profile picture for the user.
     */
    @SerializedName("ProfilePicture")
    public URL profilePictureUrl;

    /**
     * Email which can be used to contact the user, if any.
     */
    @SerializedName("ContactEmailAddress")
    public String emailAddress;

    /**
     * Country code of the user.
     */
    @SerializedName("CountryCode")
    public String countryCode;

    /**
     * For regular HTC Account, the value should be the email address user used
     * to register; For social network associated account, the value should be
     * {@code \u0001<social-network-uid>}, where {@code \u0001} is the unicode
     * character 'START OF HEADING' and {@code <social-network-uid>} refers to
     * the {@code uid} of associated social networks such as Google, Facebook,
     * Sina or QQ.
     */
    @SerializedName("EmailAddress")
    public String registrationEmailOrSocialUid;

    @Override
    public boolean isValid() {
        return !TextUtils.isEmpty(accountId);
    }
}
