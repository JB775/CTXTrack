package com.jb.jb.ctxtrack;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
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

public class Login extends Activity implements View.OnClickListener, GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener,LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private EditText user, pass;
    private Button mSubmit, mRegister;
    private String intentUserId;

    private String TAG = this.getClass().getSimpleName();
    private TextView txtConnectionStatus;
    private TextView txtLastKnownLoc;
    private EditText etLocationInterval;
    private TextView txtLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationrequest;
    private Intent mIntentService;
    private PendingIntent mPendingIntent;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser2 jsonParser = new JSONParser2();

    // php login script location:

    //edit this to correct server address
    //private static String LOGIN_URL = "http://192.168.0.6:1337/ctxtrack/login.php";
    //private static String LOGIN_URL = "http://localhost/ctxtrack/login.php";
    //delran ip
    //private static String LOGIN_URL = "http://192.168.56.101/ctxtrack/login.php";
    //home ip
    //private static final String LOGIN_URL = "http://192.168.56.1:1337/ctxtrack/login.php";
    //HostGator
    private static String LOGIN_URL = "http://www.jabdata.com/ctxtrack/login.php";


    // testing from a real server:
    // private static final String LOGIN_URL =
    // "http://www.mybringback.com/webservice/login.php";

    // JSON element ids from repsonse of php script:
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // setup input fields
        user = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.password);
        user.requestFocus();


        // setup buttons
        mSubmit = (Button) findViewById(R.id.login);
        mRegister = (Button) findViewById(R.id.register);

        // register listeners
        mSubmit.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        //does GPS work without the below 2??
        //mIntentService = new Intent(this, LocationService.class);
        //mPendingIntent = PendingIntent.getService(this, 1, mIntentService, 0);

        int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resp == ConnectionResult.SUCCESS) {
            mGoogleApiClient = new GoogleApiClient.Builder(Login.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
        } else {
            //remove this Toast!!!
            Toast.makeText(this, "Google Play Service Error " + resp, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.login:
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
               //     if (((Button) v).getText().equals("Start")) {
                        locationrequest = LocationRequest.create();
                        locationrequest.setInterval(1000);

                        // maybe change "this" to 'activityname'.this
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationrequest, this);
                      //  ((Button) v).setText("Stop");
                    }
//                else {
//                        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, Login.this);
//                        ((Button) v).setText("Start");
//                    }
  //              }

               // if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                 //   locationrequest = LocationRequest.create();
                 //   locationrequest.setInterval(100);
                 //   LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationrequest, mPendingIntent);
                //}

                new AttemptLogin().execute();
                break;
            case R.id.register:
                Intent i = new Intent(this, Register.class);
                startActivity(i);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
       super.onDestroy();
      //  if (mGoogleApiClient != null)
       //     mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "onConnected");
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
            Log.i(TAG, "Location Request :" + location.getLatitude() + "," + location.getLongitude());
            //double asdf = location.
            //add server send data here?  create class below and call class here??
            //after creating class, call class in oncreate then end in onDestroy()??
            //make it so updates are not as often
            //is this what's actually being sent to the log thruout the app??
            //add setInterval(interval here) ??????
            //must create a var for LocationRequest.create(); then use setInterval on that...will this work in background?? or do I need to use pending intent?
            //add getSpeed()???
            //look into location.notify()
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "onConnectionFailed");
    }

    class AttemptLogin extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage("Attempting login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            String username = user.getText().toString().trim();
            String password = pass.getText().toString().trim();
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));

                Log.d("request!", "starting");
                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST",
                        params);

                // check your log for json response
                Log.d("Login attempt", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Login Successful!", json.toString());
                    // save user data
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(Login.this);
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putString("username", username);
                    edit.commit();

                    intentUserId = user.getText().toString().trim();
                    Intent i = new Intent(Login.this, MainActivity.class);
                    i.putExtra("intentUserId", intentUserId);
                    finish();
                    startActivity(i);
                    return json.getString(TAG_MESSAGE);
                } else {
                    Log.d("Login Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null) {
                Toast.makeText(Login.this, file_url, Toast.LENGTH_LONG).show();
            }

        }

    }
    @Override
    public void onBackPressed()
    {


    }

}