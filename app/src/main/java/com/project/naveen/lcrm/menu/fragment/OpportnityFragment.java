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

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.project.naveen.lcrm.AppSession;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.RecyclerTouchListener;
import com.project.naveen.lcrm.adapters.OpportunityAdapter;
import com.project.naveen.lcrm.addactivity.AddOppActivity;
import com.project.naveen.lcrm.detailsactivity.DetailOpportunityActivity;
import com.project.naveen.lcrm.models.Opportunity;

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
public class OpportnityFragment extends Fragment {
    //ArrayList<Opportunity> opportunityArrayList;
//    RecyclerView rv;
    String token;
    MaterialSearchView searchView;
    OpportunityAdapter mAdapter;
    View v;
    public OpportnityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 600) {
             v=inflater.inflate(R.layout.fragment_opportnity,container, false);

        }
        else
        {
             v=inflater.inflate(R.layout.fragment_opportnity_mob,container, false);

        }
       // View v=inflater.inflate(R.layout.fragment_opportnity,container, false);
        token= Appconfig.TOKEN;
        searchView = (MaterialSearchView)getActivity().findViewById(R.id.search_view);
        setHasOptionsMenu(true);
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Opportunity");
        }
        AppSession.opportunityArrayList=new ArrayList<>();
        AppSession.opportunity_recyclerView=(RecyclerView)v.findViewById(R.id.rv);
         token=Appconfig.TOKEN;
          new OpportunityTask().execute(token);
        if (AppSession.opp_write==0)
        {
            v.findViewById(R.id.fab).setVisibility(View.GONE);
        }
        v.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(), AddOppActivity.class);
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
                ArrayList<Opportunity> subOppArrayList = new ArrayList<>();
                Log.i("ontypecomplete", newText);
                Log.i("list data--", String.valueOf(AppSession.opportunityArrayList.size()));
                for (int i = 0; i < AppSession.opportunityArrayList.size(); i++) {
                    if (AppSession.opportunityArrayList.get(i).getOpportunity().contains(newText)) {
                        Opportunity opportunity = new Opportunity();
                        opportunity.setOpportunity(AppSession.opportunityArrayList.get(i).getOpportunity());
                        opportunity.setNext_action_date(AppSession.opportunityArrayList.get(i).getNext_action_date());
                        opportunity.setCompany(AppSession.opportunityArrayList.get(i).getCompany());
                        opportunity.setSalesTeam(AppSession.opportunityArrayList.get(i).getSalesTeam());
                        subOppArrayList.add(opportunity);
                    }
                    //System.out.println("lead item --"+leadsArrayList.get(i).getName()+" "+leadsArrayList.get(i).getNumber());
                }
                AppSession.opportunity_recyclerView.setAdapter(new OpportunityAdapter(getActivity(), subOppArrayList));
                return false;
            }
        });
                super.onCreateOptionsMenu(menu, inflater);
    }

    public class OpportunityTask extends AsyncTask<String,Void,String>
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
                url = new URL(Appconfig.OPPORTUNITY_URL+tok);
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
//                  Log.i("response--",response);
                JSONObject jsonObject=new JSONObject(response);
                JSONArray jsonArray=jsonObject.getJSONArray("opportunities");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    Opportunity opportunity=new Opportunity();
                    opportunity.setId(object.getInt("id"));
                    opportunity.setOpportunity(object.getString("opportunity"));
                    opportunity.setNext_action_date(object.getString("next_action"));
                    opportunity.setExpected_revenue(object.getString("expected_revenue"));
                    opportunity.setStages(object.getString("stages"));
                    opportunity.setProbability(object.getString("probability"));
                    opportunity.setSalesTeam(object.getString("salesteam"));
                    opportunity.setMeetings(object.getString("meetings"));
                    opportunity.setCompany(object.getString("company"));
                    opportunity.setCalls(object.getString("calls"));
                    AppSession.opportunityArrayList.add(opportunity);

                }

                mAdapter = new OpportunityAdapter(getActivity(),AppSession.opportunityArrayList);
                AppSession.opportunity_recyclerView.setAdapter(mAdapter);
                AppSession.opportunity_recyclerView.setItemAnimator(new DefaultItemAnimator());
                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);

                AppSession.opportunity_recyclerView.setLayoutManager(lmanager);
                AppSession.opportunity_recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),AppSession.opportunity_recyclerView, new RecyclerTouchListener.ClickListener() {
                    @Override

                    public void onClick(View view, int position) {
                        Opportunity opportunity= AppSession.opportunityArrayList.get(position);
                        final int opp_id=opportunity.getId();
                        Intent i=new Intent(getActivity(), DetailOpportunityActivity.class);
                        i.putExtra("opp_id",opp_id);
                        i.putExtra("opp_position",position);
                        Log.i("opp_position", String.valueOf(position));
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
