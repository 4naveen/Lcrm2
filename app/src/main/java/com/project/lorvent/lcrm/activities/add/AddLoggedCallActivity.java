package com.project.lorvent.lcrm.activities.add;

import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.adapters.LoggedCallAdapter;
import com.project.lorvent.lcrm.models.LoggedCalls;
import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.Connection;
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
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class AddLoggedCallActivity extends AppCompatActivity {
    BetterSpinner contacts,responsible;
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
        Connection.getStaffList(Appconfig.TOKEN,AddLoggedCallActivity.this);
        Connection.getCustomerList(Appconfig.TOKEN,AddLoggedCallActivity.this);
        Connection.getDateSettings(Appconfig.TOKEN,AddLoggedCallActivity.this);
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
        ArrayAdapter<String> contactArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,AppSession.companyNameList);
        contactArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        contacts.setAdapter(contactArrayAdapter);

        ArrayAdapter<String> responsibleArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,AppSession.responsibleNameList);
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

        call_date.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_date.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        call_summary.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_call_summary.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        duration.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_duration.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_contact.setError("");
            }
        });
        responsible.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_responsible.setError("");
            }
        });
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

                else if (contacts.getText().toString().isEmpty()){
                    input_contact.setError("Please enter contact");
                    return;
                }
                else if (responsible.getText().toString().isEmpty()){
                    input_responsible.setError("Please enter responsible person");
                    return;
                }

                else if (duration.getText().toString().isEmpty()){
                    input_duration.setError("Please enter call duration");
                    return;
                }
                else {
                    //do nothing
                }
                int companyId=AppSession.companyList.get(AppSession.companyNameList.indexOf(contacts.getText().toString())).getId();
                int responsibleId=AppSession.responsibleList.get(AppSession.responsibleNameList.indexOf(responsible.getText().toString())).getId();
                 new AddCall().execute(Appconfig.TOKEN,call_date.getText().toString(),call_summary.getText().toString(),String.valueOf(companyId),
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
            Date date = new Date(year - 1900, month, dayOfMonth);
            call_date.setText(Connection.getFormatedDate(date));
            call_date.setSelection(call_date.getText().length());
            call_date.clearFocus();
        }
    };

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
                SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String post_loggedcall_url;
                if (text_url != null) {
                    post_loggedcall_url = text_url + "/user/post_call?token=";
                } else {
                    post_loggedcall_url = Appconfig.POST_CALL_URL;
                }
                url = new URL(post_loggedcall_url+tok);
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
                new GetLoggedCalls().execute(Appconfig.TOKEN);
              /*  call_date.setText("");
                call_summary.setText("");
                duration.setText("");*/
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
                SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String get_url;
                if (text_url != null) {
                    get_url = text_url + "/user/calls?token=";
                } else {
                    get_url = Appconfig.CALL_URL;
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
            } catch (IOException e) {
                e.printStackTrace();
                Crashlytics.logException(e);

            }
            return response;
        }
        @Override
        protected void onPostExecute(String response) {
            try {
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
                Crashlytics.logException(e);

            }
        }

    }
}
