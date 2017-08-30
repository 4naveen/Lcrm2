package com.project.lorvent.lcrm.activities.details.admin;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.crashlytics.android.Crashlytics;
import com.joaquimley.faboptions.FabOptions;
import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.adapters.QtemplateAdapter;
import com.project.lorvent.lcrm.fragments.admin.details.QtemplateDetailFragment;
import com.project.lorvent.lcrm.fragments.admin.edit.QtemplateEditFragment;

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

public class QtemplateDetailsActivity extends AppCompatActivity {

    FrameLayout layout;
    FabOptions fabOptions;
    int qtemplate_id_position;
    String qtemplate_id;
    QtemplateAdapter qtemplateAdapter;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration config = getResources().getConfiguration();
        setContentView(R.layout.activity_quotation_details);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Quotation template Details");
        }
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        linearLayout= (LinearLayout) findViewById(R.id.layout);

        qtemplate_id=getIntent().getStringExtra("qtemplateId");
        qtemplate_id_position=getIntent().getIntExtra("qtemplateId_position",0);
        Bundle bundle=new Bundle();
        bundle.putString("qtemplateId",qtemplate_id);
        FragmentTransaction trans1=getSupportFragmentManager().beginTransaction();
        Fragment fragment1=new QtemplateDetailFragment();
        fragment1.setArguments(bundle);
        trans1.replace(R.id.frame,fragment1);
        trans1.addToBackStack(null);
        trans1.commit();

        fabOptions = (FabOptions) findViewById(R.id.fab_options);
        fabOptions.setButtonsMenu(this, R.menu.quotation_menu);

        fabOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                Fragment fragment=null;
                switch (v.getId()) {
                    case R.id.edit:
                    {
                        fragment=new QtemplateEditFragment();
                        Bundle bundle=new Bundle();
                        bundle.putString("qtemplateId", String.valueOf(qtemplate_id));
                        fragment.setArguments(bundle);
                        transaction.replace(R.id.frame,fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                    }


                    case R.id.delete:
                    {
                        new SweetAlertDialog(QtemplateDetailsActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Are you sure?")
                                .setContentText("Won't be able to recover this file!")
                                .setCancelText("cancel")
                                .setConfirmText("Yes")
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
                                        new DeleteQtemplateTask().execute(Appconfig.TOKEN,qtemplate_id);

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
    private class DeleteQtemplateTask extends AsyncTask<String,Void,String>
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
            String qtemplateId=params[1];
            URL url;
            try {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("qtemplate_id",qtemplateId);
                SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String delete_url;
                if (text_url != null) {
                    delete_url = text_url + "/user/delete_qtemplate?token=";
                } else {
                    delete_url = Appconfig.QTEMPLATE_DELETE_URL;
                }
                url = new URL(delete_url+tok);
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
                int responseCode=conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                    json = new JSONObject(response);


                    jsonresponse = json.getString("success");

                }
                else {
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        response += line;
                    }
                    json = new JSONObject(response);
                    jsonresponse=json.getString("error");

                }

            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.logException(e);

            }
            return jsonresponse;
        }
        @Override
        protected void onPostExecute(String result) {
            //  dialog.dismiss();
            if (result!=null)
            {
                if (result.equals("success"))
                {
                    AppSession.qtemplateArrayList.remove(qtemplate_id_position);
                    qtemplateAdapter = new QtemplateAdapter(getApplicationContext(), AppSession.qtemplateArrayList);
                    qtemplateAdapter.notifyItemRemoved(qtemplate_id_position);
                    AppSession.qtemplate_recyclerView.setAdapter(qtemplateAdapter);
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
