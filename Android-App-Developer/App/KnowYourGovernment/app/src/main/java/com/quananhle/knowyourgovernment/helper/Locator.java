package com.quananhle.knowyourgovernment.helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.quananhle.knowyourgovernment.MainActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Locator {
    private static final String TAG = "Locator";
    private MainActivity owner;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private final int REQUEST_CODE = 5;
    private Criteria criteria;
    private String currentLatLon = "", currentLocation = "";

    //default constructor
    public Locator(MainActivity mainActivity) {
        this.owner = mainActivity;
        if (checkPermission()){
            Log.d("Setup Locator", "Step 1");
            setUpLocationManager();
            determineLocation();
        }
    }

    public boolean checkPermission(){
        if (ContextCompat.checkSelfPermission(owner, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(owner, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return false;
        }
        return true;
    }
    public void setUpLocationManager(){
        if (locationManager != null){
            return;
        }
        if (!checkPermission()){
            return;
        }
        locationManager = (LocationManager) owner.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Toast.makeText(owner, "Update from " + location.getProvider(), Toast.LENGTH_SHORT).show();
                Log.d("Get position", "" + location.getLatitude() + location.getLongitude());
                owner.setLocation(location.getLatitude(), location.getLongitude());
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {;}
            public void onProviderEnabled(String provider) {;}
            public void onProviderDisabled(String provider) {;}
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
    }
    public void determineLocation(){
        if (!checkPermission()){
            return;
        }
        if (locationManager == null){
            setUpLocationManager();
        }
        if (locationManager != null){
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null){
                owner.setLocation(location.getLatitude(), location.getLongitude());
                Toast.makeText(owner, "Using " + LocationManager.NETWORK_PROVIDER +
                        " Location Provider", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (locationManager != null){
            Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (location != null){
                owner.setLocation(location.getLatitude(), location.getLongitude());
                Toast.makeText(owner, "Using " + LocationManager.PASSIVE_PROVIDER +
                        " Location Provider", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (locationManager != null){
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null){
                owner.setLocation(location.getLatitude(), location.getLongitude());
                Toast.makeText(owner, "Using " + LocationManager.GPS_PROVIDER +
                        " Location Provider", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        return;
    }
    public void shutDown(){
        locationManager.removeUpdates(locationListener);
        locationListener = null;
    }

//    public void getCurrentLocation() {
//        String bestProvider = locationManager.getBestProvider(criteria, true);
//        @SuppressLint("MissingPermission") Location currentLocation = locationManager.getLastKnownLocation(bestProvider);
//        if (currentLocation != null) {
//            currentLatLon = String.format(Locale.getDefault(),  "%.4f, %.4f",
//                    currentLocation.getLatitude(), currentLocation.getLongitude());
//            locationView.setText(currentLatLon);
//        }
//        else {
//            locationView.setText("Location Can't Be Found!");
//        }
//    }
//    public void setLocation() {
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        criteria = new Criteria();
//        criteria.setPowerRequirement(Criteria.POWER_HIGH);
//        criteria.setAccuracy(Criteria.ACCURACY_FINE);
//        criteria.setAltitudeRequired(false);
//        criteria.setBearingRequired(false);
//        criteria.setSpeedRequired(false);
//        if(ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 329);
//        }
//        else {
//            getCurrentLocation();
//        }
//    }
//    public void getLatLon() {
//        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//        try {
//            List<Address> addresses;
//            if (!currentLatLon.trim().isEmpty()) {
//                String[] latLon = currentLatLon.split(",");
//                double lat = Double.parseDouble(latLon[0]);
//                double lon = Double.parseDouble(latLon[1]);
//                addresses = geocoder.getFromLocation(lat, lon, 1);
//                if(!addresses.get(0).getPostalCode().equals("")) {
//                    currentLocation = addresses.get(0).getPostalCode();
//                }
//                else if(!addresses.get(0).getLocality().equals("")) {
//                    currentLocation = addresses.get(0).getLocality();
//                }
//                Log.d(TAG, "getLatLon: addresses: " + addresses.get(0).getPostalCode());
//                Toast.makeText(this, "Location Found: " + addresses.get(0).getLocality(), Toast.LENGTH_SHORT).show();
//            }
//
//        }
//        catch (IOException e) {
//            Log.d(TAG, "convertLatLon: " + e);
//        }
//    }
//    public void getCurrentLocationOnCreate(){
//        setLocation();
//        getLatLon();
//        getCurrentLocation();
//        if (!currentLatLon.equals("")) {
//            if(isConnected()) {
//                doRunnable(currentLocation);
//            }
//            else {
//                showMessage(ERROR_ICON,
//                        "NO NETWORK CONNECTION",
//                        "Data cannot be accessed/loaded without an Internet connection");
//            }
//        }
//    }

}