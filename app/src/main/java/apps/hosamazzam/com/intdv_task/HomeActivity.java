package apps.hosamazzam.com.intdv_task;

import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;

import apps.hosamazzam.com.intdv_task.views.MapActivity;

public class HomeActivity extends MapActivity implements MapActivity.onMapReadyListener {

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
    }

    public void prepareView() {

    }

    public void listener() {

    }

    @Override
    public void onReady(GoogleMap googleMap) {

    }
}
