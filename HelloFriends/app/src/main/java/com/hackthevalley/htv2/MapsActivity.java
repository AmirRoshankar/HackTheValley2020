package com.hackthevalley.htv2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener

{

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient; // client
    private LocationRequest locationRequest; // location request
    private Location lastLocation;
    private Marker currentUserLocationMarker; //marker
    private static final int USER_LOCATION_CODE = 99; //mhm

    private ArrayList<double[]> history = new ArrayList<double[]>();

    SQLiteDatabase myDB;

    DatabaseHelper db;
    //private Timestamp ts = new Timestamp();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // add
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkUserLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


//        try{
//            myDB = openOrCreateDatabase("locations.db",MODE_PRIVATE,null);
//
//            addData(1,1.01,1.02,1.03);
//
//        }
//        catch (Exception e){
//            Log.d("Error data", e.toString());
//        }

        db = new DatabaseHelper((this));
//        boolean insert = db.insertData(1,1.011,1.022,1.033);
//        if(insert==true){
//            Toast.makeText(getApplicationContext(),"Successfully inserted",Toast.LENGTH_SHORT).show();
//        }
        AddData();
    }
    public void AddData() {
        db.insertData(1, 1.01111, 1.0222, 1.0333);
        db.insertData(2, 2.01111, 2.0222, 2.0333);
        db.insertData(3, 2.01111, 2.0222, 2.0333);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(43.7839, -79.1874);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Where Andy's Laptop Got Shiet On"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            buildGoogleApiClient();

            mMap.setMyLocationEnabled(true);
        } // permission for you
    }

    // checking PERMISSION
    public boolean checkUserLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, USER_LOCATION_CODE);
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, USER_LOCATION_CODE);
            }
            return false;
        }
        else {
            return true;
        }
    }

    // request permission response
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case USER_LOCATION_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null) {
                            buildGoogleApiClient(); // create new client
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                    else {
                        Toast.makeText(this, "Why you deny my permission", Toast.LENGTH_SHORT).show(); //***EDIT
                    }
                }
        }
    }



    //protected added
    protected synchronized void buildGoogleApiClient()
    {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    // location tracker
    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;

        if (currentUserLocationMarker != null) {
            currentUserLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        String cityname = getCityName(latLng);
//        try {
//            double[] temp = new double[3];
//            temp[0] = Float.parseFloat(cityname.substring(cityname.indexOf("latitude=") + "latitude=".length(), cityname.substring(cityname.indexOf("latitude=") + "latitude=".length()).indexOf(",")));
//            temp[1] = Float.parseFloat(cityname.substring(cityname.indexOf("longitude=") + "longitude=".length(), cityname.substring(cityname.indexOf("longitude=") + "longitude=".length()).indexOf(",")));
//            temp[2] = System.currentTimeMillis() / 1000.0 / 60 / 60;//pass the time;
//            boolean makeNew = true;
//            for (int count = 0; count < history.size(); count++) {
//                //double r = Math.pow(Math.pow((temp[0]-history.get(count)[0]),2.0) + Math.pow(temp[1]-history.get(count)[1],2),0.5);
//                double R = 6371e3; // metres
//                double a1 = Math.toRadians(temp[0]);
//                double a2 = Math.toRadians(history.get(count)[0]);
//                double delta1 = Math.toRadians((history.get(count)[0] - temp[0]));
//                double delta2 = Math.toRadians(history.get(count)[1] - temp[1]);
//
//                double a = Math.sin(delta1 / 2) * Math.sin(delta1 / 2) +
//                        Math.cos(a1) * Math.cos(a2) *
//                                Math.sin(delta2 / 2) * Math.sin(delta2 / 2);
//                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//
//                double d = R * c;
//                if (d <= 100) {
//                    history.get(count)[2] += temp[2];
//                    Log.d("sql data", getData());
//                    makeNew = false;
//                    break;
//                }
//            }
//            if (makeNew) {
//                history.add(temp);
//                addData(1, temp[0], temp[1], temp[2]);
//                Log.d("sql data", getData());
//            }
//
//        }
//        catch(Exception e){
//            Log.d("Error", e.toString());
//        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

        currentUserLocationMarker = mMap.addMarker(markerOptions); // gmarker for current location

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(50)); //****change for zooms

        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this); // remove
        }
    }

    private String getCityName(LatLng myCoordinates)
    {
        String myCity = "";
        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault() );
        try {

            List<Address> addresses = geocoder.getFromLocation(myCoordinates.latitude, myCoordinates.longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            myCity = addresses.get(0).getLocality();
            Log.d("mylog", "Complete Address: " + addresses.toString());
            Log.d("mylog", "Address: " + address);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return myCity;
    }

//    public String getData()
//    {
//        Log.d("IM TRYING", "TO BE A GOOD BUDHIST");
//        return myDB.rawQuery("SELECT * FROM location_data WHERE ID = 1 Limit 2;", null).toString();
//    }


    // time interval
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//              LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
//        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}