package com.project.naveen.lcrm.menu.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.swipe.util.Attributes;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.project.naveen.lcrm.AppSession;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.RecyclerTouchListener;
import com.project.naveen.lcrm.adapters.LoggedCallAdapter;
import com.project.naveen.lcrm.addactivity.AddLoggedCallActivity;
import com.project.naveen.lcrm.detailsactivity.DetailsLoggedCallActivity;
import com.project.naveen.lcrm.models.LoggedCalls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoggedCallsFragment extends Fragment {
     View v;
     LoggedCallAdapter loggedCallAdapter;
     String token;
     MaterialSearchView searchView;
     public LoggedCallsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 600) {
            v=inflater.inflate(R.layout.fragment_logged_calls, container, false);
        }
        else
        {
            v=inflater.inflate(R.layout.fragment_logged_calls_mob, container, false);
        }
//        View v=inflater.inflate(R.layout.fragment_logged_calls, container, false);
        setHasOptionsMenu(true);
        searchView=(MaterialSearchView)getActivity().findViewById(R.id.search_view);
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Logged Calls");
        }
        token=Appconfig.TOKEN;
        AppSession.loggedCallsArrayList =new ArrayList<>();
        AppSession.loggedCall_recyclerView=(RecyclerView)v.findViewById(R.id.rv);

        new GetLoggedCalls().execute(token);
        if (AppSession.loggedcall_write==0)
        {
            v.findViewById(R.id.fab).setVisibility(View.GONE);
        }
        v.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //animation= AnimationUtils.loadAnimation(getActivity(),R.anim.zoom_in);

//                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                Intent i=new Intent(getActivity(), AddLoggedCallActivity.class);
                getActivity().startActivity(i);

            }
        });

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search_staff,menu);
        MenuItem menuItem=menu.findItem(R.id.action_search);
        searchView.setMenuItem(menuItem);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<LoggedCalls> subLoggedCallArrayList=new ArrayList<>();
                Log.i("ontypecomplete", newText);
                Log.i("list data--", String.valueOf(AppSession.loggedCallsArrayList.size()));
                for (int i=0;i<AppSession.loggedCallsArrayList.size();i++)
                {
                    if (AppSession.loggedCallsArrayList.get(i).getCompany().contains(newText))
                    {
                       LoggedCalls loggedCalls=new LoggedCalls();
                        loggedCalls.setCompany(AppSession.loggedCallsArrayList.get(i).getCompany());
                        loggedCalls.setDate(AppSession.loggedCallsArrayList.get(i).getDate());
                        loggedCalls.setResponsible(AppSession.loggedCallsArrayList.get(i).getResponsible());
                        loggedCalls.setCall_summary(AppSession.loggedCallsArrayList.get(i).getCall_summary());

                        subLoggedCallArrayList.add(loggedCalls);
                    }
                    //System.out.println("lead item --"+leadsArrayList.get(i).getName()+" "+leadsArrayList.get(i).getNumber());
                }
                AppSession.loggedCall_recyclerView.setAdapter(new LoggedCallAdapter(getActivity(),subLoggedCallArrayList));
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    public class GetLoggedCalls extends AsyncTask<String,Void,String>
    {   String response;
        ProgressDialog dialog;

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
            URL url ;
            HttpURLConnection connection ;
            String tok=params[0];
            try {
                url = new URL(Appconfig.CALL_URL+tok);
                connection = (HttpURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String temp;
                while ((temp=br.readLine())!=null)
                {
                    buffer.append(temp);
                }
                response=buffer.toString();
//                  Log.i("response in d",response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
        @Override
        protected void onPostExecute(String response) {
            dialog.dismiss();
            try {
                  Log.i("response--",response);
                JSONObject jsonObject=new JSONObject(response);
                JSONArray jsonArray=jsonObject.getJSONArray("calls");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    LoggedCalls loggedCalls=new LoggedCalls();
                    loggedCalls.setId(object.getInt("id"));
                    loggedCalls.setCompany(object.getString("company"));
                    loggedCalls.setDate(object.getString("date"));
                    loggedCalls.setResponsible(object.getString("user"));
                    loggedCalls.setCall_summary(object.getString("call_summary"));
                    AppSession.loggedCallsArrayList.add(loggedCalls);

                }
                loggedCallAdapter= new LoggedCallAdapter(getActivity(),AppSession.loggedCallsArrayList);
                AppSession.loggedCall_recyclerView.setAdapter(loggedCallAdapter);
                AppSession.loggedCall_recyclerView.setItemAnimator(new DefaultItemAnimator());
                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);

                AppSession.loggedCall_recyclerView.setLayoutManager(lmanager);
                AppSession.loggedCall_recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),AppSession.loggedCall_recyclerView, new RecyclerTouchListener.ClickListener() {
                    @Override

                    public void onClick(View view, int position) {
                        LoggedCalls loggedCalls= AppSession.loggedCallsArrayList.get(position);
                        final int call_id=loggedCalls.getId();
                        Intent i=new Intent(getActivity(), DetailsLoggedCallActivity.class);
                        i.putExtra("call_id",String.valueOf(call_id));
                        i.putExtra("call_id_position",position);
                        Log.i("call_id_position", String.valueOf(position));
                        getActivity().startActivity(i);
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
