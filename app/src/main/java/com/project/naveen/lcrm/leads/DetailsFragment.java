package com.project.naveen.lcrm.leads;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {
String lead_id;
    TextView textViewOpp,textCompanyName,textViewAddress,textViewContactName,textViewTitle,textViewCountry,textViewCustomer
            ,textViewEmail,textViewMobile,textViewSalesperson,textViewSalesteam,textViewPriority;
    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v=inflater.inflate(R.layout.fragment_details2, container, false);
        lead_id=getArguments().getString("lead_id");
        textViewOpp=(TextView)v.findViewById(R.id.opp);
        textCompanyName=(TextView)v.findViewById(R.id.company_name);
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
            Log.i("lead_id=",lead_id);
            URL url;
            try {
                url = new URL(Appconfig.LEAD_DETAILS_URL+tok+"&lead_id="+lead_id);
                conn = (HttpURLConnection) url.openConnection();
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
                        Log.d("output lines", line);
                    }
                    Log.i("response",response);
                    json = new JSONObject(response);
                    jsonResponse=json.getString("error");
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
                JSONArray jsonArray=jsonObject.getJSONArray("lead");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObject1=jsonArray.getJSONObject(i);
                    textViewOpp.setText(jsonObject1.getString("opportunity"));
                    textCompanyName.setText(jsonObject1.getString("company"));

                    textViewAddress.setText(jsonObject1.getString("address"));
                    textViewContactName.setText(jsonObject1.getString("contact_name"));
                    textViewTitle.setText(jsonObject1.getString("title"));
                    textViewEmail.setText(jsonObject1.getString("email"));
                    textViewMobile.setText(jsonObject1.getString("phone"));
                    textViewPriority.setText(jsonObject1.getString("priority"));
                    textViewSalesperson.setText(jsonObject1.getString("sales_person"));
                    textViewSalesteam.setText(jsonObject1.getString("salesteam"));
                    textViewCustomer.setText(jsonObject1.getString("customer"));
                    textViewCountry.setText(jsonObject1.getString("country"));


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }
}
