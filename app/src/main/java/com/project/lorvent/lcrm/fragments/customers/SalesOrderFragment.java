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
import com.project.lorvent.lcrm.adapters.customers.SalesOrderAdapter;
import com.project.lorvent.lcrm.models.customer.SalesOrder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SalesOrderFragment extends Fragment {
    ArrayList<SalesOrder> salesOrderArrayList;
    RecyclerView rv;
    String token;
    SalesOrderAdapter mAdapter;
    MaterialSearchView searchView;
    ProgressDialog progressDialog;
    View v;
    public SalesOrderFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_sales_order2, container, false);
        setHasOptionsMenu(true);
        salesOrderArrayList =new ArrayList<>();
        token= Appconfig.TOKEN;
        searchView = (MaterialSearchView)getActivity().findViewById(R.id.search_view);

        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("SalesOrder");
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
            getSalesOrderList(token);
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
                ArrayList<SalesOrder> subQuotationArrayList=new ArrayList<>();

                for (int i=0;i<salesOrderArrayList.size();i++)
                {
                    if (salesOrderArrayList.get(i).getPerson().contains(newText))
                    {
                        /*SalesOrder salesOrder=new SalesOrder();
                        salesOrder.setCustomer(salesOrderArrayList.get(i).getCustomer());
                        salesOrder.setPerson(salesOrderArrayList.get(i).getPerson());
                        salesOrder.setGrand_total(salesOrderArrayList.get(i).getGrand_total());
                        salesOrder.setSales_number(salesOrderArrayList.get(i).getSales_number());
                        salesOrder.setDate(salesOrderArrayList.get(i).getDate());
                        salesOrder.setStatus(salesOrderArrayList.get(i).getStatus());*/
                        subQuotationArrayList.add(salesOrderArrayList.get(i));
                    }
                    //System.out.println("lead item --"+leadsArrayList.get(i).getName()+" "+leadsArrayList.get(i).getNumber());
                }
                rv.setAdapter(new SalesOrderAdapter(getActivity(),subQuotationArrayList));
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }


    public void getSalesOrderList(String token)
    {  progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading, please wait");
        progressDialog.setTitle("Connecting server");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Appconfig.CUSTOMER_SALESORDER_URL+token,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        JSONArray jsonArray=jsonObject.getJSONArray("salesorder");
                        for (int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject object=jsonArray.getJSONObject(i);
                            SalesOrder salesOrder=new SalesOrder();
                            salesOrder.setId(object.getInt("id"));
                            salesOrder.setCustomer(object.getString("customer"));
                            salesOrder.setSales_number(object.getString("sale_number"));
                            salesOrder.setDate(object.getString("date"));
                            salesOrder.setGrand_total(object.getString("grand_total"));
                            salesOrder.setPerson(object.getString("person"));
                            salesOrder.setStatus(object.getString("status"));
                            salesOrderArrayList.add(salesOrder);
                        }

                        mAdapter=new SalesOrderAdapter(getActivity(),salesOrderArrayList);

                        rv.setAdapter(mAdapter);
                        rv.setItemAnimator(new DefaultItemAnimator());
                        // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                        RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
                        //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);
                        rv.setLayoutManager(lmanager);

                       /* rv.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),rv, new RecyclerTouchListener.ClickListener() {
                            @Override

                            public void onClick(View view, int position) {
                                SalesOrder salesOrder= salesOrderArrayList.get(position);
                                final int sales_order_id=salesOrder.getId();
                                Intent i=new Intent(getActivity(),SalesOrederDetailActivity.class);
                                i.putExtra("sales_order_id",String.valueOf(sales_order_id));
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
