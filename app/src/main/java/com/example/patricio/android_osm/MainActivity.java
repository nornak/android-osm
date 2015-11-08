package com.example.patricio.android_osm;

import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.api.IMapView;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MapView miMapa;
    private IMapController mapaController;
    private Location pos = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        miMapa = (MapView)findViewById(R.id.openmapview);
        miMapa.setMultiTouchControls(true);
        mapaController = miMapa.getController();
        mapaController.setZoom(14);

        initGPS();
        mostrarPosicion();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void initGPS(){
        LocationManager locManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        pos = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Log.d("MAIN", "lat: " + pos.getLatitude());
        Log.d("MAIN", "lng: " + pos.getLongitude());

        mapaController.setCenter(new GeoPoint(pos.getLatitude(), pos.getLongitude()));

        LocationListener locListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("MAIN", "onLocationChanged");
                pos = location;
                mostrarPosicion();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("MAIN", "onStatusChanged");

            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("MAIN", "onProviderEnabled");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("MAIN", "onProviderDisabled");
            }
        };

        if(!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this,"sin gps", Toast.LENGTH_SHORT).show();
        }
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, locListener);
    }

    private void mostrarPosicion() {
        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        items.add(new OverlayItem("POS", "", new GeoPoint(pos.getLatitude(), pos.getLongitude())));

        ResourceProxy mResourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
        ItemizedIconOverlay<OverlayItem> mMyLocationOverlay = new ItemizedIconOverlay<OverlayItem>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index,
                                                     final OverlayItem item) {
                        return true; // We 'handled' this event.
                    }

                    @Override
                    public boolean onItemLongPress(final int index,
                                                   final OverlayItem item) {
                        return false;
                    }
                }, mResourceProxy);

        miMapa.getOverlays().add(mMyLocationOverlay);
    }
}
