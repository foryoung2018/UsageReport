
package com.htc.lib1.cc.graphic.activityhelper;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import com.htc.lib1.cc.test.R;

import com.htc.lib1.cc.graphic.ThermometerDrawable;

public class ThermometerDrawableDemoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawable_aut);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        ThermometerDrawable thermometerDrawable = new ThermometerDrawable();
        thermometerDrawable.setThermUnit(ThermometerDrawable.UNIT_TYPE_SIGN);
        thermometerDrawable.setTextStyle(this, ThermometerDrawable.TYPE_NUMBER, R.style.weather_01);
        thermometerDrawable.setTextStyle(this, ThermometerDrawable.TYPE_THERM, R.style.weather_02);
        thermometerDrawable.setThermNumber(-3);

        ImageView imageView = (ImageView) findViewById(R.id.image1);
        imageView.setImageDrawable(thermometerDrawable);

        ThermometerDrawable thermometerDrawable2 = new ThermometerDrawable();
        thermometerDrawable2.setThermNumber(200);
        thermometerDrawable2.setThermUnit(ThermometerDrawable.UNIT_TYPE_FAHRENHEIT);
        thermometerDrawable2.setTextStyle(this, ThermometerDrawable.TYPE_NUMBER, R.style.weather_07);
        thermometerDrawable2.setTextStyle(this, ThermometerDrawable.TYPE_THERM, R.style.weather_08);

        ImageView imageView2 = (ImageView) findViewById(R.id.image2);
        imageView2.setImageDrawable(thermometerDrawable2);

        ThermometerDrawable thermometerDrawable3 = new ThermometerDrawable();
        thermometerDrawable3.setThermNumber(95);
        thermometerDrawable3.setThermUnit(ThermometerDrawable.UNIT_TYPE_CELSIUS);
        thermometerDrawable3.setTextStyle(this, ThermometerDrawable.TYPE_NUMBER, R.style.weather_05);
        thermometerDrawable3.setTextStyle(this, ThermometerDrawable.TYPE_THERM, R.style.weather_06);

        ImageView imageView3 = (ImageView) findViewById(R.id.image3);
        imageView3.setImageDrawable(thermometerDrawable3);

    }
}
