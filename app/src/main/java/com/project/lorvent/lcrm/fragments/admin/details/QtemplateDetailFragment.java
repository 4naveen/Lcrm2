package com.project.lorvent.lcrm.fragments.admin.details;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class QtemplateDetailFragment extends Fragment {
    TextView quotation_template,quotation_duration,immediate_pay,total,tax_amount,grand_total,terms_and_condition;
    String qtemplateId,token;
    TableLayout tab_products;
    View v;
    private boolean helpDisplayed = false;
    private static final String PREF_FIRSTLAUNCH_HELP = "helpDisplayed";
    public QtemplateDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_detail3, container, false);
        showHelpForFirstLaunch();

        tab_products=(TableLayout)v.findViewById(R.id.table_products);
        quotation_template=(TextView)v.findViewById(R.id.qtemplate);
        quotation_duration=(TextView)v.findViewById(R.id.qduration);
        immediate_pay=(TextView)v.findViewById(R.id.immediate_pay);
        total=(TextView)v.findViewById(R.id.total);
        tax_amount=(TextView)v.findViewById(R.id.taxes);
        grand_total=(TextView)v.findViewById(R.id.grand_total);
        terms_and_condition=(TextView)v.findViewById(R.id.terms_condition);
        qtemplateId=getArguments().getString("qtemplateId");
        token= Appconfig.TOKEN;


        new QtemplateDetailsTask().execute(token,qtemplateId);
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
                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String detail_url;
                if (text_url != null) {
                    detail_url = text_url + "/user/qtemplate?token=";
                } else {
                    detail_url= Appconfig.QTEMPLATE_DETAILS_URL;
                }
                url = new URL(detail_url+tok+"&qtemplate_id="+qtemplateId);
                conn = (HttpURLConnection) url.openConnection();
                int responseCode=conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }

                    jsonResponse=response;

                }
                else {
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        response += line;
                    }
                    json = new JSONObject(response);
                    jsonResponse=json.getString("error");
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
                JSONObject jsonObject1=jsonObject.getJSONObject("qtemplate");
                JSONArray array=jsonObject.getJSONArray("products");
                quotation_template.setText(jsonObject1.getString("quotation_template"));
                quotation_duration.setText(String.valueOf(jsonObject1.getString("quotation_duration")));
                immediate_pay.setText(String.valueOf(jsonObject1.get("immediate_payment")));
                for (int i=0;i<array.length();i++)
                {
                    JSONObject product_object=array.getJSONObject(i);
                    TableRow row=new TableRow(getActivity());
                    tab_products.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

                    TextView product=new TextView(getActivity());
                    product.setEllipsize(TextUtils.TruncateAt.END);
                    product.setEms(5);
                    product.setSingleLine();
                    product.setText(product_object.getString("product"));
                    product.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    product.setPadding(5,0,0,0);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        product.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                    }
                    TextView quantity=new TextView(getActivity());
                    quantity.setText(String.valueOf(product_object.get("quantity")));
                    quantity.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    quantity.setPadding(5,0,0,0);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        quantity.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                    }
                    product.setVisibility(View.VISIBLE);
                    TextView unit_price=new TextView(getActivity());
                    unit_price.setText(String.valueOf(product_object.get("unit_price")));
                    unit_price.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    unit_price.setPadding(5,0,0,0);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        unit_price.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                    }
                    product.setVisibility(View.VISIBLE);
                    TextView taxes=new TextView(getActivity());
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
                    TextView sub_total=new TextView(getActivity());
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
                tax_amount.setText(String.valueOf(jsonObject1.getString("tax_amount")));
                total.setText(String.valueOf(jsonObject1.getString("total")));
                grand_total.setText(String.valueOf(jsonObject1.getString("grand_total")));
                terms_and_condition.setText(jsonObject1.getString("terms_and_conditions"));
                dialog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
                Crashlytics.logException(e);
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
}
