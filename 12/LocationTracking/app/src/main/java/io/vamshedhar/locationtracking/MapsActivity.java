package io.vamshedhar.locationtracking;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 111;

    LocationManager mLocationManager;
    LocationListener mLocationListner;

    Location currentLocation, startLocation;
    PolylineOptions polylineOptions;
    ArrayList<LatLng> positionsList;
    boolean trackingStart, trackStartLocation, loadInitialLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        trackingStart = false;
        trackStartLocation = true;
        loadInitialLocation = true;
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        polylineOptions = new PolylineOptions();
        positionsList = new ArrayList<>();
    }

    public String getAddress(double latitude, double longitude) {
        // Reference
        // https://stackoverflow.com/questions/9409195/how-to-get-complete-address-from-latitude-and-longitude
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();

            String completeAddress = address + ", " + city;

            return completeAddress;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return "Location - No Address";
    }

    private void setBoundaries() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(startLocation.getLatitude(), startLocation.getLongitude()));
        builder.include(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10);

        LatLngBounds bounds = builder.build();

        LatLngBounds currentBounds = mMap.getProjection().getVisibleRegion().latLngBounds;

        if (!currentBounds.contains(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))){
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Service not enabled")
                    .setMessage("Would you like to enable GPS settings?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            mLocationListner = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d("LocationTracker", "Tracker");
                    if (loadInitialLocation){

                        LatLng currentLocationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocationLatLng, 15));
                        mLocationManager.removeUpdates(mLocationListner);
                        loadInitialLocation = false;
                    } else{
                        currentLocation = location;

                        if (trackStartLocation) {
                            startLocation = location;
                            mMap.clear();
                            polylineOptions = new PolylineOptions().width(5).color(getResources().getColor(R.color.colorPrimary));
                            positionsList = new ArrayList<>();

                            LatLng currentLocationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(currentLocationLatLng).title(getAddress(location.getLatitude(), location.getLongitude()))).setSnippet("Start Location");
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocationLatLng, 15));

                            trackStartLocation=false;
                        }

                        polylineOptions.add(new LatLng(location.getLatitude(), location.getLongitude()));
                        mMap.addPolyline(polylineOptions);

                        setBoundaries();
                    }

                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };
        }
    }

    public void loadMap(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mMap.setMyLocationEnabled(true);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 20, mLocationListner);
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                if (!trackingStart) {

                    Toast.makeText(MapsActivity.this, "Started Location Tracking", Toast.LENGTH_LONG).show();

                    if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 20, mLocationListner);
                    trackingStart = true;
                } else {
                    trackingStart = false;
                    trackStartLocation = true;
                    mLocationManager.removeUpdates(mLocationListner);
                    Toast.makeText(MapsActivity.this, "Stopped Location Tracking", Toast.LENGTH_LONG).show();

                    if (currentLocation != null) {
                        LatLng endLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(endLocation).title(getAddress(endLocation.latitude, endLocation.longitude))).setSnippet("End Location");
                        setBoundaries();

                    }
                }


            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED  && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, // Activity
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        } else {
            loadMap();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED  && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    loadMap();

                } else {
                    Toast.makeText(this, "Please Grant required permissions for the App to work!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }
}
