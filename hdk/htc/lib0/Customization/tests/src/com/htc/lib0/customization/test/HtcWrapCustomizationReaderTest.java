package com.htc.lib0.customization.test;

import com.htc.lib0.customization.HtcWrapCustomizationManager;
import com.htc.lib0.customization.HtcWrapCustomizationReader;

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
public class HtcWrapCustomizationReaderTest {

	@Rule
	public PowerMockRule rule = new PowerMockRule();

	private HtcWrapCustomizationReader reader;
	private HtcWrapCustomizationReader nullReader;

	@Before
	public void setUp() throws Exception {
	    HtcWrapCustomizationManager manager = new HtcWrapCustomizationManager();
	    reader = manager.getCustomizationReader("name", 0, false);
	    nullReader = new HtcWrapCustomizationReader(null);
	}

	@Test
    public void testReadInteger() {
	    Assert.assertEquals(Integer.MAX_VALUE, reader.readInteger("ACC", Integer.MAX_VALUE));
	}

	@Test
    public void testReadIntegerNullReader() {
        Assert.assertEquals(Integer.MAX_VALUE, nullReader.readInteger("ACC", Integer.MAX_VALUE));
    }

	@Test
    public void testReadString() {
	    Assert.assertEquals("", reader.readString("ACC", ""));
    }

	@Test
    public void testReadStringNullReader() {
        Assert.assertEquals("", nullReader.readString("ACC", ""));
    }

	@Test
    public void testReadNullableBoolean() {
	    Assert.assertNull(reader.readNullableBoolean("ACC", null));
    }

	@Test
    public void testReadNullableBooleanNullReader() {
        Assert.assertNull(nullReader.readNullableBoolean("ACC", null));
    }

	@Test
    public void testReadBoolean() {
	    Assert.assertTrue(reader.readBoolean("ACC", true));
    }

	@Test
    public void testReadBooleanNullReader() {
        Assert.assertTrue(nullReader.readBoolean("ACC", true));
    }

	@Test
    public void testReadByte() {
	    Assert.assertEquals(Byte.MAX_VALUE, reader.readByte("ACC", Byte.MAX_VALUE));
    }

	@Test
    public void testReadByteNullReader() {
        Assert.assertEquals(Byte.MAX_VALUE, nullReader.readByte("ACC", Byte.MAX_VALUE));
    }

	@Test
    public void testReadIntArray() {
	    Assert.assertNull(reader.readIntArray("ACC", null));
    }

	@Test
    public void testReadIntArrayNullReader() {
        Assert.assertNull(nullReader.readIntArray("ACC", null));
    }

	@Test
    public void testReadStringArray() {
	    Assert.assertNull(reader.readStringArray("ACC", null));
    }

	@Test
    public void testReadStringArrayNullReader() {
        Assert.assertNull(nullReader.readStringArray("ACC", null));
    }
}
