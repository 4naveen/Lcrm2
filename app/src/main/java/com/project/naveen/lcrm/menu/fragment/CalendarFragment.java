package com.project.naveen.lcrm.menu.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.project.naveen.lcrm.AppSession;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.adapters.CalendarAdapter;
import com.project.naveen.lcrm.models.Events;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment {

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    MaterialCalendarView widget;
    SimpleDateFormat simpleDateFormat;
    // private ArrayList<Events>eventsArrayList;
    CalendarAdapter calendarAdapter;
    RecyclerView rv;
    String token;
    String selected_date;
    public CalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_calendar, container, false);
        AppSession.eventsArrayList=new ArrayList<>();
        widget=(MaterialCalendarView)v.findViewById(R.id.calendarView);

            widget.setCurrentDate(new Date());
        rv=(RecyclerView)v.findViewById(R.id.rv);
        token= Appconfig.TOKEN;

        widget.setOnDateChangedListener(new OnDateSelectedListener() {
              @Override
              public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                  CalendarDay date_selected = widget.getSelectedDate();
                   selected_date=FORMATTER.format(date_selected.getDate());
                  new GetAllEvents().execute(token);

                  Toast.makeText(getActivity(),selected_date,Toast.LENGTH_LONG).show();
              }
          });
        return v;
    }


    class GetAllEvents extends AsyncTask<String,Void,String>
    {   String response;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            URL url;
            HttpURLConnection connection;
            try {
                url = new URL(Appconfig.CALENDAR_URL+params[0]);
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
            // connection.disconnect();
            return response;
        }
        @Override
        protected void onPostExecute(String response) {
            ArrayList<Events>subEventArrayList=new ArrayList<>();
            try {
               // Log.i("response--",response);
                JSONObject jsonObject=new JSONObject(response);
                JSONArray jsonArray=jsonObject.getJSONArray("events");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    Events events=new Events();
                    events.setTitle(object.getString("title"));
                    events.setStart(object.getString("start"));
                    events.setEnd(object.getString("end"));
                    events.setDescription(object.getString("description"));
                    AppSession.eventsArrayList.add(events);

                }
                Date date=new Date(selected_date);
//                simpleDateFormat = new SimpleDateFormat(AppSession.date_format.replace('Y', 'y').replace('m', 'M'), Locale.ENGLISH);
                simpleDateFormat = new SimpleDateFormat(AppSession.date_format.replace("Y","y").replace("m","MM").replace("d","dd"), Locale.ENGLISH);

                String formated_date=simpleDateFormat.format(date);
                Log.i("formated date--",formated_date);
                for (int i=0;i< AppSession.eventsArrayList.size();i++)
                {
                    if (formated_date.equalsIgnoreCase( AppSession.eventsArrayList.get(i).getStart()))
                    {
                        Events subevent= AppSession.eventsArrayList.get(i);

                        subEventArrayList.add(subevent);

                    }
                }

                calendarAdapter=new CalendarAdapter(getActivity(),subEventArrayList);

                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
                rv.setLayoutManager(lmanager);

                rv.setItemAnimator(new DefaultItemAnimator());
                rv.setAdapter(calendarAdapter);
                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));

                //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
}
