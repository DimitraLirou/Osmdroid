package com.example.osmdroid;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Debug;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.tabs.TabLayout;


import org.osmdroid.api.IMapController;
import org.osmdroid.api.Polyline;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.GroundOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final String MY_USER_AGENT = null;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map;
    private Object GeoPoint;
    Polyline polyline;
    ArrayList<OverlayItem> anotherOverlayItemArray;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        setContentView(R.layout.activity_main);
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK); //render
        map.setMultiTouchControls(true);

        GeoPoint AgrinioPoint = new GeoPoint(38.6272443930541, 21.418050853391883);
        GeoPoint PatrasPoint = new GeoPoint(38.29042025855486, 21.79600110957484);
        GeoPoint AthensPoint = new GeoPoint(38.013122242304256, 23.721082893094078);
        GeoPoint ThessalonikiPoint = new GeoPoint(40.67354575955466, 22.94276517764502);
        GeoPoint KavalaPoint = new GeoPoint(40.95736847290031, 24.414952873906188);


        IMapController mapController = map.getController();
        mapController.setCenter(AgrinioPoint);
        mapController.setCenter(PatrasPoint);
        mapController.setZoom(10.0);

        Marker startMarker = new Marker(map);
        startMarker.setPosition(AgrinioPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(startMarker);
        map.invalidate();

        startMarker.setIcon(getResources().getDrawable(R.drawable.ic_launcher));
        startMarker.setTitle("Start point");

        RoadManager roadManager = new OSRMRoadManager(this, MY_USER_AGENT);
        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        AgrinioPoint = new GeoPoint(38.6272443930541, 21.418050853391883);
        PatrasPoint = new GeoPoint(38.29042025855486, 21.79600110957484);
        waypoints.add(AgrinioPoint);
        waypoints.add(PatrasPoint);

        Road road = roadManager.getRoad(waypoints);
        org.osmdroid.views.overlay.Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
        map.getOverlays().add(roadOverlay);
        map.invalidate();


        Location startPoint = new Location("locationA");
        AgrinioPoint.setLatitude(38.6272443930541);
        AgrinioPoint.setLongitude(21.418050853391883);

        Location endPoint = new Location("locationB");
        PatrasPoint.setLatitude(38.29042025855486);
        PatrasPoint.setLongitude(21.79600110957484);

        double distance = startPoint.distanceTo(endPoint);

        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

        OverlayItem home = new OverlayItem("Dimitra's office", "my office", new GeoPoint(38.6272443930541, 21.418050853391883));
        Drawable m = home.getMarker(0);
        items.add(home);
        items.add(new OverlayItem("Campus", "CEID", new GeoPoint(38.29042025855486, 21.79600110957484)));
        items.add(new OverlayItem("Athens", "Centre", new GeoPoint(38.013122242304256, 23.721082893094078)));
        items.add(new OverlayItem("Thessaloniki", "Centre", new GeoPoint(40.67354575955466, 22.94276517764502)));

        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(getApplicationContext(),
                items, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(int index, OverlayItem item) {
                return true;
            }

            @Override
            public boolean onItemLongPress(int index, OverlayItem item) {
                return false;
            }
        });

        mOverlay.setFocusItemsOnTap(true);
        map.getOverlays().add(mOverlay);


        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE  // WRITE_EXTERNAL_STORAGE is required in order to show the map
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }


    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause(); //needed for compass, my location overlays, v6.0.0 and up
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
}