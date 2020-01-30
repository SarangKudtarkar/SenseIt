package com.example.android.senseit;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public static final int  RECORD_AUDIO=0;
private SensorManager sensormanager;
public static DecimalFormat decimalFormatter;
    private TextView reading;
    private int countState=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Show welcome screen

        DecimalFormatSymbols decimalFormatSymbols=new DecimalFormatSymbols(Locale.US);
        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormatter=new DecimalFormat("#.000",decimalFormatSymbols);
        sensormanager = (SensorManager) getSystemService(SENSOR_SERVICE);
        reading=findViewById(R.id.label_result);
        reading.setVisibility(View.GONE);
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Welcome", Snackbar.LENGTH_LONG);
        snackbar.show();
        //ask for permission
        final Button soundMeasure=findViewById(R.id.button_sound);
        soundMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    reading.setVisibility(View.VISIBLE);
                    if(countState%2==0) {
                        soundMeasure.setText("Stop");
                    }
                else {
                        soundMeasure.setText("Sense");
                    }
                countState=countState+1;

            }
        });





    }

    @Override
    protected void onResume() {
        super.onResume();
        sensormanager.registerListener(this,sensormanager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),sensormanager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensormanager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD)
{
    float magx,magy,magz;
    magx=event.values[0];
    magy=event.values[1];
    magz=event.values[2];

    double magnitude=Math.sqrt((magx*magx)+(magy*magy)+(magz*magz));
reading.setText(""+magnitude);
}
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
