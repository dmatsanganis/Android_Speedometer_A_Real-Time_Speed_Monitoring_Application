package com.dmatsanganis.speedometer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Objects;

import com.dmatsanganis.speedometer.adapters.MapAdapter;
import com.dmatsanganis.speedometer.object.ViolationObject;
import com.dmatsanganis.speedometer.database.DatabaseConfiguration;

public class Map extends AppCompatActivity implements OnMapReadyCallback {

    private  GoogleMap mMap;
    private MySpeechRecognizer speechRecognizer;
    List<ViolationObject> items;
    DatabaseConfiguration dbHandler = new DatabaseConfiguration(this, null, null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialize Text to Speech variables, speechRecognizer.
        speechRecognizer = new MySpeechRecognizer(this);

        // Get all violations from Database.
        items = dbHandler.getViolations();

        if (items != null)
        {
        // Obtain the SupportMapFragment and get notified when the Map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapfragment);
        mapFragment.getMapAsync(this);
        }
        else{
            // No Violation Toast appears.
            Toast.makeText(getApplicationContext(),"Congrats! You have not commit any violation yet!",Toast.LENGTH_LONG).show();
            // Assistant's Voice Help.
            speechRecognizer.speak("Congrats! You have not commit any violation yet!");
        }
        // Display back arrow button on actionbar.
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // Set custom marker info view.
        mMap.setInfoWindowAdapter(new MapAdapter(Map.this));

        // Add markers via Lambda Expression.
        items.forEach(item -> {
            LatLng marker = new LatLng(item.getLatitude(), item.getLongitude());

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(marker)
                    .snippet("Date: " + item.getTimestamp() + "\nLongitude: " + item.getLongitude() + "\nLatitude: " + item.getLatitude() + "\nSpeed: " + item.getSpeed() + " km/h");
            mMap.addMarker(markerOptions);

            // Move screen to the latest marker.
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(items.get(0).getLatitude(), items.get(0).getLongitude()), 5));
        });
    }

    // Add functionality to back arrow button.
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}