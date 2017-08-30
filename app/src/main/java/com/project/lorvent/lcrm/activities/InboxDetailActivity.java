package com.project.lorvent.lcrm.activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.adapters.InboxAdapter;
import com.project.lorvent.lcrm.models.Inbox;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.RecyclerSwipeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class InboxDetailActivity extends AppCompatActivity {
    CardView cardView;
    Button reply,submit;
    int email_id;
    TextView title,text,sent_date;
    EditText message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_detail);
        reply = (Button) findViewById(R.id.reply);
        cardView = (CardView) findViewById(R.id.cv1);
        email_id= getIntent().getIntExtra("email_id",0);
        title=(TextView)findViewById(R.id.title);
        text=(TextView)findViewById(R.id.text);
        sent_date=(TextView)findViewById(R.id.sent_date);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Details");
        }
        new GetEmail().execute(Appconfig.TOKEN, String.valueOf(email_id));

        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView.setVisibility(View.VISIBLE);
                submit = (Button) findViewById(R.id.submit);
                message= (EditText) findViewById(R.id.message);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new replyEmail().execute(Appconfig.TOKEN,String.valueOf(email_id),message.getText().toString());

                    }
                });

            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }
    private class GetEmail extends AsyncTask<String,Void,String>
    {
        ProgressDialog dialog;
        HttpURLConnection conn;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(InboxDetailActivity.this);
            dialog.setMessage("Loading, please wait");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String response="",jsonResponse="";
            JSONObject json;
            BufferedReader bufferedReader;
            String tok=params[0];
            String email_id=params[1];
            URL url;
            try {

                SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String details_url;
                if (text_url != null) {
                    details_url = text_url + "/email?token=";
                } else {
                    details_url = Appconfig.EMAIL_URL;
                }
                url = new URL(details_url+tok+"&email_id="+email_id);
                conn = (HttpURLConnection) url.openConnection();
                int responseCode=conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }

                    //Get Values from JSONobject
                    //System.out.println("success=" + json.get("success"));
                    jsonResponse=response;

                }
                else {
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        response += line;
                    }
                    json = new JSONObject(response);
                    jsonResponse=json.getString("error");
                    //System.out.println("error=" + json.get("error"));
                    //succes = json.getString("success");
                }

            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
            return jsonResponse;
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result);
                JSONObject object=jsonObject.getJSONObject("email");
                    title.setText(object.getString("subject"));
                    text.setText(object.getString("message"));
                    sent_date.setText(object.getString("created_at"));

            } catch (JSONException e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }


        }

    }
    private class replyEmail extends AsyncTask<String,Void,String>
    {
        // ProgressDialog dialog;
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String response="",jsonresponse="";
            JSONObject json;
            BufferedReader bufferedReader;
            String tok=params[0];
            String email_id=params[1];
            String message=params[2];
            URL url;
            try {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("email_id",email_id);
                jsonObject.put("message",message);

                Log.i("json", tok);

                SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String reply_mail_url;
                if (text_url != null) {
                    reply_mail_url = text_url + "/replay_email?token=";
                } else {
                    reply_mail_url = Appconfig.REPLY_EMAIL_URL;
                }
                url = new URL(reply_mail_url+tok);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();
                os.close();
                //Log.i("res code--",""+conn.getResponseCode());
                int responseCode=conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    //Log.d("Output",br.toString());
                    while ((line = br.readLine()) != null) {
                        response += line;
                        Log.d("output lines", line);
                    }
                    json = new JSONObject(response);
                    //Get Values from JSONobject
                    //System.out.println("success=" + json.get("success"));
                    jsonresponse = json.getString("success");

                }
                else {
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        response += line;
                        Log.d("output lines", line);
                    }
                    Log.i("response",response);
                    json = new JSONObject(response);
                    jsonresponse=json.getString("error");
                    //System.out.println("error=" + json.get("error"));
                    //succes = json.getString("success");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonresponse;
        }
        @Override
        protected void onPostExecute(String result) {
           // progressDialog.dismiss();
            if (result!=null)
            {
                if (result.equals("success"))
                {
                    Toast.makeText(getApplicationContext(),"sent successfully",Toast.LENGTH_LONG).show();
                }
                if (result.equals("not_valid_data"))
                {
                    Toast.makeText(getApplicationContext(),"error occured!Try Again",Toast.LENGTH_LONG).show();
                }
            }

        }

    }
}
