package com.interfaceApp.droneInterface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.interfaceApp.FacadeInterface;

import com.communication.GPS.GPSTracker;
import com.communication.FacadeCom;

import com.interfaceApp.R;


/**
 * Created by lucille on 14/04/15.
 */
public class Screen extends Activity {


    private TextView console;
    private String modifyText;
    private FacadeInterface inter;
    private Handler handlerBat = new Handler();
    private Handler handlerLoc = new Handler();
    private boolean connected;
    private static FacadeCom com;
    private GPSTracker gps;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drone);

        this.com = FacadeCom.getSingleton();
        this.inter = FacadeInterface.getInstance(this);
        this.com.setScreen(this);
        this.inter.setDroneActivity(this);
        this.connected = false;

        console = (TextView) findViewById(R.id.Console);

        /*
        final ScheduledExecutorService scheduledPool = Executors.newSingleThreadScheduledExecutor();

        scheduledPool.scheduleWithFixedDelay(new Runnable() {

            @Override
            public void run() {
                Context context = getApplicationContext();
                IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = context.registerReceiver(null, ifilter);
                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

                float batteryPct = level / (float)scale;
                inter.sendBattery(batteryPct);
            }

        }, 0, 2, TimeUnit.SECONDS);
        */

       /* Test des fonctions
        onMessage("Youhou");
        onNewMessage("Salut");
        int i;

        for (i=0; i<100; i++)
            onNewMessage("Les petits chou");
       */


    }


    // Remplit le textView nomme console à chaque nouveau message
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

    private Runnable updateBatteryTask = new Runnable() {
        public void run() {
            updateBattery();
            handlerBat.postDelayed(this, 1000);
        }
    };

    private void updateBattery() {

        Context context = getApplicationContext();
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float)scale;

        this.com.setBattery(batteryPct);
        this.com.sendInfo();

    }

    public void onConnectedState () {

        this.connected = false;

        System.out.println("avant lancement gps");
        this.gps = new GPSTracker(this);
        this.gps.start();


 /*       updateLocation();
        handlerLoc.removeCallbacks(updateLocationTask);
        handlerLoc.postDelayed(updateLocationTask, 1000);
*/
        updateBattery();
        handlerBat.removeCallbacks(updateBatteryTask);
        handlerBat.postDelayed(updateBatteryTask, 1000);
    }


    private Runnable updateLocationTask = new Runnable() {
        public void run() {
            updateLocation();
            handlerLoc.postDelayed(this, 1000);
        }
    };

    private void updateLocation() {
        this.gps.getLocation();
    }


    @Override
    protected void onStop() {
        super.onStop();
        handlerLoc.removeCallbacks(updateBatteryTask);
    }

  /*  @Override
    protected void onResume() {
        if (this.connected) {
            super.onResume();
            handler.removeCallbacks(updateBatteryTask);
            handler.postDelayed(updateBatteryTask, 1000);
            System.out.println("fonction on Resume");
        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if ( handlerBat != null )
            handlerBat.removeCallbacks(updateBatteryTask);
        handlerBat = null;

        this.gps.getLooper().quit();
    }

    public static FacadeCom getCom() {
        return com;
    }
}
