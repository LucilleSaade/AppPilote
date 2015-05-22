package com.interfaceApp.piloteInterface;

import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;

import com.interfaceApp.R;

public class MapActivity extends FragmentActivity {

    Button btn1 ;
    Button btn2 ;
    Button btn3 ;
    Button btn4 ;
    Button btn5 ;
    Button btn6 ;
    Button btn7 ;
    int i = 0;

    private GoogleMap mMap;
    static final LatLng TOULOUSE = new LatLng(43.604, 1.446);
    public static LatLng COORDINATES ;
    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        setUpMapIfNeeded();
        btn1 = (Button) findViewById(R.id.button);
        btn2 = (Button) findViewById(R.id.button2);
        btn3 = (Button) findViewById(R.id.button3);
        btn4 = (Button) findViewById(R.id.button4);
        btn5 = (Button) findViewById(R.id.button5);
        btn6 = (Button) findViewById(R.id.button6);
        btn7 = (Button) findViewById(R.id.button7);




        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, ImageActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, ConnectActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
        });
        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });



        handler.removeCallbacks(updateMarkerTask);
        handler.postDelayed(updateMarkerTask, 1000);


    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            /* Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap(TOULOUSE);
            }*/
        }
    }

   private Runnable updateMarkerTask = new Runnable() {
        public void run() {

            LatLng TEST = COORDINATES;
            /*if(i ==0){
                TEST = COORDINATES;
                i = 1;
            }
            else {
                TEST=TOULOUSE;
                i = 0;
            }*/
            mMap.addMarker(new MarkerOptions().position(TEST).title("Marker"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(TEST,18));
            handler.postDelayed(this, 5000);
        }
    };

    //public LatLng getCoordinates(LatLng COORD){ return COORD ;}

    public void setUpMap() {
        int i = 0;
        if(i ==0) {
            mMap.addMarker(new MarkerOptions().position(COORDINATES).title("Marker"));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(updateMarkerTask);

    }

    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        handler.removeCallbacks(updateMarkerTask);
        handler.postDelayed(updateMarkerTask, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if ( handler != null )
            handler.removeCallbacks(updateMarkerTask);
        handler = null;
    }
}
