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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.Box;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.HtcBox;
import com.htc.lib1.htcmp4parser.utils.Log;

/**
 * @hide
 * {@exthide}
 */
public class Movie {

    /**
     * @hide
     */
	private HtcBox.HTCMetaDataTable mHtctable = null;
	
    /**
     * @hide
     */
	public HtcBox.HTCMetaDataTable getTable(){
		return mHtctable;
	}
	
    /**
     * @hide
     */
	public void setTable(HtcBox.HTCMetaDataTable table){
		mHtctable = table;
	}

    /**
     * @hide
     */
    List<Track> tracks = new LinkedList<Track>();
    /**
     * @hide
     */
    List<Box> boxs = new LinkedList<Box>();
    /**
     * @hide
     * @return
     */
    public List<Track> getTracks() {
        return tracks;
    }
    /**
     * @hide
     * @param tracks
     */
    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }
    /**
     * @hide
     * @param nuTrack
     */
    public void addTrack(Track nuTrack) {
        // do some checking
        // perhaps the movie needs to get longer!
        if (getTrackByTrackId(nuTrack.getTrackMetaData().getTrackId()) != null) {
            // We already have a track with that trackId. Create a new one
            nuTrack.getTrackMetaData().setTrackId(getNextTrackId());
        }
        tracks.add(nuTrack);
    }

    /**
     * @hide
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Movie{ ");
        for (Track track : tracks) {
            s.append("track_" + track.getTrackMetaData().getTrackId() + " (" + track.getHandler() + ") ");
        }

        s.append("}");
        return s.toString();
    }
    
    /**
     * @hide
     * @return
     */
    public long getNextTrackId() {
        long nextTrackId = 0;
        for (Track track : tracks) {
            nextTrackId = nextTrackId < track.getTrackMetaData().getTrackId() ? track.getTrackMetaData().getTrackId() : nextTrackId;
        }
        return ++nextTrackId;
    }

    /**
     * @hide
     * @param trackId
     * @return
     */
    public Track getTrackByTrackId(long trackId) {
        for (Track track : tracks) {
            if (track.getTrackMetaData().getTrackId() == trackId) {
                return track;
            }
        }
        return null;
    }

    /**
     * @hide
     * @return
     */
    public long getTimescale() {
        long timescale = this.getTracks().iterator().next().getTrackMetaData().getTimescale();
        for (Track track : this.getTracks()) {
            timescale = gcd(track.getTrackMetaData().getTimescale(), timescale);
        }
        return timescale;
    }

    /**
     * @hide
     * @param a
     * @param b
     * @return
     */
    public static long gcd(long a, long b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }
    
    /**
     * @hide
     * @param box
     */
    public void addBox(Box box) {
    	boxs.add(box);
    }
    
    /**
     * @hide
     * @return
     */
    public List<Box> getBoxs() {
    	return boxs;
    }
    
    /**
     * @hide
     * @param c
     */
    public void removeBox(Class<?> c) {
    	List<Box> removeBox = new LinkedList<Box>();
    	
    	for (Box b : boxs) {
    		if (c != null && c.isInstance(b)) {
    			removeBox.add(b);
    		}
    	}
    	boxs.removeAll(removeBox);
    	
    }
    
    /**
     * @hide
     * @param c
     * @return
     */
    public Box getBox(Class<?> c) {
    	for (Box b : boxs) {
    		if (c != null && c.isInstance(b)) {
    			return b;
    		}
    	}
    	return null;
    }
    
    
    /**
     * get max duration of this video 
     * @return time in ms
     * @hide
     */
    public long getDuration() {
    	long duration = -1;
    	for (Track track : tracks) {
    		Log.d(this.getClass().getSimpleName(), "Track:" + track);
    		duration = Math.max(duration, track.getTrackMetaData().getDuration() / track.getTrackMetaData().getTimescale() * 1000);
    	}
    	
    	return duration;
    }    
}
