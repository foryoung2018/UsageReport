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

package com.htc.lib1.htcmp4parser.coremedia.iso.boxes;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;
import com.htc.lib1.htcmp4parser.coremedia.iso.Utf8;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractFullBox;

import java.nio.ByteBuffer;

/**
 * Meta information in a 'udta' box about a track.
 * Defined in 3GPP 26.244.
 * @hide
 * {@exthide}
 *
 * @see com.htc.lib1.htcmp4parser.coremedia.iso.boxes.UserDataBox
 */
public class AlbumBox extends AbstractFullBox {
	/**
	 *  @hide
	 */
	public static final String TYPE = "albm";

    private String language;
    private String albumTitle;
    private int trackNumber;
    
    /**
	 *  @hide
	 */
    public AlbumBox() {
        super(TYPE);
    }

    /**
     * Declares the language code for the {@link #getAlbumTitle()} return value. See ISO 639-2/T for the set of three
     * character codes.Each character is packed as the difference between its ASCII value and 0x60. The code is
     * confined to being three lower-case letters, so these values are strictly positive.
     * *  @hide
     * @return the language code
     */
    public String getLanguage() {
        return language;
    }
    
    /**
	 *  @hide
	 */
    public String getAlbumTitle() {
        return albumTitle;
    }

    /**
	 *  @hide
	 */
    public int getTrackNumber() {
        return trackNumber;
    }

    /**
	 *  @hide
	 */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
	 *  @hide
	 */
    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    /**
	 *  @hide
	 */
    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }

    /**
	 *  @hide
	 */
    protected long getContentSize() {
        return 6 + Utf8.utf8StringLengthInBytes(albumTitle) + 1 + (trackNumber == -1 ? 0 : 1);
    }

    /**
	 *  @hide
	 */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        language = IsoTypeReader.readIso639(content);
        albumTitle = IsoTypeReader.readString(content);

        if (content.remaining() > 0) {
            trackNumber = IsoTypeReader.readUInt8(content);
        } else {
            trackNumber = -1;
        }
    }

    /**
	 *  @hide
	 */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeIso639(byteBuffer, language);
        byteBuffer.put(Utf8.convert(albumTitle));
        byteBuffer.put((byte) 0);
        if (trackNumber != -1) {
            IsoTypeWriter.writeUInt8(byteBuffer, trackNumber);
        }
    }

    /**
	 *  @hide
	 */
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("AlbumBox[language=").append(getLanguage()).append(";");
        buffer.append("albumTitle=").append(getAlbumTitle());
        if (trackNumber >= 0) {
            buffer.append(";trackNumber=").append(getTrackNumber());
        }
        buffer.append("]");
        return buffer.toString();
    }
}
