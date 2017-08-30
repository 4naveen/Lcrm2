package com.project.lorvent.lcrm.fragments.admin.details;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.project.lorvent.lcrm.activities.LoginActivity;
import com.project.lorvent.lcrm.adapters.OpportunityAdapter;
import com.project.lorvent.lcrm.models.Opportunity;
import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.utils.Connection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class OpportunityDetailsFragment extends Fragment {
    String token,opportunity_id;
    TextView textViewOpp,textViewActionDate,textViewNextAction,textViewStage,textViewLead,textViewExpectedRevenue,textViewCustomer
            ,textViewEmail,textViewMobile,textViewSalesperson,textViewSalesteam,textViewPriority,textViewCall,textViewMeeting;
    CardView cardView;
     FrameLayout layout;
    View v;
    int customer_id,salesTeamId,salesPersonId;
    private boolean helpDisplayed = false;
    private static final String PREF_FIRSTLAUNCH_HELP = "helpDisplayed";
    public OpportunityDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_details, container, false);
        showHelpForFirstLaunch();
        setHasOptionsMenu(true);

        cardView=(CardView)v.findViewById(R.id.cv);
        opportunity_id=getArguments().getString("opp_id");
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
        token=Appconfig.TOKEN;
        new OpportunityDetailsTask().execute(token,opportunity_id);
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
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_convert,menu);
        menu.findItem(R.id.convert).setTitle("Convert to Quotation");
        menu.findItem(R.id.email).setVisible(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.convert)
        {
            final MaterialDialog dialog1 = new MaterialDialog.Builder(getActivity())
                    .title("Convert to Quotation")
                    .content("Are you sure to convert into Quotation?")
                    .positiveText("convert")
                    .autoDismiss(false)
                    .positiveColorRes(R.color.colorPrimary)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            new Convert_opportunity_to_quotation().execute(token,opportunity_id);
                            dialog.dismiss();
                        }
                    })
                    .show();

        }
        return super.onOptionsItemSelected(item);
    }

    private class Convert_opportunity_to_quotation extends AsyncTask<String,Void,String>
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
            String opportunity_id=params[1];
            URL url;
            try {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("opportunity_id",opportunity_id);
                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String convert_url;
                if (text_url != null) {
                    convert_url = text_url + "/user/convert_opportunity_to_quotation?token=";
                } else {
                    convert_url= Appconfig.OPPORTUNITY_TO_QUOTATION_URL;
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
                { new OpportunityTask().execute(token);
                    Connection.getDashboard(Appconfig.TOKEN,getActivity());
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
                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String detail_url;
                if (text_url != null) {
                    detail_url = text_url + "/user/opportunity?token=";
                } else {
                    detail_url= Appconfig.OPPORTUNITY_DETAILS_URL;
                }
                url = new URL(detail_url+tok+"&opportunity_id="+opportunity_id);
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
            if (dialog.isShowing()){
                dialog.dismiss();
            }

            JSONObject jsonObject;

            try {
                jsonObject = new JSONObject(result);
                JSONArray jsonArray=jsonObject.getJSONArray("opportunity");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObject1=jsonArray.getJSONObject(i);
                    if (jsonObject1.getInt("id")==Integer.parseInt(opportunity_id))
                    {
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
                    }

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
                e.printStackTrace();  Crashlytics.logException(e);
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
    public class OpportunityTask extends AsyncTask<String, Void, String> {
        String response;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            URL url;
            HttpURLConnection connection;
            String tok = params[0];
            try {
                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String get_url;
                if (text_url != null) {
                    get_url = text_url + "/user/opportunities?token=";
                } else {
                    get_url= Appconfig.OPPORTUNITY_URL;
                }

                url = new URL(get_url+ tok);
                connection = (HttpURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String temp;
                while ((temp = br.readLine()) != null) {
                    buffer.append(temp);
                }
                response = buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
                Crashlytics.logException(e);

            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("opportunities");
                AppSession.opportunityArrayList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    Opportunity opportunity = new Opportunity();
                    opportunity.setId(object.getInt("id"));
                    opportunity.setOpportunity(object.getString("opportunity"));
                    opportunity.setNext_action_date(object.getString("next_action"));
                    opportunity.setExpected_revenue(object.getString("expected_revenue"));
                    opportunity.setStages(object.getString("stages"));
                    opportunity.setProbability(object.getString("probability"));
                    opportunity.setSalesTeam(object.getString("salesteam"));
                    opportunity.setMeetings(object.getString("meetings"));
                    opportunity.setCompany(object.getString("company"));
                    opportunity.setCalls(object.getString("calls"));

                    AppSession.opportunityArrayList.add(opportunity);

                }

                OpportunityAdapter mAdapter = new OpportunityAdapter(getActivity(), AppSession.opportunityArrayList);
                AppSession.opportunity_recyclerView.setAdapter(mAdapter);

                AppSession.opportunity_recyclerView.setItemAnimator(new DefaultItemAnimator());
                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                RecyclerView.LayoutManager lmanager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

                AppSession.opportunity_recyclerView.setLayoutManager(lmanager);

            } catch (JSONException e) {
                e.printStackTrace();
                Crashlytics.logException(e);

            }


        }

    }

}
