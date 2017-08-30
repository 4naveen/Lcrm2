package com.project.lorvent.lcrm.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.project.lorvent.lcrm.activities.ComposeMailCustomerActivity;
import com.project.lorvent.lcrm.activities.ComposeMailActivity;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.adapters.InboxAdapter;
import com.project.lorvent.lcrm.models.Inbox;
import com.project.lorvent.lcrm.utils.AppSession;
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
public class InboxFragment extends Fragment {
    ArrayList<Inbox> inboxArrayList;
    RecyclerView rv;
    String token;
    InboxAdapter mAdapter;
    MaterialSearchView searchView;
    public InboxFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_inbox, container, false);
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        inboxArrayList=new ArrayList<>();
        token= Appconfig.TOKEN;
        searchView=(MaterialSearchView)getActivity().findViewById(R.id.search_view);
        rv=(RecyclerView)v.findViewById(R.id.rv);
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
            new GetAllInboxEmail().execute(Appconfig.TOKEN);
        }
        v.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                final PackageManager pm =getActivity().getPackageManager();
                final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
                ResolveInfo best = null;
                for(final ResolveInfo info : matches)
                    if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                        best = info;
                if (best != null)
                    emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
                getActivity().startActivity(emailIntent);*/
//                SendMail mail=new SendMail(getActivity(),et3.getText().toString(),et4.getText().toString(),et5.getText().toString());
//                mail.execute();

                if (AppSession.role.equals("customer"))
                {
                    Intent intent=new Intent(getActivity(), ComposeMailCustomerActivity.class);
                    getActivity().startActivity(intent);
                }
                else {
                    Intent intent=new Intent(getActivity(), ComposeMailActivity.class);
                    getActivity().startActivity(intent);
                }

            }
        });

        return v;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search_staff,menu);
        MenuItem menuItem=menu.findItem(R.id.action_search);
        searchView.setMenuItem(menuItem);
        Log.i("oncreatre-","jhgfdgdgufvg");
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Inbox> subInboxArrayList=new ArrayList<>();
                Log.i("ontypecomplete", newText);
                // Log.i("list data--", String.valueOf(InboxArrayList.size()));
                for (int i=0;i<inboxArrayList.size();i++)
                {
                    if (inboxArrayList.get(i).getTitle().contains(newText))
                    {

                        subInboxArrayList.add(inboxArrayList.get(i));
                        Log.i("oncreatre-","jhgfdgdgufvg");
                    }
                    //System.out.println("lead item --"+leadsArrayList.get(i).getName()+" "+leadsArrayList.get(i).getNumber());
                }
                rv.setAdapter(new InboxAdapter(getActivity(),subInboxArrayList));
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.action_search)
        {
            Toast.makeText(getActivity(),"gvfhgvfhvbfjvbd", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    class GetAllInboxEmail extends AsyncTask<String,Void,String>
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
            Log.i("response",response);
            if (dialog!=null&&dialog.isShowing()){dialog.dismiss();}
            try {
                JSONObject jsonObject=new JSONObject(response);
                JSONArray jsonArray=jsonObject.getJSONArray("get_emails");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    Inbox inbox=new Inbox();
                    inbox.setId(object.getInt("id"));
                    inbox.setDate(object.getString("created_at"));
                    //Log.i("sender", String.valueOf(object.get("sender")));
                    if (String.valueOf(object.get("sender")).equalsIgnoreCase("null"))
                    {
                        inbox.setTitle("user deleted");
                    }
                    else {
                        JSONObject sub_object=object.getJSONObject("sender");
                        inbox.setTitle(sub_object.getString("full_name"));
                    }
                    inbox.setSubtitle(object.getString("subject"));
                    inbox.setDescription(object.getString("message"));
                    inboxArrayList.add(inbox);

                }
                mAdapter=new InboxAdapter(getActivity(),inboxArrayList);
                rv.setAdapter(mAdapter);
                rv.setItemAnimator(new DefaultItemAnimator());
                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
                //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);
                rv.setLayoutManager(lmanager);
                ItemTouchHelper.Callback callback = new RecyclerSwipeListener(mAdapter,0);
                ItemTouchHelper helper = new ItemTouchHelper(callback);
                helper.attachToRecyclerView(rv);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }}
}
