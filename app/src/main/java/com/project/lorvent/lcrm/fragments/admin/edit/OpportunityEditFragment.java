package com.project.lorvent.lcrm.fragments.admin.edit;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.project.lorvent.lcrm.fragments.admin.details.OpportunityDetailsFragment;
import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.Connection;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.models.Company;
import com.project.lorvent.lcrm.models.SalesTeam;
import com.project.lorvent.lcrm.utils.Util;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class OpportunityEditFragment extends Fragment {
    TextInputLayout input_opp,input_email,input_next_action,input_expected_closing,input_customer,input_sales_team,input_satges;
    EditText opp,email,nextAction,expectedClosing;
    int next_day,next_month,next_year,expected_day,expected_month,expected_year;
    FragmentTransaction transaction;
    Fragment fragment = null;
    static ArrayList<String> companyNameList,sales_teamNameList,stagesList;
    ArrayList<SalesTeam>sales_teamList;
    ArrayList<Company> companyList;
    BetterSpinner customer,sales_team,stages;
    String token,opportunity_id,date_format;
    Button submit;
    String next_month_text,expected_month_text;
    LinearLayout frameLayout;
    public OpportunityEditFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_edit_opp, container, false);
        Connection.getDateSettings(Appconfig.TOKEN,getActivity());
        Connection.getContactList(Appconfig.TOKEN,getActivity());
        Connection.getSalesTeamList(Appconfig.TOKEN,getActivity());
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        opportunity_id=getArguments().getString("opp_id");
        frameLayout=(LinearLayout) v.findViewById(R.id.layout);
        setHasOptionsMenu(true);
        if (actionBar != null) {
            actionBar.setTitle("Edit Opportunity");
            actionBar.setHomeButtonEnabled(false);
        }
        token= Appconfig.TOKEN;
        opp=(EditText)v.findViewById(R.id.opp);
        email=(EditText)v.findViewById(R.id.email);
        nextAction=(EditText)v.findViewById(R.id.next_action);
        expectedClosing=(EditText)v.findViewById(R.id.expected_closing);
        input_opp=(TextInputLayout)v.findViewById(R.id.input_layout_opp);
        input_email=(TextInputLayout)v.findViewById(R.id.input_layout_email);
        input_next_action=(TextInputLayout)v.findViewById(R.id.input_layout_next_action);
        input_expected_closing=(TextInputLayout)v.findViewById(R.id.input_layout_expected_closing);
        input_customer=(TextInputLayout)v.findViewById(R.id.input_layout_customer);
        input_sales_team=(TextInputLayout)v.findViewById(R.id.input_layout_salesteam);
        input_satges=(TextInputLayout)v.findViewById(R.id.input_layout_stages);
        companyList=new ArrayList<>();
        sales_teamList=new ArrayList<>();
        companyNameList=new ArrayList<>();
        sales_teamNameList=new ArrayList<>();
        stagesList=new ArrayList<>();

        stagesList.add("New");
        stagesList.add("Qualification");
        stagesList.add("Proposition");
        stagesList.add("Negotiation");
        stagesList.add("Won");
        stagesList.add("Lost");
        stagesList.add("Expired");

        customer=(BetterSpinner)v.findViewById(R.id.company);
        sales_team=(BetterSpinner)v.findViewById(R.id.salesteam);
        stages=(BetterSpinner)v.findViewById(R.id.stages);

        ArrayAdapter<String> stagesArrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,stagesList);
        stagesArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        stages.setAdapter(stagesArrayAdapter);

        ArrayAdapter<String> customerArrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item, AppSession.customerNameList);
        customerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        customer.setAdapter(customerArrayAdapter);
        ArrayAdapter<String> salesteamArrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,AppSession.sales_teamNameList);
        customerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sales_team.setAdapter(salesteamArrayAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            nextAction.setShowSoftInputOnFocus(false);
            expectedClosing.setShowSoftInputOnFocus(false);

        }
        new OpportunityDetailsTask().execute(token,opportunity_id);
        next_year= Calendar.getInstance().get(Calendar.YEAR);
        expected_year= Calendar.getInstance().get(Calendar.YEAR);
        next_month=Calendar.getInstance().get(Calendar.MONTH);
        expected_month=Calendar.getInstance().get(Calendar.MONTH);
        next_day=Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        expected_day=Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        submit=(Button)v.findViewById(R.id.submit);
        nextAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(nextAction.getWindowToken(), 0);
                DatePickerDialog d = new DatePickerDialog(getActivity(), nextActionListener,next_year,next_month,next_day);
                d.show();
            }
        });
        nextAction.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(nextAction.getWindowToken(), 0);
                    DatePickerDialog d = new DatePickerDialog(getActivity(), nextActionListener,next_year,next_month,next_day);
                    d.show();
                }
            }
        });
        expectedClosing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(nextAction.getWindowToken(), 0);
                DatePickerDialog d = new DatePickerDialog(getActivity(), expectedClosingListener, expected_year, expected_month, expected_day);
                d.show();

            }
        });
        expectedClosing.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(nextAction.getWindowToken(), 0);
                    DatePickerDialog d = new DatePickerDialog(getActivity(), expectedClosingListener, expected_year, expected_month, expected_day);
                    d.show();
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
                if (opp.getText().toString().isEmpty())
                {
                    input_opp.setError("Please enter opportunity name");
                    return;
                }
                else if (stages.getText().toString().isEmpty()){
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
                }
                else if (customer.getText().toString().isEmpty()){
                    input_customer.setError("Please select customer name");
                    return;
                }
                else if (sales_team.getText().toString().isEmpty()){
                    input_sales_team.setError("Please select salesTeam name");
                    return;
                }
                else if (nextAction.getText().toString().isEmpty()){
                    input_next_action.setError("Please enter nextAction date");
                    return;
                }
                else if (expectedClosing.getText().toString().isEmpty()){
                    input_expected_closing.setError("Please enter expectedClosing date");
                    return;
                }

                else {
                    //do nothing
                }
                int salesTeamId=AppSession.salesTeamList.get(AppSession.sales_teamNameList.indexOf(sales_team.getText().toString())).getId();
                int companyId=AppSession.customerList.get(AppSession.customerNameList.indexOf(customer.getText().toString())).getId();

                new EditOppTask().execute(token,opp.getText().toString(),email.getText().toString(),String.valueOf(companyId),
                        String.valueOf(salesTeamId),nextAction.getText().toString(),expectedClosing.getText().toString(),opportunity_id,stages.getText().toString());
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(submit.getWindowToken(), 0);
            }
        });
        return v;
    }
    DatePickerDialog.OnDateSetListener nextActionListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            next_year = year;
            next_month= monthOfYear;
            next_day = dayOfMonth;
            Date date = new Date(year - 1900, monthOfYear, dayOfMonth);
            nextAction.setText(Connection.getFormatedDate(date));
            nextAction.clearFocus();
        }
    };
    DatePickerDialog.OnDateSetListener expectedClosingListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            expected_year= year;
            expected_month = monthOfYear;
            expected_day= dayOfMonth;
            Date date = new Date(year - 1900, monthOfYear, dayOfMonth);
            expectedClosing.setText(Connection.getFormatedDate(date));
            expectedClosing.clearFocus();
        }
    };

   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_back,menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
if (item.getItemId()==R.id.back)
{
FragmentTransaction transaction=getFragmentManager().beginTransaction();

}
        return super.onOptionsItemSelected(item);
    }*/


    class EditOppTask extends AsyncTask<String,Void,String>
    {
        ProgressDialog dialog;
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
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
            String email =params[2] ;
            String customerId = params[3];
            String salesTeamId = params[4];
            String nextAction = params[5];
            String expectedACtion=params[6];
            String opportunity_id=params[7];
            String stages=params[8];
            URL url = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("opportunity_id", opportunity_id);
                jsonObject.put("opportunity", opp);
                jsonObject.put("email", email);
                jsonObject.put("customer_id", customerId);
                jsonObject.put("sales_team_id", salesTeamId);
                jsonObject.put("next_action", nextAction);
                jsonObject.put("expected_closing", expectedACtion);
                jsonObject.put("stages", stages);


                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String edit_url;
                if (text_url != null) {
                    edit_url= text_url + "/user/edit_opportunity?token=";
                } else {
                    edit_url= Appconfig.EDIT_OPPPORTUNITY_URL;
                }

                url = new URL(edit_url+tok);
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
                    String line ="";
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
            }
            return jsonresponse;
        }
        @Override
        protected void onPostExecute(String result) {

            dialog.dismiss();
            if (result.equals("success"))
            {
                final Snackbar snackbar = Snackbar.make(frameLayout, "Updated Succesfully!", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
                Bundle bundle=new Bundle();
                bundle.putString("opp_id", String.valueOf(opportunity_id));
                Fragment fragment1 = new OpportunityDetailsFragment();
                FragmentTransaction trans1 = getFragmentManager().beginTransaction();
                fragment1.setArguments(bundle);
                trans1.replace(R.id.frame, fragment1);
                trans1.addToBackStack(null);
                trans1.commit();
               /* new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                                       }
                },3000);
*/
            }
            else {
                final Snackbar snackbar = Snackbar.make(frameLayout, "Item not updated! Try Again", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
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
                    detail_url= text_url + "/user/opportunity?token=";
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
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    if (jsonObject1.getInt("id")==Integer.parseInt(opportunity_id)) {
                        opp.setText(jsonObject1.getString("opportunity"));
                        email.setText(jsonObject1.getString("email"));
                        customer.setText(jsonObject1.getString("company"));
                        sales_team.setText(jsonObject1.getString("salesteam"));
                        nextAction.setText(jsonObject1.getString("next_action"));
                        stages.setText(jsonObject1.getString("stages"));
                        expectedClosing.setText(jsonObject1.getString("expected_closing"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }


}
