package com.jb.jb.ctxtrack;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class StartScreen extends Activity {

    Button submitButton;
    EditText usernameEditText;
    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        submitButton = (Button) findViewById(R.id.submit_button);
        usernameEditText = (EditText) findViewById(R.id.insertUsernameEditText);
        passwordEditText = (EditText) findViewById(R.id.insertPasswordEditText);

    }



}
