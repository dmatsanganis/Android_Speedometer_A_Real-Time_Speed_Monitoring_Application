package com.dmatsanganis.speedometer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import com.dmatsanganis.speedometer.R;

// Marker bubble configuration
public class MapAdapter implements GoogleMap.InfoWindowAdapter {

    View mView;
    Context context;

    public MapAdapter(Context context) {
        this.context = context;
        this.mView = LayoutInflater.from(context).inflate(R.layout.map_items, null );
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderViewText(marker, mView);
        return mView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderViewText(marker, mView);
        return mView;
    }

    private void renderViewText(Marker marker, View view) {
        String snippet = marker.getSnippet();
        TextView markerDetails = view.findViewById(R.id.mapview_info);
        markerDetails.setText(snippet);
    }
}

