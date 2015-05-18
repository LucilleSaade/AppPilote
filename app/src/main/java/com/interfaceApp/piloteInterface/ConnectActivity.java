package com.interfaceApp.piloteInterface;

import android.support.v4.app.FragmentActivity;
import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import android.widget.Button;
import android.view.Menu;
import android.content.Intent;

import com.interfaceApp.FacadeInterface;
import com.interfaceApp.R;

public class ConnectActivity extends Activity {

    private Button btn ;
    private TextView t1 ;
    private FacadeInterface inter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        btn = (Button) findViewById(R.id.button);
        t1 = (TextView) findViewById(R.id.textView2);
        this.inter = FacadeInterface.getInstance(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // ConnectActivity.this.inter.demandeConnect();
                Intent intent = new Intent(ConnectActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_connect, menu);
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


}
