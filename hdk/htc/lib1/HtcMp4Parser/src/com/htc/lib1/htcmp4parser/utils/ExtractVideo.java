package com.htc.lib1.htcmp4parser.utils; 

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import android.os.Handler;
import android.os.Message;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoFile;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.Movie;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.builder.CompressZoeMp4Builder;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.container.mp4.MovieCreator;

/**
 * Extract MP4 Video from ZOE file
 *
 */
public class ExtractVideo {

	private static final String TAG = "ExtractVideo";
	
	/**
	 * The handler for progress notification.
	 */
	public static abstract class ProgressListener extends Handler {

		/**
		 * @deprecated [Not use any longer]
		 * We don't allow anyone to override this function any more.
		 */
		@Deprecated
		public void handleMessage(Message msg) {
			onProgress(msg.arg1);
		}

		/**
		 * Implement the onProgress function to update your UI.
		 * @param progress the percentage: 0 ~ 100
		 */
		public abstract void onProgress(int progress);
	}
	
	/**
	 * ERR_CODE for checking error message
	 */
	public static enum ERR_CODE {
		/**
		 * ERR_OK: no error
		 */
		ERR_OK,
		/**
		 * ERR_BAD_FORMAT: the file format can't be recognized.
		 */
		ERR_BAD_FORMAT,
		/**
		 * ERR_IOEXCEPTION: Storage was full.
		 */
		ERR_IOEXEPTION,
		/**
		 * ERR_UNKNOWN: undefined error.
		 */
		ERR_UNKNOWN
	}
	
	/**
	 * Extract the pure MP4 data from ZOE file. DO NOT invoke this function in UI thread.
	 * @param srcFD the descriptor of the source file.
	 * @param dstFD the descriptor of the destination file.
	 * @param listener the progress listener you would register for UI.
	 * @return ERR_CODE to check for the result.
	 * @throws InterruptedException you can cancel this sync. function by interrupt
	 */
	public static ERR_CODE extractPureMP4(
			final FileDescriptor srcFD, 
			final FileDescriptor dstFD, 
			final ProgressListener listener) throws InterruptedException {
		return processMP4(srcFD,dstFD,listener,new CompressZoeMp4Builder().setKeepZJPG(false));
	}
	
	
	/**
	 * Compress v2 ZOE , keeps only one image to reduce size . DO NOT invoke this function in UI thread.
	 * @param srcFD the descriptor of the source file.
	 * @param dstFD the descriptor of the destination file.
	 * @param listener the progress listener you would register for UI.
	 * @return ERR_CODE to check for the result.
	 * @throws InterruptedException you can cancel this sync. function by interrupt
	 */
	public static ERR_CODE compressZOE(
			final FileDescriptor srcFD, 
			final FileDescriptor dstFD, 
			final ProgressListener listener) throws InterruptedException {
		return processMP4(srcFD,dstFD,listener,new CompressZoeMp4Builder());
	}
	

	private static ERR_CODE processMP4(
			final FileDescriptor srcFD, 
			final FileDescriptor dstFD, 
			final ProgressListener listener,
			final DefaultMp4Builder builder) throws InterruptedException {
		//return code
		ERR_CODE ret = ERR_CODE.ERR_OK;
		
		final long tid = Thread.currentThread().getId();
		final long startTime = System.currentTimeMillis();
		
		FileInputStream fis = null;
		FileChannel in = null;
		
		FileOutputStream fos = null;
		FileChannel out = null;
		
		try {	
			// prepare input stream
			in = (fis = new FileInputStream(srcFD)).getChannel();
			
			// generate movie for source video
			final Movie movie = MovieCreator.build(in);
			
			// we check if source has "track" to evaluate if it was a MP4 file.
			if(0 == movie.getTracks().size()) {
				Log.d(TAG, "the size of track is 0, return ERR_CODE.ERR_BAD_FORMAT");
				ret = ERR_CODE.ERR_BAD_FORMAT;
				return ret;
			}

			// generate the structure used to create output clip.
			final IsoFile isoFile = builder.build(movie, fis.getFD());

			// calculate the size of destination file
			final long totalSize = isoFile.getSize();
			Log.d(TAG, "total size should be:" + totalSize);
			
			// prepare output stream
			out = (fos = new FileOutputStream(dstFD)).getChannel();
			
			//
			final WritableByteChannel wbc = out;
			final WritableByteChannel cfc = new WritableByteChannel() {
				private long accumulated = 0;
				private long previous = 0;
				private final int threshold = (int)((double)totalSize/100);		//we want to trigger onProgress each percent only.
				
				@Override
				public void close() throws IOException {
					wbc.close();
				}
				
				@Override
				public boolean isOpen() {
					return wbc.isOpen();
				}
				
				@Override
				public int write(final ByteBuffer buffer) throws IOException {
					final int result = wbc.write(buffer);
					accumulated += result;
					final long delta = accumulated - previous;
					if( (delta >= threshold) || (0 == delta) || accumulated == totalSize) {
						// update progress
						final int progress = (int)((((double)accumulated)/totalSize) * 100);
						Log.d(String.format("update progress:prev(%d) accu(%d) pct(%d)", previous , accumulated , progress));
						// set previous to accumulated
						previous = accumulated;
						
						if(null != listener) {
							final Message msg = listener.obtainMessage();
							msg.arg1 = progress;
							msg.sendToTarget();
						}
					}
					return result;
				}
			};
			
			// output the file
			isoFile.getBox(cfc);
		} catch(java.nio.channels.ClosedByInterruptException e) {
			// interrupt
			Log.d("ExtractVideo extractPureMP4 interrupted!");
			throw new InterruptedException(e.getMessage());
		} catch(final IOException e) {
			ret = ERR_CODE.ERR_IOEXEPTION;
			Log.d(TAG, "ExtractVideo extractPureMP4 io exception! ", e);
		} catch(final Exception e) {
			ret = ERR_CODE.ERR_UNKNOWN;
			Log.d(TAG, "ExtractVideo  extractPureMP4 unexpected exception!", e);
		} finally {
			close(fis);
			close(in);
			close(fos);
			close(out);
			Log.d("ExtractVideo extractPureMP4", "duration:" + (System.currentTimeMillis() - startTime) + " in tid:" + tid);
		}
		
		return ret;
	} // end of convertMP4
	
	/**
	 * @hide
	 * {@exthide}
	 */
	public static void close(java.io.Closeable c) {
		if(null != c) {
			try {
				c.close();
			} catch(final IOException e) {
				Log.d(TAG, "ExtractVideo close io exception! ", e);
			}
		}
	}
	
	/**
	 * @hide
	 * {@exthide}
	 */
	public static void close(java.nio.channels.Channel c) {
		if(null != c) {
			try {
				c.close();
			} catch(final IOException e) {
				Log.d(TAG, "ExtractVideo close nio exception! ", e);
			}
		}
	}
	
}
