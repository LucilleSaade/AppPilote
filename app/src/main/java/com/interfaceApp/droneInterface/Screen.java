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
import com.interfaceApp.FacadeInterface;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import com.communication.GPS.GPSTracker;
import com.communication.FacadeCom;

import com.interfaceApp.R;

import java.io.IOException;


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
    private Handler handlerBlu = new Handler();
    private BluetoothAdapter bluetoothAdapter;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drone);

        this.com = FacadeCom.getSingleton();
        this.inter = FacadeInterface.getInstance(this);
        this.com.setScreen(this);
        this.inter.setDroneActivity(this);
        this.connected = false;
        inter.setCurrentActivity(this);

        console = (TextView) findViewById(R.id.Console);



        SurfaceView surface = (SurfaceView)findViewById(R.id.surfaceView);

        SurfaceHolder holder = surface.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // On déclare que la classe actuelle gérera les callbacks
        holder.addCallback(this);



        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null)
            Toast.makeText(getApplicationContext(), "Pas de Bluetooth",
                    Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplicationContext(), "Avec Bluetooth",
                    Toast.LENGTH_SHORT).show();
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

        updateBattery();
        handlerBat.removeCallbacks(updateBatteryTask);
        handlerBat.postDelayed(updateBatteryTask, 1000);

        handlerBlu.removeCallbacks(findBluetoothDevices);
        handlerBlu.postDelayed(findBluetoothDevices, 10000);
    }
    private Runnable findBluetoothDevices = new Runnable() {
        public void run() {
            findBluetoothDevices();
            handlerBlu.postDelayed(this, 10000);
        }
    };

    public void onDeconnectedState() {
        if ( handlerBat != null )
            handlerBat.removeCallbacks(updateBatteryTask);
        handlerBat = null;
        this.gps.interrupt();
    }

    public final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Toast.makeText(getApplicationContext(),"Personne en danger détectée !! ",Toast.LENGTH_LONG).show();
                com.sendBluetoothDetecte();
            }

        }
    };
    public void findBluetoothDevices(){

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothReceiver, filter);
        Toast.makeText(getApplicationContext(),"Discovery launched",Toast.LENGTH_LONG).show();

        bluetoothAdapter.startDiscovery();
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

        if ( handlerBlu != null )
            handlerBlu.removeCallbacks(takePictureTask);
        handlerBlu = null;
        bluetoothAdapter.cancelDiscovery();
        unregisterReceiver(bluetoothReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        try{

            camera = getCameraInstance();

            Camera.Parameters params = camera.getParameters();

            camera.setDisplayOrientation(90);
            params.setJpegQuality(5);
            camera.setParameters(params);


        }catch(Exception e){
            e.printStackTrace();
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
                camera.startPreview();
                handlerCam.postDelayed(this, 1500);
            }

        }
    };

    private void takePicture() {
        // Sera lancée une fois l'image traitée, on l'envoie direct sans la transformer
        Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                System.out.println("Envoi de photo (screen)");
                com.sendPhoto(data);
                System.out.println("photo envoyé (screen)");

            }
        };
        camera.takePicture(null, null, jpegCallback);
        //camera.stopPreview();
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
        handlerCam.postDelayed(takePictureTask, 1500);
    }

    public void arretPhoto(){
        handlerCam.removeCallbacks(takePictureTask);
        camera.stopPreview();
    }


    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    public static FacadeCom getCom() {
        return com;
    }
}
