package com.project.lorvent.lcrm.fragments.admin.edit;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;

import com.project.lorvent.lcrm.fragments.admin.details.MeetingDetailsFragment;
import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.Connection;
import com.project.lorvent.lcrm.utils.ContactsCompletionView;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.models.Person;
import com.project.lorvent.lcrm.models.Staff;
import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;
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
import java.util.Locale;
import java.util.StringTokenizer;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeetingEditFragment extends Fragment implements TokenCompleteTextView.TokenListener<Person>,AdapterView.OnItemSelectedListener {

    String token,meeting_id;
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

    public MeetingEditFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_edit8, container, false);
        Connection.getStaffList(Appconfig.TOKEN,getActivity());
        Connection.getDateSettings(Appconfig.TOKEN,getActivity());
        token= Appconfig.TOKEN;
        meeting_id=getArguments().getString("meetingId");
        linearLayout=(LinearLayout)v.findViewById(R.id.layout);

        meeting_sub=(EditText)v.findViewById(R.id.meeting_subject);
        start_date=(EditText)v.findViewById(R.id.start_date);
        end_date=(EditText)v.findViewById(R.id.end_date);
        start_time=(EditText)v.findViewById(R.id.start_time);
        end_time=(EditText)v.findViewById(R.id.end_time);

        responsible=(BetterSpinner)v.findViewById(R.id.responsible);
        input_meeting_subject=(TextInputLayout)v.findViewById(R.id.input_layout_meeting_subject);
        input_responsible=(TextInputLayout)v.findViewById(R.id.input_layout_responsible);
        input_attendies=(TextInputLayout)v.findViewById(R.id.input_layout_attendies);
        input_start_date=(TextInputLayout)v.findViewById(R.id.text_start_date);
        input_start_time=(TextInputLayout)v.findViewById(R.id.text_start_time);
        input_end_date=(TextInputLayout)v.findViewById(R.id.input_layout_end_date);
        input_end_time=(TextInputLayout)v.findViewById(R.id.input_layout_end_time);

        submit=(Button)v.findViewById(R.id.submit);
        responsibleList=new ArrayList<>();
        responsibleNameList=new ArrayList<>();
        staffArrayList=new ArrayList<>();
        staffNameArrayList=new ArrayList<>();

        new MeetingDetailsTask().execute(Appconfig.TOKEN,meeting_id);
        persons=new ArrayList<>();

        ArrayAdapter<String> responsibleArrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,AppSession.responsibleNameList);
        responsibleArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        responsible.setAdapter(responsibleArrayAdapter);

        for (int i = 0; i < AppSession.teamMemberList.size(); i++) {
            Person person = new Person(AppSession.teamMemberList.get(i).getName());
            persons.add(person);
        }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            start_time.setShowSoftInputOnFocus(false);
            end_time.setShowSoftInputOnFocus(false);
            start_date.setShowSoftInputOnFocus(false);
            end_date.setShowSoftInputOnFocus(false);
        }
        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(start_date.getWindowToken(), 0);
                DatePickerDialog d = new DatePickerDialog(getActivity(), next_listener,start_year,start_month,start_day);
                d.show();
            }
        });
        start_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {InputMethodManager imm = (InputMethodManager)getActivity(). getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(start_date.getWindowToken(), 0);
                    DatePickerDialog d = new DatePickerDialog(getActivity(), next_listener,start_year,start_month,start_day);
                    d.show();                }
            }
        });
        end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getActivity(). getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(end_date.getWindowToken(), 0);
                DatePickerDialog d = new DatePickerDialog(getActivity(), expected_listener,end_year,end_month,end_day);
                d.show();
            }
        });
        end_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {InputMethodManager imm = (InputMethodManager)getActivity(). getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(end_date.getWindowToken(), 0);
                    DatePickerDialog d = new DatePickerDialog(getActivity(), expected_listener,end_year,end_month,end_day);
                    d.show();
                }
            }
        });

        start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getActivity(). getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(start_time.getWindowToken(), 0);
                TimePickerDialog timePickerDialog=new TimePickerDialog(getActivity(),start_time_listener,start_hr,start_min,true);
                timePickerDialog.show();
            }
        });
        end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getActivity(). getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(end_time.getWindowToken(), 0);
                TimePickerDialog timePickerDialog=new TimePickerDialog(getActivity(),end_time_listener,end_hr,end_min,true);
                timePickerDialog.show();
            }
        });

        adapter = new FilteredArrayAdapter<Person>(getActivity(), R.layout.person_layout,persons) {
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
        completionView = (ContactsCompletionView)v.findViewById(R.id.searchView);
        completionView.setAdapter(adapter);
       // completionView.setTokenListener(getActivity());
        completionView.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Select);
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
        start_date.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_start_date.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        end_date.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_end_date.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        start_time.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_start_time.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        end_time.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_end_time.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

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
                   // input_start_time.setError("Please enter start time");
                    return;
                }
                else if (end_date.getText().toString().isEmpty()){
                   // input_end_date.setError("Please enter end date");
                    return;
                }
                else if (end_time.getText().toString().isEmpty()){
                    //input_end_time.setError("Please select end time");
                    return;
                }
                else if (responsible.getText().toString().isEmpty()){
                    //input_responsible.setError("Please select responsible person");
                    return;
                }
                else if (completionView.getText().toString().isEmpty()){
                    //input_attendies.setError("Please select attendies");
                    return;
                }
                else {
                    //do nothing
                }
                member_id= new ArrayList<>();

                for (Object o:completionView.getObjects())
                {
                    member_id.add(String.valueOf(AppSession.teamMemberList.get(AppSession.teamMemberNameList.indexOf(o.toString())).getId()));
                }
                String starting_date=start_date.getText().toString()+""+start_time.getText().toString();
                String ending_date=end_date.getText().toString()+""+end_time.getText().toString();
                int responsibleId=AppSession.responsibleList.get(AppSession.responsibleNameList.indexOf(responsible.getText().toString())).getId();

                new EditMeeting().execute(token,meeting_sub.getText().toString(),starting_date,ending_date,String.valueOf(responsibleId),meeting_id);

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
          // start_time.setText(start_hr+":"+start_min);
       }
   };
    final TimePickerDialog.OnTimeSetListener end_time_listener=new TimePickerDialog.OnTimeSetListener()
    {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            end_hr=hourOfDay;
            end_min=minute;
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
           // end_time.setText(end_hr+":"+end_min);
        }
    };
    final DatePickerDialog.OnDateSetListener next_listener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            start_year=year;
            start_month=month;
            // start_month_text=new DateFormatSymbols().getMonths()[start_month];
            start_day=dayOfMonth;
            Date date = new Date(year - 1900, month, dayOfMonth);
            start_date.setText(Connection.getFormatedDate(date));
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
            Date date = new Date(year - 1900, month, dayOfMonth);
            end_date.setText(Connection.getFormatedDate(date));
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

    class EditMeeting extends AsyncTask<String,Void,String>
    {
        ProgressDialog dialog;
        HttpsURLConnection conn;
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
            String token=params[0];
            String response="",jsonresponse="";
            JSONObject json=null;
            BufferedReader bufferedReader=null;
            URL url = null;

            String tok = params[0];

            String meeting_subject = params[1];
            String starting_date =params[2];
            String ending_date = params[3];
            String responsibleId=params[4];
            String meeting_id = params[5];

            try {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("meeting_id", meeting_id);
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
             //  Log.i("jsonobject", String.valueOf(jsonObject));

                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String edit_url;
                if (text_url != null) {
                    edit_url= text_url + "/user/edit_meeting?token=";
                } else {
                    edit_url= Appconfig.MEETING_EDIT_URL;
                }
                url = new URL(edit_url+ tok);
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
                    while ((line = br.readLine()) != null) {
                        response += line;
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
                    }
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
            {
                final Snackbar snackbar = Snackbar.make(linearLayout, "Updated Succesfully!", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
                Bundle bundle=new Bundle();
                bundle.putString("meetingId",meeting_id);
                FragmentTransaction trans1=getFragmentManager().beginTransaction();
                Fragment fragment1=new MeetingDetailsFragment();
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


    private class MeetingDetailsTask extends AsyncTask<String,Void,String>
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
            String meetingId=params[1];
            URL url;
            try {
                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String detail_url;
                if (text_url != null) {
                    detail_url= text_url + "/user/meeting?token=";
                } else {
                    detail_url= Appconfig.MEETING_DETAILS_URL;
                }
                url = new URL(detail_url+tok+"&meeting_id="+meetingId);
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
            ArrayList<String>start_date_time,end_date_time;
            JSONObject jsonObject;
            try {

                jsonObject = new JSONObject(result);
                start_date_time=new ArrayList<>();
                end_date_time=new ArrayList<>();
                JSONArray meetingJsonArray = jsonObject.getJSONArray("meeting");
                for (int i = 0; i < meetingJsonArray.length(); i++) {
                    JSONObject jsonObject1 = meetingJsonArray.getJSONObject(i);
                    meeting_sub.setText(jsonObject1.getString("meeting_subject"));
                    responsible.setText(jsonObject1.getString("responsible"));

                    StringTokenizer tk1=new StringTokenizer(jsonObject1.getString("starting_date"));
                    StringTokenizer tk2=new StringTokenizer(jsonObject1.getString("ending_date"));

                    while (tk1.hasMoreTokens())
                    {
                        //Log.i("token",tk.nextToken());
                        start_date_time.add(tk1.nextToken());
                    }
                    while (tk2.hasMoreTokens())
                    {
                        //Log.i("token",tk.nextToken());
                        end_date_time.add(tk2.nextToken());
                    }
                   /* for (int j = 0; j <date_time.size() ; j++) {
                        Log.i("date_time",date_time.get(j));

                    }*/
                    start_date.setText(start_date_time.get(0)+""+start_date_time.get(1));
                    start_time.setText(start_date_time.get(2));
                    end_date.setText(end_date_time.get(0)+""+end_date_time.get(1));
                    end_time.setText(end_date_time.get(2));


                }
                JSONArray attendiesJsonArray = jsonObject.getJSONArray("attendees");

               /* for (int i=0;i<attendiesJsonArray.length();i++)
                {
                    JSONArray JsonArray = attendiesJsonArray.getJSONArray(i);
                    for (int j=0;j<=JsonArray.length();j++)
                    {
                        tv2.setText(tv2.getText()+"  "+JsonArray.getString(j));
                    }
                }*/

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
