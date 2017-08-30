package com.project.lorvent.lcrm.fragments;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.adapters.InboxAdapter;
import com.project.lorvent.lcrm.adapters.SentMailAdapter;
import com.project.lorvent.lcrm.models.Inbox;
import com.project.lorvent.lcrm.models.SentMail;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.NetworkStatus;
import com.project.lorvent.lcrm.utils.RecyclerSwipeListener;

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
public class SentMailFragment extends Fragment {
    ArrayList<SentMail> sentMailArrayList;
    RecyclerView rv;
    String token;
    SentMailAdapter mAdapter;
    MaterialSearchView searchView;
    public SentMailFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_sent_mail, container, false);
        setHasOptionsMenu(true);
        sentMailArrayList=new ArrayList<>();
        token= Appconfig.TOKEN;
        searchView=(MaterialSearchView)getActivity().findViewById(R.id.search_view);
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();

        rv=(RecyclerView)v.findViewById(R.id.rv);
       /* for (int i=0;i<20;i++)
        {
            SentMail sentMail=new SentMail();
            sentMail.setTitle("Gmail Admin");
            sentMail.setDescription("jvfjkvfvfvfjvfovfiuvhfhgfhgfhgfhgdjhgfjhgjghjghjgddhhufufifvf");
            sentMail.setSubtitle("A greeting from gmail team");
            sentMailArrayList.add(sentMail);
        }*/

        if (!NetworkStatus.isConnected(getActivity())) {
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
            new GetAllSentEmail().execute(Appconfig.TOKEN);
        }
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
                ArrayList<SentMail> subSentMailArrayList=new ArrayList<>();
                Log.i("ontypecomplete", newText);
                Log.i("list data--", String.valueOf(sentMailArrayList.size()));
                for (int i=0;i<sentMailArrayList.size();i++)
                {
                    if (sentMailArrayList.get(i).getTitle().contains(newText))
                    {
                        subSentMailArrayList.add(sentMailArrayList.get(i));
                    }
                    //System.out.println("lead item --"+leadsArrayList.get(i).getName()+" "+leadsArrayList.get(i).getNumber());
                }
                rv.setAdapter(new SentMailAdapter(getActivity(),subSentMailArrayList));
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    class GetAllSentEmail extends AsyncTask<String,Void,String>
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
                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String get_url;
                if (text_url != null) {
                    get_url = text_url + "/emails?token=";
                } else {
                    get_url = Appconfig.EMAILS_URL;
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
                JSONArray jsonArray=jsonObject.getJSONArray("sent_emails");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    SentMail sentMail=new SentMail();
                    sentMail.setId(object.getInt("id"));
                    sentMail.setDate(object.getString("created_at"));
                    if (String.valueOf(object.get("receiver")).equalsIgnoreCase("null"))
                    {
                        sentMail.setTitle("user deleted");
                    }
                    else {
                        JSONObject sub_object=object.getJSONObject("receiver");
                        sentMail.setTitle(sub_object.getString("full_name"));
                    }
                    sentMail.setSubtitle(object.getString("subject"));
                    sentMail.setDescription(object.getString("message"));
                    sentMailArrayList.add(sentMail);

                }
                mAdapter=new SentMailAdapter(getActivity(),sentMailArrayList);
                rv.setAdapter(mAdapter);
                rv.setItemAnimator(new DefaultItemAnimator());
                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
                rv.setLayoutManager(lmanager);
                ItemTouchHelper.Callback callback = new RecyclerSwipeListener(mAdapter,1);
                ItemTouchHelper helper = new ItemTouchHelper(callback);
                helper.attachToRecyclerView(rv);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }}
}
