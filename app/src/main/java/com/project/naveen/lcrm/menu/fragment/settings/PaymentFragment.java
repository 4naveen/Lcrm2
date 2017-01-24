package com.project.naveen.lcrm.menu.fragment.settings;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.MyVolleySingleton;
import com.project.naveen.lcrm.R;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentFragment extends Fragment {
    BetterSpinner currency;
    ArrayList<String> currencyList;
    RadioGroup paypal;
    CoordinatorLayout coordinator;
    String token,paypal_mode;
    EditText sales_tax_percent,pay_term1,pay_term3,pay_term6,opp_remind,contract_remind,invoice_remind,paypal_uname,paypal_pwd,paypal_sign;
    public PaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_payment2, container, false);
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        coordinator=(CoordinatorLayout)v.findViewById(R.id.coordinator);
        currency=(BetterSpinner)v.findViewById(R.id.currency);
        sales_tax_percent=(EditText)v.findViewById(R.id.sales_tax_perent);
        pay_term1=(EditText)v.findViewById(R.id.pay_term1);
        pay_term3=(EditText)v.findViewById(R.id.pay_term3);
        pay_term6=(EditText)v.findViewById(R.id.pay_term6);
        opp_remind=(EditText)v.findViewById(R.id.opp_remind);
        contract_remind=(EditText)v.findViewById(R.id.contract_ren_remind);
        invoice_remind=(EditText)v.findViewById(R.id.invoice_remind);
        paypal_uname=(EditText)v.findViewById(R.id.paypal_uname);
        paypal_pwd=(EditText)v.findViewById(R.id.paypal_paasword);
        paypal_sign=(EditText)v.findViewById(R.id.signature);
        paypal=(RadioGroup)v.findViewById(R.id.paypal);
//        paypal_mode = ((RadioButton)v.findViewById(paypal.getCheckedRadioButtonId())).getText().toString();
        currencyList=new ArrayList<>();
        currencyList.add("USD");
        currencyList.add("EUR");
        currencyList.add("RUPEES");
        if (actionBar != null) {
            actionBar.setTitle("Payment setting");
        }
        setHasOptionsMenu(true);
        currency=(BetterSpinner)v.findViewById(R.id.currency);
        ArrayAdapter<String> currencyArrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,currencyList);
        currencyArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        currency.setAdapter(currencyArrayAdapter);
        return v;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit_ok,menu);
//       super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
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
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Appconfig.UPDATE_SETTINGS_URL+token,
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
                Map<String, String> params = new HashMap<String, String>();
                params.put("sales_tax",sales_tax_percent.getText().toString());
                params.put("payment_term1",pay_term1.getText().toString());
                params.put("payment_term2",pay_term3.getText().toString());
                params.put("payment_term3",pay_term6.getText().toString());
                params.put("opportunities_reminder_days",opp_remind.getText().toString());
                params.put("contract_renewal_days",contract_remind.getText().toString());
                params.put("invoice_reminder_days",invoice_remind.getText().toString());
                params.put("paypal_username",paypal_uname.getText().toString());
                params.put("paypal_password",paypal_pwd.getText().toString());
                params.put("paypal_signature",paypal_sign.getText().toString());
//                params.put("paypal_testmode",paypal_mode);

                return params;
            }
        };
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);

    }

}
