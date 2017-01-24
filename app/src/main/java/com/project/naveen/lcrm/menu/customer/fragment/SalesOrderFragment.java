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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.project.naveen.lcrm.AppSession;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.MyVolleySingleton;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.RecyclerTouchListener;
import com.project.naveen.lcrm.adapters.customers.SalesOrderAdapter;
import com.project.naveen.lcrm.customer.detailactivity.SalesOrederDetailActivity;
import com.project.naveen.lcrm.detailsactivity.SalesOrderDetailsActivity;
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
public class SalesOrderFragment extends Fragment {
    ArrayList<SalesOrder> salesOrderArrayList;
    RecyclerView rv;
    String token;
    SalesOrderAdapter mAdapter;
    MaterialSearchView searchView;
    ProgressDialog progressDialog;

    public SalesOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_sales_order2, container, false);

        setHasOptionsMenu(true);
        salesOrderArrayList =new ArrayList<>();
        token= Appconfig.TOKEN;
        searchView = (MaterialSearchView)getActivity().findViewById(R.id.search_view);

        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("SalesOrder");
        }
        rv=(RecyclerView)v.findViewById(R.id.rv);

           getSalesOrderList(token);
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
                Log.i("ontypecomplete", newText);
                Log.i("list data--", String.valueOf(salesOrderArrayList.size()));
                for (int i=0;i<salesOrderArrayList.size();i++)
                {
                    if (salesOrderArrayList.get(i).getCustomer().contains(newText))
                    {
                        SalesOrder salesOrder=new SalesOrder();
                        salesOrder.setCustomer(salesOrderArrayList.get(i).getCustomer());
                        salesOrder.setPerson(salesOrderArrayList.get(i).getPerson());
                        salesOrder.setGrand_total(salesOrderArrayList.get(i).getGrand_total());
                        salesOrder.setSales_number(salesOrderArrayList.get(i).getSales_number());
                        salesOrder.setDate(salesOrderArrayList.get(i).getDate());
                        salesOrder.setStatus(salesOrderArrayList.get(i).getStatus());
                        subQuotationArrayList.add(salesOrder);
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
                        Log.i("response--", String.valueOf(response));
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

                        rv.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),rv, new RecyclerTouchListener.ClickListener() {
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
