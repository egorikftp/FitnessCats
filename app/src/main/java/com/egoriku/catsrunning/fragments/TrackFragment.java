package com.egoriku.catsrunning.fragments;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.activities.MainActivity;
import com.egoriku.catsrunning.models.TrackFragmentModel;
import com.egoriku.catsrunning.utils.ConverterTime;
import com.google.android.gms.maps.CameraUpdate;
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


public class TrackFragment extends Fragment implements OnMapReadyCallback {

    private static final String KEY_ID = "KEY_ID";
    public static final String TAG_TRACK_FRAGMENT = "TAG_TRACK_FRAGMENT";
    private static final String KEY_DISTANCE = "KEY_DISTANCE";
    private static final String KEY_TIME_RUNNING = "KEY_TIME_RUNNING";
    private static final int paddingMap = 150;

    private ArrayList<TrackFragmentModel> arrayTrackModels = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    private ArrayList<LatLng> coordList = new ArrayList<LatLng>();

    private SupportMapFragment mapFragment;
    private TextView distanceText;
    private TextView timeRunningText;

    private String startRunningHint;
    private String endRunningHint;

    public TrackFragment() {
    }


    public static TrackFragment newInstance(int id, long distance, long timeRunning) {
        TrackFragment trackFragment = new TrackFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_ID, id);
        args.putLong(KEY_DISTANCE, distance);
        args.putLong(KEY_TIME_RUNNING, timeRunning);
        trackFragment.setArguments(args);
        return trackFragment;
    }


    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).onFragmentStart(R.string.fragment_track_toolbar_title, TAG_TRACK_FRAGMENT);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        markers.clear();
        coordList.clear();

        Cursor cursor = App.getInstance().getDb().rawQuery(
                "SELECT Point._id AS id, Point.longitude AS lng, Point.latitude AS lat FROM Point Where Point.trackId = ?",
                new String[]{String.valueOf(getArguments().getInt(KEY_ID))}
        );

        if (cursor != null) {
            if (cursor.moveToNext()) {
                do {
                    TrackFragmentModel trackFragmentModel = new TrackFragmentModel();
                    trackFragmentModel.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    trackFragmentModel.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow("lat")));
                    trackFragmentModel.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow("lng")));

                    arrayTrackModels.add(trackFragmentModel);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        startRunningHint = getString(R.string.track_fragment_start_running_hint);
        endRunningHint = getString(R.string.track_fragment_end_running_hint);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track, container, false);
        distanceText = (TextView) view.findViewById(R.id.fragment_track_distance_text_view);
        timeRunningText = (TextView) view.findViewById(R.id.fragment_track_time_running_text_view);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        distanceText.setText(String.format(getString(R.string.track_fragment_distance_meter), getArguments().getLong(KEY_DISTANCE)));
        timeRunningText.setText(ConverterTime.ConvertTimeToStringWithMill(getArguments().getLong(KEY_TIME_RUNNING)));
    }


    @Override
    public void onMapReady(GoogleMap map) {
        map.clear();
        MapStyleOptions styleOptions = MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.maps_style);
        map.setMapStyle(styleOptions);

        for (int i = 0; i < arrayTrackModels.size(); i++) {
            coordList.add(new LatLng(arrayTrackModels.get(i).getLatitude(), arrayTrackModels.get(i).getLongitude()));
        }

        if (coordList.size() == 0) {
            Toast.makeText(getActivity(), getString(R.string.track_fragment_toast_text_no_points), Toast.LENGTH_SHORT).show();
        } else {
            createMarker(map, coordList.get(0), startRunningHint, R.drawable.ic_location_on_map_start);
            createMarker(map, coordList.get(coordList.size() - 1), endRunningHint, R.drawable.ic_location_on_map_end);

            map.addPolyline(new PolylineOptions()
                    .addAll(coordList)
                    .color(Color.BLUE)
                    .width(10)
            );

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : markers) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, paddingMap);
            map.moveCamera(cu);
            map.getUiSettings().setZoomControlsEnabled(true);
        }
    }


    private void createMarker(GoogleMap map, LatLng latLng, String title, int idIco) {
        Marker marker = map.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(idIco))
        );
        markers.add(marker);
    }
}
