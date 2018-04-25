package com.htc.libfeedframework.image;

import android.accounts.AuthenticatorDescription;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * A bundle of data containing all the information required to load a specific image.
 * <p>
 * This is used in conjunction with an {@link ExtendedFeedImageHolder} for loading images.
 * </p>
 */
public final class FeedImageData implements Parcelable {

    /**
     * Invalid image target area.
     */
    public static final int AREA_INVALID = 0;

    /**
     * Image target area: background.
     */
    public static final int AREA_BACKGROUND = 100;

    /**
     * Image target area: primary image.
     */
    public static final int AREA_PRIMARY = 101;

    /**
     * Image target area: footer icon.
     */
    public static final int AREA_FOOTER_ICON = 200;

    /**
     * Image target area: avatar portrait.
     */
    public static final int AREA_AVATAR = 300;

    /**
     * Image target area: content area 0.
     */
    public static final int AREA_CONTENT_0 = 400;

    /**
     * Image target area: content area 1.
     */
    public static final int AREA_CONTENT_1 = 401;

    /**
     * Image target area: content area 2.
     */
    public static final int AREA_CONTENT_2 = 402;

    /**
     * Image target area: content area 3.
     */
    public static final int AREA_CONTENT_3 = 403;

    /**
     * Image target area: content area 4.
     */
    public static final int AREA_CONTENT_4 = 404;

    /**
     * Image target area: content area 5.
     */
    public static final int AREA_CONTENT_5 = 405;

    /**
     * Image target area: search. This area is used exclusively by search views.
     */
    public static final int AREA_SEARCH = 500;

    /**
     * Image target area: custom area 1: like icon used by facebook, twitter, etc
     */
    public static final int AREA_CUSTOM_1 = 900;


    /**
     * Image source type: An invalid type if not specified by default.
     */
    public static final int TYPE_INVALID = 0;

    /**
     * Image source type: An icon retrieved by account type from it's {@link AuthenticatorDescription}.
     */
    public static final int TYPE_ACCOUNT_ICON = 100;

    /**
     * Image source type: An installed application's or activity's icon.
     */
    public static final int TYPE_APP_ICON = 101;

    /**
     * Image source type: A contact's photo.
     */
    public static final int TYPE_CONTACT_PHOTO = 102;

    /**
     * Image source type: An image loaded from a content URI via a {@link ContentResolver}.
     */
    public static final int TYPE_CONTENT_URI = 103;

    /**
     * Image source type: A local image file in the device.
     */
    public static final int TYPE_LOCAL_PATH = 104;

    /**
     * Image source type: A remote image file downloaded from a link.
     */
    public static final int TYPE_REMOTE_PATH = 105;

    /**
     * Image source type: An image resource inside an installed package.
     */
    public static final int TYPE_RESOURCE_ID = 106;

    /**
     * Image source type: A video preview thumbnail.
     */
    public static final int TYPE_VIDEO_THUMBNAIL = 107;

    /**
     * Image source type: A zoe video preview thumbnail.
     */
    public static final int TYPE_VIDEO_THUMBNAIL_ZOE = 108;


    private static final String KEY_BUNDLE_BODY = "key-bundle-body";

    private static final String KEY_BUNDLE_EXTRA = "key-bundle-extra";

    private static final String KEY_INT_TYPE = "key-int-type";

    private static final String KEY_INT_AREA = "key-int-area";

    static final String KEY_STRING_ACCOUNT_NAME = "key-string-account-name";

    static final String KEY_STRING_PACKAGE_NAME = "key-string-package-name";

    static final String KEY_STRING_ACTIVITY_NAME = "key-string-activity-name";

    static final String KEY_STRING_LOCAL_PATH = "key-string-local-path";

    static final String KEY_STRING_REMOTE_PATH = "key-string-remote-path";

    static final String KEY_STRING_VIDEO_THUMBNAIL_PATH = "key-string-video-thumbnail-path";

    static final String KEY_PARCELABLE_URI_CONTACT_PHOTO = "key-parcelable-uri-contact-photo";

    static final String KEY_PARCELABLE_URI_CONTENT = "key-parcelable-uri-content";

    static final String KEY_PARCELABLE_URI_PACKAGE_RESOURCE = "key-parcelable-uri-package-resource";

    /**
     * The http uri scheme
     */
    private static final String URI_SCHEME_HTTP = "http";

    /**
     * The https uri scheme
     */
    private static final String URI_SCHEME_HTTPS = "https";


    public static final int BUILD_VERSION_CODE = 1;

    /**
     * Log tag for printing logs.
     */
    private static final String LOG_TAG = FeedImageData.class.getSimpleName() + "_" + BUILD_VERSION_CODE;

    private final int mVersionCode;

    /**
     * A {@link Bundle} to hold standard data that most feed items should contain, e.g., footer text and primary text.
     */
    private final Bundle mBody;

    /**
     * A {@link Bundle} to hold extra data.
     */
    private final Bundle mExtra;

    public static final Parcelable.Creator<FeedImageData> CREATOR = new Parcelable.Creator<FeedImageData>() {

        @Override
        public FeedImageData createFromParcel(Parcel parcelIn) {
            int versionCode = 0;
            FeedImageData feedImageData = null;
            Bundle bundleBody = null;
            Bundle bundleExtra = null;
            try {
                versionCode = parcelIn.readInt();
                if (versionCode <= 0) {
                    Log.d(LOG_TAG, String.format("invalid data version code %d", versionCode));
                    return null;
                }
                //  TODO: Add version compatibility check here, if not compatible, we may have to add non-compatible-dummy data.
            } catch (Exception e) {
                return null;
            }
            try {
                bundleBody = parcelIn.readBundle(FeedImageData.class.getClassLoader());
            } catch (Exception e) {
                Log.d(LOG_TAG, "invalid data body while read from parcel");
                return null;
            }
            try {
                bundleExtra = parcelIn.readBundle(FeedImageData.class.getClassLoader());
            } catch (Exception e) {
                Log.d(LOG_TAG, "invalid data extra while read from parcel");
            }

            try {
                feedImageData = new FeedImageData(versionCode, bundleBody, bundleExtra);
            } catch (Exception e) {
                Log.d(LOG_TAG, "Exception while read bundle of body", e);
            }
            return feedImageData;
        }

        @Override
        public FeedImageData[] newArray(int size) {
            return new FeedImageData[size];
        }

    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        try {
            dest.writeInt(mVersionCode);
            dest.writeBundle(mBody);
            dest.writeBundle(mExtra);
        } catch (Exception e) {
            Log.d(LOG_TAG, String.format("Exception while write to parcel"), e);
        }
    }

    private FeedImageData() {
        this(BUILD_VERSION_CODE, new Bundle(), new Bundle());
    }

    /**
     * Constructor for a FeedImageData.
     *
     * @param area the target area to set this image after loading
     */
    private FeedImageData(int versionCode, Bundle body, Bundle extra) {
        mVersionCode = versionCode;
        if (body == null) {
            body = new Bundle();
        }
        mBody = body;

        if (extra == null) {
            extra = new Bundle();
        } else {
            try {
                //  force unmarshall
                extra.getInt(KEY_BUNDLE_EXTRA);
            } catch (Exception e) {
                extra = new Bundle();
            }
        }
        mExtra = extra;
    }

    public final int getVersionCode() {
        return mVersionCode;
    }

    public final void setType(int type) {
        putInt(KEY_INT_TYPE, type);
    }

    /**
     * Returns the type of this image
     *
     * @return the the type of this image
     */
    public final int getType() {
        return getInt(KEY_INT_TYPE, TYPE_INVALID);
    }

    public final void setArea(int area) {
        putInt(KEY_INT_AREA, area);
    }

    /**
     * Returns the target area to set this image after loading.
     *
     * @return the target area to set this image after loading
     */
    public final int getArea() {
        return getInt(KEY_INT_AREA, AREA_INVALID);
    }

    public final int getInt(String key, int defaultValue) {
        return mBody.getInt(key, defaultValue);
    }

    public final void putInt(String key, int value) {
        mBody.putInt(key, value);
    }

    public final long getLong(String key, long defaultValue) {
        return mBody.getLong(key, defaultValue);
    }

    public final void putLong(String key, long value) {
        mBody.putLong(key, value);
    }

    public final float getFloat(String key, float defaultValue) {
        return mBody.getFloat(key, defaultValue);
    }

    public final void putFloat(String key, float value) {
        mBody.putFloat(key, value);
    }

    public final double getDouble(String key, double defaultValue) {
        return mBody.getDouble(key, defaultValue);
    }

    public final void putDouble(String key, double value) {
        mBody.putDouble(key, value);
    }

    public final boolean getBoolean(String key, boolean defaultValue) {
        return mBody.getBoolean(key, defaultValue);
    }

    public final void putBoolean(String key, boolean value) {
        mBody.putBoolean(key, value);
    }

    public final CharSequence getCharSequence(String key, CharSequence defaultValue) {
        return mBody.getCharSequence(key, defaultValue);
    }

    public final void putCharSequence(String key, CharSequence value) {
        mBody.putCharSequence(key, value);
    }

    public final String getString(String key) {
        return mBody.getString(key);
    }

    public final String getString(String key, String defaultValue) {
        return mBody.getString(key, defaultValue);
    }

    public final void putString(String key, String value) {
        mBody.putString(key, value);
    }

    private final void putParcelable(String key, Parcelable value) {
        mBody.putParcelable(key, value);
    }

    public final Parcelable getParcelable(String key) {
        return mBody.getParcelable(key);
    }

    public final void putExtraParcelable(String key, Parcelable value) {
        mExtra.putParcelable(key, value);
    }

    public final Parcelable getExtraParcelable(String key) {
        return mExtra.getParcelable(key);
    }


    /**
     * Creates a {@link FeedImageData} containing all the information required to load the icon from an account type's
     * {@link AuthenticatorDescription}.
     *
     * @param area        the target area to set this image after loading (example: {@link #AREA_FOOTER_ICON})
     * @param accountType the name of the account registered in Android's accounts
     * @return a FeedImageData with all the information to load an account icon
     */
    public static FeedImageData createAccountIconImageData(int area, String accountType) {
        FeedImageData feedImageData = new FeedImageData();
        feedImageData.setType(TYPE_ACCOUNT_ICON);
        feedImageData.setArea(area);
        feedImageData.putString(KEY_STRING_ACCOUNT_NAME, accountType);

        return feedImageData;
    }

    /**
     * Creates a {@link FeedImageData} containing all the information required to load an activity's icon.
     *
     * @param area         the target area to set this image after loading (example: {@link #AREA_FOOTER_ICON})
     * @param packageName  the name of the activity's package (example: com.htc.launcher)
     * @param activityName the full name of the activity (example: com.htc.launcher.Launcher)
     * @return a FeedImageData with all the information to load an activity's icon
     * @see #createApplicationIconImageData(int, String)
     */
    public static FeedImageData createActivityIconImageData(int area, String packageName, String activityName) {
        FeedImageData feedImageData = new FeedImageData();
        feedImageData.setType(TYPE_APP_ICON);
        feedImageData.setArea(area);
        feedImageData.putString(KEY_STRING_PACKAGE_NAME, packageName);
        feedImageData.putString(KEY_STRING_ACTIVITY_NAME, activityName);

        return feedImageData;
    }

    /**
     * Creates a {@link FeedImageData} containing all the information required to load an application's icon.
     *
     * @param area        the target area to set this image after loading (example: {@link #AREA_FOOTER_ICON})
     * @param packageName the application's package name (example: com.htc.launcher)
     * @return a FeedImageData with all the information to load an application's icon
     * @see #createActivityIconImageData(int, String, String)
     */
    public static FeedImageData createApplicationIconImageData(int area, String packageName) {
        return createActivityIconImageData(area, packageName, null);
    }

    /**
     * Creates a {@link FeedImageData} containing all the information required to load a contact's photo.
     *
     * @param area the target area to set this image after loading (example: {@link #AREA_AVATAR})
     * @param uri  the content URI of the contact's image resource
     * @return a FeedImageData with all the information to load a contact's photo
     */
    public static FeedImageData createContactPhotoImageData(int area, Uri uri) {
        FeedImageData feedImageData = new FeedImageData();
        feedImageData.setType(TYPE_CONTACT_PHOTO);
        feedImageData.setArea(area);
        feedImageData.putParcelable(KEY_PARCELABLE_URI_CONTACT_PHOTO, uri);

        return feedImageData;
    }

    /**
     * Creates a {@link FeedImageData} containing all the information required to load an image from a content URI via a
     * {@link ContentResolver}.
     *
     * @param area the target area to set this image after loading (example: {@link #AREA_BACKGROUND})
     * @param uri  the content URI of the image resource
     * @return a FeedImageData with all the information to load a specific content URI image
     */
    public static FeedImageData createContentUriImageData(int area, Uri uri) {
        FeedImageData feedImageData = new FeedImageData();
        feedImageData.setType(TYPE_CONTENT_URI);
        feedImageData.setArea(area);
        feedImageData.putParcelable(KEY_PARCELABLE_URI_CONTENT, uri);

        return feedImageData;
    }

    /**
     * @param area the target area to set this image after loading (example: {@link #AREA_BACKGROUND})
     * @param uri  the uri of the image resource.
     * @return
     */
    public static FeedImageData createImageDataFromUri(int area, Uri uri) {
        final String uriScheme = (uri == null ? null : uri.getScheme());
        if (URI_SCHEME_HTTP.equals(uriScheme) || URI_SCHEME_HTTPS.equals(uriScheme)) {
            return createRemotePathImageData(area, uri.toString());
        } else if (ContentResolver.SCHEME_FILE.equals(uriScheme)) {
            return createLocalPathImageData(area, uri.getPath());
        } else if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(uriScheme)) {
            return createPackageResourceImageData(area, uri);
        } else if (ContentResolver.SCHEME_CONTENT.equals(uriScheme)) {
            return createContentUriImageData(area, uri);
        } else {
            Log.w(LOG_TAG, String.format("Unknown uri: %s", uri));
            return null;
        }
    }

    /**
     * Creates a {@link FeedImageData} containing all the information required to load a local image file.
     *
     * @param area the target area to set this image after loading (example: {@link #AREA_BACKGROUND})
     * @param url  the file path of the image
     * @return a FeedImageData with all the information to load the local image file
     */
    public static FeedImageData createLocalPathImageData(int area, String url) {
        FeedImageData feedImageData = new FeedImageData();
        feedImageData.setType(TYPE_LOCAL_PATH);
        feedImageData.setArea(area);
        feedImageData.putString(KEY_STRING_LOCAL_PATH, url);

        return feedImageData;
    }

    /**
     * Creates a {@link FeedImageData} containing all the information required to load a remote image file from a link.
     *
     * @param area the target area to set this image after loading (example: {@link #AREA_BACKGROUND})
     * @param url  the link to download the image from
     * @return a FeedImageData with all the information to load the remote image file
     */
    public static FeedImageData createRemotePathImageData(int area, String url) {
        FeedImageData feedImageData = new FeedImageData();
        feedImageData.setType(TYPE_REMOTE_PATH);
        feedImageData.setArea(area);
        feedImageData.putString(KEY_STRING_REMOTE_PATH, url);

        return feedImageData;
    }

    /**
     * Creates a {@link FeedImageData} containing all the information required to load an image resource from a package.
     *
     * @param area        the target area to set this image after loading (example: {@link #AREA_BACKGROUND})
     * @param packageName the name of the package to load the resource from
     * @param resourceId  the resource ID of the resource to load
     * @return a FeedImageData with all the information to load a specific package resource
     */
    public static FeedImageData createPackageResourceImageData(int area, String packageName, int resourceId) {
        Uri mUri = Uri.EMPTY;
        try {
            mUri = Uri.parse(String.format("%s://%s/%d", ContentResolver.SCHEME_ANDROID_RESOURCE, packageName, resourceId));
        } catch (Exception e) {
            Log.w(LOG_TAG, String.format("Exception while parse Uri from %d %s", resourceId, packageName), e);
            mUri = Uri.EMPTY;
        }
        return createPackageResourceImageData(area, mUri);
    }

    /**
     * Creates a {@link FeedImageData} containing all the information required to load an image resource from a package.
     *
     * @param area the target area to set this image after loading (example: {@link #AREA_BACKGROUND})
     * @param uri  the uri
     * @return a FeedImageData with all the information to load a specific package resource
     */
    public static FeedImageData createPackageResourceImageData(int area, Uri uri) {
        FeedImageData feedImageData = new FeedImageData();
        feedImageData.setType(TYPE_RESOURCE_ID);
        feedImageData.setArea(area);
        feedImageData.putParcelable(KEY_PARCELABLE_URI_PACKAGE_RESOURCE, uri);

        return feedImageData;
    }


    /**
     * Creates a {@link FeedImageData} containing all the information required to create a video preview thumbnail
     * image.
     *
     * @param area          the target area to set this image after loading (example: {@link #AREA_BACKGROUND})
     * @param videoFilePath the file path of the video
     * @return a FeedImageData with all the information to create a video preview thumbnail
     */
    public static FeedImageData createVideoThumbnailImageData(int area, String videoFilePath) {
        return createVideoThumbnailImageData(area, videoFilePath, false);
    }

    /**
     * Creates a {@link FeedImageData} containing all the information required to create a video preview thumbnail
     * image.
     *
     * @param area          the target area to set this image after loading (example: {@link #AREA_BACKGROUND})
     * @param videoFilePath the file path of the video
     * @param isZoe         true if the image is for Zoe video
     * @return a FeedImageData with all the information to create a video preview thumbnail
     */
    public static FeedImageData createVideoThumbnailImageData(int area, String videoFilePath, boolean isZoe) {
        FeedImageData feedImageData = new FeedImageData();
        if (isZoe) {
            feedImageData.setType(TYPE_VIDEO_THUMBNAIL_ZOE);
        } else {
            feedImageData.setType(TYPE_VIDEO_THUMBNAIL);
        }
        feedImageData.setArea(area);
        feedImageData.putString(KEY_STRING_VIDEO_THUMBNAIL_PATH, videoFilePath);

        return feedImageData;
    }
}
