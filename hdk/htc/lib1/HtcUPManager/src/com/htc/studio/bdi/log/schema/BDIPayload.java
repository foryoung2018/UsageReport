// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: ./proto_v0_nodescriptor\BDILogProtos.proto
package com.htc.studio.bdi.log.schema;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;
import java.util.Collections;
import java.util.List;

import static com.squareup.wire.Message.Label.REPEATED;

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *   BDIPayload
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
public final class BDIPayload extends Message {

  public static final List<Attribute> DEFAULT_ATTR = Collections.emptyList();

  @ProtoField(tag = 2)
  public final Timestamp record;

  /**
   * ts when the log record is created
   */
  @ProtoField(tag = 3)
  public final Timestamp send;

  /**
   * ts when the log record is sent
   */
  @ProtoField(tag = 22, label = REPEATED)
  public final List<Attribute> attr;

  /**
   * attribute information
   */
  @ProtoField(tag = 30)
  public final Event event;

  public BDIPayload(Timestamp record, Timestamp send, List<Attribute> attr, Event event) {
    this.record = record;
    this.send = send;
    this.attr = immutableCopyOf(attr);
    this.event = event;
  }

  private BDIPayload(Builder builder) {
    this(builder.record, builder.send, builder.attr, builder.event);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof BDIPayload)) return false;
    BDIPayload o = (BDIPayload) other;
    return equals(record, o.record)
        && equals(send, o.send)
        && equals(attr, o.attr)
        && equals(event, o.event);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = record != null ? record.hashCode() : 0;
      result = result * 37 + (send != null ? send.hashCode() : 0);
      result = result * 37 + (attr != null ? attr.hashCode() : 1);
      result = result * 37 + (event != null ? event.hashCode() : 0);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends Message.Builder<BDIPayload> {

    public Timestamp record;
    public Timestamp send;
    public List<Attribute> attr;
    public Event event;

    public Builder() {
    }

    public Builder(BDIPayload message) {
      super(message);
      if (message == null) return;
      this.record = message.record;
      this.send = message.send;
      this.attr = copyOf(message.attr);
      this.event = message.event;
    }

    public Builder record(Timestamp record) {
      this.record = record;
      return this;
    }

    /**
     * ts when the log record is created
     */
    public Builder send(Timestamp send) {
      this.send = send;
      return this;
    }

    /**
     * ts when the log record is sent
     */
    public Builder attr(List<Attribute> attr) {
      this.attr = checkForNulls(attr);
      return this;
    }

    /**
     * attribute information
     */
    public Builder event(Event event) {
      this.event = event;
      return this;
    }

    @Override
    public BDIPayload build() {
      return new BDIPayload(this);
    }
  }
}
