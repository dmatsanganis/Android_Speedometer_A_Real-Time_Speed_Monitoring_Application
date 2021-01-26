package com.dmatsanganis.speedometer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dmatsanganis.speedometer.database.DatabaseConfiguration;
import com.dmatsanganis.speedometer.object.ViolationObject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LocationListener{

    //Initialize Global Variables.
    private SharedPreferences preferences;
    private MySpeechRecognizer speechRecognizer;
    private float speedLimit;
    // Button's transformation Boolean variable, initialize as true.
    private boolean isClicked = true;
    private boolean gps_enabled;
    private boolean speedViolation;
    private static final int REC_RESULT = 389;
    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    LocationManager locationManager;
    Intent intent;
    ActionBar actionBar;

    FloatingActionButton speechRecognitionButton;

    TextView speedTextView, limitTextView;
    Button violationsButton;
    Button mapButton;
    Button setlimitButton;
    Button startButton;
    EditText editText;
    DatabaseConfiguration dbHandler = new DatabaseConfiguration(this, null, null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        speedLimit = preferences.getFloat("speed_limit_value", 0);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Initialize as false, in order to
        // capture every (and the first) violation.
        speedViolation = false;

        speechRecognizer = new MySpeechRecognizer(this);
        speedTextView = findViewById(R.id.speedtxt);
        violationsButton = findViewById(R.id.violations_button);
        mapButton = findViewById(R.id.map_button);
        startButton = findViewById(R.id.startButton);
        speechRecognitionButton = findViewById(R.id.mic);
        setlimitButton = findViewById(R.id.setlimitButton);
        limitTextView = findViewById(R.id.limitTextView);

        // Lambda Expression Violations button listener.
        violationsButton.setOnClickListener((View view) -> {
            intent = new Intent(this, Violations.class);
            this.startActivity(intent);
        });

        // Lambda Expression Map button listener.
        mapButton.setOnClickListener((View view) -> {
            intent = new Intent(this, Map.class);
            this.startActivity(intent);
        });

        // Location's Permission check on create of the App.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            //Ask for Location's Permission.
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},234);
        }
        isLocationEnabled(this);


        // Lambda Expression for Enable button listener.
        startButton.setOnClickListener((View view) -> {

            // if Boolean variable isClicked is true.
            if(isClicked)
            {
                //Provide Location's updates.
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

                startButton.setText("Stop");

                isClicked = false;

                // Toast Message.
                Toast.makeText(this, "Speed capture is enabled", Toast.LENGTH_SHORT).show();
            }
            else{
                //Stop getting Location's updates.
                locationManager.removeUpdates(this);
                actionBar.setTitle("Speedometer");
                actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#14C5DC")));
                speedTextView.setText("--");

                //Background Color becomes white.
                getWindow().getDecorView().setBackgroundColor(Color.WHITE);

                startButton.setText("Start");

                isClicked=true;
                // Toast Message.
                Toast.makeText(this, "Speed capture is disabled", Toast.LENGTH_SHORT).show();
            }
        });

        // Lambda Expression Limit button listener.
        setlimitButton.setOnClickListener((view) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View view1= inflater.inflate(R.layout.setlimit_dialog,null);
            editText = view1.findViewById(R.id.editTextNumberDecimal1);
            editText.setText(String.valueOf(preferences.getFloat("speed_limit_value",0)));

            builder.setCancelable(true);
            builder.setTitle("Speed Limit")
                    .setMessage("Set your Speed Limit")
                    .setView(view1)
                    .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.cancel();
                        }
                    })
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            // Save speed limit as a Shared Preference.
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putFloat("speed_limit_value",Float.valueOf(String.valueOf(editText.getText())));
                            editor.apply();

                            // Speed Limit Update Toast Message.
                            Toast.makeText(getApplicationContext(),"The Speed Limit has been updated!",Toast.LENGTH_LONG).show();
                            // Set the new speed limit to textview.
                            limitTextView.setText("Current limit has been set at: "+String.valueOf(preferences.getFloat("speed_limit_value",0)));
                        }
                    })
                    .show();
        });

        // Lambda Expression FAB listener.
        speechRecognitionButton.setOnClickListener((view) -> {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            {
                // Mic's Permission Check.
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 878);
            }
            else {

                // If permission is already given continue, with the initialization
                //  of the Speech Recognizer function, initSpeechRecognizer().
                initSpeechRecognizer();
            }

        });

        // Shared Preferences stored Boolean variable, first_time, initialize as true.
        boolean first_time = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("first_time", true);

        //Checks if it is the first time executing the app in order to set the speed limit.
        if (first_time)
        {
            setlimitButton.performClick();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("first_time", false)
                    .apply();
        }

        // Set the speed limit to textview.
        limitTextView.setText("Current limit has been set at: "+String.valueOf(preferences.getFloat("speed_limit_value",0)));
    }

    // Create Setting Menu (3 dots) on Main Activity.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    // Settings Menu On-Click Event Handler.
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Switch statement regarding user's selection.
        switch (item.getItemId())
        {
            case R.id.help:
                intent = new Intent(this, Help.class);
                this.startActivity(intent);
                return true;
            case R.id.about:
                intent = new Intent(this, About.class);
                this.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (location != null)
        {
            //location.setSpeed(10);

            double currentSpeed = location.getSpeed() * 3.6;
            // gets current speed on each location change.
            // SpeedConverter is an object in order to get km/h

            String speed = decimalFormat.format(currentSpeed); //μετατροπή της ταχύτητας σε decimal με στρογγυλοποίηση στο 2ο δεκαδικό ψηφίο
            speedTextView.setText(speed);

            // Private float variable gets the user's Shared Preference's imported value.
            speedLimit = preferences.getFloat("speed_limit_value", 0);

            // No Violation.
            // There is no violation if current speed is lower or equal to speed limit (the SP).
            if (currentSpeed <= speedLimit && !isClicked)
            {
                speedViolation = false;

                if (currentSpeed > 0)
                {
                    // When currentSpeed is greater than 0,
                    // the text will become green.
                    actionBar.setTitle("Speedometer");
                    actionBar.setBackgroundDrawable(new ColorDrawable(Color.GREEN));
                    getWindow().getDecorView().setBackgroundColor(Color.WHITE);

                }
            }

            // Violation.
            // If there is a violation, create a new database record and inform the user.
            else if (currentSpeed > speedLimit && !isClicked)
            {
                getWindow().getDecorView().setBackgroundColor(Color.RED); //background red

                if (!speedViolation)
                {
                    speedViolation = true;

                    // Create db record
                    ViolationObject violationObject = new ViolationObject(location.getLongitude(), location.getLatitude(), currentSpeed, new Timestamp(System.currentTimeMillis()));

                    // Save the db record
                    dbHandler.addViolation(violationObject);

                    speechRecognizer.speak("Caution! You have exceeded the speed limit.");
                    actionBar.setTitle("Caution! You have exceeded the limit!");
                    actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));

                    Toast.makeText(this, violationObject.toString(), Toast.LENGTH_LONG).show();
                }
            }
            else {
                speedTextView.setTextColor(Color.DKGRAY);
            }
        }
        else {
            //if location == null.
            speedTextView.setText("--");
        }
    }

    // Check activity results for Speech Recognition and Mic's Events.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REC_RESULT && resultCode == RESULT_OK)
        {
            // Results' Array.
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if ((matches.contains("start") || matches.contains("εκκίνηση")) && isClicked)
            {
                startButton.performClick();
            }
            else if ((matches.contains("stop") || matches.contains("pause") || matches.contains("παύση") || matches.contains("σταμάτα")) && (!isClicked))
            {
                // When rec's result matches the
                startButton.performClick();
            }
            else if (matches.contains("violations") || matches.contains("παραβιάσεις"))
            {
                violationsButton.performClick();
            }
            else if (matches.contains("limit") || matches.contains("όριο"))
            {
                setlimitButton.performClick();
            }
            else if (matches.contains("map") || matches.contains("χάρτης"))
            {
                mapButton.performClick();
            }
            else{
                // Toast Message.
                Toast.makeText(this, "Available commands are 'start', 'stop', 'violations', 'limit', 'map'", Toast.LENGTH_LONG).show();
                // Assistant's Voice Help.
                speechRecognizer.speak("Available commands are 'start', 'stop', 'violations', 'limit', 'map'!");
            }
        }
        else {
            // Toast Message.
            Toast.makeText(this, "Available commands are 'start', 'stop', 'violations', 'limit', 'map'", Toast.LENGTH_LONG).show();
            // Assistant's Voice Help.
            speechRecognizer.speak("Available commands are 'start', 'stop', 'violations', 'limit', 'map'!");
        }
    }

    // Speech Recognizer Void.
    public void initSpeechRecognizer()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please say something!");
        startActivityForResult(intent,REC_RESULT);
    }

    // Checks if location service is enabled by the user.
    public void isLocationEnabled(Context context)
    {
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        catch(Exception ex) {}

        if(!gps_enabled) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Locations is not Enabled")
                    .setMessage("Please go to Settings and Εnable your location.")
                    .setPositiveButton("Enable Location", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.cancel();
                        }
                    })
                    .show();
        }
    }

    // Ask to enable location if it is disabled.
    @Override
    public void onResume(){
        super.onResume();
        isLocationEnabled(this);
    }

    /*
    // Further Improvement.
    private void strobeStart() {
        ObjectAnimator anim = ObjectAnimator.ofInt(speedTextView, "backgroundColor", Color.WHITE, Color.RED, Color.WHITE);
        anim.setDuration(1500);
        anim.setEvaluator(new ArgbEvaluator());
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        anim.start();
    }
    */

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}