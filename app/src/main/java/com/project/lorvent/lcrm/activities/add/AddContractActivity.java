package com.project.lorvent.lcrm.activities.add;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
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
import com.project.lorvent.lcrm.adapters.ContractAdapter;
import com.project.lorvent.lcrm.models.Company;
import com.project.lorvent.lcrm.models.Contracts;
import com.project.lorvent.lcrm.models.Staff;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
    TextInputLayout input_start_date, input_end_date,input_layout_customer, input_layout_responsible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contract);
        linearLayout=(LinearLayout)findViewById(R.id.layout);
        Connection.getStaffList(Appconfig.TOKEN,AddContractActivity.this);
        Connection.getCustomerList(Appconfig.TOKEN,AddContractActivity.this);
        Connection.getDateSettings(Appconfig.TOKEN,AddContractActivity.this);
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
        input_start_date = (TextInputLayout) findViewById(R.id.input_layout_start_date);
        input_end_date = (TextInputLayout) findViewById(R.id.input_layout_end_date);
        input_layout_customer = (TextInputLayout) findViewById(R.id.input_layout_customer);
        input_layout_responsible = (TextInputLayout) findViewById(R.id.input_layout_responsible);

        customer=(BetterSpinner)findViewById(R.id.company);
        responsible=(BetterSpinner)findViewById(R.id.responsible);
        description=(EditText)findViewById(R.id.description);
        start_date=(EditText)findViewById(R.id.start_date);
        end_date=(EditText)findViewById(R.id.end_date);


           submit=(Button)findViewById(R.id.submit);

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
      /*  start_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(start_date.getWindowToken(), 0);
                    showDialog(START_DATE_PICKER_ID);
                }
            }
        });*/
        end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(end_date.getWindowToken(), 0);
                showDialog(END_DATE_PICKER_ID);

            }
        });
  /*      end_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(end_date.getWindowToken(), 0);
                    showDialog(END_DATE_PICKER_ID);
                }
            }
        });*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            start_date.setShowSoftInputOnFocus(false);
            end_date.setShowSoftInputOnFocus(false);
        }
        ArrayAdapter<String> customerArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,AppSession.companyNameList);
        customerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        customer.setAdapter(customerArrayAdapter);


        ArrayAdapter<String> responsibleArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,AppSession.responsibleNameList);
        responsibleArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        responsible.setAdapter(responsibleArrayAdapter);
        start_date.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_start_date.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        end_date.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_end_date.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                description.setError(null);


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
                input_layout_customer.setError("");
            }
        });
        responsible.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_layout_responsible.setError("");
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (start_date.getText().toString().isEmpty()) {
                    input_start_date.setError("Please enter start date");
                    return;
                }
                else if (end_date.getText().toString().isEmpty()) {
                    input_end_date.setError("Please select end date");
                    return;
                }
                else if (description.getText().toString().isEmpty()) {
                    description.setError("Please enter description");
                    return;
                }
                else if (customer.getText().toString().isEmpty()) {
                    input_layout_customer.setError("Please select customer");
                    return;
                }
                else if (responsible.getText().toString().isEmpty()) {
                    input_layout_responsible.setError("Please select responsible staff");
                    return;
                }
                else {
                    //do nothing
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                int companyId=AppSession.companyList.get(AppSession.companyNameList.indexOf(customer.getText().toString())).getId();
                int responsibleId=AppSession.responsibleList.get(AppSession.responsibleNameList.indexOf(responsible.getText().toString())).getId();

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
          //  Log.i("date",start_year+""+start_month+""+start_day);
            Date date = new Date(year - 1900, month, dayOfMonth);
            start_date.setText(Connection.getFormatedDate(date));
            start_date.clearFocus();
        }
    };
    final DatePickerDialog.OnDateSetListener end_listener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            end_year=year;
            end_month=month;
            end_day=dayOfMonth;

            Date date = new Date(year - 1900, month, dayOfMonth);
            end_date.setText(Connection.getFormatedDate(date));
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

                SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String post_contract_url;
                if (text_url != null) {
                    post_contract_url = text_url + "/user/post_contract?token=";
                } else {
                    post_contract_url = Appconfig.POST_CONTRACT_URL;
                }
                url = new URL(post_contract_url+tok);
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
                new GetAllContract().execute(Appconfig.TOKEN);
                Connection.getDashboard(Appconfig.TOKEN,AddContractActivity.this);
     /*           start_date.setText("");
                end_date.setText("");
                description.setText("");*/
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
                SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String get_url;
                if (text_url != null) {
                    get_url = text_url + "/user/contracts?token=";
                } else {
                    get_url = Appconfig.CONTRACT_URL;
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
            }
            return response;
        }
        @Override
        protected void onPostExecute(String response) {
            try {
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
                Crashlytics.logException(e);

            }
        }

    }
}
