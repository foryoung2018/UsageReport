
package com.htc.lib1.cc.graphic.test;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import com.htc.lib1.cc.test.R;

import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.util.ScreenShotUtil;

import com.htc.lib1.cc.graphic.activityhelper.GraphicMockActivity;
import com.htc.lib1.cc.graphic.ThermometerDrawable;

public class ThermometerDrawableTest extends HtcActivityTestCaseBase {

    public ThermometerDrawableTest() {
        super(GraphicMockActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityIntent(null);
        initActivity();
        mSolo.setActivityOrientation(getOrientation());
    }

    public final void testCurrent_sign() {
        initDrawableAndAssert(1, ThermometerDrawable.UNIT_TYPE_SIGN, R.style.weather_01, R.style.weather_02);
    }
    public final void testCurrent_celsius() {
        initDrawableAndAssert(23, ThermometerDrawable.UNIT_TYPE_CELSIUS, R.style.weather_01, R.style.weather_02);
    }
    public final void testCurrent_fahrenheit() {
        initDrawableAndAssert(456, ThermometerDrawable.UNIT_TYPE_FAHRENHEIT, R.style.weather_01, R.style.weather_02);
    }
    public final void testHigh_sign() {
        initDrawableAndAssert(7, ThermometerDrawable.UNIT_TYPE_SIGN, R.style.weather_05, R.style.weather_06);
    }
    public final void testHigh_celsius() {
        initDrawableAndAssert(89, ThermometerDrawable.UNIT_TYPE_CELSIUS, R.style.weather_05, R.style.weather_06);
    }
    public final void testHigh_fahrenheit() {
        initDrawableAndAssert(101, ThermometerDrawable.UNIT_TYPE_FAHRENHEIT, R.style.weather_05, R.style.weather_06);
    }
    public final void testLow_sign() {
        initDrawableAndAssert(987, ThermometerDrawable.UNIT_TYPE_SIGN, R.style.weather_07, R.style.weather_08);
    }
    public final void testLow_celsius() {
        initDrawableAndAssert(65, ThermometerDrawable.UNIT_TYPE_CELSIUS, R.style.weather_07, R.style.weather_08);
    }
    public final void testLow_fahrenheit() {
        initDrawableAndAssert(4, ThermometerDrawable.UNIT_TYPE_FAHRENHEIT, R.style.weather_07, R.style.weather_08);
    }
    public final void testSetBounds() {
        initDrawableAndAssert(23, ThermometerDrawable.UNIT_TYPE_CELSIUS, R.style.weather_01, R.style.weather_02, true);
    }

    private void initDrawableAndAssert(int numValue, int thermUnit, int numStyle, int thermStyle) {
        initDrawableAndAssert(numValue, thermUnit, numStyle, thermStyle, false);
    }

    private void initDrawableAndAssert(int numValue, int thermUnit, int numStyle, int thermStyle, boolean boundsShift) {
        ThermometerDrawable thermometerDrawable = new ThermometerDrawable();
        thermometerDrawable.setThermNumber(numValue);
        thermometerDrawable.setThermUnit(thermUnit);
        thermometerDrawable.setTextStyle(mActivity, ThermometerDrawable.TYPE_NUMBER, numStyle);
        thermometerDrawable.setTextStyle(mActivity, ThermometerDrawable.TYPE_THERM, thermStyle);

        final int w = thermometerDrawable.getIntrinsicWidth();
        final int h = thermometerDrawable.getIntrinsicHeight();

        if (boundsShift) {
            thermometerDrawable.setBounds((int) (w * 0.5), (int) (h * 0.5), (int) (w * 1.5), (int) (h * 1.5));
        } else {
            thermometerDrawable.setBounds(0, 0, w, h);
        }
        assertScreenShot(thermometerDrawable);
    }
    private void assertScreenShot(Drawable drawable) {
        final Rect bounds = drawable.getBounds();
        assertFalse(bounds.isEmpty());

        Bitmap bitmap = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);

        ScreenShotUtil.AssertViewEqualBefore(mSolo, bitmap, this);

        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }

    }
}
