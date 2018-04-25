package com.htc.lib1.htcmp4parser.coremedia.iso.boxes;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.coremedia.iso.Utf8;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractFullBox;

import java.nio.ByteBuffer;

/**
 * @hide
 * {@exthide}
 */
public class XmlBox extends AbstractFullBox {
	/**
	 * @hide
	 */
	String xml = "";
	/**
	 * @hide
	 */
	public static final String TYPE = "xml ";

	/**
	 * @hide
	 */
    public XmlBox() {
        super(TYPE);
    }

    /**
     * @hide
     */
    public String getXml() {
        return xml;
    }

    /**
     * @hide
     */
    public void setXml(String xml) {
        this.xml = xml;
    }

    /**
     * @hide
     */
    @Override
    protected long getContentSize() {
        return 4 + Utf8.utf8StringLengthInBytes(xml);
    }

    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        xml = IsoTypeReader.readString(content, content.remaining());
    }

    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        byteBuffer.put(Utf8.convert(xml));
    }

    /**
     * @hide
     */
    @Override
    public String toString() {
        return "XmlBox{" +
                "xml='" + xml + '\'' +
                '}';
    }
}
