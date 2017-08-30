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
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.MyVolleySingleton;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.utils.NetworkStatus;
import com.project.lorvent.lcrm.adapters.StaffAdapter;
import com.project.lorvent.lcrm.activities.add.AddStaffActivity;
import com.project.lorvent.lcrm.models.Staff;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class StaffFragment extends Fragment {

    String token;
    View v;
    StaffAdapter staffAdapter;
    MaterialSearchView searchView;
    ProgressDialog progressDialog;
    private boolean helpDisplayed = false;
    TextInputLayout input_forget_email;
    EditText forget_email;
    private static final String PREF_FIRSTLAUNCH_HELP = "helpDisplayed";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_staff, container, false);
        AppSession.staffArrayList=new ArrayList<>();
        token= Appconfig.TOKEN;

        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Staff");
        }

         searchView = (MaterialSearchView)getActivity().findViewById(R.id.search_view);
        setHasOptionsMenu(true);
        AppSession.staff_recyclerView=(RecyclerView)v.findViewById(R.id.rv);

        if (AppSession.staff_write==0)
        {
         v.findViewById(R.id.multiple_actions).setVisibility(View.GONE);

        }
      /*  if (AppSession.staff_write==0)
        {
            v.findViewById(R.id.fab).setVisibility(View.GONE);
        }*/
   /*     v.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(), AddStaffActivity.class);
                getActivity().startActivity(i);
            }
        });*/

        final com.getbase.floatingactionbutton.FloatingActionButton actionA = (com.getbase.floatingactionbutton.FloatingActionButton)v.findViewById(R.id.action_a);
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(),AddStaffActivity.class);
                getActivity().startActivity(i);
                //Toast.makeText(getActivity(), " Action A is clicked",Toast.LENGTH_SHORT).show();
            }
        });
        final com.getbase.floatingactionbutton.FloatingActionButton actionB = (com.getbase.floatingactionbutton.FloatingActionButton)v.findViewById(R.id.action_b);
        actionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MaterialDialog dialog1 = new MaterialDialog.Builder(getActivity())
                        .title("Invite Staff")
                        .customView(R.layout.invite_staff_dialog, true)
                        .positiveText("send")
                        .autoDismiss(false)
                        .positiveColorRes(R.color.colorPrimary)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (forget_email.getText().toString().isEmpty()) {
                                    if (forget_email.getText().toString().isEmpty()) {
                                        input_forget_email.setError("please enter your email");
                                        return;
                                    }
                                 /*   if (!isValidEmail(forget_email.getText().toString())) {
                                        input_forget_email.setError("Please enter valid  email");
                                        return;
                                    }*/
                                }
                                else {
                                    dialog.dismiss();
                                }

                                new Invite_staff().execute(token,forget_email.getText().toString());

                            }
                        })
                        .show();
                View view1 = dialog1.getCustomView();
                if (view1 != null) {
                    forget_email = (EditText) dialog1.getCustomView().findViewById(R.id.email);
                    input_forget_email = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.input_forget_email);
                    forget_email.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            input_forget_email.setError("");
                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });

                }
            }
        });
        if (v.findViewById(R.id.multiple_actions).getVisibility()==View.VISIBLE)
        {showHelpForFirstLaunch();}
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

            getAllStaff(token);
        }

        //new MytaskStaff().execute(token);
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
        return v ;
    }

    private void getAllStaff(String token) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading, please wait");
            progressDialog.setTitle("Connecting server");
            progressDialog.show();
            progressDialog.setCancelable(false);

        SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String get_url;
        if (text_url != null) {
            get_url = text_url + "/user/staffs?token=";
        } else {
            get_url = Appconfig.STAFF_URL;
        }
            StringRequest stringRequest = new StringRequest(Request.Method.GET,get_url+token,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject=new JSONObject(response);
                                //JSONArray jsonArray=jsonObject.getJSONArray("staffs");
                                JSONObject  jsonObject1=jsonObject.getJSONObject("staffs");
                                Iterator<String> iter = jsonObject1.keys();
                                while (iter.hasNext()) {
                                    String key = iter.next();

                                    try {
                                        Object value = jsonObject1.get(key);
                                        JSONObject jsonObject2= (JSONObject) jsonObject1.get(key);
                                        Staff staff =new Staff();
                                        staff.setName(jsonObject2.getString("full_name"));
                                        staff.setEmail(jsonObject2.getString("email"));
                                        staff.setId(jsonObject2.getInt("id"));
                                        AppSession.staffArrayList.add(staff);

                                    } catch (JSONException e) {
                                        // Something went wrong!
                                    }
                                }

                               /* for (int i=0;i<jsonArray.length();i++)
                                {
                                    JSONObject object=jsonArray.getJSONObject(i);
                                    Staff staff =new Staff();
                                    staff.setName(object.getString("full_name"));
                                    staff.setEmail(object.getString("email"));
                                    staff.setId(object.getInt("id"));
                                    AppSession.staffArrayList.add(staff);
                                }*/

                                staffAdapter=new StaffAdapter(getActivity(),AppSession.staffArrayList);
                               // AppSession.staffArrayListGlobal=staffArrayList;
                                AppSession.staff_recyclerView.setAdapter(staffAdapter);
                                AppSession.staff_recyclerView.setItemAnimator(new DefaultItemAnimator());
                                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
                                //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);

                                AppSession.staff_recyclerView.setLayoutManager(lmanager);
                                /*AppSession.staff_recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),AppSession.staff_recyclerView, new RecyclerTouchListener.ClickListener() {
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
                                }));*/

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (progressDialog!=null&&progressDialog.isShowing()){progressDialog.dismiss();}


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

                for (int i=0;i<AppSession.staffArrayList.size();i++)
                {
                    if (AppSession.staffArrayList.get(i).getName().contains(newText))
                    {
                        subStaffArrayList.add(AppSession.staffArrayList.get(i));
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

    @Override
    public void onPause() {
        super.onPause();

        if ((progressDialog != null) && progressDialog.isShowing())
            progressDialog.dismiss();
        progressDialog= null;
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
    private class Invite_staff extends AsyncTask<String,Void,String>
    {
        // ProgressDialog dialog;
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String response="",jsonresponse="";
            JSONObject json;
            BufferedReader bufferedReader;
            String tok=params[0];
            String emails=params[1];
            URL url;
            try {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("emails",emails);
                Log.i("json", tok);

                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String invite_url;
                if (text_url != null) {
                    invite_url = text_url + "/user/invite_staff?token=";
                } else {
                    invite_url = Appconfig.INVITE_STAFF_URL;
                }
                url = new URL(invite_url+tok);
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
            progressDialog.dismiss();
            if (result!=null)
            {
                if (result.equals("success"))
                {
                    Toast.makeText(getActivity(),"we have sent invitation link to your email",Toast.LENGTH_LONG).show();
                }
                if (result.equals("not_valid_data"))
                {
                    Toast.makeText(getActivity(),"error occured!Try Again",Toast.LENGTH_LONG).show();
                }
            }

        }

    }
}
