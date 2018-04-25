package com.htc.lib1.exifengine;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * @author amt_masd_kg_shen@htc.com
 */
public class MainActivity extends Activity {
	final static String TAG = "ExifEngine";
	private TextView tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv = (TextView) findViewById(R.id.exif_result);
		//simple test
		testFileExif("/sdcard/exif.jpg");
		// testFileExif("/sdcard/test_imgs/1.jpg");
	}

	private void testFileExif(String testImgFile) {
		String initErrorMsg = null;
		ExifEngine exif = null;
		try {
			File file = new File(testImgFile);
			if (file == null || !file.exists()) {
				initErrorMsg = testImgFile + " not found";
			} else {
				exif = new ExifEngine(testImgFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
			initErrorMsg = e.getMessage();
		} finally {
			if (exif == null) {
				initErrorMsg += "ExifEngine init failed";
			}
			if (initErrorMsg != null) {
				Toast.makeText(this, initErrorMsg, Toast.LENGTH_LONG).show();
				tv.setText(initErrorMsg);
				return;
			}
		}
		testFileExifRead(exif);
		// testFileExifWrite(exif);
	}

	void testFileExifRead(ExifEngine exif) {
		Field[] fileds = ExifEngine.class.getFields();
		StringBuilder sb = new StringBuilder();
		for (Field f : fileds) {
			if (f.getName().startsWith("EXIF_TAG")) {
				int key = -1;
				try {
					key = f.getInt(exif);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					continue;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					continue;
				}
				if (key != -1) {
					String v = exif.getTagValueString(key);
					if (v != null && v.length() > 0) {
						v = "<font color=blue>" + v + "</font>";
						sb.append(f.getName().replace("EXIF_TAG_", "") + "="
								+ v);
						sb.append("<br/>");
					}
				}
			}
		}
		tv.setText(Html.fromHtml(sb.toString()));
	}

	void testFileExifWrite(ExifEngine exif) {
		Field[] fileds = ExifEngine.class.getFields();
		int i = 0;
		for (Field f : fileds) {
			i++;
			if (f.getName().startsWith("EXIF_TAG")) {
				int key = -1;
				try {
					key = f.getInt(exif);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					continue;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					continue;
				}
				if (key != -1) {
					exif.setTagValueString(key, "" + i);
				}
			}
		}
		try {
			exif.saveExifDataToFile();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, "Exif info save failed", Toast.LENGTH_LONG)
					.show();
		}
	}
}