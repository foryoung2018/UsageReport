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
package com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.container.mp4;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.List;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoFile;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.Box;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.ContainerBox;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.MovieBox;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.TrackBox;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.Movie;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.Mp4TrackImpl;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.GeoDataBox;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.HtcBox;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.HtcSlowMotionBox;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.HtcBox.HTCMetaDataTable;
import com.htc.lib1.htcmp4parser.utils.Log;

/**
 * Shortcut to build a movie from an MP4 file.
 * @hide
 *  {@exthide}
 */
public class MovieCreator {
    /**
     * @hide
     * @param channel
     * @return
     * @throws IOException
     */
    public static Movie build(ReadableByteChannel channel) throws IOException {
        final IsoFile isoFile = new IsoFile(channel);
        final Movie movie = new Movie();
        final MovieBox movieBox = isoFile.getMovieBox();
        
        //add HTC or google specified boxes here...
        final Class[] classes = {HtcSlowMotionBox.class , GeoDataBox.class , HtcBox.class};
        for( Class cls : classes ){
        	final List<Box> list = isoFile.getBoxes(cls,true);
        	if(null == list || 0 == list.size()){
        		continue;
        	}
        	
        	final Box box  = list.get(0);
        	if( box instanceof HtcBox ){
        		movie.setTable(parseTable((HtcBox)box,channel));
        	}
        	movie.addBox(box);
        }
        
        List<TrackBox> trackBoxes = null;
        if (movieBox != null) {
        	trackBoxes = movieBox.getBoxes(TrackBox.class);
        } else {
        	Log.e(MovieCreator.class.getName(), "Get movieBox is null");
        }
        for (TrackBox trackBox : trackBoxes) {
            movie.addTrack(new Mp4TrackImpl(trackBox));
        }
        return movie;
    }
    
    
    private static final HTCMetaDataTable parseTable(final HtcBox box , ReadableByteChannel channel) throws IOException{
        	if(channel instanceof FileChannel){
        		final FileChannel ch = (FileChannel)channel;
        		System.out.println("offset:" + box.getOffset() + " size:" + box.getSize());
        		final ByteBuffer buffer = ch.map(FileChannel.MapMode.READ_ONLY, box.getOffset() , box.getTableSize());
        		//parse HTCMetaDataTable
        		if(null != buffer){
        			HtcBox.HTCMetaDataTable table = new HtcBox.HTCMetaDataTable();
        			table.parse(buffer);
        			return table;
        		}
        	}
        	return null;
    }
}
