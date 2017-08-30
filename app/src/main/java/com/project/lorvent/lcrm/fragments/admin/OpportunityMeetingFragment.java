package com.project.lorvent.lcrm.fragments.admin;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
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
import android.widget.TimePicker;

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
import com.project.lorvent.lcrm.adapters.subadapter.OppMeetingAdapter;
import com.project.lorvent.lcrm.models.Staff;
import com.project.lorvent.lcrm.models.submodels.OppMeeting;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class OpportunityMeetingFragment extends Fragment {
    ArrayList<OppMeeting> oppMeetingArrayList;
    RecyclerView rv;
    String token, opportunity_id, date_format;
    OppMeetingAdapter mAdapter;
    int start_day, start_month, start_year, end_day, end_month, end_year;
    int start_hr, start_min, end_hr, end_min;
    EditText meeting_sub, start_date, end_date, start_time, end_time;
    ArrayList<Staff> responsibleStaffList;
    ArrayList<String> responsibleList;
    SimpleDateFormat simpleDateFormat;
    public static Dialog mdialog;
    BetterSpinner responsible;
    ProgressDialog dialog;
    TextInputLayout input_meeting_subject,input_start_date,inpur_end_date,input_start_time,input_end_time,input_responsible;
    public OpportunityMeetingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_meeting2, container, false);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        opportunity_id = getArguments().getString("opp_id");
        setHasOptionsMenu(true);
        if (actionBar != null) {
            actionBar.setTitle("Opportunity Meeting ");
        }
        oppMeetingArrayList = new ArrayList<>();
        responsibleList = new ArrayList<>();
        responsibleStaffList = new ArrayList<>();
        start_year = Calendar.getInstance().get(Calendar.YEAR);
        end_year = Calendar.getInstance().get(Calendar.YEAR);
        start_month = Calendar.getInstance().get(Calendar.MONTH);
        start_hr = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        start_min = Calendar.getInstance().get(Calendar.MINUTE);

        end_month = Calendar.getInstance().get(Calendar.MONTH);
        start_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        end_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        end_hr = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        end_min = Calendar.getInstance().get(Calendar.MINUTE);
        token = Appconfig.TOKEN;
        if (!NetworkStatus.isConnected(getActivity())){
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

        if (NetworkStatus.isConnected(getActivity())){
            getOpportunityMeeting(opportunity_id);
        }
        getDateSettings(token);
        rv = (RecyclerView) v.findViewById(R.id.rv);

        return v;
    }

    final TimePickerDialog.OnTimeSetListener start_time_listener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            start_hr = hourOfDay;
            start_min = minute;
            if (AppSession.time_format.equals("g:i a"))
            {
                Date date=new Date(Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DATE),start_hr,start_min);
                String strDateFormat = "H:mm a";
                SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat,Locale.ENGLISH);
                start_time.setText(sdf.format(date).toLowerCase());
            }
            if (AppSession.time_format.equals("g:i A"))
            {  Date date=new Date(Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DATE),start_hr,start_min);
                String strDateFormat = "H:mm a";
                SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat,Locale.ENGLISH);
                start_time.setText(sdf.format(date));

            }
            if (AppSession.time_format.equals("H:i"))
            {
                Date date=new Date(Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DATE),start_hr,start_min);
                String strDateFormat = "HH:mm";
                SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat,Locale.ENGLISH);
                start_time.setText(sdf.format(date));

            }
          //  start_time.setText(start_hr + ":" + start_min);
        }
    };
    final TimePickerDialog.OnTimeSetListener end_time_listener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            end_hr = hourOfDay;
            end_min = minute;
            if (AppSession.time_format.equals("g:i a"))
            {
                Date date=new Date(Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DATE),end_hr,end_min);
                String strDateFormat = "H:mm a";
                SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat,Locale.ENGLISH);
                end_time.setText(sdf.format(date));
            }
            if (AppSession.time_format.equals("g:i A"))
            {    Date date=new Date(Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DATE),end_hr,end_min);
                String strDateFormat = "H:mm a";
                SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat,Locale.ENGLISH);
                end_time.setText(sdf.format(date));

            }
            if (AppSession.time_format.equals("H:i"))
            {
                Date date=new Date(Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DATE),end_hr,end_min);
                String strDateFormat = "HH:mm";
                SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat,Locale.ENGLISH);
                end_time.setText(sdf.format(date));

            }
            //end_time.setText(end_hr + ":" + end_min);
        }
    };
    DatePickerDialog.OnDateSetListener startDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            start_year = year;
            start_month = monthOfYear;
            start_day = dayOfMonth;

            Date date = new Date(year - 1900, monthOfYear, dayOfMonth);
            start_date.setText(Connection.getFormatedDate(date));
            start_date.clearFocus();
        }
    };
    DatePickerDialog.OnDateSetListener endDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            end_year = year;
            end_month = monthOfYear;
            end_day = dayOfMonth;

            Date date = new Date(year - 1900, monthOfYear, dayOfMonth);
            end_date.setText(Connection.getFormatedDate(date));
            end_date.clearFocus();
        }
    };

    private void getOpportunityMeeting(String opportunityId) {
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading, please wait");
        dialog.setTitle("Connecting server");
        dialog.show();
        dialog.setCancelable(false);

        SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String get_url;
        if (text_url != null) {
            get_url = text_url + "/user/opportunity_meetings?token=";
        } else {
            get_url = Appconfig.OPPORTUNITY_MEETING_URL;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET,get_url+ Appconfig.TOKEN + "&opportunity_id=" + opportunityId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("meetings");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                OppMeeting oppMeeting = new OppMeeting();
                                oppMeeting.setStart_date(object.getString("starting_date"));
                                oppMeeting.setEnd_date(object.getString("ending_date"));
                                oppMeeting.setMeeting_subject(object.getString("meeting_subject"));
                                oppMeeting.setResponsible(object.getString("responsible"));
                                oppMeeting.setMeeting_id(object.getInt("id"));
                                oppMeetingArrayList.add(oppMeeting);


                            }
                            for (int i = 0; i < oppMeetingArrayList.size(); i++) {
                                System.out.println("opplist--" + oppMeetingArrayList.get(i).getMeeting_subject());
                            }
                            rv.setItemAnimator(new DefaultItemAnimator());
                            mAdapter = new OppMeetingAdapter(oppMeetingArrayList, getActivity(), opportunity_id);
                            // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                            RecyclerView.LayoutManager lmanager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                            //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);

                            rv.setLayoutManager(lmanager);
                            rv.setAdapter(mAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (dialog!=null&&dialog.isShowing()){dialog.dismiss();}
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);


    }

    private void getDateSettings(String token) {

        SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String get_url;
        if (text_url != null) {
            get_url = text_url + "/user/settings?token=";
        } else {
            get_url = Appconfig.SETTINGS_URL;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET,get_url+ token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject pre_settings = jsonObject.getJSONObject("settings");
                            date_format = (pre_settings.getString("date_format"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }

        };
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);


    }

    public void getResponsibleStaffList(String token) {
        SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String get_url;
        if (text_url != null) {
            get_url = text_url + "/user/staffs?token=";
        } else {
            get_url = Appconfig.STAFF_URL;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET,get_url+ token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            // Log.i("response--", String.valueOf(response));

                            JSONObject jsonObject = new JSONObject(response);
                    /*        JSONArray jsonArray = jsonObject.getJSONArray("staffs");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                Staff staff = new Staff();
                                staff.setId(object.getInt("id"));
                                staff.setName(object.getString("full_name"));
                                responsibleList.add(object.getString("full_name"));
                                responsibleStaffList.add(staff);
                            }*/
                            JSONObject  jsonObject1=jsonObject.getJSONObject("staffs");
                            Iterator<String> iter = jsonObject1.keys();
                            while (iter.hasNext()) {
                                String key = iter.next();

                                try {
                                    Object value = jsonObject1.get(key);
                                    JSONObject jsonObject2= (JSONObject) jsonObject1.get(key);
                                    Staff staff =new Staff();
                                    staff.setName(jsonObject2.getString("full_name"));
                                    staff.setId(jsonObject2.getInt("id"));
                                    responsibleList.add(jsonObject2.getString("full_name"));
                                    responsibleStaffList.add(staff);

                                } catch (JSONException e) {
                                    // Something went wrong!
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }

        };
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);


    }

    private class AddOppMeeting extends AsyncTask<String, Void, String> {
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
            String responsibleId = params[5];

            URL url = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("opportunity_id", opportunityId);
                jsonObject.put("meeting_subject", meeting);
                jsonObject.put("starting_date", startDate);
                jsonObject.put("ending_date", endDate);
                jsonObject.put("responsible_id", responsibleId);
                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String post_url;
                if (text_url != null) {
                    post_url = text_url + "/user/post_opportunity_meeting?token=";
                } else {
                    post_url = Appconfig.POST_OPPPORTUNITY_MEETING_URL;
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
                oppMeetingArrayList.clear();
                getOpportunityMeeting(opportunity_id);

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
            getResponsibleStaffList(Appconfig.TOKEN);
            MaterialDialog dialog1 = new MaterialDialog.Builder(getActivity())
                    .title("New Qpportunity Meeting")
                    .customView(R.layout.opp_meeting_add_dialog, true)
                    .positiveText("SUBMIT")
                    .autoDismiss(false)
                    .positiveColorRes(R.color.colorPrimary)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (start_date.getText().toString().isEmpty()) {
                                input_start_date.setError("please enter start date");
                                return;
                            }
                            else if (start_time.getText().toString().isEmpty()) {
                                input_start_time.setError("please enter start time");
                                return;
                            }
                            else if (end_date.getText().toString().isEmpty()) {
                                inpur_end_date.setError("please select end date");
                                return;
                            }
                            else if (end_time.getText().toString().isEmpty()) {
                                input_end_time.setError("please select end time");
                                return;
                            }
                            else if (meeting_sub.getText().toString().isEmpty()) {
                                input_meeting_subject.setError("please enter meeting subject");
                                return;
                            }
                            else if (responsible.getText().toString().isEmpty()) {
                                input_responsible.setError("please select responsible staff");
                                return;
                            }

                            else {
                                dialog.dismiss();
                            }

                            String starting_date = start_date.getText().toString() + " " + start_time.getText().toString();
                            String ending_date = end_date.getText().toString() + " " + end_time.getText().toString();
                            int responsibleId = responsibleStaffList.get(responsibleList.indexOf(responsible.getText().toString())).getId();

                            new AddOppMeeting().execute(token, opportunity_id, starting_date, ending_date, meeting_sub.getText().toString(),
                                    String.valueOf(responsibleId));

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

            View view = dialog1.getCustomView();
            if (view != null) {
                start_time = (EditText) dialog1.getCustomView().findViewById(R.id.start_time);
                end_time = (EditText) dialog1.getCustomView().findViewById(R.id.end_time);
                start_date = (EditText) dialog1.getCustomView().findViewById(R.id.start_date);
                end_date = (EditText) dialog1.getCustomView().findViewById(R.id.end_date);
                meeting_sub = (EditText) dialog1.getCustomView().findViewById(R.id.meeting_subject);
                responsible = (BetterSpinner) dialog1.getCustomView().findViewById(R.id.responsible);
                input_start_date = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.text_start_date);
                inpur_end_date = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.input_layout_end_date);
                input_start_time = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.text_start_time);
                input_end_time = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.input_layout_end_time);
                input_meeting_subject = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.input_layout_meeting_subject);
                input_responsible = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.input_layout_responsible);
                responsible.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        input_responsible.setError("");
                    }
                });
                meeting_sub.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        input_meeting_subject.setError("");

                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    start_time.setShowSoftInputOnFocus(false);
                    end_time.setShowSoftInputOnFocus(false);
                    start_date.setShowSoftInputOnFocus(false);
                    end_date.setShowSoftInputOnFocus(false);
                }
            }
            start_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    input_start_date.setError("");

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(start_date.getWindowToken(), 0);
                    DatePickerDialog d = new DatePickerDialog(getActivity(), startDateSetListener, start_year, start_month, start_day);
                    d.show();
                }
            });

            end_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    input_end_time.setError("");

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(end_date.getWindowToken(), 0);
                    DatePickerDialog d = new DatePickerDialog(getActivity(), endDateSetListener, end_year, end_month, end_day);
                    d.show();

                }
            });
            start_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    input_start_time.setError("");
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(start_time.getWindowToken(), 0);
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), start_time_listener, start_hr, start_min, true);
                    timePickerDialog.show();
                }
            });
            end_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    input_end_time.setError("");
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(end_time.getWindowToken(), 0);
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), end_time_listener, end_hr, end_min, true);
                    timePickerDialog.show();
                }
            });
            ArrayAdapter<String> responsibleArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, responsibleList);
            responsibleArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            responsible.setAdapter(responsibleArrayAdapter);

        }
        return super.onOptionsItemSelected(item);
    }
}
