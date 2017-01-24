package com.project.naveen.lcrm.leads;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.MyVolleySingleton;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.addactivity.AddLeadActivity;
import com.project.naveen.lcrm.models.Company;
import com.project.naveen.lcrm.models.SalesTeam;
import com.project.naveen.lcrm.models.Staff;
import com.weiwangcn.betterspinner.library.BetterSpinner;

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
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditLeadFragment extends Fragment {
    EditText opportunity,email;
    TextInputLayout input_opp,input_email,input_customer,input_sales_team,input_sales_person,input_country;
    Fragment fragment = null;
    static ArrayList<String> CompanyNameList,countryNameList,sales_personNameList,sales_teamNameList;
    BetterSpinner customer,sales_team,country,sales_person;
    ArrayList<Company>companyList;
    ArrayList<Staff>sales_personList;
    ArrayList<SalesTeam>sales_teamList;
    ArrayList<Country>countryList;
    String token,lead_id;
    Button submit;
    FrameLayout frameLayout;

    public EditLeadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_edit_lead, container, false);
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        lead_id=getArguments().getString("lead_id");
        token=Appconfig.TOKEN;
        frameLayout=(FrameLayout)v.findViewById(R.id.layout);

        setHasOptionsMenu(true);
        if (actionBar != null) {
            actionBar.setTitle("Edit Lead");
            actionBar.setHomeButtonEnabled(false);
        }
        opportunity=(EditText)v.findViewById(R.id.opp);
        email=(EditText)v.findViewById(R.id.email);
        input_opp=(TextInputLayout)v.findViewById(R.id.input_layout_opp);
        input_email=(TextInputLayout)v.findViewById(R.id.input_layout_email);
        input_sales_person=(TextInputLayout)v.findViewById(R.id.input_layout_salesperson);
        input_country=(TextInputLayout)v.findViewById(R.id.input_layout_country);
        input_customer=(TextInputLayout)v.findViewById(R.id.input_layout_customer);
        input_sales_team=(TextInputLayout)v.findViewById(R.id.input_layout_sales_team);
        companyList=new ArrayList<>();
        CompanyNameList=new ArrayList<>();

        sales_personList=new ArrayList<>();
        sales_personNameList=new ArrayList<>();

        sales_teamList=new ArrayList<>();
        sales_teamNameList=new ArrayList<>();

        countryList=new ArrayList<>();
        countryNameList=new ArrayList<>();
  /*      for (int i=0;i<5;i++)
        {
            customerList.add("xyz Pvt Ltd.");
            sales_teamList.add("Krishna");
            countryList.add("India");
        }*/


        customer=(BetterSpinner)v.findViewById(R.id.company);
        sales_team=(BetterSpinner)v.findViewById(R.id.salesteam);
        country=(BetterSpinner)v.findViewById(R.id.country);
        sales_person=(BetterSpinner)v.findViewById(R.id.salesperson);

        getSalesTeamList(token);
        getCustomerList(token);
        getCountriesList(token);
        getSalesPersonList(token);

        ArrayAdapter<String> customerArrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,CompanyNameList);
        customerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        customer.setAdapter(customerArrayAdapter);

        ArrayAdapter<String> salesteamArrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,sales_teamNameList);
        customerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sales_team.setAdapter(salesteamArrayAdapter);

        ArrayAdapter<String> countryArrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,countryNameList);
        countryArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        country.setAdapter(countryArrayAdapter);

        ArrayAdapter<String> salespersonArrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,sales_personNameList);
        customerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sales_person.setAdapter(salespersonArrayAdapter);

        submit=(Button)v.findViewById(R.id.submit);
        new LeadDetailsTask().execute(token,lead_id);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (opportunity.getText().toString().isEmpty())
                {
                    input_opp.setError("Please enter opportunity name");

                    return;
                }

                else if (email.getText().toString().isEmpty()){
                    input_email.setError("Please enter email");
                    return;
                }

                else if (country.getText().toString().isEmpty()){
                    input_country.setError("Please select your country");
                    return;
                }
                else if (customer.getText().toString().isEmpty()){
                    input_customer.setError("Please select customer name");
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
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                // Log.i("nameid-", String.valueOf(sales_teamNameList.indexOf(sales_team.getText().toString())));
                int salesTeamId=sales_teamList.get(sales_teamNameList.indexOf(sales_team.getText().toString())).getId();
                //Log.i("salesteamId-", String.valueOf(salesTeamId));
                int companyId=companyList.get(CompanyNameList.indexOf(customer.getText().toString())).getId();
                // Log.i("salesteamId-", String.valueOf(salesTeamId));
                int salesPersonId=sales_personList.get(sales_personNameList.indexOf(sales_person.getText().toString())).getId();

                String countryId=countryList.get(countryNameList.indexOf(country.getText().toString())).getId();

                new EditLead().execute(token,opportunity.getText().toString(),email.getText().toString(),String.valueOf(companyId),
                        String.valueOf(salesTeamId),String.valueOf(salesPersonId),countryId,lead_id);
            }
        });
        return v;
    }


    class EditLead extends AsyncTask<String,Void,String>
    {
        ProgressDialog dialog;
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
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
            String lead_id=params[7];
            URL url = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("lead_id",lead_id);
                jsonObject.put("opportunity", opp);
                jsonObject.put("email", email);
                jsonObject.put("customer_id", companyId);
                jsonObject.put("sales_team_id", salesTeamId);
                jsonObject.put("sales_person_id", SalesPersonId);
                jsonObject.put("country_id", countryId);

                url = new URL(Appconfig.EDIT_LEAD_URL+tok);
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
            if (result.equals("success"))
            {
                final Snackbar snackbar = Snackbar.make(frameLayout, "Updated Succesfully!", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
            }
            else {
                final Snackbar snackbar = Snackbar.make(frameLayout, "Item not updated! Try Again", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
            }
        }
    }
    public void getCustomerList(String token)
    { StringRequest stringRequest = new StringRequest(Request.Method.GET, Appconfig.COMPANY_URL+token,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        Log.i("response--", String.valueOf(response));

                        JSONObject jsonObject=new JSONObject(response);
                        JSONArray jsonArray=jsonObject.getJSONArray("companies");
                        for (int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject object=jsonArray.getJSONObject(i);
                            Company company=new Company();
                            company.setId(object.getInt("id"));
                            company.setName(object.getString("name"));
                            CompanyNameList.add(object.getString("name"));
                            companyList.add(company);
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
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);

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
                                sales_teamNameList.add(object.getString("salesteam"));
                                sales_teamList.add(salesTeam);
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
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);

    }

    public void getSalesPersonList(String token)
    { StringRequest stringRequest = new StringRequest(Request.Method.GET, Appconfig.STAFF_URL+token,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        // Log.i("response--", String.valueOf(response));

                        JSONObject jsonObject=new JSONObject(response);
                        JSONArray jsonArray=jsonObject.getJSONArray("staffs");
                        for (int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject object=jsonArray.getJSONObject(i);
                            Staff staff=new Staff();
                            staff.setId(object.getInt("id"));
                            staff.setName(object.getString("full_name"));
                            sales_personNameList.add(object.getString("full_name"));
                            sales_personList.add(staff);
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
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);


    }
    public void getCountriesList(String token)
    { StringRequest stringRequest = new StringRequest(Request.Method.GET, Appconfig.COUNTRY_URL+token,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        // Log.i("response--", String.valueOf(response));

                        JSONObject jsonObject=new JSONObject(response);
                        JSONObject object=jsonObject.getJSONObject("countries");

                        for (int i=0;i<246;i++)
                        { Country country=new Country();
                            country.setId(String.valueOf(i+1));
                            country.setCountryName(object.getString(String.valueOf(i+1)));
                            countryList.add(country);
                            countryNameList.add(object.getString(String.valueOf(i+1)));
                        }
                        Log.i("coutryList",countryNameList.toString());
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
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);


    }
    public class Country{
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

    private class LeadDetailsTask extends AsyncTask<String,Void,String>
    {
        ProgressDialog dialog;
        HttpURLConnection conn;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
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
            String lead_id=params[1];
            Log.i("lead_id=",lead_id);

            URL url;
            try {

                url = new URL(Appconfig.LEAD_DETAILS_URL+tok+"&lead_id="+lead_id);
                conn = (HttpURLConnection) url.openConnection();
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
                        Log.d("output lines", line);
                    }
                    Log.i("response",response);
                    json = new JSONObject(response);
                    jsonResponse=json.getString("error");
                    //System.out.println("error=" + json.get("error"));
                    //succes = json.getString("success");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonResponse;
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result);
                JSONArray jsonArray=jsonObject.getJSONArray("lead");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObject1=jsonArray.getJSONObject(i);
                    opportunity.setText(jsonObject1.getString("opportunity"));
                    email.setText(jsonObject1.getString("email"));
                    sales_person.setText(jsonObject1.getString("sales_person"));
                    sales_team.setText(jsonObject1.getString("salesteam"));
                    customer.setText(jsonObject1.getString("company_name"));

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }
}
