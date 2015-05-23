package com.communication.GPS;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.communication.FacadeCom;


/**
 * Created by lucille on 18/04/15.
 */
public class GPSTracker extends Thread implements LocationListener {

    private final Context mContext;

    private FacadeCom com;

    /**
     * boolean isGPSEnabled : flag for GPS status
     */
    boolean isGPSEnabled = false;

    /** 
     *  boolean isNetworkEnabled : flag for network status
     */
    boolean isNetworkEnabled = false;

    /** 
     * boolean canGetLocation : flag for GPS status
     */
    boolean canGetLocation = false;

    /**
     * Location location : location du device
     */
    Location location;
    
    /**
     * double latitude : latitude du device
     */
    double latitude;
    
    /**
     *  double longitude : longitude
     */
    double longitude;

    /**
     * static final long MIN_DISTANCE_CHANGE_FOR_UPDATES : The minimum distance to change Updates in meters
     */
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    /**
     * static final long MIN_TIME_BW_UPDATES : The minimum time between updates in milliseconds
     */
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    /**
     * LocationManager locationManager : Declaring a Location Manager
     */
    protected LocationManager locationManager;

    private Looper looper;

    /**
     * Constructeur de la classe GPSTracker
     */
    public GPSTracker(Context context) {
        this.mContext = context;
        this.com=FacadeCom.getSingleton();
        Log.d("Lucille","Service GPS lance");
        this.com.printDrone("Yeah GPS service launch");
        Log.d("Lucille","Service GPS ecriture");


    }


    /**
     * run du thread GPSTracker, regarde périodiquement (1s) la localisation du device
     */
    public void run() {

        this.looper.prepare();

        getLocation();

           try {
               currentThread().sleep(1000);
               onLocationChanged(this.location);

           } catch (Exception e) {
                e.printStackTrace();
           }

        this.looper.loop();

    }



    /**
     * Methode appelée au début de l'exécution du thread, initialise tous ce qui est necessaire pour trouver la localisation du device
     * et met à jour la localisation.
     */
    public Location getLocation() {

        try {
            locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    System.out.println("Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            this.com.setLat(latitude);
                            this.com.setLong(longitude);
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        System.out.println("GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                this.com.setLat(latitude);
                                this.com.setLong(longitude);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Arrète l'utilisation du GPS
     * Appeler cette methode va arrter l'utilisation du GPS
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    /**
     * Methode retournant la latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
            this.com.setLat(latitude);
        }

        // return latitude
        return latitude;
    }

    /**
     * Methode retournant la longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
            this.com.setLong(longitude);
        }

        // return longitude
        return longitude;
    }

    /**
     * Methode retournant le looper
     * */
    public Looper getLooper() {
        return looper;
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }


    /**
     * Methode appelée periodiquement par le thread du GPSTracker pour mettre a jour la localisation
     */
    @Override
    public void onLocationChanged(Location location) {
        this.longitude = location.getLongitude();
        this.latitude = location.getLatitude();
        this.com.setLat(latitude);
        this.com.setLong(longitude);
        System.out.println("LocationChangedGPS LAT: "+ this.latitude +" longi: "+ this.longitude);
        this.com.sendInfo();
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }


    public IBinder onBind(Intent arg0) {
        return null;
    }

}
