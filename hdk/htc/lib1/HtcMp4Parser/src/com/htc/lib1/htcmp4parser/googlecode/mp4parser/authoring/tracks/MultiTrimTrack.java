package com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.tracks;

import android.annotation.SuppressLint;

import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.*;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.mdat.SampleList;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.mdat.SampleList.SampleOffset;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.AbstractTrack;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.Track;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.TrackMetaData;
import com.htc.lib1.htcmp4parser.utils.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Generates a Track that starts at fromSample and ends at toSample (exclusive). The user of this class
 * has to make sure that the fromSample is a random access sample.
 * <ul>
 * <li>In AAC this is every single sample</li>
 * <li>In H264 this is every sample that is marked in the SyncSampleBox</li>
 * </ul>
 * @hide
 * {@exthide}
 */
@SuppressLint("ParserError")
public class MultiTrimTrack extends AbstractTrack {
    /**
     * @hide
     */
    Track origTrack;

    private final String TAG = this.getClass().getSimpleName();
    
    private long[] syncSampleArray;
    private ArrayList<VideoSector> sectors;

    /**
     * @hide
     * @param origTrack
     * @param sectors
     */
    public MultiTrimTrack(Track origTrack, ArrayList<VideoSector> sectors) {
        this.origTrack = origTrack;
        this.sectors = sectors;
    }
    
    /**
     * @hide
     * @return
     */
    public List<SampleOffset> getSampleOffsets() {
    	final List<SampleOffset> ret = new ArrayList<SampleOffset>();
    	List<SampleOffset> sof = origTrack.getSampleOffsets();
    	
    	if (sof == null) {
    		final List<ByteBuffer> samples = origTrack.getSamples();        	
        	if (samples instanceof SampleList) {        		
        		sof =  ((SampleList)samples).getSampleOffsets();        		
        	}
    	}
    	
    	if (sof != null) {
	    	for (int i=0 ; i < sof.size(); i++) {
	    		for (VideoSector s : sectors) {
		       		 if (i >= s.startSample && i <= s.endSample) {
		       			ret.add(sof.get(i));
		       		 }  
	            }
	    	}
			Log.d(TAG ,"MultiTrimTrack getSampleOffsets sucess");
    	}
    	
    	return ret;
    }
    
    
    private SampleListAdapter mSampleListAdapter = null;
    private Object mSampleListAdapterLock = new Object();
    /**
     * @hide
     * @return
     */
    public List<ByteBuffer> getSamples() {
    	
    	synchronized (mSampleListAdapterLock) {
    		if (mSampleListAdapter == null) {
    			final List<ByteBuffer> samples = origTrack.getSamples();
    			if (samples instanceof SampleList) {
    				mSampleListAdapter = new SampleListAdapter((SampleList) samples, sectors);
    			}
        	}
    		
    		if (mSampleListAdapter != null) {
    			return mSampleListAdapter;
    		} else {
    			Log.e(TAG ,"cannot get SampleListAdapter , some this might fault!!!!!!");
    			return origTrack.getSamples();
    		}    		
    	}    	
    }
    
    /**
     * @hide
     * @return
     */
    public SampleDescriptionBox getSampleDescriptionBox() {
        return origTrack.getSampleDescriptionBox();
    }
    
    
    private List<TimeToSampleBox.Entry> mDecodingTimeEntries = null;
    private Object mDecodingTimeEntriesLock = new Object();
    /**
     * @hide
     * @return
     */
    public List<TimeToSampleBox.Entry> getDecodingTimeEntries() {
        
    	synchronized(mDecodingTimeEntriesLock) {
    		if ( mDecodingTimeEntries == null ) {
    			if (origTrack.getDecodingTimeEntries() != null && !origTrack.getDecodingTimeEntries().isEmpty()) {
    	            // todo optimize! too much long is allocated but then not used
    	            final long[] decodingTimes = TimeToSampleBox.blowupTimeToSamples(origTrack.getDecodingTimeEntries());
    	            
    	            final LinkedList<TimeToSampleBox.Entry> returnDecodingEntries = new LinkedList<TimeToSampleBox.Entry>();

    	            for (int i=0;i<decodingTimes.length;i++) {
    	            	long nuDecodingTime = decodingTimes[i];
    	            	for (VideoSector s : sectors) {
    	    	       		 if (i >= s.startSample && i <= s.endSample) {
    	    	       			if (returnDecodingEntries.isEmpty() || returnDecodingEntries.getLast().getDelta() != nuDecodingTime) {
    	    	                    TimeToSampleBox.Entry e = new TimeToSampleBox.Entry(1, nuDecodingTime);
    	    	                    returnDecodingEntries.add(e);
    	    	                } else {
    	    	                    TimeToSampleBox.Entry e = returnDecodingEntries.getLast();
    	    	                    e.setCount(e.getCount() + 1);
    	    	                }    	       		 
    	    	       		 }
    	            	}
    	            }
    	            mDecodingTimeEntries = returnDecodingEntries;
    	        } else {
    	        	return null;
    	        }
    		}
    		return mDecodingTimeEntries;
    	}
    }
    
    private List<CompositionTimeToSample.Entry> mCompositionTimeEntries = null;
    private Object mCompositionTimeEntriesLock = new Object();
    /**
     * @hide
     * @return
     */
    public List<CompositionTimeToSample.Entry> getCompositionTimeEntries() {
        
    	synchronized(mCompositionTimeEntriesLock) {
    		if (mCompositionTimeEntries == null) {
    			if (origTrack.getCompositionTimeEntries() != null && !origTrack.getCompositionTimeEntries().isEmpty()) {
    	            int[] compositionTime = CompositionTimeToSample.blowupCompositionTimes(origTrack.getCompositionTimeEntries());

    	            LinkedList<CompositionTimeToSample.Entry> returnCompositionTime = new LinkedList<CompositionTimeToSample.Entry>();

    	            for (int i=0;i<compositionTime.length;i++) {
    	            	int nuDecodingTime = compositionTime[i];
    	            	for (VideoSector s : sectors) {
    		   	       		 if (i >= s.startSample && i <= s.endSample) {
    				   	       		if (returnCompositionTime.isEmpty() || returnCompositionTime.getLast().getOffset() != nuDecodingTime) {
    				                    CompositionTimeToSample.Entry e = new CompositionTimeToSample.Entry(1, nuDecodingTime);
    				                    returnCompositionTime.add(e);
    				                } else {
    				                    CompositionTimeToSample.Entry e = returnCompositionTime.getLast();
    				                    e.setCount(e.getCount() + 1);
    				                }
    		   	       		 }
    	            	}
    	            }
    	            mCompositionTimeEntries = returnCompositionTime;
    	        } else {
    	            return null;
    	        }
    		}    		
    		return mCompositionTimeEntries;
    	}    	
    }
        

    /**
     *  find the start base of this sample should align
     *  @param sample : to find the start base for this sample 
     *  
     *  ex: 
     *  sync samples : 1,10,20,30,40,50,60,70,80,90,100
     *  after trim ,the following sync sample remain: 10, 40,50, 80,90
     * 
     *  expect sync sample table result: 1,11,21,31,41
     *  
     *  for sync sample 10 ,we should reset its base line by 10-(10-1) = 1
     *  for sync sample 40 ,we should reset its base line by 40-(10-1)-(40-20) = 11
     *  for sync sample 50 ,we should reset its base line by 50-(10-1)-(40-20) = 21
     *  for sync sample 80 ,we should reset its base line by 80-(10-1)-(40-20)-(80-60) = 31
     *  for sync sample 90 ,we should reset its base line by 90-(10-1)-(40-20)-(80-60) = 41
     *  
     *  note: 
     *  VideoSector sample index is start from 0
     *  origTrack.getSyncSamples() sample index is start from 1
     */
    private long findCroppedSampleBefore(long sample) {
    	long retSampleCnt = 0;
    	if (origTrack.getSyncSamples() != null && origTrack.getSyncSamples().length > 0) {
    		
    		Log.d(TAG , "debug, current checking sample: " + sample);
    		for (long l : origTrack.getSyncSamples()) {
    			if (l > sample) break;
    			 
    			final boolean isEndSample = isEndSample(l) && l <= sample;;
    			final boolean isFirstStart = isFirstStartSample(l);
    			
    			long nextStartSample = Long.MAX_VALUE;
    			if (isFirstStart) {
    				retSampleCnt = l - 1; // to match the base line
    				Log.d(TAG , "debug, find StartSample base : " + retSampleCnt );
    			} else if (isEndSample) {
    				for (VideoSector s : sectors) {
		   				 if ( s.startSample > l && s.startSample < nextStartSample) {
		   					nextStartSample = s.startSample;
		   					Log.d(TAG , "debug, nextStartSample : " + nextStartSample + " endSample:" + (l - 1));
		   				 }
    				}
    				retSampleCnt += nextStartSample - (l - 1); // (l-1) is to match record data structure
    			}
    		}
    	}
    		
    	return retSampleCnt;
    }
    
    private boolean isFirstStartSample(long sample) {
    	
    	boolean isStartSample = false;
    	for (VideoSector s : sectors) {
    		if ( (s.startSample + 1) == sample)
    			isStartSample = true;
    		
    		if ( (s.startSample + 1) < sample ) {
    			return false;
    		}
		}
    	
    	//Log.d(TAG, "debug, sameple:" + sample + " isFirstStartSample:"+ isStartSample);
    	return true && isStartSample;
    }
    
    private boolean isEndSample(long sample) {
    	for (VideoSector s : sectors) {
    		if ( (s.endSample + 1) == sample ) {
    			return true;
    		}
		}
    	return false;
    }
    
    /**
     * @hide
     * @return
     */
    synchronized public long[] getSyncSamples() {
        if (this.syncSampleArray == null) {
            if (origTrack.getSyncSamples() != null && origTrack.getSyncSamples().length > 0) {
                List<Long> syncSamples = new ArrayList<Long>();
                for (long l : origTrack.getSyncSamples()) {
                	//Log.d(TAG ,"sync sample:" + l);
                	for (VideoSector s : sectors) {
                		 if (l >= s.startSample && l < s.endSample) {
                             final long CroppedSampleBefore = findCroppedSampleBefore(l);
                			 syncSamples.add(l - CroppedSampleBefore);
                             break;
                         }
                    }
                }
                syncSampleArray = new long[syncSamples.size()];
                for (int i = 0; i < syncSampleArray.length; i++) {
                    syncSampleArray[i] = syncSamples.get(i);
                    
                    /*StringBuilder sb = new StringBuilder();
                    sb.append("write sync sample:");
                    sb.append(syncSampleArray[i]);
                    sb.append(" its size:" );
                    sb.append(origTrack.getSamples().get((int)syncSampleArray[i]-1).limit());
                    if( syncSampleArray[i] > 1) {
                    	sb.append(" pre size:" );
                        sb.append(origTrack.getSamples().get((int)syncSampleArray[i]-2).limit());
                    }
                    sb.append(" next size:" );
                    sb.append(origTrack.getSamples().get((int)syncSampleArray[i]).limit());
                    
                    Log.d(TAG , sb.toString());*/

                }
                return syncSampleArray;
            } else {
                return null;
            }
        } else {
            return this.syncSampleArray;
        }
    }
    
    private List<SampleDependencyTypeBox.Entry> mSampleDependencies = null;
    private Object mSampleDependenciesLock = new Object();
    /**
     * @hide
     */
    public List<SampleDependencyTypeBox.Entry> getSampleDependencies() {
        
    	synchronized (mSampleDependenciesLock) {
    		if (mSampleDependencies == null) {
    			if (origTrack.getSampleDependencies() != null && !origTrack.getSampleDependencies().isEmpty()) {
    	        	final List<SampleDependencyTypeBox.Entry> sampleDependencies = origTrack.getSampleDependencies();
    	        	final List<SampleDependencyTypeBox.Entry> ret = new LinkedList<SampleDependencyTypeBox.Entry>();
    	        	
    	        	for (int i=0 ; i < sampleDependencies.size(); i++) {
    	        		for (VideoSector s : sectors) {
    			       		 if (i >= s.startSample && i <= s.endSample) {
    			       			ret.add(sampleDependencies.get(i));
    			       		 }  
    	                }
    	        	}
    	        	mSampleDependencies = ret;//origTrack.getSampleDependencies().subList(fromSample, toSample);
    	        } else {
        			return null;
        		}
    		}
    		return mSampleDependencies;
    	}
    }
    
    /**
     * @hide
     * @return
     */
    public TrackMetaData getTrackMetaData() {
        return origTrack.getTrackMetaData();
    }
    
    /**
     * @hide
     * @return
     */
    public String getHandler() {
        return origTrack.getHandler();
    }
    
    /**
     * @hide
     * @return
     */
    public Box getMediaHeaderBox() {
        return origTrack.getMediaHeaderBox();
    }
    
    /**
     * @hide
     * @return
     */
    public SubSampleInformationBox getSubsampleInformationBox() {
        return origTrack.getSubsampleInformationBox();
    }

}