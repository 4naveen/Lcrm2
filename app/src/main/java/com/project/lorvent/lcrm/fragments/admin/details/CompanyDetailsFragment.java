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
public class CompanyDetailsFragment extends Fragment {
    TextView company_name,email,phone,mobile,fax,main_contact_person,salesTeam,country,website,title,address;
    String company_id;
    View v;
    private boolean helpDisplayed = false;
    private static final String PREF_FIRSTLAUNCH_HELP = "helpDisplayed";
    public CompanyDetailsFragment() {
        // Required empty public constructor
    }
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_details5, container, false);
        showHelpForFirstLaunch();
        company_name=(TextView)v.findViewById(R.id.name);
        email=(TextView)v.findViewById(R.id.email);
        phone=(TextView)v.findViewById(R.id.phone);
        mobile=(TextView)v.findViewById(R.id.mobile);
        fax=(TextView)v.findViewById(R.id.fax);
        main_contact_person=(TextView)v.findViewById(R.id.main_contact_person);
        salesTeam=(TextView)v.findViewById(R.id.salesteam);
        country=(TextView)v.findViewById(R.id.country);
        website=(TextView)v.findViewById(R.id.website);
        title=(TextView)v.findViewById(R.id.title);
        address=(TextView)v.findViewById(R.id.address);
        company_id=getArguments().getString("company_id");

        new CompanyDetails().execute(Appconfig.TOKEN,company_id);
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
    private class CompanyDetails extends AsyncTask<String,Void,String>
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
            String company_id=params[1];
            URL url;
            try {
                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String detail_url;
                if (text_url != null) {
                    detail_url = text_url + "/user/company?token=";
                } else {
                    detail_url= Appconfig.COMPANY_DETAILS_URL;
                }
                url = new URL(detail_url+tok+"&company_id="+company_id);
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
                //Log.i("response",result);
                JSONArray jsonArray=jsonObject.getJSONArray("company");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObject1=jsonArray.getJSONObject(i);
                    company_name.setText(jsonObject1.getString("name"));
                    email.setText(jsonObject1.getString("email"));
                    phone.setText(jsonObject1.getString("phone"));
                    email.setText(jsonObject1.getString("email"));
                    fax.setText(jsonObject1.getString("fax"));
                    website.setText(jsonObject1.getString("website"));
                    title.setText(jsonObject1.getString("title"));
                    mobile.setText(jsonObject1.getString("mobile"));
                    salesTeam.setText(jsonObject1.getString("sales_team"));

                    main_contact_person.setText(jsonObject1.getString("main_contact_person"));
                    country.setText(jsonObject1.getString("country_id"));
                    address.setText(jsonObject1.getString("address"));

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
