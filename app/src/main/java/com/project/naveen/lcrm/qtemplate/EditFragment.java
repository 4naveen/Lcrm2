package com.project.naveen.lcrm.qtemplate;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.MyVolleySingleton;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.models.Products;

import org.json.JSONArray;
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
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment extends Fragment {
    Button submit, add_products;
    Spinner product_name;
    ArrayList<String> productNameList;
    ArrayList<Products> productsArrayList;
    EditText description, quantity, unit_price, sub_total,total,tax,gtotal,qtemplate,qtemplate_duration;
    TableLayout tab_products;
    ArrayList<String> product_id_array, product_name_array, description_array, quantity_array, price_array, sub_total_array;
    double calculated_tax, calulated_total;
    String selected_product_name, selected_description, selected_unit_price, selected_sub_total, selected_quantity;
    public static Dialog mdialog;
    String qtemplateId;
LinearLayout frameLayout;
    public EditFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_edit7, container, false);
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        qtemplateId=getArguments().getString("qtemplateId");
        frameLayout=(LinearLayout) v.findViewById(R.id.layout);
        if (actionBar != null) {
            actionBar.setTitle("Edit Quotation Template");
        }
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        product_id_array = new ArrayList<>();
        product_name_array = new ArrayList<>();
        description_array = new ArrayList<>();
        quantity_array = new ArrayList<>();
        price_array = new ArrayList<>();
        sub_total_array = new ArrayList<>();

        productNameList = new ArrayList<String>();
        productNameList.add("Please select");
        productsArrayList = new ArrayList<>();


        qtemplate = (EditText)v.findViewById(R.id.qtemplate);
        qtemplate_duration = (EditText)v. findViewById(R.id.qduration);
        total = (EditText)v. findViewById(R.id.total);
        tax = (EditText)v. findViewById(R.id.tax);
        gtotal = (EditText)v. findViewById(R.id.gtotal);

        submit = (Button)v. findViewById(R.id.submit);
        add_products = (Button)v. findViewById(R.id.add_products);

        tab_products = (TableLayout)v. findViewById(R.id.table_products);

        getProductList(Appconfig.TOKEN);

        new QtemplateDetailsTask().execute(Appconfig.TOKEN, qtemplateId);

        add_products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.add_products_dialog);
                product_name = (Spinner) dialog.findViewById(R.id.products_name);
                description = (EditText) dialog.findViewById(R.id.description);
                quantity = (EditText) dialog.findViewById(R.id.quantity);
                unit_price = (EditText) dialog.findViewById(R.id.unit_price);
                sub_total = (EditText) dialog.findViewById(R.id.sub_total);
                Button cancel = (Button) dialog.findViewById(R.id.cancel);
                Button add = (Button) dialog.findViewById(R.id.submit);

                ArrayAdapter<String> product_nameArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, productNameList);
                product_nameArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                product_name.setAdapter(product_nameArrayAdapter);
                product_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                         String product_name_text=productNameList.get(position);
                        selected_product_name = String.valueOf(parent.getItemAtPosition(position));
                        Log.i("productname--", selected_product_name);
                        int productId = productsArrayList.get(productNameList.indexOf(selected_product_name)).getId();
                        Log.i("product_id-", String.valueOf(productId));
                        if (product_name.getSelectedItemPosition() > 0) {
                            getProductDetails(Appconfig.TOKEN, String.valueOf(productId));
                        }


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                sub_total.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        int quantity_int = Integer.parseInt(quantity.getText().toString());
                        selected_quantity = quantity.getText().toString();
                        int sub_total_int = quantity_int * Integer.parseInt(unit_price.getText().toString());
                        Log.i("subtotal--", String.valueOf(sub_total_int) + "--" + quantity_int);
                        sub_total.setText(String.valueOf(sub_total_int));
                        selected_sub_total = String.valueOf(sub_total_int);
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        TableRow row = new TableRow(getActivity());
                        tab_products.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                        int productId = productsArrayList.get(productNameList.indexOf(selected_product_name)).getId();
                        product_id_array.add(String.valueOf(productId));

                        TextView product = new TextView(getActivity());
                        product.setText(selected_product_name);
                        product_name_array.add(selected_product_name);
                        Log.i("product--", String.valueOf(product.getText()));
                        product.setVisibility(View.VISIBLE);


                        TextView quantity = new TextView(getActivity());
                        quantity.setText(selected_quantity);

                        quantity_array.add(selected_quantity);
                        product.setVisibility(View.VISIBLE);


                        TextView unit_price = new TextView(getActivity());
                        unit_price.setText(selected_unit_price);
                        price_array.add(selected_unit_price);
                        product.setVisibility(View.VISIBLE);


                        TextView description = new TextView(getActivity());
                        description.setText(selected_description);
                        description_array.add(selected_description);
                        product.setVisibility(View.VISIBLE);


                        TextView sub_total = new TextView(getActivity());
                        sub_total.setText(selected_sub_total);
                        sub_total_array.add(selected_sub_total);
                        product.setVisibility(View.VISIBLE);

                        row.addView(product);
                        row.addView(quantity);
                        row.addView(unit_price);
                        row.addView(sub_total);
                        row.addView(description);
                        row.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                        for (int i = 0; i < sub_total_array.size(); i++) {

                            calulated_total += Integer.parseInt(sub_total_array.get(i));
                        }
                        total.setText(String.valueOf(calulated_total));
                        calculated_tax = (calulated_total * 10) / 100;
                        tax.setText(String.valueOf(calculated_tax));
                        gtotal.setText(String.valueOf(calulated_total + calculated_tax));

                    }
                });

                dialog.setCancelable(true);
                // dialog.setTitle("Lead Details");
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.70);
                int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.50);
                dialog.getWindow().setLayout(width, height);
                setDialog(dialog);
                dialog.show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EditQtemplate().execute(Appconfig.TOKEN,qtemplateId, qtemplate.getText().toString(),qtemplate_duration.getText().toString(),
                        total.getText().toString(),tax.getText().toString(), gtotal.getText().toString());

            }
        });

        return v;
    }
    public void setDialog(Dialog dialog) {
        mdialog = dialog;
    }



    class EditQtemplate extends AsyncTask<String, Void, String> {
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
            String response = "", jsonresponse = "";
            JSONObject json;
            BufferedReader bufferedReader;
            String tok = params[0];
            String qtemplate_id=params[1];
            String qtemplate=params[2];
            String qduration=params[3];
            String total=params[4];
            String tax=params[5];
            String gtotal=params[6];



            URL url;
            try {
                JSONObject jsonObject = new JSONObject();
                JSONArray array1 = new JSONArray();
                JSONArray array2 = new JSONArray();
                JSONArray array3 = new JSONArray();
                JSONArray array4 = new JSONArray();
                JSONArray array5 = new JSONArray();
                JSONArray array6 = new JSONArray();

                jsonObject.put("qtemplate_id", qtemplate_id);
                jsonObject.put("quotation_template", qtemplate);
                jsonObject.put("quotation_duration", qduration);
                jsonObject.put("total", total);
                jsonObject.put("tax_amount", tax);
                jsonObject.put("grand_total", gtotal);



                for (int i = 0; i < product_id_array.size(); i++) {
                    array1.put(product_id_array.get(i));
                    array2.put(product_name_array.get(i));
                    array3.put(description_array.get(i));
                    array4.put(quantity_array.get(i));
                    array5.put(price_array.get(i));
                    array6.put(sub_total_array.get(i));

                }

                jsonObject.put("product_id", array1);
                jsonObject.put("product_name", array2);
                jsonObject.put("description", array3);
                jsonObject.put("quantity", array4);
                jsonObject.put("price", array5);
                jsonObject.put("sub_total", array6);

                url = new URL(Appconfig.QTEMPLATE_EDIT_URL + tok);
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
                Log.i("jsonObject", jsonObject.toString());
//                Log.i("jsonObject1",jsonObject1.toString());

                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();
                os.close();
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
                    json = new JSONObject(response);
                    jsonresponse = json.getString("success");
                } else {
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        response += line;
                        Log.d("output lines", line);
                    }
                    json = new JSONObject(response);
                    jsonresponse = json.getString("error");
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
                final Snackbar snackbar = Snackbar.make(frameLayout, "Updated Succesfully!", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
            }
            else {
                final Snackbar snackbar = Snackbar.make(frameLayout, "Item not updated! Try Again", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
            }
        }
    }

    public void getProductList(String token) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Appconfig.PRODUCTS_URL + token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("response--", String.valueOf(response));
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("products");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                Products products = new Products();
                                products.setId(object.getInt("id"));
                                products.setProduct_name(object.getString("product_name"));
                                productNameList.add(object.getString("product_name"));
                                productsArrayList.add(products);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("response--", String.valueOf(error));
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }

        };
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);

    }

    public void getProductDetails(String token, String product_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Appconfig.PRODUCTS_DETAILS_URL + token + "&product_id=" + product_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("response--", String.valueOf(response));
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("product");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                description.setText(object.getString("description"));
                                selected_description = object.getString("description");
                                unit_price.setText(object.getString("sale_price"));
                                selected_unit_price = object.getString("sale_price");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("response--", String.valueOf(error));
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }

        };
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);

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
                qtemplate.setText(jsonObject1.getString("quotation_template"));
                qtemplate_duration.setText(String.valueOf(jsonObject1.getString("quotation_duration")));
                tax.setText(String.valueOf(jsonObject1.getString("tax_amount")));
                total.setText(String.valueOf(jsonObject1.getString("total")));
                gtotal.setText(String.valueOf(jsonObject1.getString("grand_total")));

                JSONArray array=jsonObject.getJSONArray("products");
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

                dialog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }
}
