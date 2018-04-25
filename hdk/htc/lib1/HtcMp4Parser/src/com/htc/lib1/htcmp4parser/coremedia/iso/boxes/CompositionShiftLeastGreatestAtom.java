package com.htc.lib1.htcmp4parser.coremedia.iso.boxes;

import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractFullBox;

import java.nio.ByteBuffer;

/**
 * The optional composition shift least greatest atom summarizes the calculated
 * minimum and maximum offsets between decode and composition time, as well as
 * the start and end times, for all samples. This allows a reader to determine
 * the minimum required time for decode to obtain proper presentation order without
 * needing to scan the sample table for the range of offsets. The type of the
 * composition shift least greatest atom is ‘cslg’.
 *  @hide
 * {@exthide}
 */
public class CompositionShiftLeastGreatestAtom extends AbstractFullBox {
	/**
	 *  @hide
	 */
	public CompositionShiftLeastGreatestAtom() {
        super("cslg");
    }

    // A 32-bit unsigned integer that specifies the calculated value.
	/**
	 *  @hide
	 */
    int compositionOffsetToDisplayOffsetShift;

    // A 32-bit signed integer that specifies the calculated value.
    /**
     *  @hide
     */
    int leastDisplayOffset;

    // A 32-bit signed integer that specifies the calculated value.
    /**
     *  @hide
     */
    int greatestDisplayOffset;

    //A 32-bit signed integer that specifies the calculated value.
    /**
     *  @hide
     */
    int displayStartTime;

    //A 32-bit signed integer that specifies the calculated value.
    /**
     *  @hide
     */
    int displayEndTime;

    /**
     *  @hide
     */
    @Override
    protected long getContentSize() {
        return 24;
    }

    /**
     *  @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        compositionOffsetToDisplayOffsetShift = content.getInt();
        leastDisplayOffset = content.getInt();
        greatestDisplayOffset = content.getInt();
        displayStartTime = content.getInt();
        displayEndTime = content.getInt();
    }

    /**
     *  @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        byteBuffer.putInt(compositionOffsetToDisplayOffsetShift);
        byteBuffer.putInt(leastDisplayOffset);
        byteBuffer.putInt(greatestDisplayOffset);
        byteBuffer.putInt(displayStartTime);
        byteBuffer.putInt(displayEndTime);
    }

    /**
     *  @hide
     */
    public int getCompositionOffsetToDisplayOffsetShift() {
        return compositionOffsetToDisplayOffsetShift;
    }

    /**
     *  @hide
     */
    public void setCompositionOffsetToDisplayOffsetShift(int compositionOffsetToDisplayOffsetShift) {
        this.compositionOffsetToDisplayOffsetShift = compositionOffsetToDisplayOffsetShift;
    }

    /**
     *  @hide
     */
    public int getLeastDisplayOffset() {
        return leastDisplayOffset;
    }

    /**
     *  @hide
     */
    public void setLeastDisplayOffset(int leastDisplayOffset) {
        this.leastDisplayOffset = leastDisplayOffset;
    }

    /**
     *  @hide
     */
    public int getGreatestDisplayOffset() {
        return greatestDisplayOffset;
    }

    /**
     *  @hide
     */
    public void setGreatestDisplayOffset(int greatestDisplayOffset) {
        this.greatestDisplayOffset = greatestDisplayOffset;
    }

    /**
     *  @hide
     */
    public int getDisplayStartTime() {
        return displayStartTime;
    }
    
    /**
     *  @hide
     */
    public void setDisplayStartTime(int displayStartTime) {
        this.displayStartTime = displayStartTime;
    }

    /**
     *  @hide
     */
    public int getDisplayEndTime() {
        return displayEndTime;
    }

    /**
     *  @hide
     */
    public void setDisplayEndTime(int displayEndTime) {
        this.displayEndTime = displayEndTime;
    }
}
