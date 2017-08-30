package com.project.lorvent.lcrm.fragments.admin;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.Connection;
import com.project.lorvent.lcrm.utils.MyVolleySingleton;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.adapters.subadapter.OpportunityCallAdapter;
import com.project.lorvent.lcrm.models.Company;
import com.project.lorvent.lcrm.models.Staff;
import com.project.lorvent.lcrm.models.submodels.OppCall;
import com.project.lorvent.lcrm.utils.NetworkStatus;
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
public class OpportunityCallFragment extends Fragment {
    ArrayList<OppCall> oppCallArrayList;
    RecyclerView rv;
    String token, opportunity_id, date_format;
    OpportunityCallAdapter mAdapter;
    public static int current_year, current_month, current_day;
    public static String current_month_text;
    EditText editTextDate, call_summary;
    BetterSpinner company, responsible;
    ArrayList<String> companyNameList, responsibleList;
    ArrayList<Company> companyArrayList;
    ArrayList<Staff> responsibleStaffList;
    public static Dialog mdialog;
    Configuration config;
    ProgressDialog dialog;
    TextInputLayout input_date, input_call_summary, input_customer, input_responsible;

    public OpportunityCallFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_call, container, false);
        setHasOptionsMenu(true);
        Connection.getDateSettings(Appconfig.TOKEN, getActivity());
        Connection.getCustomerList(Appconfig.TOKEN, getActivity());
        Connection.getStaffList(Appconfig.TOKEN, getActivity());
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        config = getResources().getConfiguration();

        if (actionBar != null) {
            actionBar.setTitle("Opportunity Call");
        }
        opportunity_id = getArguments().getString("opp_id");
        oppCallArrayList = new ArrayList<>();
        companyNameList = new ArrayList<>();
        responsibleList = new ArrayList<>();
        companyArrayList = new ArrayList<>();
        responsibleStaffList = new ArrayList<>();
        if (!NetworkStatus.isConnected(getActivity())) {
            new MaterialDialog.Builder(getActivity())
                    .title("Please check your internet Connectivity !")
                    .titleColor(Color.RED)
                    .positiveText("Ok")
                    .positiveColorRes(R.color.colorPrimary)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        }
                    })
                    .negativeColorRes(R.color.colorPrimary)
                    .negativeText("")
                    .show();

        }

        if (NetworkStatus.isConnected(getActivity())) {
            getOpportunityCall(opportunity_id);
        }
        current_year = Calendar.getInstance().get(Calendar.YEAR);
        current_month = Calendar.getInstance().get(Calendar.MONTH);
        current_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        token = Appconfig.TOKEN;

        rv = (RecyclerView) v.findViewById(R.id.rv);


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

   /*         SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppSession.date_format.replace("Y", "y").replace("m", "MM").replace("d", "dd"), Locale.ENGLISH);
            Date date = new Date((current_year - 1900), current_month, current_day);
            String text_date = simpleDateFormat.format(date);
            Log.i("text_date", text_date);
            editTextDate.setText(text_date);*/

            //Log.i("dateformat", date_format);
            Date date = new Date(year - 1900, monthOfYear, dayOfMonth);
            editTextDate.setText(Connection.getFormatedDate(date));
            editTextDate.clearFocus();
        }
    };

    private void getOpportunityCall(final String opportunityId) {
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading, please wait");
        dialog.setTitle("Connecting server");
        dialog.show();
        dialog.setCancelable(false);
        SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String get_url;
        if (text_url != null) {
            get_url = text_url + "/user/opportunity_calls?token=";
        } else {
            get_url = Appconfig.OPPORTUNITY_CALL_URL;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET,get_url+ Appconfig.TOKEN + "&opportunity_id=" + opportunityId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("calls");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                OppCall oppCall = new OppCall();
                                oppCall.setDate(object.getString("date"));
                                oppCall.setCall_summary(object.getString("call_summary"));
                                oppCall.setCompany(object.getString("company"));
                                oppCall.setResponsible(object.getString("responsible"));
                                oppCall.setCall_id(object.getInt("id"));
                                oppCallArrayList.add(oppCall);


                            }
                            mAdapter = new OpportunityCallAdapter(oppCallArrayList, getActivity(), opportunityId);
                            // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                            RecyclerView.LayoutManager lmanager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                            //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);
                            rv.setItemAnimator(new DefaultItemAnimator());
                            rv.setLayoutManager(lmanager);
                            rv.setAdapter(mAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);


    }

    class AddOppCallTask extends AsyncTask<String, Void, String> {
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
            String responsibleId = params[5];

            URL url = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("opportunity_id", opportunityId);
                jsonObject.put("date", callDate);
                jsonObject.put("call_summary", callSummary);
                jsonObject.put("company_id", companyId);
                jsonObject.put("resp_staff_id", responsibleId);

                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String post_url;
                if (text_url != null) {
                    post_url = text_url + "/user/post_opportunity_call?token=";
                } else {
                    post_url = Appconfig.POST_OPPPORTUNITY_CALL_URL;
                }
                url = new URL(post_url+tok);
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
            }
            return jsonresponse;
        }

        @Override
        protected void onPostExecute(String result) {

            dialog.dismiss();
            if (result.equals("success")) {
                oppCallArrayList.clear();
                getOpportunityCall(opportunity_id);

            }
        }
    }


    public void setDialog(Dialog dialog) {
        mdialog = dialog;
    }

    @Override
    public void onPause() {
        super.onPause();

        if ((dialog != null) && dialog.isShowing())
            dialog.dismiss();
        dialog = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_add,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.add){
            MaterialDialog dialog1 = new MaterialDialog.Builder(getActivity())
                    .title("New Qpportunity Call")
                    .customView(R.layout.opp_call_add_dialog, true)
                    .positiveText("SUBMIT")
                    .autoDismiss(false)
                    .positiveColorRes(R.color.colorPrimary)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            if (editTextDate.getText().toString().isEmpty()) {
                                input_date.setError("please enter date");
                                return;
                            }  else if (company.getText().toString().isEmpty()) {
                                input_customer.setError("please select company");
                                return;
                            } else if (responsible.getText().toString().isEmpty()) {
                                input_responsible.setError("please select user");
                                return;
                            }
                            else if (call_summary.getText().toString().isEmpty()) {
                                input_call_summary.setError("please enter description");
                                return;
                            }else {
                                dialog.dismiss();
                            }

                            int companyId=AppSession.companyList.get(AppSession.companyNameList.indexOf(company.getText().toString())).getId();
                            int responsibleId=AppSession.responsibleList.get(AppSession.responsibleNameList.indexOf(responsible.getText().toString())).getId();

                            new AddOppCallTask().execute(token, opportunity_id, editTextDate.getText().toString(), call_summary.getText().toString(),
                                    String.valueOf(companyId), String.valueOf(responsibleId));

                        }
                    })
                    .negativeColorRes(R.color.colorPrimary)
                    .negativeText("CANCEL")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();

                        }
                    })
                    .show();
            // MaterialDialog dialog1 = new MaterialDialog.Builder(getActivity()).build();
            View view = dialog1.getCustomView();
            if (view != null) {
                editTextDate = (EditText) dialog1.getCustomView().findViewById(R.id.date);
                call_summary = (EditText) dialog1.getCustomView().findViewById(R.id.call_sumaary);
                company = (BetterSpinner) dialog1.getCustomView().findViewById(R.id.company);
                responsible = (BetterSpinner) dialog1.getCustomView().findViewById(R.id.responsible);
                input_date = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.input_layout_date);
                input_call_summary = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.input_layout_call_sumaary);
                input_customer = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.input_layout_customer);
                input_responsible = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.input_layout_responsible);
                call_summary.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        input_call_summary.setError("");

                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                company.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        input_customer.setError("");
                    }
                });
                responsible.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        input_responsible.setError("");
                    }
                });

            }
            editTextDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    input_date.setError("");
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editTextDate.getWindowToken(), 0);
                    DatePickerDialog d = new DatePickerDialog(getActivity(), mDateSetListener, current_year, current_month, current_day);
                    d.show();
                }
            });

            ArrayAdapter<String> companyArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, AppSession.companyNameList);
            companyArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            company.setAdapter(companyArrayAdapter);
            ArrayAdapter<String> responsibleArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, AppSession.responsibleNameList);
            responsibleArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            responsible.setAdapter(responsibleArrayAdapter);

        }
        return super.onOptionsItemSelected(item);


    }
}