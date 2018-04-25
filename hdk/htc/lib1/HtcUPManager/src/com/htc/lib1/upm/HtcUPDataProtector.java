package com.htc.lib1.upm;

import android.content.Context;
import com.htc.lib1.security.HtcKeyGenerator;
import com.htc.lib1.security.HtcUtils;
import java.nio.ByteBuffer;
import javax.crypto.SecretKey;

public class HtcUPDataProtector {
    private static final String TAG = "HtcUPDataProtector";
    private static final String ALIAS = "KEY";
    private static HtcUPDataProtector sInstance;
    public static HtcUPDataProtector getInstance(Context context) {
        if (sInstance == null)
            sInstance = new HtcUPDataProtector(context);
        return sInstance;
    }
    
    private Context mContext;
    private SecretKey mSecretKey;
    private HtcUPDataProtector(Context context) {
        mContext = context;
        mSecretKey = getSecretKey(mContext);
    }
    /**
     * Encrypt a plain text to an encrypted byte array by symmetric key.
     * @param The bytes in the input buffer
     * @return An encrypted byte array or null if no secret key
     */
    public byte[] encrypt(byte[] plainText) {
        if (mSecretKey == null)
            return null;
        byte[] encrypted = null;
        try {
            encrypted = HtcUtils.encryptSymmetric(plainText, mSecretKey);
        } catch (Exception e) {
            Log.e(TAG, "Failed to encrypt UP data!", e);
        }
        return encrypted;
    }
    
    /**
     * Decrypt an encrypted byte array to plain text by symmetric key.
     * @param An encrypted byte array which using a symmetric key
     * @return Plain text
     */
    public String decrypt(byte[] encrypted) {
        if (mSecretKey == null)
            return null;
        
        String result = null;
        try {
            byte[] byteArray = HtcUtils.decryptSymmetric(encrypted, mSecretKey);
            result = new String(byteArray, "UTF-8");
        } catch (Exception e) {
            Log.e(TAG, "Failed to decrypt encrypted byte array to string!", e);
        }
        return result;
    }
    
    /**
     * Decrypt an encrypted byte array to integer by symmetric key.
     * @param An encrypted byte array which using a symmetric key
     * @return Integer
     */
    public int decryptInt(byte[] encrypted) {
        if (mSecretKey == null)
            return -1;
        
        int result = -1;
        try {
            byte[] byteArray = HtcUtils.decryptSymmetric(encrypted, mSecretKey);
            ByteBuffer wrapper = ByteBuffer.wrap(byteArray);
            result = wrapper.getInt();
        } catch (Exception e) {
            Log.e(TAG, "Failed to decrypt encrypted byte array to integer!", e);
        }
        return result;
    }
    
    public SecretKey getSecretKey() {
    	return mSecretKey;
    }
    
    private static SecretKey getSecretKey(Context context) {
        SecretKey key = null;
        try {
           key = HtcUtils.loadSecretKey(context, ALIAS);
           if (key != null) 
               return key;
        } catch (Exception e) {
            Log.e(TAG, "Failed to load secret key from key store, this is first time to create key (" + e.getMessage() + ")");
        }
        
        try {
            Log.d(TAG, "Generate new key for protecting data.");
            key = HtcKeyGenerator.generateRandomKey();
            HtcUtils.storeSecretKey(context, key, ALIAS);
        } catch (Exception e) {
            Log.e(TAG, "Failed to store key to key store!", e);
        }
        return key;
    }
}
