package com.jb.jb.ctxtrack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener,LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // Progress Dialog
    private ProgressDialog pDialog;

    //make JSONParser private???
    JSONParser jsonParser = new JSONParser();
    private String intentTruckNumber;
    private String intentTrailerNumber;
    private String intentUserId;



    private EditText truckNumber;
    private EditText trailerNumber;
    private EditText truckMiles;
    private EditText delranDeparture;
    private Button submitAndGo;
    private TextView userId;

    //edit this to correct server address
    //private static String url_create_product = "http://192.168.0.6:1337/ctxtrack/create_product.php";
    //private static String url_create_product = "http://localhost/ctxtrack/activity_main.php";
    //delran ip
    //private static String url_create_product = "http://192.168.56.101/ctxtrack/activity_main.php";
    //home ip
    //private static String url_create_product = "http://192.168.56.1:1337/ctxtrack/activity_main.php";
    //HostGator
    private static String url_create_product = "http://www.jabdata.com/ctxtrack/activity_main.php";



    private static final String TAG_SUCCESS = "success";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        submitAndGo = (Button) findViewById(R.id.submit_button);
        truckNumber = (EditText) findViewById(R.id.truck_number_editText);
        trailerNumber = (EditText) findViewById(R.id.trailer_editText);
        truckMiles = (EditText) findViewById(R.id.truck_mileage);
        delranDeparture = (EditText) findViewById(R.id.delranDepartureTime_editText);
        userId = (TextView) findViewById(R.id.userIdMain);

        Intent intent = getIntent();
        if(intent != null) {
            intentUserId = intent.getStringExtra("intentUserId");
            userId.setText(intentUserId);
        }

        submitAndGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //need to set up correct php files
                new InfoBegin().execute();

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
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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
            String truckMileage = truckMiles.getText().toString();
            //String shiftBegin = truckMileage.getText().toString();
            String delranDepartTime = delranDeparture.getText().toString();
// Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("truckNum", truckNum));
            params.add(new BasicNameValuePair("trailerNum", trailerNum));
            params.add(new BasicNameValuePair("truckMileage", truckMileage));
            //params.add(new BasicNameValuePair("starttime", shiftBegin));
            params.add(new BasicNameValuePair("delranDepartTime", delranDepartTime));
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

                    intentTruckNumber = truckNumber.getText().toString();
                    intentTrailerNumber = trailerNumber.getText().toString();
                    intentUserId = userId.getText().toString();

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
}
