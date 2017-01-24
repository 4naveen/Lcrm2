package com.project.naveen.lcrm.menu.fragment.invoicefrag;


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
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.RecyclerTouchListener;
import com.project.naveen.lcrm.adapters.PaymentAdapter;
import com.project.naveen.lcrm.addactivity.AddPaymentActivity;
import com.project.naveen.lcrm.detailsactivity.DetailPaymentActivity;
import com.project.naveen.lcrm.models.Payment;

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
public class PaymentFragment extends Fragment {

    ArrayList<Payment> paymentArrayList;
    RecyclerView rv;
    String token;
    MaterialSearchView searchView;
    PaymentAdapter mAdapter;
    public PaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_payment, container, false);
        setHasOptionsMenu(true);
        searchView = (MaterialSearchView)getActivity().findViewById(R.id.search_view);

        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Payment Log");
        }
        rv=(RecyclerView)v.findViewById(R.id.rv);
        paymentArrayList=new ArrayList<>();


       new GetAllPaymentLog().execute(Appconfig.TOKEN);
        v.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(), AddPaymentActivity.class);
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
                ArrayList<Payment> subStaffArrayList=new ArrayList<>();
                Log.i("ontypecomplete", newText);
                Log.i("list data--", String.valueOf(paymentArrayList.size()));
                for (int i=0;i<paymentArrayList.size();i++)
                {
                    if (paymentArrayList.get(i).getPay_number().contains(newText))
                    {
                       Payment payment=new Payment();
                        payment.setCustomer(paymentArrayList.get(i).getCustomer());
                        payment.setPay_date(paymentArrayList.get(i).getPay_date());
                        payment.setPay_method(paymentArrayList.get(i).getPay_method());
                        payment.setPay_received(paymentArrayList.get(i).getPay_received());
                        payment.setPay_number(paymentArrayList.get(i).getPay_number());
                        payment.setPerson(paymentArrayList.get(i).getPerson());
                        subStaffArrayList.add(payment);
                    }
                    //System.out.println("lead item --"+leadsArrayList.get(i).getName()+" "+leadsArrayList.get(i).getNumber());
                }
                rv.setAdapter(new PaymentAdapter(getActivity(),subStaffArrayList));
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    class GetAllPaymentLog extends AsyncTask<String,Void,String>
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
            URL url = null;
            HttpURLConnection connection = null ;
            String tok=params[0];
            try {
                url = new URL(Appconfig.PAYLOG_URL+tok);
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
                JSONArray jsonArray=jsonObject.getJSONArray("invoice_payments");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    Payment payment=new Payment();
                    payment.setId(object.getInt("id"));
                    payment.setPay_number(object.getString("payment_number"));
                    payment.setPay_date(object.getString("payment_date"));
                    payment.setPay_method(object.getString("payment_method"));
                    payment.setPay_received(object.getString("payment_received"));
                    payment.setPerson(object.getString("salesperson"));
                    payment.setCustomer(object.getString("customer"));
                    paymentArrayList.add(payment);

                    //  Log.i("leadslist--",lead.getName());
                }
                rv.setAdapter(new PaymentAdapter(getActivity(), paymentArrayList));
                rv.setItemAnimator(new DefaultItemAnimator());
                RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
                rv.setLayoutManager(layoutManager);
                rv.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),rv, new RecyclerTouchListener.ClickListener() {
                    @Override

                    public void onClick(View view, int position) {
                        Intent i=new Intent(getActivity(), DetailPaymentActivity.class);
                        Payment payment= paymentArrayList.get(position);
                        final int pay_id=payment.getId();
                        i.putExtra("pay_id",String.valueOf(pay_id));
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
