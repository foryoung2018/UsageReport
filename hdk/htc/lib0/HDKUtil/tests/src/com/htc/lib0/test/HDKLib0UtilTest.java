package com.htc.lib0.test;

import android.app.Activity;
import android.os.Build;

import com.htc.lib0.HDKLib0Util;
import com.htc.lib0.HDKLib0Util.HDK_VERSION_CODES;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.res.builder.RobolectricPackageManager;

@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({"android.*","org.robolectric.*"})
@PrepareForTest({HDKLib0Util.class})
public class HDKLib0UtilTest {

	@Rule
	public PowerMockRule rule = new PowerMockRule();

	@Test
    public void testIsHDKLib3SupportedInDevice() {
	    Assert.assertTrue(HDKLib0Util.isHDKLib3SupportedInDevice() <= 3);
	}

	private Float fakeGetHDKBaseVersion() {
	    return HDK_VERSION_CODES.KITKAT_1;
	}

	@Test
    public void getHDKVersionTest() {
        HDKLib0Util.getHDKVersion();
    }

	@Test
    public void getHDKBaseVersionTest() {
        HDKLib0Util.getHDKBaseVersion();
    }

	@Test
    public void testIsODMDeviceContextIsNull() {
        Boolean rtn = HDKLib0Util.isODMDevice(null);
        Assert.assertNull(rtn);
    }

	@Test
    public void testIsODMDeviceHasSystemFeature() {
	    Activity mContext = Robolectric.buildActivity(Activity.class).create().get();
	    RobolectricPackageManager pm =
                (RobolectricPackageManager)Robolectric.application.getPackageManager();
        pm.setSystemFeature("com.htc.software.ODF", true);
	    Assert.assertTrue(HDKLib0Util.isODMDevice(mContext));
    }

	@Test
    public void testIsODMDeviceNotHasSystemFeature() {
        Activity mContext = Robolectric.buildActivity(Activity.class).create().get();
        RobolectricPackageManager pm =
                (RobolectricPackageManager)Robolectric.application.getPackageManager();
        pm.setSystemFeature("com.htc.software.ODF", false);
        Assert.assertFalse(HDKLib0Util.isODMDevice(mContext));
    }

	@Test
    public void testIsHTCDevice() {
	    Assert.assertTrue(HDKLib0Util.isHTCDevice());
    }

	@Test
    public void testIsStockUIDeviceContextIsNull() {
	    Boolean rtn = HDKLib0Util.isStockUIDevice(null);
        Assert.assertNull(rtn);
    }

	@Test
    public void testIsStockUIDeviceNoHTCFeature() {
	    Activity mContext = Robolectric.buildActivity(Activity.class).create().get();
	    RobolectricPackageManager pm =
	            (RobolectricPackageManager)Robolectric.application.getPackageManager();
	    pm.setSystemFeature("com.htc.software.HTC", false);
	    Assert.assertTrue(HDKLib0Util.isStockUIDevice(mContext));
    }

	@Test
    public void testIsStockUIDeviceHasHTCFeature() {
        Activity mContext = Robolectric.buildActivity(Activity.class).create().get();
        RobolectricPackageManager pm =
                (RobolectricPackageManager)Robolectric.application.getPackageManager();
        pm.setSystemFeature("com.htc.software.HTC", true);
        Assert.assertFalse(HDKLib0Util.isStockUIDevice(mContext));
    }

	@Test
    public void testIsHEPDeviceContextIsNull() {
	    Boolean rtn = HDKLib0Util.isHEPDevice(null);
	    Assert.assertNull(rtn);
    }

	@Test
    public void testIsHEPDeviceNoODMFeature() {
	    Activity mContext = Robolectric.buildActivity(Activity.class).create().get();
	    RobolectricPackageManager pm =
                (RobolectricPackageManager)Robolectric.application.getPackageManager();

	    pm.setSystemFeature("com.htc.software.HTC", true);
	    pm.setSystemFeature("com.htc.software.ODF", false);
	    Assert.assertTrue(HDKLib0Util.isHEPDevice(mContext));
    }

	@Test
    public void testIsHEPDeviceHasODMFeature() {
        Activity mContext = Robolectric.buildActivity(Activity.class).create().get();
        RobolectricPackageManager pm =
                (RobolectricPackageManager)Robolectric.application.getPackageManager();

        pm.setSystemFeature("com.htc.software.HTC", true);
        pm.setSystemFeature("com.htc.software.ODF", true);
        Assert.assertFalse(HDKLib0Util.isHEPDevice(mContext));
    }

	@Test
	public void testHDKException() {
	    new HDKLib0Util.HDKException();
	}

	@Test
    public void testHDKExceptionWithMessage() {
        new HDKLib0Util.HDKException("HDKException");
    }
}
