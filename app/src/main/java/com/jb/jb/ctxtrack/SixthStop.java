package com.jb.jb.ctxtrack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SixthStop extends Activity {

    //private TextView stopNumber;
    //private TextView userId;

    private Button backInDelranButton;
    private Button arrivedSixthStopButton;
    private Button departedSixthStopButton;
    private EditText enterTrailerEditText;
    private EditText notesEditText;
    private TextView truckTextview;
    private TextView trailerTextview;
    private CheckBox checkBox;
    private String a;
    private String b;
    private String c;
    //private String d;
    private TextView userIdSixthStop;
    private String intentUserId;

    //make JSONParser private???
    private JSONParser jsonParser = new JSONParser();

    // Progress Dialog
    private ProgressDialog pDialog;


    //GPS Variables
    //public static final String TAG = FirstStop.class.getSimpleName();
    //private LocationProvider mLocationProvider;

    private int arrivedClick = 0;
    private int arrivedClickCount;
    private int arrivedFirstClick = 0;
    private int arrivedWasClicked = 0;


    private String intentTrailerNumber;
    private String intentTruckNumber;
    private String intentNewTrailerNumber;
    //private String dispatchNotes;

    private static final String TAG_SUCCESS = "success";

    private static String server_url = "http://www.jabdata.com/ctxtrack/activity_main2.php";
    private static String server_url_2 = "http://www.jabdata.com/ctxtrack/activity_main2.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sixth_stop);

        arrivedClick = 0;
        arrivedFirstClick = 0;
        arrivedWasClicked = 0;


        backInDelranButton = (Button) findViewById(R.id.backInDelranButton);
        arrivedSixthStopButton = (Button) findViewById(R.id.arrivedToStop);
        departedSixthStopButton = (Button) findViewById(R.id.departedSixthStop);
        enterTrailerEditText = (EditText) findViewById(R.id.enterTrailerEditText);
        truckTextview = (TextView) findViewById(R.id.truckNumID);
        trailerTextview = (TextView) findViewById(R.id.trailerNumID);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        notesEditText = (EditText) findViewById(R.id.notes);
        userIdSixthStop = (TextView) findViewById(R.id.userIdStop6);

        Intent intent = getIntent();
        {
            a = intent.getStringExtra("intentTruckNumber");
            b = intent.getStringExtra("intentTrailerNumber");
            c = intent.getStringExtra("intentUserId");
            truckTextview.setText(a);
            trailerTextview.setText(b);
            userIdSixthStop.setText(c);
        }

        arrivedSixthStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                backInDelranButton.setEnabled(false);
                checkBox.setChecked(true);
                arrivedClick = 1;
                arrivedWasClicked = 1;
                arrivedClickCount = arrivedFirstClick++;

                if (arrivedClickCount == 0) {
                    new InfoBegin2().execute();
                    Toast.makeText(getApplicationContext(), R.string.arrival_time_submitted, Toast.LENGTH_LONG).show();
                }
                if (arrivedClickCount >= 1) {
                    Toast.makeText(getApplicationContext(), R.string.arrival_time_already_submitted, Toast.LENGTH_LONG).show();
                }
            }
        });

        departedSixthStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                arrivedSixthStopButton.setEnabled(false);

                if (arrivedWasClicked == 0 && notesEditText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.enter_arrival_time_into_notes, Toast.LENGTH_LONG).show();
                } else {
                    arrivedClick = 0;
                    if (arrivedWasClicked == 1){
                        Toast.makeText(getApplicationContext(), R.string.departure_time_submitted, Toast.LENGTH_LONG).show();
                        new InfoBegin2().execute();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.next_time_arrival, Toast.LENGTH_LONG).show();
                        new InfoBegin2().execute();
                    }
                }
            }
        });

        backInDelranButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), R.string.back_in_delran, Toast.LENGTH_LONG).show();
                arrivedClick = 2;
                new InfoBegin2().execute();
            }
        });
    }

    class InfoBegin2 extends AsyncTask<String, String, String> {

        // Showing Progress Dialog

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SixthStop.this);
            pDialog.setMessage("Updating Information...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            String dispatchNotes = notesEditText.getText().toString();
            intentTruckNumber = truckTextview.getText().toString();
            intentTrailerNumber = trailerTextview.getText().toString();
            intentNewTrailerNumber = enterTrailerEditText.getText().toString();
            intentUserId = userIdSixthStop.getText().toString();
            String userId2 = userIdSixthStop.getText().toString();
            String stopNumArrival = getResources().getString(R.string.stop6_arrival);
            String stopNumDeparture = getResources().getString(R.string.stop6_departure);
            String backToDelran = getResources().getString(R.string.arrived_back_to_delran);

            // Building Parameters ArrayList
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            if (intentNewTrailerNumber.isEmpty() || intentNewTrailerNumber.length() == 0 || intentNewTrailerNumber.equals("")) {
                params.add(new BasicNameValuePair("trailerNum", intentTrailerNumber));
            } else {
                params.add(new BasicNameValuePair("trailerNum", intentNewTrailerNumber));
            }
            if (arrivedClick == 1) {
                params.add(new BasicNameValuePair("stop", stopNumArrival));
            } else if (arrivedClick == 0){
                params.add(new BasicNameValuePair("stop", stopNumDeparture));
            } else if (arrivedClick == 2) {
                params.add(new BasicNameValuePair("stop", backToDelran));
            }

            params.add(new BasicNameValuePair("dispatchNotes", dispatchNotes));
            params.add(new BasicNameValuePair("userId2", userId2));

            // getting JSON Object - POST Method
            JSONObject json;
            if (arrivedClick == 1) {
                json = jsonParser.makeHttpRequest(server_url,
                        "POST", params);
            } else {
                //same URL right now but may break apart into multiple databases
                json = jsonParser.makeHttpRequest(server_url_2,
                        "POST", params);
            }
            // checking log cat for response
            Log.d("Create Response", json.toString());
            // checking for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {

                    if (arrivedClick == 0) {
                        Intent intent = new Intent(SixthStop.this, BackInDelran.class);

                        if (intentNewTrailerNumber.isEmpty() || intentNewTrailerNumber.length() == 0 || intentNewTrailerNumber.equals("")) {
                            intent.putExtra("intentTrailerNumber", intentTrailerNumber);
                        } else {
                            intent.putExtra("intentTrailerNumber", intentNewTrailerNumber);
                        }
                        intent.putExtra("intentTruckNumber", intentTruckNumber);
                        //intent.putExtra("intentNewTrailerNumber", intentNewTrailerNumber);
                        intent.putExtra("intentUserId", intentUserId);

                        startActivity(intent);

                        // closing screen
                        finish();
                    } else if (arrivedClick == 2) {

                        Intent intent = new Intent(SixthStop.this, BackInDelran.class);
                        if (intentNewTrailerNumber.isEmpty() || intentNewTrailerNumber.length() == 0 || intentNewTrailerNumber.equals("")) {
                            intent.putExtra("intentTrailerNumber", intentTrailerNumber);
                        } else {
                            intent.putExtra("intentTrailerNumber", intentNewTrailerNumber);
                        }
                        intent.putExtra("intentTruckNumber", intentTruckNumber);
                        intent.putExtra("intentUserId", intentUserId);
                        startActivity(intent);
                    }
                } else {
                    // failed
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        // After complete background task, dismiss progress dialog
        protected void onPostExecute(String file_url) {
            // dismiss the dialog upon completion
            pDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed()
    {


    }

}