package com.project.lorvent.lcrm.fragments.admin.invoicefrag;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.ChartFragment;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.utils.NetworkStatus;
import com.project.lorvent.lcrm.adapters.Invoice2Adapter;
import com.project.lorvent.lcrm.adapters.InvoicesAdapter;
import com.project.lorvent.lcrm.activities.add.AddInvoicesActivity;
import com.project.lorvent.lcrm.models.Invoice;

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

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class InvoicesFragment extends ChartFragment {
    RecyclerView recyclerView1;
    PieChart mPieChart;
    MaterialSearchView searchView;
    View v;
    ProgressDialog dialog;
    private boolean helpDisplayed = false;
    private static final String PREF_FIRSTLAUNCH_HELP = "helpDisplayed";
    ArrayList<Double> invoices;
    TextView text_open,text_overdue,text_paid;
    public InvoicesFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_invoices, container, false);
        setHasOptionsMenu(true);
        //showHelpForFirstLaunch();
        AppSession.invoicesArrayList=new ArrayList<>();
        searchView = (MaterialSearchView)getActivity().findViewById(R.id.search_view);
        invoices=new ArrayList<>();
        mPieChart = (PieChart) v.findViewById(R.id.piechart);
        text_open=(TextView)v.findViewById(R.id.text_open);
        text_overdue=(TextView)v.findViewById(R.id.text_overdue);
        text_paid=(TextView)v.findViewById(R.id.text_paid);
        //recyclerView1=(RecyclerView)v.findViewById(R.id.recycler);

        AppSession.invoices_recyclerView=(RecyclerView)v.findViewById(R.id.rv);

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
            new GetAllInvoices().execute(Appconfig.TOKEN);
        }

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
        if (v.findViewById(R.id.fab).getVisibility()==View.VISIBLE)
        {showHelpForFirstLaunch();}
        return v;
    }
    private void loadData() {
        double total=invoices.get(0)+invoices.get(1)+invoices.get(2);
        float open_invoice= (float) ((invoices.get(2)/total)*100);
        float overdue_invoice= (float) ((invoices.get(0)/total)*100);
        float paid_invoice= (float) ((invoices.get(1)/total)*100);

        text_open.setText(String.valueOf(invoices.get(2)+"$"));
        text_overdue.setText(String.valueOf(invoices.get(0)+"$"));
        text_paid.setText(String.valueOf(invoices.get(1)+"$"));


        mPieChart.addPieSlice(new PieModel("Open invoice",open_invoice, Color.parseColor("#03A9F4")));
        mPieChart.addPieSlice(new PieModel("Overdue invoice", overdue_invoice, Color.parseColor("#FF8A65")));
        mPieChart.addPieSlice(new PieModel("Paid invoice", paid_invoice, Color.parseColor("#8BC34A")));
        mPieChart.setOnItemFocusChangedListener(new IOnItemFocusChangedListener() {
            @Override
            public void onItemFocusChanged(int _Position) {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
       // mPieChart.startAnimation();
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        }
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
                for (int i=0;i<AppSession.invoicesArrayList.size();i++)
                {
                    if (AppSession.invoicesArrayList.get(i).getCustomer().contains(newText))
                    {
                        subInvoiceArrayList.add(AppSession.invoicesArrayList.get(i));
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
                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String get_url;
                if (text_url != null) {
                    get_url = text_url + "/user/invoices?token=";
                } else {
                    get_url = Appconfig.INVOICES_URL;
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
                invoices.add(jsonObject.getDouble("month_overdue"));
                invoices.add(jsonObject.getDouble("month_paid"));
                invoices.add(jsonObject.getDouble("month_open"));
                invoices.add(jsonObject.getDouble("month_overdue")+jsonObject.getDouble("month_paid")+jsonObject.getDouble("month_open"));

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
/*
                recyclerView1.setAdapter(new InvoicesAdapter(invoices, getActivity()));
                recyclerView1.setItemAnimator(new DefaultItemAnimator());
                RecyclerView.LayoutManager layoutManager=new GridLayoutManager(getActivity(),2);
                recyclerView1.setLayoutManager(layoutManager);*/
                loadData();
            } catch (JSONException e) {
                e.printStackTrace();
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
    private void showHelpForFirstLaunch() {
        if (helpDisplayed)
            return;
        helpDisplayed = getPreferenceValue(PREF_FIRSTLAUNCH_HELP, false);
        if (!helpDisplayed) {
            savePreference(PREF_FIRSTLAUNCH_HELP, true);
            showOverLay();
        }
    }

    private boolean getPreferenceValue(String key, boolean defaultValue) {
        SharedPreferences preferences = getActivity().getSharedPreferences("pref1",MODE_PRIVATE);
        return preferences.getBoolean(key, defaultValue);
    }

    private void savePreference(String key, boolean value) {
        SharedPreferences preferences = getActivity().getSharedPreferences("pref1",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    private void showOverLay(){

        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);

        dialog.setContentView(R.layout.overlay_view);
        LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.overlayLayout);
        ImageView image=(ImageView)dialog.findViewById(R.id.imageView1);
        image.setImageResource(R.drawable.overlay2);
        layout.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View arg0) {

                dialog.dismiss();

            }

        });

        dialog.show();

    }
}
