/*
 * HTC Corporation Proprietary Rights Acknowledgment
 *
 * Copyright (C) 2013 HTC Corporation
 *
 * All Rights Reserved.
 *
 * The information contained in this work is the exclusive property of
 * HTC Corporation ("HTC"). Only the user who is legally
 * authorized by HTC ("Authorized User") has right to employ this work
 * within the scope of this statement. Nevertheless, the Authorized User
 * shall not use this work for any purpose other than the purpose agreed by HTC.
 * Any and all addition or modification to this work shall be unconditionally
 * granted back to HTC and such addition or modification shall be solely owned by HTC.
 * No right is granted under this statement, including but not limited to,
 * distribution, reproduction, and transmission, except as otherwise provided in this statement.
 * Any other usage of this work shall be subject to the further written consent of HTC.
 *
 *
 */

package com.htc.lib0.media.zoe.test;

import junit.framework.Assert;
import junit.framework.TestCase;


import com.htc.lib0.media.zoe.HtcZoeMetadata;

/**
 * @author Vin Huang(vin_huang@htc.com)
 */
public class HtcZoeUtilTest extends TestCase{

    private static final String[] dataKey = {HtcZoeMetadata.HTC_DATA_ZOE_JPEG};

    private static final String[] metaDataKeyForInt = {HtcZoeMetadata.HTC_METADATA_ZOE_COVER_INDEX,
                                                       HtcZoeMetadata.HTC_METADATA_ZOE_SHOT_INDEX,
                                                       HtcZoeMetadata.HTC_METADATA_ZOE_PHOTO_WIDTH,
                                                       HtcZoeMetadata.HTC_METADATA_ZOE_PHOTO_HEIGHT,
                                                       HtcZoeMetadata.HTC_METADATA_DUAL_LENS,
                                                       HtcZoeMetadata.HTC_METADATA_SLOW_MOTION};

    private static final String[] metaDataKeyForString = {HtcZoeMetadata.HTC_METADATA_MEDIA_TAKEN_TIME,
                                                          HtcZoeMetadata.HTC_METADATA_KEY_LOCATION};

    private static final String[] keyReadOnly = {HtcZoeMetadata.HTC_METADATA_SLOW_MOTION,
                                                 HtcZoeMetadata.HTC_METADATA_KEY_LOCATION};

    private static final String[] invalidFormatKey = {null, "", "abcde", "abc"};

    /*
     * Test Data keys are valid
     */
    public void testIsDataKeyValid(){
        for(String key : dataKey){
            Assert.assertTrue(HtcZoeMetadata.isDataKeyValid(key));
        }
    }

    /*
     * Test Integer/String keys are invald to use as Data Key.
     */
    public void testIsDataKeyValid_useInvalidKey(){
        for(String key : metaDataKeyForInt){
            Assert.assertFalse(HtcZoeMetadata.isDataKeyValid(key));
        }
        for(String key : metaDataKeyForString){
            Assert.assertFalse(HtcZoeMetadata.isDataKeyValid(key));
        }
    }

    /*
     * Test Data key in unsupport format is invalid to use.
     * format of key: 4 characters. ex, "abcd"
     */
    public void testIsDataKeyValid_useInvalidFormat(){
        for(String key: invalidFormatKey){
            try{
                HtcZoeMetadata.isDataKeyValid(key);
                Assert.fail();
            }catch(IllegalArgumentException e){
            }
        }
    }

    /*
     * Test Integer keys are valid
     */
    public void testIsIntKeyValid(){
        for(String key : metaDataKeyForInt){
            Assert.assertTrue(HtcZoeMetadata.isMetadataKeyValidForInt(key));
        }
    }

    /*
     * Test Data/String keys are invald to use as Integer Key.
     */
    public void testIsIntKeyValid_useInvalidKey(){
        for(String key : dataKey){
            Assert.assertFalse(HtcZoeMetadata.isMetadataKeyValidForInt(key));
        }
        for(String key : metaDataKeyForString){
            Assert.assertFalse(HtcZoeMetadata.isMetadataKeyValidForInt(key));
        }
    }

    /*
     * Test Integer key in unsupport format is invalid to use.
     * format of key: 4 characters. ex, "abcd"
     */
    public void testIsIntKeyValid_useInvalidFormat(){
        for(String key: invalidFormatKey){
            try{
                HtcZoeMetadata.isMetadataKeyValidForInt(key);
                Assert.fail();
            }catch(IllegalArgumentException e){
            }
        }
    }

    /*
     * Test String keys are valid
     */
    public void testIsStringKeyValid(){
        for(String key : metaDataKeyForString){
            Assert.assertTrue(HtcZoeMetadata.isMetadataKeyValidForString(key));
        }
    }

    /*
     * Test Data/Integer keys are invald to use as String Key.
     */
    public void testIsStringKeyValid_useInvalidKey(){
        for(String key : dataKey){
            Assert.assertFalse(HtcZoeMetadata.isMetadataKeyValidForString(key));
        }
        for(String key : metaDataKeyForInt){
            Assert.assertFalse(HtcZoeMetadata.isMetadataKeyValidForString(key));
        }
    }

    /*
     * Test String key in unsupport format is invalid to use.
     * format of key: 4 characters. ex, "abcd"
     */
    public void testIsStringKeyValid_useInvalidFormat(){
        for(String key: invalidFormatKey){
            try{
                HtcZoeMetadata.isMetadataKeyValidForString(key);
                Assert.fail();
            }catch(IllegalArgumentException e){
            }
        }
    }

    /*
     * Test Read Only keys are valid
     */
    public void testIsReadOnlyKeyValid(){
        for(String key : keyReadOnly){
            Assert.assertTrue(HtcZoeMetadata.isKeyReadOnly(key));
        }
    }

    /*
     * Test Not ReadOnly keys are invald to use as Read Only Key.
     */
    public void testIsReadOnlyKeyValid_useInvalidKey(){
        Assert.assertFalse(HtcZoeMetadata.isKeyReadOnly(HtcZoeMetadata.HTC_DATA_ZOE_JPEG));
    }

    /*
     * Test Read Only key in unsupport format is invalid to use.
     * format of key: 4 characters. ex, "abcd"
     */
    public void testIsReadOnlyKeyValid_useInvalidFormat(){
        for(String key: invalidFormatKey){
            try{
                HtcZoeMetadata.isKeyReadOnly(key);
                Assert.fail();
            }catch(IllegalArgumentException e){
            }
        }
    }
}
