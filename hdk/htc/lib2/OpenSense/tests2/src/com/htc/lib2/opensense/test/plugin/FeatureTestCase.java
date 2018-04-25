package com.htc.lib2.opensense.test.plugin;

import com.htc.lib2.opensense.plugin.Feature;

import junit.framework.Assert;
import junit.framework.TestCase;

public class FeatureTestCase extends TestCase {

    private static final int FEATURE_ID = 33;
    private static final int FEATURE_VERSION = 5;
    private static final String FEATURE_NAME = "featureName";
    private static final String FEATURE_TYPE = "featureType";

    public void testConstructor1() throws Throwable {
        Feature feature = new Feature();
        Assert.assertEquals(0, feature.getId());
        Assert.assertEquals(0, feature.getVersion());
        Assert.assertEquals(null, feature.getName());
        Assert.assertEquals(null, feature.getType());
    }

    public void testConstructor2() throws Throwable {
        Feature feature = new Feature(FEATURE_NAME, FEATURE_TYPE);
        Assert.assertEquals(0, feature.getId());
        Assert.assertEquals(0, feature.getVersion());
        Assert.assertEquals(FEATURE_NAME, feature.getName());
        Assert.assertEquals(FEATURE_TYPE, feature.getType());
    }

    public void testConstructor3() throws Throwable {
        Feature feature = new Feature(FEATURE_ID, FEATURE_VERSION, FEATURE_NAME, FEATURE_TYPE);
        Assert.assertEquals(FEATURE_ID, feature.getId());
        Assert.assertEquals(FEATURE_VERSION, feature.getVersion());
        Assert.assertEquals(FEATURE_NAME, feature.getName());
        Assert.assertEquals(FEATURE_TYPE, feature.getType());
    }

    public void testSetId() throws Throwable {
        Feature feature = new Feature();
        feature.setId(FEATURE_ID);
        Assert.assertEquals(FEATURE_ID, feature.getId());
        Assert.assertEquals(0, feature.getVersion());
        Assert.assertEquals(null, feature.getName());
        Assert.assertEquals(null, feature.getType());
    }

    public void testSetVersion() throws Throwable {
        Feature feature = new Feature();
        feature.setVersion(FEATURE_VERSION);
        Assert.assertEquals(0, feature.getId());
        Assert.assertEquals(FEATURE_VERSION, feature.getVersion());
        Assert.assertEquals(null, feature.getName());
        Assert.assertEquals(null, feature.getType());
    }

    public void testSetName() throws Throwable {
        Feature feature = new Feature();
        feature.setName(FEATURE_NAME);
        Assert.assertEquals(0, feature.getId());
        Assert.assertEquals(0, feature.getVersion());
        Assert.assertEquals(FEATURE_NAME, feature.getName());
        Assert.assertEquals(null, feature.getType());
    }

    public void testSetType() throws Throwable {
        Feature feature = new Feature();
        feature.setType(FEATURE_TYPE);
        Assert.assertEquals(0, feature.getId());
        Assert.assertEquals(0, feature.getVersion());
        Assert.assertEquals(null, feature.getName());
        Assert.assertEquals(FEATURE_TYPE, feature.getType());
    }

    public void testSetAll1() throws Throwable {
        Feature feature = new Feature();
        feature.setId(FEATURE_ID);
        feature.setVersion(FEATURE_VERSION);
        feature.setName(FEATURE_NAME);
        feature.setType(FEATURE_TYPE);
        Assert.assertEquals(FEATURE_ID, feature.getId());
        Assert.assertEquals(FEATURE_VERSION, feature.getVersion());
        Assert.assertEquals(FEATURE_NAME, feature.getName());
        Assert.assertEquals(FEATURE_TYPE, feature.getType());
    }

    public void testSetAll2() throws Throwable {
        Feature feature = new Feature("123", "456");
        feature.setId(FEATURE_ID);
        feature.setVersion(FEATURE_VERSION);
        feature.setName(FEATURE_NAME);
        feature.setType(FEATURE_TYPE);
        Assert.assertEquals(FEATURE_ID, feature.getId());
        Assert.assertEquals(FEATURE_VERSION, feature.getVersion());
        Assert.assertEquals(FEATURE_NAME, feature.getName());
        Assert.assertEquals(FEATURE_TYPE, feature.getType());
    }

    public void testSetAll3() throws Throwable {
        Feature feature = new Feature(9, 4, "123", "456");
        feature.setId(FEATURE_ID);
        feature.setVersion(FEATURE_VERSION);
        feature.setName(FEATURE_NAME);
        feature.setType(FEATURE_TYPE);
        Assert.assertEquals(FEATURE_ID, feature.getId());
        Assert.assertEquals(FEATURE_VERSION, feature.getVersion());
        Assert.assertEquals(FEATURE_NAME, feature.getName());
        Assert.assertEquals(FEATURE_TYPE, feature.getType());
    }
}
