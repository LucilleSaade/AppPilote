package com.interfaceApp;

import android.app.Activity;
import android.content.Intent;

import com.communication.FacadeCom;
import com.interfaceApp.droneInterface.Screen;
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


    private FacadeInterface(Activity activity) {
        firstActivity = activity;
        this.droneActivity = null;
    }


    /**
     * permet de démarrer l'activité associé au bon user et démarre la facade com
     * @param activity
     */
    public void demarrageActivity(Class activity, typeUser user, boolean drone) {
        Intent i = new Intent(firstActivity, activity);
        firstActivity.startActivityForResult(i, 1);
        this.user = user;
        this.drone = drone;
        this.com = new FacadeCom(user, this, this.drone);
    }


    /**
     * Permet de recuperer l'instance de FacadeView
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

    public void processInfo(Informations info) {

    }

    /********************************
     *       PARTIE POUR DRONE      *
     ********************************/

    // A n'utiliser que dans le cas du drone !!!!!
    public void printTxt(String text) {
      //  if(this.drone)
            droneActivity.onNewMessage(text);
    }

    public void setDrone(Screen droneAct) {
        this.droneActivity = droneAct;
    }
}
