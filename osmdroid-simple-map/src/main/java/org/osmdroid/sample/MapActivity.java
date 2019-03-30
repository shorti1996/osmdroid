package org.osmdroid.sample;

import android.Manifest;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.simplemap.R;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.List;

/**
 * Bare bones osmdroid example
 * created on 2/17/2018.
 *
 * @author Alex O'Ree
 */

public class MapActivity extends AppCompatActivity {
    private MapView mapView = null;

    private IMapController mapController;
    private MyLocationNewOverlay mLocationOverlay;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        //TODO check permissions
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);

        // permissions
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();

        mapController = mapView.getController();
        mapController.setZoom(5.0);

        mapView.setTileSource(TileSourceFactory.MAPNIK);

        GpsMyLocationProvider provider = new GpsMyLocationProvider(getApplicationContext());
        provider.addLocationSource(LocationManager.GPS_PROVIDER);
        mLocationOverlay = new MyLocationNewOverlay(provider, mapView);
        mapView.getOverlays().add(mLocationOverlay);
        mapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);

        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
    }

    @Override
    public void onResume(){
        super.onResume();
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        if (mapView!=null) {
            mapView.onResume();
        }
        if (mLocationOverlay != null) {
            mLocationOverlay.enableFollowLocation();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        Configuration.getInstance().save(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        if (mapView!=null) {
            mapView.onPause();
        }
    }
}
