package com.project.lorvent.lcrm.fragments.customers;


import android.app.Dialog;
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
import android.widget.TextView;


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
import com.project.lorvent.lcrm.adapters.customers.ContractAdapter;
import com.project.lorvent.lcrm.models.customer.Contracts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContractsFragment extends Fragment {
    ArrayList<Contracts> contractsArrayList;
    RecyclerView rv;
    String token;
    ContractAdapter mAdapter;
    MaterialSearchView searchView;
    ProgressDialog progressDialog;
    TextView description, tvDuration;
    public static Dialog mdialog;

    public ContractsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contracts2, container, false);
        setHasOptionsMenu(true);
        contractsArrayList = new ArrayList<>();
        token = Appconfig.TOKEN;
        searchView = (MaterialSearchView) getActivity().findViewById(R.id.search_view);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Contracts");
        }
        rv = (RecyclerView) v.findViewById(R.id.rv);
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
            getContractList(token);
        }


        return v;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search_staff, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        searchView.setMenuItem(menuItem);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Contracts> subContrcatArrayList = new ArrayList<>();

                for (int i = 0; i < contractsArrayList.size(); i++) {
                    if (contractsArrayList.get(i).getCompany().contains(newText)) {
                        Contracts contracts = new Contracts();
                        contracts.setCompany(contractsArrayList.get(i).getCompany());
                        contracts.setUser(contractsArrayList.get(i).getUser());
                        contracts.setStart_date(contractsArrayList.get(i).getStart_date());
                        contracts.setEnd_date(contractsArrayList.get(i).getEnd_date());
                        contracts.setDescription(contractsArrayList.get(i).getDescription());
                        subContrcatArrayList.add(contracts);
                    }
                    //System.out.println("lead item --"+leadsArrayList.get(i).getName()+" "+leadsArrayList.get(i).getNumber());
                }
                rv.setAdapter(new ContractAdapter(getActivity(), subContrcatArrayList));
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void getContractList(String token) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading, please wait");
        progressDialog.setTitle("Connecting server");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Appconfig.CUSTOMER_CONTRACT_URL + token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("contracts");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                Contracts contracts = new Contracts();
                                contracts.setStart_date(object.getString("start_date"));
                                contracts.setEnd_date(object.getString("end_date"));
                                contracts.setCompany(object.getString("company"));
                                contracts.setUser(object.getString("user"));
                                contracts.setDescription(object.getString("description"));
                                contractsArrayList.add(contracts);
                            }

                            mAdapter = new ContractAdapter(getActivity(), contractsArrayList);
                            rv.setAdapter(mAdapter);
                            rv.setItemAnimator(new DefaultItemAnimator());
                            // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                            RecyclerView.LayoutManager lmanager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                            //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);

                            rv.setLayoutManager(lmanager);
                           /* rv.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv, new RecyclerTouchListener.ClickListener() {
                                @Override

                                public void onClick(View view, int position) {
                                    Contracts contract = contractsArrayList.get(position);
                                    String contract_description = contract.getDescription();
                                    final Dialog dialog = new Dialog(getActivity());
                                    Configuration config = getActivity().getResources().getConfiguration();
                                    if (config.smallestScreenWidthDp >= 600) {
                                        dialog.setContentView(R.layout.contract_description);
                                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                                        int width = (int) (getActivity().getResources().getDisplayMetrics().widthPixels * 0.70);
                                        int height = (int) (getActivity().getResources().getDisplayMetrics().heightPixels * 0.50);
                                        dialog.getWindow().setLayout(width, height);
                                        setDialog(dialog);
                                    } else {
                                        dialog.setContentView(R.layout.contract_description_mob);
                                        tvDuration = (TextView) dialog.findViewById(R.id.duration);
                                        tvDuration.setText(contract.getStart_date() + "-" + contract.getEnd_date());

                                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                                        int width = (int) (getActivity().getResources().getDisplayMetrics().widthPixels * 0.70);
                                        int height = (int) (getActivity().getResources().getDisplayMetrics().heightPixels * 0.50);
                                        dialog.getWindow().setLayout(width, height);
                                        setDialog(dialog);
                                    }
                                    dialog.setTitle("Contract Description");

                                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                                    description = (TextView) dialog.findViewById(R.id.description);

                                    dialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });
                                    description.setText(contract_description);

                                    dialog.setCancelable(false);
                                *//*    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                                    int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.75);
                                    int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.40);
                                    dialog.getWindow().setLayout(width, height);
                                    setDialog(dialog);*//*
                                    dialog.show();
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
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }

        };
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);

    }

    public void setDialog(Dialog dialog) {
        mdialog = dialog;
    }
    @Override
    public void onPause() {
        super.onPause();

        if ((progressDialog != null) && progressDialog.isShowing())
            progressDialog.dismiss();
        progressDialog= null;
    }
}
