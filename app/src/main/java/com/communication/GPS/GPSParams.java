package com.communication.GPS;

import android.app.Activity;

import com.communication.FacadeCom;
import com.interfaceApp.droneInterface.Screen;

/**
 * Created by lucille on 15/05/15.
 */
public class GPSParams {

    /**
     * private Screen screen : instance de l'activité principale de l'application drone
     * private GPSTracker gps : instance de la classe
     * private FacadeCom com;
     * private boolean drone;
     **/
    private Screen screen;
    private GPSTracker gps;
    private FacadeCom com;
    private boolean drone;

    public GPSParams(Screen screen, GPSTracker gps, boolean drone, FacadeCom com) {
        this.screen = screen;
        this.gps = gps;
        this.drone = drone;
        this.com = com;
    }


    public GPSTracker getGps() {
        return gps;
    }

    public void setGps(GPSTracker gps) {
        this.gps = gps;
        //this.screen.setGps(gps);
    }

    public Screen getScreen() {
        return screen;
    }

    public FacadeCom getCom() {
        return com;
    }

    public boolean isDrone() {
        return drone;
    }
}
