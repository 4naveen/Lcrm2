package com.project.naveen.lcrm.qtemplate;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.detailsactivity.QtemplateDetailsActivity;

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
public class DetailFragment extends Fragment {
    TextView quotation_template,quotation_duration,immediate_pay,total,tax_amount,grand_total,terms_and_condition;
    String qtemplateId,token;
    TableLayout tab_products;
    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_detail3, container, false);;
        tab_products=(TableLayout)v.findViewById(R.id.table_products);

        quotation_template=(TextView)v.findViewById(R.id.qtemplate);
        quotation_duration=(TextView)v.findViewById(R.id.qduration);
        immediate_pay=(TextView)v.findViewById(R.id.immediate_pay);
        total=(TextView)v.findViewById(R.id.total);
        tax_amount=(TextView)v.findViewById(R.id.taxes);
        grand_total=(TextView)v.findViewById(R.id.grand_total);
        terms_and_condition=(TextView)v.findViewById(R.id.terms_condition);
        qtemplateId=getArguments().getString("qtemplateId");
        Log.i("qtemplateId-",qtemplateId);
        token= Appconfig.TOKEN;


        new QtemplateDetailsTask().execute(token,qtemplateId);
        return v;

    }
    private class QtemplateDetailsTask extends AsyncTask<String,Void,String>
    {

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
            String response="",jsonResponse="";
            JSONObject json;
            BufferedReader bufferedReader;
            String tok=params[0];
            String qtemplateId=params[1];
            URL url;
            try {
                url = new URL(Appconfig.QTEMPLATE_DETAILS_URL+tok+"&qtemplate_id="+qtemplateId);
                conn = (HttpURLConnection) url.openConnection();
                //Log.i("res code--",""+conn.getResponseCode());
                int responseCode=conn.getResponseCode();
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

                    jsonResponse=response;

                }
                else {
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        response += line;
                        //  Log.d("output lines", line);
                    }
                    Log.i("response",response);
                    json = new JSONObject(response);
                    jsonResponse=json.getString("error");
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
                JSONObject jsonObject1=jsonObject.getJSONObject("qtemplate");
                JSONArray array=jsonObject.getJSONArray("products");
                quotation_template.setText(jsonObject1.getString("quotation_template"));
                quotation_duration.setText(String.valueOf(jsonObject1.getString("quotation_duration")));
                immediate_pay.setText(String.valueOf(jsonObject1.get("immediate_payment")));
                for (int i=0;i<array.length();i++)
                {
                    JSONObject product_object=array.getJSONObject(i);
                    TableRow row=new TableRow(getActivity());
                    tab_products.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

                    TextView product=new TextView(getActivity());
                    product.setText(product_object.getString("product"));
                    Log.i("product--", String.valueOf(product.getText()));
                    //product.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    product.setPadding(5,5,5,5);
                    TextView quantity=new TextView(getActivity());
                    quantity.setText(String.valueOf(product_object.get("quantity")));
                    //quantity.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    product.setPadding(5,5,5,5);
                    product.setVisibility(View.VISIBLE);
                    TextView unit_price=new TextView(getActivity());
                    unit_price.setText(String.valueOf(product_object.get("unit_price")));
                    //unit_price.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    product.setPadding(5,5,5,5);
                    product.setVisibility(View.VISIBLE);
                    TextView taxes=new TextView(getActivity());
                    taxes.setText(product_object.getString("taxes"));
                    // taxes.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    product.setPadding(5,5,5,5);
                    product.setVisibility(View.VISIBLE);
                    TextView sub_total=new TextView(getActivity());
                    sub_total.setText(String.valueOf(product_object.get("subtotal")));
                    //sub_total.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    product.setPadding(5,5,5,5);
                    product.setVisibility(View.VISIBLE);
                    row.addView(product);
                    row.addView(quantity);
                    row.addView(unit_price);
                    row.addView(taxes);
                    row.addView(sub_total);
                    row.setVisibility(View.VISIBLE);
                }
                tax_amount.setText(String.valueOf(jsonObject1.getString("tax_amount")));
                total.setText(String.valueOf(jsonObject1.getString("total")));
                grand_total.setText(String.valueOf(jsonObject1.getString("grand_total")));
                terms_and_condition.setText(jsonObject1.getString("terms_and_conditions"));
                dialog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }
}
