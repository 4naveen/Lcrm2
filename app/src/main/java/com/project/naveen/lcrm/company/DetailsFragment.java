package com.project.naveen.lcrm.company;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
TextView company_name,email,phone,mobile,fax,main_contact_person,salesTeam,country,website,title,address;
String company_id;

    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_details5, container, false);
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
            Log.i("company_id--",company_id);
            URL url;
            try {

                url = new URL(Appconfig.COMPANY_DETAILS_URL+tok+"&company_id="+company_id);
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
                    main_contact_person.setText(jsonObject1.getString("main_contact_person"));
                    country.setText(jsonObject1.getString("country_id"));
                    address.setText(jsonObject1.getString("address"));

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }
}
