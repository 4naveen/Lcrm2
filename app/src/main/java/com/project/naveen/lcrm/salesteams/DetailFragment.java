package com.project.naveen.lcrm.salesteams;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.detailsactivity.DetailSalesTeamActivity;

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
public class DetailFragment extends Fragment {

    String sales_team_id,sales_team_id_position;
    String token;
    TextView tv1,tv2,tv3,tv4,tv5,tv6,tv7,tv8,tv9;
    CardView cardView;
    LinearLayout layout;
    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_detail5, container, false);;
         sales_team_id=getArguments().getString("salesteam_id");
        cardView=(CardView)v.findViewById(R.id.cv);
        layout=(LinearLayout)v.findViewById(R.id.layout);
        token= Appconfig.TOKEN;
        tv1=(TextView)v.findViewById(R.id.salesteamName);
        tv2=(TextView)v.findViewById(R.id.invoice_target);
        tv3=(TextView)v.findViewById(R.id.invoice_forecast);
        tv4=(TextView)v.findViewById(R.id.actual_invoice);
        tv5=(TextView)v.findViewById(R.id.leads);
        tv6=(TextView)v.findViewById(R.id.team_leader);
        tv7=(TextView)v.findViewById(R.id.opportnity);
        tv8=(TextView)v.findViewById(R.id.quotation);
        tv9=(TextView)v.findViewById(R.id.created);

        new SalesTeamDetailsTask().execute(token,sales_team_id);
        return v;
    }
    private class SalesTeamDetailsTask extends AsyncTask<String,Void,String>
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
            String sales_team_id=params[1];
            URL url;
            try {

                url = new URL(Appconfig.SALESTEAM_DETAILS_URL+tok+"&salesteam_id="+sales_team_id);
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
                        //  Log.d("output lines", line);
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
                JSONArray jsonArray=jsonObject.getJSONArray("salesteam");
                for (int i=0;i<jsonArray.length();i++)
                {      JSONObject jsonObject1=jsonArray.getJSONObject(i);
                    tv1.setText(jsonObject1.getString("salesteam"));
                    tv2.setText(String.valueOf(jsonObject1.get("invoice_target")));
                    tv3.setText(String.valueOf(jsonObject1.get("invoice_forecast")));
                    tv4.setText(String.valueOf(jsonObject1.get("actual_invoice")));
                    tv5.setText(String.valueOf(jsonObject1.get("leads")));
                    tv6.setText(String.valueOf(jsonObject1.get("team_leader")));
                    tv7.setText(String.valueOf(jsonObject1.get("opportunities")));
                    tv8.setText(String.valueOf(jsonObject1.get("quotations")));
                    tv9.setText(jsonObject1.getString("created_at"));

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }
}
