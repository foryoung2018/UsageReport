
package com.htc.lib1.cs.auth.client;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.text.TextUtils;
import android.util.Base64;

import com.htc.lib1.cs.Hex;
import com.htc.lib1.cs.auth.AuthLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class to get the signature of a specific package.
 * 
 * @author samael_wang@htc.com
 */
public class SignatureHelper {
    private HtcLogger mLogger = new AuthLoggerFactory(this).create();
    private Signature[] mSignatures;

    /**
     * Create an instance.
     * 
     * @param context Context to operate on.
     * @param packageName Package name to look up.
     */
    public SignatureHelper(Context context, String packageName) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");
        if (TextUtils.isEmpty(packageName))
            throw new IllegalArgumentException("'packageName' is null or empty.");

        try {
            mSignatures = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES).signatures;
        } catch (NameNotFoundException e) {
            mLogger.error(e);
            mSignatures = new Signature[] {};
        }
    }

    /**
     * Helper method to get instance.
     * 
     * @param context {@link Context}.
     * @param packageName Package name.
     * @return Instance of {@link SignatureHelper}.
     */
    public static SignatureHelper get(Context context, String packageName) {
        return new SignatureHelper(context, packageName);
    }

    /**
     * Get sha1 hash of the signature (if any).
     * 
     * @return byte array.
     */
    public byte[] getSha1Hash() {
        for (Signature signature : mSignatures) {
            return getSha1HashBinary(signature.toByteArray());
        }
        return null;
    }

    /**
     * Get SHA-1 hash of the signature in base64 format, which is used by
     * Facebook.
     * 
     * @return SHA1 hashed string in base64.
     */
    public String getBase64Sha1HashString() {
        byte[] sha1 = getSha1Hash();
        return sha1 != null ? Base64.encodeToString(sha1, Base64.NO_WRAP) : null;
    }

    /**
     * Get SHA-1 hash of the signature in colon-separated hex format, which is
     * used by Google.
     * 
     * @return SHA1 hased string in colon-separated hex.
     */
    public String getHexSha1HashStringWithColon() {
        byte[] sha1 = getSha1Hash();
        return sha1 != null ? Hex.splitWithColon(Hex.encodeHexString(sha1)) : null;
    }

    /**
     * Get SHA-1 hash of the signature in hex format, which is used by HTC.
     * 
     * @return SHA1 hashed string in hex.
     */
    public String getHexSha1HashString() {
        byte[] sha1 = getSha1Hash();
        return sha1 != null ? Hex.encodeHexString(sha1) : null;
    }

    private static byte[] getSha1HashBinary(byte[] input) {
        if (input == null)
            throw new IllegalArgumentException("'input' is null.");

        try {
            /* Get SHA-1 algorithm */
            MessageDigest algorithm = MessageDigest.getInstance("SHA-1");
            algorithm.reset();

            /* Hash input string */
            algorithm.update(input);
            return algorithm.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
