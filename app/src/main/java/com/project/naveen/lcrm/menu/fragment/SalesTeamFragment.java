package com.project.naveen.lcrm.menu.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
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
import com.project.naveen.lcrm.adapters.SalesTeamAdapter;
import com.project.naveen.lcrm.addactivity.AddSalesTeamActivity;
import com.project.naveen.lcrm.detailsactivity.DetailSalesTeamActivity;
import com.project.naveen.lcrm.models.SalesTeam;

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
public class SalesTeamFragment extends Fragment {

    String token;
    SalesTeamAdapter mAdapter;
    MaterialSearchView searchView;

    public SalesTeamFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_sales_team, container, false);
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
       // String salesteam= getArguments().getString("salesteam_read");

        searchView = (MaterialSearchView)getActivity().findViewById(R.id.search_view);
        AppSession.salesTeamArrayList=new ArrayList<>();
        AppSession.salesTeam_recyclerView=(RecyclerView)v.findViewById(R.id.rv);
        token= Appconfig.TOKEN;

        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("SalesTeam");
        }
        new GetAllSalesTeamTask().execute(token);
        final com.getbase.floatingactionbutton.FloatingActionButton actionA = (com.getbase.floatingactionbutton.FloatingActionButton)v.findViewById(R.id.action_a);
        if (AppSession.salesteam_write==0)
        {
            v.findViewById(R.id.fab).setVisibility(View.GONE);
        }
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(),AddSalesTeamActivity.class);
                getActivity().startActivity(i);
            }
        });
        final com.getbase.floatingactionbutton.FloatingActionButton actionB = (com.getbase.floatingactionbutton.FloatingActionButton)v.findViewById(R.id.action_b);
        actionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                ArrayList<SalesTeam> subSalesTeamArrayList=new ArrayList<>();
                Log.i("ontypecomplete", newText);
                Log.i("list data--", String.valueOf(AppSession.salesTeamArrayList.size()));
                for (int i=0;i<AppSession.salesTeamArrayList.size();i++)
                {
                    if (AppSession.salesTeamArrayList.get(i).getSalesteam().contains(newText))
                    {
                       SalesTeam salesTeam=new SalesTeam();
                        salesTeam.setId(AppSession.salesTeamArrayList.get(i).getId());
                        salesTeam.setSalesteam(AppSession.salesTeamArrayList.get(i).getSalesteam());
                        salesTeam.setTarget(AppSession.salesTeamArrayList.get(i).getTarget());
                        salesTeam.setInvoice_forecast(AppSession.salesTeamArrayList.get(i).getInvoice_forecast());
                        salesTeam.setActual_invoice(AppSession.salesTeamArrayList.get(i).getActual_invoice());
                        subSalesTeamArrayList.add(salesTeam);
                    }
                    //System.out.println("lead item --"+leadsArrayList.get(i).getName()+" "+leadsArrayList.get(i).getNumber());
                }
                AppSession.salesTeam_recyclerView.setAdapter(new SalesTeamAdapter(getActivity(),subSalesTeamArrayList));
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
    class GetAllSalesTeamTask extends AsyncTask<String,Void,String>
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
                url = new URL(Appconfig.SALESTEAM_URL+params[0]);
                connection = (HttpURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String temp;
                while ((temp=br.readLine())!=null)
                {
                    buffer.append(temp);
                }
                response=buffer.toString();


                  Log.i("response in d",response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
        @Override
        protected void onPostExecute(String response) {
            dialog.dismiss();
            try {
                //  Log.i("response--",response);
                JSONObject jsonObject=new JSONObject(response);
                JSONArray jsonArray=jsonObject.getJSONArray("salesteams");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    SalesTeam salesTeam=new SalesTeam();
                    salesTeam.setId(object.getInt("id"));
                    salesTeam.setSalesteam(object.getString("salesteam"));
                    salesTeam.setTarget(object.getString("target"));
                    salesTeam.setInvoice_forecast(object.getString("invoice_forecast"));
                    salesTeam.setActual_invoice(object.getString("actual_invoice"));
                    AppSession.salesTeamArrayList.add(salesTeam);
                    //  Log.i("leadslist--",lead.getName());
                }
                mAdapter=new SalesTeamAdapter(getActivity(),AppSession.salesTeamArrayList);
                AppSession.salesTeam_recyclerView.setAdapter(mAdapter);
                AppSession.salesTeam_recyclerView.setItemAnimator(new DefaultItemAnimator());
                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
                //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);

                AppSession.salesTeam_recyclerView.setLayoutManager(lmanager);

                AppSession.salesTeam_recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),AppSession.salesTeam_recyclerView, new RecyclerTouchListener.ClickListener() {
                    @Override

                    public void onClick(View view, int position) {
                        SalesTeam salesTeam=AppSession.salesTeamArrayList.get(position);
                        int sales_team_id=salesTeam.getId();
                        Intent i=new Intent(getActivity(), DetailSalesTeamActivity.class);
                        i.putExtra("sales_team_id",String.valueOf(sales_team_id));
                        i.putExtra("sales_team_id_position",position);
                        Log.i("sales_team_id_position", String.valueOf(position));
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
