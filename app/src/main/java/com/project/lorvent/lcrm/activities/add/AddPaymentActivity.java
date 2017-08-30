package com.project.lorvent.lcrm.activities.add;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;
import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.MyVolleySingleton;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.models.Invoice;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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

public class AddPaymentActivity extends AppCompatActivity {
    EditText payment_date,amount_received;
    String token,date_format;
    Button submit;
    public static int current_year, current_month, current_day;
    public static String current_month_text;
    ArrayList<String> invoice_numberList,pay_methodList;
    BetterSpinner invoice_number,pay_method;
    SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment);
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Add opportunity");
        }
        token= Appconfig.TOKEN;
        current_year= Calendar.getInstance().get(Calendar.YEAR);
        current_day= Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        current_month=Calendar.getInstance().get(Calendar.MONTH);
        amount_received=(EditText)findViewById(R.id.amt_received);
        payment_date=(EditText)findViewById(R.id.pay_date);
        invoice_numberList=new ArrayList<>();
        pay_methodList=new ArrayList<>();
        for (int i=0;i<AppSession.invoicesArrayList.size();i++)
        {
            Invoice invoice= AppSession.invoicesArrayList.get(i);
              invoice_numberList.add(invoice.getInvoice_number());
        }
        pay_methodList.add("Cash");
        pay_methodList.add("Check");
        pay_methodList.add("Bank Account");
        pay_methodList.add("Credit Card");
        getDateSettings(token);

        invoice_number=(BetterSpinner)findViewById(R.id.invoice_number);
        pay_method=(BetterSpinner)findViewById(R.id.pay_method);
        ArrayAdapter<String> customerArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,invoice_numberList);
        customerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        invoice_number.setAdapter(customerArrayAdapter);
        ArrayAdapter<String> salesteamArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,pay_methodList);
        customerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        pay_method.setAdapter(salesteamArrayAdapter);
        payment_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(payment_date.getWindowToken(), 0);
                DatePickerDialog d = new DatePickerDialog(AddPaymentActivity.this, mDateSetListener, current_year, current_month, current_day);
                d.show();



            }
        });

        submit=(Button)findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int invoiceId=AppSession.invoicesArrayList.get(invoice_numberList.indexOf(invoice_number.getText().toString())).getId();

                  new AddInvoicePatyment().execute(token,String.valueOf(invoiceId),payment_date.getText().toString(),pay_method.getText().toString(),
                          amount_received.getText().toString());
               // Toast.makeText(getApplicationContext(),"this is dummy Gui test",Toast.LENGTH_LONG).show();

            }
        });

    }

    DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            current_year = year;
            current_month = monthOfYear;
            current_day = dayOfMonth;
            //current_month_text=new DateFormatSymbols().getMonths()[current_month];
            //payment_date.setText(current_month_text+current_day+","+current_year);
            if (date_format.equals("Y-d-m"))
            {
                simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'), Locale.ENGLISH);
                Date date=new Date(year-1900,monthOfYear,dayOfMonth);
                String text_date=simpleDateFormat.format(date);

                payment_date.setText(text_date);
            }
                           /* if (date_format.equals("F j,Y"))
                            {
                                simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);

                            }*/
            if (date_format.equals("d.m.Y."))
            {
                simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);
                Date date=new Date(year-1900,monthOfYear,dayOfMonth);
                String text_date=simpleDateFormat.format(date);

                payment_date.setText(text_date);
            }
            if (date_format.equals("d.m.Y"))
            {
                simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);
                Date date=new Date(year-1900,monthOfYear,dayOfMonth);
                String text_date=simpleDateFormat.format(date);

                payment_date.setText(text_date);
            }
            if (date_format.equals("d/m/Y"))
            {
                simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);
                Date date=new Date(year-1900,monthOfYear,dayOfMonth);
                String text_date=simpleDateFormat.format(date);

                payment_date.setText(text_date);
            }
            if (date_format.equals( "m/d/Y"))
            {
                simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);
                Date date=new Date(year-1900,monthOfYear,dayOfMonth);
                String text_date=simpleDateFormat.format(date);

                payment_date.setText(text_date);
            }
            payment_date.setSelection(payment_date.getText().length());
            payment_date.clearFocus();
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {finish();

            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void getDateSettings(String token) {
        SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String get_url;
        if (text_url != null) {
            get_url = text_url + "/user/settings?token=";
        } else {
            get_url = Appconfig.SETTINGS_URL;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET,get_url+token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject=new JSONObject(response);
                            JSONObject pre_settings=jsonObject.getJSONObject("settings");
                            date_format=(pre_settings.getString("date_format"));

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
        MyVolleySingleton.getInstance(AddPaymentActivity.this).getRequestQueue().add(stringRequest);

    }
    class AddInvoicePatyment extends AsyncTask<String,Void,String>
    {
        ProgressDialog dialog;
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(AddPaymentActivity.this);
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
            String invoice_id = params[1];
            String pay_date = params[2];
            String pay_method = params[3];
            String amount_received=params[4];

            URL url = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("invoice_id", invoice_id);
                jsonObject.put("payment_date", pay_date);
                jsonObject.put("payment_method", pay_method);
                jsonObject.put("payment_received", amount_received);
                SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String post_pay_url;
                if (text_url != null) {
                    post_pay_url = text_url + "/user/post_invoice_payment?token=";
                } else {
                    post_pay_url = Appconfig.POST_PAYLOG_URL;
                }

                url = new URL(post_pay_url+tok);
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
            if (result!=null)
            {
                Toast.makeText(getApplicationContext(),""+result,Toast.LENGTH_LONG).show();
                //finish();
            }
        }
    }
}
