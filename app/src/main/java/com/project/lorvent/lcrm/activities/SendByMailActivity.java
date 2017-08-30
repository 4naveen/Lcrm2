package com.project.lorvent.lcrm.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.models.Person;
import com.project.lorvent.lcrm.models.Staff;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.ContactsCompletionView;
import com.project.lorvent.lcrm.utils.MyVolleySingleton;
import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class SendByMailActivity extends AppCompatActivity implements TokenCompleteTextView.TokenListener<Person>,AdapterView.OnItemSelectedListener{
    ContactsCompletionView completionView;
    ArrayList<Person> persons;
    ArrayAdapter<Person> adapter;
    EditText subject,message;
    TextView send;
    LinearLayout linearLayout;
    ArrayList<Staff>staffArrayList;
    ArrayList<String>staffList;
    TextInputLayout input_layout_subject,input_layout_message,input_layout_recipients;
    ArrayList<String>member_id;
    String quotationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_by_mail);
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
           // actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Send Quotation");
        }
        input_layout_subject=(TextInputLayout)findViewById(R.id.input_layout_subject);
        input_layout_message=(TextInputLayout)findViewById(R.id.input_layout_message);
        input_layout_recipients=(TextInputLayout)findViewById(R.id.input_layout_recipients);
        member_id= new ArrayList<>();
        quotationId=getIntent().getStringExtra("quotation_id");
        subject=(EditText)findViewById(R.id.subject);
        message=(EditText)findViewById(R.id.message);
        send=(TextView) findViewById(R.id.send);
        linearLayout=(LinearLayout)findViewById(R.id.layout);
        completionView = (ContactsCompletionView)findViewById(R.id.searchView);

        persons=new ArrayList<>();
        staffArrayList=new ArrayList<>();
        staffList=new ArrayList<>();
        getStaffList(Appconfig.TOKEN);

        subject.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_layout_subject.setError("");

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_layout_message.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subject.getText().toString().isEmpty())
                {
                    input_layout_subject.setError("Please enter subject");
                    return;
                }
                else if (message.getText().toString().isEmpty()){
                    input_layout_message.setError("Please enter message");
                    return;
                }
                else if (completionView.getText().toString().isEmpty()){
                    input_layout_recipients.setError("Please select recipients");
                    return;
                }
                for (Object o:completionView.getObjects())
                {
                    member_id.add(String.valueOf(staffArrayList.get(staffList.indexOf(o.toString())).getEmail()));
                }
                for (String s:member_id) {
                    Log.i("memeber_id",s);
                }
                new SendEmail().execute(Appconfig.TOKEN,subject.getText().toString(),message.getText().toString(),quotationId);
            }
        });
    }
    public  void getStaffList(String token)
    {   SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String get_url;
        if (text_url != null) {
            get_url = text_url + "/user/staffs?token=";
        } else {
            get_url = Appconfig.STAFF_URL;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET,get_url+token,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        JSONObject  jsonObject1=jsonObject.getJSONObject("staffs");
                        Iterator<String> iter = jsonObject1.keys();
                        while (iter.hasNext()) {
                            String key = iter.next();
                            try {
                                Object value = jsonObject1.get(key);
                                JSONObject jsonObject2= (JSONObject) jsonObject1.get(key);
                                Staff staff =new Staff();
                                staff.setName(jsonObject2.getString("full_name"));
                                staff.setId(jsonObject2.getInt("id"));
                                staff.setEmail(jsonObject2.getString("email"));
                                staffList.add(jsonObject2.getString("full_name"));
                                staffArrayList.add(staff);
                            } catch (JSONException e) {
                                // Something went wrong!
                            }
                        }
                        for (int i = 0; i < staffArrayList.size(); i++) {
                            Person person = new Person(staffArrayList.get(i).getName());
                            // Log.i("team member",staffArrayList.get(i).getName());
                            persons.add(person);
                        }
                        adapter = new FilteredArrayAdapter<Person>(SendByMailActivity.this, R.layout.person_layout,persons) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                if (convertView == null) {
                                    LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                                    convertView = l.inflate(R.layout.person_layout, parent, false);
                                }
                                Person p = getItem(position);
                                ((TextView)convertView.findViewById(R.id.name)).setText(p.getName());
                                return convertView;
                            }

                            @Override
                            protected boolean keepObject(Person person, String mask) {
                                mask = mask.toLowerCase();
                                return person.getName().toLowerCase().startsWith(mask);
                            }
                        };
                        completionView.setAdapter(adapter);
                        completionView.allowDuplicates(false);
                        completionView.setTokenListener(SendByMailActivity.this);
                        completionView.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Select);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

            // Log.i("response--", String.valueOf(error));
        }
    }){
        @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();

            return params;
        }

    } ;
        MyVolleySingleton.getInstance(SendByMailActivity.this).getRequestQueue().add(stringRequest);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onTokenAdded(Person token) {

    }

    @Override
    public void onTokenRemoved(Person token) {

    }

    private class SendEmail extends AsyncTask<String,Void,String>
    {
        ProgressDialog dialog;
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(SendByMailActivity.this);
            dialog.setMessage("Loading, please wait...");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String response="",jsonresponse="";
            JSONObject json=null;
            BufferedReader bufferedReader=null;
            URL url = null;
            String token=params[0];
            String subject = params[1];
            String message =params[2];
            String quotationId =params[3];

            try {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("subject", subject);
                jsonObject.put("body", message);
                jsonObject.put("quotation_id", quotationId);

                JSONArray array = new JSONArray();
                for (int i = 0; i <member_id.size(); i++) {
                    array.put(member_id.get(i));

                }
                jsonObject.put("recipients", array);
                Log.i("jsonobject",jsonObject.toString());


                SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String post_mail_url;
                if (text_url != null) {
                    post_mail_url = text_url + "/user/send_quotation?token=";
                } else {
                    post_mail_url = Appconfig.SEND_QUOTATION_URL;
                }
                url = new URL(post_mail_url+token);
                conn= (HttpURLConnection) url.openConnection();
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
                int responseCode=conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                    json = new JSONObject(response);
                    //Get Values from JSONobject
                    //System.out.println("success=" + json.get("success"));
                    jsonresponse = json.getString("success");

                }
                else {
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line="";
                    while ((line = bufferedReader.readLine()) != null) {
                        response += line;
                    }
                    json = new JSONObject(response);
                    jsonresponse=json.getString("error");
                    //System.out.println("error=" + json.get("error"));
                    //succes = json.getString("success");
                }

            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.logException(e);

            }

            return jsonresponse;
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            if (result.equals("success"))
            {
                final Snackbar snackbar = Snackbar.make(linearLayout, "sent Succesfully!", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },3000);
            }
            else {
                final Snackbar snackbar = Snackbar.make(linearLayout, "not sent! Try Again", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
            }
        }
    }
}
