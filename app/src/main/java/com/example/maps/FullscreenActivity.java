package com.example.maps;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;

import com.example.maps.databinding.ActivityFullscreenBinding;

public class FullscreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
    }

    public void singIn(View v){
        FullscreenActivity createPackageContext;
        Intent i = new Intent(createPackageContext = FullscreenActivity.this, MainActivity.class);
        startActivity(i);
    }

    public void singUp(View v){
        FullscreenActivity createPackageContext;
        //Intent j = new Intent(createPackageContext = FullscreenActivity.this, RegisterActivity.class);
        Intent j = new Intent(createPackageContext = FullscreenActivity.this, FirebaseActivity.class);
        startActivity(j);
    }
}