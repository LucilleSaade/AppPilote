package com.message;

/**
 * Created by lucille on 24/03/15.
 */
public class Informations extends AbstractMessage{
    //Mettre les objets de position,

    private double latitude;
    private double longitude;
    private float battery_level;

    public Informations(double lat, double longi, float bat){
        this.type=typeContenu.INFORMATIONS;
        this.latitude = lat;
        this.longitude = longi;
        this.battery_level = bat;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setBattery_level(float battery_level) {
        this.battery_level = battery_level;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getBattery_level() {
        return battery_level;
    }
}


