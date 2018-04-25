package com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.tracks;

import java.nio.ByteBuffer;
import java.util.AbstractList;
import java.util.ArrayList;

import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.mdat.SampleList;
import com.htc.lib1.htcmp4parser.utils.Log;

/**
 * 
 * @hide
 * {@exthide}
 */
public class SampleListAdapter extends AbstractList<ByteBuffer> {
	/**
	 * @hide
	 */
	private SampleList mSampleList = null;
	/**
	 * @hide
	 */
	private ArrayList<Integer> filtList = new ArrayList<Integer>();
	/**
	 * @hide
	 */
	private ArrayList<VideoSector> sectors;
	/**
	 * @hide
	 */
	private final String TAG = "SampleListAdapter";
	
	/**
	 * @hide
	 * @param sampleList
	 * @param videoSector
	 */
	public SampleListAdapter(SampleList sampleList, ArrayList<VideoSector> videoSector) {
		super();
		mSampleList = sampleList;
		sectors = videoSector;
		
		if (sectors != null && sectors.size() > 0) {
			
			// init need mapping table
			final int origSampleListSize = mSampleList.size(); 
			final boolean mappingSamples[] = new boolean[origSampleListSize];
			
			for (int i=0; i<origSampleListSize; i++ ) {
				for (VideoSector s : sectors) {
		       		 if (i >= s.startSample && i <= s.endSample) {
		       			mappingSamples[i] = true;
		       		 }	       			 
	            }
			}			
			
			// constr mapping filtList
			for (int i=0; i<origSampleListSize; i++) {

				// skip not map 
				if (mappingSamples[i] != true) {
					continue;
				}
				
				filtList.add(i);
			}
		}
		
	}
	
	/**
	 * @hide
	 */
	@Override
	public ByteBuffer get(int index) {
		if (mSampleList == null) {
			return null;
		}
		
		if (sectors == null) {
			return mSampleList.get(index);
		}
		
		final int filtIndex = filtList.get(index);
		//Log.d(TAG ,"orig index:" + index + " mapping index: " + filtIndex);
		
		return mSampleList.get(filtIndex);
	}
	
	/**
	 * @hide
	 */
	@Override
	public int size() {
		if (mSampleList == null) {
			return 0;
		}
		
		if (sectors == null) {
			return mSampleList.size();
		}
		
		return filtList.size();
	}

	
}