package com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.piff;


import com.htc.lib1.htcmp4parser.coremedia.iso.Hex;

import java.lang.Class;
import java.lang.IllegalAccessException;
import java.lang.InstantiationException;
import java.lang.Object;
import java.lang.Override;
import java.lang.RuntimeException;
import java.lang.String;
import java.lang.StringBuilder;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @hide
 */
public class ProtectionSpecificHeader {
	/**
	 * @hide
	 */
    protected static Map<UUID, Class<? extends ProtectionSpecificHeader>> uuidRegistry = new HashMap<UUID, Class<? extends ProtectionSpecificHeader>>();
    /**
     * @hide
     */
    ByteBuffer data;

    static {
        uuidRegistry.put(UUID.fromString("9A04F079-9840-4286-AB92-E65BE0885F95"), PlayReadyHeader.class);
    }
    /**
     * @hide
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ProtectionSpecificHeader) {
            if (this.getClass().equals(obj.getClass())) {
                return data.equals(((ProtectionSpecificHeader) obj).data);
            }
        }
        return false;
    }
    /**
     * @hide
     */
    public static ProtectionSpecificHeader createFor(UUID systemId, ByteBuffer bufferWrapper) {
        final Class<? extends ProtectionSpecificHeader> aClass = uuidRegistry.get(systemId);

        ProtectionSpecificHeader protectionSpecificHeader = new ProtectionSpecificHeader();
        if (aClass != null) {
            try {
                protectionSpecificHeader = aClass.newInstance();

            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        protectionSpecificHeader.parse(bufferWrapper);
        return protectionSpecificHeader;

    }
    /**
     * @hide
     */
    public void parse(ByteBuffer buffer) {
        data = buffer;

    }
    /**
     * @hide
     */
    public ByteBuffer getData() {
        return data;
    }
    /**
     * @hide
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ProtectionSpecificHeader");
        sb.append("{data=");
        ByteBuffer data = getData().duplicate();
        data.rewind();
        byte[] bytes = new byte[data.limit()];
        data.get(bytes);
        sb.append(Hex.encodeHex(bytes));
        sb.append('}');
        return sb.toString();
    }
}
