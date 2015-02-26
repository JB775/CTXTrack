package com.jb.jb.ctxtrack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FirstStop extends Activity {

    Button arrivedFirstStop;
    Button departedFirstStop;
    EditText enteredTrailer;

    private String trailerNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_stop);
        arrivedFirstStop = (Button) findViewById(R.id.arrivedSecondStop);
        departedFirstStop = (Button) findViewById(R.id.departedSecondStop);
        enteredTrailer = (EditText) findViewById(R.id.enterTrailerEditText);

        arrivedFirstStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trailerNumber = enteredTrailer.getText().toString();
                Toast.makeText(getApplicationContext(), trailerNumber, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(FirstStop.this, SecondStop.class);
                //EditText editText = (EditText) findViewById(R.id.edit_message);
                //String message = enteredTrailer.getText().toString();

                intent.putExtra(trailerNumber, trailerNumber);
                startActivity(intent);


            }
        });

        departedFirstStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //startService(new Intent(this, AndroidLocationService.class));


    }



}
