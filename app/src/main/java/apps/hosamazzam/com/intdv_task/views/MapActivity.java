package apps.hosamazzam.com.intdv_task.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
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
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import apps.hosamazzam.com.intdv_task.R;
import apps.hosamazzam.com.intdv_task.adapters.PlaceAutocompleteAdapter;
import apps.hosamazzam.com.intdv_task.helpers.Utility;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, PlaceAutocompleteAdapter.PlaceAutoCompleteInterface, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, View.OnClickListener {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 970;
    private static GoogleMap mMap;
    private SupportMapFragment mapFragment;
    public View locationButton;

    private static final LatLngBounds BOUNDS_Egypt = new LatLngBounds(
            new LatLng(22.006347, 25.010315), new LatLng(31.322526, 34.219822));
    GoogleApiClient mGoogleApiClient;
    LinearLayoutManager llm;
    PlaceAutocompleteAdapter mAdapter;
    EditText mSearchEdittext;
    ImageView mClear;
    private RecyclerView mRecyclerView;


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
            Toast.makeText(MapActivity.this, "location detected", Toast.LENGTH_SHORT).show();
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(),
                    mCurrentLocation.getLongitude());
            if (mMap != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                System.out.println("map not null : " + mCurrentLocation.getLatitude() + " " + mCurrentLocation.getLongitude());
                if (locationButton != null) {
                    System.out.println("location button not null");
                    locationButton.performClick();
                }
            }
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);

        }

        @Override
        public void onLocationAvailability(LocationAvailability availability) {
            //boolean isLocation = availability.isLocationAvailable();
        }
    };
    private onMapReadyListener onMapReadyListener;
    private onPlaceReadyListener onPlaceReadyListener;

    public void registerMapReadyListener(onMapReadyListener listener) {
        onMapReadyListener = listener;


        mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, Utility.pxFromDp(MapActivity.this, 30));
        locationButton.setLayoutParams(rlp);

        checkPermision();
    }

    public void registerPlaceReadyListener(onPlaceReadyListener listener) {
        onPlaceReadyListener = listener;
        initSearch();
    }

    private void initSearch() {
        mRecyclerView = findViewById(R.id.list_search);
        mRecyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setNestedScrollingEnabled(false);

        mSearchEdittext = findViewById(R.id.search_et);
        mClear = findViewById(R.id.search_clear);
        mClear.setOnClickListener(this);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("EG")
                .build();

        mAdapter = new PlaceAutocompleteAdapter(this, R.layout.view_placesearch,
                mGoogleApiClient, BOUNDS_Egypt, typeFilter);
        mRecyclerView.setAdapter(mAdapter);

        mSearchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mClear.setVisibility(View.VISIBLE);
                    if (mAdapter != null) {
                        mRecyclerView.setAdapter(mAdapter);
                    }
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    mClear.setVisibility(View.GONE);
                    //     if (mSavedAdapter != null && mSavedAddressList.size() > 0) {
                    //       mRecyclerView.setAdapter(mSavedAdapter);
                    //    }
                }
                if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
                    mAdapter.getFilter().filter(s.toString());
                } else if (!mGoogleApiClient.isConnected()) {
//                    Toast.makeText(getApplicationContext(), Constants.API_NOT_CONNECTED, Toast.LENGTH_SHORT).show();
                    Log.e("", "NOT CONNECTED");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


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
                    Toast.makeText(MapActivity.this, "Finding your location", Toast.LENGTH_LONG).show();
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

        onMapReadyListener.onReady(mMap);
    }


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    @Override
    public void onClick(View v) {
        if (v == mClear) {
            mSearchEdittext.setText("");
            if (mAdapter != null) {
                mAdapter.clearList();
            }

        }
    }


    @Override
    public void onPlaceClick(ArrayList<PlaceAutocompleteAdapter.PlaceAutocomplete> mResultList, int position) {
        if (mResultList != null) {
            try {
                final String placeId = String.valueOf(mResultList.get(position).placeId);
                        /*
                             Issue a request to the Places Geo Data API to retrieve a Place object with additional details about the place.
                         */

                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getCount() == 1) {
                            //Do the things here on Click.....
                            apps.hosamazzam.com.intdv_task.Db.Address address = new apps.hosamazzam.com.intdv_task.Db.Address();
                            address.setName(String.valueOf(places.get(0).getName()));
                            address.setDesc(String.valueOf(places.get(0).getAddress()));
                            address.setLng(places.get(0).getLatLng().longitude);
                            address.setLat(places.get(0).getLatLng().latitude);
                            onPlaceReadyListener.onPlaceClick(address);
                            mSearchEdittext.setText("");
                        } else {
                            Toast.makeText(getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {

            }

        }
    }


    public Address getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();

    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public interface onMapReadyListener {
        void onReady(GoogleMap googleMap);
    }

    public interface onPlaceReadyListener {
        void onPlaceClick(apps.hosamazzam.com.intdv_task.Db.Address address);
    }

}
