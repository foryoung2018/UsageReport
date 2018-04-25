package com.htc.lib1.phonecontacts.HEPOnly;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
import com.htc.lib1.phonecontacts.R; 
import com.htc.lib1.phonecontacts.HEPOnly.PeopleConstants;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Entity;
import android.content.Entity.NamedContentValues;
import android.content.EntityIterator;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.Groups;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList; 
import java.util.HashMap;

public class GroupUtils {
	private static final boolean DEBUG = HtcWrapHtcDebugFlag.Htc_DEBUG_flag;

	private static final String ACTION_GROUP_MODIFIACTION = "anddroid.intent.action.GROUP_MODIFICATION";	
		
        
    /**
     * SYSTEM_GROUP_TITLE_MYCONTACTS
     */
    private static final String SYSTEM_GROUP_TITLE_MYCONTACTS = "My Contacts";

    
    
    
    
    /**
     * add a contact to group 
     * @param context   context obj
     * @param title     which group to add 
     * @param contactsList  the contact id to be added to this group 
     * @return return false if title is empty
     */
    public static boolean addContactsToGroup(Context context, String title,
            ArrayList<Long> contactsList) {
    	
    	
        if (TextUtils.isEmpty(title)) {
            return false;
        }
        String targetName = title;
        ArrayList<Long> toAddList = new ArrayList<Long>();
        ArrayList<Long> toRemoveList = new ArrayList<Long>();
        toAddList.addAll(contactsList);
        GroupEntity.updateGroup(context, toAddList, toRemoveList, targetName,
                targetName);
        return true;
    }
    
    /**
     * adding /removing contacts to group , or rename the group name.  
     * @param context   context obj
     * @param toAddList  the contact id to be added to this group
     * @param toRemoveList  the contact id to be removed from this group
     * @param targetName     which group to add 
     * @param newName     new group name  
     */
    public static void updateGroup(Context context, ArrayList<Long>toAddList, ArrayList<Long> toRemoveList, String targetName, String newName){
    	GroupEntity.updateGroup(context, toAddList, toRemoveList, targetName, newName);
    }
    
    private static class GroupEntity {    	
           private static Uri CONTENT_FAVORITE_URI = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, "favorite");          
           
           final static String TAG ="GroupEntity";
           final static String[] sContactProjection=new String[]{
               Contacts._ID,
               Contacts.DISPLAY_NAME,
           };
           
           final static String[] sGroupProjection = new String[]{
                   Groups._ID,
                   Groups.TITLE,
                   Groups.ACCOUNT_NAME,
                   Groups.ACCOUNT_TYPE,
                   Groups.DELETED,
                   Groups.SOURCE_ID,
                   //add by Jerry:Ics field
                   Groups.GROUP_IS_READ_ONLY
               };
           
           final static String[] sRawContactProjection = new String[]{
                   RawContacts._ID,
                   RawContacts.ACCOUNT_NAME,
                   RawContacts.ACCOUNT_TYPE,
                   RawContacts.CONTACT_ID,
                   RawContacts.DELETED,
                   RawContacts.DATA_SET
               };

           
           private Context mContext;
           private int mGroupIsReadOnly=0;
           private String mTitle;
           private ArrayList<AccountData> mAccountList = new ArrayList<AccountData>();
           private ArrayList<GroupDelta> mGroupsList = new ArrayList<GroupDelta>();
           private ArrayList<Long> mContactList = new ArrayList<Long>();
           private ArrayList<Long> mAfterContactList = new ArrayList<Long>();
    	   private  static void updateGroup(Context context, ArrayList<Long>toAddList, ArrayList<Long> toRemoveList, String targetName, String newName){
    	        if(targetName!= null && targetName.equals(PeopleConstants.DEFAULT_GROUP_FAVORITE)){
    	            updateFavorite(context, toAddList , toRemoveList);
    	            return;
    	        }
    	        ContentResolver resolver = context.getContentResolver();
    	        if(DEBUG) Log.v(TAG, "updateGroup E:  "+targetName);
    	        GroupEntity entity = GroupEntity.fromQuery(resolver, targetName);
    	        if(entity == null){
    	            return;
    	        }
    	        entity.mContext = context;
    	        if(DEBUG) Log.v(TAG,"toAddList: "+toAddList);
    	        if(DEBUG) Log.v(TAG,"toRemoveList: "+toRemoveList);

    	        entity.mAfterContactList.addAll(toAddList);
    	        entity.mAfterContactList.removeAll(toRemoveList);

    	        entity.updateGroup(resolver, newName);
    	        if(DEBUG) Log.v(TAG, "updateGroup X");
    	    }
    	   
    	    private static void updateFavorite(Context context, ArrayList<Long> toBeAddedId ,ArrayList<Long> toBeRemovedId){
    	        if(DEBUG) Log.v(TAG,"doUpdateFavorite E");
    	        ArrayList<Long> addList = new ArrayList<Long>();
    	        for(Long id : toBeAddedId){
    	            addList.add(id);
    	        }

    	        ArrayList<Long> removeList = new ArrayList<Long>();
    	        for(Long id : toBeRemovedId){
    	            removeList.add(id);
    	        }

    	        if(DEBUG) {
    	            Log.v(TAG,"mToBeAddedId:  "+addList);
    	            Log.v(TAG,"mToBeRemovedId:  "+removeList);
    	            }

    	        ContentResolver resolver = context.getContentResolver();    	       
    	        Uri updateUri = CONTENT_FAVORITE_URI;


    	        String where = getInWhere(Contacts._ID , addList);
    	        ContentValues values = new ContentValues(1);
    	        values.put(Contacts.STARRED, 1);
    	        resolver.update(updateUri, values, where, null);

    	        values.clear();
    	        values.put(Contacts.STARRED, 0);
    	        where = getInWhere(Contacts._ID , removeList);
    	        resolver.update(updateUri, values, where, null);


    	        values.clear();
    	        

    	         if(addList.size() == 0 && removeList.size() > 0){
    	                ArrayList<Integer> deleteContacts = new ArrayList<Integer>();
    	                for(Long data : removeList){
    	                    int deletedPerson = (int)(data.longValue());
    	                    deleteContacts.add(deletedPerson);
    	                }
    	                broadcastFavoriteChangeDeletion(context, PeopleConstants.DEFAULT_GROUP_FAVORITE, deleteContacts,1);
    	          }
    	         else{
    	             Intent intent = new Intent(PeopleConstants.ACTION_FAVORITE_CHANGE);
    	             context.sendBroadcast(intent);
    	         }
    	    }
    	    
    	    
    	    
    	    private void updateGroup(ContentResolver resolver, String newTitle){
    	        Uri updateGroupUri = Uri.parse("content://com.android.contacts/group_with_favorite");
    	        buildDiff(resolver);
    	        ContentValues values = new ContentValues();
    	         String selection = getWhere(mTitle);
    	        if(newTitle !=null && !newTitle.equals(mTitle)){
    	            values.put(Groups.TITLE, newTitle);
    	        }
    	        if(values.size() > 0){
    	            resolver.update(updateGroupUri , values, selection, null);
    	        }
    	    }

    	    private static String getWhere(String  name){
    			//Modify by Jerry:ICS PORTING.System Group title should compare group is read only
    			return " ( "+  Groups.TITLE + "="+DatabaseUtils.sqlEscapeString(name)+")";
    	    }

    	    private static GroupEntity fromQuery(ContentResolver resolver, String targetName){
    	        Uri uri = Groups.CONTENT_URI;
    	        String pattern = android.database.DatabaseUtils.sqlEscapeString(targetName);
    	        String selection = Groups.TITLE + "=" +pattern + "  AND "+Groups.DELETED +" = 0";
    	        EntityIterator iterator = null;
    	        ArrayList<GroupDelta> groupsList = new ArrayList<GroupDelta>();

    	        GroupEntity groupEntity = new GroupEntity();
    	        Uri queryUri = Uri.parse("content://com.android.contacts/groups_raw");

    	            // =FROYO= remove
    	        Cursor cursor = resolver.query(queryUri, null, selection, null, null);
    	        if(cursor!=null){
    	            int idIdx = cursor.getColumnIndex(Groups._ID);
    	            int accountNameIdx = cursor.getColumnIndex(Groups.ACCOUNT_NAME);
    	            int accountTypeIdx = cursor.getColumnIndex(Groups.ACCOUNT_TYPE);
    	            int titleIdx = cursor.getColumnIndex(Groups.TITLE);
    	            int sourceIdIdx = cursor.getColumnIndex(Groups.SOURCE_ID);
    	            int SystemIdIdx = cursor.getColumnIndex(Groups.SYSTEM_ID);
    	            int notesIdx = cursor.getColumnIndex(Groups.NOTES);
    	            int deletedIdx = cursor.getColumnIndex(Groups.DELETED);
    	            int photoIdx = cursor.getColumnIndex("photo");


    	            try{
    	                 while(cursor.moveToNext()){

    	                     GroupDelta delta = new GroupDelta();
    	                     delta.mAccountName = cursor.getString(accountNameIdx);
    	                     delta.mAccountType = cursor.getString(accountTypeIdx);
    	                     delta.mSourceId = cursor.getString(sourceIdIdx);
    	                     delta.mSystemId = cursor.getString(SystemIdIdx);
   	                         delta.mNotes = cursor.getString(notesIdx);
    	                     delta.mId = cursor.getLong(idIdx);
    	                     delta.mIsDeleted = cursor.getInt(deletedIdx);
    	                     byte[] bytes = cursor.getBlob(photoIdx);

    	                     if(bytes!=null && bytes.length > 0){
    	                         delta.mBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    	                     }

    	                     groupsList.add(delta);
    	                     AccountData data = new AccountData(delta.mAccountName,delta.mAccountType );
    	                     if(!groupEntity.mAccountList.contains(data)){
    	                         groupEntity.mAccountList.add(data);
    	                     }
    	                 }
    	            }finally{
    	                cursor.close();
    	            }
    	        }

    	        if(groupsList.size() == 0){
    	            return null;
    	        }

    	        groupEntity.mGroupsList = groupsList;
    	        groupEntity.mTitle = targetName;

    	        Cursor c = queryContacts(resolver, groupEntity.mTitle);
    	        if(c!=null){
    	            if(c.moveToFirst()){
    	                do{
    	                long id = c.getLong(0);
    	                groupEntity.mContactList.add(id);
    	                }while(c.moveToNext());
    	            }
    	            c.close();
    	        }

    	        groupEntity.mAfterContactList.addAll(groupEntity.mContactList);
    	        return groupEntity;
    	    }
    	    private static Cursor queryContacts(ContentResolver resolver, String name){
    	        Uri attachuri =Contacts.CONTENT_GROUP_URI;
    	        Uri.Builder builder = attachuri.buildUpon();
    	        builder.appendPath(name);
    	        attachuri = builder.build();        
    	        Cursor c = resolver.query(attachuri, sContactProjection, null, null, null);
    	        return c;
    	    }

    	    
    	    private static String getInWhere(String columnName , ArrayList<Long>  idList){
    	        String result = null;
    	        if(idList == null || idList.size() == 0){
    	            result = "0";
    	        }
    	        else{
    	            StringBuffer buffer = new StringBuffer();
    	            int size = idList.size();
    	            for(int i = 0; i < size ; i++){
    	                long id = idList.get(i);
    	                buffer.append(id);
    	                if( i < (size -1)){
    	                    buffer.append(" ,");
    	                }
    	            }
    	            buffer.insert(0, columnName+ "    IN (");
    	            buffer.append(" ) ");
    	            result = buffer.toString();
    	        }
    	        return result;
    	    }
    	    
    	    private static final void broadcastFavoriteChangeDeletion(Context context,
    	               String title, ArrayList<Integer> deleteList,int groupIsReadOnly) {
    	           if (context != null) {
    	               Intent intent = new Intent(PeopleConstants.ACTION_FAVORITE_CHANGE);
    	               intent.putExtra(Groups.TITLE, title);
    	               intent.putExtra("contact_id_list", deleteList);
    	               intent.putExtra("delete", true);

    	               context.sendBroadcast(intent);
    	           }
    	    }

    	    private void buildDiff(ContentResolver resolver) {
    	    	// process by afterContact and then know what should be added
    	    	ArrayList<AccountData> outAccountList = new ArrayList<AccountData>();
    	    	processMember(mContext, resolver, mAfterContactList, null,
    	    			outAccountList);

    	    	// added
    	    	ArrayList<AccountData> addGroupList = new ArrayList<AccountData>();
    	    	for (AccountData data : outAccountList) {
    	    		if (DEBUG)
    	    			Log.v(TAG,
							"mAccountList.contains(data)?  "
									+ mAccountList.contains(data) + "size:  "
									+ mAccountList.size());
    	    		if (!mAccountList.contains(data)) {
    	    			addGroupList.add(data);
    	    		}
    	    	}

    	    	// removed
    	    	ArrayList<Long> removedList = new ArrayList<Long>();
    	    	for (long id : mContactList) {
    	    		if (!mAfterContactList.contains(id)) {
    	    			removedList.add(id);
    	    		}
    	    	}
    	    	ArrayList<RawContactData> removedMemberList = new ArrayList<RawContactData>();
    	    	processMember(mContext, resolver, removedList, removedMemberList,
    	    			null);

    	    	
    	    	// added
    	    	ArrayList<Long> addList = new ArrayList<Long>();
    	    	for (long id : mAfterContactList) {
    	    		if (!mContactList.contains(id)) {
    	    			addList.add(id);
    	    		}
    	    	}
    	    	ArrayList<RawContactData> addedMemberList = new ArrayList<RawContactData>();
    	    	processMember(mContext, resolver, addList, addedMemberList, null);

    	    	// add group
    	    	boolean bNewGroups = createNewGroup(resolver, addGroupList);
    	    	// add member
    	    	addGroupMember(resolver, addedMemberList);
    	    	// remove member
    	    	removeGroupMember(resolver, removedMemberList);


    	    	if (addedMemberList.size() == 0 && removedMemberList.size() > 0) {
    	    		ArrayList<Integer> deleteContacts = new ArrayList<Integer>();
    	    		for (RawContactData data : removedMemberList) {
    	    			int deletedPerson = (int) data._Id;
    	    			deleteContacts.add(deletedPerson);
    	    		}
    	    		broadcastFavoriteChangeDeletion(this.mContext, this.mTitle,
    	    				deleteContacts, this.mGroupIsReadOnly);
    	    	} else if (addedMemberList.size() > 0) {
    	    		broadcastFavoriteChangeIntent(this.mContext, this.mTitle);
    	    	}

    	    	if (bNewGroups) {
    	    		broadcastGroupModifiaction(mContext, mTitle,
    	    				mGroupIsReadOnly);
    	    	}    	    	
		   }
    	    
    	    private final static int RAW_CONTACT_IDIdx = 0;
    	    private final static int RAW_CONTACT_AccountNameIdx = 1;
    	    private final static int RAW_CONTACT_AccountTypeIdx = 2;
    	    private final static int RAW_CONTACT_CONTACTIdx = 3;
    	    private final static int RAW_CONTACT_DELETEDIdx = 4;
    	    private final static int RAW_CONTACT_DATA_SETIdx = 5;
    	        	        	        	          
    	    private static void processMember(Context context, ContentResolver resolver, ArrayList<Long>inContactList
    	            , ArrayList<RawContactData> outRawContactId, ArrayList<AccountData> outAccountList){

    	        String selection = RawContacts.CONTACT_ID + " IN (  ";
    	        StringBuffer buffer = new StringBuffer(selection);

    	        int size = inContactList.size();
    	        if(inContactList == null || size == 0){
    	            return;
    	        }

    	        for(int i = 0; i<size;i++){
    	            long cid = inContactList.get(i);
    	            buffer.append(cid);

    	            if(i < size-1){
    	                buffer.append(", ");
    	            }
    	        }
    	        buffer.append(" )  ");
    	        selection = buffer.toString();

    	        Cursor cursor = resolver.query(RawContacts.CONTENT_URI, sRawContactProjection, selection,  null,null);
    	        ArrayList<AccountData> list = new ArrayList<AccountData>();
    	        ArrayList<RawContactData> rawContactlist = new ArrayList<RawContactData>();

    	        if(cursor != null){
    	            while(cursor.moveToNext()){
    	                long rawId = cursor.getLong(0);
    	                String account_name = cursor.getString(RAW_CONTACT_AccountNameIdx);
    	                String account_type = cursor.getString(RAW_CONTACT_AccountTypeIdx);
    	                
    	                String dataSet = cursor.getString(RAW_CONTACT_DATA_SETIdx);
    	                //long contactId = cursor.getLong(RAW_CONTACT_CONTACTIdx);
    	                int idDelete = cursor.getInt(RAW_CONTACT_DELETEDIdx);

    	                if(idDelete > 0){
    	                    continue;
    	                }

    	                if(isGroupMemeberReadOnlyAccountType(context, account_type, dataSet)){
    	                    continue;
    	                }

    	                if(!rawContactlist.contains(rawId)){
    	                    RawContactData rawdata = new RawContactData(account_name,  account_type, dataSet, rawId);
    	                    rawContactlist.add(rawdata);
    	                }
    	                AccountData data = new AccountData(account_name, account_type);
    	                if(!list.contains(data)){
    	                    list.add(data);
    	                }
    	            }
    	            cursor.close();
    	        }

    	        if(outRawContactId!=null){
    	        outRawContactId.addAll(rawContactlist);
    	        }
    	        if(outAccountList!=null){
    	        outAccountList.addAll(list);
    	        }
    	    }
    	    
    	    private static void broadcastFavoriteChangeIntent(Context context, String groupName){
    	        Intent intent = new Intent(PeopleConstants.ACTION_FAVORITE_CHANGE);
    	        intent.putExtra(Groups.TITLE, groupName);
    	        context.sendBroadcast(intent);
    	    }
    	    
    	    private static final void broadcastGroupModifiaction(Context context){
    	        if(context!=null){
    	            Intent intent = new Intent(ACTION_GROUP_MODIFIACTION);
    	            context.sendBroadcast(intent);
    	        }
    	    }
    	    
    	    private static final void broadcastGroupModifiaction(Context context, String name,int groupIsReadOnly){
    	            if(context!=null){
    	                Intent intent = new Intent(ACTION_GROUP_MODIFIACTION);
    	                intent.putExtra(Groups.TITLE, name);

    	                context.sendBroadcast(intent);
    	            }
    	   }


    	    private boolean createNewGroup(ContentResolver resolver,  ArrayList<AccountData> accountList){

    	        ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
    	        ContentProviderOperation operation = null;
    	        HashMap<String, Integer> visibleMap = new HashMap<String, Integer>();
    	         //generate data
    	         for(  AccountData  account: accountList){
    	             if(!mAccountList.contains(account)){
    	                 if(isGroupMemeberReadOnlyAccountType(mContext, account.accountType, account.dataSet)){
    	                     continue;
    	                 }


    	                   int visible = 1;
    	                   if(account.accountType!=null){
    	                       Integer value = visibleMap.get(account.accountType);
    	                       if(value == null){
    	                           visible = isVisibleForAccountType(resolver, account.accountType)? 1 :0;
    	                           visibleMap.put(account.accountType, visible);
    	                       }
    	                       else{
    	                           visible = value.intValue();
    	                       }
    	                   }

    	                ContentValues values = new ContentValues();
    	                values.put(Groups.ACCOUNT_NAME, account.accountName);
    	               values.put(Groups.ACCOUNT_TYPE, account.accountType);
    	               values.put(Groups.TITLE, mTitle);
    	               values.put(Groups.NOTES, mTitle);
    	               values.put(Groups.GROUP_VISIBLE, visible);
    	               //Add by Jerry:ICS porting,System group should set group_is_read_only =1
    	               values.put(Groups.GROUP_IS_READ_ONLY, mGroupIsReadOnly);

    	               ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(Groups.CONTENT_URI);
    	                builder.withValues(values );
    	                operation = builder.build();
    	                operationList.add(operation);

    	               //Uri uri  = resolver.insert(Groups.CONTENT_URI, values);
    	               //long gid = ContentUris.parseId(uri);

    	             }
    	         }

    	         if(operationList == null || operationList.size() == 0){
    	             return false;
    	         }
    	         try {
    	             ContentProviderResult[] results = resolver.applyBatch(ContactsContract.AUTHORITY, operationList);
    	             int index = 0;
    	             if(results == null){
    	                 return false;
    	             }
    	           
    	        } catch (RemoteException e) {
    	            // TODO Auto-generated catch block
    	            e.printStackTrace();
    	            return false;
    	        } catch (OperationApplicationException e) {
    	            // TODO Auto-generated catch block
    	            e.printStackTrace();
    	            return false;
    	        }

    	        return true;
    	    }
    	    
    	    
    	    /***
    	     * It is for adding new Group. We use this method to determine set the groupVisible of the new group. 
    	     * @param resolver content resolver
    	     * @param accountType acccount type
    	     * @return isVisibleForAccountType
    	     */
    	    private static boolean isVisibleForAccountType(ContentResolver resolver,
    				String accountType) {
    			if (accountType == null)
    				return true;
    			String selection = android.provider.ContactsContract.Settings.ACCOUNT_TYPE
    					+ " = " + DatabaseUtils.sqlEscapeString(accountType);
    			Cursor cursor = resolver
    					.query(
    							android.provider.ContactsContract.Settings.CONTENT_URI,
    							new String[] { android.provider.ContactsContract.Settings.UNGROUPED_VISIBLE },
    							selection, null, null);
    			boolean result = true;
    			if (cursor != null) {
    				if (cursor.moveToFirst()) {
    					int visible = cursor.getInt(0);
    					result = visible > 0;
    				}
    				cursor.close();
    			}
    			
    			String[] projection = new String[]{
    					android.provider.ContactsContract.Groups.GROUP_VISIBLE
    			};
    			selection = android.provider.ContactsContract.Groups.TITLE+" = "+
    				DatabaseUtils.sqlEscapeString(SYSTEM_GROUP_TITLE_MYCONTACTS)
    				//add by Jerry :for new google My Contact Group should be set auto_add and group_is_read_only
    				+ " AND "+ android.provider.ContactsContract.Groups.AUTO_ADD + "=1 "
    				+ " AND "+ android.provider.ContactsContract.Groups.GROUP_IS_READ_ONLY + "=1 "
    				;
    			
    			
    			if(accountType.equals(PeopleConstants.ACCOUNT_TYPE_GOOGLE)){
    	    	    cursor = resolver.query(android.provider.ContactsContract.Groups.CONTENT_URI , projection , selection, null, null);
    	    	    if(cursor!=null){
    	    		    try{
    	        		    if(cursor.moveToFirst()){
    	        			    int mycontactGroupVisible = cursor.getInt(0);
    	        			    result = mycontactGroupVisible>0;
    	        		    }
    	        		    else{
    	        	            result = true;
    	        	        }
    	    	        }finally{
    	    		        cursor.close();
    	    	        }
    	            }
    	        }
    			return result;
    		}
    	    
    	    /*
    	     *   remove rawcontact member in the group with target name
    	     */
    	    private void removeGroupMember(ContentResolver resolver, ArrayList<RawContactData> list){

    	        Uri uri  = Uri.parse("content://"+ContactsContract.AUTHORITY+"/groups_member/name");
    	        String pattern=DatabaseUtils.sqlEscapeString(mTitle);
    	        StringBuffer buffer = new StringBuffer();

    	        buffer.append("("+Groups.TITLE + "=" +pattern + ")");

    	        String selection = Data.RAW_CONTACT_ID + " IN ( ";

    	        int size = list.size();
    	        if(size <= 0){
    	            return;
    	        }

    	        for(int i =0; i < size ; i++){
    	            RawContactData data = list.get(i);
    	            selection = selection+data._Id;
    	            if(i<size -1){
    	                selection = selection + " ,   ";
    	            }
    	        }
    	        selection = selection+ " ) ";
    	        buffer.append("  AND ");
    	        buffer.append(selection);
    	        resolver.delete(uri, buffer.toString(), null);
    	    }
    	    private static final int MAX_APPLY = 400;

    	    private boolean addGroupMember(ContentResolver resolver, ArrayList<RawContactData> list){
    	        if(list.size() == 0){
    	            return true;
    	        }

    	        ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
    	        ContentProviderOperation operation = null;
    	        int applyCount = 0;

    	        String selection = Groups.TITLE +"="+DatabaseUtils.sqlEscapeString(mTitle);
    	        Cursor c = resolver.query(Groups.CONTENT_URI, sGroupProjection, selection, null, null);
    	        if(c!=null){
    	            if(c.moveToFirst()){
    	                do{
    	                    long id = c.getLong(0);
    	                    String accountName = c.getString(2);
    	                    String accountType = c.getString(3);
    	                    String sourceId = c.getString(5);

    	                    AccountData data = new AccountData(accountName, accountType);
    	                    for(RawContactData rdata : list){
    	                        if(DEBUG) Log.v(TAG,"addGroupMember to group: "+data + "   gid: "+id);
    	                        AccountData rawdata = new AccountData(rdata.accountName, rdata.accountType);

    	                        //the same
    	                            if(data.equals(rawdata)){

    	                                if(DEBUG) Log.v(TAG,"insert member ( "+rawdata  +" )  to ("+data+"  , " + id+"   )");

    	                                  ContentValues values = new ContentValues();
    	                                     values.put(Data.MIMETYPE, GroupMembership.CONTENT_ITEM_TYPE);
    	                                     values.put(Data.RAW_CONTACT_ID, rdata._Id);
    	                                     values.put(GroupMembership.GROUP_ROW_ID,id);
    	                                  // + modifed by Lucy:workaround for google sync new group won't sync group memeber issu
    	                                     if(TextUtils.isEmpty(sourceId) && rdata.accountType.equals(PeopleConstants.ACCOUNT_TYPE_GOOGLE)){
    	                                         values.put(Data.DATA5, "1");  //mark data5 as should sync group rawcontact or not, 1 = not
    	                                     }
    	                                  // - modifed by Lucy:workaround for google sync new group won't sync group memeber issu
    	                                     Uri insertGroupMemberUri = ContentUris.withAppendedId(
    	                                             RawContacts.CONTENT_URI, rdata._Id);
    	                                         Uri insertUri = Uri.withAppendedPath(insertGroupMemberUri, Contacts.Data.CONTENT_DIRECTORY);
    	                                         //insert group member
    	                                     //resolver.insert(insertUri, values);
    	                                     //**********
    	                                         ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(insertUri);
    	                                         builder.withValues(values );
    	                                        operation = builder.build();
    	                                        operationList.add(operation);
    	                                        applyCount++;
    	                                        if(applyCount >=MAX_APPLY){
    	                                            try {
    	                                                resolver.applyBatch(ContactsContract.AUTHORITY, operationList);
    	                                                operationList.clear();
    	                                                applyCount = 0;
    	                                            } catch (RemoteException e) {
    	                                                e.printStackTrace();
    	                                            } catch (OperationApplicationException e) {
    	                                            }
    	                                        }
    	                                     //*********
    	                            }
    	                    }
    	                }while(c.moveToNext());
    	            }
    	            c.close(); // + Lucy fix cursor leak
    	            c= null;
    	             if(operationList.size() > 0){
    	                 try {
    	                        resolver.applyBatch(ContactsContract.AUTHORITY, operationList);
    	                        operationList.clear();
    	                        applyCount = 0;
    	                    } catch (RemoteException e) {
    	                        e.printStackTrace();
    	                    } catch (OperationApplicationException e) {
    	                    }
    	                 }
    	                 operationList.clear();
    	        }


    	        return true;
    	    }
    	    
    	    private static boolean isGroupMemeberReadOnlyAccountType(Context context, String accountType, String dataSet){
    	        if(accountType == null){
    	            return true;
    	        }
    	        
    	        if(accountType.equals(PeopleConstants.ACCOUNT_TYPE_FACEBOOK)
    	                ||  accountType.equals(PeopleConstants.ACCOUNT_TYPE_FLICKR)
    	                ||  accountType.equals(PeopleConstants.ACCOUNT_TYPE_TWITTER)
    	                ||  accountType.equals(PeopleConstants.ACCOUNT_TYPE_PLURK)
    	                ||  accountType.equals(PeopleConstants.ACCOUNT_TYPE_ORIGINAL_TWITTER)
    	                ||  accountType.equals(PeopleConstants.ACCOUNT_TYPE_ORIGINAL_FACEBOOK)
    	                ){
    	            return true;
    	        }
    	        //TODO
    	        return false;
    	    }


    	    
	}

	private static class GroupDelta {
		private long mId;
		private String mAccountName;
		private String mAccountType;
		private String mDataSet;
		private int mIsDeleted;
		private String mNotes;
		private String mSystemId;
		private String mSourceId;
		private Bitmap mBitmap;
		// Dennis.RT_Lin, for MyCommunity Tariff Indicator Icon
		private Bitmap mTiIcon;

		private ArrayList<Long> mRawContactList = new ArrayList<Long>();

		private static GroupDelta fromEntity(Entity entity) {
			GroupDelta delta = new GroupDelta();
			ContentValues values = entity.getEntityValues();
			delta.mAccountName = values.getAsString(Groups.ACCOUNT_NAME);
			delta.mAccountType = values.getAsString(Groups.ACCOUNT_TYPE);
			delta.mSourceId = values.getAsString(Groups.SOURCE_ID);
			delta.mSystemId = values.getAsString(Groups.SYSTEM_ID);
			delta.mDataSet = values.getAsString(Groups.DATA_SET);
			delta.mNotes = values.getAsString(Groups.NOTES);
			
			delta.mId = values.getAsLong(Groups._ID);
			delta.mIsDeleted = values.getAsInteger(Groups.DELETED);

			for (NamedContentValues namedValues : entity.getSubValues()) {
				if (namedValues.values != null) {
					String rowId = namedValues.values
							.getAsString(GroupMembership.GROUP_ROW_ID);
					delta.mRawContactList.add(Long.parseLong(rowId));
				}
			}
			return delta;
		}

	}    
	
	private static class RawContactData extends AccountData{
		 private long _Id;
		 private long sourceId;
		 private RawContactData(String name, String type, String dataSet, long rawId){
		            super(name, type);
		            _Id = rawId;
		        }
		 public String toString(){
		            String str = isFallback? "default" :  "  accountName: "+accountName +", "+accountType ;
		            return   " (" +str+" , "+_Id +" ) ";
	     }
	}
	
	private static class AccountData{
    	protected String accountName;
    	protected String accountType;
    	private String dataSet;
    	protected boolean isFallback;

    	private AccountData(String name, String type){
            if(name == null|| type ==null){
                isFallback = true;
            }
            else{
                accountName = name;
                accountType = type;
            }
        }

    	public String toString(){
            return isFallback? "default" :  " ( accountName: "+accountName +", "+accountType +" ) ";
        }

        @Override
        public int hashCode(){
            return 1;
        }
        @Override
        public boolean equals(Object obj){
            /*
            if( obj instanceof AccountData ){
                return false;
            }*/
            AccountData data = (AccountData)obj;
            if(isFallback && data.isFallback){
                return true;
            }
            else if(!isFallback && !data.isFallback){
                if(accountName.equals(data.accountName) && accountType.equals(data.accountType)
                        && CompareDataSet(data.dataSet, dataSet)){
                    return true;
                }
            }
            else{
                return false;
            }
            return false;
        }
        
        private static boolean CompareDataSet(String dataSet1, String dataSet2){
            if(dataSet1 == dataSet2){ 
                return true;
            }
            if(dataSet1 == null || dataSet2 == null){
                return false;
            }
            return dataSet1.equals(dataSet2);
        }

    }
    	    
  //set by Jerry: should have group_is_read_only to check system group
  	/**
  	 * getDisplayGroupName 
  	 * @param context context 
  	 * @param name name
  	 * @param groupIsReadonly groupIsReadonly
  	 * @return string 
  	 */
  	public static final String getDisplayGroupName(Context context, String name,int groupIsReadonly) {
      	CharSequence sequence = null;
          if (name != null && name.equals(PeopleConstants.DEFAULT_GROUP_FAVORITE)) {
          	sequence = context.getText(R.string.favorite_group);
          }
          else if (name != null && name.equals(PeopleConstants.DEFAULT_GROUP_IMPORTANCE)) {
          	sequence = context.getString(R.string.vip_group);
          }
          else if (name != null && name.equals(PeopleConstants.DEFAULT_GROUP_FAMILY) && (groupIsReadonly==1)) {
          	sequence = context.getString(R.string.family_group);
          }
          else if (name != null && name.equals(PeopleConstants.DEFAULT_GROUP_FRIEND) && (groupIsReadonly==1)) {
          	sequence = context.getString(R.string.friends_group);
          }
          else if (name != null && name.equals(PeopleConstants.DEFAULT_GROUP_COWORK) && (groupIsReadonly==1)) {
          	sequence = context.getString(R.string.coworkers_group);
          }
          else if (name != null && name.equals(PeopleConstants.DEFAULT_GROUP_FREQUENT_CONTACT)) {
              sequence = context.getString(R.string.frequent_group);
          }
          
          
          return sequence!=null ? sequence.toString() : name;
    }

}
