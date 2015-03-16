package com.jb.jb.ctxtrack;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class LocationActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener,LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private String TAG = this.getClass().getSimpleName();
    private TextView txtConnectionStatus;
    private TextView txtLastKnownLoc;
    private EditText etLocationInterval;
    private TextView txtLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationrequest;
    private Intent mIntentService;
    private PendingIntent mPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        txtConnectionStatus = (TextView) findViewById(R.id.txtConnectionStatus);
        txtLastKnownLoc = (TextView) findViewById(R.id.txtLastKnownLoc);
        etLocationInterval = (EditText) findViewById(R.id.etLocationInterval);
        txtLocationRequest = (TextView) findViewById(R.id.txtLocationRequest);
        mIntentService = new Intent(this, LocationService.class);
        mPendingIntent = PendingIntent.getService(this, 1, mIntentService, 0);
        int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resp == ConnectionResult.SUCCESS) {
            mGoogleApiClient = new GoogleApiClient.Builder(LocationActivity.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
        } else {
            Toast.makeText(this, "Google Play Service Error " + resp, Toast.LENGTH_LONG).show();
        }
    }

    public void buttonClicked(View v) {
        if (v.getId() == R.id.btnLastLoc) {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                Log.i(TAG, "Last Known Location :" + loc.getLatitude() + "," + loc.getLongitude());
                txtLastKnownLoc.setText(loc.getLatitude() + "," + loc.getLongitude());
            }
        }
        if (v.getId() == R.id.btnStartRequest) {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                if (((Button) v).getText().equals("Start")) {
                    locationrequest = LocationRequest.create();
                    locationrequest.setInterval(Long.parseLong(etLocationInterval.getText().toString()));
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationrequest, LocationActivity.this);
                    ((Button) v).setText("Stop");
                } else {
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, LocationActivity.this);
                    ((Button) v).setText("Start");
                }
            }
        }
        if (v.getId() == R.id.btnRequestLocationIntent) {
            if (((Button) v).getText().equals("Start")) {
                locationrequest = LocationRequest.create();
                locationrequest.setInterval(100);
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationrequest, mPendingIntent);
                ((Button) v).setText("Stop");
            } else {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, LocationActivity.this);
                ((Button) v).setText("Start");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "onConnected");
        txtConnectionStatus.setText("Connection Status : Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onDisconnected() {
        Log.i(TAG, "onDisconnected");
        txtConnectionStatus.setText("Connection Status : Disconnected");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "onConnectionFailed");
        txtConnectionStatus.setText("Connection Status : Fail");
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.i(TAG, "Location Request :" + location.getLatitude() + "," + location.getLongitude());
            txtLocationRequest.setText(location.getLatitude() + "," + location.getLongitude());
        }
    }
}