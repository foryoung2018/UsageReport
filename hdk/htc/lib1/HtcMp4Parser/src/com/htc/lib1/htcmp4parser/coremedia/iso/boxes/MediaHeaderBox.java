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
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractFullBox;

import java.nio.ByteBuffer;

/**
 * This box defines overall information which is media-independent, and relevant to the entire presentation
 * considered as a whole.
 * @hide
 * {@exthide}
 */
public class MediaHeaderBox extends AbstractFullBox {
	/**
	 * @hide
	 */
	public static final String TYPE = "mdhd";


    private long creationTime;
    private long modificationTime;
    private long timescale;
    private long duration;
    private String language;

    /**
     * @hide
     */
    public MediaHeaderBox() {
        super(TYPE);
    }

    /**
     * @hide
     */
    public long getCreationTime() {
        return creationTime;
    }

    /**
     * @hide
     */
    public long getModificationTime() {
        return modificationTime;
    }

    /**
     * @hide
     */
    public long getTimescale() {
        return timescale;
    }

    /**
     * @hide
     */
    public long getDuration() {
        return duration;
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
    protected long getContentSize() {
        long contentSize = 4;
        if (getVersion() == 1) {
            contentSize += 8 + 8 + 4 + 8;
        } else {
            contentSize += 4 + 4 + 4 + 4;
        }
        contentSize += 2;
        contentSize += 2;
        return contentSize;

    }

    /**
     * @hide
     */
    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    /**
     * @hide
     */
    public void setModificationTime(long modificationTime) {
        this.modificationTime = modificationTime;
    }

    /**
     * @hide
     */
    public void setTimescale(long timescale) {
        this.timescale = timescale;
    }

    /**
     * @hide
     */
    public void setDuration(long duration) {
        this.duration = duration;
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
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        if (getVersion() == 1) {
            creationTime = IsoTypeReader.readUInt64(content);
            modificationTime = IsoTypeReader.readUInt64(content);
            timescale = IsoTypeReader.readUInt32(content);
            duration = IsoTypeReader.readUInt64(content);
        } else {
            creationTime = IsoTypeReader.readUInt32(content);
            modificationTime = IsoTypeReader.readUInt32(content);
            timescale = IsoTypeReader.readUInt32(content);
            duration = IsoTypeReader.readUInt32(content);
        }
        language = IsoTypeReader.readIso639(content);
        IsoTypeReader.readUInt16(content);
    }

    /**
     * @hide
     */
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("MediaHeaderBox[");
        result.append("creationTime=").append(getCreationTime());
        result.append(";");
        result.append("modificationTime=").append(getModificationTime());
        result.append(";");
        result.append("timescale=").append(getTimescale());
        result.append(";");
        result.append("duration=").append(getDuration());
        result.append(";");
        result.append("language=").append(getLanguage());
        result.append("]");
        return result.toString();
    }

    /**
     * @hide
     */
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        if (getVersion() == 1) {
            IsoTypeWriter.writeUInt64(byteBuffer, creationTime);
            IsoTypeWriter.writeUInt64(byteBuffer, modificationTime);
            IsoTypeWriter.writeUInt32(byteBuffer, timescale);
            IsoTypeWriter.writeUInt64(byteBuffer, duration);
        } else {
            IsoTypeWriter.writeUInt32(byteBuffer, creationTime);
            IsoTypeWriter.writeUInt32(byteBuffer, modificationTime);
            IsoTypeWriter.writeUInt32(byteBuffer, timescale);
            IsoTypeWriter.writeUInt32(byteBuffer, duration);
        }
        IsoTypeWriter.writeIso639(byteBuffer, language);
        IsoTypeWriter.writeUInt16(byteBuffer, 0);
    }
}
