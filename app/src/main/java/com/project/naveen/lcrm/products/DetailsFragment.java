package com.project.naveen.lcrm.products;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {
    private TextView product_name,category,product_type,status,qHand,qAvailable,sales_price,description;
      String product_id;
    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_details4, container, false);;

        product_name=(TextView)v.findViewById(R.id.products_name);
        category=(TextView)v.findViewById(R.id.category);
        product_type=(TextView)v.findViewById(R.id.product_type);
        status=(TextView)v.findViewById(R.id.status);
        qHand=(TextView)v.findViewById(R.id.qHand);
        qAvailable=(TextView)v.findViewById(R.id.qAvailable);
        sales_price=(TextView)v.findViewById(R.id.sales_price);
        description=(TextView)v.findViewById(R.id.description);
        product_id=getArguments().getString("productId");
       new ProductsDetails().execute(Appconfig.TOKEN,product_id);
        return v;
    }
    private class ProductsDetails extends AsyncTask<String,Void,String>
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
            String product_id=params[1];
            URL url;
            try {

                url = new URL(Appconfig.PRODUCTS_DETAILS_URL+tok+"&product_id="+product_id);
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
                        Log.d("output lines", line);
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
            dialog.dismiss();
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result);
                JSONArray jsonArray=jsonObject.getJSONArray("product");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObject1=jsonArray.getJSONObject(i);
                    product_name.setText(jsonObject1.getString("product_name"));
                    category.setText(jsonObject1.getString("category_id"));
                    product_type.setText(jsonObject1.getString("product_type"));
                    status.setText(jsonObject1.getString("status"));
                    qHand.setText(jsonObject1.getString("quantity_on_hand"));
                    qAvailable.setText(jsonObject1.getString("quantity_available"));
                    sales_price.setText(jsonObject1.getString("sale_price"));
                    description.setText(jsonObject1.getString("description"));

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }
}
