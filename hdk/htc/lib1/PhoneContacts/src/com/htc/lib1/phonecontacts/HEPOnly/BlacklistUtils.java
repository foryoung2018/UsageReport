/*
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
 * @author: [HTC]
 */
package com.htc.lib1.phonecontacts.HEPOnly;


import java.util.ArrayList;
import java.util.HashSet;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds;

import com.htc.lib0.customization.HtcWrapCustomizationManager;
import com.htc.lib0.customization.HtcWrapCustomizationReader;
import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
import com.htc.lib0.HDKLib0Util;
import com.htc.lib0.HDKLib0Util.HDKException;

import android.text.TextUtils;
import android.util.Log;


/**
 * We do vip and sendToVoicemail check at provider, when set vip, make sure sendToVoicemail is clear and vice versa.
 */
public class BlacklistUtils{
	
	private static final boolean DEBUG = HtcWrapHtcDebugFlag.Htc_DEBUG_flag;
	
	private static final String TAG = "BlacklistUtils"; 	
	

    private static final String ACCOUNT_TYPE_SIM = "com.anddroid.contacts.sim";
    private static final String ACCOUNT_TYPE_SIM_NEW = "com.htc.contacts.sim";
    
	/**
	 * The content:// style URL for this table
	 */
	private static final Uri BLOCKED_SIM_CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI,"blocked_sim_contacts");

	/**
	 * The content:// style URL for this table
	 */
	private static final Uri BLOCKED_NUMBER_CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI,"blacknumbers");

	/**
     * The extra field for the black phone number.
     * <P>Type: String/P>
     */
	private static final String BLOCKED_NUMBER_NUMBER = "number";
	
	/**
	 * The extra field for the VIP column
	 * <P>Type: Integer/P>
	 */
	private static final String VIP_COLUMN = "vip";
	
        

	// + Lucy for notify mms intent  
	/**
	 * BLOCK_UPDATE_INTENT
	 * HTC device only Intent  
	 */
	public static final String BLOCK_UPDATE_INTENT = "com.htc.contacts.actions.BLACK_LIST_UPDATE";

	/**
	 * BLOCK_UPDATE_ID_LIST
	 * HTC device only 
	 */
	public static final String BLOCK_UPDATE_ID_LIST = "block_id_list";
	
	/**
	 * BLOCK_UPDATE_NUMBER_List
	 * HTC device only 
	 */
	public static final String BLOCK_UPDATE_NUMBER_List = "block_number_list";
	// - Lucy for notify mms intent
	
	/**
	 * MANAGE_CONTACT_TYPE	
	 * We make contact have 4 types, undo state, clear state, block only and vip only.
	 * Used for block/vip number, sync and block/vip note
	 */	     
	public static enum MANAGE_CONTACT_TYPE{
		UNDO_STATE, CLEAR_STATE, BLOCK_ONLY, VIP_ONLY
	}
	
	
	private static boolean isAboveSense7 = false;
	private static boolean isSenseDevice = false;
	
	static {
		float sense_version = getSenseVersion();
		isAboveSense7 = sense_version >= 7.0f;
		isSenseDevice = sense_version > 0.0f;
	}
	
	 /**
	  * modify SendToVoiceMail fields and notify 
	  * @param context context 
	  * @param contact_uri contact uri 
	  * @param block block or not
	  * @param is_sim block sim contact or not
	  * @param is_notify need to send broadcast notify or not 	 
	  */
	public static void modifyBlockContact(Context context, Uri contact_uri, boolean block, boolean is_sim, boolean is_notify) throws HDKException{
		if(HDKLib0Util.isHDKLib3SupportedInDevice()==HDKLib0Util.NOT_SUPPORT && !isSenseDevice){
			Log.e(TAG,"not support modifyBlockContact at none hep device");
			throw new HDKException();
		}
		
		long contact_id = ContentUris.parseId(contact_uri);
		if(context==null || contact_id<0){
			Log.e(TAG,"incorrect parameter at modifyBlockContact");
			return;
		}
		
		if(is_sim){
			modifySendToVoiceMailForSim(context, contact_uri, block);
		}else{
			modifySendToVoiceMail(context, contact_uri, block);			
		}
		
		if(is_notify){
			notifyUpdateBlackList(context, (int)contact_id);
		}
	}
	
	/**
	 * modify SendToVoiceMail field
	 * @param context context
	 * @param contact_uri contact uri
	 * @param block block or not 
	 */
	private static void modifySendToVoiceMail(Context context, Uri contact_uri, boolean block){		
		long contact_id = ContentUris.parseId(contact_uri);	
		
		ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
		ContentProviderOperation.Builder operation = ContentProviderOperation.newUpdate(contact_uri);
		operation.withValue(ContactsContract.Contacts.SEND_TO_VOICEMAIL, block ? 1 : 0);	
		//we clear vip column to match with note 
		operation.withValue(VIP_COLUMN, 0);	
		operationList.add(operation.build());
		
		try{
			BlockCallNote.updateNoteForSyncContacts(context, (int)contact_id, block ? MANAGE_CONTACT_TYPE.BLOCK_ONLY:MANAGE_CONTACT_TYPE.CLEAR_STATE , operationList);
		}catch(HDKException e){
			Log.e(TAG,"not support updateNoteForSyncContacts at none hep device");
		}
	}
	
	/**
	 * modify SendtoVoiceMail for SIM  
	 * @param context context 
	 * @param contact_uri contact_uri 
	 * @param block block or not 
	 */
	private static void modifySendToVoiceMailForSim(Context context, Uri contact_uri, boolean block){
		long contact_id = ContentUris.parseId(contact_uri);
		
		updateValueForSIM(context, contact_uri, block);
		Uri simUri = ContentUris.withAppendedId(BLOCKED_SIM_CONTENT_URI,contact_id);
		updateValueSIMRecordTable(context, simUri, block);
	}
	
	
	//we clear vip/block column when update block/vip
	private static void updateValueForSIM(Context context, Uri contact_uri, boolean block){
		
		ContentValues values = new ContentValues();
		
		if(block){
			values.put(ContactsContract.Contacts.SEND_TO_VOICEMAIL, 1);	
			values.put(VIP_COLUMN, 0);			
		}
		else{
			values.put(ContactsContract.Contacts.SEND_TO_VOICEMAIL, 0);	
			values.put(VIP_COLUMN, 0);				
		}				
			
		context.getContentResolver().update(contact_uri,values,null,null);
	}
	
	//we clear vip/block column when update block/vip
	private static void updateValueSIMRecordTable(Context context, Uri simUri, boolean block){
		if(simUri == null){
			return;
		}
		
		ContentValues values = new ContentValues();
		
		if(block){
			context.getContentResolver().insert(simUri,values);			
		}		
		else{		
			context.getContentResolver().delete(simUri, null, null);			
		}
		
	}	
	
	
    /**
     * addBlackNumber
     * @param context context 
     * @param black_number black number	 
     * @param is_notify need to send broadcast notify or not 	 
	 */	     
	public static void addBlackNumber(Context context, String black_number, boolean is_notify) throws HDKException{
		if(HDKLib0Util.isHDKLib3SupportedInDevice()==HDKLib0Util.NOT_SUPPORT && !isSenseDevice){
			Log.e(TAG,"not support addBlackNumber at none hep device");
			throw new HDKException();
		}
		
		if(context==null || TextUtils.isEmpty(black_number)){
			Log.e(TAG,"incorrect parameter at addBlackNumber");
			return;
		}
		            			    
		ContentValues values = new ContentValues();
		values.put(BLOCKED_NUMBER_NUMBER,black_number);
		try{
			context.getContentResolver().insert(BLOCKED_NUMBER_CONTENT_URI, values);			
		}
		catch(SQLiteConstraintException e){
			//don't insert duplicate number!
			Log.e(TAG,e.toString());
		}
		
		if(is_notify){
			notifyUpdateBlackList(context, black_number);
		}
	}
	
	/**
	 * add block/vip number and check duplicate and notify 
	 * @param context contet
	 * @param number number
	 * @return bundle
	 */
	public static Bundle addBlackNumberAndCheckDuplicate(Context context, String number, boolean is_notify) throws HDKException{
		if(HDKLib0Util.isHDKLib3SupportedInDevice()==HDKLib0Util.NOT_SUPPORT && !isSenseDevice){
			Log.e(TAG,"not support addBlackNumberAndCheckDuplicate at none hep device");
			throw new HDKException();
		}
		
		if(context==null || TextUtils.isEmpty(number)){
			Log.e(TAG,"incorrect parameter at addBlackNumberAndCheckDuplicate");
			return null;
		}
		
    	ContentResolver resolver = context.getContentResolver();

    	Cursor c = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI , 
    			new String[]{ContactsContract.Data.CONTACT_ID, ContactsContract.RawContacts.ACCOUNT_TYPE, ContactsContract.Contacts.DISPLAY_NAME},
    			"PHONE_NUMBERS_EQUAL( " + ContactsContract.CommonDataKinds.Phone.NUMBER + " , '" + number + "')"
    			, null, null);    	
    	Bundle bundle = null;
    	ArrayList<Integer> idList = new ArrayList<Integer>();
    	
    	if(c!=null){
    		while(c.moveToNext()){
    			int id = c.getInt(0);        				
    			if(id>0){
    				idList.add(id);
    				String accountType = c.getString(1);        		
    				if(accountType.equals(ACCOUNT_TYPE_SIM)  ||
    				   accountType.equals(ACCOUNT_TYPE_SIM_NEW) 
    				   ){    					
    					BlacklistUtils.modifySendToVoiceMailForSim(context, ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id), true);    					
    				}
    				else{    					
    					BlacklistUtils.modifySendToVoiceMail(context, ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id), true);    					
    				}
    				if(bundle==null){
    					//only return last one information
    					bundle = new Bundle();
    					bundle.putString("name", c.getString(2));
    					bundle.putString("number", number);
    				}
    			}    			
    		}
    		c.close();   		

    	}

    	if(idList.size()>0){  
    		if(is_notify){
    			notifyUpdateBlacklist(context, idList, null);    
    		}
		}else{ 			
			addBlackNumber(context, number, is_notify);			
    	}
    	return bundle;
    }	
	
    
    /**
     * remove black number directly
     * @param context context
     * @param number number
     * @param is_notify need to send broadcast notify or not 	 
     */
    public static void removeBlackNumber(Context context, String number, boolean is_notify) throws HDKException{
		if(HDKLib0Util.isHDKLib3SupportedInDevice()==HDKLib0Util.NOT_SUPPORT && !isSenseDevice){
			Log.e(TAG,"not support removeBlackNumber at none hep device");
			throw new HDKException();
		}
    	
    	if(context==null || TextUtils.isEmpty(number)){
			Log.e(TAG,"incorrect parameter at removeBlackNumber");
			return;
		}
    	
		context.getContentResolver().delete(BLOCKED_NUMBER_CONTENT_URI,
				"PHONE_NUMBERS_EQUAL(" + BLOCKED_NUMBER_NUMBER + ", '" + number + "') ", null);
		
		if(is_notify){
			notifyUpdateBlackList(context, number);		
		}

	}
	
	
    /**
     * notify mms blacklist has updated
     * @param context context 
     * @param id id 
     */
    public static void notifyUpdateBlackList(Context context, int id) throws HDKException{
		if(HDKLib0Util.isHDKLib3SupportedInDevice()==HDKLib0Util.NOT_SUPPORT && !isSenseDevice){
			Log.e(TAG,"not support notifyUpdateBlackList at none hep device");
			throw new HDKException();
		}
    	
    	if(/*BuildUtils.isMMSBlockedEnabled()*/true && context!=null && id>0){
    		ArrayList<Integer> idList = new ArrayList<Integer>();
    		idList.add(id);
    		notifyUpdateBlacklist(context, idList, null);
    	}
    }
    
   /**
    * notify mms blacklist has updated
    * @param context context 
    * @param idList id list
    * @param numberList number list
    */
    public static void notifyUpdateBlacklist(Context context,ArrayList<Integer> idList ,ArrayList<String> numberList) throws HDKException{
		if(HDKLib0Util.isHDKLib3SupportedInDevice()==HDKLib0Util.NOT_SUPPORT && !isSenseDevice){
			Log.e(TAG,"not support notifyUpdateBlackList at none hep device");
			throw new HDKException();
		}
    	
    	if(/*BuildUtils.isMMSBlockedEnabled()*/true && context!=null){    		
    		Intent intent = new Intent(BLOCK_UPDATE_INTENT);    		
    		intent.putIntegerArrayListExtra(BLOCK_UPDATE_ID_LIST, idList);    		
    		intent.putStringArrayListExtra(BLOCK_UPDATE_NUMBER_List, numberList);    		
    		if(isAboveSense7){    			
    			context.sendBroadcast(intent, "com.htc.permission.APP_SHARED");
    		}else{
    			context.sendBroadcast(intent);
    		}
    	}
    }

   /**
    * notify mms blacklist has updated
    * @param context context
    * @param number number
    */
    public static void notifyUpdateBlackList(Context context,String number) throws HDKException{
		if(HDKLib0Util.isHDKLib3SupportedInDevice()==HDKLib0Util.NOT_SUPPORT && !isSenseDevice){
			Log.e(TAG,"not support notifyUpdateBlackList at none hep device");
			throw new HDKException();
		}
    	
    	if(/*BuildUtils.isMMSBlockedEnabled()*/true && context!=null && !TextUtils.isEmpty(number)){
    		ArrayList<String> numList = new ArrayList<String>();
    		numList.add(number);
    		notifyUpdateBlacklist(context, null, numList);
    	}
    }  
    
    
    /** HTCDATA format is as below
     *  <HTCDATA>
     *   <BlockCall/><VIPCall/>
     *   <Facebook>id:xxx/friendof:xxx|id:xxx/friend:xxx</Facebook>
     *   <Flickr>id:xxx/friendof:xxx</Flickr>
     *   <Favorite>actionid:xxx</Favorite>
     *  <HTCDATA>
     *  always append BlockCall/VIPCall at first position, vip > block
     */
    public static class BlockCallNote{
    	/**
    	 * BLOCK_CALL_TOKEN
    	 */
    	public final static String BLOCK_CALL_TOKEN = "<BlockCall/>";    	
    	/**
    	 * VIP_CALL_TOKEN
    	 */
    	public final static String VIP_CALL_TOKEN = "<VIPCall/>";
    	/**
    	 * HTC_DATA_STARTING_TOKEN
    	 */    	
    	private final static String HTC_DATA_STARTING_TOKEN = "<HTCData>";
    	/**
    	 * HTC_DATA_ENDING_TOKEN
    	 */    	
    	private final static String HTC_DATA_ENDING_TOKEN   = "</HTCData>";
		
		/**
		 * addBlockNote
		 * @param text text
		 * @return string
		 */
		public static String addBlockNote(String text) throws HDKException{
			if(HDKLib0Util.isHDKLib3SupportedInDevice()==HDKLib0Util.NOT_SUPPORT && !isSenseDevice){
				Log.e(TAG,"not support addBlockNote at none hep device");
				throw new HDKException();
			}
			
			StringBuilder builder = new StringBuilder();
			
			if(TextUtils.isEmpty(text)) {
				//build new one
				appendBlockCall(true,builder);
			} else {
				//parse note
				//transfer to new format first if it is old format
				//TODO: ignore transfer on hms update here
				//text = SNLinkUtils.transferToNewFormat(text);
				
				int startingIndex = text.indexOf(HTC_DATA_STARTING_TOKEN);
				int endingIndex = text.lastIndexOf(HTC_DATA_ENDING_TOKEN);
				if(endingIndex < startingIndex || startingIndex == -1 || endingIndex == -1) {
					//build new one
					//the ori text need to add to note back when build new one
					builder.append(extractNoteText(text));
					appendBlockCall(true,builder);
				} else {
					//add block call if not existed
					String subText = text.substring(startingIndex + HTC_DATA_STARTING_TOKEN.length(), endingIndex);			

					if(TextUtils.isEmpty(subText)) {
						//build new one
						//the ori text need to add to note back when build new one
						builder.append(extractNoteText(text));
						appendBlockCall(true,builder);
					} else {
						int index_block = subText.indexOf(BLOCK_CALL_TOKEN);		
						text = text.replace(VIP_CALL_TOKEN, ""); //we remove vip token first
						if(index_block == -1) {
							//not found, so insert at first position
							builder.append(extractNoteText(text));
							builder.append(HTC_DATA_STARTING_TOKEN);  //add <HTCData>							
							appendBlockCall(false,builder);								
							builder.append(text.substring(startingIndex + HTC_DATA_STARTING_TOKEN.length())); 						
						}else{
							//already have block so return original one text
							builder.append(text);
						}						
					}
				}
			}
			
			if(DEBUG) Log.d(TAG,"addBlockCall: " + builder.toString());
			return builder.toString();
		}
		
		/**
		 * addVIPNote
		 * @param text text
		 * @return string
		 */
		public static String addVIPNote(String text) throws HDKException{
			if(HDKLib0Util.isHDKLib3SupportedInDevice()==HDKLib0Util.NOT_SUPPORT && !isSenseDevice){
				Log.e(TAG,"not support addVIPNote at none hep device");
				throw new HDKException();
			}
			
			StringBuilder builder = new StringBuilder();
			
			if(TextUtils.isEmpty(text)) {
				//build new one
				appendVIPCall(true,builder);
			} else {
				//parse note
				//transfer to new format first if it is old format
				//TODO: ignore transfer on hms update here
				//text = SNLinkUtils.transferToNewFormat(text);
				
				int startingIndex = text.indexOf(HTC_DATA_STARTING_TOKEN);
				int endingIndex = text.lastIndexOf(HTC_DATA_ENDING_TOKEN);
				if(endingIndex < startingIndex || startingIndex == -1 || endingIndex == -1) {
					//build new one
					//the ori text need to add to note back when build new one
					builder.append(extractNoteText(text));
					appendVIPCall(true,builder);
				} else {
					//add block call if not existed
					String subText = text.substring(startingIndex + HTC_DATA_STARTING_TOKEN.length(), endingIndex);			

					if(TextUtils.isEmpty(subText)) {
						//build new one
						//the ori text need to add to note back when build new one
						builder.append(extractNoteText(text));
						appendVIPCall(true,builder);
					} else {
						int index_vip = subText.indexOf(VIP_CALL_TOKEN);	
						text = text.replace(BLOCK_CALL_TOKEN, ""); //we remove block token first
						if(index_vip == -1) {
							//not found, so insert at first position
							builder.append(extractNoteText(text));
							builder.append(HTC_DATA_STARTING_TOKEN);  //add <HTCData>							
							appendVIPCall(false,builder);							
							builder.append(text.substring(startingIndex + HTC_DATA_STARTING_TOKEN.length()));  							
						}else{
							//already have block so return original one text
							builder.append(text);
						}						
					}
				}
			}
			
			if(DEBUG) Log.d(TAG,"addVIPCall: " + builder.toString());
			return builder.toString();
		}
		
		/**
		 * removeBlockNote
		 * @param text text
		 * @return string
		 */
		public static String removeBlockNote(String text) throws HDKException{
			if(HDKLib0Util.isHDKLib3SupportedInDevice()==HDKLib0Util.NOT_SUPPORT && !isSenseDevice){
				Log.e(TAG,"not support removeBlockNote at none hep device");
				throw new HDKException();
			}
			
			if(TextUtils.isEmpty(text)){
				return text;
			}
		
			int startingIndex = text.indexOf(HTC_DATA_STARTING_TOKEN);
			int endingIndex = text.lastIndexOf(HTC_DATA_ENDING_TOKEN);
			if(endingIndex < startingIndex || startingIndex == -1 || endingIndex == -1) {
				return text;
			} else {
				//replace block token to empty string
				String subText = text.substring(startingIndex + HTC_DATA_STARTING_TOKEN.length(), endingIndex);			
				if(!TextUtils.isEmpty(subText)) {
					if(subText.trim().equals(BLOCK_CALL_TOKEN)){
						//remove whole word due to htc data only have block call tag
						return extractNoteText(text);					
					}else if(subText.indexOf(BLOCK_CALL_TOKEN)!=-1){
						//remove block call tag from content
						return text.replace(BLOCK_CALL_TOKEN, "");		
					}						
				}
			}
			
			return text;
		}
		
		/**
		 * removeVIPNote
		 * @param text text
		 * @return string
		 */
		public static String removeVIPNote(String text) throws HDKException{
			if(HDKLib0Util.isHDKLib3SupportedInDevice()==HDKLib0Util.NOT_SUPPORT && !isSenseDevice){
				Log.e(TAG,"not support removeVIPNote at none hep device");
				throw new HDKException();
			}
			
			if(TextUtils.isEmpty(text)){
				return text;
			}
		
			int startingIndex = text.indexOf(HTC_DATA_STARTING_TOKEN);
			int endingIndex = text.lastIndexOf(HTC_DATA_ENDING_TOKEN);
			if(endingIndex < startingIndex || startingIndex == -1 || endingIndex == -1) {
				return text;
			} else {
				//replace VIP token to empty string
				String subText = text.substring(startingIndex + HTC_DATA_STARTING_TOKEN.length(), endingIndex);			
				if(!TextUtils.isEmpty(subText)) {
					if(subText.trim().equals(VIP_CALL_TOKEN)){
						//remove whole word due to htc data only have VIP call tag
						return extractNoteText(text);					
					}else if(subText.indexOf(VIP_CALL_TOKEN)!=-1){
						//remove VIP call tag from content
						return text.replace(VIP_CALL_TOKEN, "");		
					}								
				}
			}
			
			return text;
		}	
		
		private static void appendBlockCall(boolean addHeader, StringBuilder text){
			if(addHeader){
				text.append(HTC_DATA_STARTING_TOKEN);
			}
			text.append(BLOCK_CALL_TOKEN);			
			if(addHeader){
				text.append(HTC_DATA_ENDING_TOKEN);
			}
		}
		
		private static void appendVIPCall(boolean addHeader, StringBuilder text){
			if(addHeader){
				text.append(HTC_DATA_STARTING_TOKEN);
			}
			text.append(VIP_CALL_TOKEN);			
			if(addHeader){
				text.append(HTC_DATA_ENDING_TOKEN);
			}
		}

		/**
		 *  extract note text from new format HTCDATA
		 *  @param text text
		 *  @return string   
		 */
		private static String extractNoteText(final String text) {
			if(TextUtils.isEmpty(text)) {
				return text;
			}
			int startingIndex = text.indexOf(HTC_DATA_STARTING_TOKEN);
			int endingIndex = text.lastIndexOf(HTC_DATA_ENDING_TOKEN);
			if(endingIndex < startingIndex || startingIndex == -1 || endingIndex == -1) {
				return text;
			}
			String subTextFirstHalf = text.substring(0, startingIndex);
			String subTextSecondHalf = text.substring(endingIndex + HTC_DATA_ENDING_TOKEN.length());
			return subTextFirstHalf + subTextSecondHalf;
		}	


		/**
		 * update BLOCK_CALL_TOKEN/VIP_CALL_TOKEN to note if contact contains Phone/Google/Exchange Source
		 * @param context context 
		 * @param contact_id contact id
		 * @param block_type block/vip or not 
		 * @param operationList operation list		 
		 */
		public static void updateNoteForSyncContacts(Context context, long contact_id, MANAGE_CONTACT_TYPE block_type, ArrayList<ContentProviderOperation> operationList ) throws HDKException{
			if(HDKLib0Util.isHDKLib3SupportedInDevice()==HDKLib0Util.NOT_SUPPORT && !isSenseDevice){
				Log.e(TAG,"not support updateNoteForSyncContacts at none hep device");
				throw new HDKException();
			}
	    	 
	    	 HashSet<Integer> rawList = new HashSet<Integer>();
	    	 if(operationList==null){
	    		 operationList = new ArrayList<ContentProviderOperation>();
	    	 }
	    	 ContentProviderOperation.Builder operation = null;
	    	 
	    	 StringBuilder builder = new StringBuilder();
	    	 if(context!=null && contact_id > 0 && block_type!=MANAGE_CONTACT_TYPE.UNDO_STATE){
	    		 //get the rawList first
	    		 Cursor c = context.getContentResolver().query(RawContacts.CONTENT_URI, new String[]{RawContacts._ID}, 
	    			RawContacts.CONTACT_ID + " =? " + " AND " +
	    			"(" + RawContacts.ACCOUNT_TYPE + " = '" + PeopleConstants.ACCOUNT_TYPE_PC + "'" + " OR "
	    			+ RawContacts.ACCOUNT_TYPE + " = '" + PeopleConstants.ACCOUNT_TYPE_GOOGLE + "'" + " OR " 
	    			+ RawContacts.ACCOUNT_TYPE + " = '" + PeopleConstants.ACCOUNT_TYPE_HTC_EXCHANGE + "'" + " OR " 
				+ RawContacts.ACCOUNT_TYPE + " = 'com.google.android.exchange'" + " OR " 
				+ RawContacts.ACCOUNT_TYPE + " = 'com.google.android.gm.exchange'" + " OR " 
	    			+ RawContacts.ACCOUNT_TYPE + " = 'com.android.exchange'" + ")" + " AND "
	    			+ RawContacts.RAW_CONTACT_IS_READ_ONLY + " = 0 " , new String[]{Long.toString(contact_id)}, null);	    		 
	    		 if(c!=null){
	    			 builder.append(Data.RAW_CONTACT_ID);
	    			 builder.append(" IN (");    			 
	    			 while(c.moveToNext()){  
	    				 int id = c.getInt(0);
	    				 if(id>0){	    					 
	    					 rawList.add(id);
	    					 builder.append(id);
	    					 builder.append(",");
	    				 }
	    			 }
	    			 builder.setLength(builder.length()-1);
	    			 builder.append(")");
	    			 c.close();
	    		 }
	    		 
	    		 if(rawList.size()>0){
	    			 builder.append(" AND ");


	    			 //get the raw that have note data and update
	    			 c = context.getContentResolver().query(Data.CONTENT_URI, new String[]{Data.RAW_CONTACT_ID, Data._ID, CommonDataKinds.Note.NOTE}, 
	    					 builder.toString() + Data.MIMETYPE + " = '" + CommonDataKinds.Note.CONTENT_ITEM_TYPE + "'" ,
	    					 null, null);
	    			 if(c!=null){
	    				 while(c.moveToNext()){
	    					 int raw_id = c.getInt(0);
	    					 rawList.remove(raw_id);
	    					 String note = c.getString(2);
	    					 String newNote = null;
	    					 /*if(block){
	    						 newNote = addBlockNote(note);
	    					 }else{
	    						 newNote = removeBlockNote(note);
	    					 }*/
	    					 switch (block_type){
	    						 case BLOCK_ONLY:{
	    							 newNote = addBlockNote(note);
	    							 break;
	    						 }
	    						 case VIP_ONLY:{
	    							 newNote = addVIPNote(note);
	    							 break;
	    						 }
	    						 case CLEAR_STATE:{	    							
	    							 String temp = removeBlockNote(note);
	    							 newNote = removeVIPNote(temp);
	    							 break;
	    						 }	    						 		 
	    					 }

	    					 if(TextUtils.isEmpty(newNote)){
							if(!TextUtils.isEmpty(note)){
								//clear data instead of remove whole data
								operation = ContentProviderOperation.newUpdate(Data.CONTENT_URI);
	    						 operation.withValue(CommonDataKinds.Note.NOTE, "");
	    						 operation.withSelection(Data._ID + "=?", new String[]{c.getString(1)});
	    						 operationList.add(operation.build());
							}
	    						 //operation = ContentProviderOperation.newDelete(Data.CONTENT_URI);
	    						 //operation.withSelection(Data._ID + "=?", new String[]{c.getString(1)});
	    						 //operationList.add(operation.build());
	    					 }else if(!newNote.equals(note)){
	    						 operation = ContentProviderOperation.newUpdate(Data.CONTENT_URI);
	    						 operation.withValue(CommonDataKinds.Note.NOTE, newNote);
	    						 operation.withSelection(Data._ID + "=?", new String[]{c.getString(1)});
	    						 operationList.add(operation.build());
	    					 }
	    				 }
	    				 c.close();
	    			 }

	    			 //insert note to the last rawList without note data
	    			 String content = null;
	    			 if(block_type==MANAGE_CONTACT_TYPE.BLOCK_ONLY){
	    				 content = addBlockNote(null);
	    			 }else if(block_type==MANAGE_CONTACT_TYPE.VIP_ONLY){
	    				 content = addVIPNote(null);
	    			 }
	    			 if(!TextUtils.isEmpty(content)){
	    				 for(int rid:rawList){	    			 				 
	    					 operation = ContentProviderOperation.newInsert(Data.CONTENT_URI);	
	    					 operation.withValue(Data.RAW_CONTACT_ID, rid);
	    					 operation.withValue(Data.MIMETYPE, CommonDataKinds.Note.CONTENT_ITEM_TYPE);
	    					 operation.withValue(CommonDataKinds.Note.NOTE, content);	    				 
	    					 operationList.add(operation.build());
	    				 }

	    			 }
	    		 }

	    	 }
	    	 
	    	 try {
    			 if(context!=null && operationList.size() > 0) {
    				 context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, operationList);
    				 operationList.clear();
    			 }
    		 }catch (RemoteException e) {
    			 Log.e(TAG, String.format("%s: %s", e.toString(), e.getMessage()));
    			 return;
    		 } catch (OperationApplicationException e) {
    			 Log.e(TAG, String.format("%s: %s", e.toString(), e.getMessage()));
    			 return;
    		 }
	    }
	
	  
    }   
    
    private static float getSenseVersion() {
        HtcWrapCustomizationManager manager = new HtcWrapCustomizationManager();
        HtcWrapCustomizationReader reader = null;
        if (manager != null) {
            reader = manager.getCustomizationReader("system",
                    HtcWrapCustomizationManager.READER_TYPE_XML, false);
        }

        float senseVersion = 0.0f;
        try {
            if (reader != null) {
                final String readSenseVersion = reader.readString("sense_version", "0.0");
                senseVersion = readSenseVersion != null ? Float.parseFloat(readSenseVersion)
                                                        : 0.0f;
            }
        } catch (Exception e) {
            if (HtcWrapHtcDebugFlag.Htc_DEBUG_flag) {
                Log.w(TAG, "Sense version parse failed.", e);
            }
        }
        return senseVersion;
    }
  
	
}
