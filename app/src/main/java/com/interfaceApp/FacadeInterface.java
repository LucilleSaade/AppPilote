package com.interfaceApp;

import android.app.Activity;
import android.content.Intent;

import com.interfaceApp.droneInterface.Screen;

/**
 * Created by lucille on 14/04/15.
 */
public class FacadeInterface {

    private static FacadeInterface singleton ;
    private Screen screen;
    private Activity firstActivity;


    private FacadeInterface(Activity activity) {
        firstActivity = activity;
    }


    /**
     * permet de changer d'activity
     * @param activity
     */
    public void changeActivity(Class activity) {
        Intent i = new Intent(firstActivity, activity);
        firstActivity.startActivityForResult(i, 1);
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
}
