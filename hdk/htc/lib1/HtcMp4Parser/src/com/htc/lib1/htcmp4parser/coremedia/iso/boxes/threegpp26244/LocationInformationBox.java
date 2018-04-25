package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.threegpp26244;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;
import com.htc.lib1.htcmp4parser.coremedia.iso.Utf8;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractFullBox;

import java.nio.ByteBuffer;

/**
 * Location Information Box as specified in TS 26.244.
 *  @hide
 * {@exthide}
 */
public class LocationInformationBox extends AbstractFullBox {
    /**
     * @hide
     */
    public static final String TYPE = "loci";

    private String language;
    private String name = "";
    private int role;
    private double longitude;
    private double latitude;
    private double altitude;
    private String astronomicalBody = "";
    private String additionalNotes = "";
    /**
     * @hide
     */
    public LocationInformationBox() {
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
    public void setLanguage(String language) {
        this.language = language;
    }
    /**
     * @hide
     */
    public String getName() {
        return name;
    }
    /**
     * @hide
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @hide
     */
    public int getRole() {
        return role;
    }
    /**
     * @hide
     */
    public void setRole(int role) {
        this.role = role;
    }
    /**
     * @hide
     */
    public double getLongitude() {
        return longitude;
    }
    /**
     * @hide
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    /**
     * @hide
     */
    public double getLatitude() {
        return latitude;
    }
    /**
     * @hide
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    /**
     * @hide
     */
    public double getAltitude() {
        return altitude;
    }
    /**
     * @hide
     */
    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
    /**
     * @hide
     */
    public String getAstronomicalBody() {
        return astronomicalBody;
    }
    /**
     * @hide
     */
    public void setAstronomicalBody(String astronomicalBody) {
        this.astronomicalBody = astronomicalBody;
    }
    /**
     * @hide
     */
    public String getAdditionalNotes() {
        return additionalNotes;
    }
    /**
     * @hide
     */
    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }
    /**
     * @hide
     */
    protected long getContentSize() {
        return 22 + Utf8.utf8StringLengthInBytes(name) + Utf8.utf8StringLengthInBytes(astronomicalBody) + Utf8.utf8StringLengthInBytes(additionalNotes);
    }
    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        language = IsoTypeReader.readIso639(content);
        name = IsoTypeReader.readString(content);
        role = IsoTypeReader.readUInt8(content);
        longitude = IsoTypeReader.readFixedPoint1616(content);
        latitude = IsoTypeReader.readFixedPoint1616(content);
        altitude = IsoTypeReader.readFixedPoint1616(content);
        astronomicalBody = IsoTypeReader.readString(content);
        additionalNotes = IsoTypeReader.readString(content);
    }

    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeIso639(byteBuffer, language);
        byteBuffer.put(Utf8.convert(name));
        byteBuffer.put((byte) 0);
        IsoTypeWriter.writeUInt8(byteBuffer, role);
        IsoTypeWriter.writeFixedPont1616(byteBuffer, longitude);
        IsoTypeWriter.writeFixedPont1616(byteBuffer, latitude);
        IsoTypeWriter.writeFixedPont1616(byteBuffer, altitude);
        byteBuffer.put(Utf8.convert(astronomicalBody));
        byteBuffer.put((byte) 0);
        byteBuffer.put(Utf8.convert(additionalNotes));
        byteBuffer.put((byte) 0);
    }
}
