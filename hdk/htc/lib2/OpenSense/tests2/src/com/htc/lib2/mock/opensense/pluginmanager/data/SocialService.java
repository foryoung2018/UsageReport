package com.htc.lib2.mock.opensense.pluginmanager.data;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class SocialService {
    private static final String TAG_META = "meta_data";
    String name;
    String account_type = "";
    private String clazz = "";

    public static final String ACCOUNTTYPE = "accountType";
    public static final String CLAZZ = "class";

    public static SocialService parse(XmlPullParser parser)
        throws XmlPullParserException, IOException {
        SocialService service = new SocialService();

        if ( parser.getEventType() != XmlPullParser.START_TAG
            || !TAG_META.equals(parser.getName()) )
            throw new XmlPullParserException("Illegal access");

        int attributeCount = parser.getAttributeCount();
        if ( attributeCount == 0 )
            throw new XmlPullParserException("Service name must be specified!");

        if ( attributeCount > 0 ) {
            for ( int i = 0; i < attributeCount; i++ ) {
                if ( "name".equals(parser.getAttributeName(i)) ) {
                    service.name = parser.getAttributeValue(i);
                }
            }
        }

        while ( ! ( parser.getEventType() == XmlPullParser.END_TAG && TAG_META
            .equals(parser.getName()) ) ) {
            if ( parser.getEventType() == XmlPullParser.START_TAG ) {
                String tagName = parser.getName();

                if ( ACCOUNTTYPE.equals(tagName) ) {
                    service.account_type = parser.nextText();
                } else if ( CLAZZ.equals(tagName) ) {
                    service.clazz = parser.nextText();
                }
            }
            parser.next();
        }

        return service;
    }

    public String getName() {
        return name;
    }

    public String getAccountType() {
        return account_type;
    }

    public String getClazz() {
        return clazz;
    }

}