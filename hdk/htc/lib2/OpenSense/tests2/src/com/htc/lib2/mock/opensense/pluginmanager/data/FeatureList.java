package com.htc.lib2.mock.opensense.pluginmanager.data;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

public class FeatureList extends ArrayList<FeatureList.Feature> {
	private static final String LOG_TAG = "FeatureList";
	private static final String TAG_FEATURE = "feature";
	
	public static class Feature {
		private static final String ATTR_NAME = "name";
		private static final String ATTR_VERSION = "version";
		
		
		int version = 0;
		String name = null;
		String type = null;
		String description = null;
		ArrayList<String> application_pkg = new ArrayList<String>();
		
		static Feature parse(XmlPullParser parser) throws XmlPullParserException, IOException {
			Feature feature = new Feature();
			
			if(parser.getEventType()!=XmlPullParser.START_TAG || !TAG_FEATURE.equals(parser.getName()))
				throw new XmlPullParserException("Illegal access");
			
			int attributeCount = parser.getAttributeCount();
			if(attributeCount > 0) {
				for(int i = 0;i<attributeCount;i++){
					if(ATTR_NAME.equals(parser.getAttributeName(i))) {
						feature.name = parser.getAttributeValue(i);
					} else if(ATTR_VERSION.equals(parser.getAttributeName(i))) {
						feature.version = Integer.parseInt(parser.getAttributeValue(i));
					}
				}
			} else
				throw new XmlPullParserException("Must have attr name");
			
			while(!(parser.getEventType()==XmlPullParser.END_TAG && TAG_FEATURE.equals(parser.getName()))) {
				if(parser.getEventType()==XmlPullParser.START_TAG) {
					String tagName = parser.getName();
					
					Log.d(LOG_TAG, tagName);
					if("type".equals(tagName)) {
						feature.type = parser.nextText();
					} else if("description".equals(tagName)) {
						feature.description = parser.nextText();
					} else if("application_package".equals(tagName)) {		
						final String app_pkg = parser.nextText();
						if(app_pkg!=null)
							feature.application_pkg.add(app_pkg);
					}
				}
				parser.next();
			}
			
			if(feature.type == null)
				throw new XmlPullParserException("Feature type is not specified");
			return feature;
		}

		public String getName() {
			return name;
		}

		public String getType() {
			return type;
		}

		public String getDescription() {
			return description;
		}

		public ArrayList<String> getApplication_pkg() {
			return application_pkg;
		}

		public int getVersion() {
			return version;
		}
	}

	public static FeatureList parse(XmlPullParser parser) throws XmlPullParserException, IOException {
		FeatureList list = new FeatureList();
		while(parser.getEventType() != XmlPullParser.END_DOCUMENT) {
			if(parser.getEventType() == XmlPullParser.START_TAG) {
				if(TAG_FEATURE.equals(parser.getName())) {
					Feature f = Feature.parse(parser);
					if(f!=null)
						list.add(f);
				} 			
			}
			parser.next();
		}
		return list;
	}
}