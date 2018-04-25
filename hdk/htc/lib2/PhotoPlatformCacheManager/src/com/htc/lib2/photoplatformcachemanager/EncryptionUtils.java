package com.htc.lib2.photoplatformcachemanager;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.content.Context;
import android.text.TextUtils;

public class EncryptionUtils {
    private static String sEncryptionKey = "0123456789012345678901234567890123456789012345678901234567890123";
    private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int CIPHER_STRENGTH = 128;    
    private static final String CIPHER_PROVIDER = "BC";
    private static final String CIPHER_ALGORITHM = "AES";
    private static final ConcurrentMap<String, byte[]> RAWKEY_MAP = new ConcurrentHashMap<String, byte[]>();
    public static final String CIPHER_IV_STR = "2648171190913351";
    
    private static String getXorString(String base, String xor) {
        if ( TextUtils.isEmpty(base) ) {
            return null;
        }
        if ( TextUtils.isEmpty(xor) ) {
            return base;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < xor.length(); i++) {
            if ( i < base.length() ) {
                char a = (char) (base.charAt(i) ^ xor.charAt(i));
                builder.append(a);
            }
        }
        return builder.toString();
    }    
    
    private static List<String> getSplittedStrings(String key, int bitStrength) {
        List<String> list = new ArrayList<String>();
        if ( TextUtils.isEmpty(key) || bitStrength <= 0 ) {
            return list;
        }
        int length = key.length(); // 64
        int byteStrength = bitStrength / 8; // 16 (from 128bit)
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i += byteStrength) {
            builder.append(key.substring(i, Math.min(length, i + byteStrength)));
            int offset = byteStrength - builder.length();
            if ( offset > 0 ) {
                for (int j = 0; j < offset; j++) {
                    builder.append(" ");
                }
            }
            list.add(builder.toString());
            builder.setLength(0); // clear
        }
        return list;
    }    
    
    private static byte[] getRawKey(String key) {
        if ( TextUtils.isEmpty(key) ) {
            return null;
        }
        if ( RAWKEY_MAP.containsKey(key) ) {
            return RAWKEY_MAP.get(key);
        }
        String base = null;
        for (String s : getSplittedStrings(key, CIPHER_STRENGTH)) {
            if ( base == null ) {
                base = s;
            } else {
                base = getXorString(base, s);
            }
        }
        byte[] value = null;
        if ( base != null ) {
            value = base.getBytes();
            RAWKEY_MAP.put(key, value);
        }
        return value;
    }    
    
    public static Cipher getCipher(int mode, String key, String ivString) {
        if ( key == null || ivString == null ) {
            return null;
        }
        Cipher cipher = null;
        try {
            SecretKeySpec spec = new SecretKeySpec(getRawKey(key), CIPHER_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(ivString.getBytes());
            cipher = Cipher.getInstance(CIPHER_TRANSFORMATION, CIPHER_PROVIDER);
            if ( cipher == null ) {            	
                return null;
            }
            cipher.init(mode, spec, iv);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return cipher;
    }    
    
    public static String getEncryptionKey(Context context) {
//      if ( context == null ) {
//          return null;
//      }
//      if ( sEncryptionKey == null ) {
//          synchronized (LOCK_KEY) {
//              if ( sEncryptionKey == null ) {
//                  ContentProviderClient providerClient = null;
//                  Cursor c = null;
//                  providerClient = context.getContentResolver()
//                          .acquireUnstableContentProviderClient(ENCRYPTION_KEY_PROVIDER_AUTHORITY);
//                  if ( providerClient != null ) {
//                      try {
//                          c = providerClient.query(
//                                  Download.ENCRYPTION_KEY_URI,
//                                  null,
//                                  null,
//                                  null,
//                                  null
//                          );
//                          if ( c != null && c.moveToNext() ) {
//                              if ( "encryption_key".equals(c.getString(0)) ) {
//                                  String key = c.getString(1);
//                                  sEncryptionKey = key;
//                              }
//                          }
//                      } catch (RemoteException e) {
//                          e.printStackTrace();
//                      } finally {
//                          if ( c != null ) {
//                              c.close();
//                          }
//                          if ( providerClient != null ) {
//                              providerClient.release();
//                          }
//                      }
//                  }
//              }
//          }
//      }
      return sEncryptionKey;
  }        
}
