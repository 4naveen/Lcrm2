package com.project.lorvent.lcrm.activities.add;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;
import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.Connection;
import com.project.lorvent.lcrm.utils.MyVolleySingleton;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.adapters.QtemplateAdapter;
import com.project.lorvent.lcrm.models.Products;
import com.project.lorvent.lcrm.models.Qtemplate;

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
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class AddQtemplateActivity extends AppCompatActivity {
    Button submit, add_products;
    Spinner product_name;
    ArrayList<String> productNameList;
    ArrayList<Products> productsArrayList;
    EditText description, quantity, unit_price, sub_total,total,tax,gtotal,qtemplate,qtemplate_duration;
    SimpleDateFormat simpleDateFormat;
    TableLayout tab_products;
    ArrayList<String> product_id_array, product_name_array, description_array, quantity_array, price_array, sub_total_array;
    Configuration config;
    double calculated_tax, calulated_total;
    String selected_product_name, selected_description, selected_unit_price, selected_sub_total, selected_quantity;
    LinearLayout linearLayout;
    TextInputLayout input_qtemplate,input_duration;
    public static Dialog mdialog;
    TextInputLayout input_product_name, input_quantity,input_subtotal;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_qtemplate);
        Connection.getProductList(Appconfig.TOKEN,AddQtemplateActivity.this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Add Quotation Template");
        }
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        input_qtemplate=(TextInputLayout)findViewById(R.id.input_layout_quotation_template);
        input_duration=(TextInputLayout)findViewById(R.id.input_layout_qtemplate_duration);

        product_id_array = new ArrayList<>();
        product_name_array = new ArrayList<>();
        description_array = new ArrayList<>();
        quantity_array = new ArrayList<>();
        price_array = new ArrayList<>();
        sub_total_array = new ArrayList<>();

        productNameList = new ArrayList<String>();
        productNameList.add("Please select");
        productsArrayList = new ArrayList<>();

        linearLayout=(LinearLayout)findViewById(R.id.layout);
        config = getResources().getConfiguration();

        qtemplate = (EditText) findViewById(R.id.qtemplate);
        qtemplate_duration = (EditText) findViewById(R.id.qduration);
        total = (EditText) findViewById(R.id.total);
        tax = (EditText) findViewById(R.id.tax);
        gtotal = (EditText) findViewById(R.id.gtotal);

        submit = (Button) findViewById(R.id.submit);
        add_products = (Button) findViewById(R.id.add_products);

        tab_products = (TableLayout) findViewById(R.id.table_products);

        //getProductList(Appconfig.TOKEN);
        qtemplate.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_qtemplate.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        qtemplate_duration.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_duration.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        add_products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog1=new MaterialDialog.Builder(AddQtemplateActivity.this)
                        .title("Add Products")
                        .customView(R.layout.add_products_dialog, true)
                        .positiveText("ADD")
                        .autoDismiss(false)
                        .positiveColorRes(R.color.colorPrimary)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (product_name.getSelectedItem().toString().isEmpty()) {
                                    input_product_name.setError("please enter your email");
                                    return;
                                }
                                else if (quantity.getText().toString().isEmpty()) {
                                    input_quantity.setError("please enter your quantity");
                                    return;
                                }
                                else if (sub_total.getText().toString().isEmpty()) {
                                    input_subtotal.setError("please enter subtotal value");
                                    return;
                                }
                                else {
                                    dialog.dismiss();
                                }
                                TableRow row = new TableRow(AddQtemplateActivity.this);
                                tab_products.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                                int productId = AppSession.productsArrayList.get(AppSession.productNameList.indexOf(selected_product_name)).getId();
                                product_id_array.add(String.valueOf(productId));

                                TextView product = new TextView(AddQtemplateActivity.this);
                                product.setText(selected_product_name);
                                product_name_array.add(selected_product_name);
                                product.setVisibility(View.VISIBLE);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    product.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                                }
                                product.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                                product.setPadding(5,0,0,0);
                                TextView quantity = new TextView(AddQtemplateActivity.this);
                                quantity.setText(selected_quantity);
                                quantity_array.add(selected_quantity);
                                product.setVisibility(View.VISIBLE);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    quantity.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                                }
                                quantity.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                                quantity.setPadding(5,0,0,0);
                                TextView unit_price = new TextView(AddQtemplateActivity.this);
                                unit_price.setText(selected_unit_price);
                                price_array.add(selected_unit_price);
                                product.setVisibility(View.VISIBLE);
                                unit_price.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                                unit_price.setPadding(5,0,0,0);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    unit_price.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                                }
                              /*  TextView description = new TextView(AddQtemplateActivity.this);
                                description.setText(selected_description);*/
                                description_array.add(selected_description);
                                product.setVisibility(View.VISIBLE);


                                TextView sub_total = new TextView(AddQtemplateActivity.this);
                                sub_total.setText(selected_sub_total);
                                sub_total_array.add(selected_sub_total);
                                product.setVisibility(View.VISIBLE);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    sub_total.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                                }
                                sub_total.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                                sub_total.setPadding(5,0,0,0);
                                row.addView(product);
                                row.addView(quantity);
                                row.addView(unit_price);
                                row.addView(sub_total);
                               // row.addView(description);
                                row.setVisibility(View.VISIBLE);
                                dialog.dismiss();
                                calulated_total+=Double.parseDouble(selected_sub_total);

                                total.setText(String.valueOf(calulated_total));
                                calculated_tax = (calulated_total * 10) / 100;
                                tax.setText(String.valueOf(calculated_tax));
                                gtotal.setText(String.valueOf(calulated_total + calculated_tax));


                            }
                        })
                        .negativeColorRes(R.color.colorPrimary)
                        .negativeText("CANCEL")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();

                            }
                        })
                        .show();
                View view = dialog1.getCustomView();
                if (view != null) {
                    product_name = (Spinner) dialog1.getCustomView().findViewById(R.id.products_name);
                    input_product_name = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.input_layout_product_name);

                    description = (EditText) dialog1.getCustomView().findViewById(R.id.description);
                    quantity = (EditText) dialog1.getCustomView().findViewById(R.id.quantity);
                    input_quantity = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.input_layout_quantity);

                    unit_price = (EditText) dialog1.getCustomView().findViewById(R.id.unit_price);
                    sub_total = (EditText) dialog1.getCustomView().findViewById(R.id.sub_total);
                    input_subtotal = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.input_layout_sub_total);
                    quantity.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            input_quantity.setError("");

                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    sub_total.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            input_subtotal.setError("");

                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                }


                ArrayAdapter<String> product_nameArrayAdapter = new ArrayAdapter<>(AddQtemplateActivity.this, android.R.layout.simple_dropdown_item_1line, AppSession.productNameList);
                product_nameArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                product_name.setAdapter(product_nameArrayAdapter);


                product_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                         String product_name_text=productNameList.get(position);
                        selected_product_name = String.valueOf(parent.getItemAtPosition(position));
                        int productId =AppSession.productsArrayList.get(AppSession.productNameList.indexOf(selected_product_name)).getId();
                        if (product_name.getSelectedItemPosition() >=0) {
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
                        if (quantity.getText().toString().isEmpty()) {
                            input_quantity.setError("please enter your quantity");
                            return;
                        }
                        int quantity_int = Integer.parseInt(quantity.getText().toString());
                        selected_quantity = quantity.getText().toString();
                        int sub_total_int = quantity_int * Integer.parseInt(unit_price.getText().toString());
                        sub_total.setText(String.valueOf(sub_total_int));
                        selected_sub_total = String.valueOf(sub_total_int);
                    }
                });


            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qtemplate.getText().toString().isEmpty())
                {
                    input_qtemplate.setError("Please enter quotation template name");
                    return;
                }

                else if (qtemplate_duration.getText().toString().isEmpty()){
                    input_duration.setError("Please enter Qtemplate duration");
                    return;
                }
            else {
                    //do nothing
                }
                new AddQtemplate().execute(Appconfig.TOKEN, qtemplate.getText().toString(),qtemplate_duration.getText().toString(),
                        total.getText().toString(),tax.getText().toString(), gtotal.getText().toString());

            }
        });
    }

    public void setDialog(Dialog dialog) {
        mdialog = dialog;
    }

    class AddQtemplate extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
        HttpURLConnection conn;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(AddQtemplateActivity.this);
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
            String qtemplate = params[1];
            String qtemplate_duration = params[2];
            String total = params[3];
            String tax_amount = params[4];
            String grand_total = params[5];


            URL url;
            try {
                JSONObject jsonObject = new JSONObject();
                JSONArray array1 = new JSONArray();
                JSONArray array2 = new JSONArray();
                JSONArray array3 = new JSONArray();
                JSONArray array4 = new JSONArray();
                JSONArray array5 = new JSONArray();
                JSONArray array6 = new JSONArray();

                jsonObject.put("quotation_template", qtemplate);
                jsonObject.put("quotation_duration", qtemplate_duration);
                jsonObject.put("grand_total", grand_total);
                jsonObject.put("tax_amount", tax_amount);
                jsonObject.put("total", total);


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
                SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String post_qtemplate_url;
                if (text_url != null) {
                    post_qtemplate_url = text_url + "/user/post_qtemplate?token=";
                } else {
                    post_qtemplate_url = Appconfig.QTEMPLATE_POST_URL;
                }
                url = new URL(post_qtemplate_url + tok);
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
                    jsonresponse = json.getString("success");
                } else {
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
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
                new GetAllQtemplateTask().execute(Appconfig.TOKEN);
         /*         qtemplate.setText("");
                qtemplate_duration.setText("");*/
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

    public void getProductDetails(String token, String product_id) {
        SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String get_url;
        if (text_url != null) {
            get_url = text_url + "/user/product?token=";
        } else {
            get_url = Appconfig.PRODUCTS_DETAILS_URL;
        }
        progressDialog = new ProgressDialog(AddQtemplateActivity.this);
        progressDialog.setMessage("Loading, please wait");
        progressDialog.setTitle("Connecting server");
        progressDialog.show();
        progressDialog.setCancelable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,get_url+ token + "&product_id=" + product_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
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
                            Crashlytics.logException(e);

                        }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }

        };
        MyVolleySingleton.getInstance(AddQtemplateActivity.this).getRequestQueue().add(stringRequest);

    }

    class GetAllQtemplateTask extends AsyncTask<String,Void,String>
    {   String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            URL url;
            HttpURLConnection connection;
            try {
                SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String get_url;
                if (text_url != null) {
                    get_url = text_url + "/user/qtemplates?token=";
                } else {
                    get_url = Appconfig.QTEMPLATE_URL;
                }
                url = new URL(get_url+params[0]);
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
                Crashlytics.logException(e);

            }
            return response;
        }
        @Override
        protected void onPostExecute(String response) {
            try {
                JSONObject jsonObject=new JSONObject(response);
                JSONArray jsonArray=jsonObject.getJSONArray("qtemplates");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    Qtemplate qtemplate=new Qtemplate();
                    qtemplate.setQuatation_template(object.getString("quotation_template"));
                    qtemplate.setQuatation_duration(object.getString("quotation_duration"));
                    qtemplate.setId(object.getInt("id"));
                    AppSession.qtemplateArrayList.add(qtemplate);

                }

                QtemplateAdapter mAdapter=new QtemplateAdapter(AddQtemplateActivity.this,AppSession.qtemplateArrayList);


                AppSession.qtemplate_recyclerView.setAdapter(mAdapter);
                AppSession.qtemplate_recyclerView.setItemAnimator(new DefaultItemAnimator());
                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(AddQtemplateActivity.this, LinearLayoutManager.VERTICAL,false);
                //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);

                AppSession.qtemplate_recyclerView.setLayoutManager(lmanager);

            } catch (JSONException e) {
                e.printStackTrace();
                Crashlytics.logException(e);

            }
        }}
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
}
