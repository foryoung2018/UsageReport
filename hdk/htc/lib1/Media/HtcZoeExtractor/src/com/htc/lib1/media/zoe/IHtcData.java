package com.htc.lib1.media.zoe;

import java.lang.IndexOutOfBoundsException;

public interface IHtcData {

    public int getCounts() throws IndexOutOfBoundsException;
    public long getOffset(int index) throws IndexOutOfBoundsException;
    public int getLength(int index) throws IndexOutOfBoundsException;

}
