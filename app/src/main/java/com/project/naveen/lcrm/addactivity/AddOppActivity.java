package com.project.naveen.lcrm.addactivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.naveen.lcrm.AppSession;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.MyVolleySingleton;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.RecyclerTouchListener;
import com.project.naveen.lcrm.adapters.OpportunityAdapter;
import com.project.naveen.lcrm.models.Company;
import com.project.naveen.lcrm.models.Contacts;
import com.project.naveen.lcrm.models.Opportunity;
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
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class AddOppActivity extends AppCompatActivity {
    EditText opp,email,nextAction,expectedClosing;
    TextInputLayout input_opp,input_email,input_next_action,input_expected_closing,input_customer,input_sales_team,input_satges;
    int next_day,next_month,next_year,expected_day,expected_month,expected_year;
    static final int NEXT_DATE_PICKER_ID=111;
    static final int ENCLOSE_DATE_PICKER_ID=112;
    String token,date_format;
    Button submit;
    String next_month_text,expected_month_text;
    ArrayList<SalesTeam>sales_teamList;
    ArrayList<Company> companyList;
    ArrayList<String>sales_teamNameList,companyNameList,stagesList;
    BetterSpinner customer,sales_team,stages;
    SimpleDateFormat simpleDateFormat;
    CoordinatorLayout coordinator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_opp);
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Add opportunity");
        }
        coordinator=(CoordinatorLayout)findViewById(R.id.coordinator);
        token= Appconfig.TOKEN;
        Log.i("token--",token);
        opp=(EditText)findViewById(R.id.opp);
        email=(EditText)findViewById(R.id.email);
        input_opp=(TextInputLayout)findViewById(R.id.input_layout_opp);
        input_email=(TextInputLayout)findViewById(R.id.input_layout_email);
        input_next_action=(TextInputLayout)findViewById(R.id.input_layout_next_action);
        input_expected_closing=(TextInputLayout)findViewById(R.id.input_layout_expected_closing);
        input_customer=(TextInputLayout)findViewById(R.id.input_layout_customer);
        input_sales_team=(TextInputLayout)findViewById(R.id.input_layout_salesteam);
        input_satges=(TextInputLayout)findViewById(R.id.input_layout_stages);
        stagesList=new ArrayList<>();
       /* getSalesTeamList(token);
        getCustomerList(token);*/

        stagesList.add("New");
        stagesList.add("Qualification");
        stagesList.add("Proposition");
        stagesList.add("Negotiation");
        stagesList.add("Won");
        stagesList.add("Lost");
        stagesList.add("Expired");

        customer=(BetterSpinner)findViewById(R.id.company);
        sales_team=(BetterSpinner)findViewById(R.id.salesteam);
        stages=(BetterSpinner)findViewById(R.id.stages);


        ArrayAdapter<String> stagesArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,stagesList);
        stagesArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        stages.setAdapter(stagesArrayAdapter);
        ArrayAdapter<String> customerArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, AppSession.companyNameList);
        customerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        customer.setAdapter(customerArrayAdapter);
        ArrayAdapter<String> salesteamArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,AppSession.sales_teamNameList);
        customerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sales_team.setAdapter(salesteamArrayAdapter);
        nextAction=(EditText)findViewById(R.id.next_action);
        expectedClosing=(EditText)findViewById(R.id.expected_closing);
        submit=(Button)findViewById(R.id.submit);
              getDateSettings(token);
        next_year= Calendar.getInstance().get(Calendar.YEAR);
        expected_year= Calendar.getInstance().get(Calendar.YEAR);
        next_month=Calendar.getInstance().get(Calendar.MONTH);
        expected_month=Calendar.getInstance().get(Calendar.MONTH);
        next_day=Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        expected_day=Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        nextAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(nextAction.getWindowToken(), 0);
                showDialog(NEXT_DATE_PICKER_ID);

            }
        });
        nextAction.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(nextAction.getWindowToken(), 0);
                    showDialog(NEXT_DATE_PICKER_ID);
                }
            }
        });
        expectedClosing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(nextAction.getWindowToken(), 0);
                showDialog(ENCLOSE_DATE_PICKER_ID);

            }
        });
        expectedClosing.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(nextAction.getWindowToken(), 0);
                    showDialog(ENCLOSE_DATE_PICKER_ID);
                }
            }
        });
         submit.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
            if (opp.getText().toString().isEmpty())
            {
                input_opp.setError("Please enter opportunity name");

                return;
            }

            else if (email.getText().toString().isEmpty()){
                input_email.setError("Please enter email");
                return;
            }
            else if (nextAction.getText().toString().isEmpty()){
                input_next_action.setError("Please enter nextAction date");
                return;
            }
            else if (expectedClosing.getText().toString().isEmpty()){
                input_expected_closing.setError("Please enter expectedClosing date");
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
            else if (stages.getText().toString().isEmpty()){
                input_satges.setError("Please select stages");
                return;
            }
                 else {
                //do nothing
            }
                //Log.i("nameid-", String.valueOf(sales_teamNameList.indexOf(sales_team.getText().toString())));
                 int salesTeamId=AppSession.salesTeamList.get(AppSession.sales_teamNameList.indexOf(sales_team.getText().toString())).getId();
                // Log.i("salesteamId-", String.valueOf(salesTeamId));
                 int companyId=AppSession.companyList.get(AppSession.companyNameList.indexOf(customer.getText().toString())).getId();

                 new AddOppTask().execute(token,opp.getText().toString(),email.getText().toString(), String.valueOf(companyId),
                          String.valueOf(salesTeamId),nextAction.getText().toString(),expectedClosing.getText().toString(),stages.getText().toString());
//                 Toast.makeText(getApplicationContext(),"this is dummy Gui test",Toast.LENGTH_LONG).show();
                 InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                 imm.hideSoftInputFromWindow(submit.getWindowToken(), 0);
             }
         });

    }
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id==NEXT_DATE_PICKER_ID)
        {
            return new DatePickerDialog(AddOppActivity.this, next_listener,next_year,next_month,next_day);
        }
        if (id==ENCLOSE_DATE_PICKER_ID)
        {
            return new DatePickerDialog(AddOppActivity.this, expected_listener,expected_year,expected_month,expected_day);
        }
        return null;
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
        final DatePickerDialog.OnDateSetListener next_listener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                next_year=year;
                next_month=month;
                next_day=dayOfMonth;
//                next_month_text=new DateFormatSymbols().getMonths()[next_month];
                Log.i("date",next_year+""+next_month+""+next_day);
                if (date_format.equals("Y-d-m"))
                {
                    simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);
                    Date date=new Date(year-1900,month,dayOfMonth);
                    String text_date=simpleDateFormat.format(date);
                    Log.i("text_date",text_date);

                    nextAction.setText(text_date);
                }
                           /* if (date_format.equals("F j,Y"))
                            {
                                simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);

                            }*/
                if (date_format.equals("d.m.Y."))
                {
                    simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);
                    Date date=new Date(year-1900,month,dayOfMonth);
                    String text_date=simpleDateFormat.format(date);
                    Log.i("text_date",text_date);

                    nextAction.setText(text_date);
                }
                if (date_format.equals("d.m.Y"))
                {
                    simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);
                    Date date=new Date(year-1900,month,dayOfMonth);
                    String text_date=simpleDateFormat.format(date);
                    Log.i("text_date",text_date);

                    nextAction.setText(text_date);
                }
                if (date_format.equals("d/m/Y"))
                {
                    simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);
                    Date date=new Date(year-1900,month,dayOfMonth);
                    String text_date=simpleDateFormat.format(date);
                    Log.i("text_date",text_date);
                    nextAction.setText(text_date);
                }
                if (date_format.equals( "m/d/Y"))
                {
                    simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);
                    Date date=new Date(year-1900,month,dayOfMonth);
                    String text_date=simpleDateFormat.format(date);
                    Log.i("text_date",text_date);

                    nextAction.setText(text_date);
                }
//                SimpleDateFormat simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);

                nextAction.clearFocus();
            }
        };
        final DatePickerDialog.OnDateSetListener expected_listener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                expected_year=year;
                expected_month=month;
                expected_day=dayOfMonth;
//                expected_month_text=new DateFormatSymbols().getMonths()[expected_month];
                //     expectedClosing.setText(expected_month_text+expected_day+","+expected_year);
                Log.i("date",next_year+""+next_month+""+next_day);

//                SimpleDateFormat simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);
                Log.i("date",date_format.replace('Y','y'));
                if (date_format.equals("Y-d-m"))
                {
                    simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);
                    Date date=new Date(year-1900,month,dayOfMonth);
                    String text_date=simpleDateFormat.format(date);
                    Log.i("text_date",text_date);
                    expectedClosing.setText(text_date);
                }
                           /* if (date_format.equals("F j,Y"))
                            {
                                simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);

                            }*/
                if (date_format.equals("d.m.Y."))
                {
                    simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);
                    Date date=new Date(year-1900,month,dayOfMonth);
                    String text_date=simpleDateFormat.format(date);
                    Log.i("text_date",text_date);
                    expectedClosing.setText(text_date);
                }
                if (date_format.equals("d.m.Y"))
                {
                    simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);
                    Date date=new Date(year-1900,month,dayOfMonth);
                    String text_date=simpleDateFormat.format(date);
                    Log.i("text_date",text_date);
                    expectedClosing.setText(text_date);
                }
                if (date_format.equals("d/m/Y"))
                {
                    simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);
                    Date date=new Date(year-1900,month,dayOfMonth);
                    String text_date=simpleDateFormat.format(date);
                    Log.i("text_date",text_date);
                    expectedClosing.setText(text_date);
                }
                if (date_format.equals( "m/d/Y"))
                {
                    simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);
                    Date date=new Date(year-1900,month,dayOfMonth);
                    String text_date=simpleDateFormat.format(date);
                    Log.i("text_date",text_date);
                    expectedClosing.setText(text_date);

                }
                expectedClosing.clearFocus();
            }
        };
    class AddOppTask extends AsyncTask<String,Void,String>
    {
        ProgressDialog dialog;
         HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(AddOppActivity.this);
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
            String nextAction = params[5];
            String expectedACtion=params[6];
            String stages=params[7];

            URL url = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("opportunity", opp);
                jsonObject.put("email", email);
                jsonObject.put("customer_id", companyId);
                jsonObject.put("sales_team_id", salesTeamId);
                jsonObject.put("next_action", nextAction);
                jsonObject.put("expected_closing", expectedACtion);
                jsonObject.put("stages", stages);
Log.i("jsonobject--",jsonObject.toString());
                url = new URL(Appconfig.POST_OPPPORTUNITY_URL+tok);
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
                {          new OpportunityTask().execute(token);

                    final Snackbar snackbar = Snackbar.make(coordinator, "Added 1 item Succesfully!", Snackbar.LENGTH_LONG);
                    View v = snackbar.getView();
                    v.setMinimumWidth(1000);
                    TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.YELLOW);
                    snackbar.show();
                }
                else {
                    final Snackbar snackbar = Snackbar.make(coordinator, "Item not added! Try Again", Snackbar.LENGTH_LONG);
                    View v = snackbar.getView();
                    v.setMinimumWidth(1000);
                    TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.YELLOW);
                    snackbar.show();
                }

        }
    }
    private void getDateSettings(String token) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Appconfig.SETTINGS_URL+token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            // Log.i("response--", String.valueOf(response));

                            JSONObject jsonObject=new JSONObject(response);
                            JSONObject pre_settings=jsonObject.getJSONObject("settings");
                              date_format=(pre_settings.getString("date_format"));

                                Log.i("date--",date_format);


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
        MyVolleySingleton.getInstance(AddOppActivity.this).getRequestQueue().add(stringRequest);


    }

 /*   public void getCustomerList(String token)
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
                            companyNameList.add(object.getString("name"));
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
        MyVolleySingleton.getInstance(AddOppActivity.this).getRequestQueue().add(stringRequest);

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
        MyVolleySingleton.getInstance(AddOppActivity.this).getRequestQueue().add(stringRequest);

    }*/

    public class OpportunityTask extends AsyncTask<String,Void,String>
    {   String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            URL url ;
            HttpURLConnection connection ;
            String tok=params[0];
            try {
                url = new URL(Appconfig.OPPORTUNITY_URL+tok);
                connection = (HttpURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String temp;
                while ((temp=br.readLine())!=null)
                {
                    buffer.append(temp);
                }
                response=buffer.toString();
//                  Log.i("response in d",response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
        @Override
        protected void onPostExecute(String response) {
            try {
//                  Log.i("response--",response);
                JSONObject jsonObject=new JSONObject(response);
                JSONArray jsonArray=jsonObject.getJSONArray("opportunities");
                AppSession.opportunityArrayList.clear();
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    Opportunity opportunity=new Opportunity();
                    opportunity.setId(object.getInt("id"));
                    opportunity.setOpportunity(object.getString("opportunity"));
                    opportunity.setNext_action_date(object.getString("next_action"));
                    opportunity.setExpected_revenue(object.getString("expected_revenue"));
                    opportunity.setStages(object.getString("stages"));
                    opportunity.setProbability(object.getString("probability"));
                    opportunity.setSalesTeam(object.getString("salesteam"));
                    opportunity.setMeetings(object.getString("meetings"));
                    opportunity.setCompany(object.getString("company"));
                    opportunity.setCalls(object.getString("calls"));

                    AppSession.opportunityArrayList.add(opportunity);

                }

                OpportunityAdapter mAdapter = new OpportunityAdapter(getApplicationContext(),AppSession.opportunityArrayList);
                AppSession.opportunity_recyclerView.setAdapter(mAdapter);

                AppSession.opportunity_recyclerView.setItemAnimator(new DefaultItemAnimator());
                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false);

                AppSession.opportunity_recyclerView.setLayoutManager(lmanager);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }
}
