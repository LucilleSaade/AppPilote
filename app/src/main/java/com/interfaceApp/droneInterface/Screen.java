package com.interfaceApp.droneInterface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.Camera.Parameters ;
import android.hardware.Camera.Size ;
import com.interfaceApp.FacadeInterface;

import com.communication.GPS.GPSTracker;
import com.communication.FacadeCom;

import com.interfaceApp.R;

import java.io.IOException;
import java.util.List;


/**
 * Created by lucille on 14/04/15.
 */
public class Screen extends Activity implements SurfaceHolder.Callback{


    private TextView console;
    private String modifyText;
    private FacadeInterface inter;
    private Handler handlerBat = new Handler();
    private Handler handlerLoc = new Handler();
    private boolean connected;
    private static FacadeCom com;
    private GPSTracker gps;
    private Camera camera = null;
    private Handler handlerCam = new Handler();


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


        SurfaceView surface = (SurfaceView)findViewById(R.id.surfaceView);

        SurfaceHolder holder = surface.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // On déclare que la classe actuelle gérera les callbacks
        holder.addCallback(this);

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
        handlerCam.removeCallbacks(takePictureTask);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if ( handlerBat != null )
            handlerBat.removeCallbacks(updateBatteryTask);
        handlerBat = null;

        this.gps.getLooper().quit();
        if ( handlerCam != null )
            handlerCam.removeCallbacks(takePictureTask);
        handlerBat = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
       /* if (this.connected) {
            super.onResume();
            handler.removeCallbacks(updateBatteryTask);
            handler.postDelayed(updateBatteryTask, 1000);
            System.out.println("fonction on Resume");
        }*/
        try{

            camera = Camera.open();

            Camera.Parameters params = camera.getParameters();
           // List<Camera.Size> lol = params.getSupportedPictureSizes();

        /*    for (int i=0;i<lol.size();i++){
                result = (Size) lol.get(i);
                Log.i("PictureSize", "Supported Size. Width: " + result.width + "height : " + result.height);
            }*/

           // camera.Size() lol2 = new camera.Size() ;

           // System.out.println(lol);
           /* params.setJpegQuality(5);
            params.setPictureSize(100,50);
           // camera.setParameters(params);

           /* handlerCam.removeCallbacks(takePictureTask);
            handlerCam.postDelayed(takePictureTask, 1600);*/

        }catch(Exception e){
            e.printStackTrace();
            // System.out.println("ouverture de la camera impossible");

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        handlerCam.removeCallbacks(takePictureTask);
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    private Runnable takePictureTask = new Runnable() {
        public void run() {
            if(camera!=null){
                takePicture();
                handlerCam.postDelayed(this, 1600);
            }

        }
    };

    private void takePicture() {
        // Sera lancée une fois l'image traitée, on l'envoie direct sans la transformer
        Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                camera.startPreview();
                com.sendPhoto(data);
            }
        };
        camera.takePicture(null, null, jpegCallback);
    }

    // Se déclenche quand la surface est créée
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Se déclenche quand la surface change de dimensions ou de format
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    // Se déclenche quand la surface est détruite
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(camera!=null){
            camera.stopPreview();
        }
    }

    public void lancerPhoto(){
        handlerCam.removeCallbacks(takePictureTask);
        handlerCam.postDelayed(takePictureTask, 1600);
    }

    public void arretPhoto(){
        handlerCam.removeCallbacks(takePictureTask);
        camera.stopPreview();
    }

    public static FacadeCom getCom() {
        return com;
    }
}
