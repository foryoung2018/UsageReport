package com.htc.lib2.mock.opensense.pluginmanager.data;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class Plugin {
    private static final String TAG_CLASS = "class";
    private static final String TAG_FEATURE = "feature";
    private static final String ATTR_VERSION = "version";
    private static final String TAG_PLUGIN = "plugin";
    private static final String TAG_DESCRIPTION = "description";
	private static final String TAG_PLUGIN_META = "plugin_meta";
    int version = -1;
    String feature = null;
    String className = null;
    String description = null;
    String pluginMeta = null;
    

    public static Plugin parse(XmlPullParser parser)
        throws XmlPullParserException, IOException {
        Plugin p = new Plugin();

        if ( parser.getEventType() != XmlPullParser.START_TAG
            || !TAG_PLUGIN.equals(parser.getName()) )
            throw new XmlPullParserException("Illegal access");

        int attributeCount = parser.getAttributeCount();
        if ( attributeCount > 0 ) {
            for ( int i = 0; i < attributeCount; i++ ) {
                if ( ATTR_VERSION.equals(parser.getAttributeName(i)) ) {
                    try {
                        p.version = Integer.parseInt(parser.getAttributeValue(i));
                    } catch ( NumberFormatException e ) {
                        throw new XmlPullParserException(e.getMessage());
                    }
                }
            }
        }

        while ( ! ( parser.getEventType() == XmlPullParser.END_TAG && TAG_PLUGIN.equals(parser.getName()) ) ) {
            if ( parser.getEventType() == XmlPullParser.START_TAG ) {
                String tagName = parser.getName();

                if ( tagName.equals(TAG_FEATURE) ) {
                    p.feature = parser.nextText();
                } else if ( tagName.equals(TAG_CLASS) ) {
                    p.className = parser.nextText();
                } else if ( tagName.equals(TAG_DESCRIPTION) ) {
                    p.description = parser.nextText();
                }
                else if ( tagName.equals(TAG_PLUGIN_META) ) {
                    p.pluginMeta = parser.nextText();
                }
            }
            parser.next();
        }

        if ( p.className == null || p.feature == null )
            throw new XmlPullParserException(
                "Class or Feature is not specified");

        return p;
    }

    public String getDescription() {
        return description;
    }

    public int getVersion() {
        return version;
    }

    public String getFeature() {
        return feature;
    }

    public String getClassName() {
        return className;
    }
    
    public String getPluginMeta() {
        return pluginMeta;
    }
}