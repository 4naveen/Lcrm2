package com.project.lorvent.lcrm.fragments.admin;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.adapters.CalendarAdapter;
import com.project.lorvent.lcrm.models.Events;
import com.project.lorvent.lcrm.utils.Connection;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment {
    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    MaterialCalendarView widget;
    SimpleDateFormat simpleDateFormat;
    CalendarAdapter calendarAdapter;
    RecyclerView rv;
    String token;
    String selected_date;
    LinearLayout frameLayout;
    private ProgressBar spinner;
    View v;
    public CalendarFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_calendar, container, false);
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Calendar");
        }
        AppSession.eventsArrayList = new ArrayList<>();
        Connection.getDateSettings(Appconfig.TOKEN,getActivity());
        spinner = (ProgressBar) v.findViewById(R.id.pbHeaderProgress);
        spinner.setVisibility(View.GONE);
        widget = (MaterialCalendarView) v.findViewById(R.id.calendarView);
        frameLayout = (LinearLayout) v.findViewById(R.id.frame);
        widget.setCurrentDate(new Date());
        rv = (RecyclerView) v.findViewById(R.id.rv);
        token = Appconfig.TOKEN;
        widget.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                CalendarDay date_selected = widget.getSelectedDate();
                selected_date = FORMATTER.format(date_selected.getDate());
                new GetAllEvents().execute(token);

            }
        });
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        NavigationView navigationView=(NavigationView) getActivity().findViewById(R.id.nav_view);
                        navigationView.getMenu().findItem(R.id.dashboard).setChecked(true);
                        Fragment fragment1 = new DashboardFragment();
                        FragmentTransaction trans1 = getFragmentManager().beginTransaction();
                        trans1.replace(R.id.frame, fragment1);
                        trans1.addToBackStack(null);
                        trans1.commit();
                        return true;
                    }
                }
                return false;
            }
        });
        return v;
    }
    class GetAllEvents extends AsyncTask<String, Void, String> {
        String response;

        @Override
        protected void onPreExecute() {
            spinner.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            URL url;
            HttpURLConnection connection;
            try {
                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String get_url;
                if (text_url != null) {
                    get_url = text_url + "/user/calendar?token=";
                } else {
                    get_url = Appconfig.CALENDAR_URL;
                }
                url = new URL(get_url+params[0]);
                connection = (HttpURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String temp;
                while ((temp = br.readLine()) != null) {
                    buffer.append(temp);
                }
                response = buffer.toString();

                //  Log.i("response in d",response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // connection.disconnect();
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            spinner.setVisibility(View.GONE);
            String formated_date=null;
            ArrayList<Events> subEventArrayList = new ArrayList<>();
            try {
                // Log.i("response--",response);
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("events");
                AppSession.eventsArrayList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    Events events = new Events();
                    events.setTitle(object.getString("title"));
                    events.setStart(object.getString("start"));
                    events.setEnd(object.getString("end"));
                    events.setType(object.getString("type"));
                    AppSession.eventsArrayList.add(events);
                }
                Date date = new Date(selected_date);
                if (AppSession.date_format.equals("Y-d-m")) {
                    simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    //simpleDateFormat = new SimpleDateFormat(AppSession.date_format.replace("Y", "y").replace("m", "MM").replace("d", "dd"), Locale.ENGLISH);
                    formated_date = simpleDateFormat.format(date);
                }
                if (AppSession.date_format.equals("F j,Y")) {
                    // simpleDateFormat = new SimpleDateFormat(date_format.replace('Y', 'y').replace('m', 'M'), Locale.ENGLISH);
                    simpleDateFormat = new SimpleDateFormat(AppSession.date_format.replace("Y", "y").replace("F", "MMMM").replace("j", "d"), Locale.ENGLISH);
                    formated_date = simpleDateFormat.format(date);
                }
                if (AppSession.date_format.equals("d.m.Y.")) {
                    // simpleDateFormat = new SimpleDateFormat(AppSession.date_format.replace('Y', 'y').replace('m', 'M'), Locale.ENGLISH);
                    simpleDateFormat = new SimpleDateFormat(AppSession.date_format.replace("Y", "y").replace("m", "MM").replace("d", "dd"), Locale.ENGLISH);
                    formated_date = simpleDateFormat.format(date);
                }
                if (AppSession.date_format.equals("d.m.Y")) {
                    simpleDateFormat = new SimpleDateFormat(AppSession.date_format.replace("Y", "y").replace("m", "MM").replace("d", "dd"), Locale.ENGLISH);
                    formated_date = simpleDateFormat.format(date);
                }
                if (AppSession.date_format.equals("d/m/Y")) {
                    simpleDateFormat = new SimpleDateFormat(AppSession.date_format.replace("Y", "y").replace("m", "MM").replace("d", "dd"), Locale.ENGLISH);
                    formated_date = simpleDateFormat.format(date);
                }
                if (AppSession.date_format.equals("m/d/Y")) {
                    simpleDateFormat = new SimpleDateFormat(AppSession.date_format.replace("Y", "y").replace("m", "MM").replace("d", "dd"), Locale.ENGLISH);
                    formated_date = simpleDateFormat.format(date);
                }
                subEventArrayList.clear();
                for (int i = 0; i < AppSession.eventsArrayList.size(); i++) {
                    if (formated_date.equalsIgnoreCase(AppSession.eventsArrayList.get(i).getStart())||formated_date.equalsIgnoreCase(AppSession.eventsArrayList.get(i).getEnd())) {
                        Events subevent = AppSession.eventsArrayList.get(i);
                        subEventArrayList.add(subevent);

                    }
                }
                if (subEventArrayList.isEmpty()) {
                    final Snackbar snackbar = Snackbar.make(frameLayout, "No data found in database !", Snackbar.LENGTH_LONG);
                    View v = snackbar.getView();
                    v.setMinimumWidth(1000);
                    TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.WHITE);
                    snackbar.show();
                }
                calendarAdapter = new CalendarAdapter(getActivity(), subEventArrayList);
                RecyclerView.LayoutManager lmanager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                rv.setLayoutManager(lmanager);
                rv.setItemAnimator(new DefaultItemAnimator());
                rv.setAdapter(calendarAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
