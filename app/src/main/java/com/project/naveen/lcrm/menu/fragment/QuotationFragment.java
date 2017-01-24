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

import com.daimajia.swipe.util.Attributes;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.project.naveen.lcrm.AppSession;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.RecyclerTouchListener;
import com.project.naveen.lcrm.adapters.QuotationAdapter;
import com.project.naveen.lcrm.addactivity.AddQuotationActivity;
import com.project.naveen.lcrm.detailsactivity.QuotationDetailsActivity;
import com.project.naveen.lcrm.models.Quotation;

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
public class QuotationFragment extends Fragment {
   // RecyclerView rv;
    String token;
    QuotationAdapter mAdapter;
    MaterialSearchView searchView;
    public QuotationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_quotation, container, false);
        setHasOptionsMenu(true);
        AppSession.quotationArrayList =new ArrayList<>();
        token= Appconfig.TOKEN;
        searchView = (MaterialSearchView)getActivity().findViewById(R.id.search_view);

        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Quotation");
        }
        AppSession.quotation_recyclerView=(RecyclerView)v.findViewById(R.id.rv);
        if (AppSession.quotation_write==0)
        {
            v.findViewById(R.id.fab).setVisibility(View.GONE);
        }
        v.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(), AddQuotationActivity.class);
                getActivity().startActivity(i);

            }
        });

        new GetAllQuotationTask().execute(token);
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
                ArrayList<Quotation> subQuotationArrayList=new ArrayList<>();
                Log.i("ontypecomplete", newText);
                Log.i("list data--", String.valueOf( AppSession.quotationArrayList.size()));
                for (int i=0;i< AppSession.quotationArrayList.size();i++)
                {
                    if ( AppSession.quotationArrayList.get(i).getCustomer().contains(newText))
                    {
                        Quotation quotation=new Quotation();
                        quotation.setCustomer( AppSession.quotationArrayList.get(i).getCustomer());
                        quotation.setPerson( AppSession.quotationArrayList.get(i).getPerson());
                        quotation.setId( AppSession.quotationArrayList.get(i).getId());
                        quotation.setDate( AppSession.quotationArrayList.get(i).getDate());
                        quotation.setFinal_price( AppSession.quotationArrayList.get(i).getFinal_price());

                        subQuotationArrayList.add(quotation);
                    }
                    //System.out.println("lead item --"+leadsArrayList.get(i).getName()+" "+leadsArrayList.get(i).getNumber());
                }
                AppSession.quotation_recyclerView.setAdapter(new QuotationAdapter(getActivity(),subQuotationArrayList));
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
    class GetAllQuotationTask extends AsyncTask<String,Void,String>
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
                url = new URL(Appconfig.QUOTATION_URL+params[0]);
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
                JSONArray jsonArray=jsonObject.getJSONArray("quotations");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    Quotation quotation=new Quotation();
                    quotation.setCustomer(object.getString("customer"));
                    quotation.setPerson(object.getString("person"));
                    quotation.setId(object.getInt("id"));
                    quotation.setDate(object.getString("date"));
                    quotation.setFinal_price(object.getString("final_price"));
                    AppSession.quotationArrayList.add(quotation);
                    //  Log.i("leadslist--",lead.getName());
                }

                mAdapter=new QuotationAdapter(getActivity(), AppSession.quotationArrayList);

                AppSession.quotation_recyclerView.setAdapter(mAdapter);
                AppSession.quotation_recyclerView.setItemAnimator(new DefaultItemAnimator());
                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
                //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);

                AppSession.quotation_recyclerView.setLayoutManager(lmanager);

                AppSession.quotation_recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), AppSession.quotation_recyclerView, new RecyclerTouchListener.ClickListener() {
                    @Override

                    public void onClick(View view, int position) {
                        // Leads leads=leadsArrayList.get(position);
//                        Toast.makeText(getActivity(),"you selected on"+leads.getName(),Toast.LENGTH_LONG).show();
                        Quotation quotation= AppSession.quotationArrayList.get(position);
                        final int quotationId=quotation.getId();
                        Intent i=new Intent(getActivity(), QuotationDetailsActivity.class);
                        i.putExtra("quotationId",String.valueOf(quotationId));
                        i.putExtra("quotationIdPosition",position);
                        Log.i("quotationIdPosition", String.valueOf(position));
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
