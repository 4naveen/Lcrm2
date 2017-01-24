package com.project.naveen.lcrm.salesteams;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.naveen.lcrm.AppSession;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.ContactsCompletionView;
import com.project.naveen.lcrm.MyVolleySingleton;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.addactivity.AddSalesTeamActivity;
import com.project.naveen.lcrm.menu.fragment.salesteam.Person;
import com.project.naveen.lcrm.models.Staff;
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
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment extends Fragment {

    String sales_team_id;
    ArrayList<Staff> staffArrayList;
    String token;
    EditText sales_team_name, invoice_target, invoice_forecast;
    Button submit;
    BetterSpinner team_leader;
    ArrayList<String>spinnerArrayList,staffNameArrayList;
    ContactsCompletionView completionView;
    ArrayList<Person> persons;
    ArrayAdapter<Person> adapter;
    FrameLayout frameLayout;
    TextInputLayout input_team_leader,input_invoice_target,input_invoice_forecast,input_sales_team,input_team_member;

    public EditFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_edit10, container, false);
        submit=(Button)v.findViewById(R.id.submit);
        frameLayout=(FrameLayout)v.findViewById(R.id.layout);
        input_invoice_target=(TextInputLayout)v.findViewById(R.id.input_layout_invoice_target);
        input_sales_team=(TextInputLayout)v.findViewById(R.id.input_layout_salesteam);
        input_invoice_forecast=(TextInputLayout)v.findViewById(R.id.input_layout_invoice_forecast);
        input_team_leader=(TextInputLayout)v.findViewById(R.id.input_layout_team_leader);
        input_team_member=(TextInputLayout)v.findViewById(R.id.input_layout_team_member);
        sales_team_name = (EditText)v.findViewById(R.id.salesteamName);
        invoice_target = (EditText)v.findViewById(R.id.invoice_target);
        invoice_forecast = (EditText)v.findViewById(R.id.invoice_forecast);
        team_leader = (BetterSpinner)v. findViewById(R.id.team_leader);
        staffArrayList = new ArrayList<>();
        spinnerArrayList = new ArrayList<>();
        staffNameArrayList=new ArrayList<>();

        persons = new ArrayList<>();
        token = Appconfig.TOKEN;
        sales_team_id= getArguments().getString("salesteam_id");
        Log.i("saletid--", sales_team_id);
        getTeamLeaderList(token);
        new SalesTeamDetailsTask().execute(token,sales_team_id);

        ArrayAdapter<String> staffArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,AppSession.sales_personNameList);
        staffArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        team_leader.setAdapter(staffArrayAdapter);

        adapter = new FilteredArrayAdapter<Person>(getActivity(), R.layout.person_layout, persons) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {

                    LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                    convertView = l.inflate(R.layout.person_layout, parent, false);
                }
                Person p = getItem(position);
                ((TextView) convertView.findViewById(R.id.name)).setText(p.getName());
                return convertView;
            }

            @Override
            protected boolean keepObject(Person person, String mask) {
                mask = mask.toLowerCase();
                return person.getName().toLowerCase().startsWith(mask);
            }
        };
        completionView = (ContactsCompletionView)v.findViewById(R.id.searchView);
        completionView.setAdapter(adapter);
        //completionView.setTokenListener(getActivity());
        completionView.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Select);

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
                ArrayList<Integer> member_id = new ArrayList<>();
                final StringBuffer memberId = new StringBuffer();
                final int leader_id = AppSession.salesPersonList.get(AppSession.sales_personNameList.indexOf(team_leader.getText().toString())).getId();

                for (Object o : completionView.getObjects()) {
                    //Log.i("members--",o.toString());
                    member_id.add(staffArrayList.get(staffNameArrayList.indexOf(o.toString())).getId());
                    // Log.i("memberId-", String.valueOf(member_id));
                    // Log.i("memberId-", String.valueOf(staffArrayList.get(spinnerArrayList.indexOf(o.toString())).getId()));
                }

                for (int i = 1; i <= member_id.size(); i++) {
                   /* Log.i("i--", String.valueOf(i));
                    Log.i("member_ids--", String.valueOf(member_id.size()));*/
                    if (i < member_id.size()) {
                        memberId.append(member_id.get(i - 1) + ",");
                    }
                    if (i == member_id.size()) {
                        memberId.append(member_id.get(i - 1));
                    }
                }
                Log.i("memberId-", String.valueOf(memberId));
                          /*Log.i("leader name-",team_leader.getText().toString());
                          Log.i("leaderid--", String.valueOf(leader_id));*/

                new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("Are you sure you want edit it!")
                        .setCancelText("No,cancel plx!")
                        .setConfirmText("Yes,Edit it!")
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
                                new EditSalesTeamTask().execute(token, sales_team_id, sales_team_name.getText().toString(), invoice_target.getText().toString(),
                                        invoice_forecast.getText().toString(), String.valueOf(leader_id), String.valueOf(memberId));
                                sweetAlertDialog.cancel();
                            }
                        })
                        .show();

                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
        return v;
    }
    public void getTeamLeaderList(String token)
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
                            staffNameArrayList.add(object.getString("full_name"));
                            staffArrayList.add(staff);
                        }
                        for (int i = 0; i < staffArrayList.size(); i++) {
                            staffNameArrayList.add(staffArrayList.get(i).getName());
                            Person person = new Person(staffArrayList.get(i).getName());
                            Log.i("staffName--",staffArrayList.get(i).getName());
                            persons.add(person);
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

    class EditSalesTeamTask extends AsyncTask<String, Void, String> {
        HttpURLConnection conn;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            String response = "", jsonresponse = "";
            JSONObject json;
            BufferedReader bufferedReader;
            String tok = params[0];
            String sales_team_id = params[1];
            String sales_team_name = params[2];
            String invoice_target = params[3];
            String invoice_forecast = params[4];
            String team_leader = params[5];
            String team_number = params[6];
            URL url;
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("salesteam_id", sales_team_id);
                jsonObject.put("salesteam", sales_team_name);
                jsonObject.put("invoice_target", invoice_target);
                jsonObject.put("invoice_forecast", invoice_forecast);
                jsonObject.put("team_leader", team_leader);
                jsonObject.put("team_members", team_number);
                url = new URL(Appconfig.SALESTEAM_EDIT_URL + tok);
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
                    String line;
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
    private class SalesTeamDetailsTask extends AsyncTask<String,Void,String>
    {

        HttpURLConnection conn;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            String response="",jsonResponse="";
            JSONObject json;
            BufferedReader bufferedReader;
            String tok=params[0];
            String sales_team_id=params[1];
            URL url;
            try {

                url = new URL(Appconfig.SALESTEAM_DETAILS_URL+tok+"&salesteam_id="+sales_team_id);
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
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result);
                JSONArray jsonArray=jsonObject.getJSONArray("salesteam");
                for (int i=0;i<jsonArray.length();i++)
                {      JSONObject jsonObject1=jsonArray.getJSONObject(i);
                    sales_team_name.setText(jsonObject1.getString("salesteam"));
                    invoice_target.setText(String.valueOf(jsonObject1.get("invoice_target")));
                    invoice_forecast.setText(String.valueOf(jsonObject1.get("invoice_forecast")));
                    team_leader.setText(String.valueOf(jsonObject1.get("team_leader")));

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }
}
