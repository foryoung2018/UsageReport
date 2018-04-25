/*  
 * Copyright 2008 CoreMedia AG, Hamburg
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

package com.htc.lib1.htcmp4parser.coremedia.iso.boxes;

import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractContainerBox;

/**
 * Tracks are used for two purposes: (a) to contain media data (media tracks) and (b) to contain packetization
 * information for streaming protocols (hint tracks).  <br>
 * There shall be at least one media track within an ISO file, and all the media tracks that contributed to the hint
 * tracks shall remain in the file, even if the media data within them is not referenced by the hint tracks; after
 * deleting all hint tracks, the entire un-hinted presentation shall remain.
 */
/**
 * @hide
 * {@exthide}
 */
public class TrackBox extends AbstractContainerBox {
	/**
	 * @hide
	 */
	public static final String TYPE = "trak";

	/**
	 * @hide
	 */
    public TrackBox() {
        super(TYPE);
    }

    /**
     * @hide
     */
    public TrackHeaderBox getTrackHeaderBox() {
        for (Box box : boxes) {
            if (box instanceof TrackHeaderBox) {
                return (TrackHeaderBox) box;
            }
        }
        return null;
    }

    /**
     * Gets the SampleTableBox at mdia/minf/stbl if existing.
     *
     * @return the SampleTableBox or <code>null</code>
     * @hide
     */
    public SampleTableBox getSampleTableBox() {
        MediaBox mdia = getMediaBox();
        if (mdia != null) {
            MediaInformationBox minf = mdia.getMediaInformationBox();
            if (minf != null) {
                return minf.getSampleTableBox();
            }
        }
        return null;

    }

    /**
     * @hide
     */
    public MediaBox getMediaBox() {
        for (Box box : boxes) {
            if (box instanceof MediaBox) {
                return (MediaBox) box;
            }
        }
        return null;
    }

}
