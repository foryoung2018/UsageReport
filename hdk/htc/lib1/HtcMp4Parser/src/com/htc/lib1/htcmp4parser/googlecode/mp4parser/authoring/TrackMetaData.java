/*
 * Copyright 2012 Sebastian Annies, Hamburg
 *
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an AS IS BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring;

import java.util.Date;

/**
 * @hide
 * {@exthide}
 */
public class TrackMetaData implements Cloneable {
    private String language;
    private long timescale;
    private Date modificationTime;
    private Date creationTime;
    private double width;
    private double height;
    private float volume;
    private long trackId = 1; // zero is not allowed
    private int group = 0;
    private double startTime = 0;
    private long duration = -1;
    private long[] matrix;


    /**
     * specifies the front-to-back ordering of video tracks; tracks with lower
     * numbers are closer to the viewer. 0 is the normal value, and -1 would be
     * in front of track 0, and so on.
     * @hide
     */
    int layer;
    
    /**
     * @hide
     * @return
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @hide
     * @return
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @hide
     * @return
     */
    public long getTimescale() {
        return timescale;
    }

    /**
     * @hide
     * @return
     */
    public void setTimescale(long timescale) {
        this.timescale = timescale;
    }

    /**
     * @hide
     * @return
     */
    public Date getModificationTime() {
        return modificationTime;
    }

    /**
     * @hide
     * @return
     */
    public void setModificationTime(Date modificationTime) {
        this.modificationTime = modificationTime;
    }

    /**
     * @hide
     * @return
     */
    public Date getCreationTime() {
        return creationTime;
    }

    /**
     * @hide
     * @return
     */
    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    /**
     * @hide
     * @return
     */
    public double getWidth() {
        return width;
    }

    /**
     * @hide
     * @return
     */
    public void setWidth(double width) {
        this.width = width;
    }

    /**
     * @hide
     * @return
     */
    public double getHeight() {
        return height;
    }

    /**
     * @hide
     * @return
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * @hide
     * @return
     */
    public long getTrackId() {
        return trackId;
    }

    /**
     * @hide
     * @return
     */
    public void setTrackId(long trackId) {
        this.trackId = trackId;
    }

    /**
     * @hide
     * @return
     */
    public int getLayer() {
        return layer;
    }

    /**
     * @hide
     * @return
     */
    public void setLayer(int layer) {
        this.layer = layer;
    }

    /**
     * @hide
     * @return
     */
    public float getVolume() {
        return volume;
    }

    /**
     * @hide
     * @return
     */
    public void setVolume(float volume) {
        this.volume = volume;
    }

    /**
     * @hide
     * @return
     */
    public int getGroup() {
        return group;
    }

    /**
     * @hide
     * @return
     */
    public void setGroup(int group) {
        this.group = group;
    }

    /**
     * @hide
     * @return
     */
    public void setDuration(long duration) {
    	this.duration = duration;
    }

    /**
     * @hide
     * @return
     */
    public long getDuration() {
    	return this.duration;
    }    

    /**
     * @hide
     * @return
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            android.util.Log.d("TrackMetaData","clone ",e);
        }
        return null;
    }
    
    public long[] getMatrix() {
    	return matrix;
    }
    
    public void setMatrix(long[] matrix) {
    	if (matrix != null) {
    		this.matrix = matrix.clone();
    	} else {
    		android.util.Log.d("TrackMetaData", "matrix clone fault");
    	}
    }

}
