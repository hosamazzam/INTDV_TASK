package apps.hosamazzam.com.intdv_task.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import apps.hosamazzam.com.intdv_task.R;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 970;
    public static GoogleMap mMap;
    private SupportMapFragment mapFragment;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult result) {
            super.onLocationResult(result);
            if (result == null) return;
            mCurrentLocation = result.getLocations().get(0);
            System.out.println("location ok : " + mCurrentLocation.getLatitude() + " " + mCurrentLocation.getLongitude());
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }

        @Override
        public void onLocationAvailability(LocationAvailability availability) {
            //boolean isLocation = availability.isLocationAvailable();
        }
    };
    private onMapReadyListener onMapReadyListener;

    public void registerMapReadyListener(onMapReadyListener listener) {
        onMapReadyListener = listener;
        mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        checkPermision();

    }

    private void checkPermision() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                checkProvider();
            }
        } else {
            checkProvider();

        }
    }

    @SuppressLint("RestrictedApi")
    private void getDeviceLocation() {
        // Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SettingsClient mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationRequest = new LocationRequest();
        //  mLocationRequest.setInterval(10000);
        //  mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setNumUpdates(1);

        LocationManager locateManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = locateManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) { // network
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        } else { // gps
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        }


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest mLocationSettingsRequest = builder.build();

        try {

            Task<LocationSettingsResponse> locationResponse = mSettingsClient.checkLocationSettings(mLocationSettingsRequest);
            locationResponse.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    Log.e("Response", "Successful acquisition of location information!!");

                    System.out.println("setting ok");
                    if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        System.out.println("Permission denied");
                        return;
                    }
                    mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                }
            });

            locationResponse.addOnFailureListener(this, new

                    OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            int statusCode = ((ApiException) e).getStatusCode();
                            switch (statusCode) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    checkProvider();
                                    e.printStackTrace();
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    e.printStackTrace();
                                    //   Toast.makeText(getContext(), "عفوا لم نتمكن من معرفه موقعك هاتفك لا يدعم", Toast.LENGTH_LONG).show();
                                    //   lat = -1;
                                    //   lng = -1;
                                    //  checkProvider();
                                    //  Log.e("onFailure", errorMessage);
                            }
                        }
                    });

        } catch (SecurityException e) {
            Log.e("MAp", "getDeviceLocation: SecurityException: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void checkProvider() {
        LocationManager locateManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = locateManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            enabled = locateManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        // check if enabled and if not send user to the GPS settings
        if (!enabled) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Location services is turned off,please enable it");

            builder.setPositiveButton("Go to setting", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, 333);
                }

            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

            // change text color
            //    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#61b8d4"));
            //    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#61b8d4"));
        } else {
            getDeviceLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    getDeviceLocation();
                    mapFragment.getMapAsync(this);
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();

                }
                break;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 333) {
                getDeviceLocation();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            mMap.setMyLocationEnabled(true);
        }
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setAllGesturesEnabled(true);
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setCompassEnabled(false);

    }

    public interface onMapReadyListener {
        void onReady(GoogleMap googleMap);
    }

}
