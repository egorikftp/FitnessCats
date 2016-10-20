package com.egoriku.catsrunning.activities;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.helpers.DbCursor;
import com.egoriku.catsrunning.helpers.InquiryBuilder;
import com.egoriku.catsrunning.utils.ConverterTime;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import static com.egoriku.catsrunning.models.State.LAT;
import static com.egoriku.catsrunning.models.State.LNG;
import static com.egoriku.catsrunning.models.State.TABLE_POINT;
import static com.egoriku.catsrunning.models.State.TRACK_ID_EQ;

public class TrackOnMapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String KEY_ID = "KEY_ID";
    public static final String KEY_DISTANCE = "KEY_DISTANCE";
    public static final String KEY_TIME_RUNNING = "KEY_TIME_RUNNING";
    private static final int paddingMap = 150;

    private GoogleMap mMap;
    private Toolbar toolbar;
    private SupportMapFragment mapFragment;
    private TextView distanceText;
    private TextView timeRunningText;

    private List<LatLng> coordinatesList;
    private String startRunningHint;
    private String endRunningHint;

    @SuppressLint("StringFormatMatches")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_on_maps);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.track_on_maps_activity_map_fragment);
        toolbar = (Toolbar) findViewById(R.id.toolbar_app);
        distanceText = (TextView) findViewById(R.id.track_on_maps_activity_distance_text);
        timeRunningText = (TextView) findViewById(R.id.track_on_maps_activity_time_running_text);

        startRunningHint = getString(R.string.track_fragment_start_running_hint);
        endRunningHint = getString(R.string.track_fragment_end_running_hint);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.fragment_track_toolbar_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        coordinatesList = new ArrayList<>();

        if (getIntent().getExtras() != null) {
            Cursor cursorPoints = new InquiryBuilder()
                    .get(LNG, LAT)
                    .from(TABLE_POINT)
                    .where(false, TRACK_ID_EQ, String.valueOf(getIntent().getExtras().getInt(KEY_ID)))
                    .select();
            DbCursor dbCursor = new DbCursor(cursorPoints);

            if (dbCursor.isValid()) {
                do {
                    coordinatesList.add(new LatLng(dbCursor.getDouble(LAT), dbCursor.getDouble(LNG))
                    );
                } while (cursorPoints.moveToNext());
            }
            dbCursor.close();

            distanceText.setText(String.format(getString(R.string.track_fragment_distance_meter), getIntent().getExtras().getLong(KEY_DISTANCE)));
            timeRunningText.setText(ConverterTime.ConvertTimeToStringWithMill(getIntent().getExtras().getLong(KEY_TIME_RUNNING)));
        }
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        MapStyleOptions styleOptions = MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.maps_style);
        mMap.setMapStyle(styleOptions);

        if (coordinatesList.size() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.track_fragment_toast_text_no_points), Toast.LENGTH_SHORT).show();
        } else {
            mMap.addPolyline(new PolylineOptions()
                    .addAll(coordinatesList)
                    .color(getResources().getColor(R.color.colorAccent))
                    .width(10)
            );

            final LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (int i = 0; i < coordinatesList.size(); i++) {
                if (i == 0) {
                    builder.include(createMarker(mMap, coordinatesList.get(i), startRunningHint, R.drawable.ic_location_on_map_start));
                    continue;
                }

                if (i == coordinatesList.size() - 1) {
                    builder.include(createMarker(mMap, coordinatesList.get(coordinatesList.size() - 1), endRunningHint, R.drawable.ic_location_on_map_end));
                    continue;
                }
                builder.include(coordinatesList.get(i));
            }
            final LatLngBounds bounds = builder.build();

            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, paddingMap));
                }
            });
            mMap.getUiSettings().setZoomControlsEnabled(true);
        }
    }


    private LatLng createMarker(GoogleMap map, LatLng latLng, String title, int idIco) {
        Marker marker = map.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(idIco))
        );
        return marker.getPosition();
    }
}
