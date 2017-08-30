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
import com.project.lorvent.lcrm.utils.NetworkStatus;
import com.project.lorvent.lcrm.adapters.SalesTeamAdapter;
import com.project.lorvent.lcrm.activities.add.AddSalesTeamActivity;
import com.project.lorvent.lcrm.models.SalesTeam;

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
public class SalesTeamFragment extends Fragment {

    String token;
    SalesTeamAdapter mAdapter;
    MaterialSearchView searchView;
    ProgressDialog dialog;
    private boolean helpDisplayed = false;
    private static final String PREF_FIRSTLAUNCH_HELP = "helpDisplayed";
    public SalesTeamFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View v=inflater.inflate(R.layout.fragment_sales_team, container, false);

        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
       // String salesteam= getArguments().getString("salesteam_read");

        searchView = (MaterialSearchView)getActivity().findViewById(R.id.search_view);
        AppSession.salesTeamArrayList=new ArrayList<>();
        AppSession.salesTeam_recyclerView=(RecyclerView)v.findViewById(R.id.rv);
        token= Appconfig.TOKEN;

        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("SalesTeam");
        }

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
            new GetAllSalesTeamTask().execute(token);
        }

        final com.getbase.floatingactionbutton.FloatingActionButton actionA = (com.getbase.floatingactionbutton.FloatingActionButton)v.findViewById(R.id.action_a);
        if (AppSession.salesteam_write==0)
        {
            v.findViewById(R.id.fab).setVisibility(View.GONE);
        }
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(),AddSalesTeamActivity.class);
                getActivity().startActivity(i);
            }
        });
        final com.getbase.floatingactionbutton.FloatingActionButton actionB = (com.getbase.floatingactionbutton.FloatingActionButton)v.findViewById(R.id.action_b);
        actionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        if (v.findViewById(R.id.action_a).getVisibility()==View.VISIBLE&&v.findViewById(R.id.action_b).getVisibility()==View.VISIBLE)
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
                ArrayList<SalesTeam> subSalesTeamArrayList=new ArrayList<>();

                for (int i=0;i<AppSession.salesTeamArrayList.size();i++)
                {
                    if (AppSession.salesTeamArrayList.get(i).getSalesteam().contains(newText))
                    {
                        subSalesTeamArrayList.add(AppSession.salesTeamArrayList.get(i));
                    }
                }
                AppSession.salesTeam_recyclerView.setAdapter(new SalesTeamAdapter(getActivity(),subSalesTeamArrayList));
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
    class GetAllSalesTeamTask extends AsyncTask<String,Void,String>
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
                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String get_url;
                if (text_url != null) {
                    get_url = text_url + "/user/salesteams?token=";
                } else {
                    get_url = Appconfig.SALESTEAM_URL;
                }
                url = new URL(get_url+params[0]);
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
                JSONArray jsonArray=jsonObject.getJSONArray("salesteams");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    SalesTeam salesTeam=new SalesTeam();
                    salesTeam.setId(object.getInt("id"));
                    salesTeam.setSalesteam(object.getString("salesteam"));
                    salesTeam.setTarget(object.getString("target"));
                    salesTeam.setInvoice_forecast(object.getString("invoice_forecast"));
                    salesTeam.setActual_invoice(object.getString("actual_invoice"));
                    AppSession.salesTeamArrayList.add(salesTeam);
                }
                mAdapter=new SalesTeamAdapter(getActivity(),AppSession.salesTeamArrayList);
                AppSession.salesTeam_recyclerView.setAdapter(mAdapter);
                AppSession.salesTeam_recyclerView.setItemAnimator(new DefaultItemAnimator());
                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
                //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);

                AppSession.salesTeam_recyclerView.setLayoutManager(lmanager);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }}
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
