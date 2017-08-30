package com.project.lorvent.lcrm.activities.add;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.crashlytics.android.Crashlytics;
import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.Connection;
import com.project.lorvent.lcrm.utils.ContactsCompletionView;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.adapters.SalesTeamAdapter;
import com.project.lorvent.lcrm.models.Person;
import com.project.lorvent.lcrm.models.SalesTeam;
import com.project.lorvent.lcrm.models.Staff;
import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;
import com.weiwangcn.betterspinner.library.BetterSpinner;

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
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class AddSalesTeamActivity extends AppCompatActivity implements TokenCompleteTextView.TokenListener<Person>,AdapterView.OnItemSelectedListener {
    ArrayList<Staff> staffArrayList;
    String token;
    EditText sales_team_name,invoice_target,invoice_forecast;
    TextView leader;
    Button submit;
    TextInputLayout input_team_leader,input_invoice_target,input_invoice_forecast,input_sales_team,input_team_member;
    //Spinner team_leader;
    BetterSpinner team_leader;
    ArrayList<String>spinnerArrayList,staffNameArrayList;
    ContactsCompletionView completionView;
    ArrayList<Person> persons;
    ArrayAdapter<Person> adapter;
    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sales_team);
        //team_leader=(Spinner)findViewById(R.id.team_leader);
        Connection.getStaffList(Appconfig.TOKEN,AddSalesTeamActivity.this);

        sales_team_name=(EditText)findViewById(R.id.salesteamName);
        invoice_target=(EditText)findViewById(R.id.invoice_target);
        invoice_forecast=(EditText)findViewById(R.id.invoice_forecast);

        input_invoice_target=(TextInputLayout)findViewById(R.id.input_layout_invoice_target);
        input_sales_team=(TextInputLayout)findViewById(R.id.input_layout_salesteam);
        input_invoice_forecast=(TextInputLayout)findViewById(R.id.input_layout_invoice_forecast);
        input_team_leader=(TextInputLayout)findViewById(R.id.input_layout_team_leader);
        input_team_member=(TextInputLayout)findViewById(R.id.input_layout_team_member);

        submit=(Button)findViewById(R.id.submit);
        team_leader=(BetterSpinner)findViewById(R.id.team_leader);

        spinnerArrayList=new ArrayList<>();
        persons=new ArrayList<>();
        token= Appconfig.TOKEN;
        linearLayout=(LinearLayout)findViewById(R.id.layout);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Add SalesTeam");
        }
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ArrayAdapter<String>staffArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,AppSession.teamLeaderNameList);
        staffArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        team_leader.setAdapter(staffArrayAdapter);

        for (int i = 0; i < AppSession.teamMemberList.size(); i++) {
            Person person = new Person(AppSession.teamMemberList.get(i).getName());
            persons.add(person);
        }

        String leader= String.valueOf(team_leader.getText());
        adapter = new FilteredArrayAdapter<Person>(this, R.layout.person_layout,persons) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                    convertView = l.inflate(R.layout.person_layout, parent, false);
                }

                Person p = getItem(position);
                ((TextView)convertView.findViewById(R.id.name)).setText(p.getName());
                return convertView;
            }

            @Override
            protected boolean keepObject(Person person, String mask) {
                mask = mask.toLowerCase();
                return person.getName().toLowerCase().startsWith(mask);
            }
        };
        completionView = (ContactsCompletionView)findViewById(R.id.searchView);
        completionView.setAdapter(adapter);
        completionView.setTokenListener(AddSalesTeamActivity.this);
        completionView.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Select);

        sales_team_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_sales_team.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        invoice_target.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_invoice_target.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        invoice_forecast.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_invoice_forecast.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        team_leader.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_team_leader.setError("");
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          if (sales_team_name.getText().toString().isEmpty())
                          {
                              input_sales_team.setError("Please enter SalesTeam name");
                              return;
                          }

                          else if (invoice_target.getText().toString().isEmpty()){
                              input_invoice_target.setError("Please enter invoice target");
                              return;
                          }

                          else if (invoice_forecast.getText().toString().isEmpty()){
                              input_invoice_forecast.setError("Please enter invoice forecast");
                              return;
                          }
                          else if (team_leader.getText().toString().isEmpty()){
                              input_team_leader.setError("Please select TeamLeader name");
                              return;
                          }
                          else if (completionView.getText().toString().isEmpty()){
                              input_team_member.setError("Please select Team member name");
                              return;
                          }

                          else {
                              //do nothing
                          }
                          ArrayList<Integer>member_id= new ArrayList<>();
                          StringBuffer memberId=new StringBuffer();
                          int leader_id=AppSession.teamLeaderList.get(AppSession.teamLeaderNameList.indexOf(team_leader.getText().toString())).getId();

                          for (Object o:completionView.getObjects())
                          {
                              member_id.add(AppSession.teamMemberList.get(AppSession.teamMemberNameList.indexOf(o.toString())).getId());
                          }

                          for (int i=1;i<=member_id.size();i++)
                          {
                           if (i<member_id.size())
                           {
                               memberId.append(member_id.get(i-1)+",");
                           }
                              if (i==member_id.size())
                              {
                                  memberId.append(member_id.get(i-1));
                              }
                          }

                          new AddSalesTeamTask().execute(token,sales_team_name.getText().toString(),invoice_target.getText().toString(),
                                  invoice_forecast.getText().toString(),String.valueOf(leader_id),String.valueOf(memberId));

                    /*      InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                          imm.hideSoftInputFromWindow(submit.getWindowToken(), 0);*/
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

    @Override
    public void onTokenAdded(Person token) {

    }

    @Override
    public void onTokenRemoved(Person token) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



    class AddSalesTeamTask extends AsyncTask<String,Void,String>
    {
        ProgressDialog dialog;
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(AddSalesTeamActivity.this);
            dialog.setMessage("Loading, please wait...");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String response="",jsonresponse="";
            JSONObject json;
            BufferedReader bufferedReader;
            String tok=params[0];
            String sales_team_name=params[1];
            String invoice_target=params[2];
            String invoice_forecast=params[3];
            String team_leader=params[4];
            String team_number=params[5];
            URL url;
            try {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("salesteam",sales_team_name);
                jsonObject.put("invoice_target",invoice_target);
                jsonObject.put("invoice_forecast" ,invoice_forecast);
                jsonObject.put("team_leader",team_leader);
                jsonObject.put("team_members",team_number);

                SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String post_salesteam_url;
                if (text_url != null) {
                    post_salesteam_url = text_url + "/user/post_salesteam?token=";
                } else {
                    post_salesteam_url = Appconfig.SALESTEAM_ADD_URL;
                }
                url = new URL(post_salesteam_url+tok);
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
                    }
                    json = new JSONObject(response);
                    jsonresponse=json.getString("error");
                    //System.out.println("error=" + json.get("error"));
                    //succes = json.getString("success");
                }

            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.logException(e);

            }
            return jsonresponse;
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            if (result.equals("success"))
            {
                new GetAllSalesTeamTask().execute(token);
            /*    sales_team_name.setText("");
                invoice_target.setText("");
                invoice_forecast.setText("");*/
                final Snackbar snackbar = Snackbar.make(linearLayout, "Added 1 item Succesfully!", Snackbar.LENGTH_LONG);
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
                final Snackbar snackbar = Snackbar.make(linearLayout, "Item not added! Try Again", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
            }
        }
    }
    class GetAllSalesTeamTask extends AsyncTask<String,Void,String>
    {   String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            URL url;
            HttpURLConnection connection;
            try {
                SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String get_url;
                if (text_url != null) {
                    get_url = text_url + "/user/salesteams?token=";
                } else {
                    get_url = Appconfig.SALESTEAM_URL;
                }
                url = new URL(get_url+params[0]);
                connection = (HttpURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String temp;
                while ((temp=br.readLine())!=null)
                {
                    buffer.append(temp);
                }
                response=buffer.toString();

            } catch (IOException e) {
                e.printStackTrace();
                Crashlytics.logException(e);

            }
            return response;
        }
        @Override
        protected void onPostExecute(String response) {
            try {
                JSONObject jsonObject=new JSONObject(response);
                JSONArray jsonArray=jsonObject.getJSONArray("salesteams");
                AppSession.salesTeamArrayList.clear();
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    SalesTeam salesTeam=new SalesTeam();
                    salesTeam.setId(object.getInt("id"));
                    salesTeam.setSalesteam(object.getString("salesteam"));
                    salesTeam.setTarget(object.getString("target"));
                    salesTeam.setInvoice_forecast(object.getString("invoice_forecast"));
                    salesTeam.setActual_invoice(object.getString("actual_invoice"));
                    AppSession.salesTeamArrayList.add(salesTeam);
                }
                SalesTeamAdapter mAdapter=new SalesTeamAdapter(AddSalesTeamActivity.this,AppSession.salesTeamArrayList);
                AppSession.salesTeam_recyclerView.setAdapter(mAdapter);
                AppSession.salesTeam_recyclerView.setItemAnimator(new DefaultItemAnimator());
                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(AddSalesTeamActivity.this, LinearLayoutManager.VERTICAL,false);
                //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);

                AppSession.salesTeam_recyclerView.setLayoutManager(lmanager);

            } catch (JSONException e) {
                e.printStackTrace();
                Crashlytics.logException(e);

            }
        }}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
