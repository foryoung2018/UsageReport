
package com.htc.lib1.cs.account;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.text.TextUtils;

import com.htc.lib1.cs.app.ApplicationInfoHelper;
import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * Helper class to extract the integrated app's OAuth 2.0 client configs for HTC
 * Account.
 * 
 * @author samael_wang@htc.com
 */
public class OAuth2ConfigHelper {

    /**
     * Value object describes an OAuth 2.0 client.
     * 
     * @author samael_wang@htc.com
     */
    public static class AuthClient {
        public static final String DEFAULT_REDIRECT_URL = "https://www.htcsense.com/$SS$/Services/OAuth/Approval";

        /**
         * Create an instance.
         * 
         * @param id Client id. Must not be {@code null} or empty.
         * @param secret Client secret.
         * @param scopes Space-separated list of scopes.
         * @param redirectUrl Redirect URL. If not given, it uses
         *            {@link #DEFAULT_REDIRECT_URL}.
         */
        public AuthClient(String id, String secret, String scopes, String redirectUrl) {
            if (TextUtils.isEmpty(id))
                throw new IllegalArgumentException("'id' is null or empty.");
            if (TextUtils.isEmpty(redirectUrl))
                redirectUrl = DEFAULT_REDIRECT_URL;

            this.id = id;
            this.secret = secret;
            this.scopes = scopes;
            this.redirectUrl = redirectUrl;
        }

        @Override
        public String toString() {
            return new StringBuilder(getClass().getSimpleName())
                    .append(" {")
                    .append("id='").append(id)
                    .append("', secret='").append(secret)
                    .append("', scopes='").append(scopes)
                    .append("', redirectUrl='").append(redirectUrl)
                    .append("'}").toString();
        }

        /**
         * OAuth 2.0 client id.
         */
        public String id;

        /**
         * OAuth 2.0 client secret.
         */
        public String secret;

        /**
         * Space-separated OAuth 2.0 scopes.
         */
        public String scopes;

        /**
         * OAuth 2.0 redirect URL.
         */
        public String redirectUrl;
    }

    private static final String META_KEY_AUTH_CONFIG = "com.htc.cs.identity.auth.config";
    private static final String AUTH_CONFIG_START_TAG = "auth-config";
    private static final String AUTH_CONFIG_CLIENT_TAG = "client";
    private static final String CLIENT_ATTR_ID = "id";
    private static final String CLIENT_ATTR_SECRET = "secret";
    private static final String CLIENT_ATTR_SCOPES = "scopes";
    private static final String CLIENT_ATTR_REDIRECT_URL = "redirectUrl";
    private HtcLogger mLogger = new CommLoggerFactory(this).create();
    private Context mContext;

    /**
     * Construct an instance.
     * 
     * @param context Context to operate on.
     */
    public OAuth2ConfigHelper(Context context) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null or empty.");

        mContext = context;
    }

    /**
     * Get all identity OAuth 2.0 clients declared in the specific package's
     * configuration XML.
     * 
     * @param packageName Name of the package.
     * @return An array of auth clients. Since it's mandatory for authorization,
     *         it throws {@link IllegalStateException} if no valid clients found
     *         to prompt developers fixing the configuration.
     * @throws NameNotFoundException If there's no application with given
     *             package name.
     */
    public AuthClient[] getAuthClients(String packageName) throws NameNotFoundException {
        if (TextUtils.isEmpty(packageName))
            throw new IllegalArgumentException("'packageName' is null or empty.");

        // Find the configuration resource.
        Context packageContext = mContext.createPackageContext(packageName,
                Context.CONTEXT_IGNORE_SECURITY);
        int resId = new ApplicationInfoHelper(mContext).getApplicationMetaData(packageName)
                .getInt(META_KEY_AUTH_CONFIG);

        XmlResourceParser xml;
        try {
            xml = packageContext.getResources().getXml(resId);
        } catch (Resources.NotFoundException e) {
            throw new IllegalStateException(
                    "Not able to find the auth config. Have you declared "
                            + "the following element within <application> element?\n"
                            + "<meta-data\n"
                            + "    android:name=\"" + META_KEY_AUTH_CONFIG + "\"\n"
                            + "    android:resource=\"@xml/your_auth_config_file\" />", e);
        }

        List<AuthClient> clientList = new ArrayList<AuthClient>();
        try {
            // Move to the start tag.
            while (xml.next() != XmlResourceParser.START_TAG)
                ;

            // Find <auth-config>.
            String startTag = xml.getName();
            mLogger.verboseS("startTag=", startTag);
            String configRes = packageContext.getResources().getResourceEntryName(resId);
            if (!AUTH_CONFIG_START_TAG.equals(startTag)) {
                throw new IllegalStateException("The start tag in '@xml/" + configRes +
                        "' must be <" + AUTH_CONFIG_START_TAG + ">");
            }

            // Find all <client>.
            while (xml.next() != XmlResourceParser.END_TAG) {
                String nodeTag = xml.getName();
                mLogger.verboseS("nodeTag=", nodeTag);
                if (AUTH_CONFIG_CLIENT_TAG.equals(nodeTag)) {
                    String id = xml.getAttributeValue(null, CLIENT_ATTR_ID);
                    String secret = xml.getAttributeValue(null, CLIENT_ATTR_SECRET);
                    String scopes = xml.getAttributeValue(null, CLIENT_ATTR_SCOPES);
                    String redirectUrl = xml.getAttributeValue(null, CLIENT_ATTR_REDIRECT_URL);

                    // Check client id.
                    if (TextUtils.isEmpty(id)) {
                        throw new IllegalStateException("The attribute '" + CLIENT_ATTR_ID
                                + "' not found in the element <" + AUTH_CONFIG_CLIENT_TAG
                                + "> or the value is empty. "
                                + "Please check the content of '@xml/" + configRes + "'");
                    }

                    // Check client secret.
                    if (TextUtils.isEmpty(secret)) {
                        mLogger.warning("The attribute '", CLIENT_ATTR_SECRET,
                                "' not found in the element <", AUTH_CONFIG_CLIENT_TAG,
                                "> or the value is empty. ",
                                "The app won't be able to use WebView sign-in ",
                                "if system authenticator is not available.");
                    }

                    // Check scopes.
                    if (TextUtils.isEmpty(scopes)) {
                        mLogger.warning("The attribute '", CLIENT_ATTR_SCOPES,
                                "' not found in the element <", AUTH_CONFIG_CLIENT_TAG,
                                "> or the value is empty. ",
                                "The app will get access tokens with no scopes at all.");
                    }

                    AuthClient client = new AuthClient(id, secret, scopes, redirectUrl);
                    mLogger.debugS("Found client: ", client);
                    clientList.add(client);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (XmlPullParserException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        if (clientList.isEmpty())
            throw new IllegalStateException("No valid client found. Have you declared "
                    + "<" + AUTH_CONFIG_CLIENT_TAG + "> within the element "
                    + "<" + AUTH_CONFIG_START_TAG + ">?");

        AuthClient[] clients = new AuthClient[clientList.size()];
        return clientList.toArray(clients);
    }

    /**
     * Get the default (i.e. the 1st) identity OAuth 2.0 client declared in the
     * local configuration XML.
     * 
     * @return The first client found.
     * @see #getAuthClients()
     */
    public AuthClient getDefaultAuthClient() {
        try {
            return getDefaultAuthClient(mContext.getPackageName());
        } catch (NameNotFoundException e) {
            /*
             * The package name comes from a working context. It will never be
             * not found.
             */
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Get the default (i.e. the 1st) identity OAuth 2.0 client declared in the
     * specific package's configuration XML.
     * 
     * @param packageName Name of the package.
     * @return The first client found.
     * @throws NameNotFoundException If there's no application with given
     *             package name.
     * @see #getAuthClients()
     */
    public AuthClient getDefaultAuthClient(String packageName) throws NameNotFoundException {
        return getAuthClients(packageName)[0];
    }
}
