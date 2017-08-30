package com.project.lorvent.lcrm.activities.add;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
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

import com.crashlytics.android.Crashlytics;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.adapters.CompanyAdapter;
import com.project.lorvent.lcrm.models.Company;
import com.project.lorvent.lcrm.models.SalesTeam;
import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.Connection;
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

import javax.net.ssl.HttpsURLConnection;

public class AddCompanyActivity extends AppCompatActivity {
    EditText company_name,phone,email,address;
    BetterSpinner sales_team,main_contact_person;
    ArrayList <String>main_contact_personList,salesTeamNameList;
    ArrayList<SalesTeam>salesTeamArrayList;
    Button submit;
    ImageView upload;
    LinearLayout linearLayout;
    String encoded_image;
    TextInputLayout input_company_name,input_email,input_address,input_phone,input_main_contact_person,input_salesteam;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_company);

        Connection.getContactList(Appconfig.TOKEN,AddCompanyActivity.this);
        Connection.getSalesTeamList(Appconfig.TOKEN,AddCompanyActivity.this);
        linearLayout=(LinearLayout)findViewById(R.id.layout);

        company_name=(EditText)findViewById(R.id.company_name);


        input_company_name=(TextInputLayout)findViewById(R.id.input_layout_company_name);
        input_email=(TextInputLayout)findViewById(R.id.input_layout_email);
        input_address=(TextInputLayout)findViewById(R.id.input_layout_address);
        input_phone=(TextInputLayout)findViewById(R.id.input_layout_phone);
        input_main_contact_person=(TextInputLayout)findViewById(R.id.input_layout_main_contact_person);
        input_salesteam=(TextInputLayout)findViewById(R.id.input_layout_salesteam);
        main_contact_personList=new ArrayList<>();
        salesTeamNameList=new ArrayList<>();
        salesTeamArrayList=new ArrayList<>();
        email=(EditText)findViewById(R.id.email);
        phone=(EditText)findViewById(R.id.phone);
        address=(EditText)findViewById(R.id.address);
        main_contact_person=(BetterSpinner) findViewById(R.id.mcp);
        sales_team=(BetterSpinner) findViewById(R.id.salesteam);
        submit=(Button)findViewById(R.id.submit);
        upload=(ImageView)findViewById(R.id.upload);
        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 600) {
            upload.setImageResource(R.drawable.upload);

        }
        else
        {
            upload.setImageResource(R.drawable.upload_mob);
        }
       // upload=(ImageView)findViewById(R.id.upload);

        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        actionBar.setTitle("Add Company");

        ArrayAdapter<String> salesteamArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, AppSession.sales_teamNameList);
        salesteamArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sales_team.setAdapter(salesteamArrayAdapter);

        ArrayAdapter<String> mcpArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item, AppSession.customerNameList);
        mcpArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        main_contact_person.setAdapter(mcpArrayAdapter);


        company_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_company_name.setError("");

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
        address.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_address.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
       /* main_contact_person(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_main_contact_person.setError("");
            }
        });*/
        main_contact_person.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                         String product_name_text=productNameList.get(position);
                input_main_contact_person.setError("");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        sales_team.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_salesteam.setError("");
            }
        });
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
                    input_company_name.setError("Please enter company name");
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
                else if (address.getText().toString().isEmpty()){
                    input_address.setError("Please enter address");
                    return;
                }
                else if (phone.getText().toString().isEmpty()){
                    input_phone.setError("Please enter phone number");
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
                int salesTeamId = AppSession.salesTeamList.get(AppSession.sales_teamNameList.indexOf(sales_team.getText().toString())).getId();
                new AddCompany().execute(Appconfig.TOKEN,company_name.getText().toString(),
                        email.getText().toString(),address.getText().toString(),String.valueOf(salesTeamId),
                        main_contact_person.getText().toString(),phone.getText().toString(),encoded_image);
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
                //Log.d("image--", String.valueOf(bitmap));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] imageInByte = stream.toByteArray();
                long lengthbmp = imageInByte.length;
                //Log.i("image length", String.valueOf(lengthbmp));
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

    private class AddCompany extends AsyncTask<String,Void,String>
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
            String image=params[7];

            URL url = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("name", name);
                jsonObject.put("email", email);
                jsonObject.put("address", address);
                jsonObject.put("sales_team_id", sales_team_id);
                jsonObject.put("main_contact_person", main_contact_person);
                jsonObject.put("phone", phone);
                jsonObject.put("avatar",image);
               // Log.i("jsonobject request", String.valueOf(jsonObject));
                SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String post_compnay_url;
                if (text_url != null) {
                    post_compnay_url = text_url + "/user/post_company?token=";
                } else {
                    post_compnay_url = Appconfig.COMPANY_POST_URL;
                }
                url = new URL(post_compnay_url+tok);
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
                       // Log.d("output lines", line);
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
                        //Log.d("output lines", line);
                    }
                   // Log.i("response", response);
                    json = new JSONObject(response);
                    jsonresponse = json.getString("error");
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
                new GetAllCompanyTask().execute(Appconfig.TOKEN);
                Connection.getDashboard(Appconfig.TOKEN,AddCompanyActivity.this);
           /*     company_name.setText("");
                email.setText("");
                phone.setText("");
                address.setText("");*/
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
    private class GetAllCompanyTask extends AsyncTask<String,Void,String>
    {   String response;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            URL url;
            HttpURLConnection connection ;
            String tok=params[0];
            try {
                SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String get_url;
                if (text_url != null) {
                    get_url = text_url + "/user/companies?token=";
                } else {
                    get_url = Appconfig.COMPANY_URL;
                }
                url = new URL(get_url+tok);
                connection = (HttpURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String temp;
                while ((temp=br.readLine())!=null)
                {
                    buffer.append(temp);
                }
                response=buffer.toString();
                //  Log.i("response in d",response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
        @Override
        protected void onPostExecute(String response) {
            if (dialog!=null&&dialog.isShowing()){dialog.dismiss();}
            try {
                //  Log.i("response--",response);
                JSONObject jsonObject=new JSONObject(response);
                JSONArray jsonArray=jsonObject.getJSONArray("companies");
                AppSession.companyArrayList.clear();
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    Company company=new Company();
                    company.setId(object.getInt("id"));
                    company.setName(object.getString("name"));
                    company.setCustomer(object.getString("customer"));
                    company.setPhone(object.getString("phone"));
                    AppSession.companyArrayList.add(company);
                    //  Log.i("leadslist--",lead.getName());
                }

                AppSession.company_recyclerView.setAdapter(new CompanyAdapter(AddCompanyActivity.this, AppSession.companyArrayList));
                AppSession.company_recyclerView.setItemAnimator(new DefaultItemAnimator());
                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(AddCompanyActivity.this, LinearLayoutManager.VERTICAL,false);
                //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);
                AppSession.company_recyclerView.setLayoutManager(lmanager);


            } catch (JSONException e) {
                e.printStackTrace();
                Crashlytics.logException(e);

            }
        }
    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
       // Log.i("encodedImageString",encodedImage);
        return encodedImage;
    }

}
