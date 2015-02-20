package com.jb.jb.ctxtrack;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

public class SecondStop extends Activity {

    Button backInDelranButton;
    Button endShiftButton;
    Button arrivedSecondStopButton;
    Button departedSecondStopButton;
    EditText enterTrailerEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_stop);
        backInDelranButton = (Button) findViewById(R.id.backInDelranButton);
        endShiftButton = (Button) findViewById(R.id.endShiftButton);
        arrivedSecondStopButton = (Button) findViewById(R.id.arrivedSecondStop);
        departedSecondStopButton = (Button) findViewById(R.id.departedSecondStop);
        enterTrailerEditText = (EditText) findViewById(R.id.enterTrailerEditText);
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
