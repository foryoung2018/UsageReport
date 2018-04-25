package com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.tracks;


/**
 * @hide
 * {@exthide}
 *
 */
public class VideoSector implements java.io.Serializable  {
	/**
	 * @hide
	 */
	private static final long serialVersionUID = 1L;
	/**
     * @hide
     */
	public long startTime = -1;
	/**
     * @hide
     */
	public long endTime = -1;
	/**
     * @hide
     */
	public double slowMotionScale = -1;
	/**
     * @hide
     */
	public long startSample = -1;
	/**
     * @hide
     */
	public long endSample = -1;
	/**
     * @hide
     */
	public VideoSector(long startTime, long endTime ,double scale) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.slowMotionScale = scale;
	}
	/**
     * @hide
     */
	public VideoSector(long startTime, long endTime) {
		this.startTime = startTime;
		this.endTime = endTime;		
	}
}