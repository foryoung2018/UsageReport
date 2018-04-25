/*
 * HTC Corporation Proprietary Rights Acknowledgment
 *
 * Copyright (C) 2013 HTC Corporation
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
 */
package com.htc.lib1.security;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.UnrecoverableKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

import android.util.Log;
import android.content.Context;


public class HtcUtils
{
    public static final String TAG = "HtcCrypto.HtcUtils";
    
    /**
     * The constant name of algorithm for Secure Hash Algorithm uses 32-bit words.
     */
	public static final String MESSAGE_DIGEST = "SHA-256";
	
    /**
     * The constant name of algorithm for keyed-hash message authentication code (HMAC) which use SHA-256 cryptographic hash functions.
     */
	public static final String HMAC = "HmacSHA256";
	
    /**
     * The constant name of algorithm for RSA PKCS1 (v1.5) signature with SHA256 hash and X.509 encoding format.
     */
	public static final String SIGNATURE = "SHA256withRSA";
	
    /**
     * The constant name of algorithm for Advanced Encryption Standard (AES).
     */
	public static final String SYMMETRIC_ENCRYPTION = "AES";
	
    /**
     * The constant name of algorithm for an AES cipher in CBC (Cipher Block Chaining) mode, with PKCS5-style padding.
     */
	public static final String SYMMETRIC_ENCRYPTION_FULL = "AES/CBC/PKCS5Padding";
	
    /**
     * The constant name of algorithm for RSA cipher in ECB (Electronic Codebook) mode, with OAEP SHA-256 hash crypto and MGF1 in OAEP Padding.
     */
	public static final String ASYMMETRIC_ENCRYPTION_FULL = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
	
    /**
     * The constant name of algorithm for PBKDF2 (Password-Based Key Derivation Function 2) with keyed-hash message authentication code (HMAC) which use SHA-1 cryptographic hash functions.
     */
	public static final String GENERATE_SYMMETRIC_KEY = "PBKDF2WithHmacSHA1";

	
    /**
     * The constant name of key store type for Bouncy Castle APIs Set.
     */
	public static final String KEY_STORE_TYPE = "BKS";
	
	
    /**
     * The constant name of key store file for Bouncy Castle APIs Set.
     */
	public static final String KEY_STORE_FILE = "keystore.bks";	

	
	//*************************************************************************
    /**
     * Constructors
     */
	private HtcUtils(){}	

	
    /**
     * Get the name of algorithm used in MessageDigest.
     * @return the name of algorithm.
     */
	public static String getMessageDigestAlgorithm()
	{
		return MESSAGE_DIGEST;
	}
	
	
    /**
     * Get the name of algorithm used in HMAC.
     * @return the name of algorithm.
     */
	public static String getHmacAlgorithm()
	{
		return HMAC;
	}
	
	
    /**
     * Get the name of algorithm used in Signature.
     * @return the name of algorithm.
     */
	public static String getSignatureAlgorithm()
	{
		return SIGNATURE;
	}
	
	
    /**
     * Get the name of algorithm used in Signature.
     * @return the name of algorithm.
     */
	public static String getSymmetricAlgorithm()
	{
	    return SYMMETRIC_ENCRYPTION_FULL;
	}
	
	
    /**
     * Get the name of algorithm used in Signature.
     * @return the name of algorithm.
     */
	public static String getAsymmetricAlgorithm()
	{
	    return ASYMMETRIC_ENCRYPTION_FULL;
	}
	
	
    /**
     * Decrypt an InputStream to a CipherInputStream with a given secretkey.
     * @param is the to-be-processed input stream.
     * @param secretKey a random-generated symmetric key.
     * @return a CipherInputStream from an InputStream and a Cipher.
     */
	public static InputStream decryptStreamSymmetric(InputStream is, SecretKey secretKey) 
	        throws IOException, InvalidKeyException, InvalidAlgorithmParameterException
	{
		byte[] iv = new byte[HtcKeyGenerator.IV_SIZE];
		try
		{
			is.read(iv);
			IvParameterSpec ivSpec = new IvParameterSpec(iv);
			SecretKeySpec skeySpec = new SecretKeySpec(secretKey.getEncoded(), SYMMETRIC_ENCRYPTION);
			Cipher cipher = Cipher.getInstance(SYMMETRIC_ENCRYPTION_FULL);
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);
			return new CipherInputStream(is, cipher);
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchPaddingException e)
		{
			e.printStackTrace();
		}
				
		return null;
	}
	
	
    /**
     * Encrypt a OutputStream to a CipherOutputStream with a given secretkey.
     * @param os the OutputStream object.
     * @param secretKey a random-generated symmetric key for initializing a Cipher object. 
     * @return a CipherOutputStream from an OutputStream and a Cipher.
     */
	public static OutputStream encryptStreamSymmetric(OutputStream os, SecretKey secretKey) 
	        throws IOException, InvalidKeyException, InvalidAlgorithmParameterException
	{
		try
		{
			byte[] iv = new byte[HtcKeyGenerator.IV_SIZE];
			SecureRandom sr = secureRandom();
			sr.nextBytes(iv);
			os.write(iv);
			IvParameterSpec ivSpec = new IvParameterSpec(iv);
			SecretKeySpec skeySpec = new SecretKeySpec(secretKey.getEncoded(), SYMMETRIC_ENCRYPTION);
			Cipher cipher = Cipher.getInstance(SYMMETRIC_ENCRYPTION_FULL);
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
			return new CipherOutputStream(os, cipher);
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchPaddingException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/**
	 * Encrypt a plaintext to an encrypted byte array using a symmetric key generated from passwd.
	 * @param plaintext the bytes in the input buffer.
	 * @param passwd the password for generating a symmetric key.
	 * @return encryped byte array using a symmetric key.
	 */
	public static byte[] encryptSymmetric(byte[] plaintext, char[] passwd) 
	        throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException
	{
		try
		{
			byte[] salt = new byte[HtcKeyGenerator.SALT_SIZE];
			byte[] iv = new byte[HtcKeyGenerator.IV_SIZE];
			SecureRandom sr = secureRandom();
			sr.nextBytes(salt);
			sr.nextBytes(iv);
			SecretKey secretKey = HtcKeyGenerator.generateKey(passwd, salt);
			
			IvParameterSpec ivSpec = new IvParameterSpec(iv);
			SecretKeySpec skeySpec = new SecretKeySpec(secretKey.getEncoded(), HtcUtils.SYMMETRIC_ENCRYPTION);
			Cipher cipher = Cipher.getInstance(HtcUtils.SYMMETRIC_ENCRYPTION_FULL);
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);

			byte[] encrypted = cipher.doFinal(plaintext);
			byte[] merged = new byte[HtcKeyGenerator.SALT_SIZE + HtcKeyGenerator.IV_SIZE + encrypted.length];
			
			System.arraycopy(salt, 0, merged, 0, HtcKeyGenerator.SALT_SIZE);
			System.arraycopy(iv, 0, merged, HtcKeyGenerator.SALT_SIZE, HtcKeyGenerator.IV_SIZE);
			System.arraycopy(encrypted, 0, merged, HtcKeyGenerator.SALT_SIZE + HtcKeyGenerator.IV_SIZE, encrypted.length);
			return merged;
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchPaddingException e)
		{
			e.printStackTrace();
		}

		return null;
	}
	
	
	/**
	 * Encrypt a plaintext to an encrypted byte array using a giving symmetric key.
	 * @param plaintext the bytes in the input buffer.
	 * @param secretKey a random-generated symmetric key.
	 * @return an Encrypted byte array.
	 */
	public static byte[] encryptSymmetric(byte[] plaintext, SecretKey secretKey) 
	        throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
	{
		try
		{
			byte[] iv = new byte[HtcKeyGenerator.IV_SIZE];
			SecureRandom sr = secureRandom();
			sr.nextBytes(iv);
			IvParameterSpec ivSpec = new IvParameterSpec(iv);
			SecretKeySpec skeySpec = new SecretKeySpec(secretKey.getEncoded(), HtcUtils.SYMMETRIC_ENCRYPTION);
			Cipher cipher = Cipher.getInstance(HtcUtils.SYMMETRIC_ENCRYPTION_FULL);
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
			byte[] encrypted = cipher.doFinal(plaintext);
			byte[] merged = new byte[HtcKeyGenerator.IV_SIZE + encrypted.length];
			System.arraycopy(iv, 0, merged, 0, HtcKeyGenerator.IV_SIZE);
			System.arraycopy(encrypted, 0, merged, HtcKeyGenerator.IV_SIZE, encrypted.length);
			
			return merged;
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchPaddingException e)
		{
			e.printStackTrace();
		}

		return null;
	}
	
	
	/**
	 * Decrypt an encrypted byte array to plaintext using a symmetric key generated from password.
	 * @param encrypted an encrypted byte array which using a symmetric key.
	 * @param passwd the password.
	 * @return a decypted byte array.
	 */
	public static byte[] decryptSymmetric(byte[] encrypted, char[] passwd) 
	        throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException
	{
		try
		{
			byte[] salt = new byte[HtcKeyGenerator.SALT_SIZE];
			int cipherSize = encrypted.length - HtcKeyGenerator.IV_SIZE - HtcKeyGenerator.SALT_SIZE;
			System.arraycopy(encrypted, 0, salt, 0, HtcKeyGenerator.SALT_SIZE);
			SecretKey secretKey = HtcKeyGenerator.generateKey(passwd, salt);

			IvParameterSpec ivSpec = new IvParameterSpec(encrypted, HtcKeyGenerator.SALT_SIZE, HtcKeyGenerator.IV_SIZE);
			SecretKeySpec skeySpec = new SecretKeySpec(secretKey.getEncoded(), HtcUtils.SYMMETRIC_ENCRYPTION);
			Cipher cipher = Cipher.getInstance(HtcUtils.SYMMETRIC_ENCRYPTION_FULL);
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);
			
			return cipher.doFinal(encrypted, HtcKeyGenerator.SALT_SIZE + HtcKeyGenerator.IV_SIZE, cipherSize);
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchPaddingException e)
		{
			e.printStackTrace();
		}

		return null;
	}
	

	/**
	 * Decrypt an encrypted byte array to plaintext using a secretKey that have encrypted this plaintext.
	 * @param encrypted the buffer with the IV. The contents of the buffer are copied to protect against subsequent modification. 
	 * @param secretKey a random-generated symmetric key.
	 * @return decrypted byte array.
	 */
	public static byte[] decryptSymmetric(byte[] encrypted, SecretKey secretKey) 
	        throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
	{
		try
		{
			int cipherSize = encrypted.length - HtcKeyGenerator.IV_SIZE;
			IvParameterSpec ivSpec = new IvParameterSpec(encrypted, 0, HtcKeyGenerator.IV_SIZE);
			SecretKeySpec skeySpec = new SecretKeySpec(secretKey.getEncoded(), HtcUtils.SYMMETRIC_ENCRYPTION);
			Cipher cipher;
			cipher = Cipher.getInstance(HtcUtils.SYMMETRIC_ENCRYPTION_FULL);
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);
			
			return cipher.doFinal(encrypted, HtcKeyGenerator.IV_SIZE, cipherSize);
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchPaddingException e)
		{
			e.printStackTrace();
		}

		return null;
	}
	
	
	/**
	 * Encrypt a plaintext to an encrypted byte array using a public key.
	 * @param source the bytes in the input buffer.
	 * @param publicKey the public key.
	 * @return an encrypted byte array.
	 */
	public static byte[] encryptAsymmetric(byte[] source, PublicKey publicKey) 
	        throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int offset = 0, sourceSize = source.length, ret;
        while (true)
        {
			try
			{
				if (offset + HtcKeyPairGenerator.ENCRYPT_BLOCK_SIZE > sourceSize)
				{
					ret = (int) (sourceSize - offset);
				}
				else
				{
					ret = HtcKeyPairGenerator.ENCRYPT_BLOCK_SIZE;
				}
				
				Cipher cipher = Cipher.getInstance(HtcUtils.ASYMMETRIC_ENCRYPTION_FULL);
				cipher.init(Cipher.ENCRYPT_MODE, publicKey);
				byte[] encryptedBlock = cipher.doFinal(source, offset, ret);
				offset += ret;
				baos.write(encryptedBlock, 0, encryptedBlock.length);
				if (offset >= sourceSize)
				{
					break;
				}
				
			}
			catch (NoSuchAlgorithmException e)
			{
				e.printStackTrace();
			}
			catch (NoSuchPaddingException e)
			{
				
				e.printStackTrace();
			}
			
        }
        
        byte[] encrypted = baos.toByteArray();
        try
        {
			baos.close();
		}
        catch (IOException e)
        {
			e.printStackTrace();
			throw new IOException(e);
			
		}
        
        return encrypted;
	}
	
	
	/**
	 * Decrypt a encrypted byte array to plaintext byte array using a private key.
	 * @param source An encrypted byte array which using a Asymmetric key.
	 * @param privateKey the private key.
	 * @return decrypted  byte array using given private key.
	 */
	public static byte[] decryptAsymmetric(byte[] source, PrivateKey privateKey) 
	        throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int offset = 0, sourceSize = source.length, ret;

        while (true)
        {
			try
			{
				if(offset + HtcKeyPairGenerator.DECRYPT_BLOCK_SIZE > sourceSize)
				{
					ret = (int)(sourceSize - offset);
				}
				else
				{
					ret = HtcKeyPairGenerator.DECRYPT_BLOCK_SIZE;
				}
				
				Cipher cipher;
				cipher = Cipher.getInstance(HtcUtils.ASYMMETRIC_ENCRYPTION_FULL);
				cipher.init(Cipher.DECRYPT_MODE, privateKey);
				byte[] decryptedBlock = cipher.doFinal(source, offset, ret);
				offset += ret;
				baos.write(decryptedBlock, 0, decryptedBlock.length);
				
				if (offset >= sourceSize)
				{
					break;
				}
				
			}
			catch (NoSuchAlgorithmException e)
			{
				e.printStackTrace();
			}
			catch (NoSuchPaddingException e)
			{
				e.printStackTrace();
			}
        }
        
        byte[] decrypted = baos.toByteArray();
        try
        {
			baos.close();
		}
        catch (IOException e)
        {
			e.printStackTrace();
			throw new IOException(e);
		}
        
        return decrypted;
	}
	
	
	/**
	 * Get the MAC(Message Authentication Code) using key as secret key and source as input.
	 * @param key the key material of the secret key.
	 * @param source data in bytes for MAC (Message Authentication Code) algorithm.
	 * @return the result of processing the given array of bytes and finishes the MAC operation.
	 */
	public static byte[] hmacFunction(byte[] key, byte[] source) 
	        throws InvalidKeyException
	{
		try
		{
			Mac mac = Mac.getInstance(HtcUtils.HMAC);
			SecretKeySpec secret_key = new SecretKeySpec(key, HtcUtils.HMAC);
			mac.init(secret_key);
			return mac.doFinal(source);
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	
	/**
	 * Get the hash from the data.
	 * @param data the array of bytes for updating a message digest algorithm, such as SHA-256.
	 * @return the array of bytes for the resulting hash value.
	 */
	public static byte[] hashFunction(byte[] data)
	{
		try
		{
			MessageDigest hash = MessageDigest.getInstance(HtcUtils.MESSAGE_DIGEST);
			hash.update(data);
			return hash.digest();
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}

	
	/**
	 * Get a SecureRandom instance without being seeded to specific seed.
	 * @return a secure random number generator (RNG) implementing the default random number algorithm.
	 */
	public static SecureRandom secureRandom()
	{
		return new SecureRandom();
	}
	
	
	/**
	 * Verify data with signature and publicKey.
	 * @param data the byte array to use for updating signature.
	 * @param sigBytes the signature bytes to be verified.
	 * @param publicKey the public key of the identity whose signature is going to be verified.
	 * @return true if the signature was verified, false if not.
	 */
	public static boolean verifyData(byte[] data, byte[] sigBytes, PublicKey publicKey) 
	        throws InvalidKeyException, SignatureException
	{
		try
		{
			Signature signature = Signature.getInstance(HtcUtils.SIGNATURE);
			signature.initVerify(publicKey);
			signature.update(data);
			return signature.verify(sigBytes);
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}


	/**
	 * Get a signature byte array generated from data and privateKey.
	 * @param data the byte array to use for updating signature.
	 * @param privateKey the private key of the identity whose signature is going to be generated.
	 * @return the signature bytes of the signing operation's result.
	 */
	public static byte[] signature(byte[] data, PrivateKey privateKey) 
	        throws InvalidKeyException, SignatureException
	{
		try
		{
			Signature signature = Signature.getInstance(HtcUtils.SIGNATURE);
			signature.initSign(privateKey);
			signature.update(data);
			return signature.sign();
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}

	
    /**
     * Store the giving SecretKey with keyName to KeyStore file placed in application’s folder (/data/data).
     * @param context the Android applicaiton's context instance for storing KeyStore
     * @param key the given SecretKey
     * @param keyName the given key name
     * @return return true if storing key successful, otherwise false.
     */
	public static boolean storeSecretKey(Context context, SecretKey key, String keyName) 
	        throws CertificateException, IOException, KeyStoreException, FileNotFoundException
	{
	    if (key == null || keyName == null || context == null)
	    {
	        Log.e(TAG, "NULL parameters !!!");
            return false;
	    }
 
        KeyStore ks = null;
        File keyStoreFile = null;
        
        try 
        {
            // getFilesDir() to get the path of application’s folder (/data/data/[PACKAGE]/files/)
            keyStoreFile = new File(context.getApplicationContext().getFilesDir(), KEY_STORE_FILE);
            
            // get KeyStore object of the BKS type.
            ks = KeyStore.getInstance(KEY_STORE_TYPE);
 
            // If KeyStore file exists, load from KeyStore file or from an new one.
            if (keyStoreFile.exists())
            {
                ks.load(new FileInputStream(keyStoreFile), null);
            }
            else
            {
                Log.d(TAG, "KeyStore file doesn't exist !");
                ks.load(null, null);
            }
           
            // Check if the alias of key exist or not.
            if (ks.containsAlias(keyName))
            {
                Log.d(TAG, "The alias of key exist.");
                return false;
            }
 
            // Store the SecretKey with giving alias name to KeyStore, and export as a file.
            ks.setEntry(keyName, new KeyStore.SecretKeyEntry(key), null);
            ks.store(new FileOutputStream(keyStoreFile), null);
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        return true;
	}
	
	
    /**
     * Load the SecretKey with giving keyName from the KeyStore file placed in application’s folder (/data/data).
     * @param context the Android applicaiton's context instance for loading SecureKey from KeyStore
     * @param keyName the key name for loading specific SecretKey from KeyStore
     * @return return SecretKey if existing, otherwise null.
     */
	public static SecretKey loadSecretKey(Context context, String keyName) 
	        throws CertificateException, IOException, UnrecoverableKeyException, KeyStoreException, FileNotFoundException
	{
        if (context == null || keyName == null)
        {
	        Log.e(TAG, "NULL parameters !!!");           
            return null;
        }
 
        KeyStore ks = null;
        // getFilesDir() to get the path of application’s folder (/data/data/[PACKAGE]/files/)
        File keyStoreFile = new File(context.getApplicationContext().getFilesDir(), KEY_STORE_FILE);
        SecretKey key = null;
       
        // Check if the KeyStore file exist or not. If it doesn’t exist, can’t load from file.
        if (keyStoreFile == null || !keyStoreFile.exists() || keyStoreFile.length() == 0)
        {
            Log.d(TAG, "The KeyStore file doesn't exist or the size is 0.");
            throw new FileNotFoundException();
        }

        try
        {
            // Init a KeyStore object of the BKS type, and load from KeyStore file.
            ks = KeyStore.getInstance(KEY_STORE_TYPE);
            ks.load(new FileInputStream(keyStoreFile), null);
            // Get SecretKey with giving alias name from KeyStore file.
            key = (SecretKey) ks.getKey(keyName, null);
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        
        return key;
	}	
}

