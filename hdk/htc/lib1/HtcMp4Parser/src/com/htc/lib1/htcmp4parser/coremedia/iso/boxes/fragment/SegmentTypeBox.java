/*
 * Copyright 2008 CoreMedia AG, Hamburg
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

package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.fragment;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoFile;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractBox;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.annotations.DoNotParseDetail;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This box identifies the specifications to which this file complies. <br>
 * Each brand is a printable four-character code, registered with ISO, that
 * identifies a precise specification.
 *  @hide
 * {@exthide}
 */
public class SegmentTypeBox extends AbstractBox {
    /**
     * @hide
     */
    public static final String TYPE = "styp";

    private String majorBrand;
    private long minorVersion;
    private List<String> compatibleBrands = Collections.emptyList();
    /**
     * @hide
     */
    public SegmentTypeBox() {
        super(TYPE);
    }
    /**
     * @hide
     */
    public SegmentTypeBox(String majorBrand, long minorVersion, List<String> compatibleBrands) {
        super(TYPE);
        this.majorBrand = majorBrand;
        this.minorVersion = minorVersion;
        this.compatibleBrands = compatibleBrands;
    }
    /**
     * @hide
     */
    protected long getContentSize() {
        return 8 + compatibleBrands.size() * 4;

    }
    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        majorBrand = IsoTypeReader.read4cc(content);
        minorVersion = IsoTypeReader.readUInt32(content);
        int compatibleBrandsCount = content.remaining() / 4;
        compatibleBrands = new LinkedList<String>();
        for (int i = 0; i < compatibleBrandsCount; i++) {
            compatibleBrands.add(IsoTypeReader.read4cc(content));
        }
    }
    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        byteBuffer.put(IsoFile.fourCCtoBytes(majorBrand));
        IsoTypeWriter.writeUInt32(byteBuffer, minorVersion);
        for (String compatibleBrand : compatibleBrands) {
            byteBuffer.put(IsoFile.fourCCtoBytes(compatibleBrand));
        }

    }

    /**
     * Gets the brand identifier.
     *
     * @return the brand identifier
     * @hide
     */
    public String getMajorBrand() {
        return majorBrand;
    }

    /**
     * Sets the major brand of the file used to determine an appropriate reader.
     *
     * @param majorBrand the new major brand
     * @hide
     */
    public void setMajorBrand(String majorBrand) {
        this.majorBrand = majorBrand;
    }

    /**
     * Sets the "informative integer for the minor version of the major brand".
     *
     * @param minorVersion the version number of the major brand
     * @hide
     */
    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }

    /**
     * Gets an informative integer for the minor version of the major brand.
     *
     * @return an informative integer
     * @see SegmentTypeBox#getMajorBrand()
     * @hide
     */
    public long getMinorVersion() {
        return minorVersion;
    }

    /**
     * Gets an array of 4-cc brands.
     *
     * @return the compatible brands
     * @hide
     */
    public List<String> getCompatibleBrands() {
        return compatibleBrands;
    }
    /**
     * @hide
     */
    public void setCompatibleBrands(List<String> compatibleBrands) {
        this.compatibleBrands = compatibleBrands;
    }
    /**
     * @hide
     */
    @DoNotParseDetail
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("SegmentTypeBox[");
        result.append("majorBrand=").append(getMajorBrand());
        result.append(";");
        result.append("minorVersion=").append(getMinorVersion());
        for (String compatibleBrand : compatibleBrands) {
            result.append(";");
            result.append("compatibleBrand=").append(compatibleBrand);
        }
        result.append("]");
        return result.toString();
    }
}
