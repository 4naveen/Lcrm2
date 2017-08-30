package com.project.lorvent.lcrm.activities.add;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.project.lorvent.lcrm.adapters.LeadAdapter;
import com.project.lorvent.lcrm.models.Company;
import com.project.lorvent.lcrm.models.Leads;
import com.project.lorvent.lcrm.models.SalesTeam;
import com.project.lorvent.lcrm.models.Staff;
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

public class AddLeadActivity extends AppCompatActivity {
    EditText opportunity,email;
    Button submit;
    ArrayList<String> CompanyNameList,countryNameList,sales_personNameList,sales_teamNameList;
    TextInputLayout input_opp,input_email,input_customer,input_sales_team,input_sales_person,input_country;
    ArrayList<Company>companyList;
    ArrayList<Staff>sales_personList;
    ArrayList<SalesTeam>sales_teamList;
    ArrayList<Country>countryList;
    BetterSpinner customer,sales_person,sales_team,country;
    String token;
    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lead);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Add Lead");
        }
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        token= Appconfig.TOKEN;
        opportunity=(EditText)findViewById(R.id.opportnity);
        email=(EditText)findViewById(R.id.email);
        input_opp=(TextInputLayout)findViewById(R.id.input_layout_opp);
        input_email=(TextInputLayout)findViewById(R.id.input_layout_email);
        input_sales_person=(TextInputLayout)findViewById(R.id.input_layout_salesperson);
        input_country=(TextInputLayout)findViewById(R.id.input_layout_country);
        input_customer=(TextInputLayout)findViewById(R.id.input_layout_customer);
        input_sales_team=(TextInputLayout)findViewById(R.id.input_layout_sales_team);
        companyList=new ArrayList<>();
        CompanyNameList=new ArrayList<>();

        sales_personList=new ArrayList<>();
        sales_personNameList=new ArrayList<>();

        sales_teamList=new ArrayList<>();
        sales_teamNameList=new ArrayList<>();

        countryList=new ArrayList<>();
        countryNameList=new ArrayList<>();

        Connection.getCustomerList(Appconfig.TOKEN,AddLeadActivity.this);
        Connection.getSalesTeamList(Appconfig.TOKEN,AddLeadActivity.this);
        Connection.getStaffList(Appconfig.TOKEN,AddLeadActivity.this);

        getCountriesList(token);
        customer=(BetterSpinner)findViewById(R.id.company);
        country=(BetterSpinner)findViewById(R.id.country);
        sales_person=(BetterSpinner)findViewById(R.id.salesperson);
        sales_team=(BetterSpinner)findViewById(R.id.salesteam);

        ArrayAdapter<String> customerArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,AppSession.companyNameList);
        customerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        customer.setAdapter(customerArrayAdapter);
        ArrayAdapter<String> countryArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,countryNameList);
        countryArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        country.setAdapter(countryArrayAdapter);
        ArrayAdapter<String> salesteamArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,AppSession.sales_teamNameList);
        salesteamArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sales_team.setAdapter(salesteamArrayAdapter);
        ArrayAdapter<String> salespersonArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,AppSession.sales_personNameList);
        salespersonArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sales_person.setAdapter(salespersonArrayAdapter);

        submit=(Button)findViewById(R.id.submit);
        linearLayout=(LinearLayout) findViewById(R.id.layout);

        opportunity.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_opp.setError("");

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
        customer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_customer.setError("");
            }
        });
        sales_person.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_sales_person.setError("");
            }
        });
        sales_team.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_sales_team.setError("");
            }
        });
        country.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_country.setError("");
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (opportunity.getText().toString().isEmpty())
                {
                    input_opp.setError("Please enter opportunity name");

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
                else if (customer.getText().toString().isEmpty()){
                    input_customer.setError("Please select customer name");
                    return;
                }
                else if (country.getText().toString().isEmpty()){
                    input_country.setError("Please select your country");
                    return;
                }

                else if (sales_team.getText().toString().isEmpty()){
                    input_sales_team.setError("Please select salesTeam name");
                    return;
                }
                else if (sales_person.getText().toString().isEmpty()){
                    input_sales_person.setError("Please select sales person");
                    return;
                }
                else {
                    //do nothing
                }
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                int salesTeamId=AppSession.salesTeamList.get(AppSession.sales_teamNameList.indexOf(sales_team.getText().toString())).getId();
                int companyId=AppSession.companyList.get(AppSession.companyNameList.indexOf(customer.getText().toString())).getId();
                int salesPersonId=AppSession.salesPersonList.get(AppSession.sales_personNameList.indexOf(sales_person.getText().toString())).getId();

                String countryId=countryList.get(countryNameList.indexOf(country.getText().toString())).getId();

                 new AddLead().execute(token,opportunity.getText().toString(),email.getText().toString(),String.valueOf(companyId),
                         String.valueOf(salesTeamId),String.valueOf(salesPersonId),countryId);
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

    class AddLead extends AsyncTask<String,Void,String>
    {
        ProgressDialog dialog;
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(AddLeadActivity.this);
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
            String opp = params[1];
            String email =params[2] ;
            String companyId = params[3];
            String salesTeamId = params[4];
            String SalesPersonId=params[5];
            String  countryId=params[6];
            URL url = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("opportunity", opp);
                jsonObject.put("email", email);
                jsonObject.put("customer_id", companyId);
                jsonObject.put("sales_team_id", salesTeamId);
                jsonObject.put("sales_person_id", SalesPersonId);
                    jsonObject.put("country_id", countryId);

                SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String post_lead_url;
                if (text_url != null) {
                    post_lead_url = text_url + "/user/post_lead?token=";
                } else {
                    post_lead_url = Appconfig.POST_LEAD_URL;
                }
                url = new URL(post_lead_url+tok);
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
                    //Get Values from JSONobject
                    //System.out.println("success=" + json.get("success"));

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
                new GetAllLeadTask().execute(token);
                Connection.getDashboard(Appconfig.TOKEN,AddLeadActivity.this);
           /*     opportunity.setText("");
                email.setText("");*/
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

    public void getCountriesList(String token)
    {
        SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String get_url;
        if (text_url != null) {
            get_url = text_url + "/user/countries?token=";
        } else {
            get_url = Appconfig.COUNTRY_URL;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET,get_url+token,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {

                        JSONObject jsonObject=new JSONObject(response);
                        JSONObject object=jsonObject.getJSONObject("countries");

                        for (int i=0;i<246;i++)
                        {      Country country=new Country();
                              country.setId(String.valueOf(i+1));
                            country.setCountryName(object.getString(String.valueOf(i+1)));
                            countryList.add(country);
                            countryNameList.add(object.getString(String.valueOf(i+1)));
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
        MyVolleySingleton.getInstance(AddLeadActivity.this).getRequestQueue().add(stringRequest);


    }
    private class Country{
        String id,countryName;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCountryName() {
            return countryName;
        }

        public void setCountryName(String countryName) {
            this.countryName = countryName;
        }
    }

    class GetAllLeadTask extends AsyncTask<String,Void,String>
    {   String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            URL url;
            HttpURLConnection connection;
            try {
                SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String get_url;
                if (text_url != null) {
                    get_url = text_url + "/user/leads?token=";
                } else {
                    get_url = Appconfig.LEADS_URL;
                }
                url = new URL(get_url+params[0]);
                connection = (HttpURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String temp;
                while ((temp=br.readLine())!=null)
                {
                    buffer.append(temp);
                }
                response=buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
        @Override
        protected void onPostExecute(String response) {
            try {
                JSONObject jsonObject=new JSONObject(response);
                JSONArray jsonArray=jsonObject.getJSONArray("leads");
                AppSession.leadArrayList.clear();
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    Leads lead=new Leads();
                    lead.setOpportunity(object.getString("opportunity"));
                    lead.setSales_team(object.getString("salesteam"));
                    lead.setId(object.getInt("id"));
                    lead.setEmail(object.getString("email"));
                    AppSession.leadArrayList.add(lead);
                }
                LeadAdapter mAdapter = new LeadAdapter(getApplicationContext(), AppSession.leadArrayList);

                AppSession.lead_recyclerView.setAdapter(mAdapter);
                AppSession.lead_recyclerView.setItemAnimator(new DefaultItemAnimator());
                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false);
                //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);

                AppSession.lead_recyclerView.setLayoutManager(lmanager);


            } catch (JSONException e) {
                e.printStackTrace();
                Crashlytics.logException(e);

            }
        }}
}
