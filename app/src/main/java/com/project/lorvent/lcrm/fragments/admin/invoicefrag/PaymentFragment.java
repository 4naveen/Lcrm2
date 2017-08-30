package com.project.lorvent.lcrm.fragments.admin.invoicefrag;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.crashlytics.android.Crashlytics;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.utils.NetworkStatus;
import com.project.lorvent.lcrm.utils.RecyclerTouchListener;
import com.project.lorvent.lcrm.adapters.PaymentAdapter;
import com.project.lorvent.lcrm.activities.add.AddPaymentActivity;
import com.project.lorvent.lcrm.activities.details.admin.DetailPaymentActivity;
import com.project.lorvent.lcrm.models.Payment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentFragment extends Fragment {

    ArrayList<Payment> paymentArrayList;
    RecyclerView rv;
    MaterialSearchView searchView;
    ProgressDialog dialog;
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
        rv=(RecyclerView)v.findViewById(R.id.rv);
        paymentArrayList=new ArrayList<>();
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
           new GetAllPaymentLog().execute(Appconfig.TOKEN);
        }


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
            String tok=params[0];
            try {
                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String get_url;
                if (text_url != null) {
                    get_url = text_url + "/user/invoice_payments?token=";
                } else {
                    get_url = Appconfig.PAYLOG_URL;
                }
                url = new URL(get_url+tok);
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
                Crashlytics.logException(e);

            }
            return response;
        }
        @Override
        protected void onPostExecute(String response) {
            if (dialog!=null&&dialog.isShowing()){dialog.dismiss();}
            try {
                  //Log.i("response--",response);
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
                Crashlytics.logException(e);

            }


        }

    }

    @Override
    public void onPause() {
        super.onPause();

        if ((dialog != null) && dialog.isShowing())
            dialog.dismiss();
        dialog = null;
    }
}
