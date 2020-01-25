package com.example.android.senseit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Activity : ","created");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Activity : ","started");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Activity : ","stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Activity : ","destroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Activity : ","paused");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Activity : ","resumed");
    }
}
