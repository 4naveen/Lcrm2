package com.project.naveen.lcrm.quotation;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.project.naveen.lcrm.AppSession;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.MyVolleySingleton;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.addactivity.AddQuotationActivity;
import com.project.naveen.lcrm.models.Products;
import com.project.naveen.lcrm.models.Quotation;
import com.weiwangcn.betterspinner.library.BetterSpinner;

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
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment extends Fragment {
    BetterSpinner customer, quotation_template, sales_person, sales_team, status, pay_term;
    Button submit, add_products;
    String quotationId;
    EditText start_date, exp_date, total, tax, gtotal, discount, final_price;
    int start_year, start_month, start_day, exp_year, exp_month, exp_day;
    Spinner product_name;
    ArrayList<String> productNameList;
    ArrayList<Products> productsArrayList;
    EditText description, quantity, unit_price, sub_total;
    SimpleDateFormat simpleDateFormat;
    TableLayout tab_products;
    ArrayList<String> product_id_array, product_name_array, description_array, quantity_array, price_array, sub_total_array;
    ImageView edit;
    double calculated_tax, calulated_total;
    String selected_product_name, selected_description, selected_unit_price, selected_sub_total, selected_quantity;

    public static Dialog mdialog;
    LinearLayout linearLayout;
    TextInputLayout input_customer,input_qtemplate,input_date,input_exp_date,input_pay_term,input_salesteam,input_salesperson,
            input_status;
    public EditFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_edit, container, false);;
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        quotationId=getArguments().getString("quotationId");
        linearLayout=(LinearLayout)v.findViewById(R.id.layout);
        setHasOptionsMenu(true);
        if (actionBar != null) {
            actionBar.setTitle("Edit Quotation");
            actionBar.setHomeButtonEnabled(false);
        }
        product_id_array = new ArrayList<>();
        product_name_array = new ArrayList<>();
        description_array = new ArrayList<>();
        quantity_array = new ArrayList<>();
        price_array = new ArrayList<>();
        sub_total_array = new ArrayList<>();

        input_customer=(TextInputLayout)v.findViewById(R.id.input_layout_customer);
        input_qtemplate=(TextInputLayout)v.findViewById(R.id.input_layout_qtemplate);
        input_date=(TextInputLayout)v.findViewById(R.id.input_layout_date);
        input_exp_date=(TextInputLayout)v.findViewById(R.id.input_layout_exp_date);
        input_pay_term=(TextInputLayout)v.findViewById(R.id.input_layout_pay_term);
        input_salesteam=(TextInputLayout)v.findViewById(R.id.input_layout_salesteam);
        input_salesperson=(TextInputLayout)v.findViewById(R.id.input_layout_salesperson);
        input_status=(TextInputLayout)v.findViewById(R.id.input_layout_status);

        productNameList = new ArrayList<String>();
        productNameList.add("Please select");
        productsArrayList = new ArrayList<>();
        customer = (BetterSpinner)v.findViewById(R.id.customer);
        quotation_template = (BetterSpinner)v.findViewById(R.id.quotation_template);
        sales_person = (BetterSpinner)v.findViewById(R.id.salesperson);
        sales_team = (BetterSpinner)v. findViewById(R.id.salesteam);
        pay_term = (BetterSpinner)v.findViewById(R.id.pay_term);
        status = (BetterSpinner)v. findViewById(R.id.status);

        start_date = (EditText)v. findViewById(R.id.date);
        exp_date = (EditText)v. findViewById(R.id.exp_date);

        total = (EditText)v. findViewById(R.id.total);
        tax = (EditText)v. findViewById(R.id.tax);
        gtotal = (EditText)v. findViewById(R.id.gtotal);
        discount = (EditText)v.findViewById(R.id.discount);
        final_price = (EditText)v. findViewById(R.id.final_price);

        submit = (Button)v. findViewById(R.id.submit);
        add_products = (Button)v. findViewById(R.id.add_products);
        tab_products = (TableLayout)v. findViewById(R.id.table_products);

        getProductList(Appconfig.TOKEN);
        new QuotationDetailsTask().execute(Appconfig.TOKEN, quotationId);

        start_year = Calendar.getInstance().get(Calendar.YEAR);
        start_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        exp_year = Calendar.getInstance().get(Calendar.YEAR);
        start_month = Calendar.getInstance().get(Calendar.MONTH);
        exp_month = Calendar.getInstance().get(Calendar.MONTH);
        exp_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        ArrayAdapter<String> customerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, AppSession.customerNameList);
        customerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        customer.setAdapter(customerArrayAdapter);
        ArrayAdapter<String> sales_teamArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, AppSession.sales_teamNameList);
        sales_teamArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sales_team.setAdapter(sales_teamArrayAdapter);
        ArrayAdapter<String> sales_personArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, AppSession.sales_personNameList);
        sales_personArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sales_person.setAdapter(sales_personArrayAdapter);
        ArrayAdapter<String> quotation_templateArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, AppSession.quotation_templateNameList);
        quotation_templateArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        quotation_template.setAdapter(quotation_templateArrayAdapter);

        ArrayAdapter<String> statusArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, AppSession.statusList);
        statusArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        status.setAdapter(statusArrayAdapter);

        ArrayAdapter<String> pay_termArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, AppSession.pay_termList);
        pay_termArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        pay_term.setAdapter(pay_termArrayAdapter);

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
                        discount.setText("0.00");
                        final_price.setText(String.valueOf(calulated_total + calculated_tax));
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

        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(start_date.getWindowToken(), 0);
                DatePickerDialog d = new DatePickerDialog(getActivity(), start_date_listener, start_year, start_month, start_day);
                d.show();
            }
        });

        start_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager)getActivity(). getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(start_date.getWindowToken(), 0);
                    DatePickerDialog d = new DatePickerDialog(getActivity(), start_date_listener, start_year, start_month, start_day);
                    d.show();                 }
            }
        });
        exp_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getActivity(). getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(start_date.getWindowToken(), 0);
                DatePickerDialog d = new DatePickerDialog(getActivity(), exp_date_listener, exp_year, exp_month, exp_day);
                d.show();
            }
        });

        exp_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager)getActivity(). getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(start_date.getWindowToken(), 0);
                    DatePickerDialog d = new DatePickerDialog(getActivity(), exp_date_listener, exp_year, exp_month, exp_day);
                    d.show();                }
            }
        });



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

      /*          if (customer.getText().toString().isEmpty())
                {
                  *//*  input_customer.setError("Please enter customer name");

                    return;*//*
                }*/

                 if (quotation_template.getText().toString().isEmpty()){
                    input_qtemplate.setError("Please select quotation template");
                    return;
                }
                else if (start_date.getText().toString().isEmpty()){
                    input_date.setError("Please enter start date");
                    return;
                }
                else if (exp_date.getText().toString().isEmpty()){
                    input_exp_date.setError("Please enter expire date");
                    return;
                }
                else if (pay_term.getText().toString().isEmpty()){
                    input_pay_term.setError("Please select customer name");
                    return;
                }
                else if (sales_team.getText().toString().isEmpty()){
                    input_salesteam.setError("Please select payment term");
                    return;
                }
                else if (sales_person.getText().toString().isEmpty()){
                    input_salesperson.setError("Please select sales person name");
                    return;
                }
                else if (status.getText().toString().isEmpty()){
                    input_status.setError("Please select status");
                    return;
                }
                else {
                    //do nothing
                }
                int salesTeamId = AppSession.salesTeamList.get(AppSession.sales_teamNameList.indexOf(sales_team.getText().toString())).getId();
                int salesPersonId = AppSession.salesPersonList.get(AppSession.sales_personNameList.indexOf(sales_person.getText().toString())).getId();
                int qtemplateId=AppSession.qTemplateList.get(AppSession.quotation_templateNameList.indexOf(quotation_template.getText().toString())).getId();

                new EditQuotation().execute(Appconfig.TOKEN, "105", String.valueOf(qtemplateId), start_date.getText().toString(),
                        exp_date.getText().toString(), pay_term.getText().toString(), String.valueOf(salesTeamId),
                        String.valueOf(salesPersonId), status.getText().toString(), total.getText().toString(), tax.getText().toString(),
                        gtotal.getText().toString(), discount.getText().toString(), final_price.getText().toString(), AppSession.quotation_prefix,
                        AppSession.quotation_start_number,quotationId);

            }
        });
        return v;
    }

    public void setDialog(Dialog dialog) {
        mdialog = dialog;
    }


    final DatePickerDialog.OnDateSetListener start_date_listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            start_year = year;
            start_month = month;
            start_day = dayOfMonth;
            simpleDateFormat = new SimpleDateFormat(AppSession.date_format.replace('Y', 'y').replace('m', 'M'), Locale.ENGLISH);
            Date date = new Date(year - 1900, month, dayOfMonth);
            String text_date = simpleDateFormat.format(date);
            start_date.setText(text_date);
            start_date.clearFocus();
        }
    };

    final DatePickerDialog.OnDateSetListener exp_date_listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            exp_year = year;
            exp_month = month;
            exp_day = dayOfMonth;
            simpleDateFormat = new SimpleDateFormat(AppSession.date_format.replace('Y', 'y').replace('m', 'M'), Locale.ENGLISH);
            Date date = new Date(year - 1900, month, dayOfMonth);
            String text_date = simpleDateFormat.format(date);
            exp_date.setText(text_date);
            exp_date.clearFocus();
        }
    };
    class EditQuotation extends AsyncTask<String, Void, String> {
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
            String customer_id = params[1];
            String qtemplate_id = params[2];
            String start_date = params[3];
            String exp_date = params[4];
            String payment_term = params[5];
            String sales_team_id = params[6];
            String sales_person_id = params[7];
            String status = params[8];
            String total = params[9];
            String tax_amount = params[10];
            String grand_total = params[11];
            String discount = params[12];
            String final_price = params[13];
            String quotation_prefix = params[14];
            String quotation_start_number = params[15];
            String quotation_id=params[16];

            URL url;
            try {
                JSONObject jsonObject = new JSONObject();
                JSONArray array1 = new JSONArray();
                JSONArray array2 = new JSONArray();
                JSONArray array3 = new JSONArray();
                JSONArray array4 = new JSONArray();
                JSONArray array5 = new JSONArray();
                JSONArray array6 = new JSONArray();

                jsonObject.put("quotation_id", quotation_id);
                jsonObject.put("customer_id", customer_id);
                jsonObject.put("qtemplate_id", qtemplate_id);
                jsonObject.put("payment_term", payment_term);
                jsonObject.put("date", start_date);
                jsonObject.put("exp_date", exp_date);
                jsonObject.put("sales_person_id", sales_person_id);
                jsonObject.put("sales_team_id", sales_team_id);
                jsonObject.put("grand_total", grand_total);
                jsonObject.put("discount", discount);
                jsonObject.put("final_price", final_price);
                jsonObject.put("quotation_prefix", quotation_prefix);
                jsonObject.put("quotation_start_number", quotation_start_number);
                jsonObject.put("status", status);
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

                url = new URL(Appconfig.EDIT_QUOTATION_URL + tok);
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
                    quotation_template.setText(jsonObject1.getString("qtemplate"));
                    customer.setText(String.valueOf(jsonObject1.getString("company")));
                    start_date.setText(jsonObject1.getString("date"));
                    exp_date.setText(jsonObject1.getString("exp_date"));
                    pay_term.setText(jsonObject1.getString("payment_term"));
                    status.setText(jsonObject1.getString("status"));
                    sales_team.setText(jsonObject1.getString("salesteam"));
                    sales_person.setText(jsonObject1.getString("sales_person"));
                    tax.setText(jsonObject1.getString("tax_amount"));
                    total.setText(jsonObject1.getString("total"));
                    gtotal.setText(jsonObject1.getString("grand_total"));
                    discount.setText(jsonObject1.getString("discount"));
                    final_price.setText(jsonObject1.getString("final_price"));
                    //terms_and_condition.setText(jsonObject1.getString("terms_and_conditions"));

                }
                JSONArray productsJsonArray = jsonObject.getJSONArray("products");

                for (int i = 0; i < productsJsonArray.length(); i++) {
                    JSONObject product_object = productsJsonArray.getJSONObject(i);
                    final TableRow row = new TableRow(getActivity());
                    tab_products.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

                   final  TextView product = new TextView(getActivity());
                    product.setId(i);
                    product.setText(product_object.getString("product"));
                    Log.i("product--", String.valueOf(product.getText()));

                   final TextView quantity_textview = new TextView(getActivity());
                    quantity_textview.setId(i);
                    quantity_textview.setText(String.valueOf(product_object.get("quantity")));
                    product.setVisibility(View.VISIBLE);

                   final TextView unit_price_textview = new TextView(getActivity());
                    unit_price_textview.setId(i);
                    unit_price_textview.setText(String.valueOf(product_object.get("unit_price")));
                    product.setVisibility(View.VISIBLE);

                   final TextView description_textview = new TextView(getActivity());
                    description_textview.setId(i);
                    description_textview.setText(product_object.getString("description"));
                    product.setVisibility(View.VISIBLE);

                   final TextView sub_total_textview = new TextView(getActivity());
                    sub_total_textview.setId(i);
                    sub_total_textview.setText(String.valueOf(product_object.get("subtotal")));
                    product.setVisibility(View.VISIBLE);

                    edit=new ImageView(getActivity());
                    edit.setImageResource(R.mipmap.ic_create_black_18dp);
                    edit.setId(R.id.edit);

                    row.addView(product);
                    row.addView(quantity_textview);
                    row.addView(unit_price_textview);
                    row.addView(sub_total_textview);
                    row.addView(description_textview);
                    row.addView(edit);
                      row.setId(i);
                    row.setVisibility(View.VISIBLE);

                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getActivity(),"row clicked"+row.getId(),Toast.LENGTH_LONG).show();
                            final Dialog dialog = new Dialog(getActivity());
                            dialog.setContentView(R.layout.edit_products_dialog);
                             product_name = (Spinner) dialog.findViewById(R.id.products_name);
                             description = (EditText) dialog.findViewById(R.id.description);
                             quantity = (EditText) dialog.findViewById(R.id.quantity);
                             unit_price = (EditText) dialog.findViewById(R.id.unit_price);
                             sub_total = (EditText) dialog.findViewById(R.id.sub_total);
                            Button cancel = (Button) dialog.findViewById(R.id.cancel);
                            Button edit = (Button) dialog.findViewById(R.id.submit);

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
                            edit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    int productId = productsArrayList.get(productNameList.indexOf(selected_product_name)).getId();
                                    product_id_array.add(String.valueOf(productId));
                                    product_name_array.add(selected_product_name);
                                    quantity_array.add(selected_quantity);
                                    unit_price.setText(selected_unit_price);
                                    price_array.add(selected_unit_price);
                                    description_array.add(selected_description);
                                    sub_total_array.add(selected_sub_total);

                                    if (row.getId()==product.getId())
                                    {
                                        product.setText(selected_product_name);
                                        quantity_textview.setText(selected_quantity);
                                        unit_price_textview.setText(selected_unit_price);
                                        description_textview.setText(selected_description);
                                        sub_total_textview.setText(selected_sub_total);

                                    }
                                    dialog.dismiss();
                                    for (int i = 0; i < sub_total_array.size(); i++) {

                                        calulated_total += Integer.parseInt(sub_total_array.get(i));
                                    }
                                    total.setText(String.valueOf(calulated_total));
                                    calculated_tax = (calulated_total * 10) / 100;
                                    tax.setText(String.valueOf(calculated_tax));
                                    gtotal.setText(String.valueOf(calulated_total + calculated_tax));
                                    discount.setText("0.00");
                                    final_price.setText(String.valueOf(calulated_total + calculated_tax));
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
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

}
