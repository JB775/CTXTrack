package com.jb.jb.ctxtrack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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


public class MainActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener,LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // Progress Dialog
    private ProgressDialog pDialog;

    private JSONParser jsonParser = new JSONParser();
    private String intentTruckNumber;
    private String intentTrailerNumber;
    private String intentUserId;



    private EditText truckNumber;
    private EditText trailerNumber;
    private EditText truckMiles;
    private EditText delranDeparture;
    private Button submitAndGo;
    private TextView userIdzz;

    //private String TAG = this.getClass().getSimpleName();

    //GPS Variables
    private GoogleApiClient locationclient;
    private LocationRequest locationrequest;
    private double lat;
    private double long3;
    public static final String TAG = MainActivity.class.getSimpleName();

    //edit this to correct server address
    //private static String url_create_product = "http://192.168.0.6:1337/ctxtrack/create_product.php";
    //private static String url_create_product = "http://localhost/ctxtrack/activity_main.php";
    //private static String url_create_product = "http://192.168.56.101/ctxtrack/activity_main.php";
    //home ip
    //private static String url_create_product = "http://192.168.56.1:1337/ctxtrack/activity_main.php";
    //HostGator
    private static String url_create_product = "http://www.jabdata.com/ctxtrack/activity_main.php";
    private static String server_location = "http://www.jabdata.com/ctxtrack/location.php";

    private static final String TAG_SUCCESS = "success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resp == ConnectionResult.SUCCESS){
            locationclient =      new GoogleApiClient.Builder(MainActivity.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            locationclient.connect();
        }
        else{
            Toast.makeText(this, "Google Play Service Error " + resp, Toast.LENGTH_LONG).show();
        }

        submitAndGo = (Button) findViewById(R.id.submit_button);
        truckNumber = (EditText) findViewById(R.id.truck_number_editText);
        trailerNumber = (EditText) findViewById(R.id.trailer_editText);
        truckMiles = (EditText) findViewById(R.id.truck_mileage);
        delranDeparture = (EditText) findViewById(R.id.delranDepartureTime_editText);
        userIdzz = (TextView) findViewById(R.id.userIdMain);

        Intent intent = getIntent();
        if(intent != null) {
            intentUserId = intent.getStringExtra("intentUserId");
            String userId3 = intentUserId;
            userIdzz.setText(intentUserId);
        }


        submitAndGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //need to set up correct php files

                if (truckNumber.getText().toString().trim().isEmpty() ||  trailerNumber.getText().toString().trim().isEmpty() ||  truckMiles.getText().toString().trim().isEmpty() ||  delranDeparture.getText().toString().trim().isEmpty()) {

                    Toast.makeText(MainActivity.this, R.string.please_complete_all_fields , Toast.LENGTH_LONG).show();
                } else {
                    new InfoBegin().execute();
                }

//                intentTruckNumber = truckNumber.getText().toString();
//                intentTrailerNumber = trailerNumber.getText().toString();
//                Intent intent = new Intent(MainActivity.this, FirstStop.class);
//                intent.putExtra("intentTruckNumber", intentTruckNumber);
//                intent.putExtra("intentTrailerNumber", intentTrailerNumber);
//                startActivity(intent);
//


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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
        Log.i(TAG, "onDisconnected");
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
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "onConnectionFailed");
    }


    class InfoBegin extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Updating Information...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            String truckNum = truckNumber.getText().toString();
            String trailerNum = trailerNumber.getText().toString();
            String truckMileage;
            String x = truckMiles.getText().toString().trim();
            int z = x.length();
            if (z >= 1 && z <= 3) {
                truckMileage = x;
            }
            else if (z == 4) {
                truckMileage = x.subSequence(0,1) + "," + x.subSequence(1,4);
            }
            else if (z == 5) {
                truckMileage = x.subSequence(0,2) + "," + x.subSequence(2,5);
            }
            else if (z == 6) {
                truckMileage = x.subSequence(0,3) + "," + x.subSequence(3,6);
            }
            else if (z >= 7) {
                truckMileage = x.subSequence(0,1) + "," + x.subSequence(0,4) + "," + x.subSequence(4,7);
            } else {
                truckMileage = x;
            }


            //String truckMileage = truckMiles.getText().toString();
            //String shiftBegin = truckMileage.getText().toString();
            String departTime = delranDeparture.getText().toString();
            String userId2 = userIdzz.getText().toString();
            String departingDelran = getResources().getString(R.string.departing_delran);
// Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("truckNum", truckNum));
            params.add(new BasicNameValuePair("trailerNum", trailerNum));
            params.add(new BasicNameValuePair("truckMileage", truckMileage));
            //params.add(new BasicNameValuePair("starttime", shiftBegin));
            params.add(new BasicNameValuePair("departTime", departTime));
            params.add(new BasicNameValuePair("userId2", userId2));
            params.add(new BasicNameValuePair("stop", departingDelran));
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

                    intentTruckNumber = truckNumber.getText().toString();
                    intentTrailerNumber = trailerNumber.getText().toString();
                    String intentUserId2 = userIdzz.getText().toString();

                    Intent intent = new Intent(getApplicationContext(), FirstStop.class);
                    intent.putExtra("intentTruckNumber", intentTruckNumber);
                    intent.putExtra("intentUserId", intentUserId2);
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

    class InfoBegin3 extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {

            String userId2 = userIdzz.getText().toString();
            String backToDelran = getResources().getString(R.string.still_in_delran);
            String lat2 = String.valueOf(lat);
            String long2 = String.valueOf(long3);

            // Building Parameters ArrayList
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("latitude", lat2));
            params.add(new BasicNameValuePair("longitude", long2));
            //params.add(new BasicNameValuePair("stop", "LOCATION"));
            params.add(new BasicNameValuePair("stop", backToDelran));
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
