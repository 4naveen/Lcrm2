package com.project.naveen.lcrm.detailsactivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TableRow;
import android.widget.TextView;

import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;



public class DetailPaymentActivity extends AppCompatActivity {
    TextView invoice_number,pay_received,pay_method,pay_date,customer,sales_person,pay_number;
    String invoice_pay_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_payment);
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (actionBar != null) {
            actionBar.setTitle("Payment Information");
        }
        invoice_number=(TextView)findViewById(R.id.invoice_number);
        pay_number=(TextView)findViewById(R.id.pay_number);
        pay_received=(TextView)findViewById(R.id.pay_received);
        pay_method=(TextView)findViewById(R.id.pay_method);
        pay_date=(TextView)findViewById(R.id.pay_date);
        customer=(TextView)findViewById(R.id.customer);
        sales_person=(TextView)findViewById(R.id.salesperson);
        invoice_pay_id=getIntent().getStringExtra("pay_id");
        Log.i("invoice_pay_id",invoice_pay_id);
        new InvoiceDetailsTask().execute(Appconfig.TOKEN,invoice_pay_id);
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
    private class InvoiceDetailsTask extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;
        HttpURLConnection conn;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(DetailPaymentActivity.this);
            dialog.setMessage("Loading, please wait");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "", jsonResponse = "";
            JSONObject json;
            BufferedReader bufferedReader;
            String tok = params[0];
            String invoice_pay_id = params[1];
            URL url;
            try {

                url = new URL(Appconfig.DETAILS_PAYLOG_URL + tok + "&invoice_payment_id=" + invoice_pay_id);
                conn = (HttpURLConnection) url.openConnection();
                //Log.i("res code--",""+conn.getResponseCode());
                int responseCode = conn.getResponseCode();
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

                    jsonResponse = response;

                } else {
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        response += line;
                        //  Log.d("output lines", line);
                    }
                    Log.i("response", response);
                    json = new JSONObject(response);
                    jsonResponse = json.getString("error");
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
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result);
                JSONArray array = jsonObject.getJSONArray("invoice_payment");
                for (int i=0;i<array.length();i++)
                {
                    JSONObject jsonObject1 = array.getJSONObject(i);
                    invoice_number.setText(jsonObject1.getString("invoice_number"));
                    customer.setText(String.valueOf(jsonObject1.getString("customer")));
                    pay_date.setText(jsonObject1.getString("payment_date"));
                    pay_number.setText(jsonObject1.getString("payment_number"));
                    pay_received.setText(jsonObject1.getString("payment_received"));
                    pay_method.setText(jsonObject1.getString("payment_method"));
                    sales_person.setText(jsonObject1.getString("salesperson"));

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();

        }

    }
}
