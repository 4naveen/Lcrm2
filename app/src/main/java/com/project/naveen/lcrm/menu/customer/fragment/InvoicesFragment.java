package com.project.naveen.lcrm.menu.customer.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.MyVolleySingleton;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.RecyclerTouchListener;
import com.project.naveen.lcrm.adapters.customers.InvoiceAdapter;
import com.project.naveen.lcrm.adapters.customers.SalesOrderAdapter;
import com.project.naveen.lcrm.customer.detailactivity.InvoiceDetailActivity;
import com.project.naveen.lcrm.models.customer.Invoice;
import com.project.naveen.lcrm.models.customer.SalesOrder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class InvoicesFragment extends Fragment {

    ArrayList<Invoice> invoiceArrayList;
    RecyclerView rv;
    String token;
    InvoiceAdapter mAdapter;
    MaterialSearchView searchView;
    ProgressDialog progressDialog;

    public InvoicesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v=inflater.inflate(R.layout.fragment_invoices2, container, false);
        setHasOptionsMenu(true);
        invoiceArrayList =new ArrayList<>();
        token= Appconfig.TOKEN;
        searchView = (MaterialSearchView)getActivity().findViewById(R.id.search_view);

        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Invoices");
        }
        rv=(RecyclerView)v.findViewById(R.id.rv);

         getInvoicesList(token);

        return v;
    }
    public void getInvoicesList(String token)
    {  progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading, please wait");
        progressDialog.setTitle("Connecting server");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Appconfig.CUSTOMER_INVOICES_URL+token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            Log.i("response--", String.valueOf(response));
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("invoices");
                            for (int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject object=jsonArray.getJSONObject(i);
                                Invoice invoice=new Invoice();
                                invoice.setId(object.getInt("id"));
                                invoice.setCustomer(object.getString("customer"));
                                invoice.setStatus(object.getString("status"));
                                invoice.setDue_date(object.getString("due_date"));
                                invoice.setgTotal(object.getString("grand_total"));
                                invoice.setInvoice_date(object.getString("invoice_date"));
                                invoice.setInvoice_number(object.getString("invoice_number"));
                                invoice.setUnpaid_amount(object.getString("unpaid_amount"));
                                invoiceArrayList.add(invoice);
                            }

                            mAdapter=new InvoiceAdapter(getActivity(),invoiceArrayList);

                            rv.setAdapter(mAdapter);
                            rv.setItemAnimator(new DefaultItemAnimator());
                            // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                            RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
                            //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);
                            rv.setLayoutManager(lmanager);

                            rv.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),rv, new RecyclerTouchListener.ClickListener() {
                                @Override

                                public void onClick(View view, int position) {
                                    Invoice invoice= invoiceArrayList.get(position);
                                    final int invoiceId=invoice.getId();
                                    Intent i=new Intent(getActivity(), InvoiceDetailActivity.class);
                                    i.putExtra("invoice_id",String.valueOf(invoiceId));
                                    getActivity().startActivity(i);
                                }

                                @Override
                                public void onLongClick(View view, int position) {

                                }
                            }));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progressDialog.dismiss();

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

    }

}
