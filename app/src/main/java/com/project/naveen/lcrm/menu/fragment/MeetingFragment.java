package com.project.naveen.lcrm.menu.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.project.naveen.lcrm.AppSession;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.RecyclerSwipeListener;
import com.project.naveen.lcrm.RecyclerTouchListener;
import com.project.naveen.lcrm.adapters.MeetingAdapter;
import com.project.naveen.lcrm.addactivity.AddMeetingActivity;
import com.project.naveen.lcrm.detailsactivity.DetailMeetingActivity;
import com.project.naveen.lcrm.models.Meeting;

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
public class MeetingFragment extends Fragment {
    //ArrayList<Meeting> meetingArrayList;
    //RecyclerView rv;
    String token;
    MeetingAdapter mAdapter;
    View v;
    MaterialSearchView searchView;

    public MeetingFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp>= 600) {
            v=inflater.inflate(R.layout.fragment_meeting, container, false);
        }
        else
        {
            v=inflater.inflate(R.layout.fragment_meeting_mob, container, false);
        }
//        View v=inflater.inflate(R.layout.fragment_meeting, container, false);
        setHasOptionsMenu(true);
        searchView = (MaterialSearchView)getActivity().findViewById(R.id.search_view);
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Meeting");
        }

        AppSession.meetingArrayList=new ArrayList<>();
        token= Appconfig.TOKEN;
        AppSession.meeting_recyclerView=(RecyclerView)v.findViewById(R.id.rv);

        new GetAllMeetingTask().execute(token);

        if (AppSession.meeting_write==0)
        {
            v.findViewById(R.id.fab).setVisibility(View.GONE);
        }
        v.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //animation= AnimationUtils.loadAnimation(getActivity(),R.anim.zoom_in);

//                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                Intent i=new Intent(getActivity(), AddMeetingActivity.class);
                getActivity().startActivity(i);

            }
        });
        ItemTouchHelper.Callback callback = new RecyclerSwipeListener(mAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(AppSession.meeting_recyclerView);
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
                ArrayList<Meeting> subMeetingTeamArrayList=new ArrayList<>();
                Log.i("ontypecomplete", newText);
                Log.i("list data--", String.valueOf(AppSession.meetingArrayList.size()));
                for (int i=0;i<AppSession.meetingArrayList.size();i++)
                {
                    if (AppSession.meetingArrayList.get(i).getMeeting_subject().contains(newText))
                    {
                        Meeting meeting=new Meeting();
                        meeting.setMeeting_subject(AppSession.meetingArrayList.get(i).getMeeting_subject());
                        meeting.setResponsible(AppSession.meetingArrayList.get(i).getResponsible());
                        meeting.setStarting_date(AppSession.meetingArrayList.get(i).getStarting_date());

                        subMeetingTeamArrayList.add(meeting);
                    }
                    //System.out.println("lead item --"+leadsArrayList.get(i).getName()+" "+leadsArrayList.get(i).getNumber());
                }
                AppSession.meeting_recyclerView.setAdapter(new MeetingAdapter(getActivity(),subMeetingTeamArrayList));
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    class GetAllMeetingTask extends AsyncTask<String,Void,String>
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
            URL url = null;
            HttpURLConnection connection = null ;
            String tok=params[0];
            try {
                url = new URL(Appconfig.MEETINGS_URL+tok);
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

            try {
                  Log.i("response--",response);
                JSONObject jsonObject=new JSONObject(response);
                JSONArray jsonArray=jsonObject.getJSONArray("meetings");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    Meeting meeting=new Meeting();
                    meeting.setId(object.getInt("id"));
                    meeting.setMeeting_subject(object.getString("meeting_subject"));
                    meeting.setStarting_date(object.getString("starting_date"));
                    AppSession.meetingArrayList.add(meeting);

                    //  Log.i("leadslist--",lead.getName());
                }

                mAdapter = new MeetingAdapter(getActivity(),AppSession.meetingArrayList);


                AppSession.meeting_recyclerView.setAdapter(mAdapter);
                AppSession.meeting_recyclerView.setItemAnimator(new DefaultItemAnimator());
                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
                //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);
                AppSession.meeting_recyclerView.setLayoutManager(lmanager);
                dialog.dismiss();
                AppSession.meeting_recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),AppSession.meeting_recyclerView, new RecyclerTouchListener.ClickListener() {
                    @Override

                    public void onClick(View view, int position) {
                        Meeting meeting=AppSession.meetingArrayList.get(position);
                        final int meeting_id=meeting.getId();
                        Intent i=new Intent(getActivity(), DetailMeetingActivity.class);
                        i.putExtra("meeting_id",String.valueOf(meeting_id));
                        i.putExtra("meeting_id__position",position);
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
}
