/*
 * Copyright 2011 castLabs, Berlin
 *
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an AS IS BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.ultraviolet;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.coremedia.iso.Utf8;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractFullBox;

import java.nio.ByteBuffer;

/**
 * AssetInformationBox as defined Common File Format Spec.
 * @hide
 */
public class AssetInformationBox extends AbstractFullBox {
	/**
	 * @hide
	 */
    String apid = "";
    /**
     * @hide
     */
    String profileVersion = "0000";

    /**
     * @hide
     */
    public AssetInformationBox() {
        super("ainf");
    }
    /**
     * @hide
     */
    @Override
    protected long getContentSize() {
        return Utf8.utf8StringLengthInBytes(apid) + 9;
    }

    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        byteBuffer.put(Utf8.convert(profileVersion), 0, 4);
        byteBuffer.put(Utf8.convert(apid));
        byteBuffer.put((byte) 0);
    }

    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        profileVersion = IsoTypeReader.readString(content, 4);
        apid = IsoTypeReader.readString(content);
        content = null;
    }
    /**
     * @hide
     */
    public String getApid() {
        return apid;
    }
    /**
     * @hide
     */
    public void setApid(String apid) {
        this.apid = apid;
    }
    /**
     * @hide
     */
    public String getProfileVersion() {
        return profileVersion;
    }
    /**
     * @hide
     */
    public void setProfileVersion(String profileVersion) {
        assert profileVersion != null && profileVersion.length() == 4;
        this.profileVersion = profileVersion;
    }
}
