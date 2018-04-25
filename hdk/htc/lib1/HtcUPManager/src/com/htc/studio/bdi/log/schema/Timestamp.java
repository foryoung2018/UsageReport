// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: ./proto_v0_nodescriptor\BDILogProtos.proto
package com.htc.studio.bdi.log.schema;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;

import static com.squareup.wire.Message.Datatype.SINT32;
import static com.squareup.wire.Message.Datatype.SINT64;

/**
 * import "descriptor.proto";
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *   Timestamp
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
public final class Timestamp extends Message {

  public static final Long DEFAULT_TS = 0L;
  public static final Integer DEFAULT_TZ = 0;

  @ProtoField(tag = 1, type = SINT64)
  public final Long ts;

  /**
   * time stamp in milliseconds
   */
  @ProtoField(tag = 2, type = SINT32)
  public final Integer tz;

  public Timestamp(Long ts, Integer tz) {
    this.ts = ts;
    this.tz = tz;
  }

  private Timestamp(Builder builder) {
    this(builder.ts, builder.tz);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof Timestamp)) return false;
    Timestamp o = (Timestamp) other;
    return equals(ts, o.ts)
        && equals(tz, o.tz);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = ts != null ? ts.hashCode() : 0;
      result = result * 37 + (tz != null ? tz.hashCode() : 0);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends Message.Builder<Timestamp> {

    public Long ts;
    public Integer tz;

    public Builder() {
    }

    public Builder(Timestamp message) {
      super(message);
      if (message == null) return;
      this.ts = message.ts;
      this.tz = message.tz;
    }

    public Builder ts(Long ts) {
      this.ts = ts;
      return this;
    }

    /**
     * time stamp in milliseconds
     */
    public Builder tz(Integer tz) {
      this.tz = tz;
      return this;
    }

    @Override
    public Timestamp build() {
      return new Timestamp(this);
    }
  }
}
