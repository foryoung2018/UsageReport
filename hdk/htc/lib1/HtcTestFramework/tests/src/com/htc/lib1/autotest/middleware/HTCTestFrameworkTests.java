package com.htc.lib1.autotest.middleware;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import android.os.Handler;

public class HTCTestFrameworkTests {
	CSRAction mAction = new CSRAction();
	CSRController mController = new MyController(null, null); 
	CSREvent mEvent = new CSREvent("testEvent");
	ISRCallback mCallback = new MyCallback();
	
	@Test
	public void testGenTapActionString() {
		assertEquals("genTapActionString returns wrong string", "Tap -10 0", mAction.genTapActionString(-10, 0));
	}
	
	@Test
	public void testGenDoubleTapActionString() {
		assertEquals("genDoubleTapActionString returns wrong string", "DoubleTap -10 0", mAction.genDoubleTapActionString(-10, 0));
	}
	
	@Test
	public void testGenLongPressActionString() {
		assertEquals("genLongPressActionString returns wrong string", "LongPress -10 0 0", mAction.genLongPressActionString(-10, 0, 0));
	}
	
	@Test
	public void testGenTwoFingerScrollActionString() {
		assertEquals("genTwoFingerScrollActionString returns wrong string", "TwoFingerScroll -10 0 -10 0 -10 0 0", mAction.genTwoFingerScrollActionString(-10, 0, -10, 0, -10, 0, 0));
	}
	
	@Test
	public void testGenPanActionString() {
		assertEquals("genPanActionString returns wrong string", "Pan -10 0 -10 0 0 0", mAction.genPanActionString(-10, 0, -10, 0, 0, 0));
	}
	
	@Test
	public void testGenPinchSpreadString() {
		assertEquals("genPinchSpreadString returns wrong string", "PinchSpread -10 0 -10 0 -10 0 -10 0 0", mAction.genPinchSpreadString(-10, 0, -10, 0, -10, 0, -10, 0, 0));
	}
	
	@Test
	public void testGenRotateActionString() {
		assertEquals("genRotateActionString returns wrong string", "Rotate -10 0 0 0 0", mAction.genRotateActionString(-10, 0, 0, 0, 0));
	}
	
	@Test
	public void testGenTwoHandRotateActionString() {
		assertEquals("genTwoHandRotateActionString returns wrong string", "Rotate -10 0 0 0 0 -10 0 0 0 0", mAction.genTwoHandRotateActionString(-10, 0, 0, 0, 0, -10, 0, 0, 0, 0));
	}
	
	@Test
	public void testGenKeyboardActionString() {
		assertEquals("genKeyboardActionString returns wrong string", "Keyboard -10 0", mAction.genKeyboardActionString(-10, 0));
	}
	
	@Test
	public void testGenDelayActionString() {
		assertEquals("genDelayActionString returns wrong string", "Delay 0", mAction.genDelayActionString(0));
	}
	
	@Test
	public void testRecordEvent() {
		try {
			mController.setCallback(mCallback);
			mController.recordEvent(mEvent);
		} catch(Exception e) {
			fail("recordEvent fail: " + e.getMessage());
		}
	}
	
	class MyController extends CSRController {

		public MyController(Object controlTarget, Handler h) {
			super(controlTarget, h);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Handler getHandler() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void injectEvent(CSREvent event) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public ISRSpy getSpy() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String[] genActionString(CSREvent event) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	class MyCallback implements ISRCallback {

		@Override
		public void record(CSREvent event) {
			// TODO Auto-generated method stub
			
		}
	}
}
