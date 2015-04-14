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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        // Creation de la facade
        this.facade = FacadeInterface.getInstance(this);

    }

    public void toDrone(View v) {
        facade.changeActivity(Screen.class);
    }

    public void toPilote(View v) {
        facade.changeActivity(ConnectActivity.class);
    }
}
