package com.jb.jb.ctxtrack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

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
    private TextView mainTextView;

    //make JSONParser private???
    JSONParser jsonParser = new JSONParser();

    // Progress Dialog
    private ProgressDialog pDialog;


    //GPS Variables
    public static final String TAG = FirstStop.class.getSimpleName();
    private LocationProvider mLocationProvider;


    private String intentTrailerNumber;
    private String intentTruckNumber;
    private String intentNewTrailerNumber;

    private String newTrailerNumber;
    private String dispatchNotes;

    private static final String TAG_SUCCESS = "success";

    //edit this to correct server address and update to correct php file
    //private static String url_create_product = "http://192.168.0.6:1337/ctxtrack/.php";
    //private static String url_create_product = "http://localhost/ctxtrack/.php";
    //delran
    private static String url_create_product = "http://192.168.56.101/ctxtrack/.php";
    //home
    //private static String url_create_product = "http://192.168.56.1:1337/ctxtrack/first_stop.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_stop);

        arrivedFirstStop = (Button) findViewById(R.id.arrivedFirstStop);
        departedFirstStop = (Button) findViewById(R.id.departedFirstStop);
        enteredTrailer = (EditText) findViewById(R.id.enterTrailerEditText);
        truckTextview = (TextView) findViewById(R.id.truckNumID);
        trailerTextview = (TextView) findViewById(R.id.trailerNumID);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        notesEditText = (EditText) findViewById(R.id.notes);
        userIdStop1 = (TextView) findViewById(R.id.userIdStop1);
        mainTextView = (TextView) findViewById(R.id.stop6);

        Intent intent = getIntent();{
            a = intent.getStringExtra("intentTruckNumber");
            b = intent.getStringExtra("intentTrailerNumber");
            c = intent.getStringExtra("intentUserId");
            truckTextview.setText(a);
            trailerTextview.setText(b);
            userIdStop1.setText(c);
        }
        if(intent != null)

        arrivedFirstStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Time now = new Time();
                now.setToNow();

                Toast.makeText(getApplicationContext(), now.toString(), Toast.LENGTH_LONG).show();
                checkBox.setChecked(true);


            }
        });

        departedFirstStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Toast.makeText(getApplicationContext(), R.string.departure_time_submitted, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(FirstStop.this, SecondStop.class);
                intentTruckNumber = truckTextview.getText().toString();
                intentTrailerNumber = trailerTextview.getText().toString();
                //use trim();????
                intentNewTrailerNumber = enteredTrailer.getText().toString();
                intentUserId = userIdStop1.getText().toString();

                //EditText editText = (EditText) findViewById(R.id.edit_message);
                //String message = enteredTrailer.getText().toString();

                intent.putExtra("intentTrailerNumber", intentTrailerNumber);
                intent.putExtra("intentTruckNumber", intentTruckNumber);
                intent.putExtra("intentNewTrailerNumber", intentNewTrailerNumber);
                intent.putExtra("intentUserId", intentUserId);
                startActivity(intent);
            }
        });

        //startService(new Intent(this, AndroidLocationService.class));


    }

//    @Override
//    public void handleNewLocation(Location location) {
//
//    }

    class InfoBegin extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         * */
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
         * */
        protected String doInBackground(String... args) {
            String newTrailer = enteredTrailer.getText().toString();
            String dispatchNotes = notesEditText.getText().toString();

// Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("newTrailer", newTrailer));
            params.add(new BasicNameValuePair("dispatchNotes", dispatchNotes));
            params.add(new BasicNameValuePair("intentUserId", intentUserId));
// getting JSON Object
// Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_product,
                    "POST", params);
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

                    Intent intent = new Intent(getApplicationContext(), FirstStop.class);
                    intent.putExtra("intentTruckNumber", intentTruckNumber);
                    intent.putExtra("intentUserId", intentUserId);
                    intent.putExtra("intentTrailerNumber", intentTrailerNumber);
                    startActivity(intent);
// closing this screen
                    finish();
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
         * **/
        protected void onPostExecute(String file_url) {
// dismiss the dialog once done
            pDialog.dismiss();
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


}
