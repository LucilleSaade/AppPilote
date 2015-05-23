package com.interfaceApp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.communication.FacadeCom;
import com.google.android.gms.maps.model.LatLng;
import com.interfaceApp.droneInterface.Screen;
import com.interfaceApp.piloteInterface.ImageActivity;
import com.interfaceApp.piloteInterface.MapActivity;
import com.message.Informations;

/**
 * Created by lucille on 14/04/15.
 */
public class FacadeInterface {

    private static FacadeInterface singleton ;
    private Screen droneActivity;
    private Activity firstActivity;
    private FacadeCom com;
    private typeUser user;
    private boolean drone;
    private float batteryLevel ;
    private Bitmap image;


    private FacadeInterface(Activity activity) {
        firstActivity = activity;
        this.droneActivity = null;
    }


    /**
     * permet de démarrer l'activité associé au bon user et démarre la facade com
     * @param activity
     */
    public void demarrageActivity(Class activity, typeUser user, boolean drone) {
        this.user = user;
        this.drone = drone;
       // if(drone) {
            this.com = FacadeCom.getInstance(user, this, this.drone);
        //}
        Intent i = new Intent(firstActivity, activity);
        firstActivity.startActivityForResult(i, 1);
    }


    /**
     * Permet de recuperer l'instance de FacadeInterface
     * @return singleton
     */
    public static FacadeInterface getInstance (Activity activity) {
        if (singleton == null) {
            singleton = new FacadeInterface(activity) ;
        }
        return singleton ;
    }


    /********************************
     *      PARTIE POUR PILOTE      *
     ********************************/

    public void changeActivity(Class activity) {
        Intent i = new Intent(firstActivity, activity);
        firstActivity.startActivityForResult(i, 1);
    }


    public void demandeConnect() {
        this.com.demandeConnect();
    }

    public void demandeDeconnect() {
        this.com.demandeDeconnect();
    }

    /**
     * Méthode utilisée par le pilote qui récupère l'information envoyée par le drone.
     * @param info
     *              La nouvelle info envoyée par le drone.
     * Ecrit dans l'attribut COORDONNEES de l'activité Map la nouvelle coordonnée contenue dans info.
     *
     */
    public void processInfo(Informations info) {

        final LatLng COORD = new LatLng( info.getLatitude(), info.getLongitude());
        MapActivity.COORDONNEES = COORD;

    }

    /**
     * Méthode utilisée par le pilote qui récupère l'image envoyée par le drone.
     * @param data
     *
     */
    public void recupererPhoto(byte [] data){
        image = BitmapFactory.decodeByteArray(data, 0, data.length);
        if(firstActivity instanceof ImageActivity){
            ((ImageActivity)firstActivity).afficherImage(image);
        }

    }

    public void sendDepartPhotos(){
        this.com.sendDebutPhoto();
    }
    public void sendFinPhotos(){
        this.com.sendFinPhoto();
    }

    public void receiveBattery(float batteryLevel){
        this.batteryLevel = batteryLevel ;
    }
    /********************************
     *       PARTIE POUR DRONE      *
     ********************************/

    // A n'utiliser que dans le cas du drone !!!!!
    public void printTxt(String text) {
        if(this.drone)
            droneActivity.onNewMessage(text);
    }

    public void setDrone(Screen droneAct) {
        this.droneActivity = droneAct;
    }

    public void setDroneActivity(Screen droneActivity) {
        this.droneActivity = droneActivity;
    }

    public void sendBattery(float batteryLevel){

    }

    public void processDebutPhoto(){
        //Commencer le début de la prise de vue et de l'envoi de photos
       this.droneActivity.lancerPhoto();
    }

    public void processFinPhoto(){
        //Changer de mode et arrêter prévisualisation et envoi
        this.droneActivity.arretPhoto();
    }
}
