package com.htc.lib0.customization.test;

import com.htc.lib0.customization.HtcWrapCustomizationManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({"android.*","org.robolectric.*"})
@PrepareForTest({})
public class HtcWrapCustomizationManagerTest {

	@Rule
	public PowerMockRule rule = new PowerMockRule();

	private HtcWrapCustomizationManager manager;

	@Before
    public void setUp() throws Exception {
	    manager = new HtcWrapCustomizationManager();
	}

	@Test
    public void testReadCID() {
	    manager.readCID();
	}

	@Test
    public void testGetCustomizationReader() {
	    manager.getCustomizationReader(
	            "system", HtcWrapCustomizationManager.READER_TYPE_XML, false);
    }
}
