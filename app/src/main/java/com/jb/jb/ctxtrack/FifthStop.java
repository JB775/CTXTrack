package com.jb.jb.ctxtrack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FifthStop extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener,LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //private TextView stopNumber;
    //private TextView userId;

    private Button backInDelranButton;
    private Button arrivedFifthStopButton;
    private Button departedFifthStopButton;
    private EditText enterTrailerEditText;
    private EditText notesEditText;
    private TextView truckTextview;
    private TextView trailerTextview;
    private CheckBox checkBox;
    private String a;
    private String b;
    private String c;
    //private String d;
    private TextView userIdFifthStop;
    private String intentUserId;

    private JSONParser jsonParser = new JSONParser();

    // Progress Dialog
    private ProgressDialog pDialog;

    //GPS Variables
    private GoogleApiClient locationclient;
    private LocationRequest locationrequest;
    private double lat;
    private double long3;
    public static final String TAG = FifthStop.class.getSimpleName();

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
    private static String server_location = "http://www.jabdata.com/ctxtrack/location.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fifth_stop);

        int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resp == ConnectionResult.SUCCESS){
            locationclient =      new GoogleApiClient.Builder(FifthStop.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            locationclient.connect();
        }
        else{
            Toast.makeText(this, "Google Play Service Error " + resp, Toast.LENGTH_LONG).show();
        }

        arrivedClick = 0;
        arrivedFirstClick = 0;
        arrivedWasClicked = 0;


        backInDelranButton = (Button) findViewById(R.id.backInDelranButton);
        arrivedFifthStopButton = (Button) findViewById(R.id.arrivedToStop);
        departedFifthStopButton = (Button) findViewById(R.id.departedFifthStop);
        enterTrailerEditText = (EditText) findViewById(R.id.enterTrailerEditText);
        truckTextview = (TextView) findViewById(R.id.truckNumID);
        trailerTextview = (TextView) findViewById(R.id.trailerNumID);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        notesEditText = (EditText) findViewById(R.id.notes);
        userIdFifthStop = (TextView) findViewById(R.id.userIdStop5);

        Intent intent = getIntent();
        {
            a = intent.getStringExtra("intentTruckNumber");
            b = intent.getStringExtra("intentTrailerNumber");
            c = intent.getStringExtra("intentUserId");
            truckTextview.setText(a);
            trailerTextview.setText(b);
            userIdFifthStop.setText(c);
        }

        arrivedFifthStopButton.setOnClickListener(new View.OnClickListener() {
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

        departedFifthStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                arrivedFifthStopButton.setEnabled(false);

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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationclient != null)
            locationclient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(locationclient!=null && locationclient.isConnected()){
            locationrequest = LocationRequest.create();
            //location update frequency
            locationrequest.setInterval(60*1000);
            LocationServices.FusedLocationApi.requestLocationUpdates(locationclient, locationrequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            long3 = location.getLongitude();
            new InfoBegin3().execute();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    class InfoBegin2 extends AsyncTask<String, String, String> {

        // Showing Progress Dialog

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(FifthStop.this);
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
            intentUserId = userIdFifthStop.getText().toString();
            String userId2 = userIdFifthStop.getText().toString();
            String stopNumArrival = getResources().getString(R.string.stop5_arrival);
            String stopNumDeparture = getResources().getString(R.string.stop5_departure);
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
                        Intent intent = new Intent(FifthStop.this, SixthStop.class);

                        if (intentNewTrailerNumber.isEmpty() || intentNewTrailerNumber.length() == 0 || intentNewTrailerNumber.equals("")) {
                            intent.putExtra("intentTrailerNumber", intentTrailerNumber);
                        } else {
                            intent.putExtra("intentTrailerNumber", intentNewTrailerNumber);
                        }
                        intent.putExtra("intentTruckNumber", intentTruckNumber);
                        intent.putExtra("intentUserId", intentUserId);

                        startActivity(intent);

                        // closing screen
                        finish();
                    } else if (arrivedClick == 2) {

                        Intent intent = new Intent(FifthStop.this, BackInDelran.class);
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

    class InfoBegin3 extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {

            String userId2 = userIdFifthStop.getText().toString();
            String stopNumArrival = getResources().getString(R.string.stop5_arrival);
            String inTransit = getResources().getString(R.string.in_transit);
            String backToDelran = getResources().getString(R.string.arrived_back_to_delran);
            String lat2 = String.valueOf(lat);
            String long2 = String.valueOf(long3);

            // Building Parameters ArrayList
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("latitude", lat2));
            params.add(new BasicNameValuePair("longitude", long2));
            //params.add(new BasicNameValuePair("stop", "LOCATION"));
            if (arrivedClick == 0) {
                params.add(new BasicNameValuePair("stop", inTransit));
            } else if (arrivedClick == 1){
                params.add(new BasicNameValuePair("stop", stopNumArrival));
            } else if (arrivedClick == 2) {
                params.add(new BasicNameValuePair("stop", backToDelran));
            }
            params.add(new BasicNameValuePair("latLong", lat2+","+long2));
            params.add(new BasicNameValuePair("userId2", userId2));

            // getting JSON Object - POST Method
            JSONObject json;

            json = jsonParser.makeHttpRequest(server_location,
                    "POST", params);

            // checking log cat for response
            Log.d("Create Response", json.toString());
            // checking for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {

                } else {
                    // failed
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void onBackPressed()
    {


    }
}