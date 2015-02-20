package com.jb.jb.ctxtrack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();


    EditText truckNumber;
    EditText trailerNumber;
    EditText shiftStart;
    EditText delranDeparture;
    Button submitAndGo;

    //edit this to correct server address
    private static String url_create_product = "http://192.168.0.6:1337/ctxtrack/create_product.php";

    private static final String TAG_SUCCESS = "success";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        submitAndGo = (Button) findViewById(R.id.submit_button);
        truckNumber = (EditText) findViewById(R.id.truck_number_editText);
        trailerNumber = (EditText) findViewById(R.id.trailer_editText);
        shiftStart = (EditText) findViewById(R.id.shiftStart_editText);
        delranDeparture = (EditText) findViewById(R.id.delranDepartureTime_editText);

        submitAndGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new InfoBegin().execute();

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
            String trailerNum2 = trailerNumber.getText().toString();
            String shiftBegin = shiftStart.getText().toString();
            //String delranDepart = delranDeparture.getText().toString();
// Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("name", truckNum));
            params.add(new BasicNameValuePair("price", trailerNum));
            params.add(new BasicNameValuePair("description", trailerNum2));
            params.add(new BasicNameValuePair("starttime", shiftBegin));
            //params.add(new BasicNameValuePair("delranDepart", delranDepart));
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
                    Intent i = new Intent(getApplicationContext(), FirstStop.class);

                    startActivity(i);
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
