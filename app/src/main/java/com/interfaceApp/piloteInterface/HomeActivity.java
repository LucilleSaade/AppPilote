package com.interfaceApp.piloteInterface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.interfaceApp.FacadeInterface;
import com.interfaceApp.R;

/**
 * Created by mohamedsqualli on 01/04/2015.
 */
public class HomeActivity extends Activity {

    Button btn1 ;
    Button btn2 ;
    Button btn3 ;
    Button btn4 ;
    FacadeInterface inter ;
    TextView t1;
    TextView batteryLevel ;
    int i =0;
    private Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_page);
        inter = FacadeInterface.getInstance(this);
        btn1 = (Button) findViewById(R.id.button);
        btn2 = (Button) findViewById(R.id.button2);
        btn3 = (Button) findViewById(R.id.button3);
        btn4 = (Button) findViewById(R.id.button4);
        t1 = (TextView) findViewById(R.id.textView3);

        batteryLevel = (TextView) findViewById(R.id.batteryLevel);

        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MapActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ImageActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ConnectActivity.class);
                startActivity(intent);
                finish();
            }
        });

        updateBattery();

        handler.removeCallbacks(updateBatteryTask);
        handler.postDelayed(updateBatteryTask, 1000);

    }

    private Runnable updateBatteryTask = new Runnable() {
        public void run() {
            updateBattery();
            handler.postDelayed(this, 1000);
        }
    };

    private void updateBattery() {
        if (i==0){
            Context context = getApplicationContext();
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            float batteryPct = level / (float)scale;
            String mytext = Float.toString(batteryPct*100);
            batteryLevel.setText(mytext + " %");
            i=1;
        }else{
            batteryLevel.setText("Ca marche !!");
            i=0;
        }
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
