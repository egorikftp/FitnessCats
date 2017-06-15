package com.egoriku.catsrunning.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.activities.TracksActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import timber.log.Timber;

import static com.egoriku.catsrunning.utils.VectorToDrawable.createBitmapFromVector;

public class WhereIFragment extends Fragment
        implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult> {

    private static final int REQUEST_CODE_PERMISSION = 2;
    private static final int REQUEST_CODE_LOCATION = 3;

    private GoogleMap googleMap;
    private MapView mapView;
    private Marker marker;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    private static final long UPDATE_INTERVAL = 10000L;
    private static final long FASTEST_INTERVAL = 2000L;

    private PendingResult<LocationSettingsResult> result;

    public WhereIFragment() {
    }

    public static WhereIFragment newInstance() {
        return new WhereIFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_where_i, container, false);

        Timber.d("onCreateView: ");

        checkLocationSetting();

        mapView = (MapView) view.findViewById(R.id.fragment_where_i_map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            Timber.e(e);
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                MapStyleOptions styleOptions = MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.maps_style);
                googleMap.setMapStyle(styleOptions);

                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            REQUEST_CODE_PERMISSION
                    );
                    return;
                }

                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(true);

            }
        });
        return view;
    }

    private void checkLocationSetting() {
        Timber.d("checkLocationSetting: ");
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(this);
    }

    private void requestNewLocationBySettingLocationReq() {
        Timber.d("requestNewLocationBySettingLocationReq: ");
        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        } else {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_CODE_PERMISSION
                );
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ((TracksActivity) getActivity()).onFragmentStart(R.string.navigation_drawer_where_i, FragmentsTag.WHERE_I);
        googleApiClient.connect();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Timber.d("onRequestPermissionsResult: ");
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                return;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.d("onActivityResult: ");
        switch (requestCode) {
            case REQUEST_CODE_LOCATION: {
                final LocationSettingsStates settingsStates = LocationSettingsStates.fromIntent(data);
                switch (requestCode) {
                    case Activity.RESULT_OK:
                        requestNewLocationBySettingLocationReq();
                        break;

                    case Activity.RESULT_CANCELED:
                        if (!settingsStates.isLocationUsable() && !settingsStates.isGpsUsable()) {
                            Timber.d("Please enable GPS and try again.");
                        }

                        break;

                    default:
                        break;
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Timber.d("onConnected: ");
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_LOCATION
            );
            return;
        }

        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastLocation != null) {
            updateMapWithCurrentLocation(lastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Timber.d("onConnectionSuspended: " + i);
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(getContext(), "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(getContext(), "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Timber.d("onLocationChanged: ");
        if (location != null) {
            updateMapWithCurrentLocation(location);
        }
    }

    private void updateMapWithCurrentLocation(Location location) {
        Timber.d("updateMapWithCurrentLocation: ");

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);

        if (marker == null) {
            marker = googleMap.addMarker((new MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.where_i_fragment_marker_title))
                    .snippet(getString(R.string.where_i_fragment_marker_snippet))
                    .icon(BitmapDescriptorFactory.fromBitmap(createBitmapFromVector(App.getInstance().getResources(), R.drawable.ic_vec_location_start)))));
        } else {
            marker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        }

        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(19).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onStop() {
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
        stopLocationUpdate();
        super.onStop();
    }

    private void stopLocationUpdate() {
        Timber.d("stopLocationUpdate: ");
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        Timber.d("onResult: ");
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Timber.d("onResult: success");
                requestNewLocationBySettingLocationReq();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Timber.d("onResult: required");
                try {
                    status.startResolutionForResult(getActivity(), REQUEST_CODE_LOCATION);
                } catch (IntentSender.SendIntentException e) {
                    Timber.e(e);
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                break;
        }
    }
}
