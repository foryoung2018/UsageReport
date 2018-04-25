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
package com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.builder;

import android.text.TextUtils;

import com.htc.lib1.htcmp4parser.coremedia.iso.BoxParser;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoFile;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.Box;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.CompositionTimeToSample;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.ContainerBox;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.DataEntryUrlBox;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.DataInformationBox;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.DataReferenceBox;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.FileTypeBox;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.HandlerBox;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.MediaBox;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.MediaHeaderBox;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.MediaInformationBox;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.MovieBox;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.MovieHeaderBox;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.SampleSizeBox;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.SampleTableBox;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.SampleToChunkBox;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.StaticChunkOffsetBox;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.SyncSampleBox;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.TimeToSampleBox;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.TrackBox;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.TrackHeaderBox;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.mdat.SampleList;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.mdat.SampleList.SampleOffset;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.DateHelper;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.Movie;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.Track;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.HtcSlowMotionBox;
import com.htc.lib1.htcmp4parser.utils.Log;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.htc.lib1.htcmp4parser.googlecode.mp4parser.util.CastUtils.l2i;

/**
 * Creates a plain MP4 file from a video. Plain as plain can be.
 * @hide
 * {@exthide}
 */
public class DefaultMp4Builder implements Mp4Builder {
    /**
     * @hide
     */
    Set<StaticChunkOffsetBox> chunkOffsetBoxes = new HashSet<StaticChunkOffsetBox>();
    private static Logger LOG = Logger.getLogger(DefaultMp4Builder.class.getName());
    
    /**
     * @hide
     */
    HashMap<Track, List<ByteBuffer>> track2Sample = new HashMap<Track, List<ByteBuffer>>();
    /**
     * @hide
     */
    HashMap<Track, long[]> track2SampleSizes = new HashMap<Track, long[]>();
    private FragmentIntersectionFinder intersectionFinder = new TwoSecondIntersectionFinder();
    
    /**
     * @hide
     * @param intersectionFinder
     */
    public void setIntersectionFinder(FragmentIntersectionFinder intersectionFinder) {
        this.intersectionFinder = intersectionFinder;
    }
    
    private volatile boolean mCancel = false;
    private HashMap<Track, List<SampleOffset>> track2SampleOffset = new HashMap<Track, List<SampleOffset>>();
    private final String TAG = DefaultMp4Builder.class.toString();

    /**
     * @hide
     */
    protected FileDescriptor mSrcFileDescriptor = null;
    /**
     * @hide
     */
    protected String mFilePath = null;
    
    /**
     * @hide
     * @param movie
     * @param filePath
     * @return
     */
    public IsoFile build(final Movie movie, final String filePath) {
    	// to write mdat by randomAccessFile , prevent buffer overuse peoblem
    	// but we can only use this in MultiTrimTrack and MotionChangeTrack
    	mFilePath = filePath;  
    	return build(movie);
    }
    
    /**
     * @hide
     * @param movie
     * @param file descriptor
     * @return
     */
    public IsoFile build(final Movie movie, final FileDescriptor srcFD) {
    	// to write mdat by FileChannel , prevent buffer overuse problem
    	mSrcFileDescriptor = srcFD;
    	return build(movie);
    }
    
    /**
     *  @hide
     */
    protected InterleaveChunkMdat newInterleaveChunkMdat(final Movie movie){
    	return new InterleaveChunkMdat(movie);
    }
    
    
    /**
     * {@inheritDoc}
     * @hide
     */
    public IsoFile build(Movie movie) {
        LOG.fine("Creating movie " + movie);
        IsoFile isoFile = new IsoFile();
        
        for (Track track : movie.getTracks()) {
            
        	if (isCancel()) {
            	return isoFile;
            }
        	
        	// getting the samples may be a time consuming activity
            List<ByteBuffer> samples = track.getSamples();
            
            if (!isFilePathEmpty()) {
            	
            	final List<SampleOffset> offsets = track.getSampleOffsets();
                if (offsets == null) {
                	// in this case , we cannot get sample offset form sampleList
                	// Do not use randomAccessFile to write mdat 
                	mFilePath = null;
                	android.util.Log.d("DefaultMp4Builder", "cannot getSampleList ");
                }
                
            	putSampleOffsets(track, offsets);
            }
            
            if (isCancel()) {
            	return isoFile;
            }
            
            putSamples(track, samples);            
            long[] sizes = new long[samples.size()];
            for (int i = 0; i < sizes.length; i++) {
                sizes[i] = samples.get(i).limit();
                if (isCancel()) {
                	return isoFile;
                }
            }
            putSampleSizes(track, sizes);
        }

        
        // ouch that is ugly but I don't know how to do it else
        List<String> minorBrands = new LinkedList<String>();
        minorBrands.add("isom");
        minorBrands.add("iso2");
        minorBrands.add("avc1");

        isoFile.addBox(new FileTypeBox("isom", 0, minorBrands));
        
        for(Box box: movie.getBoxs()) {
        	if (box instanceof HtcSlowMotionBox) {
        		isoFile.addBox(box);
        		break;
        	}
        }
        
        //add moov
        isoFile.addBox( createMovieBox(movie));
        
        //add mdat
        final InterleaveChunkMdat mdat = newInterleaveChunkMdat(movie);
        isoFile.addBox(mdat);
        
        /*
        dataOffset is where the first sample starts. In this special mdat the samples always start
        at offset 16 so that we can use the same offset for large boxes and small boxes
         */
        long dataOffset = mdat.getDataOffset();
        for (StaticChunkOffsetBox chunkOffsetBox : chunkOffsetBoxes) {
            long[] offsets = chunkOffsetBox.getChunkOffsets();
            for (int i = 0; i < offsets.length; i++) {
                offsets[i] += dataOffset;
                if (isCancel()) {
                	return isoFile;
                }
            }
        }


        return isoFile;
    }
    /**
     * @hide
     */
    public void cancel() {
    	mCancel = true;
    }
    /**
     * @hide
     * @return
     */
    private boolean isCancel() {
    	return mCancel;
    }

    /**
     * @hide
     * @return
     */
    public FragmentIntersectionFinder getFragmentIntersectionFinder() {
        throw new UnsupportedOperationException("No fragment intersection finder in default MP4 builder!");
    }
    
    /**
     * @hide
     * @param track
     * @param sizes
     * @return
     */
    protected long[] putSampleSizes(Track track, long[] sizes) {
        return track2SampleSizes.put(track, sizes);
    }

    /**
     * @hide 
     * @param track
     * @param samples
     * @return
     */
    protected List<ByteBuffer> putSamples(Track track, List<ByteBuffer> samples) {
        return track2Sample.put(track, samples);
    }
    
    private List<SampleOffset> putSampleOffsets(Track track, List<SampleOffset> offsets) {
    	return track2SampleOffset.put(track,offsets);
    }
    
    /**
     *  @hide
     */
    protected MovieBox createMovieBox(Movie movie) {
        MovieBox movieBox = new MovieBox();
        MovieHeaderBox mvhd = new MovieHeaderBox();

        mvhd.setCreationTime(DateHelper.convert(new Date()));
        mvhd.setModificationTime(DateHelper.convert(new Date()));

        long movieTimeScale = getTimescale(movie);
        long duration = 0;

        for (Track track : movie.getTracks()) {
            long tracksDuration = getDuration(track) * movieTimeScale / track.getTrackMetaData().getTimescale();
            if (tracksDuration > duration) {
                duration = tracksDuration;
            }
        }

        mvhd.setDuration(duration);
        mvhd.setTimescale(movieTimeScale);
        // find the next available trackId
        long nextTrackId = 0;
        for (Track track : movie.getTracks()) {
            nextTrackId = nextTrackId < track.getTrackMetaData().getTrackId() ? track.getTrackMetaData().getTrackId() : nextTrackId;
        }
        mvhd.setNextTrackId(++nextTrackId);
        if (mvhd.getCreationTime() >= 1l << 32 ||
                mvhd.getModificationTime() >= 1l << 32 ||
                mvhd.getDuration() >= 1l << 32) {
            mvhd.setVersion(1);
        }

        movieBox.addBox(mvhd);
        
        if (isCancel()) {
        	return movieBox;
        }
        
        for (Track track : movie.getTracks()) {
        	if (isCancel()) {
        		return movieBox;
        	}
            movieBox.addBox(createTrackBox(track, movie));
        }
        // metadata here
        Box udta = createUdta(movie);
        if (udta != null) {
            movieBox.addBox(udta);
        }
        return movieBox;

    }

    /**
     * Override to create a user data box that may contain metadata.
     * @hide
     * @return a 'udta' box or <code>null</code> if none provided
     */
    protected Box createUdta(Movie movie) {
        return null;
    }

    private TrackBox createTrackBox(Track track, Movie movie) {

        LOG.info("Creating Mp4TrackImpl " + track);
        TrackBox trackBox = new TrackBox();
        TrackHeaderBox tkhd = new TrackHeaderBox();
        int flags = 0;
        if (track.isEnabled()) {
            flags += 1;
        }

        if (track.isInMovie()) {
            flags += 2;
        }

        if (track.isInPreview()) {
            flags += 4;
        }

        if (track.isInPoster()) {
            flags += 8;
        }
        tkhd.setFlags(flags);

        tkhd.setAlternateGroup(track.getTrackMetaData().getGroup());
        tkhd.setCreationTime(DateHelper.convert(track.getTrackMetaData().getCreationTime()));
        // We need to take edit list box into account in trackheader duration
        // but as long as I don't support edit list boxes it is sufficient to
        // just translate media duration to movie timescale
        tkhd.setDuration(getDuration(track) * getTimescale(movie) / track.getTrackMetaData().getTimescale());
        tkhd.setHeight(track.getTrackMetaData().getHeight());
        tkhd.setWidth(track.getTrackMetaData().getWidth());
        tkhd.setLayer(track.getTrackMetaData().getLayer());
        tkhd.setModificationTime(DateHelper.convert(new Date()));
        tkhd.setTrackId(track.getTrackMetaData().getTrackId());
        tkhd.setVolume(track.getTrackMetaData().getVolume());
        tkhd.setMatrix(track.getTrackMetaData().getMatrix());
        if (tkhd.getCreationTime() >= 1l << 32 ||
                tkhd.getModificationTime() >= 1l << 32 ||
                tkhd.getDuration() >= 1l << 32) {
            tkhd.setVersion(1);
        }

        trackBox.addBox(tkhd);
        
        if (isCancel()) {
        	return trackBox;
        }
        
/*
        EditBox edit = new EditBox();
        EditListBox editListBox = new EditListBox();
        editListBox.setEntries(Collections.singletonList(
                new EditListBox.Entry(editListBox, (long) (track.getTrackMetaData().getStartTime() * getTimescale(movie)), -1, 1)));
        edit.addBox(editListBox);
        trackBox.addBox(edit);
*/

        MediaBox mdia = new MediaBox();
        trackBox.addBox(mdia);
        MediaHeaderBox mdhd = new MediaHeaderBox();
        mdhd.setCreationTime(DateHelper.convert(track.getTrackMetaData().getCreationTime()));
        mdhd.setDuration(getDuration(track));
        mdhd.setTimescale(track.getTrackMetaData().getTimescale());
        mdhd.setLanguage(track.getTrackMetaData().getLanguage());
        mdia.addBox(mdhd);
        HandlerBox hdlr = new HandlerBox();
        mdia.addBox(hdlr);

        hdlr.setHandlerType(track.getHandler());

        MediaInformationBox minf = new MediaInformationBox();
        minf.addBox(track.getMediaHeaderBox());

        // dinf: all these three boxes tell us is that the actual
        // data is in the current file and not somewhere external
        DataInformationBox dinf = new DataInformationBox();
        DataReferenceBox dref = new DataReferenceBox();
        dinf.addBox(dref);
        DataEntryUrlBox url = new DataEntryUrlBox();
        url.setFlags(1);
        dref.addBox(url);
        minf.addBox(dinf);
        //

        SampleTableBox stbl = new SampleTableBox();

        stbl.addBox(track.getSampleDescriptionBox());

        List<TimeToSampleBox.Entry> decodingTimeToSampleEntries = track.getDecodingTimeEntries();
        if (decodingTimeToSampleEntries != null && !track.getDecodingTimeEntries().isEmpty()) {
            TimeToSampleBox stts = new TimeToSampleBox();
            stts.setEntries(track.getDecodingTimeEntries());
            stbl.addBox(stts);
            if (isCancel()) {
            	return trackBox;
            }
        }

        List<CompositionTimeToSample.Entry> compositionTimeToSampleEntries = track.getCompositionTimeEntries();
        if (compositionTimeToSampleEntries != null && !compositionTimeToSampleEntries.isEmpty()) {
            CompositionTimeToSample ctts = new CompositionTimeToSample();
            ctts.setEntries(compositionTimeToSampleEntries);
            stbl.addBox(ctts);
            if (isCancel()) {
            	return trackBox;
            }
        }

        long[] syncSamples = track.getSyncSamples();
        if (syncSamples != null && syncSamples.length > 0) {
            SyncSampleBox stss = new SyncSampleBox();
            stss.setSampleNumber(syncSamples);
            stbl.addBox(stss);
        }

        if (track.getSampleDependencies() != null && !track.getSampleDependencies().isEmpty()) {
            SampleDependencyTypeBox sdtp = new SampleDependencyTypeBox();
            sdtp.setEntries(track.getSampleDependencies());
            stbl.addBox(sdtp);
        }
        HashMap<Track, int[]> track2ChunkSizes = new HashMap<Track, int[]>();
        for (Track current : movie.getTracks()) {
            track2ChunkSizes.put(current, getChunkSizes(current, movie));
            if (isCancel()) {
            	return trackBox;
            }
        }
        int[] tracksChunkSizes = track2ChunkSizes.get(track);

        SampleToChunkBox stsc = new SampleToChunkBox();
        stsc.setEntries(new LinkedList<SampleToChunkBox.Entry>());
        long lastChunkSize = Integer.MIN_VALUE; // to be sure the first chunks hasn't got the same size
        
        final int tracksChunkSizesLength;
        
		if (tracksChunkSizes != null) {
			tracksChunkSizesLength = tracksChunkSizes.length;
			for (int i = 0; i < tracksChunkSizesLength; i++) {
				// The sample description index references the sample description box
				// that describes the samples of this chunk. My Tracks cannot have more
				// than one sample description box. Therefore 1 is always right
				// the first chunk has the number '1'
				if (lastChunkSize != tracksChunkSizes[i]) {
					stsc.getEntries().add( new SampleToChunkBox.Entry(i + 1, tracksChunkSizes[i], 1));
					lastChunkSize = tracksChunkSizes[i];
				}
				if (isCancel()) {
	            	return trackBox;
	            }
			}
		} else {
			tracksChunkSizesLength = 0;
			Log.e(DefaultMp4Builder.class.getName(), "Get tracksChunkSizes from map is null in createTrackBox()");
		}
        stbl.addBox(stsc);

        SampleSizeBox stsz = new SampleSizeBox();
        stsz.setSampleSizes(track2SampleSizes.get(track));

        stbl.addBox(stsz);
        // The ChunkOffsetBox we create here is just a stub
        // since we haven't created the whole structure we can't tell where the
        // first chunk starts (mdat box). So I just let the chunk offset
        // start at zero and I will add the mdat offset later.
        StaticChunkOffsetBox stco = new StaticChunkOffsetBox();
        this.chunkOffsetBoxes.add(stco);
        long offset = 0;
        long[] chunkOffset = new long[tracksChunkSizesLength];
        // all tracks have the same number of chunks
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Calculating chunk offsets for track_" + track.getTrackMetaData().getTrackId());
        }


        for (int i = 0; i < tracksChunkSizesLength; i++) {
            // The filelayout will be:
            // chunk_1_track_1,... ,chunk_1_track_n, chunk_2_track_1,... ,chunk_2_track_n, ... , chunk_m_track_1,... ,chunk_m_track_n
            // calculating the offsets
            if (LOG.isLoggable(Level.FINER)) {
                LOG.finer("Calculating chunk offsets for track_" + track.getTrackMetaData().getTrackId() + " chunk " + i);
            }
            for (Track current : movie.getTracks()) {
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("Adding offsets of track_" + current.getTrackMetaData().getTrackId());
                }
				int[] chunkSizes = track2ChunkSizes.get(current);
				long firstSampleOfChunk = 0;
				if (current == track) {
					chunkOffset[i] = offset;
				}
				if (chunkSizes != null) {
					for (int j = 0; j < i; j++) {
						firstSampleOfChunk += chunkSizes[j];
					}             
					for (int j = l2i(firstSampleOfChunk); j < firstSampleOfChunk + chunkSizes[i]; j++) {
						final long[] track2SampleSizeArray = track2SampleSizes.get(current);
						final Long track2SampleSize = (track2SampleSizeArray != null) ? track2SampleSizeArray[j] : null; 
						if (track2SampleSize != null) {
							offset += track2SampleSize;
						} else {
							Log.e(DefaultMp4Builder.class.getName(), "Get track2SampleSize from map is null");
						}
						if (isCancel()) {
			            	return trackBox;
			            }
					}
				}
				if (isCancel()) {
	            	return trackBox;
	            }
			}
        }
        stco.setChunkOffsets(chunkOffset);
        stbl.addBox(stco);
        minf.addBox(stbl);
        mdia.addBox(minf);

        return trackBox;
    }
    
    /**
     * @hide
     * {@exthide}
     */
    public class InterleaveChunkMdat implements Box {
        /**
         * @hide
         */
        List<Track> tracks;
        
        /**
         * @hide
         */
        List<ByteBuffer> samples = new ArrayList<ByteBuffer>();
        
        /**
         * @hide
         */
        ArrayList<SampleOffset> sampleOffsets = new ArrayList<SampleList.SampleOffset>();
        
        /**
         * @hide
         */
        ContainerBox parent;
        
        /**
         * @hide
         */
        long contentSize = 0;

        /**
         * @hide
         * @return
         */
        public ContainerBox getParent() {
            return parent;
        }
        
        /**
         * @hide
         * @param parent
         */
        public void setParent(ContainerBox parent) {
            this.parent = parent;
        }
        
        /**
         * @hide
         * @param readableByteChannel
         * @param header
         * @param contentSize
         * @param boxParser
         * @throws IOException
         */
        public void parse(ReadableByteChannel readableByteChannel, ByteBuffer header, long contentSize, BoxParser boxParser) throws IOException {
        }
        
        /**
         * @hide         
         */
        protected InterleaveChunkMdat(Movie movie) {

            tracks = movie.getTracks();
            Map<Track, int[]> chunks = new HashMap<Track, int[]>();
            for (Track track : movie.getTracks()) {
            	if (DefaultMp4Builder.this.isCancel()) {
            		return;
            	}
                chunks.put(track, getChunkSizes(track, movie));
            }

            for (int i = 0; i < chunks.values().iterator().next().length; i++) {
                
            	if (DefaultMp4Builder.this.isCancel()) {
            		return;
            	}
            	
            	for (Track track : tracks) {
            		
            		if (DefaultMp4Builder.this.isCancel()) {
                		return;
                	}

                    int[] chunkSizes = chunks.get(track);
                    long firstSampleOfChunk = 0;
                    
                    if (chunkSizes != null) {
                    	for (int j = 0; j < i; j++) {
                            firstSampleOfChunk += chunkSizes[j];
                        }

                        for (int j = l2i(firstSampleOfChunk); j < firstSampleOfChunk + chunkSizes[i]; j++) {
                        	
                        	if (DefaultMp4Builder.this.isCancel()) {
                        		return;
                        	}
                        	
                        	if (!isFilePathEmpty() && DefaultMp4Builder.this.track2SampleOffset != null) {
                        		List<SampleOffset> sampleOffsetList = DefaultMp4Builder.this.track2SampleOffset.get(track);
                        		if (sampleOffsetList != null ){
                        			final SampleOffset offset = sampleOffsetList.get(j);
                        			contentSize += offset.getLength();
                            		sampleOffsets.add(offset);
                        		} else {
                        			Log.e(DefaultMp4Builder.class.getName(), "Get sampleOffsetList from map is null");
                        		}                    		                   		
                        	} else {
                        		final List<ByteBuffer> sample = DefaultMp4Builder.this.track2Sample.get(track);
                        		final ByteBuffer s = (sample != null) ? sample.get(j) : null;
                        		if (s != null) {
                        			contentSize += s.limit();
                        			samples.add((ByteBuffer) s.rewind());
                        		}
                        	}
                        	
                        }
                    } else {
                    	Log.e(DefaultMp4Builder.class.getName(), "Get chunkSizes from map is null in InterleaveChunkMdat()");
                    }

                }

            }

        }
        
        /**
         * @hide
         * @return
         */
        public long getDataOffset() {
            Box b = this;            
            long offset = isSmallBox()?8:16;
            while (b.getParent() != null) {
                for (Box box : b.getParent().getBoxes()) {
                    if (b == box) {
                        break;
                    }
                    offset += box.getSize();
                }
                b = b.getParent();
            }
            return offset;
        }

        /**
         * @hide
         * @return
         */
        public String getType() {
            return "mdat";
        }
        
        /**
         * @hide
         * @return
         */
        public long getSize() {
        	if (isSmallBox())
        		return 8 + contentSize;
        	else 
        		return 16 + contentSize;
        }

        private boolean isSmallBox() {
            return (contentSize + 8) < 4294967296L;
        }
        
        /**
         * @hide
         * @param writableByteChannel
         * @throws IOException
         */
        public void getBoxHeader(WritableByteChannel writableByteChannel) throws IOException  {
        	ByteBuffer bb = ByteBuffer.allocate(16);
            long size = getSize();
            if (isSmallBox()) {
                IsoTypeWriter.writeUInt32(bb, size);
            } else {
                IsoTypeWriter.writeUInt32(bb, 1);
            }
            bb.put(IsoFile.fourCCtoBytes("mdat"));
            if (isSmallBox()) {
                //bb.put(new byte[8]);
            	//we would not always write extra 8 byte when mdata is small box
            	bb.flip();
            } else {
                IsoTypeWriter.writeUInt64(bb, size);
            }
            bb.rewind();
            writableByteChannel.write(bb);
        }
        
        /**
         * @hide
         * @param writableByteChannel
         * @throws IOException
         */
        public void getBox(WritableByteChannel writableByteChannel) throws IOException {
        	getBoxHeader(writableByteChannel);
            writeMdat(writableByteChannel);
        }
        
        /**
         * @hide
         * @param writableByteChannel
         * @throws IOException
         */
        public void writeMdat(WritableByteChannel writableByteChannel) throws IOException {
            if (writableByteChannel instanceof GatheringByteChannel) {
                List<ByteBuffer> nuSamples = unifyAdjacentBuffers(samples);

                int STEPSIZE = 1024;
                for (int i = 0; i < Math.ceil((double) nuSamples.size() / STEPSIZE); i++) {
                    List<ByteBuffer> sublist = nuSamples.subList(
                            i * STEPSIZE, // start
                            (i + 1) * STEPSIZE < nuSamples.size() ? (i + 1) * STEPSIZE : nuSamples.size()); // end
                    ByteBuffer sampleArray[] = sublist.toArray(new ByteBuffer[sublist.size()]);
                    do {
                        ((GatheringByteChannel) writableByteChannel).write(sampleArray);
                    } while (sampleArray[sampleArray.length - 1].remaining() > 0);
                }
                //System.err.println(bytesWritten);
            } else {
            	
            	if (isFilePathEmpty()) {
	            	for (ByteBuffer sample : samples) {
	                    sample.rewind();
	                    writableByteChannel.write(sample);	                    
	                }
            	} else {
            		Log.d(TAG , "input parameter FileDescriptor or FilePath, use FileChannel");
            		
            		final int BUFFER_SIZE = 1024 * 16;
            		final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
	            	
	            	FileInputStream fis = null;
	            	try {
	            		FileChannel inChannel = (fis = (null != mFilePath) ? new FileInputStream(mFilePath) : new FileInputStream(mSrcFileDescriptor)).getChannel();
	            		
						for (SampleOffset sampleOffset : sampleOffsets) {
							final int sampleSize = sampleOffset.getLength();
							final long samplePos = sampleOffset.getStartPos();
							if (inChannel != null) {
								inChannel.position(samplePos);
								
							    int remainSize = sampleSize;
							    while (remainSize > 0) {
									int sizeToRead = 0; 
									if (remainSize > BUFFER_SIZE)
										sizeToRead = BUFFER_SIZE;
									else
										sizeToRead = remainSize;
									
									buffer.clear();
									buffer.limit(sizeToRead);
									
									final int readSize = inChannel.read(buffer);
									remainSize -= readSize;	            			
							
									if (readSize > 0) {
										buffer.flip();
										writableByteChannel.write(buffer);
									} 
								}
							}
						} //end write
					} finally {
						try{
/*							if(null != fis){
								fis.close();
							}*/
						}catch(final Exception e){
							Log.d("close: " + e.getMessage());
						}
					}
            	}
            	
            }
        }

    }

    /**
     * Gets the chunk sizes for the given track.
     * @hide
     * @param track
     * @param movie
     * @return
     */
    int[] getChunkSizes(Track track, Movie movie) {

        long[] referenceChunkStarts = intersectionFinder.sampleNumbers(track, movie);
        int[] chunkSizes = new int[referenceChunkStarts.length];


        for (int i = 0; i < referenceChunkStarts.length; i++) {
            long start = referenceChunkStarts[i] - 1;
            long end;
            if (referenceChunkStarts.length == i + 1) {
                end = track.getSamples().size();
            } else {
                end = referenceChunkStarts[i + 1] - 1;
            }

            chunkSizes[i] = l2i(end - start);
            // The Stretch makes sure that there are as much audio and video chunks!
        }
        assert DefaultMp4Builder.this.track2Sample.get(track).size() == sum(chunkSizes) : "The number of samples and the sum of all chunk lengths must be equal";
        return chunkSizes;


    }

    private static long sum(int[] ls) {
        long rc = 0;
        for (long l : ls) {
            rc += l;
        }
        return rc;
    }
    
    /**
     * @hide
     * @param track
     * @return
     */
    protected static long getDuration(Track track) {
        long duration = 0;
        for (TimeToSampleBox.Entry entry : track.getDecodingTimeEntries()) {
            duration += entry.getCount() * entry.getDelta();
        }
        return duration;
    }
    
    /**
     * @hide
     * @param movie
     * @return
     */
    public long getTimescale(Movie movie) {
        long timescale = movie.getTracks().iterator().next().getTrackMetaData().getTimescale();
        for (Track track : movie.getTracks()) {
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
     * @param samples
     * @return
     */
    public List<ByteBuffer> unifyAdjacentBuffers(List<ByteBuffer> samples) {
        ArrayList<ByteBuffer> nuSamples = new ArrayList<ByteBuffer>(samples.size());
        for (ByteBuffer buffer : samples) {
            int lastIndex = nuSamples.size() - 1;
            if (lastIndex >= 0 && buffer.hasArray() && nuSamples.get(lastIndex).hasArray() && buffer.array() == nuSamples.get(lastIndex).array() &&
                    nuSamples.get(lastIndex).arrayOffset() + nuSamples.get(lastIndex).limit() == buffer.arrayOffset()) {
                ByteBuffer oldBuffer = nuSamples.remove(lastIndex);
                ByteBuffer nu = ByteBuffer.wrap(buffer.array(), oldBuffer.arrayOffset(), oldBuffer.limit() + buffer.limit()).slice();
                // We need to slice here since wrap([], offset, length) just sets position and not the arrayOffset.
                nuSamples.add(nu);
            } else if (lastIndex >= 0 &&
                    buffer instanceof MappedByteBuffer && nuSamples.get(lastIndex) instanceof MappedByteBuffer &&
                    nuSamples.get(lastIndex).limit() == nuSamples.get(lastIndex).capacity() - buffer.capacity()) {
                // This can go wrong - but will it?
                ByteBuffer oldBuffer = nuSamples.get(lastIndex);
                oldBuffer.limit(buffer.limit() + oldBuffer.limit());
            } else {
                nuSamples.add(buffer);
            }
        }
        return nuSamples;
    }
    
    /**
     * {@inheritDoc}
     * @hide
     */
    private boolean isFilePathEmpty() {
    	return (TextUtils.isEmpty(mFilePath) && mSrcFileDescriptor == null) ? true : false;
    }
}
