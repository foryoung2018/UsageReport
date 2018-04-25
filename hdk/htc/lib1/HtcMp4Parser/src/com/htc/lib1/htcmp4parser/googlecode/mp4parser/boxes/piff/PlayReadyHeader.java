package com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.piff;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoFile;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.util.Path;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Specifications > Microsoft PlayReady Format Specification > 2. PlayReady Media Format > 2.7. ASF GUIDs
 * <p/>
 * <p/>
 * ASF_Protection_System_Identifier_Object
 * 9A04F079-9840-4286-AB92E65BE0885F95
 * <p/>
 * ASF_Content_Protection_System_Microsoft_PlayReady
 * F4637010-03C3-42CD-B932B48ADF3A6A54
 * <p/>
 * ASF_StreamType_PlayReady_Encrypted_Command_Media
 * 8683973A-6639-463A-ABD764F1CE3EEAE0
 * <p/>
 * <p/>
 * Specifications > Microsoft PlayReady Format Specification > 2. PlayReady Media Format > 2.5. Data Objects > 2.5.1. Payload Extension for AES in Counter Mode
 * <p/>
 * The sample Id is used as the IV in CTR mode. Block offset, starting at 0 and incremented by 1 after every 16 bytes, from the beginning of the sample is used as the Counter.
 * <p/>
 * The sample ID for each sample (media object) is stored as an ASF payload extension system with the ID of ASF_Payload_Extension_Encryption_SampleID = {6698B84E-0AFA-4330-AEB2-1C0A98D7A44D}. The payload extension can be stored as a fixed size extension of 8 bytes.
 * <p/>
 * The sample ID is always stored in big-endian byte order.
 * @hide
 */
public class PlayReadyHeader extends ProtectionSpecificHeader {
    private long length;
    private List<PlayReadyRecord> records;
    /**
     * @hide
     */
    public PlayReadyHeader() {

    }
    /**
     * @hide
     */
    @Override
    public void parse(ByteBuffer byteBuffer) {
        /*
   Length DWORD 32

   PlayReady Record Count WORD 16

   PlayReady Records See Text Varies

        */

        length = IsoTypeReader.readUInt32BE(byteBuffer);
        int recordCount = IsoTypeReader.readUInt16BE(byteBuffer);

        records = PlayReadyRecord.createFor(byteBuffer, recordCount);
    }
    /**
     * @hide
     */
    @Override
    public ByteBuffer getData() {

        int size = 4 + 2;
        for (PlayReadyRecord record : records) {
            size += 2 + 2;
            size += record.getValue().rewind().limit();
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);

        IsoTypeWriter.writeUInt32BE(byteBuffer, size);
        IsoTypeWriter.writeUInt16BE(byteBuffer, records.size());
        for (PlayReadyRecord record : records) {
            IsoTypeWriter.writeUInt16BE(byteBuffer, record.type);
            IsoTypeWriter.writeUInt16BE(byteBuffer, record.getValue().limit());
            ByteBuffer tmp4debug = record.getValue();
            byteBuffer.put(tmp4debug);
        }

        return byteBuffer;
    }
    /**
     * @hide
     */
    public void setRecords(List<PlayReadyRecord> records) {
        this.records = records;
    }
    /**
     * @hide
     */
    public List<PlayReadyRecord> getRecords() {
        return Collections.unmodifiableList(records);
    }
    /**
     * @hide
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("PlayReadyHeader");
        sb.append("{length=").append(length);
        sb.append(", recordCount=").append(records.size());
        sb.append(", records=").append(records);
        sb.append('}');
        return sb.toString();
    }
    /**
     * @hide
     */
    public static abstract class PlayReadyRecord {
    	/**
    	 * @hide
    	 */
        int type;

        /**
         * @hide
         */
        public PlayReadyRecord(int type) {
            this.type = type;
        }
        /**
         * @hide
         */
        public static List<PlayReadyRecord> createFor(ByteBuffer byteBuffer, int recordCount) {
            List<PlayReadyRecord> records = new ArrayList<PlayReadyRecord>(recordCount);

            for (int i = 0; i < recordCount; i++) {
                PlayReadyRecord record;
                int type = IsoTypeReader.readUInt16BE(byteBuffer);
                int length = IsoTypeReader.readUInt16BE(byteBuffer);
                switch (type) {
                    case 0x1:
                        record = new RMHeader();
                        break;
                    case 0x2:
                        record = new DefaulPlayReadyRecord(0x02);
                        break;
                    case 0x3:
                        record = new EmeddedLicenseStore();
                        break;
                    default:
                        record = new DefaulPlayReadyRecord(type);
                }
                record.parse((ByteBuffer) byteBuffer.slice().limit(length));
                byteBuffer.position(byteBuffer.position() + length);
                records.add(record);
            }

            return records;
        }
        /**
         * @hide
         */
        public abstract void parse(ByteBuffer bytes);
        /**
         * @hide
         */
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("PlayReadyRecord");
            sb.append("{type=").append(type);
            sb.append(", length=").append(getValue().limit());
//            sb.append(", value=").append(Hex.encodeHex(getValue())).append('\'');
            sb.append('}');
            return sb.toString();
        }
        /**
         * @hide
         */
        public abstract ByteBuffer getValue();
        /**
         * @hide
         */
        public static class RMHeader extends PlayReadyRecord {
            String header;
            /**
             * @hide
             */
            public RMHeader() {
                super(0x01);
            }
            /**
             * @hide
             */
            @Override
            public void parse(ByteBuffer bytes) {
                try {
                    byte[] str = new byte[bytes.slice().limit()];
                    bytes.get(str);
                    header = new String(str, "UTF-16LE");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
            /**
             * @hide
             */
            @Override
            public ByteBuffer getValue() {
                byte[] headerBytes;
                try {
                    headerBytes = header.getBytes("UTF-16LE");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                return ByteBuffer.wrap(headerBytes);
            }
            /**
             * @hide
             */
            public void setHeader(String header) {
                this.header = header;
            }
            /**
             * @hide
             */
            public String getHeader() {
                return header;
            }
            /**
             * @hide
             */
            @Override
            public String toString() {
                final StringBuilder sb = new StringBuilder();
                sb.append("RMHeader");
                sb.append("{length=").append(getValue().limit());
                sb.append(", header='").append(header).append('\'');
                sb.append('}');
                return sb.toString();
            }
        }
        /**
         * @hide
         */
        public static class EmeddedLicenseStore extends PlayReadyRecord {
        	/**
        	 * @hide
        	 */
            ByteBuffer value;
            /**
             * @hide
             */
            public EmeddedLicenseStore() {
                super(0x03);
            }
            /**
             * @hide
             */
            @Override
            public void parse(ByteBuffer bytes) {
                this.value = bytes.duplicate();
            }
            /**
             * @hide
             */
            @Override
            public ByteBuffer getValue() {
                return value;
            }
            /**
             * @hide
             */
            @Override
            public String toString() {
                final StringBuilder sb = new StringBuilder();
                sb.append("EmeddedLicenseStore");
                sb.append("{length=").append(getValue().limit());
                //sb.append(", value='").append(Hex.encodeHex(getValue())).append('\'');
                sb.append('}');
                return sb.toString();
            }
        }
        /**
         * @hide
         */
        public static class DefaulPlayReadyRecord extends PlayReadyRecord {
        	/**
        	 * @hide
        	 */
            ByteBuffer value;
            /**
             * @hide
             */
            public DefaulPlayReadyRecord(int type) {
                super(type);
            }
            /**
             * @hide
             */
            @Override
            public void parse(ByteBuffer bytes) {
                this.value = bytes.duplicate();
            }
            /**
             * @hide
             */
            @Override
            public ByteBuffer getValue() {
                return value;
            }

        }

    }

}
