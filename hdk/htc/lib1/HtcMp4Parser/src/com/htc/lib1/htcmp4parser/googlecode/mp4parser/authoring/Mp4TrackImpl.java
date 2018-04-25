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
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.SampleDependencyTypeBox.Entry;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.fragment.*;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.mdat.SampleList;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.mdat.SampleList.SampleOffset;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.tracks.SilenceTrackImpl;
import com.htc.lib1.htcmp4parser.utils.Log;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.htc.lib1.htcmp4parser.googlecode.mp4parser.util.CastUtils.l2i;

/**
 * Represents a single track of an MP4 file.
 * @hide
 * {@exthide}
 */
public class Mp4TrackImpl extends AbstractTrack {
    private List<ByteBuffer> samples;
    private SampleDescriptionBox sampleDescriptionBox;
    private List<TimeToSampleBox.Entry> decodingTimeEntries;
    private List<CompositionTimeToSample.Entry> compositionTimeEntries;
    private long[] syncSamples = new long[0];
    private List<SampleDependencyTypeBox.Entry> sampleDependencies;
    private TrackMetaData trackMetaData = new TrackMetaData();
    private String handler;
    private AbstractMediaHeaderBox mihd;

    /**
     * @hide
     * @param trackBox
     */
    public Mp4TrackImpl(TrackBox trackBox) {
        
    	final TrackHeaderBox tkhd = trackBox.getTrackHeaderBox();    	
    	final long trackId = (tkhd!=null) ? tkhd.getTrackId() : null;	
    	samples = new SampleList(trackBox);
        
        final MediaBox mediaBox = trackBox.getMediaBox();
        SampleTableBox stbl = null;
        
        if (mediaBox != null ) {
        	if (mediaBox.getHandlerBox() != null) {
        		handler = mediaBox.getHandlerBox().getHandlerType();
        	} else {
        		Log.e(Mp4TrackImpl.class.getName(), "mediaBox.getHandlerBox() is null");
        	}
        	
        	final MediaInformationBox mediaInfoBox = mediaBox.getMediaInformationBox();
        	if (mediaInfoBox != null) {
        		stbl = mediaInfoBox.getSampleTableBox();
                mihd = mediaInfoBox.getMediaHeaderBox(); 
        	} else {
        		Log.e(Mp4TrackImpl.class.getName(), "mediaBox.getMediaInformationBox() is null");
        	}        	           
        } else {
        	Log.e(Mp4TrackImpl.class.getName(), "Get mediaBox is null");
        }
        
        decodingTimeEntries = new LinkedList<TimeToSampleBox.Entry>();
        compositionTimeEntries = new LinkedList<CompositionTimeToSample.Entry>();
        sampleDependencies = new LinkedList<SampleDependencyTypeBox.Entry>();        
        
        if (stbl != null) {
        	final TimeToSampleBox timeToSampleBox = stbl.getTimeToSampleBox();
        	final List<com.htc.lib1.htcmp4parser.coremedia.iso.boxes.TimeToSampleBox.Entry> timeToSampleBoxEntries = (timeToSampleBox != null) ? timeToSampleBox.getEntries() : null;
        	if (timeToSampleBoxEntries != null) {
        		decodingTimeEntries.addAll(timeToSampleBoxEntries);
        	}
        	
        	final CompositionTimeToSample composTimeToSample = stbl.getCompositionTimeToSample();
        	final List<com.htc.lib1.htcmp4parser.coremedia.iso.boxes.CompositionTimeToSample.Entry> composTimeToSampleEntries = (composTimeToSample != null) ? composTimeToSample.getEntries() : null;
        	if (composTimeToSampleEntries != null) {
        		compositionTimeEntries.addAll(composTimeToSampleEntries);
        	}
        	
        	if (stbl.getSampleDependencyTypeBox() != null) {
        		final List<Entry> sdtbEntries = stbl.getSampleDependencyTypeBox().getEntries();
        		if (sdtbEntries != null) {
        			sampleDependencies.addAll(sdtbEntries);
        		}
        	}
        	if (stbl.getSyncSampleBox() != null) {
        		syncSamples = stbl.getSyncSampleBox().getSampleNumber();
        	}
        	sampleDescriptionBox = stbl.getSampleDescriptionBox();
        }
        
        final List<MovieExtendsBox> movieExtendsBoxes = trackBox.getParent().getBoxes(MovieExtendsBox.class);
        if (movieExtendsBoxes.size() > 0) {
            for (MovieExtendsBox mvex : movieExtendsBoxes) {
                final List<TrackExtendsBox> trackExtendsBoxes = mvex.getBoxes(TrackExtendsBox.class);
                for (TrackExtendsBox trex : trackExtendsBoxes) {
                    if (trex.getTrackId() == trackId) {
                        List<Long> syncSampleList = new LinkedList<Long>();

                        for (MovieFragmentBox movieFragmentBox : trackBox.getIsoFile().getBoxes(MovieFragmentBox.class)) {
                            List<TrackFragmentBox> trafs = movieFragmentBox.getBoxes(TrackFragmentBox.class);
                            long sampleNumber = 1;
                            for (TrackFragmentBox traf : trafs) {
                                if (traf.getTrackFragmentHeaderBox() != null && traf.getTrackFragmentHeaderBox().getTrackId() == trackId) {
                                    List<TrackRunBox> truns = traf.getBoxes(TrackRunBox.class);
                                    for (TrackRunBox trun : truns) {
                                        final TrackFragmentHeaderBox tfhd = ((TrackFragmentBox) trun.getParent()).getTrackFragmentHeaderBox();
                                        boolean first = true;
                                        for (TrackRunBox.Entry entry : trun.getEntries()) {
                                            if (trun.isSampleDurationPresent()) {
                                                if (decodingTimeEntries.size() == 0 ||
                                                        decodingTimeEntries.get(decodingTimeEntries.size() - 1).getDelta() != entry.getSampleDuration()) {
                                                    decodingTimeEntries.add(new TimeToSampleBox.Entry(1, entry.getSampleDuration()));
                                                } else {
                                                    TimeToSampleBox.Entry e = decodingTimeEntries.get(decodingTimeEntries.size() - 1);
                                                    e.setCount(e.getCount() + 1);
                                                }
                                            } else {
                                                if ( tfhd != null && tfhd.hasDefaultSampleDuration()) {
                                                    decodingTimeEntries.add(new TimeToSampleBox.Entry(1, tfhd.getDefaultSampleDuration()));
                                                } else {
                                                    decodingTimeEntries.add(new TimeToSampleBox.Entry(1, trex.getDefaultSampleDuration()));
                                                }
                                            }

                                            if (trun.isSampleCompositionTimeOffsetPresent()) {
                                                if (compositionTimeEntries.size() == 0 ||
                                                        compositionTimeEntries.get(compositionTimeEntries.size() - 1).getOffset() != entry.getSampleCompositionTimeOffset()) {
                                                    compositionTimeEntries.add(new CompositionTimeToSample.Entry(1, l2i(entry.getSampleCompositionTimeOffset())));
                                                } else {
                                                    CompositionTimeToSample.Entry e = compositionTimeEntries.get(compositionTimeEntries.size() - 1);
                                                    e.setCount(e.getCount() + 1);
                                                }
                                            }
                                            final SampleFlags sampleFlags;
                                            if (trun.isSampleFlagsPresent()) {
                                                sampleFlags = entry.getSampleFlags();
                                            } else {
                                                if (first && trun.isFirstSampleFlagsPresent()) {
                                                    sampleFlags = trun.getFirstSampleFlags();
                                                } else {
                                                    if (tfhd != null && tfhd.hasDefaultSampleFlags()) {
                                                        sampleFlags = tfhd.getDefaultSampleFlags();
                                                    } else {
                                                        sampleFlags = trex.getDefaultSampleFlags();
                                                    }
                                                }
                                            }
                                            if (sampleFlags != null && !sampleFlags.isSampleIsDifferenceSample()) {
                                                //iframe
                                                syncSampleList.add(sampleNumber);
                                            }
                                            sampleNumber++;
                                            first = false;
                                        }
                                    }
                                }
                            }
                        }
                        // Warning: Crappy code
                        long[] oldSS = syncSamples;
                        syncSamples = new long[syncSamples.length + syncSampleList.size()];
                        System.arraycopy(oldSS, 0, syncSamples, 0, oldSS.length);
                        final Iterator<Long> iterator = syncSampleList.iterator();
                        int i = oldSS.length;
                        while (iterator.hasNext()) {
                            Long syncSampleNumber = iterator.next();
                            syncSamples[i++] = syncSampleNumber;
                        }
                    }
                }
            }
        }
        
        MediaHeaderBox mdhd = null;
        if (mediaBox != null) {
        	mdhd = mediaBox.getMediaHeaderBox();
        } else {
        	Log.e(Mp4TrackImpl.class.getName(), "Get mediaBox is null");
        }

        if (tkhd != null) {
        	setEnabled(tkhd.isEnabled());
            setInMovie(tkhd.isInMovie());
            setInPoster(tkhd.isInPoster());
            setInPreview(tkhd.isInPreview());
        	
        	trackMetaData.setTrackId(tkhd.getTrackId());
        	trackMetaData.setHeight(tkhd.getHeight());
        	trackMetaData.setWidth(tkhd.getWidth());
        	trackMetaData.setLayer(tkhd.getLayer());
        	trackMetaData.setMatrix(tkhd.getMatrix());
        }
        
        if (mdhd != null) {
        	trackMetaData.setCreationTime(DateHelper.convert(mdhd.getCreationTime()));
        	trackMetaData.setLanguage(mdhd.getLanguage());
        	trackMetaData.setModificationTime(DateHelper.convert(mdhd.getModificationTime()));
        	trackMetaData.setTimescale(mdhd.getTimescale());
        	trackMetaData.setDuration(mdhd.getDuration());
        }
/*        System.err.println(mdhd.getModificationTime());
        System.err.println(DateHelper.convert(mdhd.getModificationTime()));
        System.err.println(DateHelper.convert(DateHelper.convert(mdhd.getModificationTime())));
        System.err.println(DateHelper.convert(DateHelper.convert(DateHelper.convert(mdhd.getModificationTime()))));*/

    }

    /**
     * @hide
     * @return
     */
    public List<ByteBuffer> getSamples() {
        return samples;
    }

    /**
     * @hide
     * @return
     */
    public SampleDescriptionBox getSampleDescriptionBox() {
        return sampleDescriptionBox;
    }

    /**
     * @hide
     * @return
     */
    public List<TimeToSampleBox.Entry> getDecodingTimeEntries() {
        return decodingTimeEntries;
    }

    /**
     * @hide
     * @return
     */
    public List<CompositionTimeToSample.Entry> getCompositionTimeEntries() {
        return compositionTimeEntries;
    }

    /**
     * @hide
     * @return
     */
    public long[] getSyncSamples() {
        return syncSamples;
    }

    /**
     * @hide
     * @return
     */
    public List<SampleDependencyTypeBox.Entry> getSampleDependencies() {
        return sampleDependencies;
    }

    /**
     * @hide
     * @return
     */
    public TrackMetaData getTrackMetaData() {
        return trackMetaData;
    }

    /**
     * @hide
     * @return
     */
    public String getHandler() {
        return handler;
    }

    /**
     * @hide
     * @return
     */
    public AbstractMediaHeaderBox getMediaHeaderBox() {
        return mihd;
    }

    /**
     * @hide
     * @return
     */
    public SubSampleInformationBox getSubsampleInformationBox() {
        return null;
    }

    /**
     * @hide
     * @return
     */
    @Override
    public String toString() {
        return "Mp4TrackImpl{" +
                "handler='" + handler + '\'' +
                '}';
    }

    /**
     * @hide
     * @return
     */
	@Override
	public List<SampleOffset> getSampleOffsets() {
		if (samples instanceof SampleList)
			return ((SampleList)samples).getSampleOffsets();
		else 
			return null;
	}
}
