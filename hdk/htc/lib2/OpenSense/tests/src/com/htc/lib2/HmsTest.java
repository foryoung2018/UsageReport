package com.htc.lib2;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import com.htc.lib2.Hms.CompatibilityException;
import com.htc.lib2.opensense.internal.SystemWrapper;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;

@RunWith(RobolectricTestRunner.class)
@PrepareForTest({
        ApplicationInfo.class,
        Context.class,
        Log.class,
        PackageManager.class,
        SystemProperties.class,
        SystemWrapper.class,
        SystemWrapper.Build.class
})
@Config(manifest=Config.NONE)
public class HmsTest {

    private static String PACKAGE_NAME_HSP = "com.htc.sense.hsp";
    private static String PACKAGE_NAME_HMS = "com.htc.lib2.opensense.tests";
    private static String MODULE_NAME_A = "ModuleA";
    private static String MODULE_NAME_B = "ModuleB";
    private static String API_PREFIX_0 = "hdkapi0_";
    private static String API_PREFIX_1 = "hdkapi1_";

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Test
    public void testCheckCompatibility00() {
        mockSystemProperties0();

        Exception exception = null;
        try {
            Hms.checkCompatibility(null);
        } catch (IllegalArgumentException e) {
            exception = e;
        } catch (CompatibilityException e) {
            exception = e;
        }
        Assert.assertNotNull(exception);
        Assert.assertTrue(exception instanceof IllegalArgumentException);
        Assert.assertEquals("context == null", exception.getMessage());
    }

    @Test
    public void testCheckCompatibility01() {
        mockSystemProperties0();
        Context context = getMockContext0();

        Hms.CompatibilityStatus status = Hms.CompatibilityStatus.ERROR_UNKNOWN;
        Exception exception = null;
        try {
            status = Hms.checkCompatibility(context);
        } catch (IllegalArgumentException e) {
            exception = e;
        } catch (CompatibilityException e) {
            exception = e;
        }
        Assert.assertNull(exception);
        Assert.assertNotEquals(Hms.CompatibilityStatus.ERROR_UNKNOWN, status);
        Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
    }

    @Test
    public void testCheckCompatibility02() throws Exception {
        mockSystemProperties0();
        mockSystemWrapper0(false);
        mockSystemWrapperBuild0(0.0f);
        Context context = getMockContext0();

        Hms.CompatibilityStatus status = Hms.CompatibilityStatus.ERROR_UNKNOWN;
        Exception exception = null;
        try {
            status = Hms.checkCompatibility(context);
        } catch (IllegalArgumentException e) {
            exception = e;
        } catch (CompatibilityException e) {
            exception = e;
        }
        Assert.assertNull(exception);
        Assert.assertNotEquals(Hms.CompatibilityStatus.ERROR_UNKNOWN, status);
        Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
    }

    @Test
    public void testCheckCompatibility03() throws Exception {
        mockSystemProperties0();
        mockSystemWrapper0(false);
        mockSystemWrapperBuild0(1.0f);
        Context context = getMockContext0();

        Hms.CompatibilityStatus status = Hms.CompatibilityStatus.ERROR_UNKNOWN;
        Exception exception = null;
        try {
            status = Hms.checkCompatibility(context);
        } catch (IllegalArgumentException e) {
            exception = e;
        } catch (CompatibilityException e) {
            exception = e;
        }
        Assert.assertNull(exception);
        Assert.assertNotEquals(Hms.CompatibilityStatus.ERROR_UNKNOWN, status);
        Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
    }

    @Test
    public void testCheckCompatibility04() throws Exception {
        mockSystemProperties0();
        mockSystemWrapper0(false);
        mockSystemWrapperBuild0(1.0f);
        Context context = getMockContext0();

        Bundle bundle = null;
        Hms.CompatibilityStatus status = Hms.CompatibilityStatus.ERROR_UNKNOWN;
        Exception exception = null;
        try {
            status = Hms.checkCompatibility(context, bundle);
        } catch (IllegalArgumentException e) {
            exception = e;
        } catch (CompatibilityException e) {
            exception = e;
        }
        Assert.assertNull(exception);
        Assert.assertNotEquals(Hms.CompatibilityStatus.ERROR_UNKNOWN, status);
        Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
    }

    @Test
    public void testCheckCompatibility05() throws Exception {
        mockSystemProperties0();
        mockSystemWrapper0(false);
        mockSystemWrapperBuild0(1.0f);
        Context context = getMockContext0();

        Bundle bundle = new Bundle();
        Hms.CompatibilityStatus status = Hms.CompatibilityStatus.ERROR_UNKNOWN;
        Exception exception = null;
        try {
            status = Hms.checkCompatibility(context, bundle);
        } catch (IllegalArgumentException e) {
            exception = e;
        } catch (CompatibilityException e) {
            exception = e;
        }
        Assert.assertNull(exception);
        Assert.assertNotEquals(Hms.CompatibilityStatus.ERROR_UNKNOWN, status);
        Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
    }

    @Test
    public void testCheckCompatibility06() throws Exception {
        mockSystemProperties0();
        mockSystemWrapper0(false);
        mockSystemWrapperBuild0(1.0f);
        Context context = getMockContext0();

        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, true);
        Hms.CompatibilityStatus status = Hms.CompatibilityStatus.ERROR_UNKNOWN;
        Exception exception = null;
        try {
            status = Hms.checkCompatibility(context, bundle);
        } catch (IllegalArgumentException e) {
            exception = e;
        } catch (CompatibilityException e) {
            exception = e;
        }
        Assert.assertNull(exception);
        Assert.assertNotEquals(Hms.CompatibilityStatus.ERROR_UNKNOWN, status);
        Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
    }

    @Test
    public void testCheckCompatibility07() throws Exception {
        mockSystemProperties0();
        mockSystemWrapper0(false);
        mockSystemWrapperBuild0(1.0f);
        Context context = getMockContext0();
        mockPackageManager0(context, true);

        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, true);
        Hms.CompatibilityStatus status = Hms.CompatibilityStatus.ERROR_UNKNOWN;
        Exception exception = null;
        try {
            status = Hms.checkCompatibility(context, bundle);
        } catch (IllegalArgumentException e) {
            exception = e;
        } catch (CompatibilityException e) {
            exception = e;
        }
        Assert.assertNull(exception);
        Assert.assertNotEquals(Hms.CompatibilityStatus.ERROR_UNKNOWN, status);
        Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_SUPPORTED, status);
    }

    @Test
    public void testCheckCompatibility08() throws Exception {
        mockSystemProperties0();
        mockSystemWrapper0(true);
        mockSystemWrapperBuild0(1.0f);
        Context context = getMockContext0();
        mockPackageManager1(context, PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER);

        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, true);
        Hms.CompatibilityStatus status = Hms.CompatibilityStatus.ERROR_UNKNOWN;
        Exception exception = null;
        try {
            status = Hms.checkCompatibility(context, bundle);
        } catch (IllegalArgumentException e) {
            exception = e;
        } catch (CompatibilityException e) {
            exception = e;
        }
        Assert.assertNull(exception);
        Assert.assertNotEquals(Hms.CompatibilityStatus.ERROR_UNKNOWN, status);
        Assert.assertEquals(Hms.CompatibilityStatus.ERROR_HSP_NOT_ENABLED, status);
    }

    @Test
    public void testCheckCompatibility09() throws Exception {
        mockSystemProperties0();
        mockSystemWrapper1(true, PACKAGE_NAME_HSP);
        mockSystemWrapperBuild0(1.0f);
        Context context = getMockContext1(PACKAGE_NAME_HMS);
        mockPackageManager2(
                context,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PACKAGE_NAME_HSP,
                null,
                null,
                PACKAGE_NAME_HMS,
                null,
                null
        );

        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, true);
        Hms.CompatibilityStatus status = Hms.CompatibilityStatus.ERROR_UNKNOWN;
        Exception exception = null;
        try {
            status = Hms.checkCompatibility(context, bundle);
        } catch (IllegalArgumentException e) {
            exception = e;
        } catch (CompatibilityException e) {
            exception = e;
        }
        Assert.assertNull(exception);
        Assert.assertNotEquals(Hms.CompatibilityStatus.ERROR_UNKNOWN, status);
        Assert.assertEquals(Hms.CompatibilityStatus.HSP_UPDATE_REQUIRED, status);
    }

    @Test
    public void testCheckCompatibility10() throws Exception {
        mockSystemProperties0();
        mockSystemWrapper1(true, PACKAGE_NAME_HSP);
        mockSystemWrapperBuild0(1.0f);
        mockLog0();
        Context context = getMockContext1(PACKAGE_NAME_HMS);
        mockPackageManager2(
                context,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PACKAGE_NAME_HSP,
                MODULE_NAME_A,
                "1",
                PACKAGE_NAME_HMS,
                null,
                null
        );

        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, true);
        Exception exception = null;
        try {
            Hms.checkCompatibility(context, bundle);
        } catch (IllegalArgumentException e) {
            exception = e;
        } catch (CompatibilityException e) {
            exception = e;
        }
        Assert.assertNotNull(exception);
        Assert.assertTrue(exception instanceof CompatibilityException);
        Assert.assertEquals("hdkMetaData == null || hdkMetaData.isEmpty()", exception.getMessage());
    }

    @Test
    public void testCheckCompatibility11() throws Exception {
        mockSystemProperties0();
        mockSystemWrapper2(true, PACKAGE_NAME_HSP, API_PREFIX_0, API_PREFIX_1);
        mockSystemWrapperBuild0(1.0f);
        Context context = getMockContext1(PACKAGE_NAME_HMS);
        mockPackageManager2(
                context,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PACKAGE_NAME_HSP,
                MODULE_NAME_A,
                "1",
                PACKAGE_NAME_HMS,
                MODULE_NAME_A,
                "2"
        );

        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, true);
        Exception exception = null;
        try {
            Hms.checkCompatibility(context, bundle);
        } catch (IllegalArgumentException e) {
            exception = e;
        } catch (CompatibilityException e) {
            exception = e;
        }
        Assert.assertNotNull(exception);
        Assert.assertTrue(exception instanceof CompatibilityException);
        Assert.assertEquals("Unknown error.", exception.getMessage());
    }

    @Test
    public void testCheckCompatibility12() throws Exception {
        mockSystemProperties0();
        mockSystemWrapper2(true, PACKAGE_NAME_HSP, API_PREFIX_0, API_PREFIX_1);
        mockSystemWrapperBuild0(1.0f);
        Context context = getMockContext1(PACKAGE_NAME_HMS);
        mockPackageManager2(
                context,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PACKAGE_NAME_HSP,
                API_PREFIX_1 + MODULE_NAME_A,
                "1",
                PACKAGE_NAME_HMS,
                API_PREFIX_0 + MODULE_NAME_A,
                "2"
        );

        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, true);
        Hms.CompatibilityStatus status = Hms.CompatibilityStatus.ERROR_UNKNOWN;
        Exception exception = null;
        try {
            status = Hms.checkCompatibility(context, bundle);
        } catch (IllegalArgumentException e) {
            exception = e;
        } catch (CompatibilityException e) {
            exception = e;
        }
        Assert.assertNull(exception);
        Assert.assertNotEquals(Hms.CompatibilityStatus.ERROR_UNKNOWN, status);
        Assert.assertEquals(Hms.CompatibilityStatus.HSP_UPDATE_REQUIRED, status);
    }

    @Test
    public void testCheckCompatibility13() throws Exception {
        mockSystemProperties0();
        mockSystemWrapper2(true, PACKAGE_NAME_HSP, API_PREFIX_0, API_PREFIX_1);
        mockSystemWrapperBuild0(1.0f);
        Context context = getMockContext1(PACKAGE_NAME_HMS);
        mockPackageManager2(
                context,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PACKAGE_NAME_HSP,
                API_PREFIX_1 + MODULE_NAME_A,
                "1",
                PACKAGE_NAME_HMS,
                API_PREFIX_0 + MODULE_NAME_B,
                "1"
        );

        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, true);
        Hms.CompatibilityStatus status = Hms.CompatibilityStatus.ERROR_UNKNOWN;
        Exception exception = null;
        try {
            status = Hms.checkCompatibility(context, bundle);
        } catch (IllegalArgumentException e) {
            exception = e;
        } catch (CompatibilityException e) {
            exception = e;
        }
        Assert.assertNull(exception);
        Assert.assertNotEquals(Hms.CompatibilityStatus.ERROR_UNKNOWN, status);
        Assert.assertEquals(Hms.CompatibilityStatus.HSP_UPDATE_REQUIRED, status);
    }

    @Test
    public void testCheckCompatibility14() throws Exception {
        mockSystemProperties0();
        mockSystemWrapper2(true, PACKAGE_NAME_HSP, API_PREFIX_0, API_PREFIX_1);
        mockSystemWrapperBuild0(1.0f);
        Context context = getMockContext1(PACKAGE_NAME_HMS);
        mockPackageManager2(
                context,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PACKAGE_NAME_HSP,
                API_PREFIX_1 + MODULE_NAME_A,
                "",
                PACKAGE_NAME_HMS,
                API_PREFIX_0 + MODULE_NAME_A,
                "1.1.1.1"
        );

        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, true);
        Hms.CompatibilityStatus status = Hms.CompatibilityStatus.ERROR_UNKNOWN;
        Exception exception = null;
        try {
            status = Hms.checkCompatibility(context, bundle);
        } catch (IllegalArgumentException e) {
            exception = e;
        } catch (CompatibilityException e) {
            exception = e;
        }
        Assert.assertNull(exception);
        Assert.assertNotEquals(Hms.CompatibilityStatus.ERROR_UNKNOWN, status);
        Assert.assertEquals(Hms.CompatibilityStatus.HSP_UPDATE_REQUIRED, status);
    }

    @Test
    public void testCheckCompatibility15() throws Exception {
        mockSystemProperties0();
        mockSystemWrapper2(true, PACKAGE_NAME_HSP, API_PREFIX_0, API_PREFIX_1);
        mockSystemWrapperBuild0(1.0f);
        Context context = getMockContext1(PACKAGE_NAME_HMS);
        mockPackageManager2(
                context,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PACKAGE_NAME_HSP,
                API_PREFIX_1 + MODULE_NAME_A,
                "1.0",
                PACKAGE_NAME_HMS,
                API_PREFIX_0 + MODULE_NAME_A,
                "2.0"
        );

        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, true);
        Hms.CompatibilityStatus status = Hms.CompatibilityStatus.ERROR_UNKNOWN;
        Exception exception = null;
        try {
            status = Hms.checkCompatibility(context, bundle);
        } catch (IllegalArgumentException e) {
            exception = e;
        } catch (CompatibilityException e) {
            exception = e;
        }
        Assert.assertNull(exception);
        Assert.assertNotEquals(Hms.CompatibilityStatus.ERROR_UNKNOWN, status);
        Assert.assertEquals(Hms.CompatibilityStatus.HSP_UPDATE_REQUIRED, status);
    }

    @Test
    public void testCheckCompatibility16() throws Exception {
        mockSystemProperties0();
        mockSystemWrapper2(true, PACKAGE_NAME_HSP, API_PREFIX_0, API_PREFIX_1);
        mockSystemWrapperBuild0(1.0f);
        Context context = getMockContext1(PACKAGE_NAME_HMS);
        mockPackageManager2(
                context,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PACKAGE_NAME_HSP,
                API_PREFIX_1 + MODULE_NAME_A,
                "2.0",
                PACKAGE_NAME_HMS,
                API_PREFIX_0 + MODULE_NAME_A,
                "1.0"
        );

        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, true);
        Hms.CompatibilityStatus status = Hms.CompatibilityStatus.ERROR_UNKNOWN;
        Exception exception = null;
        try {
            status = Hms.checkCompatibility(context, bundle);
        } catch (IllegalArgumentException e) {
            exception = e;
        } catch (CompatibilityException e) {
            exception = e;
        }
        Assert.assertNull(exception);
        Assert.assertNotEquals(Hms.CompatibilityStatus.ERROR_UNKNOWN, status);
        Assert.assertEquals(Hms.CompatibilityStatus.HMS_APP_UPDATE_REQUIRED, status);
    }

    @Test
    public void testCheckCompatibility17() throws Exception {
        mockSystemProperties0();
        mockSystemWrapper2(true, PACKAGE_NAME_HSP, API_PREFIX_0, API_PREFIX_1);
        mockSystemWrapperBuild0(1.0f);
        Context context = getMockContext1(PACKAGE_NAME_HMS);
        mockPackageManager2(
                context,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PACKAGE_NAME_HSP,
                API_PREFIX_1 + MODULE_NAME_A,
                "1.0",
                PACKAGE_NAME_HMS,
                API_PREFIX_0 + MODULE_NAME_A,
                "1.1"
        );

        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, true);
        Hms.CompatibilityStatus status = Hms.CompatibilityStatus.ERROR_UNKNOWN;
        Exception exception = null;
        try {
            status = Hms.checkCompatibility(context, bundle);
        } catch (IllegalArgumentException e) {
            exception = e;
        } catch (CompatibilityException e) {
            exception = e;
        }
        Assert.assertNull(exception);
        Assert.assertNotEquals(Hms.CompatibilityStatus.ERROR_UNKNOWN, status);
        Assert.assertEquals(Hms.CompatibilityStatus.HSP_UPDATE_REQUIRED, status);
    }

    @Test
    public void testCheckCompatibility18() throws Exception {
        mockSystemProperties0();
        mockSystemWrapper2(true, PACKAGE_NAME_HSP, API_PREFIX_0, API_PREFIX_1);
        mockSystemWrapperBuild0(1.0f);
        Context context = getMockContext1(PACKAGE_NAME_HMS);
        mockPackageManager2(
                context,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PACKAGE_NAME_HSP,
                API_PREFIX_1 + MODULE_NAME_A,
                "1.1",
                PACKAGE_NAME_HMS,
                API_PREFIX_0 + MODULE_NAME_A,
                "1.0"
        );

        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, true);
        Hms.CompatibilityStatus status = Hms.CompatibilityStatus.ERROR_UNKNOWN;
        Exception exception = null;
        try {
            status = Hms.checkCompatibility(context, bundle);
        } catch (IllegalArgumentException e) {
            exception = e;
        } catch (CompatibilityException e) {
            exception = e;
        }
        Assert.assertNull(exception);
        Assert.assertNotEquals(Hms.CompatibilityStatus.ERROR_UNKNOWN, status);
        Assert.assertEquals(Hms.CompatibilityStatus.COMPATIBLE, status);
    }

    @Test
    public void testCheckCompatibility19() throws Exception {
        mockSystemProperties0();
        mockSystemWrapper2(true, PACKAGE_NAME_HSP, API_PREFIX_0, API_PREFIX_1);
        mockSystemWrapperBuild0(1.0f);
        Context context = getMockContext1(PACKAGE_NAME_HMS);
        mockPackageManager2(
                context,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PACKAGE_NAME_HSP,
                API_PREFIX_1 + MODULE_NAME_A,
                "1.1.1",
                PACKAGE_NAME_HMS,
                API_PREFIX_0 + MODULE_NAME_A,
                "1.0.18"
        );

        Bundle bundle = new Bundle();
        bundle.putBoolean(Hms.BUNDLE_KEY_INCLUDE_GOOGLE_PLAY_EDITION, true);
        Hms.CompatibilityStatus status = Hms.CompatibilityStatus.ERROR_UNKNOWN;
        Exception exception = null;
        try {
            status = Hms.checkCompatibility(context, bundle);
        } catch (IllegalArgumentException e) {
            exception = e;
        } catch (CompatibilityException e) {
            exception = e;
        }
        Assert.assertNull(exception);
        Assert.assertNotEquals(Hms.CompatibilityStatus.ERROR_UNKNOWN, status);
        Assert.assertEquals(Hms.CompatibilityStatus.COMPATIBLE, status);
    }

    /**
     * Mock all SystemProperties values as empty
     */
    public static void mockSystemProperties0() {
        PowerMockito.mockStatic(SystemProperties.class);
        PowerMockito.when(SystemProperties.get(Mockito.anyString(), Mockito.anyString())).thenReturn("");
    }

    /**
     * <ul>
     * <li>SystemWrapper.getIgnoreHdkSupportCheck() : From your input argument</li>
     * </ul>
     */
    public static void mockSystemWrapper0(boolean ignoreHdkSupportCheck) {
        PowerMockito.mockStatic(SystemWrapper.class);
        PowerMockito.when(SystemWrapper.getIgnoreHdkSupportCheck()).thenReturn(ignoreHdkSupportCheck);
    }

    /**
     * <ul>
     * <li>SystemWrapper.getIgnoreHdkSupportCheck() : From your input argument</li>
     * <li>SystemWrapper.getHspPackageName() : From your input argument</li>
     * </ul>
     */
    public static void mockSystemWrapper1(boolean ignoreHdkSupportCheck, String hspPackageName) {
        PowerMockito.mockStatic(SystemWrapper.class);
        PowerMockito.when(SystemWrapper.getIgnoreHdkSupportCheck()).thenReturn(ignoreHdkSupportCheck);
        PowerMockito.when(SystemWrapper.getHspPackageName()).thenReturn(hspPackageName);
    }

    /**
     * <ul>
     * <li>SystemWrapper.getIgnoreHdkSupportCheck() : From your input argument</li>
     * <li>SystemWrapper.getHspPackageName() : From your input argument</li>
     * <li>SystemWrapper.getHdkApiPrefix() : From your input argument</li>
     * <li>SystemWrapper.getHspApiPrefix() : From your input argument</li>
     * </ul>
     */
    public static void mockSystemWrapper2(boolean ignoreHdkSupportCheck, String hspPackageName,
            String hmsApiPrefix, String hspApiPrefix) {
        PowerMockito.mockStatic(SystemWrapper.class);
        PowerMockito.when(SystemWrapper.getIgnoreHdkSupportCheck()).thenReturn(ignoreHdkSupportCheck);
        PowerMockito.when(SystemWrapper.getHspPackageName()).thenReturn(hspPackageName);
        PowerMockito.when(SystemWrapper.getHdkApiPrefix()).thenReturn(hmsApiPrefix);
        PowerMockito.when(SystemWrapper.getHspApiPrefix()).thenReturn(hspApiPrefix);
    }

    /**
     * <ul>
     * <li>SystemWrapper.Build.getHDKBaseVersion() : From your input argument</li>
     * </ul>
     */
    public static void mockSystemWrapperBuild0(float hdkBaseVersion) {
        PowerMockito.mockStatic(SystemWrapper.Build.class);
        PowerMockito.when(SystemWrapper.Build.getHDKBaseVersion()).thenReturn(hdkBaseVersion);
    }

    /**
     * Mock Context
     * <ul>
     * <li>Context.getApplicationContext() : <code>Context</code></li>
     * </ul>
     */
    public static Context getMockContext0() {
        Context context = PowerMockito.mock(Context.class);
        PowerMockito.when(context.getApplicationContext()).thenReturn(context);
        return context;
    }

    /**
     * Mock Context
     * <ul>
     * <li>Context.getApplicationContext() : <code>Context</code></li>
     * <li>Context.getPackageName() : From your input argument</li>
     * </ul>
     * 
     * @return mocked Context
     */
    public static Context getMockContext1(String hmsPackageName) {
        Context context = PowerMockito.mock(Context.class);
        PowerMockito.when(context.getApplicationContext()).thenReturn(context);
        PowerMockito.when(context.getPackageName()).thenReturn(hmsPackageName);
        return context;
    }

    /**
     * Mock PackageManager
     * <ul>
     * <li>PackageManager.hasSystemFeature(Mockito.anyString()) : From your input argument</li>
     * </ul>
     * 
     * @param context given (mocked) Context
     */
    public static void mockPackageManager0(Context context, boolean hasSystemFeature) {
        PackageManager packageManager = PowerMockito.mock(PackageManager.class);
        PowerMockito.when(context.getPackageManager()).thenReturn(packageManager);
        PowerMockito.when(packageManager.hasSystemFeature(Mockito.anyString())).thenReturn(hasSystemFeature);
    }

    /**
     * Mock PackageManager
     * <ul>
     * <li>PackageManager.getApplicationEnabledSetting(Mockito.anyString()) : From your input argument</li>
     * </ul>
     * 
     * @param context given (mocked) Context
     */
    public static void mockPackageManager1(Context context, int componentState) {
        PackageManager packageManager = PowerMockito.mock(PackageManager.class);
        PowerMockito.when(context.getPackageManager()).thenReturn(packageManager);
        PowerMockito.when(packageManager.getApplicationEnabledSetting(Mockito.anyString())).thenReturn(componentState);
    }

    /**
     * Mock PackageManager
     * <ul>
     * <li>PackageManager.getApplicationEnabledSetting(Mockito.anyString()) : From your input argument</li>
     * <li>PackageManager.getApplicationInfo(Mockito.eq("com.htc.sense.hsp"), Mockito.eq(PackageManager.GET_META_DATA)) : <code>ApplicationInfo</code> with dummy value</li>
     * <li>PackageManager.getApplicationInfo(Mockito.eq("com.htc.lib2.opensense.tests"), Mockito.eq(PackageManager.GET_META_DATA)) : <code>ApplicationInfo</code> with dummy value</li>
     * </ul>
     * 
     * @param context given (mocked) Context
     * @throws NameNotFoundException if happened
     */
    public static void mockPackageManager2(Context context, int componentState, String hspPackageName,
            String hspMetaDataKey, String hspMetaDataValue, String hmsPackageName, String hmsMetaDataKey,
            String hmsMetaDataValue) throws NameNotFoundException {
        PackageManager packageManager = PowerMockito.mock(PackageManager.class);
        PowerMockito.when(context.getPackageManager()).thenReturn(packageManager);
        PowerMockito.when(packageManager.getApplicationEnabledSetting(Mockito.anyString())).thenReturn(componentState);
        ApplicationInfo hspApplicationInfo = PowerMockito.mock(ApplicationInfo.class);
        if ( hspMetaDataKey != null || hspMetaDataValue != null ) {
            Bundle hspMetaData = new Bundle();
            hspMetaData.putString(hspMetaDataKey, hspMetaDataValue);
            hspApplicationInfo.metaData = hspMetaData;
        }
        PowerMockito.when(
                packageManager.getApplicationInfo(Mockito.eq(hspPackageName), Mockito.eq(PackageManager.GET_META_DATA))
        ).thenReturn(
                hspApplicationInfo
        );
        ApplicationInfo hmsApplicationInfo = PowerMockito.mock(ApplicationInfo.class);
        if ( hmsMetaDataKey != null || hmsMetaDataValue != null ) {
            Bundle hmsMetaData = new Bundle();
            hmsMetaData.putString(hmsMetaDataKey, hmsMetaDataValue);
            hmsApplicationInfo.metaData = hmsMetaData;
        }
        PowerMockito.when(
                packageManager.getApplicationInfo(Mockito.eq(hmsPackageName), Mockito.eq(PackageManager.GET_META_DATA))
        ).thenReturn(
                hmsApplicationInfo
        );
    }

    /**
     * Mock Log as dummy class
     */
    public static void mockLog0() {
        PowerMockito.mockStatic(Log.class);
        PowerMockito.when(Log.d(Mockito.anyString(), Mockito.anyString())).thenReturn(1);
        PowerMockito.when(Log.e(Mockito.anyString(), Mockito.anyString())).thenReturn(1);
        PowerMockito.when(Log.i(Mockito.anyString(), Mockito.anyString())).thenReturn(1);
        PowerMockito.when(Log.v(Mockito.anyString(), Mockito.anyString())).thenReturn(1);
        PowerMockito.when(Log.w(Mockito.anyString(), Mockito.anyString())).thenReturn(1);
    }
}
