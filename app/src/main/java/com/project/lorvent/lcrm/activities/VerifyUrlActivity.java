package com.project.lorvent.lcrm.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.project.lorvent.lcrm.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class VerifyUrlActivity extends AppCompatActivity {
    EditText url;
    TextInputLayout input_url;
    LinearLayout linearLayout;
    SharedPreferences preferences1;
    //Button verfiy;
    ActionProcessButton verfiy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_url);
        linearLayout = (LinearLayout) findViewById(R.id.layout);
        url = (EditText)findViewById(R.id.url);
        input_url = (TextInputLayout) findViewById(R.id.input_layout_url);
       // verfiy = (Button) findViewById(R.id.verify);
        changeStatusBarColor();
        verfiy = (ActionProcessButton) findViewById(R.id.btnSignIn);
        verfiy.setMode(ActionProcessButton.Mode.PROGRESS);
        preferences1 = getSharedPreferences("pref", MODE_PRIVATE);

      /*  SharedPreferences.Editor editor = preferences.edit();
        editor.remove("url");
        editor.apply();
        editor.commit();*/
        String text_url = preferences1.getString("url", null);
        if (text_url != null) {
            url.setText(text_url.substring(0,text_url.length()-3));
        } else {
            url.setText("http://demo.lcrmapp.com/");
        }
        verfiy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (url.getText().toString().isEmpty()) {
                    url.requestFocus();
                    if (url.getText().toString().isEmpty()) {
                        url.requestFocus();
                        input_url.setError("please enter valid url");
                        return;
                    }
                }

                if (!isConnected()) {
                    Intent i = new Intent(VerifyUrlActivity.this, NetworkErrorActivity.class);
                    startActivity(i);
                }

                if (isConnected()) {
                    new CheckUrl().execute(url.getText().toString());
                }
            }
        });
    }

    public boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    private class CheckUrl extends AsyncTask<String, Integer, String> {
        String murl;
        boolean status;
        boolean running;
        int progressStatus = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            verfiy.setProgress(progressStatus);
            running = true;
        }

        @Override
        protected String doInBackground(String... params) {
            URL url;
            String response = "";
            HttpURLConnection connection;
            BufferedReader bufferedReader;
            StringBuffer buffer;
            murl = params[0];
            try {
                url = new URL(params[0]+"api");
                // url = new URL("https://api.myjson.com/bins/vsgth");
                // url = new URL("http://54.lcrm.in");
                connection = (HttpURLConnection) url.openConnection();
                int responseCode = connection.getResponseCode();
                System.out.println("responsecode--"+responseCode);
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    buffer = new StringBuffer();
                    while ((line = bufferedReader.readLine()) != null) {
                        // response += line;
                        buffer.append(line);
                    }
                    response = buffer.toString();
                    status=true;
                } else {
                    InputStreamReader inputStreamReader = new InputStreamReader(connection.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    buffer = new StringBuffer();
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        //  response += line;
                        buffer.append(line);
                    }
                    response = buffer.toString();
                    status=false;
                }
                int i = 100;
                while (running & progressStatus < 100) {
                    try {
                        progressStatus=progressStatus+20;
                        publishProgress(progressStatus);
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (i-- == 0) {
                        running = false;
                    }
                    publishProgress(i);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            verfiy.setProgress(progressStatus);
            verfiy.setLoadingText("Verfiying");
        }

        @Override
        protected void onPostExecute(String response) {
            JSONObject jsonObject= null;
            try {
                jsonObject = new JSONObject(response);
                if (jsonObject.getString("success").equals("This is a LCRM installation")) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                        }
                    },20000);
                    verfiy.setProgress(100);
                    verfiy.setCompleteText("Verfied");
                    SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("url", murl+"api");

                  //  editor.putString("url", "http://54.lcrm.in/api");

                    editor.apply();
                    editor.commit();
                    startActivity(new Intent(VerifyUrlActivity.this,LoginActivity.class));

                } else {
                    verfiy.setProgress(-1);
                    verfiy.setErrorText("Not Verfied");
                    final Snackbar snackbar = Snackbar.make(linearLayout, "Error ! Please enter valid url", Snackbar.LENGTH_LONG);
                    View v = snackbar.getView();
                    v.setMinimumWidth(1000);
                    snackbar.show();
                    SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("url", "http://saas.lcrmapp.com/");
                    editor.apply();
                    editor.commit();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


 /*           if (status) {
                verfiy.setProgress(100);
                verfiy.setCompleteText("Verfied");
                SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("url", murl+"api");
                editor.apply();
                editor.commit();
                startActivity(new Intent(VerifyUrlActivity.this,LoginActivity.class));

                //  finish();
            } else {
                //Toast.makeText(getApplicationContext(), "Error!Please enter valid url", Toast.LENGTH_LONG).show();
                verfiy.setProgress(-1);
                verfiy.setErrorText("Not Verfied");
                final Snackbar snackbar = Snackbar.make(linearLayout, "Error ! Please enter valid url", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
          *//*      TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.WHITE);*//*
                snackbar.show();
                SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("url", "http://54.lcrm.in/");
                editor.apply();
                editor.commit();
            }*/
        }
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // window.setStatusBarColor(Color.rgb(104, 159, 56));
            //  window.setStatusBarColor(Color.rgb(105, 168,241));
            window.setStatusBarColor(Color.rgb(44, 68, 102));
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        verfiy.setProgress(0);

    }
}
