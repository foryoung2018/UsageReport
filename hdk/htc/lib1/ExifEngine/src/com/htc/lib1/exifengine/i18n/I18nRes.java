package com.htc.lib1.exifengine.i18n;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import com.htc.lib1.exifengine.ExifEngine;


/**
 * Exif i18n Resources
 *
 * @author amt_masd_kg_shen@htc.com
 */
public final class I18nRes {
	static final String TAG = "I18nRes";
	//
	public static final String UNIT_SECOND = "unit_second";
	public static final String UNIT_MM = "unit_mm";
	private static final String XML_STRING_TAG = "string";
	private static final String XML_STRING_NAME = "name";

	/**
	 * load i18n resources by Locale
	 *
	 * @param locale
	 * @param out    keys{@link #UNIT_SECOND}...
	 */
	public static void loadRes(Locale locale, Map<String, String> out) {
		if (out == null) {
			return;
		}
		InputStream is = null;
		try {
			is = openResourceStream(locale);
            if(is == null){
                return;
            }
			XmlPullParser xml = Xml.newPullParser();
			xml.setInput(is, "UTF-8");
			int type = xml.getEventType();
			String name = null;
			String value = null;
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
					case XmlPullParser.START_TAG:
						if (XML_STRING_TAG.equals(xml.getName())) {
							name = xml.getAttributeValue(null, XML_STRING_NAME);
						}
						break;
					case XmlPullParser.TEXT:
						value = xml.getText();
						break;
					case XmlPullParser.END_TAG:
						if (XML_STRING_TAG.equals(xml.getName())) {
							out.put(name, value);
							name = null;
							value = null;
						}
						break;
				}
				type = xml.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static final String FILE_ROOT = "/assets/i18n";
	// string-zh-rTW.xml
	private static final String FILE_SUFFIX_NAME = "xml";
	private static final String FILE_NAME_PREFIX = "strings";

	/**
	 * open i18n language format res Stream
	 *
	 * @param locale
	 * @return
	 * @throws IOException
	 */
	private static InputStream openResourceStream(Locale locale)
			throws IOException {
		if (locale == null) {
			locale = Locale.getDefault();
		}
		// I18nRes.class.getResourceAsStream(resName);
		String l = locale.getLanguage();
		String c = locale.getCountry();
		if (TextUtils.isEmpty(l)) {
			if (ExifEngine.DEBUG) {
				Log.d(TAG, "Bad Locale,load default language:"
						+ getDefaultLanguagePath());
			}
			return I18nRes.class.getResourceAsStream(getDefaultLanguagePath());
		}
		InputStream in = null;
		// 1,try load by "strings-language-country.xml"
		try {
			if (ExifEngine.DEBUG) {
				Log.d(TAG, "load by language-country:" + getLanguagePath(l, c));
			}
			in = I18nRes.class.getResourceAsStream(getLanguagePath(l, c));
		} catch (Exception e) {
		}
		//2,try load by "strings-language.xml"
		if (in == null && !TextUtils.isEmpty(c)) {
			try {
				if (ExifEngine.DEBUG) {
					Log.d(TAG, "load by language:" + getLanguagePath(l, null));
				}
				in = I18nRes.class
						.getResourceAsStream(getLanguagePath(l, null));
			} catch (Exception e) {
			}
		}
		//2,try load by default"strings.xml"
		if (in == null) {
			if (ExifEngine.DEBUG) {
				Log.d(TAG, "load default language:" + getDefaultLanguagePath());
			}
			in = I18nRes.class.getResourceAsStream(getDefaultLanguagePath());
		}
		return in;
	}

	private static String getLanguagePath(String fileName) {// string-zh-rTW.xml
		return FILE_ROOT + File.separator + fileName + "." + FILE_SUFFIX_NAME;
	}

	private static String getDefaultLanguagePath() {
		return getLanguagePath(null, null);
	}

	private static String getLanguagePath(String language, String country) {
		if (TextUtils.isEmpty(language)) {
			//default xml
			return getLanguagePath(FILE_NAME_PREFIX);
		} else if (TextUtils.isEmpty(country)) {
			//language.xml
			return getLanguagePath(FILE_NAME_PREFIX + "-" + language);
		} else {
			//language-country.xml
			return getLanguagePath(FILE_NAME_PREFIX + "-" + language + "-r" + country);
		}
	}
}
