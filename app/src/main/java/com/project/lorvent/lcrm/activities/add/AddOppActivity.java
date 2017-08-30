package com.project.lorvent.lcrm.activities.add;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.adapters.OpportunityAdapter;
import com.project.lorvent.lcrm.models.Company;
import com.project.lorvent.lcrm.models.Opportunity;
import com.project.lorvent.lcrm.models.SalesTeam;
import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.Connection;
import com.project.lorvent.lcrm.utils.Util;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class AddOppActivity extends AppCompatActivity {
    EditText opp, email, nextAction, expectedClosing;
    TextInputLayout input_opp, input_email, input_next_action, input_expected_closing, input_customer, input_sales_team, input_satges;
    int next_day, next_month, next_year, expected_day, expected_month, expected_year;
    static final int NEXT_DATE_PICKER_ID = 111;
    static final int ENCLOSE_DATE_PICKER_ID = 112;
    String token, date_format;
    Button submit;
    String next_month_text, expected_month_text;
    ArrayList<SalesTeam> sales_teamList;
    ArrayList<Company> companyList;
    ArrayList<String> sales_teamNameList, companyNameList, stagesList;
    BetterSpinner customer, sales_team, stages;
    SimpleDateFormat simpleDateFormat;
    CoordinatorLayout coordinator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_opp);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Add opportunity");
        }
        coordinator = (CoordinatorLayout) findViewById(R.id.coordinator);
        token = Appconfig.TOKEN;

        opp = (EditText) findViewById(R.id.opp);
        email = (EditText) findViewById(R.id.email);
        input_opp = (TextInputLayout) findViewById(R.id.input_layout_opp);
        input_email = (TextInputLayout) findViewById(R.id.input_layout_email);
        input_next_action = (TextInputLayout) findViewById(R.id.input_layout_next_action);
        input_expected_closing = (TextInputLayout) findViewById(R.id.input_layout_expected_closing);
        input_customer = (TextInputLayout) findViewById(R.id.input_layout_customer);
        input_sales_team = (TextInputLayout) findViewById(R.id.input_layout_salesteam);
        input_satges = (TextInputLayout) findViewById(R.id.input_layout_stages);
        stagesList = new ArrayList<>();

        Connection.getDateSettings(Appconfig.TOKEN, AddOppActivity.this);
        Connection.getContactList(Appconfig.TOKEN, AddOppActivity.this);
        Connection.getSalesTeamList(Appconfig.TOKEN, AddOppActivity.this);

        stagesList.add("New");
        stagesList.add("Qualification");
        stagesList.add("Proposition");
        stagesList.add("Negotiation");
        stagesList.add("Won");
        stagesList.add("Lost");
        stagesList.add("Expired");

        customer = (BetterSpinner) findViewById(R.id.company);
        sales_team = (BetterSpinner) findViewById(R.id.salesteam);
        stages = (BetterSpinner) findViewById(R.id.stages);

        ArrayAdapter<String> stagesArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, stagesList);
        stagesArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        stages.setAdapter(stagesArrayAdapter);
        ArrayAdapter<String> customerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, AppSession.customerNameList);
        customerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        customer.setAdapter(customerArrayAdapter);
        ArrayAdapter<String> salesteamArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, AppSession.sales_teamNameList);
        customerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sales_team.setAdapter(salesteamArrayAdapter);
        nextAction = (EditText) findViewById(R.id.next_action);
        expectedClosing = (EditText) findViewById(R.id.expected_closing);
        submit = (Button) findViewById(R.id.submit);

        next_year = Calendar.getInstance().get(Calendar.YEAR);
        expected_year = Calendar.getInstance().get(Calendar.YEAR);
        next_month = Calendar.getInstance().get(Calendar.MONTH);
        expected_month = Calendar.getInstance().get(Calendar.MONTH);
        next_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        expected_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            nextAction.setShowSoftInputOnFocus(false);
            expectedClosing.setShowSoftInputOnFocus(false);

        }

        nextAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(nextAction.getWindowToken(), 0);
                showDialog(NEXT_DATE_PICKER_ID);

            }
        });
        nextAction.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(nextAction.getWindowToken(), 0);
                    showDialog(NEXT_DATE_PICKER_ID);
                }
            }
        });
        expectedClosing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(nextAction.getWindowToken(), 0);
                showDialog(ENCLOSE_DATE_PICKER_ID);

            }
        });
        expectedClosing.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(nextAction.getWindowToken(), 0);
                    showDialog(ENCLOSE_DATE_PICKER_ID);
                }
            }
        });
        opp.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_opp.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_email.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        nextAction.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_next_action.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        expectedClosing.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_expected_closing.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        stages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_satges.setError("");
            }
        });
        customer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_customer.setError("");
            }
        });
        sales_team.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_sales_team.setError("");
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (opp.getText().toString().isEmpty()) {
                    input_opp.setError("Please enter opportunity name");
                    return;
                }
                else if (stages.getText().toString().isEmpty()) {
                    input_satges.setError("Please select stages");
                    return;
                }
                else if (email.getText().toString().isEmpty()||!Util.isValidEmail(email.getText().toString())) {
                    if (email.getText().toString().isEmpty()) {
                        input_email.setError("Please enter email");
                        return;
                    }
                    if (!Util.isValidEmail(email.getText().toString()))
                    {
                        input_email.setError("Please enter valid  email");
                        return;
                    }
                }  else if (customer.getText().toString().isEmpty()) {
                    input_customer.setError("Please select customer name");
                    return;
                } else if (sales_team.getText().toString().isEmpty()) {
                    input_sales_team.setError("Please select salesTeam name");
                    return;
                }
                else if (nextAction.getText().toString().isEmpty()) {
                    input_next_action.setError("Please enter nextAction date");
                    return;
                } else if (expectedClosing.getText().toString().isEmpty()) {
                    input_expected_closing.setError("Please enter expectedClosing date");
                    return;
                }
                 else {
                    //do nothing
                }
                int salesTeamId = AppSession.salesTeamList.get(AppSession.sales_teamNameList.indexOf(sales_team.getText().toString())).getId();
                int companyId = AppSession.customerList.get(AppSession.customerNameList.indexOf(customer.getText().toString())).getId();

                new AddOppTask().execute(token, opp.getText().toString(), email.getText().toString(), String.valueOf(companyId),
                        String.valueOf(salesTeamId), nextAction.getText().toString(), expectedClosing.getText().toString(), stages.getText().toString());

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(submit.getWindowToken(), 0);
            }
        });

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == NEXT_DATE_PICKER_ID) {
            return new DatePickerDialog(AddOppActivity.this, next_listener, next_year, next_month, next_day);
        }
        if (id == ENCLOSE_DATE_PICKER_ID) {
            return new DatePickerDialog(AddOppActivity.this, expected_listener, expected_year, expected_month, expected_day);
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    final DatePickerDialog.OnDateSetListener next_listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            next_year = year;
            next_month = month;
            next_day = dayOfMonth;
            Date date = new Date(year - 1900, month, dayOfMonth);
            nextAction.setText(Connection.getFormatedDate(date));
            nextAction.setSelection(nextAction.getText().length());

            nextAction.clearFocus();
        }
    };
    final DatePickerDialog.OnDateSetListener expected_listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            expected_year = year;
            expected_month = month;
            expected_day = dayOfMonth;
            Date date = new Date(year - 1900, month, dayOfMonth);
            expectedClosing.setText(Connection.getFormatedDate(date));
            expectedClosing.setSelection(expectedClosing.getText().length());

            expectedClosing.clearFocus();
        }
    };

    class AddOppTask extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
        HttpURLConnection conn;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(AddOppActivity.this);
            dialog.setMessage("Loading, please wait...");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "", jsonresponse = "";
            BufferedReader bufferedReader = null;
            JSONObject json = null;
            JSONObject jsonObject = null;
            String tok = params[0];
            String opp = params[1];
            String email = params[2];
            String companyId = params[3];
            String salesTeamId = params[4];
            String nextAction = params[5];
            String expectedACtion = params[6];
            String stages = params[7];
            URL url = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("opportunity", opp);
                jsonObject.put("email", email);
                jsonObject.put("customer_id", companyId);
                jsonObject.put("sales_team_id", salesTeamId);
                jsonObject.put("next_action", nextAction);
                jsonObject.put("expected_closing", expectedACtion);
                jsonObject.put("stages", stages);
                SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String post_opp_url;
                if (text_url != null) {
                    post_opp_url = text_url + "/user/post_opportunity?token=";
                } else {
                    post_opp_url = Appconfig.POST_OPPPORTUNITY_URL;
                }

                url = new URL(post_opp_url + tok);
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
                int responseCode = conn.getResponseCode();
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

                } else {
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        response += line;
                    }
                    json = new JSONObject(response);
                    jsonresponse = json.getString("error");
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
            if (result.equals("success")) {
           /*     opp.setText("");
                email.setText("");
                nextAction.setText("");
                expectedClosing.setText("");*/
                new OpportunityTask().execute(token);
                Connection.getDashboard(Appconfig.TOKEN,AddOppActivity.this);
                final Snackbar snackbar = Snackbar.make(coordinator, "Added 1 item Succesfully!", Snackbar.LENGTH_LONG);
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
            } else {
                final Snackbar snackbar = Snackbar.make(coordinator, "Item not added! Try Again", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
            }

        }
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
                SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String get_url;
                if (text_url != null) {
                    get_url = text_url + "/user/opportunities?token=";
                } else {
                    get_url = Appconfig.OPPORTUNITY_URL;
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

                OpportunityAdapter mAdapter = new OpportunityAdapter(getApplicationContext(), AppSession.opportunityArrayList);
                AppSession.opportunity_recyclerView.setAdapter(mAdapter);

                AppSession.opportunity_recyclerView.setItemAnimator(new DefaultItemAnimator());
                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                RecyclerView.LayoutManager lmanager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);

                AppSession.opportunity_recyclerView.setLayoutManager(lmanager);

            } catch (JSONException e) {
                e.printStackTrace();
                Crashlytics.logException(e);

            }


        }

    }
}
