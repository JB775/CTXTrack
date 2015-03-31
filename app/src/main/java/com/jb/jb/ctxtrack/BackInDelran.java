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


public class BackInDelran extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener,LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // Progress Dialog
    private ProgressDialog pDialog;

    private JSONParser jsonParser = new JSONParser();

    private String intentUserId;

    private String a;
    private String b;
    private String c;

    int arrivedClick;

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

    //GPS Variables
    private GoogleApiClient locationclient;
    private LocationRequest locationrequest;
    private double lat;
    private double long3;
    public static final String TAG = BackInDelran.class.getSimpleName();

    //HostGator
    private static String server_url = "http://www.jabdata.com/ctxtrack/activity_main2.php";
    private static String server_url_2 = "http://www.jabdata.com/ctxtrack/activity_main2.php";
    private static String server_location = "http://www.jabdata.com/ctxtrack/location.php";


    private static final String TAG_SUCCESS = "success";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_in_delran);

        int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resp == ConnectionResult.SUCCESS){
            locationclient =      new GoogleApiClient.Builder(BackInDelran.this)
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
                arrivedClick = 0;
                Toast.makeText(getApplicationContext(), R.string.logged_out, Toast.LENGTH_LONG).show();
                //  if (mGoogleApiClient != null)
                //     mGoogleApiClient.disconnect();
                new InfoBegin2().execute();
            }
        });

        submitAndContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add continue to next stop button code here
                arrivedClick = 1;
                Toast.makeText(getApplicationContext(), R.string.departure_time_submitted, Toast.LENGTH_LONG).show();

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
            locationrequest.setInterval(600*1000);
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
            String shiftEnded = getResources().getString(R.string.shift_ended);
            String departingDelran = getResources().getString(R.string.departing_delran);

            // Building Parameters ArrayList
            List<NameValuePair> params = new ArrayList<NameValuePair>();


            if (arrivedClick == 0) {
                params.add(new BasicNameValuePair("stop", shiftEnded));
                params.add(new BasicNameValuePair("trailerNum", ""));
            } else if (arrivedClick == 1){
                params.add(new BasicNameValuePair("stop", stopNumDeparture));
                if (intentNewTrailerNumber.isEmpty() || intentNewTrailerNumber.length() == 0 || intentNewTrailerNumber.equals("")) {
                    params.add(new BasicNameValuePair("trailerNum", intentTrailerNumber));
                } else {
                    params.add(new BasicNameValuePair("trailerNum", intentNewTrailerNumber));
                }
            }
            //else if (arrivedClick == 2) {
              //  params.add(new BasicNameValuePair("stop", backToDelran));
            //}

            params.add(new BasicNameValuePair("dispatchNotes", dispatchNotes));
            params.add(new BasicNameValuePair("userId2", userId2));

            // getting JSON Object - POST Method
            JSONObject json;
           // if (arrivedClick == 0) {
            //    json = jsonParser.makeHttpRequest(server_url,
           //             "POST", params);
           // } else {
                //same URL right now but may break apart into multiple databases
                json = jsonParser.makeHttpRequest(server_url_2,
                        "POST", params);
           // }
            // checking log cat for response
            Log.d("Create Response", json.toString());
            // checking for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {

                    if (arrivedClick == 1) {
                        Intent intent = new Intent(BackInDelran.this, FirstStop.class);

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
                    } else if (arrivedClick == 0) {

                        Intent intent = new Intent(BackInDelran.this, Login.class);
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

            String userId2 = userIdBackInDelran.getText().toString();
            String stopNumArrival = getResources().getString(R.string.arrived_back_to_delran);
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