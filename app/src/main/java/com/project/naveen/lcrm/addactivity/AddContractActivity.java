package com.project.naveen.lcrm.addactivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
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
import com.project.naveen.lcrm.adapters.ContractAdapter;
import com.project.naveen.lcrm.models.Company;
import com.project.naveen.lcrm.models.Contracts;
import com.project.naveen.lcrm.models.Staff;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class AddContractActivity extends AppCompatActivity {
    ArrayList<String> companyNameList,responsibleNameList;
    ArrayList<Company> companyList;
    ArrayList<Staff>responsibleList;
    BetterSpinner customer,responsible;
    EditText start_date,end_date,description;
    Button submit;
    String date_format;
    int start_day,start_month,start_year,end_day,end_month,end_year;
    static final int START_DATE_PICKER_ID=111;
    static final int END_DATE_PICKER_ID=112;
    SimpleDateFormat simpleDateFormat;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contract);
        linearLayout=(LinearLayout)findViewById(R.id.layout);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Add Contract");
        }
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        companyNameList=new ArrayList<>();
        responsibleNameList=new ArrayList<>();
        companyList=new ArrayList<>();
        responsibleList=new ArrayList<>();
        getCustomerList(Appconfig.TOKEN);
        getResponsibleList(Appconfig.TOKEN);
        customer=(BetterSpinner)findViewById(R.id.company);
        responsible=(BetterSpinner)findViewById(R.id.responsible);
        description=(EditText)findViewById(R.id.description);
        start_date=(EditText)findViewById(R.id.start_date);
        end_date=(EditText)findViewById(R.id.end_date);
           submit=(Button)findViewById(R.id.submit);
        getDateSettings(Appconfig.TOKEN);

        start_year= Calendar.getInstance().get(Calendar.YEAR);
        end_year= Calendar.getInstance().get(Calendar.YEAR);
        start_month=Calendar.getInstance().get(Calendar.MONTH);
        end_month=Calendar.getInstance().get(Calendar.MONTH);
        start_day=Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        end_day=Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(start_date.getWindowToken(), 0);
                showDialog(START_DATE_PICKER_ID);

            }
        });
        start_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(start_date.getWindowToken(), 0);
                    showDialog(START_DATE_PICKER_ID);
                }
            }
        });
        end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(end_date.getWindowToken(), 0);
                showDialog(END_DATE_PICKER_ID);

            }
        });
        end_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(end_date.getWindowToken(), 0);
                    showDialog(END_DATE_PICKER_ID);
                }
            }
        });
        ArrayAdapter<String> customerArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,companyNameList);
        customerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        customer.setAdapter(customerArrayAdapter);


        ArrayAdapter<String> responsibleArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,responsibleNameList);
        responsibleArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        responsible.setAdapter(responsibleArrayAdapter);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                int companyId=companyList.get(companyNameList.indexOf(customer.getText().toString())).getId();
                int responsibleId=responsibleList.get(responsibleNameList.indexOf(responsible.getText().toString())).getId();

                new AddContract().execute(Appconfig.TOKEN,start_date.getText().toString(),end_date.getText().toString(),
                             description.getText().toString(),String.valueOf(companyId),String.valueOf(responsibleId));

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {

                finish();
                break;
            }

        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id==START_DATE_PICKER_ID)
        {
            return new DatePickerDialog(AddContractActivity.this, start_listener,start_year,start_month,start_day);
        }
        if (id==END_DATE_PICKER_ID)
        {
            return new DatePickerDialog(AddContractActivity.this, end_listener,end_year,end_month,end_day);
        }
        return null;
    }
    final DatePickerDialog.OnDateSetListener start_listener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            start_year=year;
            start_month=month;
            start_day=dayOfMonth;
//                next_month_text=new DateFormatSymbols().getMonths()[next_month];
            Log.i("date",start_year+""+start_month+""+start_day);
            if (date_format.equals("Y-d-m"))
            {
                simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'), Locale.ENGLISH);
                Date date=new Date(year-1900,month,dayOfMonth);
                String text_date=simpleDateFormat.format(date);
                Log.i("text_date",text_date);

                start_date.setText(text_date);
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

                start_date.setText(text_date);
            }
            if (date_format.equals("d.m.Y"))
            {
                simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);
                Date date=new Date(year-1900,month,dayOfMonth);
                String text_date=simpleDateFormat.format(date);
                Log.i("text_date",text_date);

                start_date.setText(text_date);
            }
            if (date_format.equals("d/m/Y"))
            {
                simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);
                Date date=new Date(year-1900,month,dayOfMonth);
                String text_date=simpleDateFormat.format(date);
                Log.i("text_date",text_date);

                start_date.setText(text_date);
            }
            if (date_format.equals( "m/d/Y"))
            {
                simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);
                Date date=new Date(year-1900,month,dayOfMonth);
                String text_date=simpleDateFormat.format(date);
                Log.i("text_date",text_date);

                start_date.setText(text_date);
            }
//                SimpleDateFormat simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);

            start_date.clearFocus();
        }
    };
    final DatePickerDialog.OnDateSetListener end_listener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            end_year=year;
            end_month=month;
            end_day=dayOfMonth;
//                end_month_text=new DateFormatSymbols().getMonths()[end_month];
            //     endClosing.setText(end_month_text+end_day+","+end_year);
            Log.i("date",start_year+""+start_month+""+start_day);

//                SimpleDateFormat simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);
            Log.i("date",date_format.replace('Y','y'));
            if (date_format.equals("Y-d-m"))
            {
                simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);
                Date date=new Date(year-1900,month,dayOfMonth);
                String text_date=simpleDateFormat.format(date);
                Log.i("text_date",text_date);
                end_date.setText(text_date);
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
                end_date.setText(text_date);
            }
            if (date_format.equals("d.m.Y"))
            {
                simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);
                Date date=new Date(year-1900,month,dayOfMonth);
                String text_date=simpleDateFormat.format(date);
                Log.i("text_date",text_date);
                end_date.setText(text_date);
            }
            if (date_format.equals("d/m/Y"))
            {
                simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);
                Date date=new Date(year-1900,month,dayOfMonth);
                String text_date=simpleDateFormat.format(date);
                Log.i("text_date",text_date);
                end_date.setText(text_date);
            }
            if (date_format.equals( "m/d/Y"))
            {
                simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);
                Date date=new Date(year-1900,month,dayOfMonth);
                String text_date=simpleDateFormat.format(date);
                Log.i("text_date",text_date);
                end_date.setText(text_date);

            }
            end_date.clearFocus();
        }
    };
    class AddContract extends AsyncTask<String,Void,String>
    {
        ProgressDialog dialog;
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(AddContractActivity.this);
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
            String start_date = params[1];
            String end_date=params[2] ;
            String description = params[3];
            String companyId = params[4];
            String resp_staff_id = params[5];

            URL url = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("start_date", start_date);
                jsonObject.put("end_date", end_date);
                jsonObject.put("description", description);
                jsonObject.put("company_id",companyId );
                jsonObject.put("resp_staff_id", resp_staff_id);
                url = new URL(Appconfig.POST_CONTRACT_URL+tok);
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
                new GetAllContract().execute(Appconfig.TOKEN);
                ;
                final Snackbar snackbar = Snackbar.make(linearLayout, "Added 1 item Succesfully!", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
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
        MyVolleySingleton.getInstance(AddContractActivity.this).getRequestQueue().add(stringRequest);


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
        MyVolleySingleton.getInstance(AddContractActivity.this).getRequestQueue().add(stringRequest);

    }
    public void getResponsibleList(String token)
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
                            responsibleNameList.add(object.getString("full_name"));
                            responsibleList.add(staff);
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
        MyVolleySingleton.getInstance(AddContractActivity.this).getRequestQueue().add(stringRequest);


    }
    public class GetAllContract extends AsyncTask<String,Void,String>
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
                url = new URL(Appconfig.CONTRACT_URL+tok);
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
                JSONArray jsonArray=jsonObject.getJSONArray("contracts");
                AppSession.contractArrayList.clear();
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    Contracts contracts=new Contracts();
                    contracts.setId(object.getInt("id"));
                    contracts.setStart_date(object.getString("start_date"));
                    contracts.setResponsible(object.getString("user"));
                    contracts.setDescription(object.getString("description"));
                    contracts.setContact(object.getString("name"));

                    AppSession.contractArrayList.add(contracts);

                }

                AppSession.contract_recyclerView.setAdapter(new ContractAdapter(AppSession.contractArrayList,AddContractActivity.this));
                AppSession.contract_recyclerView.setItemAnimator(new DefaultItemAnimator());
                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(AddContractActivity.this, LinearLayoutManager.VERTICAL,false);
                //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);
                AppSession.contract_recyclerView.setLayoutManager(lmanager);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }
}
