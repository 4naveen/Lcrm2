package com.project.naveen.lcrm.quotation;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.detailsactivity.QuotationDetailsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailQuotationFragment extends Fragment {
    TableLayout tab_products;

    TextView quotation_number,customer,date,expiration_date,payment_term,Salesteam,Salesperson,untaxedAmount,taxes,total,discount,final_price,terms_and_condition;
    String quotationId,token;
    public DetailQuotationFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_detail_quotation, container, false);;
        tab_products = (TableLayout)v.findViewById(R.id.table_products);

        quotation_number = (TextView)v. findViewById(R.id.quotation_number);
        customer = (TextView)v. findViewById(R.id.customer);
        date = (TextView)v. findViewById(R.id.date);
        expiration_date = (TextView)v. findViewById(R.id.exp_date);
        payment_term = (TextView) v.findViewById(R.id.pay_term);
        Salesteam = (TextView)v.findViewById(R.id.salesteam);
        Salesperson = (TextView)v.findViewById(R.id.salesperson);
        untaxedAmount = (TextView)v.findViewById(R.id.un_amount);
        taxes = (TextView)v.findViewById(R.id.taxes);
        total = (TextView)v.findViewById(R.id.total);
        discount = (TextView)v.findViewById(R.id.discount);
        final_price = (TextView)v. findViewById(R.id.final_price);
        terms_and_condition = (TextView)v.findViewById(R.id.terms_condition);
//        quotationId = getIntent().getStringExtra("quotationId");
//            Log.i("quotationId-", quotationId);
        token = Appconfig.TOKEN;
        Log.i("quotationId---", "hgifhgfhguhguihu");

//        quotationId=((QuotationDetailsActivity)getActivity()).getQuotation_id();
       quotationId=getArguments().getString("quotationId");

        Log.i("quotationId---", "hgifhgfhguhguihufbfb");

        new QuotationDetailsTask().execute(token, quotationId);

       /* final com.getbase.floatingactionbutton.FloatingActionButton actionA = (com.getbase.floatingactionbutton.FloatingActionButton)v.findViewById(R.id.action_a);
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             *//*   Intent i=new Intent(getActivity(),AddSalesTeamActivity.class);
                getActivity().startActivity(i);*//*
                //Toast.makeText(getActivity(), " Action A is clicked",Toast.LENGTH_SHORT).show();
            }
        });
        final com.getbase.floatingactionbutton.FloatingActionButton actionB = (com.getbase.floatingactionbutton.FloatingActionButton)v.findViewById(R.id.action_b);
        actionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
        return v;
    }


    private class QuotationDetailsTask extends AsyncTask<String, Void, String> {

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
            String response = "", jsonResponse = "";
            JSONObject json;
            BufferedReader bufferedReader;
            String tok = params[0];
            String quotationId = params[1];
            URL url;
            try {
                url = new URL(Appconfig.QUOTATION_DETAILS_URL + tok + "&quotation_id=" + quotationId);
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
            dialog.dismiss();
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result);
                JSONArray quotationJsonArray=jsonObject.getJSONArray("quotation");
                for (int i=0;i<quotationJsonArray.length();i++)
                {
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
                    TableRow row = new TableRow(getActivity());
                    tab_products.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

                    TextView product = new TextView(getActivity());
                    product.setText(product_object.getString("product"));
                    Log.i("product--", String.valueOf(product.getText()));
                   /* product.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                     product.setPadding(5,5,5,5);*/
                    TextView quantity = new TextView(getActivity());
                    quantity.setText(String.valueOf(product_object.get("quantity")));
                   /* quantity.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    product.setPadding(5,5,5,5);*/
                    product.setVisibility(View.VISIBLE);
                    TextView unit_price = new TextView(getActivity());
                    unit_price.setText(String.valueOf(product_object.get("unit_price")));
                  /*  unit_price.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    product.setPadding(5,5,5,5);*/
                    product.setVisibility(View.VISIBLE);
                    TextView taxes = new TextView(getActivity());
                    taxes.setText(product_object.getString("description"));
                    /*taxes.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    product.setPadding(5,5,5,5);*/
                    product.setVisibility(View.VISIBLE);
                    TextView sub_total = new TextView(getActivity());
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


        }

    }

}
