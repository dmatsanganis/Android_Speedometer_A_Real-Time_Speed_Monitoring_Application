package com.dmatsanganis.speedometer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.Objects;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle("About");

        // Display back arrow button on actionbar.
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    // Add functionality to back arrow button.
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}