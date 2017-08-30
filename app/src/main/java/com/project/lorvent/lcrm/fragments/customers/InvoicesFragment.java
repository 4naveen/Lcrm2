package com.project.lorvent.lcrm.fragments.customers;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.MyVolleySingleton;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.utils.NetworkStatus;
import com.project.lorvent.lcrm.adapters.customers.InvoiceAdapter;
import com.project.lorvent.lcrm.models.customer.Invoice;

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
    View v;
    public InvoicesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_invoices2, container, false);
        setHasOptionsMenu(true);
        invoiceArrayList =new ArrayList<>();
        token= Appconfig.TOKEN;
        searchView = (MaterialSearchView)getActivity().findViewById(R.id.search_view);

        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Invoices");
        }
        rv=(RecyclerView)v.findViewById(R.id.rv);
        if (!NetworkStatus.isConnected(getActivity())){
            new MaterialDialog.Builder(getActivity())
                    .title("Please check your internet Connectivity !")
                    .titleColor(Color.RED)
                    .positiveText("Ok")
                    .positiveColorRes(R.color.colorPrimary)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        }
                    })
                    .negativeColorRes(R.color.colorPrimary)
                    .negativeText("")
                    .show();

        }

        if (NetworkStatus.isConnected(getActivity())){
            getInvoicesList(token);
        }
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
                ArrayList<Invoice> subInvoiceArrayList=new ArrayList<>();

                for (int i=0;i<invoiceArrayList.size();i++)
                {
                    if (invoiceArrayList.get(i).getInvoice_date().contains(newText))
                    {
         /*               Quotation quotation=new Quotation();
                        quotation.setCustomer(quotationArrayList.get(i).getCustomer());
                        quotation.setPerson(quotationArrayList.get(i).getPerson());
                        quotation.setQuotations_number(quotationArrayList.get(i).getQuotations_number());
                        quotation.setDate(quotationArrayList.get(i).getDate());
                        quotation.setFinal_price(quotationArrayList.get(i).getFinal_price());*/

                        subInvoiceArrayList.add(invoiceArrayList.get(i));
                    }
                    //System.out.println("lead item --"+leadsArrayList.get(i).getName()+" "+leadsArrayList.get(i).getNumber());
                }
                rv.setAdapter(new InvoiceAdapter(getActivity(),subInvoiceArrayList));
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
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

                          /*  rv.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),rv, new RecyclerTouchListener.ClickListener() {
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
                            }));*/
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Crashlytics.logException(e);
                        }

                        if (progressDialog!=null&&progressDialog.isShowing()){progressDialog.dismiss();}

                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
    @Override
    public void onPause() {
        super.onPause();

        if ((progressDialog != null) && progressDialog.isShowing())
            progressDialog.dismiss();
        progressDialog= null;
    }
    @Override
    public void onResume() {

        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        }
        super.onResume();
    }
}
