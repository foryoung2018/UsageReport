package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;
import com.htc.lib1.htcmp4parser.coremedia.iso.Utf8;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractBox;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.Box;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.ContainerBox;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.util.ByteBufferByteChannel;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 *  @hide
 * {@exthide}
 */
public abstract class AbstractAppleMetaDataBox extends AbstractBox implements ContainerBox {
    private static Logger LOG = Logger.getLogger(AbstractAppleMetaDataBox.class.getName());
    /**
     * @hide
     */
    AppleDataBox appleDataBox = new AppleDataBox();
    /**
     * @hide
     */
    public List<Box> getBoxes() {
        return Collections.singletonList((Box) appleDataBox);
    }
    /**
     * @hide
     */
    public void setBoxes(List<Box> boxes) {
        if (boxes.size() == 1 && boxes.get(0) instanceof AppleDataBox) {
            appleDataBox = (AppleDataBox) boxes.get(0);
        } else {
            throw new IllegalArgumentException("This box only accepts one AppleDataBox child");
        }
    }
    /**
     * @hide
     */
    public <T extends Box> List<T> getBoxes(Class<T> clazz) {
        return getBoxes(clazz, false);
    }
    /**
     * @hide
     */
    public <T extends Box> List<T> getBoxes(Class<T> clazz, boolean recursive) {
        //todo recursive?
        if (clazz.isAssignableFrom(appleDataBox.getClass())) {
            return (List<T>) Collections.singletonList(appleDataBox);
        }
        return null;
    }
    /**
     * @hide
     */
    public AbstractAppleMetaDataBox(String type) {
        super(type);
    }
    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        long dataBoxSize = IsoTypeReader.readUInt32(content);
        String thisShouldBeData = IsoTypeReader.read4cc(content);
        assert "data".equals(thisShouldBeData);
        appleDataBox = new AppleDataBox();
        try {
            appleDataBox.parse(new ByteBufferByteChannel(content), null, content.remaining(), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        appleDataBox.setParent(this);
    }

    /**
     * @hide
     */
    protected long getContentSize() {
        return appleDataBox.getSize();
    }
    /**
     * @hide
     */
    protected void getContent(ByteBuffer byteBuffer) {
        try {
            appleDataBox.getBox(new ByteBufferByteChannel(byteBuffer));
        } catch (IOException e) {
            throw new RuntimeException("The Channel is based on a ByteBuffer and therefore it shouldn't throw any exception");
        }
    }
    /**
     * @hide
     */
    public long getNumOfBytesToFirstChild() {
        return getSize() - appleDataBox.getSize();
    }
    /**
     * @hide
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "appleDataBox=" + getValue() +
                '}';
    }
    /**
     * @hide
     */
    static long toLong(byte b) {
        return b < 0 ? b + 256 : b;
    }
    /**
     * @hide
     */
    public void setValue(String value) {
        if (appleDataBox.getFlags() == 1) {
            appleDataBox = new AppleDataBox();
            appleDataBox.setVersion(0);
            appleDataBox.setFlags(1);
            appleDataBox.setFourBytes(new byte[4]);
            appleDataBox.setData(Utf8.convert(value));
        } else if (appleDataBox.getFlags() == 21) {
            byte[] content = appleDataBox.getData();
            appleDataBox = new AppleDataBox();
            appleDataBox.setVersion(0);
            appleDataBox.setFlags(21);
            appleDataBox.setFourBytes(new byte[4]);

            ByteBuffer bb = ByteBuffer.allocate(content.length);
            if (content.length == 1) {
                IsoTypeWriter.writeUInt8(bb, (Byte.parseByte(value) & 0xFF));
            } else if (content.length == 2) {
                IsoTypeWriter.writeUInt16(bb, Integer.parseInt(value));
            } else if (content.length == 4) {
                IsoTypeWriter.writeUInt32(bb, Long.parseLong(value));
            } else if (content.length == 8) {
                IsoTypeWriter.writeUInt64(bb, Long.parseLong(value));
            } else {
                throw new Error("The content length within the appleDataBox is neither 1, 2, 4 or 8. I can't handle that!");
            }
            appleDataBox.setData(bb.array());
        } else if (appleDataBox.getFlags() == 0) {
            appleDataBox = new AppleDataBox();
            appleDataBox.setVersion(0);
            appleDataBox.setFlags(0);
            appleDataBox.setFourBytes(new byte[4]);
            appleDataBox.setData(hexStringToByteArray(value));

        } else {
            LOG.warning("Don't know how to handle appleDataBox with flag=" + appleDataBox.getFlags());
        }
    }
    /**
     * @hide
     */
    public String getValue() {
        if (appleDataBox.getFlags() == 1) {
            return Utf8.convert(appleDataBox.getData());
        } else if (appleDataBox.getFlags() == 21) {
            byte[] content = appleDataBox.getData();
            long l = 0;
            int current = 1;
            int length = content.length;
            for (byte b : content) {
                l += toLong(b) << (8 * (length - current++));
            }
            return "" + l;
        } else if (appleDataBox.getFlags() == 0) {
            return String.format("%x", new BigInteger(appleDataBox.getData()));
        } else {
            return "unknown";
        }
    }
    /**
     * @hide
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }


}
