package com.project.lorvent.lcrm.activities.add;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.models.SalesTeam;
import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.Connection;
import com.project.lorvent.lcrm.utils.MyVolleySingleton;
import com.project.lorvent.lcrm.utils.Util;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
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

public class AddContactsActivity extends AppCompatActivity {
    EditText first_name,last_name,email,password,password_confirm,phone;
    BetterSpinner salesTeam,company;
    Button submit;
    ImageView upload;
    AppCompatCheckBox main_contact_person;
    ArrayList<String>salesTeamNameList;
    ArrayList<SalesTeam>salesTeamArrayList;
    LinearLayout linearLayout;
    TextInputLayout input_first_name,input_last_name,input_email,input_password,input_phone,input_password_confirm,input_salesteam,input_company;
    String encoded_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);
        salesTeam=(BetterSpinner) findViewById(R.id.salesteam);
        company=(BetterSpinner) findViewById(R.id.company);
        first_name=(EditText)findViewById(R.id.first_name);
        email=(EditText)findViewById(R.id.email);
        last_name=(EditText)findViewById(R.id.last_name);
        password=(EditText)findViewById(R.id.password);
        password_confirm=(EditText)findViewById(R.id.password_confirm);
        phone=(EditText)findViewById(R.id.phone);
        main_contact_person=(AppCompatCheckBox) findViewById(R.id.mcp);
        submit=(Button)findViewById(R.id.submit);
        upload=(ImageView)findViewById(R.id.upload);
        input_first_name=(TextInputLayout)findViewById(R.id.input_layout_firstName);
        input_last_name=(TextInputLayout)findViewById(R.id.input_layout_lastName);
        input_email=(TextInputLayout)findViewById(R.id.input_layout_email);
        input_password=(TextInputLayout)findViewById(R.id.input_layout_password);
        input_password_confirm=(TextInputLayout)findViewById(R.id.input_layout_password_confirm);
        input_phone=(TextInputLayout)findViewById(R.id.input_layout_phone);
        input_salesteam=(TextInputLayout)findViewById(R.id.input_layout_salesteam);
        input_company=(TextInputLayout)findViewById(R.id.input_layout_company);

        salesTeamNameList=new ArrayList<>();
        salesTeamArrayList=new ArrayList<>();
        getSalesTeamList(Appconfig.TOKEN);
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        actionBar.setTitle("New Contact");
        linearLayout=(LinearLayout)findViewById(R.id.layout);
        Connection.getCustomerList(Appconfig.TOKEN,AddContactsActivity.this);

        ArrayAdapter<String> salesteamArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,salesTeamNameList);
        salesteamArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        salesTeam.setAdapter(salesteamArrayAdapter);
        ArrayAdapter<String> customerArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, AppSession.companyNameList);
        customerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        company.setAdapter(customerArrayAdapter);

        first_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_first_name.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        last_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_last_name.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_email.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_phone.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_password.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password_confirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_password_confirm.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        salesTeam.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_salesteam.setError("");
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Sorry! for some reason this function is disabled in app",Toast.LENGTH_LONG).show();
                Intent i=new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i.createChooser(i,"select pic"),101);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (first_name.getText().toString().isEmpty())
                {
                    input_first_name.setError("Please enter first name");
                    return;
                }
                else if (last_name.getText().toString().isEmpty()){
                    input_last_name.setError("Please enter last name");
                    return;
                }
                else if (email.getText().toString().isEmpty()||!Util.isValidEmail(email.getText().toString())) {
                    if (email.getText().toString().isEmpty()) {
                        input_email.setError("Please enter email");
                        return;
                    }
                    if (!Util.isValidEmail(email.getText().toString()))
                    {
                        input_email.setError("Please enter valid  email");
                        return;
                    }
                }
                else if (password.getText().toString().isEmpty()){
                    input_password.setError("Please select password");
                    return;
                }
                else if (password_confirm.getText().toString().isEmpty()){
                    input_password_confirm.setError("Please confirm password");
                    return;
                }

                else if (phone.getText().toString().isEmpty()){
                    input_phone.setError("Please enter phone number");
                    return;
                }

                else if (salesTeam.getText().toString().isEmpty()){
                    input_salesteam.setError("Please select salesTeam name");
                    return;
                }
                else if (company.getText().toString().isEmpty()){
                    input_company.setError("Please select company name");
                    return;
                }
                else {
                    //do nothing
                }
                int companyId=AppSession.companyList.get(AppSession.companyNameList.indexOf(company.getText().toString())).getId();

                int salesTeamId=salesTeamArrayList.get(salesTeamNameList.indexOf(salesTeam.getText().toString())).getId();
                new AddContacts().execute(Appconfig.TOKEN,first_name.getText().toString(),last_name.getText().toString(),
                        email.getText().toString(),password.getText().toString(), password_confirm.getText().toString(),
                        phone.getText().toString(),String.valueOf(salesTeamId),String.valueOf(companyId),encoded_image);



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
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] imageInByte = stream.toByteArray();
                long lengthbmp = imageInByte.length;
                Log.i("image length", String.valueOf(lengthbmp));
                if (lengthbmp>512000)
                {
                    Toast.makeText(getApplicationContext(),"image size should be less than 500kb",Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    upload.setImageBitmap(bitmap);
                }
                encoded_image=getStringImage(bitmap);
               // upload.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Crashlytics.logException(e);

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
        SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String get_url;
        if (text_url != null) {
            get_url = text_url + "/user/salesteams?token=";
        } else {
            get_url = Appconfig.SALESTEAM_URL;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET,get_url+token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

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
                            Crashlytics.logException(e);

                        }


                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }

        } ;
        MyVolleySingleton.getInstance(AddContactsActivity.this).getRequestQueue().add(stringRequest);

    }

    private class AddContacts extends AsyncTask<String,Void,String>
    {
        ProgressDialog dialog;
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(AddContactsActivity.this);
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
            String first_name = params[1];
            String last_name =params[2];
            String email = params[3];
            String password = params[4];
            String password_confirmation=params[5];
            String phone=params[6];
            String sales_team_id=params[7];
            String company_id=params[8];
            String image=params[9];

            URL url = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("first_name", first_name);
                jsonObject.put("last_name", last_name);
                jsonObject.put("email", email);
                jsonObject.put("password", password);
                jsonObject.put("password_confirmation", password_confirmation);
                jsonObject.put("phone_number", phone);
                jsonObject.put("sales_team_id", sales_team_id);
                jsonObject.put("company_id", company_id);
                jsonObject.put("avatar",image);
                SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String post_contacts_url;
                if (text_url != null) {
                    post_contacts_url = text_url + "/user/post_customer?token=";
                } else {
                    post_contacts_url = Appconfig.CONTACTS_POST_URL;
                }
                url = new URL(post_contacts_url+tok);
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
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                    json = new JSONObject(response);
                    jsonresponse = json.getString("success");

                } else {
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line ="";
                    while ((line = bufferedReader.readLine()) != null) {
                        response += line;
                    }
                    json = new JSONObject(response);
                    jsonresponse = json.getString("error");

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
            {  /*first_name.setText("");
                email.setText("");
                phone.setText("");
                last_name.setText("");
                password.setText("");
                password_confirm.setText("");*/
                final Snackbar snackbar = Snackbar.make(linearLayout, "Added 1 item Succesfully!", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
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
                final Snackbar snackbar = Snackbar.make(linearLayout, "Item not added! Try Again", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
            }
        }
    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        return encodedImage;
    }

}
