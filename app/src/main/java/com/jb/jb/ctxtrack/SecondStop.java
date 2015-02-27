package com.jb.jb.ctxtrack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SecondStop extends Activity {

    Button backInDelranButton;
    Button endShiftButton;
    Button arrivedSecondStopButton;
    Button departedSecondStopButton;
    EditText enterTrailerEditText;
    TextView truckTextview;
    TextView trailerTextview;
    private String a;
    private String b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_stop);
        backInDelranButton = (Button) findViewById(R.id.backInDelranButton);
        endShiftButton = (Button) findViewById(R.id.endShiftButton);
        arrivedSecondStopButton = (Button) findViewById(R.id.arrivedSecondStop);
        departedSecondStopButton = (Button) findViewById(R.id.departedSecondStop);
        enterTrailerEditText = (EditText) findViewById(R.id.enterTrailerEditText);
        truckTextview = (TextView) findViewById(R.id.truckNumID);
        trailerTextview = (TextView) findViewById(R.id.trailerNumID);



        Intent intent = getIntent();
        if(intent != null) {
            a = intent.getStringExtra("intentTrailerNumber");
            b = intent.getStringExtra("intentTruckNumber");
            trailerTextview.setText(a);
            truckTextview.setText(b);
        }
         //Intent iin= getIntent();
//        Bundle b = iin.getExtras();
//
//        if(b!=null)
//        {
//            String j =(String) b.get("trailerNumber");
//            mainTextview.setText(j);
//        }

        arrivedSecondStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), R.string.arrival_time_submitted, Toast.LENGTH_LONG).show();



            }
        });

        departedSecondStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), R.string.departure_time_submitted, Toast.LENGTH_LONG).show();



            }
        });

        backInDelranButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), R.string.arrival_time_submitted, Toast.LENGTH_LONG).show();



            }
        });

        endShiftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), R.string.ended_shift_submitted, Toast.LENGTH_LONG).show();



            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.second_stop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
