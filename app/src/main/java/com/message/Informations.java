package com.message;

/**
 * Created by lucille on 24/03/15.
 */
public class Informations extends AbstractMessage{
    //Mettre les objets de position,

    private double latitude;
    private double longitude;

    public Informations(double lat, double longi){
        this.type=typeContenu.INFORMATIONS;
        this.latitude = lat;
        this.longitude = longi;
    }
}
