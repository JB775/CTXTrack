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

public class FirstStop extends Activity {

    private String a;
    private String b;
    private String c;

    private Button arrivedFirstStop;
    private Button departedFirstStop;
    private EditText enteredTrailer;
    private TextView truckTextview;
    private TextView trailerTextview;
    private CheckBox checkBox;
    private EditText notesEditText;
    private TextView userIdStop1;
    private String intentUserId;
    private TextView stopNumber;
    private TextView userId;

    //make JSONParser private???
    JSONParser jsonParser = new JSONParser();

    // Progress Dialog
    private ProgressDialog pDialog;


    //GPS Variables
    public static final String TAG = FirstStop.class.getSimpleName();
    private LocationProvider mLocationProvider;

    private int arrivedClick = 0;
    private int arrivedClickCount;
    private int arrivedFirstClick = 0;
    private int arrivedWasClicked = 0;


    private String intentTrailerNumber;
    private String intentTruckNumber;
    private String intentNewTrailerNumber;
    private String dispatchNotes;

    private static final String TAG_SUCCESS = "success";

    //edit this to correct server address and update to correct php file
    //private static String url_create_product = "http://192.168.0.6:1337/ctxtrack/.php";
    //private static String url_create_product = "http://localhost/ctxtrack/.php";
    //delran
    private static String url_create_product = "http://www.jabdata.com/ctxtrack/activity_main2.php";
    private static String url_create_product2 = "http://www.jabdata.com/ctxtrack/activity_main2.php";
    //home
    //private static String url_create_product = "http://192.168.56.1:1337/ctxtrack/first_stop.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_stop);

        //do I even need arrivedClick??
        arrivedClick = 0;
        arrivedFirstClick = 0;
        arrivedWasClicked = 0;

        arrivedFirstStop = (Button) findViewById(R.id.arrivedToStop);
        departedFirstStop = (Button) findViewById(R.id.departedFirstStop);
        enteredTrailer = (EditText) findViewById(R.id.enterTrailerEditText);
        truckTextview = (TextView) findViewById(R.id.truckNumID);
        trailerTextview = (TextView) findViewById(R.id.trailerNumID);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        notesEditText = (EditText) findViewById(R.id.notes);
        userIdStop1 = (TextView) findViewById(R.id.userIdStop1);
        stopNumber = (TextView) findViewById(R.id.stopNumber);
        userId = (TextView) findViewById(R.id.userIdStop1);

        Intent intent = getIntent();
        {
            a = intent.getStringExtra("intentTruckNumber");
            b = intent.getStringExtra("intentTrailerNumber");
            c = intent.getStringExtra("intentUserId");
            truckTextview.setText(a);
            trailerTextview.setText(b);
            userIdStop1.setText(c);
        }

        //delete this if statement???
        if (intent != null)

            arrivedFirstStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                Time now = new Time();
//                now.setToNow();


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

        departedFirstStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                arrivedFirstStop.setEnabled(false);

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

//                Intent intent = new Intent(FirstStop.this, SecondStop.class);
//                intentTruckNumber = truckTextview.getText().toString();
//                intentTrailerNumber = trailerTextview.getText().toString();
//                //use trim();????
//                intentNewTrailerNumber = enteredTrailer.getText().toString();
//                intentUserId = userIdStop1.getText().toString();
//
//                //EditText editText = (EditText) findViewById(R.id.edit_message);
//                //String message = enteredTrailer.getText().toString();
//
//                intent.putExtra("intentTrailerNumber", intentTrailerNumber);
//                intent.putExtra("intentTruckNumber", intentTruckNumber);
//                intent.putExtra("intentNewTrailerNumber", intentNewTrailerNumber);
//                intent.putExtra("intentUserId", intentUserId);
//                startActivity(intent);
            }
        });

        //startService(new Intent(this, AndroidLocationService.class));


    }

//    @Override
//    public void handleNewLocation(Location location) {
//
//    }

    class InfoBegin2 extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(FirstStop.this);
            pDialog.setMessage("Updating Information...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         */
        protected String doInBackground(String... args) {

            String dispatchNotes = notesEditText.getText().toString();
            intentTruckNumber = truckTextview.getText().toString();
            intentTrailerNumber = trailerTextview.getText().toString();
            //use trim();????
            intentNewTrailerNumber = enteredTrailer.getText().toString();
            intentUserId = userIdStop1.getText().toString();
            String userId2 = userId.getText().toString();
            //String stopNumArrival = stopNumber.getText().toString();
            String stopNumArrival = getResources().getString(R.string.stop1_arrival);
            String stopNumDeparture = getResources().getString(R.string.stop1_departure);

// Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            if (intentNewTrailerNumber.isEmpty() || intentNewTrailerNumber.length() == 0 || intentNewTrailerNumber.equals("")) {
                params.add(new BasicNameValuePair("trailerNum", intentTrailerNumber));
            } else {
                params.add(new BasicNameValuePair("trailerNum", intentNewTrailerNumber));
            }
            if (arrivedClick == 1) {
                params.add(new BasicNameValuePair("stop", stopNumArrival));
            } else {
                params.add(new BasicNameValuePair("stop", stopNumDeparture));
            }

            params.add(new BasicNameValuePair("dispatchNotes", dispatchNotes));
            params.add(new BasicNameValuePair("userId2", userId2));

// getting JSON Object
// Note that create product url accepts POST method
            JSONObject json;
            //   if(checkBox.isChecked()) {
            if (arrivedClick == 1) {
                json = jsonParser.makeHttpRequest(url_create_product,
                        "POST", params);
            } else {
                json = jsonParser.makeHttpRequest(url_create_product2,
                        "POST", params);
            }
// check log cat fro response
            Log.d("Create Response", json.toString());
// check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
// successfully created product
                    //Intent i = new Intent(getApplicationContext(), FirstStop.class);
                    //startActivity(i);

//                    intentTruckNumber = truckNumber.getText().toString();
//                    intentTrailerNumber = trailerNumber.getText().toString();
//                    intentUserId = userId.getText().toString();

//                    Intent intent = new Intent(getApplicationContext(), SecondStop.class);
//                    intent.putExtra("intentTruckNumber", intentTruckNumber);
//                    intent.putExtra("intentUserId", intentUserId);
//                    intent.putExtra("intentTrailerNumber", intentTrailerNumber);
//                    startActivity(intent);
                    if (arrivedClick == 0) {
                        Intent intent = new Intent(FirstStop.this, SecondStop.class);


                        //EditText editText = (EditText) findViewById(R.id.edit_message);
                        //String message = enteredTrailer.getText().toString();

                        if (intentNewTrailerNumber.isEmpty() || intentNewTrailerNumber.length() == 0 || intentNewTrailerNumber.equals("")) {
                            intent.putExtra("intentTrailerNumber", intentTrailerNumber);
                        } else {
                            intent.putExtra("intentTrailerNumber", intentNewTrailerNumber);
                        }
                        intent.putExtra("intentTruckNumber", intentTruckNumber);
                        intent.putExtra("intentNewTrailerNumber", intentNewTrailerNumber);
                        intent.putExtra("intentUserId", intentUserId);

                        startActivity(intent);

// closing this screen
                        finish();
                    }
                } else {
// failed to create product
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * *
         */
        protected void onPostExecute(String file_url) {
// dismiss the dialog once done
            pDialog.dismiss();
        }
    }

}



//    @Override
//    protected void onResume() {
//        super.onResume();
//        mLocationProvider.connect();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mLocationProvider.disconnect();
//    }
//
//
//    @Override
//    public void handleNewLocation(Location location) {
//
//        Log.d(TAG, location.toString());
//
//        double currentLatitude = location.getLatitude();
//        String currentLat = String.valueOf(currentLatitude);
//        double currentLongitude = location.getLongitude();
//        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
//        mainTextView.setText(currentLat);
//
//
//    }




