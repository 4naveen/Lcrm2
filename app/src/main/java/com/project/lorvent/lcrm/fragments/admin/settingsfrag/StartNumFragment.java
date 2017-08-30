package com.project.lorvent.lcrm.fragments.admin.settingsfrag;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.MyVolleySingleton;
import com.project.lorvent.lcrm.R;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class StartNumFragment extends Fragment {
    BetterSpinner invoice_template,sales_order_template,quotation_template;
    ArrayList<String> invoiceArrayList,salesOrderArrayList,quotationArrayList;
    CoordinatorLayout coordinator;
    EditText quotation_prefix,quotation_start_number,sales_prefix,sales_start_number,invoice_prefix,
            invoice_start_number,invoice_payment_prefix,invoice_payment_start_number;
    public StartNumFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_start_num, container, false);
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();

        quotation_prefix=(EditText)v.findViewById(R.id.q_prefix);
        quotation_start_number=(EditText)v.findViewById(R.id.qStartNumber);
        sales_prefix=(EditText)v.findViewById(R.id.sales_prefix);
        sales_start_number=(EditText)v.findViewById(R.id.sales_start_number);
        invoice_prefix=(EditText)v.findViewById(R.id.invoice_prefix);
        invoice_start_number=(EditText)v.findViewById(R.id.invoice_start_number);
        invoice_payment_prefix=(EditText)v.findViewById(R.id.invoice_pay_prefix);
        invoice_payment_start_number=(EditText)v.findViewById(R.id.invoice_pay_start_number);

        coordinator=(CoordinatorLayout)v.findViewById(R.id.coordinator);

        invoiceArrayList=new ArrayList<>();
        salesOrderArrayList=new ArrayList<>();
        quotationArrayList=new ArrayList<>();

        invoiceArrayList.add("RED");
        invoiceArrayList.add("BLUE");
        invoiceArrayList.add("RED-GREEN");
        salesOrderArrayList.add("RED");
        salesOrderArrayList.add("BLUE");
        salesOrderArrayList.add("RED-GREEN");

        quotationArrayList.add("RED");
        quotationArrayList.add("BLUE");
        quotationArrayList.add("RED-GREEN");
        if (actionBar != null) {
            actionBar.setTitle("Start Number setting");
        }
        setHasOptionsMenu(true);

        invoice_template=(BetterSpinner)v.findViewById(R.id.invoice_template);
        sales_order_template=(BetterSpinner)v.findViewById(R.id.salesorder_template);
        quotation_template=(BetterSpinner)v.findViewById(R.id.quotation_template);
        ArrayAdapter<String> invoiceArrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,invoiceArrayList);
        invoiceArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        invoice_template.setAdapter(invoiceArrayAdapter);

        ArrayAdapter<String> salesOrderArrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,salesOrderArrayList);
        salesOrderArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sales_order_template.setAdapter(salesOrderArrayAdapter);

        ArrayAdapter<String> quotationArrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,quotationArrayList);
        quotationArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        quotation_template.setAdapter(quotationArrayAdapter);

        return v;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit_ok,menu);
//       super.onCreateOptionsMenu(menu, inflater);

    }  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.edit)
        {InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            updateSetting(Appconfig.TOKEN);

        }
        return super.onOptionsItemSelected(item);
    }
    private void updateSetting(String token) {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading, please wait");
        progressDialog.setTitle("Connecting server");
        progressDialog.show();
        progressDialog.setCancelable(false);

        SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String update_url;
        if (text_url != null) {
            update_url= text_url + "/user/update_settings?token=";
        } else {
            update_url= Appconfig.UPDATE_SETTINGS_URL;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST,update_url+token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("response--", String.valueOf(response));

                        final Snackbar snackbar=Snackbar.make(coordinator, " Successfully updated ",Snackbar.LENGTH_LONG);
                        View v=snackbar.getView();
                        v.setMinimumWidth(1000);

                        TextView tv=(TextView)v.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.RED);
                        snackbar.show();
                        progressDialog.dismiss();

                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("response--", String.valueOf(error));
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("quotation_prefix",quotation_prefix.getText().toString());
                params.put("quotation_start_number",quotation_start_number.getText().toString());
                params.put("sales_prefix",sales_prefix.getText().toString());
                params.put("sales_start_number",sales_start_number.getText().toString());
                params.put("invoice_prefix",invoice_prefix.getText().toString());
                params.put("invoice_start_number",invoice_start_number.getText().toString());
                params.put("invoice_payment_prefix",invoice_payment_prefix.getText().toString());
                params.put("invoice_payment_start_number",invoice_payment_start_number.getText().toString());


                params.put("quotation_template",quotation_template.getText().toString());
                params.put("saleorder_template",sales_order_template.getText().toString());
                params.put("invoice_template",invoice_template.getText().toString());


                return params;
            }
        } ;
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);



    }
}
