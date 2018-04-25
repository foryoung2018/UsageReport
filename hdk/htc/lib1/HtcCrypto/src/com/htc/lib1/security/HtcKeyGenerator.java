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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class HtcKeyGenerator
{
    // Generate a 256-bit key
    final static int GEN_KEY_LENGTH = 256;
    final static int GEN_KEY_ITERATIONS = 1000;
    final static String GEN_KEY_ALGORITHM = "AES";
	final static String GEN_KEY_ALGORITHM_FULL = "AES/CBC/PKCS5Padding";
	final static int IV_SIZE = 16;
	final static int SALT_SIZE = 8;

	
    /**
     * Constructors
     */
	private HtcKeyGenerator(){}
	
	
    /**
     * Get the name of algorithm used to generate symmetric key.
     * @return the name of algorithm.
     */
	public static String getGeneratorAlgorithm()
	{
		return GEN_KEY_ALGORITHM;
	}
	
	
    /**
     * Get the key length of symmetric key.
     * @return the length of symmetric key.
     */
	public static int getKeyLength()
	{
		return GEN_KEY_LENGTH;
	}

	
    /**
     * Generate a symmetric key using SecureRandom as source of randomness.
     * @return a new SecretKey with cryptographically strong random number generator.
     */
	public static SecretKey generateRandomKey()
	{
        SecureRandom secureRandom = new SecureRandom();
        // Do *not* seed secureRandom! Automatically seeded from system entropy.
        KeyGenerator keyGenerator = null;
        try
        {
        	keyGenerator = KeyGenerator.getInstance(GEN_KEY_ALGORITHM);
        }
        catch (NoSuchAlgorithmException e)
        {
        	e.printStackTrace();
        	return null;
        }

        keyGenerator.init(GEN_KEY_LENGTH, secureRandom);
        SecretKey secretKey = keyGenerator.generateKey();
        
        return secretKey;
	}

	
    /**
     * Generate a symmetric key using user-input password/pin and salt.
     * @param passphraseOrPin the password.
     * @param salt the salt.
     * @return the SecretKey with PBEKeySpec.
     */
	public static SecretKey generateKey(char[] passphraseOrPin, byte[] salt) 
	        throws InvalidKeySpecException
	{
        // Number of PBKDF2 hardening rounds to use. Larger values increase
        // computation time. You should select a value that causes computation
        // to take >100ms.

        // Generate a 256-bit key
		try
		{
			SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(HtcUtils.GENERATE_SYMMETRIC_KEY);
			KeySpec keySpec = new PBEKeySpec(passphraseOrPin, salt, GEN_KEY_ITERATIONS, GEN_KEY_LENGTH);
			SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
			return secretKey;
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		
		return null;
    }
	
	
    /**
     * Restore key from a byte array (usually from secretKey.getEncoded()).
     * @param keyBytes the key material of the secret key. The first len bytes of the array beginning at offset inclusive are copied to protect against subsequent modification.
     * @return a new SecretKeySpec instance.
     */
	public static SecretKey restoreKey(byte[] keyBytes)
	{
		return new SecretKeySpec(keyBytes, 0, keyBytes.length, GEN_KEY_ALGORITHM_FULL);
	}
}
