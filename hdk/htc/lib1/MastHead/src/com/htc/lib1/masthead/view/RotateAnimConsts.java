package com.htc.lib1.masthead.view;

/**
 * Rotate animation constants.
 * @hide
 */
public class RotateAnimConsts {
	public static final float ANIMATION_RATE = 1.0f;

	public static final long ROTATE_OFFSET = (long) (33 * ANIMATION_RATE);
	public static final float ROTATE_BEZIER = 0.8f;
	// slide
	public static final long SLIDE_DURATION = (long) (660 * ANIMATION_RATE);
	public static final long SLIDE_DELAY = (long) (297 * ANIMATION_RATE);

	public static final float SLIDE_BEZIER = 0.8f;
	// animation list
	// Outgoing Digit (6) Top Half: animate out
	public static final long UP_FRONT_ROTATION_DURATION = (long) (1287 * ANIMATION_RATE);
	public static final long UP_FRONT_OPACITY_DURATION = (long) (150 * ANIMATION_RATE);
	public static final long UP_FRONT_OPACITY_DELAY = (long) (500 * ANIMATION_RATE);
	// Incoming Digit (7) Top Half: animate in
	public static final long UP_REAR_ROTATION_DURATION = (long) (1287 * ANIMATION_RATE);
	public static final long UP_REAR_OPACITY_DURATION = (long) (150 * ANIMATION_RATE);
	public static final long UP_REAR_OPACITY_DELAY = (long) (561 * ANIMATION_RATE);
	// Outgoing Digit (6) Bottom Half: animate out
	public static final long DOWN_FRONT_ROTATION_DURATION = (long) (1800 * ANIMATION_RATE);
	public static final long DOWN_FRONT_OPACITY_DURATION = (long) (200 * ANIMATION_RATE);
	public static final long DOWN_FRONT_OPACITY_DELAY = (long) (500 * ANIMATION_RATE);
	// Incoming Digit (7) Bottom Half: animate in
	public static final long DOWN_REAR_ROTATION_DURATION = (long) (1287 * ANIMATION_RATE);
	public static final long DOWN_REAR_OPACITY_DURATION = (long) (150 * ANIMATION_RATE);
	public static final long DOWN_REAR_OPACITY_DELAY = (long) (561 * ANIMATION_RATE);
}
