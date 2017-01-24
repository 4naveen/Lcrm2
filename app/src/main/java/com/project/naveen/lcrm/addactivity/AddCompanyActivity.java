package com.project.naveen.lcrm.addactivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.MyVolleySingleton;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.models.Contacts;
import com.project.naveen.lcrm.models.SalesTeam;
import com.weiwangcn.betterspinner.library.BetterSpinner;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class AddCompanyActivity extends AppCompatActivity {
    EditText company_name,phone,email,address;
    BetterSpinner main_contact_person,sales_team;
    ArrayList <String>main_contact_personList,salesTeamNameList;
    ArrayList<SalesTeam>salesTeamArrayList;
    Button submit;
    ImageView upload;
    TextInputLayout input_company_name,input_email,input_address,input_phone,input_main_contact_person,input_salesteam;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_company);
        company_name=(EditText)findViewById(R.id.company_name);

        input_company_name=(TextInputLayout)findViewById(R.id.input_layout_company_name);
        input_email=(TextInputLayout)findViewById(R.id.input_layout_email);
        input_address=(TextInputLayout)findViewById(R.id.input_layout_address);
        input_phone=(TextInputLayout)findViewById(R.id.input_layout_phone);
        input_main_contact_person=(TextInputLayout)findViewById(R.id.input_layout_main_contact_person);
        input_salesteam=(TextInputLayout)findViewById(R.id.input_layout_salesteam);
        main_contact_personList=new ArrayList<>();
        salesTeamNameList=new ArrayList<>();
        getSalesTeamList(Appconfig.TOKEN);
        getContactPersonList(Appconfig.TOKEN);
        salesTeamArrayList=new ArrayList<>();
        email=(EditText)findViewById(R.id.email);
        phone=(EditText)findViewById(R.id.phone);
        address=(EditText)findViewById(R.id.address);
        main_contact_person=(BetterSpinner) findViewById(R.id.mcp);
        sales_team=(BetterSpinner) findViewById(R.id.salesteam);
        submit=(Button)findViewById(R.id.submit);
        upload=(ImageView)findViewById(R.id.upload);
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        actionBar.setTitle("Add Company");

        ArrayAdapter<String> salesteamArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,salesTeamNameList);
        salesteamArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sales_team.setAdapter(salesteamArrayAdapter);

        ArrayAdapter<String> mcpArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,main_contact_personList);
        mcpArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        main_contact_person.setAdapter(mcpArrayAdapter);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i.createChooser(i,"select pic"),101);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (company_name.getText().toString().isEmpty())
                {
                    input_company_name.setError("Please enter opportunity name");

                    return;
                }

                else if (email.getText().toString().isEmpty()){
                    input_email.setError("Please enter email");
                    return;
                }
                else if (phone.getText().toString().isEmpty()){
                    input_phone.setError("Please enter nextAction date");
                    return;
                }
                else if (address.getText().toString().isEmpty()){
                    input_address.setError("Please enter expectedClosing date");
                    return;
                }
                else if (main_contact_person.getText().toString().isEmpty()){
                    input_main_contact_person.setError("Please select customer name");
                    return;
                }
                else if (sales_team.getText().toString().isEmpty()){
                    input_salesteam.setError("Please select salesTeam name");
                    return;
                }

                else {
                    //do nothing
                }
                int salesTeamId=salesTeamArrayList.get(salesTeamNameList.indexOf(sales_team.getText().toString())).getId();
                new AddCompany().execute(Appconfig.TOKEN,company_name.getText().toString(),
                        email.getText().toString(),address.getText().toString(),String.valueOf(salesTeamId),
                        main_contact_person.getText().toString(),phone.getText().toString());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==101&&resultCode== Activity.RESULT_OK)
        {
            Uri uri=data.getData();
            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Log.d("image--", String.valueOf(bitmap));

                upload.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
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


    public  void  getSalesTeamList(String token)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Appconfig.SALESTEAM_URL+token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            Log.i("response--", String.valueOf(response));

                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("salesteams");
                            for (int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject object=jsonArray.getJSONObject(i);
                                SalesTeam salesTeam=new SalesTeam();
                                salesTeam.setId(object.getInt("id"));
                                salesTeam.setSalesteam(object.getString("salesteam"));
                                salesTeamNameList.add(object.getString("salesteam"));
                                salesTeamArrayList.add(salesTeam);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("response--", String.valueOf(error));
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }

        } ;
        MyVolleySingleton.getInstance(AddCompanyActivity.this).getRequestQueue().add(stringRequest);

    }

    public void getContactPersonList(String token)
    { StringRequest stringRequest = new StringRequest(Request.Method.GET, Appconfig.CUSTOMER_URL+token,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.i("response--", String.valueOf(response));
                        JSONObject jsonObject=new JSONObject(response);
                        JSONArray jsonArray=jsonObject.getJSONArray("customers");
                        for (int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject object=jsonArray.getJSONObject(i);
                            main_contact_personList.add(object.getString("full_name"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

            Log.i("response--", String.valueOf(error));
        }
    }){
        @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();

            return params;
        }

    } ;
        MyVolleySingleton.getInstance(AddCompanyActivity.this).getRequestQueue().add(stringRequest);

    }

    class AddCompany extends AsyncTask<String,Void,String>
    {
        ProgressDialog dialog;
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(AddCompanyActivity.this);
            dialog.setMessage("Loading, please wait...");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "", jsonresponse = "";
            BufferedReader bufferedReader = null;
            JSONObject json = null;
            JSONObject jsonObject = null;
            String tok = params[0];
            String name = params[1];
            String email =params[2] ;
            String address = params[3];
            String sales_team_id = params[4];
            String main_contact_person=params[5];
            String phone=params[6];

            URL url = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("name", name);
                jsonObject.put("email", email);
                jsonObject.put("address", address);
                jsonObject.put("sales_team_id", sales_team_id);
                jsonObject.put("main_contact_person", main_contact_person);
                jsonObject.put("phone", phone);

                url = new URL(Appconfig.COMPANY_POST_URL+tok);
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
                int responseCode = conn.getResponseCode();
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

                } else {
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line ="";
                    while ((line = bufferedReader.readLine()) != null) {
                        response += line;
                        Log.d("output lines", line);
                    }
                    Log.i("response", response);
                    json = new JSONObject(response);
                    jsonresponse = json.getString("error");
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

            dialog.dismiss();
            if (result!=null)
            {
                Toast.makeText(getApplicationContext(),""+result,Toast.LENGTH_LONG).show();
                //finish();
            }
        }
    }
}
