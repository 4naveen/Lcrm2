package com.project.lorvent.lcrm.activities.add;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.project.lorvent.lcrm.adapters.SalesOrderAdapter;
import com.project.lorvent.lcrm.models.Products;
import com.project.lorvent.lcrm.models.SalesOrder;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class AddSalesOrderActivity extends AppCompatActivity {
    BetterSpinner customer, quotation_template, sales_person, sales_team, status, pay_term;
    Button submit, add_products;
    static final int START_DATE_PICKER_ID = 111;
    static final int EXP_DATE_PICKER_ID = 112;
    EditText start_date, exp_date, total, tax, gtotal, discount, final_price;
    int start_year, start_month, start_day, exp_year, exp_month, exp_day;
    Spinner product_name;
    ArrayList<String> productNameList;
    ArrayList<Products> productsArrayList;
    EditText description, quantity, unit_price, sub_total;
    SimpleDateFormat simpleDateFormat;
    TableLayout tab_products;
    ArrayList<String> product_id_array, product_name_array, description_array, quantity_array, price_array, sub_total_array;
    ArrayList<String>statusList;
    Configuration config;
    double calculated_tax, calulated_total;
    String selected_product_name, selected_description, selected_unit_price, selected_sub_total, selected_quantity;
    LinearLayout linearLayout;
    TextInputLayout input_customer,input_qtemplate,input_date,input_exp_date,input_pay_term,input_salesteam,input_salesperson,
            input_status;
    public static Dialog mdialog;
    TextInputLayout input_product_name, input_quantity,input_subtotal;
    ProgressDialog progressDialog;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sales_order);
        linearLayout=(LinearLayout)findViewById(R.id.layout);
        config = getResources().getConfiguration();

        Connection.getStaffList(Appconfig.TOKEN,AddSalesOrderActivity.this);
        Connection.getContactList(Appconfig.TOKEN,AddSalesOrderActivity.this);
        Connection.getSalesTeamList(Appconfig.TOKEN,AddSalesOrderActivity.this);
        Connection.getDateSettings(Appconfig.TOKEN,AddSalesOrderActivity.this);
        Connection.getProductList(Appconfig.TOKEN,AddSalesOrderActivity.this);
        Connection.getQtemplateList(Appconfig.TOKEN,AddSalesOrderActivity.this);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Add SalesOrder");
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
        input_customer=(TextInputLayout)findViewById(R.id.input_layout_customer);
        input_qtemplate=(TextInputLayout)findViewById(R.id.input_layout_qtemplate);
        input_date=(TextInputLayout)findViewById(R.id.input_layout_date);
        input_exp_date=(TextInputLayout)findViewById(R.id.input_layout_exp_date);
        input_pay_term=(TextInputLayout)findViewById(R.id.input_layout_pay_term);
        input_salesteam=(TextInputLayout)findViewById(R.id.input_layout_salesteam);
        input_salesperson=(TextInputLayout)findViewById(R.id.input_layout_salesperson);
        input_status=(TextInputLayout)findViewById(R.id.input_layout_status);

        productNameList = new ArrayList<String>();
        productNameList.add("Please select");
        productsArrayList = new ArrayList<>();
        statusList=new ArrayList<>();
        customer = (BetterSpinner) findViewById(R.id.customer);
        quotation_template = (BetterSpinner) findViewById(R.id.quotation_template);
        sales_person = (BetterSpinner) findViewById(R.id.salesperson);
        sales_team = (BetterSpinner) findViewById(R.id.salesteam);
        pay_term = (BetterSpinner) findViewById(R.id.pay_term);
        status = (BetterSpinner) findViewById(R.id.status);

        start_date = (EditText) findViewById(R.id.date);
        exp_date = (EditText) findViewById(R.id.exp_date);

        total = (EditText) findViewById(R.id.total);
        tax = (EditText) findViewById(R.id.tax);
        gtotal = (EditText) findViewById(R.id.gtotal);
        discount = (EditText) findViewById(R.id.discount);
        final_price = (EditText) findViewById(R.id.final_price);

        submit=(Button)findViewById(R.id.submit);
        add_products = (Button) findViewById(R.id.add_products);

        tab_products = (TableLayout) findViewById(R.id.table_products);

        statusList.add("Sales order sent");
        statusList.add("Draft sales order ");

        start_year = Calendar.getInstance().get(Calendar.YEAR);
        start_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        exp_year = Calendar.getInstance().get(Calendar.YEAR);
        start_month = Calendar.getInstance().get(Calendar.MONTH);
        exp_month = Calendar.getInstance().get(Calendar.MONTH);
        exp_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        ArrayAdapter<String> customerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, AppSession.customerNameList);
        customerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        customer.setAdapter(customerArrayAdapter);
        ArrayAdapter<String> sales_teamArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, AppSession.sales_teamNameList);
        sales_teamArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sales_team.setAdapter(sales_teamArrayAdapter);
        ArrayAdapter<String> sales_personArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, AppSession.sales_personNameList);
        sales_personArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sales_person.setAdapter(sales_personArrayAdapter);
        ArrayAdapter<String> quotation_templateArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, AppSession.quotation_templateNameList);
        quotation_templateArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        quotation_template.setAdapter(quotation_templateArrayAdapter);

        ArrayAdapter<String> statusArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusList);
        statusArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        status.setAdapter(statusArrayAdapter);

        ArrayAdapter<String> pay_termArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, AppSession.pay_termList);
        pay_termArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        pay_term.setAdapter(pay_termArrayAdapter);


        add_products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog1=new MaterialDialog.Builder(AddSalesOrderActivity.this)
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

                                TableRow row = new TableRow(AddSalesOrderActivity.this);
                                tab_products.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                                int productId = AppSession.productsArrayList.get(AppSession.productNameList.indexOf(selected_product_name)).getId();
                                product_id_array.add(String.valueOf(productId));

                                TextView product = new TextView(AddSalesOrderActivity.this);
                                product.setText(selected_product_name);
                                product_name_array.add(selected_product_name);
                                product.setVisibility(View.VISIBLE);
                                product.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                                product.setPadding(5,0,0,0);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    product.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                                }
                                TextView quantity = new TextView(AddSalesOrderActivity.this);
                                quantity.setText(selected_quantity);
                                quantity_array.add(selected_quantity);
                                product.setVisibility(View.VISIBLE);
                                quantity.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                                quantity.setPadding(5,0,0,0);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    quantity.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                                }
                                TextView unit_price = new TextView(AddSalesOrderActivity.this);
                                unit_price.setText(selected_unit_price);
                                price_array.add(selected_unit_price);
                                product.setVisibility(View.VISIBLE);
                                unit_price.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                                unit_price.setPadding(5,0,0,0);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    unit_price.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                                }
/*
                                TextView description = new TextView(AddSalesOrderActivity.this);
                                description.setText(selected_description);*/
                                description_array.add(selected_description);
                                product.setVisibility(View.VISIBLE);


                                TextView sub_total = new TextView(AddSalesOrderActivity.this);
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
                              //  row.addView(description);
                                row.setVisibility(View.VISIBLE);
                                dialog.dismiss();

                                calulated_total+=Double.parseDouble(selected_sub_total);

                                total.setText(String.valueOf(calulated_total));
                                calculated_tax = (calulated_total * 10) / 100;
                                tax.setText(String.valueOf(calculated_tax));
                                gtotal.setText(String.valueOf(calulated_total + calculated_tax));
                                discount.setText("0.00");
                                final_price.setText(String.valueOf(calulated_total + calculated_tax));
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

                ArrayAdapter<String> product_nameArrayAdapter = new ArrayAdapter<>(AddSalesOrderActivity.this, android.R.layout.simple_dropdown_item_1line,AppSession.productNameList);
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




        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(start_date.getWindowToken(), 0);
                showDialog(START_DATE_PICKER_ID);

            }
        });
        exp_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(start_date.getWindowToken(), 0);
                showDialog(EXP_DATE_PICKER_ID);

            }
        });

        start_date.setShowSoftInputOnFocus(false);
        exp_date.setShowSoftInputOnFocus(false);

        start_date.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_date.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        exp_date.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_exp_date.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        customer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_customer.setError("");
            }
        });
        quotation_template.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_qtemplate.setError("");
            }
        });
        sales_team.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_salesteam.setError("");
            }
        });
        sales_person.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_salesperson.setError("");
            }
        });
        pay_term.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_pay_term.setError("");
            }
        });
        status.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_status.setError("");
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (start_date.getText().toString().isEmpty()){
                    input_date.setError("Please enter start date");
                    return;
                }
                else if (exp_date.getText().toString().isEmpty()){
                    input_exp_date.setError("Please enter expire date");
                    return;
                }
                else if (customer.getText().toString().isEmpty())
                {
                    input_customer.setError("Please enter customer name");
                    return;
                }

                 if (quotation_template.getText().toString().isEmpty()){
                    input_qtemplate.setError("Please select quotation template");
                    return;
                }

                else if (pay_term.getText().toString().isEmpty()){
                    input_pay_term.setError("Please select customer name");
                    return;
                }
                 else if (sales_person.getText().toString().isEmpty()){
                     input_salesperson.setError("Please select sales person name");
                     return;
                 }
                else if (sales_team.getText().toString().isEmpty()){
                    input_salesteam.setError("Please select payment term");
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
                int customerId=AppSession.customerList.get(AppSession.customerNameList.indexOf(customer.getText().toString())).getId();

                new AddSalesOrder().execute(Appconfig.TOKEN, String.valueOf(customerId), String.valueOf(qtemplateId), start_date.getText().toString(),
                        exp_date.getText().toString(), pay_term.getText().toString(), String.valueOf(salesTeamId),
                        String.valueOf(salesPersonId), status.getText().toString(), total.getText().toString(), tax.getText().toString(),
                        gtotal.getText().toString(), discount.getText().toString(), final_price.getText().toString(), AppSession.sales_prefix,
                        AppSession.sales_start_number);
            }
        });
    }
    public void setDialog(Dialog dialog) {
        mdialog = dialog;
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

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == START_DATE_PICKER_ID) {
            return new DatePickerDialog(AddSalesOrderActivity.this, start_date_listener, start_year, start_month, start_day);
        }
        if (id == EXP_DATE_PICKER_ID) {
            return new DatePickerDialog(AddSalesOrderActivity.this, exp_date_listener, exp_year, exp_month, exp_day);
        }
        return null;
    }
    final DatePickerDialog.OnDateSetListener start_date_listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            start_year = year;
            start_month = month;
            start_day = dayOfMonth;

            Date date = new Date(year - 1900, month, dayOfMonth);
            start_date.setText(Connection.getFormatedDate(date));
            start_date.setSelection(start_date.getText().length());

            start_date.clearFocus();
        }
    };

    final DatePickerDialog.OnDateSetListener exp_date_listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            exp_year = year;
            exp_month = month;
            exp_day = dayOfMonth;

            Date date = new Date(year - 1900, month, dayOfMonth);
            exp_date.setText(Connection.getFormatedDate(date));
            exp_date.setSelection(exp_date.getText().length());
            exp_date.clearFocus();
        }
    };
    class AddSalesOrder extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
                dialog = new ProgressDialog(AddSalesOrderActivity.this);
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
            String sales_prefix = params[14];
            String sales_start_number = params[15];

            URL url;
            try {
                JSONObject jsonObject = new JSONObject();
                JSONArray array1 = new JSONArray();
                JSONArray array2 = new JSONArray();
                JSONArray array3 = new JSONArray();
                JSONArray array4 = new JSONArray();
                JSONArray array5 = new JSONArray();
                JSONArray array6 = new JSONArray();

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
                jsonObject.put("sales_prefix", sales_prefix);
                jsonObject.put("sales_start_number",sales_start_number);
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
                SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String post_sales_order_url;
                if (text_url != null) {
                    post_sales_order_url = text_url + "/user/post_sales_order?token=";
                } else {
                    post_sales_order_url = Appconfig.SALESORDER_ADD_URL;
                }

                url = new URL(post_sales_order_url+ tok);
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
                    //System.out.println("error=" + json.get("error"));
                    //succes = json.getString("success");
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
            {   new GetAllSalesOrder().execute(Appconfig.TOKEN);
     /*           start_date.setText("");
                exp_date.setText("");
                total.setText("");
                tax.setText("");
                gtotal.setText("");
                discount.setText("");
                final_price.setText("");*/

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
        progressDialog = new ProgressDialog(AddSalesOrderActivity.this);
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
        MyVolleySingleton.getInstance(AddSalesOrderActivity.this).getRequestQueue().add(stringRequest);

    }

    class GetAllSalesOrder extends AsyncTask<String,Void,String>
    {
        String response;

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
                    get_url = text_url + "/user/sales_orders?token=";
                } else {
                    get_url = Appconfig.SALESORDER_URL;
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
                JSONArray jsonArray=jsonObject.getJSONArray("salesorders");
                AppSession.salesOrderArrayList.clear();
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    SalesOrder salesOrder=new SalesOrder();
                    salesOrder.setCustomer(object.getString("customer"));
                    salesOrder.setSalesPerson(object.getString("person"));
                    salesOrder.setId(object.getInt("id"));
                    salesOrder.setFinal_price(object.getString("final_price"));
                    AppSession.salesOrderArrayList.add(salesOrder);
                }
                AppSession.salesOrder_recyclerView.setAdapter(new SalesOrderAdapter(AddSalesOrderActivity.this, AppSession.salesOrderArrayList));
                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(AddSalesOrderActivity.this, LinearLayoutManager.VERTICAL,false);
                AppSession.salesOrder_recyclerView.setLayoutManager(lmanager);
                AppSession.salesOrder_recyclerView.setItemAnimator(new DefaultItemAnimator());                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));

            } catch (JSONException e) {
                e.printStackTrace();
                Crashlytics.logException(e);

            }
        }}
}
