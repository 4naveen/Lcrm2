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
import com.project.naveen.lcrm.adapters.ContractAdapter;
import com.project.naveen.lcrm.adapters.OpportunityAdapter;
import com.project.naveen.lcrm.addactivity.AddContractActivity;
import com.project.naveen.lcrm.detailsactivity.ContractDetailActivity;
import com.project.naveen.lcrm.detailsactivity.DetailOpportunityActivity;
import com.project.naveen.lcrm.models.Contracts;
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
public class ContractsFragment extends Fragment {
    MaterialSearchView searchView;
    public ContractsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_contracts, container, false);
        AppSession.contractArrayList=new ArrayList<>();
        setHasOptionsMenu(true);
        searchView=(MaterialSearchView)getActivity().findViewById(R.id.search_view);
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Contract");
        }

        AppSession.contract_recyclerView=(RecyclerView)v.findViewById(R.id.rv);
             new GetAllContract().execute(Appconfig.TOKEN);
        if (AppSession.contracts_write==0)
        {
            v.findViewById(R.id.fab).setVisibility(View.GONE);
        }
        v.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(), AddContractActivity.class);
                getActivity().startActivity(i);
         /*       i.setAction(Intent.ACTION_SEND);
                i.putExtra(Intent.EXTRA_EMAIL,"2navkumar@gmail.com");
                i.putExtra(Intent.EXTRA_SUBJECT,"greeting");
                i.putExtra(Intent.EXTRA_TEXT,"good evening");
                i.setType("message/rfc882");
                startActivity(i.createChooser(i,"select email app"));*/


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
                ArrayList<Contracts> subContractsArrayList=new ArrayList<>();
                Log.i("ontypecomplete", newText);
                Log.i("list data--", String.valueOf(AppSession.contractArrayList.size()));
                for (int i=0;i<AppSession.contractArrayList.size();i++)
                {
                    if (AppSession.contractArrayList.get(i).getContact().contains(newText))
                    {
                        Contracts contracts=new Contracts();
                        contracts.setContact(AppSession.contractArrayList.get(i).getContact());
                        contracts.setResponsible(AppSession.contractArrayList.get(i).getResponsible());
                        contracts.setStart_date(AppSession.contractArrayList.get(i).getStart_date());
                        contracts.setDescription(AppSession.contractArrayList.get(i).getDescription());
                        subContractsArrayList.add(contracts);
                    }
                    //System.out.println("lead item --"+leadsArrayList.get(i).getName()+" "+leadsArrayList.get(i).getNumber());
                }
                AppSession.contract_recyclerView.setAdapter(new ContractAdapter(subContractsArrayList, getActivity()));
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    public class GetAllContract extends AsyncTask<String,Void,String>
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
                url = new URL(Appconfig.CONTRACT_URL+tok);
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
                JSONArray jsonArray=jsonObject.getJSONArray("contracts");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    Contracts  contracts=new Contracts();
                    contracts.setId(object.getInt("id"));
                    contracts.setStart_date(object.getString("start_date"));
                    contracts.setResponsible(object.getString("user"));
                    contracts.setDescription(object.getString("description"));
                    contracts.setContact(object.getString("name"));

                    AppSession.contractArrayList.add(contracts);

                }

                AppSession.contract_recyclerView.setAdapter(new ContractAdapter(AppSession.contractArrayList, getActivity()));
                AppSession.contract_recyclerView.setItemAnimator(new DefaultItemAnimator());
                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
                //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);
                AppSession.contract_recyclerView.setLayoutManager(lmanager);
                AppSession.contract_recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),AppSession.contract_recyclerView, new RecyclerTouchListener.ClickListener() {
                    @Override

                    public void onClick(View view, int position) {
                        Contracts contracts = AppSession.contractArrayList.get(position);
                        final int contract_id=contracts.getId();
                        Intent i=new Intent(getActivity(), ContractDetailActivity.class);
                        i.putExtra("contract_id",String.valueOf(contract_id));
                        i.putExtra("contract_id_position",position);
                        Log.i("contract_position", String.valueOf(position));
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
