package com.example.tripplanner.Home.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.example.tripplanner.R;
import com.example.tripplanner.TripData.Final;

public class MainSplash extends AppCompatActivity {

    private static final int SPLASH_SCREEN_TIME_OUT = Final.SPLASH_SCREEN_TIME_OUT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainSplash.this, Home_Activity.class);
            //Intent is used to switch from one activity to another.
            startActivity(intent);
            finish();
        }, SPLASH_SCREEN_TIME_OUT);
    }
}