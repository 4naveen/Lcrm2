package com.project.naveen.lcrm.menu.fragment.invoicefrag;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.project.naveen.lcrm.ChartFragment;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.RecyclerTouchListener;
import com.project.naveen.lcrm.adapters.Invoice2Adapter;
import com.project.naveen.lcrm.adapters.InvoicesAdapter;
import com.project.naveen.lcrm.addactivity.AddInvoicesActivity;
import com.project.naveen.lcrm.detailsactivity.InvoicesDetailsActivity;
import com.project.naveen.lcrm.models.Invoice;

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
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class InvoicesFragment extends ChartFragment {
    RecyclerView recyclerView1;
//    RecyclerView recyclerView;
    PieChart mPieChart;
    MaterialSearchView searchView;
    View v;
//    ArrayList<Invoice> invoiceArrayList;
    public InvoicesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 600) {
            v=inflater.inflate(R.layout.fragment_invoices, container, false);
        }
        else
        {
            v=inflater.inflate(R.layout.fragment_invoices_mob, container, false);
        }
//        View v=inflater.inflate(R.layout.fragment_invoices, container, false);
        setHasOptionsMenu(true);
        searchView = (MaterialSearchView)getActivity().findViewById(R.id.search_view);

        mPieChart = (PieChart) v.findViewById(R.id.piechart);
        loadData();
        recyclerView1=(RecyclerView)v.findViewById(R.id.rv1);
        recyclerView1.setAdapter(new InvoicesAdapter());
        recyclerView1.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager layoutManager=new GridLayoutManager(getActivity(),2);
        AppSession.invoicesArrayList=new ArrayList<>();
        recyclerView1.setLayoutManager(layoutManager);
      AppSession.invoices_recyclerView=(RecyclerView)v.findViewById(R.id.rv);
      new GetAllInvoices().execute(Appconfig.TOKEN);
        if (AppSession.invoices_write==0)
        {
            v.findViewById(R.id.fab).setVisibility(View.GONE);
        }
        v.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(), AddInvoicesActivity.class);
                getActivity().startActivity(i);

            }
        });
        return v;

    }


    private void loadData() {
        mPieChart.addPieSlice(new PieModel("Open invoice",25, Color.parseColor("#FE6DA8")));
        mPieChart.addPieSlice(new PieModel("Overdue invoice", 35, Color.parseColor("#56B7F1")));
        mPieChart.addPieSlice(new PieModel("Paid invoice", 40, Color.parseColor("#CDA67F")));
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
                ArrayList<Invoice> subInvoiceArrayList=new ArrayList<>();
                Log.i("ontypecomplete", newText);
                Log.i("list data--", String.valueOf(AppSession.invoicesArrayList.size()));
                for (int i=0;i<AppSession.invoicesArrayList.size();i++)
                {
                    if (AppSession.invoicesArrayList.get(i).getCustomer().contains(newText))
                    {
                         Invoice invoice=new Invoice();
                        invoice.setCustomer(AppSession.invoicesArrayList.get(i).getCustomer());
                        invoice.setInvoice_date(AppSession.invoicesArrayList.get(i).getInvoice_date());
                        invoice.setStatus(AppSession.invoicesArrayList.get(i).getStatus());

                        subInvoiceArrayList.add(invoice);
                    }
                    //System.out.println("lead item --"+leadsArrayList.get(i).getName()+" "+leadsArrayList.get(i).getNumber());
                }
                AppSession.invoices_recyclerView.setAdapter(new Invoice2Adapter(subInvoiceArrayList, getActivity()));
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
    public class GetAllInvoices extends AsyncTask<String,Void,String>
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
                url = new URL(Appconfig.INVOICES_URL+tok);
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
                JSONArray jsonArray=jsonObject.getJSONArray("invoices");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    Invoice invoice=new Invoice();
                    invoice.setId(object.getInt("id"));
                    invoice.setCustomer(object.getString("customer"));
                    invoice.setStatus(object.getString("status"));
                    invoice.setInvoice_number(object.getString("invoice_number"));
                    invoice.setInvoice_date(object.getString("invoice_date"));

                    AppSession.invoicesArrayList.add(invoice);

                }

                AppSession.invoices_recyclerView.setAdapter(new Invoice2Adapter(AppSession.invoicesArrayList,getActivity()));
                AppSession.invoices_recyclerView.setItemAnimator(new DefaultItemAnimator());
                RecyclerView.LayoutManager layoutManager1=new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
                AppSession.invoices_recyclerView.setLayoutManager(layoutManager1);
                AppSession.invoices_recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),AppSession.invoices_recyclerView, new RecyclerTouchListener.ClickListener() {
                    @Override

                    public void onClick(View view, int position) {
                        Invoice invoice= AppSession.invoicesArrayList.get(position);
                        final int invoice_id=invoice.getId();
                        Intent i=new Intent(getActivity(), InvoicesDetailsActivity.class);
                        i.putExtra("invoice_id",invoice_id);
                        i.putExtra("invoice_id_position",position);
                        Log.i("invoice_id_position", String.valueOf(position));
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
