package com.htc.lib2.mock.opensense.pluginmanager.data;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class PluginPackage {
	private static final String TAG_META_DATA = "meta_data";
	private static final String TAG_PLUGIN = "plugin";
	private static final String ATTR_CERTIFICATION = "certification";
	private static final String ATTR_VERSION = "version";
	private static final String TAG_PACKAGE = "pluginpackage";
	
	String name;
	int version;
	String certification;
	
	ArrayList<Plugin> plugins = new ArrayList<Plugin>();
	ArrayList<MetaData> services = new ArrayList<MetaData>(); 
	
//	private static final String LOG_TAG = "PluginManager";
	
	public static PluginPackage parse(XmlPullParser parser) throws XmlPullParserException, IOException {
		PluginPackage p = new PluginPackage();
		while(parser.getEventType() != XmlPullParser.END_DOCUMENT) {
			if(parser.getEventType() == XmlPullParser.START_TAG) {
				if(TAG_PACKAGE.equals(parser.getName())) {
					int attrCnt = parser.getAttributeCount();
					if(attrCnt < 2)
						throw new XmlPullParserException("Number of attributes is not match (2)");
					for(int i=0; i<attrCnt; i++) {
						if(ATTR_VERSION.equals(parser.getAttributeName(i))) {
							try {
								p.version = Integer.parseInt(parser.getAttributeValue(i));
							} catch (NumberFormatException e) {
								throw new XmlPullParserException(e.getMessage());
							}
						} else if(ATTR_CERTIFICATION.equals(parser.getAttributeName(i))) {
							p.certification = parser.getAttributeValue(i);
						}
					}
				} else if(TAG_PLUGIN.equals(parser.getName())) {
					Plugin plugin = Plugin.parse(parser);
					p.plugins.add(plugin);
				} else if(TAG_META_DATA.equals(parser.getName())) {
				    MetaData service = MetaData.parse(parser);
					p.services.add(service);
				}				
			}
			parser.next();
		}
		return p;
	}

	public int getVersion() {
		return version;
	}

	public String getCertification() {
		return certification;
	}

	public ArrayList<Plugin> getPlugins() {
		return plugins;
	}

	public ArrayList<MetaData> getServices() {
		return services;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}