package com.project.lorvent.lcrm.fragments.admin.customerfrag;


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
import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.utils.NetworkStatus;
import com.project.lorvent.lcrm.adapters.ContactPersonAdapter;
import com.project.lorvent.lcrm.activities.add.AddContactsActivity;
import com.project.lorvent.lcrm.models.Contacts;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactPersonFragment extends Fragment {
   /* ArrayList<Contacts> contactsArrayList;
    RecyclerView rv;*/
    String token;
    ProgressDialog dialog;
    ContactPersonAdapter mAdapter;
    MaterialSearchView searchView;
    public ContactPersonFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_contact_person,container, false);
        setHasOptionsMenu(true);
        AppSession.contactArrayList =new ArrayList<>();
        token= Appconfig.TOKEN;
        searchView = (MaterialSearchView)getActivity().findViewById(R.id.search_view);
        AppSession.customer_recyclerView=(RecyclerView)v.findViewById(R.id.rv);

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
            new GetAllContacts().execute(token);
        }

        final com.getbase.floatingactionbutton.FloatingActionButton actionA = (com.getbase.floatingactionbutton.FloatingActionButton)v.findViewById(R.id.action_a);
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(),AddContactsActivity.class);
                getActivity().startActivity(i);
                //Toast.makeText(getActivity(), " Action A is clicked",Toast.LENGTH_SHORT).show();
            }
        });
        final com.getbase.floatingactionbutton.FloatingActionButton actionB = (com.getbase.floatingactionbutton.FloatingActionButton)v.findViewById(R.id.action_b);
        actionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                ArrayList<Contacts> subContactArrayList=new ArrayList<>();
                for (int i=0;i<AppSession.contactArrayList.size();i++)
                {
                    if (AppSession.contactArrayList.get(i).getName().contains(newText))
                    {
                        subContactArrayList.add(AppSession.contactArrayList.get(i));
                    }
                    //System.out.println("lead item --"+leadsArrayList.get(i).getName()+" "+leadsArrayList.get(i).getNumber());
                }
                AppSession.customer_recyclerView.setAdapter(new ContactPersonAdapter(getActivity(),subContactArrayList));
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
    private class GetAllContacts extends AsyncTask<String,Void,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading, please wait");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(true);
        }

        @Override
        protected String doInBackground(String... params) {
            URL url;
            String response="";
            HttpURLConnection connection ;
            BufferedReader  bufferedReader;
            StringBuffer  buffer;
            String tok=params[0];
            try {
                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String get_url;
                if (text_url != null) {
                    get_url = text_url + "/user/customers?token=";
                } else {
                    get_url = Appconfig.CUSTOMER_URL;
                }

                url = new URL(get_url+tok);
                connection = (HttpURLConnection) url.openConnection();
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    buffer=new StringBuffer();
                    while ((line = bufferedReader.readLine()) != null) {
                       // response += line;
                        buffer.append(line);
                    }
                    response=buffer.toString();

                }
                else {
                    InputStreamReader inputStreamReader = new InputStreamReader(connection.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    buffer=new StringBuffer();

                    String line ="";
                    while ((line = bufferedReader.readLine()) != null) {
                      //  response += line;
                        buffer.append(line);

                    }
                    response=buffer.toString();
                }
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
                    JSONObject  jsonObject1=jsonObject.getJSONObject("customers");
                Iterator<String> iter = jsonObject1.keys();
                while (iter.hasNext()) {
                    String key = iter.next();

                    try {
                        Object value = jsonObject1.get(key);
                        JSONObject jsonObject2= (JSONObject) jsonObject1.get(key);
                        Contacts contacts=new Contacts();
                        contacts.setId(jsonObject2.getInt("customer_id"));
                        contacts.setName(jsonObject2.getString("full_name"));
                        contacts.setEmail(jsonObject2.getString("email"));
                        contacts.setAvatar(jsonObject2.getString("avatar"));
                        AppSession.contactArrayList.add(contacts);
                    } catch (JSONException e) {
                        Crashlytics.logException(e);
                    }
                }

                mAdapter = new ContactPersonAdapter(getActivity(),AppSession.contactArrayList);
                AppSession.customer_recyclerView.setAdapter(mAdapter);
                AppSession.customer_recyclerView.setItemAnimator(new DefaultItemAnimator());
                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
                //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);

                AppSession.customer_recyclerView.setLayoutManager(lmanager);

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
