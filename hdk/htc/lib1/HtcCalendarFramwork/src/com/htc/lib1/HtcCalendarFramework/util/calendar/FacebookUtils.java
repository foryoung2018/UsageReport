package com.htc.lib1.HtcCalendarFramework.util.calendar;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import com.htc.lib1.HtcCalendarFramework.provider.HtcExCalendar;
import android.text.TextUtils;

/**
  * The Facebook Utils class
  */
public class FacebookUtils {

        /**
          * The FacebookUtils constructor
          * @deprecated [Not use any longer]
          */
     	/**@hide*/ 
        public FacebookUtils() {
        }
        
	private static final String TAG = "FacebookUtils";
	
	/**
	  * Define the linked account type constant value
	  */
	public static final String LINKED_ACCOUNT_TYPE="linked_account_type";
	
	/**
	  * To get the raw contact uri
	  * @param context Context The Context
	  * @param uid String The user id
	  * @return Uri Return the raw contact URI
	  */
	public static final Uri getRawContactUri(Context context, String uid) {
        Uri uri = Uri.EMPTY;
        final long raw_contact_id = getRawContactId(context, uid);
        if ( raw_contact_id != -1 ) {
            uri = ContentUris.withAppendedId(
                ContactsContract.Contacts.CONTENT_URI, raw_contact_id);
        }
        return uri;
    }

/**
 * 20120629 Cherry + for Sense 4+, to get Contacts according to fb source id
 * DataSet is used to distinguish google and google plus due to the same account type
 * 		- if DataSet is not null, then it's google plus account.   
 * LOOKUP_KEY is used to check if contact have multiple accounts linked.
 * @param context Context The Context
 * @param uid String The uid
 * @return FBContacts Return the FBContacts data
 * @deprecated [Not use any longer]
 * */
   /**@hide*/ 
	public static final FBContacts getContactsBySourceId(Context context, String uid){
    	    
	    Uri uri = Uri.EMPTY;

    	long raw_contact_id = -1;
        String account_type = "";
        String dataset="";
        Cursor c = null;
        try {
            c = context.getContentResolver()
                .query(	Data.CONTENT_URI,
                    new String[] { RawContacts.CONTACT_ID,RawContacts.DATA_SET, RawContacts.ACCOUNT_TYPE,Contacts.LOOKUP_KEY },
                       RawContacts.SOURCE_ID + "=?",
                    new String[] {uid }, null);
            if ( c != null ) {
                while ( c.moveToNext() ) {
                    raw_contact_id = c.getLong(c.getColumnIndexOrThrow(RawContacts.CONTACT_ID));
                	dataset = c.getString(c.getColumnIndexOrThrow(RawContacts.DATA_SET));
                	String lookup = c.getString(c.getColumnIndexOrThrow(Contacts.LOOKUP_KEY));
                	account_type = c.getString(c.getColumnIndexOrThrow(RawContacts.ACCOUNT_TYPE));
                	 if ( raw_contact_id != -1 ) {
                         uri = ContentUris.withAppendedId(
                             ContactsContract.Contacts.CONTENT_URI, raw_contact_id);
                     }
                	
                	 return new FBContacts(account_type, dataset,lookup,raw_contact_id,uri);
                }
            }
        } catch ( Exception e ) {
            Log.e(TAG, "getContactsBySourceId failed! uid=" + uid, e);
        } finally {
            if ( c != null ) {
                c.close();
            }
        }
        return null;
	}
	
/**
  * To get the raw contact ID
  * @param context the Context
  * @param uid the uid string
  *  Hide automatically by SDK TEAM [U12000] 
  *  @hide
  */
    public static final long getRawContactId(Context context, String uid) {
        long raw_contact_id = -1;
        Cursor c = null;
        try {
            c = context.getContentResolver()
                .query(
                    RawContacts.CONTENT_URI,
                    new String[] { RawContacts.CONTACT_ID },
                       RawContacts.SOURCE_ID + "=?",
                    new String[] {uid }, null);
            if ( c != null ) {
                while ( c.moveToNext() ) {
                    raw_contact_id = c.getLong(c.getColumnIndexOrThrow(RawContacts.CONTACT_ID));
                }
            }
        } catch ( Exception e ) {
            Log.e(TAG, "getRawContactId failed! uid=" + uid, e);
        } finally {
            if ( c != null ) {
                c.close();
            }
        }
        return raw_contact_id;
    }

	/**
	  * The api to check if contact is linked type
	  * @param lookupKey String The lookup key string value
	  * @return boolean Return true when lookup key is linked the contact, otherwise return false
	  * @deprecated [Module internal use]
	  */
    /**@hide*/ 
    public static boolean isLinkedContact(String lookupKey){ 

                if(TextUtils.isEmpty(lookupKey)){
                        return false;
                }
                boolean isLinked = false;
                int size = lookupKey.length();
                //contains single . is linked contact
                for(int i=0;i<size;i++){
                        if(lookupKey.charAt(i)=='.' && !(((i+1) < size) && lookupKey.charAt(i+1)=='.') && !(((i-1) > 0) && lookupKey.charAt(i-1)=='.')){
                                isLinked = true;
                                break;
                        }
                }
                return isLinked;
        }
    
        /**
          * The Facebook contacts
          */
	public static class FBContacts {

                /**
                  * The FBContacts constructor
                  * @deprecated [Not use any longer]
                  */
				  /**@hide*/ 
                  public FBContacts() {
                  }
                  
		private String mAccount_Type="";
		private String mDataSet="";
		private String mLookup="";
		private long mRaw_Contact_Id=0;
		private Uri mUri;

                /**
                  * The FBContacts constructor
                  * @param account_type the account type string
                  * @param dataset the data set string
                  * @param lookup the lookup string
                  * @param contactid the contact id
                  * @param uri the Uri
                  * @deprecated [Not use any longer]
                  */
		/**@hide*/ 
		public FBContacts(String account_type, String dataset,String lookup, long contactid, Uri uri) {
			mAccount_Type = account_type;
			mDataSet = dataset;
			mLookup = lookup;
			mRaw_Contact_Id = contactid;
			mUri=uri;
		}

                /**
                  * To get the account type
                  * @return the account type value
                  * @deprecated [Not use any longer]
                  */
		/**@hide*/ 
		public String getAccountType(){
			
			if (!TextUtils.isEmpty(mLookup) && isLinkedContact(mLookup)){
				mAccount_Type=LINKED_ACCOUNT_TYPE;
        	}
			return mAccount_Type;
		}
	    
	        /**
	          * To get the data set
	          * @return the data set string value
	          * @deprecated [Not use any longer]
	          */
		/**@hide*/ 
		public String getDataSet(){
			return mDataSet;
		}
		
		/**
		  * To get lookup
		  * @return the lookup string value
		  * @deprecated [Not use any longer]
		  */
		/**@hide*/ 
		public String getLoolup(){
			return mLookup;
		}
		
		/**
		  * To get contact id
		  * @return the raw contact id value
		  * @deprecated [Not use any longer]
		  */
		/**@hide*/ 
		public long getContactId(){
			return mRaw_Contact_Id;
		}
		
		/**
		  * To get the Uri
		  * @return the Uri value
		  * @deprecated [Not use any longer]
		  */
		/**@hide*/ 
		public Uri getUri(){
			return mUri;
		}
	}
}
