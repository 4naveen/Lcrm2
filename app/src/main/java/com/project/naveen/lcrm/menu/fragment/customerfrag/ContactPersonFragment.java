package com.project.naveen.lcrm.menu.fragment.customerfrag;


import android.app.Dialog;
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
import android.view.Window;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daimajia.swipe.util.Attributes;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.project.naveen.lcrm.AppSession;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.MyVolleySingleton;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.RecyclerTouchListener;
import com.project.naveen.lcrm.adapters.ContactPersonAdapter;
import com.project.naveen.lcrm.addactivity.AddContactsActivity;
import com.project.naveen.lcrm.detailsactivity.DetailCompanyActivity;
import com.project.naveen.lcrm.models.Contacts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactPersonFragment extends Fragment {
    ArrayList<Contacts> contactsArrayList;
    RecyclerView rv;
    String token;
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
        contactsArrayList =new ArrayList<>();
        token= Appconfig.TOKEN;
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Contacts");
        }
        searchView = (MaterialSearchView)getActivity().findViewById(R.id.search_view);
        rv=(RecyclerView)v.findViewById(R.id.rv);
        for (int i = 0; i <15 ; i++) {
            Contacts contacts=new Contacts();
            contacts.setName("Ravi Reddy");
            contacts.setEmail("gsds@gewfuy.com");
            contactsArrayList.add(contacts);
        }
            new GetAllContacts().execute(token);
//        getCustomerList(Appconfig.TOKEN);
        mAdapter = new ContactPersonAdapter(getActivity(),contactsArrayList);
        rv.setAdapter(mAdapter);
        rv.setItemAnimator(new DefaultItemAnimator());
        // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
        RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);

        rv.setLayoutManager(lmanager);

        rv.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),rv, new RecyclerTouchListener.ClickListener() {
            @Override

            public void onClick(View view, int position) {

                Contacts contacts= contactsArrayList.get(position);
                final int contact_id=contacts.getId();
                Intent i=new Intent(getActivity(), DetailCompanyActivity.class);
                i.putExtra("contact_id",contact_id);
                i.putExtra("contact_id_position",position);
                // Log.i("company_id_position", String.valueOf(position));
                getActivity().startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
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
              /*  Log.i("ontypecomplete", newText);
                Log.i("list data--", String.valueOf(contactsArrayList.size()));*/
                for (int i=0;i<contactsArrayList.size();i++)
                {
                    if (contactsArrayList.get(i).getName().contains(newText))
                    {
                       Contacts contacts=new Contacts();
                        contacts.setName(contactsArrayList.get(i).getName());
                        contacts.setEmail(contactsArrayList.get(i).getEmail());

                        subContactArrayList.add(contacts);
                    }
                    //System.out.println("lead item --"+leadsArrayList.get(i).getName()+" "+leadsArrayList.get(i).getNumber());
                }
                rv.setAdapter(new ContactPersonAdapter(getActivity(),subContactArrayList));
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }


    class GetAllContacts extends AsyncTask<String,Void,String>
    {
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
            String response="";
            HttpURLConnection connection ;
            BufferedReader  bufferedReader;
            StringBuffer  buffer;
            String tok=params[0];
            try {
                url = new URL(Appconfig.CUSTOMER_URL+tok);
                connection = (HttpURLConnection) url.openConnection();
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    buffer=new StringBuffer();
                    //Log.d("Output",br.toString());
                    while ((line = bufferedReader.readLine()) != null) {
                       // response += line;
                        buffer.append(line);
                        Log.d("output lines", line);
                    }
                    response=buffer.toString();
                    //Get Values from JSONobject
                    //System.out.println("success=" + json.get("success"));
                    Log.i("response in customer", response);
                }
                else {
                    InputStreamReader inputStreamReader = new InputStreamReader(connection.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    buffer=new StringBuffer();

                    String line ="";
                    while ((line = bufferedReader.readLine()) != null) {
                      //  response += line;
                        buffer.append(line);

                        Log.d("output lines", line);
                    }
                    response=buffer.toString();
                    Log.i("response in customer", response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }
        @Override
        protected void onPostExecute(String response) {
            dialog.dismiss();
           /* try {
                //  Log.i("response--",response);
                JSONObject jsonObject=new JSONObject(response);
              //  JSONArray jsonArray=jsonObject.getJSONArray("companies");
                JSONObject  jsonObject1=jsonObject.getJSONObject("customers");
                Iterator<String> iter = jsonObject1.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        Object value = jsonObject1.get(key);
                        Log.i("jsonobjectvalue1--",value.toString());
                    } catch (JSONException e) {
                        // Something went wrong!
                    }
                }

               // mAdapter = new CompanyAdapter(getActivity(), AppSession.companyArrayList);

               // AppSession.company_recyclerView.setAdapter(mAdapter);
                //AppSession.company_recyclerView.setItemAnimator(new DefaultItemAnimator());
                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
               // RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
                //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);

               // AppSession.company_recyclerView.setLayoutManager(lmanager);

            } catch (JSONException e) {
                e.printStackTrace();
            }
*/
        }

    }
   /* public void getCustomerList(String token)
    { StringRequest stringRequest = new StringRequest(Request.Method.GET, Appconfig.CUSTOMER_URL+token,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.i("response--", String.valueOf(response));
                        JSONObject jsonObject=new JSONObject(response);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

            Log.i("response--", String.valueOf(error));
        }
    }){
        @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();
            return params;
        }

    } ;
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);
    }*/
}
