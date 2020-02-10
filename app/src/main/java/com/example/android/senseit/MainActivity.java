package com.example.android.senseit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    public static final int RECORD_AUDIO = 0;
    private SensorManager sensormanager;
    public static DecimalFormat decimalFormatter;
    private TextView reading;

    ToneGenerator toneGen1;
    private int countState = 0;
    float prevx = 0, prevy = 0, prevz = 0, nowx = 0, nowy = 0, nowz = 0;
    private int firstobservation = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormatter = new DecimalFormat("#.000", decimalFormatSymbols);
        sensormanager = (SensorManager) getSystemService(SENSOR_SERVICE);

        StringBuilder data=new StringBuilder();
        String heading="X,Y,Z"+"\n";
data.append(heading);
        try{
            //saving the file into device
            FileOutputStream out = openFileOutput("data.csv", Context.MODE_PRIVATE);
            out.write((data.toString()).getBytes());
            out.close();

            Context context = getApplicationContext();
            File filelocation = new File(getFilesDir(), "data.csv");
            Uri path = FileProvider.getUriForFile(context, "com.example.android.senseit.fileprovider", filelocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(Intent.createChooser(fileIntent, "Send mail"));

Log.i("TRY","TRY");

        }
        catch(Exception e){
            e.printStackTrace();
        }





        data.append(heading);
        reading = findViewById(R.id.label_result);
        reading.setVisibility(View.GONE);
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Welcome", Snackbar.LENGTH_LONG);
        snackbar.show();
        //ask for permission
        final Button soundMeasure = findViewById(R.id.button_sound);
        soundMeasure.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {


                reading.setVisibility(View.VISIBLE);
                if (countState % 2 == 0) {
                    soundMeasure.setText("Stop");
                    soundMeasure.setBackgroundColor(Color.parseColor("#d50000"));
                    onResume();
                } else {
                    soundMeasure.setText("Sense");
                    soundMeasure.setBackgroundColor(Color.parseColor("#1b5e20"));





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
        sensormanager.registerListener(this, sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensormanager.SENSOR_DELAY_NORMAL);
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
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float accx, accy, accz;
            accx = event.values[0];
            accy = event.values[1];
            accz = event.values[2];
            lowpass(event);
            int magnitude = (int) Math.sqrt((accx * accx) + (accy * accy) + (accz * accz));
            reading.setText("" + magnitude + " m/s2");
            if (magnitude > 10) {
                toneGen1.startTone(ToneGenerator.TONE_CDMA_ANSWER, (int) magnitude);
            } else if (magnitude < 8) {
                toneGen1.startTone(ToneGenerator.TONE_CDMA_DIAL_TONE_LITE, (int) magnitude);
            } else {
                //  toneGen1.startTone(ToneGenerator.TONE_SUP_RINGTONE, (int) magnitude);
            }
        }
    }
    StringBuilder data=new StringBuilder();
    StringBuilder datap=new StringBuilder();
    private void lowpass(SensorEvent event) {

        if (firstobservation == 0) {
            nowx = event.values[0];
            nowy = event.values[1];
            nowz = event.values[2];
             prevx=0;
             prevy=0;
             prevz=0;
            firstobservation = 1;
        } else {
            nowx = event.values[0];
            nowy = event.values[1];
            nowz = event.values[2];
            float lowpassfactor = (float) 0.25; //no filtering occuring if alpha isequal to 0 or 1

            String observation=nowx+","+nowy+","+nowz+"\n";
             data.append(observation);




            //low pass implementation
            Log.i("lowpass", "values before passing through filter " + nowx + " " + nowy+ " " + nowz);
            nowx = nowx + lowpassfactor * (prevx - nowx);
            nowy = nowy + lowpassfactor * (prevy - nowy);
            nowz = nowz + lowpassfactor * (prevz - nowz);
            String observationprev=nowx+","+nowy+","+nowz+"\n";
            datap.append(observationprev);
        }
        Log.i("lowpass", "values after passing through filter " + nowx + " " + nowy+ " " + nowz);
         prevx=nowx;
        prevy=nowy;
      prevz=nowz;




    }




    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
