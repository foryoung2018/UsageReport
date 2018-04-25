package com.htc.lib2.opensense.test.social;

import com.htc.lib2.opensense.social.SyncType;

import junit.framework.Assert;
import junit.framework.TestCase;

public class SyncTypeTestCase extends TestCase {

    private static final String SYNCTYPE_ID = "com.htc.synctype.id";
    private static final String SYNCTYPE_TITLE = "SyncType Title";
    private static final String SYNCTYPE_TITLE_RES_NAME = "title_name";
    private static final String SYNCTYPE_SUBTITLE = "SyncType SubTitle";
    private static final String SYNCTYPE_SUBTITLE_RES_NAME = "subtitle_name";
    private static final String SYNCTYPE_PACKAGE_NAME = "com.htc.package.test";
    private static final String SYNCTYPE_EDITION = "SyncType Edition";
    private static final String SYNCTYPE_EDITION_RES_NAME = "edition_name";
    private static final String SYNCTYPE_CATEGORY = "SyncType Category";
    private static final String SYNCTYPE_CATEGORY_RES_NAME = "category_name";
    private static final String SYNCTYPE_ICON_URL = "SyncType Icon URL";
    private static final String SYNCTYPE_ICON_RES_NAME = "icon_name";
    private static final String SYNCTYPE_CATEGORY_ICON_URL = "SyncType Category Icon URL";
    private static final String SYNCTYPE_CATEGORY_ICON_RES_NAME = "category_icon_name";
    private static final int SYNCTYPE_COLOR = 0xffaabbcc;
    private static final int SYNCTYPE_CATEGORY_ICON_COLOR = 0xff114477;

    public void testConstructor() {
        SyncType oldSyncType = new SyncType();
        SyncType newSyncType = new SyncType();
        Assert.assertEquals(oldSyncType, newSyncType);
    }

    public void testSetId() throws Throwable {
        SyncType oldSyncType = new SyncType();
        oldSyncType.setId(SYNCTYPE_ID);
        SyncType newSyncType = new SyncType();
        newSyncType.setId(SYNCTYPE_ID);
        Assert.assertEquals(oldSyncType, newSyncType);
    }

    public void testSetTitle() throws Throwable {
        SyncType oldSyncType = new SyncType();
        oldSyncType.setTitle(SYNCTYPE_TITLE);
        SyncType newSyncType = new SyncType();
        newSyncType.setTitle(SYNCTYPE_TITLE);
        Assert.assertEquals(oldSyncType, newSyncType);
    }

    public void testSetSubTitle() throws Throwable {
        SyncType oldSyncType = new SyncType();
        oldSyncType.setSubTitle(SYNCTYPE_SUBTITLE);
        SyncType newSyncType = new SyncType();
        newSyncType.setSubTitle(SYNCTYPE_SUBTITLE);
        Assert.assertEquals(oldSyncType, newSyncType);
    }

    public void testSetPackageName() throws Throwable {
        SyncType oldSyncType = new SyncType();
        oldSyncType.setPackageName(SYNCTYPE_PACKAGE_NAME);
        SyncType newSyncType = new SyncType();
        newSyncType.setPackageName(SYNCTYPE_PACKAGE_NAME);
        Assert.assertEquals(oldSyncType, newSyncType);
    }

    public void testSetSubTitleResName() throws Throwable {
        SyncType oldSyncType = new SyncType();
        oldSyncType.setSubTitleResName(SYNCTYPE_SUBTITLE_RES_NAME);
        SyncType newSyncType = new SyncType();
        newSyncType.setSubTitleResName(SYNCTYPE_SUBTITLE_RES_NAME);
        Assert.assertEquals(oldSyncType, newSyncType);
    }

    public void testSetTitleResName() throws Throwable {
        SyncType oldSyncType = new SyncType();
        oldSyncType.setTitleResName(SYNCTYPE_TITLE_RES_NAME);
        SyncType newSyncType = new SyncType();
        newSyncType.setTitleResName(SYNCTYPE_TITLE_RES_NAME);
        Assert.assertEquals(oldSyncType, newSyncType);
    }

    public void testSetColor() throws Throwable {
        SyncType oldSyncType = new SyncType();
        oldSyncType.setColor(SYNCTYPE_COLOR);
        SyncType newSyncType = new SyncType();
        newSyncType.setColor(SYNCTYPE_COLOR);
        Assert.assertEquals(oldSyncType, newSyncType);
    }

    public void testSetEditionResName() throws Throwable {
        SyncType oldSyncType = new SyncType();
        oldSyncType.setEditionResName(SYNCTYPE_EDITION_RES_NAME);
        SyncType newSyncType = new SyncType();
        newSyncType.setEditionResName(SYNCTYPE_EDITION_RES_NAME);
        Assert.assertEquals(oldSyncType, newSyncType);
    }

    public void testSetEdition() throws Throwable {
        SyncType oldSyncType = new SyncType();
        oldSyncType.setEdition(SYNCTYPE_EDITION);
        SyncType newSyncType = new SyncType();
        newSyncType.setEdition(SYNCTYPE_EDITION);
        Assert.assertEquals(oldSyncType, newSyncType);
    }

    public void testSetCategoryResName() throws Throwable {
        SyncType oldSyncType = new SyncType();
        oldSyncType.setCategoryResName(SYNCTYPE_CATEGORY_RES_NAME);
        SyncType newSyncType = new SyncType();
        newSyncType.setCategoryResName(SYNCTYPE_CATEGORY_RES_NAME);
        Assert.assertEquals(oldSyncType, newSyncType);
    }

    public void testSetCategory() throws Throwable {
        SyncType oldSyncType = new SyncType();
        oldSyncType.setCategory(SYNCTYPE_CATEGORY);
        SyncType newSyncType = new SyncType();
        newSyncType.setCategory(SYNCTYPE_CATEGORY);
        Assert.assertEquals(oldSyncType, newSyncType);
    }

    public void testSetIconUrl() throws Throwable {
        SyncType oldSyncType = new SyncType();
        oldSyncType.setIconUrl(SYNCTYPE_ICON_URL);
        SyncType newSyncType = new SyncType();
        newSyncType.setIconUrl(SYNCTYPE_ICON_URL);
        Assert.assertEquals(oldSyncType, newSyncType);
    }

    public void testSetIconResName() throws Throwable {
        SyncType oldSyncType = new SyncType();
        oldSyncType.setIconResName(SYNCTYPE_ICON_RES_NAME);
        SyncType newSyncType = new SyncType();
        newSyncType.setIconResName(SYNCTYPE_ICON_RES_NAME);
        Assert.assertEquals(oldSyncType, newSyncType);
    }

    public void testSetCategoryIconUrl() throws Throwable {
        SyncType oldSyncType = new SyncType();
        oldSyncType.setCategoryIconUrl(SYNCTYPE_CATEGORY_ICON_URL);
        SyncType newSyncType = new SyncType();
        newSyncType.setCategoryIconUrl(SYNCTYPE_CATEGORY_ICON_URL);
        Assert.assertEquals(oldSyncType, newSyncType);
    }

    public void testSetCategoryIconResName() throws Throwable {
        SyncType oldSyncType = new SyncType();
        oldSyncType.setCategoryIconResName(SYNCTYPE_CATEGORY_ICON_RES_NAME);
        SyncType newSyncType = new SyncType();
        newSyncType.setCategoryIconResName(SYNCTYPE_CATEGORY_ICON_RES_NAME);
        Assert.assertEquals(oldSyncType, newSyncType);
    }

    public void testSetCategoryIconColor() throws Throwable {
        SyncType oldSyncType = new SyncType();
        oldSyncType.setCategoryIconColor(SYNCTYPE_CATEGORY_ICON_COLOR);
        SyncType newSyncType = new SyncType();
        newSyncType.setCategoryIconColor(SYNCTYPE_CATEGORY_ICON_COLOR);
        Assert.assertEquals(oldSyncType, newSyncType);
    }

    public void testSetAll() throws Throwable {
        SyncType oldSyncType = new SyncType();
        oldSyncType.setId(SYNCTYPE_ID);
        oldSyncType.setTitle(SYNCTYPE_TITLE);
        oldSyncType.setSubTitle(SYNCTYPE_SUBTITLE);
        oldSyncType.setPackageName(SYNCTYPE_PACKAGE_NAME);
        oldSyncType.setSubTitleResName(SYNCTYPE_SUBTITLE_RES_NAME);
        oldSyncType.setTitleResName(SYNCTYPE_TITLE_RES_NAME);
        oldSyncType.setColor(SYNCTYPE_COLOR);
        oldSyncType.setEditionResName(SYNCTYPE_EDITION_RES_NAME);
        oldSyncType.setEdition(SYNCTYPE_EDITION);
        oldSyncType.setCategoryResName(SYNCTYPE_CATEGORY_RES_NAME);
        oldSyncType.setCategory(SYNCTYPE_CATEGORY);
        oldSyncType.setIconUrl(SYNCTYPE_ICON_URL);
        oldSyncType.setIconResName(SYNCTYPE_ICON_RES_NAME);
        oldSyncType.setCategoryIconUrl(SYNCTYPE_CATEGORY_ICON_URL);
        oldSyncType.setCategoryIconResName(SYNCTYPE_CATEGORY_ICON_RES_NAME);
        oldSyncType.setCategoryIconColor(SYNCTYPE_CATEGORY_ICON_COLOR);
        SyncType newSyncType = new SyncType();
        newSyncType.setId(SYNCTYPE_ID);
        newSyncType.setTitle(SYNCTYPE_TITLE);
        newSyncType.setSubTitle(SYNCTYPE_SUBTITLE);
        newSyncType.setPackageName(SYNCTYPE_PACKAGE_NAME);
        newSyncType.setSubTitleResName(SYNCTYPE_SUBTITLE_RES_NAME);
        newSyncType.setTitleResName(SYNCTYPE_TITLE_RES_NAME);
        newSyncType.setColor(SYNCTYPE_COLOR);
        newSyncType.setEditionResName(SYNCTYPE_EDITION_RES_NAME);
        newSyncType.setEdition(SYNCTYPE_EDITION);
        newSyncType.setCategoryResName(SYNCTYPE_CATEGORY_RES_NAME);
        newSyncType.setCategory(SYNCTYPE_CATEGORY);
        newSyncType.setIconUrl(SYNCTYPE_ICON_URL);
        newSyncType.setIconResName(SYNCTYPE_ICON_RES_NAME);
        newSyncType.setCategoryIconUrl(SYNCTYPE_CATEGORY_ICON_URL);
        newSyncType.setCategoryIconResName(SYNCTYPE_CATEGORY_ICON_RES_NAME);
        newSyncType.setCategoryIconColor(SYNCTYPE_CATEGORY_ICON_COLOR);
        Assert.assertEquals(oldSyncType, newSyncType);
    }
}
