package com.project.lorvent.lcrm.fragments.customers;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
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
import com.crashlytics.android.Crashlytics;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.utils.NetworkStatus;
import com.project.lorvent.lcrm.adapters.customers.QuotationAdapter;
import com.project.lorvent.lcrm.models.customer.Quotation;

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
public class QuoatationFragment extends Fragment {

    ArrayList<Quotation> quotationArrayList;
    RecyclerView rv;
    String token;
    QuotationAdapter mAdapter;
    View v;
    MaterialSearchView searchView;
    ProgressDialog dialog;

    public QuoatationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        v=inflater.inflate(R.layout.fragment_quoatation, container, false);

        setHasOptionsMenu(true);
        quotationArrayList =new ArrayList<>();
        token= Appconfig.TOKEN;
        searchView = (MaterialSearchView)getActivity().findViewById(R.id.search_view);

        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Quotation");
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
            new GetAllQuotationTask().execute(token);
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
                ArrayList<Quotation> subQuotationArrayList=new ArrayList<>();

                for (int i=0;i<quotationArrayList.size();i++)
                {
                    if (quotationArrayList.get(i).getPerson().contains(newText))
                    {
         /*               Quotation quotation=new Quotation();
                        quotation.setCustomer(quotationArrayList.get(i).getCustomer());
                        quotation.setPerson(quotationArrayList.get(i).getPerson());
                        quotation.setQuotations_number(quotationArrayList.get(i).getQuotations_number());
                        quotation.setDate(quotationArrayList.get(i).getDate());
                        quotation.setFinal_price(quotationArrayList.get(i).getFinal_price());*/

                        subQuotationArrayList.add(quotationArrayList.get(i));
                    }
                    //System.out.println("lead item --"+leadsArrayList.get(i).getName()+" "+leadsArrayList.get(i).getNumber());
                }
                rv.setAdapter(new QuotationAdapter(getActivity(),subQuotationArrayList));
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    class GetAllQuotationTask extends AsyncTask<String,Void,String>
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
            try {
                url = new URL(Appconfig.CUSTOMER_QUOTATION_URL+params[0]);
                connection = (HttpURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String temp;
                while ((temp=br.readLine())!=null)
                {
                    buffer.append(temp);
                }
                response=buffer.toString();
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
                JSONObject jsonObject=new JSONObject(response);
                JSONArray jsonArray=jsonObject.getJSONArray("quotations");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    Quotation quotation=new Quotation();
                    quotation.setId(object.getInt("id"));
                    quotation.setQuotations_number(object.getString("quotations_number"));
                    quotation.setPerson(object.getString("person"));
                    quotation.setFinal_price(object.getString("grand_total"));
                    quotationArrayList.add(quotation);
                }

                mAdapter=new QuotationAdapter(getActivity(), quotationArrayList);

                rv.setAdapter(mAdapter);
                rv.setItemAnimator(new DefaultItemAnimator());
                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
                //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);

                rv.setLayoutManager(lmanager);
               /* rv.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),rv, new RecyclerTouchListener.ClickListener() {
                    @Override

                    public void onClick(View view, int position) {
                        Quotation quotation= quotationArrayList.get(position);
                        final int quotationId=quotation.getId();
                        Intent i=new Intent(getActivity(), QuotationDetailActivity.class);
                        i.putExtra("quotation_id",String.valueOf(quotationId));
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
        }}

    @Override
    public void onPause() {
        super.onPause();

        if ((dialog != null) && dialog.isShowing())
            dialog.dismiss();
        dialog= null;
    }
    @Override
    public void onResume() {

        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        }
        super.onResume();
    }
}
