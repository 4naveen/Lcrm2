package com.project.naveen.lcrm.menu.fragment;


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

import com.daimajia.swipe.util.Attributes;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.project.naveen.lcrm.AppSession;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.RecyclerTouchListener;
import com.project.naveen.lcrm.adapters.QtemplateAdapter;
import com.project.naveen.lcrm.addactivity.AddQtemplateActivity;
import com.project.naveen.lcrm.detailsactivity.QtemplateDetailsActivity;
import com.project.naveen.lcrm.models.Qtemplate;

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
public class QtemplateFragment extends Fragment {
    /*ArrayList<Qtemplate> qtemplateArrayList;
    RecyclerView rv;*/
    MaterialSearchView searchView;
    String token;
    QtemplateAdapter mAdapter;

    public QtemplateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_qtemplate, container, false);
        AppSession.qtemplateArrayList =new ArrayList<>();
        token= Appconfig.TOKEN;
         setHasOptionsMenu(true);
        searchView=(MaterialSearchView)getActivity().findViewById(R.id.search_view);
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Quotation Template");
        }
        AppSession.qtemplate_recyclerView=(RecyclerView)v.findViewById(R.id.rv);
        v.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(), AddQtemplateActivity.class);
                getActivity().startActivity(i);
            }
        });
        new GetAllQtemplateTask().execute(token);
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
                ArrayList<Qtemplate> subQtemplateArrayList=new ArrayList<>();
                Log.i("ontypecomplete", newText);
                Log.i("list data--", String.valueOf(AppSession.qtemplateArrayList.size()));
                for (int i=0;i<AppSession.qtemplateArrayList.size();i++)
                {
                    if (AppSession.qtemplateArrayList.get(i).getQuatation_template().contains(newText))
                    {
                        Qtemplate qtemplate=new Qtemplate();
                        qtemplate.setQuatation_duration(AppSession.qtemplateArrayList.get(i).getQuatation_duration());
                        qtemplate.setQuatation_template(AppSession.qtemplateArrayList.get(i).getQuatation_template());
                        qtemplate.setId(AppSession.qtemplateArrayList.get(i).getId());
                        subQtemplateArrayList.add(qtemplate);
                    }
                    //System.out.println("lead item --"+leadsArrayList.get(i).getName()+" "+leadsArrayList.get(i).getNumber());
                }
                AppSession.qtemplate_recyclerView.setAdapter(new QtemplateAdapter(getActivity(),subQtemplateArrayList));
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
    class GetAllQtemplateTask extends AsyncTask<String,Void,String>
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
            URL url;
            HttpURLConnection connection;
            try {
                url = new URL(Appconfig.QTEMPLATE_URL+params[0]);
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
                //  Log.i("response--",response);
                JSONObject jsonObject=new JSONObject(response);
                JSONArray jsonArray=jsonObject.getJSONArray("qtemplates");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                     Qtemplate qtemplate=new Qtemplate();
                    qtemplate.setQuatation_template(object.getString("quotation_template"));
                    qtemplate.setQuatation_duration(object.getString("quotation_duration"));
                    qtemplate.setId(object.getInt("id"));
                    AppSession.qtemplateArrayList.add(qtemplate);

                    //  Log.i("leadslist--",lead.getName());
                }

                mAdapter=new QtemplateAdapter(getActivity(),AppSession.qtemplateArrayList);


                AppSession.qtemplate_recyclerView.setAdapter(mAdapter);
                AppSession.qtemplate_recyclerView.setItemAnimator(new DefaultItemAnimator());
                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
                //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);

                AppSession.qtemplate_recyclerView.setLayoutManager(lmanager);
                AppSession.qtemplate_recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),   AppSession.qtemplate_recyclerView, new RecyclerTouchListener.ClickListener() {
                    @Override

                    public void onClick(View view, int position) {
                        // Leads leads=leadsArrayList.get(position);
//                        Toast.makeText(getActivity(),"you selected on"+leads.getName(),Toast.LENGTH_LONG).show();
                        Qtemplate qtemplate= AppSession.qtemplateArrayList.get(position);
                        final int qtemplateId=qtemplate.getId();
                        Intent i=new Intent(getActivity(), QtemplateDetailsActivity.class);
                        i.putExtra("qtemplateId",String.valueOf(qtemplateId));
                        i.putExtra("qtemplateId_position",position);
                        Log.i("qtemplateId_position", String.valueOf(position));
                        getActivity().startActivity(i);
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }}
}
