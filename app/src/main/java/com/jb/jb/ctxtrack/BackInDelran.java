package com.jb.jb.ctxtrack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class BackInDelran extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    //make JSONParser private???
    JSONParser jsonParser = new JSONParser();
    private String intentTruckNumber;
    private String intentTrailerNumber;
    private String intentUserId;

    private String a;
    private String b;
    private String c;

    private TextView truckTextview;
    private TextView trailerTextview;
    private TextView userIdBackInDelran;
    private EditText truckNumberEditText;
    private EditText enterTrailerEditText;
    private Button endShiftButton;
    private Button submitAndContinueButton;
    private EditText notesEditText;

    private String intentTrailerNumber;
    private String intentTruckNumber;
    private String intentNewTrailerNumber;


    //HostGator
    //Need to create a new PHP file???
    private static String server_url = "http://www.jabdata.com/ctxtrack/activity_main2.php";
    private static String server_url_2 = "http://www.jabdata.com/ctxtrack/activity_main2.php";



    private static final String TAG_SUCCESS = "success";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_in_delran);


        truckTextview = (TextView) findViewById(R.id.truckNumID);
        trailerTextview = (TextView) findViewById(R.id.trailerNumID);
        userIdBackInDelran = (TextView) findViewById(R.id.userIdDelran);
        truckNumberEditText = (EditText) findViewById(R.id.truck_number_editText);
        enterTrailerEditText = (EditText) findViewById(R.id.trailer_editText);
        endShiftButton = (Button) findViewById(R.id.endShiftButton);
        submitAndContinueButton = (Button) findViewById(R.id.submitAndContinue);
        notesEditText = (EditText) findViewById(R.id.notesEditText);


        Intent intent = getIntent();
        {
            a = intent.getStringExtra("intentTruckNumber");
            b = intent.getStringExtra("intentTrailerNumber");
            c = intent.getStringExtra("intentUserId");
            truckTextview.setText(a);
            trailerTextview.setText(b);
            userIdBackInDelran.setText(c);
        }

        endShiftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add logout button code here
            }
        });

        submitAndContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add continue to next stop button code here
            }
        });



    }



    class InfoBegin2 extends AsyncTask<String, String, String> {

        // Showing Progress Dialog

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(BackInDelran.this);
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
            intentUserId = userIdBackInDelran.getText().toString();
            String userId2 = userIdBackInDelran.getText().toString();
            String stopNumArrival = getResources().getString(R.string.stop6_arrival);
            String stopNumDeparture = getResources().getString(R.string.departing_delran);
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
                        Intent intent = new Intent(BackInDelran.this, FirstStop.class);

                        if (intentNewTrailerNumber.isEmpty() || intentNewTrailerNumber.length() == 0 || intentNewTrailerNumber.equals("")) {
                            intent.putExtra("intentTrailerNumber", intentTrailerNumber);
                        } else {
                            intent.putExtra("intentTrailerNumber", intentNewTrailerNumber);
                        }
                        intent.putExtra("intentTruckNumber", intentTruckNumber);
                        intent.putExtra("intentNewTrailerNumber", intentNewTrailerNumber);
                        intent.putExtra("intentUserId", intentUserId);

                        startActivity(intent);

                        // closing screen
                        finish();
                    } else if (arrivedClick == 2) {

                        Intent intent = new Intent(BackInDelran.this, FirstStop.class);
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
}