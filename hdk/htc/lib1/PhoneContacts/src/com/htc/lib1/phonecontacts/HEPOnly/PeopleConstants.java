
/**
 * HTC Corporation Proprietary Rights Acknowledgment
 *
 * Copyright (C) 2008 HTC Corporation
 *
 * All Rights Reserved.
 *
 * The information contained in this work is the exclusive property of
 * HTC Corporation ("HTC"). Only the user who is legally
 * authorized by HTC ("Authorized User") has right to employ this work
 * within the scope of this statement. Nevertheless, the Authorized User
 * shall not use this work for any purpose other than the purpose agreed by HTC.
 * Any and all addition or modification to this work shall be unconditionally
 * granted back to HTC and such addition or modification shall be solely owned by HTC.
 * No right is granted under this statement, including but not limited to,
 * distribution, reproduction, and transmission, except as otherwise provided in this statement.
 * Any other usage of this work shall be subject to the further written consent of HTC.
 *
 * @author: William Chao
 *
 * The ultima purpose of this file is to extend ContactsContract in the android.provider
 * package but not replace it. Hopefully, this helps to reduce the amount of direct
 * modification to ContactsContract and then eases our future porting efford.
 */
package com.htc.lib1.phonecontacts.HEPOnly;

import android.net.Uri;
import android.provider.ContactsContract;




public final class PeopleConstants {
    
    /**
     * The account name for the Outlook sync. Note that PC sync uses a single
     * account for contacts sync so this same account name will be used across
     * all PC sync.
     */
	public static final String ACCOUNT_NAME_PC = "pcsc";

    /**
     * The account type for the Outlook sync.
     */
	public static final String ACCOUNT_TYPE_PC = "com.htc.android.pcsc";

    /**
     * The account type for the Exchange sync.
     */
	public static final String ACCOUNT_TYPE_HTC_EXCHANGE = "com.htc.android.mail.eas";

    /**
     * The account type for the Windows Live.
     */
	public static final String ACCOUNT_TYPE_WINDOWSLIVE = "com.htc.android.windowslive";

    /**
     * The account type for the Google sync.
     */
	public static final String ACCOUNT_TYPE_GOOGLE = "com.google";

    /**
     * The account type for the HTC Facebook sync.
     */
    public static final String ACCOUNT_TYPE_FACEBOOK = "com.htc.socialnetwork.facebook";

    /**
     * The account type for the HTC Facebook sync.
     */
    public static final String ACCOUNT_TYPE_FLICKR = "com.htc.socialnetwork.flickr";

    /**
     * The account type for the HTC Plurk sync.
     */
    public static final String ACCOUNT_TYPE_PLURK = "com.htc.socialnetwork.plurk";

    /**
     * The account type for the HTC Twitter sync.
     */
    public static final String ACCOUNT_TYPE_TWITTER = "com.htc.htctwitter";

    /**
     * The account type for the original Facebook
     */
    public static final String ACCOUNT_TYPE_ORIGINAL_FACEBOOK="com.facebook.auth.login";

    
    /**
     * The account type for the original Twitter
     */
    public static final String ACCOUNT_TYPE_ORIGINAL_TWITTER="com.twitter.android.auth.login";
  
  	/**
    	 *DEFAULT_GROUP_COWORK
  	 */
   	public static final String DEFAULT_GROUP_COWORK = "Coworkers";
   	
   	/**
      * DEFAULT_GROUP_FAMILY
    */
    public static final String DEFAULT_GROUP_FAMILY = "Family";
    
    /**
     * DEFAULT_GROUP_FRIEND
     */
    public static final String DEFAULT_GROUP_FRIEND = "Friends";

    /**
     * default group name
     */
    public static final String DEFAULT_GROUP_FAVORITE= "Favorite_8656150684447252476_6727701920173350445";

  
    /**
     * DEFAULT_GROUP_FREQUENT_CONTACT
     */
    public static final String DEFAULT_GROUP_FREQUENT_CONTACT = "Frequent Contacts";
    

    /**
     * default group name
     */
    public static final String DEFAULT_GROUP_IMPORTANCE = "VIP";
    

    /**
     * Intent sent when contacts changed
     */
    public static final String ACTION_FAVORITE_CHANGE = "com.htc.intent.action.FAVORITE_CHANGE";

    
    public static final class PrivateUri {        
        private static final String AUTHORITY = ContactsContract.AUTHORITY;
        private static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
        /**
         * The content:// style URI for this table joined with details data from
         */
        public static final Uri CONTENT_NAME_LIST_URI = Uri.withAppendedPath(AUTHORITY_URI,"groups_name_list");
        
        /**
         * Append group's title to this Uri
         */
        public static Uri ORDERED_GROUP_CONTACT_URI = Uri.withAppendedPath(android.provider.ContactsContract.Contacts.CONTENT_URI, "order_group_favorite_contact");
        
        
        public static final Uri BLACKLIST_CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI,"blacklist");
        
        public static final class Frequency{ 
            /**
             * TIMES
             */
            public final static String TIMES = "times";
        }
    }

}