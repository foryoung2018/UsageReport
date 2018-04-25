package com.htc.lib0.htcdebugflag.test;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;

import example.SampleClass;

@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({"android.*","org.robolectric.*"})
@PrepareForTest({SampleClass.class})
public class TestHtcWrapHtcDebugFlag {

	SampleClass sampleClass = new SampleClass();

	@Rule
	public PowerMockRule rule = new PowerMockRule();

	@Test
	public void test1() {
		PowerMockito.mockStatic(SampleClass.class);
		PowerMockito.when(SampleClass.webchatEnable(Mockito.anyString()))
				.thenReturn(false);
		Assert.assertEquals(false, sampleClass.modifyData());
	}

	@Test
    public void Htc_DEBUG_flag_test() {
	    boolean debugflag = HtcWrapHtcDebugFlag.Htc_DEBUG_flag;
	}

	@Test
    public void Htc_SECURITY_DEBUG_flag_test() {
        boolean debugflag = HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag;
    }
}
