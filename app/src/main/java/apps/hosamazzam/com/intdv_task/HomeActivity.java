package apps.hosamazzam.com.intdv_task;

import android.content.Intent;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import apps.hosamazzam.com.intdv_task.helpers.IntentHelper;
import apps.hosamazzam.com.intdv_task.helpers.Utility;
import apps.hosamazzam.com.intdv_task.views.MapActivity;

public class HomeActivity extends MapActivity implements MapActivity.onMapReadyListener {
    private Marker mMarker;
    private Address mCurrentAddress;

    private TextView mLogout, mClear, mSave, mNavigate;
    private Spinner mAddress_spin;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initView();
        prepareView();
        listener();

    }

    public void initView() {
        registerMapReadyListener(this);
        mLogout = findViewById(R.id.logout);
        mClear = findViewById(R.id.clear);
        mSave = findViewById(R.id.save);
        mNavigate = findViewById(R.id.navigate);
        mAddress_spin = findViewById(R.id.address_spin);
        initGoLogin();
    }

    public void prepareView() {

    }

    private void initGoLogin() {
        try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestId()
                    .requestIdToken("879669234373-qp5gvmbtt0paaqp9ddd10elqbdo0rovr.apps.googleusercontent.com")
                    .build();
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        }
                    } /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.something_wrong, Toast.LENGTH_SHORT).show();
        }

    }

    public void listener() {
        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getAlpha() == 1) {
                    mMarker.remove();
                    v.setAlpha(0.3f);
                    mSave.setAlpha(0.3f);
                    mNavigate.setAlpha(0.3f);
                    Toast.makeText(HomeActivity.this, "Address cleared!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getAlpha() == 1) {
                    saveAddress(mCurrentAddress);
                }
            }
        });

        mNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getAlpha() == 1) {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + mCurrentAddress.getLatitude()
                            + "," + mCurrentAddress.getLongitude() + "&mode=d");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            }
        });

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleApiClient.stopAutoManage(HomeActivity.this);
                mGoogleApiClient.disconnect();
                Utility.unRegisterUserLogin(HomeActivity.this);
                IntentHelper.goTOAndFinish(HomeActivity.this, MainActivity.class, null);
            }
        });
    }

    @Override
    public void onReady(final GoogleMap googleMap) {
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {

                //remove previously placed Marker
                if (mMarker != null) {
                    mMarker.remove();
                }

                // place marker where user just clicked
                // Toast.makeText(HomeActivity.this, "location selected..", Toast.LENGTH_SHORT).show();
                mCurrentAddress = getAddress(point.latitude, point.longitude);
                if (mCurrentAddress != null) {
                    mMarker = googleMap.addMarker(new MarkerOptions().position(point).title(mCurrentAddress.getFeatureName())
                            .snippet(mCurrentAddress.getAddressLine(0))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    mMarker.showInfoWindow();
                    mClear.setAlpha(1);
                    mSave.setAlpha(1);
                    mNavigate.setAlpha(1);
                }
            }
        });
    }

    public void saveAddress(Address address) {

    }

}
