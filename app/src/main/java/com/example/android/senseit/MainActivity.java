package com.example.android.senseit;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    public static final int RECORD_AUDIO = 0;
    private SensorManager sensormanager;
    public static DecimalFormat decimalFormatter;
    private TextView reading;
    MediaPlayer mediaPlayer;
    ToneGenerator toneGen1;
    private int countState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Show welcome screen
        //mediaPlayer = MediaPlayer.create(this, R.raw.sleep_away);
        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 80);
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormatter = new DecimalFormat("#.000", decimalFormatSymbols);
        sensormanager = (SensorManager) getSystemService(SENSOR_SERVICE);

        reading = findViewById(R.id.label_result);
        reading.setVisibility(View.GONE);
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Welcome", Snackbar.LENGTH_LONG);
        snackbar.show();
        //ask for permission
        final Button soundMeasure = findViewById(R.id.button_sound);
        soundMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                reading.setVisibility(View.VISIBLE);
                if (countState % 2 == 0) {
                    soundMeasure.setText("Stop");

                    onResume();
                } else {
                    soundMeasure.setText("Sense");

                    onPause();
                }
                countState = countState + 1;

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        //   mediaPlayer.start();
        sensormanager.registerListener(this, sensormanager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), sensormanager.SENSOR_DELAY_NORMAL);
        //sensormanager.registerListener(this, sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensormanager.SENSOR_DELAY_GAME);

    }

    @Override
    protected void onPause() {
        super.onPause();
        //   mediaPlayer.pause();
        sensormanager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float magx, magy, magz;
            magx = event.values[0];
            magy = event.values[1];
            magz = event.values[2];

            int magnitude = (int) Math.sqrt((magx * magx) + (magy * magy) + (magz * magz));
            reading.setText("" + magnitude+ " uT");
            if (magnitude > 100) {
                toneGen1.startTone(ToneGenerator.TONE_CDMA_ANSWER, (int) magnitude);
            } else if(magnitude >50) {
                toneGen1.startTone(ToneGenerator.TONE_CDMA_DIAL_TONE_LITE, (int) magnitude);
            }
            else {
                toneGen1.startTone(ToneGenerator.TONE_SUP_RINGTONE, (int) magnitude);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
