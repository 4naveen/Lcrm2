package com.project.lorvent.lcrm.fragments.admin.edit;


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
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.lorvent.lcrm.fragments.admin.details.SalesOrderDetailFragment;
import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.Connection;
import com.project.lorvent.lcrm.utils.MyVolleySingleton;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.models.Products;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SalesOrderEditFragment extends Fragment {

    BetterSpinner customer, quotation_template, sales_person, sales_team, status,pay_term;
    Button submit, add_products;
    String salesOrderId;
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
    TextInputLayout input_customer,input_qtemplate,input_date,input_exp_date,input_pay_term,input_salesteam,input_salesperson,input_status;
    Configuration config;
    LinearLayout linearLayout;
    TextInputLayout input_product_name, input_quantity,input_subtotal;
    ProgressDialog progressDialog;
    ArrayList<String>statusList;
    double previous_sub_total;

    public SalesOrderEditFragment() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_edit11, container, false);
        config = getResources().getConfiguration();

        Connection.getStaffList(Appconfig.TOKEN,getActivity());
        Connection.getContactList(Appconfig.TOKEN,getActivity());
        Connection.getSalesTeamList(Appconfig.TOKEN,getActivity());
        Connection.getDateSettings(Appconfig.TOKEN,getActivity());
        Connection.getProductList(Appconfig.TOKEN,getActivity());
        Connection.getQtemplateList(Appconfig.TOKEN,getActivity());
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        salesOrderId=getArguments().getString("salesOrderId");
        linearLayout=(LinearLayout)v.findViewById(R.id.layout);

        setHasOptionsMenu(true);
        if (actionBar != null) {
            actionBar.setTitle("Edit SalesOrder");
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
        statusList=new ArrayList<>();
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

        statusList.add("Sales order sent");
        statusList.add("Draft sales order ");
        new SalesOrderDetailsTask().execute(Appconfig.TOKEN, salesOrderId);

        start_year = Calendar.getInstance().get(Calendar.YEAR);
        start_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        exp_year = Calendar.getInstance().get(Calendar.YEAR);
        start_month = Calendar.getInstance().get(Calendar.MONTH);
        exp_month = Calendar.getInstance().get(Calendar.MONTH);
        exp_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

          customer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
              @Override
              public void onFocusChange(View v, boolean hasFocus) {
                  ArrayAdapter<String> customerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, AppSession.customerNameList);
                  customerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                  customer.setAdapter(customerArrayAdapter);
              }
          });
        sales_team.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ArrayAdapter<String> sales_teamArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, AppSession.sales_teamNameList);
                sales_teamArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                sales_team.setAdapter(sales_teamArrayAdapter);
            }
        });
        sales_person.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ArrayAdapter<String> sales_personArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, AppSession.sales_personNameList);
                sales_personArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                sales_person.setAdapter(sales_personArrayAdapter);
            }
        });
        quotation_template.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ArrayAdapter<String> quotation_templateArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, AppSession.quotation_templateNameList);
                quotation_templateArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                quotation_template.setAdapter(quotation_templateArrayAdapter);
            }
        });

        pay_term.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ArrayAdapter<String> pay_termArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, AppSession.pay_termList);
                pay_termArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                pay_term.setAdapter(pay_termArrayAdapter);
            }
        });
        status.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ArrayAdapter<String> statusArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, AppSession.statusList);
                statusArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                status.setAdapter(statusArrayAdapter);
            }
        });

        add_products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog1=new MaterialDialog.Builder(getActivity())
                        .title("Add Products")
                        .autoDismiss(false)
                        .customView(R.layout.add_products_dialog, true)
                        .positiveText("SAVE")
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

                                final TableRow row = new TableRow(getActivity());
                                tab_products.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                                int productId = AppSession.productsArrayList.get(AppSession.productNameList.indexOf(selected_product_name)).getId();
                                product_id_array.add(String.valueOf(productId));

                                TextView product = new TextView(getActivity());
                                product.setText(selected_product_name);
                                product_name_array.add(selected_product_name);
                                product.setVisibility(View.VISIBLE);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    product.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                                }
                                product.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                                product.setPadding(5,0,0,0);
                                TextView quantity = new TextView(getActivity());
                                quantity.setText(selected_quantity);

                                quantity_array.add(selected_quantity);
                                product.setVisibility(View.VISIBLE);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    quantity.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                                }
                                quantity.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                                quantity.setPadding(5,0,0,0);
                                TextView unit_price = new TextView(getActivity());
                                unit_price.setText(selected_unit_price);
                                price_array.add(selected_unit_price);
                                product.setVisibility(View.VISIBLE);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    unit_price.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                                }
                                unit_price.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                                unit_price.setPadding(5,0,0,0);
//
//                                TextView description = new TextView(getActivity());
//                                description.setText(selected_description);
//                                description_array.add(selected_description);
                                product.setVisibility(View.VISIBLE);
                                TextView sub_total = new TextView(getActivity());
                                sub_total.setText(selected_sub_total);
                                sub_total_array.add(selected_sub_total);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    sub_total.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                                }
                                sub_total.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                                sub_total.setPadding(5,0,0,0);

                                product.setVisibility(View.VISIBLE);
                                final  ImageView delete=new ImageView(getActivity());
                                delete.setImageResource(R.mipmap.ic_delete_forever_black_18dp);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    delete.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                                }
                                delete.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                                delete.setPadding(5,0,0,0);
                                delete.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        row.removeAllViews();
                                    }
                                });
                                row.addView(product);
                                row.addView(quantity);
                                row.addView(unit_price);
                                row.addView(sub_total);
                                row.addView(delete);
                              //  row.addView(description);
                                row.setVisibility(View.VISIBLE);
                                dialog.dismiss();
                                calulated_total+=Double.parseDouble(selected_sub_total);
                                double final_calculated_total=previous_sub_total+calulated_total;
                                total.setText(String.valueOf(final_calculated_total));
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

                ArrayAdapter<String> product_nameArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line,AppSession.productNameList);
                product_nameArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                product_name.setAdapter(product_nameArrayAdapter);
                product_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                         String product_name_text=productNameList.get(position);
                        selected_product_name = String.valueOf(parent.getItemAtPosition(position));
                        int productId = AppSession.productsArrayList.get(AppSession.productNameList.indexOf(selected_product_name)).getId();
                        if (product_name.getSelectedItemPosition() >= 0) {
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
                        sub_total.setText(String.valueOf(sub_total_int));
                        selected_sub_total = String.valueOf(sub_total_int);
                    }
                });

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

        exp_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getActivity(). getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(start_date.getWindowToken(), 0);
                DatePickerDialog d = new DatePickerDialog(getActivity(), exp_date_listener, exp_year, exp_month, exp_day);
                d.show();
            }
        });
        start_date.setShowSoftInputOnFocus(false);
        exp_date.setShowSoftInputOnFocus(false);

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

                new EditQuotation().execute(Appconfig.TOKEN,  String.valueOf(customerId), String.valueOf(qtemplateId), start_date.getText().toString(),
                        exp_date.getText().toString(), pay_term.getText().toString(), String.valueOf(salesTeamId),
                        String.valueOf(salesPersonId), total.getText().toString(), tax.getText().toString(),
                        gtotal.getText().toString(), discount.getText().toString(), final_price.getText().toString(),salesOrderId);

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
            Date date = new Date(year - 1900, month, dayOfMonth);
            start_date.setText(Connection.getFormatedDate(date));
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
           // String status = params[8];
            String total = params[8];
            String tax_amount = params[9];
            String grand_total = params[10];
            String discount = params[11];
            String final_price = params[12];
            String sales_order_id=params[13];

            URL url;
            try {
                JSONObject jsonObject = new JSONObject();
                JSONArray array1 = new JSONArray();
                JSONArray array2 = new JSONArray();
                JSONArray array3 = new JSONArray();
                JSONArray array4 = new JSONArray();
                JSONArray array5 = new JSONArray();
                JSONArray array6 = new JSONArray();

                jsonObject.put("sales_order_id", sales_order_id);
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
               // jsonObject.put("status", status);
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

                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String edit_url;
                if (text_url != null) {
                    edit_url= text_url + "/user/edit_sales_order?token=";
                } else {
                    edit_url= Appconfig.SALESORDER_EDIT_URL;
                }
                url = new URL(edit_url+ tok);
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

                Bundle bundle=new Bundle();
                bundle.putString("salesorder_id",salesOrderId);
                FragmentTransaction trans1=getFragmentManager().beginTransaction();
                Fragment fragment1=new SalesOrderDetailFragment();
                fragment1.setArguments(bundle);
                trans1.replace(R.id.frame,fragment1);
                trans1.addToBackStack(null);
                trans1.commit();
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


    public void getProductDetails(String token, String product_id) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading, please wait");
        progressDialog.setTitle("Connecting server");
        progressDialog.show();
        progressDialog.setCancelable(false);

        SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String detail_url;
        if (text_url != null) {
            detail_url= text_url + "/user/product?token=";
        } else {
            detail_url= Appconfig.PRODUCTS_DETAILS_URL;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET, detail_url+ token + "&product_id=" + product_id,
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
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);

    }


    private class SalesOrderDetailsTask extends AsyncTask<String, Void, String> {

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
            String salesOrderId = params[1];
            URL url;
            try {

                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String detail_url;
                if (text_url != null) {
                    detail_url= text_url + "/user/sales_order?token=";
                } else {
                    detail_url= Appconfig.SALESORDER_DETAILS_URL;
                }

                url = new URL(detail_url+ tok + "&salesorder_id=" + salesOrderId);
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
            }
            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result);
                JSONArray quotationJsonArray=jsonObject.getJSONArray("salesorder");
                for (int i=0;i<quotationJsonArray.length();i++)
                {
                    JSONObject jsonObject1 = quotationJsonArray.getJSONObject(i);
                    customer.setText(String.valueOf(jsonObject1.getString("customer")));
                    start_date.setText(jsonObject1.getString("date"));
                    exp_date.setText(jsonObject1.getString("exp_date"));
                    pay_term.setText(jsonObject1.getString("payment_term"));
                    sales_team.setText(jsonObject1.getString("salesteam"));
                    sales_person.setText(jsonObject1.getString("sales_person"));
                    tax.setText(jsonObject1.getString("tax_amount"));
                    total.setText(jsonObject1.getString("total"));
                    previous_sub_total=Double.parseDouble(String.valueOf(jsonObject1.getString("total")));

                    discount.setText(jsonObject1.getString("discount"));
                    final_price.setText(jsonObject1.getString("final_price"));

                }
                JSONArray productsJsonArray = jsonObject.getJSONArray("products");

                for (int i = 0; i < productsJsonArray.length(); i++) {
                    JSONObject product_object = productsJsonArray.getJSONObject(i);
                    final TableRow row = new TableRow(getActivity());
                    tab_products.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

                    final  TextView product = new TextView(getActivity());
                    product.setId(i);
                    product.setText(product_object.getString("product"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        product.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                    }
                    product.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    product.setPadding(5,0,0,0);
                    final TextView quantity_textview = new TextView(getActivity());
                    quantity_textview.setId(i);
                    quantity_textview.setText(String.valueOf(product_object.get("quantity")));
                    product.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        quantity_textview.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                    }
                    quantity_textview.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    quantity_textview.setPadding(5,0,0,0);
                    final TextView unit_price_textview = new TextView(getActivity());
                    unit_price_textview.setId(i);
                    unit_price_textview.setText(String.valueOf(product_object.get("unit_price")));
                    product.setVisibility(View.VISIBLE);
/*
                    final TextView description_textview = new TextView(getActivity());
                    description_textview.setId(i);
                    description_textview.setText(product_object.getString("description"));*/
                    product.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        unit_price_textview.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                    }
                    unit_price_textview.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    unit_price_textview.setPadding(5,0,0,0);

                    final TextView sub_total_textview = new TextView(getActivity());
                    sub_total_textview.setId(i);
                    sub_total_textview.setText(String.valueOf(product_object.get("subtotal")));
                    product.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        sub_total_textview.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                    }
                    sub_total_textview.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    sub_total_textview.setPadding(5,0,0,0);
                    final  ImageView delete=new ImageView(getActivity());
                    delete.setImageResource(R.mipmap.ic_delete_forever_black_18dp);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        delete.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                    }
                    delete.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    delete.setPadding(5,0,0,0);
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            row.removeAllViews();
                        }
                    });
                /*    edit=new ImageView(getActivity());
                    edit.setImageResource(R.mipmap.ic_create_black_18dp);
                    edit.setId(R.id.edit);*/

                   /* sub_total.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    product.setPadding(5,5,5,5);*/
                    row.addView(product);
                    row.addView(quantity_textview);
                    row.addView(unit_price_textview);
                    row.addView(sub_total_textview);
                    row.addView(delete);
                    //row.addView(description_textview);

                    row.setVisibility(View.VISIBLE);


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }

}
