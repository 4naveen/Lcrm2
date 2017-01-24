package com.project.naveen.lcrm.menu.fragment;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.project.naveen.lcrm.AppSession;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.ChartFragment;
import com.project.naveen.lcrm.MyVolleySingleton;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.adapters.DashboradAdapter;
import com.project.naveen.lcrm.adapters.SwipeRecyclerAdapter;
import com.project.naveen.lcrm.models.Events;
import com.project.naveen.lcrm.models.OppLeads;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.communication.IOnItemFocusChangedListener;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class DashboardFragment extends ChartFragment {
    RecyclerView recyclerView;
    PieChart mPieChart;
    TextView events;
    BarChart barChart;
    View v;
    ScrollView scrollView;
    public static int  current_year,current_month,current_day;
    SimpleDateFormat simpleDateFormat;
    ArrayList<Events>subEventArrayList;
    private List<SwipeItem> itemList;
    public ArrayList<Integer>dashboardArrayList1;
    public ArrayList<OppLeads>dashboardArrayList2;
    RecyclerView rv;
    ImageView left,right;
    Runnable runnable;
    // public static int count=0;
    Handler handler;
    SwipeRecyclerAdapter recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;
    public DashboardFragment() {
        new DashboardAdmin().execute(Appconfig.TOKEN);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 600) {
             v=inflater.inflate(R.layout.fragment_dashboard, container, false);
            events=(TextView)v.findViewById(R.id.events);
            events.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.translate_text));

        }
        else
        {
            v=inflater.inflate(R.layout.fragment_dashboard_mob, container, false);
            scrollView=(ScrollView)v.findViewById(R.id.sv);
            events=(TextView)v.findViewById(R.id.events);
            events.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.translate_text_mob));
            v.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    scrollView.post(new Runnable() {
                        public void run() {
                            // scrollView.scrollTo(0, scrollView.getScrollY());
                            scrollView.fullScroll(View.FOCUS_UP);
                        }
                    });

                }
            });
        }
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Dashboard");
        }
       /* Toolbar toolbar=(Toolbar)v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);*/
        left=(ImageView)v.findViewById(R.id.left);
        right=(ImageView)v.findViewById(R.id.right);
        AppSession.eventsArrayList=new ArrayList<>();
        subEventArrayList=new ArrayList<>();
        dashboardArrayList1=new ArrayList<>();
        dashboardArrayList2=new ArrayList<>();

        current_year = Calendar.getInstance().get(Calendar.YEAR);
        current_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        current_month = Calendar.getInstance().get(Calendar.MONTH);

        recyclerView=(RecyclerView)v.findViewById(R.id.rv);
        rv=(RecyclerView)v.findViewById(R.id.swipe);
        mPieChart = (PieChart) v.findViewById(R.id.piechart);
        loadData();

        barChart=(BarChart)v.findViewById(R.id.chart);
             XAxis xAxis=barChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);
        BarData data=new BarData(getXAxisValues(),getDataSet());
        barChart.setData(data);
        //chart.setData(data);
        barChart.setDescription("Lead Vs Opportunity");
        barChart.animateXY(2000, 2000);
        barChart.invalidate();

      /*  TranslateAnimation translateAnimation=new TranslateAnimation(4.0f,600.0f,0.0f,0.0f);
        translateAnimation.setDuration(5000);
        translateAnimation.setRepeatCount(5);
        translateAnimation.setRepeatMode(2);
        events.startAnimation(translateAnimation);*/
       // events.setSelected(true);


        new GetAllEvents().execute(Appconfig.TOKEN);
       /* SwipeSelector swipeSelector = (SwipeSelector)v.findViewById(R.id.swipeSelector);
        for (int i = 0; i <subEventArrayList.size() ; i++) {
          itemList.add(new SwipeItem(i,subEventArrayList.get(i).getTitle(),subEventArrayList.get(i).getDescription()));
            swipeSelector.setItems(itemList.get(i));

        }*/
/*        swipeSelector.setItems(
                // The first argument is the value for that item, and should in most cases be unique for the
                // current SwipeSelector, just as you would assign values to radio buttons.
                // You can use the value later on to check what the selected item was.
                // The value can be any Object, here we're using ints.

                new SwipeItem(0,"Leads","Two Leads created with name ghdghd and dgfffjg" +
                        "\nkjbjnbbnbkngfbjgbgjkbnjgnlgnlgb"),
                new SwipeItem(1, "Meeting", "Meeting held on today"+
                "\nvbjhbvbvfbvfbvfbfbgflbhsiluhbgfbhgbghbubgbg"),
                new SwipeItem(2, "Opportunity", "One Opportunity created with name uidfgdu"+
                "\nhvhjbvhjvbfkfhbfbvlfhbvfvbfhbvfbvjhbfhbfhbvfbvfhff"),
                new SwipeItem(3, "Contracts", " Three Contracs Signed today."+"\nbjhbvfbvfjhvbjfbvfbbbbkjbk"),
                new SwipeItem(3, "New Customer", "one new Customer added whose name is hdgfdshvfvh "+"\njhvgbhvbhjbvhjbvjhbfhvbfhbvfvbfvbfvvbvnbcjhbvjhvbjh")

        );*/
        //swipeSelector.setActivated(true);

        return v;
    }

    private void loadData() {
        mPieChart.addPieSlice(new PieModel("Freetime",5, Color.parseColor("#FE6DA8")));
        mPieChart.addPieSlice(new PieModel("Sleep", 30, Color.parseColor("#56B7F1")));
        mPieChart.addPieSlice(new PieModel("Work", 40, Color.parseColor("#CDA67F")));
        mPieChart.addPieSlice(new PieModel("Eating",5, Color.parseColor("#FED70E")));
        mPieChart.setOnItemFocusChangedListener(new IOnItemFocusChangedListener() {
            @Override
            public void onItemFocusChanged(int _Position) {
//                Log.d("PieChart", "Position: " + _Position);
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        mPieChart.startAnimation();
    }

    @Override
    public void restartAnimation() {
        mPieChart.startAnimation();
    }

    @Override
    public void onReset() {

    }
    private ArrayList<IBarDataSet> getDataSet() {
        ArrayList<IBarDataSet> dataSets ;

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        BarEntry v1e1 = new BarEntry(110.000f, 0); // Jan
        valueSet1.add(v1e1);
        BarEntry v1e2 = new BarEntry(40.000f, 1); // Feb
        valueSet1.add(v1e2);
        BarEntry v1e3 = new BarEntry(60.000f, 2); // Mar
        valueSet1.add(v1e3);
        BarEntry v1e4 = new BarEntry(30.000f, 3); // Apr
        valueSet1.add(v1e4);
        BarEntry v1e5 = new BarEntry(90.000f, 4); // May
        valueSet1.add(v1e5);
   /*     BarEntry v1e6 = new BarEntry(100.000f, 5); // Jun
        valueSet1.add(v1e6);*/

        ArrayList<BarEntry> valueSet2 = new ArrayList<>();
        BarEntry v2e1 = new BarEntry(150.000f, 0); // Jan
        valueSet2.add(v2e1);
        BarEntry v2e2 = new BarEntry(90.000f, 1); // Feb
        valueSet2.add(v2e2);
        BarEntry v2e3 = new BarEntry(120.000f, 2); // Mar
        valueSet2.add(v2e3);
        BarEntry v2e4 = new BarEntry(60.000f, 3); // Apr
        valueSet2.add(v2e4);
        BarEntry v2e5 = new BarEntry(20.000f, 4); // May
        valueSet2.add(v2e5);
       /* BarEntry v2e6 = new BarEntry(80.000f, 5); // Jun
        valueSet2.add(v2e6);*/

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Lead");
        barDataSet1.setColor(Color.rgb(79, 193, 233));
        BarDataSet barDataSet2 = new BarDataSet(valueSet2, "Opportunity");
        barDataSet2.setColor(Color.rgb(244, 134, 112));

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);
        return dataSets;
    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("Feb-April 16");
        xAxis.add("April-June 16");
        xAxis.add("Jun-August 16");
        xAxis.add("August-Oct 16");
        xAxis.add("Oct-Dec 16");
        return xAxis;
    }

    class DashboardAdmin extends AsyncTask<String,Void,String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("In Dashboard","in class Dashboard");
        }
        @Override
        protected String doInBackground(String... params) {
            URL url;
            String response="";
            HttpURLConnection connection ;
            BufferedReader bufferedReader;
            StringBuffer  buffer;
            String token=Appconfig.TOKEN;
            try {
                url = new URL(Appconfig.DASHBOARD_URL+token);
                connection = (HttpURLConnection) url.openConnection();
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line="";
                    bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    buffer=new StringBuffer();
                    //Log.d("Output",br.toString());
                    while ((line = bufferedReader.readLine()) != null) {
                        // response += line;
                        buffer.append(line);
                        Log.d("output lines in dash", line);
                    }
                    response=buffer.toString();

                    //System.out.println("success=" + json.get("success"));
                    Log.i("response in dash", response);
                }
                else {
                    InputStreamReader inputStreamReader = new InputStreamReader(connection.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    buffer=new StringBuffer();

                    String line ="";
                    while ((line = bufferedReader.readLine()) != null) {
                        //  response += line;
                        buffer.append(line);
                        Log.d("output lines in dash", line);
                    }
                    response=buffer.toString();
                    Log.i("response in dash", response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // connection.disconnect();
            return response;
        }
        @Override
        protected void onPostExecute(String response) {
/*            try {
                 //Log.i("response--",response);
                JSONObject jsonObject=new JSONObject(response);
                dashboardArrayList1.add(jsonObject.getInt("customers"));
                dashboardArrayList1.add(jsonObject.getInt("contracts"));
                dashboardArrayList1.add(jsonObject.getInt("products"));
                dashboardArrayList1.add(jsonObject.getInt("opportunities"));

                JSONArray jsonArray=jsonObject.getJSONArray("opportunity_leads");
                for (int i=0;i<jsonArray.length();i++)
                {    OppLeads oppLeads=new OppLeads();
                    JSONObject object=jsonArray.getJSONObject(i);
                    oppLeads.setMonth(object.getString("month"));
                    oppLeads.setMonth(object.getString("year"));
                    oppLeads.setOpp(object.getInt("opportunity"));
                    oppLeads.setLeads(object.getInt("leads"));
                    dashboardArrayList2.add(oppLeads);
                }

                recyclerView.setAdapter(new DashboradAdapter(dashboardArrayList1));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                RecyclerView.LayoutManager layoutManager=new GridLayoutManager(getActivity(),2);

                recyclerView.setLayoutManager(layoutManager);*/

       /*     } catch (JSONException e) {
                e.printStackTrace();
            }*/

        }
    }

 /*   public void getDashboard(String token)
    { StringRequest stringRequest = new StringRequest(Request.Method.GET, Appconfig.DASHBOARD_URL+token,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.i("response--", String.valueOf(response));
                        JSONObject jsonObject=new JSONObject(response);
                        dashboardArrayList1.add(jsonObject.getInt("customers"));
                        dashboardArrayList1.add(jsonObject.getInt("contracts"));
                        dashboardArrayList1.add(jsonObject.getInt("products"));
                        dashboardArrayList1.add(jsonObject.getInt("opportunities"));

                        JSONArray jsonArray=jsonObject.getJSONArray("opportunity_leads");
                        for (int i=0;i<jsonArray.length();i++)
                        {    OppLeads oppLeads=new OppLeads();
                            JSONObject object=jsonArray.getJSONObject(i);
                            oppLeads.setMonth(object.getString("month"));
                            oppLeads.setMonth(object.getString("year"));
                            oppLeads.setOpp(object.getInt("opportunity"));
                            oppLeads.setLeads(object.getInt("leads"));
                            dashboardArrayList2.add(oppLeads);
                        }

                        recyclerView.setAdapter(new DashboradAdapter(dashboardArrayList1));
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        RecyclerView.LayoutManager layoutManager=new GridLayoutManager(getActivity(),2);

                        recyclerView.setLayoutManager(layoutManager);

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

    class GetAllEvents extends AsyncTask<String,Void,String>
    {   String response;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
       /*     progressDialog.setMessage("Loading, please wait");
            progressDialog.setTitle("Connecting server");*/
            progressDialog.show();
            progressDialog.setCancelable(false);
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
                progressDialog.dismiss();
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
                Date date=new Date();
                Log.i("current date",date.toString());
//                simpleDateFormat = new SimpleDateFormat(AppSession.date_format.replace('Y', 'y').replace('m', 'M'), Locale.ENGLISH);
                simpleDateFormat = new SimpleDateFormat(AppSession.date_format.replace("Y","y").replace("m","MM").replace("d","dd"), Locale.ENGLISH);
                String formated_date=simpleDateFormat.format(date);
                Log.i("formated date--",formated_date);
                for (int i=0;i< AppSession.eventsArrayList.size();i++)
                {
                    if ("1970-01-01".equalsIgnoreCase(AppSession.eventsArrayList.get(i).getStart()))
                    {
                        Events subevent= AppSession.eventsArrayList.get(i);
                        subEventArrayList.add(subevent);
                    }
                }
                recyclerAdapter=new SwipeRecyclerAdapter(getActivity(),subEventArrayList);
                layoutManager=new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
                rv.setLayoutManager(layoutManager);
                rv.setItemAnimator(new DefaultItemAnimator());
                rv.setAdapter(recyclerAdapter);
                autoScroll();
                right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rv.getLayoutManager().scrollToPosition(recyclerAdapter.getItemCount());
                    }
                });
                left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rv.getLayoutManager().scrollToPosition(0);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
    public void autoScroll() {
        final int speedScroll = 3000;
        handler = new Handler();
        runnable = new Runnable() {
            int count = -1;

            @Override
            public void run() {
                if (count < rv.getAdapter().getItemCount()) {
                    rv.smoothScrollToPosition(++count);
                    handler.postDelayed(this, speedScroll);
                }
                if (count == rv.getAdapter().getItemCount()) {
                    rv.setLayoutManager(layoutManager);
                    rv.smoothScrollToPosition(--count);
                    handler.postDelayed(this, speedScroll);
                }

            }

        };
        handler.post(runnable);
    }
}
