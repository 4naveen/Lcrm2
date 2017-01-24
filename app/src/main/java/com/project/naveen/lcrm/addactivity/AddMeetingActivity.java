package com.project.naveen.lcrm.addactivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import android.widget.TimePicker;
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
import com.project.naveen.lcrm.RecyclerTouchListener;
import com.project.naveen.lcrm.adapters.MeetingAdapter;
import com.project.naveen.lcrm.menu.fragment.salesteam.Person;
import com.project.naveen.lcrm.models.Meeting;
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
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class AddMeetingActivity extends AppCompatActivity implements TokenCompleteTextView.TokenListener<Person>,AdapterView.OnItemSelectedListener {
    String token;
    EditText meeting_sub,start_date,end_date,start_time,end_time;
    int start_day,start_month,start_year,end_day,end_month,end_year;
    int start_hr,start_min,end_hr,end_min;
    static final int START_DATE_PICKER_ID=111;
    static final int END_DATE_PICKER_ID=112;
    Button submit;
    String start_month_text,end_month_text,date_format;
    ArrayList<String> responsibleNameList;
    ArrayList<Staff>responsibleList;
    ArrayList<Staff> staffArrayList;
    ArrayList<String>staffNameArrayList;
    ArrayList<String>member_id;
    BetterSpinner responsible;
    SimpleDateFormat simpleDateFormat;
    ContactsCompletionView completionView;
    ArrayList<Person> persons;
    ArrayAdapter<Person> adapter;
    LinearLayout linearLayout;
    TextInputLayout input_meeting_subject,input_responsible,input_attendies,input_start_date,input_start_time,input_end_date,input_end_time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meeting);
        token= Appconfig.TOKEN;
        linearLayout=(LinearLayout)findViewById(R.id.layout);
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Add Meeting");
        }
        meeting_sub=(EditText)findViewById(R.id.meeting_subject);
        start_date=(EditText)findViewById(R.id.start_date);
        end_date=(EditText)findViewById(R.id.end_date);
        start_time=(EditText)findViewById(R.id.start_time);
        end_time=(EditText)findViewById(R.id.end_time);

        responsible=(BetterSpinner)findViewById(R.id.responsible);

        input_meeting_subject=(TextInputLayout)findViewById(R.id.input_layout_meeting_subject);
        input_responsible=(TextInputLayout)findViewById(R.id.input_layout_responsible);
        input_attendies=(TextInputLayout)findViewById(R.id.input_layout_attendies);
        input_start_date=(TextInputLayout)findViewById(R.id.text_start_date);
        input_start_time=(TextInputLayout)findViewById(R.id.text_start_time);
        input_end_date=(TextInputLayout)findViewById(R.id.input_layout_end_date);
        input_end_time=(TextInputLayout)findViewById(R.id.input_layout_end_time);

        submit=(Button)findViewById(R.id.submit);
        responsibleList=new ArrayList<>();
        responsibleNameList=new ArrayList<>();
        getresponsibleList(Appconfig.TOKEN);
        staffArrayList=new ArrayList<>();
        staffNameArrayList=new ArrayList<>();
        persons=new ArrayList<>();

        ArrayAdapter<String> responsibleArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,responsibleNameList);
        responsibleArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        responsible.setAdapter(responsibleArrayAdapter);
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
        getDateSettings(token);
        getTeamLeaderList(token);
        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(start_date.getWindowToken(), 0);
                showDialog(START_DATE_PICKER_ID);

            }
        });
        start_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(start_date.getWindowToken(), 0);
                    showDialog(START_DATE_PICKER_ID);
                }
            }
        });
        end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(end_date.getWindowToken(), 0);
                showDialog(END_DATE_PICKER_ID);

            }
        });
        end_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(end_date.getWindowToken(), 0);
                    showDialog(END_DATE_PICKER_ID);
                }
            }
        });
        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(start_date.getWindowToken(), 0);
                showDialog(START_DATE_PICKER_ID);

            }
        });
        end_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(end_date.getWindowToken(), 0);
                    showDialog(END_DATE_PICKER_ID);
                }
            }
        });
        start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog=new TimePickerDialog(AddMeetingActivity.this,start_time_listener,start_hr,start_min,true);
                timePickerDialog.show();
            }
        });
        end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog=new TimePickerDialog(AddMeetingActivity.this,end_time_listener,end_hr,end_min,true);
                timePickerDialog.show();
            }
        });

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
        completionView.setTokenListener(AddMeetingActivity.this);
        completionView.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Select);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (meeting_sub.getText().toString().isEmpty())
                {
                    input_meeting_subject.setError("Please enter subject");

                    return;
                }

                else if (start_date.getText().toString().isEmpty()){
                    input_start_date.setError("Please enter start date");
                    return;
                }
                else if (start_time.getText().toString().isEmpty()){
                    input_start_time.setError("Please enter start time");
                    return;
                }
                else if (end_date.getText().toString().isEmpty()){
                    input_end_date.setError("Please enter end date");
                    return;
                }
                else if (end_time.getText().toString().isEmpty()){
                    input_end_time.setError("Please select end time");
                    return;
                }
                else if (responsible.getText().toString().isEmpty()){
                    input_responsible.setError("Please select responsible person");
                    return;
                }
                else if (completionView.getText().toString().isEmpty()){
                    input_attendies.setError("Please select attendies");
                    return;
                }
                else {
                    //do nothing
                }
                member_id= new ArrayList<>();

                for (Object o:completionView.getObjects())
                {
                    //Log.i("members--",o.toString());
                    member_id.add(String.valueOf(staffArrayList.get(staffNameArrayList.indexOf(o.toString())).getId()));
                    // Log.i("memberId-", String.valueOf(member_id));
                    // Log.i("memberId-", String.valueOf(staffArrayList.get(spinnerArrayList.indexOf(o.toString())).getId()));
                }
                    String starting_date=start_date.getText().toString()+" "+start_time.getText().toString();
                    String ending_date=end_date.getText().toString()+" "+end_time.getText().toString();
                    Log.i("startingdate-",starting_date);
                    Log.i("endingdate-",ending_date);
                int responsibleId=responsibleList.get(responsibleNameList.indexOf(responsible.getText().toString())).getId();

                new AddMeeting().execute(token,meeting_sub.getText().toString(),starting_date,ending_date,String.valueOf(responsibleId));

            }
        });

    }
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id==START_DATE_PICKER_ID)
        {
            return new DatePickerDialog(AddMeetingActivity.this, next_listener,start_year,start_month,start_day);
        }
        if (id==END_DATE_PICKER_ID)
        {
            return new DatePickerDialog(AddMeetingActivity.this, expected_listener,end_year,end_month,end_day);
        }
        return null;
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
    final DatePickerDialog.OnDateSetListener next_listener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            start_year=year;
            start_month=month;
           // start_month_text=new DateFormatSymbols().getMonths()[start_month];
            start_day=dayOfMonth;
            //start_date.setText(start_month_text+start_day+","+start_year);
            simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'), Locale.ENGLISH);
            Date date=new Date(year-1900,month,dayOfMonth);
            String text_date=simpleDateFormat.format(date);
            start_date.setText(text_date);

            start_date.clearFocus();
        }
    };
    final DatePickerDialog.OnDateSetListener expected_listener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            end_year=year;
            end_month=month;
          //  end_month_text=new DateFormatSymbols().getMonths()[end_month];
            end_day=dayOfMonth;
           // end_date.setText(end_month_text+end_day+","+end_year);
            simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'), Locale.ENGLISH);
            Date date=new Date(year-1900,month,dayOfMonth);
            String text_date=simpleDateFormat.format(date);
            end_date.setText(text_date);

            end_date.clearFocus();
        }
    };

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onTokenAdded(Person token) {

    }

    @Override
    public void onTokenRemoved(Person token) {

    }

    class AddMeeting extends AsyncTask<String,Void,String>
    {
        ProgressDialog dialog;
        HttpsURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(AddMeetingActivity.this);
            dialog.setMessage("Loading, please wait...");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String token=params[0];
            String response="",jsonresponse="";
            JSONObject json=null;
            BufferedReader bufferedReader=null;
            URL url = null;

            String tok = params[0];
            String meeting_subject = params[1];
            String starting_date =params[2];
            String ending_date = params[3];
           // String attendees = params[4];
            String responsibleId=params[4];
            try {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("meeting_subject", meeting_subject);
                jsonObject.put("starting_date", starting_date);
                jsonObject.put("ending_date", ending_date);
                //jsonObject.put("attendees", "1,2");
                jsonObject.put("responsible_id", responsibleId);
                JSONArray array = new JSONArray();
                for (int i = 0; i <member_id.size(); i++) {
                    array.put(member_id.get(i));

                }

                jsonObject.put("attendees", array);

                Log.i("jsonobject--",jsonObject.toString());
                url = new URL(Appconfig.MEETING_POST_URL+tok);
                conn= (HttpsURLConnection) url.openConnection();
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
                    //Log.d("Output",br.toString());
                    while ((line = br.readLine()) != null) {
                        response += line;
                        Log.d("output lines", line);
                    }
                    json = new JSONObject(response);
                    //Get Values from JSONobject
                    //System.out.println("success=" + json.get("success"));

                    jsonresponse = json.getString("success");

                }
                else {
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line="";
                    while ((line = bufferedReader.readLine()) != null) {
                        response += line;
                        Log.d("output lines", line);
                    }
                    Log.i("response",response);
                    json = new JSONObject(response);
                    jsonresponse=json.getString("error");
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
            {        new GetAllMeetingTask().execute(token);

                final Snackbar snackbar = Snackbar.make(linearLayout, "Added 1 item Succesfully!", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
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
        MyVolleySingleton.getInstance(AddMeetingActivity.this).getRequestQueue().add(stringRequest);
    }
    public void getresponsibleList(String token)
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
                            responsibleNameList.add(object.getString("full_name"));
                            responsibleList.add(staff);
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
        MyVolleySingleton.getInstance(AddMeetingActivity.this).getRequestQueue().add(stringRequest);


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
        MyVolleySingleton.getInstance(AddMeetingActivity.this).getRequestQueue().add(stringRequest);


    }

    class GetAllMeetingTask extends AsyncTask<String,Void,String>
    {   String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            HttpURLConnection connection = null ;
            String tok=params[0];
            try {
                url = new URL(Appconfig.MEETINGS_URL+tok);
                connection = (HttpURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String temp;
                while ((temp=br.readLine())!=null)
                {
                    buffer.append(temp);
                }
                response=buffer.toString();
                //  Log.i("response in d",response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
        @Override
        protected void onPostExecute(String response) {

            try {
                Log.i("response--",response);
                JSONObject jsonObject=new JSONObject(response);
                JSONArray jsonArray=jsonObject.getJSONArray("meetings");
                AppSession.meetingArrayList.clear();
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    Meeting meeting=new Meeting();
                    meeting.setId(object.getInt("id"));
                    meeting.setMeeting_subject(object.getString("meeting_subject"));
                    meeting.setStarting_date(object.getString("starting_date"));
                    AppSession.meetingArrayList.add(meeting);

                    //  Log.i("leadslist--",lead.getName());
                }

                MeetingAdapter mAdapter = new MeetingAdapter(AddMeetingActivity.this,AppSession.meetingArrayList);


                AppSession.meeting_recyclerView.setAdapter(mAdapter);
                AppSession.meeting_recyclerView.setItemAnimator(new DefaultItemAnimator());
                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(AddMeetingActivity.this, LinearLayoutManager.VERTICAL,false);
                //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);
                AppSession.meeting_recyclerView.setLayoutManager(lmanager);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
