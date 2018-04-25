package com.htc.lib2.mock.opensense.pluginmanager.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

public class MetaData {
    private static final String TAG_META = "meta_data";
    String name;
    List<TypeValue> dataList;
    public static final String LOG_TAG = "MetaData";

    public MetaData() {
        dataList = new ArrayList<TypeValue>();
    }

    public static MetaData parse(XmlPullParser parser)
        throws XmlPullParserException, IOException {
        MetaData service = new MetaData();

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
            Log.d(LOG_TAG, "name = " + service.name);
            if ( parser.getEventType() == XmlPullParser.START_TAG ) {
                String tagName = parser.getName();
                if ( !tagName.equals(TAG_META) ) {
                    Log.d(LOG_TAG, "tagName = " + tagName);
                    service.dataList.add(new TypeValue(tagName, parser
                        .nextText()));
                }
            }
            parser.next();
        }

        return service;
    }

    public String getName() {
        return name;
    }

    public List<TypeValue> getDataList() {
        return dataList;
    }

    public static class TypeValue {
        public TypeValue(String t, String v) {
            type = t;
            value = v;
        }

        public String type;
        public String value;
    }
}