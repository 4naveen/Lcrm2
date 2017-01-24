package com.project.naveen.lcrm.customer.detailactivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
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

public class InvoiceDetailActivity extends AppCompatActivity {
    TextView invoice_number,customer,invoice_date,due_date,payment_term,Salesteam,Salesperson,untaxedAmount,taxes,total,discount,final_price,unpaid_amount,status;
    TableLayout tab_products;
    String invoiceId,token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_detail);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Invoices Details");
        }
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        tab_products = (TableLayout)findViewById(R.id.table_products);

        invoice_number=(TextView)findViewById(R.id.invoice_number);
        customer=(TextView)findViewById(R.id.customer);
        invoice_date=(TextView)findViewById(R.id.invoice_date);
        due_date=(TextView)findViewById(R.id.due_date);
        payment_term=(TextView)findViewById(R.id.pay_term);
        Salesteam=(TextView)findViewById(R.id.salesteam);
        Salesperson=(TextView)findViewById(R.id.salesperson);
        untaxedAmount=(TextView)findViewById(R.id.un_amount);
        taxes=(TextView)findViewById(R.id.taxes);
        total=(TextView)findViewById(R.id.total);
        discount=(TextView)findViewById(R.id.discount);
        final_price=(TextView)findViewById(R.id.final_price);
        unpaid_amount=(TextView)findViewById(R.id.unpaid_amount);
        status=(TextView)findViewById(R.id.status);
        token = Appconfig.TOKEN;
        Log.i("quotationId---", "hgifhgfhguhguihu");

        invoiceId=getIntent().getStringExtra("invoice_id");
        new InvoiceDetailsTask().execute(token, invoiceId);


    }

    private class InvoiceDetailsTask extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;
        HttpURLConnection conn;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(InvoiceDetailActivity.this);
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
            String invoiceId = params[1];
            URL url;
            try {

                url = new URL(Appconfig.CUSTOMER_DETAIL_INVOICES_URL + tok + "&invoice_id=" + invoiceId);
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
                JSONArray invoiceJsonArray=jsonObject.getJSONArray("invoice");
                for (int i=0;i<invoiceJsonArray.length();i++)
                {
                    JSONObject jsonObject1 = invoiceJsonArray.getJSONObject(i);
                    invoice_number.setText(jsonObject1.getString("invoice_number"));
                    customer.setText(String.valueOf(jsonObject1.getString("customer")));
                    invoice_date.setText(jsonObject1.getString("invoice_date"));
                    due_date.setText(jsonObject1.getString("due_date"));
                    payment_term.setText(jsonObject1.getString("payment_term"));
                    Salesteam.setText(jsonObject1.getString("salesteam"));
                    Salesperson.setText(jsonObject1.getString("sales_person"));
                    taxes.setText(jsonObject1.getString("tax_amount"));
                    total.setText(jsonObject1.getString("total"));
                    discount.setText(jsonObject1.getString("discount"));
                    final_price.setText(jsonObject1.getString("final_price"));
                    unpaid_amount.setText(jsonObject1.getString("unpaid_amount"));
                    status.setText(jsonObject1.getString("status"));

                }

                JSONArray array = jsonObject.getJSONArray("products");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject product_object = array.getJSONObject(i);
                    TableRow row = new TableRow(InvoiceDetailActivity.this);
                    tab_products.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

                    TextView product = new TextView(InvoiceDetailActivity.this);
                    product.setText(product_object.getString("product"));
                    Log.i("product--", String.valueOf(product.getText()));
                   /* product.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                     product.setPadding(5,5,5,5);*/
                    TextView quantity = new TextView(InvoiceDetailActivity.this);
                    quantity.setText(String.valueOf(product_object.get("quantity")));
                   /* quantity.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    product.setPadding(5,5,5,5);*/
                    product.setVisibility(View.VISIBLE);
                    TextView unit_price = new TextView(InvoiceDetailActivity.this);
                    unit_price.setText(String.valueOf(product_object.get("unit_price")));
                  /*  unit_price.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    product.setPadding(5,5,5,5);*/
                    product.setVisibility(View.VISIBLE);
                    TextView taxes = new TextView(InvoiceDetailActivity.this);
                    taxes.setText(product_object.getString("description"));
                    /*taxes.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    product.setPadding(5,5,5,5);*/
                    product.setVisibility(View.VISIBLE);
                    TextView sub_total = new TextView(InvoiceDetailActivity.this);
                    sub_total.setText(String.valueOf(product_object.get("subtotal")));
                   /* sub_total.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    product.setPadding(5,5,5,5);*/
                    product.setVisibility(View.VISIBLE);
                    row.addView(product);
                    row.addView(quantity);
                    row.addView(unit_price);
                    row.addView(taxes);
                    row.addView(sub_total);
                    row.setVisibility(View.VISIBLE);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            dialog.dismiss();

        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            }

        }
        return super.onOptionsItemSelected(item);
    }
}
