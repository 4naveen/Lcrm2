package com.project.naveen.lcrm.menu.fragment.customerfrag;


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
import android.widget.Toast;


import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.project.naveen.lcrm.AppSession;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.RecyclerTouchListener;
import com.project.naveen.lcrm.adapters.CompanyAdapter;
import com.project.naveen.lcrm.addactivity.AddCompanyActivity;
import com.project.naveen.lcrm.detailsactivity.DetailCompanyActivity;
import com.project.naveen.lcrm.models.Company;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompanyFragment extends Fragment {

//    ArrayList<Company> companyArrayList;
//    RecyclerView rv;
    String token;
    MaterialSearchView searchView;
    CompanyAdapter mAdapter;
    public CompanyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_company, container, false);
        setHasOptionsMenu(true);
        searchView=(MaterialSearchView)getActivity().findViewById(R.id.search_view);
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Company");
        }
        AppSession.companyArrayList=new ArrayList<>();
        AppSession.company_recyclerView=(RecyclerView)v.findViewById(R.id.rv);
        token= Appconfig.TOKEN;
        new GetAllCompanyTask().execute(token);
        if (AppSession.contacts_write==0)
        {
            v.findViewById(R.id.fab).setVisibility(View.GONE);
        }
        v.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(getActivity(), AddCompanyActivity.class));

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
                ArrayList<Company> subCompanyArrayList=new ArrayList<>();
              /*  Log.i("ontypecomplete", newText);
                Log.i("list data--", String.valueOf( AppSession.companyArrayList.size()));*/
                for (int i=0;i< AppSession.companyArrayList.size();i++)
                {
                    if ( AppSession.companyArrayList.get(i).getName()


                            .contains(newText))
                    {
                       Company company=new Company();
                        company.setCustomer( AppSession.companyArrayList.get(i).getCustomer());
                        company.setId( AppSession.companyArrayList.get(i).getId());
                        company.setName( AppSession.companyArrayList.get(i).getName());
                        company.setPhone( AppSession.companyArrayList.get(i).getPhone());

                        subCompanyArrayList.add(company);
                    }
                    //System.out.println("lead item --"+leadsArrayList.get(i).getName()+" "+leadsArrayList.get(i).getNumber());
                }
                AppSession.company_recyclerView.setAdapter(new CompanyAdapter(getActivity(),subCompanyArrayList));
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
    class GetAllCompanyTask extends AsyncTask<String,Void,String>
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
            HttpURLConnection connection ;
            String tok=params[0];
            try {
                url = new URL(Appconfig.COMPANY_URL+tok);
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
                JSONArray jsonArray=jsonObject.getJSONArray("companies");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    Company company=new Company();
                    company.setId(object.getInt("id"));
                    company.setName(object.getString("name"));
                    company.setCustomer(object.getString("customer"));
                    company.setPhone(object.getString("phone"));
                    AppSession.companyArrayList.add(company);
                    //  Log.i("leadslist--",lead.getName());
                }

                mAdapter = new CompanyAdapter(getActivity(), AppSession.companyArrayList);
                AppSession.company_recyclerView.setAdapter(mAdapter);
                AppSession.company_recyclerView.setItemAnimator(new DefaultItemAnimator());
                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
                //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);
                AppSession.company_recyclerView.setLayoutManager(lmanager);

                AppSession.company_recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), AppSession.company_recyclerView, new RecyclerTouchListener.ClickListener() {
                    @Override

                    public void onClick(View view, int position) {
                        // Leads leads=leadsArrayList.get(position);
//                        Toast.makeText(getActivity(),"you selected on"+leads.getName(),Toast.LENGTH_LONG).show();
                        Company company= AppSession.companyArrayList.get(position);
                        final int company_id=company.getId();
                        Intent i=new Intent(getActivity(), DetailCompanyActivity.class);
                        i.putExtra("company_id",String.valueOf(company_id));
                        i.putExtra("company_id_position",position);
                       // Log.i("company_id_position", String.valueOf(position));
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

    class AddCompanyTask extends AsyncTask<String,Void,String>
    {
        ProgressDialog dialog;
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading, please wait...");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String response="",jsonresponse="";
            JSONObject json;
            BufferedReader bufferedReader;
            String tok=params[0];
            String company_name=params[1];
            String company_email=params[2];
            URL url;
            try {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("name",company_name);
                jsonObject.put("email",company_email);

                url = new URL(Appconfig.COMPANY_POST_URL+tok);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));


                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();
                os.close();
                //Log.i("res code--",""+conn.getResponseCode());
                int responseCode=conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    //Log.d("Output",br.toString());
                    while ((line = br.readLine()) != null) {
                        response += line;
                        Log.d("output lines", line);
                    }
                    json = new JSONObject(response);
                    //Get Values from JSONobject
                    //System.out.println("success=" + json.get("success"));

                    jsonresponse = json.getString("success");

                }
                else {
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        response += line;
                        Log.d("output lines", line);
                    }
                    Log.i("response",response);
                    json = new JSONObject(response);
                    jsonresponse=json.getString("error");
                    //System.out.println("error=" + json.get("error"));
                    //succes = json.getString("success");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonresponse;
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            if (result!=null)
                Toast.makeText(getContext(),""+result,Toast.LENGTH_LONG).show();
            // new StaffAdapter(getApplicationContext(),StaffAdapter.staffs).notifyItemInserted(StaffAdapter.staffs.size()+1);
            //getActivity().finish();

        }
    }
}
