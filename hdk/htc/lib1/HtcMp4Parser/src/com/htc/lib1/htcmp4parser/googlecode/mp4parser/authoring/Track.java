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

import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.*;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.mdat.SampleList.SampleOffset;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Represents a Track. A track is a timed sequence of related samples.
 * <p/>
 * <b>NOTE: </b><br/
 * For media data, a track corresponds to a sequence of images or sampled audio; for hint tracks, a track
 * corresponds to a streaming channel.
 * @hide
 * {@exthide}
 */
public interface Track {
    /**
     * @hide
     * @return
     */
    SampleDescriptionBox getSampleDescriptionBox();

    /**
     * @hide
     * @return
     */
    List<TimeToSampleBox.Entry> getDecodingTimeEntries();

    /**
     * @hide
     * @return
     */
    List<CompositionTimeToSample.Entry> getCompositionTimeEntries();

    /**
     * @hide
     * @return
     */
    long[] getSyncSamples();

    /**
     * @hide
     * @return
     */
    List<SampleDependencyTypeBox.Entry> getSampleDependencies();

    /**
     * @hide
     * @return
     */
    TrackMetaData getTrackMetaData();

    /**
     * @hide
     * @return
     */
    String getHandler();

    /**
     * @hide
     * @return
     */
    boolean isEnabled();

    /**
     * @hide
     * @return
     */
    boolean isInMovie();

    /**
     * @hide
     * @return
     */
    boolean isInPreview();

    /**
     * @hide
     * @return
     */
    boolean isInPoster();

    /**
     * @hide
     * @return
     */
    List<ByteBuffer> getSamples();

    /**
     * @hide
     * @return
     */
    public Box getMediaHeaderBox();

    /**
     * @hide
     * @return
     */
    public SubSampleInformationBox getSubsampleInformationBox();

    /**
     * @hide
     * @return
     */
    List<SampleOffset> getSampleOffsets();

}
