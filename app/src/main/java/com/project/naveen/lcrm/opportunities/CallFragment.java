package com.project.naveen.lcrm.opportunities;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.naveen.lcrm.AppSession;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.MyVolleySingleton;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.adapters.subadapter.OpportunityCallAdapter;
import com.project.naveen.lcrm.addactivity.AddOppActivity;
import com.project.naveen.lcrm.models.Company;
import com.project.naveen.lcrm.models.Contacts;
import com.project.naveen.lcrm.models.Staff;
import com.project.naveen.lcrm.models.submodels.OppCall;
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
public class CallFragment extends Fragment {
    ArrayList<OppCall> oppCallArrayList;
    RecyclerView rv;
    String token,opportunity_id,date_format;
    OpportunityCallAdapter mAdapter;
    public static int current_year, current_month, current_day;
    public static String current_month_text;
    EditText editTextDate;
    BetterSpinner company, responsible;
    ArrayList<String> companyNameList,responsibleList;
    ArrayList<Company>companyArrayList;
    ArrayList<Staff>responsibleStaffList;
    public static Dialog mdialog;

    public CallFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_call, container, false);
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Opportunity Call");
        }
        opportunity_id=getArguments().getString("opp_id");
        oppCallArrayList = new ArrayList<>();
        companyNameList = new ArrayList<>();
        responsibleList = new ArrayList<>();
        companyArrayList=new ArrayList<>();
        responsibleStaffList=new ArrayList<>();
        getOpportunityCall(opportunity_id);
        current_year= Calendar.getInstance().get(Calendar.YEAR);
        current_month=Calendar.getInstance().get(Calendar.MONTH);
        current_day=Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        token = Appconfig.TOKEN;
        getDateSettings(token);
        getCompanyList(token);
        getResponsibleStaffList(token);
        rv = (RecyclerView) v.findViewById(R.id.rv);

        v.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.opp_call_add_dialog);

        editTextDate = (EditText) dialog.findViewById(R.id.date);
        final EditText call_summary = (EditText) dialog.findViewById(R.id.call_sumaary);
        company = (BetterSpinner) dialog.findViewById(R.id.company);
        responsible = (BetterSpinner) dialog.findViewById(R.id.responsible);
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editTextDate.getWindowToken(), 0);
                DatePickerDialog d = new DatePickerDialog(getActivity(), mDateSetListener, current_year, current_month, current_day);
                d.show();

                Log.i("date1--", current_month_text + "" + current_day + "" + current_year);


            }
        });

        ArrayAdapter<String> companyArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, companyNameList);
        companyArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        company.setAdapter(companyArrayAdapter);
        ArrayAdapter<String> responsibleArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, responsibleList);
        responsibleArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        responsible.setAdapter(responsibleArrayAdapter);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button add = (Button) dialog.findViewById(R.id.submit);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int companyId=companyArrayList.get(companyNameList.indexOf(company.getText().toString())).getId();
                Log.i("companyId-", String.valueOf(companyId));
                int responsibleId=responsibleStaffList.get(responsibleList.indexOf(responsible.getText().toString())).getId();
                Log.i("responsibleId-", String.valueOf(responsibleId));

                new AddOppCallTask().execute(token,opportunity_id,editTextDate.getText().toString(),call_summary.getText().toString(),
                   String.valueOf(companyId),String.valueOf(responsibleId));
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        // dialog.setTitle("Lead Details");
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.75);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.50);
        dialog.getWindow().setLayout(width, height);
        setDialog(dialog);
        dialog.show();

    }
});
        return v;
    }
    DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            current_year = year;
            current_month = monthOfYear;
            current_day = dayOfMonth;
           /* current_month_text=new DateFormatSymbols().getMonths()[current_month];
            date.setText(current_month_text+current_day+","+current_year);
            date.setSelection(date.getText().length());*/

            SimpleDateFormat simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'),Locale.ENGLISH);
            Date date=new Date((current_year-1900),current_month,current_day);
            String text_date=simpleDateFormat.format(date);
            Log.i("text_date",text_date);
            Log.i("dateformat",date_format);

            editTextDate.setText(text_date);
            editTextDate.clearFocus();
        }
    };
    private void getOpportunityCall(final String opportunityId) {
        Log.i("oppId--", String.valueOf(opportunityId));
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading, please wait");
        dialog.setTitle("Connecting server");
        dialog.show();
        dialog.setCancelable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,Appconfig.OPPORTUNITY_CALL_URL+Appconfig.TOKEN+"&opportunity_id="+opportunityId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("response--",response);
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("calls");
                            for (int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject object=jsonArray.getJSONObject(i);
                                OppCall oppCall = new OppCall();
                                oppCall.setDate(object.getString("date"));
                                oppCall.setCall_summary(object.getString("call_summary"));
                                oppCall.setCompany(object.getString("company"));
                                oppCall.setResponsible(object.getString("responsible"));
                                oppCall.setCall_id(object.getInt("id"));
                                oppCallArrayList.add(oppCall);


                            }
                            mAdapter = new OpportunityCallAdapter(oppCallArrayList, getActivity(),opportunityId);
                            // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                            RecyclerView.LayoutManager lmanager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                            //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);
                            rv.setItemAnimator(new DefaultItemAnimator());
                            rv.setLayoutManager(lmanager);
                            rv.setAdapter(mAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                          dialog.dismiss();
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("response--", String.valueOf(error));
            }
        }) ;
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);



    }

    class AddOppCallTask extends AsyncTask<String,Void,String>
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
            String opportunityId = params[1];
            String callDate = params[2];
            String callSummary = params[3];
            String companyId = params[4];
            String responsibleId=params[5];

            URL url = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("opportunity_id", opportunityId);
                jsonObject.put("date", callDate);
                jsonObject.put("call_summary", callSummary);
                jsonObject.put("company_id", companyId);
                jsonObject.put("resp_staff_id", responsibleId);
                url = new URL(Appconfig.POST_OPPPORTUNITY_CALL_URL+tok);
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
                oppCallArrayList.clear();
                getOpportunityCall(opportunity_id);

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
                            date_format=pre_settings.getString("date_format");

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

    public void getCompanyList(String token)
    { StringRequest stringRequest = new StringRequest(Request.Method.GET, Appconfig.COMPANY_URL+token,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        // Log.i("response--", String.valueOf(response));

                        JSONObject jsonObject=new JSONObject(response);
                        JSONArray jsonArray=jsonObject.getJSONArray("companies");
                        for (int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject object=jsonArray.getJSONObject(i);
                            Company company=new Company();
                            company.setId(object.getInt("id"));
                            company.setName(object.getString("name"));
                            companyNameList.add(object.getString("name"));
                            companyArrayList.add(company);
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

    public void getResponsibleStaffList(String token)
    { StringRequest stringRequest = new StringRequest(Request.Method.GET, Appconfig.STAFF_URL+token,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        // Log.i("response--", String.valueOf(response));

                        JSONObject jsonObject=new JSONObject(response);
                        JSONArray jsonArray=jsonObject.getJSONArray("staffs");
                        for (int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject object=jsonArray.getJSONObject(i);
                            Staff staff=new Staff();
                            staff.setId(object.getInt("id"));
                            staff.setName(object.getString("full_name"));
                            responsibleList.add(object.getString("full_name"));
                           responsibleStaffList.add(staff);
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
    public void setDialog(Dialog dialog) {
        mdialog = dialog;
    }

}