package com.project.naveen.lcrm.menu.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
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
import com.project.naveen.lcrm.adapters.StaffAdapter;
import com.project.naveen.lcrm.addactivity.AddStaffActivity;
import com.project.naveen.lcrm.detailsactivity.StaffDetailActivity;
import com.project.naveen.lcrm.models.Staff;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class StaffFragment extends Fragment {

    String token;
    View v;
    StaffAdapter staffAdapter;
    MaterialSearchView searchView;
    ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        AppSession.staffArrayList=new ArrayList<>();
        token= Appconfig.TOKEN;
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Staff");
        }
        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 600) {
            v=inflater.inflate(R.layout.fragment_staff, container, false);
        }
        else
        {
            v=inflater.inflate(R.layout.fragment_staff_mob, container, false);

        }
//        View v=inflater.inflate(R.layout.fragment_staff, container, false);
         searchView = (MaterialSearchView)getActivity().findViewById(R.id.search_view);
        setHasOptionsMenu(true);
        AppSession.staff_recyclerView=(RecyclerView)v.findViewById(R.id.rv);
        if (AppSession.staff_write==0)
        {
            v.findViewById(R.id.fab).setVisibility(View.GONE);
        }
        v.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(), AddStaffActivity.class);
                getActivity().startActivity(i);
            }
        });
          getAllStaff(token);
        //new MytaskStaff().execute(token);

        Log.i("token--",""+token);
        return v ;
    }

    private void getAllStaff(String token) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading, please wait");
            progressDialog.setTitle("Connecting server");
            progressDialog.show();
            progressDialog.setCancelable(false);
            StringRequest stringRequest = new StringRequest(Request.Method.GET,Appconfig.STAFF_URL+token,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.i("response--",response);
                                JSONObject jsonObject=new JSONObject(response);
                                JSONArray jsonArray=jsonObject.getJSONArray("staffs");
                                for (int i=0;i<jsonArray.length();i++)
                                {
                                    JSONObject object=jsonArray.getJSONObject(i);
                                    Staff staff =new Staff();
                                    staff.setName(object.getString("full_name"));
                                    staff.setEmail(object.getString("email"));
                                    staff.setId(object.getInt("id"));
                                    AppSession.staffArrayList.add(staff);
                                }
                                staffAdapter=new StaffAdapter(getActivity(),AppSession.staffArrayList);
                               // AppSession.staffArrayListGlobal=staffArrayList;
                                AppSession.staff_recyclerView.setAdapter(staffAdapter);
                                AppSession.staff_recyclerView.setItemAnimator(new DefaultItemAnimator());
                                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
                                //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);

                                AppSession.staff_recyclerView.setLayoutManager(lmanager);
                                AppSession.staff_recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),AppSession.staff_recyclerView, new RecyclerTouchListener.ClickListener() {
                                    @Override

                                    public void onClick(View view, int position) {
                                        Staff staff= AppSession.staffArrayList.get(position);
                                        final int staff_id=staff.getId();
                                        Intent i=new Intent(getActivity(),StaffDetailActivity.class);
                                        i.putExtra("staff_id",String.valueOf(staff_id));
                                        i.putExtra("staff_id_position",position);
                                        Log.i("staff_id_position", String.valueOf(position));
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

                    //Log.i("response--", String.valueOf(error));
                }
            }) ;
            MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);

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
                ArrayList<Staff> subStaffArrayList=new ArrayList<>();
                Log.i("ontypecomplete", newText);
                Log.i("list data--", String.valueOf(AppSession.staffArrayList.size()));
                for (int i=0;i<AppSession.staffArrayList.size();i++)
                {
                    if (AppSession.staffArrayList.get(i).getName().contains(newText))
                    {
                        Staff staff =new Staff();
                        staff.setName(AppSession.staffArrayList.get(i).getName());
                        staff.setEmail(AppSession.staffArrayList.get(i).getEmail());

                        subStaffArrayList.add(staff);
                    }
                    //System.out.println("lead item --"+leadsArrayList.get(i).getName()+" "+leadsArrayList.get(i).getNumber());
                }
                AppSession.staff_recyclerView.setAdapter(new StaffAdapter(getActivity(),subStaffArrayList));
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

 /*   class MytaskStaff extends AsyncTask<String,Void,String>
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
                url = new URL(Appconfig.STAFF_URL+params[0]);
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
           // connection.disconnect();
            return response;
        }
        @Override
        protected void onPostExecute(String response) {
            dialog.dismiss();
            try {
                 Log.i("response--",response);
                JSONObject jsonObject=new JSONObject(response);
                JSONArray jsonArray=jsonObject.getJSONArray("staff");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    Staff staff =new Staff();
                    staff.setName(object.getString("full_name"));
                    staff.setEmail(object.getString("email"));
                    staff.setId(object.getInt("id"));
                    AppSession.staffArrayList.add(staff);
                     Log.i("leadslist--",staff.getName());
                }
                staffAdapter=new StaffAdapter(getActivity(),AppSession.staffArrayList);
                AppSession.staffArrayListGlobal=staffArrayList;
                AppSession.staff_recyclerView.setAdapter(staffAdapter);
                AppSession.staff_recyclerView.setItemAnimator(new DefaultItemAnimator());
                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
                //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);

                AppSession.staff_recyclerView.setLayoutManager(lmanager);


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }*/


}
