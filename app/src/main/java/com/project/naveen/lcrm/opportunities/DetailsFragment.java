package com.project.naveen.lcrm.opportunities;


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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.MyVolleySingleton;
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
    String token,opportunity_id;
    TextView textViewOpp,textViewActionDate,textViewNextAction,textViewStage,textViewLead,textViewExpectedRevenue,textViewCustomer
            ,textViewEmail,textViewMobile,textViewSalesperson,textViewSalesteam,textViewPriority,textViewCall,textViewMeeting;
    CardView cardView;
     FrameLayout layout;
    int customer_id,salesTeamId,salesPersonId;
    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_details, container, false);
        opportunity_id=getArguments().getString("opp_id");
        cardView=(CardView)v.findViewById(R.id.cv);
        layout=(FrameLayout)v.findViewById(R.id.layout);
        textViewOpp=(TextView)v.findViewById(R.id.opp);
        textViewActionDate=(TextView)v.findViewById(R.id.action_date);
        textViewNextAction=(TextView)v.findViewById(R.id.next_action);
        textViewStage=(TextView)v.findViewById(R.id.stages);
        textViewExpectedRevenue=(TextView)v.findViewById(R.id.expeted_revenue);
        textViewCustomer=(TextView)v.findViewById(R.id.customer);
        textViewEmail=(TextView)v.findViewById(R.id.email);
        textViewMobile=(TextView)v.findViewById(R.id.mobile);
        textViewSalesperson=(TextView)v.findViewById(R.id.salesperson);
        textViewSalesteam=(TextView)v.findViewById(R.id.salesteam);
        textViewPriority=(TextView)v.findViewById(R.id.priority);
        textViewCall=(TextView)v.findViewById(R.id.oppcall);
        textViewMeeting=(TextView)v.findViewById(R.id.oppmeeting);
        Log.i("oppor_id",opportunity_id);
        token=Appconfig.TOKEN;
        new OpportunityDetailsTask().execute(token,opportunity_id);
        return v;
    }
    private class OpportunityDetailsTask extends AsyncTask<String,Void,String>
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
            String opportunity_id=params[1];
            URL url;
            try {

                url = new URL(Appconfig.OPPORTUNITY_DETAILS_URL+tok+"&opportunity_id="+opportunity_id);
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
                JSONArray jsonArray=jsonObject.getJSONArray("opportunity");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObject1=jsonArray.getJSONObject(i);
                    textViewOpp.setText(jsonObject1.getString("opportunity"));
                    textViewActionDate.setText(jsonObject1.getString("next_action"));
                    textViewNextAction.setText(jsonObject1.getString("next_action_title"));
                    textViewStage.setText(jsonObject1.getString("stages"));
                    textViewExpectedRevenue.setText(jsonObject1.getString("expected_revenue"));
                    textViewEmail.setText(jsonObject1.getString("email"));
                    textViewMobile.setText(jsonObject1.getString("phone"));
                    textViewPriority.setText(jsonObject1.getString("priority"));
                    textViewSalesperson.setText(jsonObject1.getString("sales_person"));
                    textViewSalesteam.setText(jsonObject1.getString("salesteam"));
                    textViewCustomer.setText(jsonObject1.getString("company"));
                    //textViewCall.setText(jsonObject1.getInt("calls"));
                   // textViewMeeting.setText(jsonObject1.getInt("meetings"));

                }

     /*           customer_id=jsonObject1.getInt("customer_id");
//                getCustomerName(customer_id);
                salesPersonId=jsonObject1.getInt("sales_person_id");
//                getSalesPersonName(salesPersonId);
                salesTeamId=jsonObject1.getInt("sales_team_id");*/
//                getSalesTeamName(salesTeamId);


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }

    private void getSalesPersonName(int salesPersonId) {
Log.i("salesper--", String.valueOf(salesPersonId));
            StringRequest stringRequest = new StringRequest(Request.Method.GET,Appconfig.STAFF_DETAILS_URL+Appconfig.TOKEN+"&staff_id="+salesPersonId,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.i("response--",response);
                                JSONObject jsonObject=new JSONObject(response);
                                JSONObject jsonObject1=jsonObject.getJSONObject("staff");

                                textViewSalesperson.setText(jsonObject1.getString("first_name")+" "+jsonObject1.getString("last_name"));


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    },new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.i("response--", String.valueOf(error));
                }
            }) ;
            MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);

    }

    private void getSalesTeamName(int salesTeamId) {
        Log.i("salesteam--", String.valueOf(salesTeamId));

        StringRequest stringRequest = new StringRequest(Request.Method.GET,Appconfig.SALESTEAM_DETAILS_URL+Appconfig.TOKEN+"&salesteam_id="+salesTeamId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("response--",response);
                            JSONObject jsonObject=new JSONObject(response);
                            JSONObject jsonObject1=jsonObject.getJSONObject("salesteam");

                            textViewSalesteam.setText(jsonObject1.getString("salesteam"));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("response--", String.valueOf(error));
            }
        }) ;
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);
    }

    private void getCustomerName(int customer_id) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET,Appconfig.CUSTOMER_DETAILS_URL+Appconfig.TOKEN+"&customer_id="+customer_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("response--",response);
                            JSONObject jsonObject=new JSONObject(response);
                            JSONObject jsonObject1=jsonObject.getJSONObject("");

                            textViewCustomer.setText(String.valueOf(jsonObject1.get(" ")));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("response--", String.valueOf(error));
            }
        }) ;
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);

    }
}
