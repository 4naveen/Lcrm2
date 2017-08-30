package com.project.lorvent.lcrm.activities.details.customers;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class QuotationDetailActivity extends AppCompatActivity {
    TableLayout tab_products;
    TextView quotation_number,customer,date,expiration_date,payment_term,Salesteam,Salesperson,untaxedAmount,taxes,total,discount,final_price,terms_and_condition;
    String quotationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotation_detail);

        tab_products = (TableLayout)findViewById(R.id.table_products);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Quotation Details");
        }
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        quotation_number = (TextView)findViewById(R.id.quotation_number);
        customer = (TextView)findViewById(R.id.customer);
        date = (TextView)findViewById(R.id.date);
        expiration_date = (TextView) findViewById(R.id.exp_date);
        payment_term = (TextView) findViewById(R.id.pay_term);
        Salesteam = (TextView)findViewById(R.id.salesteam);
        Salesperson = (TextView)findViewById(R.id.salesperson);
        untaxedAmount = (TextView)findViewById(R.id.un_amount);
        taxes = (TextView)findViewById(R.id.taxes);
        total = (TextView)findViewById(R.id.total);
        discount = (TextView)findViewById(R.id.discount);
        final_price = (TextView)findViewById(R.id.final_price);
        terms_and_condition = (TextView)findViewById(R.id.terms_condition);

         quotationId=getIntent().getStringExtra("quotation_id");

        new QuotationDetailsTask().execute(Appconfig.TOKEN, quotationId);
    }

    private class QuotationDetailsTask extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;
        HttpURLConnection conn;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(QuotationDetailActivity.this);
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
            String quotationId = params[1];
            URL url;
            try {
                SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String details_url;
                if (text_url != null) {
                    details_url = text_url + "/customer/quotation?token=";
                } else {
                    details_url = Appconfig.CUSTOMER_DETAIL_QUOTATION_URL;
                }
                url = new URL(details_url+ tok + "&quotation_id=" + quotationId);
                conn = (HttpURLConnection) url.openConnection();
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
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
                    }
                    json = new JSONObject(response);
                    jsonResponse = json.getString("error");
                    //System.out.println("error=" + json.get("error"));
                    //succes = json.getString("success");
                }

            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.logException(e);

            }
            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result);
                JSONArray quotationJsonArray=jsonObject.getJSONArray("quotation");
                for (int i=0;i<quotationJsonArray.length();i++) {
                    JSONObject jsonObject1 = quotationJsonArray.getJSONObject(i);
                    quotation_number.setText(jsonObject1.getString("quotations_number"));
                    customer.setText(String.valueOf(jsonObject1.getString("company")));
                    date.setText(jsonObject1.getString("date"));
                    expiration_date.setText(jsonObject1.getString("exp_date"));
                    payment_term.setText(jsonObject1.getString("payment_term"));
                    Salesteam.setText(jsonObject1.getString("salesteam"));
                    Salesperson.setText(jsonObject1.getString("sales_person"));
                    taxes.setText(jsonObject1.getString("tax_amount"));
                    total.setText(jsonObject1.getString("total"));
                    discount.setText(jsonObject1.getString("discount"));
                    final_price.setText(jsonObject1.getString("final_price"));
                    terms_and_condition.setText(jsonObject1.getString("terms_and_conditions"));

                }
                JSONArray productsJsonArray = jsonObject.getJSONArray("products");

                for (int i = 0; i < productsJsonArray.length(); i++) {
                    JSONObject product_object = productsJsonArray.getJSONObject(i);
                    TableRow row = new TableRow(QuotationDetailActivity.this);
                    tab_products.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

                    TextView product = new TextView(QuotationDetailActivity.this);
                    product.setEllipsize(TextUtils.TruncateAt.END);
                    product.setEms(4);
                    product.setSingleLine();
                    product.setText(product_object.getString("product"));
                   /* product.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                     product.setPadding(5,5,5,5);*/
                    TextView quantity = new TextView(QuotationDetailActivity.this);
                    quantity.setText(String.valueOf(product_object.get("quantity")));
                   /* quantity.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    product.setPadding(5,5,5,5);*/
                    product.setVisibility(View.VISIBLE);
                    TextView unit_price = new TextView(QuotationDetailActivity.this);
                    unit_price.setText(String.valueOf(product_object.get("unit_price")));
                  /*  unit_price.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    product.setPadding(5,5,5,5);*/
                    product.setVisibility(View.VISIBLE);
                    TextView taxes = new TextView(QuotationDetailActivity.this);
                    taxes.setEllipsize(TextUtils.TruncateAt.END);
                    taxes.setEms(3);
                    taxes.setSingleLine();
                    taxes.setText(product_object.getString("taxes"));
                    /*taxes.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    product.setPadding(5,5,5,5);*/
                    product.setVisibility(View.VISIBLE);
                    TextView sub_total = new TextView(QuotationDetailActivity.this);
                    sub_total.setEllipsize(TextUtils.TruncateAt.END);
                    sub_total.setEms(3);
                    sub_total.setSingleLine();
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
                Crashlytics.logException(e);


            }


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
