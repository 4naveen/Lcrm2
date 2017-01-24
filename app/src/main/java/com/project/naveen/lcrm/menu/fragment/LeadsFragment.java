package com.project.naveen.lcrm.menu.fragment;


import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
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
import android.widget.SearchView;

import com.daimajia.swipe.util.Attributes;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.project.naveen.lcrm.AppSession;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.RecyclerTouchListener;
import com.project.naveen.lcrm.adapters.LeadAdapter;
import com.project.naveen.lcrm.addactivity.AddLeadActivity;
import com.project.naveen.lcrm.detailsactivity.DetailsLeadActivity;
import com.project.naveen.lcrm.models.Leads;


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
public class LeadsFragment extends Fragment {

//    ArrayList<Leads> leadsArrayList;
//    RecyclerView rv;
    String token;
    View v;
    LeadAdapter mAdapter;
    MaterialSearchView searchView;
    public LeadsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 600) {
            v=inflater.inflate(R.layout.fragment_leads, container, false);
        }
        else
        {
            v=inflater.inflate(R.layout.fragment_leads_mob, container, false);
        }
        //View v=inflater.inflate(R.layout.fragment_leads, container, false);
        setHasOptionsMenu(true);
//        String lead= getArguments().getString("leads_write");
        if (AppSession.lead_write==1)
        {
            v.findViewById(R.id.fab).setVisibility(View.VISIBLE);
        }
        if (AppSession.lead_write==0)
        {
            v.findViewById(R.id.fab).setVisibility(View.GONE);
        }
        AppSession.leadArrayList =new ArrayList<>();
        token= Appconfig.TOKEN;
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Lead");
        }
        //Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        searchView = (MaterialSearchView)getActivity().findViewById(R.id.search_view);

        AppSession.lead_recyclerView=(RecyclerView)v.findViewById(R.id.rv);
       // final FloatingActionsMenu menuMultipleActions = (FloatingActionsMenu)v.findViewById(R.id.multiple_actions);
        if (AppSession.lead_write==0)
        {
            v.findViewById(R.id.fab).setVisibility(View.GONE);
        }
        v.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),AddLeadActivity.class);
                getActivity().startActivity(i);
            }
        });


        new GetAllLeadTask().execute(token);

        Log.i("token--",""+token);
        return v ;
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
                ArrayList<Leads> subLeadList = new ArrayList<>();
                Log.i("ontypecomplete", newText);
                Log.i("list data--", String.valueOf(AppSession.opportunityArrayList.size()));
                for (int i = 0; i < AppSession.opportunityArrayList.size(); i++) {
                    if (AppSession.leadArrayList.get(i).getOpportunity().contains(newText)) {
                        Leads leads=new Leads();
                        leads.setOpportunity(AppSession.leadArrayList.get(i).getOpportunity());
                        leads.setEmail(AppSession.leadArrayList.get(i).getEmail());
                        leads.setSales_team(AppSession.opportunityArrayList.get(i).getSalesTeam());

                        subLeadList.add(leads);
                    }
                    //System.out.println("lead item --"+leadsArrayList.get(i).getName()+" "+leadsArrayList.get(i).getNumber());
                }
                AppSession.lead_recyclerView.setAdapter(new LeadAdapter(getActivity(), subLeadList));
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }


    class GetAllLeadTask extends AsyncTask<String,Void,String>
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
            URL url;
            HttpURLConnection connection;
            try {
                url = new URL(Appconfig.LEADS_URL+params[0]);
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
            dialog.dismiss();
            try {
                  Log.i("response--",response);
                JSONObject jsonObject=new JSONObject(response);
                JSONArray jsonArray=jsonObject.getJSONArray("leads");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    Leads lead=new Leads();
                    lead.setOpportunity(object.getString("opportunity"));
                    lead.setSales_team(object.getString("salesteam"));
                    lead.setId(object.getInt("id"));
                    lead.setEmail(object.getString("email"));
                    AppSession.leadArrayList.add(lead);
                  //  Log.i("leadslist--",lead.getName());
                }
                mAdapter = new LeadAdapter(getActivity(), AppSession.leadArrayList);

                AppSession.lead_recyclerView.setAdapter(mAdapter);
                AppSession.lead_recyclerView.setItemAnimator(new DefaultItemAnimator());
               // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
                //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);

                AppSession.lead_recyclerView.setLayoutManager(lmanager);

                AppSession.lead_recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),AppSession.lead_recyclerView, new RecyclerTouchListener.ClickListener() {
                    @Override

                    public void onClick(View view, int position) {
                       // Leads leads=leadsArrayList.get(position);
//                        Toast.makeText(getActivity(),"you selected on"+leads.getName(),Toast.LENGTH_LONG).show();
                        Leads leads= AppSession.leadArrayList.get(position);
                        final int lead_id=leads.getId();
                        Intent i=new Intent(getActivity(), DetailsLeadActivity.class);
                        i.putExtra("lead_id",lead_id);
                        i.putExtra("lead_id_position",position);
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
        }}
}
