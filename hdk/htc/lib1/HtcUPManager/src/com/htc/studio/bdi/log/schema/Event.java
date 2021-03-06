// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: ./proto_v0_nodescriptor\BDILogProtos.proto
package com.htc.studio.bdi.log.schema;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;

import static com.squareup.wire.Message.Datatype.SINT64;
import static com.squareup.wire.Message.Datatype.STRING;

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *   Event
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
public final class Event extends Message {

  public static final String DEFAULT_CATEGORY = "";
  public static final String DEFAULT_ACTION = "";
  public static final String DEFAULT_LABEL = "";
  public static final Long DEFAULT_VALUE = 0L;

  @ProtoField(tag = 1, type = STRING)
  public final String category;

  /**
   * event category
   */
  @ProtoField(tag = 2, type = STRING)
  public final String action;

  /**
   * event action
   */
  @ProtoField(tag = 3, type = STRING)
  public final String label;

  /**
   * event label
   */
  @ProtoField(tag = 4, type = SINT64)
  public final Long value;

  public Event(String category, String action, String label, Long value) {
    this.category = category;
    this.action = action;
    this.label = label;
    this.value = value;
  }

  private Event(Builder builder) {
    this(builder.category, builder.action, builder.label, builder.value);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof Event)) return false;
    Event o = (Event) other;
    return equals(category, o.category)
        && equals(action, o.action)
        && equals(label, o.label)
        && equals(value, o.value);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = category != null ? category.hashCode() : 0;
      result = result * 37 + (action != null ? action.hashCode() : 0);
      result = result * 37 + (label != null ? label.hashCode() : 0);
      result = result * 37 + (value != null ? value.hashCode() : 0);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends Message.Builder<Event> {

    public String category;
    public String action;
    public String label;
    public Long value;

    public Builder() {
    }

    public Builder(Event message) {
      super(message);
      if (message == null) return;
      this.category = message.category;
      this.action = message.action;
      this.label = message.label;
      this.value = message.value;
    }

    public Builder category(String category) {
      this.category = category;
      return this;
    }

    /**
     * event category
     */
    public Builder action(String action) {
      this.action = action;
      return this;
    }

    /**
     * event action
     */
    public Builder label(String label) {
      this.label = label;
      return this;
    }

    /**
     * event label
     */
    public Builder value(Long value) {
      this.value = value;
      return this;
    }

    @Override
    public Event build() {
      return new Event(this);
    }
  }
}
