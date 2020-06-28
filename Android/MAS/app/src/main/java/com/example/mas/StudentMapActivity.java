package com.example.mas;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StudentMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "StudentMapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private double nypLat;
    private double nypLng;
    private double currentLat;
    private double currentLng;

    //widgets
    private ImageView mGps;
    private TextView msgTxt;

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_map);

        mGps = (ImageView) findViewById(R.id.ic_gps);
        msgTxt = (TextView) findViewById(R.id.msgTxt);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Google Map");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getLocationPermission();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void init(){
        Log.d(TAG, "init: initializing");

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });

        nypLocation();
    }

    private void nypLocation(){
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = "Nanyang Polytechnic";

        Geocoder geocoder = new Geocoder(StudentMapActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG, "nypLocation: IOException: " + e.getMessage() );
        }

        if(list.size() > 0){
            Address address = list.get(0);

            Log.d(TAG, "nypLocation: found a location: " + address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

            nypLat = address.getLatitude();
            nypLng = address.getLongitude();

            addMarker(new LatLng(address.getLatitude(), address.getLongitude()), address.getAddressLine(0), "RED");
        }
    }

    private void currentLocation(double currentLat, double currentLng){
        Geocoder geocoder = new Geocoder(StudentMapActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocation(currentLat, currentLng, 1);
        }catch (IOException e){
            Log.e(TAG, "currentLocation: IOException: " + e.getMessage());
        }

        if(list.size() > 0){
            Address address = list.get(0);

            Log.d(TAG, "currentLocation: found a location: " + address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

            addMarker(new LatLng(address.getLatitude(), address.getLongitude()), address.getAddressLine(0), "GREEN");
        }
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location cLocation = (Location) task.getResult();
                            currentLat = cLocation.getLatitude();
                            currentLng = cLocation.getLongitude();
                            currentLocation(currentLat, currentLng);

                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(new LatLng(currentLat, currentLng));
                            builder.include(new LatLng(nypLat, nypLng));
                            LatLngBounds bounds = builder.build();

                            float[] results = new float[1];
                            Location.distanceBetween(currentLat, currentLng, nypLat, nypLng, results);
                            if(results[0] >= 300) {
                                PolylineOptions line = new PolylineOptions().add(new LatLng(currentLat, currentLng),
                                        new LatLng(nypLat, nypLng)).width(5).color(Color.RED);
                                mMap.addPolyline(line);

                                mMap.addCircle(new CircleOptions()
                                        .center(new LatLng(nypLat, nypLng))
                                        .radius(300)
                                        .strokeColor(Color.RED)
                                        .fillColor(Color.TRANSPARENT));

                                msgTxt.setText("Your are not in NYP!\n" + "Distance: " + results[0] + "m");
                                msgTxt.setTextColor(Color.rgb(255,0,0));
                            }
                            else {
                                PolylineOptions line = new PolylineOptions().add(new LatLng(currentLat, currentLng),
                                        new LatLng(nypLat, nypLng)).width(5).color(Color.GREEN);
                                mMap.addPolyline(line);

                                mMap.addCircle(new CircleOptions()
                                        .center(new LatLng(nypLat, nypLng))
                                        .radius(300)
                                        .strokeColor(Color.GREEN)
                                        .fillColor(Color.TRANSPARENT));

                                msgTxt.setText("Your are in NYP!\n" + "Distance: " + results[0] + "m");
                                msgTxt.setTextColor(Color.rgb(0,255,0));
                            }

                            int width = getResources().getDisplayMetrics().widthPixels;
                            int padding = (int) (width * 0.15);

                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                            mMap.animateCamera(cu);

                            String Url = getRequestUrl();
                            requestDirectionTask rTask = new requestDirectionTask();
                            rTask.execute(Url);
                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(StudentMapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void addMarker(LatLng latLng, String title, String color){
        if(color.equals("RED")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mMap.addMarker(options);
        }
        else if(color.equals("GREEN")) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMap.addMarker(options);
        }
    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(StudentMapActivity.this);
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                //initialize our map
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private String getRequestUrl() {
        String str_org = "origin=" + currentLat + "," + currentLng;
        String str_dest = "destination=" + nypLat + "," + nypLng;
        String str_key = "key=AIzaSyCk3_MHia0cNjEGUVYbi8p0aOuNHWdmSl8";
        String param = str_org + "&" + str_dest + "&" + str_key;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        return url;
    }

    private String requestDirection(String reqUrl) {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            init();
        }
    }

    public class requestDirectionTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            parserTask pTask = new parserTask();
            pTask.execute(s);
        }
    }

    public class parserTask extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            ArrayList points = null;
            PolylineOptions polylineOptions =null;

            for(List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for(HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat, lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(10);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }

            if(polylineOptions != null) {
                mMap.addPolyline(polylineOptions);
            }
            else {
                Toast.makeText(getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
