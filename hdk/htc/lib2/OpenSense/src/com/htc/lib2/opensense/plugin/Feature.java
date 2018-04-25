package com.htc.lib2.opensense.plugin;

/**
 * A container class which holds information about plugin features.
 * 
 * @hide
 */
public class Feature {

    private int mId;
    private int mVersion;
    private String mName;
    private String mType;

    /**
     * @hide
     */
    public Feature() {
    }

    /**
     * Used by PluginRegistryHelper.getPlugins()
     * 
     * @hide
     */
    public Feature(String name, String type) {
        this(0, 0, name, type);
    }

    /**
     * Used by PluginRegistryHelper.getPlugins()
     * 
     * @hide
     */
    public Feature(int id, int version, String name, String type) {
        super();
        mId = id;
        mVersion = version;
        mName = name;
        mType = type;
    }

    /**
     * Gets the feature's ID
     * 
     * @return the feature's ID
     * 
     * @hide
     */
    public int getId() {
        return mId;
    }

    /**
     * Gets the feature's version
     * 
     * @return the feature's version
     * 
     * @hide
     */
    public int getVersion() {
        return mVersion;
    }

    /**
     * Gets the feature's name
     * 
     * @return the feature's name
     * 
     * @hide
     */
    public String getName() {
        return mName;
    }

    /**
     * Gets the feature's type
     * 
     * @return the feature's type
     * 
     * @hide
     */
    public String getType() {
        return mType;
    }

    /**
     * @hide
     */
    public void setId(int id) {
        mId = id;
    }

    /**
     * @hide
     */
    public void setVersion(int version) {
        mVersion = version;
    }

    /**
     * @hide
     */
    public void setName(String name) {
        mName = name;
    }

    /**
     * @hide
     */
    public void setType(String type) {
        mType = type;
    }

    /**
     * @hide
     */
    @Override
    public boolean equals(Object object) {
        if ( object == null ) {
            return false;
        }
        if ( !(object instanceof Feature) ) {
            return false;
        }
        Feature feature = (Feature) object;
        if ( feature.getId() != getId() ) {
            return false;
        }
        if ( feature.getVersion() != getVersion() ) {
            return false;
        }
        if ( !isEqualString(feature.getName(), getName()) ) {
            return false;
        }
        if ( !isEqualString(feature.getType(), getType()) ) {
            return false;
        }
        return true;
    }

    private static boolean isEqualString(String a, String b) {
        if ( a == null ) {
            if ( b != null ) {
                return false;
            }
        } else {
            if ( !a.equals(b) ) {
                return false;
            }
        }
        return true;
    }
}
