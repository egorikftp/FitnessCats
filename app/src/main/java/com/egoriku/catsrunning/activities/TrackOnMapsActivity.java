package com.egoriku.catsrunning.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
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
import com.egoriku.catsrunning.models.Firebase.SaveModel;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.egoriku.catsrunning.models.Constants.Extras.EXTRA_TRACK_ON_MAPS;
import static com.egoriku.catsrunning.models.Constants.FirebaseFields.TRACKS;
import static com.egoriku.catsrunning.models.Constants.TracksOnMapActivity.KEY_LIKED;
import static com.egoriku.catsrunning.util.DrawableKt.drawableCompat;
import static com.egoriku.catsrunning.utils.TypeFitBuilder.getTypeFit;
import static com.egoriku.catsrunning.utils.VectorToDrawable.createBitmapFromVector;

public class TrackOnMapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int paddingMap = 150;
    private GoogleMap mMap;
    private Toolbar toolbar;
    private String trackToken;

    private List<LatLng> coordinatesList;
    private String startRunningHint;
    private String endRunningHint;
    private boolean isFavorite;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private SaveModel saveModel;

    @SuppressWarnings("ConstantConditions")
    @SuppressLint("StringFormatMatches")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_on_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.track_on_maps_activity_map_fragment);
        toolbar = (Toolbar) findViewById(R.id.toolbar_app);
        TextView distanceText = (TextView) findViewById(R.id.track_on_maps_activity_distance_text);
        TextView timeRunningText = (TextView) findViewById(R.id.track_on_maps_activity_time_running_text);
        TextView typeFitText = (TextView) findViewById(R.id.track_on_maps_activity_type_fit);

        startRunningHint = getString(R.string.track_fragment_start_running_hint);
        endRunningHint = getString(R.string.track_fragment_end_running_hint);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.fragment_track_toolbar_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        coordinatesList = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            saveModel = (SaveModel) bundle.get(EXTRA_TRACK_ON_MAPS);

            for (int i = 0; i < saveModel.getPoints().size(); i++) {
                coordinatesList.add(new LatLng(saveModel.getPoints().get(i).getLat(), saveModel.getPoints().get(i).getLng()));
            }

            distanceText.setText(String.format(getString(R.string.track_fragment_distance_meter), saveModel.getDistance()));
            typeFitText.setText(String.format(getString(R.string.track_fragment_time_running), getTypeFit(saveModel.getTypeFit(), true, R.array.all_fitness_data_categories)));
            timeRunningText.setText(ConverterTime.ConvertTimeToString(saveModel.getTime()));
            isFavorite = saveModel.isFavorite();
            trackToken = saveModel.getTrackToken();
        }

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        MapStyleOptions styleOptions = MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.maps_style);
        mMap.setMapStyle(styleOptions);

        if (coordinatesList.isEmpty()) {
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
                    builder.include(createMarker(mMap, coordinatesList.get(i), startRunningHint, R.drawable.ic_vec_location_start));
                    continue;
                }

                if (i == coordinatesList.size() - 1) {
                    builder.include(createMarker(mMap, coordinatesList.get(coordinatesList.size() - 1), endRunningHint, R.drawable.ic_vec_location_end));
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

    private LatLng createMarker(GoogleMap map, LatLng latLng, String title, @DrawableRes int idIco) {
        Marker marker = map.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title)
                .icon(BitmapDescriptorFactory.fromBitmap(createBitmapFromVector(App.appInstance.getResources(), idIco))));
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
        likedItem.setIcon(isFavorite ?
                drawableCompat(this, R.drawable.ic_vec_star_white) :
                drawableCompat(this, R.drawable.ic_vec_star_border_white));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_tracks_on_map_activity_action_like:
                isFavorite = !isFavorite;
                saveModel.setFavorite(isFavorite);
                updateTrackFavorire(saveModel);
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
                                        removeTrack();
                                        break;
                                }
                            }
                        }).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateTrackFavorire(SaveModel saveModel) {
        if (user != null) {
            databaseReference
                    .child(TRACKS)
                    .child(user.getUid())
                    .child(trackToken)
                    .setValue(saveModel, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Toast.makeText(TrackOnMapsActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private void removeTrack() {
        if (user != null && trackToken != null) {
            databaseReference
                    .child(TRACKS)
                    .child(user.getUid())
                    .child(trackToken)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            dataSnapshot.getRef().setValue(null);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(TrackOnMapsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_LIKED, isFavorite);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isFavorite = savedInstanceState.getBoolean(KEY_LIKED);
    }

    public static void start(Context context, SaveModel saveModel) {
        context.startActivity(new Intent(context, TrackOnMapsActivity.class)
                .putExtra(EXTRA_TRACK_ON_MAPS, saveModel));
    }
}
