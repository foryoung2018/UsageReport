package com.htc.lib1.autotest.middleware;

/**
 * CSRAction is using to generate action string which could transform to motion event
 */
public class CSRAction {

    /**
     * Delimit String is using on seperate parms
     */
	public static final String PARAMETER_DELIMIT_STRING = " ";

	public enum ActionType {
		Tap, DoubleTap, LongPress, TwoFingerScroll, Pan, PinchSpread, Rotate, TwoHandRotate, Keyboard, Delay, /* Pen */
	}
	
	/**
	 * Generate Tap action String using by SmartRecorder.
	 * @param x Tap coordinate.
	 * @param y Tap coordinate.
	 * @return action String.
	 */
	public String genTapActionString(int x, int y) {

		return ActionType.Tap.toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(x).toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(y).toString();
	}

	/**
	 * Generate Double Tap action String using by SmartRecorder.
	 * @param x Tap coordinate.
	 * @param y Tap coordinate.
	 * @return action String.
	 */
	public String genDoubleTapActionString(int x, int y) {

		return ActionType.DoubleTap.toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(x).toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(y).toString();
	}
	
	/**
	 * Generate Long Press action String using by SmartRecorder.
	 * @param x Long Press coordinate.
	 * @param y Long Press coordinate.
	 * @param time Long Press time.
	 * @return action String.
	 */
	public String genLongPressActionString(int x, int y, int time) {

		return ActionType.LongPress.toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(x).toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(y).toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(time).toString();
	}
	
	/**
	 * Generate TwoFingerScroll action String using by SmartRecorder.
	 * @param sitck_x Stick finger coordinate.
	 * @param sitck_y Stick finger coordinate.
	 * @param from_x Scroll finger from coordinate.
	 * @param from_y Scroll finger from coordinate.
	 * @param to_x Scroll finger to coordinate.
	 * @param to_y Scroll finger to coordinate.
	 * @param time Scroll duration time.
	 * @return action String.
	 */
	public String genTwoFingerScrollActionString(int sitck_x, int sitck_y, int from_x, int from_y, int to_x, int to_y, int time) {

		return ActionType.TwoFingerScroll.toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(sitck_x).toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(sitck_y).toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(from_x).toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(from_y).toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(to_x).toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(to_y).toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(time).toString();
	}
	
	/**
	 * Generate TwoFingerScroll action String using by SmartRecorder.
	 * @param from_x Scroll finger from coordinate.
	 * @param from_y Scroll finger from coordinate.
	 * @param to_x Scroll finger to coordinate.
	 * @param to_y Scroll finger to coordinate.
	 * @param time Pan duration time.
	 * @param longPress_timeout Long press time before Pan.
	 * @return action String.
	 */
	public String genPanActionString(int from_x, int from_y, int to_x, int to_y, int time, int longPress_timeout) {

		return ActionType.Pan.toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(from_x).toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(from_y).toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(to_x).toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(to_y).toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(time).toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(longPress_timeout).toString();
	}

	/**
	 * Generate PinchSpread action String using by SmartRecorder.
	 * @param lfrom_x Left finger from coordinate.
	 * @param lfrom_y Left finger from coordinate.
	 * @param lto_x Left finger to coordinate.
	 * @param lto_y Left finger to coordinate.
	 * @param rfrom_x Right finger from coordinate.
	 * @param rfrom_y Right finger from coordinate.
	 * @param rto_x Right finger to coordinate.
	 * @param rto_y Right finger to coordinate.
	 * @param time Motion duration time.
	 * @return action String.
	 */
	public String genPinchSpreadString(int lfrom_x, int lfrom_y, int lto_x, int lto_y, int rfrom_x, int rfrom_y, int rto_x, int rto_y, int time) {

		return ActionType.PinchSpread.toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(lfrom_x).toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(lfrom_y).toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(lto_x).toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(lto_y).toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(rfrom_x).toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(rfrom_y).toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(rto_x).toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(rto_y).toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(time).toString();
	}
	
	/**
	 * Generate Rotate action String using by SmartRecorder.
	 * @param center_x Rotate origin point.
	 * @param center_y Rotate origin point.
	 * @param r Radius.
	 * @param start_angle Rotate start angle.
	 * @param rotate_angle Rotate angle. Minus value to perform CCW rotate.
	 * @return action String.
	 */
	public String genRotateActionString(int center_x, int center_y, int r,
			int start_angle, int rotate_angle) {

		return ActionType.Rotate.toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(center_x).toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(center_y).toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(r).toString()	+ PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(start_angle).toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(rotate_angle).toString();
	}
	
	/**
	 * Generate TwoHandRotate action String using by SmartRecorder.
	 * @param left_center_x Left rotate origin point.
	 * @param left_center_y Left rotate origin point.
	 * @param left_r Left radius.
	 * @param left_start_angle Left rotate start angle.
	 * @param left_rotate_angle Left rotate angle. Minus value to perform CCW rotate.
	 * @param right_center_x Right rotate origin point.
	 * @param right_center_y Right rotate origin point.
	 * @param right_r Right radius.
	 * @param right_start_angle Right rotate start angle.
	 * @param right_rotate_angle Right rotate angle. Minus value to perform CCW rotate.
	 * @return action String.
	 */
	public String genTwoHandRotateActionString(int left_center_x, int left_center_y, int left_r,
			int left_start_angle, int left_rotate_angle, int right_center_x, int right_center_y, int right_r,
			int right_start_angle, int right_rotate_angle) {

		return ActionType.Rotate.toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(left_center_x).toString()	+ PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(left_center_y).toString()	+ PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(left_r).toString()+ PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(left_start_angle).toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(left_rotate_angle).toString()	+ PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(right_center_x).toString()	+ PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(right_center_y).toString()	+ PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(right_r).toString()+ PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(right_start_angle).toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(right_rotate_angle).toString();
	}

	/**
	 * Generate keyboard action String using by SmartRecorder.
	 * @param key_code Hardware key code.
	 * @param timeout Press time.
	 * @return action String.
	 */
	public String genKeyboardActionString(int key_code, int timeout) {

		return ActionType.Keyboard.toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(key_code).toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(timeout).toString();
	}

	/**
	 * Generate delay action String using by SmartRecorder.
	 * @param timeout Delay time.
	 * @return action String.
	 */
	public String genDelayActionString(int timeout) {

		return ActionType.Delay.toString() + PARAMETER_DELIMIT_STRING
				+ Integer.valueOf(timeout).toString();
	}
}

