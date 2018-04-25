package com.htc.lib2.opensense.test.internal;

import java.io.File;

import android.os.Environment;
import android.os.SystemProperties;

import com.htc.lib2.opensense.internal.SystemWrapper;

import junit.framework.Assert;
import junit.framework.TestCase;

public class SystemWrapperTestCase extends TestCase {

    private static String LINK_1_DECODED = "http://中文.tw/images/w4.gif";
    private static String LINK_1_ENCODED = "http://xn--fiq228c.tw/images/w4.gif";
    private static String LINK_2_DECODED =
            "http://news.aniarc.com/wp-content/uploads/2013/10/1026-日本聲優見面會照-200x200.jpg";
    private static String LINK_2_ENCODED =
            "http://news.aniarc.com/wp-content/uploads/2013/10/1026-%E6%97%A5%E6%9C%AC%E8%81%B2%E5%84%AA%E8%A6%8B%E9%9D%A2%E6%9C%83%E7%85%A7-200x200.jpg";
    private static String LINK_3_ENCODED =
            "https://fbexternal-a.akamaihd.net/safe_image.php?d=AQAINdOZ_Am8UFsR&w=130&h=130&url=https%3A%2F%2Fdmxgmmqiylm9v.cloudfront.net%2Fbb6b83b9-4d74-428e-8abe-c7e0195339bd%2F1417514058340%2Fe2215f0c-c5d4-48dd-9633-f95e88df3df5.jpg%3FExpires%3D1977281192%26Signature%3DTQk9Y6qyr3y7LyhVZoR1t4HRF1nbQxpKiF67RWHTJGvltxfC14h1d66CbwvjE1xEbPgsk4addrjtQBunZDQ4e-SysVjJ9O9P9Mpr7S8FjaYUDmVauQiw9tl1E9dAr4EXKRR5Fs3cckREXGc0wEJO22-cDAs-ceoJpSBppB-NOLo_%26Key-Pair-Id%3DAPKAICXLZP27IUJJDALQ";
//    private static String LINK_3_ENCODED =
//            "https://fbexternal-a.akamaihd.net/safe_image.php?";

    public void testEnvironmentHasRemovableStorageSlot() throws Throwable {
        boolean wrapperValue = SystemWrapper.Environment.hasRemovableStorageSlot();
        boolean systemValue = false;
        try {
            systemValue = Environment.hasRemovableStorageSlot();
        } catch (NoSuchMethodError e) {
        }
        Assert.assertEquals(systemValue, wrapperValue);
    }

    public void testEnvironmentGetRemovableStorageState() throws Throwable {
        String wrapperValue = SystemWrapper.Environment.getRemovableStorageState();
        String systemValue = Environment.MEDIA_REMOVED;
        try {
            systemValue = Environment.getRemovableStorageState();
        } catch (NoSuchMethodError e) {
        }
        Assert.assertEquals(systemValue, wrapperValue);
    }

    public void testEnvironmentGetRemovableStorageDirectory() throws Throwable {
        File wrapperValue = SystemWrapper.Environment.getRemovableStorageDirectory();
        File systemValue = new File("/storage/ext_sd");
        try {
            systemValue = Environment.getRemovableStorageDirectory();
        } catch (NoSuchMethodError e) {
        }
        Assert.assertEquals(systemValue, wrapperValue);
    }

    public void testEnvironmentHasPhoneStorage() throws Throwable {
        boolean wrapperValue = SystemWrapper.Environment.hasPhoneStorage();
        boolean systemValue = false;
        try {
            systemValue = Environment.hasPhoneStorage();
        } catch (NoSuchMethodError e) {
        }
        Assert.assertEquals(systemValue, wrapperValue);
    }

    public void testEnvironmentGetPhoneStorageState() throws Throwable {
        String wrapperValue = SystemWrapper.Environment.getPhoneStorageState();
        String systemValue = Environment.MEDIA_REMOVED;
        try {
            systemValue = Environment.getPhoneStorageState();
        } catch (NoSuchMethodError e) {
        }
        Assert.assertEquals(systemValue, wrapperValue);
    }

    public void testEnvironmentGetPhoneStorageDirectory() throws Throwable {
        File wrapperValue = SystemWrapper.Environment.getPhoneStorageDirectory();
        File systemValue = new File("/storage/emmc");
        try {
            systemValue = Environment.getPhoneStorageDirectory();
        } catch (NoSuchMethodError e) {
        }
        Assert.assertEquals(systemValue, wrapperValue);
    }

    public void testSystemPropertiesGet1() throws Throwable {
        String key = "ro.build.version.codename";
        String wrapperValue = SystemWrapper.SystemProperties.get(key);
        String systemValue = SystemProperties.get(key);
        Assert.assertEquals(systemValue, wrapperValue);
        Assert.assertEquals("REL", wrapperValue);
    }

    public void testSystemPropertiesGet2() throws Throwable {
        String key = "ro.build.version.codename";
        String defaultValue1 = "KeyLimePie";
        String defaultValue2 = "REL";
        String wrapperValue = SystemWrapper.SystemProperties.get(key, defaultValue1);
        String systemValue = SystemProperties.get(key, defaultValue1);
        Assert.assertEquals(systemValue, wrapperValue);
        Assert.assertEquals(defaultValue2, wrapperValue);
    }

    public void testSystemPropertiesGetBoolean() throws Throwable {
        String key = "persist.timed.enable";
        boolean defaultValue1 = false;
        boolean defaultValue2 = true;
        boolean wrapperValue = SystemWrapper.SystemProperties.getBoolean(key, defaultValue1);
        boolean systemValue = SystemProperties.getBoolean(key, defaultValue1);
        Assert.assertEquals(systemValue, wrapperValue);
        Assert.assertEquals(defaultValue2, wrapperValue);
    }

    public void testSystemPropertiesGetInt() throws Throwable {
        String key = "ro.sf.lcd_density";
        int defaultValue = 1;
        int wrapperValue = SystemWrapper.SystemProperties.getInt(key, defaultValue);
        int systemValue = SystemProperties.getInt(key, defaultValue);
        Assert.assertEquals(systemValue, wrapperValue);
        Assert.assertTrue(wrapperValue > defaultValue);
    }

    public void testSystemPropertiesGetLong() throws Throwable {
        String key = "ro.build.date.utc";
        long defaultValue = 1L;
        long wrapperValue = SystemWrapper.SystemProperties.getLong(key, defaultValue);
        long systemValue = SystemProperties.getLong(key, defaultValue);
        Assert.assertEquals(systemValue, wrapperValue);
        Assert.assertTrue(wrapperValue > defaultValue);
    }

    public void testHtcBuildFlagHtcDebugFlag() throws Throwable {
        boolean wrapperValue = SystemWrapper.HtcBuildFlag.Htc_DEBUG_flag;
        boolean systemValue = false;
        try {
            systemValue = com.htc.htcjavaflag.HtcBuildFlag.Htc_DEBUG_flag;
        } catch (NoClassDefFoundError e) {
        }
        Assert.assertEquals(systemValue, wrapperValue);
    }

    public void testHttpLinkConverterGetAsciiLink1() throws Throwable {
        String value1 = SystemWrapper.HttpLinkConverter.getAsciiLink(LINK_1_DECODED);
        String value2 = LINK_1_ENCODED;
        Assert.assertEquals(value2, value1);
    }

    public void testHttpLinkConverterGetAsciiLink2() throws Throwable {
        String value1 = SystemWrapper.HttpLinkConverter.getAsciiLink(LINK_2_DECODED);
        String value2 = LINK_2_ENCODED;
        Assert.assertEquals(value2, value1);
    }

    public void testHttpLinkConverterGetAsciiLink3() throws Throwable {
        String value1 = SystemWrapper.HttpLinkConverter.getAsciiLink(LINK_1_ENCODED);
        String value2 = LINK_1_ENCODED;
        Assert.assertEquals(value2, value1);
    }

    public void testHttpLinkConverterGetAsciiLink4() throws Throwable {
        String value1 = SystemWrapper.HttpLinkConverter.getAsciiLink(LINK_2_ENCODED);
        String value2 = LINK_2_ENCODED;
        Assert.assertEquals(value2, value1);
    }

    public void testHttpLinkConverterGetAsciiLink5() throws Throwable {
        String value1 = SystemWrapper.HttpLinkConverter.getAsciiLink(LINK_3_ENCODED);
        String value2 = LINK_3_ENCODED;
        Assert.assertEquals(value2, value1);
    }
}
