package com.project.lorvent.lcrm.fragments.admin.details;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
public class LeadDetailsFragment extends Fragment {
String lead_id;
    View v;
    TextView textViewOpp,textCompanyName,textViewAddress,textViewContactName,textViewTitle,textViewCountry,textViewCustomer
            ,textViewEmail,textViewMobile,textViewSalesperson,textViewSalesteam,textViewPriority;
    private boolean helpDisplayed = false;
    private static final String PREF_FIRSTLAUNCH_HELP = "helpDisplayed";
    public LeadDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_details2, container, false);
        showHelpForFirstLaunch();

        lead_id=getArguments().getString("lead_id");
        textViewOpp=(TextView)v.findViewById(R.id.opp);
       //textCompanyName=(TextView)v.findViewById(R.id.company_name);
        textViewAddress=(TextView)v.findViewById(R.id.address);
        textViewContactName=(TextView)v.findViewById(R.id.contacts);
        textViewTitle=(TextView)v.findViewById(R.id.title);
        textViewCustomer=(TextView)v.findViewById(R.id.customer);
        textViewEmail=(TextView)v.findViewById(R.id.email);
        textViewMobile=(TextView)v.findViewById(R.id.mobile);
        textViewSalesperson=(TextView)v.findViewById(R.id.salesperson);
        textViewSalesteam=(TextView)v.findViewById(R.id.salesteam);
        textViewPriority=(TextView)v.findViewById(R.id.priority);
        textViewCountry=(TextView)v.findViewById(R.id.country);
        new LeadDetailsTask().execute(Appconfig.TOKEN,lead_id);
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
    private class LeadDetailsTask extends AsyncTask<String,Void,String>
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
            String lead_id=params[1];
            URL url;
            try {
                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String detail_url;
                if (text_url != null) {
                    detail_url = text_url + "/user/lead?token=";
                } else {
                    detail_url= Appconfig.LEAD_DETAILS_URL;
                }
                url = new URL(detail_url+tok+"&lead_id="+lead_id);
                conn = (HttpURLConnection) url.openConnection();
                int responseCode=conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
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
            dialog.dismiss();
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result);
                JSONArray jsonArray=jsonObject.getJSONArray("lead");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObject1=jsonArray.getJSONObject(i);
                    textViewOpp.setText(jsonObject1.getString("opportunity"));
                   // textCompanyName.setText(jsonObject1.getString("customer"));
                    textViewCustomer.setText(jsonObject1.getString("customer"));
                    textViewCountry.setText(jsonObject1.getString("country"));
                    textViewAddress.setText(jsonObject1.getString("address"));
                    textViewContactName.setText(jsonObject1.getString("contact_name"));
                    textViewTitle.setText(jsonObject1.getString("title"));
                    textViewEmail.setText(jsonObject1.getString("email"));
                    textViewMobile.setText(jsonObject1.getString("phone"));
                    textViewPriority.setText(jsonObject1.getString("priority"));
                    textViewSalesperson.setText(jsonObject1.getString("sales_person"));
                    JSONObject jsonObject2=jsonObject1.getJSONObject("salesteam");
                    textViewSalesteam.setText(jsonObject2.getString("salesteam"));

                }

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
