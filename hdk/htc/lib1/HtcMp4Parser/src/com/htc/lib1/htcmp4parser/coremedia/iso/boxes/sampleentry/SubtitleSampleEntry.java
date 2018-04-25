package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;

import java.nio.ByteBuffer;

/**
 * Created by IntelliJ IDEA.
 * User: magnus
 * Date: 2012-03-08
 * Time: 11:36
 * To change this template use File | Settings | File Templates.
 *  @hide
 * {@exthide}
 */
public class SubtitleSampleEntry extends SampleEntry {
    /**
     * @hide
     */
    public static final String TYPE1 = "stpp";
    /**
     * @hide
     */
    public static final String TYPE_ENCRYPTED = ""; // This is not known!

    private String namespace;
    private String schemaLocation;
    private String imageMimeType;
    /**
     * @hide
     */
    public SubtitleSampleEntry(String type) {
        super(type);
    }
    /**
     * @hide
     */
    @Override
    protected long getContentSize() {
        long contentSize = 8 + namespace.length() + schemaLocation.length() + imageMimeType.length() + 3;
        return contentSize;
    }
    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        _parseReservedAndDataReferenceIndex(content);
        namespace = IsoTypeReader.readString(content);
        schemaLocation = IsoTypeReader.readString(content);
        imageMimeType = IsoTypeReader.readString(content);
        _parseChildBoxes(content);
    }
    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        _writeReservedAndDataReferenceIndex(byteBuffer);
        IsoTypeWriter.writeUtf8String(byteBuffer, namespace);
        IsoTypeWriter.writeUtf8String(byteBuffer, schemaLocation);
        IsoTypeWriter.writeUtf8String(byteBuffer, imageMimeType);
    }
    /**
     * @hide
     */
    public String getNamespace() {
        return namespace;
    }
    /**
     * @hide
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
    /**
     * @hide
     */
    public String getSchemaLocation() {
        return schemaLocation;
    }
    /**
     * @hide
     */
    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }
    /**
     * @hide
     */
    public String getImageMimeType() {
        return imageMimeType;
    }
    /**
     * @hide
     */
    public void setImageMimeType(String imageMimeType) {
        this.imageMimeType = imageMimeType;
    }
}

