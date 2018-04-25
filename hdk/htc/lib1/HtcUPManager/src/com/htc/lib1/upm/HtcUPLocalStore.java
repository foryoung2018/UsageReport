package com.htc.lib1.upm;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import java.io.EOFException;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Comparator;

public final class HtcUPLocalStore {
    
    private static final String TAG = "HtcUPLocalStore";
    private static HtcUPLocalStore sLocalStore;
    private final static String LOCAL_STORE_PREFIX = "local_store_";
    private final static String LOCAL_STORE = LOCAL_STORE_PREFIX + "0";
    private final static int MAX_BROKEN_FILE = 5;
    private final static int MAX_READ_BYTE = 100 * 1024;  // 100 KB
    private final static long MAX_LOCAL_STORE_SIZE = 1 * 1024 * 1024; // 1 MB for single store
    private final static int VERSION = 1;  //version code
    
    //data type
    private final static int TYPE_NO_DATA = 0;
    private final static int TYPE_PLAIN_TEXT = 1;
    private final static int TYPE_ENCRYPTED_DATA = 2;
    
    //byte length
    private final static int PRESERVER_LENGTH = 9;
    private final static int BYTE_LENGTH_LONG = 8;
    private final static int BYTE_LENGTH_INT = 4;
    private final static int BYTE_LENGTH = 1;
    public static HtcUPLocalStore getInstance(Context context) {
        if (sLocalStore == null) 
            sLocalStore = new HtcUPLocalStore(context);
        return sLocalStore;
    }
    
    public static abstract interface DataStoreGetter {
        public abstract void getData(String appID, String action, String category, String label, int value, String[] labels, String[] values, long timestamp);
        public abstract void clearAllData();
    }
    
    private Context mContext;
    private HtcUPDataProtector mProtector;
    private File mFolder;
    private File mCurrentStore;
    private boolean mExistPendingData;
    private HtcUPLocalStore(Context context) {
        mContext = context;
        mFolder = mContext.getDir("up", Context.MODE_PRIVATE);
        mProtector = HtcUPDataProtector.getInstance(mContext);
        mExistPendingData = checkPendingData();
    }
    
    public boolean existPendingData() {
        return mExistPendingData;
    }
    
    public void storeDataToFile(Bundle data, boolean doEncryption) {
    	//Cache current store
        if (mCurrentStore == null) {
        	mCurrentStore = getCurrentStore();
        }
        if (data != null && data.getBoolean(Common.IS_DEBUGGING, false))
            HtcUPDataUtils.printDataForDebugging(data);
        internalStore(data, doEncryption, mCurrentStore);
    }
    
    // Encryption fields:  category, action, label, value
    private void internalStore(Bundle data, boolean doEncryption, File localStore) {
    	
    	if (localStore == null) {
    		Log.d(TAG, "[Warning] Cannot get or create local store, so ignore data.");
    		return;
    	}
        
        RandomAccessFile store = null;
        long length = 0;
        int flag = 0;
        int version = 0;
        int currentDataLength = 0;
        int lengthChecker = 0;
        int writeLength = 0;
        byte[] byteArray = null;
        byte[] encrypted = null;
        boolean isFinished = false;
        try {
            
        	store = new RandomAccessFile(localStore, "rw");
            
            if (localStore.exists() && localStore.length() >= PRESERVER_LENGTH) {
            	version = store.read();
            	currentDataLength = store.readInt();
            	lengthChecker = store.readInt();
            } else {
            	store.write(VERSION);
            	store.writeInt(currentDataLength);
            	store.writeInt(lengthChecker);
            	version = VERSION;
            }
            
            length = store.length();
            
            Log.d(TAG, "Version: " + version + "  File total length: " + length + " bytes.  Data length: " + currentDataLength + "bytes");
            
            if (currentDataLength != lengthChecker || currentDataLength > length || currentDataLength < 0) {
            	Log.d(TAG, "File currption --> " + localStore.getName());
            	internalStore(data, doEncryption, createNewStore(localStore));
            } else if (length <= MAX_LOCAL_STORE_SIZE){
                if (version == VERSION) {
                	//Seek to end of data.
                	store.skipBytes(currentDataLength);
                    
                    // write appId (required)
                	byteArray = getBytes(data.getString(Common.APP_ID));
                	flag = write(store, byteArray, false);
                    writeLength = writeLength +flag;
                    
                    // write timestamp (required)
                    store.writeLong(data.getLong(Common.TIMESTAMP));
                    writeLength = writeLength + BYTE_LENGTH_LONG;
                                    
                    // write action 
                    byteArray = getBytes(data.getString(Common.EVENT_ACTION));
                    flag = write(store, byteArray, doEncryption);
                    writeLength = writeLength +flag;
                                   
                    // write category
                    byteArray = getBytes(data.getString(Common.EVENT_CATEGORY));
                    flag = write(store, byteArray, doEncryption);
                    writeLength = writeLength +flag;
                    
                    // write label
                    byteArray = getBytes(data.getString(Common.EVENT_LABEL));
                    flag = write(store, byteArray, doEncryption);
                    writeLength = writeLength +flag;
                    
                    
                    // write value
                    flag = data.getInt(Common.EVENT_VALUE, -1) < 0 ? 0 : 1;
                    if (flag == 1) {
                        if (doEncryption) {
                        	store.write(TYPE_ENCRYPTED_DATA);
                            encrypted = mProtector.encrypt(ByteBuffer.allocate(4).putInt(data.getInt(Common.EVENT_VALUE)).array());
                            if (encrypted != null) {
                                flag = encrypted.length;
                                store.writeInt(flag);  // write the length of encrypted byte array
                                if (flag > 0) {
                                	store.write(encrypted);
                                    writeLength = writeLength + flag;
                                }
                            } else  // encryption failed
                            	store.writeInt(0);
                            
                        } else {
                        	store.write(TYPE_PLAIN_TEXT);
                        	store.writeInt(data.getInt(Common.EVENT_VALUE));
                        }
                        writeLength = writeLength + BYTE_LENGTH + BYTE_LENGTH_INT;
                    } else {
                    	store.write(TYPE_NO_DATA);
                        writeLength = writeLength + BYTE_LENGTH;
                    }       
                    
                    // write attributes
                    String[] labels = data.getStringArray(Common.ATTRIBUTE_LABLE);
                    String[] values = data.getStringArray(Common.ATTRIBUTE_EXTRA);
                    if (labels != null && values != null && (labels.length == values.length)) {
                        int N = labels.length;
                        store.writeInt(N);
                        for (int i = 0 ;  i < N ; i ++) {
                        	byteArray = getBytes(labels[i]);
                        	flag = write(store, byteArray, false);
                        	writeLength = writeLength +flag;
                        	
                        	byteArray = getBytes(values[i]);
                        	flag = write(store, byteArray, false);
                        	writeLength = writeLength +flag;
                        }
                    } else {
                    	store.writeInt(0);
                    }
                    writeLength = writeLength + BYTE_LENGTH_INT;
                    
                    //write new length back to file header
                    //Log.d(TAG, "addition length: " + writeLength + "bytes");
                    store.seek(0); //back to head
                    store.skipBytes(BYTE_LENGTH); //skip version code
                    store.writeInt(currentDataLength + writeLength); //write new data length back to header
                    store.writeInt(lengthChecker + writeLength);
                    mExistPendingData = true;
                }
            } else {
            	Log.d(TAG, "[internalStore] The size of " + localStore.getName() + " is over the limit: " + length);
            }
            isFinished = true;
        } catch (Exception ioe) {
            Log.e(TAG,"Open local store failed", ioe);
            isFinished = false;
        } finally {
            if (store != null) {
                try {
                	store.close();
                	if (!isFinished) 
                	    resetStorage(localStore);
                } catch (IOException ioe) {  }
            }
        }
    }
    
    public void delivery(DataStoreGetter dateGetter) {
        if (mFolder != null) {
        	File[] stores = mFolder.listFiles(new StoreFilter());
        	for (File store : stores) {
                internalDelivery(dateGetter, store);
        	}
        }
    }
    
    public void internalDelivery(DataStoreGetter dateGetter, File localStore) {
        
    	if (localStore == null || !localStore.exists() ||  localStore.length() < PRESERVER_LENGTH)
            return;
    	
        RandomAccessFile store = null;
        int attributesCount = 0;
        int length = 0;
        int type = 0;
        byte[] plainText;
        String appId = "";
        long timestamp = 0;
        String action = "";
        String category = "";
        String label = "";
        int value = -1;
        String[] labels = null;
        String[] values = null;
        int version = 0;
        int currentDataLength = 0;
        int LengthChecker = 0;
        try {
                                   
            store = new RandomAccessFile(localStore, "r");
            
            version = store.read();
            if (version == VERSION) {
                currentDataLength = store.readInt();
                LengthChecker = store.readInt();
                Log.d(TAG,"[internalDelivery] data length:  " + currentDataLength + ", checker: " + LengthChecker);
                if (currentDataLength > 0 && (currentDataLength == LengthChecker)) {
                	while (true) {
                		appId = "";
                		action = "";
                        category = "";
                        label = "";
                        value = -1;
                        labels = null;
                        values = null;
                        attributesCount = 0;
                        plainText = null;
                        length = 0;
                        
                        // get appid
                        appId = read(store);
                                               
                        // get timestamp
                        timestamp = store.readLong();
                        
                        // get action
                        action = read(store);
                        
                        // get category
                        category = read(store);
                        
                        // get label
                        label = read(store);
                        
                        // get value
                        type = store.read();  // read type
                        if (type == TYPE_PLAIN_TEXT) {
                        	value = store.readInt();
                        } else if (type == TYPE_ENCRYPTED_DATA) {
                        	length = store.readInt();    // read length
                        	if (length > 0 && length < MAX_READ_BYTE) {
                        		plainText = new byte[length];
                            	store.readFully(plainText);  // read encrypted data
                            	value = mProtector.decryptInt(plainText);	
                        	} else if (length >= MAX_READ_BYTE) {
                        		int skipped = store.skipBytes(length); //If data length is too long, then we just skipped the length.
                            	Log.d(TAG, "[read] data length is too long: " + length + ", and skipped: " + skipped + "byte!");
                        	}
                        }
                        
                        //get Attributes
                        attributesCount = store.readInt();
                        if (attributesCount > 0 && attributesCount <=64 ) {
                            labels = new String[attributesCount];
                            values = new String[attributesCount];
                            for (int j = 0 ; j < attributesCount ; j ++)  {
                            	labels[j] = read(store);
                            	values[j] = read(store);
                            }
                        } else if ( attributesCount < 0 || attributesCount > 64 ){
                        	Log.d(TAG, "[Warning] Abnormal attributes count: " + attributesCount + ", ignore data! ");
                        	printDebugInformation(appId, category, action, label, value, attributesCount, labels, values);
                        	break;
                        }

                        dateGetter.getData(appId, action, category, label, value, labels, values, timestamp);
                	}
                } else {
                	Log.d(TAG, "[internalDelivery] Ignore all data in '" + localStore.getName() + "' due to the file may broken!");
                }
        	}
        } catch (EOFException eofe) { 
            Log.d(TAG, "Reach end of store: " + localStore.getName());
        } catch (IOException ioe) {
            Log.e(TAG,"Retreive local store failed: " + ioe.getMessage(), ioe);
            printDebugInformation(appId, category, action, label, value, attributesCount, labels, values);
        } catch (Exception e) {/*catch any other unexpected exception*/
        	Log.e(TAG,"Retreive local store failed: " + e.getMessage(), e);
        	printDebugInformation(appId, category, action, label, value, attributesCount, labels, values);
        } catch (OutOfMemoryError oom) {
        	Log.e(TAG,"Retreive local store failed: " + oom.getMessage(), oom);
        	dateGetter.clearAllData(); //release all data.
        	printDebugInformation(appId, category, action, label, value, attributesCount, labels, values);        	
        } finally {
            if (store != null) {
                try {
                    store.close();
                } catch (IOException ioe) {  }
            }
            resetStorage(localStore);
        }
    }
    
    public void clearDataStore() {
    	Log.d(TAG, "Clear data store!");
        if (mFolder == null)
        	return;
        
        File[] localStores = mFolder.listFiles();
        if (localStores == null || localStores.length <=0)
            return;
        
        for (File store : localStores) {
        	if (store != null && store.exists())
        		store.delete();
        }
    }
    
    private boolean checkPendingData() {
        if (mFolder != null) {
            File[] files = mFolder.listFiles(new StoreFilter());
            if (files != null && files.length > 0)
                return true;
        }
        return false;
    }
    
    private void resetStorage(File store) {
        if (store != null && store.exists()) {
        	if (mCurrentStore != null) {
        		if (mCurrentStore.getName().equals(store.getName()))
        			mCurrentStore = null;
        	}
            store.delete();
        }
        
        File[] stores = mFolder.listFiles(new StoreFilter());
        if (stores == null || stores.length <= 0)
            mExistPendingData = false;
    }
    
    private File getCurrentStore() {
    	if (mFolder == null) {
    		Log.d(TAG, "Connot open store folder!");
    		return null;
    	}
    	
    	File[] localStores = mFolder.listFiles(new StoreFilter());
    	
    	// If there is no store, then create the first store.
    	if (localStores == null || localStores.length <= 0) {
    		return new File(mFolder, LOCAL_STORE);
    	}
    	
    	Arrays.sort(localStores, new Comparator<File>(){
			@Override
			public int compare(File o1, File o2) {
				int n1 = extractNumber(o1.getName());
                int n2 = extractNumber(o2.getName());
                return n1 - n2;
			}
			
			private int extractNumber(String fileName) {
				int number = 0;
				String stringNumber = fileName.substring(LOCAL_STORE_PREFIX.length());
				try {
					number = Integer.parseInt(stringNumber);
				} catch (NumberFormatException nfe) {
					Log.d(TAG, "Cannot parse file name :" + fileName + " due to--> " + nfe.getMessage());
				}
				return number;
			}    		
    	});
    	Log.d(TAG, "Get current store: " + localStores[localStores.length-1].getName());
    	return localStores[localStores.length-1];
    }
    
    private File createNewStore(File localStore) {
    	String fileName = localStore.getName();
    	String stringNumber = fileName.substring(LOCAL_STORE_PREFIX.length());
    	int number = 0;
    	try {
            number = Integer.parseInt(stringNumber);
        } catch (NumberFormatException nfe) {
            Log.d(TAG, "[createNewStore] Cannot parse file name :" + fileName + " due to--> " + nfe.getMessage());
            resetStorage(localStore);
            return null;
        }
    	
    	number ++;
    	
    	if (number > MAX_BROKEN_FILE) {
    		Log.d(TAG, "[createNewStore] Too many borken files, so stop creating new file!");
            return null;
    	}
    	
    	File newStore = new File(mFolder, (LOCAL_STORE_PREFIX + number));
    	mCurrentStore = newStore;
    	Log.d(TAG, "[createNewStore] New file: " + newStore.getName());
        return newStore;
    }
    
    private int write(RandomAccessFile store, byte[] byteArray, boolean doEncyption) throws IOException{
    	int totalWriteLength = 0;
    	int length = 0;
    	byte[] encrypted = null;
    	
    	if (byteArray != null) {
            if (doEncyption) {
            	store.write(TYPE_ENCRYPTED_DATA);
                encrypted = mProtector.encrypt(byteArray);
                if (encrypted != null) {
                	length = encrypted.length;
                    store.writeInt(length); // write the length of encrypted byte array
                    if (length > 0) {
                    	store.write(encrypted);
                        totalWriteLength = totalWriteLength + length;
                    }
                } else // encryption failed
                	store.writeInt(0);
            } else {
            	store.write(TYPE_PLAIN_TEXT);  //write type (1 byte)
            	length = byteArray.length;
                store.writeInt(length);             // write length (4 bytes)
                store.write(byteArray);            // write data (n bytes)
                totalWriteLength = totalWriteLength + length;
            }
            totalWriteLength = totalWriteLength + BYTE_LENGTH_INT + BYTE_LENGTH ; //integer length of type(1) and length(4)
        } else {
        	store.write(TYPE_NO_DATA);
            totalWriteLength = totalWriteLength + BYTE_LENGTH;
        }
    	
    	return totalWriteLength;
    }
    
    private String read(RandomAccessFile store) throws IOException, EOFException{
        String result = null;
        int type = 0;
        int length = 0;
        byte[] data = null;
        
        type = store.read(); // read type (1 byte)
        if (type != TYPE_NO_DATA) {
            length = store.readInt();  // read length (4 bytes)
            if (length > 0 && length <= MAX_READ_BYTE) {
                data = new byte[length];
                store.readFully(data);  // read data (n bytes)
                if (type == TYPE_PLAIN_TEXT) {
                    result = byteToString(data);
                } else if (type == TYPE_ENCRYPTED_DATA){
                    result = mProtector.decrypt(data);
                }
            } else if (length > MAX_READ_BYTE) {
            	int skipped = store.skipBytes(length); //If data length is too long, then we just skipped the length.
            	Log.d(TAG, "[read] data length is too long: " + length + ", and skipped: " + skipped + "byte!");
            }
        }
        
        return result;
    }
    
    private static String byteToString(byte[] data) { 
    	if (data == null || data.length <= 0)
    		return null;
    	try {
    		return new String(data, "UTF-8");
    	} catch (UnsupportedEncodingException uee) {
    		Log.e(TAG, "UTF-8 is not supported", uee);
    	}
    	return null;
    }
    
    private static byte[] getBytes(String data) {
    	if (TextUtils.isEmpty(data))
    		return null;
    	try {
    		return data.getBytes("UTF-8");
    	} catch (UnsupportedEncodingException uee) {
    		Log.e(TAG, "UTF-8 is not supported", uee);
    	}
    	return null;
    }
    
    private void printDebugInformation(String appId, String category, String action, String label, int value, int attributesCount, String[] labels, String[] values) {
    	if (!HtcUPDataUtils.isShippingRom(mContext)) {
    	    StringBuilder sb = new StringBuilder();
    	    sb.append("Debugging information for problem data: ").append("\n")
    	    .append("appid: ").append(appId).append("\n")
        	.append("category: ").append(category).append("\n");
        	if (!TextUtils.isEmpty(action))
        		sb.append("action: ").append(action).append("\n");
        	if (!TextUtils.isEmpty(label))
        		sb.append("label: ").append(label).append("\n");
        	if (value >= 0)
        		sb.append("value").append(value).append("\n");
        	if (attributesCount > 0 && attributesCount <= 64) {
        		sb.append("attributeCount: ").append(attributesCount).append("\n");
        		if (labels != null && values != null) {
                    sb.append("labels: ");
                    for (String element: labels) {
                    	sb.append(element).append(";");
                    }
                    sb.append("\n");
                    for (String element: values) {
                    	sb.append(element).append(";");
                    }
        		}
        	}
        	Log.d(TAG, sb.toString());
    	}
    }
    
    private static class StoreFilter implements FilenameFilter {   	
        @Override
		public boolean accept(File dir, String filename) {
        	if (filename != null)
        		return filename.startsWith(LOCAL_STORE_PREFIX);
            return false;
		}
    }
}
