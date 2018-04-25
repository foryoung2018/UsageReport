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

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class HtcKeyPairGenerator
{
    final static String GEN_KEY_ALGORITHM = "RSA";
    
    static final int KEY_LENGTH = 2048;
    static final int DIGEST_LENGTH = 256 / 8;
    static final int DECRYPT_BLOCK_SIZE = KEY_LENGTH / 8;
    static final int ENCRYPT_BLOCK_SIZE = DECRYPT_BLOCK_SIZE - 2 - 2 * DIGEST_LENGTH;

    
    /**
     * Constructors
     */
    private HtcKeyPairGenerator(){}    

    
    /**
     * Get the name of algorithm used to generate asymmetric keypair.
     * @return the name of algorithm. 
     */
	public static String getAlgorithm()
	{
		return GEN_KEY_ALGORITHM;
	}

	
    /**
     * Get the key length of asymmetric keypair.
     * @return the length of asymmetric key.
     */
	public static int getKeyLength()
	{
		return KEY_LENGTH;
	}
	
	
    /**
     * Get the the digest length that is supported by this asymmetric keypair.
     * @return the length of digest.
     */
	public static int getDigestLength()
	{
		return DIGEST_LENGTH;
	}
	
	
    /**
     * Get the block size of decryption.
     * @return the block size of decryption.
     */
	public static int getDecryptBlockSize()
	{
		return DECRYPT_BLOCK_SIZE;
	}
	

    /**
     * Get the block size of encryption.
     * @return the block size of encryption.
     */
	public static int getEncryptBlockSize()
	{
		return ENCRYPT_BLOCK_SIZE;
	}
	
	
    /**
     * Generate an asymmetric keypair using SecureRandom as source of randomness.
     * @return a new KeyPair with SecureRandom as source of randomness.
     */
	public static KeyPair genRandomKeyPair()
	{
		try
		{
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance(GEN_KEY_ALGORITHM);
			keyGen.initialize(KEY_LENGTH);
			
			return keyGen.genKeyPair();
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	
    /**
     * Restore keypair from two byte arrays (usually from key.getEncoded()).
     * @param encodedPrivateKey the given encoded key for generating a new PKCS8EncodedKeySpec.
     * @param encodedPublicKey the given encoded key for generating a new X509EncodedKeySpec.
     * @return the restored Keypair from given two byte arrays.
     */
	public static KeyPair restoreKey(byte[] encodedPrivateKey, byte[] encodedPublicKey) 
	        throws InvalidKeySpecException
	{
		try
		{
			KeyFactory keyFactory = KeyFactory.getInstance(GEN_KEY_ALGORITHM);
			X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
			PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
			
			PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
			PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
			
			return new KeyPair(publicKey, privateKey);

		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
}
