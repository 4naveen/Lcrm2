package com.project.naveen.lcrm.opportunities;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.MyVolleySingleton;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.adapters.subadapter.OppMeetingAdapter;
import com.project.naveen.lcrm.adapters.subadapter.OpportunityCallAdapter;
import com.project.naveen.lcrm.addactivity.AddOppActivity;
import com.project.naveen.lcrm.menu.fragment.opportunity.MeetingActivity;
import com.project.naveen.lcrm.models.Staff;
import com.project.naveen.lcrm.models.submodels.OppCall;
import com.project.naveen.lcrm.models.submodels.OppMeeting;

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
public class MeetingFragment extends Fragment {
    ArrayList<OppMeeting> oppMeetingArrayList;
    RecyclerView rv;
    String token,opportunity_id,date_format;
    OppMeetingAdapter mAdapter;
    int start_day,start_month,start_year,end_day,end_month,end_year;
    int start_hr,start_min,end_hr,end_min;
    EditText meeting_sub,start_date,end_date,start_time,end_time;
    ArrayList<Staff>responsibleStaffList;
     ArrayList<String>responsibleList;
    TextInputLayout input_meeting_subject,input_responsible,input_start_date,input_start_time,input_end_date,input_end_time;
    SimpleDateFormat simpleDateFormat;
    public static Dialog mdialog;

    public MeetingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_meeting2, container, false);
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        opportunity_id=getArguments().getString("opp_id");

        if (actionBar != null) {
            actionBar.setTitle("Opportunity Meeting ");
        }
        oppMeetingArrayList =new ArrayList<>();
        responsibleList = new ArrayList<>();
        responsibleStaffList=new ArrayList<>();
        start_year= Calendar.getInstance().get(Calendar.YEAR);
        end_year= Calendar.getInstance().get(Calendar.YEAR);
        start_month=Calendar.getInstance().get(Calendar.MONTH);
        start_hr=Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        start_min=Calendar.getInstance().get(Calendar.MINUTE);

        end_month=Calendar.getInstance().get(Calendar.MONTH);
        start_day=Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        end_day=Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        end_hr=Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        end_min=Calendar.getInstance().get(Calendar.MINUTE);
        token= Appconfig.TOKEN;
        getOpportunityMeeting(opportunity_id);
        getDateSettings(token);
//        getResponsibleStaffList(token);

        rv=(RecyclerView)v.findViewById(R.id.rv);

        v.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.opp_meeting_add_dialog);
                start_time=(EditText)dialog.findViewById(R.id.start_time);
                end_time=(EditText)dialog.findViewById(R.id.end_time);
                start_date=(EditText)dialog.findViewById(R.id.start_date);
                start_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(start_date.getWindowToken(), 0);
                        DatePickerDialog d = new DatePickerDialog(getActivity(), startDateSetListener, start_year,start_month,start_day);
                        d.show();
                    }
                });

                end_date=(EditText)dialog.findViewById(R.id.end_date);
                end_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(end_date.getWindowToken(), 0);
                        DatePickerDialog d = new DatePickerDialog(getActivity(), endDateSetListener, end_year,end_month,end_day);
                        d.show();

                    }
                });
                start_time.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(start_time.getWindowToken(), 0);
                        TimePickerDialog timePickerDialog=new TimePickerDialog(getActivity(),start_time_listener,start_hr,start_min,true);
                        timePickerDialog.show();
                    }
                });
                end_time.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(end_time.getWindowToken(), 0);
                        TimePickerDialog timePickerDialog=new TimePickerDialog(getActivity(),end_time_listener,end_hr,end_min,true);
                        timePickerDialog.show();
                    }
                });
                 final EditText meeting=(EditText)dialog.findViewById(R.id.meeting_subject);

//                final EditText responsible=(EditText)dialog.findViewById(R.id.responsible);

                Button cancel=(Button) dialog.findViewById(R.id.cancel);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                Button add=(Button) dialog.findViewById(R.id.submit);
                       add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                     /*   int responsibleId=responsibleStaffList.get(responsibleList.indexOf(responsible.getText().toString())).getId();
                        Log.i("responsibleId-", String.valueOf(responsibleId));*/
                        String starting_date=start_date.getText().toString()+" "+start_time.getText().toString();
                        String ending_date=end_date.getText().toString()+" "+end_time.getText().toString();
                        new AddOppMeeting().execute(token,opportunity_id,starting_date,ending_date,meeting.getText().toString());
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
    final TimePickerDialog.OnTimeSetListener start_time_listener=new TimePickerDialog.OnTimeSetListener()
    {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            start_hr=hourOfDay;
            start_min=minute;
            start_time.setText(start_hr+":"+start_min);
        }
    };
    final TimePickerDialog.OnTimeSetListener end_time_listener=new TimePickerDialog.OnTimeSetListener()
    {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            end_hr=hourOfDay;
            end_min=minute;
            end_time.setText(end_hr+":"+end_min);
        }
    };
    DatePickerDialog.OnDateSetListener startDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            start_year=year;
            start_month=monthOfYear;
            start_day=dayOfMonth;
            simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'), Locale.ENGLISH);
            Date date=new Date(year-1900,monthOfYear,dayOfMonth);
            String text_date=simpleDateFormat.format(date);
            start_date.setText(text_date);
            start_date.clearFocus();
        }
    };
    DatePickerDialog.OnDateSetListener endDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            end_year=year;
            end_month=monthOfYear;
            end_day=dayOfMonth;

            simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'), Locale.ENGLISH);
            Date date=new Date(year-1900,monthOfYear,dayOfMonth);
            String text_date=simpleDateFormat.format(date);
            end_date.setText(text_date);

            end_date.clearFocus();
        }
    };

    private void getOpportunityMeeting(String opportunityId) {
        Log.i("oppId--", String.valueOf(opportunityId));
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading, please wait");
        dialog.setTitle("Connecting server");
        dialog.show();
        dialog.setCancelable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,Appconfig.OPPORTUNITY_MEETING_URL+Appconfig.TOKEN+"&opportunity_id="+opportunityId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("response--",response);
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("meetings");
                            for (int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject object=jsonArray.getJSONObject(i);
                                OppMeeting oppMeeting=new OppMeeting();
                                oppMeeting.setStart_date(object.getString("starting_date"));
                                oppMeeting.setEnd_date(object.getString("ending_date"));
                                oppMeeting.setMeeting_subject(object.getString("meeting_subject"));
                                oppMeeting.setResponsible(object.getString("responsible"));
                                oppMeeting.setMeeting_id(object.getInt("id"));
                                oppMeetingArrayList.add(oppMeeting);


                            }
                            for (int i = 0; i <oppMeetingArrayList.size() ; i++) {
                                System.out.println("opplist--"+oppMeetingArrayList.get(i).getMeeting_subject());
                                Log.i("oppcalllist-",oppMeetingArrayList.get(i).getMeeting_subject());
                            }
                            rv.setItemAnimator(new DefaultItemAnimator());
                            mAdapter=new OppMeetingAdapter(oppMeetingArrayList,getActivity(),opportunity_id);
                            // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                            RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
                            //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);

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
 /*   public void getResponsibleStaffList(String token)
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


    }*/

    class AddOppMeeting extends AsyncTask<String,Void,String>
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
            String startDate = params[2];
            String endDate = params[3];
            String meeting = params[4];
//            String responsibleId=params[5];

            URL url = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("opportunity_id", opportunityId);
                jsonObject.put("meeting_subject", meeting);
                jsonObject.put("starting_date", startDate);
                jsonObject.put("ending_date", endDate);
//                jsonObject.put("resp_staff_id", responsibleId);
                url = new URL(Appconfig.POST_OPPPORTUNITY_MEETING_URL+tok);
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
                //Log.d("jsonobject lines", jsonObject.toString());

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
                oppMeetingArrayList.clear();
                getOpportunityMeeting(opportunity_id);

            }
        }
    }
    public void setDialog(Dialog dialog) {
        mdialog = dialog;
    }

}
