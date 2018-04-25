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
 * This box contains all the objects that declare characteristic information of the media in the track.
 * @hide
 * {@exthide}
 */
public class MediaInformationBox extends AbstractContainerBox {
	/**
	 * @hide
	 */
	public static final String TYPE = "minf";

	/**
	 * @hide
	 */
    public MediaInformationBox() {
        super(TYPE);
    }

    /**
     * @hide
     */
    public SampleTableBox getSampleTableBox() {
        for (Box box : boxes) {
            if (box instanceof SampleTableBox) {
                return (SampleTableBox) box;
            }
        }
        return null;
    }

    /**
     * @hide
     */
    public AbstractMediaHeaderBox getMediaHeaderBox() {
        for (Box box : boxes) {
            if (box instanceof AbstractMediaHeaderBox) {
                return (AbstractMediaHeaderBox) box;
            }
        }
        return null;
    }

}
