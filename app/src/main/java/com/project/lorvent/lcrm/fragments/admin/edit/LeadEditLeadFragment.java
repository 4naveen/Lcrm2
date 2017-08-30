package com.project.lorvent.lcrm.fragments.admin.edit;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.project.lorvent.lcrm.fragments.admin.details.LeadDetailsFragment;
import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.Connection;
import com.project.lorvent.lcrm.utils.MyVolleySingleton;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.models.Company;
import com.project.lorvent.lcrm.models.SalesTeam;
import com.project.lorvent.lcrm.models.Staff;
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

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class LeadEditLeadFragment extends Fragment {
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
    LinearLayout frameLayout;

    public LeadEditLeadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_edit_lead, container, false);
        Connection.getCustomerList(Appconfig.TOKEN,getActivity());
        Connection.getSalesTeamList(Appconfig.TOKEN,getActivity());
        Connection.getStaffList(Appconfig.TOKEN,getActivity());
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        lead_id=getArguments().getString("lead_id");
        token=Appconfig.TOKEN;
        frameLayout=(LinearLayout) v.findViewById(R.id.layout);

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
        getCountriesList(token);
        ArrayAdapter<String> customerArrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item, AppSession.companyNameList);
        customerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        customer.setAdapter(customerArrayAdapter);

        ArrayAdapter<String> salesteamArrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,AppSession.sales_teamNameList);
        customerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sales_team.setAdapter(salesteamArrayAdapter);

        ArrayAdapter<String> countryArrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,countryNameList);
        countryArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        country.setAdapter(countryArrayAdapter);

        ArrayAdapter<String> salespersonArrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,AppSession.sales_personNameList);
        customerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sales_person.setAdapter(salespersonArrayAdapter);

        submit=(Button)v.findViewById(R.id.submit);
        new LeadDetailsTask().execute(token,lead_id);
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

                else if (email.getText().toString().isEmpty()){
                    input_email.setError("Please enter email");
                    return;
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
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                int salesTeamId=AppSession.salesTeamList.get(AppSession.sales_teamNameList.indexOf(sales_team.getText().toString())).getId();
                int companyId=AppSession.companyList.get(AppSession.companyNameList.indexOf(customer.getText().toString())).getId();
                int salesPersonId=AppSession.salesPersonList.get(AppSession.sales_personNameList.indexOf(sales_person.getText().toString())).getId();
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

                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String edit_url;
                if (text_url != null) {
                    edit_url= text_url + "/user/edit_lead?token=";
                } else {
                    edit_url= Appconfig.EDIT_LEAD_URL;
                }
                url = new URL(edit_url+ tok);

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
                Bundle bundle=new Bundle();
                bundle.putString("lead_id", String.valueOf(lead_id));
                Fragment fragment1 = new LeadDetailsFragment();
                FragmentTransaction trans1 = getFragmentManager().beginTransaction();
                fragment1.setArguments(bundle);
                trans1.replace(R.id.frame, fragment1);
                trans1.addToBackStack(null);
                trans1.commit();
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

    public void getCountriesList(String token)
    { StringRequest stringRequest = new StringRequest(Request.Method.GET, Appconfig.COUNTRY_URL+token,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {

                        JSONObject jsonObject=new JSONObject(response);
                        JSONObject object=jsonObject.getJSONObject("countries");

                        for (int i=0;i<246;i++)
                        { Country country=new Country();
                            country.setId(String.valueOf(i+1));
                            country.setCountryName(object.getString(String.valueOf(i+1)));
                            countryList.add(country);
                            countryNameList.add(object.getString(String.valueOf(i+1)));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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

            URL url;
            try {

                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String detail_url;
                if (text_url != null) {
                    detail_url= text_url + "/user/lead?token=";
                } else {
                    detail_url= Appconfig.LEAD_DETAILS_URL;
                }

                url = new URL(detail_url+tok+"&lead_id="+lead_id);
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
                    customer.setText(jsonObject1.getString("customer"));
                    country.setText(jsonObject1.getString("country"));
                    sales_person.setText(jsonObject1.getString("sales_person"));
                    JSONObject jsonObject2=jsonObject1.getJSONObject("salesteam");
                    sales_team.setText(jsonObject2.getString("salesteam"));


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }
}
