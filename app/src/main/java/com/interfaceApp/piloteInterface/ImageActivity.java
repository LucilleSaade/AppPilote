package com.interfaceApp.piloteInterface;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.interfaceApp.FacadeInterface;
import com.interfaceApp.R;

public class ImageActivity extends Activity {

    Button btn1 ;
    Button btn2 ;
    Button btn3 ;
    Button btn4 ;
    private ImageView imageView;
    private FacadeInterface inter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        this.inter = FacadeInterface.getInstance(this);

        btn1 = (Button) findViewById(R.id.button);
        btn2 = (Button) findViewById(R.id.button2);
        btn3 = (Button) findViewById(R.id.button3);
        btn4 = (Button) findViewById(R.id.button4);
        imageView = (ImageView) findViewById(R.id.imageView);

        inter.setCurrentActivity(this);

        //Tous les boutons créent l'envoi d'un FinPhoto avant de lancer une autre application
        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                inter.sendFinPhotos();
                //Intent intent = new Intent(ImageActivity.this, HomeActivity.class);
                //startActivity(intent);
                inter.changeActivity(HomeActivity.class);
                finish();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inter.sendFinPhotos();
               // Intent intent = new Intent(ImageActivity.this, MapActivity.class);
                //startActivity(intent);
                inter.changeActivity(MapActivity.class);
                finish();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inter.sendFinPhotos();
                inter.demandeDeconnect();
                //Intent intent = new Intent(ImageActivity.this, ConnectActivity.class);
                //startActivity(intent);
                inter.changeActivity(ConnectActivity.class);
                finish();
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        this.inter.sendDepartPhotos();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image, menu);
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

    public void afficherImage(Bitmap image){
        System.out.println("ImageActivity : afficher image");
        imageView.setImageBitmap(image);
        //imageView.setImageBitmap(RotateBitmap(image,90));
    }

    public void afficherBluetoothRecu(){
        Toast.makeText(getApplicationContext(), "Personne en danger détectée !! ", Toast.LENGTH_LONG).show();

    }


}
