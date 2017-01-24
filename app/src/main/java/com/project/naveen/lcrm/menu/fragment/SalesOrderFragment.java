package com.project.naveen.lcrm.menu.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
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
import com.project.naveen.lcrm.adapters.LeadAdapter;
import com.project.naveen.lcrm.adapters.SalesOrderAdapter;
import com.project.naveen.lcrm.addactivity.AddSalesOrderActivity;
import com.project.naveen.lcrm.detailsactivity.DetailsLeadActivity;
import com.project.naveen.lcrm.detailsactivity.SalesOrderDetailsActivity;
import com.project.naveen.lcrm.models.Leads;
import com.project.naveen.lcrm.models.SalesOrder;

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
public class SalesOrderFragment extends Fragment {
   // ArrayList<SalesOrder> salesOrderArrayList;
    //RecyclerView recyclerView;
    MaterialSearchView searchView;
    public SalesOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_sales_order, container, false);
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        setHasOptionsMenu(true);
        searchView=(MaterialSearchView)getActivity().findViewById(R.id.search_view);
        if (actionBar != null) {
            actionBar.setTitle("SalesOrder");
        }
        AppSession.salesOrder_recyclerView=(RecyclerView)v.findViewById(R.id.rv);

        AppSession.salesOrderArrayList=new ArrayList<SalesOrder>();

        new GetAllSalesOrder().execute(Appconfig.TOKEN);
        if (AppSession.salesorder_write==0)
        {
            v.findViewById(R.id.fab).setVisibility(View.GONE);
        }
        v.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(), AddSalesOrderActivity.class);
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
                ArrayList<SalesOrder> subSalesOrderArrayList=new ArrayList<>();
                Log.i("ontypecomplete", newText);
                Log.i("list data--", String.valueOf(AppSession.salesOrderArrayList.size()));
                for (int i=0;i<AppSession.salesOrderArrayList.size();i++)
                {
                    if (AppSession.salesOrderArrayList.get(i).getCustomer().contains(newText))
                    {
                        SalesOrder salesOrder=new SalesOrder();
                        salesOrder.setCustomer(AppSession.salesOrderArrayList.get(i).getCustomer());
                        salesOrder.setSalesPerson(AppSession.salesOrderArrayList.get(i).getSalesPerson());
                        salesOrder.setFinal_price(AppSession.salesOrderArrayList.get(i).getFinal_price());
                        salesOrder.setDate(AppSession.salesOrderArrayList.get(i).getDate());
                        salesOrder.setStatus(AppSession.salesOrderArrayList.get(i).getStatus());
                        subSalesOrderArrayList.add(salesOrder);
                    }
                    //System.out.println("lead item --"+leadsArrayList.get(i).getName()+" "+leadsArrayList.get(i).getNumber());
                }
                AppSession.salesOrder_recyclerView.setAdapter(new SalesOrderAdapter(getActivity(),subSalesOrderArrayList));
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }


    class GetAllSalesOrder extends AsyncTask<String,Void,String>
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
                url = new URL(Appconfig.SALESORDER_URL+params[0]);
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
                JSONArray jsonArray=jsonObject.getJSONArray("salesorders");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    SalesOrder salesOrder=new SalesOrder();
                    salesOrder.setCustomer(object.getString("customer"));
                    salesOrder.setSalesPerson(object.getString("person"));
                    salesOrder.setId(object.getInt("id"));
                    salesOrder.setFinal_price(object.getString("final_price"));
                    AppSession.salesOrderArrayList.add(salesOrder);
                    //  Log.i("leadslist--",lead.getName());
                }
                AppSession.salesOrder_recyclerView.setAdapter(new SalesOrderAdapter(getActivity(), AppSession.salesOrderArrayList));
                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
                AppSession.salesOrder_recyclerView.setLayoutManager(lmanager);
                AppSession.salesOrder_recyclerView.setItemAnimator(new DefaultItemAnimator());                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));


                AppSession.salesOrder_recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),AppSession.salesOrder_recyclerView, new RecyclerTouchListener.ClickListener() {
                    @Override

                    public void onClick(View view, int position) {
                        SalesOrder salesOrder= AppSession.salesOrderArrayList.get(position);
                        final int sales_order_id=salesOrder.getId();
                        Intent i=new Intent(getActivity(), SalesOrderDetailsActivity.class);
                        i.putExtra("sales_order_id",String.valueOf(sales_order_id));
                        i.putExtra("sales_order_id_position",position);
                        Log.i("sales_order_id_position", String.valueOf(position));
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
