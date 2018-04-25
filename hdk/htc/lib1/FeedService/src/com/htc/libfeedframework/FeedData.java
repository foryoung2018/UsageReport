package com.htc.libfeedframework;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.renderscript.RenderScript.Priority;
import android.util.Log;

import com.htc.libfeedframework.image.FeedImageData;

import java.util.ArrayList;
import java.util.List;

public class FeedData implements Parcelable {

    public static class ViewType {

        /**
         * Undefined FeedView type
         */
        public static final int UNDEFINED = 0;

        /**
         * FeedView with content text and footer
         */
        public static final int TEXT_ONLY = 100;

        /**
         * FeedView with background image and footer
         */
        public static final int IMAGE_ONLY = 101;

        /**
         * FeedView with background image only (no footer)
         */
        public static final int IMAGE_ONLY_NO_FOOTER = 102;

        /**
         * FeedView with background image, content text, and footer
         */
        public static final int IMAGE_WITH_TEXT = 103;

        /**
         * FeedView with background image, caption text, and footer
         */
        public static final int IMAGE_WITH_CAPTION = 104;

        /**
         * FeedView with small image, content text and footer
         */
        public static final int SMALL_IMAGE = 105;

        /**
         * FeedView with fixed ratio image, content text and footer
         */
        public static final int FIXED_RATIO_IMAGE = 106;

        /**
         * FeedView with avatar, content text and footer
         */
        public static final int TEXT_WITH_AVATAR = 107;

        /**
         * FeedView with avatar, caption text and footer
         */
        public static final int IMAGE_WITH_CAPTION_AND_AVATAR = 108;

        /**
         * FeedView with small image, content text, footer and avatar
         */
        public static final int SMALL_IAMGE_WITH_AVATAR = 109;

        /**
         * A bundle of FeedView
         */
        public static final int BUNDLE_VIEW = 110;

        /**
         * FeedView with calendar event
         */
        public static final int EVENT_VIEW = 111;

        /**
         * FeedView with morning bundle
         */
        public static final int MORNING_BUNDLE_VIEW = 112;

        /**
         * FeedView with meal time bundle
         */
        public static final int MEALTIME_BUNDLE_VIEW = 1985;

        /**
         * FeedView with meal time recommend
         */
        public static final int MEALTIME_RECOMMEND_VIEW = 113;

        /**
         * FeedView with TellMeMore tile
         */
        public static final int TELL_ME_MORE_VIEW = 114;

        /**
         * FeedView with Service & App recommend tile
         */
        public static final int SERVICE_APP_RECOMMEND_VIEW = 115;

        /**
         * FeedView with Partner Bundle tile
         */
        public static final int PARTNER_BUNDLE_VIEW = 116;

        /**
         * FeedView with avatar, caption text and footer (GIF)
         */
        public static final int GIF_WITH_CAPTION_AND_AVATAR = 117;

        /**
         * Inline Video view
         */
        public static final int VIDEO_PREVIEW = 118;

        /**
         * FeedView with Partner Bundle tile
         */
        public static final int PARTNER_TILE_VIEW = 120;

        /**
         * FeedView with Oobe Partner tile
         */
        public static final int OOBE_PARTNER_TILE_VIEW = 121;

        /**
         * FeedView with Promotion Partner
         */
        public static final int PROMOTION_PARTNER_VIEW = 122;
    }

    public static final class Constants {

        /**
         * Placed above all other priority levels. The most significant feed items should use this priority.
         */
        public static final int PRIORITY_TOPMOST = 3;

        /**
         * Placed after feed items of {@link #TOPMOST} priority.
         */
        public static final int PRIORITY_DAILY = 2;

        /**
         * A priority level higher than normal.
         */
        public static final int PRIORITY_HIGHER = 1;

        /**
         * The normal priority level used for generic feed items.
         */
        public static final int PRIORITY_GENERIC = 0;

        /**
         * A priority level lower than normal.
         */
        public static final int PRIORITY_LOWER = -1;


        /**
         * Metadata describes the contents of a feed item.
         */
        /**
         * Default flag indicating that the feed item contains no data.
         */
        public static final int META_NONE = 0x00000000;

        /**
         * Flag indicating that the feed item contains text.
         */
        public static final int META_TEXT = 0x00000001;

        /**
         * Flag indicating that the feed item contains an avatar image.
         */
        public static final int META_AVATAR = 0x00000002;

        /**
         * Flag indicating that the feed item contains image(s).
         */
        public static final int META_IMAGE = 0x0000004;

        /**
         * Flag indicating that the feed item contains a video.
         */
        public static final int META_VIDEO = 0x00000008;

        /**
         * Flag indicating that the feed item contains a video preview.
         */
        public static final int META_VIDEO_PREVIEW = 0x00000010;

        /**
         * Flag indicating that the feed item favor largest view
         */
        public static final int META_VIEW_LARGEST = 0x00010000;


        /**
         * Quality grade of the feed item's content.
         * <p>
         * Valid values:
         * <ul>
         * <li>{@link FeedData#CONTENT_QUALITY_VERY_HIGH}
         * <li>{@link FeedData#CONTENT_QUALITY_HIGH}
         * <li>{@link FeedData#CONTENT_QUALITY_MEDIUM}
         * <li>{@link FeedData#CONTENT_QUALITY_LOW}
         * <li>{@link FeedData#CONTENT_QUALITY_UNDEFINED}
         * </ul>
         * </p>
         */
        /**
         * Feed item's content quality is not defined.
         */
        public static final int CONTENT_QUALITY_UNDEFINED = 0;

        /**
         * Feed item contains low quality content.
         */
        public static final int CONTENT_QUALITY_LOW = 1;

        /**
         * Feed item contains medium quality content.
         */
        public static final int CONTENT_QUALITY_MEDIUM = 2;

        /**
         * Feed item contains high quality content.
         */
        public static final int CONTENT_QUALITY_HIGH = 3;

        /**
         * Feed item contains very high quality content.
         */
        public static final int CONTENT_QUALITY_VERY_HIGH = 4;


        /**
         * Indicates that the {@link #clickActionIntent} should be used as a broadcast.
         */
        public static final int CLICK_ACTION_BROADCASTINTENT = 1;

        /**
         * Indicates that the {@link #clickActionIntent} should be used to start an activity.
         */
        public static final int CLICK_ACTION_STARTACTIVITY = 0;

    }

    private static final String KEY_BUNDLE_BODY = "key-bundle-body";

    private static final String KEY_BUNDLE_EXTRA = "key-bundle-extra";

    private final static String KEY_LONG_ID = "key-long-id";

    /**
     * Metadata flags describing the contents of this feed item.
     */
    private final static String KEY_INT_META_FLAGS = "key-int-meta-flags";

    private final static String KEY_INT_PRIORITY = "key-int-priority";

    private final static String KEY_INT_VIEW_TYPE = "key-int-view-type";

    private static final String KEY_INT_CONTENT_QUALITY = "key-int-content-quality";

    private final static String KEY_LONG_TIMESTAMP = "key-long-timestamp";

    private final static String KEY_LONG_EXPIRED_TIMESTAMP = "key-long-expired-timestamp";

    private final static String KEY_BOOLEAN_USE_CUSTOM_VIEW = "key-boolean-use-custom-view-type";

    public final static String KEY_EXTRA_INT_IMAGE_WIDTH = "key-extra-int-image-width";

    public final static String KEY_EXTRA_INT_IMAGE_HEIGHT = "key-extra-int-image-height";

    public final static String KEY_EXTRA_BOOLEAN_IS_ZAWGYI = "key-extra-boolean-is-zawgyi";

    public final static String KEY_EXTRA_BOOLEAN_IMAGE_FIT_CENTER = "key-extra-boolean-image-fit-center";

    /**
     * Intent to run when this feed item is clicked.
     */
    private final static String KEY_PARCELABLE_INTENT = "key-parcelable-intent";

    private final static String KEY_PARCELABLEARRAYLIST_FEEDIMAGEDATA = "key-parcelablearraylist-feed-image-data";


    /**
     * The primary text of a feed item. TYPE: CharSequence.
     */
    public final static String KEY_TEXT_PRIMARY = "key-text-primary";

    /**
     * The secondary text of a feed item. TYPE: CharSequence.
     */
    public final static String KEY_TEXT_SECONDARY = "key-text-secondary";

    /**
     * The footer text of a feed item. TYPE: CharSequence.
     */
    public final static String KEY_TEXT_FOOTER = "key-text-footer";

    /**
     * The footer secondary text
     */
    public final static String KEY_TEXT_FOOTER_SECONDARY = "key-text-footer-secondary";

    public final static int BUILD_VERSION_CODE = 1;

    private final static String LOG_TAG = FeedData.class.getSimpleName() + "_" + BUILD_VERSION_CODE;

    private final int mVersionCode;

    /**
     * A {@link Bundle} to hold standard data that most feed items should contain, e.g., footer text and primary text.
     */
    private final Bundle mBody;

    /**
     * A {@link Bundle} to hold extra data.
     */
    private final Bundle mExtra;

    private final ArrayList<FeedImageData> mFeedImageDataList = new ArrayList<FeedImageData>(DEFAULT_FEEDIMAGEDATA_LIST_SIZE);

    /**
     * An extras key describing the behavior of the {@link #clickActionIntent}.
     * <p>
     * Valid values: {@link FeedData#CLICK_ACTION_STARTACTIVITY}, {@link FeedData#CLICK_ACTION_BROADCASTINTENT}
     * </p>
     * <p>
     * Use in conjunction with {@link #putIntExtra(String, int)} and {@link #getIntExtra(String, int)}.
     * </p>
     */
    public static final String CLICK_ACTION_KEY = "clickActionIntentType";

    private static final int DEFAULT_FEEDIMAGEDATA_LIST_SIZE = 3;

    public static final Parcelable.Creator<FeedData> CREATOR = new Parcelable.Creator<FeedData>() {
        public FeedData createFromParcel(Parcel parcelIn) {
            int versionCode = 0;
            FeedData feedData = null;
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
                bundleBody = parcelIn.readBundle(FeedData.class.getClassLoader());
            } catch (Exception e) {
                Log.d(LOG_TAG, "invalid data body while read from parcel");
                return null;
            }
            try {
                bundleExtra = parcelIn.readBundle(FeedData.class.getClassLoader());
            } catch (Exception e) {
                Log.d(LOG_TAG, "invalid data extra while read from parcel");
            }

            try {
                feedData = new FeedData(versionCode, bundleBody, bundleExtra);
            } catch (Exception e) {
                Log.d(LOG_TAG, "Exception while read bundle of body", e);
            }
            return feedData;
        }

        public FeedData[] newArray(int size) {
            return new FeedData[size];
        }
    };

    @Override
    public final int describeContents() {
        return 0;
    }

    @Override
    public final void writeToParcel(Parcel parcelOut, int describeContents) {
        try {
            synchronized (mBody) {
                mBody.putParcelableArrayList(KEY_PARCELABLEARRAYLIST_FEEDIMAGEDATA, mFeedImageDataList);
            }
            parcelOut.writeInt(BUILD_VERSION_CODE);
            parcelOut.writeBundle(mBody);
            parcelOut.writeBundle(mExtra);
        } catch (Exception e) {
            Log.d(LOG_TAG, String.format("Exception while write to parcel"), e);
        }
    }

    public FeedData() {
        mVersionCode = BUILD_VERSION_CODE;
        mBody = new Bundle();
        mExtra = new Bundle();
    }

    public FeedData(long id) {
        this();
        mBody.putLong(KEY_LONG_ID, id);
    }

    private FeedData(int versionCode, Bundle body, Bundle extra) {
        mVersionCode = versionCode;
        if (body == null) {
            body = new Bundle();
        } else {
            try {
                body.setClassLoader(FeedData.class.getClassLoader());
                ArrayList<Parcelable> parcelableList = body.getParcelableArrayList(KEY_PARCELABLEARRAYLIST_FEEDIMAGEDATA);
                if (parcelableList != null) {
                    for (Parcelable parcelable : parcelableList) {
                        if (parcelable instanceof FeedImageData) {
                            mFeedImageDataList.add((FeedImageData) parcelable);
                        }
                    }
                }
            } catch (Exception e) {
                Log.d(LOG_TAG, String.format("Exception while extract FeedImageData from body bundle"), e);
            }
        }
        mBody = body;

        if (extra == null) {
            extra = new Bundle();
        } else {
            try {
                //  force unmarshall
                extra.getInt(KEY_BUNDLE_EXTRA);
            } catch (Exception e) {
                Log.d(LOG_TAG, String.format("Exception while force unmarshall from extra bundle"), e);
                extra = new Bundle();
            }
        }
        mExtra = extra;
    }

    public final int getVersionCode() {
        return mVersionCode;
    }

    /**
     * Retrieves the ID of this feed item. Each ID must be unique within a feed adapter.
     *
     * @return the ID of this feed item.
     */
    public final long getId() {
        return getLong(KEY_LONG_ID, 0);
    }

    public final void setId(long id) {
        putLong(KEY_LONG_ID, id);
    }

    /**
     * Retrieves the {@link FeedData.Priority} level of this feed item.
     * <p>
     * Priority levels available:
     * <ul>
     * <li>{@link Priority#TOPMOST TOPMOST}</li>
     * <li>{@link Priority#DAILY DAILY}</li>
     * <li>{@link Priority#GENERIC GENERIC}</li>
     * </ul>
     * </p>
     *
     * @return the Priority of this feed item.
     */
    public final int getPriority() {
        return getInt(KEY_INT_PRIORITY, FeedData.Constants.PRIORITY_GENERIC);
    }

    public final void setPriority(int priority) {
        putInt(KEY_INT_PRIORITY, priority);
    }

    /**
     * Determines whether or not this feed item matches a given pattern.
     * <p>
     * This is used for searching feeds.
     * </p>
     *
     * @param pattern the string to search for in this feed item
     * @return true if this item matches the pattern, false otherwise.
     */
    public boolean isMatch(String pattern) {
        return false;
    }


    /**
     * Returns the quality grade of the feed item's contents.
     * <p>
     * Values:
     * <ul>
     * <li>{@link FeedData#CONTENT_QUALITY_VERY_HIGH}
     * <li>{@link FeedData#CONTENT_QUALITY_HIGH}
     * <li>{@link FeedData#CONTENT_QUALITY_MEDIUM}
     * <li>{@link FeedData#CONTENT_QUALITY_LOW}
     * <li>{@link FeedData#CONTENT_QUALITY_UNDEFINED}
     * </ul>
     * </p>
     *
     * @return the quality grade of the feed item's contents
     */
    public final int getContentQuality() {
        return getInt(KEY_INT_CONTENT_QUALITY, FeedData.Constants.CONTENT_QUALITY_UNDEFINED);
    }

    public final void setContentQuality(int contentQuality) {
        putInt(KEY_INT_CONTENT_QUALITY, contentQuality);
    }


    /**
     * Returns the Intent to run when this feed item is clicked.
     *
     * @param options extra options; or null
     * @return the Intent to run when this feed item is clicked
     */
    public Intent getClickIntent(Bundle optionsIn) {
        Parcelable parcelable = getParcelable(KEY_PARCELABLE_INTENT);
        Intent intent = null;
        if (parcelable instanceof Intent) {
            intent = (Intent) parcelable;
        }
        return intent;
    }

    public void setClickIntent(Intent intent) {
        putParcelable(KEY_PARCELABLE_INTENT, intent);
    }

    /**
     * Retrieves the text of the given type from this feed item.
     *
     * @param type         the type of feed item text to retrieve (examples: {@link FeedData#TEXT_FOOTER},
     *                     {@link FeedData#TEXT_PRIMARY}, {@link FeedData#TEXT_SECONDARY})
     * @param defaultValue value to return if the type does not exist
     * @return the text of the given type; or the defaultValue if the type does not exist.
     */
    public final CharSequence getText(String type, CharSequence defaultValue) {
        return getCharSequence(type, defaultValue);
    }

    /**
     * Sets the text for the given type to this feed item.
     *
     * @param type  the type of feed item text to set (examples: {@link FeedData#TEXT_FOOTER},
     *              {@link FeedData#TEXT_PRIMARY}, {@link FeedData#TEXT_SECONDARY})
     * @param value the text string to set
     */
    public final void setText(String type, CharSequence value) {
        putCharSequence(type, value);
    }

    public final long getTimestamp() {
        return getLong(KEY_LONG_TIMESTAMP, 0);
    }

    public final void setTimestamp(long timestamp) {
        putLong(KEY_LONG_TIMESTAMP, timestamp);
    }

    public final long getExpiredTimestamp() {
        return getLong(KEY_LONG_EXPIRED_TIMESTAMP, 0);
    }

    public final void setExpiredTimestamp(long timestamp) {
        putLong(KEY_LONG_EXPIRED_TIMESTAMP, timestamp);
    }

    /**
     * Retrieves the view type ID of this feed item.
     * <p>
     * If this feed item uses a standard view type, this method should return one of the following:
     * <ul>
     * <li>{@link IFeedView#TYPE_TEXT_ONLY}</li>
     * <li>{@link IFeedView#TYPE_IMAGE_ONLY}</li>
     * <li>{@link IFeedView#TYPE_IMAGE_ONLY_NO_FOOTER}</li>
     * <li>{@link IFeedView#TYPE_IMAGE_WITH_CAPTION}</li>
     * <li>{@link IFeedView#TYPE_IMAGE_WITH_TEXT}</li>
     * <li>{@link IFeedView#TYPE_TEXT_WITH_CONTENT_IMAGE}</li>
     * </ul>
     * </p>
     * <p>
     * If this feed item uses a custom view type, the view type ID is defined by the provider.
     * </p>
     *
     * @param cellSpanX the horizontal cell span of the target cell
     * @param cellSpanY the vertical cell span of the target cell
     * @return the ID of the desired view type for this feed item.
     */
    public int getViewType(int cellSpanX, int cellSpanY) {
        return getInt(KEY_INT_VIEW_TYPE, FeedData.ViewType.UNDEFINED);
    }

    public void setViewType(int viewType) {
        putInt(KEY_INT_VIEW_TYPE, viewType);
    }

    /**
     * Declares whether or not this feed item will use a custom view type instead of standard view types defined in the
     * framework.
     *
     * @return true if this feed item uses a custom view type; false if it uses a standard type.
     */
    public final boolean usesCustomView() {
        return getBoolean(KEY_BOOLEAN_USE_CUSTOM_VIEW, false);
    }

    public final void setUseCustomView(boolean useCustomView) {
        putBoolean(KEY_BOOLEAN_USE_CUSTOM_VIEW, useCustomView);
    }

    /**
     * Add a {@link FeedImageData} to this feed item.
     * <p>
     * The image data contains all the information required to load the image. Each feed item can contain multiple
     * FeedImageDatas, e.g., a footer icon and a background image.
     * </p>
     *
     * @param imageData the FeedImageData to add
     */
    public final void addImageData(FeedImageData imageData) {
        if (imageData != null) {
            mFeedImageDataList.add(imageData);
        }
    }

    /**
     * Retrieves the {@link FeedImageData}s of all the images to be loaded for this feed item.
     *
     * @return a list of FeedImageDatas containing information on how to load each image.
     */
    public final List<FeedImageData> getImageData() {
        return (List<FeedImageData>) mFeedImageDataList.clone();
    }

    /**
     * Returns the FeedImageData for the specified area.
     *
     * @param area the area to look for
     * @return the {@link FeedImageData} for the specified area, or null if none were found
     */
    public final FeedImageData getImageDataForArea(int area) {
        for (FeedImageData imageData : mFeedImageDataList) {
            if (imageData.getArea() == area) {
                return imageData;
            }
        }
        return null;
    }

    /**
     * Remove a {@link FeedImageData} from this feed item.
     *
     * @param imageData the FeedImageData to remove
     */
    public final void removeImageData(FeedImageData imageData) {
        mFeedImageDataList.remove(imageData);
    }

    /**
     * Clears all the {@link FeedImageData}s from this feed item.
     */
    public final void clearImageData() {
        mFeedImageDataList.clear();
    }

    private final boolean getBoolean(String key, boolean defaultValue) {
        synchronized (mBody) {
            return mBody.getBoolean(key, defaultValue);
        }
    }

    private final void putBoolean(String key, boolean value) {
        synchronized (mBody) {
            mBody.putBoolean(key, value);
        }
    }

    private final int getInt(String key, int defaultValue) {
        synchronized (mBody) {
            return mBody.getInt(key, defaultValue);
        }
    }

    private final void putInt(String key, int value) {
        synchronized (mBody) {
            mBody.putInt(key, value);
        }
    }

    private final long getLong(String key, long defaultValue) {
        synchronized (mBody) {
            return mBody.getLong(key, defaultValue);
        }
    }

    private final void putLong(String key, long value) {
        synchronized (mBody) {
            mBody.putLong(key, value);
        }
    }

    private final CharSequence getCharSequence(String key, CharSequence defaultValue) {
        synchronized (mBody) {
            return mBody.getCharSequence(key, defaultValue);
        }
    }

    private final void putCharSequence(String key, CharSequence defaultValue) {
        synchronized (mBody) {
            mBody.putCharSequence(key, defaultValue);
        }
    }

    private final Parcelable getParcelable(String key) {
        synchronized (mBody) {
            return mBody.getParcelable(key);
        }
    }

    private final void putParcelable(String key, Parcelable parcelable) {
        synchronized (mBody) {
            mBody.putParcelable(key, parcelable);
        }
    }

    /**
     * Retrieves this feed item's metadata flags.
     *
     * @return flags describing the feed item
     */
    public final int getMetaFlags() {
        return getInt(KEY_INT_META_FLAGS, FeedData.Constants.META_NONE);
    }

    /**
     * Appends a metadata flag to the existing flags for this feed item.
     * <p>
     * Available flags:
     * <ul>
     * <li>{@link Meta#TEXT}
     * <li>{@link Meta#AVATAR}
     * <li>{@link Meta#IMAGE}
     * <li>{@link Meta#VIDEO}
     * </ul>
     * </p>
     *
     * @param flags the metadata flag to add
     */
    public final void appendMetaFlags(int flags) {
        int metaFlags = getInt(KEY_INT_META_FLAGS, 0);
        putInt(KEY_INT_META_FLAGS, metaFlags |= flags);
    }

    /**
     * Checks whether this feed item contains the specified metadata flag.
     *
     * @param flag the metadata flag to check for
     * @return true if this feed item contains the metadata flag; false otherwise.
     */
    public final boolean containsMetaFlag(int flag) {
        int metaFlags = getInt(KEY_INT_META_FLAGS, Constants.META_NONE);
        return (metaFlags & flag) != 0;
    }

    /**
     * Remove a metadata flag from this feed item.
     *
     * @param flags the metadata flag to remove
     */
    public final void removeMetaFlags(int flags) {
        int metaFlags = getInt(KEY_INT_META_FLAGS, FeedData.Constants.META_NONE);
        metaFlags &= ~flags;
        putInt(KEY_INT_META_FLAGS, metaFlags);
    }


    /**
     * Clears all metadata flags from this feed item.
     */
    public final void clearMetaFlags() {
        putInt(KEY_INT_META_FLAGS, FeedData.Constants.META_NONE);
    }

    /**
     * Retrieves the boolean value associated with the given key.
     *
     * @param key          the key field to retrieve from
     * @param defaultValue value to return if key does not exist
     * @return the boolean associated with the key; or the defaultValue if the key does not exist.
     */
    public final boolean getBooleanExtra(String key, boolean defaultValue) {
        synchronized (mExtra) {
            return mExtra.getBoolean(key, defaultValue);
        }
    }

    /**
     * Inserts a boolean value into the extra fields mapping of this feed item.
     *
     * @param key   a name string for the extra key field
     * @param value the boolean to insert
     */
    public final void putBooleanExtra(String key, boolean value) {
        synchronized (mExtra) {
            mExtra.putBoolean(key, value);
        }
    }

    /**
     * Retrieves the integer value associated with the given key.
     *
     * @param key          the key field to retrieve from
     * @param defaultValue value to return if key does not exist
     * @return the integer associated with the key; or the defaultValue if the key does not exist.
     */
    public final int getIntExtra(String key, int defaultValue) {
        synchronized (mExtra) {
            return mExtra.getInt(key, defaultValue);
        }
    }

    /**
     * Inserts an integer value into the extra fields mapping of this feed item.
     *
     * @param key   a name string for the extra key field
     * @param value the integer to insert
     */
    public final void putIntExtra(String key, int value) {
        synchronized (mExtra) {
            mExtra.putInt(key, value);
        }
    }

    /**
     * Retrieves the text string value associated with the given key.
     *
     * @param key          the key field to retrieve from
     * @param defaultValue value to return if key does not exist
     * @return the text string associated with the key; or the defaultValue if the key does not exist.
     */
    public final CharSequence getCharSequenceExtra(String key, CharSequence defaultValue) {
        synchronized (mExtra) {
            return mExtra.getCharSequence(key, defaultValue);
        }
    }

    /**
     * Inserts a text string into the extra fields mapping of this feed item.
     *
     * @param key   a name string for the extra key field
     * @param value the text string to insert
     */
    public final void putCharSequenceExtra(String key, CharSequence value) {
        synchronized (mExtra) {
            mExtra.putCharSequence(key, value);
        }
    }

    /**
     * Retrieves the Long value associated with the given key.
     *
     * @param key          the key field to retrieve from
     * @param defaultValue value to return if key does not exist
     * @return the Long associated with the key; or the defaultValue if the key does not exist.
     */
    public final Long getLongExtra(String key, long defaultValue) {
        synchronized (mExtra) {
            return mExtra.getLong(key, defaultValue);
        }
    }

    /**
     * Inserts a Long value into the extra fields mapping of this feed item.
     *
     * @param key   a name string for the extra key field
     * @param value the Long to insert
     */
    public final void putLongExtra(String key, long value) {
        synchronized (mExtra) {
            mExtra.putLong(key, value);
        }
    }

    /**
     * Retrieves the Parcelable associated with the given key.
     *
     * @param key the key field to retrieve from
     * @return the Parcelable associated with the key; or null.
     */
    public final Parcelable getParcelableExtra(String key) {
        synchronized (mExtra) {
            return mExtra.getParcelable(key);
        }
    }

    /**
     * Inserts an Parcelable into the extra fields mapping of this feed item.
     *
     * @param key   a name string for the extra key field
     * @param value the Parcelable to insert
     */
    public final void putParcelableExtra(String key, Parcelable value) {
        synchronized (mExtra) {
            mExtra.putParcelable(key, value);
        }
    }

    /**
     * Retrieves the Parcelable array associated with the given key.
     *
     * @param key the key field to retrieve from
     * @return the Parcelable array associated with the key; or null.
     */
    public final Parcelable[] getParcelableArrayExtra(String key) {
        synchronized (mExtra) {
            return mExtra.getParcelableArray(key);
        }
    }

    /**
     * Inserts an string array into the extra fields mapping of this feed item.
     *
     * @param key   a name string for the extra key field
     * @param value the string array to insert
     */
    public final void putStringArray(String key, String[] value) {
        synchronized (mExtra) {
            mExtra.putStringArray(key, value);
        }
    }

    /**
     * Retrieves the String array associated with the given key.
     *
     * @param key the key field to retrieve from
     * @return the String array associated with the key; or null.
     */
    public final String[] getStringArray(String key) {
        synchronized (mExtra) {
            return mExtra.getStringArray(key);
        }
    }

    /**
     * Inserts an int array into the extra fields mapping of this feed item.
     *
     * @param key   a name string for the extra key field
     * @param value the int array to insert
     */
    public final void putIntArray(String key, int[] value) {
        synchronized (mExtra) {
            mExtra.putIntArray(key, value);
        }
    }

    /**
     * Retrieves the Integer array associated with the given key.
     *
     * @param key the key field to retrieve from
     * @return the Integer array associated with the key; or null.
     */
    public final int[] getIntArray(String key) {
        synchronized (mExtra) {
            return mExtra.getIntArray(key);
        }
    }

    /**
     * Inserts an long array into the extra fields mapping of this feed item.
     *
     * @param key   a name string for the extra key field
     * @param value the long array to insert
     */
    public final void putLongArray(String key, long[] value) {
        synchronized (mExtra) {
            mExtra.putLongArray(key, value);
        }
    }

    /**
     * Retrieves the Long array associated with the given key.
     *
     * @param key the key field to retrieve from
     * @return the Long array associated with the key; or null.
     */
    public final long[] getLongArray(String key) {
        synchronized (mExtra) {
            return mExtra.getLongArray(key);
        }
    }

    /**
     * Inserts an boolean array into the extra fields mapping of this feed item.
     *
     * @param key   a name string for the extra key field
     * @param value the boolean array to insert
     */
    public final void putBooleanArray(String key, boolean[] value) {
        synchronized (mExtra) {
            mExtra.putBooleanArray(key, value);
        }
    }

    /**
     * Retrieves the boolean array associated with the given key.
     *
     * @param key the key field to retrieve from
     * @return the boolean array associated with the key; or null.
     */
    public final boolean[] getBooleanArray(String key) {
        synchronized (mExtra) {
            return mExtra.getBooleanArray(key);
        }
    }

    /**
     * Inserts an Parcelable array into the extra fields mapping of this feed item.
     *
     * @param key   a name string for the extra key field
     * @param value the Parcelable array to insert
     */
    public final void putParcelableArrayExtra(String key, Parcelable[] value) {
        synchronized (mExtra) {
            mExtra.putParcelableArray(key, value);
        }
    }

    /**
     * Checks whether the extra fields contain the given key.
     *
     * @param key the key to lookup
     * @return true if the extra fields contain the given key; false otherwise.
     */
    public final boolean containsExtra(String key) {
        synchronized (mExtra) {
            return mExtra.containsKey(key);
        }
    }

    /**
     * Removes the key-value mapping from the extra fields.
     *
     * @param key the key of the mapping to remove
     */
    public final void removeExtra(String key) {
        synchronized (mExtra) {
            mExtra.remove(key);
        }
    }

    /**
     * Clears all the extra fields.
     */
    public final void clearExtras() {
        synchronized (mExtra) {
            mExtra.clear();
        }
    }

}
