package com.egoriku.catsrunning.activities;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.helpers.DbCursor;
import com.egoriku.catsrunning.helpers.InquiryBuilder;
import com.egoriku.catsrunning.utils.ConverterTime;
import com.egoriku.catsrunning.utils.VectorToDrawable;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.egoriku.catsrunning.helpers.DbActions.updateIsTrackDelete;
import static com.egoriku.catsrunning.helpers.DbActions.updateLikedDigit;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.LAT;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Columns.LNG;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Query.TRACK_ID_EQ;
import static com.egoriku.catsrunning.models.Constants.ConstantsSQL.Tables.TABLE_POINT;
import static com.egoriku.catsrunning.models.Constants.Extras.KEY_TYPE_FIT;
import static com.egoriku.catsrunning.models.Constants.KeyReminder.KEY_ID;
import static com.egoriku.catsrunning.models.Constants.TracksOnMApActivity.KEY_DISTANCE;
import static com.egoriku.catsrunning.models.Constants.TracksOnMApActivity.KEY_LIKED;
import static com.egoriku.catsrunning.models.Constants.TracksOnMApActivity.KEY_TIME_RUNNING;
import static com.egoriku.catsrunning.models.Constants.TracksOnMApActivity.KEY_TOKEN;
import static com.egoriku.catsrunning.utils.TypeFitBuilder.getTypeFit;

public class TrackOnMapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int paddingMap = 150;
    private GoogleMap mMap;
    private Toolbar toolbar;
    private SupportMapFragment mapFragment;
    private TextView distanceText;
    private TextView timeRunningText;
    private TextView typeFitText;

    private List<LatLng> coordinatesList;
    private String startRunningHint;
    private String endRunningHint;
    private int liked;


    @SuppressLint("StringFormatMatches")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_on_maps);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.track_on_maps_activity_map_fragment);
        toolbar = (Toolbar) findViewById(R.id.toolbar_app);
        distanceText = (TextView) findViewById(R.id.track_on_maps_activity_distance_text);
        timeRunningText = (TextView) findViewById(R.id.track_on_maps_activity_time_running_text);
        typeFitText = (TextView) findViewById(R.id.track_on_maps_activity_type_fit);

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
                    coordinatesList.add(new LatLng(dbCursor.getDouble(LAT), dbCursor.getDouble(LNG)));
                } while (cursorPoints.moveToNext());
            }
            dbCursor.close();

            distanceText.setText(String.format(getString(R.string.track_fragment_distance_meter), getIntent().getExtras().getLong(KEY_DISTANCE)));
            typeFitText.setText(String.format(getString(R.string.track_fragment_time_running), getTypeFit(getIntent().getExtras().getInt(KEY_TYPE_FIT), true, R.array.all_fitness_data_categories)));
            timeRunningText.setText(ConverterTime.ConvertTimeToString(getIntent().getExtras().getLong(KEY_TIME_RUNNING)));
            liked = getIntent().getExtras().getInt(KEY_LIKED, -1);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.track_on_map_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem likedItem = menu.findItem(R.id.menu_tracks_on_map_activity_action_like);
        switch (liked) {
            case 0:
                likedItem.setIcon(VectorToDrawable.getDrawable(R.drawable.ic_vec_star_border_white));
                break;
            case 1:
                likedItem.setIcon(VectorToDrawable.getDrawable(R.drawable.ic_vec_star_white));
                break;

            default:
                likedItem.setVisible(false);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_tracks_on_map_activity_action_like:
                switch (liked) {
                    case 0:
                        liked = 1;
                        updateLikedDigit(liked, getIntent().getExtras().getInt(KEY_ID));
                        break;

                    case 1:
                        liked = 0;
                        updateLikedDigit(liked, getIntent().getExtras().getInt(KEY_ID));
                        break;
                }
                invalidateOptionsMenu();
                return true;

            case R.id.menu_tracks_on_map_activity_action_delete:
                Snackbar.make(toolbar, getString(R.string.track_on_map_activity_track_delete), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.track_on_map_activity_cancel_delete), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Snackbar.make(toolbar, getString(R.string.track_on_map_activity_track_cancel_delete_success), Snackbar.LENGTH_SHORT).show();
                            }
                        })
                        .setCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                switch (event) {
                                    case Snackbar.Callback.DISMISS_EVENT_TIMEOUT:
                                        updateIsTrackDelete(getIntent().getExtras().getInt(KEY_ID));

                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        App.getInstance().getFirebaseDbReference().child(user.getUid()).child(getIntent().getExtras().getString(KEY_TOKEN)).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                dataSnapshot.getRef().setValue(null);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                Toast.makeText(TrackOnMapsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        break;
                                }
                            }
                        }).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_LIKED, liked);
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        liked = savedInstanceState.getInt(KEY_LIKED);
        super.onRestoreInstanceState(savedInstanceState);
    }
}
