package com.dmatsanganis.speedometer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Objects;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ViolationInfo extends AppCompatActivity implements OnMapReadyCallback {

    Intent intent;

    TextView timestampTextView;
    TextView longitudeTextView;
    TextView latitudeTextView;
    TextView speedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_violation_info);

        // Enable back button on actionbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Get intents that passed from the selected violation
        intent = getIntent();

        // Load map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Set timestamp textView field
        timestampTextView = findViewById(R.id.violation_info_timestamp);
        timestampTextView.setText(intent.getStringExtra("timestamp"));

        // Set longitude textView field
        longitudeTextView = findViewById(R.id.violation_info_longitude);
        longitudeTextView.setText(String.valueOf(intent.getDoubleExtra("longitude", 0)));

        // Set latitude textView field
        latitudeTextView = findViewById(R.id.violation_info_latitude);
        latitudeTextView.setText(String.valueOf(intent.getDoubleExtra("latitude", 0)));

        // Set speed textView field
        speedTextView = findViewById(R.id.violation_info_speed_red);
        speedTextView.setText(intent.getStringExtra("speed") + " km/h");

    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Create Map's marker.
        LatLng marker = new LatLng(intent.getDoubleExtra("latitude", 0), intent.getDoubleExtra("longitude", 0));

        // Add marker on Map.
        map.addMarker(new MarkerOptions().position(marker));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 10));
    }

    // Add functionality to back arrow button.
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}