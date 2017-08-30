package com.project.lorvent.lcrm.fragments.admin.details;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.project.lorvent.lcrm.activities.SendByMailActivity;
import com.project.lorvent.lcrm.adapters.QuotationAdapter;
import com.project.lorvent.lcrm.models.Quotation;
import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuotationDetailFragment extends Fragment {
    TableLayout tab_products;
    TextView quotation_number,customer,date,expiration_date,payment_term,Salesteam,Salesperson,untaxedAmount,taxes,total,discount,final_price,terms_and_condition;
    String quotationId,token;
    View v;
    private boolean helpDisplayed = false;
    private static final String PREF_FIRSTLAUNCH_HELP = "helpDisplayed";
    public QuotationDetailFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_detail_quotation, container, false);
        showHelpForFirstLaunch();
         setHasOptionsMenu(true);
        tab_products = (TableLayout)v.findViewById(R.id.table_products);
        quotation_number = (TextView)v. findViewById(R.id.quotation_number);
        customer = (TextView)v. findViewById(R.id.customer);
        date = (TextView)v. findViewById(R.id.date);
        expiration_date = (TextView)v. findViewById(R.id.exp_date);
        payment_term = (TextView) v.findViewById(R.id.pay_term);
        Salesteam = (TextView)v.findViewById(R.id.salesteam);
        Salesperson = (TextView)v.findViewById(R.id.salesperson);
        untaxedAmount = (TextView)v.findViewById(R.id.un_amount);
        taxes = (TextView)v.findViewById(R.id.taxes);
        total = (TextView)v.findViewById(R.id.total);
        discount = (TextView)v.findViewById(R.id.discount);
        final_price = (TextView)v. findViewById(R.id.final_price);
        terms_and_condition = (TextView)v.findViewById(R.id.terms_condition);

        token = Appconfig.TOKEN;
        quotationId=getArguments().getString("quotationId");
        new QuotationDetailsTask().execute(token, quotationId);
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
        menu.findItem(R.id.convert).setTitle("Convert to SalesOrder");

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.convert)
        {
            final MaterialDialog dialog1 = new MaterialDialog.Builder(getActivity())
                    .title("Convert to Quotation")
                    .content("Are you sure to convert into SalesOrder?")
                    .positiveText("convert")
                    .autoDismiss(false)
                    .positiveColorRes(R.color.colorPrimary)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            new Convert_quotation_to_salesOrder().execute(token,quotationId);
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
        if (item.getItemId()==R.id.email){
          Intent intent=new Intent(getActivity(), SendByMailActivity.class);
            intent.putExtra("quotation_id",quotationId);
            getActivity().startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    private class Convert_quotation_to_salesOrder extends AsyncTask<String,Void,String>
    {
        // ProgressDialog dialog;
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String response="",jsonresponse="";
            JSONObject json;
            BufferedReader bufferedReader;
            String tok=params[0];
            String quotation_id=params[1];
            URL url;
            try {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("quotation_id",quotation_id);
                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String convert_url;
                if (text_url != null) {
                    convert_url = text_url + "/user/convert_quotation_to_sale_order?token=";
                } else {
                    convert_url= Appconfig.QUOTATION__TO_SALESORDER_URL;
                }
                url = new URL(convert_url+tok);
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

            if (result!=null)
            {
                if (result.equals("success"))
                {    new GetAllQuotationTask().execute(Appconfig.TOKEN);
                    Toast.makeText(getActivity(),"successfully converted",Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().finish();
                        }
                    },3000);
                }
                if (result.equals("not_valid_data"))
                {
                    Toast.makeText(getActivity(),"error occured!Try Again",Toast.LENGTH_LONG).show();
                }
            }

        }

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
                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String detail_url;
                if (text_url != null) {
                    detail_url = text_url + "/user/quotation?token=";
                } else {
                    detail_url= Appconfig.QUOTATION_DETAILS_URL;
                }
                url = new URL(detail_url+ tok + "&quotation_id=" + quotationId);
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
            dialog.dismiss();
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result);
                JSONArray quotationJsonArray=jsonObject.getJSONArray("quotation");
                for (int i=0;i<quotationJsonArray.length();i++)
                {
                    JSONObject jsonObject1 = quotationJsonArray.getJSONObject(i);
                    customer.setText(String.valueOf(jsonObject1.getString("company")));
                    date.setText(jsonObject1.getString("date"));
                    expiration_date.setText(jsonObject1.getString("exp_date"));
                    payment_term.setText(jsonObject1.getString("payment_term"));
                    Salesteam.setText(jsonObject1.getString("salesteam"));
                    Salesperson.setText(jsonObject1.getString("sales_person"));
                    taxes.setText(jsonObject1.getString("tax_amount"));
                    total.setText(jsonObject1.getString("total"));
                    discount.setText(jsonObject1.getString("discount"));
                    final_price.setText(jsonObject1.getString("final_price"));
                    terms_and_condition.setText(jsonObject1.getString("terms_and_conditions"));
                    quotation_number.setText(jsonObject1.getString("quotations_number"));

                }
                JSONArray productsJsonArray = jsonObject.getJSONArray("products");

                for (int i = 0; i < productsJsonArray.length(); i++) {
                    JSONObject product_object = productsJsonArray.getJSONObject(i);
                    TableRow row = new TableRow(getActivity());
                    tab_products.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

                    TextView product = new TextView(getActivity());
                    product.setEllipsize(TextUtils.TruncateAt.END);
                    product.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    product.setPadding(5,0,0,0);
                    product.setEms(5);
                    product.setSingleLine();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        product.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                    }
                    product.setText(product_object.getString("product"));

                    TextView quantity = new TextView(getActivity());
                    quantity.setText(String.valueOf(product_object.get("quantity")));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        quantity.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                    }
                    quantity.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    quantity.setPadding(5,0,0,0);
                    product.setVisibility(View.VISIBLE);
                    TextView unit_price = new TextView(getActivity());
                    unit_price.setText(String.valueOf(product_object.get("unit_price")));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        unit_price.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                    }
                    unit_price.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    unit_price.setPadding(5,0,0,0);
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
                    /*taxes.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    product.setPadding(5,5,5,5);*/
                    product.setVisibility(View.VISIBLE);
                    TextView sub_total = new TextView(getActivity());
                    sub_total.setEllipsize(TextUtils.TruncateAt.END);
                    sub_total.setEms(3);
                    sub_total.setSingleLine();
                    sub_total.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    sub_total.setPadding(5,0,0,0);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        sub_total.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                    }
                    sub_total.setText(String.valueOf(product_object.get("subtotal")));
                   /* sub_total.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    product.setPadding(5,5,5,5);*/
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
              //  Crashlytics.logException(e);
            }


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

    class GetAllQuotationTask extends AsyncTask<String,Void,String>
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
                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String get_url;
                if (text_url != null) {
                    get_url = text_url + "/user/quotations?token=";
                } else {
                    get_url= Appconfig.QUOTATION_URL;
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
                JSONArray jsonArray=jsonObject.getJSONArray("quotations");
                if (AppSession.quotationArrayList.size()!=0)
                {AppSession.quotationArrayList.clear();}

                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    Quotation quotation=new Quotation();
                    quotation.setCustomer(object.getString("customer"));
                    quotation.setPerson(object.getString("person"));
                    quotation.setId(object.getInt("id"));
                    quotation.setDate(object.getString("date"));
                    quotation.setFinal_price(object.getString("final_price"));
                    AppSession.quotationArrayList.add(quotation);
                }

                QuotationAdapter mAdapter=new QuotationAdapter(getActivity(), AppSession.quotationArrayList);

                AppSession.quotation_recyclerView.setAdapter(mAdapter);
                AppSession.quotation_recyclerView.setItemAnimator(new DefaultItemAnimator());
                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
                //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);

                AppSession.quotation_recyclerView.setLayoutManager(lmanager);

            } catch (JSONException e) {
                e.printStackTrace();
                Crashlytics.logException(e);

            }
        }}
}
