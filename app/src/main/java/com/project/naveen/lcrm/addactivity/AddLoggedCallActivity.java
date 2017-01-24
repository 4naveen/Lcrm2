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
import android.support.design.widget.TextInputLayout;
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
import com.project.naveen.lcrm.adapters.LoggedCallAdapter;
import com.project.naveen.lcrm.models.Company;
import com.project.naveen.lcrm.models.LoggedCalls;
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
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class AddLoggedCallActivity extends AppCompatActivity {
    BetterSpinner contacts,responsible;
    ArrayList<String> contactNameList,responsibleNameList;
    ArrayList<Company>contactsList;
    ArrayList<Staff>responsibleList;
    Button submit;
    SimpleDateFormat simpleDateFormat;
    static final int DATE_PICKER_ID=111;
    int  current_year,current_month,current_day;
    String current_month_text,date_format;
    EditText call_date,call_summary,duration;
    LinearLayout linearLayout;
    TextInputLayout input_date,input_call_summary,input_contact,input_responsible,input_duration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_logged_call);
        ActionBar actionBar=getSupportActionBar();
        call_date=(EditText)findViewById(R.id.date);
        call_summary=(EditText)findViewById(R.id.call_sumaary);
        duration=(EditText)findViewById(R.id.duration);
        contacts=(BetterSpinner)findViewById(R.id.contacts);
        responsible=(BetterSpinner)findViewById(R.id.responsible);

        input_date=(TextInputLayout)findViewById(R.id.input_layout_date);
        input_call_summary=(TextInputLayout)findViewById(R.id.input_layout_call_sumaary);
        input_contact=(TextInputLayout)findViewById(R.id.input_layout_customer);
        input_responsible=(TextInputLayout)findViewById(R.id.input_layout_responsible);
        input_duration=(TextInputLayout)findViewById(R.id.input_layout_duration);

        current_year=Calendar.getInstance().get(Calendar.YEAR);
        current_month=Calendar.getInstance().get(Calendar.MONTH);
        current_day=Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        linearLayout=(LinearLayout)findViewById(R.id.layout);
        submit=(Button)findViewById(R.id.submit);
        if (actionBar != null) {
            actionBar.setTitle("Add Call");
        }
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

        }
        contactsList=new ArrayList<>();
        contactNameList=new ArrayList<>();
        responsibleList=new ArrayList<>();
        responsibleNameList=new ArrayList<>();
        getContactList(Appconfig.TOKEN);
        getresponsibleList(Appconfig.TOKEN);
        getDateSettings(Appconfig.TOKEN);
        ArrayAdapter<String> contactArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,contactNameList);
        contactArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        contacts.setAdapter(contactArrayAdapter);

        ArrayAdapter<String> responsibleArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,responsibleNameList);
        responsibleArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        responsible.setAdapter(responsibleArrayAdapter);
        call_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(call_date.getWindowToken(), 0);
                showDialog(DATE_PICKER_ID);

            }
        });
   /*     date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(date.getWindowToken(), 0);
                    showDialog(DATE_PICKER_ID);
                }
            }
        });*/
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (call_date.getText().toString().isEmpty())
                {
                    input_date.setError("Please enter date");
                    return;
                }
                else if (call_summary.getText().toString().isEmpty()){
                    input_call_summary.setError("Please enter call summary");
                    return;
                }
                else if (duration.getText().toString().isEmpty()){
                    input_duration.setError("Please enter call duration");
                    return;
                }
                else if (contacts.getText().toString().isEmpty()){
                    input_contact.setError("Please enter contact");
                    return;
                }
                else if (responsible.getText().toString().isEmpty()){
                    input_responsible.setError("Please enter responsible person");
                    return;
                }


                else {
                    //do nothing
                }
                int contactId=contactsList.get(contactNameList.indexOf(contacts.getText().toString())).getId();
                int responsibleId=responsibleList.get(responsibleNameList.indexOf(responsible.getText().toString())).getId();
                 new AddCall().execute(Appconfig.TOKEN,call_date.getText().toString(),call_summary.getText().toString(),String.valueOf(contactId),
                 String.valueOf(responsibleId),duration.getText().toString());
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(submit.getWindowToken(), 0);
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
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id==DATE_PICKER_ID)
        {
            return new DatePickerDialog(AddLoggedCallActivity.this, date_listener,current_year,current_month,current_day);
        }

        return null;
    }

    final DatePickerDialog.OnDateSetListener date_listener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            current_year=year;
            current_month=month;
            current_month_text=new DateFormatSymbols().getMonths()[current_month];
            current_day=dayOfMonth;
           // date.setText(current_month_text+current_day+","+current_year);
            if (date_format.equals("Y-d-m"))
            {
                simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'), Locale.ENGLISH);
                Date date=new Date(year-1900,month,dayOfMonth);
                String text_date=simpleDateFormat.format(date);
                Log.i("text_date",text_date);
                call_date.setText(text_date);
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
                call_date.setText(text_date);
            }
            if (date_format.equals("d.m.Y"))
            {
                simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);
                Date date=new Date(year-1900,month,dayOfMonth);
                String text_date=simpleDateFormat.format(date);
                Log.i("text_date",text_date);
                call_date.setText(text_date);
            }
            if (date_format.equals("d/m/Y"))
            {
                simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);
                Date date=new Date(year-1900,month,dayOfMonth);
                String text_date=simpleDateFormat.format(date);
                Log.i("text_date",text_date);
                call_date.setText(text_date);
            }
            if (date_format.equals( "m/d/Y"))
            {
                simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);
                Date date=new Date(year-1900,month,dayOfMonth);
                String text_date=simpleDateFormat.format(date);
                Log.i("text_date",text_date);
                call_date.setText(text_date);

            }
            call_date.setSelection(call_date.getText().length());
            call_date.clearFocus();
        }
    };


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
        MyVolleySingleton.getInstance(AddLoggedCallActivity.this).getRequestQueue().add(stringRequest);


    }

    public void getContactList(String token)
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
                            contactNameList.add(object.getString("name"));
                            contactsList.add(company);
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
        MyVolleySingleton.getInstance(AddLoggedCallActivity.this).getRequestQueue().add(stringRequest);

    }

    public void getresponsibleList(String token)
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
        MyVolleySingleton.getInstance(AddLoggedCallActivity.this).getRequestQueue().add(stringRequest);


    }

    class AddCall extends AsyncTask<String,Void,String>
    {
        ProgressDialog dialog;
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(AddLoggedCallActivity.this);
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
            String date = params[1];
            String call_summary =params[2] ;
            String companyId = params[3];
            String resp_staff_id = params[4];
            String duration=params[5];
            URL url = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("date", date);
                jsonObject.put("call_summary", call_summary);
                jsonObject.put("company_id", companyId);
                jsonObject.put("resp_staff_id", resp_staff_id);
                jsonObject.put("duration", duration);

                url = new URL(Appconfig.POST_CALL_URL+tok);
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
            {      new GetLoggedCalls().execute(Appconfig.TOKEN);
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
    public class GetLoggedCalls extends AsyncTask<String,Void,String>
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
                url = new URL(Appconfig.CALL_URL+tok);
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
                Log.i("response--",response);
                JSONObject jsonObject=new JSONObject(response);
                JSONArray jsonArray=jsonObject.getJSONArray("calls");
                AppSession.loggedCallsArrayList.clear();
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    LoggedCalls loggedCalls=new LoggedCalls();
                    loggedCalls.setId(object.getInt("id"));
                    loggedCalls.setCompany(object.getString("company"));
                    loggedCalls.setDate(object.getString("date"));
                    loggedCalls.setResponsible(object.getString("user"));
                    loggedCalls.setCall_summary(object.getString("call_summary"));
                    AppSession.loggedCallsArrayList.add(loggedCalls);

                }
                LoggedCallAdapter loggedCallAdapter= new LoggedCallAdapter(AddLoggedCallActivity.this,AppSession.loggedCallsArrayList);
                AppSession.loggedCall_recyclerView.setAdapter(loggedCallAdapter);
                AppSession.loggedCall_recyclerView.setItemAnimator(new DefaultItemAnimator());
                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(AddLoggedCallActivity.this, LinearLayoutManager.VERTICAL,false);
                AppSession.loggedCall_recyclerView.setLayoutManager(lmanager);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
