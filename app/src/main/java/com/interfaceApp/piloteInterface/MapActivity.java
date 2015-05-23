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

/**
 * Classe MapActivity, de type FragmentActivity pour pouvoir utiliser une GoogleMaps.
 * ATTRIBUTS :
 * 8 boutons;
 * moving : indique le mode de la caméra de la map (déplacement ou statique);
 * mMap: correspond à la Map, de type GoogleMap de l'API ;
 * COORDONNEES: contient les coordonnées envoyées par le drone,
 * de type LatLng qui appartient à l'API,
 * attribut public pour pouvoir y accéder depuis la classe FacadeInterface;
 * handler : permet de gérer le thread périodique updateMarkerTask.
 */
public class MapActivity extends FragmentActivity {

    Button btn1 ;
    Button btn2 ;
    Button btn3 ;
    Button btn4 ;
    Button btn5 ;
    Button btn6 ;
    Button btn7 ;
    Button btn8 ;
    int moving = 0;

    private GoogleMap mMap;
    static final LatLng TOULOUSE = new LatLng(43.604, 1.446);
    public static LatLng COORDONNEES ;
    private Handler handler = new Handler();


    /**
     * OnCreate() : Création de l'activité, quand l'utilisateur y vient pour la première fois.
     * @param savedInstanceState
     * Création des 8 boutons ( menu + boutons pour la map)
     * Association à chaque bouton de sa fonction
     *
     */
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
        btn8 = (Button) findViewById(R.id.button8);



        // Bouton Home --> retourne sur l'activité home
        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Bouton Map --> ne fait rien
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Bouton Image --> passe sur l'activité Image
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, ImageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Bouton Quit --> retourne sur l'activité Connect
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, ConnectActivity.class);
                startActivity(intent);
                finish();
            }
        });

    /** BOUTONS ASSOCIES A LA MAP **/

        /**
         * Bouton Satellite --> carte en mode satellite
         * grâce à la méthode setMapType de l'API
         */
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });

        /**
         * Bouton Terrain--> carte en mode terrain
         * grâce à la méthode setMapType de l'API
         */
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
        });

        /**
         * Bouton Normal--> carte en mode normal
         * grâce à la méthode setMapType de l'API
         */
        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });

        /**
         * Bouton Moving --> permet d'activer le mouvement de la caméra de la map.
         * Moving = 0 --> la caméra bouge
         * Moving = 1 --> la caméra est à l'arrêt
         * l'appuie sur ce bouton permet de passer d'un mode à l'autre.
         */
        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( moving == 0) {
                    moving = 1;
                    btn8.setText("Start Moving");
                }
                else{
                    moving = 0;
                    btn8.setText("Stop Moving");
                }
            }
        });


        /**
         * Lancement du thread updateMarkerTask, et instanciation de la période : 1000ms
         * grâce à postDelayed. Auparavant on supprime les tâches updateMarkerTask
         * qui pourraient déjà être lancées, avec la méthode removeCallbacks.
         */

        handler.removeCallbacks(updateMarkerTask);
        handler.postDelayed(updateMarkerTask, 1000);


    }

    /**
     * Methode appelée pour récupérer la map depuis internet, grâce à des fonctions de l'API
     */
    private void setUpMapIfNeeded() {
        // On vérifie que mMap est null et n'a ps déjà été instancié
        if (mMap == null) {
            // Essaie d'obtenir la map de SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        }
    }

    /**
     * thread updateMarkerTask
     * utilise la fonction addMarker de l'API googleMaps pour positionner le marker sur mMap
     * puis, si le mode moving est activé, la caméra de la maps se positionne sur la
     * coordonnée récupérée dans l'attribut COORDONNEES, avec un zoom de coefficient 15.
     */
   private Runnable updateMarkerTask = new Runnable() {
        public void run() {

            mMap.addMarker(new MarkerOptions().position(COORDONNEES).title("Marker"));
            if(moving == 0) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(COORDONNEES, 15));
            }
            handler.postDelayed(this, 1000);
        }
    };

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

    /**
     * onStop(): Quand l'utilisateur sort de l'activité
     * Arrêt de la tâche updteMarkerTask lorsqu'on sort de l'activité Map,
     * avec la méthode removeCallbacks.
     */
    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(updateMarkerTask);

    }

    /**
     * onResume() : Quand l'utilisateur revient sur l'activité,
     * on relance la tâche updateMarkerTask grâce à postDelayed
     */
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        handler.removeCallbacks(updateMarkerTask);
        handler.postDelayed(updateMarkerTask, 1000);
    }

    /**
     * OnDestroy() : Destruction de l'activité.
     * Arrêt de la tâche updteMarkerTask lorsque l'activité Map est détruite.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if ( handler != null )
            handler.removeCallbacks(updateMarkerTask);
        handler = null;
    }
}
