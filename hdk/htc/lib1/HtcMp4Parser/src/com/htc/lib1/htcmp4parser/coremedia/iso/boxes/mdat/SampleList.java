package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.mdat;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoFile;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.*;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.fragment.*;
import com.htc.lib1.htcmp4parser.utils.Log;

import java.nio.ByteBuffer;
import java.util.*;

import static com.htc.lib1.htcmp4parser.googlecode.mp4parser.util.CastUtils.l2i;

/**
 * Creates a list of <code>ByteBuffer</code>s that represent the samples of a given track.
 *  @hide
 * {@exthide}
 */
public class SampleList extends AbstractList<ByteBuffer> {

    /**
     * @hide
     */
    long[] offsets;
    /**
     * @hide
     */
    long[] sizes;
    /**
     * @hide
     */
    IsoFile isoFile;
    /**
     * @hide
     */
    HashMap<MediaDataBox, Long> mdatStartCache = new HashMap<MediaDataBox, Long>();
    /**
     * @hide
     */
    HashMap<MediaDataBox, Long> mdatEndCache = new HashMap<MediaDataBox, Long>();
    /**
     * @hide
     */
    MediaDataBox[] mdats;

    /**
     * Gets a sorted random access optimized list of all sample offsets.
     * Basically it is a map from sample number to sample offset.
     *
     * @return the sorted list of sample offsets
     * @hide
     */
    public long[] getOffsetKeys() {
        return offsets;
    }

    /**
     * @hide
     */
    public SampleList(TrackBox trackBox) {
        initIsoFile(trackBox.getIsoFile()); // where are we?

        // first we get all sample from the 'normal' MP4 part.
        // if there are none - no problem.
        
        final SampleTableBox sampleTableBox = trackBox.getSampleTableBox();
        
        SampleSizeBox sampleSizeBox = null;
        ChunkOffsetBox chunkOffsetBox = null;
        SampleToChunkBox sampleToChunkBox = null;
        
        if (sampleTableBox != null) {
        	sampleSizeBox = sampleTableBox.getSampleSizeBox();
        	chunkOffsetBox = sampleTableBox.getChunkOffsetBox();
        	sampleToChunkBox = sampleTableBox.getSampleToChunkBox();
        } else {
        	Log.e(SampleList.class.getName(), "Get sampleTableBoxis is null");
        }        


        final long[] chunkOffsets = chunkOffsetBox != null ? chunkOffsetBox.getChunkOffsets() : new long[0];
        if (sampleToChunkBox != null && sampleToChunkBox.getEntries().size() > 0 &&
                chunkOffsets.length > 0 && sampleSizeBox != null && sampleSizeBox.getSampleCount() > 0) {
            long[] numberOfSamplesInChunk = sampleToChunkBox.blowup(chunkOffsets.length);

            int sampleIndex = 0;

            if (sampleSizeBox.getSampleSize() > 0) {
                sizes = new long[l2i(sampleSizeBox.getSampleCount())];
                Arrays.fill(sizes, sampleSizeBox.getSampleSize());
            } else {
                sizes = sampleSizeBox.getSampleSizes();
            }
            offsets = new long[sizes.length];

                for (int i = 0; i < numberOfSamplesInChunk.length; i++) {
                    long thisChunksNumberOfSamples = numberOfSamplesInChunk[i];
                    long sampleOffset = chunkOffsets[i];
                    for (int j = 0; j < thisChunksNumberOfSamples; j++) {
                    long sampleSize = sizes[sampleIndex];
                    offsets[sampleIndex] = sampleOffset;
                        sampleOffset += sampleSize;
                        sampleIndex++;
                    }
                }

            }

        // Next we add all samples from the fragments
        // in most cases - I've never seen it different it's either normal or fragmented.        
        List<MovieExtendsBox> movieExtendsBoxes = trackBox.getParent().getBoxes(MovieExtendsBox.class);
        
        final TrackHeaderBox trackHeaderBox = trackBox.getTrackHeaderBox();
        
        if (trackHeaderBox != null && movieExtendsBoxes.size() > 0) {
            Map<Long, Long> offsets2Sizes = new HashMap<Long, Long>();
            List<TrackExtendsBox> trackExtendsBoxes = movieExtendsBoxes.get(0).getBoxes(TrackExtendsBox.class);
            for (TrackExtendsBox trackExtendsBox : trackExtendsBoxes) {
                if (trackExtendsBox.getTrackId() == trackHeaderBox.getTrackId()) {
                    for (MovieFragmentBox movieFragmentBox : trackBox.getIsoFile().getBoxes(MovieFragmentBox.class)) {
                        offsets2Sizes.putAll(getOffsets(movieFragmentBox, trackHeaderBox.getTrackId(), trackExtendsBox));
                    }
                }
            }
            
            if (sizes == null || offsets == null) {
                sizes = new long[0];
                offsets = new long[0];
            }
            
            splitToArrays(offsets2Sizes);
        }
        
        // We have now a map from all sample offsets to their sizes
    }

    private void splitToArrays(Map<Long, Long> offsets2Sizes) {
        List<Long> keys = new ArrayList<Long>(offsets2Sizes.keySet());
        Collections.sort(keys);

        long[] nuSizes = new long[sizes.length + keys.size()];
        System.arraycopy(sizes, 0, nuSizes, 0, sizes.length);
        long[] nuOffsets = new long[offsets.length + keys.size()];
        System.arraycopy(offsets, 0, nuOffsets, 0, offsets.length);
        for (int i = 0; i < keys.size(); i++) {
            nuOffsets[i + offsets.length] = keys.get(i);
            nuSizes[i + sizes.length] = offsets2Sizes.get(keys.get(i));
        }
        sizes = nuSizes;
        offsets = nuOffsets;
    }
    /**
     * @hide
     */    
    public SampleList(TrackFragmentBox traf) {
        sizes = new long[0];
        offsets = new long[0];
        Map<Long, Long> offsets2Sizes = new HashMap<Long, Long>();
        initIsoFile(traf.getIsoFile());

        final List<MovieFragmentBox> movieFragmentBoxList = isoFile.getBoxes(MovieFragmentBox.class);        
        final TrackFragmentHeaderBox trackFragmentHeaderBox = traf.getTrackFragmentHeaderBox();
        
        long trackId = 0;
        if (trackFragmentHeaderBox != null) {
        	trackId = trackFragmentHeaderBox.getTrackId();
        } else {
        	Log.e(SampleList.class.getName(), "Get trackFragmentHeaderBox is null");
        }
        
        for (MovieFragmentBox moof : movieFragmentBoxList) {
            final List<TrackFragmentHeaderBox> trackFragmentHeaderBoxes = moof.getTrackFragmentHeaderBoxes();
            for (TrackFragmentHeaderBox tfhd : trackFragmentHeaderBoxes) {
                if (tfhd.getTrackId() == trackId) {
                    offsets2Sizes.putAll(getOffsets(moof, trackId, null));
                }
            }
        }
        splitToArrays(offsets2Sizes);
    }

    private void initIsoFile(IsoFile isoFile) {
        this.isoFile = isoFile;
        // find all mdats first to be able to use them later with explicitly looking them up
        long currentOffset = 0;
        LinkedList<MediaDataBox> mdats = new LinkedList<MediaDataBox>();
        for (Box b : this.isoFile.getBoxes()) {
            long currentSize = b.getSize();
            if ("mdat".equals(b.getType())) {
                if (b instanceof MediaDataBox) {
                    long contentOffset = currentOffset + ((MediaDataBox) b).getHeader().limit();
                    mdatStartCache.put((MediaDataBox) b, contentOffset);
                    mdatEndCache.put((MediaDataBox) b, contentOffset + currentSize);
                    mdats.add((MediaDataBox) b);
                } else {
                    throw new RuntimeException("Sample need to be in mdats and mdats need to be instanceof MediaDataBox");
                }
            }
            currentOffset += currentSize;
        }
        this.mdats = mdats.toArray(new MediaDataBox[mdats.size()]);
    }

    /**
     * @hide
     */
    @Override
    public int size() {
        return sizes.length;
    }

    /**
     * @hide
     */
    @Override
    public ByteBuffer get(int index) {
        // it is a two stage lookup: from index to offset to size
        long offset = offsets[index];
        int sampleSize = l2i(sizes[index]);

        for (MediaDataBox mediaDataBox : mdats) {
        	long start = getMediaDataBox(mdatStartCache, mediaDataBox);
        	long end = getMediaDataBox(mdatEndCache, mediaDataBox);
            if ((start <= offset) && (offset + sampleSize <= end)) {
                return mediaDataBox.getContent(offset - start, sampleSize);
            }
        }

        throw new RuntimeException("The sample with offset " + offset + " and size " + sampleSize + " is NOT located within an mdat");
    }
    /**
     * @hide
     */    
    public List<SampleOffset> getSampleOffsets() {
    	List<SampleOffset> ret = new ArrayList<SampleOffset>();
    	for (int index=0, size=this.size(); index<size ; index++) {
    		long offset = offsets[index];
            int sampleSize = l2i(sizes[index]);
            
            boolean found = false;
            for (MediaDataBox mediaDataBox : mdats) {
            	long start = getMediaDataBox(mdatStartCache, mediaDataBox);
            	long end = getMediaDataBox(mdatEndCache, mediaDataBox);
                if ((start <= offset) && (offset + sampleSize <= end)) {
                	ret.add( mediaDataBox.getContentOffset(offset - start, sampleSize) );
                	found = true;
                	break;
                }
            }
            
            if (!found)
            	throw new RuntimeException("getSampleOffsets, The sample with offset " + offset + " and size " + sampleSize + " is NOT located within an mdat");           
    	}
    	return ret;
    }
    /**
     * @hide
     */
    Map<Long, Long> getOffsets(MovieFragmentBox moof, long trackId, TrackExtendsBox trex) {
        Map<Long, Long> offsets2Sizes = new HashMap<Long, Long>();
        List<TrackFragmentBox> traf = moof.getBoxes(TrackFragmentBox.class);
        for (TrackFragmentBox trackFragmentBox : traf) {        	
        	final TrackFragmentHeaderBox trackFragmentHeaderBox = trackFragmentBox.getTrackFragmentHeaderBox();        	
            if (trackFragmentHeaderBox != null && trackFragmentHeaderBox.getTrackId() == trackId) {
                long baseDataOffset;
                if (trackFragmentHeaderBox.hasBaseDataOffset()) {
                    baseDataOffset = trackFragmentHeaderBox.getBaseDataOffset();
                } else {
                    baseDataOffset = moof.getOffset();
                }

                for (TrackRunBox trun : trackFragmentBox.getBoxes(TrackRunBox.class)) {
                    long sampleBaseOffset = baseDataOffset + trun.getDataOffset();
                    final TrackFragmentHeaderBox tfhd = ((TrackFragmentBox) trun.getParent()).getTrackFragmentHeaderBox();

                    long offset = 0;
                    for (TrackRunBox.Entry entry : trun.getEntries()) {
                        final long sampleSize;
                        if (trun.isSampleSizePresent()) {
                            sampleSize = entry.getSampleSize();
                            offsets2Sizes.put(offset + sampleBaseOffset, sampleSize);
                            offset += sampleSize;
                        } else {
                            if (tfhd != null && tfhd.hasDefaultSampleSize()) {
                                sampleSize = tfhd.getDefaultSampleSize();
                                offsets2Sizes.put(offset + sampleBaseOffset, sampleSize);
                                offset += sampleSize;
                            } else {
                                if (trex == null) {
                                    throw new RuntimeException("File doesn't contain trex box but track fragments aren't fully self contained. Cannot determine sample size.");
                                }
                                sampleSize = trex.getDefaultSampleSize();
                                offsets2Sizes.put(offset + sampleBaseOffset, sampleSize);
                                offset += sampleSize;
                            }
                        }
                    }
                }
            }
        }
        return offsets2Sizes;
    }
    /**
     * @hide
     */    
    public static class SampleOffset {
    	private long startPos;
    	private int length;
    	
    	public SampleOffset(final long start, final int length) {
    		this.startPos = start;
    		this.length = length;
    	}
    	
    	public long getStartPos() {
    		return startPos;
    	}
    	
    	public int getLength() {
    		return length;
    	}
    	
    	@Override
		public String toString() {
    		return "startPos: " + startPos + " endPos:" + length;
    	}
    }
    
    private long getMediaDataBox(HashMap<MediaDataBox, Long> dataCache, MediaDataBox mediaDataBox) {
    	long returnValue = 0;    	
    	Long meidaIndex = dataCache.get(mediaDataBox);
    	// check nullpointer
    	if (meidaIndex != null) {
    		returnValue = meidaIndex;
    	} else {
    		Log.e(SampleList.class.getName(), "Get meidaIndex from mediaDataBox is null");
    	}    	
    	return returnValue;
    }
    /**
     * @hide
     */    
    public long getMetaDataBoxOffset() {
    	if (mdats == null || mdats.length != 1) {
    		return -1;
    	}
    	
    	final MediaDataBox mdat = mdats[0];
    	final long mdatOffset = mdat.getMediaDataBoxStartPos();
    	Log.d(SampleList.class.getSimpleName(), "mDataOffset: " + mdatOffset);
    	return mdatOffset;
    }
    /**
     * @hide
     */    
    public long getMetaDataBoxSize() {
    	if (mdats == null || mdats.length != 1) {
    		return -1;
    	}
    	
    	final MediaDataBox mdat = mdats[0];
    	final long mdatSize = mdat.getContentSize();
    	Log.d(SampleList.class.getSimpleName(), "mDataSize: " + mdatSize);
    	return mdatSize;
    }

}
