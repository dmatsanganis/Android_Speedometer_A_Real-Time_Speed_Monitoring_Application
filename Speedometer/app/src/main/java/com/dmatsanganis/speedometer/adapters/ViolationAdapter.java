package com.dmatsanganis.speedometer.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dmatsanganis.speedometer.R;
import com.dmatsanganis.speedometer.ViolationInfo;
import com.dmatsanganis.speedometer.object.ViolationObject;


import java.text.DecimalFormat;
import java.util.List;

public class ViolationAdapter extends RecyclerView.Adapter<ViolationAdapter.ViewHolder> {

    // Tag for Debugging Purpose.
    private static final String TAG = "ViolationAdapter";
    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    List<ViolationObject> items;

    Context context;

    public ViolationAdapter(List<ViolationObject> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Get specific view from customs XML files.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.violation_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        // Update speed and timestamp text view values.
        holder.speedTextView.setText(String.valueOf(decimalFormat.format(items.get(position).getSpeed())));
        holder.timestampTextView.setText(items.get(position).getTimestamp().toString());

        // On click "info" button start violation detail activity.
        holder.moreButton.setOnClickListener((View view) -> {
            Log.d(TAG, "Violation item button: click.");

            Context context = view.getContext();
            Intent intent = new Intent(context, ViolationInfo.class);
            intent
                    .putExtra("timestamp", items.get(position).getTimestamp().toString())
                    .putExtra("longitude", items.get(position).getLongitude())
                    .putExtra("latitude", items.get(position).getLatitude())
                    .putExtra("speed", String.valueOf(decimalFormat.format(items.get(position).getSpeed())));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {

        return items.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        android.widget.LinearLayout LinearLayout;
        TextView timestampTextView;
        TextView speedTextView;
        Button moreButton;

        public ViewHolder(View itemView) {
            super(itemView);
            LinearLayout = itemView.findViewById(R.id.violation_list_parent);
            timestampTextView = itemView.findViewById(R.id.violation_timestamp);
            speedTextView = itemView.findViewById(R.id.violation_speed);
            moreButton = itemView.findViewById(R.id.violation_details);
        }
    }
}