package com.project.naveen.lcrm.products;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.MyVolleySingleton;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.models.Category;
import com.project.naveen.lcrm.models.Products;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment extends Fragment {

    ArrayList<Products> productsArrayList;
    ArrayList<String>categoryNameList,statusList,product_typeList;
    ArrayList<Category>categoryList;
    String token;
    BetterSpinner category,status,product_type;
    EditText product_name,sales_price,description,qHand,qAvailable;
    Button submit;
    LinearLayout linearLayout;
    String product_id;
    TextInputLayout input_product_name,input_category,input_status,input_qhand,input_qAvailable,input_product_type,input_sales_price,input_description;

    public EditFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_edit3, container, false);
        linearLayout=(LinearLayout)v.findViewById(R.id.layout);
        product_id=getArguments().getString("productId");
        input_product_name=(TextInputLayout)v.findViewById(R.id.input_layout_product_name);
        input_category=(TextInputLayout)v.findViewById(R.id.input_layout_category);
        input_status=(TextInputLayout)v.findViewById(R.id.input_layout_status);
        input_qhand=(TextInputLayout)v.findViewById(R.id.input_layout_qHand);
        input_qAvailable=(TextInputLayout)v.findViewById(R.id.input_layout_qAvailable);
        input_product_type=(TextInputLayout)v.findViewById(R.id.input_layout_product_type);
        input_sales_price=(TextInputLayout)v.findViewById(R.id.input_layout_sales_price);
        input_description=(TextInputLayout)v.findViewById(R.id.input_layout_description);

        token= Appconfig.TOKEN;
        productsArrayList=new ArrayList<>();
        categoryNameList=new ArrayList<>();
        categoryList=new ArrayList<>();
        product_typeList=new ArrayList<>();
        statusList=new ArrayList<>();
        category=(BetterSpinner)v.findViewById(R.id.category);
        status=(BetterSpinner)v.findViewById(R.id.status);
        product_type=(BetterSpinner)v.findViewById(R.id.product_type);
        sales_price=(EditText)v.findViewById(R.id.sales_price);
        product_name=(EditText)v.findViewById(R.id.products_name);
        description=(EditText)v.findViewById(R.id.description);
        qHand=(EditText)v.findViewById(R.id.qHand);
        qAvailable=(EditText)v.findViewById(R.id.qAvailable);

        statusList.add("In Development");
        statusList.add("Normal");
        statusList.add("End of Lifecycle");
        statusList.add("Obsolete");
        product_typeList.add("Stockable Product");
        product_typeList.add("Consumable");
        product_typeList.add("Service");
        getCategoryList(token);
        new ProductsDetails().execute(Appconfig.TOKEN,product_id);
        ArrayAdapter<String> categoryArrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,categoryNameList);
        categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        category.setAdapter(categoryArrayAdapter);
        ArrayAdapter<String> statusArrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,statusList);
        statusArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        status.setAdapter(statusArrayAdapter);
        ArrayAdapter<String> product_typeArrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,product_typeList);
        product_typeArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        product_type.setAdapter(product_typeArrayAdapter);
        submit=(Button)v.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (product_name.getText().toString().isEmpty())
                {
                    input_product_name.setError("Please enter product name");

                    return;
                }

                else if (sales_price.getText().toString().isEmpty()){
                    input_sales_price.setError("Please enter sales price");
                    return;
                }
                else if (description.getText().toString().isEmpty()){
                    input_description.setError("Please enter description");
                    return;
                }
                else if (qHand.getText().toString().isEmpty()){
                    input_qhand.setError("Please enter quantity In Hand");
                    return;
                }
                else if (qAvailable.getText().toString().isEmpty()){
                    input_qAvailable.setError("Please select quantity available");
                    return;
                }
                else if (category.getText().toString().isEmpty()){
                    input_category.setError("Please select category");
                    return;
                }
                else if (status.getText().toString().isEmpty()){
                    input_status.setError("Please select status");
                    return;
                }
                else if (product_type.getText().toString().isEmpty()){
                    input_product_type.setError("Please select product type");
                    return;
                }
                else {
                    //do nothing
                }
                new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("Are you sure you want edit it!")
                        .setCancelText("No,cancel plx!")
                        .setConfirmText("Yes,Edit it!")
                        .showCancelButton(true)

                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                int categoryId=categoryList.get(categoryNameList.indexOf(category.getText().toString())).getId();

                                new EditProductTask().execute(token,product_name.getText().toString(),sales_price.getText().toString(),
                                        description.getText().toString(),qHand.getText().toString(),qAvailable.getText().toString(),
                                        product_type.getText().toString(),String.valueOf(categoryId),status.getText().toString(),product_id);


                                sweetAlertDialog.cancel();
                            }
                        })
                        .show();

            }
        });
        return v;
    }

    public void getCategoryList(String token)
    { StringRequest stringRequest = new StringRequest(Request.Method.GET, Appconfig.CATEGORY_URL+token,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        Log.i("response--", String.valueOf(response));

                        JSONObject jsonObject=new JSONObject(response);
                        JSONArray jsonArray=jsonObject.getJSONArray("categories");
                        for (int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject object=jsonArray.getJSONObject(i);
                            Category category=new Category();
                            category.setId(object.getInt("id"));
                            category.setName(object.getString("name"));
                            categoryNameList.add(object.getString("name"));
                            categoryList.add(category);
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
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);

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
                   /// category.setText(jsonObject1.getString("category_id"));
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

    class EditProductTask extends AsyncTask<String,Void,String>
    {
        ProgressDialog dialog;
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading, please wait...");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String response="",jsonresponse="";
            JSONObject json;
            BufferedReader bufferedReader;
            String tok=params[0];
            String product_name=params[1];
            String sales_price=params[2];
            String description=params[3];
            String qHand=params[4];
            String qAvailable=params[5];
            String product_type=params[6];
            String category_id=params[7];
            String status=params[8];
            String product_id=params[9];
            URL url;
            try {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("product_name",product_name);
                jsonObject.put("sale_price",sales_price);
                jsonObject.put("description" ,description);
                jsonObject.put("quantity_on_hand",qHand);
                jsonObject.put("quantity_available",qAvailable);
                jsonObject.put("product_type",product_type);
                jsonObject.put("status",status);
                jsonObject.put("category_id",category_id);
                jsonObject.put("product_id",product_id);
                Log.i("jsonObject--",jsonObject.toString());

                url = new URL(Appconfig.PRODUCTS_EDIT_URL+tok);

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
                    json = new JSONObject(response);
                    //Get Values from JSONobject
                    //System.out.println("success=" + json.get("success"));

                    jsonresponse = json.getString("success");

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
                    jsonresponse=json.getString("error");
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
            {
                final Snackbar snackbar = Snackbar.make(linearLayout, "Updated Succesfully!", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
            }
            else {
                final Snackbar snackbar = Snackbar.make(linearLayout, "Item not updated! Try Again", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
            }
        }
    }
}
