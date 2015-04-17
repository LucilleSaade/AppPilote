package com.interfaceApp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.interfaceApp.droneInterface.*;
import com.interfaceApp.piloteInterface.*;

/**
 * Created by lucille on 14/04/15.
 */
public class Accueil extends Activity {

    private FacadeInterface facade;
    private boolean drone;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        // Creation de la facade
        this.facade = FacadeInterface.getInstance(this);

    }

    public void toDrone(View v) {
        facade.demarrageActivity(Screen.class, typeUser.DRONE, true);
    }

    public void toPilote(View v) {
        facade.demarrageActivity(ConnectActivity.class, typeUser.PILOTE, false);
    }
}
