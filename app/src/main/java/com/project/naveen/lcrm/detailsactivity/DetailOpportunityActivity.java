package com.project.naveen.lcrm.detailsactivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joaquimley.faboptions.FabOptions;
import com.project.naveen.lcrm.AppSession;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.adapters.OpportunityAdapter;
import com.project.naveen.lcrm.adapters.SalesTeamAdapter;
import com.project.naveen.lcrm.opportunities.CallFragment;
import com.project.naveen.lcrm.opportunities.DetailsFragment;
import com.project.naveen.lcrm.opportunities.EditOppFragment;
import com.project.naveen.lcrm.opportunities.MeetingFragment;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DetailOpportunityActivity extends AppCompatActivity {

    FrameLayout layout;
    FabOptions fabOptions;
    int opp_id,opp_id_position;
    String token;
    OpportunityAdapter opportunityAdapter;
    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_opportunity);
        opp_id=getIntent().getIntExtra("opp_id",0);
        opp_id_position=getIntent().getIntExtra("opp_position",0);
        Log.i("oppidpos--", String.valueOf(opp_id_position));
        token= Appconfig.TOKEN;
        linearLayout= (LinearLayout) findViewById(R.id.layout);
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (actionBar != null) {
            actionBar.setTitle("Opportunity Information");
        }

        FragmentTransaction trans1=getSupportFragmentManager().beginTransaction();
        Fragment fragment1=new DetailsFragment();

        Bundle bundle=new Bundle();
        bundle.putString("opp_id", String.valueOf(opp_id));
        fragment1.setArguments(bundle);
        trans1.replace(R.id.frame,fragment1);
        trans1.addToBackStack(null);
        trans1.commit();

         fabOptions = (FabOptions) findViewById(R.id.fab_options);
        fabOptions.setButtonsMenu(this, R.menu.opportunity_menu);
        if (AppSession.opp_write==0)
        {
            fabOptions.findViewById(R.id.edit).setVisibility(View.INVISIBLE);
            fabOptions.findViewById(R.id.call).setVisibility(View.INVISIBLE);
            fabOptions.findViewById(R.id.meeting).setVisibility(View.INVISIBLE);

        }
        if (AppSession.opp_delete==0)
        {
            fabOptions.findViewById(R.id.delete).setVisibility(View.INVISIBLE);

        }
        fabOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                Fragment fragment=null;
                switch (v.getId()) {
                    case R.id.edit:
                    {
                        fragment=new EditOppFragment();
                        Bundle bundle=new Bundle();
                        bundle.putString("opp_id", String.valueOf(opp_id));
                        fragment.setArguments(bundle);
                        transaction.replace(R.id.frame,fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                    }


                    case R.id.delete:
                    {
                        new SweetAlertDialog(DetailOpportunityActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Are you sure?")
                                .setContentText("Won't be able to recover this file!")
                                .setCancelText("No,cancel plx!")
                                .setConfirmText("Yes,delete it!")
                                .showCancelButton(true)
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.cancel();
                                    }
                                })
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                       // new DeleteSalesTeamTask().execute(token, String.valueOf(sales_team_id));
                                        Log.i("opp_id-pos", String.valueOf(opp_id_position));
                                        new DeleteOpportunity().execute(token,String.valueOf(opp_id));

                                        sweetAlertDialog.cancel();
                                    }
                                })
                                .show();

                        break;
                    }
                    case R.id.call:
                    {
                        fragment=new CallFragment();
                        Bundle bundle=new Bundle();
                        bundle.putString("opp_id", String.valueOf(opp_id));
                        fragment.setArguments(bundle);
                        transaction.replace(R.id.frame,fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();

                        break;
                    }
                    case R.id.meeting:
                    {
                        fragment=new MeetingFragment();
                        Bundle bundle=new Bundle();
                        bundle.putString("opp_id", String.valueOf(opp_id));
                        fragment.setArguments(bundle);
                        transaction.replace(R.id.frame,fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();

                        break;
                    }
                    /*case R.id.details:
                    {
                        fragment=new DetailsFragment();
                        transaction.replace(R.id.frame,fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();

                        break;
                    }*/
                    default:
                        // no-op


                }
            }
        });

       /* cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView.setCardElevation(20);
            }
        });
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView.setCardElevation(0);
            }
        });*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                finish();
            }

        }
        return super.onOptionsItemSelected(item);
    }


    private class DeleteOpportunity extends AsyncTask<String,Void,String>
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

                url = new URL(Appconfig.DELETE_OPPORTUNITY_URL+tok);
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
            //  dialog.dismiss();
            if (result!=null)
            {
                if (result.equals("success")) {
                    AppSession.opportunityArrayList.remove(opp_id_position);
                    opportunityAdapter = new OpportunityAdapter(getApplicationContext(), AppSession.opportunityArrayList);
                    opportunityAdapter.notifyItemRemoved(opp_id_position);
                    AppSession.opportunity_recyclerView.setAdapter(opportunityAdapter);
                    final Snackbar snackbar = Snackbar.make(linearLayout, "Deleted 1 item Succesfully!", Snackbar.LENGTH_LONG);
                    View v = snackbar.getView();
                    v.setMinimumWidth(1000);
                    TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.YELLOW);
                    snackbar.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    },3000);

                    //Log.i("Res--",result);
                }

            else {
                    final Snackbar snackbar = Snackbar.make(linearLayout, "Item not deleted! Try Again", Snackbar.LENGTH_LONG);
                    View v = snackbar.getView();
                    v.setMinimumWidth(1000);
                    TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.YELLOW);
                    snackbar.show();
                }

            }

        }

    }
}
