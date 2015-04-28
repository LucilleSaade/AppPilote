package com.interfaceApp.droneInterface;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.interfaceApp.FacadeInterface;
import com.interfaceApp.R;


/**
 * Created by lucille on 14/04/15.
 */
public class Screen extends Activity {


    private TextView console;
    private String modifyText;
    private FacadeInterface inter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drone);

        this.inter = FacadeInterface.getInstance(this);
        this.inter.setDrone(this);

        console = (TextView) findViewById(R.id.Console);

       /* Test des fonctions
        onMessage("Youhou");
        onNewMessage("Salut");
        int i;

        for (i=0; i<100; i++)
            onNewMessage("Les petits chou");
       */

    }


    // Remplit le textView nomme console à chaque nouveau message)
    public void onNewMessage(String msg) {

        modifyText = console.getText().toString() + "\n" + msg;
        console.setText(modifyText);
    }


    // Affiche un message ephémere en bas de l'ecran
    public void onMessage(String msg) {

        Context context = getApplicationContext();
        CharSequence text = (CharSequence) msg;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

    }

}
