package com.interfaceApp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

/*import com.communication.Envoi.ComParams;
import com.communication.Envoi.UDPAsyncTask;*/
import com.communication.FacadeCom;
import com.google.android.gms.maps.model.LatLng;
import com.interfaceApp.droneInterface.Screen;
import com.interfaceApp.piloteInterface.HomeActivity;
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
    private double latitude ;
    private double longitude ;

    private Activity currentActivity;
    private Handler imageHandler = new Handler(Looper.getMainLooper());
    private Handler blueHandler = new Handler(Looper.getMainLooper());

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
        firstActivity.startActivity(i);
        System.out.println("CHANGE ACTIVITY : " + firstActivity);
    }

    public void setCurrentActivity(Activity currentActivity){
        this.currentActivity = currentActivity ;
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
        this.batteryLevel = info.getBattery_level() ;
        this.latitude = info.getLatitude();
        this.longitude = info.getLongitude();
    }

    /**
     * Méthode utilisée par le pilote qui récupère l'image envoyée par le drone.
     * @param data
     *
     */
    public void recupererPhoto(byte [] data){
        System.out.println("Facade Interface : Récupérer Photo");
        image = BitmapFactory.decodeByteArray(data, 0, data.length);
        System.out.println("Facade Interface : Récupérer Photo 2222222");
        System.out.println("FIRST ACTIVITY : " + currentActivity);

        imageHandler.post(new Runnable() {

            public void run() {
                if(currentActivity instanceof ImageActivity){
                    System.out.println("Facade Interface : Récupérer Photo DANS LE IFFFFFFFFF");
                    ((ImageActivity)currentActivity).afficherImage(image);
                }
            }
        });


        System.out.println("Facade Interface : Récupérer Photo 33333333");
    }

    public void processBluetoothDetecte(){
        imageHandler.post(new Runnable() {
            public void run() {
                if (currentActivity instanceof ImageActivity) {

                    ((ImageActivity) currentActivity).afficherBluetoothRecu();
                } else if (currentActivity instanceof MapActivity) {

                    ((MapActivity) currentActivity).afficherBluetoothRecu();

                } else if (currentActivity instanceof HomeActivity) {
                    ((HomeActivity) currentActivity).afficherBluetoothRecu();
                }
            }
        });
    }

    public void sendDepartPhotos(){
        this.com.sendDebutPhoto();
    }
    public void sendFinPhotos(){
        this.com.sendFinPhoto();
    }
    public float getBattery(){
        return this.batteryLevel;
    }
    public double getLatitude(){
        return this.latitude;
    }
    public double getLongitude(){
        return this.longitude;
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

    public void processDebutPhoto(){
        //Commencer le début de la prise de vue et de l'envoi de photos
       this.droneActivity.lancerPhoto();
    }

    public void processFinPhoto(){
        //Changer de mode et arrêter prévisualisation et envoi
        this.droneActivity.arretPhoto();
    }



}
