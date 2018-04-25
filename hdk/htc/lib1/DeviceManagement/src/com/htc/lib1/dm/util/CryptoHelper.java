package com.htc.lib1.dm.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import android.util.Base64;

/**
 * Created by Joe_Wu on 8/23/14.
 */
public class CryptoHelper {

    // The cryptographic library provider.
    private static final String CRYPTO_PROVIDER = "BC";

    // Hash algorithm.
    private static final String SHA_256_HASH_ALGORITHM = "SHA-256";

    // --------------------------------------------------

    // Not instantiable...
    private CryptoHelper() {}

    // --------------------------------------------------

    /**
     * Generate a cryptographic hash of the input text.
     * <p>
     * Produces a base 64 encoded cryptographic hash of the input text.
     *
     * @param inText - the text to hash
     * @return a base 64 encoded cryptographic hash of the input text
     */
    public static String computeHash(String inText) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(SHA_256_HASH_ALGORITHM, CRYPTO_PROVIDER);
            digest.update(inText.getBytes(), 0, inText.length());
            byte[] binaryHash = digest.digest();

            // The following is equivalent to Commons Codec: org.apache.commons.codec.binary.Base64.encodeBase64String(binaryHash);
            return Base64.encodeToString(binaryHash, Base64.NO_WRAP);
        }
        catch (NoSuchProviderException ex) {
            throw new IllegalArgumentException(ex);
        }
        catch (NoSuchAlgorithmException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

}