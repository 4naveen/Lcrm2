package com.project.naveen.lcrm.detailsactivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joaquimley.faboptions.FabOptions;
import com.project.naveen.lcrm.AppSession;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.adapters.ContractAdapter;
import com.project.naveen.lcrm.contracts.DetailFragment;
import com.project.naveen.lcrm.contracts.EditFragment;
import com.project.naveen.lcrm.quotation.DetailQuotationFragment;

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

public class ContractDetailActivity extends AppCompatActivity {
    FabOptions fabOptions;
    int contract_id_position;
    String contract_id;
    ContractAdapter contractAdapter;
    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_detail);
        linearLayout= (LinearLayout) findViewById(R.id.layout);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Contract Details");
        }
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        contract_id=getIntent().getStringExtra("contract_id");
        contract_id_position=getIntent().getIntExtra("contract_id_position",0);
        Bundle bundle=new Bundle();
        bundle.putString("contractId",contract_id);
        Log.i("contractId",contract_id);
        Log.i("contractIdPosition", String.valueOf(contract_id_position));
        FragmentTransaction trans1=getSupportFragmentManager().beginTransaction();
        Fragment fragment1=new DetailFragment();
        fragment1.setArguments(bundle);
        trans1.replace(R.id.frame,fragment1);
        trans1.addToBackStack(null);
        trans1.commit();

        fabOptions = (FabOptions) findViewById(R.id.fab_options);
        fabOptions.setButtonsMenu(this, R.menu.quotation_menu);
        if (AppSession.contracts_write==0)
        {
            fabOptions.findViewById(R.id.edit).setVisibility(View.INVISIBLE);

        }
        if (AppSession.contracts_delete==0)
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
                        fragment=new EditFragment();
                        Bundle bundle=new Bundle();
                        bundle.putString("contractId",contract_id);
                        fragment.setArguments(bundle);
                        transaction.replace(R.id.frame,fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                    }

                    case R.id.delete:
                    {
                        new SweetAlertDialog(ContractDetailActivity.this, SweetAlertDialog.WARNING_TYPE)
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
                                   new DeleteContract().execute(Appconfig.TOKEN,contract_id);
                                       // Log.i("quo_id-pos", String.valueOf(quotation_id_position));

                                        sweetAlertDialog.cancel();
                                    }
                                })
                                .show();

                        break;
                    }
                    default:
                        // no-op
                }
            }
        });
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

    private class DeleteContract extends AsyncTask<String,Void,String>
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
            String contract_id=params[1];
            URL url;
            try {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("contract_id",contract_id);

                url = new URL(Appconfig.DELETE_CONTRACT_URL+tok);
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
                    AppSession.contractArrayList.remove(contract_id_position);
                    contractAdapter = new ContractAdapter(AppSession.contractArrayList, getApplicationContext());
                    contractAdapter.notifyItemRemoved(contract_id_position);
                    AppSession.contract_recyclerView.setAdapter(contractAdapter);
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
