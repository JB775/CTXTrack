package com.jb.jb.ctxtrack;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class FirstStop extends Activity {

    Button arrivedFirstStop;
    Button departedFirstStop;
    EditText enteredTrailer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_stop);
        arrivedFirstStop = (Button) findViewById(R.id.arrivedSecondStop);
        departedFirstStop = (Button) findViewById(R.id.departedSecondStop);
        enteredTrailer = (EditText) findViewById(R.id.enterTrailerEditText);

    }



}
