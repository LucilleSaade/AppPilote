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

import com.communication.GPSTracker;
import com.communication.FacadeCom;

import com.interfaceApp.R;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Created by lucille on 14/04/15.
 */
public class Screen extends Activity {


    private TextView console;
    private String modifyText;
    private FacadeInterface inter;
    private Handler handler = new Handler();
    private FacadeCom com;
    private GPSTracker gps;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drone);

        this.com = FacadeCom.getSingleton();

        gps = new GPSTracker(this);

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

        updateBattery();

        handler.removeCallbacks(updateBatteryTask);
        handler.postDelayed(updateBatteryTask, 1000);

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

    private Runnable updateBatteryTask = new Runnable() {
        public void run() {
            updateBattery();
            handler.postDelayed(this, 1000);
        }
    };

    private void updateBattery() {
            Context context = getApplicationContext();
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            float batteryPct = level / (float)scale;
            inter.sendBattery(batteryPct);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(updateBatteryTask);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.removeCallbacks(updateBatteryTask);
        handler.postDelayed(updateBatteryTask, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if ( handler != null )
            handler.removeCallbacks(updateBatteryTask);
        handler = null;
    }
}
