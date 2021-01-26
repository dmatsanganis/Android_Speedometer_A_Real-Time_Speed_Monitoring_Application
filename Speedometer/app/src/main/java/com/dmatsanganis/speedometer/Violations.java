package com.dmatsanganis.speedometer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dmatsanganis.speedometer.object.ViolationObject;
import com.dmatsanganis.speedometer.adapters.ViolationAdapter;
import com.dmatsanganis.speedometer.database.DatabaseConfiguration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class Violations extends AppCompatActivity {

    List<ViolationObject> items;

    DatabaseConfiguration dbHandler;

    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;

    LinearLayout filterPanel;
    FloatingActionButton filterFab;
    RadioGroup radioGroup;
    RadioButton radioButton;
    Button resetButton;
    Button okButton;
    Button dateFromButton;
    TextView dateFromTextView;
    Button dateToButton;
    TextView dateToTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_violations);

        // Display back arrow button on actionbar.
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Create database handler.
        dbHandler = new DatabaseConfiguration(this, null, null, 1);

        // Initialize components.
        filterPanel = findViewById(R.id.filter_panel);
        filterFab = findViewById(R.id.filter_fab);
        radioGroup = findViewById(R.id.filter_radio_group);
        resetButton = findViewById(R.id.filter_reset_button);
        okButton = findViewById(R.id.filter_search_button);
        dateFromButton = findViewById(R.id.date_from_button);
        dateFromTextView = findViewById(R.id.date_from_textview);
        dateToButton = findViewById(R.id.date_to_button);
        dateToTextView = findViewById(R.id.date_to_textview);


        // Get all violations from database.
        items = dbHandler.getViolations();

        recyclerView = findViewById(R.id.violation_list);
        recyclerView.setHasFixedSize(true);

        // Recycle view adapter.
        mAdapter = new ViolationAdapter(items, this);
        recyclerView.setAdapter(mAdapter);

        // User linear layout manager.
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Lambda Expression for Ok button listener.
        okButton.setOnClickListener((View view) -> filterData());

        // Lambda Expression for Reset button listener.
        resetButton.setOnClickListener((View view) -> {
            dateFromTextView.setText("--");
            dateToTextView.setText("--");
            radioButton = findViewById(R.id.filter_all);
            radioButton.setChecked(true);
            okButton.performClick();
        });

        // Lambda Expression for FAB onclick listener.
        filterFab.setOnClickListener((View view) -> {
            if (filterPanel.getVisibility() == View.GONE) {
                filterPanel.setVisibility(View.VISIBLE);
            } else {
                filterPanel.setVisibility(View.GONE);
            }
        });

        // Lambda Expression for Data "From" buttons listeners.
        dateFromButton.setOnClickListener((View view) -> {
            String maximumDate = null;
            long maxDateLong = -1;
            java.util.Date maxDateObject = null;

            if (!dateToTextView.getText().toString().contains("--")) {
                maximumDate = dateToTextView.getText().toString();
                try {
                    maxDateObject = new SimpleDateFormat("yyyy-MM-dd").parse(maximumDate);
                    maxDateLong = maxDateObject.getTime();
                } catch (Exception ex) {
                    // Log Message.
                    Log.e("DEBUG" , "Error while processing \"From\" date picker");
                }
            }

            createDatePicker(dateFromTextView, -1, maxDateLong);
        });

        // Lambda Expression for Data "To" buttons listeners.
        dateToButton.setOnClickListener((View view) -> {
            String minimumDate = null;
            long minDateLong = -1;
            java.util.Date minDateObject = null;

            if (!dateFromTextView.getText().toString().contains("--")) {
                minimumDate = dateFromTextView.getText().toString();
                try {
                    minDateObject = new SimpleDateFormat("yyyy-MM-dd").parse(minimumDate);
                    minDateLong = minDateObject.getTime();
                } catch (Exception ex) {
                    //Log Message
                    Log.e("DEBUG" , "Error while processing \"To\" date picker");

                }
            }

            createDatePicker(dateToTextView, minDateLong, -1);
        });
    }

    // Add functionality to back arrow button.
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    public void radioButtonCheck(View view) {
        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioButtonId);

        if (radioButton.getId() == R.id.filter_custom) {
            dateFromButton.setEnabled(true);
            dateToButton.setEnabled(true);
        } else {
            dateFromButton.setEnabled(false);
            dateToButton.setEnabled(false);
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void filterData() {
        switch (radioButton.getId())
        {
            case R.id.filter_all: {
                items = dbHandler.getViolations();
                mAdapter = new ViolationAdapter(items, this);
                recyclerView.setAdapter(mAdapter);
                break;
            }
            case R.id.filter_week: {
                items = dbHandler.getViolationsByWeek();
                mAdapter = new ViolationAdapter(items, this);
                recyclerView.setAdapter(mAdapter);
                break;
            }
            case R.id.filter_custom: {
                if (dateFromTextView.getText().toString().contains("--") || dateToTextView.getText().toString().contains("--")) {
                    Toast.makeText(this, "Select dates and try again!", Toast.LENGTH_SHORT).show();
                    break;
                }
                items = dbHandler.getViolationsByTimestamp(dateFromTextView.getText().toString(), dateToTextView.getText().toString());
                mAdapter = new ViolationAdapter(items, this);
                recyclerView.setAdapter(mAdapter);
                break;
            }
        }
    }


    // Create Date Picker.
    private void createDatePicker(TextView edtTxtToChange, long minimumDate, long maximumDate) {

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateSetListener = (DatePicker view, int year, int month, int dayOfMonth) -> {

            month = month + 1;

            StringBuilder stringBuilder = new StringBuilder(year + "-");

            if (month < 10) {
                stringBuilder.append(0);
            }
            stringBuilder.append(month).append("-");

            if (dayOfMonth < 10) {
                stringBuilder.append(0);
            }
            stringBuilder.append(dayOfMonth);
            edtTxtToChange.setText(stringBuilder.toString());
        };


        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, dateSetListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(0);
        datePickerDialog.getDatePicker().setMaxDate(java.lang.System.currentTimeMillis());

        if (minimumDate != -1) {
            try {
                datePickerDialog.getDatePicker().setMinDate(minimumDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (maximumDate != -1) {
            try {
                datePickerDialog.getDatePicker().setMaxDate(maximumDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        datePickerDialog.show();
    }
}