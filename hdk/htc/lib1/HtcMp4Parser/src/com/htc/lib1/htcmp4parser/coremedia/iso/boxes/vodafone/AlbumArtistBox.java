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

package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.vodafone;


import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;
import com.htc.lib1.htcmp4parser.coremedia.iso.Utf8;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractFullBox;

import java.nio.ByteBuffer;

/**
 * Special box used by Vodafone in their DCF containing information about the artist. Mainly used for OMA DCF files
 * containing music. Resides in the {@link com.htc.lib1.htcmp4parser.coremedia.iso.boxes.UserDataBox}.
 *  @hide
 * {@exthide}
 */
public class AlbumArtistBox extends AbstractFullBox {
    /**
     * @hide
     */
    public static final String TYPE = "albr";

    private String language;
    private String albumArtist;
    /**
     * @hide
     */
    public AlbumArtistBox() {
        super(TYPE);
    }
    /**
     * @hide
     */
    public String getLanguage() {
        return language;
    }
    /**
     * @hide
     */
    public String getAlbumArtist() {
        return albumArtist;
    }
    /**
     * @hide
     */
    public void setLanguage(String language) {
        this.language = language;
    }
    /**
     * @hide
     */
    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist;
    }
    /**
     * @hide
     */
    protected long getContentSize() {
        return 6 + Utf8.utf8StringLengthInBytes(albumArtist) + 1;
    }
    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        language = IsoTypeReader.readIso639(content);
        albumArtist = IsoTypeReader.readString(content);
    }
    /**
     * @hide
     */
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeIso639(byteBuffer, language);
        byteBuffer.put(Utf8.convert(albumArtist));
        byteBuffer.put((byte) 0);
    }
    /**
     * @hide
     */
    public String toString() {
        return "AlbumArtistBox[language=" + getLanguage() + ";albumArtist=" + getAlbumArtist() + "]";
    }
}
