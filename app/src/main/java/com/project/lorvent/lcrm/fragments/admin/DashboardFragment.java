package com.project.lorvent.lcrm.fragments.admin;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;


import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.ChartFragment;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.adapters.DashboradAdapter;
import com.project.lorvent.lcrm.adapters.SwipeRecyclerAdapter;
import com.project.lorvent.lcrm.models.Events;
import com.project.lorvent.lcrm.models.OppLeads;
import com.project.lorvent.lcrm.utils.MyVolleySingleton;

import org.eazegraph.lib.charts.PieChart;
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
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class DashboardFragment extends ChartFragment {
    RecyclerView recyclerView;
    PieChart mPieChart;
    TextView events;
    BarChart barChart;
    ScrollView scrollView;
    public static int current_year, current_month, current_day;
    SimpleDateFormat simpleDateFormat;
    ArrayList<Events> subEventArrayList;
    RecyclerView rv;
    ImageView left, right;
    Runnable runnable;
    ProgressDialog progressDialog;
    boolean isNetworkAvail;
    FrameLayout frameLayout;
    ArrayList<IBarDataSet> dataSets;
    ArrayList<BarEntry> valueSet1;
    ArrayList<BarEntry> valueSet2;
    Handler handler;
    SwipeRecyclerAdapter recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<String> xAxis;
    private String date_format;
    private ProgressBar spinner;
    private boolean helpDisplayed = false;
    String formated_date;
    private static final String PREF_FIRSTLAUNCH_HELP = "helpDisplayed";
    public DashboardFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);
        //Connection.getDateSettings(Appconfig.TOKEN,getActivity());
        showHelpForFirstLaunch();
        Configuration config = getResources().getConfiguration();
        if (AppSession.role=="admin")
        {getDateSettings(Appconfig.TOKEN);}


 /*       AppSession.dashboardArrayList1 = new ArrayList<>();
        AppSession.dashboardArrayList2 = new ArrayList<>();*/
        //getDashboard(Appconfig.TOKEN);

      /*  if (config.smallestScreenWidthDp >= 600) {
            v = inflater.inflate(R.layout.fragment_dashboard, container, false);
            events = (TextView) v.findViewById(R.id.events);
            events.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.translate_text));
            mPieChart = (PieChart) v.findViewById(R.id.piechart);
            loadData();
        } else {
            v = inflater.inflate(R.layout.fragment_dashboard, container, false);
            scrollView = (ScrollView) v.findViewById(R.id.sv);
            events = (TextView) v.findViewById(R.id.events);
            events.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.translate_text));
           *//* v.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    scrollView.post(new Runnable() {
                        public void run() {
                            // scrollView.scrollTo(0, scrollView.getScrollY());
                            scrollView.fullScroll(View.FOCUS_UP);
                        }
                    });

                }
            });*//*
        }*/
        spinner = (ProgressBar) v.findViewById(R.id.pbHeaderProgress);
        spinner.setVisibility(View.GONE);
        events = (TextView) v.findViewById(R.id.events);
        events.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.translate_text));
        //when pie chart api will added
     /*   if (config.smallestScreenWidthDp >= 600) {
            mPieChart = (PieChart) v.findViewById(R.id.piechart);
            loadData();
        }*/
        frameLayout = (FrameLayout) v.findViewById(R.id.frame);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Dashboard");
        }
       /* Toolbar toolbar=(Toolbar)v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);*/
        left = (ImageView) v.findViewById(R.id.left);
        right = (ImageView) v.findViewById(R.id.right);
        AppSession.eventsArrayList = new ArrayList<>();
        subEventArrayList = new ArrayList<>();
        xAxis = new ArrayList<>();

        current_year = Calendar.getInstance().get(Calendar.YEAR);
        current_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        current_month = Calendar.getInstance().get(Calendar.MONTH);

        recyclerView = (RecyclerView) v.findViewById(R.id.rv);
        RecyclerView.LayoutManager layoutManager=new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new DashboradAdapter(AppSession.dashboardArrayList1,getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        rv = (RecyclerView) v.findViewById(R.id.swipe);

        new GetAllEvents().execute(Appconfig.TOKEN);
        barChart = (BarChart) v.findViewById(R.id.chart);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);
        YAxis left = barChart.getAxisLeft();
        left.setSpaceBottom(0);
        BarData data=new BarData(getXAxisValues(),getDataSet());
        barChart.setData(data);
        barChart.setDescription("");
        barChart.animateXY(2000, 2000);
        barChart.invalidate();

        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        new MaterialDialog.Builder(getActivity())
                                .content("Are you sure? you want to exit!")
                                .positiveText("Ok")
                                .positiveColorRes(R.color.colorPrimary)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        getActivity().finish();

                                    }
                                })
                                .negativeColorRes(R.color.colorPrimary)
                                .negativeText("Cancel")
                                .show();


                        return true;
                    }
                }
                return false;
            }
        });
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mPieChart != null)
            mPieChart.startAnimation();
   /*     AppSession.dashboardArrayList1 = new ArrayList<>();
        AppSession.dashboardArrayList2 = new ArrayList<>();
        getDashboard1(Appconfig.TOKEN);*/
    }

    @Override
    public void restartAnimation() {
        mPieChart.startAnimation();
    }

    @Override
    public void onReset() {

    }
    private ArrayList<IBarDataSet> getDataSet() {
        valueSet1 = new ArrayList<>();
        valueSet2 = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < AppSession.dashboardArrayList2.size(); i++) {
            OppLeads oppLeads =AppSession.dashboardArrayList2.get(i);
            if (i % 2 == 0) {
                BarEntry entry1 = new BarEntry(oppLeads.getLeads(), count);
                valueSet1.add(entry1);
                BarEntry entry2 = new BarEntry(oppLeads.getOpp(), count);
                valueSet2.add(entry2);
                count++;
            }
        }
        for (OppLeads oppLeads:AppSession.dashboardArrayList2) {
          Log.i("opplrads",oppLeads.getOpp()+""+oppLeads.getLeads());

        }
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
        for (int i = 0; i < AppSession.dashboardArrayList2.size(); i++) {
            OppLeads oppLeads =AppSession.dashboardArrayList2.get(i);
            if (i % 2 == 0) {
                xAxis.add(oppLeads.getMonth() + "" + oppLeads.getYear().substring(2, 4));
            }
        }
        return xAxis;
    }

    private class GetAllEvents extends AsyncTask<String, Void, String> {
        String response;

        @Override
        protected void onPreExecute() {
      /*      progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading, please wait");
            progressDialog.setTitle("Connecting server");
            progressDialog.show();
            progressDialog.setCancelable(false);*/
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
                url = new URL(get_url+ params[0]);
                connection = (HttpURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String temp;
                while ((temp = br.readLine()) != null) {
                    buffer.append(temp);
                }
                response = buffer.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }
            // connection.disconnect();
            return response;
        }

        @Override
        protected void onPostExecute(String response) {

           /* if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }*/
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("events");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    Events events = new Events();
                    events.setTitle(object.getString("title"));
                    events.setStart(object.getString("start"));
                    events.setEnd(object.getString("end"));
                    events.setType(object.getString("type"));
                    AppSession.eventsArrayList.add(events);
                }
                if (AppSession.role.equals("admin")){
                    getDateSettings(Appconfig.TOKEN);
                }
                else {
                    getDate1Settings();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
/*    public void autoScroll() {
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
    }*/

    @Override
    public void onPause() {
        super.onPause();

        if ((progressDialog != null) && progressDialog.isShowing())
            progressDialog.dismiss();
        progressDialog = null;
    }
    private void showHelpForFirstLaunch() {
        if (helpDisplayed)
            return;
        helpDisplayed = getPreferenceValue(PREF_FIRSTLAUNCH_HELP, false);
        if (!helpDisplayed) {
            savePreference(PREF_FIRSTLAUNCH_HELP, true);
            showOverLay1();
            showOverLay2();
           // showOverLay3();
        }
    }

    private boolean getPreferenceValue(String key, boolean defaultValue) {
        SharedPreferences preferences = getActivity().getPreferences(MODE_PRIVATE);
        return preferences.getBoolean(key, defaultValue);
    }
    private void savePreference(String key, boolean value) {
        SharedPreferences preferences = getActivity().getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    private void showOverLay1(){
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);

        dialog.setContentView(R.layout.overlay_view);
        LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.overlayLayout);
        ImageView image=(ImageView)dialog.findViewById(R.id.imageView1);
        image.setImageResource(R.drawable.overlay4);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();

            }

        });

        dialog.show();
    }
    private void showOverLay2(){
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);

        dialog.setContentView(R.layout.overlay_view);
        LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.overlayLayout);
        ImageView image=(ImageView)dialog.findViewById(R.id.imageView1);
        image.setImageResource(R.drawable.overlay5);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();

            }

        });
        dialog.show();

    }
    private void showOverLay3(){
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);

        dialog.setContentView(R.layout.overlay_view);
        LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.overlayLayout);
        ImageView image=(ImageView)dialog.findViewById(R.id.imageView1);
        image.setImageResource(R.drawable.overlay6);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();

            }

        });

        dialog.show();

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
        Log.i("url--", String.valueOf(get_url));

        StringRequest stringRequest = new StringRequest(Request.Method.GET,get_url+token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        spinner.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            JSONObject pre_settings=jsonObject.getJSONObject("settings");
                            date_format=(pre_settings.getString("date_format"));
                            Log.i("date_format--", String.valueOf(date_format));

                            Date date = new Date();
                            if (date_format.equals("Y-d-m")) {
                                simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                                //simpleDateFormat = new SimpleDateFormat(AppSession.date_format.replace("Y", "y").replace("m", "MM").replace("d", "dd"), Locale.ENGLISH);
                                formated_date = simpleDateFormat.format(date);
                            }
                            if (date_format.equals("F j,Y")) {
                                // simpleDateFormat = new SimpleDateFormat(date_format.replace('Y', 'y').replace('m', 'M'), Locale.ENGLISH);
                                simpleDateFormat = new SimpleDateFormat(date_format.replace("Y", "y").replace("F", "MMMM").replace("j", "d"), Locale.ENGLISH);
                                formated_date = simpleDateFormat.format(date);
                            }
                            if (date_format.equals("d.m.Y.")) {
                                // simpleDateFormat = new SimpleDateFormat(AppSession.date_format.replace('Y', 'y').replace('m', 'M'), Locale.ENGLISH);
                                simpleDateFormat = new SimpleDateFormat(date_format.replace("Y", "y").replace("m", "MM").replace("d", "dd"), Locale.ENGLISH);
                                formated_date = simpleDateFormat.format(date);
                            }
                            if (date_format.equals("d.m.Y")) {
                                simpleDateFormat = new SimpleDateFormat(date_format.replace("Y", "y").replace("m", "MM").replace("d", "dd"), Locale.ENGLISH);
                                formated_date = simpleDateFormat.format(date);
                            }
                            if (date_format.equals("d/m/Y")) {
                                simpleDateFormat = new SimpleDateFormat(date_format.replace("Y", "y").replace("m", "MM").replace("d", "dd"), Locale.ENGLISH);
                                formated_date = simpleDateFormat.format(date);
                            }
                            if (date_format.equals("m/d/Y")) {
                                simpleDateFormat = new SimpleDateFormat(date_format.replace("Y", "y").replace("m", "MM").replace("d", "dd"), Locale.ENGLISH);
                                formated_date = simpleDateFormat.format(date);
                            }
                            subEventArrayList.clear();
                            for (int i = 0; i < AppSession.eventsArrayList.size(); i++) {
                                if (formated_date!=null){
                                if (formated_date.equalsIgnoreCase(AppSession.eventsArrayList.get(i).getStart())) {
                                    Events subevent = AppSession.eventsArrayList.get(i);
                                    subEventArrayList.add(subevent);
                                }
                                }
                            }
                            recyclerAdapter = new SwipeRecyclerAdapter(getActivity(), subEventArrayList);
                            layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                            rv.setLayoutManager(layoutManager);
                            rv.setItemAnimator(new DefaultItemAnimator());
                            rv.setAdapter(recyclerAdapter);
                            //autoScroll();
                            right.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    rv.getLayoutManager().scrollToPosition(recyclerAdapter.getItemCount() - 1);
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
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

               // Log.i("response--", String.valueOf(error));
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

    private void getDate1Settings() {
        Date date = new Date();
        date_format=AppSession.date_format1;
        if (date_format.equals("Y-d-m")) {
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            //simpleDateFormat = new SimpleDateFormat(AppSession.date_format.replace("Y", "y").replace("m", "MM").replace("d", "dd"), Locale.ENGLISH);
            formated_date = simpleDateFormat.format(date);
            Log.i("formated_date",formated_date+""+AppSession.date_format1);

        }
        if (date_format.equals("F j,Y")) {
            simpleDateFormat = new SimpleDateFormat(AppSession.date_format1.replace("Y", "y").replace("F", "MMMM").replace("j", "d"), Locale.ENGLISH);
           // simpleDateFormat = new SimpleDateFormat("M dd,yyyy", Locale.ENGLISH);
            formated_date = simpleDateFormat.format(date);
            Log.i("formated_date",formated_date+""+AppSession.date_format1);

        }
        if (date_format.equals("d.m.Y.")) {
            simpleDateFormat = new SimpleDateFormat(AppSession.date_format1.replace("Y", "y").replace("m", "MM").replace("d", "dd"), Locale.ENGLISH);
            //simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy.", Locale.ENGLISH);
            formated_date = simpleDateFormat.format(date);
        }
        if (date_format.equals("d.m.Y")) {
            simpleDateFormat = new SimpleDateFormat(AppSession.date_format1.replace("Y", "y").replace("m", "MM").replace("d", "dd"), Locale.ENGLISH);

          //  simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
            formated_date = simpleDateFormat.format(date);
        }
        if (date_format.equals("d/m/Y")) {
            simpleDateFormat = new SimpleDateFormat(AppSession.date_format1.replace("Y", "y").replace("m", "MM").replace("d", "dd"), Locale.ENGLISH);

           // simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            formated_date = simpleDateFormat.format(date);
        }
        if (date_format.equals("m/d/Y")) {
            simpleDateFormat = new SimpleDateFormat(AppSession.date_format1.replace("Y", "y").replace("m", "MM").replace("d", "dd"), Locale.ENGLISH);

           // simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
            formated_date = simpleDateFormat.format(date);
        }
        subEventArrayList.clear();
        for (int i = 0; i < AppSession.eventsArrayList.size(); i++) {
            if (formated_date!=null){
                if (formated_date.equalsIgnoreCase(AppSession.eventsArrayList.get(i).getStart())) {
                    Events subevent = AppSession.eventsArrayList.get(i);
                    subEventArrayList.add(subevent);
                }
            }
        }

        spinner.setVisibility(View.GONE);
        recyclerAdapter = new SwipeRecyclerAdapter(getActivity(), subEventArrayList);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(layoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(recyclerAdapter);
        //autoScroll();
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rv.getLayoutManager().scrollToPosition(recyclerAdapter.getItemCount() - 1);
            }
        });
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rv.getLayoutManager().scrollToPosition(0);
            }
        });
    }

}
