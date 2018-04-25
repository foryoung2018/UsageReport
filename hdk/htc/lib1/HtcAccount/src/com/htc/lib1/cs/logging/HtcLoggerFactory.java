
package com.htc.lib1.cs.logging;

import org.andlog.VerboseLoggerFactory;

import android.text.TextUtils;

/**
 * HTC customized extension to {@link VerboseLoggerFactory} which uses object /
 * class type name as prefix, and an explicit tag. The application could create
 * a class {@code com.htc.lib1.cs.logging.HtcLoggerFactoryOverride} with static
 * field {@code TAG} and {@code STAG} to override the tag and sensitive tag,
 * respectively. Noticed that in this case the tag / stag is not trimmed
 * automatically. Developer must ensure the tag / stag don't exceed 23
 * characters.
 * 
 * <pre>
 * public class HtcLoggerFactoryOverride {
 *     public static final String TAG = &quot;AppOverrideTag&quot;;
 *     public static final String STAG = &quot;AppOverrideSTag&quot;;
 * }
 * </pre>
 */
public class HtcLoggerFactory extends VerboseLoggerFactory {
    private static final String OVERRIDE_TAG;
    private static final String OVERRIDE_STAG;
    private String mSTag;
    private String mPrefix;

    static {
        // Check if the overriding class exists.
        String tag = null, stag = null;
        try {
            Class<?> cls = Class.forName("com.htc.lib1.cs.logging.HtcLoggerFactoryOverride");
            tag = (String) cls.getField("TAG").get(null);
            stag = (String) cls.getField("STAG").get(null);
        } catch (Exception e) {
        }

        // Override tag, if available.
        if (!TextUtils.isEmpty(tag)) {
            OVERRIDE_TAG = tag;
        } else {
            OVERRIDE_TAG = null;
        }

        // Override stag, if available.
        if (!TextUtils.isEmpty(stag)) {
            OVERRIDE_STAG = stag;
        } else {
            OVERRIDE_STAG = null;
        }

    }

    /**
     * Construct a {@link HtcLoggerFactory} which uses the given {@code obj} as
     * log prefix.
     * 
     * @param tag Log tag to use.
     * @param sTag Log tag to use for sensitive logs.
     * @param obj {@link Object} used to generate log prefix.
     */
    public HtcLoggerFactory(String tag, String sTag, Object obj) {
        super(tag);
        if (obj == null)
            throw new IllegalArgumentException("'obj' is null.");
        if (TextUtils.isEmpty(sTag))
            throw new IllegalArgumentException("'sTag' is null or empty.");

        mSTag = trimTag(sTag);
        mPrefix = String.format("%s {%x}", getSimpleName(obj), obj.hashCode());
    }

    /**
     * Construct a {@link HtcLoggerFactory} which uses the given {@code obj} as
     * log prefix.
     * 
     * @param tag Log tag to use.
     * @param cls {@link Class} used to generate log prefix.
     */
    public HtcLoggerFactory(String tag, String sTag, Class<?> cls) {
        super(tag);
        if (cls == null)
            throw new IllegalArgumentException("'cls' is null.");
        if (TextUtils.isEmpty(sTag))
            throw new IllegalArgumentException("'sTag' is null or empty.");

        mSTag = trimTag(sTag);
        mPrefix = cls.getSimpleName();
    }

    /**
     * Construct a {@link HtcLoggerFactory} which uses no log prefix.
     * 
     * @param tag Log tag to use.
     */
    public HtcLoggerFactory(String tag, String sTag) {
        super(tag);
        if (TextUtils.isEmpty(sTag))
            throw new IllegalArgumentException("'sTag' is null or empty.");

        mSTag = sTag;
    }

    @Override
    public HtcLogger create() {
        return new HtcLogger(TextUtils.isEmpty(OVERRIDE_TAG) ? mTag : OVERRIDE_TAG,
                TextUtils.isEmpty(OVERRIDE_STAG) ? mSTag : OVERRIDE_STAG, mPrefix, getBuilder());
    }
}
