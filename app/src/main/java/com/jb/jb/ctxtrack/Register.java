package com.jb.jb.ctxtrack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Register extends Activity implements View.OnClickListener {

    private EditText user, pass, passConfirm;
    private Button mRegister;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    //php register script

    //edit this to correct server address
    //private static String REGISTER_URL = "http://192.168.0.6:1337/ctxtrack/register.php";
    //private static String REGISTER_URL = "http://localhost/ctxtrack/register.php";
    //delran ip
    //private static String REGISTER_URL = "http://192.168.56.101/ctxtrack/register.php";
    //home ip
    //private static final String REGISTER_URL = "http://192.168.56.1:1337/ctxtrack/register.php";
    //HostGator
    private static String REGISTER_URL = "http://www.jabdata.com/ctxtrack/register.php";


    //testing from a real server:
    //private static final String REGISTER_URL = "http://www.mybringback.com/webservice/register.php";

    //ids
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        user = (EditText)findViewById(R.id.username);
        pass = (EditText)findViewById(R.id.password);
        passConfirm = (EditText)findViewById(R.id.passConfirm);
        user.requestFocus();


        mRegister = (Button)findViewById(R.id.register);
        mRegister.setOnClickListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

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
        if (pass.getText().toString().trim().equals(passConfirm.getText().toString().trim())) {
            new CreateUser().execute();
        } else {
            Toast.makeText(Register.this, R.string.passwords_dont_match, Toast.LENGTH_LONG).show();
            pass.setText("");
            passConfirm.setText("");
        }

    }

    class CreateUser extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Register.this);
            pDialog.setMessage("Creating User...");
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

                //Posting user data to script
                JSONObject json = jsonParser.makeHttpRequest(
                        REGISTER_URL, "POST", params);

                // full json response
                Log.d("Registering attempt", json.toString());

                // json success element
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {

                    Log.d("User Created!", json.toString());
                    finish();
                    return json.getString(TAG_MESSAGE);
                }else{
                    Log.d("Registering Failure!", json.getString(TAG_MESSAGE));
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
            if (file_url != null){
                Toast.makeText(Register.this, file_url, Toast.LENGTH_LONG).show();
            }

        }


    }


}
