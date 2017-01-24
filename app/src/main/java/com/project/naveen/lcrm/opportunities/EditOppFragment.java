package com.project.naveen.lcrm.opportunities;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.MyVolleySingleton;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.addactivity.AddOppActivity;
import com.project.naveen.lcrm.models.Company;
import com.project.naveen.lcrm.models.Contacts;
import com.project.naveen.lcrm.models.SalesTeam;
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
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditOppFragment extends Fragment {
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
    FrameLayout frameLayout;
    public EditOppFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_edit_opp, container, false);
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        opportunity_id=getArguments().getString("opp_id");
        frameLayout=(FrameLayout)v.findViewById(R.id.layout);
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
        getSalesTeamList(token);
        getCustomerList(token);
        customer=(BetterSpinner)v.findViewById(R.id.company);
        sales_team=(BetterSpinner)v.findViewById(R.id.salesteam);
        stages=(BetterSpinner)v.findViewById(R.id.stages);

        ArrayAdapter<String> stagesArrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,stagesList);
        stagesArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        stages.setAdapter(stagesArrayAdapter);

        ArrayAdapter<String> customerArrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,companyNameList);
        customerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        customer.setAdapter(customerArrayAdapter);
        ArrayAdapter<String> salesteamArrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,sales_teamNameList);
        customerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sales_team.setAdapter(salesteamArrayAdapter);

        new OpportunityDetailsTask().execute(token,opportunity_id);

        getDateSettings(token);
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
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (opp.getText().toString().isEmpty())
                {
                    input_opp.setError("Please enter opportunity name");

                    return;
                }

                else if (email.getText().toString().isEmpty()){
                    input_email.setError("Please enter email");
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
                else if (customer.getText().toString().isEmpty()){
                    input_customer.setError("Please select customer name");
                    return;
                }
                else if (sales_team.getText().toString().isEmpty()){
                    input_sales_team.setError("Please select salesTeam name");
                    return;
                }
                else if (stages.getText().toString().isEmpty()){
                    input_satges.setError("Please select stages");
                    return;
                }
                else {
                    //do nothing
                }
                Log.i("nameid-", String.valueOf(sales_teamNameList.indexOf(sales_team.getText().toString())));
                int salesTeamId=sales_teamList.get(sales_teamNameList.indexOf(sales_team.getText().toString())).getId();
                Log.i("salesteamId-", String.valueOf(salesTeamId));

                new EditOppTask().execute(token,opp.getText().toString(),email.getText().toString(),customer.getText().toString(),
                        String.valueOf(salesTeamId),nextAction.getText().toString(),expectedClosing.getText().toString(),opportunity_id,stages.getText().toString());
//                 Toast.makeText(getApplicationContext(),"this is dummy Gui test",Toast.LENGTH_LONG).show();
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
           /* next_month_text=new DateFormatSymbols().getMonths()[next_month];
            nextAction.setText(next_month_text+next_day+","+next_year);
            nextAction.setSelection(nextAction.getText().length());*/

            SimpleDateFormat simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'), Locale.ENGLISH);
            Date date=new Date(year-1900,monthOfYear,dayOfMonth);
            String text_date=simpleDateFormat.format(date);
            Log.i("text_date",text_date);

            nextAction.setText(text_date);
            nextAction.clearFocus();
        }
    };
    DatePickerDialog.OnDateSetListener expectedClosingListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            expected_year= year;
            expected_month = monthOfYear;
            expected_day= dayOfMonth;
         /*   expected_month_text=new DateFormatSymbols().getMonths()[expected_month];
            expectedClosing.setText(expected_month_text+expected_day+","+expected_year);
            expectedClosing.setSelection(expectedClosing.getText().length());*/
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'), Locale.ENGLISH);
            Date date=new Date(year-1900,monthOfYear,dayOfMonth);
            String text_date=simpleDateFormat.format(date);
            Log.i("text_date",text_date);

            expectedClosing.setText(text_date);
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
                jsonObject.put("customer_id", "3");
                jsonObject.put("sales_team_id", salesTeamId);
                jsonObject.put("next_action", nextAction);
                jsonObject.put("expected_closing", expectedACtion);
                jsonObject.put("stages", stages);

                url = new URL(Appconfig.EDIT_OPPPORTUNITY_URL+tok);
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
                    //Log.d("Output",br.toString());
                    while ((line = br.readLine()) != null) {
                        response += line;
                        Log.d("output lines", line);
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
                        Log.d("output lines", line);
                    }
                    Log.i("response", response);
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
    private void getDateSettings(String token) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Appconfig.SETTINGS_URL+token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            // Log.i("response--", String.valueOf(response));

                            JSONObject jsonObject=new JSONObject(response);
                            JSONObject pre_settings=jsonObject.getJSONObject("settings");
                            date_format=(pre_settings.getString("date_format"));

                            Log.i("date--",date_format);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("response--", String.valueOf(error));
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }

        } ;
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);


    }

    public void getCustomerList(String token)
    { StringRequest stringRequest = new StringRequest(Request.Method.GET, Appconfig.COMPANY_URL+token,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        Log.i("response--", String.valueOf(response));

                        JSONObject jsonObject=new JSONObject(response);
                        JSONArray jsonArray=jsonObject.getJSONArray("companies");
                        for (int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject object=jsonArray.getJSONObject(i);
                            Company company=new Company();
                            company.setId(object.getInt("id"));
                            company.setName(object.getString("name"));
                            companyNameList.add(object.getString("name"));
                            companyList.add(company);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            },new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

            Log.i("response--", String.valueOf(error));
        }
    }){
        @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();

            return params;
        }

    } ;
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);

    }

    public  void  getSalesTeamList(String token)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Appconfig.SALESTEAM_URL+token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            Log.i("response--", String.valueOf(response));

                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("salesteams");
                            for (int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject object=jsonArray.getJSONObject(i);
                                SalesTeam salesTeam=new SalesTeam();
                                salesTeam.setId(object.getInt("id"));
                                salesTeam.setSalesteam(object.getString("salesteam"));
                                sales_teamNameList.add(object.getString("salesteam"));
                                sales_teamList.add(salesTeam);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("response--", String.valueOf(error));
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }

        } ;
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);

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
                JSONArray jsonArray=jsonObject.getJSONArray("opportunity");
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    opp.setText(jsonObject1.getString("opportunity"));
                    email.setText(jsonObject1.getString("email"));
                    customer.setText(jsonObject1.getString("company"));
                    sales_team.setText(jsonObject1.getString("salesteam"));
                    nextAction.setText(jsonObject1.getString("next_action"));
                   // expectedClosing.setText(jsonObject1.getString("expected_closing"));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }


}
