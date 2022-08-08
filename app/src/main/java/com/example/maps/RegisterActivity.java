package com.example.maps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.json.JSONException;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void register(View v) throws JSONException {
        RegisterActivity createPackageContext;
        Intent i = new Intent(createPackageContext = RegisterActivity.this, MainActivity.class);
        startActivity(i);
    }
}