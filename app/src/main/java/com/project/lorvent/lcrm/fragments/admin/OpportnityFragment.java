package com.project.lorvent.lcrm.fragments.admin;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.utils.Connection;
import com.project.lorvent.lcrm.utils.NetworkStatus;
import com.project.lorvent.lcrm.adapters.OpportunityAdapter;
import com.project.lorvent.lcrm.activities.add.AddOppActivity;
import com.project.lorvent.lcrm.models.Opportunity;

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
public class OpportnityFragment extends Fragment {

    String token;
    MaterialSearchView searchView;
    OpportunityAdapter mAdapter;
    ProgressDialog dialog;
    private boolean helpDisplayed = false;
    private static final String PREF_FIRSTLAUNCH_HELP = "helpDisplayed";

    public OpportnityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

       View v=inflater.inflate(R.layout.fragment_opportnity,container, false);
        Connection.getStaffList(Appconfig.TOKEN,getActivity());
        token= Appconfig.TOKEN;
        searchView = (MaterialSearchView)getActivity().findViewById(R.id.search_view);
        setHasOptionsMenu(true);
        //showHelpForFirstLaunch();
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Opportunity");
        }


        AppSession.opportunityArrayList=new ArrayList<>();
        AppSession.opportunity_recyclerView=(RecyclerView)v.findViewById(R.id.rv);
         token=Appconfig.TOKEN;
        if (!NetworkStatus.isConnected(getActivity())){
            MaterialDialog dialog1=new MaterialDialog.Builder(getActivity())
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
            new OpportunityTask().execute(token);
        }

        if (AppSession.opp_write==0)
        {
            v.findViewById(R.id.fab).setVisibility(View.GONE);
        }
        v.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(getActivity(), AddOppActivity.class);
                getActivity().startActivity(i);
            }
        });

        if (v.findViewById(R.id.fab).getVisibility()==View.VISIBLE)
        {showHelpForFirstLaunch();}

        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        NavigationView navigationView=(NavigationView) getActivity().findViewById(R.id.nav_view);
                        navigationView.getMenu().findItem(R.id.dashboard).setChecked(true);
                        Fragment fragment1 = new DashboardFragment();
                        FragmentTransaction trans1 = getFragmentManager().beginTransaction();
                        trans1.replace(R.id.frame, fragment1);
                        trans1.addToBackStack(null);
                        trans1.commit();
                        return true;
                    }
                }
                return false;
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
                ArrayList<Opportunity> subOppArrayList = new ArrayList<>();

                for (int i = 0; i < AppSession.opportunityArrayList.size(); i++) {
                    if (AppSession.opportunityArrayList.get(i).getOpportunity().contains(newText)) {
                        subOppArrayList.add(AppSession.opportunityArrayList.get(i));
                    }
                }
                AppSession.opportunity_recyclerView.setAdapter(new OpportunityAdapter(getActivity(), subOppArrayList));
                return false;
            }
        });
                super.onCreateOptionsMenu(menu, inflater);
    }

    public class OpportunityTask extends AsyncTask<String,Void,String>
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
                    get_url = text_url + "/user/opportunities?token=";
                } else {
                    get_url = Appconfig.OPPORTUNITY_URL;
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
            }
            return response;
        }
        @Override
        protected void onPostExecute(String response) {
            if (dialog!=null&&dialog.isShowing()){dialog.dismiss();}
            try {
                JSONObject jsonObject=new JSONObject(response);
                JSONArray jsonArray=jsonObject.getJSONArray("opportunities");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    Opportunity opportunity=new Opportunity();
                    opportunity.setId(object.getInt("id"));
                    opportunity.setOpportunity(object.getString("opportunity"));
                    opportunity.setNext_action_date(object.getString("next_action"));
                    opportunity.setExpected_revenue(object.getString("expected_revenue"));
                    opportunity.setStages(object.getString("stages"));
                    opportunity.setProbability(object.getString("probability"));
                    opportunity.setSalesTeam(object.getString("salesteam"));
                    opportunity.setMeetings(object.getString("meetings"));
                    opportunity.setCompany(object.getString("company"));
                    opportunity.setCalls(object.getString("calls"));
                    AppSession.opportunityArrayList.add(opportunity);

                }

                mAdapter = new OpportunityAdapter(getActivity(),AppSession.opportunityArrayList);
                AppSession.opportunity_recyclerView.setAdapter(mAdapter);
                AppSession.opportunity_recyclerView.setItemAnimator(new DefaultItemAnimator());
                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);

                AppSession.opportunity_recyclerView.setLayoutManager(lmanager);

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
    @Override
    public void onResume() {

        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        }
        super.onResume();
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
