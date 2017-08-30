package com.project.lorvent.lcrm.fragments.admin.details;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.project.lorvent.lcrm.activities.SendByMailActivity;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class InvoiceDetailsFragment extends Fragment {
    TextView invoice_number,customer,invoice_date,due_date,payment_term,Salesteam,Salesperson,untaxedAmount,taxes,total,discount,final_price,unpaid_amount,status;
    TableLayout tab_products;
    String invoiceId,token;
    private boolean helpDisplayed = false;
    private static final String PREF_FIRSTLAUNCH_HELP = "helpDisplayed";
    View v;
    public InvoiceDetailsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_details3, container, false);
        showHelpForFirstLaunch();
        setHasOptionsMenu(true);

        tab_products = (TableLayout)v.findViewById(R.id.table_products);
        invoice_number=(TextView)v.findViewById(R.id.invoice_number);
        customer=(TextView)v.findViewById(R.id.customer);
        invoice_date=(TextView)v.findViewById(R.id.invoice_date);
        due_date=(TextView)v.findViewById(R.id.due_date);
        payment_term=(TextView)v.findViewById(R.id.pay_term);
        Salesteam=(TextView)v.findViewById(R.id.salesteam);
        Salesperson=(TextView)v.findViewById(R.id.salesperson);
        untaxedAmount=(TextView)v.findViewById(R.id.un_amount);
        taxes=(TextView)v.findViewById(R.id.taxes);
        total=(TextView)v.findViewById(R.id.total);
        discount=(TextView)v.findViewById(R.id.discount);
        final_price=(TextView)v.findViewById(R.id.final_price);
        unpaid_amount=(TextView)v.findViewById(R.id.unpaid_amount);
        status=(TextView)v.findViewById(R.id.status);
        token = Appconfig.TOKEN;

//        quotationId=((QuotationDetailsActivity)getActivity()).getQuotation_id();
        invoiceId=getArguments().getString("invoice_id");

        new InvoiceDetailsTask().execute(token, invoiceId);
        v.findViewById(R.id.multiple_actions).setVisibility(View.INVISIBLE);

        final com.getbase.floatingactionbutton.FloatingActionButton actionA = (com.getbase.floatingactionbutton.FloatingActionButton)v.findViewById(R.id.action_a);
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/plain");

                final PackageManager pm =getActivity().getPackageManager();
                final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
                ResolveInfo best = null;
                for(final ResolveInfo info : matches)
                    if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                        best = info;
                if (best != null)
                    emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
                getActivity().startActivity(emailIntent);
            }
        });
        final com.getbase.floatingactionbutton.FloatingActionButton actionB = (com.getbase.floatingactionbutton.FloatingActionButton)v.findViewById(R.id.action_b);
        actionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String path="/storage/emulated/0/record"+System.currentTimeMillis()+".jpg";
                File f=new File(path);
                Intent browser=new Intent(Intent.ACTION_VIEW);
                browser.setData(Uri.parse("http://www.w3schools.com/images/w3schools_green.jpg"));
                browser.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                startActivity(browser);
            }
        });
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        getActivity().finish();

                        return true;
                    }
                }
                return false;
            }
        });
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_convert,menu);
        menu.findItem(R.id.convert).setVisible(false);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.email)
        {
            Intent intent=new Intent(getActivity(), SendByMailActivity.class);
            intent.putExtra("quotation_id",invoiceId);
            getActivity().startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
    private class InvoiceDetailsTask extends AsyncTask<String, Void, String> {

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
            String invoiceId = params[1];
            URL url;
            try {
                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String detail_url;
                if (text_url != null) {
                    detail_url = text_url + "/user/invoice?token=";
                } else {
                    detail_url= Appconfig.INVOICES_DETAILS_URL;
                }
                url = new URL(detail_url+ tok + "&invoice_id=" + invoiceId);
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
                Crashlytics.logException(e);
            }
            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String result) {
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result);
                JSONArray invoiceJsonArray=jsonObject.getJSONArray("invoice");
                for (int i=0;i<invoiceJsonArray.length();i++)
                {
                    JSONObject jsonObject1 = invoiceJsonArray.getJSONObject(i);
                    invoice_number.setText(jsonObject1.getString("invoice_number"));
                    customer.setText(String.valueOf(jsonObject1.getString("customer")));
                    invoice_date.setText(jsonObject1.getString("invoice_date"));
                    due_date.setText(jsonObject1.getString("due_date"));
                    payment_term.setText(jsonObject1.getString("payment_term"));
                    Salesteam.setText(jsonObject1.getString("salesteam"));
                    Salesperson.setText(jsonObject1.getString("sales_person"));
                    taxes.setText(jsonObject1.getString("tax_amount"));
                    total.setText(jsonObject1.getString("total"));
                    discount.setText(jsonObject1.getString("discount"));
                    final_price.setText(jsonObject1.getString("final_price"));
                    unpaid_amount.setText(jsonObject1.getString("unpaid_amount"));
                    status.setText(jsonObject1.getString("status"));

                }

                JSONArray array = jsonObject.getJSONArray("products");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject product_object = array.getJSONObject(i);
                    TableRow row = new TableRow(getActivity());
                    tab_products.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

                    TextView product = new TextView(getActivity());
                    product.setEllipsize(TextUtils.TruncateAt.END);
                    product.setEms(5);
                    product.setSingleLine();
                    product.setText(product_object.getString("product"));
                    product.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    product.setPadding(5,0,0,0);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        product.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                    }
                    TextView quantity = new TextView(getActivity());
                    quantity.setText(String.valueOf(product_object.get("quantity")));
                    quantity.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    quantity.setPadding(5,0,0,0);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        quantity.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                    }
                    product.setVisibility(View.VISIBLE);
                    TextView unit_price = new TextView(getActivity());
                    unit_price.setText(String.valueOf(product_object.get("unit_price")));
                    unit_price.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    unit_price.setPadding(5,0,0,0);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        unit_price.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                    }
                    product.setVisibility(View.VISIBLE);
                    TextView taxes = new TextView(getActivity());
                    taxes.setEllipsize(TextUtils.TruncateAt.END);
                    taxes.setEms(3);
                    taxes.setSingleLine();
                    taxes.setText(product_object.getString("taxes"));
                    taxes.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    taxes.setPadding(5,0,0,0);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        taxes.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                    }
                    product.setVisibility(View.VISIBLE);
                    TextView sub_total = new TextView(getActivity());
                    sub_total.setEllipsize(TextUtils.TruncateAt.END);
                    sub_total.setEms(3);
                    sub_total.setSingleLine();
                    sub_total.setText(String.valueOf(product_object.get("subtotal")));
                    sub_total.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    sub_total.setPadding(5,0,0,0);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        sub_total.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                    }
                    product.setVisibility(View.VISIBLE);
                    row.addView(product);
                    row.addView(quantity);
                    row.addView(unit_price);
                    row.addView(taxes);
                    row.addView(sub_total);
                    row.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }

            dialog.dismiss();

        }

    }
    private void showHelpForFirstLaunch() {
        if (helpDisplayed)
            return;
        helpDisplayed = getPreferenceValue(PREF_FIRSTLAUNCH_HELP, false);
        if (!helpDisplayed) {
            savePreference(PREF_FIRSTLAUNCH_HELP, true);
            showOverLay();
        }
    }

    private boolean getPreferenceValue(String key, boolean defaultValue) {
        SharedPreferences preferences = getActivity().getSharedPreferences("pref2",MODE_PRIVATE);
        return preferences.getBoolean(key, defaultValue);
    }

    private void savePreference(String key, boolean value) {
        SharedPreferences preferences = getActivity().getSharedPreferences("pref2",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    private void showOverLay(){

        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);

        dialog.setContentView(R.layout.overlay_view);
        LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.overlayLayout);
        ImageView image=(ImageView)dialog.findViewById(R.id.imageView1);
        image.setImageResource(R.drawable.overlay7);
        layout.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View arg0) {

                dialog.dismiss();

            }

        });

        dialog.show();

    }
}
