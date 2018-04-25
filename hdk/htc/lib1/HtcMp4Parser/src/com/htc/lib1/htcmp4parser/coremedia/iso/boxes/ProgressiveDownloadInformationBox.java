package com.htc.lib1.htcmp4parser.coremedia.iso.boxes;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractFullBox;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @hide
 * {@exthide}
 */
public class ProgressiveDownloadInformationBox extends AbstractFullBox {

	/**
	 * @hide
	 */
    List<Entry> entries = Collections.emptyList();

    /**
     * @hide
     */
    public ProgressiveDownloadInformationBox() {
        super("pdin");
    }

    /**
     * @hide
     */
    @Override
    protected long getContentSize() {
        return 4 + entries.size() * 8;
    }

    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        for (Entry entry : entries) {
            IsoTypeWriter.writeUInt32(byteBuffer, entry.getRate());
            IsoTypeWriter.writeUInt32(byteBuffer, entry.getInitialDelay());
        }
    }

    /**
     * @hide
     */
    public List<Entry> getEntries() {
        return entries;
    }

    /**
     * @hide
     */
    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        entries = new LinkedList<Entry>();
        while (content.remaining() >= 8) {
            Entry entry = new Entry(IsoTypeReader.readUInt32(content), IsoTypeReader.readUInt32(content));
            entries.add(entry);
        }
    }

    /**
     * @hide
     * {@exthide}
     */
    public static class Entry {
    	/**
    	 * @hide
    	 */
    	long rate;
    	/**
    	 * @hide
    	 */
    	long initialDelay;

    	/**
    	 * @hide
    	 */
        public Entry(long rate, long initialDelay) {
            this.rate = rate;
            this.initialDelay = initialDelay;
        }

        /**
         * @hide
         */
        public long getRate() {
            return rate;
        }

        /**
         * @hide
         */
        public void setRate(long rate) {
            this.rate = rate;
        }

        /**
         * @hide
         */
        public long getInitialDelay() {
            return initialDelay;
        }

        /**
         * @hide
         */
        public void setInitialDelay(long initialDelay) {
            this.initialDelay = initialDelay;
        }

        /**
         * @hide
         */
        @Override
        public String toString() {
            return "Entry{" +
                    "rate=" + rate +
                    ", initialDelay=" + initialDelay +
                    '}';
        }
    }

    /**
     * @hide
     */
    @Override
    public String toString() {
        return "ProgressiveDownloadInfoBox{" +
                "entries=" + entries +
                '}';
    }

}