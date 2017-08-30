package com.project.lorvent.lcrm.fragments.admin.edit;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

import com.project.lorvent.lcrm.fragments.admin.details.LoggedCallDetailFragment;
import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.Connection;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.models.Company;
import com.project.lorvent.lcrm.models.Staff;
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

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoggedCallEditFragment extends Fragment {

    BetterSpinner contacts,responsible;
    ArrayList<String> contactNameList,responsibleNameList;
    ArrayList<Company>contactsList;
    ArrayList<Staff>responsibleList;
    Button submit;
    static final int DATE_PICKER_ID=111;
    int  current_year,current_month,current_day;
    String current_month_text,date_format,call_id;
    EditText call_date,call_summary,duration;
    SimpleDateFormat simpleDateFormat;
    LinearLayout linearLayout;
    TextInputLayout input_date,input_call_summary,input_contact,input_responsible,input_duration;

    public LoggedCallEditFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_edit9, container, false);
        Connection.getStaffList(Appconfig.TOKEN,getActivity());
        Connection.getCustomerList(Appconfig.TOKEN,getActivity());
        Connection.getDateSettings(Appconfig.TOKEN,getActivity());
        call_id=getArguments().getString("callId");
        call_date=(EditText)v.findViewById(R.id.date);
        call_summary=(EditText)v.findViewById(R.id.call_sumaary);
        contacts=(BetterSpinner)v.findViewById(R.id.contacts);
        responsible=(BetterSpinner)v.findViewById(R.id.responsible);
        duration=(EditText)v.findViewById(R.id.duration);
        linearLayout=(LinearLayout)v.findViewById(R.id.layout);
        input_date=(TextInputLayout)v.findViewById(R.id.input_layout_date);
        input_call_summary=(TextInputLayout)v.findViewById(R.id.input_layout_call_sumaary);
        input_contact=(TextInputLayout)v.findViewById(R.id.input_layout_customer);
        input_responsible=(TextInputLayout)v.findViewById(R.id.input_layout_responsible);
        input_duration=(TextInputLayout)v.findViewById(R.id.input_layout_duration);

        current_year= Calendar.getInstance().get(Calendar.YEAR);
        current_month=Calendar.getInstance().get(Calendar.MONTH);
        current_day=Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        submit=(Button)v.findViewById(R.id.submit);

        contactsList=new ArrayList<>();
        contactNameList=new ArrayList<>();
        responsibleList=new ArrayList<>();
        responsibleNameList=new ArrayList<>();
        new CallDetails().execute(Appconfig.TOKEN,call_id);


        ArrayAdapter<String> contactArrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,AppSession.companyNameList);
        contactArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        contacts.setAdapter(contactArrayAdapter);

        ArrayAdapter<String> responsibleArrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,AppSession.responsibleNameList);
        responsibleArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        responsible.setAdapter(responsibleArrayAdapter);
        call_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(call_date.getWindowToken(), 0);
                DatePickerDialog d = new DatePickerDialog(getActivity(), date_listener,current_year,current_month,current_day);
                d.show();
            }
        });
/*       call_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {InputMethodManager imm = (InputMethodManager) getActivity(). getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(call_date.getWindowToken(), 0);
                    DatePickerDialog d = new DatePickerDialog(getActivity(), date_listener,current_year,current_month,current_day);
                    d.show();
                }
            }
        });*/
        call_date.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_date.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
        duration.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_duration.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_contact.setError("");
            }
        });
        responsible.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_responsible.setError("");
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (call_date.getText().toString().isEmpty())
                {
                    input_date.setError("Please enter date");
                    return;
                }
                else if (call_summary.getText().toString().isEmpty()){
                    input_call_summary.setError("Please enter call summary");
                    return;
                }
                else if (duration.getText().toString().isEmpty()){
                    input_duration.setError("Please enter call duration");
                    return;
                }
                else if (contacts.getText().toString().isEmpty()){
                    input_contact.setError("Please enter contact");
                    return;
                }
                else if (responsible.getText().toString().isEmpty()){
                    input_responsible.setError("Please enter responsible person");
                    return;
                }


                else {
                    //do nothing
                }
                int companyId=AppSession.companyList.get(AppSession.companyNameList.indexOf(contacts.getText().toString())).getId();
                int responsibleId=AppSession.responsibleList.get(AppSession.responsibleNameList.indexOf(responsible.getText().toString())).getId();
                new EditCall().execute(Appconfig.TOKEN,call_date.getText().toString(),call_summary.getText().toString(),String.valueOf(companyId),
                        String.valueOf(responsibleId),duration.getText().toString(),call_id);

                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(submit.getWindowToken(), 0);
            }
        });
        return v;
    }

    final DatePickerDialog.OnDateSetListener date_listener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            current_year=year;
            current_month=month;
            current_month_text=new DateFormatSymbols().getMonths()[current_month];
            current_day=dayOfMonth;
            Date date = new Date(year - 1900, month, dayOfMonth);
            call_date.setText(Connection.getFormatedDate(date));
            call_date.setSelection(call_date.getText().length());
            call_date.clearFocus();
        }
    };


    class EditCall extends AsyncTask<String,Void,String>
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
            String date = params[1];
            String call_summary =params[2] ;
            String companyId = params[3];
            String resp_staff_id = params[4];
            String duration=params[5];
            String call_id=params[6];

            URL url = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("call_id", call_id);
                jsonObject.put("date", date);
                jsonObject.put("call_summary", call_summary);
                jsonObject.put("company_id", companyId);
                jsonObject.put("resp_staff_id", resp_staff_id);
                jsonObject.put("duration", duration);

                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String edit_url;
                if (text_url != null) {
                    edit_url= text_url + "/user/edit_call?token=";
                } else {
                    edit_url= Appconfig.EDIT_CALL_URL;
                }
                url = new URL(edit_url+ tok);
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
                final Snackbar snackbar = Snackbar.make(linearLayout, "Updated Succesfully!", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
                Bundle bundle=new Bundle();
                bundle.putString("callId",call_id);

                FragmentTransaction trans1=getFragmentManager().beginTransaction();
                Fragment fragment1=new LoggedCallDetailFragment();
                fragment1.setArguments(bundle);
                trans1.replace(R.id.frame,fragment1);
                trans1.addToBackStack(null);
                trans1.commit();

            }
            else {
                final Snackbar snackbar = Snackbar.make(linearLayout, "Item not updated! Try Again", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
            }
        }
    }


    private class CallDetails extends AsyncTask<String,Void,String>
    {
       // ProgressDialog dialog;
        HttpURLConnection conn;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            dialog = new ProgressDialog(getActivity());
//            dialog.setMessage("Loading, please wait");
//            dialog.setTitle("Connecting server");
//            dialog.show();
//            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String response="",jsonResponse="";
            JSONObject json;
            BufferedReader bufferedReader;
            String tok=params[0];
            String call_id=params[1];
            URL url;
            try {

                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String detail_url;
                if (text_url != null) {
                    detail_url= text_url + "/user/call?token=";
                } else {
                    detail_url= Appconfig.DETAILS_CALL_URL;
                }

                url = new URL(detail_url+tok+"&call_id="+call_id);
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
           // dialog.dismiss();
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result);
                JSONArray jsonArray=jsonObject.getJSONArray("call");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObject1=jsonArray.getJSONObject(i);
                    contacts.setText(jsonObject1.getString("company"));
                    call_date.setText(jsonObject1.getString("date"));
                    duration.setText(jsonObject1.getString("duration"));
                    responsible.setText(jsonObject1.getString("resp_staff"));
                    call_summary.setText(jsonObject1.getString("call_summary"));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
